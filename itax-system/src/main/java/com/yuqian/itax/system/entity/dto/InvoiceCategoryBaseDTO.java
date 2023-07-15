package com.yuqian.itax.system.entity.dto;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: HZ
 *  @Description: 类目库DTO
 */
@Getter
@Setter
public class InvoiceCategoryBaseDTO implements Serializable {

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
     * 失败原因
     */
    @Excel(name = "失败原因")
    private String failed;
    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 添加人
     */
    private String addUser;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 备注
     */
    private String remark;
}
