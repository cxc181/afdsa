package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
@Setter
@Getter
public class TaxBillCredentialsRecordVO  implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 税号
     */
    @Excel(name = "税号")
    private String ein;

    /**
     * 失败原因
     */
    @Excel(name = "失败原因")
    private String resultMsg;
}
