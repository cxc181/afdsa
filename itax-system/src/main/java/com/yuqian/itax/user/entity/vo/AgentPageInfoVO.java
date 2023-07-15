package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class AgentPageInfoVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 账号
     */
    private String username;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 名称
     */
    private String nickname;
    /**
     * 平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
     */
    private Integer platformType;

    /**
     * 创建时间
     */
    private Date addTime;

    /**
     * 关联推广账号
     */
    private String bindingAccount;
    /**
     * 状态 0-禁用 1-可用 2-注销
     */
    private Integer status;
    /**
     * 机构名称
     */
    private String oemName;


}
