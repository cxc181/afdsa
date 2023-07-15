package com.yuqian.itax.api.controller.order;

import cn.hutool.core.collection.CollectionUtil;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.order.entity.WithdrawOrderYishuiNotifyEntity;
import com.yuqian.itax.order.service.WithdrawOrderYishuiNotifyService;
import com.yuqian.itax.util.util.yishui.AESUtils;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import com.yuqian.itax.yishui.service.YiShuiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 易税订单统一回调处理地址
 * @author：lmh
 * @Date：2023/02/22
 * @version：1.0
 */
@Slf4j
@RestController
public class YiShuiOrderController extends BaseController {

    @Autowired
    private YiShuiService yiShuiService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WithdrawOrderYishuiNotifyService withdrawOrderYishuiNotifyService;

    @Value("${spring.profiles.active}")
    private String profilesActive;

    public static final String CREATE_BATCH_ORDER = "create_batch_order";

    @PostMapping("{merchantCode}/yishui/notify")
    public String notify(@PathVariable("merchantCode") String merchantCode, @RequestParam Map<String, String> reqData) {
       try {
           if (CollectionUtil.isEmpty(reqData)) {
               throw new BusinessException("请求参数为空");
           }
           String result = reqData.get("resoult");
           if (StringUtils.isBlank(result)) {
               throw new BusinessException("请求参数[业务请求结果]为空，不处理该订单");
           }
           String type = reqData.get("type");
           if (StringUtils.isBlank(type)) {
               throw new BusinessException("请求参数[回调类型]为空，不处理该订单");
           }
           log.info("收到易税订单回调通知，请求参数：{}，商户号：{}", reqData, merchantCode);

           if (!CREATE_BATCH_ORDER.equals(type)) {
               throw new BusinessException("不支持的回调类型");
           }
           //保存易税回调订单
           WithdrawOrderYishuiNotifyEntity entity = withdrawOrderYishuiNotifyService.save(merchantCode, reqData);

           if (StringUtils.isBlank(merchantCode)) {
               throw new BusinessException("商户号，不处理该订单");
           }
           YsMerConfig config = yiShuiService.getYsParamConfig(null, merchantCode);
           if (config == null || StringUtils.isBlank(config.getAseKey())) {
               throw new BusinessException("易税参数配置有误，请核查请求参数，商户号：" + merchantCode);
           }
           String data = AESUtils.decrypt(result, config.getAseKey());
           if (StringUtils.isBlank(data)) {
               throw new BusinessException("解密易税请求参数结果为空，不处理该订单");
           }
           log.info("解密后的易税参数：{}", data);
           //更新易税回调订单
           entity.setParamResultPlaintext(data);
           withdrawOrderYishuiNotifyService.editByIdSelective(entity);
           switch(type) {
               case CREATE_BATCH_ORDER:
                   //发送MQ处理提现订单审核
                   rabbitTemplate.convertAndSend("withdrawOrderYiShuiAuditQueue", entity.getId());
                   break;
               default:
                   log.info("当前回调类型不处理，type：{}", type);
                   break;
           }
           return "SUCCESS";
       } catch (BusinessException e) {
           log.error(e.getMessage());
           return "SUCCESS";
       } catch (Exception e) {
           log.error(e.getMessage(), e);
           return "FAIL";
       }
    }

    @PostMapping("helper-gateway/{merchantCode}/yishui/notify")
    public String notify2(@PathVariable("merchantCode") String merchantCode, @RequestParam Map<String, String> reqData) {
        String notify = notify(merchantCode, reqData);
        return notify;
    }
    /**
     * 易税回调挡板处理
     * @param merchantCode
     * @param reqData
     * @return
     */
    @PostMapping("{merchantCode}/yishui/notify/baffle")
    public String notifyBaffle(@PathVariable("merchantCode") String merchantCode, @RequestParam Map<String, String> reqData) {
       try {
           if ("prod".equals(profilesActive)) {
               return "FAIL";
           }
           if (CollectionUtil.isEmpty(reqData)) {
               throw new BusinessException("请求参数为空");
           }
           String result = reqData.get("resoult");
           if (StringUtils.isBlank(result)) {
               throw new BusinessException("请求参数[业务请求结果]为空，不处理该订单");
           }
           String type = reqData.get("type");
           if (StringUtils.isBlank(type)) {
               throw new BusinessException("请求参数[回调类型]为空，不处理该订单");
           }
           log.info("收到易税订单回调通知，请求参数：{}，商户号：{}", reqData, merchantCode);

           if (!CREATE_BATCH_ORDER.equals(type)) {
               throw new BusinessException("不支持的回调类型");
           }
           //保存易税回调订单
           WithdrawOrderYishuiNotifyEntity entity = withdrawOrderYishuiNotifyService.save(merchantCode, reqData);

           if (StringUtils.isBlank(merchantCode)) {
               throw new BusinessException("商户号为空，不处理该订单");
           }
           //更新易税回调订单
           entity.setParamResultPlaintext(result);
           withdrawOrderYishuiNotifyService.editByIdSelective(entity);
           switch(type) {
               case CREATE_BATCH_ORDER:
                   //发送MQ处理提现订单审核
                   rabbitTemplate.convertAndSend("withdrawOrderYiShuiAuditQueue", entity.getId());
                   break;
               default:
                   log.info("当前回调类型不处理，type：{}", type);
                   break;
           }
           return "SUCCESS";
       } catch (BusinessException e) {
           log.error("回调通知处理异常：" + e.getMessage());
           return "SUCCESS";
       } catch (Exception e) {
           log.error("回调通知处理异常：" + e.getMessage());
           return "FAIL";
       }
    }

}
