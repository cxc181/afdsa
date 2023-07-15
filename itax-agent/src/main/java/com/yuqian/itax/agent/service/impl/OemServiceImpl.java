package com.yuqian.itax.agent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.dao.OemMapper;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.entity.dto.OemSysConfigDTO;
import com.yuqian.itax.agent.entity.po.OemAccessPartyPO;
import com.yuqian.itax.agent.entity.po.OemPO;
import com.yuqian.itax.agent.entity.query.OemQuery;
import com.yuqian.itax.agent.entity.vo.OemDetailVO;
import com.yuqian.itax.agent.entity.vo.OemListVO;
import com.yuqian.itax.agent.entity.vo.OemSysConfigDetailVO;
import com.yuqian.itax.agent.enums.OemAccessPartyStatusEnum;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.enums.OemStatusEnum;
import com.yuqian.itax.agent.service.*;
import com.yuqian.itax.agreement.entity.AgreementTemplateEntity;
import com.yuqian.itax.agreement.service.AgreementTemplateService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.util.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


@Service("oemService")
public class OemServiceImpl extends BaseServiceImpl<OemEntity,OemMapper> implements OemService {

    @Autowired
    OemParkRelaService oemParkRelaService;

    @Autowired
    private OemParamsService oemParamsService;

    @Autowired
    private OemConfigService oemConfigService;

    @Resource
    private OemAccessPartyService oemAccessPartyService;

    @Resource
    private AgreementTemplateService agreementTemplateService;


    @Override
    public OemEntity getOem(String oemCode) {
        return this.mapper.getOem(oemCode);
    }

    /**
     * 获取当天需要结算分润的机构列表
     * @return
     */
    @Override
    public List<OemEntity> findOemInfosBySettlementCycle(){
        return mapper.findOemInfosBySettlementCycle();
    }

    @Override
    public List<OemListVO> queryOemList() {
        return mapper.queryOemList(null);
    }

    @Override
    public PageInfo<OemListVO> queryOemPageInfo(OemQuery oemQuery) {
        PageHelper.startPage(oemQuery.getPageNumber(), oemQuery.getPageSize());
        return new PageInfo<>(this.mapper.queryOemList(oemQuery));
    }

    @Override
    public void updateOem( Long id ,Integer status,String userAccount) {
        OemEntity oemEntity=mapper.selectByPrimaryKey(id);
        if(oemEntity.getOemStatus()==0){//机构状态 0-下架1-上架 2-暂停 3-待上架
            throw  new BusinessException("下架状态不能修改状态");
        }
        oemEntity.setUpdateTime(new Date());
        oemEntity.setUpdateUser(userAccount);
        oemEntity.setOemStatus(status);
        mapper.updateByPrimaryKey(oemEntity);
    }

    @Override
    public OemEntity updateOemPolicy(OemPO oemPO,String userAccount) {
        //配置机构表
        OemEntity oemEntity=mapper.selectByPrimaryKey(oemPO.getId());
//        oemEntity.setSettlementCycle(oemPO.getSettlementCycle());
//        oemEntity.setSettlementType(oemPO.getSettlementType());
//        oemEntity.setUpdateTime(new Date());
//        oemEntity.setUpdateUser(userAccount);
//        mapper.updateByPrimaryKey(oemEntity);
        return oemEntity;
    }

    @Override
    public OemEntity addOemEntity(OemPO oemPO, String userAccount) throws BusinessException {
        OemEntity oem=mapper.getOem(oemPO.getOemCode());
        if(oem!=null){
            throw  new BusinessException("机构已经存在，请确认后再继续新增。");
        }
        OemEntity oemEntity=new OemEntity();
        oemEntity.setOemCode(oemPO.getOemCode());
        oemEntity.setOemSecret( UUID.randomUUID().toString().replaceAll("-",""));
        oemEntity.setOemName(oemPO.getOemName());
        oemEntity.setCompanyName(oemPO.getCompanyName());
        oemEntity.setOemLogo(oemPO.getOemLogo());
        oemEntity.setOemUser(oemPO.getUsername());
        oemEntity.setOemPhone(oemPO.getPhone());
        oemEntity.setNetAddress(oemPO.getNetAddress());
        oemEntity.setOemStatus(3);
        oemEntity.setCustomerServiceTel(oemPO.getCustomerServiceTel());
        oemEntity.setAddTime(new Date());
        oemEntity.setAddUser(userAccount);
        oemEntity.setBelongsCompanyAddress(oemPO.getBelongsCompanyAddress());
        oemEntity.setEmployeesLimit(200);//默认200员工上限
        oemEntity.setEin(oemPO.getEin());
        oemEntity.setRemark(oemPO.getRemark());
        oemEntity.setIsBigCustomer(oemPO.getIsBigCustomer());
        if (StringUtil.isNotBlank(oemPO.getOfficialSealImg())){
            oemEntity.setOfficialSealImg(oemPO.getOfficialSealImg());
        }
        if (StringUtil.isNotBlank(oemPO.getOfficialSealImgPublic())){
            oemEntity.setOfficialSealImgPublic(oemPO.getOfficialSealImgPublic());
        }
        if (oemPO.getIsSendAuditBillsMessage() != null){
            oemEntity.setIsSendAuditBillsMessage(oemPO.getIsSendAuditBillsMessage());
        }
        if (StringUtil.isNotBlank(oemPO.getOemAppid()) && oemPO.getIsCheckstand() == 1){
            oemEntity.setOemAppid(oemPO.getOemAppid());
        }
        if (StringUtil.isNotBlank(oemPO.getOtherPayOemcode()) && oemPO.getIsOtherOemPay() == 1){
            oemEntity.setOtherPayOemcode(oemPO.getOtherPayOemcode());
        }
        // 收款银行账号
        if(StringUtil.isNotBlank(oemPO.getReceivingBankAccount())){
            oemEntity.setReceivingBankAccount(oemPO.getReceivingBankAccount());
        }
        // 收款账号开户行
        if(StringUtil.isNotBlank(oemPO.getReceivingBankAccountBranch())){
            oemEntity.setReceivingBankAccountBranch(oemPO.getReceivingBankAccountBranch());
        }
        oemEntity.setIsCheckstand(oemPO.getIsCheckstand());
        oemEntity.setIsOtherOemPay(oemPO.getIsOtherOemPay());
        if(oemPO.getIsCheckstand()!=null && oemPO.getIsCheckstand() == 1){
            OemAccessPartyPO po = new OemAccessPartyPO();
            po.setAccessPartyCode(oemEntity.getOemCode().trim()+"SYT");
            po.setAccessPartyName(oemEntity.getOemName().trim()+"收银台");
            po.setOemCode(oemEntity.getOemCode());
            po.setRemark("收银台oem机构自动添加");
            OemAccessPartyEntity entity = oemAccessPartyService.queryByAccessPartyCode(po.getAccessPartyCode().trim());
            if (entity != null){
                throw  new BusinessException("编码重复，请重新输入！");
            }
            entity = oemAccessPartyService.queryByOemCodeAndAccessPartyName(po.getOemCode(),po.getAccessPartyName().trim(),null);
            if (entity != null){
                throw  new BusinessException("收银台名称已被使用，请重新输入！");
            }
            oemAccessPartyService.addOemAccessParty(po,userAccount);
        }
        mapper.insert(oemEntity);
        return oemEntity;
    }

    @Override
    public OemDetailVO getOemDteatailById(Long id) {
        OemDetailVO oemDetailVO= new OemDetailVO();
        OemEntity oemEntity=mapper.selectByPrimaryKey(id);
        if(oemEntity == null){
            throw new BusinessException("未找到oem机构信息");
        }
        //属性值复制
        BeanUtil.copyProperties(oemEntity,oemDetailVO);
        if(StringUtils.isNotBlank(oemEntity.getOtherPayOemcode())) {
            oemDetailVO.setOtherPayOemName(getOem(oemEntity.getOtherPayOemcode()).getOemName());
        }
        if (StringUtil.isNotBlank(oemEntity.getOfficialSealImg())){
            oemDetailVO.setOfficialSealImg(oemEntity.getOfficialSealImg());
        }

        //获得机构关联得园区
        List<Long> list=oemParkRelaService.queryOemParkIdList(oemEntity.getOemCode());
        oemDetailVO.setParkIdList(list);
        oemDetailVO.setCustomerServiceTel(oemEntity.getCustomerServiceTel());
        oemDetailVO.setRemark(oemEntity.getRemark());
        oemDetailVO.setAgreementTemplateId(oemEntity.getAgreementTemplateId());
        AgreementTemplateEntity entity =  agreementTemplateService.findById(oemEntity.getAgreementTemplateId());
        if (entity != null){
            oemDetailVO.setTemplateName(entity.getTemplateName());
        }
        return oemDetailVO;
    }

    /**
     * 根据机构id获取机构系统配置详情
     * @param id
     * @return
     */
    public OemSysConfigDetailVO queryOemSysConfigDetail(Long id){
        OemEntity oemEntity=mapper.selectByPrimaryKey(id);
        if(oemEntity == null){
            throw new BusinessException("未找到oem机构信息");
        }
        OemSysConfigDetailVO vo = new OemSysConfigDetailVO();
        BeanUtil.copyProperties(oemEntity,vo);
        //获取是否接入国金
        OemConfigEntity oemConfigEntity = new OemConfigEntity();
        oemConfigEntity.setOemCode(oemEntity.getOemCode());
        oemConfigEntity.setParamsCode("is_open_channel");
        oemConfigEntity = oemConfigService.selectOne(oemConfigEntity);
        vo.setIsOpenChannel(0);
        if (oemEntity.getWorkAuditWay() != null){
            vo.setWorkAuditWay(oemEntity.getWorkAuditWay());
        }
        if(oemConfigEntity == null){

        }else{
            //接入国金，则获取国金的配置
            String values = oemConfigEntity.getParamsValue();
            if("1".equals(values)){
                vo.setIsOpenChannel(1);
                OemParamsEntity oemParamsEntity = oemParamsService.getParams(oemEntity.getOemCode(),OemParamsTypeEnum.GUOJIN_CHANNEL_CONFIG.getValue());
                if(oemParamsEntity!=null){
                    vo.setChannelUrl(oemParamsEntity.getUrl());
                    vo.setSecKey(oemParamsEntity.getSecKey());
                }
            }
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setChannelParams(OemSysConfigDTO dto,String oemCode,String updateUser){
        //判断是否接入国金助手 1-接入 0-不接入
        if(ObjectUtil.equal(1,dto.getIsOpenChannel())){
            if(StringUtils.isBlank(dto.getChannelUrl())||StringUtils.isBlank(dto.getSecKey())){
                throw new BusinessException("国金请求地址、秘钥不能为空");
            }
           OemParamsEntity oemParamsEntity = oemParamsService.getParams(oemCode,OemParamsTypeEnum.GUOJIN_CHANNEL_CONFIG.getValue());
           if(oemParamsEntity!=null){
               oemParamsEntity.setAccount("GUOJIN_CHANNEL");
               oemParamsEntity.setUrl(dto.getChannelUrl());
               oemParamsEntity.setSecKey(dto.getSecKey());
               oemParamsEntity.setUpdateTime(new Date());
               oemParamsEntity.setUpdateUser(updateUser);
               oemParamsEntity.setRemark("国金助手配置修改");
               oemParamsService.editByIdSelective(oemParamsEntity);
           }else{
               oemParamsEntity = new OemParamsEntity();
               oemParamsEntity.setOemCode(oemCode);
               oemParamsEntity.setParamsType(OemParamsTypeEnum.GUOJIN_CHANNEL_CONFIG.getValue());
               oemParamsEntity.setAccount("GUOJIN_CHANNEL");
               oemParamsEntity.setUrl(dto.getChannelUrl());
               oemParamsEntity.setSecKey(dto.getSecKey());
               oemParamsEntity.setAddTime(new Date());
               oemParamsEntity.setAddUser(updateUser);
               oemParamsEntity.setStatus(1);
               oemParamsEntity.setRemark("国金助手配置修改");
               oemParamsService.insertSelective(oemParamsEntity);
           }
        }
        OemConfigEntity oemConfigEntity = new OemConfigEntity();
        oemConfigEntity.setOemCode(oemCode);
        oemConfigEntity.setParamsCode("is_open_channel");
        oemConfigEntity = oemConfigService.selectOne(oemConfigEntity);
        if(oemConfigEntity!=null){
            oemConfigEntity.setParamsValue(dto.getIsOpenChannel().toString());
            oemConfigEntity.setUpdateTime(new Date());
            oemConfigEntity.setUpdateUser(updateUser);
            oemConfigService.editByIdSelective(oemConfigEntity);
        }else{
            oemConfigEntity = new OemConfigEntity();
            oemConfigEntity.setParamsValue(dto.getIsOpenChannel().toString());
            oemConfigEntity.setOemCode(oemCode);
            oemConfigEntity.setParamsCode("is_open_channel");
            oemConfigEntity.setAddTime(new Date());
            oemConfigEntity.setAddUser(updateUser);
            oemConfigEntity.setParamsDesc("是否接入渠道 1-接入 0-不接入");
            oemConfigService.insertSelective(oemConfigEntity);
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setChannelParams(OemPO dto,String oemCode,String updateUser){
        //判断是否接入国金助手 1-接入 0-不接入
        if(ObjectUtil.equal(1,dto.getIsOpenChannel())){
            if(StringUtils.isBlank(dto.getChannelUrl())||StringUtils.isBlank(dto.getSecKey())){
                throw new BusinessException("国金请求地址、秘钥不能为空");
            }
            OemParamsEntity oemParamsEntity = oemParamsService.getParams(oemCode,OemParamsTypeEnum.GUOJIN_CHANNEL_CONFIG.getValue());
            if(oemParamsEntity!=null){
                oemParamsEntity.setAccount("GUOJIN_CHANNEL");
                oemParamsEntity.setUrl(dto.getChannelUrl());
                oemParamsEntity.setSecKey(dto.getSecKey());
                oemParamsEntity.setUpdateTime(new Date());
                oemParamsEntity.setUpdateUser(updateUser);
                oemParamsEntity.setRemark("国金助手配置修改");
                oemParamsService.editByIdSelective(oemParamsEntity);
            }else{
                oemParamsEntity = new OemParamsEntity();
                oemParamsEntity.setOemCode(oemCode);
                oemParamsEntity.setParamsType(OemParamsTypeEnum.GUOJIN_CHANNEL_CONFIG.getValue());
                oemParamsEntity.setAccount("GUOJIN_CHANNEL");
                oemParamsEntity.setUrl(dto.getChannelUrl());
                oemParamsEntity.setSecKey(dto.getSecKey());
                oemParamsEntity.setAddTime(new Date());
                oemParamsEntity.setAddUser(updateUser);
                oemParamsEntity.setStatus(1);
                oemParamsEntity.setRemark("国金助手配置修改");
                oemParamsService.insertSelective(oemParamsEntity);
            }
        }
        OemConfigEntity oemConfigEntity = new OemConfigEntity();
        oemConfigEntity.setOemCode(oemCode);
        oemConfigEntity.setParamsCode("is_open_channel");
        oemConfigEntity = oemConfigService.selectOne(oemConfigEntity);
        if(oemConfigEntity!=null){
            oemConfigEntity.setParamsValue(dto.getIsOpenChannel().toString());
            oemConfigEntity.setUpdateTime(new Date());
            oemConfigEntity.setUpdateUser(updateUser);
            oemConfigService.editByIdSelective(oemConfigEntity);
        }else{
            oemConfigEntity = new OemConfigEntity();
            oemConfigEntity.setParamsValue(dto.getIsOpenChannel().toString());
            oemConfigEntity.setOemCode(oemCode);
            oemConfigEntity.setParamsCode("is_open_channel");
            oemConfigEntity.setAddTime(new Date());
            oemConfigEntity.setAddUser(updateUser);
            oemConfigEntity.setParamsDesc("是否接入渠道 1-接入 0-不接入");
            oemConfigService.insertSelective(oemConfigEntity);
        }
    }

    @Override
    public List<OemEntity> queryOemInfoByOemCodeAndWorkAuditWay(String oemCode, Integer workAuditWay) {
        return mapper.queryOemInfoByOemCodeAndWorkAuditWay(oemCode,workAuditWay);
    }

    @Override
    public Map<String, String> getSecretKey(String oemCode, String accessPartyCode) {
        // 查询机构
        OemEntity oem = Optional.ofNullable(this.getOem(oemCode)).orElseThrow(() -> new BusinessException("未查询到机构信息"));
        if (!OemStatusEnum.YES.getValue().equals(oem.getOemStatus())) {
            throw new BusinessException("机构不可用");
        }

        // 查询接入方
        OemAccessPartyEntity accessPartyEntity = Optional.ofNullable(oemAccessPartyService.queryByAccessPartyCode(accessPartyCode)).orElseThrow(() -> new BusinessException("未查询到接入方信息"));
        if (!OemAccessPartyStatusEnum.YES.getValue().equals(accessPartyEntity.getStatus())) {
            throw new BusinessException("接入方不可用");
        }

        return mapper.querySecretKey(oemCode, accessPartyCode);
    }

    @Override
    public List<Long> getOemIdByAgreementTemplateId(Long agreementTemplateId) {
        return mapper.getOemIdByAgreementTemplateId(agreementTemplateId);
    }

}
