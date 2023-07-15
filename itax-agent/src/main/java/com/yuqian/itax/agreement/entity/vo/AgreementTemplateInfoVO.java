package com.yuqian.itax.agreement.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AgreementTemplateInfoVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 模板id
     */
    private Long id;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 园区编码
     */
    private String parkCode;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板地址
     */
    private String templateHtmlUrl;

    /**
     * 模板类型
     */
    private Integer templateType;

    /**
     * 模板名称
     */
    private String templateName;
}
