package com.yuqian.itax.util.util;

import org.springframework.context.ApplicationContext;

/**
 * @ClassName SpringContextUtil
 * @Description TODO
 * @Author jiangni
 * @Date 2019/9/6
 * @Version 1.0
 */
public class SpringContextUtil {

    private SpringContextUtil(){}

    private static ApplicationContext applicationContext;

    //获取上下文
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //设置上下文
    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    //通过名字获取上下文中的bean
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    //通过类型获取上下文中的bean
    public static Object getBean(Class<?> requiredType){
        return applicationContext.getBean(requiredType);
    }

    /**
     * 获取当前环境配置
     * @return
     */
    public static String getActiveProfile() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }
}