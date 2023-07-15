package com.yuqian.itax.yishui.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class EnterpriseLoginReq implements Serializable {
    /**
     * 用户名
     */
    private String user_name;
    /**
     * 密码
     */
    private String password;
    /**
     * 时间戳
     */
    private String timestamp;
}
