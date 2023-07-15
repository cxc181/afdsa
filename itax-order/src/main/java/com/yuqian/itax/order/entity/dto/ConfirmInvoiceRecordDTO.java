package com.yuqian.itax.order.entity.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName ConfirmInvoiceRecordDTO
 * @Description TODO
 * @Author jiangni
 * @Date 2021/1/4
 * @Version 1.0
 */
@Data
public class ConfirmInvoiceRecordDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 开票记录id
     */
    private Long id;

    @NotNull(message = "开票记录编号不能为空")
    private String invoiceRecordNo;

    /**
     * 订单号
     */
    @NotNull(message = "订单号不能为空")
    private String orderNo;

    /**
     * 发票照片地址
     */
    @NotNull(message = "发票照片不能为空")
    private String invoiceImgs;

    /**
     * 出票日期
     */
    @NotNull(message = "出票日期不能为空")
    private String ticketTime;
    /**
     * 所属年份
     */
//    @NotNull(message = "所属季度周期不能为空")
    private Integer taxYear;

    /**
     * 所属季度
     */
//    @NotNull(message = "所属季度周期不能为空")
//    @Min(value = 1,message = "所属季度周期格式错误")
//    @Max(value = 4,message = "所属季度周期格式错误")
    private Integer taxQuarter;
}
