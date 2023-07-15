package com.yuqian.itax.mq.receiver.order;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.profits.entity.ProfitsDetailEntity;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberLevelEntity;
import com.yuqian.itax.user.entity.UserOrderStatisticsDayEntity;
import com.yuqian.itax.user.enums.ExtendTypeEnum;
import com.yuqian.itax.user.enums.MemberLevelEnum;
import com.yuqian.itax.user.enums.MemberTypeEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberLevelService;
import com.yuqian.itax.user.service.UserOrderStatisticsDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日快照统计
 * @author：HZ
 * @Date：2020/3/12 15:12
 * @version：1.0
 */
@Component
@Slf4j
public class StatisticsMemberGeneralizeReceiver {

    @Autowired
    UserOrderStatisticsDayService userOrderStatisticsDayService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    MemberLevelService memberLevelService;
    @Autowired
    OrderService orderService;
    @Autowired
    InvoiceOrderService invoiceOrderService;
    @Autowired
    ProfitsDetailService profitsDetailService;
    @Autowired
    SmsService smsService;

    @Autowired
    DictionaryService dictionaryService;

    @RabbitHandler
    @RabbitListener(queues = "statisticsMemberGeneralize", priority="100")
    public void process(JSONObject json) {

        log.info("==============================日数据统计开始==========================================");
        String orderNo=json.getString("orderNo");
        int type=json.getIntValue("type");
        Long userId=json.getLong("userId");
        OrderEntity orderEntity=orderService.queryByOrderNo(orderNo);
        MemberAccountEntity memberAccountEntity = memberAccountService.findById(userId);
        if(memberAccountEntity == null){
            return ;
        }
        MemberLevelEntity memberLevelEntity=memberLevelService.findById(memberAccountEntity.getMemberLevel());
        try{
            Thread.sleep(2000L);
            if(memberAccountEntity.getMemberType()!= null && memberAccountEntity.getMemberType().intValue()==ExtendTypeEnum.STRAIGHT_CUSTOMER.getValue()){
                return ;//员工的话不做任何处理
            }

            if (type == 0) {//用户注册分支
                if(memberLevelEntity.getLevelNo().equals(MemberLevelEnum.DIAMOND.getValue())||memberLevelEntity.getLevelNo().equals(MemberLevelEnum.GOLD.getValue())){
                    //给自己算一遍业绩
                    registMemberStatistics(memberAccountEntity, "admin", 0);
                }
                MemberAccountEntity parentEntity = memberAccountService.findById(memberAccountEntity.getParentMemberId());
                if(parentEntity==null){
                    return ;
                }
                //直推统计 直推用户
                registMemberStatistics(parentEntity, "admin", 0);
                //裂变
                //无限级算法
                //增加所上级裂变数
                String[] memberTree=parentEntity.getMemberTree().split("/");
                for (int i=memberTree.length-2;i>-1;i--){
                    MemberAccountEntity accountEntity= memberAccountService.findById(Long.parseLong(memberTree[i]));
                    if(accountEntity.getMemberType().intValue()==2){
                        i--;//跳过上级城市服务商
                    }
                    registMemberStatistics(accountEntity, "admin", 1);
                }

            } else { //订单统计分支

                //直推统计
                if(memberLevelEntity.getLevelNo().equals(MemberLevelEnum.DIAMOND.getValue())||memberLevelEntity.getLevelNo().equals(MemberLevelEnum.GOLD.getValue())){
                    //给自己算一遍业绩
                    directStatistics(orderEntity, memberAccountEntity, "admin");
                }
                MemberAccountEntity parentEntity = memberAccountService.findById(memberAccountEntity.getParentMemberId());
                if(parentEntity==null){
                    log.info("==============================日数据统计结束==========================================");
                    return ;
                }
                MemberLevelEntity parentMemberLevel=memberLevelService.findById(parentEntity.getMemberLevel());
                directStatistics(orderEntity, parentEntity, "admin");

                //裂变
                MemberAccountEntity upDiamondEntity = null;
                //上级是城市服务商 再找1级
                if (parentMemberLevel!=null && parentMemberLevel.getLevelNo().intValue()==MemberLevelEnum.DIAMOND.getValue() ) {
                    if(memberAccountEntity.getExtendType().intValue() != ExtendTypeEnum.TOP_STRAIGHT_CUSTOMER.getValue()){
                        if (parentEntity.getAttributionEmployeesId() != null) {
                            //如果有员工算员工业绩上级城市服务商不算业绩
                            MemberAccountEntity attributionEmployeesEntity = memberAccountService.findById(parentEntity.getAttributionEmployeesId());
                            //统计裂变数据
                            fissionStatistics(orderEntity, attributionEmployeesEntity, "admin");//二级
                        } else {
                            //查询出用户的上级城市服务商
                            upDiamondEntity = memberAccountService.findById(parentEntity.getUpDiamondId());
                            //统计裂变数据
                            fissionStatistics(orderEntity, upDiamondEntity, "admin");//二级
                        }
                    }
                }else if(parentEntity.getMemberType().intValue() == MemberTypeEnum.EMPLOYEE.getValue()){//员工
                    if(memberAccountEntity.getExtendType().intValue() != ExtendTypeEnum.TOP_STRAIGHT_CUSTOMER.getValue().intValue()) {
                        //查询上级城市服务商是否有上级城市服务商
                        if (parentEntity.getSuperDiamondId() != null) {
                            if (parentEntity.getSuperEmployeesId() != null) {
                                //如果上级城市服务商有归属员工算员工业绩上上上级城市服务商不算业绩
                                MemberAccountEntity superEmployeesEntity = memberAccountService.findById(parentEntity.getSuperEmployeesId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, superEmployeesEntity, "admin");//二级
                            } else {
                                //查询出用户的上上级城市服务商
                                MemberAccountEntity superDiamondEntity = memberAccountService.findById(parentEntity.getSuperDiamondId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, superDiamondEntity, "admin");//三级
                            }

                        }
                    }

                }else if(parentMemberLevel!=null && parentMemberLevel.getLevelNo().intValue()== MemberLevelEnum.NORMAL.getValue()){
                    if(memberAccountEntity.getExtendType().intValue() != ExtendTypeEnum.TOP_STRAIGHT_CUSTOMER.getValue()) {
                        //上级是非城市服务商 再找2级
                        if (parentEntity.getUpDiamondId() != null) {
                            if (parentEntity.getAttributionEmployeesId() != null) {
                                //如果有员工算员工业绩上级城市服务商不算业绩
                                MemberAccountEntity attributionEmployeesEntity = memberAccountService.findById(parentEntity.getAttributionEmployeesId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, attributionEmployeesEntity, "admin");//二级
                            } else {
                                //查询出用户的上级城市服务商
                                upDiamondEntity = memberAccountService.findById(parentEntity.getUpDiamondId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, upDiamondEntity, "admin");//二级
                            }

                        }
                        //查询上级城市服务商是否有上级城市服务商
                        if (parentEntity.getSuperDiamondId() != null) {
                            if (parentEntity.getSuperEmployeesId() != null) {
                                //如果上级城市服务商有归属员工算员工业绩上上上级城市服务商不算业绩
                                MemberAccountEntity superEmployeesEntity = memberAccountService.findById(parentEntity.getSuperEmployeesId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, superEmployeesEntity, "admin");//二级
                            } else {
                                //查询出用户的上上级城市服务商
                                MemberAccountEntity superDiamondEntity = memberAccountService.findById(parentEntity.getSuperDiamondId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, superDiamondEntity, "admin");//三级
                            }

                        }
                    }else{
                        //上级是非城市服务商 再找1级
                        if (parentEntity.getUpDiamondId() != null) {
                            if (parentEntity.getAttributionEmployeesId() != null) {
                                //如果有员工算员工业绩上级城市服务商不算业绩
                                MemberAccountEntity attributionEmployeesEntity = memberAccountService.findById(parentEntity.getAttributionEmployeesId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, attributionEmployeesEntity, "admin");//
                            } else {
                                //查询出用户的上级城市服务商
                                upDiamondEntity = memberAccountService.findById(parentEntity.getUpDiamondId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, upDiamondEntity, "admin");//
                            }

                        }
                    }

                }else{
                    if(memberAccountEntity.getExtendType().intValue() != ExtendTypeEnum.TOP_STRAIGHT_CUSTOMER.getValue()) {
                        //上级是非城市服务商 再找2级
                        if (parentEntity.getUpDiamondId() != null) {
                            if (parentEntity.getAttributionEmployeesId() != null) {
                                //如果有员工算员工业绩上级城市服务商不算业绩
                                MemberAccountEntity attributionEmployeesEntity = memberAccountService.findById(parentEntity.getAttributionEmployeesId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, attributionEmployeesEntity, "admin");//二级
                            } else {
                                //查询出用户的上级城市服务商
                                upDiamondEntity = memberAccountService.findById(parentEntity.getUpDiamondId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, upDiamondEntity, "admin");//二级
                            }

                        }
                        //查询上级城市服务商是否有上级城市服务商
                        if (parentEntity.getSuperDiamondId() != null) {
                            if (parentEntity.getSuperEmployeesId() != null) {
                                //如果上级城市服务商有归属员工算员工业绩上上上级城市服务商不算业绩
                                MemberAccountEntity superEmployeesEntity = memberAccountService.findById(parentEntity.getSuperEmployeesId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, superEmployeesEntity, "admin");//二级
                            } else {
                                //查询出用户的上上级城市服务商
                                MemberAccountEntity superDiamondEntity = memberAccountService.findById(parentEntity.getSuperDiamondId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, superDiamondEntity, "admin");//三级
                            }

                        }
                    }else{
                        //上级是非城市服务商 再找2级
                        if (parentEntity.getUpDiamondId() != null) {
                            if (parentEntity.getAttributionEmployeesId() != null) {
                                //如果有员工算员工业绩上级城市服务商不算业绩
                                MemberAccountEntity attributionEmployeesEntity = memberAccountService.findById(parentEntity.getAttributionEmployeesId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, attributionEmployeesEntity, "admin");//二级
                            } else {
                                //查询出用户的上级城市服务商
                                upDiamondEntity = memberAccountService.findById(parentEntity.getUpDiamondId());
                                //统计裂变数据
                                fissionStatistics(orderEntity, upDiamondEntity, "admin");//二级
                            }

                        }
                    }

                }

                /*//增加所上级企业注册裂变数
                String[] memberTree=parentEntity.getMemberTree().split("/");
                if(parentEntity.getMemberType().intValue()==MemberTypeEnum.EMPLOYEE.getValue()){
                    //不计算上级企业[575/576]
                    for (int i=memberTree.length-3;i>-1;i--){
                        MemberAccountEntity accountEntity= memberAccountService.findById(Long.parseLong(memberTree[i]));
                        if(accountEntity.getMemberType()==2){
                            i--;//跳过上级城市服务商
                        }
                        companyNumStatistics(orderEntity, accountEntity, "admin");
                    }
                }else{
                    for (int i=memberTree.length-2;i>-1;i--){
                        MemberAccountEntity accountEntity= memberAccountService.findById(Long.parseLong(memberTree[i]));
                        if(accountEntity.getMemberType()==2){
                            i--;//跳过上级城市服务商
                        }
                        companyNumStatistics(orderEntity, accountEntity, "admin");
                    }
                }*/

            }
        }catch (Exception e){
            log.info(e.getMessage());
            DictionaryEntity dict = dictionaryService.getByCode("emergency_contact");
            if(null != dict){
                String dicValue = dict.getDictValue();
                String[] contacts = dicValue.split(",");
                for(String contact : contacts) {
                    Map<String,Object> map = new HashMap();
                    try {
                        map.put("oemCode",memberAccountEntity.getOemCode()==null?"":memberAccountEntity.getOemCode());
                        map.put("orderNo",orderNo==null?"":orderNo);
                        map.put("account",memberAccountEntity.getMemberAccount()==null?"":memberAccountEntity.getMemberAccount());
                        smsService.sendTemplateSms(contact,orderEntity.getOemCode(), VerifyCodeTypeEnum.STATISTICS_MEMBERGENERALIZE.getValue(), map,1);
                        log.info("日统计报表败发送通知给【" + contact + "】成功");
                    } catch (Exception e1) {
                        log.error("发送短信通知紧急通知人失败, 请求参数:{}", JSONObject.toJSONString(map));
                        log.error(e.getMessage(), e1);
                    }
                }
            }

        }
        log.info("==============================日数据统计结束==========================================");
    }

    /**
     * 统计用户数量
     */
    /**
     * @param parentEntity
     * @param account
     * @param type         0-直推 1是裂变
     * @return
     */
    private void registMemberStatistics(MemberAccountEntity parentEntity, String account, int type) {
        if(parentEntity == null){
            return ;
        }
        MemberLevelEntity memberLevelEntity=memberLevelService.findById(parentEntity.getMemberLevel());
       /* if (memberLevelEntity.getLevelNo() != MemberLevelEnum.DIAMOND.getValue()) {
            return null;
        }*/
        //查询当天是否有统计数据
        UserOrderStatisticsDayEntity parentOrderStatisticsDayEntity = userOrderStatisticsDayService.queryrOrderStatisticsDayByToday(parentEntity.getOemCode(), parentEntity.getId());
        if (parentOrderStatisticsDayEntity != null) {//更新
            if (type == 0) {
                parentOrderStatisticsDayEntity.setPromoteUserDirect(parentOrderStatisticsDayEntity.getPromoteUserDirect() + 1);//直推用户
            } else if (type == 1) {
                parentOrderStatisticsDayEntity.setPromoteUserFission(parentOrderStatisticsDayEntity.getPromoteUserFission() + 1);//裂变用户数
            }
            parentOrderStatisticsDayEntity.setStatisticsDay(new Date());
            parentOrderStatisticsDayEntity.setUpdateTime(new Date());
            parentOrderStatisticsDayEntity.setUpdateUser(account);
            userOrderStatisticsDayService.editByIdSelective(parentOrderStatisticsDayEntity);
        } else {//新增
            UserOrderStatisticsDayEntity entity = new UserOrderStatisticsDayEntity();
            entity.setOemCode(parentEntity.getOemCode());//机构码
            entity.setUserId(parentEntity.getId());//会员id
            entity.setUserLevelNo(memberLevelEntity.getLevelNo());//会员等级
            entity.setParentUserId(parentEntity.getParentMemberId());//上级用户id

            //直推个体数
            entity.setIndividualDirect(0);
            ///直推企业注册费
            entity.setCompanyRegistFeeDirect(0L);
            if (type == 0) {
                entity.setPromoteUserDirect(1);//直推用户
                entity.setPromoteUserFission(0);//裂变用户数
            } else if (type == 1) {
                entity.setPromoteUserDirect(0);//直推用户
                entity.setPromoteUserFission(1);//裂变用户数
            }
            entity.setInvoiceFeeDirect(0L);//直推开票服务费
            entity.setCompanyCancelFeeDirect(0L);//直推注销服务费
            entity.setMemberUpgradeFeeDirect(0L);//直推会员升级费
            entity.setInvoiceAmountDirect(0L);//直推开票金额
            //直推分润费
            entity.setProfitsFeeDirect(0L);
            entity.setIndividualFission(0);//裂变个体数
            entity.setCompanyRegistFeeFission(0L);//裂变企业注册费
            entity.setInvoiceFeeFission(0L);//裂变开票服务费
            entity.setCompanyCancelFeeFission(0L);//裂变注销服务费
            entity.setProfitsFeeFission(0L);//裂变分润费
            entity.setInvoiceAmountFission(0L);//裂变开票金额
            entity.setStatisticsDay(new Date());//统计日期
            entity.setAddTime(new Date());
            entity.setAddUser(account);
            userOrderStatisticsDayService.insertSelective(entity);
        }
    }

    /**
     * 直推
     */
    private void directStatistics(OrderEntity orderEntity, MemberAccountEntity parentEntity, String account) {
        if(orderEntity== null || parentEntity == null){
            return ;
        }

        //查询当天是否有统计数据
        UserOrderStatisticsDayEntity userOrderStatisticsDayEntity = userOrderStatisticsDayService.queryrOrderStatisticsDayByToday(orderEntity.getOemCode(), parentEntity.getId());
        ProfitsDetailEntity profitsDetailEntity=new ProfitsDetailEntity();
        if(parentEntity.getMemberType().intValue()==MemberTypeEnum.EMPLOYEE.getValue()){
            profitsDetailEntity.setUserId(parentEntity.getParentMemberId());
        }else{
            profitsDetailEntity.setUserId(parentEntity.getId());
        }
        profitsDetailEntity.setUserType(1);
        profitsDetailEntity.setOrderNo(orderEntity.getOrderNo());
        profitsDetailEntity.setOemCode(parentEntity.getOemCode());
        List<ProfitsDetailEntity> profitsDetailEntityList=profitsDetailService.select(profitsDetailEntity);
        ProfitsDetailEntity profitsDetail=null;
        if(CollectionUtil.isNotEmpty(profitsDetailEntityList)){
            profitsDetail =profitsDetailEntityList.get(0);
        }
        InvoiceOrderEntity invoiceOrderEntity=invoiceOrderService.queryByOrderNo(orderEntity.getOrderNo());

        if (userOrderStatisticsDayEntity != null) {//更新
            if (orderEntity.getOrderType().intValue() == OrderTypeEnum.REGISTER.getValue()) {
                userOrderStatisticsDayEntity.setIndividualDirect(userOrderStatisticsDayEntity.getIndividualDirect() + 1);
                userOrderStatisticsDayEntity.setCompanyRegistFeeDirect(userOrderStatisticsDayEntity.getCompanyRegistFeeDirect()+orderEntity.getProfitAmount());
                // 修改默认为null的问题
                if(null == userOrderStatisticsDayEntity.getCompanyRegistFeeDirect()){
                    userOrderStatisticsDayEntity.setCompanyRegistFeeDirect(0L);
                }
            } else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()) {
                userOrderStatisticsDayEntity.setInvoiceFeeDirect(userOrderStatisticsDayEntity.getInvoiceFeeDirect()+orderEntity.getProfitAmount());
                userOrderStatisticsDayEntity.setInvoiceAmountDirect(userOrderStatisticsDayEntity.getInvoiceAmountDirect() + invoiceOrderEntity.getInvoiceAmount());//直推开票金额
            } else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.UPGRADE.getValue()) {
                userOrderStatisticsDayEntity.setMemberUpgradeFeeDirect(userOrderStatisticsDayEntity.getMemberUpgradeFeeDirect()+orderEntity.getProfitAmount());
            } else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.CANCELLATION.getValue()) {
                userOrderStatisticsDayEntity.setCompanyCancelFeeDirect(userOrderStatisticsDayEntity.getCompanyCancelFeeDirect()+orderEntity.getProfitAmount());
            }else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.CUSTODY_FEE_RENEWAL.getValue()) {
                userOrderStatisticsDayEntity.setCustodyFeeRenewalDirect(userOrderStatisticsDayEntity.getCustodyFeeRenewalDirect()+orderEntity.getProfitAmount());
            }

            //直推分润费
            userOrderStatisticsDayEntity.setProfitsFeeDirect(userOrderStatisticsDayEntity.getProfitsFeeDirect() + (profitsDetail==null?0:profitsDetail.getProfitsAmount()));

            userOrderStatisticsDayEntity.setStatisticsDay(new Date());
            userOrderStatisticsDayEntity.setUpdateTime(new Date());
            userOrderStatisticsDayEntity.setUpdateUser(account);
            userOrderStatisticsDayService.editByIdSelective(userOrderStatisticsDayEntity);
        } else {//新增
            UserOrderStatisticsDayEntity entity = new UserOrderStatisticsDayEntity();
            entity.setOemCode(parentEntity.getOemCode());//机构码
            entity.setUserId(parentEntity.getId());//会员id
            MemberLevelEntity memberLevelEntity = memberLevelService.findById(parentEntity.getMemberLevel());
            entity.setUserLevelNo(memberLevelEntity.getLevelNo());//会员等级
            entity.setParentUserId(parentEntity.getParentMemberId());//上级用户id

            if (orderEntity.getOrderType().intValue() == OrderTypeEnum.REGISTER.getValue()) {
                //直推个体数
                entity.setIndividualDirect(1);
                ///直推企业注册费(修改默认为null的问题)
                if(null != orderEntity.getProfitAmount()){
                    entity.setCompanyRegistFeeDirect(orderEntity.getProfitAmount());
                }else{
                    entity.setCompanyRegistFeeDirect(0L);
                }
                entity.setPromoteUserDirect(0);//直推用户
                entity.setInvoiceFeeDirect(0L);//直推开票服务费
                entity.setCompanyCancelFeeDirect(0L);//直推注销服务费
                entity.setMemberUpgradeFeeDirect(0L);//直推会员升级费
                entity.setInvoiceAmountDirect(0L);//直推开票金额
            } else if (orderEntity.getOrderType().intValue()== OrderTypeEnum.INVOICE.getValue()) {
                entity.setIndividualDirect(0);//直推个体数
                //直推开票服务费
                entity.setInvoiceFeeDirect(orderEntity.getProfitAmount());
                entity.setPromoteUserDirect(0);//直推用户
                entity.setCompanyRegistFeeDirect(0L);//直推企业注册费
                entity.setCompanyCancelFeeDirect(0L);//直推注销服务费
                entity.setMemberUpgradeFeeDirect(0L);//直推会员升级费
                entity.setInvoiceAmountDirect(invoiceOrderEntity.getInvoiceAmount());//直推开票金额

            } else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.UPGRADE.getValue()) {
                entity.setIndividualDirect(0);//直推个体数
                entity.setCompanyRegistFeeDirect(0L);
                entity.setPromoteUserDirect(0);//直推用户
                entity.setInvoiceFeeDirect(0L);//直推开票服务费
                entity.setCompanyCancelFeeDirect(0L);//直推注销服务费
                entity.setMemberUpgradeFeeDirect(orderEntity.getProfitAmount());//直推会员升级费
                entity.setInvoiceAmountDirect(0L);//直推开票金额
            } else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.CANCELLATION.getValue()) {
                entity.setIndividualDirect(0);//直推个体数
                ////直推企业注销费
                entity.setCompanyCancelFeeDirect(orderEntity.getProfitAmount());
                entity.setPromoteUserDirect(0);//直推用户
                entity.setInvoiceFeeDirect(0L);//直推开票服务费
                entity.setCompanyCancelFeeDirect(0L);//直推注销服务费
                entity.setMemberUpgradeFeeDirect(0L);//直推会员升级费
                entity.setInvoiceAmountDirect(0L);//直推开票金额
            }else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.CUSTODY_FEE_RENEWAL.getValue()) {
                entity.setIndividualDirect(0);//直推个体数
                ////直推企业注销费
                entity.setCompanyCancelFeeDirect(0L);
                entity.setPromoteUserDirect(0);//直推用户
                entity.setInvoiceFeeDirect(0L);//直推开票服务费
                entity.setCompanyCancelFeeDirect(0L);//直推注销服务费
                entity.setMemberUpgradeFeeDirect(0L);//直推会员升级费
                entity.setInvoiceAmountDirect(0L);//直推开票金额
                entity.setCustodyFeeRenewalDirect(orderEntity.getProfitAmount());
            }

            //直推分润费
            entity.setProfitsFeeDirect((profitsDetail==null?0:profitsDetail.getProfitsAmount()));

            entity.setPromoteUserFission(0);//裂变用户数
            entity.setIndividualFission(0);//裂变个体数
            entity.setCompanyRegistFeeFission(0L);//裂变企业注册费
            entity.setInvoiceFeeFission(0L);//裂变开票服务费
            entity.setMemberUpgradeFeeFission(0L);
            entity.setCompanyCancelFeeFission(0L);//裂变注销服务费
            entity.setProfitsFeeFission(0L);//裂变分润费
            entity.setInvoiceAmountFission(0L);//裂变开票金额
            entity.setStatisticsDay(new Date());//统计日期
            entity.setAddTime(new Date());
            entity.setAddUser(account);
            userOrderStatisticsDayService.insertSelective(entity);
        }
    }

    /**
     * 裂变数据统计
     */
    private void fissionStatistics(OrderEntity orderEntity, MemberAccountEntity upDiamondEntity, String account) {
        if(orderEntity== null || upDiamondEntity == null){
            return ;
        }

        //查询当天是否有统计数据
        UserOrderStatisticsDayEntity upDiamondOrderStatisticsDayEntity = userOrderStatisticsDayService.queryrOrderStatisticsDayByToday(upDiamondEntity.getOemCode(), upDiamondEntity.getId());
        ProfitsDetailEntity profitsDetailEntity=new ProfitsDetailEntity();
        if(upDiamondEntity.getMemberType().intValue()==MemberTypeEnum.EMPLOYEE.getValue()){
            profitsDetailEntity.setUserId(upDiamondEntity.getParentMemberId());
        }else{
            profitsDetailEntity.setUserId(upDiamondEntity.getId());
        }
        profitsDetailEntity.setUserType(1);
        profitsDetailEntity.setOrderNo(orderEntity.getOrderNo());
        profitsDetailEntity.setOemCode(upDiamondEntity.getOemCode());
        List<ProfitsDetailEntity> profitsDetailEntityList=profitsDetailService.select(profitsDetailEntity);
        ProfitsDetailEntity profitsDetail=null;
        if(CollectionUtil.isNotEmpty(profitsDetailEntityList)){
            profitsDetail =profitsDetailEntityList.get(0);
        }
        InvoiceOrderEntity invoiceOrderEntity=invoiceOrderService.queryByOrderNo(orderEntity.getOrderNo());

        if (upDiamondOrderStatisticsDayEntity != null) {//更新
            if (orderEntity.getOrderType().intValue() == OrderTypeEnum.REGISTER.getValue()) {
                upDiamondOrderStatisticsDayEntity.setIndividualFission(upDiamondOrderStatisticsDayEntity.getIndividualFission() + 1);
                upDiamondOrderStatisticsDayEntity.setCompanyRegistFeeFission(upDiamondOrderStatisticsDayEntity.getCompanyRegistFeeFission()+orderEntity.getProfitAmount());
            } else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()) {
                upDiamondOrderStatisticsDayEntity.setInvoiceFeeFission(upDiamondOrderStatisticsDayEntity.getInvoiceFeeFission()+orderEntity.getProfitAmount());
                upDiamondOrderStatisticsDayEntity.setInvoiceAmountFission(upDiamondOrderStatisticsDayEntity.getInvoiceAmountFission() + invoiceOrderEntity.getInvoiceAmount());//直推开票金额
            } else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.UPGRADE.getValue()) {
                upDiamondOrderStatisticsDayEntity.setMemberUpgradeFeeFission(upDiamondOrderStatisticsDayEntity.getMemberUpgradeFeeFission()+orderEntity.getProfitAmount());
            } else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.CANCELLATION.getValue()) {
                upDiamondOrderStatisticsDayEntity.setCompanyCancelFeeFission(upDiamondOrderStatisticsDayEntity.getCompanyCancelFeeFission()+orderEntity.getProfitAmount());
            }else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.CUSTODY_FEE_RENEWAL.getValue()) {
                upDiamondOrderStatisticsDayEntity.setCustodyFeeRenewalFission(upDiamondOrderStatisticsDayEntity.getCustodyFeeRenewalFission()+orderEntity.getProfitAmount());
            }
            //裂变分润费
            upDiamondOrderStatisticsDayEntity.setProfitsFeeFission(upDiamondOrderStatisticsDayEntity.getProfitsFeeFission() + (profitsDetail==null?0:profitsDetail.getProfitsAmount()));

            upDiamondOrderStatisticsDayEntity.setStatisticsDay(new Date());
            upDiamondOrderStatisticsDayEntity.setUpdateTime(new Date());
            upDiamondOrderStatisticsDayEntity.setUpdateUser(account);
            userOrderStatisticsDayService.editByIdSelective(upDiamondOrderStatisticsDayEntity);
        } else {//新增
            UserOrderStatisticsDayEntity entity = new UserOrderStatisticsDayEntity();
            entity.setOemCode(upDiamondEntity.getOemCode());//机构码
            entity.setUserId(upDiamondEntity.getId());//会员id
            MemberLevelEntity memberLevelEntity = memberLevelService.findById(upDiamondEntity.getMemberLevel());
            entity.setUserLevelNo(memberLevelEntity.getLevelNo());//会员等级
            entity.setParentUserId(upDiamondEntity.getParentMemberId());//上级用户id

            if (orderEntity.getOrderType().intValue() == OrderTypeEnum.REGISTER.getValue()) {
                //裂变个体数
                entity.setIndividualFission(1);
                ///直推企业注册费
                entity.setCompanyRegistFeeFission(orderEntity.getProfitAmount());
                entity.setPromoteUserFission(0);//裂变用户数
                entity.setInvoiceFeeFission(0L);//裂变开票服务费
                entity.setCompanyCancelFeeFission(0L);//裂变注销服务费
                entity.setInvoiceAmountFission(0L);//裂变开票金额
                entity.setMemberUpgradeFeeFission(0L);

            } else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()) {
                entity.setIndividualDirect(0);//裂变个体数
                //裂变开票服务费
                entity.setInvoiceFeeFission(orderEntity.getProfitAmount());
                entity.setPromoteUserFission(0);//裂变用户
                entity.setCompanyRegistFeeFission(0L);//裂变企业注册费
                entity.setCompanyCancelFeeFission(0L);//裂变注销服务费
                entity.setInvoiceAmountFission(invoiceOrderEntity.getInvoiceAmount());//裂变开票金额
                entity.setMemberUpgradeFeeFission(0L);

            } else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.UPGRADE.getValue()) {
                entity.setIndividualFission(0);//裂变个体数
                //裂变企业注册费
                entity.setCompanyRegistFeeFission(0L);
                entity.setPromoteUserFission(0);//裂变用户
                entity.setInvoiceFeeFission(0L);//裂变开票服务费
                entity.setMemberUpgradeFeeFission(orderEntity.getProfitAmount());
                entity.setCompanyCancelFeeFission(0L);//裂变注销服务费
                entity.setInvoiceAmountFission(0L);//裂变开票金额
            } else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.CANCELLATION.getValue()) {
                entity.setIndividualFission(0);//裂变个体数
                ////裂变企业注册费
                entity.setCompanyCancelFeeFission(orderEntity.getProfitAmount());
                entity.setPromoteUserFission(0);//裂变用户
                entity.setInvoiceFeeFission(0L);//裂变开票服务费
                entity.setCompanyCancelFeeFission(0L);//裂变注销服务费
                entity.setInvoiceAmountFission(0L);//裂变开票金额
                entity.setMemberUpgradeFeeFission(0L);
            }else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.CUSTODY_FEE_RENEWAL.getValue()) {
                entity.setIndividualFission(0);//裂变个体数
                ////裂变企业注册费
                entity.setCompanyCancelFeeFission(0L);
                entity.setPromoteUserFission(0);//裂变用户
                entity.setInvoiceFeeFission(0L);//裂变开票服务费
                entity.setCompanyCancelFeeFission(0L);//裂变注销服务费
                entity.setInvoiceAmountFission(0L);//裂变开票金额
                entity.setMemberUpgradeFeeFission(0L);
                entity.setCustodyFeeRenewalFission(orderEntity.getProfitAmount());
            }
            //裂变分润费
            entity.setProfitsFeeFission( (profitsDetail==null?0:profitsDetail.getProfitsAmount()));

            entity.setPromoteUserDirect(0);//直推用户数
            entity.setIndividualDirect(0);//直推个体数
            entity.setCompanyRegistFeeDirect(0L);//直推企业注册费
            entity.setInvoiceFeeDirect(0L);//直推开票服务费
            entity.setMemberUpgradeFeeDirect(0L);//直推会员升级服务费
            entity.setCompanyCancelFeeDirect(0L);//直推注销服务费
            entity.setProfitsFeeDirect(0L);//直推分润费
            entity.setInvoiceAmountDirect(0L);//直推开票金额
            entity.setStatisticsDay(new Date());//统计日期
            entity.setAddTime(new Date());
            entity.setAddUser(account);
            userOrderStatisticsDayService.insertSelective(entity);
        }
        return ;
    }


    /**
     * 企业注册数统计
     */
    private void companyNumStatistics(OrderEntity orderEntity, MemberAccountEntity upDiamondEntity, String account) {
        if(orderEntity== null || upDiamondEntity == null){
            return ;
        }
        if (orderEntity.getOrderType().intValue() != OrderTypeEnum.REGISTER.getValue()) {
            return ;
        }
        MemberLevelEntity memberLevelEntity=memberLevelService.findById(upDiamondEntity.getMemberLevel());
        //查询当天是否有统计数据
        UserOrderStatisticsDayEntity upDiamondOrderStatisticsDayEntity = userOrderStatisticsDayService.queryrOrderStatisticsDayByToday(upDiamondEntity.getOemCode(), upDiamondEntity.getId());
        if (upDiamondOrderStatisticsDayEntity != null) {//更新
            upDiamondOrderStatisticsDayEntity.setIndividualFission(upDiamondOrderStatisticsDayEntity.getIndividualFission() + 1);
            upDiamondOrderStatisticsDayEntity.setStatisticsDay(new Date());
            upDiamondOrderStatisticsDayEntity.setUpdateTime(new Date());
            upDiamondOrderStatisticsDayEntity.setUpdateUser(account);
            userOrderStatisticsDayService.editByIdSelective(upDiamondOrderStatisticsDayEntity);
        } else {//新增
            UserOrderStatisticsDayEntity entity = new UserOrderStatisticsDayEntity();
            entity.setOemCode(upDiamondEntity.getOemCode());//机构码
            entity.setUserId(upDiamondEntity.getId());//会员id
            entity.setUserLevelNo(memberLevelEntity.getLevelNo());//会员等级
            entity.setParentUserId(upDiamondEntity.getParentMemberId());//上级用户id
            //裂变个体数
            entity.setIndividualFission(1);
            ///直推企业注册费
            entity.setCompanyRegistFeeFission(0L);
            entity.setPromoteUserFission(0);//裂变用户数
            entity.setInvoiceFeeFission(0L);//裂变开票服务费
            entity.setCompanyCancelFeeFission(0L);//裂变注销服务费
            entity.setInvoiceAmountFission(0L);//裂变开票金额
            //裂变分润费
            entity.setProfitsFeeFission(0L);
            entity.setPromoteUserDirect(0);//直推用户数
            entity.setIndividualDirect(0);//直推个体数
            entity.setCompanyRegistFeeDirect(0L);//直推企业注册费
            entity.setInvoiceFeeDirect(0L);//直推开票服务费
            entity.setMemberUpgradeFeeDirect(0L);//直推会员升级服务费
            entity.setCompanyCancelFeeDirect(0L);//直推注销服务费
            entity.setProfitsFeeDirect(0L);//直推分润费
            entity.setInvoiceAmountDirect(0L);//直推开票金额
            entity.setStatisticsDay(new Date());//统计日期
            entity.setAddTime(new Date());
            entity.setAddUser(account);
            userOrderStatisticsDayService.insertSelective(entity);
        }
    }
}
