package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/25 14:22
 *  @Description: 订单查询bean-拓展宝
 */
@Getter
@Setter
public class TZBOrderQuery extends BaseQuery implements Serializable {
    /**
     * OEM机构编号
     */
    private String oemCode;

    /**
     * 订单创建时间开始
     */
//    @NotNull(message="订单创建开始时间不能为空")
//    @Pattern(regexp = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]", message = "订单创建开始时间格式不正确")
    private String createTimeStart;

    /**
     * 订单创建时间结束
     */
//    @NotNull(message="订单创建结束时间不能为空")
//    @Pattern(regexp = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]", message = "订单创建结束时间格式不正确")
    private String createTimeEnd;

    /**
     * 订单状态:0-待签字认证 1-待视频认证 2-审核中 3-待付款 4-出证中 5-已完成 6-已取消
     */
    private Integer orderStatus;

    /**
     * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
     */
    private Integer companyType;

    /**
     * 注册手机号
     */
    private String regPhone;

    /**
     * 注册身份证号码
     */
    private String idCard;

    /**
     * 注册企业名称
     */
    private String registerName;

    /**
     * （开票/注销）企业名称
     */
    private String companyName;

    /**
     * 行业名称
     */
    private String industryName;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 发票类型 1-普通发票 2-增值税发票
     */
    private Integer invoiceType;

    /**
     * 订单号
     */
    private String orderNo;
}