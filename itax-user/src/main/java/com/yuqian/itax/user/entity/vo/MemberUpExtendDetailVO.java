package com.yuqian.itax.user.entity.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 推广记录明细（会员升级）展示VO
 * @Author  Kaven
 * @Date   2020/6/7 10:13 下午
*/
@Getter
@Setter
public class MemberUpExtendDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 总会费
     */
    @ApiModelProperty(value = "总会费")
    private Long totalMemberFee;

    /**
     * 推广订单列表
     */
    @ApiModelProperty(value = "推广订单列表")
    private PageInfo orderPageList;
}
