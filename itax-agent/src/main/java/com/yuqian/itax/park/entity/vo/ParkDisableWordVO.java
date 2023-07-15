package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class ParkDisableWordVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * id
     */
    @Excel(name = "序号")
    private Long id;

    /**
     * 禁用字
     */
    @Excel(name = "禁用字")
    private String disableWord;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date  addTime;

    /**
     * 创建人
     */
    @Excel(name = "创建人")
    private String addUser;
}
