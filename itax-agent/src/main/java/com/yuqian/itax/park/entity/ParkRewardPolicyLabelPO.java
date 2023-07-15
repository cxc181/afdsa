package com.yuqian.itax.park.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author cxz
 * @date 2022年10月13日 16时52分24秒
 */
@ApiModel(description = "园区奖励政策表-新增参数")
@Data
public class ParkRewardPolicyLabelPO implements Serializable {

    @ApiModelProperty(value="园区奖励政策id，修改的时候传这个参数",name="id")
    private Long id;

    @ApiModelProperty(value="园区id",name="parkId")
    @NotNull(message = "园区id不能为空")
    private Long parkId;

    @ApiModelProperty(value="奖励标签",name="rewardLabel")
    @NotBlank(message = "奖励标签不能为空")
    private String rewardLabel;

    @ApiModelProperty(value="奖励说明",name="rewardDesc")
    private String rewardDesc;

    @ApiModelProperty(value="基础奖励政策id",name="rewardLabelBaseId")
    //@NotNull(message = "基础奖励政策标签id不能为空")
    private Long rewardLabelBaseId;

}
