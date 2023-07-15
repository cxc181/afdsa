package com.yuqian.itax.park.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class ParkCommentDTO implements Serializable {

    /**
     * 园区id
     */
    @NotNull(message="园区id不能为空")
    private Long parkId;

    /**
     * 用户评分
     */
    @NotNull(message="用户评分不能为空")
    private BigDecimal userRatings;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 100, message = "评论内容最多不能超过100个字符")
    private String commentsContent;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 添加用户
     */
    private String memberAccount;
}
