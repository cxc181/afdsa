package com.yuqian.itax.snapshot.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 园区交易量统计VO
 */
@Setter
@Getter
public class InvoiceOrderSnapshotParkVO implements Serializable {

    private static final long serialVersionUID = -1L;


    /**
     * 所属园区
     */
    @Excel(name = "所属园区")
    private  String parkName;

    /**
     * 个体总数
     */
    @Excel(name = "个体总数")
    private  int companyNumber;
    /**
     * 注销总数
     */
    @Excel(name = "注销总数")
    private  int cannelCompanyNumber;
    /**
     * 新增个体总数
     */
    @Excel(name = "新增个体总数")
    private  int addCompanyNumber;
    /**
     * 新增开票金额
     */
    @Excel(name = "新增开票金额")
    private BigDecimal addTotalInvoiceAmount;
    /**
     * 新增专票金额
     */
    @Excel(name = "新增专票金额")
    private BigDecimal addZpInvoiceMoney;
    /**
     * 新增普票金额
     */
    @Excel(name = "新增普票金额")
    private BigDecimal addPpInvoiceMoney;
    /**
     * 新增开票订单数
     */
    @Excel(name = "新增开票订单数")
    private  int addTotalInvoiceNumber;
    /**
     * 新增注销个体
     */
    @Excel(name = "新增注销个体")
    private  int addCannelCompanyNumber;
    /**
     * 累计开票总金额
     */
    @Excel(name = "累计开票总金额")
    private BigDecimal totalInvoiceAmount;
    /**
     * 累计专票总金额
     */
    @Excel(name = "累计专票总金额")
    private BigDecimal zpInvoiceMoney;
    /**
     * 累计普票总金额
     */
    @Excel(name = "累计普票总金额")
    private BigDecimal ppInvoiceMoney;
}
