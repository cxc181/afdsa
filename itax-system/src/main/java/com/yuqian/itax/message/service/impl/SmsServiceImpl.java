package com.yuqian.itax.message.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.message.dao.SmsMapper;
import com.yuqian.itax.message.dao.SmsTemplateMapper;
import com.yuqian.itax.message.entity.SmsEntity;
import com.yuqian.itax.message.entity.SmsTemplateEntity;
import com.yuqian.itax.message.entity.dto.SendResultDto;
import com.yuqian.itax.message.enums.GotoneResultEnum;
import com.yuqian.itax.message.enums.SendResultCodeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.message.service.SmsTemplateService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.ImContentCreator;
import com.yuqian.itax.util.util.channel.ChannelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("smsService")
public class SmsServiceImpl extends BaseServiceImpl<SmsEntity,SmsMapper> implements SmsService {
    @Resource
    private SmsTemplateMapper smsTemplateMapper;

    @Resource
    private SmsMapper smsMapper;

    @Autowired
    private OemService oemService;

    @Autowired
    private DictionaryService sysDictionaryService;

    @Autowired
    private OemParamsService oemParamsService;

    @Autowired
    private SmsTemplateService smsTemplateService;

    @Autowired
    protected RedisService redisService;

    @Value("${spring.profiles.active}")
    String profile;

    @Override
    public Map<String, Object> sendTemplateSms(String phone, String oemCode, String template_type, Map<String, Object> templateArgs,Integer userType) throws BusinessException {
        SmsEntity smsEntity = new SmsEntity();
        smsEntity.setUserType(userType);
        smsEntity.setUserPhone(phone);
        smsEntity.setOemCode(oemCode);
        //根据业务配置代码获取业务短信类型表信息
        SmsTemplateEntity smsTemplateEntity = smsTemplateMapper.getByTemplateType(template_type,oemCode);
        if(smsTemplateEntity == null){
            Map<String,Object> map =new HashMap<>();
            map.put("code","999");
            map.put("message","未配置相关短信模板！");
            return map;
        }
        smsEntity.setSmsTmplId(smsTemplateEntity.getId());

        log.info("开始处理模板短信发送请求[smsTemplateMessage={}]", smsEntity);
        return sendSms(smsEntity,templateArgs);
    }

    @Override
    public int insertSmsOut(SmsEntity smsEntity) {
        return smsMapper.insert(smsEntity);
    }

    @Override
    public void verifyCode(String key, String code) throws BusinessException {
//        String profile = SpringContextUtil.getActiveProfile();
//        System.out.println(profile);
        System.out.println(profile);
        //校验环境配置
        if (StringUtils.isBlank(profile) || !"dev".equalsIgnoreCase(profile)) {
            if (StringUtils.isBlank(code)) {
                throw new BusinessException("验证码不能为空");
            }
            if (!code.equals(redisService.get(key))) {
                throw new BusinessException("验证码不正确");
            }
        }
    }

    /**
     * @param smsEntity templateArgs
     * @return Map
     */
    private Map<String,Object> sendSms(SmsEntity smsEntity,Map<String, Object> templateArgs) throws BusinessException {
        Map<String,Object> map = new HashMap<>();

        try {
            smsEntity = loadSmsModel(smsEntity,templateArgs);
            // 发送短信
                SendResultDto sendResultDto = sendMessge(smsEntity);
            map.put("code",sendResultDto.getResultCode().getValue());
            map.put("message",sendResultDto.getResultCode().getMessage());
        } catch (Exception e) {
            log.error("发送短信时发生异常{}",e);
            map.put("code","error");
            map.put("message",e.getMessage());
        }
        return map;
    }

    /**
     * @Description 装载预发送短信对象
     * @Author  Kaven
     * @Date   2019/12/6 16:04
     * @Param  smsEntity templateArgs
     * @Return SmsEntity
     * @Exception  BusinessException
    */
    private SmsEntity loadSmsModel(SmsEntity smsEntity,Map<String, Object> templateArgs) throws BusinessException{
        /*
         * 若计划发送时间为空或者小于当前时间，则重设成当前时间
         */
        if (smsEntity.getReserveSendTime() == null || DateUtil.isBeforeNow(smsEntity.getReserveSendTime())) {
            smsEntity.setReserveSendTime(new Date());
        }else {
            //否则设置计划发送时间
            smsEntity.setReserveSendTime(smsEntity.getReserveSendTime());
        }

        OemEntity oemEntity = oemService.getOem(smsEntity.getOemCode());
        String sign = "【"+oemEntity.getOemName()+"】";
        //获取短信内容
        String content = getSmsContent(smsEntity.getSmsTmplId(),sign,templateArgs);

        smsEntity.setSmsContent(content);
        return smsEntity;
    }

    /**
     * @Description 转译短信内容
     * @Author  Kaven
     * @Date   2019/12/6 16:03
     * @Param  templateId sign templateArgs
     * @Return  String
     * @Exception BusinessException
    */
    private String getSmsContent(Long templateId, String sign, Map<String, Object> templateArgs) throws BusinessException {
        String mainContent = null;

        SmsTemplateEntity smsTemplateEntity = smsTemplateService.getMessageTemplateById(templateId);
        if (smsTemplateEntity == null) {
            throw new BusinessException(GotoneResultEnum.MESSAGE_TEMPLATE_NOT_EXIST.getMessage());
        } else {
            /**
             * 使用模板参数替换短信模板的变量，若模板参数为空则直接返回短信模板内容。
             */
            mainContent = ImContentCreator.customMerge(smsTemplateEntity.getTemplateContent(), templateArgs);
        }

        /**
         * 模板变量替换后再次进行内容非空校验，避免模板只有变量同时传递过来的变量map只有key没有value造成变量替换后内容为空的情况
         */
        if (StringUtils.isBlank(mainContent)) {
            throw new BusinessException(GotoneResultEnum.GOTONE_MSG_CONTENT_IS_NULL.getMessage());
        }

        if(StringUtils.isNotBlank(sign)){
            return sign + mainContent;
        }else{
            return mainContent;
        }
    }

    /**
     * @Description 发送短信，解析返回结果并记录
     * @Author  Kaven
     * @Date   2019/12/9 10:11
     * @Param  smsEntity
     * @Return SendResultDto
    */
    @Override
    public SendResultDto sendMessge(SmsEntity smsEntity) {
        SendResultDto sendResultDto = new SendResultDto();
        //oem机构 使用方式为纯api方式的请求 全部都不发送短信
        OemEntity entity = oemService.getOem(smsEntity.getOemCode());
        if(entity!=null && "4".equals(entity.getUseWay())){
            sendResultDto.setSuccess(true);
            sendResultDto.setResultCode(SendResultCodeEnum.SUCCESS);
            return sendResultDto;
        }
        try {
            String flag = sysDictionaryService.getByCode("sms_open").getDictValue();
            String result = null;
            if("0".equals(flag)){
                result = send(smsEntity);
            }
            log.info("向第三方发送短信,手机号：{}", smsEntity.getUserPhone());
            //插入一条实体记录
            smsEntity.setSendTime(new Date());
            smsEntity.setAddTime(new Date());

            // 解析返回结果
            if(StringUtils.isNotBlank(result)){
                JSONObject resultObj = JSONObject.parseObject(result);
                if("00".equals(resultObj.getString("code"))){
                    smsEntity.setSendStatus(1);
                    sendResultDto.setSuccess(true);
                    sendResultDto.setResultCode(SendResultCodeEnum.SUCCESS);
                }else{
                    smsEntity.setSendStatus(2);
                    sendResultDto.setSuccess(false);
                    sendResultDto.setResultCode(SendResultCodeEnum.ERROR);
                }
                smsEntity.setRetCode(resultObj.getString("code"));
                smsEntity.setRetMsg(resultObj.getString("msg"));
            }else{
                if("0".equals(flag)){
                    smsEntity.setSendStatus(2);
                    smsEntity.setRetCode("999");
                    smsEntity.setRetMsg("短信发送失败");
                    sendResultDto.setSuccess(false);
                    sendResultDto.setResultCode(SendResultCodeEnum.ERROR);
                }else{
                    smsEntity.setSendStatus(1);
                    smsEntity.setRetCode("00");
                    smsEntity.setRetMsg("短信挡板处理");
                    sendResultDto.setSuccess(true);
                    sendResultDto.setResultCode(SendResultCodeEnum.SUCCESS);
                }
            }
            //插入短信发信流水
            this.insertSmsOut(smsEntity);

        } catch (Exception e) {
            log.error("发送短信验证码发生异常{}",e.getMessage());
            sendResultDto.setResultCode(SendResultCodeEnum.ERROR);
            sendResultDto.setSuccess(false);
        }
        return sendResultDto;
    }

    /**
     * @Description 发送短信
     * @Author  Kaven
     * @Date   2019/12/6 15:34
     * @Param  smsEntity
     * @Return String
     * @Exception Exception
    */
    private String send(SmsEntity smsEntity) throws Exception {
        String result = null;
        try {
            //读取短信相关配置
            OemParamsEntity paramsEntity = this.oemParamsService.getParams(smsEntity.getOemCode(),1);
            if(null == paramsEntity){
                throw new BusinessException("未配置短信服务器相关信息！");
            }
            // agentNo
            String agentNo = paramsEntity.getAccount();
            // signKey
            String signKey = paramsEntity.getSecKey();
            // sendMsgUrl
            String sendMsgUrl = paramsEntity.getUrl();
            // 手机号
            String mobile = smsEntity.getUserPhone();
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("mobile", mobile);
            orderMap.put("content", smsEntity.getSmsContent());
            orderMap.put("requestId", System.currentTimeMillis() + "");

            log.info("data数据：" + JSONUtil.toJsonPrettyStr(orderMap));

            // 请求渠道接口
            result = ChannelUtils.callApi(paramsEntity.getParamsValues(),orderMap,agentNo,signKey,sendMsgUrl,"SMS");

        } catch (Exception e) {
            log.error("发送短信发生异常，异常信息{}" , e.getMessage());
        }
        return result;
    }
}

