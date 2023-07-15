package com.yuqian.itax.tax.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.tax.entity.MemberCostItemEntity;
import com.yuqian.itax.tax.entity.vo.CommonCostItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员成本项表dao
 * 
 * @Date: 2022年03月10日 10:42:35 
 * @author 蒋匿
 */
@Mapper
public interface MemberCostItemMapper extends BaseMapper<MemberCostItemEntity> {

    /**
     * 查询用户常用成本项
     * @return
     */
    List<CommonCostItemVO> queryCommonCostItem(@Param("memberId") Long memberId);
}

