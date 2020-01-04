package cn.com.jgyhw.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

/**
 * 微信用户
 */
@Data
@TableName("jgyhw_wx_user")
@ApiModel(value = "微信用户对象", description = "微信用户对象")
public class WxUser extends BaseEntity {

	/**
	 * 主键id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(value = "id", type = IdType.ID_WORKER)
	@ApiModelProperty(value = "主键id")
    private Long id;

	/**
	 * 所属租户ID
	 */
	@ApiModelProperty(value = "所属租户ID")
	private String tenantId;

    /**
     * 用户在开放平台的唯一标识符
     */
	@ApiModelProperty(value = "用户在开放平台的唯一标识符")
    private String unionId;

    /**
     * 微信用户标识-公众号
     */
	@ApiModelProperty(value = "微信用户标识-公众号")
    private String openIdGzh;

    /**
     * 微信用户标识-小程序
     */
	@ApiModelProperty(value = "微信用户标识-小程序")
    private String openIdXcx;

    /**
     * 微信用户标识-PC网站
     */
	@ApiModelProperty(value = "微信用户标识-PC网站")
    private String openIdPc;

    /**
     * 推荐人微信用户标识
     */
	@JsonSerialize(using = ToStringSerializer.class)
	@ApiModelProperty(value = "推荐人微信用户标识")
    private Long parentWxUserId;

	/**
	 * 自定义用户返现比例，0为不自定义，使用租户定义返现比例
	 */
	@ApiModelProperty(value = "自定义用户返现比例，0为不自定义，使用租户定义返现比例")
	private Integer returnMoneyShare;

	/**
	 * 自定义用户提成比例，0为不自定义，使用租户定义提成比例
	 */
	@ApiModelProperty(value = "自定义用户提成比例，0为不自定义，使用租户定义提成比例")
	private Integer returnMoneyShareTc;

    /**
     * 用户昵称
     */
	@ApiModelProperty(value = "用户昵称")
    private String nickName;

    /**
     * 用户性别，1：男性，2：女性，0：未知
     */
	@ApiModelProperty(value = "用户性别，1：男性，2：女性，0：未知")
    private Integer sex;

    /**
     * 用户填写的省份
     */
	@ApiModelProperty(value = "用户填写的省份")
    private String province;

    /**
     * 用户填写的城市
     */
	@ApiModelProperty(value = "用户填写的城市")
    private String city;

    /**
     * 用户填写的国家
     */
	@ApiModelProperty(value = "用户填写的国家")
    private String country;

    /**
     * 用户头像URL
     */
	@ApiModelProperty(value = "用户头像URL")
    private String headImgUrl;

}
