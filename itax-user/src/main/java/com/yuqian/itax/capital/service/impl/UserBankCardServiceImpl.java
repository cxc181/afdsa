package com.yuqian.itax.capital.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.service.OemConfigService;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.dao.UserBankCardMapper;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.dto.BankCardDTO;
import com.yuqian.itax.capital.entity.vo.BankCardVO;
import com.yuqian.itax.capital.enums.BankCardTypeEnum;
import com.yuqian.itax.capital.enums.CardStatusEnum;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.nabei.entity.APIH5SignRespVo;
import com.yuqian.itax.nabei.entity.APIRegisterRespVo;
import com.yuqian.itax.nabei.entity.APISignQueryRespVo;
import com.yuqian.itax.nabei.service.NabeiApiService;
import com.yuqian.itax.system.entity.BankBinEntity;
import com.yuqian.itax.system.entity.BankInfoEntity;
import com.yuqian.itax.system.entity.ChannelInfoEntity;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.BankBinService;
import com.yuqian.itax.system.service.BankInfoService;
import com.yuqian.itax.system.service.ChannelInfoService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.dao.UserMapper;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.vo.AgentDetailVO;
import com.yuqian.itax.user.enums.MemberAuthStatusEnum;
import com.yuqian.itax.user.enums.UserTypeEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberLevelService;
import com.yuqian.itax.user.service.MemberToSignNabeiChangeRecordService;
import com.yuqian.itax.user.service.MemberToSignNabeiService;
import com.yuqian.itax.util.entity.UserAuthVO;
import com.yuqian.itax.util.util.AuthKeyUtils;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.OrderNoFactory;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.util.util.channel.ChannelUtils;
import com.yuqian.itax.util.util.guojin.GuoJinUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service("bankCardService")

public class UserBankCardServiceImpl extends BaseServiceImpl<UserBankCardEntity,UserBankCardMapper> implements UserBankCardService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserBankCardMapper bankCardMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private BankBinService bankBinService;
    @Autowired
    private DictionaryService sysDictionaryService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private UserCapitalAccountService userCapitalAccountService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    private BankInfoService bankInfoService;
    @Autowired
    private NabeiApiService nabeiApiService;
    @Autowired
    private MemberToSignNabeiService memberToSignNabeiService;
    @Autowired
    private MemberToSignNabeiChangeRecordService memberToSignNabeiChangeRecordService;
    @Autowired
    private OemConfigService oemConfigService;
    @Autowired
    private ChannelInfoService channelInfoService;
    @Autowired
    private OemService oemService;

    @Override
    public AgentDetailVO getBankCardInfo(AgentDetailVO agentDetailVO,String oemCode) {
        UserBankCardEntity userBankCardEntity =bankCardMapper.getBankCardInfoByUserIdAndUserType(agentDetailVO.getId(),2,agentDetailVO.getOemCode());

        if(userBankCardEntity !=null){
            //银行卡信息
            agentDetailVO.setBankUserName(userBankCardEntity.getUserName());
            agentDetailVO.setBankPhone(userBankCardEntity.getPhone());
            agentDetailVO.setBankCode(userBankCardEntity.getBankCode());
            agentDetailVO.setIdCard(userBankCardEntity.getIdCard());
            agentDetailVO.setBankNumber(userBankCardEntity.getBankNumber());
            agentDetailVO.setBankName(userBankCardEntity.getBankName());
            agentDetailVO.setBankCardType(userBankCardEntity.getBankCardType());
        }

        return agentDetailVO;
    }

    @Override
    public UserBankCardEntity getBankCardInfoByUserIdAndUserType(Long userId, Integer userType, String oemCode) {
        return mapper.getBankCardInfoByUserIdAndUserType(userId,userType,oemCode);
    }

    /**
     * 修改银行卡
     * @return
     */
    @Override
    @Transactional(rollbackFor =  Exception.class)
    public UserBankCardEntity updateBankCardEntity(UserBankCardEntity userBankCardEntityPO, String userAccount, Long id) {

        UserBankCardEntity userBankCardEntity =mapper.getBankCardInfoByUserIdAndUserType(userBankCardEntityPO.getUserId(), 2,userBankCardEntityPO.getOemCode());

        UserEntity userEntity=userMapper.selectByPrimaryKey(id);

        BankBinEntity bank = bankBinService.findByCardNo(userBankCardEntityPO.getBankNumber());
        if(null == bank){
            throw  new BusinessException("未找到卡信息");
        }
        if(bank.getCardType()==1){
            throw  new BusinessException("不支持信用卡");
        }
        /**
         * 银行卡四要素验证
         */
        //读取要素认证相关配置 paramsType=6
        OemParamsEntity paramsEntity = oemParamsService.getParams(userEntity.getOemCode(), 6);
        if(null == paramsEntity){
            throw new BusinessException("未配置银行卡四要素相关信息！");
        }
        // agentNo
        String agentNo = paramsEntity.getAccount();
        // signKey
        String signKey = paramsEntity.getSecKey();
        // authUrl
        String authUrl = paramsEntity.getUrl();

        String authResult = AuthKeyUtils.auth4Key(agentNo,signKey,authUrl,userBankCardEntityPO.getUserName(),userBankCardEntityPO.getIdCard(),userBankCardEntityPO.getBankNumber(),
                userBankCardEntityPO.getPhone(),paramsEntity.getParamsValues());

        if(StringUtils.isBlank(authResult)){
            throw new BusinessException("银行卡四要素认证失败");
        }
        JSONObject resultObj = JSONObject.parseObject(authResult);

        if(!"00".equals(resultObj.getString("code"))){
            throw new BusinessException("银行卡四要素认证失败：" + resultObj.getString("msg"));
        }

        if(userBankCardEntity !=null){//选择绑定银行卡
            //修改银行卡
            userBankCardEntity.setBankNumber(userBankCardEntityPO.getBankNumber());
            userBankCardEntity.setBankName(userBankCardEntityPO.getBankName());
            userBankCardEntity.setBankCode(userBankCardEntityPO.getBankCode());
            userBankCardEntity.setPhone(userBankCardEntityPO.getPhone());
            userBankCardEntity.setUpdateTime(new Date());
            userBankCardEntity.setUpdateUser(userAccount);
            mapper.updateByPrimaryKey(userBankCardEntity);
        }else{
            userBankCardEntity =new UserBankCardEntity();
            BeanUtils.copyProperties(userBankCardEntityPO, userBankCardEntity);
            userBankCardEntity.setBankCardType(1);
            userBankCardEntity.setBankCode(bank.getBankCode());
            userBankCardEntity.setBankName(bank.getBankName());
            userBankCardEntity.setUserType(2);
            userBankCardEntity.setUserName(userBankCardEntityPO.getUserName());
            userBankCardEntity.setPhone((userBankCardEntityPO.getPhone()));
            userCapitalAccountService.insertBankCardEntity(userBankCardEntity,userAccount,userEntity);
        }
        return userBankCardEntity;
    }

    @Override
    public List<BankCardVO> listBankCards(Long userId,String oemCode,Integer walletType) throws BusinessException {
        List<BankCardVO> list = this.bankCardMapper.listBankCards(userId,oemCode);

        // 查询机构
        OemEntity oemEntity = Optional.ofNullable(oemService.getOem(oemCode)).orElseThrow(() -> new BusinessException("未查询到机构信息"));

        // 查询用户提现方案
        String schemeSwitch = sysDictionaryService.getValueByCode("withdraw_scheme_switch");

        //遍历list，查询银行logo/限额信息
        for (int i = 0; i < list.size(); i++) {
            BankInfoEntity t = new BankInfoEntity();
            t.setBankCode(list.get(i).getBankCode());
            BankInfoEntity bank = this.bankInfoService.selectOne(t);
            list.get(i).setLogo(bank.getBankLogoUrl());
            if ((StringUtil.isNotBlank(schemeSwitch) && "2".equals(schemeSwitch)) && walletType == 2) {
                if (null == oemEntity.getMaxCommissionWithdrawSingleLimit()) {
                    throw new BusinessException("未配置机构佣金提现单笔限额");
                }
                if (null == oemEntity.getCommissionWithdrawMonthLimit()) {
                    throw new BusinessException("未配置机构佣金提现单月限额");
                }
                list.get(i).setSingleLimitCash(new BigDecimal(oemEntity.getMaxCommissionWithdrawSingleLimit()).divide(new BigDecimal("100")).intValue());
                list.get(i).setMonthLimitCash(new BigDecimal(oemEntity.getCommissionWithdrawMonthLimit()).divide(new BigDecimal("100")).intValue());
            } else {
                // 银行卡信息表中的限额配置的单位是元！
                list.get(i).setDailyLimitCash(bank.getDailyLimitCash());
                list.get(i).setSingleLimitCash(bank.getSingleLimitCash());
            }
        }

        return list;
    }

    @Override
    public void unbind(Long userId,Long id,String verifyCode) throws BusinessException {
        // 查询用户账号
        MemberAccountEntity member = this.memberAccountService.findById(userId);
        if(null == member){
            throw new BusinessException("操作失败，用户信息不存在");
        }
        // 查询绑定银行卡是否存在
        UserBankCardEntity entity = this.bankCardMapper.selectByPrimaryKey(id);
        if(null == entity){
            throw new BusinessException("操作失败，绑定银行卡信息不存在");
        }

        // 判断银行卡是否属于当前登录人
        if(!userId.equals(entity.getUserId())){
            throw new BusinessException("非法操作，您没有权限解绑该银行卡");
        }

        // 状态判断
        if(!CardStatusEnum.BIND.getValue().equals(entity.getStatus())){
            throw new BusinessException("状态错误，当前银行卡不允许解绑");
        }

        //验证码校验
        String registRedisTime = (System.currentTimeMillis() + 300000) + "";
        // 加入redis锁机制，防重复提交
        boolean flag = redisService.lock(RedisKey.REGIST_ORDER_UN_BIND_REDIS_KEY + entity.getPhone(),registRedisTime,1);
        if(!flag){
            throw new BusinessException("请勿重复提交！");
        }
        String verficationCode = redisService.get(RedisKey.SMS_USER_UN_BIND_CARD_KEY_SUFFER + entity.getPhone());
        //验证码错误或过期
        if(verficationCode == null || "".equals(verficationCode) || !verifyCode.equals(verficationCode)){
            //释放redis锁
            redisService.unlock(RedisKey.SMS_USER_UN_BIND_CARD_KEY_SUFFER + entity.getPhone(), registRedisTime);
            throw new BusinessException(MessageEnum.PASSWORD_RESET_CODE_IS_EXPIRED.getMessage());
        }

        UserBankCardEntity t = new UserBankCardEntity();
        t.setId(id);
        t.setStatus(CardStatusEnum.UNBIND.getValue());
        t.setUpdateTime(new Date());
        t.setUpdateUser(member.getMemberAccount());
        this.bankCardMapper.updateByPrimaryKeySelective(t);
        // 释放redis锁
        redisService.unlock(RedisKey.SMS_USER_UN_BIND_CARD_KEY_SUFFER + entity.getPhone(),registRedisTime);
    }

    /**
     * @Description 构建绑卡参数
     * @Author  Kaven
     * @Date   2019/12/16 15:10
     * @Param  UserBankCardEntity
    */
    private UserBankCardEntity buildBankCardParams(String account, String oemCode, String name, String idCardNo, String bankNumber, String reserveMobile) {
        UserBankCardEntity entity = new UserBankCardEntity();
        entity.setAddTime(new Date());
        entity.setAddUser(account);
        entity.setOemCode(oemCode);
        entity.setStatus(CardStatusEnum.BIND.getValue());
        entity.setBankNumber(bankNumber);
        entity.setBankCardType(BankCardTypeEnum.DEBIT_CARD.getValue());
        entity.setUserName(name);
        entity.setIdCard(idCardNo);
        entity.setPhone(reserveMobile);
        entity.setUserType(UserTypeEnum.MEMBER.getValue());
        return entity;
    }

    @Override
    @Transactional(rollbackFor={Exception.class})
    public synchronized void bindCard(BankCardDTO dto){
        log.info("收到用户绑定银行卡请求：{}",JSON.toJSONString(dto));

        // 查询用户账号
        MemberAccountEntity member = this.memberAccountService.findById(dto.getUserId());
        if(null == member){
            throw new BusinessException("操作失败，用户信息不存在");
        }

        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());
        if(null == memberLevel){
            throw new BusinessException("未查询到会员等级");
        }

        // 加入redis锁机制，防重复提交
        boolean flag = redisService.lock(RedisKey.REGIST_ORDER_BIND_REDIS_KEY + dto.getReserveMobile(),dto.getRegistRedisTime(),10);
        if(!flag){
            throw new BusinessException("请勿重复提交！");
        }

        // 绑卡前置校验
        BankBinEntity bin = preHandleBindCard(dto, member);

        log.info("用户绑卡前置校验完成，开始进行四要素验证");

        // 银行卡四要素验证
        bankCard4KeyVerify(dto);

        log.info("银行卡四要素认证通过，开始绑卡操作");

        // 四要素认证通过，绑卡
        UserBankCardEntity entity = buildBankCardParams(member.getMemberAccount(),dto.getOemCode(), dto.getName(), dto.getIdCardNo(), dto.getBankNumber(), dto.getReserveMobile());
        entity.setBankCode(bin.getBankCode().substring(0,4));

        // 获取银行名称
        BankInfoEntity bankInfo = new BankInfoEntity();
        bankInfo.setBankCode(entity.getBankCode());
        BankInfoEntity bank = this.bankInfoService.selectOne(bankInfo);
        if(null == bank){
            throw new BusinessException("不支持的银行卡，请更换");
        }
        entity.setBankName(bank.getBankName());
        entity.setUserId(dto.getUserId());
        // 保存绑卡信息
        this.bankCardMapper.insert(entity);

        // 绑卡成功，同步更新用户账户表实名信息
        this.memberAccountService.updateUserAuth(dto.getUserId(),dto.getName(),dto.getIdCardNo(),dto.getIdCardFront(),dto.getIdCardBack(), dto.getExpireDate(),dto.getIdCardAddr(), MemberAuthStatusEnum.AUTH_SUCCESS.getValue(),"绑卡实名");
        log.info("绑卡成功，同步更新用户账户表实名信息：{},{},{}",dto.getUserId(),dto.getName(),dto.getIdCardNo());

        //------------------------------------纳呗平台API---------------------------------------------------------
        // 用户提现方案开关：1-北京代付 2-纳呗 3-其他
        if(Objects.equals(sysDictionaryService.getByCode("withdraw_scheme_switch").getDictValue(), 2)){
            // 如果会员等级是城市服务商以下，调用纳呗平台签约注册查询
            if(memberLevel.getLevelNo() < 5){
                log.info("调用纳呗平台签约注册查询");
                handleNabeiService(dto);
            }
        }

        // 清除验证码
        redisService.delete(RedisKey.SMS_USER_BIND_CARD_KEY_SUFFER + dto.getReserveMobile());
        // 释放redis锁
        redisService.unlock(RedisKey.REGIST_ORDER_BIND_REDIS_KEY + dto.getReserveMobile(),dto.getRegistRedisTime());

        log.info("绑卡成功，流程结束...{}", JSON.toJSONString(entity));
    }

    /**
     * @Description 纳呗相关业务处理
     * @Author  Kaven
     * @Date   2020/8/21 09:49
     * @Param   MemberLevelEntity BankCardDTO
     * @Return
     * @Exception  BusinessException
    */
    private void handleNabeiService(BankCardDTO dto) {
        // 读取纳呗平台API相关配置 paramsType=10
        OemParamsEntity nabeiParamsEntity = this.oemParamsService.getParams(dto.getOemCode(), 10);
        if (null == nabeiParamsEntity) {
            throw new BusinessException("未配置纳呗平台API相关信息！");
        }

        // 查询会员签约纳呗记录
        MemberToSignNabeiEntity sign = new MemberToSignNabeiEntity();
        sign.setOemCode(dto.getOemCode());
        sign.setMemberId(dto.getUserId());
        sign = memberToSignNabeiService.selectOne(sign);
        if(sign != null){
            log.info("调用纳呗平台签约注册查询......");
            APISignQueryRespVo signQueryRespVo = nabeiApiService.signQuery(nabeiParamsEntity, dto.getIdCardNo(), dto.getBankNumber());
            if(null != signQueryRespVo){
                if(Objects.equals(signQueryRespVo.getP1_resCode(), "0000")){
                    // 处理状态：0-未签约，1-已签约，2-已解约
                    if(Objects.equals(signQueryRespVo.getP3_status(), 0) || Objects.equals(signQueryRespVo.getP3_status(), 2)){
                        // 调用纳呗平台签约注册
                        nabeiSignRegister(dto.getUserId(),dto.getOemCode());
                    }
                } else if(Objects.equals(signQueryRespVo.getP1_resCode(), "0017")){
                    log.info(signQueryRespVo.getP2_resMsg());
                } else{
                    throw new BusinessException(signQueryRespVo.getP2_resMsg());
                }
            } else {
                throw new BusinessException("纳呗签约注册查询接口异常");
            }
        }
    }

    /**
     * @Description 银行卡四要素验证
     * @Author  Kaven
     * @Date   2020/8/21 09:43
     * @Param   BankCardDTO
     * @Return
     * @Exception
    */
    private void bankCard4KeyVerify(BankCardDTO dto) {
        // 读取要素认证相关配置 paramsType=6
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(dto.getOemCode(),6);
        if(null == paramsEntity){
            throw new BusinessException("未配置银行卡四要素相关信息！");
        }
        // agentNo
        String agentNo = paramsEntity.getAccount();
        // signKey
        String signKey = paramsEntity.getSecKey();
        // authUrl
        String authUrl = paramsEntity.getUrl();

        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("requestId", System.currentTimeMillis());
        orderMap.put("name", dto.getName());
        orderMap.put("idcard", dto.getIdCardNo());
        orderMap.put("bankcard", dto.getBankNumber());
        orderMap.put("mobile", dto.getReserveMobile());
        if(StringUtil.isNotBlank(paramsEntity.getParamsValues()) && paramsEntity.getParamsValues().trim().indexOf("\"channel\":\"new\"") > -1){
            orderMap.put("authType", "11");
        }

        log.info("银行卡四要素认证data数据：" + JSONUtil.toJsonPrettyStr(orderMap));
        String authResult = null;
        try {
            authResult = ChannelUtils.callApi(paramsEntity.getParamsValues(),orderMap,agentNo,signKey,authUrl,"AUTH");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException("银行卡四要素认证异常");
        }

        if(StringUtils.isBlank(authResult)){
            throw new BusinessException("银行卡四要素认证失败");
        }
        JSONObject resultObj = JSONObject.parseObject(authResult);

        if(!"00".equals(resultObj.getString("code"))){
            throw new BusinessException("银行卡四要素认证失败：" + resultObj.getString("msg"));
        }
    }

    /**
     * @Description 银行卡绑定前置校验
     * @Author  Kaven
     * @Date   2020/8/21 09:39
     * @Param   BankCardDTO
     * @Return
     * @Exception
    */
    private BankBinEntity preHandleBindCard(BankCardDTO dto, MemberAccountEntity member) {
        // 验证码校验
        String registRedisTime = System.currentTimeMillis() + 300000 + "";
        // 验证码校验
        String verficationCode = redisService.get(RedisKey.SMS_USER_BIND_CARD_KEY_SUFFER + dto.getReserveMobile());
        // 验证码错误或过期
        if (verficationCode == null || "".equals(verficationCode) || !dto.getVerifyCode().equals(verficationCode)) {
            //释放redis锁
            redisService.unlock(RedisKey.SMS_USER_BIND_CARD_KEY_SUFFER + dto.getReserveMobile(), registRedisTime);
            throw new BusinessException(MessageEnum.PASSWORD_RESET_CODE_IS_EXPIRED.getMessage());
        }

        // 当用户未实名时，查询机构配置信息,已接入渠道的机构需校验用户是否已在渠道方实名
        OemConfigEntity configEntity = Optional.ofNullable(oemConfigService.queryOemConfigByCode(dto.getOemCode(), "is_open_channel")).orElseThrow(() -> new BusinessException("未查询到机构配置信息"));
        if (!MemberAuthStatusEnum.AUTH_SUCCESS.getValue().equals(member.getAuthStatus()) && Objects.equals(configEntity.getParamsValue(), "1")) {
            //
            // 查询国金用户实名信息接口
            Map<String,Object> dataParams = new HashMap<>();
            dataParams.put("userId", Optional.ofNullable(member.getChannelUserId()).orElseThrow(() -> new BusinessException("渠道用户id为空")));
            dataParams.put("productCode", member.getOemCode());
            // 获取秘钥
            OemParamsEntity oemParamsEntity = oemParamsService.getParams(dto.getOemCode(), OemParamsTypeEnum.GUOJIN_CHANNEL_CONFIG.getValue());
            if(oemParamsEntity==null){
                throw new BusinessException("未配置国金助手信息，请联系客服！");
            }
            JSONObject jsonObject = GuoJinUtil.gjChannel(dataParams, member.getChannelCode(), oemParamsEntity.getSecKey(), oemParamsEntity.getUrl() + GuoJinUtil.USER_AUTH_INFO);
            // 校验调用结果
            if (null == jsonObject.get("data") || !"0000".equals(jsonObject.getString("retCode"))) {
                log.info("实名认证失败，调用国金用户实名信息查询接口失败：{}", jsonObject.getString("retMsg"));
                throw new BusinessException("【访问渠道失败】：" + jsonObject.getString("retMsg"));
            }
            JSONObject data = jsonObject.getJSONObject("data");
            if (null != data) {
                UserAuthVO userAuthVO = data.toJavaObject(UserAuthVO.class);
                if (Objects.equals(userAuthVO.getAuthStatus(), 1)) {
                    ChannelInfoEntity channelInfoEntity = Optional.ofNullable(channelInfoService.findByChannelCode(member.getChannelCode())).orElseThrow(() -> new BusinessException("未查询到渠道配置信息"));
                    throw new BusinessException("您已在【" + channelInfoEntity.getChannelName() +"】实名，不能重复实名");
                }
            }
        }
        // 验证身份证有效期格式
        String regex = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))-((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))";
        if(!(dto.getExpireDate().contains("长期") || Pattern.matches(regex,dto.getExpireDate()))){
            throw new BusinessException("身份证有效期格式有误，格式要求：yyyy.MM.dd-yyyy.MM.dd");
        }

        // 验证用户身份证有效期是否在有效范围内
        String[] dateArr = dto.getExpireDate().split("-");
        if(dateArr.length == 2 && !dateArr[1].contains("长期")){
            String endDate = dateArr[1].replace(".","").replace(".","");
            String nowDate = DateUtil.formatDefaultDate(new Date()).replace("-","").replace("-","");
            if(Long.parseLong(endDate) < Long.parseLong(nowDate)){
                throw new BusinessException("绑卡操作失败，身份证已过期");
            }
        }

        // 判断是否已经绑定过相同的卡
        UserBankCardEntity t = new UserBankCardEntity();
        t.setUserId(dto.getUserId());
        t.setBankNumber(dto.getBankNumber());
        t.setStatus(CardStatusEnum.BIND.getValue());
        int count = this.bankCardMapper.selectCount(t);
        if(count > 0){
            throw new BusinessException("您已经绑定过卡号为【" + dto.getBankNumber() + "】的储蓄卡，不能重复绑定");
        }

        // 判断用户是否已经绑过卡（目前只允许绑定一张卡）
        List<BankCardVO> list = this.bankCardMapper.listBankCards(dto.getUserId(),dto.getOemCode());
        if(null != list && list.size() > 0){
            throw new BusinessException("您已经绑定过卡号为【" + list.get(0).getBankNumber() + "】的储蓄卡，请先解绑再操作");
        }

        // 卡类型判断，目前只支持储蓄卡
        BankBinEntity bin = this.bankBinService.findByCardNo(dto.getBankNumber());
        if(null == bin){
            throw new BusinessException("卡bin银行卡信息识别失败");
        }
        if(bin.getCardType() != 2){
            throw new BusinessException("只能绑定储蓄卡，请更换");
        }

        /*
         * 判断银行卡是否在支持的银行列表内
         */
        // 读取系统配置参数，获得支持结算银行编码集
        DictionaryEntity dict = sysDictionaryService.getByCode("itax.support.bank");
        if(null == dict){
            throw new BusinessException("系统未配置支持银行卡列表");
        }
        String bankCodes = dict.getDictValue();
        if(!bankCodes.contains(bin.getBankCode())){
            log.error("上传银行卡不在支持范围内{}", dto.getBankNumber());
            throw new BusinessException(MessageEnum.BANK_CARD_NOT_SUPPORT.getMessage());
        }
        return bin;
    }

    /**
     * 根据银行卡号和机构编码获取系统银行号
     * @param bankNumber 银行卡号
     * @param oemCode
     * @return
     */
    @Override
    public String getBankNoByBankAccount(@Param("bankNumber") String bankNumber , @Param("oemCode") String oemCode){
        return mapper.getBankNoByBankAccount(bankNumber,oemCode);
    }

    @Override
    public Map<String,Object> nabeiSignQuery(Long userId, String oemCode) throws BusinessException{
        log.info("调用纳呗平台签约注册查询开始......");
        Map<String,Object> result = new HashMap<String,Object>();

        // 读取纳呗平台API相关配置 paramsType=10
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 10);
        if (null == paramsEntity) {
            throw new BusinessException("未配置纳呗平台API相关信息！");
        }

        // 查询用户银行卡信息
        UserBankCardEntity userBankCard = bankCardMapper.getBankCardInfoByUserIdAndUserType(userId, 1, oemCode);
        if(null == userBankCard){
            throw new BusinessException("用户银行卡信息为空！");
        }

        // 查询用户信息
        MemberAccountEntity member = memberAccountService.findById(userId);
        if (null == member) {
            throw new BusinessException("未查询到用户信息");
        }
        if (member.getAuthStatus() != 1) {
            throw new BusinessException("用户未实名");
        }

        // 调用纳呗平台签约注册查询
        APISignQueryRespVo signQueryRespVo = nabeiApiService.signQuery(paramsEntity, member.getIdCardNo(), null);
        if(null != signQueryRespVo){
            if(Objects.equals(signQueryRespVo.getP1_resCode(), "0000")){
                //处理状态：0-未签约，1-已签约，2-已解约
                result.put("status", signQueryRespVo.getP3_status());
            }else{
                throw new BusinessException("纳呗签约注册查询失败，原因：" + signQueryRespVo.getP2_resMsg());
            }
        }else{
            throw new BusinessException("纳呗签约注册查询接口异常");
        }
        log.info("调用纳呗平台签约注册查询结束......");
        return result;
    }

    @Override
    public void nabeiSignRegister(Long userId, String oemCode) throws BusinessException{
        log.info("调用纳呗平台签约注册开始......");

        // 读取纳呗平台API相关配置 paramsType=10
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 10);
        if (null == paramsEntity) {
            throw new BusinessException("未配置纳呗平台API相关信息！");
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(userId);
        if(null == member){
            throw new BusinessException("未查询到会员账号");
        }

        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());
        if(null == memberLevel){
            throw new BusinessException("未查询到会员等级");
        }

        // 查询用户银行卡信息
        UserBankCardEntity userBankCard = bankCardMapper.getBankCardInfoByUserIdAndUserType(userId, 1, oemCode);
        if(null == userBankCard){
            throw new BusinessException("未查询到会员银行卡");
        }

        // 调用纳呗平台签约注册查询
        APIRegisterRespVo registerRespVo = nabeiApiService.signRegister(paramsEntity, userBankCard.getUserName(), userBankCard.getIdCard(), userBankCard.getBankNumber(), userBankCard.getPhone());
        if(null != registerRespVo){
            if(Objects.equals(registerRespVo.getP1_resCode(), "0000")){

                // 保存会员签约纳呗记录
                MemberToSignNabeiEntity sign = new MemberToSignNabeiEntity();
                sign.setOemCode(oemCode);
                sign.setMemberId(userId);
                sign = memberToSignNabeiService.selectOne(sign);
                if(null == sign){
                    sign = new MemberToSignNabeiEntity();
                    sign.setOemCode(oemCode);
                    sign.setMemberId(userId);
                    sign.setLevelNo(memberLevel.getLevelNo());
                    sign.setBankName(userBankCard.getBankName());
                    sign.setBankCode(userBankCard.getBankCode());
                    sign.setUserName(userBankCard.getUserName());
                    sign.setIdCard(userBankCard.getIdCard());
                    sign.setPhone(userBankCard.getPhone());
                    sign.setBankNumber(userBankCard.getBankNumber());
                    sign.setSignStatus(Integer.parseInt(registerRespVo.getP3_status())); // 签约状态 0-未签约，1-已签约，2-已解约
                    sign.setProtocolno(registerRespVo.getP8_protocolNo());
                    sign.setAddTime(new Date());
                    sign.setAddUser(member.getMemberAccount());
                    sign.setRemark("纳呗平台签约注册");
                    memberToSignNabeiService.insertSelective(sign);
                } else{
                    sign.setLevelNo(memberLevel.getLevelNo());
                    sign.setBankName(userBankCard.getBankName());
                    sign.setBankCode(userBankCard.getBankCode());
                    sign.setUserName(userBankCard.getUserName());
                    sign.setIdCard(userBankCard.getIdCard());
                    sign.setPhone(userBankCard.getPhone());
                    sign.setBankNumber(userBankCard.getBankNumber());
                    sign.setSignStatus(Integer.parseInt(registerRespVo.getP3_status())); // 签约状态 0-未签约，1-已签约，2-已解约
                    sign.setProtocolno(registerRespVo.getP8_protocolNo());
                    sign.setUpdateTime(new Date());
                    sign.setUpdateUser(member.getMemberAccount());
                    sign.setRemark("纳呗平台签约注册修改");
                    memberToSignNabeiService.editByIdSelective(sign);
                }
                // 保存会员签约纳呗变更记录
                MemberToSignNabeiChangeRecordEntity signRecord = new MemberToSignNabeiChangeRecordEntity();
                BeanUtils.copyProperties(sign, signRecord);
                signRecord.setId(null);
                memberToSignNabeiChangeRecordService.insertSelective(signRecord);
            }else if(Objects.equals(registerRespVo.getP1_resCode(), "0017")){

                // 修改会员签约纳呗记录
                MemberToSignNabeiEntity sign = new MemberToSignNabeiEntity();
                sign.setOemCode(oemCode);
                sign.setMemberId(userId);
                sign = memberToSignNabeiService.selectOne(sign);
                if(null != sign){
                    sign.setLevelNo(memberLevel.getLevelNo());
                    sign.setBankName(userBankCard.getBankName());
                    sign.setBankCode(userBankCard.getBankCode());
                    sign.setUserName(userBankCard.getUserName());
                    sign.setIdCard(userBankCard.getIdCard());
                    sign.setPhone(userBankCard.getPhone());
                    sign.setBankNumber(userBankCard.getBankNumber());
                    sign.setSignStatus(1); // 签约状态 0-未签约，1-已签约，2-已解约
                    sign.setProtocolno(registerRespVo.getP8_protocolNo());
                    sign.setUpdateTime(new Date());
                    sign.setUpdateUser(member.getMemberAccount());
                    sign.setRemark("纳呗平台签约注册修改");
                    memberToSignNabeiService.editByIdSelective(sign);
                }

                // 保存会员签约纳呗变更记录
                MemberToSignNabeiChangeRecordEntity signRecord = new MemberToSignNabeiChangeRecordEntity();
                BeanUtils.copyProperties(sign, signRecord);
                signRecord.setId(null);
                memberToSignNabeiChangeRecordService.insertSelective(signRecord);
            }else{
                // 保存会员签约纳呗错误变更记录
                MemberToSignNabeiChangeRecordEntity signRecord = new MemberToSignNabeiChangeRecordEntity();
                signRecord.setOemCode(oemCode);
                signRecord.setMemberId(userId);
                signRecord.setLevelNo(memberLevel.getLevelNo());
                signRecord.setBankName(userBankCard.getBankName());
                signRecord.setBankCode(userBankCard.getBankCode());
                signRecord.setUserName(userBankCard.getUserName());
                signRecord.setIdCard(userBankCard.getIdCard());
                signRecord.setPhone(userBankCard.getPhone());
                signRecord.setBankNumber(userBankCard.getBankNumber());
                signRecord.setSignStatus(Integer.parseInt(registerRespVo.getP3_status())); // 签约状态 0-未签约，1-已签约，2-已解约
                signRecord.setProtocolno(registerRespVo.getP8_protocolNo());
                signRecord.setAddTime(new Date());
                signRecord.setAddUser(member.getMemberAccount());
                signRecord.setRemark(registerRespVo.getP2_resMsg());
                memberToSignNabeiChangeRecordService.insertSelective(signRecord);
                throw new BusinessException(registerRespVo.getP2_resMsg());
            }
        }else{
            throw new BusinessException("纳呗签约注册接口异常");
        }

        log.info("调用纳呗平台签约注册结束......");
    }

    @Override
    public Map<String, Object> nabeiH5Sign(Long userId, String oemCode) {
        log.info("调用纳呗平台签约申请开始......");
        Map<String,Object> result = new HashMap<String,Object>();

        // 读取纳呗平台API相关配置 paramsType=10
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 10);
        if (null == paramsEntity) {
            throw new BusinessException("未配置纳呗平台API相关信息！");
        }

        // 查询用户信息
        MemberAccountEntity member = memberAccountService.findById(userId);
        if (null == member) {
            throw new BusinessException("未查询到用户信息");
        }
        if (member.getAuthStatus() != 1) {
            throw new BusinessException("用户未实名");
        }

        // 查询用户银行卡信息
        UserBankCardEntity userBankCard = bankCardMapper.getBankCardInfoByUserIdAndUserType(userId, 1, oemCode);
        if(null == userBankCard){
            throw new BusinessException("用户银行卡信息为空！");
        }

        // 随机生成平台唯一订单号
        String orderNo = OrderNoFactory.getOrderCode(userId);

        // 调用纳呗平台签约注册查询
        APIH5SignRespVo apih5SignRespVo = nabeiApiService.h5Sign(paramsEntity, orderNo, member.getRealName(), member.getIdCardNo(), 1, userBankCard.getBankNumber());
        if(null != apih5SignRespVo){
            if(Objects.equals(apih5SignRespVo.getP1_resCode(), "0000")){
                if ("1".equals(apih5SignRespVo.getP3_status())) {
                    result.put("signUrl", apih5SignRespVo.getP5_signUrl());
                } else {
                    throw new BusinessException("纳呗签约申请失败，原因：" + apih5SignRespVo.getP4_message());
                }
            }else{
                throw new BusinessException("纳呗签约申请失败，原因：" + apih5SignRespVo.getP2_resMsg());
            }
        }else{
            throw new BusinessException("纳呗签约申请接口异常");
        }
        log.info("调用纳呗平台签约申请结束......");
        return result;
    }
}