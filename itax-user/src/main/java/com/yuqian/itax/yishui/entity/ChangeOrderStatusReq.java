package com.yuqian.itax.yishui.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ChangeOrderStatusReq implements Serializable {

    /**
     * 批次ID
     */
    private String enterprise_order_id;

    /**
     * 1企业内部审核，5审核拒绝
     */
    private Integer status;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 具体结算清单,与seal_img二选一
     */
    private String apply_img;

    /**
     * 公章图片,与apply_img二选一
     */
    private String seal_img;

    /**
     * 银行验证码(当订单通道为三湘通道时候 需要验证码 其他通道留空)
     */
    private String verfiy_code;

}
