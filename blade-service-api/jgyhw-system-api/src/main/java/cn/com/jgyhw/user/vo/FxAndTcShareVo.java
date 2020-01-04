package cn.com.jgyhw.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户返现提成比例
 *
 * Created by WangLei on 2019/11/30 0030 20:42
 */
@Data
@ApiModel(value = "用户返现提成比例视图对象", description = "用户返现提成比例视图对象")
public class FxAndTcShareVo {

	/**
	 * 用户返现比例
	 */
	@ApiModelProperty(value = "用户返现比例")
	private int fxShare;

	/**
	 * 用户提成比例
	 */
	@ApiModelProperty(value = "用户提成比例")
	private int tcShare;
}
