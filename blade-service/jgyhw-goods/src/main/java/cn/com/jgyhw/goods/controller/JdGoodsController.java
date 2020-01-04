package cn.com.jgyhw.goods.controller;

import cn.com.jgyhw.goods.apith.SignAndSend;
import cn.com.jgyhw.goods.entity.JdGoods;
import cn.com.jgyhw.goods.entity.JdPosition;
import cn.com.jgyhw.goods.service.IJdGoodsApiService;
import cn.com.jgyhw.goods.service.IJdGoodsService;
import cn.com.jgyhw.goods.service.IJdPositionService;
import cn.com.jgyhw.goods.vo.JdGoodsVo;
import cn.com.jgyhw.user.entity.WxUser;
import cn.com.jgyhw.user.feign.IWxUserClient;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jd.union.open.promotion.common.get.request.PromotionCodeReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springblade.common.constant.JdParamConstant;
import org.springblade.core.tool.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 京东商品控制器
 *
 * Created by WangLei on 2019/11/22 0022 00:37
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/jdGoods")
@Api(value = "京东商品", tags = "京东商品")
public class JdGoodsController {

	@Value("${jgyhw.jd.regexpAllNumber}")
	private String regexpAllNumber;

	@Value("${jgyhw.jd.regexpVerifyJdGoodsPageUrl}")
	private String regexpVerifyJdGoodsPageUrl;

	@Value("${jgyhw.jd.regexpExtractUrlJdGoodsId}")
	private String regexpExtractUrlJdGoodsId;

	@Value("${jgyhw.system.returnMoneyShareDefault}")
	private Integer systemReturnMoneyShareDefault;

	@Autowired
	private IJdGoodsApiService jdGoodsApiService;

	@Resource(name = "jdGoodsServiceRedisImpl")
	private IJdGoodsService jdGoodsServiceRedis;

	@Autowired
	private IJdPositionService jdPositionService;

	@Autowired
	private IWxUserClient wxUserClient;

	/**
	 * 根据京东商品编号获取商品主图地址
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	@GetMapping("/findJdGoodsImgUrl")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "根据京东商品编号获取商品主图地址", notes = "")
	public R<String> findJdGoodsImgUrl(@ApiParam(value = "商品编号", required = true) String goodsId){
		JdGoods jdGoods = jdGoodsApiService.reqJdApiGetJdGoodsByGoodsId(goodsId, null);
		String goodsImgUrl = null;
		if(jdGoods != null){
			goodsImgUrl = jdGoods.getGoodsImgUrl();
		}
		return R.data(goodsImgUrl);
	}

	/**
	 * 根据京东商品编号查询商品信息（缓存）
	 *
	 * @param goodsId 商品编号
	 * @return
	 */
	@GetMapping("/findJdGoodsCacheByGoodsId")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "根据京东商品编号查询商品信息（缓存）", notes = "")
	public R<JdGoods> findJdGoodsCacheByGoodsId(@ApiParam(value = "商品编号", required = true) String goodsId){
		JdGoods jdGoods = jdGoodsServiceRedis.queryJdGoodsByGoodsId(goodsId);
		return R.data(jdGoods);
	}

	/**
	 * 根据关键词查询京东推广信息（包含商品信息和推广链接）
	 *
	 * @param keyword 关键词
	 * @param wxUserId 微信用户标识
	 * @param returnMoneyShare 返现比例
	 * @param type "gzh"：公众号（默认），"xcx"：小程序
	 * @return
	 */
	@GetMapping("/findJdCpsInfoByKeyword")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "根据关键词查询京东推广信息（包含商品信息和推广链接）", notes = "")
	public R<JdGoodsVo> findJdCpsInfoByKeyword(@ApiParam(value = "关键词", required = true) String keyword,
											   @ApiParam(value = "微信用户标识", required = true) String wxUserId,
											   @ApiParam(value = "返现比例", required = true) Integer returnMoneyShare,
											   @ApiParam(value = "\"gzh\"：公众号（默认），\"xcx\"：小程序", required = true) String type){
		//验证关键字是否是全部数字
		if(StringUtils.isBlank(keyword) || StringUtils.isBlank(wxUserId)){
			return R.status(false);
		}
		keyword = keyword.trim();
		String goodsId = "";
		Pattern numberPattern = Pattern.compile(regexpAllNumber);
		//验证关键字是否是网址
		Pattern urlPattern = Pattern.compile(regexpVerifyJdGoodsPageUrl);
		if(numberPattern.matcher(keyword).matches()){
			goodsId = keyword;
		}else if(urlPattern.matcher(keyword).matches()){
			// 提取URL里的商品编号
			Pattern pattern = Pattern.compile(regexpExtractUrlJdGoodsId);
			Matcher m = pattern.matcher(keyword);
			while (m.find()) {
				goodsId += m.group(1);
			}
		}
		// 判断返现比例是否有填写
		if(returnMoneyShare == null){
			returnMoneyShare = systemReturnMoneyShareDefault;
			R<WxUser> wxUserR = wxUserClient.findWxUserById(Long.valueOf(wxUserId));
			if(wxUserR.getCode() == 200 && wxUserR.getData().getId() != null){
				WxUser wu = wxUserR.getData();
				if(wu.getReturnMoneyShare() > 0){
					returnMoneyShare = wu.getReturnMoneyShare();
				}
			}
		}

		JdGoods jdGoods = jdGoodsApiService.reqJdApiGetJdGoodsByGoodsId(goodsId, returnMoneyShare);
		if(jdGoods == null || StringUtils.isBlank(wxUserId)){
			return R.data(null);
		}
		PromotionCodeReq pcr = new PromotionCodeReq();
		pcr.setExt1(wxUserId);
		// 查询对应用户的JD推广位信息
		JdPosition jp = jdPositionService.queryJdPositionByWxUserId(Long.valueOf(wxUserId));
		if(jp != null){
			pcr.setPositionId(jp.getId());
		}
		pcr.setMaterialId(jdGoods.getMaterialUrl());
		pcr.setSiteId(JdParamConstant.JD_WEB_ID);
		// 默认获取公众号推广链接
		String cpsUrl = jdGoodsApiService.queryJdCpsUrl(pcr);
		// 判断是否小程序
		if(StringUtils.isNotBlank(type) && JdParamConstant.SPS_TYPE_XCX.equals(type)){
			String url = JdParamConstant.JD_TRANSITION_CPS_URL_OTHER;
			if (StringUtils.isBlank(url)) {
				return null;
			}
			url = url.replaceAll("MATERIAL_ID", jdGoods.getMaterialUrl());
			url = url.replaceAll("UNION_ID", JdParamConstant.JD_UNION_ID);
			if(jp != null){
				url = url.replaceAll("POSITION_ID", jp.getId().toString());
			}
			String respContent = SignAndSend.sendGet(url, JdParamConstant.APITH_SECRET_ID, JdParamConstant.APITH_SECRET_KEY);
			log.info("获取小程序推广链接结果：" + respContent);
			if (StringUtils.isNotBlank(respContent)) {
				JSONObject jsonObject = JSONObject.parseObject(respContent);
				String code = jsonObject.getString("code");
				if ("1".equals(code)) {
					JSONObject dataJsonObj = jsonObject.getJSONObject("data");
					cpsUrl = dataJsonObj.getString("shortURL");
				}
			}
		}

		JdGoodsVo jgVo = new JdGoodsVo();
		BeanCopier copier = BeanCopier.create(JdGoods.class, JdGoodsVo.class, false);
		copier.copy(jdGoods, jgVo, null);
		jgVo.setCpsUrl(cpsUrl);
		return R.data(jgVo);
	}
}
