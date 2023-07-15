package com.yuqian.itax.yishui.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Getter
@Setter
public class IssuingDataDto implements Serializable {

    /**
     * 人员ID
     */
    private Long professional_id;

    /**
     * 人员姓名
     */
    private String name;
    /**
     * 人员身份证号
     */
    private String cer_code;

    /**
     * 人员手机号码
     */
    private String mobile;
    /**
     * 人员银行卡号
     */
    private String bank_code;

    /**
     * 支付金额 (单位元 精确到小数点2位)
     */
    private String money;

    /**
     * 业绩说明
     */
    private String remark;

    /**
     * 第三方单号（贵公司系统对应的明细单号
     */
    private String request_no;

    /**
     * 多卡业务 银行卡ID 默认传0
     */
    private Long professional_bank_id = 0L;

    /**
     * 任务ID 可以通过项目众包根据项目获取项目的任务
     */
    private Integer resolve_id;

}
