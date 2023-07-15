package com.yuqian.itax.order.entity.vo;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 企业对公户提现订单前端展示VO
 * @Author  Kaven
 * @Date   2020/9/9 15:21
*/
@Getter
@Setter
public class CorpAccountWithdrawOrderVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 总提现金额
	 */
	private Long totalAmount;

    /**
     * 订单列表
     */
	private PageInfo<CorpAccWithdrawOrderVO> orderList;

}
