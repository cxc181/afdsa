package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PosterBase64VO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * base64海报
     */
    private String base64Poster;

}
