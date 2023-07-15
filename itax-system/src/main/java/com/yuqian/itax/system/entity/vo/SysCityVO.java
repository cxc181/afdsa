package com.yuqian.itax.system.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 17:14
 *  @Description: 市
 */
@Getter
@Setter
public class SysCityVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /**
     * 省编号
     */
    @ApiModelProperty(value = "省编号")
    private String provinceCode;

    /**
     * 市名称
     */
    @ApiModelProperty(value = "市名称")
    private String code;

    /**
     * 市名称
     */
    @ApiModelProperty(value = "市名称")
    private String name;

}
