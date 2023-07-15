package com.yuqian.itax.tax.entity.vo;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class CompanyTaxBillChangeVO implements Serializable {

    /**
     * 税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税 6-待核对   7-待填报成本 8-待申报 9-已作废
     */
    private Integer taxBillStatus;

    /**
     *  添加时间
     */
    private Date addTime;

    /**
     * 变动内容
     */
    private String descrip;

    /**
     * 添加人
     */
    private String addUser;
}
