package com.yuqian.itax.message.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.message.entity.SmsEntity;
import com.yuqian.itax.message.dao.SmsMapper;
import com.yuqian.itax.message.entity.dto.SendResultDto;

import java.util.Map;

/**
 * 短信记录表service
 *
 * @Date: 2019年12月06日 11:06:12
 * @author Kaven
 */
public interface SmsService extends IBaseService<SmsEntity,SmsMapper> {
    /**
     * @Description 发送模板信息
     * @Author  Kaven
     * @Date   2019/12/6 14:35
     * @Param  phone template_type templateArgs userType
     * @Return Map
     * @Exception BusinessException
     */
    Map<String, Object> sendTemplateSms(String phone, String oemCode,String template_type, Map<String, Object> templateArgs,Integer userType) throws BusinessException;

    /**
     * @Description 新增一条新短消息流水
     * @Author  Kaven
     * @Date   2019/12/6 16:01
     * @Param  smsEntity
     * @Return int
     */
    int insertSmsOut(SmsEntity smsEntity);

    /**
     * 校验验证码
     * @param key
     * @param code
     * @throws BusinessException
     */
    void verifyCode(String key, String code) throws BusinessException;

    /**
     * 发送短信
     */
    SendResultDto sendMessge(SmsEntity smsEntity) throws BusinessException;


}

