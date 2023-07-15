package com.yuqian.itax.user.entity.vo;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 对公户账户明细前端展示BEAN
 * @Author  Kaven
 * @Date   2020/9/8 10:47
*/
@Getter
@Setter
public class PublicAccountDetailVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 总收入
	 */
	private Long incomes;

	/**
	 * 总支出
	 */
	private Long expenses;

	/**
	 * 账户明细列表
	 */
	private PageInfo<CorporateAccountCollectionRecordVO> flowPageList;

}
