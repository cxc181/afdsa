package com.yuqian.itax.agreement.service.impl;

import com.yuqian.itax.agreement.dao.ParkAgreementTemplateRelaMapper;
import com.yuqian.itax.agreement.entity.ParkAgreementTemplateRelaEntity;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import com.yuqian.itax.agreement.service.ParkAgreementTemplateRelaService;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("parkAgreementTemplateRelaService")
public class ParkAgreementTemplateRelaServiceImpl extends BaseServiceImpl<ParkAgreementTemplateRelaEntity,ParkAgreementTemplateRelaMapper> implements ParkAgreementTemplateRelaService {

    @Override
    public void batchInsert(List<ParkAgreementTemplateRelaEntity> list) {
        mapper.batchInsert(list);
    }

    @Override
    public void deleteByParkId(Long parkId) {
        mapper.deleteByParkId(parkId);
    }

    @Override
    public void deleteByParkIdAndProductId(Long parkId, Long product) {
        mapper.deleteByParkIdAndProductId(parkId,product);
    }

    @Override
    public List<AgreementTemplateInfoVO> getTemplateInfo(Long parkId,Integer templateType,Long productId) {
        return mapper.getTemplateInfo(parkId,templateType,productId);
    }

    @Override
    public List<AgreementTemplateInfoVO> getTemplateInfoByParkIdAndProductId(Long parkId, Long productId) {
        return mapper.getTemplateInfoByParkIdAndProductId(parkId,productId);
    }

    @Override
    public List<Long> getParkAgreementIdByAgreementTemplateId(Long agreementTemplateId) {
        return mapper.getParkAgreementIdByAgreementTemplateId(agreementTemplateId);
    }
}

