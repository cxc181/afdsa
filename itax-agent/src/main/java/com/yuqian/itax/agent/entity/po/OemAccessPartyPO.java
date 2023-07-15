package com.yuqian.itax.agent.entity.po;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Setter
@Getter
public class OemAccessPartyPO implements Serializable {

    private static final long serialVersionUID = -1L;

    private  Long id;

    /**
     * oemCode
     */
    @NotBlank(message = "请选择oem机构")
    private String oemCode;

    /**
     * 接入方编码
     */
    @NotBlank(message = "请输入接入方编码")
    @Size(min = 6, max = 6, message = "编码为6位数字和大写字母组成")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,6}$", message = "请输入6位，数字和大写字母组成")
    private String accessPartyCode;

    /**
     * 接入方名称
     */
    @NotBlank(message = "请输入接入方名称")
    @Size(min = 1, max = 10, message = "接入方名称不能超过十个字符")
    private String accessPartyName;

    /**
     * 备注
     */
    @NotBlank(message = "描述不能为空")
    @Size(min = 1, max = 100, message = "描述不能超过一百个字符")
    private String remark;
}
