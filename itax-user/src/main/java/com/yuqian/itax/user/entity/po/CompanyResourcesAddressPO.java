package com.yuqian.itax.user.entity.po;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
public class CompanyResourcesAddressPO implements Serializable {
    private static final long serialVersionUID = -1L;

    private  Long id ;


    /**
     * 所在地变更为
     */
    private String address;
    /**
     * 备注
     */
    private String remark;
}
