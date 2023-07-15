package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class ConsumptionInvoiceOrderVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 消费发票申请ID
     */
    private Long id;
    /**
     * 申请单编号
     */
    @Excel( name = "申请单编号")
    private String orderNo;
    /**
     * 注册账号
     */
    @Excel( name = "注册账号")
    private String memberAccount;
    /**
     * 姓名
     */
    @Excel( name = "姓名")
    private String realName;
    /**
     * 发票金额
     */
    @Excel( name = "发票金额")
    private BigDecimal invoiceAmount;

    /**
     *申请状态（0-待出票 1-出票中 2-已出票 3-出票失败）
     */
    @Excel( name = "申请状态", replace = { "待出票_0","出票中_1","已完成_2","出票失败_3","待发货_4","待签收_5"})
    private Long orderStatus;

    /**
     *申请时间
     */
    @Excel(name = "申请时间", replace = { "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date addTime;

    /**
     *出票时间
     */
    @Excel(name = "出票时间", replace = { "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date completeTime;

    /**
     * 抬头公司
     */
    @Excel( name = "抬头公司")
    private String companyName;

    /**
     * 税号
     */
    private String ein;
    /**
     * 公司地址
     */
    private String companyAddress;
    /**
     * 电话号码
     */
    private String phone;
    /**
     * 开户行
     */
    private String bankName;
    /**
     * 银行账户
     */
    private String bankNumber;
    /**
     * 发票类目
     */
    @Excel( name = "发票类目")
    private String categoryName;

    /**
     * 发票方式 1-纸质发票 2-电子发票
     */
    @Excel( name = "发票方式", replace = { "纸质发票_1","电子发票_2"})
    private String invoiceWay;
    /**
     * 接收邮箱
     */
    //@Excel( name = "接收邮箱")
   // private String billToEmail;

    /**
     * 电子发票地址
     */
    private String invoicePdfUrl;

    /**
     * 发票图片
     */
    private String invoiceImgs;

    /**
     * 发票图片列表
     */
    private String[] invoiceImgList;

    /**
     * 备注
     */
    @Excel( name = "备注")
    private String remark;

    /**
     * 所属OEM
     */
    @Excel( name = "所属OEM")
    private String oemName;

    /**
     * 所属OEM code
     */
    private String oemCode;

    /**
     * 关联消费发票
     */
    private String consumptionOrderRela;

}
