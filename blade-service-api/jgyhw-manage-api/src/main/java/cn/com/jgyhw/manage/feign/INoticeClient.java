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
package cn.com.jgyhw.manage.feign;

import cn.com.jgyhw.manage.entity.Notice;
import org.springblade.common.constant.JgyhwConstant;
import org.springblade.core.tool.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 通知公告 Feign接口类
 *
 * @author Chill
 */
@FeignClient(
	value = JgyhwConstant.APPLICATION_MANAGE_NAME,
	fallback = INoticeClientFallback.class
)
public interface INoticeClient {

	String API_PREFIX = "/dashboard";

	/**
	 * 获取notice列表
	 *
	 * @param number
	 * @return
	 */
	@GetMapping(API_PREFIX + "/top")
	R<List<Notice>> top(@RequestParam("number") Integer number);

}
