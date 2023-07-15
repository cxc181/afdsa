package com.yuqian.itax.message.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.message.dao.SmsTemplateMapper;
import com.yuqian.itax.message.entity.SmsTemplateEntity;
import com.yuqian.itax.message.service.SmsTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("smsTemplateService")
public class SmsTemplateServiceImpl extends BaseServiceImpl<SmsTemplateEntity,SmsTemplateMapper> implements SmsTemplateService {
    @Resource
    private SmsTemplateMapper smsTemplateMapper;

    @Override
    public SmsTemplateEntity getMessageTemplateById(Long templateId) {
        SmsTemplateEntity entity = new SmsTemplateEntity();
        entity.setId(templateId);
        return this.smsTemplateMapper.selectByPrimaryKey(templateId);
    }

    @Override
    public void batchInsertSmsTemplateEntity(List<SmsTemplateEntity> list,String oemCode,String account) {
        mapper.addBatch(list,oemCode,new Date(),account);
    }

}

