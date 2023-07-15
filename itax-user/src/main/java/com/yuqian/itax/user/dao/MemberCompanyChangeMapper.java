package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.MemberCompanyChangeEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.vo.CompanyChangeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业变更表dao
 * 
 * @Date: 2021年06月04日 09:31:10 
 * @author 蒋匿
 */
@Mapper
public interface MemberCompanyChangeMapper extends BaseMapper<MemberCompanyChangeEntity> {

    /**
     * 根据企业id查询企业变动记录
     * @param companyId
     * @return
     */
    List<CompanyChangeVo> queryMemberCompanyChangeByCompanyId(@Param("companyId") Long companyId);
	
}

