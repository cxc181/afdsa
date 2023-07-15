package com.yuqian.itax.agent.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.dto.OemSysConfigDTO;
import com.yuqian.itax.agent.entity.po.OemPO;
import com.yuqian.itax.agent.entity.query.OemQuery;
import com.yuqian.itax.agent.entity.vo.OemDetailVO;
import com.yuqian.itax.agent.entity.vo.OemListVO;
import com.yuqian.itax.agent.entity.vo.OemSysConfigDetailVO;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.agent.dao.OemMapper;

import java.util.List;
import java.util.Map;

/**
 * 机构管理service
 * 
 * @Date: 2019年12月07日 20:32:18 
 * @author 蒋匿
 */
public interface OemService extends IBaseService<OemEntity,OemMapper> {
    /**
     * 根据OEMCODE获取OEM实体信息
     */
    OemEntity getOem(String oemCode);

    /**
     * OEM机构列表
     */
	List<OemListVO> queryOemList();

    PageInfo<OemListVO> queryOemPageInfo(OemQuery oemQuery);

    /**
     * 获取当天需要结算分润的机构列表
     * @return
     */
	List<OemEntity> findOemInfosBySettlementCycle();

    /**
     * 状态变更
     */
    void updateOem( Long id, Integer status,String userAccount);
    /**
     *编辑OEM机构
     */
    OemEntity updateOemPolicy(OemPO oemPO,String userAccount);
    /**
     * 新增OEM机构
     */
    OemEntity addOemEntity(OemPO oemPO,String userAccount) throws BusinessException;


    /**
     * 根据机构id获取机构详情
     * @param id
     * @return
     */
    OemDetailVO getOemDteatailById(Long id);

    /**
     * 根据机构id获取机构系统配置详情
     * @param id
     * @return
     */
    OemSysConfigDetailVO queryOemSysConfigDetail(Long id);

    void setChannelParams(OemSysConfigDTO dto,String oemCode,String updateUser);

    void setChannelParams(OemPO dto,String oemCode,String updateUser);

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
    Map<String, String> getSecretKey(String oemCode, String accessPartyCode);

    /**
     * 根据协议模板id获取oem机构id
     * @param agreementTemplateId
     * @return
     */
    List<Long> getOemIdByAgreementTemplateId(Long agreementTemplateId);
}

