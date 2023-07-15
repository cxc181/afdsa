package com.yuqian.itax.park.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Setter
@Getter
public class ParkBusinessScopeDTO implements Serializable {

    /**
     * 园区id
     */
    @NotNull(message="园区id不能为空")
    private Long parkId;

    /**
     * 经营范围
     */
    @NotBlank(message="经营范围不能为空")
    @Size(max = 100,message = "经营范围不能超过100个字符")
    private String businessScopeName;
}
