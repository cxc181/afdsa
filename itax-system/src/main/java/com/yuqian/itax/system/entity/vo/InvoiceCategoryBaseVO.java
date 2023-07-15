package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
public class InvoiceCategoryBaseVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 税收分类编码
     */
    @Excel(name = "税收分类编码")
    private String taxClassificationCode;

    /**
     * 税收分类名称
     */
    @Excel(name = "税收分类名称")
    private String taxClassificationName;

    /**
     * 税收分类简称
     */
    @Excel(name = "税收分类简称")
    private String taxClassificationAbbreviation;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称")
    private String goodsName;

    /**
     * 修改时间
     */
    @Excel(name = "更新时间", replace = {"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date updateTime;

    /**
     * 修改人
     */
    @Excel(name = "操作人")
    private String updateUser;

    /**
     * 备注
     */
    private String remark;

    /**
     *  是否有企业园区，机构关联类目：0-没有 1-有
     */
    private Integer flag;

    /**
     * 增值税率 (用逗号分隔)
     */
    @Excel(name = "增值税率(%)")
    private String vatFeeRate;

}
