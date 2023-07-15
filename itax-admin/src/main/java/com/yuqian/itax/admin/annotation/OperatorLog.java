package com.yuqian.itax.admin.annotation;

import java.lang.annotation.*;

/**
 * @Interface OperatorLog
 * @Description
 * @Author jiangni
 * @Date 2019/9/24
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperatorLog {

    /**
     * 模块名称
     */
    String module() default "" ;

    /**
     * 备注
     */
    String operDes() default "";

    /**
     *  0 - 查询 1-新增 2-修改 3-删除 4-其他
     * @return
     */
    int  oprType() default 0;
}
