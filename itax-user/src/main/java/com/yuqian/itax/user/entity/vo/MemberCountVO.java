package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/18 10:14
 *  @Description: 员工数量统计VO
 */
@Getter
@Setter
public class MemberCountVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 邀请员工上限
     */
    @ApiModelProperty(value = "邀请员工上限")
    private Integer limitCount;

    /**
     * 当前邀请员工数量
     */
    @ApiModelProperty(value = "当前邀请员工数量")
    private Integer totalCount;
}
