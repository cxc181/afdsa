package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Setter
@Getter
public class ParkDisableWordInsertVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 禁用字号
     */
    @Excel(name = "禁用字号")
    private String disableWord;

    /**
     * 失败原因
     */
    @Excel(name = "失败原因")
    private String  reg;

    /**
     * 是否正确数据标识
     */
    private boolean flag = true;
}
