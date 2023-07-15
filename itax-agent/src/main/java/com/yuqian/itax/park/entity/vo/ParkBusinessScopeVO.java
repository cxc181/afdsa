package com.yuqian.itax.park.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Setter
@Getter
public class ParkBusinessScopeVO implements Serializable {


    private Long id;

    /**
     * 经营范围名称
     */
    @Excel(name = "经营范围", width = 20)
    private String businessScopeName;

}
