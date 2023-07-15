package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class SearchParkListVO implements Serializable {
    private static final long serialVersionUID = -386909181392716644L;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 园区名称
     */
    private String parkName;

    /**
     * 园区编码
     */
    private String parkCode;

    /**
     * 流程标记（1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程）
     */
    private Integer processMark;

    /**
     * 可选行业列表
     */
    private List<ParkInvoiceListVO> industryList;
}
