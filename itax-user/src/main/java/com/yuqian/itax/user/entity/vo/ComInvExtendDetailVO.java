package com.yuqian.itax.user.entity.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 推广记录明细（企业开票）展示VO
 * @Author  Kaven
 * @Date   2020/6/7 10:13 下午
*/
@Getter
@Setter
public class ComInvExtendDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 待提交
     */
    @ApiModelProperty(value = "待提交")
    private Integer toBeSubmitCount;

    /**
     * 审核中
     */
    @ApiModelProperty(value = "审核中")
    private Integer toBeCheckCount;

    /**
     * 待签收
     */
    @ApiModelProperty(value = "待签收")
    private Integer toReceiptCount;

    /**
     * 已完成
     */
    @ApiModelProperty(value = "已完成")
    private Integer finishedCount;

    /**
     * 总服务费
     */
    @ApiModelProperty(value = "总服务费")
    private Long totalServiceFee;

    /**
     * 总开票
     */
    @ApiModelProperty(value = "总开票")
    private Long totalInvoiceAmount;

    /**
     * 推广订单列表
     */
    @ApiModelProperty(value = "推广订单列表")
    private PageInfo orderPageList;
}
