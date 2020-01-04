package cn.com.jgyhw.account.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 支付结果
 *
 * Created by WangLei on 2019/11/30 0030 23:10
 */
@Data
@ApiModel(value = "支付结果视图对象", description = "支付结果视图对象")
public class PayResultVo {

	/**
	 * 支付结果消息
	 */
	@ApiModelProperty(value = "支付结果消息")
	private String msg;

	/**
	 * 是否支付成功
	 */
	@ApiModelProperty(value = "是否支付成功")
	private boolean status;
}
