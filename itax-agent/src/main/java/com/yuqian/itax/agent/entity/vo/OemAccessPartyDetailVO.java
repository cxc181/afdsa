package com.yuqian.itax.agent.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class OemAccessPartyDetailVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 人群标签id
     */
    private Long id;

    /**
     * oemCode
     */
    private String oemCode;

    /**
     * oem机构名
     */
    private String oemName;

    /**
     * 标签名称
     */
    private String crowdLabelName;

    /**
     * 用户数
     */
    private Integer memberUserNum;

    /**
     * 添加用户方式 1-列表导入 2-指定接入方
     */
    private Integer addUserMode;

    /**
     * 接入方id
     */
    private Long accessPartyId;

    /**
     * 接入方名称
     */
    private String accessPartyName;

    /**
     * 标签描述
     */
    private String crowdLabelDesc;

}
