package com.yuqian.itax.message.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 悬浮公告列表返回vo
 *
 * @author yejian
 * @Date: 2020年10月23日 09:32:33
 */
@Getter
@Setter
public class NoticeManageListVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 打开方式 1-通知详情 2-h5地址链接 3-小程序功能
     */
    @ApiModelProperty(value = "打开方式 1-通知详情 2-h5地址链接 3-小程序功能")
    private Integer openMode;

    /**
     * 通知标题
     */
    @ApiModelProperty(value = "通知标题")
    private String noticeTitle;

    /**
     * 跳转地址
     */
    @ApiModelProperty(value = "跳转地址")
    private String jumpUrl;

}
