package com.yuqian.itax.mq;

import com.yuqian.itax.util.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan("com.yuqian.itax")
@MapperScan("com.yuqian.itax.*.dao")
@Slf4j
@Configuration
@EnableTransactionManagement
public class ItaxMqBootstrap extends SpringBootServletInitializer {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ItaxMqBootstrap.class, args);
        SpringContextUtil.setApplicationContext(context);
        log.info("===========itax-mq service start up success!========");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ItaxMqBootstrap.class);
    }
}
