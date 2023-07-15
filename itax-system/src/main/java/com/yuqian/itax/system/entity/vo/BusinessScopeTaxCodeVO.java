package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class BusinessScopeTaxCodeVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 经营范围
     */
    @Excel(name = "经营范围")
    private String businessScopName;

    /**
     * 税收分类编码
     */
    @Excel(name = "税收分类编码")
    private String taxClassificationCode;

    /**
     * 税费分类名称
     */
    @Excel(name = "税费分类名称")
    private String taxClassificationName;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

    /**
     * 更新时间
     */
    @Excel(name = "更新时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date updateTime;

    /**
     * 添加人
     */
    @Excel(name = "操作人")
    private String updateUser;

}
