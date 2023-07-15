package com.yuqian.itax.system.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description 开票类目和经营范围查询接收BEAN
 * @Author  Kaven
 * @Date   2020/7/30 17:31
*/
@Getter
@Setter
public class BizScopeDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 园区Code
     */
    @NotBlank(message="园区Code不能为空")
    @ApiModelProperty(value = "园区Code", required = true)
    private String parkCode;

    /**
     * 行业ID
     */
    @NotNull(message="行业ID不能为空")
    @ApiModelProperty(value = "行业ID", required = true)
    private Long industryId;
}