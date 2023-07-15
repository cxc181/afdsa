package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业变动记录表
 */
@Getter
@Setter
public class CompanyChangeVo implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 企业状态
     */
    private Integer status;

    /**
     * 变动内容
     */
    private String remark;

    /**
     * 变动时间
     */
    private Date addTime;

    /**
     * 添加人
     */
    private String addUser;

}
