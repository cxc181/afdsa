package com.yuqian.itax.agreement.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 *   协议模板新增参数
 */
@Getter
@Setter
public class AgreementTemplateDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 协议模板id
     */
    private Long id;

    /**
     * 模板名称
     */
    @NotBlank(message="模板名称不能为空")
    @Max(value = 32,message = "模板名称不能超过32位字符")
    private String templateName;

    /**
     * 模板编码
     */
    @NotNull(message="模板编码不能为空")
    private String templateCode;

    /**
     * 模板类型
     */
    @NotBlank(message="模板类型不能为空")
    private Integer templateType;

    /**
     * 模板内容
     */
    @NotBlank(message="模板内容不能为空")
    private String templateContent;

    /**
     * 模板说明
     */
    @Max(value = 100,message = "模板说明不能超过100位字符")
    private String templateDesc;

    /**
     * 模板显示名称
     */
    private String templateShowName;
}
