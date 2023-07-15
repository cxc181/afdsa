package com.yuqian.itax.coupons.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class CouponExchangeCodeVO implements Serializable {
    private static final long serialVersionUID = 7476995990063514365L;

    private Long id;
    /**
     * 兑换码
     */
    @Excel(name = "兑换码")
    private String exchangeCode;
    /**
     * 兑换码名称
     */
    @Excel(name = "兑换码名称")
    private String exchangeName;
    /**
     * 限量兑换张数
     */
    @Excel(name = "限量兑换张数")
    private Integer limitedNumber;
    /**
     * 每人可兑换张数
     */
    @Excel(name = "每人可兑换张数")
    private Integer exchangeNumberPerson;
    /**
     * 已兑换张数
     */
    @Excel(name = "已兑换张数")
    private Integer hasExchangeNumber;
    /**
     * 状态 0-未生效 1-已生效 2-已过期 3-已作废 4-已暂停
     */
    @Excel(name = "状态" , replace = { "未生效_0","已生效_1","已过期_2","已作废_3","已暂停_4" })
    private Integer status;
    /**
     * 优惠卷编码
     */
    @Excel(name = "优惠卷编码")
    private String couponsCode;
    /**
     * 优惠券名称
     */
    @Excel(name = "优惠券名称")
    private String couponsName;
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
    @JSONField(format = "yyyy-MM-dd")
    private String startDate;

    /**
     * 有效期结束
     */
    @JSONField(format = "yyyy-MM-dd")
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
    private String remark;

}
