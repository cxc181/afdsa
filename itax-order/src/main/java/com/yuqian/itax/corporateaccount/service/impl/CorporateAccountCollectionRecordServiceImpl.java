package com.yuqian.itax.corporateaccount.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.corporateaccount.dao.CorporateAccountCollectionRecordMapper;
import com.yuqian.itax.corporateaccount.entity.CollectionWithdrawalAmountChangeRecordEntity;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountCollectionRecordEntity;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountWithdrawalOrderEntity;
import com.yuqian.itax.corporateaccount.service.CollectionWithdrawalAmountChangeRecordService;
import com.yuqian.itax.corporateaccount.service.CorporateAccountCollectionRecordService;
import com.yuqian.itax.corporateaccount.service.CorporateAccountWithdrawalOrderService;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.RACWStatusEnum;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.enums.PayWaterStatusEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.entity.query.CompanyCorporateAccountVerificationQuery;
import com.yuqian.itax.user.entity.query.CorporateAccountCollectionRecordQuery;
import com.yuqian.itax.user.entity.vo.CorporateAccountCollectionRecordVO;
import com.yuqian.itax.user.entity.vo.CorporateAccountVOAdmin;
import com.yuqian.itax.user.entity.vo.IncomeAndExpenseVO;
import com.yuqian.itax.user.entity.vo.PublicAccountDetailVO;
import com.yuqian.itax.user.service.CompanyCorporateAccountService;
import com.yuqian.itax.user.service.DaifuApiService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service("corporateAccountCollectionRecordService")
public class CorporateAccountCollectionRecordServiceImpl extends BaseServiceImpl<CorporateAccountCollectionRecordEntity,CorporateAccountCollectionRecordMapper> implements CorporateAccountCollectionRecordService {


    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private DaifuApiService daifuApiService;
    @Autowired
    private CompanyCorporateAccountService companyCorporateAccountService;
    @Autowired
    private CollectionWithdrawalAmountChangeRecordService collectionWithdrawalAmountChangeRecordService;
    @Autowired
    private CorporateAccountWithdrawalOrderService corporateAccountWithdrawalOrderService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PayWaterService payWaterService;

    @Override
    public PublicAccountDetailVO getCorpAccountCollectionRecords(CorporateAccountCollectionRecordQuery query) throws BusinessException {
        log.info("对公户账户明细查询：{}", JSON.toJSONString(query));

        // 查询对公户是否存在
        CompanyCorporateAccountEntity ccae = this.companyCorporateAccountService.findById(query.getCorporateAccountId());
        if(null == ccae){
            throw new BusinessException("企业对公户信息不存在");
        }
        query.setTxnStffId(ccae.getVoucherMemberCode());
        query.setDraweeAccountNo(ccae.getCorporateAccount());

        // 默认查询本月数据，当天记录需要实时获取并入库
        fetchCardTransDetail(query);

        // 处理查询条件，时间格式化处理 YYYY-MM-DD HH:mm:ss
        if(StringUtils.isBlank(query.getMonth()) && StringUtils.isBlank(query.getStartDate()) && StringUtils.isBlank(query.getEndDate())){
            // 默认查本月
            query.setStartDate(DateUtil.getMonFirstDay() + " 00:00:00");
            query.setEndDate(DateUtil.getMonLastDay() + " 23:59:59");
        }else if(StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isBlank(query.getEndDate())){// 开始时间不为空，结束时间为空
            query.setDay(query.getStartDate());
        } else if(StringUtils.isNotBlank(query.getEndDate()) && StringUtils.isBlank(query.getStartDate())){// 结束时间不为空，开始时间为空
            query.setDay(query.getEndDate());
        } else if (StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isNotBlank(query.getEndDate())){ // 开始时间结束时间均不为空
            query.setStartDate(query.getStartDate() + " 00:00:00");
            query.setEndDate(query.getEndDate() + " 23:59:59");
        }

        // 处理抬头公司
        if(StringUtil.isNotBlank(query.getCompanyName())){
            if(query.getCompanyName().contains("公司")){
                query.setCompanyName(query.getCompanyName().substring(0,query.getCompanyName().indexOf("公司") + "公司".length()));
            } else if(query.getCompanyName().contains("有限合伙")){
                query.setCompanyName(query.getCompanyName().substring(0,query.getCompanyName().indexOf("有限合伙") + "有限合伙".length()));
            } else if(query.getCompanyName().contains("（有限合伙）")){
                query.setCompanyName(query.getCompanyName().substring(0,query.getCompanyName().indexOf("（有限合伙）")+ "（有限合伙）".length()));
            }
        }

        PublicAccountDetailVO detailVO = new PublicAccountDetailVO();
        // 统计收支金额
        IncomeAndExpenseVO incomeAndExpenseVO = this.mapper.queryIncomeAndExpense(query);
        detailVO.setIncomes(incomeAndExpenseVO.getIncomes());
        detailVO.setExpenses(incomeAndExpenseVO.getExpenses());

        // 分页查询账户明细列表
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<CorporateAccountCollectionRecordVO> list = this.mapper.queryCorpAccountCollectionRecords(query);

        PageInfo pageInfo = new PageInfo<CorporateAccountCollectionRecordVO>(list);
        detailVO.setFlowPageList(pageInfo);
        return detailVO;
    }

    @Override
    public PageInfo<CorporateAccountVOAdmin> queryCorpAccountCollectionRecordsAdmin(CorporateAccountCollectionRecordQuery query) throws BusinessException {
        log.info("对公户账户明细查询：{}", JSON.toJSONString(query));

        // 查询对公户是否存在
        CompanyCorporateAccountEntity ccae = this.companyCorporateAccountService.findById(query.getCorporateAccountId());
        if(null == ccae){
            throw new BusinessException("企业对公户信息不存在");
        }
        query.setTxnStffId(ccae.getVoucherMemberCode());
        query.setDraweeAccountNo(ccae.getCorporateAccount());
        // 默认查询本月数据，当天记录需要实时获取并入库
        fetchCardTransDetail(query);

        // 处理查询条件，时间格式化处理 YYYY-MM-DD HH:mm:ss
        if(StringUtils.isBlank(query.getMonth()) && StringUtils.isBlank(query.getStartDate()) && StringUtils.isBlank(query.getEndDate())){
            // 默认查本月
            query.setStartDate(DateUtil.getMonFirstDay() + " 00:00:00");
            query.setEndDate(DateUtil.getMonLastDay() + " 23:59:59");
        }else if(StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isBlank(query.getEndDate())){// 开始时间不为空，结束时间为空
            query.setDay(query.getStartDate());
        } else if(StringUtils.isNotBlank(query.getEndDate()) && StringUtils.isBlank(query.getStartDate())){// 结束时间不为空，开始时间为空
            query.setDay(query.getEndDate());
        } else if (StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isNotBlank(query.getEndDate())){ // 开始时间结束时间均不为空
            query.setStartDate(query.getStartDate() + " 00:00:00");
            query.setEndDate(query.getEndDate() + " 23:59:59");
        }

        // 分页查询账户明细列表
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<CorporateAccountVOAdmin> list = this.mapper.queryCorpAccountCollectionRecordsAdmin(query);

        PageInfo pageInfo = new PageInfo<CorporateAccountVOAdmin>(list);
        return pageInfo;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void returnWithdrawAmount(String orderNo,String externalOrderNo,String upStatusCode,String upResultMsg) throws BusinessException {
        log.info("开票提现金额和收款记录可提现金额回退");

        // 查询对公户提现订单信息
        CorporateAccountWithdrawalOrderEntity t = new CorporateAccountWithdrawalOrderEntity();
        t.setOrderNo(orderNo);
        CorporateAccountWithdrawalOrderEntity withdrawOrder = this.corporateAccountWithdrawalOrderService.selectOne(t);

        // 开票订单可提现额度回退
        this.invoiceOrderService.updateRemainingWithdrawAmount(withdrawOrder.getInvoiceOrderNo(),withdrawOrder.getWithdrawalAmount(),1);

        // 收款记录对应可提现金额回退
        // 根据订单号查询核销记录
        CollectionWithdrawalAmountChangeRecordEntity tt = new CollectionWithdrawalAmountChangeRecordEntity();
        tt.setOrderNo(orderNo);
        tt.setChangeType(2);
        List<CollectionWithdrawalAmountChangeRecordEntity> changeList = this.collectionWithdrawalAmountChangeRecordService.select(tt);
        changeList.stream().forEach(changeRecord ->{
            // 查询相应收款记录
            CorporateAccountCollectionRecordEntity cacre = new CorporateAccountCollectionRecordEntity();
            cacre.setBankCollectionRecordNo(changeRecord.getBankCollectionRecordNo());
            CorporateAccountCollectionRecordEntity collectionRecord = this.selectOne(cacre);

            // 添加变动记录
            CollectionWithdrawalAmountChangeRecordEntity changeRecordEntity = new CollectionWithdrawalAmountChangeRecordEntity();
            BeanUtils.copyProperties(changeRecord,changeRecordEntity);
            changeRecordEntity.setId(null);
            changeRecordEntity.setChangeType(3);// 金额退还
            changeRecordEntity.setBeforeAmount(collectionRecord.getRemainingWithdrawalAmount());
            changeRecordEntity.setAfterAmount(collectionRecord.getRemainingWithdrawalAmount() + changeRecord.getChangeAmount());
            changeRecordEntity.setAddTime(new Date());
            changeRecordEntity.setAddUser("system");
            changeRecordEntity.setRemark("核销额度退回");
            this.collectionWithdrawalAmountChangeRecordService.insertSelective(changeRecordEntity);

            // 回退可提现金额
            this.updateRemainingWithdrawAmount(collectionRecord.getId(),changeRecord.getArriveAmount(),1);
        });

        // 更新订单状态和外部订单号
        this.orderService.updateOrderStatusAndExternalOrderNo(externalOrderNo,orderNo, RACWStatusEnum.PAY_FAILURE.getValue());
        // 更新流水状态
        PayWaterEntity payWater = new PayWaterEntity();
        payWater.setOrderNo(orderNo);
        payWater.setUpdateTime(new Date());
        payWater.setUpdateUser("admin");
        payWater.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
        payWater.setUpStatusCode(upStatusCode);
        payWater.setExternalOrderNo(externalOrderNo);
        payWater.setUpResultMsg(upResultMsg);
        this.payWaterService.updatePayStatus(payWater);
    }

    @Override
    public void updateRemainingWithdrawAmount(Long recordId, Long withdrawalAmount, int flag) throws BusinessException {
        log.info("更新剩余可提现额度:{},{},{}",recordId,withdrawalAmount,flag);
        if(null == withdrawalAmount){
            withdrawalAmount = 0L;
        }
        this.mapper.updateRemainingWithdrawAmount(recordId,withdrawalAmount,flag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void companyCorporateAccountVerification(CompanyCorporateAccountVerificationQuery query,String account) {
        CorporateAccountCollectionRecordEntity corporateAccountCollectionRecordEntity=mapper.selectByPrimaryKey(query.getId());
        if(corporateAccountCollectionRecordEntity==null){
            throw new BusinessException("对公户不存在");
        }
        InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(query.getInvoiceOrderNo());
        if(invoiceOrderEntity == null){
            throw new BusinessException("未找到需要核销开票订单");
        }
        if(invoiceOrderEntity.getRemainingWithdrawalAmount() == null || invoiceOrderEntity.getRemainingWithdrawalAmount()<0
                || invoiceOrderEntity.getRemainingWithdrawalAmount() < query.getAmount()){
            throw new BusinessException("开票订单剩余提现额度不足不能额度核销");
        }
        if(invoiceOrderEntity.getInvoiceMark()==1 || invoiceOrderEntity.getCompleteTime()==null){
            throw new BusinessException("开票订单未完成或已作废，不能进行额度核销");
        }
        if(corporateAccountCollectionRecordEntity.getCrHpnam()==null||corporateAccountCollectionRecordEntity.getCrHpnam()<=0){
            throw new BusinessException("收款流水不足不能额度核销");
        }
        if(corporateAccountCollectionRecordEntity.getRemainingWithdrawalAmount()==null||corporateAccountCollectionRecordEntity.getRemainingWithdrawalAmount()-query.getAmount()<0){
            throw new BusinessException("剩余提现额度不足不能额度核销");
        }

        Long befor=corporateAccountCollectionRecordEntity.getRemainingWithdrawalAmount();
        corporateAccountCollectionRecordEntity.setRemainingWithdrawalAmount(corporateAccountCollectionRecordEntity.getRemainingWithdrawalAmount()-query.getAmount());
        corporateAccountCollectionRecordEntity.setRemark(query.getRemark());
        mapper.updateByPrimaryKey(corporateAccountCollectionRecordEntity);
        //增加核销记录
        collectionWithdrawalAmountChangeRecordService.addCollectionWithdrawalAmountChangeRecordEntity(corporateAccountCollectionRecordEntity.getCorporateAccountId(),corporateAccountCollectionRecordEntity.getBankCollectionRecordNo(),corporateAccountCollectionRecordEntity.getHpnAmt(),befor,query.getAmount(),null,account,query.getRemark(),query.getInvoiceOrderNo());
        // 开票订单可提现额度扣减
        this.invoiceOrderService.updateRemainingWithdrawAmount(invoiceOrderEntity.getOrderNo(),query.getAmount(),0);
        OrderEntity orderEntity = orderService.queryByOrderNo(query.getInvoiceOrderNo());
        invoiceOrderEntity = invoiceOrderService.queryByOrderNo(query.getInvoiceOrderNo());
        if(invoiceOrderEntity!=null && orderEntity!=null) {
            this.invoiceOrderService.editAndSaveHistory(invoiceOrderEntity,orderEntity.getOrderStatus(),account,"对公户提现额度核销");
        }
    }

    @Override
    public List<CorporateAccountCollectionRecordVO> queryCorpAccountCollectionRecords(CorporateAccountCollectionRecordQuery query) {
        return  this.mapper.queryCorpAccountCollectionRecords(query);
    }

    @Override
    public List<CorporateAccountVOAdmin> getCorpAccountCollectionRecordsAdmin(CorporateAccountCollectionRecordQuery query) {
        return mapper.queryCorpAccountCollectionRecordsAdmin(query);
    }

    /**
     * @Description 实时获取当天账户明细信息并入库
     * @Author  Kaven
     * @Date   2020/9/7 16:33
     * @Param CorporateAccountCollectionRecordQuery
     * @Return
     * @Exception
    */
    private void fetchCardTransDetail(CorporateAccountCollectionRecordQuery query) {
        log.info("实时获取当天账户明细信息:{}", JSON.toJSONString(query));

        // 读取渠道代付相关配置 paramsType=14
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(query.getOemCode(),14);
        if(null == paramsEntity){
            throw new BusinessException("未配置渠道代付相关信息！");
        }
        CorporateAccountCollectionRecordQuery corporateAccountCollectionRecordQuery=new CorporateAccountCollectionRecordQuery();
        ObjectUtil.copyObject(query,corporateAccountCollectionRecordQuery);
        if(query.getIsSystem()!=null && ObjectUtils.equals(query.getIsSystem(),1)){

        }else{
            corporateAccountCollectionRecordQuery.setEndDate(null);
            corporateAccountCollectionRecordQuery.setStartDate(null);
        }
        JSONArray jsonArray = this.daifuApiService.queryCardTransDetail(paramsEntity,corporateAccountCollectionRecordQuery);
        if(null != jsonArray){
            List<CorporateAccountCollectionRecordEntity> list = buildCorpAccCollectionRcdParams(jsonArray);
            list.stream().forEach(entity -> {
                // 先查询是否存在相同记录
                CorporateAccountCollectionRecordEntity t = new CorporateAccountCollectionRecordEntity();
                t.setBankCollectionRecordNo(entity.getBankCollectionRecordNo());// 根据银行唯一编码（交易流水号）查询
                List<CorporateAccountCollectionRecordEntity> records = this.select(t);
                if(CollectionUtil.isEmpty(records)) {
                    entity.setCorporateAccountId(query.getCorporateAccountId());// 对公户ID
                    this.mapper.insertSelective(entity);
                }
            });
        }
        log.info("当天实时对公户账户明细数据同步完成");
    }

    /**
     * @Description 参数转换
     * @Author  Kaven
     * @Date   2020/9/7 17:28
     * @Param   JSONArray
     * @Return  List<CorporateAccountCollectionRecordEntity>
     * @Exception
    */
    private List<CorporateAccountCollectionRecordEntity> buildCorpAccCollectionRcdParams(JSONArray jsonArray) {
        List<CorporateAccountCollectionRecordEntity> list = Lists.newArrayList();
        // 遍历
        for(int i = 0; i < jsonArray.size(); i++){
            JSONObject obj = jsonArray.getJSONObject(i);
            CorporateAccountCollectionRecordEntity entity = new CorporateAccountCollectionRecordEntity();
            entity.setOtherPartyBankAccount(obj.getString("accName"));
            entity.setOtherPartyBankName(obj.getString("accBankName"));
            entity.setOtherPartyBankNumber(obj.getString("accNo"));
            entity.setAcba(obj.getLong("balance"));
            entity.setBankCollectionRecordNo(obj.getString("transNo"));// 交易号
            entity.setTradingTime(DateUtil.parseDate(obj.getString("transTime"),"yyyyMMddHHmmss"));// 交易时间
            entity.setHpnAmt(Long.valueOf(obj.getString("transAmt")));// 交易金额
            Integer dbtCrDrcCd = Integer.parseInt(obj.getString("dbtCrDrcCd"));
            entity.setTradingStatus(dbtCrDrcCd);// 出入账标识 1-支出 2-收入
            if(dbtCrDrcCd == 1){
                entity.setDhamt(obj.getLong("transAmt"));
            }else if(dbtCrDrcCd == 2){
                entity.setCrHpnam(obj.getLong("transAmt"));
            }
            entity.setSmy(obj.getString("message"));
            entity.setTradingRemark(obj.getString("remark"));// 交易备注
            entity.setAddTime(new Date());
            entity.setAddUser("admin");
            entity.setRemainingWithdrawalAmount(entity.getHpnAmt());
            list.add(entity);
        }

        return list;
    }
}

