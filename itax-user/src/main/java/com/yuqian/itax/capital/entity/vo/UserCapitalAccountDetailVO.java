package com.yuqian.itax.capital.entity.vo;

import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.UserExtendEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class UserCapitalAccountDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 资金账户主键id
     */
    private Long id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 账户编码
     */
    private String capitalAccount;
    /**
     * 账户
     */
    private String username;
    /**
     * 绑定手机
     */
    private String phone;
    /**
     * 名称
     */
    private String nickname;

    /**
     * 可用余额
     */
    private Long availableAmount;

    /**
     *钱包类型 1-消费钱包 2-佣金钱包
     */
    private Integer walletType;

    /**
     * 机构名称
     */
    private String oemName;

    /**
     * 姓名
     */
    private String bankUserName;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 银行卡号
     */
    private String bankNumber;

    /**
     * 支付金额
     */
    private Long payAmount;

    /**
     * 支付状态 0-待支付 1-支付中 2 -支付成功 3-支付失败
     */
    private Integer payStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核状态（审核状态 0-待审核 1-审核通过 2-审核失败）
     */
    private Integer auditStatus;

    /**
     * 审核描述
     */
    private String auditRemark;

    /**
     * 打款流水
     */
    private List<String> payWaterImgs;

    public UserCapitalAccountDetailVO() {

    }

    public UserCapitalAccountDetailVO(UserCapitalAccountEntity userCapitalAccountEntity, UserEntity userEntity, UserExtendEntity userExtendEntity, OemEntity oem, UserBankCardEntity userBankCardEntity) {
        this.oemName = Optional.ofNullable(oem).map(OemEntity::getOemName).orElse(null);
        if(userCapitalAccountEntity != null) {
            this.id = userCapitalAccountEntity.getId();
            this.capitalAccount = userCapitalAccountEntity.getCapitalAccount();
            this.walletType = userCapitalAccountEntity.getWalletType();
            this.availableAmount = userCapitalAccountEntity.getAvailableAmount();
        }
        if(userEntity!=null){
            this.username = userEntity.getUsername();
            this.nickname = userEntity.getNickname();
        }
        if(userExtendEntity !=null) {
            this.phone = userExtendEntity.getPhone();
        }
        if(userBankCardEntity!=null) {
            this.bankUserName = userBankCardEntity.getUserName();
            this.bankName = userBankCardEntity.getBankName();
            this.bankNumber = userBankCardEntity.getBankNumber();
        }
    }
}
