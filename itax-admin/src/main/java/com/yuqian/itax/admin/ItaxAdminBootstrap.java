package com.yuqian.itax.admin;

import com.yuqian.itax.util.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;


/**
 * @ClassName MposAdminBootstrap
 * @Description
 * @Author jiangni
 * @Date 2019/7/15
 * @Version 1.0
 */
@SpringBootApplication
@EnableTransactionManagement
@ComponentScan("com.yuqian.itax")
@MapperScan("com.yuqian.itax.*.dao")
@Slf4j
public class ItaxAdminBootstrap extends SpringBootServletInitializer {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ItaxAdminBootstrap.class, args);
        SpringContextUtil.setApplicationContext(context);
        log.info("ItaxAdminBootstrap is Start Success!\n" +
                "////////////////////////////////////////////////////////////////////\n" +
                "//                          _ooOoo_                               //\n" +
                "//                         o8888888o                              //\n" +
                "//                        88\" . \"88                              //\n" +
                "//                         (| ^_^ |)                              //\n" +
                "//                        O\\  =  /O                              //\n" +
                "//                      ____/`---'\\____                           //\n" +
                "//                    .'  \\\\|     |//  `.                         //\n" +
                "//                   /  \\\\|||  :  |||//  \\                        //\n" +
                "//                  /  _||||| -:- |||||-  \\                       //\n" +
                "//                  |   | \\\\\\  -  /// |   |                       //\n" +
                "//                  | \\_|  ''\\---/''  |   |                       //\n" +
                "//                  \\  .-\\__  `-`  ___/-. /                       //\n" +
                "//                ___`. .'  /--.--\\  `. . ___                     //\n" +
                "//              .\"\" '<  `.___\\_<|>_/___.'  >'\"\".                  //\n" +
                "//            | | :  `- \\`.;`\\ _ /`;.`/ - ` : | |                 //\n" +
                "//            \\  \\ `-.   \\_ __\\ /__ _/   .-` /  /                 //\n" +
                "//      ========`-.____`-.___\\_____/___.-`____.-'========         //\n" +
                "//                           `=---='                              //\n" +
                "//      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //\n" +
                "//             佛祖保佑       永不宕机      永无BUG               //\n" +
                "//11111111111111111111111111111111111111111111111111111111111111///" +
                "");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ItaxAdminBootstrap.class);
    }
}
