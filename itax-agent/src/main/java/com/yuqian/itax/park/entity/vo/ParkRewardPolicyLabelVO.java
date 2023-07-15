package com.yuqian.itax.park.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@ApiModel(description = "园区奖励政策表-响应参数")
@Data
public class ParkRewardPolicyLabelVO implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value="主键id",name="id")
    private Long id;

    @ApiModelProperty(value="奖励标签",name="rewardLabel")
    private String rewardLabel;

    @ApiModelProperty(value="奖励说明",name="rewardDesc")
    private String rewardDesc;

    @ApiModelProperty(value="更新时间",name="updateTime")
    private Date updateTime;

    @ApiModelProperty(value="更新人姓名",name="updateUser")
    private String updateUser;

    @ApiModelProperty(value="更新人手机号码",name="updateUserPhone")
    private String updateUserPhone;

    @ApiModelProperty(value="基础奖励政策id",name="rewardLabelBaseId")
    private Long rewardLabelBaseId;

}
