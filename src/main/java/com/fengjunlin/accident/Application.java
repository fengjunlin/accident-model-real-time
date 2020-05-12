package com.fengjunlin.accident;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description spring boot启动类
 * @Author fengjl
 * @Date 2019/6/13 18:24
 * @Version 1.0
 **/
@ServletComponentScan(basePackages = "com.muheda")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    //为了打包springboot项目
    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
}
