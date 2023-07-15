package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 开票订单批量下载开票信息返回实体
 * @author：pengwei
 * @Date：2019/12/10 18:52
 * @version：1.0
 */
@Getter
@Setter
public class BatchInvOrderExportVO implements Serializable {
    /**
     * 订单号
     */
    @Excel(name = "订单编号")
    private String orderNo;
    /**
     * 开票记录编号
     */
    @Excel(name = "开票记录编号")
    private String invoiceRecordNo;
    /**
     * 企业名称
     */
    @Excel(name = "开票企业")
    private String memberCompanyName;

    /**
     * 开票方式 1-自助开票 2-集团批量开票 3-佣金提现开票
     */
    @Excel(name = "开票方式 " , replace = { "自助开票_1","集团批量开票_2","佣金提现开票_3","接入方开票_5" })
    private String createWay;

    /**
     * 经营者姓名
     */
    @Excel(name = "经营者姓名")
    private String operatorName;
    /**
     * 经营者电话
     */
    @Excel(name = "经营者手机号")
    private String operatorTel;

    /**
     * 经营者地址
     */
    @Excel(name = "经营者地址")
    private String businessAddress;

    /**
     * 发票抬头
     */
    @Excel(name = "发票抬头")
    private String invCompanyName;
    /**
     * 抬头公司地址
     */
    @Excel(name = "抬头公司地址")
    private String companyAddress;

    /**
     * 税号
     */
    @Excel(name = "税号")
    private String ein;

    /**
     * 电话号码
     */
    @Excel(name = "电话号码")
    private String phone;
    /**
     * 抬头开户银行
     */
    @Excel(name = "开户银行")
    private String bankName;
    /**
     * 抬头银行账号
     */
    @Excel(name = "银行账号")
    private String bankNumber;
    /**
     * 开票金额
     */
    @Excel(name = "开票金额")
    private BigDecimal invoiceAmount;

    /**
     * 增值税率
     */
    @Excel(name = "增值税率(%)")
    private BigDecimal vatFeeRate;

    /**
     * 发票类型 1-普通发票 2-增值税发票
     */
    @Excel(name = "开票类型 " , replace = { "普通发票_1","增值税发票_2" })
    private String invoiceTypeName;

    /**
     * 发票方式 1-纸质发票 2-电子发票
     */
    @Excel(name = "发票类型 " , replace = { "纸质发票_1","电子发票_2" })
    private String invoiceWay;

    /**
     * 开票类目
     */
    @Excel(name = "开票类目", replace = {"详见商品明细表_null"})
    private String categoryName;
    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 发票备注
     */
    @Excel(name = "发票备注", width = 20)
    private String invoiceRemark;

    /**
     * 收货人姓名
     */
    @Excel(name = "收货人姓名", width = 16)
    private String recipient;

    /**
     * 收货人手机号
     */
    @Excel(name = "收货人手机号", width = 16)
    private String recipientPhone;

    /**
     * 收货省份
     */
    @Excel(name = "收货省份")
    private String provinceName;

    /**
     * 城市
     */
    @Excel(name = "城市")
    private String cityName;

    /**
     * 区域
     */
    @Excel(name = "区域")
    private String districtName;

    /**
     * 详细地址
     */
    @Excel(name = "详细地址", width = 20)
    private String recipientAddress;
    /**
     * 对公户账号
     */
    @Excel(name = "对公户账号")
    private String corporateAccount;
    /**
     * 对公户银行
     */
    @Excel(name = "对公户银行")
    private String corporateAccountBankName;

    /**
     * 补充说明
     */
    @Excel(name = "订单备注")
    private String supplementExplain;
}
