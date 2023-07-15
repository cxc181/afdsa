package com.yuqian.itax.user.entity.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 推广记录明细（企业注册）展示VO
 * @Author  Kaven
 * @Date   2020/6/7 10:13 下午
*/
@Getter
@Setter
public class ComRegExtendDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 未注册企业
     */
    @ApiModelProperty(value = "未注册企业")
    private Integer unRegComCount;

    /**
     * 总托管费
     */
    @ApiModelProperty(value = "总托管费")
    private Long totalRegistFee;

    /**
     * 待提交订单
     */
    @ApiModelProperty(value = "待提交订单")
    private Integer toBeSubmitCount;

    /**
     * 审核中
     */
    @ApiModelProperty(value = "审核中")
    private Integer toBeCheckCount;

    /**
     * 核名驳回
     */
    @ApiModelProperty(value = "核名驳回")
    private Integer toDismissCount;

    /**
     * 出证中
     */
    @ApiModelProperty(value = "出证中")
    private Integer toCheckoutCount;

    /**
     * 已完成
     */
    @ApiModelProperty(value = "已完成")
    private Integer finishedCount;

    /**
     * 推广订单列表
     */
    @ApiModelProperty(value = "推广订单列表")
    private PageInfo<ExtendMemberVO> orderPageList;
}
