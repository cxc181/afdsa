package com.yuqian.itax.workorder.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class WorkOrderDescVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 操作坐席名称
     */
    private String customerServiceName;

    /**
     * 备注
     */
    private String descContent;

    /**
     * 操作时间
     */
    private Date addTime;
}
