package com.yuqian.itax.api.annotation;

/**
 * @Interface JsonParam
 * @Description
 * @Author jiangni
 * @Date 2019/9/7
 * @Version 1.0
 */

import java.lang.annotation.*;

/**
 * tip：只支持application/json格式下 的基本数据类型包装类，和Sring
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonParam {
    /**
     * 值
     */
    String value() default "";

    /**
     * 是否必须
     */
    boolean require() default false;
}