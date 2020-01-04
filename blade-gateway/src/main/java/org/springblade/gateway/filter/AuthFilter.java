/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springblade.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.constant.CommonConstant;
import org.springblade.common.constant.JgyhwConstant;
import org.springblade.common.tool.MD5Util;
import org.springblade.gateway.props.AuthProperties;
import org.springblade.gateway.provider.AuthProvider;
import org.springblade.gateway.provider.ResponseProvider;
import org.springblade.gateway.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 鉴权认证
 *
 * @author Chill
 */
@Slf4j
@Component
@AllArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {
	private AuthProperties authProperties;
	private ObjectMapper objectMapper;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();
		if (isSkip(path)) {
			return chain.filter(exchange);
		}
		ServerHttpResponse resp = exchange.getResponse();
		if(path.indexOf(JgyhwConstant.APPLICATION_MANAGE_NAME) != -1){
			String headerToken = exchange.getRequest().getHeaders().getFirst(AuthProvider.AUTH_KEY);
			String paramToken = exchange.getRequest().getQueryParams().getFirst(AuthProvider.AUTH_KEY);
			if (StringUtils.isAllBlank(headerToken, paramToken)) {
				return unAuth(resp, "缺失令牌,鉴权失败");
			}
			String auth = StringUtils.isBlank(headerToken) ? paramToken : headerToken;
			String token = JwtUtil.getToken(auth);
			Claims claims = JwtUtil.parseJWT(token);
			if (claims == null) {
				return unAuth(resp, "请求未授权");
			}
			return chain.filter(exchange);
		}else{
			// 验证权限
			String nonce = exchange.getRequest().getHeaders().getFirst("nonce");
			String sign = exchange.getRequest().getHeaders().getFirst("sign");
			String timestamp = exchange.getRequest().getHeaders().getFirst("timestamp");
			// 验证请求参数
			if(StringUtils.isBlank(nonce) || StringUtils.isBlank(sign) || StringUtils.isBlank(timestamp)){
				return unAuth(resp, "缺失令牌参数");
			}
			// 验证是否超时
			Long timestampLong = Long.valueOf(timestamp);
			int maxTimeout = 1000*60*5;
			if(System.currentTimeMillis() - timestampLong < 0 || System.currentTimeMillis() - timestampLong > maxTimeout){
				return unAuth(resp, "令牌过期");
			}
			// 验证随机值
			if(stringRedisTemplate.hasKey(CommonConstant.AUTH_NONCE_KEY_PREFIX + nonce)){
				return unAuth(resp, "重复请求");
			}
			// 验证签名
			String md5 = MD5Util.stringToMD5(nonce + timestamp);
			if(!sign.equals(md5)){
				return unAuth(resp, "签名错误");
			}
			// 写入随机值到Redis
			stringRedisTemplate.opsForValue().set(CommonConstant.AUTH_NONCE_KEY_PREFIX + nonce, nonce, maxTimeout, TimeUnit.MILLISECONDS);
			return chain.filter(exchange);
		}
	}

	private boolean isSkip(String path) {
		return AuthProvider.getDefaultSkipUrl().stream().map(url -> url.replace(AuthProvider.TARGET, AuthProvider.REPLACEMENT)).anyMatch(path::contains)
			|| authProperties.getSkipUrl().stream().map(url -> url.replace(AuthProvider.TARGET, AuthProvider.REPLACEMENT)).anyMatch(path::contains);
	}

	private Mono<Void> unAuth(ServerHttpResponse resp, String msg) {
		resp.setStatusCode(HttpStatus.UNAUTHORIZED);
		resp.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
		String result = "";
		try {
			result = objectMapper.writeValueAsString(ResponseProvider.unAuth(msg));
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
		}
		DataBuffer buffer = resp.bufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8));
		return resp.writeWith(Flux.just(buffer));
	}

	@Override
	public int getOrder() {
		return -100;
	}

}
