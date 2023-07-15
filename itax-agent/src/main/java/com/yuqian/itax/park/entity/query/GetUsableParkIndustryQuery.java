package com.yuqian.itax.park.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 *  @Author: lmh
 *  @Date: 2021/8/12
 *  @Description: 可选园区行业查询实体类-接入方使用
 */
@Getter
@Setter
public class GetUsableParkIndustryQuery extends BaseQuery implements Serializable {

    /**
     * 税收分类编码
     */
    private List<String> taxCodeList;

    /**
     * 意向园区列表
     */
    private List<Long> parkList;

    /**
     * 响应类型 1-需要行业数据 2-不要行业数据
     */
    private int responseType;

    /**
     * 纳税人类型 1-小规模纳税人 2-一般纳税人
     */
    private Integer taxpayerType = 1;
}