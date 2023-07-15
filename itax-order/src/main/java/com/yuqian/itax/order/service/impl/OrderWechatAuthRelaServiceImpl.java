package com.yuqian.itax.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.entity.WechatMessageTemplateEntity;
import com.yuqian.itax.message.enums.WeChatMessageTemplateTypeEnum;
import com.yuqian.itax.message.service.WechatMessageTemplateService;
import com.yuqian.itax.order.dao.OrderWechatAuthRelaMapper;
import com.yuqian.itax.order.entity.OrderWechatAuthRelaEntity;
import com.yuqian.itax.order.enums.OrderWeChatAuthEnum;
import com.yuqian.itax.order.service.OrderWechatAuthRelaService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("orderWechatAuthRelaService")
@Slf4j
public class OrderWechatAuthRelaServiceImpl extends BaseServiceImpl<OrderWechatAuthRelaEntity,OrderWechatAuthRelaMapper> implements OrderWechatAuthRelaService {

    @Autowired
    private WechatMessageTemplateService wechatMessageTemplateService;

    @Autowired
    private DictionaryService dictionaryService;

    @Override
    public void addOrUpdate(String orderNo, int templateType, Integer flag, MemberAccountEntity member, String oemCode) {
        WechatMessageTemplateEntity wechatMessageTemplateEntity = wechatMessageTemplateService.getByTemplateType(templateType, oemCode);
        if (wechatMessageTemplateEntity == null) {
            log.error("微信通知模板不存在，请求参数：orderNo：{}，templateType：{}，oemCode：{}", orderNo, templateType, oemCode);
            return;
        }
        OrderWechatAuthRelaEntity entity = new OrderWechatAuthRelaEntity();
        entity.setOrderNo(orderNo);
        entity.setWechatTmplType(templateType);
        entity.setOemCode(oemCode);
        List<OrderWechatAuthRelaEntity> list = mapper.select(entity);
        if (CollectionUtil.isEmpty(list)) {
            //新增
            entity.setAuthStatus(flag);
            entity.setAddTime(new Date());
            entity.setAddUser(member.getMemberAccount());
            entity.setMemberId(member.getId());
            entity.setWechatTmplId(wechatMessageTemplateEntity.getId());
            mapper.insertSelective(entity);
            return;
        }
        //更新
        entity = list.get(0);
        entity.setAuthStatus(flag);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(member.getMemberAccount());
        entity.setWechatResult(null);
        mapper.updateByPrimaryKeySelective(entity);
    }

    @Override
    public OrderWechatAuthRelaEntity queryByOrderNo(String orderNo, Integer templateType, Integer authStatus) {
        return mapper.queryByOrderNo(orderNo, templateType, authStatus);
    }

    @Override
    @Transactional
    public String sendNotice(String orderNo, int templateType, MemberAccountEntity member, String pageDictCode, Map<String, Object> args) {
        OrderWechatAuthRelaEntity orderWechatAuthRelaEntity = mapper.queryByOrderNo(orderNo, templateType, OrderWeChatAuthEnum.YES.getValue());
        if (orderWechatAuthRelaEntity == null) {
            return null;
        }
        String page = dictionaryService.getValueByCode(pageDictCode);
        String result = wechatMessageTemplateService.sendNotice(member.getOpenId(), page, templateType, member.getOemCode(), args);
        orderWechatAuthRelaEntity.setWechatResult(result);
        mapper.updateByPrimaryKeySelective(orderWechatAuthRelaEntity);
        return result;
    }
}

