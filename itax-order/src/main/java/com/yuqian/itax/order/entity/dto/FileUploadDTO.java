package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description 文件上传dto（外部调用）
 * @Author  Kaven
 * @Date   2020/7/20 10:22
*/
@Getter
@Setter
public class FileUploadDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    @NotBlank(message = "文件内容不能为空")
    @ApiModelProperty(value = "文件内容",required = true)
    private String file;// 文件内容

    @NotBlank(message = "文件名不能为空")
    @ApiModelProperty(value = "文件名",required = true)
    private String fileName;// 文件名

    @NotNull(message = "文件类型不能为空")
    @Min(value=1,message = "文件类型只能是图片或者视频")
    @Max(value=2,message = "文件类型只能是图片或者视频")
    @ApiModelProperty(value = "文件类型",required = true)
    private Integer fileType;// 文件类型：1-图片 2-视频
}