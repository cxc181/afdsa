package com.yuqian.itax.user.entity.vo;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class CrowdLabelChangeVO extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 标签id
     */
    private Long crowdLabelId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 操作内容
     */
    private String remark;

    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 操作人
     */
    private String addUser;
}
