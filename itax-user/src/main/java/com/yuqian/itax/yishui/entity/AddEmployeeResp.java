package com.yuqian.itax.yishui.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class AddEmployeeResp extends YiShuiBaseResp implements Serializable {

    /**
     * 签约ID（后续解约时候需要用到）
     */
    private Long enterprise_professional_facilitator_id;

    /**
     * 人员ID（后续付款需要用到）
     */
    private Long professional_id;

    /**
     * 人员编号
     */
    private String professional_sn;
}
