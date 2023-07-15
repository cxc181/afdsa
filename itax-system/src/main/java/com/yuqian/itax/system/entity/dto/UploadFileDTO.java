package com.yuqian.itax.system.entity.dto;

import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/17 11:08
 *  @Description: 文件上传Dto
 */
@Getter
@Setter
public class UploadFileDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * OEM机构编码
     */
    @ApiModelProperty(value = "OEM机构编码",required = true)
    private String oemCode;

    /**
     * 文件路径
     */
    @ApiModelProperty(value = "文件路径",required = true)
    @NotBlank(message="文件路径不能为空")
    private String fileUrl;

    /**
     * 文件类型：1-身份证正面照 2-身份证反面照
     */
    @ApiModelProperty(value = "文件类型：1-身份证正面照 2-身份证反面照",required = true)
    @NotNull(message="文件类型不能为空")
    @Min(value = 1, message = "文件类型有误")
    @Max(value = 2, message = "文件类型有误")
    private Integer type;

}
