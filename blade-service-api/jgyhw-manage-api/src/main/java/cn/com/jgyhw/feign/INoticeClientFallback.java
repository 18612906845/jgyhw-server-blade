package cn.com.jgyhw.feign;

import cn.com.jgyhw.entity.Notice;
import org.springblade.core.tool.api.R;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by WangLei on 2020/1/5 0005 13:57
 */
@Component
public class INoticeClientFallback implements INoticeClient {

	/**
	 * 获取notice列表
	 *
	 * @param number
	 * @return
	 */
	@Override
	public R<List<Notice>> top(Integer number) {
		return R.fail(400,"Feign未获取到notice列表信息");
	}
}
