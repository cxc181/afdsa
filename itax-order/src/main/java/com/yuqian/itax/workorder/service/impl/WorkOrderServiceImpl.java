package com.yuqian.itax.workorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.enums.WeChatMessageTemplateTypeEnum;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.message.service.WechatMessageTemplateService;
import com.yuqian.itax.order.dao.*;
import com.yuqian.itax.order.entity.*;
import com.yuqian.itax.order.enums.*;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.system.dao.RatifyTaxMapper;
import com.yuqian.itax.system.entity.*;
import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseStringVO;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.user.dao.CompanyInvoiceCategoryMapper;
import com.yuqian.itax.user.dao.CompanyInvoiceRecordMapper;
import com.yuqian.itax.user.dao.MemberCompanyMapper;
import com.yuqian.itax.user.dao.UserMapper;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.workorder.dao.WorkOrderChangeRecordMapper;
import com.yuqian.itax.workorder.dao.WorkOrderMapper;
import com.yuqian.itax.workorder.entity.WorkOrderChangeRecordEntity;
import com.yuqian.itax.workorder.entity.WorkOrderEntity;
import com.yuqian.itax.workorder.entity.dto.WorkOrderDTO;
import com.yuqian.itax.workorder.entity.query.WorkOrderQuery;
import com.yuqian.itax.workorder.entity.vo.WorkOrderListVO;
import com.yuqian.itax.workorder.enums.WorkOrderStatusEnum;
import com.yuqian.itax.workorder.enums.WorkOrderTypeEnum;
import com.yuqian.itax.workorder.service.WorkOrderChangeRecordService;
import com.yuqian.itax.workorder.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("workOrderService")
public class WorkOrderServiceImpl extends BaseServiceImpl<WorkOrderEntity,WorkOrderMapper> implements WorkOrderService {
    @Resource
    private WorkOrderChangeRecordMapper workOrderChangeRecordMapper;
    @Autowired
    private OrderService orderService;
    @Resource
    private RegisterOrderMapper registerOrderMapper;
    @Resource
    private RegisterOrderService registerOrderService;
    @Resource
    private RegisterOrderChangeRecordMapper registerOrderChangeRecordMapper;
    @Resource
    private OrderMapper orderMapper;
    @Autowired
    private WorkOrderChangeRecordService workOrderChangeRecordService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private MessageNoticeService messageNoticeService;
    @Resource
    private WorkOrderMapper workOrderMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private InvoiceOrderMapper invoiceOrderMapper;
    @Resource
    private CompanyInvoiceRecordMapper companyInvoiceRecordMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private OemService oemService;
    @Resource
    private InvoiceOrderChangeRecordMapper invoiceOrderChangeRecordMapper;
    @Resource
    private MemberCompanyMapper memberCompanyMapper;
    @Resource
    private CompanyInvoiceCategoryMapper companyInvoiceCategoryMapper;
    @Resource
    private BusinessScopeService businessScopeService;
    @Resource
    RatifyTaxMapper ratifyTaxMapper;
    @Autowired
    WechatMessageTemplateService wechatMessageTemplateService;
    @Autowired
    IndustryService industryService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private OrderWechatAuthRelaService orderWechatAuthRelaService;
    @Resource
    PayWaterService payWaterService;
    @Autowired
    private BusinessscopeTaxcodeService businessscopeTaxcodeService;
    @Autowired
    private InvoiceServiceFeeDetailService invoiceServiceFeeDetailService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ParkBusinessscopeService parkBusinessscopeService;

    @Override
    public PageInfo<WorkOrderListVO> queryWorkOrderPageInfo(WorkOrderQuery workOrderQuery) {
        PageHelper.startPage(workOrderQuery.getPageNumber(), workOrderQuery.getPageSize());
        List<WorkOrderListVO> list = this.mapper.queryWorkOrderList(workOrderQuery);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void updateStatus(WorkOrderEntity entity, CustomerServiceWorkNumberEntity serEntity, Integer status, String remark, Object obj) {
        entity.setWorkOrderStatus(status);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(serEntity.getWorkNumber());
        entity.setProcessorAccount(serEntity.getWorkNumber());
        entity.setProcessorName(serEntity.getWorkNumberName());
        entity.setRemark(remark);
        if (StringUtils.isBlank(entity.getCustomerServiceAccount())) {
            UserEntity userEntity = userMapper.selectByPrimaryKey(serEntity.getUserId());
            entity.setCustomerServiceAccount(Optional.ofNullable(userEntity).map(UserEntity::getUsername).orElse(null));
            entity.setCustomerServiceName(Optional.ofNullable(userEntity).map(UserEntity::getNickname).orElse(null));
            if (obj != null) {
                if (obj instanceof RegisterOrderEntity) {
                    RegisterOrderEntity orderEntity = (RegisterOrderEntity) obj;
                    orderEntity.setCustomerServicePhone(Optional.ofNullable(userEntity).map(UserEntity::getUsername).orElse(null));
                    registerOrderMapper.updateByPrimaryKeySelective(orderEntity);
                } else if (obj instanceof InvoiceOrderEntity) {
                    InvoiceOrderEntity orderEntity = (InvoiceOrderEntity) obj;
                    orderEntity.setCustomerServicePhone(Optional.ofNullable(userEntity).map(UserEntity::getUsername).orElse(null));
                    invoiceOrderMapper.updateByPrimaryKeySelective(orderEntity);
                }
            }
        }
        mapper.updateByPrimaryKeySelective(entity);
        WorkOrderChangeRecordEntity record = new WorkOrderChangeRecordEntity();
        BeanUtils.copyProperties(entity, record);
        record.setId(null);
        record.setAddTime(entity.getUpdateTime());
        record.setAddUser(serEntity.getWorkNumber());
        record.setUpdateTime(null);
        record.setUpdateUser(null);
        workOrderChangeRecordMapper.insertSelective(record);
    }

    @Override
    @Transactional
    public void updateInvOrderStatus(WorkOrderEntity workEntity, InvoiceOrderEntity invEntity, OrderEntity orderEntity, WorkOrderDTO dto, CustomerServiceWorkNumberEntity serEntity, MemberAccountEntity accEntity, boolean isNeedSendSms) {
        //更新工单
        updateStatus(workEntity, serEntity, workEntity.getWorkOrderStatus(), dto.getRemark(), null);
        String remark = invEntity.getRemark() + (StringUtils.isNotBlank(dto.getRemark()) ? "，" : "") + dto.getRemark();
        invEntity.setRemark(null);
        if (Objects.equals(workEntity.getWorkOrderType(), WorkOrderTypeEnum.INVOICE.getValue())) {
            //开票订单审核
            orderEntity.setUpdateTime(workEntity.getUpdateTime());
            orderEntity.setUpdateUser(serEntity.getWorkNumber());
            orderEntity.setOrderStatus(dto.getOrderStatus());
            orderService.updateInvOrderStatus(invEntity, orderEntity, remark);
            Map<String, Object> map = Maps.newHashMap();
            if (Objects.equals(dto.getOrderStatus(), InvoiceOrderStatusEnum.CANCELED.getValue())||Objects.equals(dto.getOrderStatus(), InvoiceOrderStatusEnum.AUDIT_FAILED.getValue())) {
                //开票金额回滚(重开订单不回滚年开票记录)
                if (!InvoiceMarkEnum.REOPEN.getValue().equals(invEntity.getInvoiceMark())) {
                    companyInvoiceRecordMapper.refund(invEntity.getCompanyId(), invEntity.getAddTime(), invEntity.getInvoiceAmount(), serEntity.getWorkNumber(), workEntity.getUpdateTime());
                }
                // 退款
                orderService.refund(orderEntity, serEntity.getWorkNumber(), workEntity.getUpdateTime(),2, false);
                map.put("reason", dto.getRemark());
                if (isNeedSendSms) {
                    smsService.sendTemplateSms(accEntity.getMemberPhone(), accEntity.getOemCode(), VerifyCodeTypeEnum.INVOICE_FAIL.getValue(), map, 2);
                }
                if (InvoiceMarkEnum.REOPEN.getValue().equals(invEntity.getInvoiceMark())) {
                    // 取消未读“作废重开”通知
                    MessageNoticeEntity noticeEntity = new MessageNoticeEntity();
                    noticeEntity.setOemCode(orderEntity.getOemCode());
                    noticeEntity.setUserId(orderEntity.getUserId());
                    noticeEntity.setBusinessType(12);
                    noticeEntity.setStatus(0);
                    noticeEntity.setIsAlert(0);
                    List<MessageNoticeEntity> list = messageNoticeService.select(noticeEntity);
                    if (null != list && !list.isEmpty()) {
                        for (MessageNoticeEntity messageNoticeEntity : list) {
                            messageNoticeService.updateStatusById(messageNoticeEntity.getId(), 3);
                        }
                    }
                }
            } else {
                smsService.sendTemplateSms(accEntity.getMemberPhone(), accEntity.getOemCode(), VerifyCodeTypeEnum.INVOICE_SUCCESS.getValue(), map, 2);
            }
        } else {
            //历史表
            InvoiceOrderChangeRecordEntity record = new InvoiceOrderChangeRecordEntity();
            BeanUtils.copyProperties(invEntity, record);
            record.setId(null);
            record.setAddTime(workEntity.getUpdateTime());
            record.setAddUser(workEntity.getUpdateUser());
            record.setUpdateTime(null);
            record.setUpdateUser(null);
            record.setRemark(remark);
            record.setOrderStatus(orderEntity.getOrderStatus());
            invoiceOrderChangeRecordMapper.insertSelective(record);
            //开票流水审核
            invoiceOrderMapper.updateByPrimaryKeySelective(invEntity);

            //开票流水审核不通过
            if (Objects.equals(invEntity.getBankWaterStatus(), 3)) {
                //审核未通过需发短信
                MemberCompanyEntity memberCompanyEntity = memberCompanyMapper.selectByPrimaryKey(invEntity.getCompanyId());
                if (memberCompanyEntity == null) {
                    throw new BusinessException("企业信息不存在");
                }
                Map<String, Object> map = Maps.newHashMap();
                map.put("companyName", memberCompanyEntity.getCompanyName());
                smsService.sendTemplateSms(accEntity.getMemberPhone(), accEntity.getOemCode(), VerifyCodeTypeEnum.INVOICE_WATER_FAIL.getValue(), map, 2);
            }
            //开票成果不通过
            if (Objects.equals(invEntity.getAchievementStatus(), 4)) {
                //审核未通过需发短信
                MemberCompanyEntity memberCompanyEntity = memberCompanyMapper.selectByPrimaryKey(invEntity.getCompanyId());
                if (memberCompanyEntity == null) {
                    throw new BusinessException("企业信息不存在");
                }
                Map<String, Object> map = Maps.newHashMap();
                map.put("companyName", memberCompanyEntity.getCompanyName());
                smsService.sendTemplateSms(accEntity.getMemberPhone(), accEntity.getOemCode(), VerifyCodeTypeEnum.ACHIEVEMENT_WATER_FAIL.getValue(), map, 2);
            }
        }

    }

    /**
     * 驳回项获取名字
     * @param rejectedItem
     * @return
     */
    private String getRejectedItemName(String rejectedItem){
        StringBuffer rejectedItemName = new StringBuffer();
        if(StringUtils.isNotBlank(rejectedItem)){
            String[] item = rejectedItem.replace("，","").split(",");
            for(String itm : item){
                if("1".equals(itm)){
                    rejectedItemName.append("字号、");
                }else if("2".equals(itm)){
                    rejectedItemName.append("身份证照片、");
                }else if("3".equals(itm)){
                    rejectedItemName.append("视频、");
                }
            }
        }
        return rejectedItemName.length()>0 ? rejectedItemName.substring(0,rejectedItemName.length()-1):null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateRegOrderStatus(WorkOrderEntity workEntity, RegisterOrderEntity regEntity, OrderEntity orderEntity, WorkOrderDTO dto, CustomerServiceWorkNumberEntity serEntity, MemberAccountEntity accEntity) {
        Map<String, Object> wetchatMap=new HashMap<>();
        if(workEntity.getWorkOrderStatus().intValue() == WorkOrderStatusEnum.REJECTED.getValue()){
            workEntity.setWorkOrderDesc("驳回项："+getRejectedItemName(regEntity.getRejectedItem()));
        }
        //更新工单
        updateStatus(workEntity, serEntity, workEntity.getWorkOrderStatus(), dto.getRemark(), null);
        //更新注册订单
//        regEntity.setAgentAccount(dto.getAgentAccount());
        regEntity.setRemark(dto.getRemark());
        regEntity.setUpdateTime(workEntity.getUpdateTime());
        regEntity.setUpdateUser(serEntity.getWorkNumber());
        registerOrderMapper.updateByPrimaryKeySelective(regEntity);
        //保存历史记录
        RegisterOrderChangeRecordEntity record = new RegisterOrderChangeRecordEntity();
        BeanUtils.copyProperties(regEntity, record);
        record.setId(null);
        record.setAddTime(workEntity.getUpdateTime());
        record.setAddUser(serEntity.getWorkNumber());
        record.setOrderStatus(dto.getOrderStatus());
        registerOrderChangeRecordMapper.insertSelective(record);
        //更新订单状态
        orderMapper.updateOrderStatus(orderEntity.getOrderNo(), dto.getOrderStatus(), serEntity.getWorkNumber(), workEntity.getUpdateTime());

        Map<String, Object> map = Maps.newHashMap();
        String noticeStatus = "审核成功";
        String noticeRemark = "预计3个工作日内出证，请您耐心等待~";
        if (dto.getOrderStatus().equals(RegOrderStatusEnum.FAILED.getValue())
            || dto.getOrderStatus().equals(RegOrderStatusEnum.REJECTED.getValue())) {
            //短信模板类型
            String code = VerifyCodeTypeEnum.REGISTER_AUDIT_REJECTED.getValue();
            noticeStatus = "个体户核名驳回";
            noticeRemark = dto.getRemark();
            if (dto.getOrderStatus().equals(RegOrderStatusEnum.FAILED.getValue())
                    && IsSelfPayingEnum.SELF_PLAYING.getValue().equals(orderEntity.getIsSelfPaying())) {
                //审核不通过发起退款，修改短信模板类型
                noticeStatus = "审核未通过";
                code = VerifyCodeTypeEnum.REGISTER_CANCELLED.getValue();

                //微信退款
                List<PayWaterEntity> list=payWaterService.queryPayWaterListByOrderNoAndStaus(workEntity.getOrderNo());
                if(CollectionUtils.isEmpty(list)){
                    throw  new BusinessException("支付流水不存在");
                }
                PayWaterEntity payWaterEntity=list.get(0);
                if(payWaterEntity.getPayWay()==1 || payWaterEntity.getPayWay()==5){
                    if(payWaterEntity.getPayAmount()>0L) {
                        try {
                            wetchatMap=registerOrderService.routeAndRefundOrder(workEntity.getOemCode(),list.get(0).getOrderNo(),list.get(0).getPayNo(),serEntity.getWorkNumber());
                        } catch (UnknownHostException e) {
                           log.error(e.getMessage());
                            throw  new BusinessException("调用渠道微信退款失败");
                        }
                    }
                }else{
                    // 退款
                    orderService.refund(orderEntity, serEntity.getWorkNumber(), workEntity.getUpdateTime(),2, false);
                }
            } else {
                // 短信模板类型(V2.10改为资料驳回)
                noticeStatus = "个体户资料驳回";
                noticeRemark = "您提交的资料不符合工商要求，请重新提交";
                String registeredName = accEntity.getMemberPhone();
                if (StringUtils.isNotBlank(accEntity.getRealName())) {
                    registeredName = accEntity.getRealName() + "/" + accEntity.getMemberPhone();
                }
                //系统通知自己直属上级
                if (accEntity.getParentMemberId() != null) {
                    DictionaryEntity entity = dictionaryService.getByCode("register_pay_notice_tmpl");
                    String value = entity.getDictValue();
                    value = value.replace("#registeredName#",registeredName);
                    saveNoticeMessage(accEntity.getParentMemberId(), regEntity.getId(), regEntity.getOrderNo(),"推广动态提醒",value, regEntity.getUpdateUser(), regEntity.getOemCode());
                }

                DictionaryEntity entity = dictionaryService.getByCode("register_rejected_notice_tmpl");
                String value = entity.getDictValue();
                saveNoticeMessage(accEntity.getId(), regEntity.getId(), regEntity.getOrderNo(),"注册申请驳回通知",value, regEntity.getUpdateUser(), regEntity.getOemCode());
            }

            //发短信
            OemEntity oem = oemService.getOem(accEntity.getOemCode());
            if (oem == null) {
                throw new BusinessException("OEM机构不存在");
            }
            map.put("telPhone", oem.getCustomerServiceTel());
            smsService.sendTemplateSms(accEntity.getMemberPhone(), accEntity.getOemCode(), code, map, 2);
        }
        try{
            //微信消息通知
            weChatNotice(regEntity, orderEntity, accEntity, noticeStatus, noticeRemark);
        }catch (Exception e){
            log.info("工单号:"+workEntity.getWorkOrderNo()+"===========发送微信消息失败："+e.getMessage());
        }
        return wetchatMap;
    }

    /**
     * 微信消息通知
     * @param regEntity
     * @param orderEntity
     * @param accEntity
     * @param noticeStatus
     * @param noticeRemark
     */
    public void weChatNotice(RegisterOrderEntity regEntity, OrderEntity orderEntity, MemberAccountEntity accEntity, String noticeStatus, String noticeRemark) {
        //微信消息通知
        OrderWechatAuthRelaEntity orderWechatAuthRelaEntity = orderWechatAuthRelaService.queryByOrderNo(orderEntity.getOrderNo(), WeChatMessageTemplateTypeEnum.REGISTER_AUDIT_RESULT.getValue(), OrderWeChatAuthEnum.YES.getValue());
        if (orderWechatAuthRelaEntity == null) {
            return;
        }
        Map<String, Object> args = wechatMessageTemplateService.getRegAuditNoticeMap(orderEntity.getOrderNo(), regEntity.getUpdateTime(), noticeStatus, noticeRemark);
        String page = null;
        DictionaryEntity pageEntity = dictionaryService.getByCode("wechat_reg_audit_notice_page");
        if (pageEntity != null) {
            page = pageEntity.getDictValue();
        }
        String result = wechatMessageTemplateService.sendNotice(accEntity.getOpenId(), page, WeChatMessageTemplateTypeEnum.REGISTER_AUDIT_RESULT.getValue(), accEntity.getOemCode(), args);
        try {
            regEntity.setWechatMessageNoticeTime(new Date());
            regEntity.setWechatMessageTemplateId(args.get("wechatMessageTemplateId") == null ? null : (Integer) args.get("wechatMessageTemplateId"));
            JSONObject jsonObject = JSONObject.parseObject(result);
            int wechatMessageNoticeResult = 0;
            regEntity.setWechatMessageErrorCause(result);
            orderWechatAuthRelaEntity.setWechatResult(result);
            if (jsonObject.getIntValue("errcode") == 0) {
                wechatMessageNoticeResult = 1;
                regEntity.setWechatMessageErrorCause(null);
            }
            regEntity.setWechatMessageNoticeResult(wechatMessageNoticeResult);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            regEntity.setWechatMessageNoticeResult(0);
            regEntity.setWechatMessageErrorCause("微信消息通知异常");
            orderWechatAuthRelaEntity.setWechatResult("微信消息通知异常");
        }
        orderWechatAuthRelaService.editByIdSelective(orderWechatAuthRelaEntity);
        registerOrderMapper.updateByPrimaryKeySelective(regEntity);
    }
    /**
     *  add ni.jiang
     * 开户客服审核后进行通知
     * @return
     */
    private int saveNoticeMessage(Long userId,Long regOrderId,String orderNo,String title,String content,String useraccount,String oemCode){
        MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
        messageNoticeEntity.setOemCode(oemCode);
        messageNoticeEntity.setOpenMode(1);
        messageNoticeEntity.setBusinessType(1);
        messageNoticeEntity.setNoticeType(2);
        messageNoticeEntity.setNoticePosition("1,2");
        messageNoticeEntity.setNoticeTitle(title);
        messageNoticeEntity.setUserId(userId);
        messageNoticeEntity.setUserType(1);
        messageNoticeEntity.setOrderNo(orderNo);
        messageNoticeEntity.setNoticeContent(content);
        messageNoticeEntity.setSourceId(regOrderId);
        messageNoticeEntity.setStatus(0);
        messageNoticeEntity.setAddUser(useraccount);
        messageNoticeEntity.setAddTime(new Date());
        return messageNoticeService.saveMessageNotice(messageNoticeEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelWorkOrder(String userAccount, String orderNo,String oemCode) throws BusinessException {
        log.info("接收到取消工单参数：{}，{}，{}",userAccount,orderNo,oemCode);

        if(StringUtils.isBlank(userAccount) || StringUtils.isBlank(orderNo)){
            throw new BusinessException("取消工单参数不正确");
        }

        OrderEntity order = this.orderMapper.queryByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 判断订单状态，在开户订单状态为“已取消”的，或者开票订单状态为“已取消”的，工单需要取消
        if(order.getOrderStatus().equals(RegOrderStatusEnum.CANCELLED.getValue()) || order.getOrderStatus().equals(InvoiceOrderStatusEnum.CANCELED.getValue())){
            // 查询可取消的工单
            WorkOrderEntity t = new WorkOrderEntity();
            t.setOrderNo(orderNo);
            t.setOemCode(oemCode);
            WorkOrderEntity workOrder = this.workOrderMapper.queryOrderByStatus(t);
            if(null != workOrder){
                // 工单状态更新为已取消
                WorkOrderEntity edit = new WorkOrderEntity();
                edit.setId(workOrder.getId());
                edit.setWorkOrderStatus(WorkOrderStatusEnum.CANCELED.getValue());
                edit.setUpdateTime(new Date());
                edit.setUpdateUser(userAccount);
                this.editByIdSelective(edit);

                // 保存工单变更记录
                WorkOrderChangeRecordEntity changeRecord = new WorkOrderChangeRecordEntity();
                BeanUtils.copyProperties(workOrder,changeRecord);
                changeRecord.setId(null);
                changeRecord.setWorkOrderStatus(WorkOrderStatusEnum.CANCELED.getValue());
                changeRecord.setUpdateTime(new Date());
                changeRecord.setUpdateUser(userAccount);
                this.workOrderChangeRecordService.insertSelective(changeRecord);
            }

        }
        log.info("取消工单请求结束");
    }
    @Resource
    InvoiceCategoryBaseService invoiceCategoryBaseService;

    @Override
    @Transactional
    public void updateRegOrder(RegisterOrderEntity regEntity, WorkOrderDTO dto, CustomerServiceWorkNumberEntity serEntity) {
        //更新注册订单
        Date date = new Date();
        regEntity.setContactPhone(StringUtils.isBlank(dto.getPhone()) ? regEntity.getContactPhone() : dto.getPhone());
        regEntity.setEmail(StringUtils.isBlank(dto.getEmail()) ? regEntity.getEmail() : dto.getEmail());
        if (dto.getIndustryId() != null && !Objects.equals(dto.getIndustryId(), regEntity.getIndustryId())) {
            List<BusinessScopeEntity> businessScopeList = businessScopeService.listBusinessScope(dto.getIndustryId());
            if (CollectionUtil.isNotEmpty(businessScopeList)) {
                regEntity.setIndustryBusinessScope(businessScopeList.get(0).getBusinessContent());
                regEntity.setBusinessScope(businessScopeList.get(0).getBusinessContent());
            }
          /*  InvoiceCategoryEntity t = new InvoiceCategoryEntity();
            t.setIndustryId(dto.getIndustryId());
            List<InvoiceCategoryEntity> invoiceCategoryList = invoiceCategoryService.select(t);
            if (CollectionUtil.isNotEmpty(invoiceCategoryList)) {
                List<String> collect = invoiceCategoryList.stream().map(InvoiceCategoryEntity::getCategoryName).collect(Collectors.toList());
                dto.setCategoryNames(collect);
            }*/
            regEntity.setExampleName(Optional.ofNullable(industryService.findById(dto.getIndustryId())).map(IndustryEntity::getExampleName).orElse(null));
        } else {
            regEntity.setExampleName(StringUtils.isBlank(dto.getExampleName()) ? regEntity.getExampleName() : dto.getExampleName());
            //  校验经营范围是否在经营范围基础库存在
            OrderEntity orderEntity = orderService.queryByOrderNo(regEntity.getOrderNo());
            if (StringUtils.isNotBlank(dto.getBusinessScope())){
                String scope = dto.getBusinessScope().replaceAll("；",";");
                if(scope.startsWith(";")){
                    scope = scope.substring(1);
                }
                String[] scopes = scope.split(";");
                for (String name:scopes){
                    ParkBusinessscopeEntity parkBusinessscopeEntity = parkBusinessscopeService.getParkBusinessscopeByParkIdAndName(orderEntity.getParkId(),name);
                    if (parkBusinessscopeEntity == null){
                        throw new BusinessException(name+"不存在!");
                    }
                }
                //  修改行业经营范围
                if (StringUtil.isNotBlank(regEntity.getIndustryBusinessScope()) ){
                    regEntity.setIndustryBusinessScope( dto.getBusinessScope());
                    String businessScope = getOrderBusinessScope(regEntity);
                    regEntity.setBusinessScope(businessScope);
                }else{
                    // 修改订单经营范围
                    regEntity.setBusinessScope(StringUtils.isBlank(dto.getBusinessScope()) ? regEntity.getBusinessScope() : dto.getBusinessScope());
                }
            }
            if (StringUtils.isNotBlank(dto.getOwnBusinessScope())){
                if (dto.getOwnBusinessScope().length()>300){
                    throw new BusinessException("自选经营范围不能超过300个字符");
                }
                String scope = dto.getOwnBusinessScope().replace("；",";");
                String[] scopes = scope.split(";");
                for (String name:scopes){
                    ParkBusinessscopeEntity parkBusinessscopeEntity = parkBusinessscopeService.getParkBusinessscopeByParkIdAndName(orderEntity.getParkId(),name);
                    if (parkBusinessscopeEntity == null){
                        throw new BusinessException(name+"不存在!");
                    }
                }
                regEntity.setOwnBusinessScope(dto.getOwnBusinessScope());
                String businessScope = getOrderBusinessScope(regEntity);
                regEntity.setBusinessScope(businessScope);
            }
        }
        //蒋匿编写，有问题找蒋匿修改
        RatifyTaxEntity ratifyTaxEntity=new RatifyTaxEntity();
        ratifyTaxEntity.setIndustryId(dto.getIndustryId() == null ? regEntity.getIndustryId() : dto.getIndustryId());
        RatifyTaxEntity ratifyTax= ratifyTaxMapper.selectOne(ratifyTaxEntity);
        regEntity.setRatifyTax(ratifyTax.getTaxName());

        regEntity.setIndustryId(dto.getIndustryId() == null ? regEntity.getIndustryId() : dto.getIndustryId());
        regEntity.setShopName(StringUtils.isBlank(dto.getShopName()) ? regEntity.getShopName() : dto.getShopName());
        regEntity.setRegisteredName(StringUtils.isBlank(dto.getShopName()) ? regEntity.getRegisteredName() : dto.getShopName());
        regEntity.setShopNameOne(StringUtils.isBlank(dto.getShopNameOne()) ? regEntity.getShopNameOne() : dto.getShopNameOne());
        regEntity.setShopNameTwo(StringUtils.isBlank(dto.getShopNameTwo()) ? regEntity.getShopNameTwo() : dto.getShopNameTwo());
        regEntity.setUpdateTime(date);
        regEntity.setUpdateUser(serEntity.getWorkNumber());
//        //个体名称 = 园区属性 + 字号 + 示例名称（个体名称后缀）
//        regEntity.setRegisteredName(dto.getParkCity() + regEntity.getShopName() + StringHandleUtil.removeStar(regEntity.getExampleName()));
        //校验是否有重名企业
        memberCompanyService.checkCompanyName(regEntity.getOemCode(), dto.getParkCity(), regEntity.getExampleName(), dto.getShopName(), dto.getShopNameOne(), dto.getShopNameTwo());

        registerOrderMapper.updateByPrimaryKeySelective(regEntity);
        //保存历史记录
        RegisterOrderChangeRecordEntity record = new RegisterOrderChangeRecordEntity();
        BeanUtils.copyProperties(regEntity, record);
        record.setId(null);
        record.setOrderStatus(dto.getOrderStatus());
        record.setAddTime(date);
        record.setAddUser(serEntity.getWorkNumber());
        record.setOrderStatus(dto.getOrderStatus());
        record.setRemark("工商注册信息编辑");
        registerOrderChangeRecordMapper.insertSelective(record);

        List<InvoiceCategoryBaseStringVO> categoryList =new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dto.getCategoryList())) {
            companyInvoiceCategoryMapper.deleteByOrderNo(regEntity.getOrderNo(), regEntity.getOemCode());
            for (InvoiceCategoryBaseStringVO vo:dto.getCategoryList()) {
                InvoiceCategoryBaseEntity entity=new InvoiceCategoryBaseEntity();
                entity.setTaxClassificationAbbreviation(vo.getTaxClassificationAbbreviation());
                entity.setGoodsName(vo.getGoodsName());
                List<InvoiceCategoryBaseEntity> list=invoiceCategoryBaseService.select(entity);
                if(CollectionUtil.isEmpty(list)){
                    throw  new BusinessException("所选类目不存在，请重新选择");
                }
                vo.setId(list.get(0).getId());
                categoryList.add(vo);
            }

            CompanyInvoiceCategoryEntity categoryEntity = new CompanyInvoiceCategoryEntity();
            categoryEntity.setOemCode(regEntity.getOemCode());
            categoryEntity.setOrderNo(regEntity.getOrderNo());
            categoryEntity.setIndustryId(regEntity.getIndustryId());
            categoryEntity.setAddTime(date);
            categoryEntity.setAddUser(serEntity.getWorkNumber());
            categoryEntity.setRemark("工单审核添加开票类目");
            companyInvoiceCategoryMapper.addBatchByInvoiceCategoryBaseStringVO(categoryEntity, categoryList);
        }
    }

    @Override
    @Transactional
    public void editAndSaveInvOrderHistory(WorkOrderEntity entity, InvoiceOrderEntity invEntity, InvoiceOrderEntity histCopyEntity, Integer orderStatus, String userAccount, String hisRemark) {
        if (Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.WATER.getValue())) {
            String hisAccountStatement = entity.getAccountStatement();
            entity.setAccountStatement(invEntity.getAccountStatement());
            invEntity.setAccountStatement(histCopyEntity.getAccountStatement());
            histCopyEntity.setAccountStatement(hisAccountStatement);
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(userAccount);
            entity.setRemark(hisRemark);
            mapper.updateByPrimaryKeySelective(entity);
            WorkOrderChangeRecordEntity record = new WorkOrderChangeRecordEntity();
            BeanUtils.copyProperties(entity, record);
            record.setId(null);
            record.setUpdateTime(null);
            record.setUpdateUser(null);
            record.setAddTime(entity.getUpdateTime());
            record.setAddUser(userAccount);
            workOrderChangeRecordMapper.insertSelective(record);
        }
        if (Objects.equals(entity.getWorkOrderType(), WorkOrderTypeEnum.ACHIEVEMENT.getValue())) {
            String hisAchievementImgs = entity.getAchievementImgs();
            String hisAchievementVideo = entity.getAchievementVideo();
            entity.setAchievementImgs(invEntity.getAchievementImgs());
            entity.setAchievementVideo(invEntity.getAchievementVideo());
            histCopyEntity.setAchievementImgs(hisAchievementImgs);
            histCopyEntity.setAchievementVideo(hisAchievementVideo);
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(userAccount);
            entity.setRemark(hisRemark);
            mapper.updateByPrimaryKeySelective(entity);
            WorkOrderChangeRecordEntity record = new WorkOrderChangeRecordEntity();
            BeanUtils.copyProperties(entity, record);
            record.setId(null);
            record.setUpdateTime(null);
            record.setUpdateUser(null);
            record.setAddTime(entity.getUpdateTime());
            record.setAddUser(userAccount);
            workOrderChangeRecordMapper.insertSelective(record);
        }
        invoiceOrderService.editAndSaveHistory(invEntity, histCopyEntity, orderStatus, userAccount, hisRemark);
    }

    /**
     * 工单转派
     * @param id 工单id
     * @param customerServiceId 转派坐席id
     * @param remark 备注
     * @param updateUserId 操作人id
     * @param updateUser 修改人
     */
    public void forward(Long id,Long customerServiceId,String remark,Long updateUserId,String updateUser){
        WorkOrderEntity entity = this.findById(id);
        if (entity == null) {
            throw new BusinessException("工单不存在");
        }
        //只有待接单和审核中状态的工单可以转派
        if (entity.getWorkOrderStatus().intValue() != WorkOrderStatusEnum.WAITING_FOR_ORDERS.getValue() && entity.getWorkOrderStatus().intValue() != WorkOrderStatusEnum.AUDITING.getValue()) {
            throw new BusinessException("该工单状态不能进行转派操作");
        }
        //判断转派坐席是否正确
        UserEntity userEntity = userService.findById(customerServiceId);
        if(userEntity==null || userEntity.getStatus().intValue() != 1 || userEntity.getAccountType().intValue() != 2){
            throw new BusinessException("转派坐席错误或不可用");
        }
        UserEntity toCustomer=userService.findById(customerServiceId);
        if(entity.getCustomerServiceId()!=null){
            UserEntity customer=userService.findById(entity.getCustomerServiceId());
            if(customer==null){
                throw new BusinessException("工单里面的坐席不存在");
            }
            if(!Objects.equals(customer.getOemCode(),toCustomer.getOemCode())){
                throw new BusinessException("转派不可能跨机构");
            }
        }else{
            OemEntity oemEntity=oemService.getOem(entity.getOemCode());
            if(oemEntity==null){
                throw new BusinessException("机构不存在");
            }
            if(oemEntity.getWorkAuditWay()==1){//平台
                if(toCustomer.getOemCode()!=null||"".equals(toCustomer.getOemCode())){
                    throw new BusinessException("平台不能转派机构");
                }
            }else{
                UserEntity customer=userService.findById(entity.getCustomerServiceId());
                if(customer==null){
                    throw new BusinessException("工单里面的坐席不存在");
                }
                if(Objects.equals(customer.getOemCode(),toCustomer.getOemCode())){
                    throw new BusinessException("不能跨机构转派工单");
                }
            }

        }
        if(Objects.equals(userEntity.getOemCode(),entity.getOemCode())){

        }

        entity.setCustomerServiceAccount(userEntity.getUsername());
        entity.setCustomerServiceName(userEntity.getNickname());
        entity.setCustomerServiceId(userEntity.getId());
        entity.setWorkOrderStatus(WorkOrderStatusEnum.WAITING_FOR_ORDERS.getValue());
        entity.setWorkOrderDesc("工单转派");
        entity.setRemark(remark);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(updateUser);
        editByIdSelective(entity);
        //修改历史记录
        WorkOrderChangeRecordEntity record = new WorkOrderChangeRecordEntity();
        BeanUtils.copyProperties(entity, record);
        record.setCustomerServiceAccount(updateUser);
        userEntity = userService.findById(updateUserId);
        if(updateUser != null) {
            record.setCustomerServiceName(userEntity.getNickname());
        }
        record.setId(null);
        record.setAddTime(entity.getUpdateTime());
        record.setAddUser(updateUser);
        record.setUpdateTime(null);
        record.setUpdateUser(null);
        workOrderChangeRecordMapper.insertSelective(record);
    }

    @Override
    public List<WorkOrderEntity> queryWorkOrderByOrderNo(String orderNo) {
        return mapper.queryWorkOrderByOrderNo(orderNo);
    }

    @Override
    public List<WorkOrderEntity> queryByUserId(WorkOrderQuery workOrderQuery) {
        return mapper.queryByUserId(workOrderQuery);
    }

    /**
     * 获取注册订单的经营范围
     * @param entity
     * @return
     */
    private String getOrderBusinessScope(RegisterOrderEntity entity){
        StringBuilder businessScope = new StringBuilder();
        String orderBusiness = "";
        if (StringUtil.isNotBlank(entity.getIndustryBusinessScope())){
            businessScope.append(entity.getIndustryBusinessScope());
        }
        if (StringUtil.isNotBlank(entity.getOwnBusinessScope())){
            businessScope.append(";");
            businessScope.append(entity.getOwnBusinessScope());
        }
        if (StringUtil.isNotBlank(entity.getTaxcodeBusinessScope())){
            businessScope.append(";");
            businessScope.append(entity.getTaxcodeBusinessScope());
        }
        if (StringUtil.isNotBlank(businessScope.toString())){
            List<String> businessScopeList = Arrays.asList(businessScope.toString().split(";"));
            List<String> list=(List) businessScopeList.stream().distinct().collect(Collectors.toList());
            for(String scope:list){
                orderBusiness += scope + ";";
            }
        }
        if(orderBusiness.startsWith(";")){
            orderBusiness = orderBusiness.substring(1);
        }
        return orderBusiness;
    }
}

