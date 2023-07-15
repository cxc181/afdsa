package com.yuqian.itax.user.entity.po;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
public class CustomerServiceWorkPO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 选择用户ID
     */
    private  Long userId;

    /**
     * 工号
     */
    private String workNumber;
    /**
     * 工号姓名
     */
    private  String workNumberName;

    /**
     * 初始姓名
     */
    private String workNumberPwd;


}
