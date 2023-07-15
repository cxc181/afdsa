package com.yuqian.itax.agent.dao;

import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.query.OemQuery;
import com.yuqian.itax.agent.entity.vo.OemListVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 机构管理dao
 * 
 * @Date: 2019年12月07日 20:32:18 
 * @author 蒋匿
 */
@Mapper
public interface OemMapper extends BaseMapper<OemEntity> {
    /**
     * 更具OEMCODE获取OEM信息
     * @param oemCode
     * @return
     */
	OemEntity getOem(@Param("oemCode")String oemCode);
    /**
     * 查询园区列表
     */
    List<OemListVO> queryOemList(OemQuery oemQuery);

    /**
     * 获取当天需要结算分润的机构列表
     * @return
     */
    List<OemEntity> findOemInfosBySettlementCycle();

    /**
     * 根据oemCode和工单审核方 查询oem信息
     * @param oemCode
     * @param workAuditWay
     * @return
     */
    List<OemEntity> queryOemInfoByOemCodeAndWorkAuditWay(String oemCode,Integer workAuditWay);

    /**
     * 获取机构及接入方秘钥
     * @param oemCode
     * @param accessPartyCode
     * @return
     */
    Map<String, String> querySecretKey(@Param("oemCode") String oemCode, @Param("accessPartyCode") String accessPartyCode);

    /**
     * 根据协议模板id获取oem机构id
     * @param agreementTemplateId
     * @return
     */
    List<Long> getOemIdByAgreementTemplateId(@Param("agreementTemplateId") Long agreementTemplateId);
}

