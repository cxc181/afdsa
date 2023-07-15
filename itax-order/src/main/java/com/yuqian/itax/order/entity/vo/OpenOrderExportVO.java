package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 开户订单返回实体
 * @author：pengwei
 * @Date：2019/12/10 18:52
 * @version：1.0
 */
@Getter
@Setter
public class OpenOrderExportVO implements Serializable {
    /**
     * 订单号
     */
    @Excel(name = "订单编号")
    private String orderNo;
    /**
     * 经营者/法人
     */
    @Excel(name = "经营者/法人")
    private String operatorName;
    /**
     * 经营者手机号
     */
    //@Excel(name = "经营者手机号")
    private String contactPhone;
    /**
     * 字号
     */
    private String shopName;
    /**
     * 备选字号1
     */
    private String shopNameOne;
    /**
     * 备选字号2
     */
    private String shopNameTwo;
    /**
     * 企业名称
     */
    @Excel(name = "企业名称")
    private String registeredName;
    /**
     * 备选字号1
     */
    @Excel(name = "备选名称1")
    private String nameOne;
    /**
     * 备选字号2
     */
    @Excel(name = "备选名称2")
    private String nameTwo;
    /**
     * 身份证号
     */
    @Excel(name = "身份证号")
    private String idCardNumber;
    /**
     * 身份证地址
     */
    @Excel(name = "身份证地址")
    private String idCardAddr;
    /**
     * 经营场所
     */
    @Excel(name = "经营场所")
    private String businessAddress;
    /**
     * 所属园区
     */
    @Excel(name = "所属园区")
    private String parkName;
    /**
     * 企业类型
     */
    @Excel(name = "企业类型", replace = {"个体户_1","个人独资企业_2","有限合伙公司_3","有限责任公司_4" })
    private Integer companyType;

    /**
     * 纳税性质
     */
    @Excel(name = "纳税性质",replace = { "-_null","小规模纳税人_1","一般纳税人_2" }, height = 10, width = 22)
    private Integer taxpayerType;

    /**
     * 委派方
     */
    @Excel(name = "委派方")
    private String appointPartyName;

    /**
     * 注册资本
     */
    @Excel(name = "注册资本(万)")
    private String registerCapital;
    /**
     * 经营范围
     */
    @Excel(name = "经营范围")
    private String businessScope;
    /**
     * 审核通过时间
     */
    @Excel(name = "审核通过时间" , replace = {"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date orderAuditTime;
    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

    private Long parkId;

    public void setRegisterCapital(String registerCapital){
        if(StringUtils.isNotBlank(registerCapital)){
            registerCapital = registerCapital.replace("万","");
            try{
                //自动去掉后面多余的零
                this.registerCapital = new BigDecimal(registerCapital).stripTrailingZeros().toPlainString()+"万";
            }catch (Exception e){
                this.registerCapital =  registerCapital;
            }
        }
        this.registerCapital =  registerCapital;
    }
}
