package cn.com.jgyhw.goods.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

/**
 * 京东推广位信息
 *
 * Created by WangLei on 2019/11/24 0024 23:01
 */
@Data
@TableName("jgyhw_jd_position")
@ApiModel(value = "京东推广位信息对象", description = "京东推广位信息对象")
public class JdPosition extends BaseEntity {

	/**
	 * 京东推广位ID
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "京东推广位ID")
	private Long id;

	/**
	 * 京东推广位名称
	 */
	@ApiModelProperty(value = "京东推广位名称")
	private String name;

	/**
	 * 微信用户标识
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "微信用户标识")
	private Long wxUserId;
}
