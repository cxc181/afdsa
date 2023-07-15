package com.yuqian.itax.product.entity.vo;

import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 园区产品模板信息
 */
@Getter
@Setter
public class ProductParkAgreementTemplateVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 园区id
     */
    private Long parkId;
    /**
     * 园区名称
     */
    private String parkName;
    /**
     * 流程标记 1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程
     */
    private Integer processMark;
    /**
     * 模板信息
     */
    List<AgreementTemplateInfoVO> agreementTemplateInfoVOList;
}
