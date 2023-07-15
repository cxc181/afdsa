package com.yuqian.itax.point.entity.dto;

import com.yuqian.itax.point.entity.BuriedPointEntity;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;

@Getter
@Setter
public class BuriedPointDTO implements Serializable {

    /**
     * 主键id
     */
    @NotNull(message = "是否默认状态不能为空", groups = {Update.class})
    private Long id;

    @NotNull(message = "操作平台", groups = {Add.class, Update.class})
    @ApiModelProperty(value = "操作平台：1-微信小程序 2-支付宝小程序 3-运营平台")
    private Integer operPlatform;

    @NotNull(message = "操作功能", groups = {Add.class, Update.class})
    @ApiModelProperty(value = "操作功能：1-banner")
    private Integer operFunction;

    @NotNull(message = "用户类型", groups = {Update.class})
    @ApiModelProperty(value = "用户类型：1-会员 2-非会员")
    private Integer userType;

    @NotNull(message = "资源id", groups = {Add.class, Update.class})
    @ApiModelProperty(value = "资源id :根据操作功能获取对应的资源id")
    private Long sourceId;

    public BuriedPointEntity toEntity(Long userId){
        BuriedPointEntity buriedPointEntity = new BuriedPointEntity();
        buriedPointEntity.setOperPlatform(operPlatform);
        buriedPointEntity.setOperFunction(operFunction);
        buriedPointEntity.setUserType(userType);
        buriedPointEntity.setSourceId(sourceId);
        buriedPointEntity.setUserId(userId);
        return  buriedPointEntity;
    }

}
