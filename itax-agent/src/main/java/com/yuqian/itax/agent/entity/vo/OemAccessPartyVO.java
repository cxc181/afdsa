package com.yuqian.itax.agent.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class OemAccessPartyVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * id
     */
    private Long id;

    /**
     * 接入方名称
     */
    private String accessPartyName;

    /**
     * 接入方编号
     */
    private String accessPartyCode;

    /**
     * 所属oem机构
     */
    private String oemCode;

    /**
     * 状态  1-上架 2-下级
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String addUser;

    /**
     * 创建时间
     */
    private Date addTime;

    /**
     * oemName
     */
    private String oemName;
}
