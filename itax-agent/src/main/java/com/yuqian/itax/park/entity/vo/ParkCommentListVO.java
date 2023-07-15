package com.yuqian.itax.park.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(description = "园区评价表-响应参数")
@Data
public class ParkCommentListVO implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value="主键id",name="id")
    private Long id;

    @ApiModelProperty(value="会员联系人手机号码",name="memberPhone")
    private String memberPhone;

    @ApiModelProperty(value="用户评分",name="userRatings")
    private BigDecimal userRatings;

    @ApiModelProperty(value="评论内容",name="commentsContent")
    private String commentsContent;

    @ApiModelProperty(value="回复内容",name="replyContent")
    private String replyContent;

    @ApiModelProperty(value="创建时间",name="addTime")
    private Date addTime;

}
