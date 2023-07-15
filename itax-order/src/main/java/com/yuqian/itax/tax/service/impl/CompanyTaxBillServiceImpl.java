package com.yuqian.itax.tax.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.message.service.NoticeManageService;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.vo.CountPeriodInvoiceAmountVO;
import com.yuqian.itax.order.entity.vo.PeriodPaidTaxVo;
import com.yuqian.itax.order.enums.OperTypeEnum;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.enums.WalletTypeEnum;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.park.entity.ParkEndtimeConfigEntity;
import com.yuqian.itax.park.enums.IncomeLevyTypeEnum;
import com.yuqian.itax.park.service.ParkEndtimeConfigService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.enums.PayChannelEnum;
import com.yuqian.itax.pay.enums.PayWaterStatusEnum;
import com.yuqian.itax.pay.enums.PayWaterTypeEnum;
import com.yuqian.itax.pay.enums.PayWayEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.tax.dao.CompanyTaxBillMapper;
import com.yuqian.itax.tax.entity.*;
import com.yuqian.itax.tax.entity.dto.FillCostDTO;
import com.yuqian.itax.tax.entity.dto.TaxAuditDTO;
import com.yuqian.itax.tax.entity.query.*;
import com.yuqian.itax.tax.entity.vo.*;
import com.yuqian.itax.tax.enums.TaxBillStatusEnum;
import com.yuqian.itax.tax.service.*;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.util.util.UniqueNumGenerator;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service("companyTaxBillService")
public class CompanyTaxBillServiceImpl extends BaseServiceImpl<CompanyTaxBillEntity,CompanyTaxBillMapper> implements CompanyTaxBillService {
    @Autowired
    DictionaryService dictionaryService;
    @Autowired
    MemberCompanyService memberCompanyService;
    @Autowired
    SmsService smsService;
    @Autowired
    InvoiceOrderService invoiceOrderService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    ParkTaxBillService parkTaxBillService;
    @Autowired
    ParkTaxBillChangeService parkTaxBillChangeService;
    @Autowired
    private ParkEndtimeConfigService parkEndtimeConfigService;
    @Autowired
    private PayWaterService payWaterService;
    @Autowired
    NoticeManageService manageService;
    @Autowired
    private UserCapitalAccountService userCapitalAccountService;
    @Autowired
    private OemService oemService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageNoticeService messageNoticeService;
    @Autowired
    private CompanyTaxCostItemService companyTaxCostItemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CompanyTaxBillServiceImpl companyTaxBillService;
    @Autowired
    private CompanyTaxBillChangeService companyTaxBillChangeService;

    @Override
    public List<CompanyTaxBillXXJOBVO> queryCompanyTaxBillByTime(CompanyTaxBillQuery query) {
        return mapper.queryCompanyTaxBillByTime(query);
    }

    @Override
    public void insertCompanyTaxBillByTaxBillYearAndTaxBillSeasonal(CompanyTaxBillQuery query) {
        mapper.insertCompanyTaxBillByTaxBillYearAndTaxBillSeasonal(query);
    }

    @Override
    public PageInfo<CompanyTaxBillListVO> listPageCompanyTaxBill(CompanyTaxBillQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<CompanyTaxBillListVO> vos = mapper.listCompanyTaxBill(query);
        if (CollectionUtil.isEmpty(vos)) {
            return new PageInfo(vos);
        }

        for (CompanyTaxBillListVO vo : vos) {
            if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(vo.getIncomeLevyType())) {
                CompanyTaxBillVO taxBillVO = new CompanyTaxBillVO();
                taxBillVO.setCompanyTaxBillId(Long.valueOf(vo.getCompanyTaxBillId()));
                if (TaxBillStatusEnum.TO_FINANCIAL_AUDIT.getValue().equals(vo.getTaxBillStatus())
                        || TaxBillStatusEnum.AUDIT_FAILED.getValue().equals(vo.getTaxBillStatus())) {
                    taxBillVO.setIncomeTaxRefundAmount(0L);
                }
                companyTaxBillService.detailOfAuditCollection(taxBillVO);
                vo.setShouldTaxMoney(taxBillVO.getShouldTaxMoney());
                vo.setAlreadyTaxMoney(taxBillVO.getAlreadyTaxMoney());
                vo.setSupplementTaxMoney(taxBillVO.getSupplementTaxMoney());
                vo.setRecoverableTaxMoney(taxBillVO.getRecoverableTaxMoney());
            }
        }
        // 查账征收税单未找到相应税期截止日的截止日取最新的
        List<CompanyTaxBillListVO> collect = vos.stream().filter(x -> Objects.equals(1, x.getIncomeLevyType()) && null == x.getOverTimeDesc()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(collect)) {
            return new PageInfo(vos);
        }
        for (CompanyTaxBillListVO vo : vos) {
            if (null == vo.getOverTimeDesc() && Objects.equals(1, vo.getIncomeLevyType())) {
                // 查询截止日
                ParkEndtimeConfigEntity parkEndtimeConfigEntity = parkEndtimeConfigService.queryByOperTypeAndParkIdAndYearAndQuarter(vo.getParkId(), OperTypeEnum.COST.getValue(), vo.getTaxBillYear(), vo.getTaxBillSeasonal());
                if (null == parkEndtimeConfigEntity) {
                    continue ;
                }
                Date endTime = parkEndtimeConfigEntity.getEndTime();
                Date date = DateUtil.parseDefaultDate(DateUtil.formatDefaultDate(endTime));
                // 截止日描述
                int i = DateUtil.diffDate(DateUtil.parseDefaultDate(DateUtil.formatDefaultDate(new Date())), date);
                if (i > 0) {
                    vo.setOverTimeDesc("已超时" + i + "天");
                } else {
                    vo.setOverTimeDesc("截止日：" + DateUtil.getMonth(endTime) + "月" + DateUtil.getDay(endTime) + "号");
                }
            }
        }
        return new PageInfo(vos);
    }

    @Override
    public List<CompanyTaxBillListVO> listCompanyTaxBill(CompanyTaxBillQuery query) {
        return mapper.listCompanyTaxBill(query);
    }

    @Override
    public List<CompanyTaxBillEntity> queryCompanyTaxBillByParkTaxBillId(Long parkTaxBillId) {
        return mapper.queryCompanyTaxBillByParkTaxBillId(parkTaxBillId);
    }

    @Override
    public PageInfo<CompanyTaxBillListVOAdmin> queryCompanyTaxBillPageInfo(CompanyTaxBillQueryAdmin query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(mapper.queryCompanyTaxBillList(query));
    }

    @Override
    public List<CompanyTaxBillListVOAdmin> queryCompanyTaxBillList(CompanyTaxBillQueryAdmin query) {
        return mapper.queryCompanyTaxBillList(query);
    }

    @Override
    public List<DownloadCompanyTaxBillVOAdmin> queryDownloadCompanyTaxBillList(CompanyTaxBillQueryAdmin query) {
        return mapper.queryDownloadCompanyTaxBillList(query);
    }

    @Override
    public List<DownloadCompanyTaxBillByAccountsVO> queryDownloadCompanyTaxBillListByAccounts(CompanyTaxBillQueryAdmin query) {
        return mapper.queryDownloadCompanyTaxBillListByAccounts(query);
    }

    @Override
    public List<TaxBillInfoToMemberVO> queryCompanyTaxBillInfoToMember(TaxBillInfoToMemberQuery query) {
        return mapper.queryCompanyTaxBillInfoToMember(query);
    }

    @Override
    public List<CompanyTaxBillEntity> queryCompanyTaxByOverTime(Integer overTime, Long companyId) {
        return mapper.queryCompanyTaxByOverTime(overTime, companyId);
    }

    @Override
    public List<CompanyTaxBillEntity> queryCompanyTaxByCompanyId(Long companyId) {
        return mapper.queryCompanyTaxByCompanyId(companyId);
    }

    @Override
    public Long queryShouldCompanyByParkTaxBillId(Long parkTaxBillId) {
        return mapper.queryShouldCompanyByParkTaxBillId(parkTaxBillId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void xxjob() {
        String overtime = dictionaryService.getValueByCode("tax_bill_overtime");
        List<CompanyTaxBillEntity> list =queryCompanyTaxByOverTime(Integer.parseInt(overtime), null);
        List<Map<String,Object>> lists=new ArrayList<>();
        //查询出来待补税得企业
        Map<String,Object> map=null;
        for (CompanyTaxBillEntity c: list) {
            MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(c.getCompanyId());
            map=new HashMap<>();
            map.put("memberId",memberCompanyEntity.getMemberId());
            if(!lists.contains(map)){
                lists.add(map);
            }
            //修改发送状态
            c.setOverTimeIsSms(1);
            mapper.updateByPrimaryKey(c);
        }
        //安排
        for(Map<String,Object> vo :lists){
            TaxBillInfoToMemberQuery query=new TaxBillInfoToMemberQuery();
            query.setMemberId((Long)vo.get("memberId"));
            List<TaxBillInfoToMemberVO> voList=queryCompanyTaxBillInfoToMember(query);
            Map<String, Object> smsMap = new HashMap<>();
            smsMap.put("number", voList.get(0).getSupplement());
            //发短信
            smsService.sendTemplateSms(voList.get(0).getMemberPhone(), voList.get(0).getOemCode(), VerifyCodeTypeEnum.TAX_BILL_SUPPLEMENT.getValue(), smsMap, 1);
        }
    }

    @Override
    public Map<String, Object> queryCompanyTaxBillTotalVatIiTfJByTime(CompanyTaxBillQueryAdmin query) {
        return mapper.queryCompanyTaxBillTotalVatIiTfJByTime(query);
    }

    @Override
    public void updateCompanyTaxBillByParkId(Long parkId, Long parkTaxBillId,  int taxBillSeasonal,  int taxBillYear) {
        mapper.updateCompanyTaxBillByParkId(parkId,parkTaxBillId,taxBillSeasonal,taxBillYear);
    }

    @Override
    public CompanyTaxBillEntity queryCompanyTaxBillByEin(String ein,int taxBillSeasonal,int taxBillYear, Long companyId) {
        return mapper.queryCompanyTaxBillByEin(ein,taxBillSeasonal,taxBillYear,companyId);
    }

    @Override
    public CompanyTaxBillEntity queryCompanyTaxBillByEinAndParkTaxBillId(String ein, Long parkTaxBillId) {
        return mapper.queryCompanyTaxBillByEinAndParkTaxBillId(ein,parkTaxBillId);
    }

    @Override
    public PageInfo<CompanyTaxBillPrepareListVO> listPagePrepareCompanyTaxBill(PrepareCompanyTaxBillQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        ArrayList<CompanyTaxBillPrepareListVO> list = Lists.newArrayList();
        // 查询用户
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(query.getMemberId())).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        // 查询用户所有企业id
        List<MemberCompanyEntity> memberCompanyEntities = null;
        if(query.getCompanyId()!=null){
            memberCompanyEntities = new ArrayList<>();
            MemberCompanyEntity company = memberCompanyService.findById(query.getCompanyId());
            if(company != null) {
                memberCompanyEntities.add(company);
            }
        }else {
            memberCompanyEntities = memberCompanyService.allMemberCompanyList(member.getId(), member.getOemCode());
        }
        // 查询企业预税单列表
        for (MemberCompanyEntity company : memberCompanyEntities) {
            CompanyTaxBillPrepareListVO vo = new CompanyTaxBillPrepareListVO();
            TaxCalculationVO entity = new TaxCalculationVO();
            entity.setCompanyId(company.getId());
            entity.setType(3);
            entity.setOrderNo(null);
            entity.setVatRate(null);
            entity.setSeason(0);
            entity.setYear(0);
            entity.setCalculationType(0);
            Map<String, Object> taxMap = invoiceOrderService.taxCalculation(entity);
            BeanUtil.copyProperties(taxMap, vo);
            vo.setCompanyId(company.getId());
            vo.setCompanyName(company.getCompanyName());
            vo.setOperatorName(company.getOperatorName());
            list.add(vo);
        }
        PageInfo<CompanyTaxBillPrepareListVO> listVOPageInfo = new PageInfo<>(list);

        return listVOPageInfo;
    }

    @Override
    public CompanyTaxBillPrepareVO prePareDetail(Long companyId, Long memberId) {
        CompanyTaxBillPrepareVO vo = new CompanyTaxBillPrepareVO();
        // 查询用户
        Optional.ofNullable(memberAccountService.findById(memberId)).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        // 查询企业预税单详情
        TaxCalculationVO entity = new TaxCalculationVO();
        entity.setCompanyId(companyId);
        entity.setType(3);
        entity.setOrderNo(null);
        entity.setVatRate(null);
        entity.setSeason(0);
        entity.setYear(0);
        entity.setCalculationType(0);
        Map<String, Object> taxMap = invoiceOrderService.taxCalculation(entity);
        BeanUtil.copyProperties(taxMap, vo);

        return vo;
    }

    @Override
    public PeriodPaidTaxVo queryPayableTaxFee(Long companyId, Integer year, Integer seasonal) {
        return mapper.queryPayableTaxFee(companyId,year,seasonal);
    }

    @Override
    @Transactional
    public Map<String,Object>  handleBill(List<TaxDeclarationVO> excelList,Long parkTaxBillId,String userName) {
        List<TaxDeclarationVO> failList = new ArrayList<>();
        List<CompanyTaxBillEntity> updateList = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();
        TParkTaxBillEntity parkTaxBillEntity = parkTaxBillService.findById(parkTaxBillId);
        String max = dictionaryService.getValueByCode("without_ticket_amount_max");
        long amountMax = Long.parseLong(max);
        final BigDecimal FIVE_MILLION = new BigDecimal("5000000.00"); // 500W（分）
        Pattern pattern=Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");

        // 获取应缴允许误差
        BigDecimal errorValue = BigDecimal.ZERO;
        String valueByCode = dictionaryService.getValueByCode("payable_allow_error_value");
        if (StringUtil.isNotBlank(valueByCode)) {
            errorValue = new BigDecimal(valueByCode);
        }

        for(int i=0;i<excelList.size();i++){
            // 校验数据不能为空
            if (StringUtil.isEmpty(excelList.get(i).getCompanyName()) || StringUtil.isEmpty(excelList.get(i).getEin())
                    || null == excelList.get(i).getVatShouldTaxMoney() || null == excelList.get(i).getAdditionalShouldTaxMoney()
                    || null == excelList.get(i).getIncomeShouldTaxMoney()){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("必填项缺失");
                failList.add(excelList.get(i));
                continue;
            }
            Matcher match;
            Long withoutTicketAmount = null;
            Long incomeTaxRefundAmount = null;
            if (excelList.get(i).getVatShouldTaxMoney() != null){
                if (excelList.get(i).getVatShouldTaxMoney().compareTo(BigDecimal.ZERO) < 0
                        || excelList.get(i).getVatShouldTaxMoney().compareTo(FIVE_MILLION) > 0) {
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("本季应缴增值税的范围是0-500W");
                    failList.add(excelList.get(i));
                    continue;
                }
                match=pattern.matcher(excelList.get(i).getVatShouldTaxMoney().toString());
                if (!match.matches()){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("本季应缴增值税只支持两位小数");
                    failList.add(excelList.get(i));
                    continue;
                }
            }
            if (excelList.get(i).getAdditionalShouldTaxMoney() != null){
                if (excelList.get(i).getAdditionalShouldTaxMoney().compareTo(BigDecimal.ZERO) < 0
                        || excelList.get(i).getAdditionalShouldTaxMoney().compareTo(FIVE_MILLION) > 0) {
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("本季应缴附加税的范围是0-500W");
                    failList.add(excelList.get(i));
                    continue;
                }
                match=pattern.matcher(excelList.get(i).getAdditionalShouldTaxMoney().toString());
                if (!match.matches()){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("本季应缴附加税只支持两位小数");
                    failList.add(excelList.get(i));
                    continue;
                }
            }
            if (excelList.get(i).getIncomeShouldTaxMoney() != null){
                if (excelList.get(i).getIncomeShouldTaxMoney().compareTo(BigDecimal.ZERO) < 0
                        || excelList.get(i).getIncomeShouldTaxMoney().compareTo(FIVE_MILLION) > 0) {
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("本年应缴所得税的范围是0-500W");
                    failList.add(excelList.get(i));
                    continue;
                }
                match=pattern.matcher(excelList.get(i).getIncomeShouldTaxMoney().toString());
                if (!match.matches()){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("本年应缴所得税只支持两位小数");
                    failList.add(excelList.get(i));
                    continue;
                }
            }
            if (excelList.get(i).getWithoutTicketAmount() != null){
                withoutTicketAmount = excelList.get(i).getWithoutTicketAmount().multiply(new BigDecimal(100)).longValue();
                if (withoutTicketAmount > amountMax){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("无票收入金额最大金额为5000000");
                    failList.add(excelList.get(i));
                    continue;
                }
                match=pattern.matcher(excelList.get(i).getWithoutTicketAmount().toString());
                if (!match.matches()){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("无票收入金额只支持两位小数");
                    failList.add(excelList.get(i));
                    continue;
                }
            }
           if (excelList.get(i).getIncomeTaxRefundAmount() != null){
               incomeTaxRefundAmount = excelList.get(i).getIncomeTaxRefundAmount().multiply(new BigDecimal(100)).longValue();
               if (incomeTaxRefundAmount > amountMax){
                   excelList.get(i).setFlag(false);
                   excelList.get(i).setReg("个税可退税额最大金额为5000000");
                   failList.add(excelList.get(i));
                   continue;
               }
               match=pattern.matcher(excelList.get(i).getIncomeTaxRefundAmount().toString());
               if (!match.matches()){
                   excelList.get(i).setFlag(false);
                   excelList.get(i).setReg("个税可退税额只支持两位小数");
                   failList.add(excelList.get(i));
                   continue;
               }
           }
            if (StringUtil.isNotBlank(excelList.get(i).getRemake()) && excelList.get(i).getRemake().length()>50){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("备注不能超过50个字符");
                failList.add(excelList.get(i));
                continue;
            }
            List<MemberCompanyEntity> companylist =  memberCompanyService.queryMemberCompanyByEinStatusNotCancellation(excelList.get(i).getEin());
            if (CollectionUtil.isEmpty(companylist)){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("该企业不存在");
                failList.add(excelList.get(i));
                continue;
            }
            //  该企业存在未完成申报的历史税期的税单，则确认失败
            PendingTaxBillQuery query = new PendingTaxBillQuery();
            query.setEin(companylist.get(0).getEin());
            query.setCompanyId(companylist.get(0).getId());
            query.setStatusRange(2);
            List<PendingTaxBillVO> taxBillVOList = pendingTaxBill(query);
            if (CollectionUtil.isNotEmpty(taxBillVOList)){
                for (PendingTaxBillVO vo:taxBillVOList){
                    if(isBeforeByBill(vo,parkTaxBillEntity)){
                        excelList.get(i).setFlag(false);
                        excelList.get(i).setReg("该企业存在未完成申报的历史税期的税单");
                        failList.add(excelList.get(i));
                        break;
                    }
                }
                if (!excelList.get(i).isFlag()){
                    continue;
                }
            }
            CompanyTaxBillEntity companyTaxBillEntity = queryCompanyTaxBillByEin(excelList.get(i).getEin(),parkTaxBillEntity.getTaxBillSeasonal(),parkTaxBillEntity.getTaxBillYear(),null);
            if (companyTaxBillEntity == null){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("企业税单不存在");
                failList.add(excelList.get(i));
                continue;
            }
            if (!Objects.equals(parkTaxBillEntity.getParkId(), companyTaxBillEntity.getParkId())) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("该企业税单不属于该园区");
                failList.add(excelList.get(i));
                continue;
            }
            if (!companyTaxBillEntity.getTaxBillStatus().equals(TaxBillStatusEnum.TO_BE_DECLARE.getValue())){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("税单状态不是待申报");
                failList.add(excelList.get(i));
                continue;
            }
            if (companyTaxBillEntity.getTaxBillStatus().equals(9)){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("该企业税单已作废");
                failList.add(excelList.get(i));
                continue;
            }
            if (incomeTaxRefundAmount != null && incomeTaxRefundAmount > companyTaxBillEntity.getIncomeTaxYearFreezeAmount()){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("个税可退税额不能超过实际应退金额");
                failList.add(excelList.get(i));
                continue;
            }
            if ((BigDecimal.valueOf(companyTaxBillEntity.getVatShouldTaxMoney()).divide(new BigDecimal("100"))
                    .compareTo(excelList.get(i).getVatShouldTaxMoney())) < 0
                    || ((BigDecimal.valueOf(companyTaxBillEntity.getVatShouldTaxMoney()).divide(new BigDecimal("100"))
                        .subtract(excelList.get(i).getVatShouldTaxMoney()))
                        .compareTo(errorValue) > 0)) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("本季应缴增值税与税单数据不一致，系统计算值为：" + BigDecimal.valueOf(companyTaxBillEntity.getVatShouldTaxMoney()).divide(new BigDecimal("100")));
                failList.add(excelList.get(i));
                continue;
            }
            if ((BigDecimal.valueOf(companyTaxBillEntity.getAdditionalShouldTaxMoney()).divide(new BigDecimal("100"))
                    .compareTo(excelList.get(i).getAdditionalShouldTaxMoney())) < 0
                    || ((BigDecimal.valueOf(companyTaxBillEntity.getAdditionalShouldTaxMoney()).divide(new BigDecimal("100"))
                        .subtract(excelList.get(i).getAdditionalShouldTaxMoney()))
                        .compareTo(errorValue) > 0)) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("本季应缴附加税与税单数据不一致，系统计算值为：" + BigDecimal.valueOf(companyTaxBillEntity.getAdditionalShouldTaxMoney()).divide(new BigDecimal("100")));
                failList.add(excelList.get(i));
                continue;
            }
            if ((BigDecimal.valueOf(companyTaxBillEntity.getYearPayableIncomeTax()).divide(new BigDecimal("100"))
                    .compareTo(excelList.get(i).getIncomeShouldTaxMoney())) < 0
                    || ((BigDecimal.valueOf(companyTaxBillEntity.getYearPayableIncomeTax()).divide(new BigDecimal("100"))
                        .subtract(excelList.get(i).getIncomeShouldTaxMoney()))
                        .compareTo(errorValue) > 0)) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("本年应缴所得税与税单数据不一致，系统计算值为：" + BigDecimal.valueOf(companyTaxBillEntity.getYearPayableIncomeTax()).divide(new BigDecimal("100")));
                failList.add(excelList.get(i));
                continue;
            }

            //应缴税费（应缴增值税，所得税，附加税）
            CompanyTaxBillVO companyTaxBillVO = new CompanyTaxBillVO();
            companyTaxBillVO.setCompanyTaxBillId(companyTaxBillEntity.getId());
            this.detailOfAuditCollection(companyTaxBillVO);
            if (incomeTaxRefundAmount != null && incomeTaxRefundAmount > 0L && companyTaxBillVO.getSupplementTaxMoney() > 0L) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("用户已补税，不允许填写可退税额");
                failList.add(excelList.get(i));
                continue;
            }

            companyTaxBillVO.setIncomeTaxRefundAmount(incomeTaxRefundAmount);
            this.detailOfAuditCollection(companyTaxBillVO);
            if (companyTaxBillVO.getSupplementTaxMoney() > 0L) {
                companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TAX_PAID.getValue());
                companyTaxBillEntity.setCompleteTime(new Date());
            } else if (companyTaxBillVO.getRecoverableTaxMoney() > 0L) {
                companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TO_FINANCIAL_AUDIT.getValue());
            } else {
                companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.NORMAL.getValue());
                companyTaxBillEntity.setCompleteTime(new Date());
            }

            if (incomeTaxRefundAmount != null){
                companyTaxBillEntity.setIncomeTaxRefundAmount(incomeTaxRefundAmount);
                // 修改年冻结税额
                companyTaxBillEntity.setIncomeTaxYearFreezeAmount(companyTaxBillEntity.getIncomeTaxYearFreezeAmount() - incomeTaxRefundAmount);
            }
            companyTaxBillEntity.setTicketFreeIncomeAmount(withoutTicketAmount);
            if (StringUtil.isNotBlank(excelList.get(i).getRemake())){
                companyTaxBillEntity.setRemark(excelList.get(i).getRemake());
            }
            companyTaxBillEntity.setUpdateTime(new Date());
            companyTaxBillEntity.setUpdateUser(userName);
            updateList.add(companyTaxBillEntity);
        }
        if (CollectionUtil.isNotEmpty(updateList)){
            for (CompanyTaxBillEntity entity:updateList){
                this.editByIdSelective(entity);
                CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
                BeanUtils.copyProperties(entity,companyTaxBillChangeEntity);
                companyTaxBillChangeEntity.setId(null);
                companyTaxBillChangeEntity.setCompanyTaxBillId(entity.getId());
                companyTaxBillChangeEntity.setDescrip("确认报税，填入个税可退税额为" +
                        (null == entity.getIncomeTaxRefundAmount() ? 0 : new BigDecimal(entity.getIncomeTaxRefundAmount()).divide(new BigDecimal("100")))+ "元");
                companyTaxBillChangeEntity.setAddTime(new Date());
                companyTaxBillChangeEntity.setAddUser(entity.getUpdateUser());
                companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
            }
        }
        // 园区下面企业税单状态都为已完成 修改园区税单状态
        List<Long> companyTaxBillList = mapper.getCompanyTaxBillByTaxBillStatus(parkTaxBillId);
        if (CollectionUtil.isEmpty(companyTaxBillList)){
            TParkTaxBillEntity tParkTaxBillEntity = parkTaxBillService.findById(parkTaxBillId);
            if (tParkTaxBillEntity != null){
                tParkTaxBillEntity.setTaxBillStatus(9);
                tParkTaxBillEntity.setUpdateTime(new Date());
                tParkTaxBillEntity.setUpdateUser(userName);
                parkTaxBillService.editByIdSelective(tParkTaxBillEntity);
                ParkTaxBillChangeEntity changeEntity = new ParkTaxBillChangeEntity();
                BeanUtils.copyProperties(tParkTaxBillEntity,changeEntity);
                changeEntity.setId(null);
                changeEntity.setParkBillsId(tParkTaxBillEntity.getId());
                changeEntity.setAddTime(new Date());
                changeEntity.setAddUser(userName);
                parkTaxBillChangeService.insertSelective(changeEntity);
            }
        }
        //报税完成的发送短信与通知
        if (CollectionUtil.isNotEmpty(updateList)){
            Map<String,Object> flagMap = new HashMap<>();
            for (CompanyTaxBillEntity entity:updateList){
                // 待财务审核状态税单不发已报税通知
                if (TaxBillStatusEnum.TO_FINANCIAL_AUDIT.getValue().equals(entity.getTaxBillStatus())) {
                    continue;
                }
                // 本期开票金额为0的税单不发已报税通知
                if (entity.getInvoiceMoney() <= 0L) {
                    continue;
                }
                MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(entity.getCompanyId());
                OemEntity oemEntity = oemService.getOem(memberCompanyEntity.getOemCode());
                if (oemEntity.getIsSendAuditBillsMessage().equals(0)){
                    continue;
                }
                MemberAccountEntity memberAccountEntity = memberAccountService.findById(memberCompanyEntity.getMemberId());
                //  每个电话号码只发一条短信
                if(flagMap.containsKey(memberAccountEntity.getMemberPhone())){
                    continue;
                }else{
                    flagMap.put(memberAccountEntity.getMemberPhone(),"1");
                }
                List<Map<String,Object>> mapList=new ArrayList<>();
                Map<String, Object> map = new HashMap<>();
                map.put("phone", memberAccountEntity.getMemberPhone());
                map.put("userId", memberAccountEntity.getId());
                mapList.add(map);
                String taxPeriod = taxPeriod(entity.getTaxBillYear(),entity.getTaxBillSeasonal());
                Map<String, Object> smsMap = new HashMap<>();
                smsMap.put("taxBillSeasonal",taxPeriod);
                smsMap.put("companyName",memberCompanyEntity.getCompanyName());
                //发短信
                smsService.sendTemplateSms(memberAccountEntity.getMemberPhone(), memberAccountEntity.getOemCode(), VerifyCodeTypeEnum.COMPLETE_TAX_RETURN.getValue(), smsMap, 2);
                //发送消息
                String noticeTemp=dictionaryService.getValueByCode("declaration_completed_title");
                noticeTemp=noticeTemp.replace("#taxBillSeasonal#",taxPeriod).replace("#companyName#",String.valueOf(memberCompanyEntity.getCompanyName()));
                MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
                messageNoticeEntity.setOemCode(memberCompanyEntity.getOemCode());
                messageNoticeEntity.setNoticeType(2);
                messageNoticeEntity.setIsAlert(0);
                messageNoticeEntity.setNoticePosition("1,2");
                messageNoticeEntity.setOpenMode(3);
                messageNoticeEntity.setBusinessType(13);
                messageNoticeEntity.setNoticeTitle("申报完成通知");
                messageNoticeEntity.setNoticeContent(noticeTemp);
                messageNoticeEntity.setUserPhones(memberAccountEntity.getMemberPhone());
                messageNoticeEntity.setStatus(0);
                messageNoticeEntity.setUserId(memberAccountEntity.getId());
                messageNoticeEntity.setUserType(1);
                messageNoticeEntity.setAddTime(new Date());
                messageNoticeEntity.setAddUser("admin");
                messageNoticeService.saveMessageNotice(messageNoticeEntity);
            }
        }
        resultMap.put("success",updateList);
        resultMap.put("fail",failList);
        return resultMap;
    }

    private boolean isBeforeByBill(PendingTaxBillVO vo,TParkTaxBillEntity parkTaxBillEntity){
        if (vo == null || parkTaxBillEntity == null){
            return false;
        }
        if (parkTaxBillEntity.getTaxBillYear() > vo.getTaxBillYear()){
            return true;
        }else if(parkTaxBillEntity.getTaxBillYear() == vo.getTaxBillYear()){
            if (parkTaxBillEntity.getTaxBillSeasonal() > vo.getTaxBillSeasonal()){
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Map<String, Object> batchAddAmount(List<DeductionAmountVO> excelList, Long parkTaxBillId, String userName) {
        List<DeductionAmountVO> failList = new ArrayList<>();
        List<DeductionAmountVO> sucessList = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();
        TParkTaxBillEntity parkTaxBillEntity = parkTaxBillService.findById(parkTaxBillId);
        String max = dictionaryService.getValueByCode("vat_deduction_amount_max");
        long amountMax = Long.parseLong(max);
        Pattern pattern=Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
        for(int i=0;i<excelList.size();i++){
            // 校验数据不能为空
            if (StringUtil.isEmpty(excelList.get(i).getCompanyName()) || StringUtil.isEmpty(excelList.get(i).getEin())){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("数据缺失");
                failList.add(excelList.get(i));
                continue;
            }
            Matcher match = null;
            Long vatShouldTaxMoney = null;
            Long additionalShouldTaxMoney = null;
            Long iitDeductionAmount = null;
            if (excelList.get(i).getVatShouldTaxMoney() != null){
                match=pattern.matcher(excelList.get(i).getVatShouldTaxMoney().toString());
                if (!match.matches()){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("应缴增值税只支持两位小数");
                    failList.add(excelList.get(i));
                    continue;
                }
                vatShouldTaxMoney = excelList.get(i).getVatShouldTaxMoney().multiply(new BigDecimal(100)).longValue();
                if (vatShouldTaxMoney > amountMax){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("应缴增值税最大金额为5000000");
                    failList.add(excelList.get(i));
                    continue;
                }

            }
            if (excelList.get(i).getAdditionalShouldTaxMoney() != null){
                match=pattern.matcher(excelList.get(i).getAdditionalShouldTaxMoney().toString());
                if (!match.matches()){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("应缴附加税只支持两位小数");
                    failList.add(excelList.get(i));
                    continue;
                }
                additionalShouldTaxMoney = excelList.get(i).getAdditionalShouldTaxMoney().multiply(new BigDecimal(100)).longValue();
                if (additionalShouldTaxMoney > amountMax){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("应缴附加税最大金额为5000000");
                    failList.add(excelList.get(i));
                    continue;
                }
            }
            if (excelList.get(i).getIitDeductionAmount() != null){
                match=pattern.matcher(excelList.get(i).getIitDeductionAmount().toString());
                if (!match.matches()){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("个税扣除金额只支持两位小数");
                    failList.add(excelList.get(i));
                    continue;
                }

                iitDeductionAmount = excelList.get(i).getIitDeductionAmount().multiply(new BigDecimal(100)).longValue();
                if (iitDeductionAmount > amountMax){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("个税扣除金额最大金额为5000000");
                    failList.add(excelList.get(i));
                    continue;
                }
            }
            List<MemberCompanyEntity> companylist =  memberCompanyService.queryMemberCompanyByEinStatusNotCancellation(excelList.get(i).getEin().trim());
            if (CollectionUtil.isEmpty(companylist)){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("该企业不存在");
                failList.add(excelList.get(i));
                continue;
            }
            CompanyTaxBillEntity companyTaxBillEntity = queryCompanyTaxBillByEin(excelList.get(i).getEin(),parkTaxBillEntity.getTaxBillSeasonal(),parkTaxBillEntity.getTaxBillYear(),null);
            if (companyTaxBillEntity == null){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("该企业税单不在当前园区");
                failList.add(excelList.get(i));
                continue;
            }
            if (companyTaxBillEntity.getInvoiceMoney() == null || companyTaxBillEntity.getInvoiceMoney().equals(0L)){
                if (excelList.get(i).getAdditionalShouldTaxMoney() != null &&  excelList.get(i).getAdditionalShouldTaxMoney().compareTo(BigDecimal.ZERO) >0){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("该企业本季无开票，不能添加大于0的应缴附加税");
                    failList.add(excelList.get(i));
                    continue;
                }
                if (excelList.get(i).getVatShouldTaxMoney() != null &&  excelList.get(i).getVatShouldTaxMoney().compareTo(BigDecimal.ZERO) >0){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("该企业本季无开票，不能添加大于0的应缴增值税");
                    failList.add(excelList.get(i));
                    continue;
                }
            }

            // 应缴增值税有变化时，修改不含税收入值
            if (!Objects.equals(vatShouldTaxMoney,companyTaxBillEntity.getVatShouldTaxMoney())) {
                Long quarterIncomeAmount = companyTaxBillEntity.getInvoiceMoney() - vatShouldTaxMoney;
                // 季度不含税收入
                companyTaxBillEntity.setQuarterIncomeAmount(quarterIncomeAmount > 0L ? quarterIncomeAmount : 0L);
                // 年累计不含税收入
//                Example example = new Example(CompanyTaxBillEntity.class);
//                example.createCriteria().andEqualTo("companyId", companyTaxBillEntity.getCompanyId())
//                        .andEqualTo("taxBillYear", companyTaxBillEntity.getTaxBillYear())
//                        .andEqualTo("taxBillSeasonal", companyTaxBillEntity.getTaxBillSeasonal() - 1)
//                        .andNotEqualTo("taxBillStatus", TaxBillStatusEnum.CANCELLED.getValue());
//                List<CompanyTaxBillEntity> list = companyTaxBillService.selectByExample(example);
//                if (CollectionUtil.isEmpty(list)) {
//                    companyTaxBillEntity.setYearIncomeAmount(quarterIncomeAmount);
//                } else {
//                    companyTaxBillEntity.setYearIncomeAmount(quarterIncomeAmount + list.get(0).getYearIncomeAmount());
//                }
                // 本年累计开票
                Date start = DateUtil.parseDefaultDate(companyTaxBillEntity.getTaxBillYear() + "-01-01");
                String[] s = DateUtil.getCurrQuarter(companyTaxBillEntity.getTaxBillYear(), companyTaxBillEntity.getTaxBillSeasonal());
                Date end = DateTime.parse(s[1]).toDate();
                List<CountPeriodInvoiceAmountVO> list = invoiceOrderService.queryCompanyInvoiceAmountByEin(companyTaxBillEntity.getCompanyId(), 2, start, end, null, 0, 0);
                Long yearInvoiceAmount = 0L;
                if (CollectionUtil.isNotEmpty(list)) {
                    yearInvoiceAmount = list.stream().mapToLong(CountPeriodInvoiceAmountVO::getCountAmountInvoiced).sum();
                }
                if (yearInvoiceAmount == 0L) {
                    companyTaxBillEntity.setYearIncomeAmount(0L);
                } else {
                    // 本年累计应缴增值税
                    Long yearPayableVatFee = 0L;
                    PeriodPaidTaxVo yearPaidTaxVo = companyTaxBillService.queryPayableTaxFee(companyTaxBillEntity.getCompanyId(), companyTaxBillEntity.getTaxBillYear(), companyTaxBillEntity.getTaxBillSeasonal());
                    if (null != yearPaidTaxVo) {
                        yearPayableVatFee = yearPaidTaxVo.getVatFee() - yearPaidTaxVo.getCurrentVatTax() + vatShouldTaxMoney;
                    }
                    companyTaxBillEntity.setYearIncomeAmount(yearInvoiceAmount - yearPayableVatFee);
                }
            }
            companyTaxBillEntity.setVatShouldTaxMoney(vatShouldTaxMoney);
            if (null != vatShouldTaxMoney) {
                long l = vatShouldTaxMoney - companyTaxBillEntity.getVatAlreadyTaxMoney();
                companyTaxBillEntity.setVatSupplementTaxMoney(Math.max(l, 0L));
                companyTaxBillEntity.setVatRecoverableTaxMoney(Math.max(-l, 0L));
            }
            companyTaxBillEntity.setAdditionalShouldTaxMoney(additionalShouldTaxMoney);
            if (null != additionalShouldTaxMoney) {
                long l = additionalShouldTaxMoney - companyTaxBillEntity.getAdditionalAlreadyTaxMoney();
                companyTaxBillEntity.setAdditionalSupplementTaxMoney(Math.max(l, 0L));
                companyTaxBillEntity.setAdditionalRecoverableTaxMoney(Math.max(-l, 0L));
            }
            companyTaxBillEntity.setIitDeductionAmount(iitDeductionAmount);
            companyTaxBillEntity.setUpdateUser(userName);
            companyTaxBillEntity.setUpdateTime(new Date());
            this.editByIdSelective(companyTaxBillEntity);
            CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
            BeanUtils.copyProperties(companyTaxBillEntity,companyTaxBillChangeEntity);
            companyTaxBillChangeEntity.setId(null);
            companyTaxBillChangeEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
            String descrip = "";
            if (excelList.get(i).getVatShouldTaxMoney() != null){
                descrip += "应缴增值税为"+excelList.get(i).getVatShouldTaxMoney()+"元 ";
            }
            if (excelList.get(i).getAdditionalShouldTaxMoney() != null){
                descrip += "应缴附加税为"+excelList.get(i).getAdditionalShouldTaxMoney()+"元 ";
            }
            if(excelList.get(i).getIitDeductionAmount() !=null){
                descrip += "添加个税扣除金额为"+excelList.get(i).getIitDeductionAmount()+ "元 " ;
            }
            companyTaxBillChangeEntity.setDescrip(descrip);
            companyTaxBillChangeEntity.setAddTime(new Date());
            companyTaxBillChangeEntity.setAddUser(userName);
            companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
            sucessList.add(excelList.get(i));
        }
/*        Integer num =  companyTaxBillMapper.getCancellationCompanyByParkTaxBillId(parkTaxBillId);
        TParkTaxBillEntity tParkTaxBillEntity = parkTaxBillService.findById(parkTaxBillId);
        tParkTaxBillEntity.setCancellationCompany(num);
        tParkTaxBillEntity.setUpdateUser(userName);
        tParkTaxBillEntity.setUpdateTime(new Date());
        parkTaxBillService.editByIdSelective(tParkTaxBillEntity);*/
        //增加历史记录
        ParkTaxBillChangeEntity parkTaxBillChangeEntity=new ParkTaxBillChangeEntity();
        BeanUtils.copyProperties(parkTaxBillEntity,parkTaxBillChangeEntity);
        parkTaxBillChangeEntity.setId(null);
        parkTaxBillChangeEntity.setParkBillsId(parkTaxBillEntity.getId());
        parkTaxBillChangeEntity.setAddUser(userName);
        parkTaxBillChangeEntity.setAddTime(new Date());
        parkTaxBillChangeEntity.setRemark("修改应缴税费");
        parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
        resultMap.put("success",sucessList);
        resultMap.put("fail",failList);
        return resultMap;
    }

    @Override
    public List<PendingTaxBillVO> pendingTaxBill(PendingTaxBillQuery query) {
        List<PendingTaxBillVO> list = Lists.newArrayList();
        // 获取当前税期
        int year = DateUtil.getYear(new Date());
        int quarter = Integer.parseInt(DateUtil.getQuarter());
        if (null != query.getRange() && query.getRange() == 1) {
            query.setTaxBillYear(year);
            query.setTaxBillSeasonal(quarter);
        }

        List<PendingTaxBillVO> vos = mapper.queryPendingTaxBill(query);
        if (CollectionUtil.isEmpty(vos)) {
            return list;
        }
        // 待核对税单校验
        if (null != query.getStatusRange() && query.getStatusRange() == 3) {
            List<PendingTaxBillVO> collect = vos.stream().filter(x -> TaxBillStatusEnum.TO_BE_CHECK.getValue().equals(x.getTaxBillStatus())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect)) {
                throw new BusinessException("企业存在待核对税单");
            }
        }
        // 税期范围 1-当前税期 2-历史税期
        if (null != query.getRange() && query.getRange() == 1) {
            PendingTaxBillVO vo = vos.get(0);
            list.add(vo);
        } else if (null != query.getRange() && query.getRange() == 2) {
            // 查询历史税期未确认成本/未申报完成税单
            int finalYear = year;
            int finalQuarter = quarter;
            list = vos.stream().filter(x -> !(Objects.equals(finalYear, x.getTaxBillYear()) && Objects.equals(finalQuarter, x.getTaxBillSeasonal()))).collect(Collectors.toList());
        } else {
            list = vos;
        }
        return list;
    }

    @Override
    public List<CompanyTaxBillEntity> getCompanyTaxBillByCompanyIdAndAddTime(Long companyId, Date addTime) {
        return mapper.getCompanyTaxBillByCompanyIdAndAddTime(companyId,addTime);
    }

    @Override
    @Transactional
    public void rechargeCompanyBill(CompanyTaxBillEntity entity, String userName,String remark) {
        String descrip = "重置税单";
        if (StringUtil.isNotBlank(entity.getOrderNo())){
            PayWaterEntity payWaterEntity = payWaterService.getPayWaterByOrderNo(entity.getOrderNo());
            if (payWaterEntity != null){
                UserEntity oemUser = new UserEntity();
                oemUser.setOemCode(payWaterEntity.getOemCode());
                oemUser.setPlatformType(2);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
                oemUser.setAccountType(1);//账号类型  1-管理员  2-城市合伙人 3-城市合伙人 4-坐席客服 5-财务 6-经办人 7-运营
                oemUser.setStatus(1);//状态 0-禁用 1-可用
                oemUser = userService.selectOne(oemUser);
                //4.2 添加自己资金
                userCapitalAccountService.addBalanceByProfits(payWaterEntity.getOemCode(), payWaterEntity.getOrderNo(), payWaterEntity.getOrderType(), payWaterEntity.getMemberId(), 1, payWaterEntity.getPayAmount(), payWaterEntity.getPayAmount(), 0L, payWaterEntity.getPayAmount(), "税单重置", userName, new Date(), 1, WalletTypeEnum.CONSUMER_WALLET.getValue());
                //4.3 给机构扣除资金
                userCapitalAccountService.addBalanceByProfits(payWaterEntity.getOemCode(), payWaterEntity.getOrderNo(), payWaterEntity.getOrderType(), oemUser.getId(), 2, payWaterEntity.getPayAmount(), 0L, 0L, 0L, "税单重置", userName, new Date(), 0, WalletTypeEnum.CONSUMER_WALLET.getValue());
                entity.setOrderNo("");
                OrderEntity orderEntity = orderService.queryByOrderNo(payWaterEntity.getOrderNo());
                // 生成支付流水
                PayWaterEntity water = new PayWaterEntity();
                water.setOemCode(payWaterEntity.getOemCode());
                water.setOemName(payWaterEntity.getOemName());
//        water.setPayTime(date);
                water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
                water.setOrderNo(payWaterEntity.getOrderNo());
                water.setMemberId(payWaterEntity.getMemberId());
                water.setUserType(payWaterEntity.getUserType());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
                water.setAddTime(new Date());
                water.setAddUser(userName);
                water.setPayChannels(PayChannelEnum.BALANCEPAY.getValue());
                water.setServiceFeeRate(BigDecimal.ZERO);
                water.setServiceFee(0L);
                water.setOrderType(OrderTypeEnum.SUPPLEMENT_TAX.getValue());
                water.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
                water.setPayWaterType(PayWaterTypeEnum.REFUND.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
                water.setPayAmount(payWaterEntity.getPayAmount());// 支付金额
                water.setOrderAmount(orderEntity.getOrderAmount());// 订单金额
                water.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
                water.setWalletType(1);
                water.setRemark("退税");
                this.payWaterService.insertSelective(water);
                if (entity.getTaxBillStatus().equals(9)){
                    descrip = "税单作废";
                }
            }
        }
        companyTaxCostItemService.deleteByCompanyTaxId(entity.getId());
        CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
        BeanUtils.copyProperties(entity,companyTaxBillChangeEntity);
        companyTaxBillChangeEntity.setId(null);
        companyTaxBillChangeEntity.setCompanyTaxBillId(entity.getId());
        if (StringUtil.isNotBlank(remark)){
            descrip = remark;
        }
        companyTaxBillChangeEntity.setDescrip(descrip);
        companyTaxBillChangeEntity.setAddTime(new Date());
        companyTaxBillChangeEntity.setAddUser(userName);
        companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(userName);
        this.editByIdSelective(entity);
    }

    @Override
    @Transactional
    public void declareTax(CompanyTaxBillEntity companyTaxBillEntity, String userName) {
        //应缴税费（应缴增值税，所得税，附加税）
        CompanyTaxBillVO companyTaxBillVO = new CompanyTaxBillVO();
        companyTaxBillVO.setCompanyTaxBillId(companyTaxBillEntity.getId());
        this.detailOfAuditCollection(companyTaxBillVO);
        if (null != companyTaxBillEntity.getIncomeTaxRefundAmount() && companyTaxBillEntity.getIncomeTaxRefundAmount() > 0L
                && companyTaxBillVO.getSupplementTaxMoney() > 0L) {
            throw new BusinessException("用户已补税，不允许填写可退税额");
        }
        companyTaxBillVO.setIncomeTaxRefundAmount(companyTaxBillEntity.getIncomeTaxRefundAmount());
        this.detailOfAuditCollection(companyTaxBillVO);
        boolean flag = true; // 是否发送报税通知
        if (companyTaxBillVO.getSupplementTaxMoney() > 0L) {
            companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TAX_PAID.getValue());
            companyTaxBillEntity.setCompleteTime(new Date());
        } else if (companyTaxBillVO.getRecoverableTaxMoney() > 0L) {
            // companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TAX_TO_BE_REFUNDED.getValue());
            companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TO_FINANCIAL_AUDIT.getValue()); // 需退税税单状态变更为“待财务审核”
//            companyTaxBillEntity.setCompleteTime(new Date());
            flag = false;
        } else {
            companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.NORMAL.getValue());
            companyTaxBillEntity.setCompleteTime(new Date());
        }
        companyTaxBillEntity.setUpdateTime(new Date());
        companyTaxBillEntity.setUpdateUser(userName);
        companyTaxBillEntity.setRecoverableTaxMoney(companyTaxBillVO.getRecoverableTaxMoney());
        this.editByIdSelective(companyTaxBillEntity);
        CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
        BeanUtils.copyProperties(companyTaxBillEntity,companyTaxBillChangeEntity);
        companyTaxBillChangeEntity.setId(null);
        companyTaxBillChangeEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
        companyTaxBillChangeEntity.setDescrip("确认报税，填入个税可退税额为" +
                (null == companyTaxBillEntity.getIncomeTaxRefundAmount() ? 0 : new BigDecimal(companyTaxBillEntity.getIncomeTaxRefundAmount()).divide(new BigDecimal("100")))+ "元");
        companyTaxBillChangeEntity.setAddTime(new Date());
        companyTaxBillChangeEntity.setAddUser(userName);
        companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
        //  发送短信与通知
        MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(companyTaxBillEntity.getCompanyId());
        MemberAccountEntity memberAccountEntity = memberAccountService.findById(memberCompanyEntity.getMemberId());
        OemEntity oemEntity = oemService.getOem(memberCompanyEntity.getOemCode());
        if (oemEntity.getIsSendAuditBillsMessage().equals(1) && flag && companyTaxBillEntity.getInvoiceMoney() > 0L){
            List<Map<String,Object>> mapList=new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("phone", memberAccountEntity.getMemberPhone());
            map.put("userId", memberAccountEntity.getId());
            mapList.add(map);
            String taxPeriod = taxPeriod(companyTaxBillEntity.getTaxBillYear(),companyTaxBillEntity.getTaxBillSeasonal());
            Map<String, Object> smsMap = new HashMap<>();
            smsMap.put("taxBillSeasonal", taxPeriod);
            smsMap.put("companyName",memberCompanyEntity.getCompanyName());
            //发短信
            smsService.sendTemplateSms(memberAccountEntity.getMemberPhone(), memberAccountEntity.getOemCode(), VerifyCodeTypeEnum.COMPLETE_TAX_RETURN.getValue(), smsMap, 2);
            //发送消息
            String noticeTemp=dictionaryService.getValueByCode("declaration_completed_title");
            noticeTemp=noticeTemp.replace("#taxBillSeasonal#",taxPeriod).replace("#companyName#",String.valueOf(memberCompanyEntity.getCompanyName()));
            MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
            messageNoticeEntity.setOemCode(memberCompanyEntity.getOemCode());
            messageNoticeEntity.setNoticeType(2);
            messageNoticeEntity.setIsAlert(0);
            messageNoticeEntity.setNoticePosition("1,2");
            messageNoticeEntity.setOpenMode(3);
            messageNoticeEntity.setBusinessType(13);
            messageNoticeEntity.setNoticeTitle("申报完成通知");
            messageNoticeEntity.setNoticeContent(noticeTemp);
            messageNoticeEntity.setUserPhones(memberAccountEntity.getMemberPhone());
            messageNoticeEntity.setStatus(0);
            messageNoticeEntity.setUserId(memberAccountEntity.getId());
            messageNoticeEntity.setUserType(1);
            messageNoticeEntity.setAddTime(new Date());
            messageNoticeEntity.setAddUser("admin");
            messageNoticeService.saveMessageNotice(messageNoticeEntity);
        }
        // 园区下面企业税单状态都为已完成 修改园区税单状态
        List<Long> companyTaxBillList = mapper.getCompanyTaxBillByTaxBillStatus(companyTaxBillEntity.getParkTaxBillId());
        if (CollectionUtil.isEmpty(companyTaxBillList)){
            TParkTaxBillEntity tParkTaxBillEntity = parkTaxBillService.findById(companyTaxBillEntity.getParkTaxBillId());
            if (tParkTaxBillEntity != null){
                tParkTaxBillEntity.setTaxBillStatus(9);
                tParkTaxBillEntity.setUpdateTime(new Date());
                tParkTaxBillEntity.setUpdateUser(userName);
                parkTaxBillService.editByIdSelective(tParkTaxBillEntity);
                ParkTaxBillChangeEntity changeEntity = new ParkTaxBillChangeEntity();
                BeanUtils.copyProperties(tParkTaxBillEntity,changeEntity);
                changeEntity.setId(null);
                changeEntity.setParkBillsId(tParkTaxBillEntity.getId());
                changeEntity.setAddTime(new Date());
                changeEntity.setAddUser(userName);
                parkTaxBillChangeService.insertSelective(changeEntity);
            }
        }
    }

    private String taxPeriod(Integer taxBillYear,Integer taxBillSeasonal){
        if (taxBillYear == null || taxBillSeasonal == null){
            return "";
        }
        String taxPeriod = "";
        switch(taxBillSeasonal){
            case 1 :
                taxPeriod = taxBillYear+"年"+"第一季度";
                break;
            case 2 :
                taxPeriod = taxBillYear+"年"+"第二季度";
                break;
            case 3 :
                taxPeriod = taxBillYear+"年"+"第三季度";
                break;
            case 4 :
                taxPeriod = taxBillYear+"年"+"第四季度";
                break;
        }
        return taxPeriod;
    }

    @Override
    public List<CompanyTaxBillEntity> getCompanyTaxBillByStatus(Long companyId) {
        return mapper.getCompanyTaxBillByStatus(companyId);
    }

    @Override
    public void detailOfAuditCollection(CompanyTaxBillVO companyTaxBillVO) {
        CompanyTaxBillEntity companyTaxBillEntity = this.findById(companyTaxBillVO.getCompanyTaxBillId());
        if (companyTaxBillEntity == null) {
            throw new BusinessException("企业税单不存在");
        }
        if (IncomeLevyTypeEnum.AUDIT_COLLECTION.equals(companyTaxBillEntity.getIncomeLevyType())) {
            throw new BusinessException("非查账征收方式税单");
        }

        if (null == companyTaxBillVO.getIncomeTaxRefundAmount()
                && (null != companyTaxBillEntity.getIncomeTaxRefundAmount() && companyTaxBillEntity.getIncomeTaxRefundAmount() > 0L)) {
            companyTaxBillVO.setIncomeTaxRefundAmount(companyTaxBillEntity.getIncomeTaxRefundAmount());
        }
        // 本年已缴所得税
        Long paidIncome = 0L;
        PeriodPaidTaxVo yearPaidTaxVo = this.queryPayableTaxFee(companyTaxBillEntity.getCompanyId(), companyTaxBillEntity.getTaxBillYear(), companyTaxBillEntity.getTaxBillSeasonal());
        paidIncome = yearPaidTaxVo.getYearHistoryIncomeTax() + companyTaxBillEntity.getIncomeAlreadyTaxMoney();
        companyTaxBillVO.setIncomeAlreadyTaxMoney(paidIncome);
        // 本年应缴所得税
        Long payableIncome = null == companyTaxBillEntity.getYearPayableIncomeTax() ? 0L : companyTaxBillEntity.getYearPayableIncomeTax();
        companyTaxBillVO.setIncomeShouldTaxMoney(payableIncome);
        // 应补、应退所得税
        long l1 = payableIncome - paidIncome;
        companyTaxBillVO.setIncomeSupplementTaxMoney(l1 < 0L ? 0L : l1);
        companyTaxBillVO.setIncomeRecoverableTaxMoney((l1 > 0L || null == companyTaxBillVO.getIncomeTaxRefundAmount()) ? 0L : companyTaxBillVO.getIncomeTaxRefundAmount());
        // 总应缴
        long shouldTaxMoney = companyTaxBillEntity.getShouldTaxMoney() - companyTaxBillEntity.getIncomeShouldTaxMoney() + payableIncome;
        companyTaxBillVO.setShouldTaxMoney(shouldTaxMoney);
        // 总已缴
        long alreadyTaxMoney = companyTaxBillEntity.getAlreadyTaxMoney() - companyTaxBillEntity.getIncomeAlreadyTaxMoney() + paidIncome;
        companyTaxBillVO.setAlreadyTaxMoney(alreadyTaxMoney);

        // 总应补、应退   增值附加应缴 - 增值附加已缴 + 应补所得税 - 个税可退税额
        long l2 = (shouldTaxMoney - payableIncome) - (alreadyTaxMoney - paidIncome) + (l1 > 0 ? l1 : 0L) - (null == companyTaxBillVO.getIncomeTaxRefundAmount() ? 0L : companyTaxBillVO.getIncomeTaxRefundAmount());
        companyTaxBillVO.setSupplementTaxMoney(l2 < 0L ? 0L : l2);
        companyTaxBillVO.setRecoverableTaxMoney(l2 > 0L ? 0L : -l2);
    }

    @Override
    @Transactional
    public void updateBillAndInsertChange(CompanyTaxBillEntity entity,String descrip) {
        this.editByIdSelective(entity);
        CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
        BeanUtils.copyProperties(entity,companyTaxBillChangeEntity);
        companyTaxBillChangeEntity.setId(null);
        companyTaxBillChangeEntity.setCompanyTaxBillId(entity.getId());
        companyTaxBillChangeEntity.setDescrip(descrip);
        companyTaxBillChangeEntity.setAddTime(new Date());
        companyTaxBillChangeEntity.setAddUser(entity.getUpdateUser());
        companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
    }

    @Override
    public List<ApprovedTaxBillVO> getApprovedTaxBill(Long parkTaxBillId) {
        return mapper.getApprovedTaxBill(parkTaxBillId);
    }

    @Override
    public TaxBillFillCostDetailVO getFillCostDetail(Long companyTaxBillId) {
        // 查询企业税单
        CompanyTaxBillEntity entity = companyTaxBillService.findById(companyTaxBillId);
        if (null == entity) {
            throw new BusinessException("企业税单不存在");
        }
        // 税单类型校验
        if (!IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(entity.getIncomeLevyType())) {
            throw new BusinessException("非查账类型税单");
        }
        // 税单状态校验
        if (!TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(entity.getTaxBillStatus())) {
            throw new BusinessException("税单状态不为待填报成本");
        }
        // 查询企业
        MemberCompanyEntity company = memberCompanyService.findById(entity.getCompanyId());
        if (null == company) {
            throw new BusinessException("企业不存在");
        }
        TaxBillFillCostDetailVO vo = new TaxBillFillCostDetailVO();
        vo.setOperatorName(company.getOperatorName());
        vo.setCompanyName(company.getCompanyName());
        vo.setCompanyId(entity.getCompanyId());
        vo.setTaxBillTime(entity.getTaxBillYear() + "年" + entity.getTaxBillSeasonal() + "季度");
        vo.setYearIncomeAmount(null == entity.getQuarterIncomeAmount() ? 0L : entity.getQuarterIncomeAmount());
        vo.setYearIncomeAlreadyTaxMoney(entity.getIncomeAlreadyTaxMoney());
        vo.setYearIitDeductionAmount(null == entity.getIitDeductionAmount() ? 0L : entity.getIitDeductionAmount());
        // 计算本年累计开票金额
        Integer year = entity.getTaxBillYear();
        String[] firstSeasonal = DateUtil.getCurrQuarter(year, 1);
        Date start = DateUtil.parseDefaultDate(firstSeasonal[0]);
        Integer seasonal = entity.getTaxBillSeasonal();
        String[] taxBillSeasonal = DateUtil.getCurrQuarter(year, seasonal);
        Date end = DateUtil.parseDefaultDate(taxBillSeasonal[1]);
        List<CountPeriodInvoiceAmountVO> list = invoiceOrderService.queryCompanyInvoiceAmountByEin(company.getId(), 2, start, end, null, 1, 0);
        Long yearInvoiceAmount = 0L;
        if (!list.isEmpty() && null != list) {
            for (CountPeriodInvoiceAmountVO companyInvoiceAmountVO : list) {
                yearInvoiceAmount += companyInvoiceAmountVO.getCountAmountInvoiced();
            }
        }
        vo.setYearInvoiceAmount(yearInvoiceAmount);
        // 本年历史累计成本
        if (entity.getTaxBillSeasonal() == 1 || DateUtil.getQuarter(company.getAddTime()).equals(entity.getTaxBillSeasonal().toString())) {
            vo.setHistoryCostAmount(0L);
        } else {
            // 查询历史季度税单
            Example example = new Example(CompanyTaxBillEntity.class);
            example.createCriteria().andEqualTo("companyId", entity.getCompanyId())
                    .andEqualTo("taxBillYear", entity.getTaxBillYear())
                    .andNotEqualTo("taxBillStatus", TaxBillStatusEnum.CANCELLED.getValue());
            List<CompanyTaxBillEntity> bills = companyTaxBillService.selectByExample(example);
            if (CollectionUtil.isEmpty(list)) {
                return vo;
            }
            // 获取上个季度税单
            List<CompanyTaxBillEntity> collect = bills.stream().filter(x -> x.getTaxBillSeasonal() == entity.getTaxBillSeasonal() - 1).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(collect)) {
                throw new BusinessException("未查询到历史税单数据");
            }
            CompanyTaxBillEntity taxBillEntity = collect.get(0);
            vo.setHistoryCostAmount(null == taxBillEntity.getYearCostAmount() ? 0L : taxBillEntity.getYearCostAmount());
            vo.setYearIncomeAmount(null == entity.getYearIncomeAmount() ? 0L : entity.getYearIncomeAmount());
            vo.setYearIncomeAlreadyTaxMoney(vo.getYearIncomeAlreadyTaxMoney() + bills.stream().mapToLong(CompanyTaxBillEntity::getIncomeShouldTaxMoney).sum());
            vo.setYearIitDeductionAmount(vo.getYearIitDeductionAmount() + (null == taxBillEntity.getIitDeductionAmount() ? 0L : taxBillEntity.getIitDeductionAmount()));
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyTaxBillEntity fillCost(FillCostDTO dto) {
        // 查询企业税单
        CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(dto.getCompanyTaxBillId());
        if (companyTaxBillEntity == null) {
            throw new BusinessException("企业税单不存在");
        }
        // 校验企业税单状态
        if (!TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(companyTaxBillEntity.getTaxBillStatus())) {
            throw new BusinessException("税单状态不正确");
        }
        // 查询企业
        MemberCompanyEntity company = memberCompanyService.findById(companyTaxBillEntity.getCompanyId());
        if (null == company) {
            throw new BusinessException("未查询到企业信息");
        }
        // 校验是否有更早的未确认成本的税单
        Example example = new Example(CompanyTaxBillEntity.class);
        example.createCriteria().andEqualTo("companyId", company.getId())
                .andIn("taxBillStatus", Arrays.asList(2,7));
        List<CompanyTaxBillEntity> list = companyTaxBillService.selectByExample(example);
        if (CollectionUtil.isNotEmpty(list)) {
            List<CompanyTaxBillEntity> collect = list.stream()
                    .filter(x -> x.getTaxBillYear().compareTo(companyTaxBillEntity.getTaxBillYear()) < 0
                            || (x.getTaxBillYear().compareTo(companyTaxBillEntity.getTaxBillYear()) == 0
                            && x.getTaxBillSeasonal().compareTo(companyTaxBillEntity.getTaxBillSeasonal()) < 0)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect)) {
                throw new BusinessException("请先处理时间更早的税单");
            }
        }

        // 删除已绑定税单的成本项
        Example taxCostItem = new Example(CompanyTaxCostItemEntity.class);
        taxCostItem.createCriteria().andEqualTo("companyTaxId", dto.getCompanyTaxBillId());
        companyTaxCostItemService.delByExample(taxCostItem);
        // 绑定税单成本项
        long allCost = 0L;
        Date date = new Date();
        if (CollectionUtil.isNotEmpty(dto.getCosts())) {
            List<Map<String,Long>> costItemList;
            for (CompanyTaxCostItemEntity cost : dto.getCosts()) {
                //校验成本项金额与本年历史成本项金额的差 < 0
                if (cost.getCostItemAmount() < 0) {
                    costItemList = mapper.getCostItemByCompanyBillsYear(companyTaxBillEntity.getTaxBillYear(),companyTaxBillEntity.getTaxBillSeasonal(),company.getId(),company.getEin(),cost.getCostItemName());
                    if (CollectionUtil.isEmpty(costItemList)
                            || (costItemList.get(0).get("totalAmount") + cost.getCostItemAmount() < 0)) {
                        throw new BusinessException("成本项【"+cost.getCostItemName()+"】本年累计金额不能为负数");
                    }
                }
                cost.setCompanyTaxId(companyTaxBillEntity.getId());
                cost.setAddTime(date);
                cost.setAddUser(dto.getOperator());
                companyTaxCostItemService.insertSelective(cost);
            }
            allCost = dto.getCosts().stream().mapToLong(CompanyTaxCostItemEntity::getCostItemAmount).sum();
        }
        // 更新企业税单成本
        companyTaxBillEntity.setQuarterCostAmount(allCost);
        // 每次填报成本都清除一次冻结税额
        companyTaxBillEntity.setIncomeTaxYearFreezeAmount(0L);
        companyTaxBillService.editByIdSelective(companyTaxBillEntity);
        // 税单计算
        TaxCalculationVO entity = new TaxCalculationVO();
        entity.setCompanyId(companyTaxBillEntity.getCompanyId());
        entity.setType(2);
        entity.setOrderNo(null);
        entity.setVatRate(null);
        entity.setSeason(companyTaxBillEntity.getTaxBillSeasonal());
        entity.setYear(companyTaxBillEntity.getTaxBillYear());
        entity.setCalculationType(2);
        Map<String, Object> map = invoiceOrderService.taxCalculation(entity);
        BeanUtil.copyProperties(map, companyTaxBillEntity);
        companyTaxBillService.editByIdSelective(companyTaxBillEntity);
        // 所得税需要使用年度数据
        CompanyTaxBillVO companyTaxBillVO = new CompanyTaxBillVO();
        companyTaxBillVO.setCompanyTaxBillId(companyTaxBillEntity.getId());
        companyTaxBillService.detailOfAuditCollection(companyTaxBillVO);
        // 运营平台操作
        if (dto.getSourceOfOperating() == 2 || dto.getSourceOfOperating() == 3) {
            companyTaxBillEntity.setUpdateUser(dto.getOperator());
            companyTaxBillEntity.setUpdateTime(new Date());
            // 修改税单状态
            if (companyTaxBillVO.getSupplementTaxMoney() > 0) {
                companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TAX_TO_BE_PAID.getValue());
            } else {
                companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TO_BE_DECLARE.getValue());
            }
            // 添加用户签名
            companyTaxBillEntity.setSignImg(company.getSignImg());
            // 增加变更记录
            CompanyTaxBillChangeEntity changeEntity = new CompanyTaxBillChangeEntity();
            BeanUtil.copyProperties(companyTaxBillEntity, changeEntity);
            changeEntity.setId(null);
            changeEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
            changeEntity.setAddTime(new Date());
            changeEntity.setAddUser(dto.getOperator());
            changeEntity.setUpdateTime(null);
            changeEntity.setUpdateUser(null);
            if (dto.getSourceOfOperating() == 2) {
                changeEntity.setDescrip("后台填报成本");
            } else {
                changeEntity.setDescrip("批量填报成本");
            }
            companyTaxBillChangeService.insertSelective(changeEntity);
        }
        companyTaxBillService.editByIdSelective(companyTaxBillEntity);
        return companyTaxBillEntity;
    }

    @Override
    public Map<String, Object> batchFillCost(List<TaxFillCostVO> excelList, Long parkTaxBillId, String operator) {
        List<TaxFillCostVO> failList = new ArrayList<>();
        List<CompanyTaxBillEntity> updateList = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();
        FillCostDTO dto = new FillCostDTO();
        TParkTaxBillEntity parkTaxBillEntity = parkTaxBillService.findById(parkTaxBillId);
        TaxFillCostVO vo = null;
        Pattern patternOfName = Pattern.compile("^[\u4e00-\u9fa5]{1,10}$");
        Pattern patternOfAmount = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
        loop: for(int i=0;i<excelList.size();i++){
            vo = excelList.get(i);
            // 校验数据不能为空
            if (StringUtil.isBlank(vo.getCompanyName()) || StringUtil.isBlank(vo.getEin()) || StringUtil.isBlank(vo.getIsNoCost())){
                vo.setFlag(false);
                vo.setReg("数据缺失");
                failList.add(vo);
                continue;
            }
            // 校验企业
            List<MemberCompanyEntity> companyEntities = memberCompanyService.queryMemberCompanyByEinStatusNotCancellation(vo.getEin());
            if (CollectionUtil.isEmpty(companyEntities)) {
                vo.setFlag(false);
                vo.setReg("企业不存在");
                failList.add(vo);
                continue;
            }
            // 校验企业名称与税号是否匹配
            if (!vo.getCompanyName().equals(companyEntities.get(0).getCompanyName())) {
                vo.setFlag(false);
                vo.setReg("企业名称与税号不匹配");
                failList.add(vo);
                continue;
            }
            // 是否0成本申报
            if (!"是".equals(vo.getIsNoCost()) && !"否".equals(vo.getIsNoCost())) {
                vo.setFlag(false);
                vo.setReg("是否0成本申报数据非法");
                failList.add(vo);
                continue;
            }
            // 0成本申报与成本项关系
            if ("是".equals(vo.getIsNoCost()) && StringUtil.isNotBlank(vo.getCostItems())) {
                vo.setFlag(false);
                vo.setReg("0成本申报无需填写成本项");
                failList.add(vo);
                continue;
            }
            if ("否".equals(vo.getIsNoCost()) && StringUtil.isBlank(vo.getCostItems())) {
                vo.setFlag(false);
                vo.setReg("请填写成本项");
                failList.add(vo);
                continue;
            }
            List<CompanyTaxCostItemEntity> costList = Lists.newArrayList();
            if ("否".equals(vo.getIsNoCost())) {
                // 获取成本项
                String costItems = vo.getCostItems().replace("（", "(").replace("）", ")")
                        .replace("，", ",").replace("；", ";");
                costItems = costItems.replace("(", "").replace(")", "");
                String[] costs = costItems.split(";");
                if (costs.length == 0) {
                    vo.setFlag(false);
                    vo.setReg("请填写完整成本项");
                    failList.add(vo);
                    continue;
                }
                // 成本项最多支持添加10项
                if (costs.length > 10) {
                    vo.setFlag(false);
                    vo.setReg("成本项最多支持添加10项");
                    failList.add(vo);
                    continue;
                }
                // 成本项名称只能为1~10个汉字
                for (String cost : costs) {
                    CompanyTaxCostItemEntity entity = new CompanyTaxCostItemEntity();
                    String[] split = cost.split(",");
                    if (split.length != 2) {
                        vo.setFlag(false);
                        vo.setReg("成本项填写不规范");
                        failList.add(vo);
                        continue loop;
                    }
                    if (!patternOfName.matcher(split[0]).matches()) {
                        vo.setFlag(false);
                        vo.setReg("成本项名称只能为1~10个汉字");
                        failList.add(vo);
                        continue loop;
                    }
                    if (!patternOfAmount.matcher(split[1]).matches()) {
                        vo.setFlag(false);
                        vo.setReg("成本项金额只支持两位小数");
                        failList.add(vo);
                        continue loop;
                    }
                    if (new BigDecimal(split[1]).compareTo(new BigDecimal("1")) < 0 || new BigDecimal(split[1]).compareTo(new BigDecimal("5000000")) > 0) {
                        vo.setFlag(false);
                        vo.setReg("成本项金额只支持1~500W的数字");
                        failList.add(vo);
                        continue loop;
                    }
                    entity.setCostItemName(split[0]);
                    entity.setCostItemAmount(new BigDecimal(split[1]).multiply(new BigDecimal("100")).longValue());
                    costList.add(entity);
                }
            }
            // 查询企业税单
            CompanyTaxBillEntity companyTaxBillEntity = queryCompanyTaxBillByEin(vo.getEin(),parkTaxBillEntity.getTaxBillSeasonal(),parkTaxBillEntity.getTaxBillYear(),null);
            if (companyTaxBillEntity == null){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("企业税单不存在");
                failList.add(excelList.get(i));
                continue;
            }
            // 企业税单是否属于当前园区
            if (!Objects.equals(parkTaxBillEntity.getParkId(), companyTaxBillEntity.getParkId())) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("该企业税单不属于该园区");
                failList.add(excelList.get(i));
                continue;
            }
            // 税单状态是否为“待填报成本”
            if (!TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(companyTaxBillEntity.getTaxBillStatus())) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("税单状态不正确");
                failList.add(excelList.get(i));
                continue;
            }

            // 填报成本
            dto.setOperator(operator);
            dto.setSourceOfOperating(3);
            dto.setCompanyTaxBillId(companyTaxBillEntity.getId());
            dto.setCosts(costList);
            try {
                this.fillCost(dto);
            } catch (Exception e) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg(e.getMessage());
                failList.add(excelList.get(i));
                continue;
            }
            updateList.add(companyTaxBillEntity);
        }
        // 添加园区税单变更记录
        ParkTaxBillChangeEntity changeEntity = new ParkTaxBillChangeEntity();
        BeanUtil.copyProperties(parkTaxBillEntity, changeEntity);
        changeEntity.setId(null);
        changeEntity.setAddUser(operator);
        changeEntity.setAddTime(new Date());
        changeEntity.setUpdateUser(null);
        changeEntity.setUpdateTime(null);
        changeEntity.setRemark("批量填报成本");
        parkTaxBillChangeService.insertSelective(changeEntity);

        resultMap.put("success",updateList);
        resultMap.put("fail",failList);
        return resultMap;
    }

    @Override
    public void taxAudit(TaxAuditDTO dto) {
        if (dto.getAuditResult() != 1 && dto.getAuditResult() != 2) {
            throw new BusinessException("审核结果数据非法");
        }
        // 审核不通过时备注必填
        if (dto.getAuditResult() == 2 && StringUtil.isBlank(dto.getRemark())) {
            throw new BusinessException("备注为空");
        }
        // 查询企业税单
        CompanyTaxBillEntity entity = companyTaxBillService.findById(dto.getCompanyTaxBillId());
        if (null == entity) {
            throw new BusinessException("未查询到企业税单");
        }
        // 校验税单状态
        if (!TaxBillStatusEnum.TO_FINANCIAL_AUDIT.getValue().equals(entity.getTaxBillStatus())) {
            throw new BusinessException("税单状态不为待财务审核状态");
        }
        // 修改企业税单状态
        String description = "审核通过";
        if (dto.getAuditResult() == 1) { // 审核通过
            entity.setTaxBillStatus(TaxBillStatusEnum.TAX_TO_BE_REFUNDED.getValue());
        } else {
            description = "审核不通过";
            entity.setTaxBillStatus(TaxBillStatusEnum.AUDIT_FAILED.getValue());
        }
        companyTaxBillService.editByIdSelective(entity);
        // 添加变更记录
        CompanyTaxBillChangeEntity changeEntity = new CompanyTaxBillChangeEntity();
        ObjectUtil.copyObject(entity, changeEntity);
        changeEntity.setId(null);
        changeEntity.setCompanyTaxBillId(entity.getId());
        changeEntity.setAddTime(new Date());
        changeEntity.setAddUser(dto.getOperator());
        changeEntity.setUpdateTime(null);
        changeEntity.setUpdateUser(null);
        changeEntity.setDescrip(description + (StringUtil.isBlank(dto.getRemark()) ? "" : "，" + dto.getRemark()));
        companyTaxBillChangeService.insertSelective(changeEntity);

        if (dto.getAuditResult() == 2) {
            return;
        }
        // 审核通过时发送已报税通知
        MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(entity.getCompanyId());
        MemberAccountEntity memberAccountEntity = memberAccountService.findById(memberCompanyEntity.getMemberId());
        OemEntity oemEntity = oemService.getOem(memberCompanyEntity.getOemCode());
        if (oemEntity.getIsSendAuditBillsMessage().equals(1)){
            List<Map<String,Object>> mapList=new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("phone", memberAccountEntity.getMemberPhone());
            map.put("userId", memberAccountEntity.getId());
            mapList.add(map);
            String taxPeriod = taxPeriod(entity.getTaxBillYear(),entity.getTaxBillSeasonal());
            Map<String, Object> smsMap = new HashMap<>();
            smsMap.put("taxBillSeasonal", taxPeriod);
            smsMap.put("companyName",memberCompanyEntity.getCompanyName());
            //发短信
            smsService.sendTemplateSms(memberAccountEntity.getMemberPhone(), memberAccountEntity.getOemCode(), VerifyCodeTypeEnum.COMPLETE_TAX_RETURN.getValue(), smsMap, 2);
            //发送消息
            String noticeTemp=dictionaryService.getValueByCode("declaration_completed_title");
            noticeTemp=noticeTemp.replace("#taxBillSeasonal#",taxPeriod).replace("#companyName#",String.valueOf(memberCompanyEntity.getCompanyName()));
            MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
            messageNoticeEntity.setOemCode(memberCompanyEntity.getOemCode());
            messageNoticeEntity.setNoticeType(2);
            messageNoticeEntity.setIsAlert(0);
            messageNoticeEntity.setNoticePosition("1,2");
            messageNoticeEntity.setOpenMode(3);
            messageNoticeEntity.setBusinessType(13);
            messageNoticeEntity.setNoticeTitle("申报完成通知");
            messageNoticeEntity.setNoticeContent(noticeTemp);
            messageNoticeEntity.setUserPhones(memberAccountEntity.getMemberPhone());
            messageNoticeEntity.setStatus(0);
            messageNoticeEntity.setUserId(memberAccountEntity.getId());
            messageNoticeEntity.setUserType(1);
            messageNoticeEntity.setAddTime(new Date());
            messageNoticeEntity.setAddUser("admin");
            messageNoticeService.saveMessageNotice(messageNoticeEntity);
        }
        // 园区下面企业税单状态都为已完成 修改园区税单状态
        List<Long> companyTaxBillList = mapper.getCompanyTaxBillByTaxBillStatus(entity.getParkTaxBillId());
        if (CollectionUtil.isEmpty(companyTaxBillList)){
            TParkTaxBillEntity tParkTaxBillEntity = parkTaxBillService.findById(entity.getParkTaxBillId());
            if (tParkTaxBillEntity != null){
                tParkTaxBillEntity.setTaxBillStatus(9);
                tParkTaxBillEntity.setUpdateTime(new Date());
                tParkTaxBillEntity.setUpdateUser(dto.getOperator());
                parkTaxBillService.editByIdSelective(tParkTaxBillEntity);
                ParkTaxBillChangeEntity parkTaxBillChangeEntity = new ParkTaxBillChangeEntity();
                BeanUtils.copyProperties(tParkTaxBillEntity,parkTaxBillChangeEntity);
                parkTaxBillChangeEntity.setId(null);
                parkTaxBillChangeEntity.setParkBillsId(tParkTaxBillEntity.getId());
                parkTaxBillChangeEntity.setAddTime(new Date());
                parkTaxBillChangeEntity.setAddUser(dto.getOperator());
                parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
            }
        }
    }

    @Override
    public Map<String, Object> batchTaxAudit(List<TaxAuditVO> excelList, Long parkTaxBillId, String operator) {
        List<TaxAuditVO> failList = new ArrayList<>();
        List<CompanyTaxBillEntity> updateList = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();
        TaxAuditDTO dto = new TaxAuditDTO();
        TParkTaxBillEntity parkTaxBillEntity = parkTaxBillService.findById(parkTaxBillId);
        TaxAuditVO vo = null;
        Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
        loop: for(int i=0;i<excelList.size();i++){
            vo = excelList.get(i);
            // 校验数据不能为空
            if (StringUtil.isBlank(vo.getCompanyName()) || StringUtil.isBlank(vo.getEin()) || StringUtil.isBlank(vo.getAuditResult())){
                vo.setFlag(false);
                vo.setReg("数据缺失");
                failList.add(vo);
                continue;
            }
            // 审核结果数据合法性校验
            if (!"通过".equals(vo.getAuditResult()) && !"不通过".equals(vo.getAuditResult())) {
                vo.setFlag(false);
                vo.setReg("审核结果数据非法");
                failList.add(vo);
                continue;
            }
            // 校验企业
            List<MemberCompanyEntity> companyEntities = memberCompanyService.queryMemberCompanyByEinStatusNotCancellation(vo.getEin());
            if (CollectionUtil.isEmpty(companyEntities)) {
                vo.setFlag(false);
                vo.setReg("企业不存在");
                failList.add(vo);
                continue;
            }
            // 校验企业名称与税号是否匹配
            if (!vo.getCompanyName().equals(companyEntities.get(0).getCompanyName())) {
                vo.setFlag(false);
                vo.setReg("企业名称与税号不匹配");
                failList.add(vo);
                continue;
            }
            // 查询企业税单
            CompanyTaxBillEntity companyTaxBillEntity = queryCompanyTaxBillByEin(vo.getEin(),parkTaxBillEntity.getTaxBillSeasonal(),parkTaxBillEntity.getTaxBillYear(),null);
            if (companyTaxBillEntity == null){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("企业税单不存在");
                failList.add(excelList.get(i));
                continue;
            }
            // 企业税单是否属于当前园区
            if (!Objects.equals(parkTaxBillEntity.getParkId(), companyTaxBillEntity.getParkId())) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("该企业税单不属于该园区");
                failList.add(excelList.get(i));
                continue;
            }
            // 税单状态是否为“待财务审核”
            if (!TaxBillStatusEnum.TO_FINANCIAL_AUDIT.getValue().equals(companyTaxBillEntity.getTaxBillStatus())) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("税单状态不正确");
                failList.add(excelList.get(i));
                continue;
            }
            // 审核通过时，应退税费不能为空
            if ("通过".equals(vo.getAuditResult())) {
                if (null == vo.getRecoverableTaxMoney()) {
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("应退税费为空");
                    failList.add(excelList.get(i));
                    continue;
                }
                // 应退税费
                if (!pattern.matcher(vo.getRecoverableTaxMoney()).matches()) {
                    vo.setFlag(false);
                    vo.setReg("应退税费只支持两位小数");
                    failList.add(vo);
                    continue;
                }
                // 审核通过，应退税费应与税单保持一致
                Long recoverableTaxMoney = companyTaxBillEntity.getVatShouldTaxMoney() - companyTaxBillEntity.getVatAlreadyTaxMoney()
                        + companyTaxBillEntity.getAdditionalShouldTaxMoney() - companyTaxBillEntity.getAdditionalAlreadyTaxMoney()
                        - companyTaxBillEntity.getIncomeTaxRefundAmount();
                if (recoverableTaxMoney > 0L) {
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("税单无总应退");
                    failList.add(excelList.get(i));
                    continue;
                }
                recoverableTaxMoney = -recoverableTaxMoney;
                if (!Objects.equals(recoverableTaxMoney, new BigDecimal(vo.getRecoverableTaxMoney()).multiply(new BigDecimal("100")).longValue())) {
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("应退税费不一致，不能审核通过");
                    failList.add(excelList.get(i));
                    continue;
                }
            }

            // 审核不通过，备注必填
            if ("不通过".equals(vo.getAuditResult()) && StringUtil.isBlank(vo.getRemark())) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("审核不通过，备注为空");
                failList.add(excelList.get(i));
                continue;
            }
            // 填报成本
            dto.setOperator(operator);
            dto.setAuditResult("通过".equals(vo.getAuditResult()) ? 1 : 2);
            dto.setCompanyTaxBillId(companyTaxBillEntity.getId());
            dto.setRemark(vo.getRemark());
            try {
                this.taxAudit(dto);
            } catch (Exception e) {
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg(e.getMessage());
                failList.add(excelList.get(i));
                continue ;
            }
            updateList.add(companyTaxBillEntity);
        }
        // 添加园区税单变更记录
        ParkTaxBillChangeEntity changeEntity = new ParkTaxBillChangeEntity();
        BeanUtil.copyProperties(parkTaxBillEntity, changeEntity);
        changeEntity.setId(null);
        changeEntity.setAddUser(operator);
        changeEntity.setAddTime(new Date());
        changeEntity.setUpdateUser(null);
        changeEntity.setUpdateTime(null);
        changeEntity.setRemark("财务批量审核退税");
        parkTaxBillChangeService.insertSelective(changeEntity);

        resultMap.put("success",updateList);
        resultMap.put("fail",failList);
        return resultMap;
    }

    @Override
    public void resubmitTaxBill(Long companyTaxBillId, Long incomeTaxRefundAmount, String operator) {
        if (null == companyTaxBillId) {
            throw new BusinessException("企业税单id为空");
        }
        if (null == incomeTaxRefundAmount) {
            throw new BusinessException("个税可退税额为空");
        }

        // 查询税单
        CompanyTaxBillEntity entity = companyTaxBillService.findById(companyTaxBillId);
        if (null == entity) {
            throw new BusinessException("税单不存在");
        }
        // 校验税单状态
        if (!TaxBillStatusEnum.AUDIT_FAILED.getValue().equals(entity.getTaxBillStatus())) {
            throw new BusinessException("税单状态不为审核不通过");
        }
        // 可退税额校验
        if (incomeTaxRefundAmount > entity.getIncomeTaxRefundAmount() + entity.getIncomeTaxYearFreezeAmount()) {
            throw new BusinessException("个税可退税额不能超过冻结税额");
        }
        // 修改税单
        entity.setIncomeTaxYearFreezeAmount(entity.getIncomeTaxYearFreezeAmount() + entity.getIncomeTaxRefundAmount() - incomeTaxRefundAmount);
        entity.setIncomeTaxRefundAmount(incomeTaxRefundAmount);
        entity.setTaxBillStatus(TaxBillStatusEnum.TO_FINANCIAL_AUDIT.getValue());
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(operator);
        companyTaxBillService.editByIdSelective(entity);
        // 添加变更记录
        CompanyTaxBillChangeEntity changeEntity = new CompanyTaxBillChangeEntity();
        ObjectUtil.copyObject(entity, changeEntity);
        changeEntity.setId(null);
        changeEntity.setCompanyTaxBillId(entity.getId());
        changeEntity.setAddUser(operator);
        changeEntity.setAddTime(new Date());
        changeEntity.setUpdateUser(null);
        changeEntity.setUpdateTime(null);
        changeEntity.setDescrip("税单重新提交审核，修改个税可退税额为" + new BigDecimal(incomeTaxRefundAmount).divide(new BigDecimal("100")) + "元");
        companyTaxBillChangeService.insertSelective(changeEntity);
    }

    /**
     * 根据税单年获取历史成本汇总
     * @param companyTaxBillId
     * @return
     */
    @Override
    public TaxBilHistoryCostItemVO getCostItemByCompanyBillsYear(Long companyTaxBillId){
        if (null == companyTaxBillId) {
            throw new BusinessException("企业税单id不能为空");
        }
        CompanyTaxBillEntity  entity = this.findById(companyTaxBillId);
        if(entity == null){
            throw new BusinessException("未找到企业税单数据");
        }
        MemberCompanyEntity companyEntity = memberCompanyService.findById(entity.getCompanyId());
        if(companyEntity == null){
            throw new BusinessException("税单对应的企业信息错误");
        }
        List<Map<String,Long>> costItemList = mapper.getCostItemByCompanyBillsYear(entity.getTaxBillYear(),entity.getTaxBillSeasonal(),companyEntity.getId(),companyEntity.getEin(),null);
        TaxBilHistoryCostItemVO vo = new TaxBilHistoryCostItemVO();
        vo.setCompanyId(companyEntity.getId());
        vo.setCostItems(costItemList);
        vo.setTaxBillYear(entity.getTaxBillYear());
        if(costItemList!=null&& costItemList.size()>0) {
            vo.setTotalCostAmount(costItemList.stream().mapToLong(map->map.get("totalAmount")).sum());
        }else{
            vo.setTotalCostAmount(0L);
        }
        return vo;
    }
}

