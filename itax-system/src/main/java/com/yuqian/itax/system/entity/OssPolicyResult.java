package com.yuqian.itax.system.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * @Description 获取OSS上传文件授权返回结果
 * @Author yejian
 * @Date 2019/12/18 17:12
 */
@Getter
@Setter
public class OssPolicyResult implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty("访问身份验证中用到用户标识")
    private String accessKeyId;

    @ApiModelProperty("用户表单上传的策略,经过base64编码过的字符串")
    private String policy;

    @ApiModelProperty("对policy签名后的字符串")
    private String signature;

    @ApiModelProperty("上传文件夹路径前缀")
    private String dir;

    @ApiModelProperty("oss对外服务的访问域名")
    private String host;

    @ApiModelProperty("上传成功后的回调设置")
    private String callback;
}
