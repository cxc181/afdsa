package com.yuqian.itax.yishui.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
@Getter
@Setter
public class FastIssuingReq implements Serializable {

    /**
     * 批次编号
     */
    private String trade_number;

    /**
     * 项目ID
     */
    private Integer crowd_id;
    /**
     * 付款详情数据
     */
    private List<IssuingDataDto> issuing_data = new ArrayList<>();
}
