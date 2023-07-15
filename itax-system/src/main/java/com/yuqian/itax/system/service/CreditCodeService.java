package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.system.entity.CreditCodeEntity;

/**
 * 税务登记号查询service
 * @Author  yejian
 * @Date   2020/02/20 12:01
 */
public interface CreditCodeService {

	/**
	 * 税务登记号查询
	 * @param keyWord 查询关键字
	 * @return
	 */
	CreditCodeEntity getCreditCode(String oemCode, String keyWord) throws BusinessException;
}
