package com.yuqian.itax.system.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description oss上传文件的回调结果
 * @Author yejian
 * @Date 2019/12/18 17:12
 */
@Getter
@Setter
public class OssCallbackResult implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty("文件名称")
    private String filename;

    @ApiModelProperty("文件大小")
    private String size;

    @ApiModelProperty("文件的mimeType")
    private String mimeType;

    @ApiModelProperty("图片文件的宽")
    private String width;

    @ApiModelProperty("图片文件的高")
    private String height;

}
