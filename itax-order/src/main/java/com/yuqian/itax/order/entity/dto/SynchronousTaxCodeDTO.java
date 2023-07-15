package com.yuqian.itax.order.entity.dto;

import com.yuqian.itax.order.entity.RegisterOrderGoodsDetailRelaEntity;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class SynchronousTaxCodeDTO implements Serializable {

    /**
     * 订单编号
     */
    @NotNull(message="订单编号不能为空")
    private String orderNo;

    /**
     * 商品编码及商品名称列表
     */
    @NotNull(message = "商品编码及商品名称列表不能为空")
    private List<RegisterOrderGoodsDetailRelaEntity> merchandises;
}
