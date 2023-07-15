package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
public class ParkSelectVO implements Serializable {
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
     * 协议模板id
     */
    private Long agreementTemplateId;

    /**
     * 模板名称
     */
    private String templateName;
}
