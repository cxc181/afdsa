package com.yuqian.itax.system.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.dao.IndustryMapper;
import com.yuqian.itax.system.entity.IndustryEntity;
import com.yuqian.itax.system.entity.dto.BizScopeDTO;
import com.yuqian.itax.system.entity.dto.IndustryApiDTO;
import com.yuqian.itax.system.entity.dto.IndustryInfoDTO;
import com.yuqian.itax.system.entity.query.IndustryQuery;
import com.yuqian.itax.system.entity.vo.*;

import java.util.List;

/**
 * 行业service
 * 
 * @Date: 2019年12月08日 20:37:33 
 * @author 蒋匿
 */
public interface IndustryService extends IBaseService<IndustryEntity,IndustryMapper> {

    /**
     * @Description 根据行业ID获取核定税种/开票类目/经营范围信息并生成名称示例
     * @Author  Kaven
     * @Date   2019/12/23 11:06
     * @Param  industryId parkId
     * @Return IndustryInfoVO
     * @Exception
    */
    IndustryInfoVO getById(Long industryId, Long parkId) throws BusinessException;

    /**
     * 分页查询行业信息
     * @param query
     * @return
     */
    PageInfo<IndustryInfoVO> listPage(IndustryQuery query);


    /**
     * 查询行业信息
     * @param query
     * @return
     */
    List<IndustryInfoVO> queryIndustryList(IndustryQuery query);
    /**
     * 保存行业数据
     * @param dto
     */
    void add(IndustryInfoDTO dto);

    /**
     * 删除行业
     * @param industryId
     */
    void delete(Long industryId);

    /**
     * 编辑
     * @param industryEntity
     * @param dto
     */
    void edit(IndustryEntity industryEntity, IndustryInfoDTO dto);

    /**
     * 查询行业列表
     * @param entity
     * @return
     */
    List<IndustryApiVO> getIndustryList(IndustryApiDTO entity) throws BusinessException;

    /**
     * @Description 根据行业id获取开票类目和经营范围(纯API)
     * @Author  Kaven
     * @Date   2020/7/31 09:16
     * @Param   BizScopeDTO
     * @Return  ScopeCategoryVO
     * @Exception  BusinessException
    */
    ScopeCategoryVO getBizScopeAndInvCategory(BizScopeDTO dto) throws BusinessException;

    /**
     * @Description 查询行业列表（剔除oem机构黑名单行业）
     * @Author  Kaven
     * @Date   2020/8/10 15:00
     * @Param   oemCode parkId companyType industryName
     * @Return  List<IndustryEntity>
     * @Exception  BusinessException
    */
    List<IndustryApiVO> selectIndustry(String oemCode,Long parkId,Integer companyType,String industryName) throws BusinessException;

    /**
     * 根据园区获取oem机构例外行业
     * @param oemCode
     * @param parkId
     * @return
     */
    List<IndustryAdminVO> queryBlackListByParkId(String oemCode, Long parkId);

    /**
     * 根据行业id查询行业信息
     *
     * @param parkId
     * @param ids
     * @return
     */
    List<IndustryEntity> selectByIndustryIds(Long parkId, List<Long> ids);

    /**
     * 根据园区id获取行业
     * @param parkIds
     * @return
     */
    List<IndustryAndParkInfoVo> queryIndustryByParkIds(List<String> parkIds);

    /**
     * 园区行业介绍
     * @param industryId
     * @return
     */
    ParkIndustryPresentationVO presentation(Long industryId);
}

