package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.GET;
import java.io.Serializable;
import java.util.Date;

/**
 * 近12个月开票记录视图VO
 * @author：pengwei
 * @Date：2020/12/7 15:01
 * @version：1.0
 */
@Getter
@Setter
public class InvoiceStatisticsViewVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 会员id
     */
    private Long userId;

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 近12个月已开票金额
     */
    private Long useTotalInvoiceAmount = 0L;

    /**
     * 本季度已开普票金额
     */
    private Long useInvoiceAmountQuarterPp = 0L;

    /**
     * 本季度已开专票金额
     */
    private Long useInvoiceAmountQuarterZp = 0L;

    /**
     * 本年已开票金额
     */
    private Long useInvoiceAmountYear = 0L;

    /**
     * 企业本年有效截至时间
     */
    private Date endTime;

}
