package com.yuqian.itax.api;

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

/**
 * @ClassName ItaxApiBootstrap
 * @Description
 * @Author jiangni
 * @Date 2019/7/15
 * @Version 1.0
 */
@EnableTransactionManagement
@SpringBootApplication
@ComponentScan("com.yuqian.itax")
@MapperScan(basePackages = "com.yuqian.itax.*.dao")
@Configuration
@Slf4j
public class ItaxApiBootstrap extends SpringBootServletInitializer {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ItaxApiBootstrap.class, args);
        SpringContextUtil.setApplicationContext(context);
        log.info("--------------------------------------------------------------------------------------------------------------------------\n" +
                "                                                            _ooOoo_\n" +
                "                                                           o8888888o\n" +
                "                                                           88\" . \"88\n" +
                "                                                           (| -_- |)\n" +
                "                                                            O\\ = /O\n" +
                "                                                        ____/`---'\\____\n" +
                "                                                      .   ' \\\\| |// `.\n" +
                "                                                       / \\\\||| : |||// \\\n" +
                "                                                     / _||||| -:- |||||- \\\n" +
                "                                                       | | \\\\\\ - /// | |\n" +
                "                                                     | \\_| ''\\---/'' | |\n" +
                "                                                      \\ .-\\__ `-` ___/-. /\n" +
                "                                                   ___`. .' /--.--\\ `. . __\n" +
                "                                                .\"\" '< `.___\\_<|>_/___.' >'\"\".\n" +
                "                                               | | : `- \\`.;`\\ _ /`;.`/ - ` : | |\n" +
                "                                                 \\ \\ `-. \\_ __\\ /__ _/ .-` / /\n" +
                "                                         ======`-.____`-.___\\_____/___.-`____.-'======\n" +
                "                                                            `=---='\n" +
                "\n" +
                "                                         .............................................\n" +
                "                                                  佛祖镇楼                  BUG辟易\n" +
                "                                          佛曰:\n" +
                "                                                  写字楼里写字间，写字间里程序员；\n" +
                "                                                  程序人员写程序，又拿程序换酒钱。\n" +
                "                                                  酒醒只在网上坐，酒醉还来网下眠；\n" +
                "                                                  酒醉酒醒日复日，网上网下年复年。\n" +
                "                                                  但愿老死电脑间，不愿鞠躬老板前；\n" +
                "                                                  奔驰宝马贵者趣，公交自行程序员。\n" +
                "                                                  别人笑我忒疯癫，我笑自己命太贱；\n" +
                "                                                  不见满街漂亮妹，哪个归得程序员？\n" +
                "---------------------------------------------------------------------------------------------------------------------------\n");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ItaxApiBootstrap.class);
    }
}
