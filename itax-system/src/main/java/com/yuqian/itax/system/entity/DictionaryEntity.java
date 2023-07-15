package com.yuqian.itax.system.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 字典表
 * 
 * @Date: 2019年12月06日 12:08:39 
 * @author Kaven
 */
@Getter
@Setter
@Table(name="sys_e_dictionary")
public class DictionaryEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 编码
     */
    @ApiModelProperty(value = "编码")
    private String dictCode;

    /**
     * 值
     */
    @ApiModelProperty(value = "值")
    private String dictValue;

    /**
     * 上级id
     */
    @ApiModelProperty(value = "上级id")
    private Long parentDictId;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String dictDesc;

    /**
     * 添加时间
     */
    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    /**
     * 添加人
     */
    @ApiModelProperty(value = "添加人")
    private String addUser;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updateUser;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
	private String remark;

}
