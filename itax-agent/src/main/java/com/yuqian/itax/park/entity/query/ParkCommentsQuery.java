package com.yuqian.itax.park.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 *
 * @author cxz
 * @date 2022年10月11日 16时52分24秒
 */
@ApiModel(description = "园区评价表-查询参数")
@Data
public class ParkCommentsQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value="园区名称",name="parkName")
    private String parkName;

    @ApiModelProperty(value="评论状态  1-可见 2-屏蔽",name="status")
    private Integer status;

    @ApiModelProperty(value="评论内容",name="commentsContent")
    private String commentsContent;

    @ApiModelProperty(value = "园区id", name = "parkId")
    private Long parkId;
}
