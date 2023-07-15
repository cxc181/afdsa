package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class UserOrderStatisticsDayAgentVO implements Serializable {

    private static final long serialVersionUID = -1L;


        /**
         * ID
         */
        private Long id;
        /**
         * oem机构
         */
        @Excel(name = "oem机构")
        private String oemName;
        /**
         * 等级
         */
        @Excel(name = "等级", replace = { "高级城市合伙人_4","城市合伙人_5" })
        private String platformType;
        /**
         * 姓名
         */
        @Excel(name = "姓名")
        private String nickname;
        /**
         * 注册账号
         */
        @Excel(name = "注册账号")
        private String username;
        /**
         * 绑定手机
         */
        @Excel(name = "绑定手机")
        private String bindingAccount;
        /**
         * 新增裂变用户数
         */
        @Excel(name = "新增裂变用户数")
        private int promoteUserFission;
        /**
         * 新增裂变个体数
         */
        @Excel(name = "新增裂变个体数")
        private int individualFission;
        /**
         * 新增裂变托管费
         */
        @Excel(name = "新增裂变托管费")
        private BigDecimal companyRegistFeeFission;
        /**
         * 新增裂变开票服务费
         */
        @Excel(name = "新增裂变开票服务费")
        private BigDecimal invoiceFeeFission;
        /**
         * 新增裂变升级费
         */
        @Excel(name = "新增裂变升级费")
        private BigDecimal memberUpgradeFeeFission;
        /**
         * 新增裂变注销服务费
         */
        @Excel(name = "新增裂变注销服务费")
        private BigDecimal companyCancelFeeFission;
        /**
         * 新增裂变托管费续费
         */
        @Excel(name = "新增裂变托管费续费")
        private BigDecimal custodyFeeRenewalFission;

        /**
         * 新增分润
         */
        @Excel(name = "新增分润")
        private BigDecimal profitsFee;
        /**
         * 总分润
         */
        @Excel(name = "总分润")
        private BigDecimal totalProfitsFee;
}
