package com.yuqian.itax.util.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class UserAuthVO {

    /**
     * 开始时间
     */
    private String beginDate;

    /**
     * 身份证地址
     */
    private String idCardAddr;

    /**
     * 结束时间
     */
    private String endDate;

    /**
     * 实名状态 0-未实名 1-已实名
     */
    private Integer authStatus;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 映射id
     */
    private String userId;

    /**
     * 身份证正面照地址
     * @param node
     * @return
     */
    private String idCardFront;

    /**
     * 身份证反面照地址
     * @param node
     * @return
     */
    private String idCardBack;
}
