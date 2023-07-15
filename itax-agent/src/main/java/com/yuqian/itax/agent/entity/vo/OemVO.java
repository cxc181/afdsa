package com.yuqian.itax.agent.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
public class OemVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 机构账号
     */
    private  String oemName;
    /**
     * 机构编号
     */
    private String oemCode;
}
