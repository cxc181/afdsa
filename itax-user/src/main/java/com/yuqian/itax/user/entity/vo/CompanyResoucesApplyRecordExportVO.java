package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class CompanyResoucesApplyRecordExportVO implements Serializable {

    private static final long serialVersionUID = -1L;


    private Long id;

    /**
     * 申请单编号
     */
    @Excel(name = "申请单编号")
    private String orderNo;

    /**
     * 申请状态  0-待发货  1-出库中 2-待签收 3-已签收 4-已取消
     */
    @Excel(name = "申请状态" , replace = { "待付款_0","待发货_1","出库中_2","待签收_3","已签收_4","已取消_5" })
    private Integer status;
    /**
     * 所属企业
     */
    @Excel(name = "所属企业")
    private String companyName;

    /**
     * 证件名称   1-公章 2-财务章 3-对公账号u盾 4-营业执照  5-营业执照副本 6-发票章 ，多个资源直接用 逗号分割
     */
    // @Excel(name = "证件名称" , replace = { "公章_1","财务章_2","对公账号u盾_3","营业执照_4","营业执照副本_5","发票章_6" })
    private String applyResouces;
    @Excel(name = "证件名称" )
    private String applyResoucess;
    /**
     * 收货人姓名
     */
    @Excel(name = "收货人姓名")
    private String recipient;
    /**
     * 收货人手机号
     */
    @Excel(name = "收货人手机号")
    private String recipientPhone;

    /**
     * 收件省名称
     */
    @Excel(name = "收货省名称")
    private String provinceName;
    /**
     * 收件市名称
     */
    @Excel(name = "收货市名称")
    private String cityName;
    /**
     * 收件人区名称
     */
    @Excel(name = "收货区名称")
    private String districtName;
    /**
     * 收件人详细地址
     */
    @Excel(name = "收货人详细地址")
    private String recipientAddress;
    /**
     * 快递单号
     */
    @Excel(name = "快递单号")
    private String courierNumber;
    /**
     * 快递公司名称
     */
    @Excel(name = "快递公司名称")
    private String courierCompanyName;

    @Excel(name = "失败原因")
    private String failed;

    public void setApplyResouces(String applyResouces){
        this.applyResouces=applyResouces;
        this.applyResoucess=applyResouces.replace("1","公章").replace("2","财务章").replace("3","对公账号u盾")
        .replace("4","营业执照").replace("5","营业执照副本").replace("6","发票章");
    }
}
