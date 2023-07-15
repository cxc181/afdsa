package com.yuqian.itax.mq.receiver.order;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.group.entity.GroupPaymentAnalysisRecordEntity;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import com.yuqian.itax.group.service.GroupPaymentAnalysisRecordService;
import com.yuqian.itax.group.service.InvoiceOrderGroupService;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.InvoiceRecordService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.tax.entity.query.PendingTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.PendingTaxBillVO;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.user.entity.CompanyTaxHostingEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.vo.MemberCompanyVo;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.enums.MemberCompanyTypeEnum;
import com.yuqian.itax.user.service.CompanyTaxHostingService;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.MoneyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 集团开票创建开票子订单
 * @author：pengwei
 * @Date：2020/3/12 15:12
 * @version：1.0
 */
@Component
@Slf4j
public class InvoiceOrderReceiver {

    @Autowired
    private RedisService redisService;

    @Autowired
    private GroupPaymentAnalysisRecordService groupPaymentAnalysisRecordService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private InvoiceOrderService invoiceOrderService;

    @Autowired
    private InvoiceOrderGroupService invoiceOrderGroupService;
    @Autowired
    private InvoiceRecordService invoiceRecordService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    CompanyTaxHostingService companyTaxHostingService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CompanyTaxBillService companyTaxBillService;

    //休眠时间
    private static final Long SLEEP_TIME = 5000L;

    @RabbitHandler
    @RabbitListener(queues = "createInvoiceOrder", priority="100")
    public void process(JSONObject json) {
        log.info("收到解析订单数据，请求参数：{}", JSONObject.toJSONString(json));
        Long analysisId = null;
        String redisTime = (System.currentTimeMillis() + 300000) + "";
        Long companyId = null;
        //mq重发标志
        boolean repeatFlag = true;
        GroupPaymentAnalysisRecordEntity analysisEntity = null;
        InvoiceOrderGroupEntity entity = null;
        try {
            analysisId = json.getLong("analysisId");
            String categoryName = json.getString("categoryName");
            Long categoryBaseId = json.getLong("categoryBaseId");
            analysisEntity = groupPaymentAnalysisRecordService.findById(analysisId);
            if (analysisEntity == null) {
                log.error("解析记录表数据不存在，解析记录表主键：{}", analysisId);
                return;
            }
            if (analysisEntity.getAnalysisResult() != 0) {
                log.error("解析记录表数据状态不是解析中，解析记录表主键：{}", analysisId);
                return;
            }
            entity = invoiceOrderGroupService.queryByOrderNo(analysisEntity.getGroupOrderNo(), analysisEntity.getOemCode());
            if (entity == null) {
                addError(analysisEntity, "集团开票主订单不存在");
                return;
            }

            List<MemberCompanyVo> companies = queryCompany(analysisEntity, categoryBaseId, entity.getVatFeeRate(),entity.getInvoiceWay());
            for (MemberCompanyVo company : companies) {
                //加锁
                companyId = company.getId();
                boolean lockResult = redisService.lock(RedisKey.LOCK_INVOICE_COMPANY + companyId, redisTime, 60);
                if(!lockResult){
                    continue;
                }
                //mq重发标志修改
                repeatFlag = false;
                // 仅支持个体户开票
                if (!MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(company.getCompanyType())) {
                    log.error("非个体企业不允许开票");
                }
                List<String> orderNoList = invoiceOrderService.getOrderNoByCreateWayAndCompanyId(companyId);
                if(CollectionUtil.isEmpty(orderNoList)){
                    log.error("存在待出款的佣金开票订单");
                }
                List<String> cacelOrderList = orderService.getOderNoByCompany(companyId);
                if(CollectionUtil.isNotEmpty(cacelOrderList)){
                    log.error("存在待处理的注销订单");
                }
                // 校验是否存在超时未确认成本税单
                PendingTaxBillQuery pendingTaxBillQuery = new PendingTaxBillQuery();
                pendingTaxBillQuery.setEin(company.getEin());
                pendingTaxBillQuery.setCompanyId(company.getId());
                pendingTaxBillQuery.setStatusRange(1);
                List<PendingTaxBillVO> pendingTaxBillVOS = companyTaxBillService.pendingTaxBill(pendingTaxBillQuery);
                if (CollectionUtil.isNotEmpty(pendingTaxBillVOS)) {
                    List<PendingTaxBillVO> collect = pendingTaxBillVOS.stream().filter(x -> x.getTimeDifference() < 0).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(collect)) {
                        log.error("该企业存在超时未确认成本的税单");
                    }
                }
                //创建订单
                InvoiceOrderEntity invoice=invoiceOrderService.createInvoiceOrderByGroup(company, analysisEntity, entity);
                MemberAccountEntity member=memberAccountService.findById(company.getMemberId());

                //查询企业托管信息
                CompanyTaxHostingEntity companyTaxHostingEntity = companyTaxHostingService.getCompanyTaxHostingByCompanyId(companyId,1);

                //创建开票记录
                //V3.1修改，开票记录创建放置财务审核之后
                //invoiceRecordService.createInvoiceRecord(invoice,companyTaxHostingEntity,company.getEin(), company.getParkId(), member.getId(), member.getMemberAccount(),true);
                break;
            }
            if (repeatFlag) {
                //休眠
                Thread.sleep(SLEEP_TIME);
                //mq重发
            }
            rabbitTemplate.convertAndSend("createInvoiceOrder", json);
        } catch (BusinessException e) {
            addError(analysisEntity, e.getMessage());
        }  catch (Exception e) {
            log.error("创建开票子订单全局异常，解析记录表主键：{}", analysisId);
            log.error(e.getMessage(), e);
            addError(analysisEntity, "创建开票子订单失败");
        } finally {
            if (entity != null) {
                invoiceOrderGroupService.ticketing(entity.getId());
            }
            if (!repeatFlag) {
                redisService.unlock(RedisKey.LOCK_INVOICE_COMPANY + companyId, redisTime);
            }
        }

    }

    /**
     * 查询可开票企业
     * @param analysisEntity
     * @param categoryBaseId
     * @param vatFeeRate
     * @throws BusinessException
     */
    public List<MemberCompanyVo> queryCompany(GroupPaymentAnalysisRecordEntity analysisEntity, Long categoryBaseId, BigDecimal vatFeeRate,Integer InvoicePreferences) throws BusinessException {
        Integer status = MemberCompanyStatusEnum.NORMAL.getValue();
        String orderBy=null;
        if(InvoicePreferences!=null&&InvoicePreferences==1){
            orderBy="DESC";
        }else{
            orderBy="ASC";
        }
        Integer result = memberCompanyService.countMemberCompanyByIdCard(analysisEntity.getPayeeIdcard(), analysisEntity.getOemCode(), status, analysisEntity.getInvoiceCompanyName(), null, null);
        if (result <= 0) {
            throw new BusinessException("当前身份证暂无满足开票条件的公司");
        }
        Long invoiceAmt = MoneyUtil.yuan2fen(new BigDecimal(analysisEntity.getInvoiceAmount())).longValue();
        result = memberCompanyService.countMemberCompanyByIdCard(analysisEntity.getPayeeIdcard(), analysisEntity.getOemCode(), status, analysisEntity.getInvoiceCompanyName(), invoiceAmt, null);
        if (result <= 0) {
            throw new BusinessException("没有可用开票额度满足开票条件的公司");
        }
        vatFeeRate = MoneyUtil.yuan2fen(vatFeeRate);
        result = memberCompanyService.countMemberCompanyByIdCard(analysisEntity.getPayeeIdcard(), analysisEntity.getOemCode(), status, analysisEntity.getInvoiceCompanyName(), invoiceAmt, vatFeeRate);
        if (result <= 0) {
            throw new BusinessException("没有增值税税率满足开票条件的公司");
        }
        List<MemberCompanyVo> companies = memberCompanyService.getMemberCompanyByIdCard(analysisEntity.getPayeeIdcard(), analysisEntity.getOemCode(), MemberCompanyStatusEnum.NORMAL.getValue(), analysisEntity.getInvoiceCompanyName(), invoiceAmt, categoryBaseId, vatFeeRate,orderBy);
        if(CollectionUtil.isEmpty(companies)) {
            throw new BusinessException("没有开票类目满足开票条件的公司");
        }
        return companies;
    }

    /**
     * 添加错误解析记录
     * @param analysisEntity
     * @param message
     */
    public void addError(GroupPaymentAnalysisRecordEntity analysisEntity, String message) {
        analysisEntity.setAnalysisResult(2);
        analysisEntity.setErrorResult(message);
        analysisEntity.setUpdateTime(new Date());
        analysisEntity.setUpdateUser(analysisEntity.getAddUser());
        groupPaymentAnalysisRecordService.editByIdSelective(analysisEntity);
    }

}
