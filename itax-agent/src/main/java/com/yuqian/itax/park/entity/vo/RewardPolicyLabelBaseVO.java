package com.yuqian.itax.park.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@ApiModel(description = "园区奖励政策标签表-响应参数")
@Data
public class RewardPolicyLabelBaseVO implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value="主键id",name="id")
    private Long id;

    @ApiModelProperty(value="奖励标签",name="rewardLabel")
    private String rewardLabel;

    @ApiModelProperty(value="奖励说明",name="rewardDesc")
    private String rewardDesc;

}
