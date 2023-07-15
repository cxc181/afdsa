package com.yuqian.itax.system.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description oss上传成功后的回调参数
 * @Author yejian
 * @Date 2019/12/18 17:12
 */
@Getter
@Setter
public class OssCallbackParam implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty("请求的回调地址")
    private String callbackUrl;

    @ApiModelProperty("回调是传入request中的参数")
    private String callbackBody;

    @ApiModelProperty("回调时传入参数的格式，比如表单提交形式")
    private String callbackBodyType;

}
