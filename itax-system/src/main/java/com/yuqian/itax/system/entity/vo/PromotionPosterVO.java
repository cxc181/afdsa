package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PromotionPosterVO implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;

    /**
     * 海报名称
     */
    private String posterName;

    /**
     * 排序号
     */
    private Integer posterSn;

    /**
     * 二维码图片左边距(px)
     */
    private Integer qrLeftMargin;

    /**
     * 二维码图片上边距(px)
     */
    private Integer qrTopMargin;

    /**
     * 二维码图片宽(px)
     */
    private Integer qrWidth;

    /**
     * 二维码图片高(px)
     */
    private Integer qrHeight;

    /**
     * 海报地址
     */
    private String posterAddress;

    /**
     * 上架OEM机构(字符串拼接版)
     */
    private String oemNames;
    /**
     * 上架OEM机构
     */
    private List<Map<String,Object>> oemList;


    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 添加人
     */
    private String addUser;
}
