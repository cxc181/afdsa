package com.yuqian.itax.xxljob.jobhandler.corporateAccount;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountCollectionRecordEntity;
import com.yuqian.itax.corporateaccount.service.CorporateAccountCollectionRecordService;
import com.yuqian.itax.user.entity.query.CorporateAccountCollectionRecordQuery;
import com.yuqian.itax.user.entity.vo.CompanyCorporateAccountHandlerVO;
import com.yuqian.itax.user.service.CompanyCorporateAccountService;
import com.yuqian.itax.user.service.DaifuApiService;
import com.yuqian.itax.util.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 获取全部对公户前一天的收款流水记录
 *
 * @Author yejian
 * @Date 2020/09/10 14:28
 */
@JobHandler(value = "corporateAccountCollectionHandler")
@Component
public class CorporateAccountCollectionHandler extends IJobHandler {

    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private CompanyCorporateAccountService companyCorporateAccountService;
    @Autowired
    private DaifuApiService daifuApiService;
    @Autowired
    private CorporateAccountCollectionRecordService corporateAccountCollectionRecordService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long t1 = System.currentTimeMillis(); // 代码执行前时间
        XxlJobLogger.log("=========获取全部对公户前一天的收款流水记录任务启动");

        // 查询企业对公户列表
        List<CompanyCorporateAccountHandlerVO> accountList = companyCorporateAccountService.queryAccountHandlerList();
        if (accountList == null || accountList.size() < 1) {
            return SUCCESS;
        }
        String startDate = "";
        if(StringUtils.isNotBlank(param)){
            JSONObject jsonObject = JSONObject.parseObject(param);
            startDate = jsonObject.getString("startDate");
        }

        // 循环企业对公户列表
        for(CompanyCorporateAccountHandlerVO account:accountList) {
            // 读取代付API相关配置 paramsType=14
            OemParamsEntity paramsEntity = this.oemParamsService.getParams(account.getOemCode(), 14);
            if (null == paramsEntity) {
                XxlJobLogger.log("未配置代付API相关信息");
            }

            // 代付账户明细查询
            CorporateAccountCollectionRecordQuery query = new CorporateAccountCollectionRecordQuery();
            if(StringUtils.isNotBlank(startDate)) {
                query.setStartDate(startDate+" 00:00:00"); //根据参数获取
            }else{
                query.setStartDate(DateUtil.stampToDate(DateUtil.getYesterdayStartTime(), DateUtil.TIMESTAMP_PATTERN)); //昨天开始时间
            }
            query.setEndDate(DateUtil.stampToDate(DateUtil.getYesterdayEndTime(), DateUtil.TIMESTAMP_PATTERN)); //昨天结束时间
            query.setTxnStffId(account.getVoucherMemberCode());
            query.setDraweeAccountNo(account.getCorporateAccount());
            JSONArray jsonArray = daifuApiService.queryCardTransDetail(paramsEntity, query);
            if (null != jsonArray) {
                List<CorporateAccountCollectionRecordEntity> recordList = buildCorpAccCollectionRcdParams(jsonArray);
                recordList.stream().forEach(entity -> {
                    // 先查询是否存在相同记录
                    CorporateAccountCollectionRecordEntity record = new CorporateAccountCollectionRecordEntity();
                    record.setBankCollectionRecordNo(entity.getBankCollectionRecordNo());// 根据银行唯一编码（交易流水号）查询
                    List<CorporateAccountCollectionRecordEntity> records = corporateAccountCollectionRecordService.select(record);
                    if (CollectionUtil.isEmpty(records)) {
                        entity.setCorporateAccountId(account.getId());// 对公户ID
                        corporateAccountCollectionRecordService.insertSelective(entity);
                    }
                });
            }
        }
        XxlJobLogger.log("=========获取全部对公户前一天的收款流水记录任务结束");
        long t2 = System.currentTimeMillis(); // 代码执行后时间
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(t2 - t1);
        XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
        return SUCCESS;
    }

    /**
     * @Description 参数转换
     * @Author yejian
     * @Date 2020/9/11 09:08
     * @Param JSONArray
     * @Return List<CorporateAccountCollectionRecordEntity>
     * @Exception
     */
    private List<CorporateAccountCollectionRecordEntity> buildCorpAccCollectionRcdParams(JSONArray jsonArray) {
        List<CorporateAccountCollectionRecordEntity> list = Lists.newArrayList();
        // 遍历
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            CorporateAccountCollectionRecordEntity entity = new CorporateAccountCollectionRecordEntity();
            entity.setBankCollectionRecordNo(obj.getString("transNo"));// 交易号
            entity.setSmy(obj.getString("message"));
            Integer dbtCrDrcCd = Integer.parseInt(obj.getString("dbtCrDrcCd"));
            if (dbtCrDrcCd == 1) {
                entity.setDhamt(obj.getLong("transAmt"));
            } else if (dbtCrDrcCd == 2) {
                entity.setCrHpnam(obj.getLong("transAmt"));
            }
            entity.setHpnAmt(Long.valueOf(obj.getString("transAmt")));// 交易金额
            entity.setAcba(obj.getLong("balance"));
            entity.setTradingTime(DateUtil.parseDate(obj.getString("transTime"),"yyyyMMddHHmmss"));// 交易时间
            entity.setOtherPartyBankAccount(obj.getString("accName"));
            entity.setOtherPartyBankName(obj.getString("accBankName"));
            entity.setOtherPartyBankNumber(obj.getString("accNo"));
            entity.setTradingStatus(dbtCrDrcCd);// 出入账标识 1-支出 2-收入
            entity.setTradingRemark(obj.getString("remark"));// 交易备注
            if (entity.getTradingStatus().intValue() == 2) { // 收支类型为2-收入时，剩余可提现额度赋值
                entity.setRemainingWithdrawalAmount(entity.getHpnAmt());
            }
            entity.setAddTime(new Date());
            entity.setAddUser("admin-xxljob");
            entity.setRemark("");
            list.add(entity);
        }
        return list;
    }
}
