package com.yuqian.itax.park.entity.vo;

import com.yuqian.itax.park.entity.ParkRewardPolicyLabelEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class ParkDetailOfMenuVO implements Serializable {
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
     * 园区所在地
     */
    private String parkCity;

    /**
     * 运营主体
     */
    private String belongsCompanyName;

    /**
     * 园区详细地址
     */
    private String parkAddress;

    /**
     * 用户评分
     */
    private BigDecimal userRatings;

    /**
     * 评分人数
     */
    private Long userRatingsNumber;

    /**
     * 园区实景图
     */
    private String parkImgs;

    /**
     * 园区实景图列表
     */
    private List<String> parkImgList;

    /**
     * 支持的企业类型
     */
    private String companyTypes;

    /**
     * 税收政策说明
     */
    private String taxPolicyDesc;

    /**
     * 政策标签列表
     */
    private List<ParkRewardPolicyLabelEntity> policyLabels;

    /**
     * 工商注册说明
     */
    private String registerDesc;

    /**
     * 税务办理说明
     */
    private String taxHandleDesc;

    /**
     * 对公户办理说明
     */
    private String corporateAccountHandleDesc;
}
