package com.yuqian.itax.agreement.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 协议模板展示数据
 */
@Getter
@Setter
public class AgreementTemlateListVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * id
     */
    private Long id;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板类型  1-收费标准 2-委托注册协议  3-园区办理协议
     */
    private Integer templateType;

    /**
     * 模板html地址
     */
    private String templateHtmlUrl;

    /**
     * 模板状态  1-启用 2-禁用
     */
    private Integer templateStatus;

    /**
     * 添加人
     */
    private String addUser;

    /**
     * 添加时间
     */
    private Date addTime;
    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 模板说明
     */
    private String templateDesc;

    /**
     * 模板显示名称
     */
    private String templateShowName;
}
