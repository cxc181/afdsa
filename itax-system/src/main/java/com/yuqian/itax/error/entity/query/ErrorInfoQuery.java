package com.yuqian.itax.error.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ErrorInfoQuery extends BaseQuery {

    /**
     * 错误编码
     */
    private String errorCode;

    /**
         * 客户端类型 1-接口  2-后台
     */
    private Integer clientType;

    /**
     * 模块名称（模糊）
     */
    private String likeModelName;

    /**
     * 类名称（模糊）
     */
    private String likeClassName;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 添加时间开始
     */
    private Date addTimeBeg;

    /**
     * 添加时间结束
     */
    private Date addTimeEnd;

}
