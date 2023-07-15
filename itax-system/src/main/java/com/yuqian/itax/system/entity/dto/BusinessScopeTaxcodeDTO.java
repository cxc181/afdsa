package com.yuqian.itax.system.entity.dto;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class BusinessScopeTaxcodeDTO implements Serializable {

    private static final long serialVersionUID = -1L;


    /**
     * 经营范围
     */
    @Size(min = 1, max = 100, message = "经营范围不能超过100个字")
    @NotBlank(message="经营范围不能为空")
    private String businessScopName;

    /**
     * 税收分类编码
     */
    @Size(min = 1, max = 50, message = "税收分类编码不能超过50个字")
    @NotBlank(message="税收分类编码不能为空")
    @Pattern(regexp = "^[0-9]*$", message = "税收分类编码只支持数字")
    private String taxClassificationCode;

    /**
     * 税费分类名称
     */
    @Size(min = 1, max = 50, message = "税费分类名称不能超过50个字")
    @NotBlank(message="税费分类名称不能为空")
    private String taxClassificationName;

    /**
     * 备注
     */
    @Size(min = 1, max = 100, message = "备注不能超过100个字")
    private String remark;
}
