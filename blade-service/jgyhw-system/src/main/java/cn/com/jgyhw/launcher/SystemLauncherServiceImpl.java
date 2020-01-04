/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package cn.com.jgyhw.launcher;

import org.springblade.core.launch.constant.NacosConstant;
import org.springblade.core.launch.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Properties;

/**
 * 启动参数拓展
 *
 * @author Chill
 */
public class SystemLauncherServiceImpl implements LauncherService {

	@Override
	public void launcher(SpringApplicationBuilder builder, String appName, String profile) {
		Properties props = System.getProperties();
		props.setProperty("spring.cloud.nacos.config.ext-config[0].data-id", NacosConstant.dataId(appName, profile));
		props.setProperty("spring.cloud.nacos.config.ext-config[0].group", NacosConstant.NACOS_CONFIG_GROUP);
		props.setProperty("spring.cloud.nacos.config.ext-config[0].refresh", NacosConstant.NACOS_CONFIG_REFRESH);
	}

	@Override
	public int getOrder() {
		return 20;
	}
}
