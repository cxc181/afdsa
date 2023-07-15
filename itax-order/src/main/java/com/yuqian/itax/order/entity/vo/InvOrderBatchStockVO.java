package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 开票订单批量出库返回实体
 * @author：pengwei
 * @Date：2019/12/10 18:52
 * @version：1.0
 */
@Getter
@Setter
public class InvOrderBatchStockVO implements Serializable {
    /**
     * 开票主键id
     */
    private Long id;
    /**
     * 订单号
     */
    @Excel(name = "订单编号")
    private String orderNo;
    /**
     订单状态
     开票： 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收 8-已取消 9-待出款 10-审核未通过
     */
    @Excel(name = "订单状态", replace = { "待创建_0","待付款_1","待审核_2","出票中_3","待发货_4","出库中_5","待收货_6","已签收_7","已取消_8","待出款_9","审核未通过_10","_null" }, height = 10, width = 22)
    private Integer orderStatus;
    /**
     * 园区名称
     */
    @Excel(name = "园区")
    private String parkName;
    /**
     * 企业名称
     */
    @Excel(name = "企业名称")
    private String companyName;
    /**
     * 发票抬头收件人
     */
    @Excel(name = "收货人姓名")
    private String recipient;
    /**
     * 发票抬头联系电话
     */
    @Excel(name = "收货人手机号")
    private String recipientPhone;
    /**
     * 收货省份
     */
    @Excel(name = "收货省份")
    private String provinceName;
    /**
     * 城市
     */
    @Excel(name = "城市")
    private String cityName;
    /**
     * 区域
     */
    @Excel(name = "区域")
    private String districtName;
    /**
     * 发票抬头收件地址
     */
    @Excel(name = "详情地址")
    private String recipientAddress;
    /**
     * 快递单号
     */
    @Excel(name = "快递单号")
    private String courierNumber;
    /**
     * 快递公司名称
     */
    @Excel(name = "快递公司编号")
    private String courierCompanyName;

    @Excel(name = "失败原因")
    private String failed;

}
