package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class UserOrderStatisticsDayMemberVO  implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * ID
     */
    private Long id;
    /**
     * oem机构
     */
    @Excel( name = "oem机构")
    private String oemName;
    /**
     * 等级
     */
    @Excel( name = "等级")
    private String levelName;
    /**
     * 姓名
     */
    @Excel( name = "姓名")
    private String realName;
    /**
     * 注册账号
     */
    @Excel( name = "注册账号")
    private String memberAccount;
    /**
     * 绑定手机
     */
    @Excel( name = "绑定手机")
    private String memberPhone;
    /**
     * 新增直推用户数
     */
    @Excel( name = "新增直推用户数")
    private int promoteUserDirect;
    /**
     * 新增直推个体数
     */
    @Excel( name = "新增直推个体数")
    private int individualDirect;
    /**
     * 新增直推托管费
     */
    @Excel( name = "新增直推托管费")
    private BigDecimal companyRegistFeeDirect;
    /**
     * 新增直推开票服务费
     */
    @Excel( name = "新增直推开票服务费")
    private BigDecimal invoiceFeeDirect;
    /**
     * 新增直推升级费
     */
    @Excel( name = "新增直推升级费")
    private BigDecimal memberUpgradeFeeDirect;
    /**
     * 新增直推注销服务费
     */
    @Excel( name = "新增直推注销服务费")
    private BigDecimal companyCancelFeeDirect;
    /**
     * 直推托管费续费
     */
    @Excel( name = "新增直推托管费续费")
    private BigDecimal custodyFeeRenewalDirect;
    /**
     * 新增裂变用户数
     */
   // @Excel( name = "新增裂变用户数")
    private int promoteUserFission;
    /**
     * 新增裂变个体数
     */
   // @Excel( name = "新增裂变个体数")
    private int individualFission;
    /**
     * 新增裂变托管费
     */
  //  @Excel( name = "新增裂变托管费")
    private BigDecimal companyRegistFeeFission;
    /**
     * 新增裂变开票服务费
     */
   // @Excel( name = "新增裂变开票服务费")
    private BigDecimal invoiceFeeFission;
    /**
     * 新增裂变升级费
     */
    //@Excel( name = "新增裂变升级费")
    private BigDecimal memberUpgradeFeeFission;
    /**
     * 新增裂变注销服务费
     */
   // @Excel( name = "新增裂变注销服务费")
    private BigDecimal companyCancelFeeFission;
    /**
     * 裂变托管费续费
     */
   // @Excel( name = "新增裂变托管费续费")
    private BigDecimal custodyFeeRenewalFission;
    /**
     * 新增分润
     */
    @Excel( name = "新增分润")
    private BigDecimal profitsFee;
}
