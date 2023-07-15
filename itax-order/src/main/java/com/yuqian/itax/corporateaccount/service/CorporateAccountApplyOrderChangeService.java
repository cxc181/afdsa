package com.yuqian.itax.corporateaccount.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountApplyOrderChangeEntity;
import com.yuqian.itax.corporateaccount.dao.CorporateAccountApplyOrderChangeMapper;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountApplyOrderEntity;

/**
 * 对公户申请订单service
 * 
 * @Date: 2020年09月07日 09:12:13 
 * @author 蒋匿
 */
public interface CorporateAccountApplyOrderChangeService extends IBaseService<CorporateAccountApplyOrderChangeEntity,CorporateAccountApplyOrderChangeMapper> {

    void addCorporateAccountApplyOrderChange(CorporateAccountApplyOrderEntity corporateAccountApplyOrderEntity, Integer status, String account);
}

