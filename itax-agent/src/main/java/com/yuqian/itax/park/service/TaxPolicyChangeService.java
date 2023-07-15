package com.yuqian.itax.park.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.dao.TaxPolicyChangeMapper;
import com.yuqian.itax.park.entity.TaxPolicyChangeEntity;
import com.yuqian.itax.park.entity.query.TaxPolicyChangeQuery;
import com.yuqian.itax.park.entity.vo.TaxPolicyChangeVO;

/**
 * 税费政策变更表service
 * 
 * @Date: 2022年04月08日 11:45:08 
 * @author 蒋匿
 */
public interface TaxPolicyChangeService extends IBaseService<TaxPolicyChangeEntity,TaxPolicyChangeMapper> {

    /**
     * 分页查询历史记录
     * @return
     */
    PageInfo<TaxPolicyChangeVO> getTaxPolicyChangeList(TaxPolicyChangeQuery query);
	
}

