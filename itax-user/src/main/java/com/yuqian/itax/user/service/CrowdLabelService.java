package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.vo.OemAccessPartyDetailVO;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.dao.CrowdLabelMapper;
import com.yuqian.itax.user.entity.CrowdLabelEntity;
import com.yuqian.itax.user.entity.vo.CrowdLabelVO;

import java.util.List;

/**
 * 人群标签service
 * 
 * @Date: 2021年07月15日 15:48:57 
 * @author 蒋匿
 */
public interface CrowdLabelService extends IBaseService<CrowdLabelEntity,CrowdLabelMapper> {

    List<Long> queryMemberIdByOemCodeAndStatus(String oemCode);

    PageInfo<CrowdLabelVO> listPageCrowdLabel(CrowdLabelVO query);

    List<CrowdLabelVO> listCrowdLabel(CrowdLabelVO query);

    /**
     * 标签作废
     * @param id
     */
    void updateStatusByid(Long id,String user);

    /**
     * 根据人群名称查询
     * @param labelName
     * @return
     */
    CrowdLabelEntity queryCrowdLabelByLabelName(String labelName,String oemCode);

    /**
     * 根据人群名称查询不包括id
     * @param labelName
     * @return
     */
    CrowdLabelEntity queryCrowdLabelByLabelNameNotId(String labelName,String oemCode,Long id);

    /**
     * 根据oem机构和标签名称模糊查询
     * @param labelName
     * @param oemCode
     * @return
     */
    List<CrowdLabelEntity> getCrowdLabelByLabelName(String labelName,String oemCode);

    /**
     * 根据oemCode获取人群标签
     * @param oemCode
     * @param status
     * @return
     */
    List<CrowdLabelEntity> queryCrowdLabelByOemCode(String oemCode,Integer status);

    /**
     * 根据接入方id获取人群标签
     * @param accessPartyId
     * @return
     */
    List<CrowdLabelEntity> queryByAccessPartyId(Long accessPartyId);

    /**
     * 根据人群标签获取使用该人群标签的活动
     * @param crowdId
     * @return
     */
    Integer getActivityByCrowdId(Long crowdId);

    /**
     * 新增人群标签
     * @param crowdLabelEntity
     */
    void addCrowd(CrowdLabelEntity crowdLabelEntity);


    /**
     * 修改人群标签
     * @param crowdLabelEntity
     */
    void updateCrowd(CrowdLabelEntity crowdLabelEntity);

    /**
     * 获取详情信息
     * @param crowdLabelId
     * @return
     */
    OemAccessPartyDetailVO getDetailInfo(Long crowdLabelId);


    /**
     * 更新人群标签用户数
     * @param crowdLabel
     */
    void updateMemberNumber(CrowdLabelEntity crowdLabel);

    List<Long> check(Long id,String oemCode);
}

