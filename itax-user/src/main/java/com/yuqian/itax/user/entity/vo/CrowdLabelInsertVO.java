package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Getter
@Setter
public class CrowdLabelInsertVO implements Serializable {

    private static final long serialVersionUID = -1L;

    @Excel(name = "手机号")
    private String phoneNumber;

    @Excel(name = "失败原因")
    private String  reg;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 判断该条数据是否为重复数据
     */
    private String status = "";
}
