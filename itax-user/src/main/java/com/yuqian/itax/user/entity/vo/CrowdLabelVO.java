package com.yuqian.itax.user.entity.vo;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Getter
@Setter
public class CrowdLabelVO extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 标签名称
     */
    @Excel(name = "人群标签名称")
    @ApiModelProperty(value = "人群标签名称")
    private String crowdLabelName;

    /**
     * oemCode
     */
    @ApiModelProperty(value = "oemCode")
    private String oemCode;

    /**
     * oemName
     */
    @Excel(name = "oem机构")
    @ApiModelProperty(value = "oem机构")
    private String oemName;

    /**
     * 用户数
     */
    @Excel(name = "用户数")
    @ApiModelProperty(value = "用户数")
    private Integer memberUserNum;

    /**
     * 状态
     */
    @Excel(name = "状态",replace = { "-_null", "正常_1", "已作废_2"})
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 添加用户方式 1-列表导入 2-指定接入方
     */
    @Excel(name = "状态",replace = { "-_null", "列表导入_1", "指定H5接入方_2"})
    private Integer addUserMode;

    /**
     * 标签描述
     */
    @Excel(name = "标签描述")
    @ApiModelProperty(value = "标签描述")
    private String crowdLabelDesc;

    /**
     * 接入方id
     */
    private Long accessPartyId;

    private String remark;

}
