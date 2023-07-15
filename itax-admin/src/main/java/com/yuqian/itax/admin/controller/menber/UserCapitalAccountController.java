package com.yuqian.itax.admin.controller.menber;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.entity.query.UserCapitalAccountQuery;
import com.yuqian.itax.capital.entity.query.UserCapitalChangeRecordQuery;
import com.yuqian.itax.capital.entity.vo.UserCapitalAccountDetailVO;
import com.yuqian.itax.capital.entity.vo.UserCapitalAccountVO;
import com.yuqian.itax.capital.entity.vo.UserCapitalChangeRecordVO;
import com.yuqian.itax.capital.enums.BankCardTypeEnum;
import com.yuqian.itax.capital.enums.CardStatusEnum;
import com.yuqian.itax.capital.enums.UserCapitalAccountStatusEnum;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.capital.service.UserCapitalChangeRecordService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.vo.BankInfoVO;
import com.yuqian.itax.system.service.BankInfoService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.UserExtendEntity;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.UserExtendService;
import com.yuqian.itax.util.util.MoneyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 资金用户controll
 */
@RestController
@RequestMapping("/userCapitalAccount")
@Slf4j
public class UserCapitalAccountController extends BaseController {

    @Autowired
    UserCapitalAccountService userCapitalAccountService;
    @Autowired
    UserCapitalChangeRecordService userCapitalChangeRecordService;
    @Autowired
    BankInfoService bankInfoService;

    @Autowired
    private OemService oemService;

    @Autowired
    private UserBankCardService userBankCardService;

    @Autowired
    private UserExtendService userExtendService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberAccountService memberAccountService;

    /**
     * 资金账户列表（分页）
     * auth: HZ
     * time: 2019/12/16
     */
    @PostMapping("/userCapitalAccountPageInfo")
    public ResultVo userCapitalAccountPageInfo( @RequestBody UserCapitalAccountQuery userCapitalAccountQuery){
        //验证登陆
        getCurrUser();
        String oemCode=getRequestHeadParams("oemCode");
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            userCapitalAccountQuery.setOemCode(oemCode);
        }

        userCapitalAccountQuery.setTree(getOrgTree());
        //分页查询
        PageInfo<UserCapitalAccountVO> pageInfoList= userCapitalAccountService.queryUserCapitalAccountPageInfo(userCapitalAccountQuery);
        return ResultVo.Success(pageInfoList);
    }
    /**
     * 资金账户导出
     * auth: HZ
     * time: 2019/12/16
     */
    @PostMapping("/exportUserCapitalAccount")
    public ResultVo exportUserCapitalAccount( @RequestBody UserCapitalAccountQuery userCapitalAccountQuery){
        //验证登陆
        getCurrUser();
        String oemCode=getRequestHeadParams("oemCode");
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            userCapitalAccountQuery.setOemCode(oemCode);
        }

        userCapitalAccountQuery.setTree(getOrgTree());
        //分页查询
        List<UserCapitalAccountVO> list= userCapitalAccountService.queryUserCapitalAccountList(userCapitalAccountQuery);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("资金账户", "资金账户", UserCapitalAccountVO.class, list);
        } catch (Exception e) {
            log.error("资金账户导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
        return ResultVo.Success();
    }



    /**
     * 资金变动明细
     * auth: HZ
     * time: 2019/12/16
     */
    @PostMapping("/userCapitalChangeRecordPageInfo")
    public ResultVo userCapitalChangeRecordPageInfo(@RequestBody UserCapitalChangeRecordQuery userCapitalChangeRecordQuery){
        //验证登陆
        getCurrUser();
        String oemCode=getRequestHeadParams("oemCode");

        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            userCapitalChangeRecordQuery.setOemCode(oemCode);
        }
        //分页查询
        PageInfo<UserCapitalChangeRecordVO> list= userCapitalChangeRecordService.queryUserCapitalChangeRecordEntityPageInfo(userCapitalChangeRecordQuery);
        return ResultVo.Success(list);
    }



    /**
     * 资金变动明细导出
     * auth: HZ
     * time: 2019/12/16
     */
    @PostMapping("/exportUserCapitalChangeRecord")
    public ResultVo exportUserCapitalChangeRecord(@RequestBody UserCapitalChangeRecordQuery userCapitalChangeRecordQuery){
        //验证登陆
        getCurrUser();
        String oemCode=getRequestHeadParams("oemCode");
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            userCapitalChangeRecordQuery.setOemCode(oemCode);
        }
        //分页查询
        List<UserCapitalChangeRecordVO> list= userCapitalChangeRecordService.queryUserCapitalChangeRecordEntityList(userCapitalChangeRecordQuery);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("资金变动明细", "资金变动明细", UserCapitalChangeRecordVO.class, list);
        } catch (Exception e) {
            log.error("资金变动明细导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
        return ResultVo.Success();
    }
    /**
     * @Author yejian
     * 查询支持银行卡列表
     * @Date 2019/12/19 14:09
     * @param
     * @return ResultVo<BankInfoVO>
     */
    @PostMapping("/listBankInfo")
    public ResultVo<List<BankInfoVO>> listBankInfo() {
        log.info("查询支持银行卡列表请求参数：{}", JSON.toJSONString(getCurrUserId()));
        if(null == getCurrUser()){
            return ResultVo.Fail("操作失败，用户未登录！");
        }
        try{
            List<BankInfoVO> bankInfoList = bankInfoService.listBankInfo();
            return ResultVo.Success(bankInfoList);
        } catch (Exception e){
            e.printStackTrace();
            log.error("查询支持银行卡列表异常{}",e.getMessage());
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 修改资金账户状态
     * @param id
     * @param status 0-禁用 1-可用
     * @return
     */
    @PostMapping("update/status")
    public ResultVo updateStatus(@JsonParam Long id, @JsonParam Integer status) {
        CurrUser currUser = getCurrUser();
        if (status == null
                || (!Objects.equals(status, UserCapitalAccountStatusEnum.FORBIDDEN.getValue())
                && !Objects.equals(status, UserCapitalAccountStatusEnum.AVAILABLE.getValue()))) {
            return ResultVo.Fail("资金账户操作有误");
        }
        try {
            UserEntity userEntity = userService.findById(currUser.getUserId());
            if (userEntity == null) {
                return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
            }
            String remark = Objects.equals(status, UserCapitalAccountStatusEnum.FORBIDDEN.getValue()) ? "冻结" : "解冻";
            UserCapitalAccountEntity entity = getUserCapitalAccountEntity(id, userEntity.getOemCode());
            if (Objects.equals(status, entity.getStatus())) {
                return ResultVo.Fail("资金账户已" + remark + "，请勿重复操作");
            }
            entity.setStatus(status);
            remark = "资金账户后台" + remark;
            userCapitalAccountService.updateUserCapitalAccountEntity(entity, remark, userEntity.getUsername(), new Date());
            return ResultVo.Success();
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultVo.Fail("修改资金账户状态失败");
        }
    }

    /**
     * 代理提现详情
     * @param id
     * @return
     */
    @PostMapping("agent/withdraw/detail")
    public ResultVo agentWithdrawDetail(@JsonParam Long id) {
        CurrUser currUser = getCurrUser();
        try {
            UserEntity userEntity = userService.findById(currUser.getUserId());
            if (userEntity == null) {
                return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
            }
            //校验资金账户
            UserCapitalAccountEntity userCapitalAccountEntity = validateUserCapitalAccountEntity(id, userEntity.getOemCode());
            OemEntity oem = oemService.getOem(userCapitalAccountEntity.getOemCode());
            if (oem == null) {
                return ResultVo.Fail("所属OEM不存在");
            }
            //判断是否为系统用户
            if(ObjectUtils.equals(userCapitalAccountEntity.getUserType(), 1)){ //会员
                MemberAccountEntity  memberAccountEntity = memberAccountService.findById(userCapitalAccountEntity.getUserId());
                if (memberAccountEntity == null) {
                    return ResultVo.Fail("会员用户不存在");
                }
                //获取银行账户
                UserBankCardEntity userBankCardEntity = getUserBankCardEntity(userCapitalAccountEntity);
                UserCapitalAccountDetailVO userCapitalAccountDetailVO = new UserCapitalAccountDetailVO(userCapitalAccountEntity, null, null, oem, userBankCardEntity);
                userCapitalAccountDetailVO.setUsername(memberAccountEntity.getMemberAccount());
                userCapitalAccountDetailVO.setNickname(StringUtils.isBlank(memberAccountEntity.getRealName())? memberAccountEntity.getMemberName():memberAccountEntity.getRealName() );
                userCapitalAccountDetailVO.setPhone(memberAccountEntity.getMemberAccount());
                return ResultVo.Success(userCapitalAccountDetailVO);
            }else { //系统用户
                userEntity = userService.findById(userCapitalAccountEntity.getUserId());
                if (userEntity == null) {
                    return ResultVo.Fail("系统用户不存在");
                }
                UserExtendEntity userExtendEntity = userExtendService.getUserExtendByUserId(userEntity.getId());
                if (userExtendEntity == null) {
                    return ResultVo.Fail("系统用户扩展信息不存在");
                }
                //获取银行账户
                UserBankCardEntity userBankCardEntity = getUserBankCardEntity(userCapitalAccountEntity);
                return ResultVo.Success(new UserCapitalAccountDetailVO(userCapitalAccountEntity, userEntity, userExtendEntity, oem, userBankCardEntity));
            }
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultVo.Fail("查询代理提现详情失败");
        }
    }

    /**
     * 代理充值详情
     * @param id
     * @return
     */
    @PostMapping("agent/recharge/detail")
    public ResultVo agentRechargeDetail(@JsonParam Long id) {
        CurrUser currUser = getCurrUser();
        try {
            UserEntity userEntity = userService.findById(currUser.getUserId());
            if (userEntity == null) {
                return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
            }
            //校验资金账户
            UserCapitalAccountEntity userCapitalAccountEntity = validateUserCapitalAccountEntity(id, userEntity.getOemCode());
            if (userCapitalAccountEntity.getStatus().equals(0)){
                return ResultVo.Fail("账户已冻结，不允许充值！");
            }
            OemEntity oem = oemService.getOem(userCapitalAccountEntity.getOemCode());
            if (oem == null) {
                return ResultVo.Fail("所属OEM不存在");
            }
            if (userCapitalAccountEntity.getWalletType() == 2){
                return ResultVo.Fail("该钱包类型不支持代理充值");
            }
            //判断是否为系统用户
            if(ObjectUtils.equals(userCapitalAccountEntity.getUserType(), 1)){ //会员
                MemberAccountEntity  memberAccountEntity = memberAccountService.findById(userCapitalAccountEntity.getUserId());
                if (memberAccountEntity == null) {
                    return ResultVo.Fail("会员用户不存在");
                }
                //获取银行账户
               /* UserBankCardEntity userBankCardEntity = getUserBankCardEntity(userCapitalAccountEntity);*/
                UserCapitalAccountDetailVO userCapitalAccountDetailVO = new UserCapitalAccountDetailVO(userCapitalAccountEntity, null, null, oem, null);
                userCapitalAccountDetailVO.setUsername(memberAccountEntity.getMemberAccount());
                userCapitalAccountDetailVO.setNickname(StringUtils.isBlank(memberAccountEntity.getRealName())? memberAccountEntity.getMemberName():memberAccountEntity.getRealName() );
                userCapitalAccountDetailVO.setPhone(memberAccountEntity.getMemberAccount());
                return ResultVo.Success(userCapitalAccountDetailVO);
            }else { //系统用户
                userEntity = userService.findById(userCapitalAccountEntity.getUserId());
                if (userEntity == null) {
                    return ResultVo.Fail("系统用户不存在");
                }
                UserExtendEntity userExtendEntity = userExtendService.getUserExtendByUserId(userEntity.getId());
                if (userExtendEntity == null) {
                    return ResultVo.Fail("系统用户扩展信息不存在");
                }
                return ResultVo.Success(new UserCapitalAccountDetailVO(userCapitalAccountEntity, userEntity, userExtendEntity, oem, null));
            }
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultVo.Fail("查询代理充值详情失败");
        }
    }

    /**
     * 代理充值（优化）
     * @param id 资金账户主键id
     * @param amount 充值金额（分）
     * @param remark 备注
     * @return
     */
    @PostMapping("agent/recharge")
    public ResultVo agentRecharge(@JsonParam Long id, @JsonParam Long amount, @JsonParam String remark) {
        CurrUser currUser = getCurrUser();
        if (amount == null) {
            return ResultVo.Fail("充值金额不能为空");
        }
        if (amount <= 0) {
            return ResultVo.Fail("充值金额必须大于0");
        }
        if (StringUtils.isBlank(remark)) {
            return ResultVo.Fail("请填写备注");
        }
        if (remark.length() > 200) {
            return ResultVo.Fail("备注不能超过200字");
        }
        String lockKey = RedisKey.USER_RECHARGE_AGENT_ADMIN_REDIS_KEY + id;
        String lockTime = (System.currentTimeMillis() + 300000) + "";
        try {
            String minAmt = Optional.ofNullable(dictionaryService.getByCode("recharge_agent_min_limit")).map(DictionaryEntity::getDictValue).orElse("0");
            String maxAmt = Optional.ofNullable(dictionaryService.getByCode("recharge_agent_max_limit")).map(DictionaryEntity::getDictValue).orElse("1000000000");
            long minAmtLimit = Long.parseLong(minAmt);
            if (amount < minAmtLimit) {
                return ResultVo.Fail("充值金额必须大于"+ MoneyUtil.fen2yuan(new BigDecimal(minAmt)) + "元");
            }
            long maxAmtLimit = Long.parseLong(maxAmt);
            if (amount > maxAmtLimit) {
                return ResultVo.Fail("充值金额必须小于"+ MoneyUtil.fen2yuan(new BigDecimal(maxAmt)) + "元");
            }
            UserEntity userEntity = userService.findById(currUser.getUserId());
            if (userEntity == null) {
                return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
            }
            boolean lockResult = redisService.lock(lockKey, lockTime, 60);
            if(!lockResult){
                return ResultVo.Fail("代理充值处理中，请稍后再试");
            }
            //校验资金账户
            UserCapitalAccountEntity userCapitalAccountEntity = validateUserCapitalAccountEntity(id, userEntity.getOemCode());
            //获取银行账户
          /*  UserBankCardEntity userBankCardEntity = getUserBankCardEntity(userCapitalAccountEntity);*/
            orderService.agentAdminRecharge(userCapitalAccountEntity, null, amount, currUser.getUseraccount(), remark);
            return ResultVo.Success();
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultVo.Fail("代理充值失败");
        } finally {
            redisService.unlock(lockKey, lockTime);
        }
    }

    /**
     * 代理提现 (优化)
     * @param id 资金账户主键id
     * @param amount 提现金额（分）
     * @param remark 备注
     * @return
     */
    @PostMapping("agent/withdraw")
    public ResultVo agentWithdraw(@JsonParam Long id, @JsonParam Long amount, @JsonParam String remark) {
        CurrUser currUser = getCurrUser();
        if (amount == null) {
            return ResultVo.Fail("提现金额不能为空");
        }
        if (amount <= 0) {
            return ResultVo.Fail("提现金额必须大于0");
        }
        if (StringUtils.isBlank(remark)) {
            return ResultVo.Fail("请填写备注");
        }
        if (remark.length() > 200) {
            return ResultVo.Fail("备注不能超过200字");
        }
        String lockKey = RedisKey.USER_WITHDRAW_AGENT_ADMIN_REDIS_KEY + id;
        String lockTime = (System.currentTimeMillis() + 300000) + "";
        try {
            String minAmt = Optional.ofNullable(dictionaryService.getByCode("withdraw_agent_min_limit")).map(DictionaryEntity::getDictValue).orElse("0");
            long minAmtLimit = Long.parseLong(minAmt);
            if (amount < minAmtLimit) {
                return ResultVo.Fail("提现金额必须大于"+ MoneyUtil.fen2yuan(new BigDecimal(minAmt)) + "元");
            }
            UserEntity userEntity = userService.findById(currUser.getUserId());
            if (userEntity == null) {
                return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
            }
            boolean lockResult = redisService.lock(lockKey, lockTime, 60);
            if(!lockResult){
                return ResultVo.Fail("代理提现处理中，请稍后再试");
            }
            //校验资金账户
            UserCapitalAccountEntity userCapitalAccountEntity = validateUserCapitalAccountEntity(id, userEntity.getOemCode());
            if (userCapitalAccountEntity.getAvailableAmount() < amount) {
                return ResultVo.Fail("可用余额不足");
            }
            //获取银行账户
            UserBankCardEntity userBankCardEntity = getUserBankCardEntity(userCapitalAccountEntity);
            orderService.agentAdminWithdraw(userCapitalAccountEntity, userBankCardEntity, amount, currUser.getUseraccount(), remark);
            return ResultVo.Success();
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultVo.Fail("代理提现失败");
        } finally {
            redisService.unlock(lockKey, lockTime);
        }
    }

    /**
     * 获取资金账户
     * @param id
     * @param oemCode
     * @return
     */
    private UserCapitalAccountEntity getUserCapitalAccountEntity(Long id, String oemCode) throws BusinessException {
        if(id == null){
            throw new BusinessException("资金账户主键不能为空");
        }
        UserCapitalAccountEntity entity = new UserCapitalAccountEntity();
        entity.setId(id);
        entity.setOemCode(oemCode);
        entity = userCapitalAccountService.selectOne(entity);
        if (entity == null) {
            throw new BusinessException("资金账户不存在");
        }
        return entity;
    }

    /**
     * 校验资金账户
     * @param id
     * @param oemCode
     * @return
     * @throws BusinessException
     */
    private UserCapitalAccountEntity validateUserCapitalAccountEntity(Long id, String oemCode) throws BusinessException {
        UserCapitalAccountEntity entity = getUserCapitalAccountEntity(id, oemCode);
        //判断资金账户状态
        if (Objects.equals(UserCapitalAccountStatusEnum.FORBIDDEN.getValue(), entity.getStatus())) {
            throw new BusinessException("资金账户已被冻结，如有疑问，请联系客服。");
        }
       /* if (!Objects.equals(2, entity.getUserType())) {
            throw new BusinessException("当前操作资金账户不是代理");
        }*/
        //判断用户组织
//        boolean belongSystem = belongSystem(entity.getUserId(), getOrgTree());
//        if (belongSystem) {
//            throw new BusinessException("订单不属于当前登录用户组织");
//        }
        return entity;
    }

    /**
     * 获取银行卡信息
     * @param entity
     * @return
     * @throws BusinessException
     */
    private UserBankCardEntity getUserBankCardEntity(UserCapitalAccountEntity entity) throws BusinessException {
        UserBankCardEntity cardEntity = new UserBankCardEntity();
        cardEntity.setUserId(entity.getUserId());
        cardEntity.setUserType(entity.getUserType());
        cardEntity.setStatus(CardStatusEnum.BIND.getValue());
        cardEntity.setOemCode(entity.getOemCode());
        cardEntity.setBankCardType(BankCardTypeEnum.DEBIT_CARD.getValue());
        List<UserBankCardEntity> lists = userBankCardService.select(cardEntity);
        if (CollectionUtil.isEmpty(lists)) {
            throw new BusinessException("暂无绑定提现银行账户，请联系上级机构添加！");
        }
        if (lists.size() != 1) {
            throw new BusinessException("存在多个银行账户");
        }
        cardEntity = lists.get(0);
        if (StringUtils.isBlank(cardEntity.getBankNumber())) {
            throw new BusinessException("银行卡号为空");
        }
        if (StringUtils.isBlank(cardEntity.getPhone())) {
            throw new BusinessException("银行账户手机号为空");
        }
        return cardEntity;
    }
}
