package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 园区行业列表VO
 */
@Setter
@Getter
public class ParkInvoiceListVO implements Serializable {
    private static final long serialVersionUID = 4955350232388073488L;

    /**
     * 行业id
     */
    private Long industryId;

    /**
     * 行业名称
     */
    private String industryName;
}
