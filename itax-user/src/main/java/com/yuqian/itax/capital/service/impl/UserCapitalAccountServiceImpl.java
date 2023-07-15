package com.yuqian.itax.capital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.entity.po.OemPO;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.dao.UserBankCardMapper;
import com.yuqian.itax.capital.dao.UserCapitalAccountMapper;
import com.yuqian.itax.capital.dao.UserCapitalChangeRecordMapper;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.entity.UserCapitalChangeRecordEntity;
import com.yuqian.itax.capital.entity.dto.UserCapitalAccountDTO;
import com.yuqian.itax.capital.entity.query.UserCapitalAccountQuery;
import com.yuqian.itax.capital.entity.vo.MemberCapitalAccountApiVO;
import com.yuqian.itax.capital.entity.vo.MemberCapitalAccountVO;
import com.yuqian.itax.capital.entity.vo.ProfitDetailVO;
import com.yuqian.itax.capital.entity.vo.UserCapitalAccountVO;
import com.yuqian.itax.capital.enums.CapitalChangeTypeEnum;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.entity.SmsTemplateEntity;
import com.yuqian.itax.message.service.SmsTemplateService;
import com.yuqian.itax.park.entity.ParkPO;
import com.yuqian.itax.system.entity.BankBinEntity;
import com.yuqian.itax.system.entity.BannerEntity;
import com.yuqian.itax.system.entity.CommonProblemsEntity;
import com.yuqian.itax.system.service.BankBinService;
import com.yuqian.itax.system.service.BannerService;
import com.yuqian.itax.system.service.CommonProblemsService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberLevelEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.po.AgentPO;
import com.yuqian.itax.user.entity.po.UserPO;
import com.yuqian.itax.user.enums.MemberAuthStatusEnum;
import com.yuqian.itax.user.enums.MemberLevelEnum;
import com.yuqian.itax.user.enums.MemberTypeEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberLevelService;
import com.yuqian.itax.user.service.MemberProfitsRulesService;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.util.util.AuthKeyUtils;
import com.yuqian.itax.util.util.UniqueNumGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service("userCapitalAccountService")
public class UserCapitalAccountServiceImpl extends BaseServiceImpl<UserCapitalAccountEntity,UserCapitalAccountMapper> implements UserCapitalAccountService {

    @Autowired
    UserService userService;
    @Autowired
    MemberAccountService memberAccountService;
    @Resource
    private UserCapitalChangeRecordMapper userCapitalChangeRecordMapper;
    @Resource
    UserBankCardMapper userBankCardMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BankBinService bankBinService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private OemService oemService;
    @Autowired
    private  MemberProfitsRulesService memberProfitsRulesService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    BannerService bannerService;
    @Autowired
    SmsTemplateService smsTemplateService;
    @Autowired
    CommonProblemsService commonProblemsService;
    @Autowired
    private UserBankCardService userBankCardService;

    @Override
    public PageInfo<UserCapitalAccountVO> queryUserCapitalAccountPageInfo(UserCapitalAccountQuery userCapitalAccountQuery) {
        PageHelper.startPage(userCapitalAccountQuery.getPageNumber(),userCapitalAccountQuery.getPageSize());
        return new PageInfo<>(mapper.queryUserCapitalAccountList(userCapitalAccountQuery));
    }

    @Override
    public List<UserCapitalAccountVO> queryUserCapitalAccountList(UserCapitalAccountQuery userCapitalAccountQuery) {
        return mapper.queryUserCapitalAccountList(userCapitalAccountQuery);
    }

    @Override
    public UserCapitalAccountEntity getUserCapitalAccountByUserId(Long userId) {
        return mapper.getUserCapitalAccountByUserId(userId);
    }

    @Override
    @Transactional (rollbackFor =  Exception.class)
    public void addAgentCapitalAccount(AgentPO agentPO, String userAccount) throws BusinessException {
        MemberAccountEntity memberAccountEntity = memberAccountService.queryByAccount(agentPO.getBindingAccount(),agentPO.getOemCode());
        if(memberAccountEntity!=null){
            throw  new BusinessException("推广账号手机号已存在，请更换！");
        }

        //新增用户
        UserEntity userEntity=userService.addAgent(agentPO,userAccount);

        //新增一个城市服务商账号和系统账号绑定
        MemberAccountEntity account = memberAccountService.registerAccount(agentPO.getBindingAccount(),userEntity.getOemCode(),"", MemberLevelEnum.DIAMOND.getValue(),userEntity,null,1,null,null);
        userService.editByIdSelective(userEntity);

        //判断是否需要绑定银行卡
        if(Objects.equals(1,agentPO.getIsBank())){
            BankBinEntity bank = bankBinService.findByCardNo(agentPO.getBankNumber());
            if(null == bank){
                throw  new BusinessException("未找到卡信息");
            }
            if(bank.getCardType()==1){
                throw  new BusinessException("不支持信用卡");
            }
            if(null == agentPO.getBankPhone() || null == agentPO.getBankUserName() || null == agentPO.getBankNumber() || null == agentPO.getBankName() || null == agentPO.getIdCard()){
                throw  new BusinessException("银行卡参数不正确");
            }
            /**
             * 银行卡四要素验证
             */
            //读取要素认证相关配置 paramsType=6
            OemParamsEntity paramsEntity = oemParamsService.getParams(agentPO.getOemCode(), 6);
            if(null == paramsEntity){
                throw new BusinessException("未配置银行卡四要素相关信息！");
            }
            // agentNo
            String agentNo = paramsEntity.getAccount();
            // signKey
            String signKey = paramsEntity.getSecKey();
            // authUrl
            String authUrl = paramsEntity.getUrl();

            String authResult = AuthKeyUtils.auth4Key(agentNo,signKey,authUrl,agentPO.getBankUserName(),agentPO.getIdCard(),
                    agentPO.getBankNumber(),agentPO.getBankPhone(),paramsEntity.getParamsValues());

            if(StringUtils.isBlank(authResult)){
                throw new BusinessException("银行卡四要素认证失败");
            }
            JSONObject resultObj = JSONObject.parseObject(authResult);

            if(!"00".equals(resultObj.getString("code"))){
                throw new BusinessException("银行卡四要素认证失败：" + resultObj.getString("msg"));
            }
            UserBankCardEntity userBankCardEntity =new UserBankCardEntity();
            BeanUtils.copyProperties(agentPO, userBankCardEntity);
            userBankCardEntity.setBankCardType(1);
            userBankCardEntity.setBankCode(bank.getBankCode());
            userBankCardEntity.setBankName(bank.getBankName());
            userBankCardEntity.setUserType(2);
            userBankCardEntity.setUserName(agentPO.getBankUserName());
            userBankCardEntity.setPhone(agentPO.getBankPhone());
            //增加银行卡管理t_e_bank_card
            insertBankCardEntity(userBankCardEntity, userAccount,  userEntity);
            //添加会员银行卡管理
            UserBankCardEntity memberBankCardEntity =new UserBankCardEntity();
            BeanUtils.copyProperties(agentPO, memberBankCardEntity);
            memberBankCardEntity.setBankCardType(1);
            memberBankCardEntity.setBankCode(bank.getBankCode());
            memberBankCardEntity.setBankName(bank.getBankName());
            memberBankCardEntity.setUserType(1);
            memberBankCardEntity.setUserName(agentPO.getBankUserName());
            memberBankCardEntity.setPhone(agentPO.getBankPhone());
            insertBankCardEntity(memberBankCardEntity, userAccount,  account);
            //会员添加实名信息
            memberAccountService.updateUserAuth(account.getId(),agentPO.getBankUserName(),agentPO.getIdCard(), null,null,null,null, MemberAuthStatusEnum.AUTH_SUCCESS.getValue(),"代理商绑定银行卡实名");

        }
        //资金账号
        insertUserCapitalAccountEntity( userAccount,  userEntity);
    }

    @Override
    @Transactional (rollbackFor= Exception.class)
    public void addOemCapitalAccount(OemPO oemPO, String userAccount) throws BusinessException {
        //新增用户
        UserEntity userEntity=userService.addOem(oemPO,userAccount);
        //添加默认的配置
        insertDefaultSetting(userEntity.getOemCode(),userAccount);
        //初始化机构会员政策
        memberProfitsRulesService.initMemberProfitsRulesByOem(oemPO.getOemCode(),userAccount);

        //判断是否需要绑定银行卡
        if(oemPO.getIsbank()==1){
            BankBinEntity bank = bankBinService.findByCardNo(oemPO.getBankNumber());
            if(null == bank){
                throw  new BusinessException("未找到卡信息");
            }
            if(bank.getCardType()==1){
                throw  new BusinessException("不支持信用卡");
            }
            if(null == oemPO.getBankPhone() || null == oemPO.getBankUserName() || null == oemPO.getBankNumber() || null == oemPO.getBankName() || null == oemPO.getIdCard()){
                throw  new BusinessException("银行卡参数不正确");
            }
            /**
             * 银行卡四要素验证
             */
            //读取要素认证相关配置 paramsType=6
            OemParamsEntity paramsEntity = oemParamsService.getParams(oemPO.getOemCode(), 6);
            if(null == paramsEntity){
                throw new BusinessException("未配置银行卡四要素相关信息！");
            }
            // agentNo
            String agentNo = paramsEntity.getAccount();
            // signKey
            String signKey = paramsEntity.getSecKey();
            // authUrl
            String authUrl = paramsEntity.getUrl();

            String authResult = AuthKeyUtils.auth4Key(agentNo,signKey,authUrl,oemPO.getBankUserName(),oemPO.getIdCard(),
                    oemPO.getBankNumber(),oemPO.getBankPhone(),paramsEntity.getParamsValues());

            if(StringUtils.isBlank(authResult)){
                throw new BusinessException("银行卡四要素认证失败");
            }
            JSONObject resultObj = JSONObject.parseObject(authResult);

            if(!"00".equals(resultObj.getString("code"))){
                throw new BusinessException("银行卡四要素认证失败：" + resultObj.getString("msg"));
            }
            UserBankCardEntity userBankCardEntity =new UserBankCardEntity();
            BeanUtils.copyProperties(oemPO, userBankCardEntity);
            userBankCardEntity.setBankCardType(1);
            userBankCardEntity.setUserName(oemPO.getBankUserName());
            userBankCardEntity.setPhone(oemPO.getBankPhone());
            userBankCardEntity.setUserType(2);
            //增加银行卡管理t_e_bank_card
            insertBankCardEntity(userBankCardEntity,  userAccount,  userEntity);
        }
        //资金账号
        insertUserCapitalAccountEntity( userAccount,userEntity);
        //增加平台资金账号
        UserCapitalAccountEntity userCapitalAccountEntity=new UserCapitalAccountEntity();
        userCapitalAccountEntity.setOemCode(userEntity.getOemCode());
        userCapitalAccountEntity.setUserId(1L);
        userCapitalAccountEntity.setUserType(2);
        userCapitalAccountEntity.setCapitalAccount(UniqueNumGenerator.generateUniqueNo());
        userCapitalAccountEntity.setTotalAmount(0L);
        userCapitalAccountEntity.setAvailableAmount(0L);
        userCapitalAccountEntity.setBlockAmount(0L);
        userCapitalAccountEntity.setOutstandingAmount(0L);
        userCapitalAccountEntity.setStatus(1);
        userCapitalAccountEntity.setAddTime(new Date());
        userCapitalAccountEntity.setAddUser(userAccount);
        userCapitalAccountEntity.setWalletType(1);
        this.mapper.insert(userCapitalAccountEntity);    }


    void insertDefaultSetting(String oemCode,String account){
        //1.短信模板
        SmsTemplateEntity smsTemplateEntity=new SmsTemplateEntity();
        smsTemplateEntity.setOemCode("YCS");
        List<SmsTemplateEntity> listSmsTemplate =smsTemplateService.select(smsTemplateEntity);
        //批量插入短信模板
        smsTemplateService.batchInsertSmsTemplateEntity(listSmsTemplate,oemCode,account);
        //2.常见问题
        CommonProblemsEntity commonProblemsEntity=new CommonProblemsEntity();
        commonProblemsEntity.setOemCode("YCS");
        List<CommonProblemsEntity> listCommonProblems=commonProblemsService.select(commonProblemsEntity);
        //批量插入常见问题
        commonProblemsService.batchInsertCommonProblems(listCommonProblems,oemCode,account);
        //3.banner
        BannerEntity bannerEntity=new BannerEntity();
        bannerEntity.setOemCode("YCS");
        List<BannerEntity> listBannerEntity=bannerService.select(bannerEntity);
        //批量插入banner信息
        bannerService.batchInsertBannerEntity(listBannerEntity,oemCode,account);
        //4.会员等级
        MemberLevelEntity memberLevelEntity=new MemberLevelEntity();
        memberLevelEntity.setOemCode("YCS");
        List<MemberLevelEntity> listMemberLevelEntity=memberLevelService.select(memberLevelEntity);
        //批量插入会员等级
        memberLevelService.addBatchMemberLevelEntity(listMemberLevelEntity,oemCode,account);
    }


    /**
     * 新增园区
     * @param parkPO
     * @param userAccount
     * @throws BusinessException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addParkCapitalAccount(ParkPO parkPO, String userAccount) throws BusinessException {
        //新增用户
        UserEntity userEntity = userService.addPark(parkPO,userAccount);

        //资金账号
        insertUserCapitalAccountEntity( userAccount,userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity addUserCapitalAccount(UserPO userPO, String userAccount) throws BusinessException {

        //新增用户
        UserEntity userEntity=userService.addUser(userPO,userAccount);

        //资金账号
        insertUserCapitalAccountEntity( userAccount,userEntity);
        return  userEntity;
    }




    /**
     * 增加资金账号
     * @athu HZ
     * @param userEntity
     * @return
     */
    public  UserCapitalAccountEntity insertUserCapitalAccountEntity( String userAccount, UserEntity userEntity){
        //资金账号
        UserCapitalAccountEntity userCapitalAccountEntity=new UserCapitalAccountEntity();
        userCapitalAccountEntity.setOemCode(userEntity.getOemCode());
        userCapitalAccountEntity.setUserId(userEntity.getId());
        userCapitalAccountEntity.setUserType(2);
        userCapitalAccountEntity.setCapitalAccount(UniqueNumGenerator.generateUniqueNo());
        userCapitalAccountEntity.setTotalAmount(0L);
        userCapitalAccountEntity.setAvailableAmount(0L);
        userCapitalAccountEntity.setBlockAmount(0L);
        userCapitalAccountEntity.setOutstandingAmount(0L);
        userCapitalAccountEntity.setStatus(1);
        userCapitalAccountEntity.setAddTime(new Date());
        userCapitalAccountEntity.setAddUser(userAccount);
        userCapitalAccountEntity.setWalletType(1);
        this.mapper.insert(userCapitalAccountEntity);
        return userCapitalAccountEntity;
    }
    /**
     * 绑定银行卡
     * @atho HZ
     * @param userEntity
     * @return
     */
    @Override
    public UserBankCardEntity insertBankCardEntity(UserBankCardEntity userBankCardEntityPO, String userAccount, UserEntity userEntity){
        //增加银行卡管理t_e_bank_card
        UserBankCardEntity userBankCardEntity = new UserBankCardEntity();
        userBankCardEntity.setUserId(userEntity.getId());
        userBankCardEntity.setOemCode(userEntity.getOemCode());
        userBankCardEntity.setBankName(userBankCardEntityPO.getBankName());
        userBankCardEntity.setBankCode(userBankCardEntityPO.getBankCode());
        userBankCardEntity.setUserName(userBankCardEntityPO.getUserName());
        userBankCardEntity.setIdCard(userBankCardEntityPO.getIdCard());
        userBankCardEntity.setPhone(userBankCardEntityPO.getPhone());
        userBankCardEntity.setBankNumber(userBankCardEntityPO.getBankNumber());
        userBankCardEntity.setUserType(userBankCardEntityPO.getUserType());
        userBankCardEntity.setStatus(2);
        userBankCardEntity.setBankCardType(userBankCardEntityPO.getBankCardType());
        userBankCardEntity.setAddTime(new Date());
        userBankCardEntity.setAddUser(userAccount);
        userBankCardMapper.insert(userBankCardEntity);
        return userBankCardEntity;
    }
    /**
     * 会员绑定银行卡
     * @atho HZ
     * @param memberAccountEntity
     * @return
     */
    public UserBankCardEntity insertBankCardEntity(UserBankCardEntity userBankCardEntityPO, String userAccount, MemberAccountEntity memberAccountEntity){
        //增加银行卡管理t_e_bank_card
        UserBankCardEntity userBankCardEntity = new UserBankCardEntity();
        userBankCardEntity.setUserId(memberAccountEntity.getId());
        userBankCardEntity.setOemCode(memberAccountEntity.getOemCode());
        userBankCardEntity.setBankName(userBankCardEntityPO.getBankName());
        userBankCardEntity.setBankCode(userBankCardEntityPO.getBankCode());
        userBankCardEntity.setUserName(userBankCardEntityPO.getUserName());
        userBankCardEntity.setIdCard(userBankCardEntityPO.getIdCard());
        userBankCardEntity.setPhone(userBankCardEntityPO.getPhone());
        userBankCardEntity.setBankNumber(userBankCardEntityPO.getBankNumber());
        userBankCardEntity.setUserType(userBankCardEntityPO.getUserType());
        userBankCardEntity.setStatus(2);
        userBankCardEntity.setBankCardType(userBankCardEntityPO.getBankCardType());
        userBankCardEntity.setAddTime(new Date());
        userBankCardEntity.setAddUser(userAccount);
        userBankCardMapper.insert(userBankCardEntity);
        return userBankCardEntity;
    }
    @Override
    public MemberCapitalAccountVO getBalance(Long userId, String oemCode){
        MemberCapitalAccountVO capital = new MemberCapitalAccountVO();

        // 查询用户信息
        MemberAccountEntity member = memberAccountService.findById(userId);
        if (null == member) {
            throw new BusinessException("未查询到用户信息");
        }
        capital.setAuthStatus(member.getAuthStatus());

        // 查询用户银行卡信息
        UserBankCardEntity userBankCard = userBankCardService.getBankCardInfoByUserIdAndUserType(userId, 1, oemCode);
        if (null != userBankCard) {
            capital.setIsBindBankCard(1);
        }

        // 查询会员消费钱包信息
        UserCapitalAccountEntity consumerCapital = new UserCapitalAccountEntity();
        consumerCapital.setUserType(1);//用户类型 1-会员 2 -系统用户
        consumerCapital.setUserId(userId);
        //consumerCapital.setStatus(1);//状态 0-禁用 1-可用
        consumerCapital.setOemCode(oemCode);
        consumerCapital.setWalletType(1);//钱包类型 1-消费钱包 2-佣金钱包
        consumerCapital = this.mapper.selectOne(consumerCapital);
        if(null == consumerCapital){
            capital.setConsumerUsableAmount(0L);
        }else{
            capital.setConsumerUsableAmount(consumerCapital.getAvailableAmount());
        }

        // 查询会员佣金钱包信息
        UserCapitalAccountEntity commissionCapital = new UserCapitalAccountEntity();
        commissionCapital.setUserType(1);//用户类型 1-会员 2 -系统用户
        commissionCapital.setUserId(userId);
        //commissionCapital.setStatus(1);//状态 0-禁用 1-可用
        commissionCapital.setOemCode(oemCode);
        commissionCapital.setWalletType(2);//钱包类型 1-消费钱包 2-佣金钱包
        commissionCapital = this.mapper.selectOne(commissionCapital);
        if(null == commissionCapital){
            capital.setCommissionUsableAmount(0L);
            capital.setCommissionOutstandingAmount(0L);
        }else{
            capital.setCommissionUsableAmount(commissionCapital.getAvailableAmount());
            capital.setCommissionOutstandingAmount(commissionCapital.getOutstandingAmount());
        }
        // 查询佣金钱包提现手续费
        OemEntity oem = this.oemService.getOem(oemCode);
        capital.setCommissionServiceFeeRate(oem.getCommissionServiceFeeRate());
        capital.setDiamondCommissionServiceFeeRate(oem.getDiamondCommissionServiceFeeRate());

        // 查询提现最小限额
        capital.setMinConsumptionWalletLimit(oem.getMinConsumptionWalletLimit());
        capital.setMinCommissionWalletLimit(oem.getMinCommissionWalletLimit());

        // 查询佣金钱包提现分润记录信息
        List<ProfitDetailVO> list = getProfitDetailListForWithdraw(userId, oemCode);
        if (CollectionUtil.isEmpty(list)) {
            return capital;
        }
        capital.setAllAmountProfitDetail(list.stream().mapToLong(ProfitDetailVO::getProfitsAmount).sum());
        capital.setQuantityProfitDetail((long) list.size());
        capital.setMaximalProfitDetailId(list.get(0).getId());

        return capital;
    }

    private List<ProfitDetailVO> getProfitDetailListForWithdraw(Long userId, String oemCode) {
        return mapper.getProfitDetailListForWithdraw(userId, oemCode);
    }

    @Override
    @Transactional
    public synchronized void addBalance(String oemCode,String orderNo, Integer orderType, Long userId, Integer userType, Long amount,String detailDesc, String updateUser, Date updateTime,Integer walletType) throws BusinessException {
        UserCapitalAccountEntity accEntity = mapper.queryByUserIdAndType(userId, userType,oemCode,walletType);
        if (accEntity == null) {
            throw new BusinessException("资金账户不存在");
        }
        //增加资金变动
        UserCapitalChangeRecordEntity record = new UserCapitalChangeRecordEntity();
        record.setAddTime(updateTime);
        record.setAddUser(updateUser);
        record.setCapitalAccountId(accEntity.getId());
        record.setChangesAmount(amount);
        record.setChangesType(CapitalChangeTypeEnum.INCOME.getValue());
        record.setChangesBeforeAmount(accEntity.getTotalAmount());
        record.setChangesAfterAmount(accEntity.getTotalAmount() + amount);
        record.setOemCode(accEntity.getOemCode());
        record.setOrderNo(orderNo);
        record.setOrderType(orderType);
        record.setUserId(userId);
        record.setUserType(userType);
        record.setDetailDesc(detailDesc);
        record.setWalletType(accEntity.getWalletType());
        userCapitalChangeRecordMapper.insertSelective(record);
        mapper.addBalance(userId, userType,walletType, amount, updateUser, updateTime);
    }

    @Override
    @Transactional
    public synchronized void minusBalance(String oemCode,String orderNo, Integer orderType, Long userId, Integer userType, Long amount,String detailDesc, String updateUser, Date updateTime,Integer walletType) throws BusinessException {
        UserCapitalAccountEntity accEntity = mapper.queryByUserIdAndType(userId, userType,oemCode,walletType);
        if (accEntity == null) {
            throw new BusinessException("资金账户不存在");
        }
        //保存资金变动记录
        UserCapitalChangeRecordEntity record = new UserCapitalChangeRecordEntity();
        record.setAddTime(updateTime);
        record.setAddUser(updateUser);
        record.setCapitalAccountId(accEntity.getId());
        record.setChangesAmount(amount);
        record.setChangesType(CapitalChangeTypeEnum.EXPENDITURE.getValue());
        record.setChangesBeforeAmount(accEntity.getTotalAmount());
        record.setChangesAfterAmount(accEntity.getTotalAmount() + amount);
        record.setOemCode(accEntity.getOemCode());
        record.setOrderNo(orderNo);
        record.setOrderType(orderType);
        record.setUserId(userId);
        record.setUserType(userType);
        record.setDetailDesc(detailDesc);
        record.setWalletType(walletType);
        userCapitalChangeRecordMapper.insertSelective(record);
        mapper.minusBalance(userId, userType,walletType, amount, updateUser, updateTime);
    }

    /**
     *
     * @param oemCode
     * @param orderNo
     * @param orderType
     * @param userId
     * @param userType
     * @param amount
     * @param availableAmount 可用金额
     * @param outstandingAmount 有待结算金额则可用金额不操作，没有待结算金额则操作可用金额
     * @param detailDesc
     * @param updateUser
     * @param updateTime
     * @param isAdd 是否添加资金  1- 添加资金 0-减少资金
     * @throws BusinessException
     */
    @Override
    @Transactional
    public void addBalanceByProfits(String oemCode, String orderNo, Integer orderType, Long userId, Integer userType, Long amount, Long availableAmount, Long outstandingAmount, Long blockAmount, String detailDesc, String updateUser, Date updateTime, Integer isAdd, Integer walletType) throws BusinessException {
        if(walletType == null || walletType == 0){
            walletType = 1 ;
        }
        if(amount == null){
            amount = 0L;
        }
        if(availableAmount == null){
            availableAmount = 0L;
        }
        if(outstandingAmount == null){
            outstandingAmount = 0L;
        }
        if(blockAmount == null) {
            blockAmount = 0L;
        }

        UserCapitalAccountEntity accEntity = mapper.queryByUserIdAndType(userId, userType,oemCode,walletType);
        if (accEntity == null) {
            log.error("资金账户不存在");
            throw new BusinessException("资金账户不存在");
        }else if(isAdd == 0 &&accEntity.getAvailableAmount() != null && accEntity.getAvailableAmount() < availableAmount ){
            log.error("可用金额不足");
            throw new BusinessException("资金账号["+accEntity.getCapitalAccount()+"]可用金额不足");
        }
        //添加锁
        String registRedisTime = (System.currentTimeMillis() + 300000) + "";
        boolean flag = redisService.lock(RedisKey.PROFIT_DETAIL_QUEUE_LOCK_KEY + accEntity.getUserType()+"_"+accEntity.getUserId(),registRedisTime,1);
        if(!flag){
            log.error("请勿重复提交");
            throw new BusinessException("请勿重复提交！");
        }
        //增加资金变动
        UserCapitalChangeRecordEntity record = new UserCapitalChangeRecordEntity();
        record.setAddTime(updateTime);
        record.setAddUser(updateUser);
        record.setCapitalAccountId(accEntity.getId());
        record.setChangesAmount(amount);
        record.setChangesBeforeAmount(accEntity.getTotalAmount());
        if(isAdd == 1) {
            record.setChangesAfterAmount(accEntity.getTotalAmount() + amount);
            record.setChangesType(1);
        }else if(isAdd == 0){
            record.setChangesAfterAmount(accEntity.getTotalAmount() - amount);
            record.setChangesType(2);
        }else{
            record.setChangesAfterAmount(accEntity.getTotalAmount());
        }
        record.setOemCode(accEntity.getOemCode());
        record.setOrderNo(orderNo);
        record.setOrderType(orderType);
        record.setUserId(userId);
        record.setUserType(userType);
        if(isAdd == 1 && blockAmount < 1) {
            record.setChangesType(1);
        }else if(isAdd == 0 && blockAmount > 0 && amount <1){
            record.setChangesAmount(blockAmount);
            record.setChangesType(3);
        }else if(isAdd == 0 && (availableAmount > 0 || blockAmount >0 )){
            record.setChangesType(2);
        }else if(isAdd == 0 && blockAmount < 1 && amount < 1){
            record.setChangesType(2);
        }
        if(outstandingAmount > 0){
            record.setOutstandingAmount(outstandingAmount);
        }
        record.setDetailDesc(detailDesc);
        record.setWalletType(walletType);
        userCapitalChangeRecordMapper.insertSelective(record);
        mapper.addBalanceByProfits(userId, userType, amount,availableAmount,outstandingAmount,blockAmount, updateUser, updateTime,isAdd,oemCode,walletType);
        // 释放redis锁
        redisService.unlock(RedisKey.PROFIT_DETAIL_QUEUE_LOCK_KEY + accEntity.getUserType()+"_"+accEntity.getUserId(),registRedisTime);
    }

    @Override
    public List<UserCapitalAccountVO> listAccountBalance(UserCapitalAccountQuery query) {
        return mapper.listAccountBalance(query);
    }

    @Override
    public List<UserCapitalAccountVO> listPlatformAccountBalance(UserCapitalAccountQuery query) {
        return mapper.listPlatformAccountBalance(query);
    }

    /**
     * 用户资金结算（将待结算金额添加到可用金额）
     * @param userCapitalChangeRecordEntity
     * @return 0-修改失败 1-修改成功 2-资金账号状态不可用
     */
    @Override
    @Transactional
    public int updateUserAmount(UserCapitalChangeRecordEntity userCapitalChangeRecordEntity){
        UserCapitalAccountEntity accEntity = mapper.queryByUserIdAndType(userCapitalChangeRecordEntity.getUserId(), userCapitalChangeRecordEntity.getUserType(),userCapitalChangeRecordEntity.getOemCode(),userCapitalChangeRecordEntity.getWalletType());
        if (accEntity == null) {
            throw new BusinessException("资金账户不存在");
        }else if(accEntity.getStatus() == 0){
            return 2;
        }

        // 加入redis锁机制，防重复提交
        String registRedisTime = (System.currentTimeMillis() + 300000) + "";
        boolean flag = redisService.lock(RedisKey.PROFIT_DETAIL_QUEUE_LOCK_KEY + userCapitalChangeRecordEntity.getUserType()+"_"+userCapitalChangeRecordEntity.getUserId(),registRedisTime,1);
        if(!flag){
            throw new BusinessException("请勿重复提交！");
        }

        //用户资金将待结算金额添加到可用金额
        accEntity.setAvailableAmount(accEntity.getAvailableAmount() + userCapitalChangeRecordEntity.getChangesAmount()); //添加可用金额
        accEntity.setOutstandingAmount(accEntity.getOutstandingAmount() - userCapitalChangeRecordEntity.getChangesAmount()); //减少待结算金额
        accEntity.setUpdateTime(new Date());
        accEntity.setUpdateUser(userCapitalChangeRecordEntity.getUpdateUser());
        mapper.updateByPrimaryKeySelective(accEntity);
        userCapitalChangeRecordEntity.setCapitalAccountId(accEntity.getId());
        userCapitalChangeRecordEntity.setChangesBeforeAmount(accEntity.getTotalAmount());
        userCapitalChangeRecordEntity.setChangesAfterAmount(accEntity.getTotalAmount());
        userCapitalChangeRecordEntity.setAddTime(new Date());
        userCapitalChangeRecordEntity.setWalletType(userCapitalChangeRecordEntity.getWalletType());
        userCapitalChangeRecordMapper.insertSelective(userCapitalChangeRecordEntity);

        // 释放redis锁
        redisService.unlock(RedisKey.PROFIT_DETAIL_QUEUE_LOCK_KEY + userCapitalChangeRecordEntity.getUserType()+"_"+userCapitalChangeRecordEntity.getUserId(),registRedisTime);
        return 1;
    }

    @Override
    @Transactional
    public synchronized void freezeBalance(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser) {
        UserCapitalChangeRecordEntity record = new UserCapitalChangeRecordEntity();
        record.setAddTime(new Date());
        record.setAddUser(updateUser);
        record.setCapitalAccountId(entity.getId());
        record.setChangesAmount(amount);
        record.setChangesType(CapitalChangeTypeEnum.FROZEN.getValue());
        record.setChangesBeforeAmount(entity.getTotalAmount());
        record.setChangesAfterAmount(entity.getTotalAmount());
        record.setOemCode(entity.getOemCode());
        record.setOrderNo(orderNo);
        record.setOrderType(orderType);
        record.setUserId(entity.getUserId());
        record.setUserType(entity.getUserType());
        record.setWalletType(entity.getWalletType());
        userCapitalChangeRecordMapper.insertSelective(record);

        UserCapitalAccountDTO dto = new UserCapitalAccountDTO();
        dto.setId(entity.getId());
        dto.setAddBlockAmount(amount);
        dto.setDelAvailableAmount(amount);
        dto.setUpdateTime(new Date());
        dto.setUpdateUser(updateUser);
        mapper.updateAmount(dto);
    }

    @Override
    public void freezeBalanceByRecharge(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser) {
        UserCapitalChangeRecordEntity record = new UserCapitalChangeRecordEntity();
        record.setAddTime(new Date());
        record.setAddUser(updateUser);
        record.setCapitalAccountId(entity.getId());
        record.setChangesAmount(amount);
        record.setChangesType(CapitalChangeTypeEnum.FROZEN.getValue());
        record.setChangesBeforeAmount(entity.getTotalAmount());
        record.setChangesAfterAmount(entity.getTotalAmount());
        record.setOemCode(entity.getOemCode());
        record.setOrderNo(orderNo);
        record.setOrderType(orderType);
        record.setUserId(entity.getUserId());
        record.setUserType(entity.getUserType());
        record.setWalletType(entity.getWalletType());
        userCapitalChangeRecordMapper.insertSelective(record);
    }

    @Override
    @Transactional
    public synchronized void unfreezeBalance(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser) {
        UserCapitalChangeRecordEntity record = new UserCapitalChangeRecordEntity();
        record.setAddTime(new Date());
        record.setAddUser(updateUser);
        record.setCapitalAccountId(entity.getId());
        record.setChangesAmount(amount);
        record.setChangesType(CapitalChangeTypeEnum.THAW.getValue());
        record.setChangesBeforeAmount(entity.getTotalAmount());
        record.setChangesAfterAmount(entity.getTotalAmount());
        record.setOemCode(entity.getOemCode());
        record.setOrderNo(orderNo);
        record.setOrderType(orderType);
        record.setUserId(entity.getUserId());
        record.setUserType(entity.getUserType());
        record.setWalletType(entity.getWalletType());
        userCapitalChangeRecordMapper.insertSelective(record);

        UserCapitalAccountDTO dto = new UserCapitalAccountDTO();
        dto.setId(entity.getId());
        dto.setDelBlockAmount(amount);
        dto.setAddAvailableAmount(amount);
        dto.setUpdateTime(new Date());
        dto.setUpdateUser(updateUser);
        mapper.updateAmount(dto);
    }
    @Override
    @Transactional
    public synchronized void unfreezeBalanceWetchat(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser) {
        UserCapitalChangeRecordEntity record = new UserCapitalChangeRecordEntity();
        record.setAddTime(new Date());
        record.setAddUser(updateUser);
        record.setCapitalAccountId(entity.getId());
        record.setChangesAmount(amount);
        record.setChangesType(CapitalChangeTypeEnum.EXPENDITURE.getValue());
        record.setChangesBeforeAmount(entity.getTotalAmount());
        record.setChangesAfterAmount(entity.getTotalAmount() - amount);
        record.setOemCode(entity.getOemCode());
        record.setOrderNo(orderNo);
        record.setOrderType(orderType);
        record.setUserId(entity.getUserId());
        record.setUserType(entity.getUserType());
        record.setWalletType(entity.getWalletType());
        record.setRemark("资金原路退回");
        userCapitalChangeRecordMapper.insertSelective(record);


        UserCapitalAccountDTO dto = new UserCapitalAccountDTO();
        dto.setId(entity.getId());
        dto.setDelBlockAmount(amount);
        dto.setDelTotalAmount(amount);
        dto.setUpdateTime(new Date());
        dto.setUpdateUser(updateUser);
        mapper.updateAmount(dto);
    }
    @Override
    public UserCapitalAccountEntity queryByUserIdAndType(Long userId, Integer userType,String oemCode,Integer walletType) {
        return mapper.queryByUserIdAndType(userId, userType,oemCode,walletType);
    }

    @Override
    public synchronized void delFreezeBalance(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser) {
        UserCapitalChangeRecordEntity record = new UserCapitalChangeRecordEntity();
        record.setAddTime(new Date());
        record.setAddUser(updateUser);
        record.setCapitalAccountId(entity.getId());
        record.setChangesAmount(amount);
        record.setChangesType(CapitalChangeTypeEnum.EXPENDITURE.getValue());
        record.setChangesBeforeAmount(entity.getTotalAmount());
        record.setChangesAfterAmount(entity.getTotalAmount() - amount);
        record.setOemCode(entity.getOemCode());
        record.setOrderNo(orderNo);
        record.setOrderType(orderType);
        record.setUserId(entity.getUserId());
        record.setUserType(entity.getUserType());
        record.setWalletType(entity.getWalletType());
        userCapitalChangeRecordMapper.insertSelective(record);

        UserCapitalAccountDTO dto = new UserCapitalAccountDTO();
        dto.setId(entity.getId());
        dto.setDelTotalAmount(amount);
        dto.setDelBlockAmount(amount);
        dto.setUpdateTime(new Date());
        dto.setUpdateUser(updateUser);
        mapper.updateAmount(dto);
    }

    @Override
    @Transactional
    public void updateUserCapitalAccountEntity(UserCapitalAccountEntity userCapitalAccountEntity, String remark, String updateUser, Date updateTime) {
        //保存资金账户主表
        userCapitalAccountEntity.setUpdateUser(updateUser);
        userCapitalAccountEntity.setUpdateTime(updateTime);
        userCapitalAccountEntity.setRemark(remark);
        mapper.updateByPrimaryKeySelective(userCapitalAccountEntity);
//        //保存历史变更表
//        UserCapitalChangeRecordEntity record = new UserCapitalChangeRecordEntity();
//        BeanUtils.copyProperties(userCapitalAccountEntity, record);
//        record.setId(null);
//        record.setAddUser(updateUser);
//        record.setAddTime(updateTime);
//        record.setUpdateUser(null);
//        record.setUpdateTime(null);
//        record.setRemark(remark);
//        userCapitalChangeRecordMapper.insertSelective(record);
    }

    @Override
    public MemberCapitalAccountApiVO getBalanceApi(Long userId, Integer levelNo, String oemCode) throws BusinessException {
        MemberCapitalAccountApiVO capital = new MemberCapitalAccountApiVO();

        // 会员等级 0-税务顾问 1-城市服务商（查询佣金钱包）
        if (Objects.equals(levelNo, 0) || Objects.equals(levelNo, 1)) {
            // 查询会员佣金钱包信息
            UserCapitalAccountEntity commissionCapital = new UserCapitalAccountEntity();
            commissionCapital.setUserType(MemberTypeEnum.MEMBER.getValue());//用户类型 1-会员 2-系统用户
            commissionCapital.setUserId(userId);
            commissionCapital.setStatus(1);//状态 0-禁用 1-可用
            commissionCapital.setOemCode(oemCode);
            commissionCapital.setWalletType(1);//钱包类型 1-消费钱包 2-佣金钱包
            commissionCapital = this.mapper.selectOne(commissionCapital);
            if (null == commissionCapital) {
                capital.setUsableAmount(0L);
                capital.setOutstandingAmount(0L);
            } else {
                capital.setUsableAmount(commissionCapital.getAvailableAmount());
                capital.setOutstandingAmount(commissionCapital.getOutstandingAmount());
            }

        // 会员等级 2-城市合伙人 3-高级城市合伙人（查询消费钱包）
        } else if (Objects.equals(levelNo, 2) || Objects.equals(levelNo, 3)) {
            // 查询会员消费钱包信息
            UserCapitalAccountEntity consumerCapital = new UserCapitalAccountEntity();
            consumerCapital.setUserType(MemberTypeEnum.EMPLOYEE.getValue());//用户类型 1-会员 2-系统用户
            consumerCapital.setUserId(userId);
            consumerCapital.setStatus(1);//状态 0-禁用 1-可用
            consumerCapital.setOemCode(oemCode);
            consumerCapital.setWalletType(1);//钱包类型 1-消费钱包 2-佣金钱包
            consumerCapital = this.mapper.selectOne(consumerCapital);
            if (null == consumerCapital) {
                capital.setUsableAmount(0L);
                capital.setOutstandingAmount(0L);
            } else {
                capital.setUsableAmount(consumerCapital.getAvailableAmount());
                capital.setOutstandingAmount(consumerCapital.getOutstandingAmount());
            }
        }
        return capital;
    }

}

