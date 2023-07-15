package com.yuqian.itax.capital.entity.vo;

import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.system.entity.BankInfoEntity;
import com.yuqian.itax.user.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 提现后台返回实体
 * @author：pengwei
 * @Date：2019/12/27 10:12
 * @version：1.0
 */
@Getter
@Setter
public class WithdrawDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * 资金账户主键id
     */
    private Long id;
    /**
     * 机构名称
     */
    private String oemName;
    /**
     * 当前用户名称
     */
    private String nickname;
    /**
     * 当前账号
     */
    private String username;
    /**
     * 可用余额
     */
    private BigDecimal availableAmount;
    /**
     * 银行卡号
     */
    private String bankNumber;
    /**
     * 开户行
     */
    private String bankName;
    /**
     * 银行预留手机号
     */
    private String phone;
    /**
     * 储蓄卡单笔限额
     */
    private Integer singleLimitCash = 50000;
    /**
     * 储蓄卡单日限额
     */
    private Integer dailyLimitCash = 200000;
    public WithdrawDetailVO() {

    }
    public WithdrawDetailVO(UserEntity userEntity, UserCapitalAccountEntity entity, UserBankCardEntity cardEntity, BankInfoEntity bankInfo) {
        this.id = entity.getId();
        this.oemName = userEntity.getOemName();
        this.nickname = userEntity.getNickname();
        this.username = userEntity.getUsername();
        this.availableAmount = new BigDecimal(entity.getAvailableAmount()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN);
        this.bankNumber = cardEntity.getBankNumber();
        if (StringUtils.isNotBlank(bankNumber) && bankNumber.length() > 8) {
            this.bankNumber = StringUtils.overlay(bankNumber, "********", 4, bankNumber.length() - 4);
        }
        this.phone = cardEntity.getPhone();
        if (StringUtils.isNotBlank(phone) && phone.length() >= 11) {
            this.phone = StringUtils.overlay(phone, "****", 3, phone.length() - 4);
        }
        this.bankName = cardEntity.getBankName();
        if (bankInfo != null) {
            if (bankInfo.getSingleLimitCash() != null) {
                this.singleLimitCash = bankInfo.getSingleLimitCash();
            }
            if (bankInfo.getDailyLimitCash() != null) {
                this.dailyLimitCash = bankInfo.getDailyLimitCash();
            }
        }
    }
}
