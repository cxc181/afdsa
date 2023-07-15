package com.yuqian.itax.agent.dao;

import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.entity.query.OemAccessPartyQuery;
import com.yuqian.itax.agent.entity.vo.OemAccessPartyInfoVO;
import com.yuqian.itax.agent.entity.vo.OemAccessPartyVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 接入方信息表dao
 * 
 * @Date: 2021年08月04日 17:18:19 
 * @author 蒋匿
 */
@Mapper
public interface OemAccessPartyMapper extends BaseMapper<OemAccessPartyEntity> {

    /**
     * 根据接入方编码查询接入方信息
     * @param query
     * @return
     */
    OemAccessPartyEntity queryByCode(OemAccessPartyQuery query);

    /**
     * 根据oemCode 和状态查询接入方信息
     * @param oemCode
     * @param status
     * @return
     */
    List<OemAccessPartyEntity> queryByOemCodeAndStatus(@Param("oemCode") String oemCode, @Param("status")Integer status);


    /**
     * 根据oemCode 和状态查询接入方信息
     * @param oemCode
     * @param status
     * @return
     */
    List<OemAccessPartyInfoVO> queryByOemCode(@Param("oemCode") String oemCode,  @Param("status") Integer status);

    /**
     * 根据条件查询信息
     * @param oemAccessPartyQuery
     * @return
     */
    List<OemAccessPartyVO> queryOemAccessPartyPageInfo(OemAccessPartyQuery oemAccessPartyQuery);

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
    OemAccessPartyEntity queryByOemCodeAndAccessPartyName(@Param("oemCode")  String oemCode,@Param("accessPartyName") String accessPartyName,@Param("id") Long id);

    /**
     * 根据编码查询
     * @param oemAccessPartyCode
     * @return
     */
    OemAccessPartyEntity queryByAccessPartyCode(@Param("oemAccessPartyCode") String oemAccessPartyCode);
}

