package com.yuqian.itax.xxljob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan("com.yuqian.itax")
@MapperScan(basePackages = "com.yuqian.itax.*.dao")
@Configuration
public class ItaxXxljobApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ItaxXxljobApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ItaxXxljobApplication.class);
    }

}
