package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Description 代付银行卡入参BEAN
 * @Author  Kaven
 * @Date   2020/9/9 10:31
*/
@Getter
@Setter
public class PaidOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private String orderNo;// 商户订单号

	private String amount;// 金额

	private String payeeName; // 收款方姓名

	private String payeeCardNo;// 收款方卡号

	private String payeeBankName;// 收款方银行名称

	private String draweeAccountNo;// 付款方账号

	private String txnStffId; // 制单员编号

	private String entrstPrjId;// 委托项目编号

	private String prjUserId; // 项目用途编号
}
