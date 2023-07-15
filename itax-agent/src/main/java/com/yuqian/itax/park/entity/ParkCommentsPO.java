package com.yuqian.itax.park.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
public class ParkCommentsPO implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value="主键id",name="id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value="回复内容",name="replyContent")
    @NotBlank(message = "回复内容不能为空")
    private String replyContent;

}
