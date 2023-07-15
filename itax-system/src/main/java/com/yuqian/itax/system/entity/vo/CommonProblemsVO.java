package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 常见问题返回实体
 */
@Setter
@Getter
public class CommonProblemsVO implements Serializable {
    private static final long serialVersionUID = -1L;

    private Long id;

    /**
     *问题
     */
    private String problem;
    /**
     *答案
     */
    private String answer;

    /**
     *排序
     */
    private Integer orderNum;
    /**
     *创建时间
     */
    private Date addTime;
    /**
     *修改时间
     */
    private Date updateTime;

    /**
     *修改时间
     */
    private String updateUser;

    /**
     *OEM机构
     */
    private String oemName;

    /**
     *OEM机构编码
     */
    private String oemCode;
}
