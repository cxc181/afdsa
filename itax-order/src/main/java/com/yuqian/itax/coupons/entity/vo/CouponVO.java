package com.yuqian.itax.coupons.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class CouponVO implements Serializable {
    private static final long serialVersionUID = 7476995990063514365L;

    private Long id;
    /**
     * 优惠券编码
     */
    @Excel(name = "优惠券编码")
    private String couponsCode;
    /**
     * 优惠券名称
     */
    @Excel(name = "优惠券名称")
    private String couponsName;
    /**
     * 面额
     */
    @Excel(name = "面额")
    private BigDecimal faceAmount;
    /**
     * 状态 0-未生效 1-已生效 2-已过期 3-已作废 4-已暂停
     */
    @Excel(name = "状态" , replace = { "未生效_0","已生效_1","已过期_2","已作废_3","已暂停_4" })
    private Integer status;
    /**
     * 可用范围  1-个体注册 2-个独注册 3-有限合伙注册 4-有限责任注册
     */
    @Excel(name = "可用范围", replace = { "个体户注册_1","个人独资企业注册_2","有限合伙公司注册_3","有限责任公司注册_4" })
    private String usableRange;
    /**
     * OEM机构
     */
    @Excel(name = "OEM机构")
    private String oemName;

    /**
     * OEM机构编码
     */
    private String oemCode;
    /**
     * 有效期
     */
    @Excel(name = "有效期")
    private String date;
    /**
     * 有效期开始
     */
    private String startDate;

    /**
     * 有效期结束
     */
    private String endDate;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date addTime;
    /**
     * 描述
     */
    @Excel(name = "描述")
    private String description;

    /**
     * 是否被使用 0-未使用 1-已使用
     */
    private Integer isUse;
}
