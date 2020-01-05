package cn.com.jgyhw.manage.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 管理服务配置类
 */
@Configuration
@MapperScan({"cn.com.jgyhw.manage.mapper.**"})
public class ManageConfiguration implements WebMvcConfigurer {

}
