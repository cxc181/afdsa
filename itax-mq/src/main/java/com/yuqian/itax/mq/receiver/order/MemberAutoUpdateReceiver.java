package com.yuqian.itax.mq.receiver.order;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemConfigService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.vo.OpenOrderVO;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.MemberOrderRelaService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberLevelEntity;
import com.yuqian.itax.user.entity.vo.MemberLevelVO;
import com.yuqian.itax.user.enums.ExtendTypeEnum;
import com.yuqian.itax.user.enums.MemberTypeEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberLevelService;
import com.yuqian.itax.user.service.UserOrderStatisticsDayService;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 会员自动升级
 * @author：pengwei
 * @Date：2020/3/12 15:12
 * @version：1.0
 */
@Component
@Slf4j
public class MemberAutoUpdateReceiver {

    @Autowired
    UserOrderStatisticsDayService userOrderStatisticsDayService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    MemberOrderRelaService memberOrderRelaService;
    @Autowired
    MemberLevelService memberLevelService;
    @Autowired
    OrderService orderService;
    @Autowired
    InvoiceOrderService invoiceOrderService;
    @Autowired
    ProfitsDetailService profitsDetailService;
    @Autowired
    OemService oemService;
    @Autowired
    DictionaryService dictionaryService;
    @Autowired
    SmsService smsService;
    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private OemConfigService oemConfigService;

    @RabbitHandler
    @RabbitListener(queues = "memberAutoUpdate", priority="100")
    public void process(JSONObject json) {
        //怕订单数据事务还没提交先睡3S
        try {
            Thread.sleep(3000L);
        } catch (Exception e) {
        }
        log.info("==============================会员自动升级开始==========================================");
        Long userId=json.getLong("userId");
        if(userId==null){
            log.info("userId未知");
            log.info("==============================会员自动升级结束==========================================");

            return;
        }
        //MemberAccountEntity accountEntity=memberAccountService.findById(userId);
        MemberAccountEntity memberAccountEntity=memberAccountService.findById(userId);
        if(memberAccountEntity==null){
            log.info("==============================会员自动升级结束==========================================");
            return;
        }
        if(memberAccountEntity.getExtendType()!= null && memberAccountEntity.getExtendType().intValue() == ExtendTypeEnum.TOP_STRAIGHT_CUSTOMER.getValue()){
            log.info("==============================会员自动升级结束==========================================");
            return;
        }
        // 查询机构信息,接入渠道的机构关闭自动升级功能
        OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(memberAccountEntity.getOemCode(), "is_open_channel");
        if (null != oemConfigEntity && Objects.equals(oemConfigEntity.getParamsValue(), "1")) {
            log.info("==============================会员自动升级结束==========================================");
            return;
        }

        try{
            //判断机构的推广中心是否打开
            OemEntity oemEntity=oemService.getOem(memberAccountEntity.getOemCode());
            // 大客户版oem机构关闭自动升级功能
            if (null != oemEntity.getIsBigCustomer() && oemEntity.getIsBigCustomer().equals(1)) {
                log.info("==============================会员自动升级结束==========================================");
                return;
            }
            if(oemEntity.getIsOpenPromotion()==1){
                //更新会员达标个体数和达标开票企业
                if(memberAccountEntity.getMemberType().intValue()== MemberTypeEnum.EMPLOYEE.getValue()){
                    log.info("==============================会员自动升级结束==========================================");
                    return;
                }
                //从城市服务商往下遍历会员等级
                MemberLevelEntity entity=new MemberLevelEntity();
                entity.setOemCode(memberAccountEntity.getOemCode());
                List<MemberLevelVO> memberLevelEntities=null;
                try{
                    memberLevelEntities=memberLevelService.getLevelUpList(memberAccountEntity,"desc");
                }catch (BusinessException e){
                    log.info(e.getErrorMsg());
                    log.info("==============================会员自动升级结束==========================================");
                    return;
                }
                for ( MemberLevelVO memberLevelEntity:memberLevelEntities) {

                    int registCompanyNum=memberLevelEntity.getRegistCompanyNum();//个体注册数
                    Long invoiceMinMoney=memberLevelEntity.getInvoiceMinMoney();//开票最低金额
                    int completeInvoiceCompanyNum=memberLevelEntity.getCompleteInvoiceCompanyNum();//开票达标个体数

                    //统计用户达标个数和达标开票企业数
                    int completeRegistCompanyNum=0;
                    int completeInvoiceNum=0;
                    List<Long> a=invoiceOrderService.queryCompanyRegistReach(memberAccountEntity.getId(),memberAccountEntity.getOemCode());
                    if(CollectionUtil.isNotEmpty(a)){
                        completeRegistCompanyNum=invoiceOrderService.queryCompanyRegistReach(memberAccountEntity.getId(),memberAccountEntity.getOemCode()).size();//达标个体数
                    }
                    List<Long> b=invoiceOrderService.queryInvoiceReach(memberAccountEntity.getId(),memberAccountEntity.getOemCode(),invoiceMinMoney);
                    if(CollectionUtil.isNotEmpty(b)){
                        completeInvoiceNum=invoiceOrderService.queryInvoiceReach(memberAccountEntity.getId(),memberAccountEntity.getOemCode(),invoiceMinMoney).size();//达标开票数
                    }

               /* if(orderEntity.getOrderType()!=OrderTypeEnum.REGISTER.getValue()&&orderEntity.getOrderType()!=OrderTypeEnum.INVOICE.getValue()){
                }else{
                    throw  new BusinessException("订单类型不正确，不允许自动升级");
                }*/

                    //判断会员是否满足升级条件
                    if(completeRegistCompanyNum>=registCompanyNum &&completeInvoiceNum>=completeInvoiceCompanyNum){

                        //生成会员升级订单
                        OrderEntity memberLevelOrderEntity=orderService.memberUpgrade(memberAccountEntity,memberLevelService.queryMemberLevel(memberAccountEntity.getOemCode(),memberLevelEntity.getLevelNo()),null,"admin","自动升级");
                        //会员升级
                        memberLevelOrderEntity.setOrderStatus(3);
                        //  无完成时间 设置完成时间
                        if (memberLevelOrderEntity.getUpdateTime() == null){
                            memberLevelOrderEntity.setUpdateTime(new Date());
                        }
                        orderService.memberAudit(memberLevelOrderEntity,memberAccountEntity);
                        //V3.0 订单推送国金
                        if (StringUtil.isNotBlank(memberLevelOrderEntity.getOrderNo())){
                            try {
                                OrderEntity  OrderEntity = orderService.queryByOrderNo(memberLevelOrderEntity.getOrderNo());
                                List<OpenOrderVO> listToBePush = new ArrayList<OpenOrderVO>();
                                OpenOrderVO vo = new OpenOrderVO();
                                vo.setOrderNo(OrderEntity.getOrderNo());
                                vo.setId(OrderEntity.getUserId());
                                vo.setOemCode(OrderEntity.getOemCode());
                                vo.setOrderType(OrderEntity.getOrderType());
                                listToBePush.add(vo);
                                rabbitTemplate.convertAndSend("orderPush", listToBePush);
                            }catch (BusinessException e){
                                log.error("推送失败：{}",e.getMessage());
                            }
                        }
                        break;
                    }
                }
            }
        }catch (BusinessException e){
            log.info(e.getErrorMsg());
            DictionaryEntity dict = dictionaryService.getByCode("emergency_contact");
            if(null != dict){
                String dicValue = dict.getDictValue();
                String[] contacts = dicValue.split(",");
                for(String contact : contacts) {
                    Map<String,Object> map = new HashMap();
                    map.put("oemCode",memberAccountEntity.getOemCode());
                    map.put("account",memberAccountEntity.getMemberAccount());
                    smsService.sendTemplateSms(contact,memberAccountEntity.getOemCode(), VerifyCodeTypeEnum.MEMBER_AUTOUPDATE.getValue(), map,1);
                    log.info("会员自动升级失败发送通知给【" + contact + "】成功");
                }
            }
        }
        log.info("==============================会员自动升级结束==========================================");

    }
}
