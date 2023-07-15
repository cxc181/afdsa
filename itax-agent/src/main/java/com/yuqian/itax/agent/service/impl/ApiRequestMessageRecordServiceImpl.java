package com.yuqian.itax.agent.service.impl;

import com.yuqian.itax.agent.dao.ApiRequestMessageRecordMapper;
import com.yuqian.itax.agent.entity.ApiRequestMessageRecordEntity;
import com.yuqian.itax.agent.service.ApiRequestMessageRecordService;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service("apiRequestMessageRecordService")
public class ApiRequestMessageRecordServiceImpl extends BaseServiceImpl<ApiRequestMessageRecordEntity,ApiRequestMessageRecordMapper> implements ApiRequestMessageRecordService {

    @Override
    public void addRequestRecord(String oemCode, String urlAddress, String sign, String version, String resultMessage, String reqParams) {
        log.info("保存api接口请求报文记录：{},{},{},{},{},{}",oemCode,urlAddress,sign,version,resultMessage,reqParams);

        ApiRequestMessageRecordEntity record = new ApiRequestMessageRecordEntity();
        record.setAddTime(new Date());
        record.setAddUser("admin");
        record.setOemCode(oemCode);
        record.setRequestParams(reqParams);
        record.setVersion(version);
        record.setSignContent(sign);
        record.setUrlAddress(urlAddress);
        record.setResultMsg(resultMessage);
        this.insertSelective(record);
    }
}

