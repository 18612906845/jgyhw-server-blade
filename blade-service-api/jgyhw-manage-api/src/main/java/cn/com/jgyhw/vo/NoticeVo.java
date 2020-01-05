package cn.com.jgyhw.vo;

import cn.com.jgyhw.entity.Notice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知公告视图类
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "通知公告视图对象", description = "通知公告视图对象")
public class NoticeVo extends Notice {

	@ApiModelProperty(value = "通知类型名")
	private String categoryName;

}
