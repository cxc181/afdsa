package com.yuqian.itax.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemParkRelaService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.park.entity.ParkCorporateAccountConfigEntity;
import com.yuqian.itax.park.enums.ParkCorporateAccountConfigStatusEnum;
import com.yuqian.itax.park.service.ParkCorporateAccountConfigService;
import com.yuqian.itax.product.entity.ProductByParkEntity;
import com.yuqian.itax.product.entity.ProductDiscountActivityEntity;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.dto.ProductDiscountActivityAPIDTO;
import com.yuqian.itax.product.entity.vo.ProductDiscountActivityVO;
import com.yuqian.itax.product.enums.ProductStatusEnum;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ProductByParkService;
import com.yuqian.itax.product.service.ProductDiscountActivityService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.system.entity.BankBinEntity;
import com.yuqian.itax.system.entity.BankInfoEntity;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.BankBinService;
import com.yuqian.itax.system.service.BankInfoService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.dao.CompanyCorporateAccountMapper;
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.po.CompanyCorporateAccountPO;
import com.yuqian.itax.user.entity.query.ComCorpAccQuery;
import com.yuqian.itax.user.entity.query.CompanyCorporateAccountDetailQuery;
import com.yuqian.itax.user.entity.query.CompanyCorporateAccountQuery;
import com.yuqian.itax.user.entity.query.CorporateAccountCollectionRecordQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.enums.MemberAuthStatusEnum;
import com.yuqian.itax.user.enums.UserTypeEnum;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringHandleUtil;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
@Service("companyCorporateAccountService")
public class CompanyCorporateAccountServiceImpl extends BaseServiceImpl<CompanyCorporateAccountEntity,CompanyCorporateAccountMapper> implements CompanyCorporateAccountService {

    @Autowired
    BankBinService bankBinService;
    @Autowired
    private BankInfoService bankInfoService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private DaifuApiService daifuApiService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private ParkCorporateAccountConfigService parkCorporateAccountConfigService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private OemParkRelaService oemParkRelaService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private ProductDiscountActivityService productDiscountActivityService;
    @Autowired
    private MemberCrowdLabelRelaService memberCrowdLabelRelaService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private ProductByParkService productByParkService;

    private static final Integer DEFAULT_SURPLUS_DAYS = 30;// 到期时间剩余天数

    @Override
    public PageInfo<CompanyCorporateAccountVO> querCompanyCorporateAccountServicePageInfo(CompanyCorporateAccountQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(mapper.querCompanyCorporateAccountServiceList(query));
    }

    @Override
    public void updateStatus(Long id, Integer status, String photoUrl) {
        CompanyCorporateAccountEntity companyCorporateAccountEntity = mapper.selectByPrimaryKey(id);
        if(companyCorporateAccountEntity==null){
            throw  new BusinessException("对公账户不存在，如有疑问请联系客服");
        }
        companyCorporateAccountEntity.setStatus(status);
        //状态(1-正常  2-冻结 3-注销 4-过期
        if(status==3){
//            if(StringUtil.isEmpty(photoUrl)){
//                throw  new BusinessException("注销对公户需要上传注销凭证.");
//            }
            if(StringUtil.isNotEmpty(photoUrl)) {
                companyCorporateAccountEntity.setCancelCredentials(photoUrl);
            }
        }
      /*  if(status==1){
            if(new Date().compareTo(companyCorporateAccountEntity.getExpirationTime())>0){
                companyCorporateAccountEntity.setStatus(4);
            }
        }*/
        mapper.updateByPrimaryKey(companyCorporateAccountEntity);
    }

    @Override
    public void update(CompanyCorporateAccountPO po,String account) {
        CompanyCorporateAccountEntity companyCorporateAccountEntity=mapper.selectByPrimaryKey(po.getId());
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(companyCorporateAccountEntity.getCompanyId());
        Example example = new Example(CompanyCorporateAccountEntity.class);
        example.createCriteria().andEqualTo("corporateAccount",po.getCorporateAccount())
                .andEqualTo("status",1)
                .andNotEqualTo("corporateAccount",companyCorporateAccountEntity.getCorporateAccount().trim());
        List<CompanyCorporateAccountEntity> list2=mapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(list2)){
            throw  new BusinessException("对公账号已经存在");
        }
        // 读取渠道代付相关配置 paramsType=13
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(memberCompanyEntity.getOemCode(),13);
        if(null == paramsEntity){
            throw new BusinessException("未配置渠道代付相关信息！");
        }
        //掩码处理
        if(po.getCorporateAccount().contains("****")){
            companyCorporateAccountEntity.setCorporateAccount(companyCorporateAccountEntity.getCorporateAccount().trim());
        }else{
            companyCorporateAccountEntity.setCorporateAccount(po.getCorporateAccount().trim());
        }

        ComCorpAccQuery comCorpAccQuery =new ComCorpAccQuery();
        comCorpAccQuery.setTxnStffId(po.getVoucherMemberCode());
        comCorpAccQuery.setDraweeAccountNo(po.getCorporateAccount());


        JSONObject jsonObj=daifuApiService.queryCardBalance(paramsEntity,comCorpAccQuery);
        if(null != jsonObj&& !"00".equals(jsonObj.getString("bizCode"))){
            throw new BusinessException("系统异常，请确定开户信息是否正确或稍后再试。");
            //throw new BusinessException(jsonObj.getString("bizCodeMsg"));
        }
        //四要素验证
        boolean flag= bankInfoService.check4ToBankCard(po.getOemCode(),po.getBankUserName(),memberCompanyEntity.getIdCardNumber(),po.getBindBankCardNumber(),po.getBindBankPhone());
        if(!flag) {
            throw new BusinessException("银行卡四要素验证不通过！");
        }
        companyCorporateAccountEntity.setCorporateAccountConfigId(po.getCorporateAccountConfigId());
        companyCorporateAccountEntity.setCorporateAccountBankName(Optional.ofNullable(parkCorporateAccountConfigService.findById(po.getCorporateAccountConfigId())).map(ParkCorporateAccountConfigEntity::getCorporateAccountBankName).orElse(null));
        companyCorporateAccountEntity.setVoucherMemberCode(po.getVoucherMemberCode());
        companyCorporateAccountEntity.setEntrustProjectCode(po.getEntrustProjectCode());
        companyCorporateAccountEntity.setProjectUseCode(po.getProjectUseCode());
        companyCorporateAccountEntity.setEntrustProjectCodeWj(po.getEntrustProjectCodeWj());
        companyCorporateAccountEntity.setProjectUseCodeWj(po.getProjectUseCodeWj());
        BankBinEntity bankBinEntity= bankBinService.findByCardNo(po.getBindBankCardNumber());
        // 查询银行信息
        BankInfoEntity t = new BankInfoEntity();
        t.setBankCode(bankBinEntity.getBankCode().substring(0,4));// 截取前4位
        BankInfoEntity bank = this.bankInfoService.selectOne(t);
        companyCorporateAccountEntity.setBindBankName(bank.getBankName());
        companyCorporateAccountEntity.setBindBankCardNumber(po.getBindBankCardNumber());
        companyCorporateAccountEntity.setBindBankPhone(po.getBindBankPhone());
        companyCorporateAccountEntity.setUpdateTime(new Date());
        companyCorporateAccountEntity.setUpdateUser(account);
        companyCorporateAccountEntity.setSingleWithdrawalLimit(po.getSingleWithdrawalLimit());
        companyCorporateAccountEntity.setDailyWithdrawalLimit(po.getDailyWithdrawalLimit());
        mapper.updateByPrimaryKey(companyCorporateAccountEntity);

    }

    @Override
    public CompanyCorporateAccountVO queryCompanyCorporateAccountDetail(Long id) {
        CompanyCorporateAccountVO companyCorporateAccountVO=mapper.queryCompanyCorporateAccountDetail(id);
        CompanyCorporateAccountEntity companyCorporateAccountEntity=mapper.selectByPrimaryKey(id);
        // 读取渠道代付相关配置 paramsType=13
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(companyCorporateAccountVO.getOemCode(),13);
        if(null == paramsEntity){
            throw new BusinessException("未配置渠道代付相关信息！");
        }
        ComCorpAccQuery comCorpAccQuery =new ComCorpAccQuery();
        comCorpAccQuery.setTxnStffId(companyCorporateAccountEntity.getVoucherMemberCode());
        comCorpAccQuery.setDraweeAccountNo(companyCorporateAccountEntity.getCorporateAccount());


        JSONObject jsonObj=daifuApiService.queryCardBalance(paramsEntity,comCorpAccQuery);
        if(!"00".equals(jsonObj.getString("bizCode"))){
            throw  new BusinessException("查询对公户余额失败，请稍后再试。");
        }
        if(null != jsonObj){
            String balance = jsonObj.getString("accAvlBal"); // 账号可用余额
            companyCorporateAccountVO.setBalanceMoney(null == balance ? new BigDecimal(0) : new BigDecimal(balance));
        }
        return companyCorporateAccountVO;
    }


    @Override
    public List<CompanyCorpAccountVO> listComCorpAccountPage(ComCorpAccQuery query) throws BusinessException {
        log.info("分页查询企业对公户信息：{}", JSON.toJSONString(query));

        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<CompanyCorpAccountVO> list = this.mapper.listComCorpAccountPage(query);

        // 读取渠道代付相关配置 paramsType=13
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(query.getOemCode(),13);
        if(null == paramsEntity){
            throw new BusinessException("未配置渠道代付相关信息！");
        }

        // 遍历list，调用渠道接口实时查询可用余额/冻结金额等信息
        list.stream().forEach(account -> {
            query.setTxnStffId(account.getVoucherMemberCode());
            query.setDraweeAccountNo(account.getCorporateAccount());
            JSONObject jsonObj = this.daifuApiService.queryCardBalance(paramsEntity,query);
            if(null != jsonObj){
                String balance = jsonObj.getString("accAvlBal"); // 账号可用余额
                account.setAvailableAmount(null == balance ? 0L : Long.parseLong(balance));
                String freezeAmount = jsonObj.getString("frzAmt");// 冻结金额
                account.setFreezeAmount(null == freezeAmount ? 0L : Long.parseLong(freezeAmount));
            }
        });

        return list;
    }

    @Override
    public CompanyCorpAccountDetailVO getDetail(Long id) throws BusinessException {
        log.info("对公户详情查询：{}",id);

        CompanyCorporateAccountEntity entity = this.findById(id);
        if(null == entity){
            throw new BusinessException("对公户信息不存在");
        }

        CompanyCorpAccountDetailVO detailVO = this.mapper.getDetail(id);
        // 身份证号、银行卡号脱敏
        detailVO.setIdCardNumber(StringHandleUtil.desensitizeIdCardNo(detailVO.getIdCardNumber()));
        detailVO.setCorporateAccountSens(StringUtil.mark(detailVO.getCorporateAccount(), '*', 4, -4));
        String bindBankCardNumber = detailVO.getBindBankCardNumber();
        detailVO.setBindBankCardNumber(StringUtil.mark(bindBankCardNumber, '*', 4, -4));
        // 查询银行名称
        BankBinEntity bankBin = this.bankBinService.findByCardNo(bindBankCardNumber);
        // 查询银行信息
        BankInfoEntity t = new BankInfoEntity();
        t.setBankCode(bankBin.getBankCode().substring(0,4));// 截取前4位
        BankInfoEntity bank = this.bankInfoService.selectOne(t);
        detailVO.setBankName(bank.getBankName());
        return detailVO;
    }

    @Override
    public CompanyCorporateAccountEntity addCompanyCorporateAccount(CompanyCorporateAccountPO po,String account) {
        //四要素验证
        boolean flag= bankInfoService.check4ToBankCard(po.getOemCode(),po.getBankUserName(),po.getIdCard(),po.getBindBankCardNumber(),po.getBindBankPhone());
        if(!flag) {
            throw new BusinessException("银行卡四要素验证不通过！");
        }
        //新增公户信息
        CompanyCorporateAccountEntity companyCorporateAccountEntity=new CompanyCorporateAccountEntity();
        companyCorporateAccountEntity.setCompanyId(po.getCompanyId());
        companyCorporateAccountEntity.setMemberId(po.getMemberId());
        companyCorporateAccountEntity.setCorporateAccountConfigId(po.getCorporateAccountConfigId());
        //add ni.jiang 保存对公户支行信息
        ParkCorporateAccountConfigEntity configEntity = parkCorporateAccountConfigService.findById(po.getCorporateAccountConfigId());
        if(configEntity == null){
            throw new BusinessException("未找到对公户支行信息！");
        }
        companyCorporateAccountEntity.setCorporateAccountBankName(configEntity.getCorporateAccountBankName());
        //设置默认的对公户限额信息
        companyCorporateAccountEntity.setSingleWithdrawalLimit(po.getSingleWithdrawalLimit());
        companyCorporateAccountEntity.setDailyWithdrawalLimit(po.getDailyWithdrawalLimit());
        companyCorporateAccountEntity.setCorporateAccount(po.getCorporateAccount().trim());
        companyCorporateAccountEntity.setVoucherMemberCode(po.getVoucherMemberCode());
        companyCorporateAccountEntity.setEntrustProjectCode(po.getEntrustProjectCode());
        companyCorporateAccountEntity.setProjectUseCode(po.getProjectUseCode());
        companyCorporateAccountEntity.setEntrustProjectCodeWj(po.getEntrustProjectCodeWj());
        companyCorporateAccountEntity.setProjectUseCodeWj(po.getProjectUseCodeWj());
        companyCorporateAccountEntity.setBindBankCardNumber(po.getBindBankCardNumber());
        companyCorporateAccountEntity.setHeadquartersName(po.getHeadquartersName());
        companyCorporateAccountEntity.setHeadquartersNo(po.getHeadquartersNo());
        BankBinEntity bankBinEntity= bankBinService.findByCardNo(po.getBindBankCardNumber());
        if(bankBinEntity == null){
            throw new BusinessException("银行卡号错误，请更换银行卡");
        }
        // 查询银行信息
        BankInfoEntity t = new BankInfoEntity();
        t.setBankCode(bankBinEntity.getBankCode().substring(0,4));// 截取前4位
        BankInfoEntity bank = this.bankInfoService.selectOne(t);
        if(bank == null){
            throw new BusinessException("银行卡号错误，请更换银行卡");
        }
        companyCorporateAccountEntity.setBindBankName(bank.getBankName());
        companyCorporateAccountEntity.setBindBankCode(bankBinEntity.getBankCode());
        companyCorporateAccountEntity.setBindBankPhone(po.getBindBankPhone());
        companyCorporateAccountEntity.setStatus(1);
        companyCorporateAccountEntity.setOverdueStatus(1);
        companyCorporateAccountEntity.setIsSendNotice(0);
        companyCorporateAccountEntity.setCancelCredentials(null);
        //日期加1年
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String stringDate = sdf.format(date);//date-->String

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, 1); //把日期往后增加一年，整数往后推，负数往前移

        companyCorporateAccountEntity.setExpirationTime(calendar.getTime());
        companyCorporateAccountEntity.setAddTime(new Date());
        companyCorporateAccountEntity.setAddUser(account);

        mapper.insert(companyCorporateAccountEntity);
        //发送短信
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", po.getCompanyName());
        smsService.sendTemplateSms(po.getMemberPhone(), po.getOemCode(), VerifyCodeTypeEnum.COMPANY_CORPORATE_ACCOUNT_SUCCESS.getValue(), map, UserTypeEnum.MEMBER.getValue());
        return companyCorporateAccountEntity;
    }

    @Override
    public PageInfo<CompanyCorporateAccountDetailVO> queryCompanyCorporateAccountDetailList(CompanyCorporateAccountDetailQuery query) {
        return null;
    }

    @Override
    public CorporateAccountBankCardVO queryCorpBankCardInfo(Long corporateAccountId,String oemCode) throws BusinessException {
        log.info("根据对公户ID查询收款个人银行卡信息:{},{}",corporateAccountId,oemCode);

        CompanyCorporateAccountEntity entity = this.findById(corporateAccountId);
        if(null == entity){
            throw new BusinessException("获取银行卡信息失败，对公户不存在");
        }
        // 读取渠道代付相关配置 paramsType=13
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode,13);
        if(null == paramsEntity){
            throw new BusinessException("未配置渠道代付相关信息！");
        }

        CorporateAccountBankCardVO bankCardVO = new CorporateAccountBankCardVO();
        bankCardVO.setBankName(entity.getBindBankName());
        bankCardVO.setBankCardNumber(entity.getBindBankCardNumber());
        // 根据银行编码查询logo
        String bankCode = entity.getBindBankCode().substring(0,4);
        BankInfoEntity t = new BankInfoEntity();
        t.setBankCode(bankCode);
        t = this.bankInfoService.selectOne(t);
        bankCardVO.setLogo(t.getBankLogoUrl());
        // 查询卡类型
        BankBinEntity bin = this.bankBinService.findByCardNo(entity.getBindBankCardNumber());
        if(bin!=null) {
            if(ObjectUtil.equal(bin.getCardType(),1)){
                bankCardVO.setCardType(2);
            }else if(ObjectUtil.equal(bin.getCardType(),2)){
                bankCardVO.setCardType(1);
            }else {
                bankCardVO.setCardType(bin.getCardType());
            }
        }

        // 调用渠道接口实时查询银行卡可用余额
        ComCorpAccQuery query = new ComCorpAccQuery();
        query.setDraweeAccountNo(entity.getCorporateAccount());
        query.setTxnStffId(entity.getVoucherMemberCode());
        JSONObject jsonObj = this.daifuApiService.queryCardBalance(paramsEntity,query);
        if(null != jsonObj){
            String balance = jsonObj.getString("accAvlBal"); // 账号可用余额
            bankCardVO.setAvailableAmount(null == balance ? 0L : Long.parseLong(balance));
        }

        // 查询单笔（日）限额信息
        MemberCompanyEntity company = this.memberCompanyService.findById(entity.getCompanyId());
        if(null == company){
            throw new BusinessException("企业信息不存在");
        }
        bankCardVO.setCompanyName(company.getCompanyName());

        if (null == entity.getSingleWithdrawalLimit() || 0 == entity.getSingleWithdrawalLimit()) {
            throw new BusinessException("对公户单笔提现限额未配置");
        }
        if (null == entity.getDailyWithdrawalLimit() || 0 == entity.getDailyWithdrawalLimit()) {
            throw new BusinessException("对公户单日提现限额未配置");
        }
        bankCardVO.setDayLimit(entity.getDailyWithdrawalLimit());
        bankCardVO.setSingleLimit(entity.getSingleWithdrawalLimit());

        return bankCardVO;
    }

    @Override
    public List<CorporateInvoiceOrderVO> listInvoiceOrderForCorp(CorporateAccountCollectionRecordQuery query) throws BusinessException {
        log.info("对公户提现-选择开票记录:{}",JSON.toJSONString(query));

        // 处理查询条件，时间格式化处理 YYYY-MM-DD HH:mm:ss
       if(StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isNotBlank(query.getEndDate())&& query.getStartDate().equals(query.getEndDate()) && "-1".equals(query.getStartDate())){
           query.setStartDate(null);
           query.setEndDate(null);
       }else if(StringUtils.isBlank(query.getMonth()) && StringUtils.isBlank(query.getStartDate()) && StringUtils.isBlank(query.getEndDate())){
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

        // 判断对公户是否存在
        CompanyCorporateAccountEntity entity = this.findById(query.getCorporateAccountId());
        if (null == entity) {
            throw new BusinessException("查询失败，对公户不存在");
        }
        query.setCompanyId(entity.getCompanyId());

        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<CorporateInvoiceOrderVO> list = this.mapper.listInvoiceOrderForCorp(query);

        return list;
    }

    @Override
    public CompanyCorpAccApplyDetailVO applyDetail(String oemCode, Long memberId, Long parkId) throws BusinessException {
        CompanyCorpAccApplyDetailVO companyCorpAccApplyDetailVO = new CompanyCorpAccApplyDetailVO();

        if (null == parkId) {
            throw new BusinessException("园区id为空");
        }

        // 通过oemCode查询已配置对公户提现的园区
        Map<String, String> parkMap = oemParkRelaService.queryOemParkCorporate(oemCode);
        if (null == parkMap || parkMap.isEmpty()) {
            throw new BusinessException("暂不支持办理对公户");
        }
        companyCorpAccApplyDetailVO.setParkName(parkMap.get("parkName"));

        // 查询对公户申请收费标准
        ProductEntity product = new ProductEntity();
        product.setOemCode(oemCode);
        product.setProdType(ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue());
        product.setStatus(ProductStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
        product = productService.selectOne(product);
        if (null == product) {
            throw new BusinessException("产品不存在或未上架");
        }
        companyCorpAccApplyDetailVO.setProdAmount(product.getProdAmount());
        companyCorpAccApplyDetailVO.setProcessingFee(product.getProcessingFee());
        // 查询园区是否存在单独定价
        ProductByParkEntity productByParkEntity = new ProductByParkEntity();
        productByParkEntity.setProductId(product.getId());
        productByParkEntity.setParkId(parkId);
        productByParkEntity.setIsDelete(0);
        ProductByParkEntity productByPark = productByParkService.selectOne(productByParkEntity);
        if (null != productByPark) {
            companyCorpAccApplyDetailVO.setProdAmount(productByPark.getProdAmount());
            companyCorpAccApplyDetailVO.setProcessingFee(productByPark.getProcessingFee());
        }
        // 查询是否存在特价活动价
        Long crowLabelId= memberCrowdLabelRelaService.getCrowLabelIdByMemberId(memberId, oemCode);
        ProductDiscountActivityEntity productDiscountActivityEntity = productDiscountActivityService.queryByCrowdLabel(crowLabelId, ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue());
        if (null != productDiscountActivityEntity) {
            companyCorpAccApplyDetailVO.setProcessingFee(productDiscountActivityEntity.getProcessingFee());
            companyCorpAccApplyDetailVO.setProdAmount(productDiscountActivityEntity.getSpecialPriceAmount());
        }
        //查询对公户申请银行收费标准
        Example example = new Example(ParkCorporateAccountConfigEntity.class);
        example.createCriteria().andEqualTo("status", ParkCorporateAccountConfigStatusEnum.AVAILABLE.getValue());
        example.createCriteria().andIn("parkId", Collections.singleton(parkMap.get("parkId")));
        example.orderBy("addTime").desc();
        List<ParkCorporateAccountConfigEntity> list = parkCorporateAccountConfigService.selectByExample(example);
        if (CollectionUtil.isEmpty(list)) {
            throw new BusinessException("对公户申请银行收费标准不存在");
        }
        companyCorpAccApplyDetailVO.setBankActivationFee(list.get(0).getBankActivationFee());
        companyCorpAccApplyDetailVO.setBankOtherFee(list.get(0).getBankOtherFee());
        companyCorpAccApplyDetailVO.setBankWithdrawalFee(list.get(0).getBankWithdrawalFee());
        companyCorpAccApplyDetailVO.setSingleWithdrawalLimit(list.get(0).getSingleWithdrawalLimit());
        companyCorpAccApplyDetailVO.setDailyWithdrawalLimit(list.get(0).getDailyWithdrawalLimit());
        return companyCorpAccApplyDetailVO;
    }

    @Override
    public List<CompanyCorporateAccountHandlerVO> queryAccountHandlerList() {
        return mapper.queryAccountHandlerList();
    }

    @Override
    public CompanyCorporateAccountEntity queryCorpByCompanyId(Long companyId) {
        return mapper.queryCorpByCompanyId(companyId);
    }

    @Override
    public int applicationConditionCheck(Long memberId) {
        log.info("用户申请对公户条件校验开始：{}", memberId);
        // 查询用户并校验用户状态
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(memberId)).orElseThrow(() -> new BusinessException("未查询到用户"));
        if (!Objects.equals(1, member.getStatus())) {
            log.info("用户申请对公户条件校验结束，用户非正常状态，不可申请对公户");
            return 0;
        }
        if (!MemberAuthStatusEnum.AUTH_SUCCESS.getValue().equals(member.getAuthStatus())) {
            log.info("用户申请对公户条件校验结束，用户未实名，不可申请对公户");
            return 0;
        }

        // 根据机构园区、园区产品关系查询可用“对公户申请”产品
        ProductEntity product = productService.queryProductByRelation(memberId, member.getOemCode(), 15);
        if (null == product) {
            log.info("用户申请对公户条件校验结束");
            return 0;
        }

        return 1;
    }

    @Override
    public List<CompanyCorpAccountVO> queryByMemberId(Long memberId) {
        ComCorpAccQuery query = new ComCorpAccQuery();
        query.setCurrUserId(memberId);
        return mapper.listComCorpAccountPage(query);
    }

    @Override
    public CompanyCorpAccRenewDetailVO companyCorpAccRenewDetail(Long companyCorpAccId, Long currUserId) {
        // 参数校验
        if (null == companyCorpAccId || null == currUserId) {
            throw new BusinessException("参数为空");
        }
        // 查询用户信息
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(currUserId)).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        // 查询对公户信息
        CompanyCorporateAccountEntity companyCorporateAccount = Optional.ofNullable(this.findById(companyCorpAccId)).orElseThrow(() -> new BusinessException("未查询到对公户信息"));
        if (!Objects.equals(member.getId(), companyCorporateAccount.getMemberId())) {
            throw new BusinessException("所查询的对公户不属于当前登录用户");
        }

        // 查询企业
        MemberCompanyEntity company = memberCompanyService.findById(companyCorporateAccount.getCompanyId());
        if (null == company) {
            throw new BusinessException("未查询到企业信息");
        }

        // 查询对公户续费产品
        ProductEntity productEntity = productService.queryProductByProdType(ProductTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue(), company.getOemCode(), company.getParkId());
        if (null == productEntity) {
            throw new BusinessException("未查询到对公户续费产品");
        }
        if (!ProductStatusEnum.ON_SHELF.getValue().equals(productEntity.getStatus())) {
            throw new BusinessException("产品未上架或已暂停");
        }

        // 查询对公户年费续费信息
        CompanyCorpAccRenewDetailVO vo = mapper.companyCorpAccRenewDetail(companyCorpAccId);
        if (null == vo) {
            throw new BusinessException("对公户信息异常");
        }

        /**
         *  判断是否存在特价活动，
         *  如果存在，订单金额 = 特价活动金额，支付金额取 = 特价活动*折扣， 优惠金额 = 订单金额 - 支付金额取
         */
        ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO = new ProductDiscountActivityAPIDTO();
        productDiscountActivityAPIDTO.setOemCode(company.getOemCode());
        productDiscountActivityAPIDTO.setMemberId(member.getId());
        productDiscountActivityAPIDTO.setIndustryId(company.getIndustryId());
        productDiscountActivityAPIDTO.setParkId(company.getParkId());
        productDiscountActivityAPIDTO.setProductType(ProductTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue());
        ProductDiscountActivityVO productDiscountActivityVO = productDiscountActivityService.getProductDiscountActivityByProductType(productDiscountActivityAPIDTO);
        if (null != productDiscountActivityVO) {
            vo.setProdAmount(productDiscountActivityVO.getSpecialPriceAmount());
        }

        return vo;
    }

    @Override
    public void updateOverdueStatus(Long id, Date expirationTime) {
        Integer surplusDays = DEFAULT_SURPLUS_DAYS;

        // 查询系统配置到期有效提醒天数
        DictionaryEntity dict = dictionaryService.getByCode("expire_surplus_days");
        if (Objects.nonNull(dict)){
            // 取系统配置天数
            surplusDays = Integer.parseInt(dict.getDictValue());
        }
        mapper.updateOverdueStatus(id, expirationTime, surplusDays);
    }

    @Override
    public List<OverdueCompanyCropAccInfoVO> getOverdueCompanyCropAcc(Integer overdueDays) {
        return mapper.queryOverdueCompanyCropAcc(overdueDays);
    }

    @Override
    public List<OverdueCompanyCropAccInfoVO> getWillExpireCompanyCropAcc(Integer surplusDays) {
        return mapper.queryWillExpireCompanyCropAcc(surplusDays);
    }

    @Override
    public void updateCompanyCorpAccOverdueStatus(Integer surplusDays) {
        mapper.updateCompanyCorpAccOverdueStatus(surplusDays);
    }

    @Override
    public void updateOverdueCorpAccStatus() {
        mapper.updateOverdueCorpAccStatus();
    }

    @Override
    public Map<String, String> applyUsablePark(String oemCode) {
        // 通过oemCode查询已配置对公户提现的园区
        Map<String, String> parkMap = oemParkRelaService.queryOemParkCorporate(oemCode);
        return parkMap;
    }
}

