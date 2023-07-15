package com.yuqian.itax.park.entity.vo;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class ParkListVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 园区名称
     */
    private String parkName;

    /**
     * 园区编码
     */
    private String parkCode;

    /**
     * 园区所在地
     */
    private String parkCity;
    /**
     * 园区账号
     */
    private String username;
    /**
     * 园区账号手机号
     */
    private String phone;
    /**
     * 创建时间
     */
    private Date addTime;
    /**
     * 园区状态
     */
    private Integer status;

    /**
     * 协议模板id
     */
    private Long agreementTemplateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 所得税征收方式 1-查账征收 2-核定征收
     */
    private Integer incomeLevyType;

    /**
     * 园区类型 1-自营园区  2-合作园区 3-外部园区
     */
    private Integer parkType;
}
