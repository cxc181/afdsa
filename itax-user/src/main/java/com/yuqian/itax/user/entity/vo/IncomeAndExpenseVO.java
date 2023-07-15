package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 对公户账户明细收支统计VO
 * @Author  Kaven
 * @Date   2020/9/8 11:09
*/
@Getter
@Setter
public class IncomeAndExpenseVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 总收入
	 */
	private Long incomes;

	/**
	 * 总支出
	 */
	private Long expenses;
}
