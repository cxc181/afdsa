package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Getter
@Setter
public class CrowdAccoutVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 手机号
     */
    @Excel(name = "手机号")
    private String memberAccount;

    /**
     * 用户姓名
     */
    @Excel(name = "用户姓名")
    private String  realName;
}
