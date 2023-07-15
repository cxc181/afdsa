package com.yuqian.itax.profits.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class MyProfitsDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;

    private Long id;

    /**
     * 订单编号
     */
    @Excel(name = "订单编号")
    private String orderNo;
    /**
     * 订单类型1-会员升级 2-工商注册 3-开票
     */
    @Excel(name = "订单类型", replace = { "会员升级_1","工商注册_2","开票_3","工商注销_4"}, height = 10, width = 22)
    private Integer orderType;
    /**
     * 分润流水号
     */
    @Excel(name = "分润流水号")
    private String profitsNo;
    /**
     * 入账金额（元）
     */
    @Excel(name = "入账金额（元）")
    private BigDecimal orderAmount;
    /**
     * 分润率
     */
    @Excel(name = "分润率")
    private String profitsRate;
    /**
     * 分润金额（元）
     */
    @Excel(name = "分润金额（元）")
    private BigDecimal profitsAmount;
    /**
     * 分润状态 0-待分润 1-分润中 2-已分润待结算 3-已分润已结算 4-分润失败
     */
    @Excel(name = "分润状态", replace = { "待分润_0","分润中_1","已分润待结算_2","已分润已结算_3","分润失败_4"}, height = 10, width = 22)
    private Integer profitsStatus;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date addTime;
    /**
     * 出款方
     */
    @Excel(name = "出款方")
    private String oemName;

}
