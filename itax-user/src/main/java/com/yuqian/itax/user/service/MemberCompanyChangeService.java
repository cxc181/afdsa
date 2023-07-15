package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.MemberCompanyChangeEntity;
import com.yuqian.itax.user.dao.MemberCompanyChangeMapper;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.query.MemberCompanyQuery;
import com.yuqian.itax.user.entity.vo.CompanyChangeVo;

import java.util.List;

/**
 * 企业变更表service
 * 
 * @Date: 2021年06月04日 09:31:10 
 * @author 蒋匿
 */
public interface MemberCompanyChangeService extends IBaseService<MemberCompanyChangeEntity,MemberCompanyChangeMapper> {

    /**
     * 根据企业id查询企业变动记录
     * @param
     * @return
     */
    PageInfo<CompanyChangeVo> queryMemberCompanyChangeByCompanyId(MemberCompanyQuery query);

    /**
     * 添加企业变更记录
     * @param memberCompanyEntity
     * @param addUser
     * @param remark
     */
    void insertChangeInfo(MemberCompanyEntity memberCompanyEntity,String addUser,String remark);
	
}

