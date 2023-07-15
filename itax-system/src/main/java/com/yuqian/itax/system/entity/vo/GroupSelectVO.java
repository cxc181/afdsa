package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GroupSelectVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 类目基础表主键id
     */
    private Long id;


    /**
     * 类目名称
     */
    private String name;
}
