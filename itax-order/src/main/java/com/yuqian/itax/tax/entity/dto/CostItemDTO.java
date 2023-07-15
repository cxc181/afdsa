package com.yuqian.itax.tax.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description 更新用户成本dto
 * @Author  lmh
 * @Date   2022/3/10
 */
@Getter
@Setter
public class CostItemDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 用户成本项id
     */
    private Long id;

    /**
     *  操作类型 1-添加 2-删除
     */
    @NotNull(message = "操作类型不能为空")
    private int operateType;

    /**
     * 成本项名称
     */
    private String costItemName;
}
