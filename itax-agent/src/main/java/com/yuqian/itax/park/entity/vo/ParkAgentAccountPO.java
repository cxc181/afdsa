package com.yuqian.itax.park.entity.vo;

import com.yuqian.itax.util.validator.IdCard;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Setter
@Getter
public class ParkAgentAccountPO implements Serializable {
    private static final long serialVersionUID = -1L;


    /**
     * 园区id
     */
    Long parkId;
    /**
     * 经办人账号
     */
    String  agentAccount;
    /**
     * 经办人姓名
     */
    String agentName;

    /**
     * 身份证号码
     */
    @NotBlank(message="身份证号码不能为空")
    @IdCard
    private String idCardNo;

    /**
     * 身份证正面照地址
     */
    @NotBlank(message="身份证正面照地址不能为空")
    private String idCardFront;

    /**
     * 身份证反面照地址
     */
    @NotBlank(message="身份证反面照地址不能为空")
    private String idCardBack;
}
