package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class IndustryAndParkInfoVo implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;

    /**
     * 注册名称示例
     */
    private String industryName;

    /**
     * 行业名称
     */
    private String exampleName;

    /**
     * 园区ID
     */
    private Long parkId;

    /**
     * 园区名称
     */
    private String parkName;
}
