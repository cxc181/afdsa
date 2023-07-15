package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 推广记录明细展示VO
 * @Author  Kaven
 * @Date   2020/6/7 10:13 下午
*/
@Getter
@Setter
public class ExtendRecordDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 企业注册推广明细
     */
    @ApiModelProperty(value = "企业注册推广明细")
    private ComRegExtendDetailVO comRegistDetail;

    /**
     * 企业开票推广明细
     */
    @ApiModelProperty(value = "企业开票推广明细")
    private ComInvExtendDetailVO comInvoiceDetail;

    /**
     * 企业注销推广明细
     */
    @ApiModelProperty(value = "企业注销推广明细")
    private ComCancelExtendDetailVO comCancelDetail;

    /**
     * 会员升级推广明细
     */
    @ApiModelProperty(value = "会员升级推广明细")
    private MemberUpExtendDetailVO memberUpgradeDetail;

    /**
     * 企业托管费续费推广明细
     */
    @ApiModelProperty(value = "企业托管费续费推广明细")
    private ContComRegExtendDetailVO contComRegExtendDetailVO;
}