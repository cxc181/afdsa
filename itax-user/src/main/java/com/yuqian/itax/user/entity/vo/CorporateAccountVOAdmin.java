package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 对公户账户明细前端展示BEAN
 * @Author  Kaven
 * @Date   2020/9/8 10:47
 */
@Getter
@Setter
public class CorporateAccountVOAdmin implements Serializable {


        private static final long serialVersionUID = -1L;

        private Long id;
        /**
         * 对公户id
         */
        private Long corporateAccountId;
        /**
         * 银行唯一编号
         */
        @Excel(name="银行唯一编号")
        private String bankCollectionRecordNo;

        /**
         * 摘要
         */
        @Excel(name="摘要")
        private String tradingRemark;
        /**
         * 交易备注
         */
        @Excel(name="交易备注")
        private String smy;
        /**
         * 借方发生额(元)
         */
        @Excel(name="借方发生额(")
        private BigDecimal dhamt;
        /**
         * 贷方发生额(元)
         */
        @Excel(name="贷方发生额")
        private BigDecimal crHpnam;
        /**
         * 发生金额（收款金额）
         */
        @Excel(name="发生金额")
        private BigDecimal hpnAmt;

        /**
         * 账户余额(元)
         */
        @Excel(name="账户余额")
        private BigDecimal acba;
        /**
         * 交易时间
         */
        @Excel(name="交易时间")
        private String tradingTime;

        /**
         * 对方账户名
         */
        @Excel(name="对方账户名")
        private String otherPartyBankAccount;

        /**
         * 对方开户行
         */
        @Excel(name="对方开户行")
        private String otherPartyBankName;
        /**
         * 对方账户
         */
        @Excel(name="对方账户")
        private String otherPartyBankNumber;
        /**
         * 备注
         */
        private String remark;
        /**
         * 剩余提现额度(元)
         */
        @Excel(name="剩余提现额度")
        private BigDecimal remainingWithdrawalAmount;

        public void setOtherPartyBankNumber(String otherPartyBankNumber){
                if (StringUtils.isNotBlank(otherPartyBankNumber)) {
                        this.otherPartyBankNumber = StringUtils.overlay(otherPartyBankNumber, "****", 4, otherPartyBankNumber.length() - 4);
                }
        }

}
