package com.yuqian.itax.message.service.impl;

import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.message.dao.WechatMessageTemplateMapper;
import com.yuqian.itax.message.entity.WechatMessageTemplateEntity;
import com.yuqian.itax.message.entity.vo.WechatMessageTemplateSimpleVO;
import com.yuqian.itax.message.service.WechatMessageTemplateService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.ImContentCreator;
import com.yuqian.itax.wechat.WeChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("wechatMessageTemplateService")
public class WechatMessageTemplateServiceImpl extends BaseServiceImpl<WechatMessageTemplateEntity,WechatMessageTemplateMapper> implements WechatMessageTemplateService {

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private OemParamsService oemParamsService;

    @Override
    public WechatMessageTemplateEntity getByTemplateType(Integer templateType, String oemCode) {
        return mapper.getByTemplateType(templateType, oemCode);
    }

    @Override
    public List<WechatMessageTemplateSimpleVO> getByTemplateTypes(int[] templateTypes, String oemCode) {
        return mapper.getByTemplateTypes(templateTypes, oemCode);
    }

    @Override
    public String sendNotice(String openid, String page, Integer templateType, String oemCode, Map<String, Object> args) {
        DictionaryEntity entity = dictionaryService.getByCode("wechat_request_url_prefix");
        if (entity == null) {
            throw new BusinessException("字典表未配置微信接口url");
        }
        OemParamsEntity paramsEntity = oemParamsService.getParams(oemCode, 8);
        if (null == paramsEntity) {
            throw new BusinessException("未配置微信小程序二维码相关信息！");
        }
        WechatMessageTemplateEntity templateEntity = mapper.getByTemplateType(templateType, oemCode);
        if (templateEntity == null) {
            throw new BusinessException("未配置微信订阅消息模板！");
        }
        //替换参数
        templateEntity.setTemplateParams(ImContentCreator.customMerge(templateEntity.getTemplateParams(), args));

        String appId = paramsEntity.getAccount();
        String appSecret = paramsEntity.getSecKey();
        //微信通知
        return weChatService.messageSubscribeSend(templateEntity, openid, page, entity.getDictValue(), appId, appSecret);
    }

    @Override
    public Map<String, Object> getRegAuditNoticeMap(String orderNo, Date workOrderUpdateTime, String workOrderStatus, String remark) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderNo", orderNo);
        map.put("workOrderUpdateTime", DateUtil.format(workOrderUpdateTime, "yyyy-MM-dd HH:mm"));
        map.put("workOrderStatus", workOrderStatus);
        map.put("workOrderRemark", remark);
        return map;
    }
}

