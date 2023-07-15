package com.yuqian.itax.yishui.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Getter
@Setter
public class ProfessionalListReq implements Serializable {

    /**
     * 查询关键字：身份证号
     */
    private String keywords;

    private String crowd_sn = "";
    /**
     * 分页信息，默认即可
     */
    private Pagination pagination = new Pagination();
}
