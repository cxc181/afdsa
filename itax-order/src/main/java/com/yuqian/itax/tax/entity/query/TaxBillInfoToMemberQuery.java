package com.yuqian.itax.tax.entity.query;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TaxBillInfoToMemberQuery  implements Serializable {

    private static final long serialVersionUID = -1L;


    private Long memberId;
    private Long parkTaxBillId;

    /**
     * 税款所属期年
     */
    private Integer taxBillYear;

    /**
     * 税款所属期-季度
     */
    private Integer taxBillSeasonal;
}
