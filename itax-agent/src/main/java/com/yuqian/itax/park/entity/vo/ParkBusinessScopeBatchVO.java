package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ParkBusinessScopeBatchVO implements Serializable {

    /**
     * 经营范围名称
     */
    @Excel(name = "经营范围")
    private String businessScopeName;


    @Excel(name = "失败原因")
    private String  msg;

    private boolean falg = true;


    /**
     * 经营范围基础库id
     */
    private List<Long> businessscopeBaseId;

}
