package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 推广记录明细-推广用户VO
 * @Author  Kaven
 * @Date   2020/6/7 10:38 下午
*/
@Getter
@Setter
public class ExtendMemberVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 会员账号
     */
    private String memberAccount;
    /**
     * 会员手机号
     */
    private String memberPhone;
    /**
     * 会员昵称
     */
    private String memberName;
    /**
     * 用户名
     */
    private String realName;
    /**
     * 会员等级
     */
    private String levelName;
    /**
     * 企业注册审核驳回原因
     */
    private String dismissReason;
    /**
     * 注册时间
     */
    private Date registTime;
    /**
     * 审核时间
     */
    private Date updateTime;
    /**
     * 会费
     */
    private Long memberFee;
    /**
     * 完成时间
     */
    private Date finishTime;
    /**
     * 企业名称
     */
    private String companyName;
    /**
     * 累计开票
     */
    private Long totalInvoiceAmount;
    /**
     * 开票金额
     */
    private Long invoiceAmount;
    /**
     * 开票服务费
     */
    private Long invoiceServiceFee;
    /**
     * 注销服务费
     */
    private Long cancelFee;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 托管费
     */
    private Long registFee;
    /**
     * 订单状态
     */
    private Integer orderStatus;

    private String orderStatusName;// 状态名称
}
