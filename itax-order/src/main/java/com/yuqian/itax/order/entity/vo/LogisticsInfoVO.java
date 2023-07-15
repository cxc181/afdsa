package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 快递信息返回实体
 * @author：lmh
 * @Date：2021/10/21
 * @version：1.0
 */
@Getter
@Setter
public class LogisticsInfoVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 时间
     */
    private String time;

    /**
     * 内容
     */
    private String context;
}
