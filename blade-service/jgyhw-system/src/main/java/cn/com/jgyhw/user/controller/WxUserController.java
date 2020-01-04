package cn.com.jgyhw.user.controller;

import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.service.IWxUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信用户控制器
 *
 * Created by WangLei on 2019/11/21 0021 22:02
 */
@Slf4j
@RestController
@RequestMapping("/wxUser")
@Api(value = "微信用户", tags = "微信用户")
public class WxUserController {

	@Autowired
	private IWxUserService wxUserService;

	/**
	 * 根据公众号标识获取微信用户
	 *
	 * @param openIdGzh 公众号标识
	 * @return
	 */
	@GetMapping("/findWxUserByOpenIdGzh")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "根据公众号标识获取微信用户", notes = "")
	public R<WxUser> findWxUserByOpenIdGzh(@ApiParam(value = "公众号标识", required = true) String openIdGzh){
		if(StringUtils.isBlank(openIdGzh)){
			return R.data(null);
		}
		WxUser wu = wxUserService.getOne(Wrappers.<WxUser>lambdaQuery().eq(WxUser::getOpenIdGzh, openIdGzh));
		return R.data(wu);
	}

	/**
	 * 根据用户标识获取微信用户
	 *
	 * @param wxUserId 用户标识
	 * @return
	 */
	@GetMapping("/findWxUserById")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "根据用户标识获取微信用户", notes = "")
	public R<WxUser> findWxUserById(@ApiParam(value = "用户标识", required = true) Long wxUserId){
		if(wxUserId == null){
			return R.data(null);
		}
		WxUser wu = wxUserService.getById(wxUserId);
		return R.data(wu);
	}

	/**
	 * 查询我的邀请总数
	 *
	 * @param loginKey 登陆标识
	 * @return
	 */
	@GetMapping("/findMyInviteSum")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "查询我的邀请总数", notes = "")
	public R<Integer> findMyInviteSum(@ApiParam(value = "登陆标识", required = true) Long loginKey){
		int userSum = wxUserService.count(Wrappers.<WxUser>lambdaQuery().eq(WxUser::getParentWxUserId, loginKey));
		return R.data(userSum);
	}

	/**
	 * 根据登陆标识查询用户并绑定推荐人标识
	 *
	 * @param loginKey 登陆标识
	 * @param parentWxUserId 推荐人标识
	 * @return
	 */
	@RequestMapping("/bindingParentWxUserId")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "根据登陆标识查询用户并绑定推荐人标识", notes = "")
	public R bindingParentWxUserId(@ApiParam(value = "登陆标识", required = true) Long loginKey,
								   @ApiParam(value = "推荐人标识", required = true) Long parentWxUserId){
		if(loginKey == null || parentWxUserId == null){
			return R.status(false);
		}
		if(loginKey.equals(parentWxUserId)){
			return R.status(false);
		}

		WxUser wu = wxUserService.getById(loginKey);
		if(wu == null){
			return R.status(false);
		}
		if(wu.getParentWxUserId() == null){
			wu.setParentWxUserId(parentWxUserId);
			wu.setUpdateTime(new Date());
			wxUserService.updateById(wu);
			return R.data("恭喜您已成功被邀请");
		}else{
			return R.data("您已经绑定过其他推荐人");
		}
	}

	/**
	 * 根据登陆标识查询我的邀请列表
	 *
	 * @param loginKey 登陆标识
	 * @param query 分页参数对象
	 * @return
	 */
	@GetMapping("/findMyInviteWxUserListByLoginKeyPage")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "根据登陆标识查询我的邀请列表", notes = "")
	public R<Map<String, Object>> findMyInviteWxUserListByLoginKeyPage(@ApiParam(value = "登陆标识", required = true) Long loginKey, Query query){
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("isMore", false);
		resultMap.put("msg", "未知错误");

		IPage<WxUser> page = wxUserService.page(Condition.getPage(query), Wrappers.<WxUser>lambdaQuery().eq(WxUser::getParentWxUserId, loginKey));

		if(page == null && CollectionUtils.isNotEmpty(page.getRecords())){
			resultMap.put("wxUserList", new ArrayList<>());
			return R.data(resultMap);
		}

		long count = query.getCurrent() * query.getSize();
		if(count >= page.getTotal()){
			resultMap.put("isMore", false);
		}else{
			resultMap.put("isMore", true);
		}
		resultMap.put("wxUserList", page.getRecords());
		return R.data(resultMap);
	}

}
