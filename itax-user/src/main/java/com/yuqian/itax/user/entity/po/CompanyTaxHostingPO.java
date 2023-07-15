package com.yuqian.itax.user.entity.po;

import lombok.Setter;
import lombok.Getter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
public class CompanyTaxHostingPO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 企业id
     */
    @NotNull(message = "企业id不能为空,请选择企业")
    private Long companyId;

    /**
     * 税务盘类型 1-ukey 2-税控盘
     */
    //@NotNull(message = "请选择税务盘类型")
    private Integer taxDiscType;

    /**
     * 税务盘编号
     */
    //@NotNull(message = "请输入税务盘编号")
    private String taxDiscCode;

    /**
     * 通道方 1-百旺 2-园区
     */
    //@NotNull(message = "请选择通道方")
    private Integer channel;

    /**
     * 托管状态 0-未托管 1-已托管
     */
    @NotNull(message = "托管状态不能为空,请选择托管状态")
    private Integer hostingStatus;
    /**
     * 票面金额类型 1-1w 2-10w 3-100w
     */
    //@NotNull(message = "请选择票面金额类型")
    private Integer faceAmountType;
    /**
     * 票面金额(分)
     */
    //@NotNull(message = "请选择票面金额类型")
    private Long faceAmount;

}
