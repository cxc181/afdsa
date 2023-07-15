package com.yuqian.itax.agent.service.impl;

import com.yuqian.itax.agent.dao.OemParkRelaMapper;
import com.yuqian.itax.agent.entity.OemParkRelaEntity;
import com.yuqian.itax.agent.service.OemParkRelaService;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("oemParkRelaService")
public class OemParkRelaServiceImpl extends BaseServiceImpl<OemParkRelaEntity, OemParkRelaMapper> implements OemParkRelaService {

    @Override
    public List<Long> queryOemParkIdList(String oemCode) {
        return mapper.queryOemParkIdList(oemCode);
    }

    @Override
    public Map<String, String> queryOemParkCorporate(String oemCode) {
        return mapper.queryOemParkCorporate(oemCode);
    }

    @Override
    public OemParkRelaEntity queryOemParkByOemCodeAndParkId(String oemCode, Long parkId) {
        return mapper.queryOemParkByOemCodeAndParkId(oemCode,parkId);
    }

    @Override
    public AgreementTemplateInfoVO getAgreementTemplateByOemCodeAndParkId(String oemCode, Long parkId) {
        return mapper.getAgreementTemplateByOemCodeAndParkId(oemCode,parkId);
    }

    @Override
    public List<Long> getOemParkIdByAgreementTemplateId(Long agreementTemplateId) {
        return mapper.getOemParkIdByAgreementTemplateId(agreementTemplateId);
    }
}

