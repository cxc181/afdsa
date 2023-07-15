package com.yuqian.itax.park.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author cxz
 * @date 2022年10月12日 16时52分24秒
 */
@ApiModel(description = "园区奖励政策表-查询参数")
@Data
public class ParkRewardPolicyLabelQuery extends BaseQuery implements Serializable {

    @ApiModelProperty(value="园区id",name="parkId")
    @NotNull(message = "园区id不能为空")
    private Long parkId;

    @ApiModelProperty(value="奖励标签",name="rewardLabel")
    private String rewardLabel;

    @ApiModelProperty(value="奖励说明",name="rewardDesc")
    private String rewardDesc;


}
