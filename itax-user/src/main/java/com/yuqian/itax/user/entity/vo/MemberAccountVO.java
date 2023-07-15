package com.yuqian.itax.user.entity.vo;

import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 会员账号表
 * 
 * @Date: 2019年12月06日 10:48:28 
 * @author Kaven
 */
@Getter
@Setter
public class MemberAccountVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 会员昵称
	 */
	private String memberName;

	/**
	 * 用户名
	 */
	private String realName;
	
	/**
	 * 会员手机号
	 */
	private String memberPhone;

	/**
	 * 会员等级
	 */
	private Long memberLevel;
	
	/**
	 * 会员等级名称
	 */
	private String levelName;

	/**
	 * 产品名称
	 */
	private String prodName;

	/**
	 * 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-税务顾问 10-城市服务商
	 */
	private Integer prodType;

	/**
	 * 是否实名（0：未实名，1：已实名）
	 * 0-未认证 1-认证成功 2-认证失败
	 */
	private Integer status;

	/**
	 * 持卡人姓名
	 */
	private String userName;

	/**
	 * 身份证号
	 */
	private String idCard;

	/**
	 * 银行卡号
	 */
	private String bankNumber;

	/**
	 * 银行名称
	 */
	private String bankName;

	/**
	 * 预留手机号
	 */
	private String phone;

	/**
	 * 推广类型 1-散客 2-直客 3-顶级直客
	 */
	private Integer extendType;

	/**
	 * 用户可升级的列表
	 */
	private List<MemberLevelVO> levelList;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 身份证正面
	 */
	private String idCardFront;

	/**
	 *  身份证反面照
	 */
	private String idCardBack;

	/**
	 * 身份证有效期
	 */
	private String expireDate;

	/**
	 * 身份证地址
	 */
	private String idCardAddr;

	public MemberAccountVO() {
	}

	public MemberAccountVO(MemberAccountEntity entity, ProductEntity proEntity, List<UserBankCardEntity> cards) {
		this.id = entity.getId();
		this.memberName = entity.getMemberName();
		this.realName = entity.getRealName();
		this.memberPhone = entity.getMemberPhone();
		this.memberLevel = entity.getMemberLevel();
		this.levelName = entity.getLevelName();
//		this.prodName = proEntity.getProdName();
//		this.prodType = proEntity.getProdType();
		this.status = entity.getAuthStatus();
		this.userName = entity.getRealName();
		this.idCard = entity.getIdCardNo();
		this.extendType = entity.getExtendType();
		this.oemCode = entity.getOemCode();
		this.idCardFront = entity.getIdCardFront();
		this.idCardBack = entity.getIdCardBack();
		this.expireDate = entity.getExpireDate();
		this.idCardAddr = entity.getIdCardAddr();
		if (CollectionUtil.isEmpty(cards)) {
			return;
		}
		UserBankCardEntity cardEntity = cards.get(0);
		this.userName = cardEntity.getUserName();
		this.idCard = cardEntity.getIdCard();
		this.bankNumber = cardEntity.getBankNumber();
		this.bankName = cardEntity.getBankName();
		this.phone = cardEntity.getPhone();
	}
}
