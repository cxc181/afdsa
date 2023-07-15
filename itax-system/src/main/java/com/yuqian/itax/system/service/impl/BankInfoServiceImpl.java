package com.yuqian.itax.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.BankInfoMapper;
import com.yuqian.itax.system.entity.BankInfoEntity;
import com.yuqian.itax.system.entity.vo.BankInfoVO;
import com.yuqian.itax.system.service.BankInfoService;
import com.yuqian.itax.util.util.AuthKeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 银行信息service impl
 */
@Service("bankInfoService")
public class BankInfoServiceImpl extends BaseServiceImpl<BankInfoEntity,BankInfoMapper> implements BankInfoService {

    @Resource
    private BankInfoMapper bankInfoMapper;
    @Autowired
    OemParamsService oemParamsService;

    @Override
    public List<BankInfoVO> listBankInfo() {
        return bankInfoMapper.listBankInfo();
    }

    @Override
    public boolean check4ToBankCard(String oemCode,String bankUserName,String idCard,String bankNumber,String BankPhone) {
        //读取要素认证相关配置 paramsType=6
        OemParamsEntity paramsEntity = oemParamsService.getParams(oemCode, 6);
        if(null == paramsEntity){
            throw new BusinessException("未配置银行卡四要素相关信息！");
        }
        // agentNo
        String agentNo = paramsEntity.getAccount();
        // signKey
        String signKey = paramsEntity.getSecKey();
        // authUrl
        String authUrl = paramsEntity.getUrl();

        String authResult = AuthKeyUtils.auth4Key(agentNo,signKey,authUrl,bankUserName,idCard,
                bankNumber,BankPhone,paramsEntity.getParamsValues());

        if(StringUtils.isBlank(authResult)){
            throw new BusinessException("银行卡四要素认证失败");
        }
        JSONObject resultObj = JSONObject.parseObject(authResult);

        if(!"00".equals(resultObj.getString("code"))){
            return false;
        }
        return true;
    }
}

