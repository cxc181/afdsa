package com.yuqian.itax.message.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.message.entity.SmsTemplateEntity;
import com.yuqian.itax.message.dao.SmsTemplateMapper;

import java.util.List;

/**
 * 短信模板配置service
 *
 * @Date: 2019年12月06日 14:59:14
 * @author Kaven
 */
public interface SmsTemplateService extends IBaseService<SmsTemplateEntity,SmsTemplateMapper> {

    /**
     * @Description 根据模板ID查询短信模板
     * @Author  Kaven
     * @Date   2019/12/6 15:50
     * @Param  templateId
     * @Return SmsTemplateEntity
     */
    SmsTemplateEntity getMessageTemplateById(Long templateId);

    /**
     * 批量插入短信模板
     */
    public void batchInsertSmsTemplateEntity(List<SmsTemplateEntity> list,String oemCode,String account) ;
}

