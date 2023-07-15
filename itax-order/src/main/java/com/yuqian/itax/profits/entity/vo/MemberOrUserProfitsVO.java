package com.yuqian.itax.profits.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @Description 分润记录返回VO
 * @Author  HZ
 * @Date   2020/11/18 3:35 下午
 */
@Getter
@Setter
public class MemberOrUserProfitsVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 昨日收益
     */
    Long yesterdayProfits;

    /**
     * 本月收益
     */
    Long monthProfits;
    /**
     * 累计收益
     */
    Long totalProfits;
}
