package com.yuqian.itax.agreement.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 协议模板详情
 */
@Getter
@Setter
public class AgreementTemplateDetailVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * id
     */
    private Long id;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板类型  1-收费标准 2-委托注册协议  3-园区办理协议
     */
    private Integer templateType;

    /**
     * 模板说明
     */
    private String templateDesc;

    /**
     * 模板内容
     */
    private String templateContent;

    /**
     * 模板显示名称
     */
    private String templateShowName;

}
