package com.yuqian.itax.agent.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class OemAccessPartyInfoVO implements Serializable {

    private static final long serialVersionUID = -1L;


    /**
     * id
     */
    private Long id;

    /**
     * 接入方名称
     */
    private String accessPartyName;
}
