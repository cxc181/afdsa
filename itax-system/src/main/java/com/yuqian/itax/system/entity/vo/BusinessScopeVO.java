package com.yuqian.itax.system.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 经营范围VO
 * @Author  Kaven
 * @Date   2020/7/31 09:10
*/
@Getter
@Setter
public class BusinessScopeVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 经营内容
     */
    @ApiModelProperty(value = "经营内容")
    private String businessContent;
}
