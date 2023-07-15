package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 园区奖励DTO
 * @Author  lmh
 * @Date   2022/09/26
 */
@Getter
@Setter
public class ParkRewardQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 总税费（分）
     */
    private Long allTax;

    /**
     * 增值税税费（分）
     */
    @NotNull(message = "增值税税费不能为空")
    private Long vatTax;

    /**
     * 所得税税费（分）
     */
    @NotNull(message = "所得税税费不能为空")
    private Long incomeTax;

    /**
     * 园区id
     */
    @NotBlank(message = "园区id不能为空")
    private String parkIds;

    /**
     * 园区id列表
     */
    private List<String> parkIdList;

    /**
     * 排序方式 0-奖励总金额降序（默认） 1-奖励总金额升序 2-增值附加升序 3-增值附加降序 4-所得税升序 5-所得税降序
     * 【默认排序时，次级排序标的为园区id升序；非默认排序时，次级排序标的依次为默认排序、园区id升序】
     */
    private int sortOrder;
}
