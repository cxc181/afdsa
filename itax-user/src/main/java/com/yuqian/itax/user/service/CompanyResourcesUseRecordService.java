package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.dao.CompanyResourcesUseRecordMapper;
import com.yuqian.itax.user.entity.CompanyResourcesUseRecordEntity;
import com.yuqian.itax.user.entity.query.CompanyResourcesUseRecordAdminQuery;
import com.yuqian.itax.user.entity.query.CompanyResourcesUseRecordQuery;
import com.yuqian.itax.user.entity.vo.ComRscTimeoutRecordVO;
import com.yuqian.itax.user.entity.vo.CompanyResourcesUseRecordAdminVO;
import com.yuqian.itax.user.entity.vo.CompanyResourcesUseRecordVO;

import java.util.List;

/**
 * 企业资源使用记录service
 * 
 * @Date: 2019年12月10日 13:42:03 
 * @author 蒋匿
 */
public interface CompanyResourcesUseRecordService extends IBaseService<CompanyResourcesUseRecordEntity,CompanyResourcesUseRecordMapper> {
    /**
     * @Description 分页查询用章记录列表
     * @Author  Kaven
     * @Date   2019/12/14 14:25
     * @Param  query
     * @Return PageInfo
    */
    PageInfo<CompanyResourcesUseRecordVO> listPage(CompanyResourcesUseRecordQuery query);


    /**
     * @Description 后台分页查询用章记录列表
     * @Author  HZ
     * @Date   2019/12/14 14:25
     * @Param  query
     * @Return PageInfo
     */
    PageInfo<CompanyResourcesUseRecordAdminVO> companyResourcesUseRecordPage(CompanyResourcesUseRecordAdminQuery query);

    /**
     * @Description 用章审批
     * @Author  Kaven
     * @Date   2019/12/14 14:49
     * @Param  userId  id auditStatus auditDesc imgAddr
     * @Exception BusinessException
    */
    void approval(Long userId, Long id, Integer auditStatus, String auditDesc, String imgAddr) throws BusinessException;

    /**
     * @Description 查询用章未审核超过24小时或72小时的记录数
     * @Author  Kaven
     * @Date   2020/3/4 14:39
     * @Param  queryType:1-超过24小时 2-超过72小时
     * @Return List
     * @Exception 
    */
    List<ComRscTimeoutRecordVO> selectTimeOutList(Integer queryType);

    /**
     * 根据用户账号修改通知状态
     * @param auditUser
     * @param queryType
     */
    void updateResourcesNoticeStatusByAccount(Long auditUser,Integer queryType);
}

