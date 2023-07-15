package com.yuqian.itax.capital.entity.vo;

import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.system.entity.BankInfoEntity;
import com.yuqian.itax.user.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分润记录返回实体
 * @author：pengwei
 * @Date：2019/12/27 10:12
 * @version：1.0
 */
@Getter
@Setter
public class ProfitDetailVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户id
     */
     private Long userId;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 分润金额
     */
    private Long profitsAmount;

    /**
     * 分润状态 0-待分润 1-分润中 2-已分润待结算 3-已分润已结算 4-分润失败
     */
    private Integer profitsStatus;

    /**
     * 钱包类型 1-消费钱包 2-佣金钱包
     */
    private Integer walletType;

    /**
     * 提现订单号
     */
    private String withdrawOrderNo;

    /**
     * 分润类型 1-会费 2-托管费 3-开票服务费 4-注销服务费 5-会费返还 6-托管费续费
     */
    private Integer profitsType;

    /**
     * 分润类型名称
     */
    private String profitsTypeName;

    /**
     * 添加时间
     */
    private Date addTime;
}
