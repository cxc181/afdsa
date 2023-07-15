package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.CompanyResourcesUseRecordEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.query.CompanyResourcesUseRecordAdminQuery;
import com.yuqian.itax.user.entity.query.CompanyResourcesUseRecordQuery;
import com.yuqian.itax.user.entity.vo.ComRscTimeoutRecordVO;
import com.yuqian.itax.user.entity.vo.CompanyResourcesUseRecordAdminVO;
import com.yuqian.itax.user.entity.vo.CompanyResourcesUseRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业资源使用记录dao
 * 
 * @Date: 2019年12月10日 13:42:03 
 * @author 蒋匿
 */
@Mapper
public interface CompanyResourcesUseRecordMapper extends BaseMapper<CompanyResourcesUseRecordEntity> {
    /**
     * @Description 分页查询用章审批记录
     * @Author  Kaven
     * @Date   2019/12/14 15:16
     * @Param  query
     * @Return List
    */
    List<CompanyResourcesUseRecordVO> listRecordPage(CompanyResourcesUseRecordQuery query);

    List<CompanyResourcesUseRecordAdminVO> getCompanyResourcesUseRecordList(CompanyResourcesUseRecordAdminQuery query);

    /**
     * @Description 查询超过24/72小时未处理的用章申请数
     * @Author  Kaven
     * @Date   2020/3/4 14:48
     * @Param  queryType
     * @Return List<ComRscTimeoutRecordVO>
     * @Exception
    */
    List<ComRscTimeoutRecordVO> selectTimeOutList(@Param(value="queryType") Integer queryType);

    /**
     * 根据用户账号修改通知状态
     * @param auditUser
     * @param queryType
     */
    void updateResourcesNoticeStatusByAccount(@Param(value="auditUser")Long auditUser,@Param(value="queryType")Integer queryType);
}

