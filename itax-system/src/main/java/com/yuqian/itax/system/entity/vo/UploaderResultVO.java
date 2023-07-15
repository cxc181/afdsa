package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 图片上传返回结果信息
 */
@Getter
@Setter
public class UploaderResultVO implements Serializable {

    private static final long serialVersionUID = -1L;
    
    /**
     * 图片上传后的名称
     */
    private String name;
    
    /**
     * 原始文件名称
     */
    private String originalFilename;
    
    /**
     * 访问url
     */
    private String url;

}
