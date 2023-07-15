package com.yuqian.itax.agreement.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 园区协议列表查询VO
 */
@Getter
@Setter
public class ParkAgreementsVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板类型  1-收费标准 2-委托注册协议  3-园区办理协议 4-企业章程
     */
    private Integer templateType;

    /**
     * 模板html地址(相对地址，公域)
     */
    private String templateHtmlUrl;

    /**
     * 模板说明
     */
    private String templateDesc;

    /**
     * 客服电话
     */
    private String customerServiceTel;
}
