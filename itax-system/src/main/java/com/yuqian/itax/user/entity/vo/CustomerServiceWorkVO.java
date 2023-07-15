package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class CustomerServiceWorkVO implements Serializable {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 账号
     */
    private String username;
    /**
     * 坐席姓名
     */
    private String nickname;
    /**
     * 工号
     */
    private String workNumber;
    /**
     * 姓名
     */
    private String workNumberName;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 新增时间
     */
    private Date addTime;
    /**
     * 所属OEM
     */
    private String oemName;
}
