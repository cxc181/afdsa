package com.yuqian.itax.system.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class PromotionPosterPO implements Serializable {
    private static final long serialVersionUID = -1L;

    Long id;

    /**
     * 海报名称
     */
    private String posterName;
    /**
     * 排序号
     */
    private Integer posterSn;
    /**
     * 上架OEM机构
     */
    private List<String> oemCodes;
    /**
     * 左边距
     */
    private Integer qrLeftMargin;
    /**
     * 上边距
     */
    private Integer qrTopMargin;
    /**
     * 二维码图片宽
     */
    private Integer qrWidth;
    /**
     * 二维码图片高
     */
    private Integer qrHeight;

    /**
     * 海报地址
     */
    private String posterAddress;
}
