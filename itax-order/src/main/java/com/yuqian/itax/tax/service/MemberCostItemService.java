package com.yuqian.itax.tax.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.tax.dao.MemberCostItemMapper;
import com.yuqian.itax.tax.entity.MemberCostItemEntity;
import com.yuqian.itax.tax.entity.vo.CommonCostItemVO;

import java.util.List;

/**
 * 会员成本项表service
 * 
 * @Date: 2022年03月10日 10:42:35 
 * @author 蒋匿
 */
public interface MemberCostItemService extends IBaseService<MemberCostItemEntity,MemberCostItemMapper> {

    /**
     * 查询用户常用成本项
     * @param memberId
     * @return
     */
    List<CommonCostItemVO> findCommonCostItem(Long memberId);
}

