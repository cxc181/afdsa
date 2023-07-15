package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class UsableParkIndustryVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 行业id
     */
    private Long industryId;

    /**
     * 行业名称
     */
    private String industryName;

    /**
     * 经营范围
     */
    private String businessContent;

    /**
     * 示例名称
     */
    private String exampleName;
}
