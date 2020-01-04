package org.springblade.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户授权认证请求参数
 *
 * Created by WangLei on 2020/1/4 0004 21:30
 */
@Data
@ApiModel(value = "用户授权认证请求参数对象", description = "用户授权认证请求参数对象")
public class AuthParam implements Serializable {

	/**
	 * 授权类型
	 */
	@ApiModelProperty(value = "授权类型", required = true)
	private String grantType = "password";

	/**
	 * 刷新令牌
	 */
	@ApiModelProperty(value = "刷新令牌")
	private String refreshToken;

	/**
	 * 租户ID
	 */
	@ApiModelProperty(value = "租户ID", required = true)
	private String tenantId = "000000";

	/**
	 * 账号
	 */
	@ApiModelProperty(value = "账号", required = true)
	private String account;

	/**
	 * 密码
	 */
	@ApiModelProperty(value = "密码", required = true)
	private String password;
}
