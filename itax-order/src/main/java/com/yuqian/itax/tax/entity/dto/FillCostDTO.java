package com.yuqian.itax.tax.entity.dto;

import com.yuqian.itax.tax.entity.CompanyTaxCostItemEntity;
import com.yuqian.itax.tax.entity.CostItemBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Description 填报成本DTO
 * @Author  lmh
 * @Date   2022/3/11
 */
@Getter
@Setter
public class FillCostDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     *  企业税单id
     */
    @NotNull(message = "企业税单id不能为空")
    private Long companyTaxBillId;

    /**
     * 成本项列表
     */
    private List<CompanyTaxCostItemEntity> costs;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作来源 1-小程序 2-企业税单 3-批量填报成本
     */
    private int sourceOfOperating;
}
