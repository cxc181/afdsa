package com.yuqian.itax.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.entity.query.OemAccessPartyQuery;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.enums.OemStatusEnum;
import com.yuqian.itax.agent.service.*;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.park.entity.ParkCorporateAccountConfigEntity;
import com.yuqian.itax.park.service.ParkCorporateAccountConfigService;
import com.yuqian.itax.system.entity.*;
import com.yuqian.itax.system.entity.vo.PosterBase64VO;
import com.yuqian.itax.system.entity.vo.SysCityVO;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.user.dao.MemberAccountMapper;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.dto.*;
import com.yuqian.itax.user.entity.query.*;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.enums.*;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.entity.Node;
import com.yuqian.itax.util.entity.TreeNode;
import com.yuqian.itax.util.entity.UserAuthVO;
import com.yuqian.itax.util.util.*;
import com.yuqian.itax.util.util.guojin.GuoJinUtil;
import com.yuqian.itax.wechat.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import sun.misc.BASE64Decoder;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service("memberAccountService")
public class MemberAccountServiceImpl extends BaseServiceImpl<MemberAccountEntity,MemberAccountMapper> implements MemberAccountService {
    @Resource
    private MemberAccountMapper memberAccountMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    private CityService sysCityService;
    @Autowired
    private ProvinceService sysProvinceService;
    @Autowired
    private UserCapitalAccountService userCapitalAccountService;
    @Autowired
    private DictionaryService sysDictionaryService;
    @Autowired
    private MemberProfitsRulesService memberProfitsRulesService;
    @Autowired
    private UserService userService;
    @Autowired
    private OemService oemService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private UserOrderStatisticsDayService userOrderStatisticsDayService;
    @Autowired
    private OssService ossService;
    @Autowired
    private InvoiceInfoByOemService invoiceInfoByOemService;
    @Autowired
    private WeChatService weChatService;
    @Autowired
    private PromotionPosterService promotionPosterService;
    @Autowired
    private ParkCorporateAccountConfigService parkCorporateAccountConfigService;
    @Autowired
    private UserExtendService userExtendService;
    @Autowired
    private OemInvoiceCategoryRelaService oemInvoiceCategoryRelaService;
    @Autowired
    private OemConfigService oemConfigService;
    @Autowired
    private MemberAccountChangeService memberAccountChangeService;
    @Autowired
    private UserBankCardService userBankCardService;
    @Autowired
    private ChannelInfoService channelInfoService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Resource
    private OemAccessPartyService oemAccessPartyService;
    @Resource
    private CrowdLabelService crowdLabelService;
    @Resource
    private MemberCrowdLabelRelaService memberCrowdLabelRelaService;
    @Resource
    private DictionaryService dictionaryService;
    @Autowired
    private CompanyCorporateAccountService companyCorporateAccountService;

    @Override
    public MemberAccountEntity queryByAccount(String account, String oemCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("account", account);
        params.put("oemCode", oemCode);
        return memberAccountMapper.queryMemberByAccount(params);
    }


    /**
     * 根据层级账号获取会员层级信息
     * @param account
     * @param memberId
     * @return
     */
    @Override
    public MemberAccountEntity findMemberTreeByAccount(String account,Long memberId){
        return memberAccountMapper.findMemberTreeByAccount(account,memberId);
    }

    @Override
    @Transactional(rollbackFor={Exception.class})
    public MemberAccountEntity registerAccount(String account,String oemCode,String inviterAccount,Integer memberType, UserEntity userEntity,String memberName,Integer regChannel,Integer sourceType,String channelCode) throws BusinessException {
        log.info("会员/员工注册请求处理：{}，{}，{}，{}，{},{},{}",account,oemCode,inviterAccount,memberType,userEntity,regChannel,sourceType);

        if(null == memberType){
            throw new BusinessException("注册会员类型不能为空！");
        }

        if(!(memberType.intValue() == MemberLevelEnum.NORMAL.getValue() ||
                memberType.intValue() == MemberLevelEnum.DIAMOND.getValue() ||  memberType.intValue() == -1)){// 0会员 -1员工 2城市服务商
            throw new BusinessException("非法的注册会员类型");
        }

        // 如果是城市服务商注册，注册渠道必须是后台
        if(MemberLevelEnum.DIAMOND.getValue().equals(memberType) && regChannel.intValue() != 1){
            throw new BusinessException("非法注册操作，请检查");
        }

        //存储会员账号信息
        MemberAccountEntity memberAccountEntity = new MemberAccountEntity();
        memberAccountEntity.setMemberAccount(account);
        memberAccountEntity.setMemberPhone(account);// 初始值默认与帐号一致
        memberAccountEntity.setOemCode(oemCode);
        memberAccountEntity.setSourceType(sourceType);// 操作来源
        memberAccountEntity.setAuthStatus(MemberAuthStatusEnum.UN_AUTH.getValue());// 会员实名认证状态，默认为“未认证”
        // 查询会员默认等级 -1-员工 0-普通会员 1-税务顾问 2-城市服务商
        MemberLevelEntity level = this.memberLevelService.queryMemberLevel(oemCode, memberType);
        if(null == level){
            throw new BusinessException("操作失败，未配置会员等级信息！");
        }
        memberAccountEntity.setMemberLevel(level.getId());
        memberAccountEntity.setMemberName(account);// 昵称默认为手机号
        memberAccountEntity.setLevelName(level.getLevelName());

        // 判断OEM机构是否存在
        OemEntity oem = this.oemService.getOem(oemCode);
        if(null == oem){
            throw new BusinessException("操作失败，OEM机构不存在");
        }
        //判断是否为oem机构挂靠用户
        boolean parentUserIsOem = false;
        if(oem.getIsInviterCheck()==0){  //不校验邀请码，则取默认要求人账号
            inviterAccount = StringUtil.isNotBlank(oem.getInviterAccount())?oem.getInviterAccount():oem.getOemPhone();
            parentUserIsOem = true;
        }

        //设置城市服务商会默认的员工上线
        if(level.getLevelNo().equals(MemberLevelEnum.DIAMOND.getValue())){
            memberAccountEntity.setEmployeesLimit(oem.getEmployeesLimit());
        }

        MemberAccountEntity invitor = null;// 邀请人信息
//        MemberLevelEntity invitorLevel = null;// 邀请人等级信息
        if(StringUtils.isNotBlank(inviterAccount)){
            boolean isOpenChannel = true;
            //判断是否接入国金助手，如果接入则查询国金助手，否则提示邀请码错误
            OemConfigEntity oemConfigEntity = new OemConfigEntity();
            oemConfigEntity.setOemCode(oemCode);
            oemConfigEntity.setParamsCode("is_open_channel");
            List<OemConfigEntity> configList = oemConfigService.select(oemConfigEntity);
            if(!MatcherUtil.isPhone(inviterAccount) && configList!=null && configList.size() == 1){
                oemConfigEntity = configList.get(0);
                String value = oemConfigEntity.getParamsValue();
                if(StringUtils.isNotBlank(value) && "1".equals(value)){
//                    if(StringUtils.isBlank(channelCode)){
//                        throw new BusinessException("国金助手编号不能为空！");
//                    }
                    //调用国金 add ni.jiang
                    OemParamsEntity oemParamsEntity = oemParamsService.getParams(oemCode,OemParamsTypeEnum.GUOJIN_CHANNEL_CONFIG.getValue());
                    if(oemParamsEntity==null){
                        throw new BusinessException("未配置国金助手信息，请联系客服！");
                    }
                    Map<String,Object> dataParams = new HashMap<>();
                    dataParams.put("inviteCode",inviterAccount);
                    dataParams.put("productCode",oemCode);
                    dataParams.put("productType",1);
                    JSONObject jsonObject = GuoJinUtil.gjChannel(dataParams,channelCode,oemParamsEntity.getSecKey(),oemParamsEntity.getUrl()+GuoJinUtil.CHANNEL_SERVICE_USER);
                    if ("0000".equals(jsonObject.getString("retCode"))&&jsonObject.containsKey("data")){
                        jsonObject = jsonObject.getJSONObject("data");
                        if(jsonObject!=null){
                            if("2".equals(jsonObject.getString("userRole"))){ //员工
                                memberAccountEntity.setChannelServiceId(jsonObject.getLong("providerId"));
                                memberAccountEntity.setChannelEmployeesId(jsonObject.getLong("userId"));
                            }else{
                                memberAccountEntity.setChannelServiceId(jsonObject.getLong("userId"));
                            }
                            memberAccountEntity.setChannelProductCode(jsonObject.getString("productCode"));
                            memberAccountEntity.setChannelCode(jsonObject.getString("oemCode"));
                            memberAccountEntity.setInviteCode(inviterAccount+"|国金助手邀请");
                        }
                    }else{
                        throw new BusinessException(jsonObject.getString("retMsg"));
                    }
                }else{
                    isOpenChannel = false;
                }
            }else{
                isOpenChannel = false;
            }
            if(!isOpenChannel && !MatcherUtil.isPhone(inviterAccount) && oem.getIsInviterCheck() == 0){// 拓展宝定制需求
                // 邀请人账号格式校验
                String regex = "^[a-zA-Z0-9]{6,20}$";
                if(!Pattern.matches(regex,inviterAccount)){
                    throw new BusinessException(ResultConstants.INVITE_CODE_INVALID);
                }
                // 绑定会员邀请关系
                memberAccountEntity.setInviteCode(inviterAccount);
                memberAccountEntity.setParentMemberAccount(inviterAccount);
            }else if(!isOpenChannel){// 正常流程
                //判断邀请码是否为手机号，如果是则查询本系统用户，没查询到则查询oem机构绑定的手机号
                boolean flag = MatcherUtil.isPhone(inviterAccount);
                if(flag) {
                    // 邀请人校验：邀请人账号不为空，判断邀请人信息是否存在
                    if(!parentUserIsOem) {
                        invitor = this.queryByAccount(inviterAccount, oemCode);
                    }
                    if (null == invitor) {
                        //查询oem机构管理员信息
                        UserAndExtendVO userAndExtendVO = userService.qeruyUserByUsernameAndOemCode(oemCode,null,inviterAccount);
                        if(userAndExtendVO==null) {
                            throw new BusinessException(ResultConstants.INVITOR_NOT_EXISTS);
                        }
                        memberAccountEntity.setInviteCode(inviterAccount+"|oem机构管理员邀请");
                    }else{
                        // 绑定会员邀请关系
                        memberAccountEntity.setInviteCode(inviterAccount);
                        memberAccountEntity.setChannelServiceId(invitor.getChannelServiceId());
                        memberAccountEntity.setChannelEmployeesId(invitor.getChannelEmployeesId());
                        memberAccountEntity.setChannelProductCode(invitor.getChannelProductCode());
                        memberAccountEntity.setChannelCode(invitor.getChannelCode());
                        memberAccountEntity.setParentMemberId(invitor.getId());
                    }
                }else{
                    throw new BusinessException("邀请码格式不正确,请输入正确的手机号");
                }
                // 备注：1.0.7需求，去掉邀请人权限限制，所有等级会员（包括员工）都具备邀请权限
                /*if(null == invitorLevel ||  MemberLevelEnum.NORMAL.getValue().equals(invitorLevel.getLevelNo())){
                    throw new BusinessException(ResultConstants.INVITOR_NO_PERMISSION);
                }*/

                // 绑定会员邀请关系
                memberAccountEntity.setParentMemberAccount(inviterAccount);

                // 员工注册
                if(memberType == -1){
                   /* // 判断邀请人是否已禁用
                    if(MemberStateEnum.STATE_UNACTIVE.getValue().equals(invitor.getStatus())){
                        throw new BusinessException(ResultConstants.INVITOR_FORBIDDEN);
                    }*/

                    // 新增姓名字段，对应用户昵称
                    if(StringUtils.isBlank(memberName)){
                        throw new BusinessException(ResultConstants.STAFF_NAME_CANNOT_NULL);
                    }

                    memberAccountEntity.setMemberName(memberName);// 员工注册时取前端传递过来的姓名字段

                    // 判断邀请人当前正常的员工账号数量是否已达到上限
                    if(null != invitor && checkInviteLimit(invitor)){
                        throw new BusinessException(ResultConstants.STAFF_COUNT_LIMIT);
                    }
                }
            }
        }else{
            //代理商生成推广城市服务商用户的时候邀请人账号为空还是为OEM机构账号
            if(userEntity == null){
                throw new BusinessException("邀请人账号不能为空");
            }
        }

        // 其它字段补充
        if(memberType > -1){
            memberAccountEntity.setMemberType(MemberTypeEnum.MEMBER.getValue());
        }else {
            memberAccountEntity.setMemberType(MemberTypeEnum.EMPLOYEE.getValue());
        }
        memberAccountEntity.setStatus(MemberStateEnum.STATE_ACTIVE.getValue());
        memberAccountEntity.setAddTime(new Date());
        memberAccountEntity.setAddUser(account);
        this.insertSelective(memberAccountEntity);

        // 设置会员层级关系
        MemberAccountEntity entity = new MemberAccountEntity();
        entity.setId(memberAccountEntity.getId());
        String parentMemberTree = "";
        if(invitor != null){
            parentMemberTree = invitor.getMemberTree();
        }

        if(StringUtils.isBlank(parentMemberTree)){
            entity.setMemberTree(memberAccountEntity.getId() + "");
        }else{
            entity.setMemberTree(parentMemberTree + "/" + memberAccountEntity.getId());
        }
        entity.setUpdateTime(new Date());

        // 设置会员对应的推广类型（散客），所属员工，上级城市服务商，上上级城市服务商数据
        if(null != invitor){
            setExtendInfo(invitor,memberAccountEntity,entity,memberType);
        }else if(regChannel.intValue() == 1){// 后台城市服务商注册时
            entity.setExtendType(ExtendTypeEnum.TOP_STRAIGHT_CUSTOMER.getValue());
            entity.setEmployeesLimit(oemService.getOem(memberAccountEntity.getOemCode()).getEmployeesLimit());
        }

        this.editByIdSelective(entity);

        //新增用户关系表
        if(null == userEntity){
            userService.insertUserRelaEntity(memberAccountEntity.getOemCode(),memberAccountEntity.getId(),5,invitor == null ? null : invitor.getId(),5,memberAccountEntity.getMemberAccount(),null,null);
        }else if( userEntity.getPlatformType() == 4){
            userService.insertUserRelaEntity(memberAccountEntity.getOemCode(),memberAccountEntity.getId(),5,userEntity.getId(),3,memberAccountEntity.getMemberAccount(),1,null);
        }else if(userEntity.getPlatformType() == 5){
            userService.insertUserRelaEntity(memberAccountEntity.getOemCode(),memberAccountEntity.getId(),5,userEntity.getId(),4,memberAccountEntity.getMemberAccount(),null,null);
        }

        // 生成会员消费钱包资金账户信息
        UserCapitalAccountEntity uca = new UserCapitalAccountEntity();
        uca.setAddUser(memberAccountEntity.getMemberAccount());
        uca.setAddTime(new Date());
        uca.setOemCode(oemCode);
        uca.setWalletType(1);// 消费钱包
        uca.setUserId(memberAccountEntity.getId());
        uca.setUserType(1);// 用户类型 1-会员 2 -系统用户
        uca.setCapitalAccount(UniqueNumGenerator.generateUniqueNo());
        uca.setTotalAmount(0L);// 初始账户金额位0
        uca.setAvailableAmount(0L);
        uca.setBlockAmount(0L);
        uca.setStatus(MemberStateEnum.STATE_ACTIVE.getValue());// 状态默认为可用
        this.userCapitalAccountService.insertSelective(uca);

        // 生成会员佣金钱包资金账户
        UserCapitalAccountEntity ucaCommission = new UserCapitalAccountEntity();
        BeanUtils.copyProperties(uca,ucaCommission);
        ucaCommission.setId(null);// 清空ID
        ucaCommission.setWalletType(2);// 佣金钱包
        ucaCommission.setCapitalAccount(UniqueNumGenerator.generateUniqueNo());
        this.userCapitalAccountService.insertSelective(ucaCommission);

        //添加会员变动表
        MemberAccountChangeEntity memberAccountChangeEntity=new MemberAccountChangeEntity();
        BeanUtils.copyProperties(memberAccountEntity,memberAccountChangeEntity);
        memberAccountChangeEntity.setAccountId(entity.getId());
        memberAccountChangeEntity.setId(null);
        memberAccountChangeEntity.setAddTime(new Date());
        memberAccountChangeEntity.setAddUser(memberAccountEntity.getMemberAccount());
        memberAccountChangeEntity.setRemark("会员注册");
        memberAccountChangeService.insertSelective(memberAccountChangeEntity);
        log.info("会员/员工注册成功：{}",JSON.toJSONString(memberAccountEntity));
        return memberAccountEntity;
    }

    /**
     * @Description 设置会员对应的推广类型（散客），所属员工，上级城市服务商，上上级城市服务商数据
     * @Author  Kaven
     * @Date   2020/6/4 10:03
     * @Param   invitor invitorLevel memberAccountEntity-当前已保存会员信息 entity-待更新会员信息 memberType
     * @Return  void
     * @Exception
    */
    private void setExtendInfo(MemberAccountEntity invitor, MemberAccountEntity memberAccountEntity, MemberAccountEntity entity, Integer memberType) {
        // 查询邀请人等级信息
        MemberLevelEntity invitorLevel = this.memberLevelService.findById(invitor.getMemberLevel());
        // entity.setExtendType(ExtendTypeEnum.INDEPENDENT_CUSTOMER.getValue());// 默认除员工外的所有会员推广角色全部为"散客"

        // V2.2需求调整：用户注册默认设置为直客
        entity.setExtendType(ExtendTypeEnum.STRAIGHT_CUSTOMER.getValue());

        // 如果邀请人为城市服务商，无所属员工
        if(MemberLevelEnum.DIAMOND.getValue().equals(invitorLevel.getLevelNo())){
            entity.setSuperDiamondId(invitor.getUpDiamondId());// 上上级
            entity.setSuperDiamondAccount(invitor.getUpDiamondAccount());
            entity.setUpDiamondId(invitor.getId());// 上级
            entity.setUpDiamondAccount(invitor.getMemberAccount());
            entity.setSuperEmployeesId(invitor.getAttributionEmployeesId());// 上上级员工
            entity.setSuperEmployeesAccount(invitor.getAttributionEmployeesAccount());
        }else if(MemberLevelEnum.MEMBER.getValue().equals(invitorLevel.getLevelNo())){ // 如果邀请人是员工
            entity.setAttributionEmployeesId(invitor.getId());
            entity.setAttributionEmployeesAccount(invitor.getMemberAccount());
            entity.setUpDiamondId(invitor.getParentMemberId());
            entity.setUpDiamondAccount(invitor.getParentMemberAccount());
            entity.setSuperEmployeesId(invitor.getSuperEmployeesId());
            entity.setSuperEmployeesAccount(invitor.getSuperEmployeesAccount());
            entity.setSuperDiamondId(invitor.getSuperDiamondId());
            entity.setSuperDiamondAccount(invitor.getSuperDiamondAccount());
        } else{ // 邀请人是普通会员
            entity.setAttributionEmployeesId(invitor.getAttributionEmployeesId());
            entity.setAttributionEmployeesAccount(invitor.getAttributionEmployeesAccount());
            entity.setUpDiamondId(invitor.getUpDiamondId());
            entity.setUpDiamondAccount(invitor.getUpDiamondAccount());
            entity.setSuperEmployeesId(invitor.getSuperEmployeesId());
            entity.setSuperEmployeesAccount(invitor.getSuperEmployeesAccount());
            entity.setSuperDiamondId(invitor.getSuperDiamondId());
            entity.setSuperDiamondAccount(invitor.getSuperDiamondAccount());
        }
        // 注册用户为员工时，推广角色为顶级直客
        if(memberType == -1){
            entity.setExtendType(ExtendTypeEnum.TOP_STRAIGHT_CUSTOMER.getValue());
        }
    }

    /**
     * @Description 判断邀请人当前正常邀请的员工账号数量是否已达到上限
     * @Author  Kaven
     * @Date   2020/4/20 10:08
     * @Param invitor-邀请人信息
     * @Return  boolean
     * @Exception
    */
    private boolean checkInviteLimit(MemberAccountEntity invitor) throws BusinessException {
        MemberCountVO countVO = this.getStaffCount(invitor.getId(),invitor.getOemCode());
        return countVO.getTotalCount() >= countVO.getLimitCount();
    }

    @Override
    public void updateUserRegion(Long userId,String provinceCode, String cityCode, String cityName) throws BusinessException {
        MemberAccountEntity account = this.memberAccountMapper.selectByPrimaryKey(userId);
        if(null == account){
            throw new BusinessException("更新失败，用户信息不存在");
        }
        if(StringUtils.isNotBlank(account.getCityCode())){
            throw new BusinessException("您已经修改过城市信息，不能再次修改");
        }
        // 小程序定位传过来的是城市名称
        if(StringUtils.isNotBlank(cityName)){
            // 根据城市名称查出省市编码进行更新
            SysCityVO cityVO = this.sysCityService.getCityByName(cityName);
            if(null == cityVO){
                throw new BusinessException("操作失败，系统无法匹配定位的城市信息！");
            }
            account.setProvinceCode(cityVO.getProvinceCode());
            account.setCityCode(cityVO.getCode());
        }else{
            // 用户自己选择的城市，传过来的是省市编码
            CityEntity city = this.sysCityService.getByCode(cityCode);
            if(null == city){
                throw new BusinessException("未查询到指定编码的城市信息，请联系客服");
            }
            account.setProvinceCode(city.getProvinceCode());
            account.setCityCode(cityCode);
        }
        this.editByIdSelective(account);
    }

    @Override
    public PageInfo<MemberPageInfoVO> memberPageInfo(MemberQuery memberQuery) {
        PageHelper.startPage(memberQuery.getPageNumber(), memberQuery.getPageSize());
        return new PageInfo<>(this.mapper.queryMemberList(memberQuery));
    }

    @Override
    public List<BatchMenberInfoExportVO> batchMemberInfo(MemberQuery memberQuery) {
        return mapper.batchMemberList(memberQuery);
    }

    @Override
    @Transactional(rollbackFor={Exception.class})
    public Map<String,Object> userLogin(MemberLoginDTO loginDto) throws BusinessException {
        String token = "";

        // 验证码校验
        String registRedisTime = (System.currentTimeMillis() + 300000) + "";
        //验证码验证
        String verficationCode = redisService.get(RedisKey.SMS_USER_LOGIN_KEY_SUFFER + loginDto.getAccount());
        //验证码错误或过期
        if (verficationCode == null || "".equals(verficationCode) || !loginDto.getVerifyCode().equals(verficationCode)) {
            //释放redis锁
            redisService.unlock(RedisKey.SMS_USER_LOGIN_KEY_SUFFER + loginDto.getAccount(), registRedisTime);
            throw new BusinessException(MessageEnum.PASSWORD_RESET_CODE_IS_EXPIRED.getMessage());
        }
        redisService.delete(RedisKey.SMS_USER_LOGIN_KEY_SUFFER + loginDto.getAccount());
        Long accountId = null;// 临时变量，存储返回的用户ID
        boolean inviteFlag = false;// 是否被邀请标识

        // 组装登录(注册)返回参数
        Map<String,Object> dataMap = new HashMap<String,Object>();

        MemberAccountEntity memberAccountEntity = this.queryByAccount(loginDto.getAccount(),loginDto.getOemCode());
        if (memberAccountEntity != null) {
            // 用户已存在，则视为登录操作
            if(memberAccountEntity.getStatus() == 0){
                throw new BusinessException(MessageEnum.MEMBER_ACCOUNT_DISABLE.getMessage());
            }
            if(memberAccountEntity.getStatus() == 2){
                // throw new BusinessException(MessageEnum.MEMBER_ACCOUNT_CANCEL.getMessage());
                log.info("用户已注销，需要重新进行注册");
            }
            // 如果附带了邀请码，需要判断会员是否已经被其他邀请人邀请过
            if(StringUtils.isNotBlank(loginDto.getInviterAccount()) && !loginDto.getInviterAccount().equals(memberAccountEntity.getParentMemberAccount())
                    && StringUtils.isNotBlank(memberAccountEntity.getParentMemberAccount())){
                log.info("您已经被" + memberAccountEntity.getParentMemberAccount() + "邀请过，无法被再次邀请！继续登录...");
                inviteFlag = true;
                // throw new BusinessException("您已经被" + memberAccountEntity.getParentMemberAccount() + "邀请过，无法被再次邀请！");
            }
        } else {
            // 接入国金渠道的oem机构关闭注册功能
            OemConfigEntity configEntity = Optional.ofNullable(oemConfigService.queryOemConfigByCode(loginDto.getOemCode(), "is_open_channel")).orElseThrow(() -> new BusinessException("未查询到机构配置信息"));
            if (Objects.equals(configEntity.getParamsValue(), "1")) {
                throw new BusinessException("该手机号未注册，请联系您的邀请人");
            }
            //会员账号不存在，直接注册
            memberAccountEntity = this.registerAccount(loginDto.getAccount(),loginDto.getOemCode(),loginDto.getInviterAccount(),loginDto.getMemberType(),null,loginDto.getMemberName(),0,loginDto.getSourceType(), loginDto.getChannelCode());
            if(loginDto.getMemberType() != -1){ // 会员日统计数据不统计员工
                dataMap.put("memberId",memberAccountEntity.getId());
                dataMap.put("isRegFlag","1");// 注册标识，以便于controller中注册完成后统计会员日推广数据
            }
        }
        accountId = memberAccountEntity.getId();

        // 清除该用户旧的token，避免多用户用同一个账号登录 add by Kaven 2020-03-04
        String oldToken = redisService.get(RedisKey.LOGIN_TOKEN_KEY + loginDto.getOemCode() + "_" + "userId_1_" + accountId);
        redisService.delete(RedisKey.LOGIN_TOKEN_KEY + loginDto.getOemCode() + "_" + oldToken);

        // 各项验证通过，生成新的token放缓存
        token = createToken();
        dataMap.put("token",token);
        String outTime = sysDictionaryService.getByCode("redis_token_outtime").getDictValue();
        redisService.set(RedisKey.LOGIN_TOKEN_KEY + loginDto.getOemCode() + "_" + "userId_1_" + accountId,token,Integer.parseInt(outTime));
        redisService.set(RedisKey.LOGIN_TOKEN_KEY + loginDto.getOemCode() + "_" + token,new CurrUser(accountId,loginDto.getAccount(),loginDto.getOemCode(),null),Integer.parseInt(outTime));

        // 获取并更新用户微信openId/支付宝用户号
        updateMemberOpenIdOrUserId(accountId,loginDto);

        // 根据是否被邀请过标识，判断是否给前端传递提示字段
        if(inviteFlag){
            dataMap.put("extraMsg","您已经被" + memberAccountEntity.getParentMemberAccount() + "邀请过，无法被再次邀请！");
        }

        // 返回会员基本信息
        MemberBaseInfoVO baseInfo = this.getMemberBaseInfo(accountId);
        dataMap.put("userInfo",baseInfo);

        log.info("登录成功，返回结果：{}", JSON.toJSONString(dataMap));
        return dataMap;
    }

    /**
     * @Description 更新用户openId或alipayUserId
     * @Author  Kaven
     * @Date   2020/10/23 17:39
     * @Param   accountId
     * @Return  loginDto
     * @Exception
    */
    public void updateMemberOpenIdOrUserId(Long accountId,MemberLoginDTO loginDto) {
        MemberAccountEntity t = new MemberAccountEntity();
        t.setId(accountId);
        t.setUpdateTime(new Date());
        t.setUpdateUser(loginDto.getAccount());
        if(loginDto.getSourceType().intValue() == 1){ // 微信
            // 是否其他机构代收单
            OemEntity oem = Optional.ofNullable(oemService.getOem(loginDto.getOemCode())).orElseThrow(() -> new BusinessException("未查询到机构信息"));
            if (null != oem.getIsOtherOemPay() && oem.getIsOtherOemPay() == 1) {
                // 使用代收单机构
                loginDto.setOtherPayOemCode(oem.getOtherPayOemcode());
            } else {
                loginDto.setOtherPayOemCode(loginDto.getOemCode());
            }
            OemParamsEntity paramsEntity = this.oemParamsService.getParams(loginDto.getOtherPayOemCode(), 2);
            if(null == paramsEntity){
                throw new BusinessException("获取用户openId失败：未配置微信支付相关信息！");
            }
            // 解析paramValues，配置样例：{"appId": "wxb884fccbb878f5b8","keyNum": "bb9aa8f2499c329b88f37567dd9aab31","signKey": "c4ac143ecafba42f528d1fcbec8c531f"}
            JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
            String appId = params.getString("appId");
            String appSecret = paramsEntity.getSecKey();
            String openId = WechatPayUtils.getWxOpenId(appId,appSecret,loginDto.getJsCode());
            t.setOpenId(openId);
        }else if(loginDto.getSourceType().intValue() == 2) { // 支付宝
            OemParamsEntity paramsEntity = this.oemParamsService.getParams(loginDto.getOemCode(),20);
            if(null == paramsEntity){
                throw new BusinessException("获取用户支付宝用户ID失败：未配置支付宝支付相关信息！");
            }
            // 解析paramValues
            JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
            String appId = params.getString("appId");
            String privateKey = paramsEntity.getPrivateKey();
            String alipayUserId = AliPayUtils.getAccessTokenAndUserId(appId,privateKey,loginDto.getAuthCode());
            t.setAlipayUserId(alipayUserId);
        }else if(loginDto.getSourceType().intValue() == 4){ //字节跳动
            OemParamsEntity paramsEntity = this.oemParamsService.getParams(loginDto.getOemCode(),30);
            if(null == paramsEntity){
                throw new BusinessException("获取用户openId失败：未配置字节跳动相关信息！");
            }
            String appId = paramsEntity.getAccount();
            String appSecret = paramsEntity.getSecKey();
            String openId = BytedanceUtils.getBytedanceOpenId(appId,appSecret,loginDto.getJsCode(),loginDto.getAnonymousCode());
            t.setOpenId(openId);
        }
        this.editByIdSelective(t);
        log.info("登录成功，更新用户openId/alipayUserId：{}/{}",t.getOpenId(),t.getAlipayUserId());
    }

    @Override
    @Transactional
    public void updatePayPassword(UpdatePayPasswordDTO param) throws BusinessException {
        //通过账号获取用户信息
        MemberAccountEntity memberAccountEntity = new MemberAccountEntity();
        memberAccountEntity.setMemberAccount(param.getMemberAccount());
        memberAccountEntity = memberAccountMapper.selectOne(memberAccountEntity);
        if (null == memberAccountEntity) {
            throw new BusinessException("找不到该用户！");
        }

        //判断是否首次设置
        if(StringUtils.isEmpty(memberAccountEntity.getPayPassword())){
            memberAccountEntity.setPayPassword(MemberPsdUtil.encrypt(param.getNewPassword(), param.getMemberAccount(), memberAccountEntity.getSlat()));
            memberAccountMapper.updateByPrimaryKey(memberAccountEntity);
        }else{
            if(StrUtil.isEmpty(param.getMemberAccount())
                    ||StrUtil.isEmpty(param.getOldPassword())
                    ||StrUtil.isEmpty(param.getNewPassword())){
                throw new BusinessException(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
            }

            //校验旧密码
            String oldPwd= MemberPsdUtil.encrypt(param.getOldPassword(), param.getMemberAccount(), memberAccountEntity.getSlat());
            if(!StringUtils.equalsIgnoreCase(oldPwd,memberAccountEntity.getPayPassword())){
                throw new BusinessException("旧密码错误！");
            }
            memberAccountEntity.setPayPassword(MemberPsdUtil.encrypt(param.getNewPassword(), param.getMemberAccount(), memberAccountEntity.getSlat()));
            memberAccountMapper.updateByPrimaryKey(memberAccountEntity);
        }
    }

    /**
     * @Description 创建token
     * @Author  Kaven
     * @Date   2019/12/9 10:53
     * @Return String
     */
    private String createToken() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    @Override
    public void updateStatus(Long id, Integer status, String updateUser) {
        mapper.updateStatus(id, status, updateUser, new Date());
    }

    private Map<String,Object> paramsAssemble(String userId,String channelCode,String oemCode){
        Map<String,Object> dataParams = new HashMap<String,Object>();
        dataParams.put("userId",userId);
        dataParams.put("oemCode",channelCode);
        dataParams.put("productCode",oemCode);
        //产品类型 1：云财  2：快开 3：纳呗
        dataParams.put("productType",1);
        return dataParams;
    }

    @Override
    public MemberBaseInfoVO getMemberBaseInfo(Long userId) throws BusinessException {
        MemberBaseInfoVO baseInfo = new MemberBaseInfoVO();
        MemberAccountEntity entity = this.memberAccountMapper.selectByPrimaryKey(userId);
        if(null == entity){
            throw new BusinessException("用户信息不存在");
        }
        // 参数拷贝
        BeanUtils.copyProperties(entity,baseInfo);
        // 身份证是否已过期
        if (entity.getAuthStatus() == 1 && StringUtil.isNotBlank(entity.getExpireDate())) {
            String[] split = entity.getExpireDate().split("-");
            if (split.length == 2 && !"长期".equals(split[1])
                    && DateUtil.parseDefaultDate(split[1].replace(".","-")).before(DateUtil.parseDefaultDate(DateUtil.formatDefaultDate(new Date())))) {
                baseInfo.setIsExpiredDocuments(1);
            }
        }
        // 优先显示渠道员工id，如渠道员工id为空则显示渠道服务商id
        Long channelId = null;
        if (entity.getChannelEmployeesId() != null){
            channelId = entity.getChannelEmployeesId();
        }else{
            channelId = entity.getChannelServiceId();
        }
        baseInfo.setServiceId(channelId);
        if (channelId != null && StringUtils.isNotBlank(entity.getChannelCode())){
            Map<String,Object> dataMap = paramsAssemble(channelId.toString(),entity.getChannelCode(),entity.getOemCode());
            OemParamsEntity oemParamsEntity =  oemParamsService.getParams(entity.getOemCode(),OemParamsTypeEnum.GUOJIN_CHANNEL_CONFIG.getValue());
            //请求国金服务商信息查询接口
            JSONObject jsonObject = GuoJinUtil.getUserInfoToGuoJin(dataMap,entity.getChannelCode(),oemParamsEntity.getSecKey(), oemParamsEntity.getUrl());
            if (jsonObject != null && "0000".equals(jsonObject.getString("retCode"))){
                JSONObject data = jsonObject.getJSONObject("data");
                if(data!=null){
                    //  获取服务商姓名  服务商手机号
                    baseInfo.setChannelServiceName(data.getString("userName"));
                    baseInfo.setChannelServiceAccount(data.getString("userAccount"));
                }

            }
        }
        // 查询省市名称
        if(StringUtils.isNotBlank(entity.getProvinceCode())){
            ProvinceEntity province = this.sysProvinceService.getByCode(entity.getProvinceCode());
            baseInfo.setProvinceName(province.getName());
        }
        if(StringUtils.isNotBlank(entity.getCityCode())){
            CityEntity city = this.sysCityService.getByCode(entity.getCityCode());
            baseInfo.setCityName(city.getName());
        }
        // 查询机构名称、机构客服电话和会员等级名称
        OemEntity oem = this.oemService.getOem(entity.getOemCode());
        baseInfo.setOemName(oem.getOemName());
        baseInfo.setOemMobile(oem.getCustomerServiceTel());
        MemberLevelEntity level = this.memberLevelService.findById(entity.getMemberLevel());
        baseInfo.setLevelName(level.getLevelName());

        baseInfo.setLevelNo(level.getLevelNo());

        // 查询是否有已完成的开户企业
        MemberCompanyEntity query = new MemberCompanyEntity();
        query.setMemberId(userId);
        query.setOemCode(entity.getOemCode());
        List<MemberCompanyEntity> list = this.memberCompanyService.select(query);
        if(CollectionUtil.isNotEmpty(list)){
            baseInfo.setHasFinishedCom(1);//是否有已完成的开户企业 0-无 1-有
        } else {
            baseInfo.setHasFinishedCom(0);
        }
        // 查询开票类目名称
//        InvoiceInfoByOemEntity t = new InvoiceInfoByOemEntity();
//        t.setOemCode(oem.getOemCode());
//        t.setStatus(1);
//        InvoiceInfoByOemEntity iiboe = this.invoiceInfoByOemService.selectOne(t);
//        baseInfo.setCategoryName(null != iiboe ? iiboe.getCategoryName() : null);
        //查询开票类目 V2.7
        Map<String, String> map = oemInvoiceCategoryRelaService.queryCategoryNameByOemCode(oem.getOemCode());
        baseInfo.setCategoryName(map.get("categoryName"));
        baseInfo.setCategoryBaseId(map.get("categoryBaseId"));
        // 查询有无个体户，设置是否显示"我的对公户"菜单标识
        List<ParkCorporateAccountConfigEntity> pcacList = this.parkCorporateAccountConfigService.selectByMemberId(userId);
        if(CollectionUtil.isNotEmpty(pcacList) && baseInfo.getHasFinishedCom().intValue() == 1){
            baseInfo.setHasIndividual(1);
        }

        // 查询渠道信息
        if (null != entity.getChannelCode() && !StringUtil.isBlank(entity.getChannelCode())) {
            ChannelInfoEntity channelInfo = Optional.ofNullable(channelInfoService.findByChannelCode(entity.getChannelCode())).orElseThrow(() -> new BusinessException("未查询到渠道信息"));
            baseInfo.setChannelName(channelInfo.getChannelName());
            baseInfo.setAppId(channelInfo.getAppId());
            baseInfo.setChannelLogo(channelInfo.getChannelLogo());
        }

        // 是否露出对公户:有对公户或符合对公户申请条件
        // 查询用户对公户信息
        List<CompanyCorpAccountVO> corpAccountVOList = companyCorporateAccountService.queryByMemberId(userId);
        if (null != corpAccountVOList && !corpAccountVOList.isEmpty()) {
            baseInfo.setIsShowCorporate(1);
            return baseInfo;
        }
        // 查询用户是否符合对公户申请条件
        int isApplicable = companyCorporateAccountService.applicationConditionCheck(userId);
        baseInfo.setIsShowCorporate(isApplicable);
        return baseInfo;
    }

    @Override
    public void updateNickname(Long userId, String nickName) throws BusinessException {
        if(StrUtil.isEmpty(nickName)){
            throw new BusinessException(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        MemberAccountEntity entity = this.memberAccountMapper.selectByPrimaryKey(userId);
        if(null == entity){
            throw new BusinessException("修改失败，" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        MemberAccountEntity t = new MemberAccountEntity();
        t.setId(userId);
        t.setMemberName(nickName);
        t.setUpdateTime(new Date());
        t.setUpdateUser(entity.getMemberAccount());
        this.memberAccountMapper.updateByPrimaryKeySelective(t);
    }

    @Override
    public MemberExtendVO memberExtendStats(MemberExtendQuery query) throws BusinessException {
        if(null == query.getGradeNo()){
            query.setGradeNo(1);// 默认查询一级推广记录下的“审核中”状态的开户列表
        }
        if(null == query.getOrderStatus()){
            query.setOrderStatus(2);// 默认查“审核中”状态
        }
        // 查询当前会员等级
        MemberAccountEntity member = this.memberAccountMapper.selectByPrimaryKey(query.getUserId());
        if(null == member){
            throw new BusinessException("用户信息不存在");
        }
        MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());
        query.setMemberType(level.getLevelNo());

        // 查询累积开户（推广个体）、累积（总）佣金以及各状态下的统计数据
        MemberExtendVO extendInfo = this.memberAccountMapper.queryRegOrderStats(query);
        //查询推广总数 TODO 此处还需要做拆分
        MemberExtendVO totleExtend = this.memberAccountMapper.statisRegOrderByUserId(query);
        extendInfo.setTotalCount(totleExtend.getTotalCount());
        extendInfo.setTotalAmount(totleExtend.getTotalAmount());

        // 查询当前用户推广用户数 add by Kaven 2020-05-18
        /*Long extendUserCount = this.memberAccountMapper.queryExtendUserCount(query);// 一级推广用户数
        extendInfo.setExtendUserCount(extendUserCount);
        Long extendSencondUserCount = this.memberAccountMapper.querySecondExtendUserCount(query);// 二级推广用户数
        extendInfo.setExtendSecondUserCount(extendSencondUserCount);*/

        // 只有税务顾问和城市服务商才有分润信息
        if(!MemberLevelEnum.MEMBER.getValue().equals(query.getMemberType())){
            // 查询优惠政策
            MemberProfitsRulesEntity mpre = new MemberProfitsRulesEntity();
            mpre.setStatus(MemberStateEnum.STATE_ACTIVE.getValue());
            mpre.setOemCode(query.getOemCode());
            if(MemberLevelEnum.GOLD.getValue().equals(query.getMemberType())){
                mpre.setUserLevel(MemberLevelEnum.GOLD.getValue());
            }else if(MemberLevelEnum.DIAMOND.getValue().equals(query.getMemberType())){
                mpre.setUserLevel(MemberLevelEnum.DIAMOND.getValue());
            }
            MemberProfitsRulesEntity profit = this.memberProfitsRulesService.selectOne(mpre);

            if(null == profit){
                throw new BusinessException("未找到会员分润规则信息");
            }
            extendInfo.setProfitsRate(profit.getServiceFeeRate());
            extendInfo.setMembershipRate(profit.getMembershipFee());
        }

        // 返回所属用户列表的分页信息
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<MemberVO> memberList = this.memberAccountMapper.getMemberListByStatus(query);

        PageInfo pageInfo = new PageInfo<MemberVO>(memberList);
        extendInfo.setMemberPageList(pageInfo);
        return extendInfo;
    }

    //public List<MemberStaffVO> queryMemberStaffList(Long memberId){
    //    Map<String,Object> params = new HashMap<String,Object>();
    //    params.put("memberId", memberId);
    //    params.put("addTime", DateUtil.format(new Date(),"yyyy-MM"));
    //    // 查询员工列表
    //    List<MemberStaffVO> staffList = memberAccountMapper.queryMemberStaffList(params);
    //    for(MemberStaffVO staff : staffList){
    //        // 查询员工邀请的一二级推广人
    //        List<MemberAccountEntity> memberList = memberAccountMapper.queryStaffExtendList(staff.getMemberTree());
    //
    //        // 查询一二级推广人的开户数
    //        Long monthRegCount = 0L;
    //        for(MemberAccountEntity member : memberList){
    //            monthRegCount += memberAccountMapper.statisRegOrderByStaff(member.getId());
    //        }
    //        staff.setMonthRegCount(monthRegCount);
    //
    //        // 查询一二级推广人的分润额
    //        Long monthProfitsCount = 0L;
    //        for(MemberAccountEntity member : memberList){
    //            monthProfitsCount += memberAccountMapper.statisProfitsByStaff(memberId, member.getId());
    //        }
    //        staff.setMonthProfitsCount(monthProfitsCount);
    //    }
    //    return staffList;
    //}

    /**
     * 初始化城市服务商关系树
     */
    public TreeNode initDiamondAllNode(Long memberId, MemberAccountEntity member){
        List<Node> diamondAllNodeList = new ArrayList<Node>();

        // 初始化城市服务商为顶级
        diamondAllNodeList.add(new Node(String.valueOf(member.getId()), member.getMemberAccount(), member.getMemberType(),
                member.getMemberLevel(), null));

        // 查询城市服务商下所有推广的用户（不包括员工）
        List<MemberAccountEntity> diamondNodeList = memberAccountMapper.getAllSubNodes(memberId);
        if(CollectionUtil.isNotEmpty(diamondNodeList)){
            for(MemberAccountEntity memberAccount : diamondNodeList){
                if(Objects.equals(memberAccount.getMemberType(), 1)){
                    diamondAllNodeList.add(new Node(String.valueOf(memberAccount.getId()), memberAccount.getMemberAccount(),memberAccount.getMemberType(),
                            member.getMemberLevel(), memberAccount.getParentMemberId()==null ? null : String.valueOf(memberAccount.getParentMemberId())));
                }
            }
        }
        TreeNode diamondAllNode = new TreeNode();
        for (Node node : diamondAllNodeList) {
            String parentId = node.getParentId();
            diamondAllNode.addNode(node, parentId);
        }
        diamondAllNode.initLevel();
        return diamondAllNode;
    }

    /**
     * 初始化员工关系树
     */
    public TreeNode initEmployeeAllNode(MemberAccountEntity member, List<MemberAccountEntity> employeeList){
        List<Node> employeeAllNodeList = new ArrayList<Node>();

        // 初始化城市服务商为顶级
        employeeAllNodeList.add(new Node(String.valueOf(member.getId()), member.getMemberAccount(), member.getMemberType(),
                member.getMemberLevel(), null));
        if (CollectionUtil.isNotEmpty(employeeList)) {
            for(MemberAccountEntity employee : employeeList){
                // 初始化员工信息
                employeeAllNodeList.add(new Node(String.valueOf(employee.getId()), employee.getMemberAccount(), employee.getMemberType(),
                        member.getMemberLevel(), employee.getParentMemberId()==null ? null : String.valueOf(employee.getParentMemberId())));
            }
        }
        if (CollectionUtil.isNotEmpty(employeeList)) {
            for(MemberAccountEntity employee : employeeList){
                // 查询员工下所有推广的用户
                List<MemberAccountEntity> employeeNodeList = memberAccountMapper.getAllSubNodes(employee.getId());
                if(CollectionUtil.isNotEmpty(employeeNodeList)){
                    for(MemberAccountEntity memberAccount : employeeNodeList){
                        employeeAllNodeList.add(new Node(String.valueOf(memberAccount.getId()), memberAccount.getMemberAccount(), memberAccount.getMemberType(),
                                member.getMemberLevel(), memberAccount.getParentMemberId()==null ? null : String.valueOf(memberAccount.getParentMemberId())));
                    }
                }
            }
        }
        TreeNode employeeAllNode = new TreeNode();
        for (Node node : employeeAllNodeList) {
            String parentId = node.getParentId();
            employeeAllNode.addNode(node, parentId);
        }
        employeeAllNode.initLevel();
        return employeeAllNode;
    }

    @Override
    public EmployeeManageOfTeamVO getEmployeeManageOfTeam(Long memberId, String oemCode) throws BusinessException{
        EmployeeManageOfTeamVO employeeManageOfTeamVO = new EmployeeManageOfTeamVO();

        // 查询会员账号
        MemberAccountEntity member = this.findById(memberId);
        if(null == member){
            throw new BusinessException("未查询到会员账号");
        }
        employeeManageOfTeamVO.setEmployeesLimit(null != member.getEmployeesLimit() ? member.getEmployeesLimit() : 0);

        // -----------------------------------------总员工数-----------------------------------------
        // 查询会员下所有状态为正常的员工列表
        List<MemberAccountEntity> normalEmployeeList = memberAccountMapper.queryMemberEmployeeList(memberId, oemCode, 2, null, null, 1);
        if(CollectionUtil.isNotEmpty(normalEmployeeList)) {
            employeeManageOfTeamVO.setEmployeeCount(Long.valueOf(normalEmployeeList.size()));
        }

        // --------------------------------城市服务商和员工本月分润费--------------------------------------
        String month = DateUtil.format(new Date(),"yyyy-MM");
        // 查询会员下所有员工列表
        List<MemberAccountEntity> employeeList = memberAccountMapper.queryMemberEmployeeList(memberId, oemCode, 2, null, null, null);

        // 获取城市服务商本月的统计
        List<UserOrderStatisticsDayEntity> diamondMonthOrderStatList = userOrderStatisticsDayService.getOrderStatByDate(memberId, null, month, null, null);
        Long diamondMonthProfitsFeeDirect = 0L;
        Long diamondMonthProfitsFeeFission = 0L;
        if(CollectionUtil.isNotEmpty(diamondMonthOrderStatList)) {
            // 累加本月直推分润费
            diamondMonthProfitsFeeDirect = diamondMonthOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum();
            // 累加本月裂变分润费
            diamondMonthProfitsFeeFission = diamondMonthOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum();
        }

        // 循环获取城市服务商下所有员工进行本月的统计
        List<UserOrderStatisticsDayEntity> employeeMonthOrderStatList = new ArrayList<UserOrderStatisticsDayEntity>();
        if (CollectionUtil.isNotEmpty(employeeList)) {
            for(MemberAccountEntity employee : employeeList){
                employeeMonthOrderStatList.addAll(userOrderStatisticsDayService.getOrderStatByDate(employee.getId(), null, month, null, null));
            }
        }
        Long employeeMonthProfitsFeeDirect = 0L;
        Long employeeMonthProfitsFeeFission = 0L;
        if(CollectionUtil.isNotEmpty(employeeMonthOrderStatList)) {
            // 累加本月直推分润费
            employeeMonthProfitsFeeDirect = employeeMonthOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum();
            // 累加本月裂变分润费
            employeeMonthProfitsFeeFission = employeeMonthOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum();
        }

        // 本月分润费(【城市服务商本月直推分润费+城市服务商本月裂变分润费】 + 【员工本月直推分润费+员工本月裂变分润费】)
        employeeManageOfTeamVO.setMonthProfit(diamondMonthProfitsFeeDirect + diamondMonthProfitsFeeFission + employeeMonthProfitsFeeDirect + employeeMonthProfitsFeeFission);


        // --------------------------------城市服务商和员工累计分润--------------------------------------
        // 获取城市服务商所有的统计
        List<UserOrderStatisticsDayEntity> diamondAllOrderStatList = userOrderStatisticsDayService.getOrderStatByDate(memberId, null, null, null, null);
        Long diamondPushCount = 0L;
        Long diamondFissionCount = 0L;
        Long diamondAllProfitsFeeDirect = 0L;
        Long diamondAllProfitsFeeFission = 0L;
        Long diamondAllIndividualDirect = 0L;
        Long diamondAllIndividualFission = 0L;
        if(CollectionUtil.isNotEmpty(diamondAllOrderStatList)) {
            // 累加城市服务商所有直推用户数
            diamondPushCount = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserDirect).sum();
            //累加城市服务商所有裂变用户数
            diamondFissionCount = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserFission).sum();
            // 累加城市服务商所有直推分润费
            diamondAllProfitsFeeDirect = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum();
            // 累加城市服务商所有裂变分润费
            diamondAllProfitsFeeFission = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum();
            // 累加城市服务商所有直推个体数
            diamondAllIndividualDirect = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualDirect).sum();
            // 累加城市服务商所有裂变个体数
            diamondAllIndividualFission = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualFission).sum();
        }

        // 循环获取城市服务商下所有员工进行所有的统计
        List<UserOrderStatisticsDayEntity> employeeAllOrderStatList = new ArrayList<UserOrderStatisticsDayEntity>();
        if (CollectionUtil.isNotEmpty(employeeList)) {
            for(MemberAccountEntity employee : employeeList){
                employeeAllOrderStatList.addAll(userOrderStatisticsDayService.getOrderStatByDate(employee.getId(), null, null, null, null));
            }
        }
        Long employeePushCount = 0L;
        Long employeeFissionCount = 0L;
        Long employeeAllProfitsFeeDirect = 0L;
        Long employeeAllProfitsFeeFission = 0L;
        Long employeeAllIndividualDirect = 0L;
        Long employeeAllIndividualFission = 0L;
        if(CollectionUtil.isNotEmpty(employeeAllOrderStatList)) {
            // 累加员工所有直推用户数
            employeePushCount = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserDirect).sum();
            //累加员工所有裂变用户数
            employeeFissionCount = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserFission).sum();
            // 累加员工所有直推分润费
            employeeAllProfitsFeeDirect = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum();
            // 累加员工所有裂变分润费
            employeeAllProfitsFeeFission = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum();
            // 累加员工所有直推个体数
            employeeAllIndividualDirect = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualDirect).sum();
            // 累加员工所有裂变个体数
            employeeAllIndividualFission = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualFission).sum();
        }

        // 总直推用户数(城市服务商直推用户数+员工直推用户数)
        employeeManageOfTeamVO.setTotalPushMember(diamondPushCount + employeePushCount);

        // 总裂变用户数(城市服务商用户裂变数+员工用户裂变数)
        employeeManageOfTeamVO.setTotalFissionMember(diamondFissionCount + employeeFissionCount);

        // 累计分润额(【城市服务商所有直推分润费+城市服务商所有裂变分润费】 + 【员工所有直推分润费+员工所有裂变分润费】)
        employeeManageOfTeamVO.setTotalProfit(diamondAllProfitsFeeDirect + diamondAllProfitsFeeFission + employeeAllProfitsFeeDirect + employeeAllProfitsFeeFission);


        // --------------------------------城市服务商和员工总个体数----------------------------------------
        // 累计分润额(【城市服务商所有直推个体数+城市服务商所有裂变个体数】 + 【员工所有直推个体数+员工所有裂变个体数】)
        employeeManageOfTeamVO.setTotalPersonality(diamondAllIndividualDirect + diamondAllIndividualFission + employeeAllIndividualDirect + employeeAllIndividualFission);

        return employeeManageOfTeamVO;
    }

    @Override
    public EmployeeManageOfSurveyVO getEmployeeManageOfSurvey(EmployeeManageOfSurveyDTO entity) throws BusinessException {
        EmployeeManageOfSurveyVO empSurvey = new EmployeeManageOfSurveyVO();

        // 查询会员账号
        MemberAccountEntity member = this.findById(entity.getMemberId());
        if(null == member){
            throw new BusinessException("未查询到会员账号");
        }

        // 查询会员下所有员工列表
        List<MemberAccountEntity> employeeList = memberAccountMapper.queryMemberEmployeeList(entity.getMemberId(), entity.getOemCode(), 2, null, null, null);


        // --------------------------------------------处理查询条件------------------------------------------------------
        String day = "";
        String month = "";
        String startDate = "";
        String endDate = "";
        if(StringUtils.isNotBlank(entity.getStartDate()) && StringUtils.isNotBlank(entity.getEndDate())){ // 按开始和结束时间查询
            startDate = entity.getStartDate()+ " 00:00:00";
            endDate = entity.getEndDate()+ " 23:59:59";
        }else if(StringUtils.isNotBlank(entity.getMonth())){ // 按月查询
            month = entity.getMonth();
        }else if(StringUtils.isNotBlank(entity.getStartDate())){// 开始时间
            day = entity.getStartDate();
        }else if(StringUtils.isNotBlank(entity.getEndDate())){// 结束时间
            day = entity.getEndDate();
        }else{
            throw new BusinessException("请选择查询时间");
        }


        // ----------------------------------------根据时间查询------------------------------------------------------
        // 获取城市服务商所有的统计
        List<UserOrderStatisticsDayEntity> diamondAllOrderStatList = userOrderStatisticsDayService.getOrderStatByDate(entity.getMemberId(), day, month, startDate, endDate);

        Long diamondAllPromoteUserDirect = 0L;
        Long diamondAllIndividualDirect = 0L;
        Long diamondAllProfitsFeeDirect = 0L;
        Long diamondAllPromoteUserFission = 0L;
        Long diamondAllIndividualFission = 0L;
        Long diamondAllProfitsFeeFission = 0L;
        if(CollectionUtil.isNotEmpty(diamondAllOrderStatList)){
            // 累加城市服务商所有直推用户数
            diamondAllPromoteUserDirect = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserDirect).sum();
            // 累加城市服务商所有直推个体数
            diamondAllIndividualDirect = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualDirect).sum();
            // 累加城市服务商所有直推分润费
            diamondAllProfitsFeeDirect = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum();
            // 累加城市服务商所有裂变用户数
            diamondAllPromoteUserFission = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserFission).sum();
            // 累加城市服务商所有裂变个体数
            diamondAllIndividualFission = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualFission).sum();
            // 累加城市服务商所有裂变分润费
            diamondAllProfitsFeeFission = diamondAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum();

        }

        // 循环获取城市服务商下所有员工进行所有的统计
        List<UserOrderStatisticsDayEntity> employeeAllOrderStatList = new ArrayList<UserOrderStatisticsDayEntity>();
        if (CollectionUtil.isNotEmpty(employeeList)) {
            for(MemberAccountEntity employee : employeeList){
                employeeAllOrderStatList.addAll(userOrderStatisticsDayService.getOrderStatByDate(employee.getId(), day, month, startDate, endDate));
            }
        }

        Long employeeAllPromoteUserDirect = 0L;
        Long employeeAllIndividualDirect = 0L;
        Long employeeAllProfitsFeeDirect = 0L;
        Long employeeAllPromoteUserFission = 0L;
        Long employeeAllIndividualFission = 0L;
        Long employeeAllProfitsFeeFission = 0L;
        if(CollectionUtil.isNotEmpty(employeeAllOrderStatList)){
            // 累加员工所有直推用户数
            employeeAllPromoteUserDirect = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserDirect).sum();
            // 累加员工所有直推个体数
            employeeAllIndividualDirect = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualDirect).sum();
            // 累加员工所有直推分润费
            employeeAllProfitsFeeDirect = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum();
            // 累加员工所有裂变用户数
            employeeAllPromoteUserFission = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserFission).sum();
            // 累加员工所有裂变个体数
            employeeAllIndividualFission = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualFission).sum();
            // 累加员工所有裂变分润费
            employeeAllProfitsFeeFission = employeeAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum();
        }

        // 累计(城市服务商+员工)
        empSurvey.setPushMemberCount(diamondAllPromoteUserDirect + employeeAllPromoteUserDirect);
        empSurvey.setPushPersonalityCount(diamondAllIndividualDirect + employeeAllIndividualDirect);
        empSurvey.setPushProfit(diamondAllProfitsFeeDirect + employeeAllProfitsFeeDirect);
        empSurvey.setFissionMemberCount(diamondAllPromoteUserFission + employeeAllPromoteUserFission);
        empSurvey.setFissionPersonalityCount(diamondAllIndividualFission + employeeAllIndividualFission);
        empSurvey.setFissProfit(diamondAllProfitsFeeFission + employeeAllProfitsFeeFission);
        return empSurvey;
    }

    @Override
    public List<EmployeeListVO> getEmployeeManageOfList(EmployeeManageOfListQuery query) {
        List<EmployeeListVO> empList = new ArrayList<EmployeeListVO>();
        EmployeeListVO employeeVO = new EmployeeListVO();

        // 查询会员账号
        MemberAccountEntity member = memberAccountMapper.queryMemberByNameOrPhone(query.getMemberId(), query.getOemCode(), query.getNameOrPhone());
        if(null != member){
            // 第一个设置为城市服务商
            employeeVO.setIsSelf(0);
            employeeVO.setId(member.getId());
            employeeVO.setMemberName(StringUtils.isNotBlank(member.getRealName()) ? member.getRealName() : member.getMemberName());
            employeeVO.setStatus(member.getStatus());
            employeeVO.setMemberPhone(member.getMemberPhone());
            employeeVO.setAddTime(member.getAddTime());
            List<UserOrderStatisticsDayEntity> diamondOrderStatList = userOrderStatisticsDayService.getOrderStatByDate(member.getId(), null, null, null, null);
            if(CollectionUtil.isNotEmpty(diamondOrderStatList)){
                employeeVO.setPushMemberCount(diamondOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserDirect).sum());
                employeeVO.setFissionMemberCount(diamondOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserFission).sum());
                employeeVO.setPersonalityCount(diamondOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualDirect).sum() + diamondOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualFission).sum());
                employeeVO.setTotalProfit(diamondOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum() + diamondOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum());
            }
            empList.add(employeeVO);
        }


        // 查询会员下所有员工列表
        List<MemberAccountEntity> employeeList = memberAccountMapper.queryMemberEmployeeList(query.getMemberId(), query.getOemCode(), 2, null, query.getNameOrPhone(), null);

        // 循环查询员工统计数据
        if (CollectionUtil.isNotEmpty(employeeList)) {
            //循环员工列表
            for(MemberAccountEntity employee : employeeList){
                employeeVO = new EmployeeListVO();
                employeeVO.setIsSelf(1);
                employeeVO.setId(employee.getId());
                employeeVO.setMemberName(StringUtils.isNotBlank(employee.getRealName()) ? employee.getRealName() : employee.getMemberName());
                employeeVO.setStatus(employee.getStatus());
                employeeVO.setMemberPhone(employee.getMemberPhone());
                employeeVO.setAddTime(employee.getAddTime());
                List<UserOrderStatisticsDayEntity> employeeOrderStatList = userOrderStatisticsDayService.getOrderStatByDate(employee.getId(), null, null, null, null);
                if(CollectionUtil.isNotEmpty(employeeOrderStatList)){
                    employeeVO.setPushMemberCount(employeeOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserDirect).sum());
                    employeeVO.setFissionMemberCount(employeeOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserFission).sum());
                    employeeVO.setPersonalityCount(employeeOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualDirect).sum() + employeeOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualFission).sum());
                    employeeVO.setTotalProfit(employeeOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum() + employeeOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum());
                }
                empList.add(employeeVO);
            }
        }
        return empList;
    }

    @Override
    public EmployeeManageOfListDetailVO getEmployeeManageOfListDetail(EmployeeManageOfListDetailDTO entity) throws BusinessException {
        EmployeeManageOfListDetailVO empListDetail = new EmployeeManageOfListDetailVO();

        // 查询会员账号
        MemberAccountEntity member = this.findById(entity.getEmpId());
        if(null == member){
            throw new BusinessException("未查询到会员账号");
        }

        // --------------------------------------------处理查询条件------------------------------------------------------
        String day = "";
        String month = "";
        String startDate = "";
        String endDate = "";
        if(StringUtils.isNotBlank(entity.getStartDate()) && StringUtils.isNotBlank(entity.getEndDate())){ // 按开始和结束时间查询
            startDate = entity.getStartDate()+ " 00:00:00";
            endDate = entity.getEndDate()+ " 23:59:59";
        }else if(StringUtils.isNotBlank(entity.getMonth())){ // 按月查询
            month = entity.getMonth();
        }else if(StringUtils.isNotBlank(entity.getStartDate())){// 开始时间
            day = entity.getStartDate();
        }else if(StringUtils.isNotBlank(entity.getEndDate())){// 结束时间
            day = entity.getEndDate();
        }else{
            throw new BusinessException("请选择查询时间");
        }


        // ----------------------------------------根据时间查询------------------------------------------------------
        // 获取会员所有的统计
        List<UserOrderStatisticsDayEntity> memberAllOrderStatList = userOrderStatisticsDayService.getOrderStatByDate(entity.getEmpId(), day, month, startDate, endDate);
        if(CollectionUtil.isNotEmpty(memberAllOrderStatList)){
            // 累加会员所有直推用户数
            empListDetail.setPushMemberCount(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserDirect).sum());
            // 累加会员所有直推个体数
            empListDetail.setPushPersonalityCount(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualDirect).sum());
            // 累加会员所有直推托管费
            empListDetail.setPushComRegFeeDirect(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getCompanyRegistFeeDirect).sum());
            // 累加会员所有直推开票服务费
            empListDetail.setPushInvoiceFeeDirect(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getInvoiceFeeDirect).sum());
            // 累加会员所有直推注销服务费
            empListDetail.setPushComCancelFeeDirect(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getCompanyCancelFeeDirect).sum());
            // 累加会员所有直推会员升级费
            empListDetail.setPushMemberUpgradeFeeDirect(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getMemberUpgradeFeeDirect).sum());
            // 累加会员所有直推托管费续费
            empListDetail.setPushContRegister(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getCustodyFeeRenewalDirect).sum());

            // 累加会员所有裂变用户数
            empListDetail.setFissionMemberCount(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getPromoteUserFission).sum());
            // 累加会员所有裂变个体数
            empListDetail.setFissionPersonalityCount(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualFission).sum());
            // 裂变托管费
            empListDetail.setFissionComRegFeeFission(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getCompanyRegistFeeFission).sum());
            // 裂变开票服务费
            empListDetail.setFissionInvoiceFeeFission(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getInvoiceFeeFission).sum());
            // 裂变注销服务费
            empListDetail.setFissionComCancelFeeFission(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getCompanyCancelFeeFission).sum());
            // 裂变托管费续费
            empListDetail.setFissionContRegister(memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getCustodyFeeRenewalFission).sum());

            // 累计会员所有分润费（直推分润费+裂变分润费）
            Long profitsFeeDirect = memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum();
            Long profitsFeeFission = memberAllOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum();
            empListDetail.setTotalProfit(profitsFeeDirect + profitsFeeFission);
        }
        return empListDetail;
    }

    @Override
    public SelectEmpPushListVO selectEmpPushList(Long memberId, Long empId,String keyword) throws BusinessException {
        log.info("处理直推用户查询请求：{},{},{}",memberId,empId,keyword);

        SelectEmpPushListVO empPush = new SelectEmpPushListVO();
        List<MemberAccountEntity> pushList = new ArrayList<MemberAccountEntity>();

        if(null == empId){
            // 查询会员账号
            MemberAccountEntity member = this.findById(memberId);
            if(null == member){
                throw new BusinessException("未查询到会员账号");
            }
            empPush.setId(member.getId());
            empPush.setMemberName(StringUtils.isNotBlank(member.getRealName()) ? member.getRealName() : member.getMemberName());
            empPush.setMemberPhone(member.getMemberPhone());

            // 查询会员下所有直推用户列表
            pushList = memberAccountMapper.queryMemberEmployeeList(member.getId(), member.getOemCode(), 1, null, null, null);

            // 查询会员下所有员工列表
            List<MemberAccountEntity> employeeList = memberAccountMapper.queryMemberEmployeeList(member.getId(), member.getOemCode(), 2, null, null, null);
            if (CollectionUtil.isNotEmpty(employeeList)) {
                //循环员工列表
                for (MemberAccountEntity employee : employeeList) {
                    // 查询员工下所有直推用户列表
                    pushList.addAll(memberAccountMapper.queryMemberEmployeeList(employee.getId(), employee.getOemCode(), 1, null, null, null));
                }
            }
        }else{
            // 查询员工账号
            MemberAccountEntity emp = this.findById(empId);
            if(null == emp){
                throw new BusinessException("未查询到会员账号");
            }
            empPush.setId(empId);
            empPush.setMemberName(StringUtils.isNotBlank(emp.getRealName()) ? emp.getRealName() : emp.getMemberName());
            empPush.setMemberPhone(emp.getMemberPhone());

            // 查询员工下所有直推用户列表
            pushList = memberAccountMapper.queryMemberEmployeeList(emp.getId(), emp.getOemCode(), 1, null, null, null);
        }

        //获取员工所有直推用户
        List<EmployeePushListVO> lists = new ArrayList<EmployeePushListVO>();
        if(CollectionUtil.isNotEmpty(pushList)) {
            empPush.setPushMemberCount(pushList.stream().count());
            for(MemberAccountEntity entity : pushList){
                EmployeePushListVO push = new EmployeePushListVO();
                push.setId(entity.getId());
                push.setMemberName(StringUtils.isNotBlank(entity.getRealName()) ? entity.getRealName() : entity.getMemberName());
                push.setMemberPhone(entity.getMemberPhone());
                // 根据关键字过滤
                if(StringUtil.isBlank(keyword)){
                    lists.add(push);
                }else if (push.getMemberPhone().contains(keyword) || push.getMemberName().contains(keyword)){
                    lists.add(push);
                }
            }
        }
        empPush.setPushList(lists);
        return empPush;
    }

    @Override
    public List<EmpManageOfPushListVO> getEmployeeManageOfPushList(EmployeeManageOfPushListQuery query) throws BusinessException {
        List<EmpManageOfPushListVO> empManageOfPushList = new ArrayList<EmpManageOfPushListVO>();

        List<MemberAccountEntity> pushList = new ArrayList<MemberAccountEntity>();
        // 查询会员账号
        MemberAccountEntity member = this.findById(query.getMemberId());

        // 查询会员等级
        MemberLevelEntity level = new MemberLevelEntity();
        level.setId(member.getMemberLevel());
        level.setOemCode(member.getOemCode());
        level = memberLevelService.selectOne(level);

        // 如果是税务顾问/会员是城市服务商就添加自己
        if (level.getLevelNo() == 3 || level.getLevelNo() == 5) {
            if (null == query.getEmpId() && null == query.getSelectUserId()) {
                if (null != member) {
                    // 第一个设置为城市服务商
                    pushList.add(member);
                }
            } else if (Objects.equals(query.getMemberId(), query.getSelectUserId())) {
                if (null != member) {
                    // 第一个设置为城市服务商
                    pushList.add(member);
                }
            } else if (null == query.getSelectUserId() && Objects.equals(query.getMemberId(), query.getEmpId())) {
                if (null != member) {
                    // 第一个设置为城市服务商
                    pushList.add(member);
                }
            }
        }

        // 如果员工id不为空，则走员工直推用户列表，为空则走推广中心直推用户列表
        if (null == query.getEmpId()) {
            if (null == query.getSelectUserId()) {
                // 查询会员下所有直推用户列表
                pushList.addAll(memberAccountMapper.queryMemberEmployeeList(query.getMemberId(), query.getOemCode(), 1, query.getSelectUserId(), null, null));

                // 查询会员下所有员工列表
                List<MemberAccountEntity> employeeList = memberAccountMapper.queryMemberEmployeeList(query.getMemberId(), query.getOemCode(), 2, query.getSelectUserId(), null, null);
                if (CollectionUtil.isNotEmpty(employeeList)) {
                    //循环员工列表
                    for (MemberAccountEntity employee : employeeList) {
                        // 查询员工下所有直推用户列表
                        pushList.addAll(memberAccountMapper.queryMemberEmployeeList(employee.getId(), employee.getOemCode(), 1, null, null, null));
                    }
                }
            } else {
                // 查询下拉选择用户ID的所属员工是否为空
                MemberAccountEntity selectMember = this.findById(query.getSelectUserId());
                if (null == selectMember) {
                    throw new BusinessException("未查询到会员账号");
                }
                if (null != selectMember.getAttributionEmployeesId()) {
                    if (CollectionUtil.isNotEmpty(pushList)) {
                        if (!Objects.equals(pushList.get(0).getId(), query.getSelectUserId())) {
                            // 查询员工下所有直推用户列表
                            pushList.addAll(memberAccountMapper.queryMemberEmployeeList(selectMember.getAttributionEmployeesId(), query.getOemCode(), 1, query.getSelectUserId(), null, null));
                        }
                    } else {
                        // 查询员工下所有直推用户列表
                        pushList.addAll(memberAccountMapper.queryMemberEmployeeList(selectMember.getAttributionEmployeesId(), query.getOemCode(), 1, query.getSelectUserId(), null, null));
                    }
                } else {
                    // 查询会员下所有直推用户列表
                    pushList.addAll(memberAccountMapper.queryMemberEmployeeList(query.getMemberId(), query.getOemCode(), 1, query.getSelectUserId(), null, null));

                    // 查询会员下所有员工列表
                    List<MemberAccountEntity> employeeList = memberAccountMapper.queryMemberEmployeeList(query.getMemberId(), query.getOemCode(), 2, query.getSelectUserId(), null, null);
                    if (CollectionUtil.isNotEmpty(employeeList)) {
                        //循环员工列表
                        for (MemberAccountEntity employee : employeeList) {
                            // 查询员工下所有直推用户列表
                            pushList.addAll(memberAccountMapper.queryMemberEmployeeList(employee.getId(), employee.getOemCode(), 1, null, null, null));
                        }
                    }
                }
            }
        }else{
            // 查询员工下所有直推用户列表
            pushList.addAll(memberAccountMapper.queryMemberEmployeeList(query.getEmpId(), query.getOemCode(), 1, query.getSelectUserId(), null, null));
        }

        // 循环list查询数量
        if(CollectionUtil.isNotEmpty(pushList)) {
            for(MemberAccountEntity entity : pushList){
                EmpManageOfPushListVO empPushVO = new EmpManageOfPushListVO();
                empPushVO.setUserId(entity.getId());
                empPushVO.setMemberName(StringUtils.isNotBlank(entity.getRealName()) ? entity.getRealName() : entity.getMemberName());
                empPushVO.setMemberPhone(entity.getMemberPhone());
                empPushVO.setLevelNo(memberLevelService.findById(entity.getMemberLevel()).getLevelNo());

                empPushVO.setExtendType(entity.getExtendType());
                empPushVO.setRemark(entity.getRemark());

                // 查询用户的直推用户数
                List<MemberAccountEntity> selectUserPushList = memberAccountMapper.queryMemberEmployeeList(entity.getId(), entity.getOemCode(), 1, null, null, null);

                // 个体数
                List<MemberCompanyEntity> selectUserCompanyList = memberCompanyService.allMemberCompanyList(entity.getId(), entity.getOemCode());

                // 查询会员下所有员工列表
                List<MemberAccountEntity> employeeList = memberAccountMapper.queryMemberEmployeeList(entity.getId(), entity.getOemCode(), 2, null, null, null);
                if (CollectionUtil.isNotEmpty(employeeList)) {
                    //循环员工列表
                    for (MemberAccountEntity employee : employeeList) {
                        // 查询员工下所有直推用户列表
                        selectUserPushList.addAll(memberAccountMapper.queryMemberEmployeeList(employee.getId(), employee.getOemCode(), 1, null, null, null));
                        // 查询员工下所有直推用户的个体数
                        selectUserCompanyList.addAll(memberCompanyService.allMemberCompanyList(employee.getId(), employee.getOemCode()));
                    }
                }
                if(CollectionUtil.isNotEmpty(selectUserPushList)) {
                    empPushVO.setPushMemberCount(selectUserPushList.stream().count());
                }

                if(CollectionUtil.isNotEmpty(selectUserCompanyList)) {
                    empPushVO.setPushPersonalityCount(selectUserCompanyList.stream().count());
                }

                // 本月开票额
                empPushVO.setMonthInvoiceAmount(mapper.queryInvoiceAmountByDate(entity.getId(), entity.getOemCode(), "month",null));

                // 本年开票额
                empPushVO.setYearInvoiceAmount(mapper.queryInvoiceAmountByDate(entity.getId(), entity.getOemCode(), null, "year"));

                // 累计开票额
                empPushVO.setTotalInvoiceAmount(mapper.queryInvoiceAmountByDate(entity.getId(), entity.getOemCode(),null,null));

                // 查询最近一次
                JSONObject lastJson = mapper.queryLastInvoice(entity.getId(), entity.getOemCode());
                if(lastJson != null){
                    empPushVO.setLastInvoiceTime(lastJson.getDate("add_time"));
                    empPushVO.setLastInvoiceAmount(lastJson.getLongValue("invoice_amount"));
                }
                empManageOfPushList.add(empPushVO);
            }
        }
        return empManageOfPushList;
    }

    @Override
    public PushExtendResultVO queryPushExtendResult(Long userId) throws BusinessException {
        PushExtendResultVO pushExtendResult = new PushExtendResultVO();

        // 查询会员账号
        MemberAccountEntity member = this.findById(userId);
        if(null == member){
            throw new BusinessException("未查询到会员账号");
        }
        pushExtendResult.setUserId(userId);
        pushExtendResult.setMemberName(StringUtils.isNotBlank(member.getRealName()) ? member.getRealName() : member.getMemberName());
        pushExtendResult.setMemberPhone(member.getMemberPhone());

        // 查询会员下所有直推用户列表
        List<MemberAccountEntity> memberPushList = memberAccountMapper.queryMemberEmployeeList(member.getId(), member.getOemCode(), 1, null, null, null);

        // 累计所有
        List<UserOrderStatisticsDayEntity> allOrderStatList = userOrderStatisticsDayService.getOrderStatByDate(userId, null, null, null, null);
        // 按月统计
        List<UserOrderStatisticsDayEntity> monthOrderStatList = userOrderStatisticsDayService.getOrderStatByDate(userId, null, DateUtil.format(new Date(),"yyyy-MM"), null, null);
        // 按月统计
        List<UserOrderStatisticsDayEntity> yearOrderStatList = userOrderStatisticsDayService.getOrderStatByDate(userId, null, null, DateUtil.getStartOrEndDayOfYear(null,true), DateUtil.getStartOrEndDayOfYear(null,false));


        // 查询会员下所有员工列表
        List<MemberAccountEntity> employeeList = memberAccountMapper.queryMemberEmployeeList(member.getId(), member.getOemCode(), 2, null, null, null);
        if (CollectionUtil.isNotEmpty(employeeList)) {
            // 循环获所有员工进行所有的统计
            for (MemberAccountEntity employee : employeeList) {
                // 查询员工下所有直推用户列表
                memberPushList.addAll(memberAccountMapper.queryMemberEmployeeList(employee.getId(), employee.getOemCode(), 1, null, null, null));

                // 查询员工下所有
                allOrderStatList.addAll(userOrderStatisticsDayService.getOrderStatByDate(employee.getId(), null, null, null, null));
                monthOrderStatList.addAll(userOrderStatisticsDayService.getOrderStatByDate(employee.getId(), null, DateUtil.format(new Date(),"yyyy-MM"), null, null));
                yearOrderStatList.addAll(userOrderStatisticsDayService.getOrderStatByDate(employee.getId(), null, null, DateUtil.getStartOrEndDayOfYear(null,true), DateUtil.getStartOrEndDayOfYear(null,false)));
            }
        }

        // 累加直推用户数
        if(CollectionUtil.isNotEmpty(memberPushList)) {
            pushExtendResult.setPushMemberCount(memberPushList.stream().count());
        }

        // 所有统计
        if(CollectionUtil.isNotEmpty(allOrderStatList)) {

            // 累加所有直推个体数
            pushExtendResult.setPushPersonalityCount(allOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getIndividualDirect).sum());

            // 累加所有直推分润费
            Long totalProfitsFeeDirect = allOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum();
            // 累加所有裂变分润费
            Long totalProfitsFeeFission = allOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum();
            pushExtendResult.setTotalProfit(totalProfitsFeeDirect + totalProfitsFeeFission);
        }

        // 按月统计
        if(CollectionUtil.isNotEmpty(monthOrderStatList)) {
            // 累加所有直推分润费
            Long monthProfitsFeeDirect = monthOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum();
            // 累加所有裂变分润费
            Long monthProfitsFeeFission = monthOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum();
            pushExtendResult.setMonthCommission(monthProfitsFeeDirect + monthProfitsFeeFission);
        }

        // 按年统计
        if(CollectionUtil.isNotEmpty(yearOrderStatList)) {
            // 累加所有直推分润费
            Long yearProfitsFeeDirect = yearOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeDirect).sum();
            // 累加所有裂变分润费
            Long yearProfitsFeeFission = yearOrderStatList.stream().mapToLong(UserOrderStatisticsDayEntity::getProfitsFeeFission).sum();
            pushExtendResult.setYearCommission(yearProfitsFeeDirect + yearProfitsFeeFission);
        }
        return pushExtendResult;
    }

    @Override
    public List<PosterBase64VO> getPoster(String oemCode, GenqrcodeDTO entity, String sourceType) {
        List<PosterBase64VO> base64PosterList = new ArrayList<PosterBase64VO>();

        // 获取小程序二维码
        String base64QRCode = weChatService.getQRCode(oemCode, entity.getScene(), entity.getWidth(), entity.getPage(), 2, sourceType);

        // base64 Data URI scheme
        String base64Image = "data:image/png;base64,";

        // 获取海报列表
        List<PromotionPosterEntity> posterList = promotionPosterService.getPromotionPosterList(oemCode);

        // 循环海报列表增加水印
        if (CollectionUtil.isNotEmpty(posterList)) {
            for (PromotionPosterEntity poster : posterList) {
                try {
                    // 给图片增加水印
                    byte[] byteArray = ImageWaterMarkUtil.addImageWatermark(base64QRCode,
                            poster.getPosterAddress(), poster.getQrLeftMargin(), poster.getQrTopMargin(),
                            poster.getQrWidth(), poster.getQrHeight(), sourceType);

                    // 字节数组转为base64
                    String base64String = Base64.encodeBase64String(byteArray);

                    //添加list
                    PosterBase64VO posterBase64VO = new PosterBase64VO();
                    posterBase64VO.setBase64Poster(base64Image + base64String);
                    base64PosterList.add(posterBase64VO);
                } catch (Exception e) {
                    String errorDisplayImage = sysDictionaryService.getByCode("error_display_image").getDictValue();
                    String base64String = ImageUtils.image2Base64(errorDisplayImage);

                    //添加list
                    PosterBase64VO posterBase64VO = new PosterBase64VO();
                    posterBase64VO.setBase64Poster(base64Image + base64String);
                    base64PosterList.add(posterBase64VO);
                }
            }
        }
        return base64PosterList;
    }

    @Override
    public void updateLevel(Long id, Long memberLevel, String levelName, String updateUser, Date updateTime) {
        mapper.updateLevel(id, memberLevel, levelName, updateUser, updateTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberAccountEntity canleMember(Long id,String accountMember ,Integer status, String account) {
        //注销代理商
        //判断会员账号余额
        UserEntity agent=userService.findById(id);
        if(agent == null){
            throw  new BusinessException("用户信息不存在");
        }
        UserCapitalAccountEntity agentUserCapitalAccount=null;
        if(agent.getPlatformType()==4){
            agentUserCapitalAccount=userCapitalAccountService.queryByUserIdAndType(id, UserTypeEnum.PARTENER.getValue(),agent.getOemCode(),1);
        }
        if(agent.getPlatformType()==5){
            agentUserCapitalAccount=userCapitalAccountService.queryByUserIdAndType(id, UserTypeEnum.SERVER.getValue(),agent.getOemCode(),1);
        }
        if(agentUserCapitalAccount!=null && agentUserCapitalAccount.getTotalAmount()>0){
            throw  new BusinessException("代理商资金账号余额不为0，不允许注销。");
        }
        //代理商ID
        userService.cancelAgent(id,2,account);

        MemberAccountEntity entity=new MemberAccountEntity();
        entity.setMemberAccount(accountMember);
        entity.setOemCode(agent.getOemCode());
        entity.setStatus(1);
        MemberAccountEntity memberAccountEntity=mapper.selectOne(entity);
        if(memberAccountEntity == null) {
            return null;
        }
        //判断会员账号余额
        UserCapitalAccountEntity userCapitalAccountEntity=userCapitalAccountService.queryByUserIdAndType(memberAccountEntity.getId(), UserTypeEnum.MEMBER.getValue(),memberAccountEntity.getOemCode(),1);
        if(userCapitalAccountEntity.getTotalAmount()>0){
            throw  new BusinessException("会员资金账号余额不为0，不允许注销。");
        }
        memberAccountEntity.setStatus(status);
        if(status==2){
            memberAccountEntity.setMemberAccount(memberAccountEntity.getMemberAccount()+"_1");
            memberAccountEntity.setMemberPhone(memberAccountEntity.getMemberPhone()+"_1");
            memberAccountEntity.setUpdateTime(new Date());
            memberAccountEntity.setUpdateUser(account);
        }
        mapper.updateByPrimaryKey(memberAccountEntity);
        return memberAccountEntity;
    }

    @Override
    public void userAuth(Long userId, String oemCode, UserAuthDTO authDTO, Integer source) throws BusinessException, IOException {
        log.info("收到用户实名认证请求：{},{},{},{}",userId,oemCode,JSON.toJSONString(authDTO),source);
        MemberAccountEntity member = this.findById(userId);
        if(null == member){
            throw new BusinessException("实名认证失败，" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        // 查询机构配置信息,已接入渠道的机构需校验用户是否已在渠道方实名
        OemConfigEntity configEntity = Optional.ofNullable(oemConfigService.queryOemConfigByCode(oemCode, "is_open_channel")).orElseThrow(() -> new BusinessException("未查询到机构配置信息"));
        if (Objects.equals(configEntity.getParamsValue(), "1")) {
            // 查询国金用户实名信息接口
            Map<String,Object> dataParams = new HashMap<>();
            dataParams.put("userId", Optional.ofNullable(member.getChannelUserId()).orElseThrow(() -> new BusinessException("渠道用户id为空")));
            dataParams.put("productCode", member.getOemCode());
            // 获取秘钥
            OemParamsEntity oemParamsEntity = oemParamsService.getParams(oemCode,OemParamsTypeEnum.GUOJIN_CHANNEL_CONFIG.getValue());
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

        // 非国金用户修改实名时需比较新旧身份证号
        if (Objects.equals(configEntity.getParamsValue(), "0") && MemberAuthStatusEnum.AUTH_SUCCESS.getValue().equals(member.getAuthStatus())
                && StringUtil.isNotBlank(member.getIdCardNo()) && !member.getIdCardNo().equals(authDTO.getIdCardNo())) {
            throw new BusinessException(ErrorCodeEnum.INCONSISTENT_INFORMATION);
        }

        // 判断证件有效期
        if(StringUtils.isBlank(authDTO.getExpireDate())){
            throw new BusinessException("实名认证失败，身份证有效期不能为空");
        }

        // 验证身份证有效期格式
        String regex = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))-((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))";
        if(!(authDTO.getExpireDate().contains("长期") || Pattern.matches(regex,authDTO.getExpireDate()))){
            throw new BusinessException("身份证有效期格式有误，格式要求：yyyy.MM.dd-yyyy.MM.dd");
        }

        // 验证用户身份证有效期是否在有效范围内
        String[] dateArr = authDTO.getExpireDate().split("-");
        if(dateArr.length == 2 && !dateArr[1].contains("长期")){
            String endDate = dateArr[1].replace(".","").replace(".","");
            String nowDate = DateUtil.formatDefaultDate(new Date()).replace("-","").replace("-","");
            if(Long.parseLong(endDate) < Long.parseLong(nowDate)){
                throw new BusinessException("实名认证失败，身份证已过期");
            }
        }

        // 验证身份证证件照地址是否存在（外部调用时，这里不需要校验地址，已在前面的逻辑中校验过）
        if(source.intValue() != 1){
            String bucketName = this.sysDictionaryService.getByCode("oss_privateBucketName").getDictValue();
            boolean front_exists = this.ossService.doesObjectExist(authDTO.getIdCardFront(),bucketName);
            boolean back_exists = this.ossService.doesObjectExist(authDTO.getIdCardBack(),bucketName);
            if(!(front_exists && back_exists)){
                throw new BusinessException("身份证证件照地址不存在，请检查");
            }
        }

        /**
         * 身份证/姓名的二要素验证
         */
        //读取要素认证相关配置 paramsType=5
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 5);
        if (null == paramsEntity) {
            throw new BusinessException("未配置身份二要素相关信息！");
        }
        // agentNo
        String agentNo = paramsEntity.getAccount();
        // signKey
        String signKey = paramsEntity.getSecKey();
        // authUrl
        String authUrl = paramsEntity.getUrl();

        String authResult = AuthKeyUtils.auth2Key(agentNo, signKey, authUrl, authDTO.getUserName(), authDTO.getIdCardNo(),paramsEntity.getParamsValues());

        if (StringUtils.isBlank(authResult)) {
            throw new BusinessException("二要素认证失败");
        }
        JSONObject resultObj = JSONObject.parseObject(authResult);

        log.info("根据实名认证结果，更新用户账户表信c息：{},{},{},{},{}",userId,authDTO.getUserName(),authDTO.getIdCardNo(),authDTO.getExpireDate());

        // 根据认证结果更新用户账户表认证信息
        if (!"00".equals(resultObj.getString("code"))) {
            throw new BusinessException("二要素认证失败：" + resultObj.getString("msg"));
        }

        // 为自己办理,或接入方新增修改实名时，二要素验证通过需要更新会员表信息
        if ((authDTO.getIsOther().intValue() == 0) || null != member.getAccessPartyId()) {
            try {
                MemberAccountEntity t = new MemberAccountEntity();
                t.setId(userId);
                t.setUpdateUser(member.getMemberAccount());
                t.setUpdateTime(new Date());
                t.setAuthStatus(MemberAuthStatusEnum.AUTH_SUCCESS.getValue());// 认证成功
                t.setRealName(authDTO.getUserName());
                t.setIdCardFront(authDTO.getIdCardFront());
                t.setIdCardBack(authDTO.getIdCardBack());
                t.setIdCardNo(authDTO.getIdCardNo());
                t.setExpireDate(authDTO.getExpireDate());
                t.setIdCardAddr(authDTO.getIdCardAddr());
                t.setAuthPushState(4);
                this.editByIdSelective(t);
            } catch (Exception e) {
                log.info("【更新用户实名信息失败】：", e.toString());
                throw new BusinessException("更新用户实名信息失败！");
            }

            // 实名成功后，将实名信息同步至渠道方
            if (Objects.equals(configEntity.getParamsValue(), "1")) {
                try {
                    member = this.findById(userId);
                    CompanyPushVo companyPushVo = new CompanyPushVo();
                    ObjectUtil.copyObject(member, companyPushVo);
                    companyPushVo.setId(member.getId());
                    companyPushVo.setUserId(member.getChannelUserId().intValue());
                    companyPushVo.setProductCode(member.getChannelProductCode());
                    String[] s = authDTO.getExpireDate().split("-");
                    companyPushVo.setBeginDate(s[0].replace(".", "-"));
                    companyPushVo.setEndDate(s[1].replace(".", "-"));
                    // 推送实名信息
                    List<CompanyPushVo> list = Lists.newArrayList();
                    list.add(companyPushVo);
                    rabbitTemplate.convertAndSend("companyAuthPush", list);
                } catch (AmqpException e) {
                    log.info("推送实名信息失败：" + e.toString());
                    // 修改推送状态
                    MemberAccountEntity memberAccountEntity = new MemberAccountEntity();
                    memberAccountEntity.setId(userId);
                    memberAccountEntity.setAuthPushState(3);
                    this.editByIdSelective(memberAccountEntity);
                }
            }
        }

        log.info("用户实名认证请求结束...");
    }

    @Override
    public void updateUserAuth(Long userId, String name, String idCardNo,String idCardFront,String idCardBack, String expireDate, String idCardAddr, Integer authStatus,String remark) throws BusinessException {
        log.info("更新用户实名信息接收参数：{}，{}，{}，{}，{}，{}，{}，{}",userId,name,idCardNo,idCardFront,idCardBack,expireDate,idCardAddr,authStatus);

        MemberAccountEntity member = this.findById(userId);
        if(null == member){
            throw new BusinessException("操作失败，" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        MemberAccountEntity updateUser = new MemberAccountEntity();
        updateUser.setId(userId);
        updateUser.setRealName(name);
        updateUser.setUpdateUser(member.getMemberAccount());
        updateUser.setAuthStatus(authStatus);
        updateUser.setIdCardNo(idCardNo);
        updateUser.setIdCardFront(idCardFront);
        updateUser.setIdCardBack(idCardBack);
        updateUser.setExpireDate(expireDate);
        updateUser.setIdCardAddr(idCardAddr);
        updateUser.setUpdateTime(new Date());
        updateUser.setRemark(remark);
        this.editByIdSelective(updateUser);

        member = this.findById(userId);
        //添加会员变动表
        MemberAccountChangeEntity memberAccountChangeEntity=new MemberAccountChangeEntity();
        BeanUtils.copyProperties(member,memberAccountChangeEntity);
        memberAccountChangeEntity.setAccountId(member.getId());
        memberAccountChangeEntity.setId(null);
        memberAccountChangeEntity.setAddTime(new Date());
        memberAccountChangeEntity.setAddUser(member.getMemberAccount());
        memberAccountChangeService.insertSelective(memberAccountChangeEntity);

        log.info("更新用户实名信息成功");
    }

    @Override
    public List<MemberAccountEntity> queryMemberByStatus( String oemCode) {
        return mapper.queryMemberByStatus(oemCode);
    }

    @Override
    public PageResultVo<MemberRegisterVO> queryRegisterData(MemberRegisterQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        List<MemberRegisterVO> list = this.memberAccountMapper.queryRegistMemberList(query);

        // 遍历list，处理身份证号等敏感信息
        list.stream().forEach(member ->{
            member.setIdCardNo(StringHandleUtil.desensitizeIdCardNo(member.getIdCardNo()));
        });

        PageInfo<MemberRegisterVO> pageInfo = new PageInfo<MemberRegisterVO>(list);

        PageResultVo<MemberRegisterVO> result = new PageResultVo<MemberRegisterVO>();
        result.setList(pageInfo.getList());
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setPageSize(query.getPageSize());
        result.setPageNum(query.getPageNumber());
        result.setOrderBy("regsiterTime DESC");
        return result;
    }

    @Override
    public String generalizedQrCode(GenExtQrcodeDTO dto) throws BusinessException{
        log.info("开始生成推广二维码：{}",JSON.toJSONString(dto));

        GenqrcodeDTO params = new GenqrcodeDTO();

        Long defaultWidth = 460L;// 默认图片大小
        // 获取图片大小
        DictionaryEntity dictWidth = this.sysDictionaryService.getByCode("qr_code_width");
        if(null != dictWidth){
            defaultWidth = Long.parseLong(dictWidth.getDictValue());
        }

        String defaultPage = "pages/login/index";// 默认二维码跳转地址
//        if(StringUtils.isNotBlank(dto.getChannelCode())) {
//            defaultPage = defaultPage + "?channelCode=" + dto.getChannelCode();
//        }
        // 获取二维码跳转地址
        DictionaryEntity dictPage = this.sysDictionaryService.getByCode("qr_code_page");
        if(null != dictPage){
            defaultPage = dictPage.getDictValue();
        }
        params.setPage(defaultPage);
        params.setWidth(defaultWidth);
        StringBuilder sb = new StringBuilder();
        sb.append(dto.getInviteCode());// 邀请码，即推荐人账号
        params.setScene(sb.toString());


        log.info("请求参数：{}", JSON.toJSONString(params));

        String qrCode = weChatService.getQRCode(dto.getOemCode(), params.getScene(), params.getWidth(), params.getPage(), 1, dto.getSourceType());

        log.info("推广二维码生成结果：{}", qrCode);

        return qrCode;
    }

    @Override
    public InvitedRegUserVO queryInvitedRegUser(MemberExtendQuery query) throws BusinessException {
        if(null == query.getGradeNo()){
            query.setGradeNo(1);// 默认查询一级推广记录下的“审核中”状态的开户列表
        }
        if(null == query.getOrderStatus()){
            query.setOrderStatus(2);// 默认查“审核中”状态
        }
        // 查询当前会员等级
        MemberAccountEntity member = this.memberAccountMapper.selectByPrimaryKey(query.getUserId());
        if(null == member){
            throw new BusinessException("用户信息不存在");
        }
        MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());
        query.setMemberType(level.getLevelNo());

        // 返回所属用户列表的分页信息
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<MemberVO> memberList = this.memberAccountMapper.getInvitedRegUserList(query);

        InvitedRegUserVO regUserInfo = new InvitedRegUserVO();
        PageInfo pageInfo = new PageInfo<MemberVO>(memberList);
        regUserInfo.setMemberPageList(pageInfo);
        regUserInfo.setTotalCount(Integer.valueOf(String.valueOf(pageInfo.getTotal())));// 总数
        return regUserInfo;
    }

    @Override
    public MemberCountVO getStaffCount(Long memberId, String oemCode) throws BusinessException {
        MemberAccountEntity member = this.findById(memberId);
        if(null == member){
            throw new BusinessException("查询失败，会员信息不存在");
        }

        // 查询邀请员工上限
        Integer limit = member.getEmployeesLimit();
        if(null == limit){
            OemEntity oem = this.oemService.getOem(member.getOemCode());
            if(null == oem.getEmployeesLimit()){
                throw new BusinessException("未配置默认邀请员工数量上限，请联系管理员");
            }
            limit = oem.getEmployeesLimit();
        }

        MemberCountVO countVO = new MemberCountVO();
        Example example = new Example(MemberAccountEntity.class);
        // 查询条件
        example.createCriteria().andEqualTo("oemCode",oemCode)
                .andEqualTo("memberType",2) // 员工
                .andEqualTo("parentMemberId",memberId)
                .andNotEqualTo("status",2);// 非注销状态
        List<MemberAccountEntity> list = this.selectByExample(example);

        log.info("邀请人：{}，当前邀请的员工账号数量：{}",member.getMemberAccount(),list.size());
        countVO.setLimitCount(limit);
        countVO.setTotalCount(list.size());
        return countVO;
    }

    @Override
    public void cancelStaff(Long userId,Long staffId, String oemCode) throws BusinessException {
        log.info("员工注销请求开始：{}，{}",staffId,oemCode);

        MemberAccountEntity member = this.findById(userId);
        if(null == member){
            throw new BusinessException("注销失败，登录" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        MemberAccountEntity staff = this.findById(staffId);
        if(null == staff){
            throw new BusinessException("注销失败，待注销员工信息不存在");
        }

        if(MemberStateEnum.STATE_OFF.getValue().equals(staff.getStatus())){
            throw new BusinessException("当前员工状态为【已注销】，不允许重复操作");
        }

        MemberAccountEntity t = new MemberAccountEntity();
        t.setStatus(MemberStateEnum.STATE_OFF.getValue());
        t.setId(staffId);
        t.setUpdateTime(new Date());
        t.setUpdateUser(member.getMemberAccount());
        this.editByIdSelective(t);

        //修改注销会员的全部下级信息
        staff.setStatus(MemberStateEnum.STATE_OFF.getValue());
        staff.setMemberAccount(staff.getMemberAccount() + "_1");
        updateMemberAccount(staff,member.getMemberAccount());
        log.info("员工注销成功");
    }

    /**
     * 修改会员的全部下级信息
     * @param memberAccountEntity
     */
    @Override
    public void updateMemberAccount(MemberAccountEntity memberAccountEntity, String updateUser){
        //修改会员账号
        memberAccountEntity.setUpdateTime(new Date());
        memberAccountEntity.setUpdateUser(updateUser);
        this.editByIdSelective(memberAccountEntity);
        //修改上级会员账号
        this.mapper.updateParentMemberAccountByMemberId(memberAccountEntity.getId());
        //所属员工账号
        this.mapper.updateAttributionEmployeesAccountByMemberId(memberAccountEntity.getId());
        //上级钻石会员账号
        this.mapper.updateUpDiamondAccountByMemberId(memberAccountEntity.getId());
        //上上级钻石会员账号
        this.mapper.updateSuperDiamondAccountByMemberId(memberAccountEntity.getId());
        //上上级员工账号
        this.mapper.updateSuperEmployeesAccountByMemberId(memberAccountEntity.getId());
    }

    @Override
    public MemberBaseInfoApiVO getMemberBaseInfoApi(MemberBaseInfoApiDTO entity) throws BusinessException {
        log.info("获取会员个人基本信息开始：{}", JSONObject.toJSONString(entity));
        MemberBaseInfoApiVO baseInfo = new MemberBaseInfoApiVO();

        // 是否需要token 1-不需要 2-需要（不需要token则获取个人基本信息，需要则获取token【税务顾问和城市服务商才需要获取token】）
        if (Objects.equals(entity.getIsNeedToken(), 1)) {
            // 会员等级 0-税务顾问 1-城市服务商
            if (Objects.equals(entity.getLevelNo(), 0) || Objects.equals(entity.getLevelNo(), 1)) {
                MemberAccountEntity memberAccountEntity = memberAccountMapper.selectByPrimaryKey(entity.getUserId());
                if (null == memberAccountEntity) {
                    throw new BusinessException("用户信息不存在");
                }
                baseInfo.setUserId(memberAccountEntity.getId());
                baseInfo.setMemberAccount(memberAccountEntity.getMemberAccount());
                baseInfo.setMemberName(StringUtils.isNotBlank(memberAccountEntity.getRealName()) ? memberAccountEntity.getRealName() : memberAccountEntity.getMemberName());
                baseInfo.setHeadImg(memberAccountEntity.getHeadImg());
                baseInfo.setStatus(memberAccountEntity.getStatus());

                // 查询会员等级名称
                MemberLevelEntity level = memberLevelService.findById(memberAccountEntity.getMemberLevel());
                baseInfo.setLevelNo(level.getLevelNo());
                baseInfo.setLevelName(level.getLevelName());
                baseInfo.setUserType(1); //用户类型 1-会员 2-系统用户
                // 会员等级 2-城市合伙人 3-高级城市合伙人
            } else if (Objects.equals(entity.getLevelNo(), 2) || Objects.equals(entity.getLevelNo(), 3)) {
                UserEntity userEntity = userService.findById(entity.getUserId());
                if (userEntity == null) {
                    throw new BusinessException("用户信息不存在");
                }
                baseInfo.setUserId(userEntity.getId());
                baseInfo.setMemberAccount(userEntity.getUsername());
                baseInfo.setMemberName(userEntity.getNickname());
                baseInfo.setStatus(userEntity.getStatus());
                baseInfo.setLevelNo(userEntity.getPlatformType());
                if (Objects.equals(userEntity.getPlatformType(), 4)) {
                    baseInfo.setLevelName("高级城市合伙人");
                } else if (Objects.equals(userEntity.getPlatformType(), 5)) {
                    baseInfo.setLevelName("城市合伙人");
                }
                baseInfo.setUserType(2); //用户类型 1-会员 2-系统用户
            }
        } else if (Objects.equals(entity.getIsNeedToken(), 2)) {
            // 会员等级 0-税务顾问 1-城市服务商
            if (Objects.equals(entity.getLevelNo(), 0) || Objects.equals(entity.getLevelNo(), 1)) {
                MemberAccountEntity memberAccountEntity = new MemberAccountEntity();
                memberAccountEntity.setOemCode(entity.getOemCode());
                memberAccountEntity.setId(entity.getUserId());
                memberAccountEntity.setStatus(1);
                MemberAccountEntity member = memberAccountMapper.selectOne(memberAccountEntity);
                if (member != null) {
                    // 生成新的token放缓存
                    String outTime = sysDictionaryService.getByCode("redis_token_outtime").getDictValue();
                    String token = UUID.randomUUID().toString().replaceAll("-", "");
                    redisService.set(RedisKey.LOGIN_TOKEN_KEY + entity.getOemCode() + "_" + "userId_1_" + member.getId(), token, Integer.parseInt(outTime));
                    redisService.set(RedisKey.LOGIN_TOKEN_KEY + entity.getOemCode() + "_" + token,
                            new CurrUser(member.getId(), member.getMemberPhone(), entity.getOemCode(), null), Integer.parseInt(outTime));
                    baseInfo.setToken(token);
                } else {
                    throw new BusinessException("获取token失败，未找到合适的用户数据！");
                }
            }
        }
        log.info("获取会员个人基本信息结束");
        return baseInfo;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public MemberAccountEntity extUserRegister(String mobile, String oemCode) throws BusinessException {
        log.info("对外用户注册请求处理：{}", mobile);

        // 判断会员是否已注册
        MemberAccountEntity member = this.queryByAccount(mobile, oemCode);
        if (null != member && !MemberStateEnum.STATE_OFF.getValue().equals(member.getStatus())) {
            throw new BusinessException("当前手机号已注册，请更换！");
        }

        //存储会员账号信息
        MemberAccountEntity memberAccountEntity = new MemberAccountEntity();
        memberAccountEntity.setMemberAccount(mobile);
        memberAccountEntity.setMemberPhone(mobile);// 初始值默认与帐号一致
        memberAccountEntity.setAuthStatus(MemberAuthStatusEnum.UN_AUTH.getValue());// 会员实名认证状态，默认为“未认证”
        // 查询会员默认等级 -1-员工 0-普通会员 1-税务顾问 2-城市服务商
        MemberLevelEntity level = this.memberLevelService.queryMemberLevel(oemCode, MemberTypeEnum.MEMBER.getValue());
        if(null == level){
            throw new BusinessException("操作失败，未配置会员等级信息！");
        }
        memberAccountEntity.setMemberLevel(level.getId());
        memberAccountEntity.setMemberName(mobile);// 昵称默认为手机号
        memberAccountEntity.setLevelName(level.getLevelName());

        // 判断OEM机构是否存在
        OemEntity oem = this.oemService.getOem(oemCode);
        if(null == oem){
            throw new BusinessException("操作失败，OEM机构不存在");
        }
        memberAccountEntity.setOemCode(oemCode);

        // 其它字段补充
        memberAccountEntity.setMemberType(MemberTypeEnum.MEMBER.getValue());
        memberAccountEntity.setStatus(MemberStateEnum.STATE_ACTIVE.getValue());
        memberAccountEntity.setAddTime(new Date());
        memberAccountEntity.setAddUser(mobile);
        this.insertSelective(memberAccountEntity);

        // 设置会员层级关系
        MemberAccountEntity entity = new MemberAccountEntity();
        entity.setId(memberAccountEntity.getId());
        entity.setMemberTree(memberAccountEntity.getId() + "");
        entity.setUpdateTime(new Date());
        this.editByIdSelective(entity);

        // 生成会员资金账户信息
        UserCapitalAccountEntity uca = new UserCapitalAccountEntity();
        uca.setAddUser(memberAccountEntity.getMemberAccount());
        uca.setAddTime(new Date());
        uca.setOemCode(oemCode);
        uca.setUserId(memberAccountEntity.getId());
        uca.setUserType(MemberTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2 -系统用户
        uca.setCapitalAccount(UniqueNumGenerator.generateUniqueNo());
        uca.setTotalAmount(0L);// 初始账户金额位0
        uca.setAvailableAmount(0L);
        uca.setBlockAmount(0L);
        uca.setStatus(MemberStateEnum.STATE_ACTIVE.getValue());// 状态默认为可用
        uca.setWalletType(1); //消费钱包
        this.userCapitalAccountService.insertSelective(uca);

        // 生成会员佣金钱包资金账户
        UserCapitalAccountEntity ucaCommission = new UserCapitalAccountEntity();
        BeanUtils.copyProperties(uca,ucaCommission);
        ucaCommission.setId(null);// 清空ID
        ucaCommission.setWalletType(2);// 佣金钱包
        ucaCommission.setCapitalAccount(UniqueNumGenerator.generateUniqueNo());
        this.userCapitalAccountService.insertSelective(ucaCommission);

        log.info("会员注册成功：{}",JSON.toJSONString(memberAccountEntity));
        return memberAccountEntity;
    }

    @Override
    public void extUserAuth(String oemCode, String mobile, String userName, String idCardNo, String idCardFront, String idCardBack) {
        log.info("收到第三方用户实名认证请求：{},{},{},{}",oemCode,userName,idCardNo,mobile);
        MemberAccountEntity member = this.queryByAccount(mobile,oemCode);
        if(null == member){
            throw new BusinessException("实名认证失败，" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        /*if(StringUtils.isBlank(expireDate)){
            throw new BusinessException("实名认证失败，身份证有效期不能为空");
        }

        // 验证身份证有效期格式
        String regex = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))-((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))";
        if(!(expireDate.contains("长期") || Pattern.matches(regex,expireDate))){
            throw new BusinessException("身份证有效期格式有误，格式要求：yyyy.MM.dd-yyyy.MM.dd");
        }

        // 验证用户身份证有效期是否在有效范围内
        String[] dateArr = expireDate.split("-");
        if(dateArr.length == 2 && !dateArr[1].contains("长期")){
            String endDate = dateArr[1].replace(".","").replace(".","");
            String nowDate = DateUtil.formatDefaultDate(new Date()).replace("-","").replace("-","");
            if(Long.parseLong(endDate) < Long.parseLong(nowDate)){
                throw new BusinessException("实名认证失败，身份证已过期");
            }
        }*/

        /**
         * 身份证/姓名的二要素验证
         */
        //读取要素认证相关配置 paramsType=5
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 5);
        if (null == paramsEntity) {
            throw new BusinessException("未配置身份二要素相关信息！");
        }
        // agentNo
        String agentNo = paramsEntity.getAccount();
        // signKey
        String signKey = paramsEntity.getSecKey();
        // authUrl
        String authUrl = paramsEntity.getUrl();

        String authResult = AuthKeyUtils.auth2Key(agentNo, signKey, authUrl, userName, idCardNo,paramsEntity.getParamsValues());

        if (StringUtils.isBlank(authResult)) {
            throw new BusinessException("二要素认证失败");
        }
        JSONObject resultObj = JSONObject.parseObject(authResult);

        log.info("根据实名认证结果，更新用户账户表信息：{},{},{},{},{}",oemCode,userName,idCardNo,mobile);

        // 更新用户账户表认证信息
        MemberAccountEntity t = new MemberAccountEntity();
        t.setId(member.getId());
        t.setUpdateUser(member.getMemberAccount());
        t.setUpdateTime(new Date());
        t.setIdCardFront(idCardFront);
        t.setIdCardBack(idCardBack);
        t.setIdCardNo(idCardNo);
        t.setExpireDate(null);
        if ("00".equals(resultObj.getString("code"))) {
            t.setAuthStatus(MemberAuthStatusEnum.AUTH_SUCCESS.getValue());// 认证成功
            t.setRealName(userName);
            this.editByIdSelective(t);
        }else{
            t.setAuthStatus(MemberAuthStatusEnum.AUTH_FAIL.getValue());// 认证失败
            this.editByIdSelective(t);
            throw new BusinessException("二要素认证失败：" + resultObj.getString("msg"));
        }

        log.info("第三方用户实名认证请求结束...");
    }

    @Override
    public int updateUserRemark(Long currUserId,Long userId, String remark,String oemCode) throws BusinessException {
        log.info("修改用户备注信息：{}，{}，{}",currUserId,userId,remark);

        MemberAccountEntity entity = this.memberAccountMapper.selectByPrimaryKey(userId);
        if(null == entity){
            throw new BusinessException("操作失败，修改" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        MemberAccountEntity member = this.memberAccountMapper.selectByPrimaryKey(currUserId);
        if(null == member){
            throw new BusinessException("操作失败，登录" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        MemberAccountEntity t = new MemberAccountEntity();
        t.setId(userId);
        t.setRemark(remark);
        t.setUpdateTime(new Date());
        t.setUpdateUser(member.getMemberAccount());
        t.setOemCode(oemCode);
        int i = this.memberAccountMapper.updateRemark(t);
        //添加会员变动表
        MemberAccountChangeEntity memberAccountChangeEntity=new MemberAccountChangeEntity();
        BeanUtils.copyProperties(entity,memberAccountChangeEntity);
        memberAccountChangeEntity.setAccountId(entity.getId());
        memberAccountChangeEntity.setId(null);
        memberAccountChangeEntity.setAddTime(new Date());
        memberAccountChangeEntity.setAddUser(member.getMemberAccount());
        memberAccountChangeService.insertSelective(memberAccountChangeEntity);
        log.info("用户备注修改成功");
        return i;
    }

    @Override
    public MemberCoStatisticVO listMemberCompany(ExtendUserQuery query) throws BusinessException {
        log.info("查看会员个体列表：{}",JSON.toJSONString(query));

        MemberAccountEntity member = this.findById(query.getUserId());
        if(null == member){
            throw new BusinessException("查询失败，" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        MemberCoStatisticVO statData = new MemberCoStatisticVO();// 返回结果

        // 查询统计个数
        MemberCoStatisticVO statisticVO = this.memberAccountMapper.queryMemberCoStatistic(query);
        statData.setTotalCount(statisticVO.getTotalCount());
        statData.setAvailableCount(statisticVO.getAvailableCount());
        statData.setCancelCount(statisticVO.getCancelCount());
        // 用户信息
        statData.setMemberAccount(member.getMemberAccount());
        statData.setMemberName(member.getMemberName());
        statData.setRealName(member.getRealName());

        // 查询企业列表（含开票统计信息，带分页）
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<MemberComVO> list = this.memberCompanyService.queryMemberCompanyByMemberId(query);
        // 遍历集合，查询开票统计信息
        list.stream().forEach(company -> {
            query.setCompanyId(company.getCompanyId());
            MemberComVO companyInvoice = this.memberAccountMapper.queryComInvoiceStatistic(query);
            company.setMonthInvoiceAmount(companyInvoice.getMonthInvoiceAmount());// 本月开票
            company.setTotalInvoiceAmount(companyInvoice.getTotalInvoiceAmount());// 累计开票
            company.setYearInvoiceAmount(companyInvoice.getYearInvoiceAmount());// 本年开票
        });
        PageResultVo<MemberComVO> companyList = PageResultVo.restPage(list);
        statData.setCompanyList(companyList);
        return statData;
    }

    @Override
    public AchievementStatVO queryAchievementStatistic(Long currUserId, String oemCode) throws BusinessException {
        log.info("推广中心-业绩总览查询开始：{}，{}", currUserId, oemCode);

        MemberAccountEntity member = this.findById(currUserId);
        if (null == member) {
            throw new BusinessException("查询失败，当前登录 " + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        Integer extendType = member.getExtendType(); //推广角色：1-散客 2-直客 3-顶级直客
        if (MemberTypeEnum.EMPLOYEE.getValue().equals(member.getMemberType())) {
            extendType = -1; // 标识员工
        }

        // 查询会员等级，获取开票最低金额
        MemberLevelEntity level = memberLevelService.findById(member.getMemberLevel());
        if (null == level) {
            throw new BusinessException("查询失败，未查询到会员等级");
        }

        // 查询业绩总览
        AchievementStatVO statData = this.memberAccountMapper.queryAchievementStatistic(currUserId, oemCode, extendType, null, level.getInvoiceMinMoney());
        statData.setTotalProfitsAmount(statData.getTotalProfitsAmount() + statData.getAddFissionProfitsAmount());// 累计佣金
        statData.setExtendInvoiceAmount(statData.getAddInvoiceAmount());// 累计直推开票

        // -------------------查询直推达标个体数-------------------
        StringBuilder ids = new StringBuilder();
        ids.append(currUserId).append(",");

        // 查询会员下所有直推用户列表
        List<MemberAccountEntity> memberPushList = memberAccountMapper.queryMemberEmployeeList(member.getId(), member.getOemCode(), 1, null, null, null);

        // 查询会员下所有员工列表
        List<MemberAccountEntity> employeeList = memberAccountMapper.queryMemberEmployeeList(member.getId(), member.getOemCode(), 2, null, null, null);
        if (CollectionUtil.isNotEmpty(employeeList)) {
            // 查询员工下所有直推用户列表
            for (MemberAccountEntity employee : employeeList) {
                ids.append(employee.getId()).append(",");
                memberPushList.addAll(memberAccountMapper.queryMemberEmployeeList(employee.getId(), employee.getOemCode(), 1, null, null, null));
            }
        }

        // 循环直推用户列表
        if (CollectionUtil.isNotEmpty(memberPushList)) {
            for (MemberAccountEntity push : memberPushList) {
                ids.append(push.getId()).append(",");
            }
        }
        ids.delete(ids.length() - 1, ids.length());
        statData.setExtendStdComCount(memberAccountMapper.statExtendStdComCount(ids.toString(), oemCode, level.getInvoiceMinMoney()));

        // 查询本月推广业绩
        AchievementStatVO monthStatData = this.memberAccountMapper.queryAchievementStatistic(currUserId, oemCode, extendType, 1, level.getInvoiceMinMoney());
        statData.setAddExtendUserCount(monthStatData.getExtendUserCount());// 本月新增直推用户
        statData.setAddExtendCompanyCount(monthStatData.getExtendCompanyCount());// 本月新增直推个体
        statData.setAddInvoiceAmount(monthStatData.getAddInvoiceAmount());// 本月新增直推开票
        statData.setAddProfitsAmount(monthStatData.getTotalProfitsAmount());// 本月新增直推佣金
        statData.setAddFissionUserCount(monthStatData.getFissionUserCount());// 本月新增裂变用户
        statData.setAddFissionCompanyCount(monthStatData.getFissionCompanyCount());// 本月新增裂变个体
        statData.setAddFissionProfitsAmount(monthStatData.getAddFissionProfitsAmount());// 本月新增裂变佣金
        statData.setAddFissionInvoiceAmount(monthStatData.getAddFissionInvoiceAmount());// 本月新增裂变开票

        // 设置当前登录会员信息
        statData.setUserName(member.getRealName());
        statData.setExtendType(member.getExtendType());
        MemberLevelEntity memberLevel = this.memberLevelService.findById(member.getMemberLevel());
        if(null == memberLevel){
            throw new BusinessException("查询失败，未查到当前会员等级信息");
        }
        statData.setLevelNo(memberLevel.getLevelNo());

        // 拼接升级描述信息
        StringBuilder upgradeDesc = new StringBuilder();

        MemberLevelEntity upLevel = null;
        // 如果当前会员不是城市服务商，查询当前会员等级上一级会员升级信息
        if(Objects.equals(memberLevel.getLevelNo(),0)){
            if (MemberLevelEnum.NORMAL.getValue().equals(memberLevel.getLevelNo())) {
                upLevel = this.memberLevelService.queryMemberLevel(oemCode, memberLevel.getLevelNo() + 1);
            } else {
                upLevel = this.memberLevelService.queryMemberLevel(oemCode, memberLevel.getLevelNo() + 2);
            }
            if (null != upLevel) {
                if (MemberLevelEnum.NORMAL.getValue().equals(memberLevel.getLevelNo())) {
                    upgradeDesc.append("推广").append(upLevel.getRegistCompanyNum()).append("个个体可升级至").append(upLevel.getLevelName());
                } else {
                    // 数值转换，分转为万
                    BigDecimal invoiceMinMoney = new BigDecimal(upLevel.getInvoiceMinMoney()).divide(new BigDecimal(1000000));
                    upgradeDesc.append("推广").append(upLevel.getCompleteInvoiceCompanyNum()).
                            append("个开票达").append(invoiceMinMoney.toPlainString()).append("万个体可升级至").append(upLevel.getLevelName());
                }
            }
        } else {
            upgradeDesc.append("您已达到最高推广等级");
        }
        statData.setUpgradeDesc(upgradeDesc.toString());

        return statData;
    }

    @Override
    public MemberAccountLevelProfitsRuleVO queryMemberAccLevelProfit(Long userId) {
        return memberAccountMapper.queryMemberAccLevelProfit(userId);
    }

    @Override
    public CompanyRegProgressVO queryCompanyRegProgress(MemberExtendQuery query) throws BusinessException {
        log.info("查询企业注册进度请求开始：{}", JSON.toJSONString(query));

        MemberAccountEntity member = this.findById(query.getUserId());
        if (null == member) {
            throw new BusinessException("查询失败，当前登录" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());
        if (null == memberLevel) {
            throw new BusinessException("查询失败，会员等级不存在！");
        }
        query.setLevelNo(memberLevel.getLevelNo());
        query.setExtendType(member.getExtendType()); //推广角色：1-散客 2-直客 3-顶级直客
        if (MemberTypeEnum.EMPLOYEE.getValue().equals(member.getMemberType())) {
            query.setExtendType(-1); // 标识员工
        }
        // 查询未注册企业用户数
        Integer unRegComCount = this.queryUnRegCompanyCount(query);

        // 查询企业注册进度统计信息
        CompanyRegProgressVO crp = this.memberAccountMapper.queryCompanyRegProgress(query);
        crp.setUnRegComCount(unRegComCount);
        return crp;
    }

    @Override
    public CompanyRegProgressVO queryCompanyRegProgressByChannelServiceId(MemberExtendQuery query) throws BusinessException {
        log.info("查询企业注册进度请求开始：{}", JSON.toJSONString(query));

        // 查询未注册企业用户数
        Integer unRegComCount = this.queryUnRegCompanyCountByChannelServiceId(query);

        // 查询企业注册进度统计信息
        CompanyRegProgressVO crp = this.memberAccountMapper.queryCompanyRegProgressByChannelServiceId(query);
        crp.setUnRegComCount(unRegComCount);
        return crp;
    }

    /**
     * @Description 查询未注册企业数
     * @Author  Kaven
     * @Date   2020/6/9 11:19 上午
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception
    */
    private Integer queryUnRegCompanyCount(MemberExtendQuery query) {
        return this.memberAccountMapper.queryUnRegMemberList(query).size();
    }

    /**
     * @Description 查询未注册企业数
     * @Author  HZ
     * @Date   2021/4/30 11:19 上午
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception
     */
    private Integer queryUnRegCompanyCountByChannelServiceId(MemberExtendQuery query) {
        return this.memberAccountMapper.queryUnRegCompanyCountByChannelServiceId(query);
    }

    @Override
    public CompanyInvoiceProgressVO queryCompanyInvoiceProgress(MemberExtendQuery query) throws BusinessException {
        log.info("查询企业开票进度请求开始：{}", JSON.toJSONString(query));
        MemberAccountEntity member = this.findById(query.getUserId());
        if (null == member) {
            throw new BusinessException("查询失败，当前登录 " + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        // 查询会员等级
        MemberLevelEntity memberLevel = memberLevelService.findById(member.getMemberLevel());
        if (null == memberLevel) {
            throw new BusinessException("查询失败，会员等级不存在！");
        }
        query.setLevelNo(memberLevel.getLevelNo());
        query.setExtendType(member.getExtendType()); //推广角色：1-散客 2-直客 3-顶级直客
        if (MemberTypeEnum.EMPLOYEE.getValue().equals(member.getMemberType())) {
            query.setExtendType(-1); // 标识员工
        }
        CompanyInvoiceProgressVO cip = this.memberAccountMapper.queryCompanyInvoiceProgress(query);
        return cip;
    }

    @Override
    public CompanyInvoiceProgressVO queryCompanyInvoiceProgressByChannelServiceId(MemberExtendQuery query) throws BusinessException {
        log.info("查询企业开票进度请求开始：{}", JSON.toJSONString(query));
        CompanyInvoiceProgressVO cip = this.memberAccountMapper.queryCompanyInvoiceProgressByChannelServiceId(query);
        return cip;
    }

    @Override
    public List<Long> queryCompanyInvoiceCount(MemberExtendQuery query) {
        return this.memberAccountMapper.queryCompanyInvoiceCount(query);
    }

    @Override
    @Transactional(rollbackFor={Exception.class})
    public Map<String, Object> userRegister(MemberRegisterDTO registerDto) throws BusinessException, IOException {
        log.info("开始处理云租用户注册请求：{}",JSONObject.toJSONString(registerDto));

        String token = "";// 返回token
        String registRedisTime = (System.currentTimeMillis() + 300000) + "";
        // 加入redis锁机制，防重复提交
        boolean flag = redisService.lock(RedisKey.REGIST_REDIS_KEY + registerDto.getMobile(),registRedisTime,1);
        if(!flag){
            throw new BusinessException("请勿重复提交！");
        }

        // 判断OEM机构是否存在
        OemEntity oem = this.oemService.getOem(registerDto.getOemCode());
        if(null == oem){
            throw new BusinessException("操作失败，OEM机构不存在");
        }

        // 判断用户是否已经注册
        MemberAccountEntity member = this.queryByAccount(registerDto.getMobile(),registerDto.getOemCode());
        if (member != null) {
            // 用户已存在，抛出异常返回对应信息
            throw new BusinessException(ResultConstants.USER_ALREADY_REGIST);
        }

        // 邀请人校验
        MemberAccountEntity invitor  = this.queryByAccount(registerDto.getInviterAccount(),registerDto.getOemCode()); // 邀请人信息
        if(null == invitor){
            throw new BusinessException(ResultConstants.INVITOR_NOT_EXISTS);
        }

        // 查询会员默认等级 -1-员工 0-普通会员
        MemberLevelEntity level = this.memberLevelService.queryMemberLevel(registerDto.getOemCode(), MemberLevelEnum.NORMAL.getValue());
        if(null == level){
            throw new BusinessException("操作失败，未配置会员等级信息！");
        }

        // 验证身份证有效期格式
        String regex = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))-((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))";
        if (!(registerDto.getExpireDate().contains("长期") || Pattern.matches(regex, registerDto.getExpireDate()))) {
            throw new BusinessException("身份证有效期格式有误，格式要求：yyyy.MM.dd-yyyy.MM.dd");
        }

        // 验证用户身份证有效期是否在有效范围内
        String[] dateArr = registerDto.getExpireDate().split("-");
        if (dateArr.length == 2 && !dateArr[1].contains("长期")) {
            String endDate = dateArr[1].replace(".", "").replace(".", "");
            String nowDate = DateUtil.formatDefaultDate(new Date()).replace("-", "").replace("-", "");
            if (Long.parseLong(endDate) < Long.parseLong(nowDate)) {
                throw new BusinessException("身份证已过期");
            }
        }

        // 会员基本信息入库
        MemberAccountEntity memberAccountEntity = new MemberAccountEntity();
        memberAccountEntity.setMemberAccount(registerDto.getMobile());
        memberAccountEntity.setMemberPhone(registerDto.getMobile());// 初始值默认与帐号一致
        memberAccountEntity.setOemCode(registerDto.getOemCode());
        memberAccountEntity.setProvinceCode(registerDto.getProvinceCode());
        memberAccountEntity.setCityCode(registerDto.getCityCode());
        memberAccountEntity.setRealName(registerDto.getUserName());
        memberAccountEntity.setMemberName(registerDto.getUserName());
        memberAccountEntity.setAuthStatus(MemberAuthStatusEnum.AUTH_SUCCESS.getValue());// 会员实名认证状态，认证成功（约定已经完成实名认证）
        // 保存实名认证信息
        //上传图片至oss私域
        byte[] bytes_front = new BASE64Decoder().decodeBuffer(registerDto.getIdCardFront());
        byte[] bytes_reverse = new BASE64Decoder().decodeBuffer(registerDto.getIdCardReverse());
        // oss存储目录
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dir = registerDto.getOemCode() + "/" + sdf.format(new Date());
        String idCardFrontUrl = dir + System.currentTimeMillis() + "_front.jpg";
        String idCardReverseUrl = dir + System.currentTimeMillis() + "_back.jpg";

        log.info("上传身份证正反面照至oss服务器：{},{}",idCardFrontUrl,idCardReverseUrl);

        this.ossService.upload(idCardFrontUrl,bytes_front);
        this.ossService.upload(idCardReverseUrl,bytes_reverse);

        memberAccountEntity.setIdCardFront(idCardFrontUrl);
        memberAccountEntity.setIdCardBack(idCardReverseUrl);
        memberAccountEntity.setIdCardNo(registerDto.getIdCardNumber());
        memberAccountEntity.setExpireDate(registerDto.getExpireDate());
        memberAccountEntity.setIdCardAddr(registerDto.getIdCardAddr());

        memberAccountEntity.setMemberLevel(level.getId());
        memberAccountEntity.setMemberName(registerDto.getMobile());// 昵称默认为手机号
        memberAccountEntity.setLevelName(level.getLevelName());

        // 绑定会员邀请关系
        memberAccountEntity.setParentMemberAccount(registerDto.getInviterAccount());
        memberAccountEntity.setParentMemberId(invitor.getId());

        // 其它字段补充
        memberAccountEntity.setMemberType(MemberTypeEnum.MEMBER.getValue());
        memberAccountEntity.setEmployeesLimit(oemService.getOem(memberAccountEntity.getOemCode()).getEmployeesLimit());
        memberAccountEntity.setStatus(MemberStateEnum.STATE_ACTIVE.getValue());
        memberAccountEntity.setAddTime(new Date());
        memberAccountEntity.setAddUser(registerDto.getMobile());
        // 保存会员信息
        this.insertSelective(memberAccountEntity);

        log.info("云租同步会员信息入库成功：{}",JSON.toJSONString(memberAccountEntity));

        Long userId = memberAccountEntity.getId();// 会员ID

        // 会员信息入库成功后，设置会员层级关系
        MemberAccountEntity entity = new MemberAccountEntity();
        entity.setId(userId);
        String parentMemberTree = "";
        if(invitor != null){
            parentMemberTree = invitor.getMemberTree();
        }

        if(StringUtils.isBlank(parentMemberTree)){
            entity.setMemberTree(memberAccountEntity.getId() + "");
        }else{
            entity.setMemberTree(parentMemberTree + "/" + memberAccountEntity.getId());
        }
        entity.setUpdateTime(new Date());

        // 设置会员对应的推广类型（散客），所属员工，上级城市服务商，上上级城市服务商数据
        setExtendInfo(invitor,memberAccountEntity,entity,MemberTypeEnum.MEMBER.getValue());

        this.editByIdSelective(entity);

        //新增用户关系
        log.info("新增用户关系...");
        userService.insertUserRelaEntity(memberAccountEntity.getOemCode(),userId,5,invitor.getId(),5,memberAccountEntity.getMemberAccount(),null,null);

        // 生成会员资金账户信息
        UserCapitalAccountEntity uca = new UserCapitalAccountEntity();
        uca.setAddUser(memberAccountEntity.getMemberAccount());
        uca.setAddTime(new Date());
        uca.setOemCode(registerDto.getOemCode());
        uca.setUserId(memberAccountEntity.getId());
        uca.setUserType(1);// 用户类型 1-会员 2 -系统用户
        uca.setCapitalAccount(UniqueNumGenerator.generateUniqueNo());
        uca.setTotalAmount(0L);// 初始账户金额位0
        uca.setAvailableAmount(0L);
        uca.setBlockAmount(0L);
        uca.setWalletType(1);
        uca.setStatus(MemberStateEnum.STATE_ACTIVE.getValue());// 状态默认为可用
        log.info("生成会员资金账户信息...");
        this.userCapitalAccountService.insertSelective(uca);

        // 生成会员佣金钱包资金账户
        UserCapitalAccountEntity ucaCommission = new UserCapitalAccountEntity();
        BeanUtils.copyProperties(uca,ucaCommission);
        ucaCommission.setId(null);// 清空ID
        ucaCommission.setWalletType(2);// 佣金钱包
        ucaCommission.setCapitalAccount(UniqueNumGenerator.generateUniqueNo());
        this.userCapitalAccountService.insertSelective(ucaCommission);

        // 释放redis锁
        redisService.unlock(RedisKey.REGIST_REDIS_KEY + memberAccountEntity.getMemberAccount(),registRedisTime);

        // 各项验证通过，生成新的token放缓存
        token = createToken();
        String outTime = sysDictionaryService.getByCode("redis_token_outtime").getDictValue();
        redisService.set(RedisKey.LOGIN_TOKEN_KEY + registerDto.getOemCode() + "_" + "userId_1_" + userId,token,Integer.parseInt(outTime));
        redisService.set(RedisKey.LOGIN_TOKEN_KEY + registerDto.getOemCode() + "_" + token,new CurrUser(userId,registerDto.getMobile(),registerDto.getOemCode(),null),Integer.parseInt(outTime));

        // 组装登录(注册)返回参数
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("status",memberAccountEntity.getStatus());
        dataMap.put("token",token);

        log.info("云租用户同步注册成功：{}",JSON.toJSONString(memberAccountEntity));
        return dataMap;
    }

    /**
     * 修改会员表的微信openId
     * @param user
     * @param jsCode
     */
    @Override
    public void updateMemberWechatOpenId(CurrUser user,String jsCode){
        // 获取并更新用户微信openId
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(user.getOemCode(),2);
        if(null == paramsEntity){
            throw new BusinessException("获取用户openId失败：未配置微信支付相关信息！");
        }
        // 解析paramValues，配置样例：{"appId": "wxb884fccbb878f5b8","keyNum": "bb9aa8f2499c329b88f37567dd9aab31","signKey": "c4ac143ecafba42f528d1fcbec8c531f"}
        JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
        String appId = params.getString("appId");
        String appSecret = paramsEntity.getSecKey();
        String openId = WechatPayUtils.getWxOpenId(appId,appSecret,jsCode);
        MemberAccountEntity t = new MemberAccountEntity();
        t.setId(user.getUserId());
        t.setOpenId(openId);
        t.setUpdateTime(new Date());
        t.setUpdateUser(user.getUseraccount());

        log.info("更新云租用户openId：{}",openId);
        this.editByIdSelective(t);
    }

    @Override
    public AccountVO queryMemberByPhone(String phone, String oemCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("phone", phone);
        params.put("oemCode", oemCode);
        return mapper.queryMemberByPhone(params);
    }
    @Override
    public List<MemberAccountEntity> queryMemberByPhoneAndOemCode(String phone, String oemCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("phone", phone);
        params.put("oemCode", oemCode);
        return mapper.queryMemberByPhoneAndOemCode(params);
    }

    @Override
    public AchievementStatVO queryAchievementStatisticApi(Long userId, String oemCode, Integer levelNo) throws BusinessException {
        log.info("推广中心-业绩总览查询开始：{}，{}，{}", userId, oemCode, levelNo);
        AchievementStatVO statData = new AchievementStatVO();

        // 会员等级 0-税务顾问 1-城市服务商
        if (Objects.equals(levelNo, 0) || Objects.equals(levelNo, 1)) {

            MemberAccountEntity member = this.findById(userId);
            if (null == member) {
                throw new BusinessException("查询失败，当前登录 " + ResultConstants.USER_NOT_EXISTS.getRetMsg());
            }
            Integer extendType = member.getExtendType(); //推广角色：1-散客 2-直客 3-顶级直客
            if (MemberTypeEnum.EMPLOYEE.getValue().equals(member.getMemberType())) {
                extendType = -1; // 标识员工
            }

            // 查询会员等级，获取开票最低金额
            MemberLevelEntity level = memberLevelService.findById(member.getMemberLevel());
            if (null == level) {
                throw new BusinessException("查询失败，未查询到会员等级");
            }

            // 查询业绩总览
            statData = this.memberAccountMapper.queryAchievementStatistic(userId, oemCode, extendType, null, level.getInvoiceMinMoney());
            statData.setTotalProfitsAmount(statData.getTotalProfitsAmount() + statData.getAddFissionProfitsAmount());// 累计佣金
            statData.setExtendInvoiceAmount(statData.getAddInvoiceAmount());// 累计直推开票

            // -------------------查询直推达标个体数-------------------
            StringBuilder ids = new StringBuilder();
            ids.append(userId).append(",");

            // 查询会员下所有直推用户列表
            List<MemberAccountEntity> memberPushList = memberAccountMapper.queryMemberEmployeeList(member.getId(), member.getOemCode(), 1, null, null, null);

            // 查询会员下所有员工列表
            List<MemberAccountEntity> employeeList = memberAccountMapper.queryMemberEmployeeList(member.getId(), member.getOemCode(), 2, null, null, null);
            if (CollectionUtil.isNotEmpty(employeeList)) {
                // 查询员工下所有直推用户列表
                for (MemberAccountEntity employee : employeeList) {
                    ids.append(employee.getId()).append(",");
                    memberPushList.addAll(memberAccountMapper.queryMemberEmployeeList(employee.getId(), employee.getOemCode(), 1, null, null, null));
                }
            }

            // 循环直推用户列表
            if (CollectionUtil.isNotEmpty(memberPushList)) {
                for (MemberAccountEntity push : memberPushList) {
                    ids.append(push.getId()).append(",");
                }
            }
            ids.delete(ids.length() - 1, ids.length());
            statData.setExtendStdComCount(memberAccountMapper.statExtendStdComCount(ids.toString(), oemCode, level.getInvoiceMinMoney()));

            // 查询本月推广业绩
            AchievementStatVO monthStatData = this.memberAccountMapper.queryAchievementStatistic(userId, oemCode, extendType, 1, level.getInvoiceMinMoney());
            statData.setAddExtendUserCount(monthStatData.getExtendUserCount());// 本月新增直推用户
            statData.setAddExtendCompanyCount(monthStatData.getExtendCompanyCount());// 本月新增直推个体
            statData.setAddInvoiceAmount(monthStatData.getAddInvoiceAmount());// 本月新增直推开票
            statData.setAddProfitsAmount(monthStatData.getTotalProfitsAmount());// 本月新增直推佣金
            statData.setAddFissionUserCount(monthStatData.getFissionUserCount());// 本月新增裂变用户
            statData.setAddFissionCompanyCount(monthStatData.getFissionCompanyCount());// 本月新增裂变个体
            statData.setAddFissionProfitsAmount(monthStatData.getAddFissionProfitsAmount());// 本月新增裂变佣金
            statData.setAddFissionInvoiceAmount(monthStatData.getAddFissionInvoiceAmount());// 本月新增裂变开票

            // 设置当前登录会员信息
            statData.setUserName(member.getRealName());
            statData.setExtendType(member.getExtendType());
            statData.setLevelNo(level.getLevelNo());
            statData.setMemberAccount(member.getMemberAccount());
            statData.setUserType(1); //用户类型 1-会员 2-系统用户

            // 拼接升级描述信息
            StringBuilder upgradeDesc = new StringBuilder();

            MemberLevelEntity upLevel = null;
            // 如果当前会员不是城市服务商，查询当前会员等级上一级会员升级信息
            if (!MemberLevelEnum.DIAMOND.getValue().equals(level.getLevelNo()) && !MemberLevelEnum.BRONZE.getValue().equals(level.getLevelNo())) {
                if (MemberLevelEnum.NORMAL.getValue().equals(level.getLevelNo())) {
                    upLevel = this.memberLevelService.queryMemberLevel(oemCode, level.getLevelNo() + 1);
                } else {
                    upLevel = this.memberLevelService.queryMemberLevel(oemCode, level.getLevelNo() + 2);
                }
                if (null != upLevel) {
                    if (MemberLevelEnum.NORMAL.getValue().equals(level.getLevelNo())) {
                        upgradeDesc.append("推广").append(upLevel.getRegistCompanyNum()).append("个个体可升级至").append(upLevel.getLevelName());
                    } else {
                        // 数值转换，分转为万
                        BigDecimal invoiceMinMoney = new BigDecimal(upLevel.getInvoiceMinMoney()).divide(new BigDecimal(1000000));
                        upgradeDesc.append("推广").append(upLevel.getCompleteInvoiceCompanyNum()).
                                append("个开票达").append(invoiceMinMoney.toPlainString()).append("万个体可升级至").append(upLevel.getLevelName());
                    }
                }
            } else {
                upgradeDesc.append("您已达到最高推广等级");
            }
            statData.setUpgradeDesc(upgradeDesc.toString());

            // 会员等 级 2-城市合伙人 3-高级城市合伙人
        } else if (Objects.equals(levelNo, 2) || Objects.equals(levelNo, 3)) {

            // 查询系统用户表获取绑定的钻石会员
            UserEntity userEntity = userService.findById(userId);
            if (userEntity == null) {
                throw new BusinessException("合伙人信息不存在");
            }

            // 查询钻石会员信息
            MemberAccountEntity member = queryByAccount(userEntity.getBindingAccount(), oemCode);
            if (null == member) {
                throw new BusinessException("查询失败，当前登录 " + ResultConstants.USER_NOT_EXISTS.getRetMsg());
            }
            Integer extendType = member.getExtendType(); //推广角色：1-散客 2-直客 3-顶级直客
            if (MemberTypeEnum.EMPLOYEE.getValue().equals(member.getMemberType())) {
                extendType = -1; // 标识员工
            }

            // 查询会员等级，获取开票最低金额
            MemberLevelEntity level = memberLevelService.findById(member.getMemberLevel());
            if (null == level) {
                throw new BusinessException("查询失败，未查询到会员等级");
            }

            // 查询业绩总览
            statData = this.memberAccountMapper.queryAchievementStatistic(member.getId(), oemCode, extendType, null, level.getInvoiceMinMoney());
            // 合伙人只有裂变用户数量，把直推用户数累加到裂变上
            // 裂变用户数 = 裂变用户数 + 直推用户数
            statData.setFissionUserCount(statData.getFissionUserCount() + statData.getExtendUserCount());
            // 裂变个体数 = 裂变个体数 + 直推个体数
            statData.setFissionCompanyCount(statData.getFissionCompanyCount() + statData.getExtendCompanyCount());

            // 查询合伙人的分润记录，累计分润佣金
            Long totalProfitsAmount = memberAccountMapper.queryProfitsByUserId(userId, oemCode, null);
            statData.setTotalProfitsAmount(totalProfitsAmount);// 累计佣金

            // 查询本月推广业绩
            AchievementStatVO monthStatData = this.memberAccountMapper.queryAchievementStatistic(member.getId(), oemCode, extendType, 1, level.getInvoiceMinMoney());
            // 合伙人只有裂变用户数量，把直推用户数累加到裂变上
            statData.setAddFissionUserCount(monthStatData.getFissionUserCount() + monthStatData.getExtendUserCount());// 本月新增裂变用户
            statData.setAddFissionCompanyCount(monthStatData.getFissionCompanyCount() + monthStatData.getExtendCompanyCount());// 本月新增裂变个体
            statData.setAddFissionInvoiceAmount(monthStatData.getAddFissionInvoiceAmount() + monthStatData.getAddInvoiceAmount());// 本月新增裂变开票

            // 查询合伙人本月分润佣金
            Long monthProfitsAmount = memberAccountMapper.queryProfitsByUserId(userId, oemCode, 1);
            statData.setAddFissionProfitsAmount(monthProfitsAmount);// 本月新增裂变佣金

            // 设置当前登录会员信息
            statData.setUserName(userEntity.getNickname());
            statData.setLevelNo(userEntity.getPlatformType());

            // 查询系统用户扩展表获取手机号码
            UserExtendEntity userExtend = new UserExtendEntity();
            userExtend.setUserId(userId);
            userExtend = userExtendService.selectOne(userExtend);
            if (null == userExtend) {
                throw new BusinessException("查询失败，未查询到用户拓展信息");
            }
            statData.setMemberAccount(userExtend.getPhone());
            statData.setUserType(2); //用户类型 1-会员 2-系统用户
        }
        return statData;
    }


//===============国金助手
    /**
     * 根据渠道用户id查询会员信息
     * @param gjMemberQuery
     * @return
     */
    @Override
    public List<GjMemberInfoVO> getMemberInfoByChannelServiceId(GjMemberQuery gjMemberQuery){
        return mapper.getMemberInfoByChannelServiceId(gjMemberQuery);
    }

    @Override
    public PageInfo<GjMemberInfoVO> getMemberInfoByChannelServiceIdPageInfo(GjMemberQuery gjMemberQuery) {
        PageHelper.startPage(gjMemberQuery.getPageNumber(),gjMemberQuery.getPageSize());
        return new PageInfo<>(mapper.getMemberInfoByChannelServiceId(gjMemberQuery));
    }

    /**
     * 国金推广用户业绩查询
     * @param query
     * @return
     */
    @Override
    public List<EmpManageOfPushListVO> getUserPushResultListByChannelUser(GjMemberQuery query){
        return mapper.getUserPushResultListByChannelUser(query);
    }

    /**
     * 国金推广用户业绩查询（分页）
     * @param query
     * @return
     */
    @Override
    public PageInfo<EmpManageOfPushListVO> getUserPushResultPageByChannelUser(GjMemberQuery query){
        List<EmpManageOfPushListVO> result = getUserPushResultListByChannelUser(query);
        if(result==null || result.size() == 0 || result.get(0) == null){
            result = new ArrayList<>();
        }
        if(result!=null &&result.size()>0) {
            JSONObject lastJson = null;
            for(EmpManageOfPushListVO vo : result ){
                // 查询最近一次
                lastJson = mapper.queryLastInvoice(vo.getUserId(), vo.getOemCode());
                if (lastJson != null) {
                    vo.setLastInvoiceTime(lastJson.getDate("add_time"));
                    vo.setLastInvoiceAmount(lastJson.getLongValue("invoice_amount"));
                }
            }
        }
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        PageInfo<EmpManageOfPushListVO> pages = PageResultVo.listToPage(query.getPageNumber(), query.getPageSize(), result);
        return pages;
    }

    @Override
    public PageInfo<AgentMemberVO> queryAgentMemberPageInfo(AgentMemberQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(mapper.queryAgentMemberList(query));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChannelServiceIdMember(AgentMemberQuery query) {
       if(query == null){
           throw new BusinessException("参数错误");
       }
       mapper.updateChannelServiceIdByChannelUserId(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChannelServiceIdMember(List<AgentMemberQuery> list) {
       if(list == null){
           throw new BusinessException("参数错误");
       }
       for(AgentMemberQuery vo : list) {
           updateChannelServiceIdMember(vo);
       }
    }

    /**
     * 根据渠道服务商统计推广数据
     * @param channelUsers
     * @param oemCode
     * @return
     */
    @Override
    public List<GjPushStatisInfoVO> userPushResultStatisByChannelUser(List<Long> channelUsers,String oemCode){
        List<GjPushStatisInfoVO> list =  mapper.userPushResultStatisByChannelUser(channelUsers,null,oemCode);
        if(list!=null &&list.size()>0){
            list.addAll(mapper.userPushResultStatisByChannelUser(null,channelUsers,oemCode));
        }else{
            list = mapper.userPushResultStatisByChannelUser(null,channelUsers,oemCode);
        }
        return list;
    }

    /**
     * 国金账号登录
     * @param query
     * @return
     */
   public String loginFormGJ(GjMemberQuery query){
       if(query == null){
           throw new BusinessException("登录参数不能为空");
       }
       MemberAccountEntity memberAccountEntity = new MemberAccountEntity();
       memberAccountEntity.setOemCode(query.getOemCode());
       memberAccountEntity.setChannelCode(query.getChannelOemCode());
       memberAccountEntity.setChannelUserId(query.getChannelUserId());
       memberAccountEntity.setMemberAccount(query.getMemberAccount());
       List<MemberAccountEntity> memberlist = this.select(memberAccountEntity);
       if(memberlist == null || memberlist.size() == 0){
           throw new BusinessException("用户不存在，请确认账号是否正确");
       }else if(memberlist.size()>1){
           throw new BusinessException("登录失败，请确认账号是否正确");
       }else{
           memberAccountEntity = memberlist.get(0);
           if(!Objects.equals(1,memberAccountEntity.getStatus())){
               throw new BusinessException("登录失败，当前用户已禁用或注销，请联系客服");
           }
           // 清除该用户旧的token，避免多用户用同一个账号登录
           String oldToken = redisService.get(RedisKey.LOGIN_TOKEN_KEY + memberAccountEntity.getOemCode() + "_" + "userId_1_" + memberAccountEntity.getId());
           redisService.delete(RedisKey.LOGIN_TOKEN_KEY + memberAccountEntity.getOemCode() + "_" + oldToken);

           // 各项验证通过，生成新的token放缓存
           String token = createToken();
           String outTime = sysDictionaryService.getByCode("redis_token_outtime").getDictValue();
           redisService.set(RedisKey.LOGIN_TOKEN_KEY + memberAccountEntity.getOemCode() + "_" + "userId_1_" + memberAccountEntity.getId(),token,Integer.parseInt(outTime));
           redisService.set(RedisKey.LOGIN_TOKEN_KEY + memberAccountEntity.getOemCode() + "_" + token,new CurrUser(memberAccountEntity.getId(),memberAccountEntity.getMemberAccount(),memberAccountEntity.getOemCode(),null),Integer.parseInt(outTime));
           return token;
       }
   }

    @Override
    @Transactional(rollbackFor={Exception.class})
    public MemberAccountEntity syncAccountFromGJ(GJUserAuthDTO gjUserAuthDTO,String oemCode) throws BusinessException {
        log.info("会员同步请求处理：{}",gjUserAuthDTO.toString());
        //会员注册参数校验
       String message =  checkSyncAccountParams(gjUserAuthDTO);
       if(StringUtils.isNotBlank(message)){
           throw new BusinessException(message);
       }
        //存储会员账号信息
        MemberAccountEntity memberAccountEntity = new MemberAccountEntity();
        memberAccountEntity.setMemberAccount(gjUserAuthDTO.getMemberAccount());
        memberAccountEntity.setMemberPhone(gjUserAuthDTO.getMemberAccount());// 初始值默认与帐号一致
        memberAccountEntity.setOemCode(oemCode);
        memberAccountEntity.setSourceType(1);// 操作来源
        memberAccountEntity.setAuthStatus(MemberAuthStatusEnum.UN_AUTH.getValue());// 会员实名认证状态，默认为“未认证”
        // 查询会员默认等级 -1-员工 0-普通会员 1-税务顾问 2-城市服务商
        MemberLevelEntity level = this.memberLevelService.queryMemberLevel(oemCode, 0);
        if(null == level){
            throw new BusinessException("操作失败，未配置会员等级信息！");
        }
        memberAccountEntity.setMemberLevel(level.getId());
        memberAccountEntity.setMemberName(gjUserAuthDTO.getMemberAccount());// 昵称默认为手机号
        memberAccountEntity.setLevelName(level.getLevelName());
        memberAccountEntity.setChannelServiceId(gjUserAuthDTO.getChannelServiceId());
        memberAccountEntity.setChannelEmployeesId(gjUserAuthDTO.getChannelEmployeesId());
        memberAccountEntity.setChannelProductCode(gjUserAuthDTO.getChannelProductCode());
        memberAccountEntity.setChannelCode(gjUserAuthDTO.getChannelOemCode());
        memberAccountEntity.setChannelUserId(gjUserAuthDTO.getChannelUserId());
        memberAccountEntity.setMemberType(MemberTypeEnum.MEMBER.getValue());
        memberAccountEntity.setMemberAuthType(gjUserAuthDTO.getMemberAuthType());
        // 判断OEM机构是否存在
        OemEntity oem = this.oemService.getOem(oemCode);
        if(null == oem){
            throw new BusinessException("操作失败，OEM机构不存在");
        }

        memberAccountEntity.setStatus(MemberStateEnum.STATE_ACTIVE.getValue());
        memberAccountEntity.setAuthPushState(0);
        memberAccountEntity.setAddTime(new Date());
        memberAccountEntity.setAddUser("国金助手");
        memberAccountEntity.setRemark("国金助手同步注册");
        this.insertSelective(memberAccountEntity);

        // 设置会员层级关系
        MemberAccountEntity entity = new MemberAccountEntity();
        entity.setId(memberAccountEntity.getId());
        entity.setMemberTree(memberAccountEntity.getId() + "");
        entity.setUpdateTime(new Date());
        this.editByIdSelective(entity);

        //新增用户关系表
        userService.insertUserRelaEntity(memberAccountEntity.getOemCode(),memberAccountEntity.getId(),5,null,5,memberAccountEntity.getMemberAccount(),null,null);

        // 生成会员消费钱包资金账户信息
        UserCapitalAccountEntity uca = new UserCapitalAccountEntity();
        uca.setAddUser(memberAccountEntity.getMemberAccount());
        uca.setAddTime(new Date());
        uca.setOemCode(oemCode);
        uca.setWalletType(1);// 消费钱包
        uca.setUserId(memberAccountEntity.getId());
        uca.setUserType(1);// 用户类型 1-会员 2 -系统用户
        uca.setCapitalAccount(UniqueNumGenerator.generateUniqueNo());
        uca.setTotalAmount(0L);// 初始账户金额位0
        uca.setAvailableAmount(0L);
        uca.setBlockAmount(0L);
        uca.setStatus(MemberStateEnum.STATE_ACTIVE.getValue());// 状态默认为可用
        this.userCapitalAccountService.insertSelective(uca);

        // 生成会员佣金钱包资金账户
        UserCapitalAccountEntity ucaCommission = new UserCapitalAccountEntity();
        BeanUtils.copyProperties(uca,ucaCommission);
        ucaCommission.setId(null);// 清空ID
        ucaCommission.setWalletType(2);// 佣金钱包
        ucaCommission.setCapitalAccount(UniqueNumGenerator.generateUniqueNo());
        this.userCapitalAccountService.insertSelective(ucaCommission);

        //添加会员变动表
        MemberAccountChangeEntity memberAccountChangeEntity=new MemberAccountChangeEntity();
        BeanUtils.copyProperties(memberAccountEntity,memberAccountChangeEntity);
        memberAccountChangeEntity.setAccountId(entity.getId());
        memberAccountChangeEntity.setId(null);
        memberAccountChangeEntity.setAddTime(new Date());
        memberAccountChangeEntity.setAddUser(memberAccountEntity.getMemberAccount());
        memberAccountChangeEntity.setRemark("会员注册");
        memberAccountChangeService.insertSelective(memberAccountChangeEntity);
        log.info("会员/员工注册成功：{}",JSON.toJSONString(memberAccountEntity));
        return memberAccountEntity;
    }

    /**
     * 国金同步实名信息
     * @param gjUserAuthDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateAuthFromGj(GJUserAuthDTO gjUserAuthDTO,MemberAccountEntity memberAccountEntity){
        if(gjUserAuthDTO.getIsUpdateAutho() == null || gjUserAuthDTO.getIsUpdateAutho() != 1){
            return "";
        }
        //参数校验
        String checkMsg = checkAuthParams(gjUserAuthDTO);
        if(StringUtils.isNotBlank(checkMsg)){
            return checkMsg;
        }
        //上传图片到oss
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dir = memberAccountEntity.getOemCode() + "/" + sdf.format(new Date());
        String bucket = sysDictionaryService.getByCode("oss_privateBucketName").getDictValue();
        String page = sysDictionaryService.getByCode("oss_page").getDictValue();
        if(org.apache.commons.lang.StringUtils.isNotBlank(page) && StringUtils.startsWith(page, "/")){ //oss包名不能以/开头
            page = page.substring(1);
        }
        //身份证正面照
        //base64图片 需要去除前缀
        String idCardFrontImgBase64 = gjUserAuthDTO.getIdCardFront().substring(gjUserAuthDTO.getIdCardFront().indexOf( "," ) + 1 );;
        String idCardFrontFileName = UUID.randomUUID().toString();
        boolean flag = ossService.uploadBase64(idCardFrontFileName+".png",page+dir+"/",bucket,idCardFrontImgBase64);
        String idCardFrontName = (dir+"/"+idCardFrontFileName+".png");

        //身份证反面照
        String idCardBackImgBase64 = gjUserAuthDTO.getIdCardBack().substring(gjUserAuthDTO.getIdCardBack().indexOf( "," ) + 1 );
        String idCardBackFileName = UUID.randomUUID().toString();
        boolean flag1 = ossService.uploadBase64(idCardBackFileName+".png",page+dir+"/",bucket,idCardBackImgBase64);
        String idCardBackName = (dir+"/"+idCardBackFileName+".png");
        if(!flag || !flag1){
            return "身份证照片上传失败，请重试";
        }
        //修改会员表的实名推送状态
        memberAccountEntity.setAuthStatus(1);
        memberAccountEntity.setAuthPushState(2);
        memberAccountEntity.setMemberAuthType(gjUserAuthDTO.getMemberAuthType());
        memberAccountEntity.setUpdateTime(new Date());
        memberAccountEntity.setUpdateUser("国金助手");
        memberAccountEntity.setMemberAuthType(gjUserAuthDTO.getMemberAuthType());
        editByIdSelective(memberAccountEntity);
        //修改实名
        updateUserAuth(memberAccountEntity.getId(), gjUserAuthDTO.getMemberName(), gjUserAuthDTO.getIdCardNo(), idCardFrontName, idCardBackName, gjUserAuthDTO.getExpireDate(), gjUserAuthDTO.getIdCardAddr(), MemberAuthStatusEnum.AUTH_SUCCESS.getValue(), "国金实名信息同步");
        //身份证号一致，不解绑银行卡
        if(!gjUserAuthDTO.getIdCardNo().equals(memberAccountEntity.getIdCardNo())) {
            //解绑银行卡，国金助手同步修改实名自动解绑银行卡
            UserBankCardEntity userBankCardEntity = userBankCardService.getBankCardInfoByUserIdAndUserType(memberAccountEntity.getId(), 1, memberAccountEntity.getOemCode());
            if (userBankCardEntity != null) {
                userBankCardEntity.setStatus(1);
                userBankCardEntity.setUpdateTime(new Date());
                userBankCardEntity.setUpdateUser("国金助手");
                userBankCardEntity.setRemark("国金助手修改实名同步解绑银行卡");
                userBankCardService.editByIdSelective(userBankCardEntity);
            }
        }
        return "";
    }

    /**
     * 校验国金同步注册参数
     * @param gjUserAuthDTO
     * @return
     */
    private String checkSyncAccountParams(GJUserAuthDTO gjUserAuthDTO){
        if(StringUtils.isBlank(gjUserAuthDTO.getMemberAccount())){
            return "手机号不能为空";
        }else if(!gjUserAuthDTO.getMemberAccount().startsWith("1")){
            return "手机号格式不正确";
        }else if(gjUserAuthDTO.getChannelServiceId() == null){
            return "所属服务商id为空";
        }else if(StringUtils.isBlank(gjUserAuthDTO.getChannelProductCode())){
            return "渠道产品编码不能为空";
        }
        return "";
    }
    /**
     * 实名参数校验
     * @param gjUserAuthDTO
     */
    private String checkAuthParams(GJUserAuthDTO gjUserAuthDTO){
        if(StringUtils.isBlank(gjUserAuthDTO.getMemberName())){
            return "用户姓名不能为空";
        }else if(StringUtils.isBlank(gjUserAuthDTO.getIdCardNo())){
            return "身份证号不能为空";
        }else if(StringUtils.isBlank(gjUserAuthDTO.getIdCardFront())){
            return "身份证正面照不能为空";
        }else if(StringUtils.isBlank(gjUserAuthDTO.getIdCardBack())){
            return "身份证反面照不能为空";
        }else if(StringUtils.isBlank(gjUserAuthDTO.getIdCardAddr())){
            return "身份证地址不能为空";
        }else if(StringUtils.isBlank(gjUserAuthDTO.getExpireDate())){
            return "身份证有效期不能为空";
        }else if(gjUserAuthDTO.getMemberAuthType() == null || gjUserAuthDTO.getMemberAuthType()<0 || gjUserAuthDTO.getMemberAuthType()>2){
            return "用户身份错误";
        }
        // 验证身份证有效期格式
        String regex = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))-((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)).02.29))";
        if(!(gjUserAuthDTO.getExpireDate().contains("长期") || Pattern.matches(regex,gjUserAuthDTO.getExpireDate()))){
            return "身份证有效期格式有误，格式要求：yyyy.MM.dd-yyyy.MM.dd";
        }
        return null;
    }

    @Override
    public MemberAccountOemInfoVO queryMemberOemInfo(Long id) {
        return mapper.queryMemberOemInfo(id);
    }

    @Override
    public List<CompanyPushVo> queryCompanyInfoByAuthPushState() {
        // 查看是否有推送中的数据
        List<CompanyPushVo> fialList = mapper.queryCompanyInfoByAuthPushState(1);
        if (fialList != null && fialList.size() >0){
            throw new BusinessException("数据正在推送中");
        }
        List<CompanyPushVo> list = mapper.queryCompanyInfoByAuthPushState(3);
        if (list == null || list.size()<1){
            return null;
        }
        return list;
    }

    @Override
    public void companyPushGJ(CompanyPushVo vo) {
        if (vo.getUserId() == null ){
            throw new BusinessException("映射userId不能为空");
        }
        Map<String,Object>dataParams = GjParams(vo);
        OemParamsEntity oemParamsEntity =  oemParamsService.getParams(vo.getOemCode(),26);
        String result = GuoJinUtil.gotoWJCompanyAuthPush(dataParams,oemParamsEntity.getSecKey(), oemParamsEntity.getUrl(),3, vo.getChannelCode());
        if ( result.equals("0000")){
            vo.setAuthPushState(2);
        }else{
            vo.setAuthPushState(3);
        }
        mapper.updateAuthPushStateById(vo.getId(),vo.getAuthPushState());
    }

    public Map<String,Object> GjParams(CompanyPushVo vo){
        if (StringUtil.isNotBlank(vo.getExpireDate())){
            String[] data = vo.getExpireDate().split("-");
            if (data != null && data.length == 2){
                vo.setBeginDate(data[0].replace(".","-"));
                vo.setEndDate(data[1].replace(".","-"));
            }
        }
        //  获取图片的base64
        String idCardFront = ImageUtils.netImageToBase64(ossService.getPrivateImgUrl(vo.getIdCardFront()));
        String idCardBack = ImageUtils.netImageToBase64(ossService.getPrivateImgUrl(vo.getIdCardBack()));
        String suffix = vo.getIdCardFront().substring(vo.getIdCardFront().lastIndexOf(".") + 1);
        vo.setIdCardFront(suffix+","+idCardFront);
        suffix = vo.getIdCardBack().substring(vo.getIdCardBack().lastIndexOf(".") + 1);
        vo.setIdCardBack(suffix+ "," +idCardBack);

        Map<String,Object> dataParams = new HashMap<String,Object>();
        dataParams.put("userId",vo.getUserId());
        dataParams.put("productCode",vo.getProductCode());
        dataParams.put("beginDate",vo.getBeginDate());
        dataParams.put("idCardAddr",vo.getIdCardAddr());
        dataParams.put("endDate",vo.getEndDate());
        dataParams.put("idCardNo",vo.getIdCardNo());
        dataParams.put("realName",vo.getRealName());
        dataParams.put("idCardFront",vo.getIdCardFront());
        dataParams.put("idCardBack",vo.getIdCardBack());
        return dataParams;
    }

    /**
     * 国金数据统计
     * @param channelDirectServicesUserIds 直推服务商直推用户列表
     * @param channelFissionServicesUserIds 直推服务商裂变用户列表
     * @param channelDirectUserIds  直推用户列表
     * @param channelFissionUserIds 裂变用户列表
     * @param oemCode  云财oemcode
     * @return
     */
    public Map<String,Object> gjDataStatisByChannelUser(List<Long> channelDirectServicesUserIds,List<Long> channelFissionServicesUserIds,
                                                 List<Long> channelDirectUserIds,List<Long> channelFissionUserIds,String oemCode){
        Map<String,Object>  result = new HashMap<>();
        //直推合伙人企业数
        if(channelDirectServicesUserIds!=null){
            Map<String,Object> resultMap =  mapper.gjCompanyStatisByChannelUser(null,channelDirectServicesUserIds,oemCode);
            if(resultMap!=null){
                result.put("monthDirectCompanyNumByServiceUser",resultMap.get("monthCompanyNum"));
                result.put("totalDirectCompanyNumByServiceUser",resultMap.get("totalCompanyNum"));
            }else{
                result.put("monthDirectCompanyNumByServiceUser",0);
                result.put("totalDirectCompanyNumByServiceUser",0);
            }
        }else{
            result.put("monthDirectCompanyNumByServiceUser",0);
            result.put("totalDirectCompanyNumByServiceUser",0);
        }
        //裂变合伙人企业数
        if(channelFissionServicesUserIds!=null){
            Map<String,Object> resultMap =  mapper.gjCompanyStatisByChannelUser(null,channelFissionServicesUserIds,oemCode);
            if(resultMap!=null){
                result.put("monthFissionCompanyNumByServiceUser",resultMap.get("monthCompanyNum"));
                result.put("totalFissionCompanyNumByServiceUser",resultMap.get("totalCompanyNum"));
            }else{
                result.put("monthFissionCompanyNumByServiceUser",0);
                result.put("totalFissionCompanyNumByServiceUser",0);
            }
        }else{
            result.put("monthFissionCompanyNumByServiceUser",0);
            result.put("totalFissionCompanyNumByServiceUser",0);
        }
        //直推用户企业数
        if(channelDirectUserIds!=null){
            Map<String,Object> resultMap =  mapper.gjCompanyStatisByChannelUser(null,channelDirectUserIds,oemCode);
            if(resultMap!=null){
                result.put("monthDirectCompanyNumByUser",resultMap.get("monthCompanyNum"));
                result.put("totalDirectCompanyNumByUser",resultMap.get("totalCompanyNum"));
            }else{
                result.put("monthDirectCompanyNumByUser",0);
                result.put("totalDirectCompanyNumByUser",0);
            }
        }else{
            result.put("monthDirectCompanyNumByUser",0);
            result.put("totalDirectCompanyNumByUser",0);
        }
        //裂变用户企业数
        if(channelFissionUserIds!=null){
            Map<String,Object> resultMap =  mapper.gjCompanyStatisByChannelUser(null,channelFissionUserIds,oemCode);
            if(resultMap!=null){
                result.put("monthFissionCompanyNumByUser",resultMap.get("monthCompanyNum"));
                result.put("totalFissionCompanyNumByUser",resultMap.get("totalCompanyNum"));
            }else{
                result.put("monthFissionCompanyNumByUser",0);
                result.put("totalFissionCompanyNumByUser",0);
            }
        }else{
            result.put("monthFissionCompanyNumByUser",0);
            result.put("totalFissionCompanyNumByUser",0);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> loginOfAccessParty(AccessPartyLoginDTO dto) throws BusinessException{
        log.info("【收到第三方用户注册/登录请求】：account {}, accessPartyCode {}, oemCode {}", dto.getAccount(), dto.getAccessPartyCode(), dto.getOemCode());
        Map<String,Object> dataMap = new HashMap<>();

        // 校验接入方是否上架
        OemAccessPartyQuery oemAccessPartyQuery = new OemAccessPartyQuery();
        oemAccessPartyQuery.setAccessPartyCode(dto.getAccessPartyCode());
        oemAccessPartyQuery.setStatus(1);
        OemAccessPartyEntity oemAccessParty = Optional.ofNullable(oemAccessPartyService.findByCode(oemAccessPartyQuery)).orElseThrow(() -> new BusinessException("未查询到接入方信息！"));

        // 校验机构是否上架
        String oemCode = dto.getOemCode();
        OemEntity oem = Optional.ofNullable(oemService.getOem(oemCode)).orElseThrow(() -> new BusinessException("未查询到机构信息！"));
        if (!OemStatusEnum.YES.getValue().equals(oem.getOemStatus())) {
            throw new BusinessException("oem机构不可用！");
        }

        // 查询用户
        String account = dto.getAccount();
        MemberAccountEntity member = this.queryByAccount(account, oemCode);

        // 账号为空，为用户注册账号
        if (null == member || MemberStateEnum.STATE_OFF.getValue().equals(member.getStatus())) {
            // 注册会员账号
            String registRedisTime = (System.currentTimeMillis() + 300000) + "";
            // 加入redis锁机制，防重复提交
            boolean flag = redisService.lock(RedisKey.REGIST_REDIS_KEY + dto.getAccount() + oemCode,registRedisTime,10);
            if(!flag){
                throw new BusinessException("请勿重复提交！");
            }
            try {
                member = this.registerAccount(account, oemCode, oem.getInviterAccount(), MemberLevelEnum.NORMAL.getValue(), null, account, 0, null, null);
            } catch (BusinessException e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.error(e.getMessage());
                throw new BusinessException("账号注册失败");
            } finally {
                // 释放redis锁
                redisService.unlock(RedisKey.REGIST_REDIS_KEY + dto.getAccount() + oemCode,registRedisTime);
            }
            member.setAccessPartyId(oemAccessParty.getId());
            this.updateMemberAccount(member, member.getMemberAccount());
            // 若接入方已绑定人群标签，需要添加一条关系数据
            CrowdLabelEntity crowdLabelEntity = new CrowdLabelEntity();
            crowdLabelEntity.setAccessPartyId(oemAccessParty.getId());
            crowdLabelEntity.setStatus(1);
            CrowdLabelEntity crowdLabel = crowdLabelService.selectOne(crowdLabelEntity);
            if (null != crowdLabel) {
                memberCrowdLabelRelaService.addUserByH5Access(Lists.newArrayList(member.getId()), oem.getOemCode(), crowdLabel.getId(), member.getMemberAccount());
                // 更新人群数
                crowdLabel.setMemberUserNum(crowdLabel.getMemberUserNum() + 1);
                crowdLabelService.updateMemberNumber(crowdLabel);
            }
            log.info("会员注册成功：{}",JSON.toJSONString(member));
        }

        // 账号不为空，校验接入方编码
        Long accessPartyId = member.getAccessPartyId();
        if (null == accessPartyId || !accessPartyId.equals(oemAccessParty.getId())) {
            throw new BusinessException("该账号已在第三方注册");
        }

        // 账号不为空，且接入方校验一致，登录账号
        // 校验用户状态
        if (MemberStateEnum.STATE_UNACTIVE.getValue().equals(member.getStatus())) {
            throw new BusinessException("用户已禁用");
        }
        // 清除该用户旧的token，避免多用户用同一个账号登录
        String oldToken = redisService.get(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + "userId_1_" + member.getId());
        redisService.delete(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + oldToken);

        // 各项验证通过，生成新的token放缓存
        String token = UUID.randomUUID().toString().replaceAll("-","");
        dataMap.put("token",token);
        // 获取接入方超时时间
        Long loginTime = oemAccessParty.getLoginTime();
        redisService.set(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + "userId_1_" + member.getId(), token, loginTime.intValue());
        redisService.set(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + token,new CurrUser(member.getId(), member.getMemberAccount() , oemCode,null), loginTime.intValue());

        // 返回会员基本信息
        dataMap.put("memberId", member.getId());
        return dataMap;
    }

    @Override
    public List<MemberAccountEntity> findByAccessPartyId(Long accessPartyId) {
        return mapper.queryByAccessPartyId(accessPartyId);
    }

    @Override
    public RegPreOrderVO queryRegOrderByOrderNo(String orderNo) {
        return mapper.queryRegOrderByOrderNo(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> loginOfTaxCalculator(TaxCalculatorLoginDTO dto) {
        log.info("【收到税费测算用户注册/登录请求】：account {}, oemCode {}, inviterAccount {}", dto.getAccount(), dto.getOemCode(), dto.getInviterAccount());
        Map<String,Object> dataMap = new HashMap<>();

        // 查询机构信息
        OemEntity oemEntity = Optional.ofNullable(oemService.getOem(dto.getOemCode())).orElseThrow(() -> new BusinessException("机构不存在"));
        if (!OemStatusEnum.YES.getValue().equals(oemEntity.getOemStatus())) {
            throw new BusinessException("机构不可用");
        }

        // 用户是否存在
        MemberAccountEntity member = this.queryByAccount(dto.getAccount(), dto.getOemCode());

        // 用户不存在时，机构是否接入国金
        if (null == member || MemberStateEnum.STATE_OFF.getValue().equals(member.getStatus())) {
            // 查询机构配置
            OemConfigEntity oemConfig = Optional.ofNullable(oemConfigService.queryOemConfigByCode(dto.getOemCode(), "is_open_channel")).orElseThrow(() -> new BusinessException("未查询到机构配置"));
            // 获取邀请人信息
            if (StringUtil.isBlank(dto.getInviterAccount())) {
                dto.setInviterAccount(oemEntity.getInviterAccount());
            }
            MemberAccountEntity inviter = Optional.ofNullable(this.queryByAccount(dto.getInviterAccount(), dto.getOemCode())).orElseThrow(() -> new BusinessException("未查询到邀请人信息"));
            // 机构已接入国金，请求国金账号注册接口
            Long channelUserId = null;
            if ("1".equals(oemConfig.getParamsValue())) {
                // 获取渠道接入配置
                OemParamsEntity oemParams = Optional.ofNullable(oemParamsService.getParams(dto.getOemCode(), OemParamsTypeEnum.GUOJIN_CHANNEL_CONFIG.getValue())).orElseThrow(() -> new BusinessException("未查询到渠道接入配置"));
                // 请求参数
                Map<String, Object> map = Maps.newHashMap();
                map.put("userPhone", dto.getAccount());
                map.put("inviteUserId", inviter.getChannelUserId());
                map.put("oemCode", inviter.getChannelCode());
                map.put("productCode", dto.getOemCode());
                JSONObject jsonObject = GuoJinUtil.gjChannel(map, inviter.getChannelCode(), oemParams.getSecKey(), oemParams.getUrl() + GuoJinUtil.USER_REGISTER);
                // 校验调用结果
                if (null == jsonObject.get("data") || !"0000".equals(jsonObject.getString("retCode"))) {
                    log.info("用户注册失败，调用国金用户注册接口失败：{}", jsonObject.getString("retMsg"));
                    throw new BusinessException("【访问渠道失败】：" + jsonObject.getString("retMsg"));
                }
                JSONObject data = jsonObject.getJSONObject("data");
                if (null != data) {
                    channelUserId = data.getLong("userId");
                    if (null == channelUserId) {
                        log.info("用户注册失败，调用国金用户注册未获取到国金用户id");
                        throw new BusinessException("访问渠道失败】：未找到国金用户");
                    }
                }
            }

            // 云财用户注册
            String registRedisTime = (System.currentTimeMillis() + 300000) + "";
            // 加入redis锁机制，防重复提交
            boolean flag = redisService.lock(RedisKey.REGIST_REDIS_KEY + dto.getAccount() + dto.getOemCode(), registRedisTime,10);
            if(!flag){
                throw new BusinessException("请勿重复提交！");
            }
            try {
                member = this.registerAccount(dto.getAccount(), dto.getOemCode(), inviter.getMemberAccount(), MemberLevelEnum.NORMAL.getValue(), null, dto.getAccount(), 0, null, null);
            } catch (BusinessException e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.error(e.getMessage());
                throw new BusinessException("账号注册失败");
            } finally {
                // 释放redis锁
                redisService.unlock(RedisKey.REGIST_REDIS_KEY + dto.getAccount() + dto.getOemCode(), registRedisTime);
            }

            // 保存渠道用户id
            member.setChannelUserId(channelUserId);
            member.setChannelCode(inviter.getChannelCode());
            this.editByIdSelective(member);
        }

        // 登录账号
        if (null == member) {
            throw new BusinessException("登录失败");
        }
        // 校验用户状态
        if (MemberStateEnum.STATE_UNACTIVE.getValue().equals(member.getStatus())) {
            throw new BusinessException("用户已禁用");
        }
        // 清除该用户旧的token，避免多用户用同一个账号登录
        String oldToken = redisService.get(RedisKey.LOGIN_TOKEN_KEY + dto.getOemCode() + "_" + "userId_1_" + member.getId());
        redisService.delete(RedisKey.LOGIN_TOKEN_KEY + dto.getOemCode() + "_" + oldToken);

        // 各项验证通过，生成新的token放缓存
        String token = UUID.randomUUID().toString().replaceAll("-","");
        dataMap.put("token",token);
        // 获取接入方超时时间
        String outTime = sysDictionaryService.getByCode("redis_token_outtime").getDictValue();
        redisService.set(RedisKey.LOGIN_TOKEN_KEY + dto.getOemCode() + "_" + "userId_1_" + member.getId(),token,Integer.parseInt(outTime));
        redisService.set(RedisKey.LOGIN_TOKEN_KEY + dto.getOemCode() + "_" + token,new CurrUser(member.getId(),member.getMemberAccount(),member.getOemCode(),null),Integer.parseInt(outTime));

        return dataMap;
    }
}

