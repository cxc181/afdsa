package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;


/**
 * 电票返回
 * @author：pengwei
 * @Date：2019/12/10 18:52
 * @version：1.0
 */
@Getter
@Setter
public class InvoiceDetailVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * oss电子发票图片地址
     */
    private String eInvoiceOssImgUrl;
}
