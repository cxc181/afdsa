package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 消费发票申请查询接受实体
 */
@Getter
@Setter
public class ConsumptionInvoiceOrderQuery  extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;


    /**
     * 申请单编号
     */
    private String orderNo;
    /**
     * 注册账号
     */
    private String memberAccount;
    /**
     * 姓名
     */
    private String memberName;

    /**
     * 申请状态0-待出票 1-出票中 2-已出票 3-出票失败
     */
    private Integer orderStatus;
    /**
     * 申请时间
     */
    private String startApplyTime;
    /**
     * 申请时间
     */
    private String endApplyTime;
    /**
     * 出票时间
     */
    private String startCompleteTime;
    /**
     * 出票时间
     */
    private String endCompleteTime;

    /**
     * 抬头公司
     */
    private String companyName;
    /**
     * 接收邮箱
     */
    private String billToEmail;
    /**
     * 所属OEM
     */
    private String oemCode;

    /**
     * 发票类型  纸质发票_1","电子发票_2
     */
    private String invoiceWay;
}
