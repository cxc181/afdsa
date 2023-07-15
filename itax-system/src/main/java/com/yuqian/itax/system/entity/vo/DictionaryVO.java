package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
@Setter
@Getter
public class DictionaryVO implements Serializable {

    private static final long serialVersionUID = -1L;

    private  Long id;
    /**
     * 编码
     */
    private  String dictCode;

    /**
     * 值
     */
    private  String dictValue;

    /**
     * 上级CODE
     */
    private  String parentDictCode;

    /**
     * 创建时间
     */
    private Date addTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}
