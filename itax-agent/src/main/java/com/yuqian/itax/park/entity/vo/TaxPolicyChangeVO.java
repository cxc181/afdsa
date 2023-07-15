package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class TaxPolicyChangeVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 税费规则配置json
     */
    private String taxRulesConfigJson;

    /**
     * 修改人
     */
    private String addUser;

    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 备注
     */
    private String remark;
}
