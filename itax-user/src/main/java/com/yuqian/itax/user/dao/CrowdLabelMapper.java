package com.yuqian.itax.user.dao;

import com.yuqian.itax.agent.entity.vo.OemAccessPartyDetailVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.CrowdLabelEntity;
import com.yuqian.itax.user.entity.vo.CrowdLabelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人群标签dao
 * 
 * @Date: 2021年07月15日 15:48:57 
 * @author 蒋匿
 */
@Mapper
public interface CrowdLabelMapper extends BaseMapper<CrowdLabelEntity> {

    List<Long> queryMemberIdByOemCodeAndStatus(String oemCode);

    List listPageCrowdLabel(CrowdLabelVO query);

    void updateStatusByid(Long id);

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
     * 根据oemCode获取人群标签
     * @param oemCode
     * @param status
     * @return
     */
    List<CrowdLabelEntity> queryCrowdLabelByOemCode(String oemCode,Integer status);

    /**
     * 根据oem机构和标签名称模糊查询
     * @param crowdLabelName
     * @param oemCode
     * @return
     */
    List<CrowdLabelEntity> getCrowdLabelByLabelName(String crowdLabelName,String oemCode);

    /**
     * 根据人群标签获取使用该人群标签的活动
     * @param crowdId
     * @return
     */
    Integer getActivityByCrowdId(Long crowdId);

    /**
     * 获取详情信息
     * @param crowdLabelId
     * @return
     */
    OemAccessPartyDetailVO getDetailInfo(@Param("crowdLabelId") Long crowdLabelId);


    /**
     * 根据接入方id获取人群标签
     * @param accessPartyId
     * @return
     */
    List<CrowdLabelEntity> queryByAccessPartyId(@Param("accessPartyId") Long accessPartyId);

}

