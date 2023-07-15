package com.yuqian.itax.park.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.TaxPolicyChangeEntity;
import com.yuqian.itax.park.entity.vo.TaxPolicyChangeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 税费政策变更表dao
 * 
 * @Date: 2022年04月08日 11:45:08 
 * @author 蒋匿
 */
@Mapper
public interface TaxPolicyChangeMapper extends BaseMapper<TaxPolicyChangeEntity> {

    /**
     * 分页查询历史记录
     * @return
     */
    List<TaxPolicyChangeVO> getTaxPolicyChangeList(@Param("parkId") Long parkId,@Param("policyId") Long policyId,@Param("type") Integer type);
	
}

