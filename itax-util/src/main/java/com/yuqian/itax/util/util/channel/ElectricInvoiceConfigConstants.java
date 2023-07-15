package com.yuqian.itax.util.util.channel;


/**
 * 电票配置
 */
public class ElectricInvoiceConfigConstants {
    //百望开票接口
    public static final String BWINVOICEISSUE = "/gateway/api/bwinvoice/v1/apply";
    //百望领用存信息查询
    public static final String BWQUERYINVENTORY = "/gateway/api/bwinvoice/v1/queryInventory";
    //百望发票打印
    public static final String BWPRINTINVOICE = "/gateway/api/bwinvoice/v1/printInvoice";
    //百望版式文件生成
    public static final String BWFORMATCREATE = "/gateway/api/bwinvoice/v1/formatCreate";
    //百望订单查询
    public static final String QUERYINVOICEORDER = "/gateway/api/bwinvoice/v1/queryOrder";
    
}
