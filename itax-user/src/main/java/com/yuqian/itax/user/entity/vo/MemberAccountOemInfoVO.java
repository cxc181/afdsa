package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MemberAccountOemInfoVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 是否接入渠道 1-接入 0-不接入
     */
    private String paramsValue;

    private String oemCode;

}
