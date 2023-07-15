package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName CreatCorpAccContOrderVO
 * @Description 创建对公户续费订单返回VO
 * @Author Administrator
 * @Date 2021/9/8 11:56
 * @Version 1.0
 */

@Setter
@Getter
public class CreatCorpAccContOrderVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private Long payAmount;

    /**
     * 服务内容
     */
    private String serviceContent;
}
