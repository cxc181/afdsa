package com.yuqian.itax.agent.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.dao.OemAccessPartyMapper;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.entity.po.OemAccessPartyPO;
import com.yuqian.itax.agent.entity.query.OemAccessPartyQuery;
import com.yuqian.itax.agent.entity.vo.OemAccessPartyInfoVO;
import com.yuqian.itax.agent.entity.vo.OemAccessPartyVO;
import com.yuqian.itax.common.base.service.IBaseService;

import java.util.List;

/**
 * 接入方信息表service
 * 
 * @Date: 2021年08月04日 17:18:19 
 * @author 蒋匿
 */
public interface OemAccessPartyService extends IBaseService<OemAccessPartyEntity,OemAccessPartyMapper> {

    /**
     * 根据接入方编码查询接入方信息
     * @param query
     * @return
     */
    OemAccessPartyEntity findByCode(OemAccessPartyQuery query);

    /**
     * 根据oemCode 和状态查询接入方信息
     * @param oemCode
     * @param status
     * @return
     */
    List<OemAccessPartyEntity> queryByOemCodeAndStatus(String oemCode,Integer status);

    /**
     * 根据oemCode 和状态查询接入方信息
     * @param oemCode
     * @param status
     * @return
     */
    List<OemAccessPartyInfoVO> queryByOemCode(String oemCode,Integer status);

    /**
     * 分页查询
     * @param oemAccessPartyQuery
     * @return
     */
    PageInfo<OemAccessPartyVO> queryOemAccessPartyPageInfo(OemAccessPartyQuery oemAccessPartyQuery);

    /**
     * 新增
     * @param po
     */
    void addOemAccessParty(OemAccessPartyPO po,String userName);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    OemAccessPartyVO  queryById(Long id);

    /**
     * 根据oemCode 和接入方名称接入方编码验证
     * @param oemCode
     * @param accessPartyName
     * @param id
     * @return
     */
    OemAccessPartyEntity queryByOemCodeAndAccessPartyName(String oemCode,String accessPartyName,Long id);

    /**
     * 根据编码查询
     * @param oemAccessPartyCode
     * @return
     */
    OemAccessPartyEntity queryByAccessPartyCode(String oemAccessPartyCode);

    /**
     * 编辑H5接入方
     * @param po
     * @param userName
     */
    void updateOemAccessParty(OemAccessPartyPO po,String userName);

}

