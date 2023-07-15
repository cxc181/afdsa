package com.yuqian.itax.profits.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.entity.UserCapitalChangeRecordEntity;
import com.yuqian.itax.capital.entity.vo.ProfitDetailVO;
import com.yuqian.itax.capital.enums.CapitalChangeTypeEnum;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.nabei.entity.AchievementExcelVo;
import com.yuqian.itax.order.entity.MemberOrderRelaEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.*;
import com.yuqian.itax.order.service.MemberOrderRelaService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.profits.dao.ProfitsDetailMapper;
import com.yuqian.itax.profits.entity.ProfitsDetailEntity;
import com.yuqian.itax.profits.entity.query.MemberProfitsGateWayQuery;
import com.yuqian.itax.profits.entity.query.MemberProfitsQuery;
import com.yuqian.itax.profits.entity.query.ProfitsDetailQuery;
import com.yuqian.itax.profits.entity.vo.MemberOrUserProfitsVO;
import com.yuqian.itax.profits.entity.vo.MemberProfitsRecordVO;
import com.yuqian.itax.profits.entity.vo.MemberProfitsVO;
import com.yuqian.itax.profits.entity.vo.ProfitsDetailVO;
import com.yuqian.itax.profits.enums.ProfitsDetailStatusEnum;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.enums.MemberLevelEnum;
import com.yuqian.itax.user.enums.UserTypeEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.OrderNoFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Service("profitsDetailService")
public class ProfitsDetailServiceImpl extends BaseServiceImpl<ProfitsDetailEntity,ProfitsDetailMapper> implements ProfitsDetailService {
    @Resource
    private ProfitsDetailMapper profitsDetailMapper;

    @Autowired
    private UserCapitalAccountService userCapitalAccountService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberOrderRelaService memberOrderRelaService;

    @Autowired
    private MemberAccountService memberAccountService;

    @Autowired
    private UserService userService;

    @Autowired
    private OemService oemService;

    @Override
    public PageInfo<ProfitsDetailVO> profitsDetailPageInfo(ProfitsDetailQuery profitsDetailQuery) {
        PageHelper.startPage(profitsDetailQuery.getPageNumber(),profitsDetailQuery.getPageSize());
    return  new PageInfo<>(this.mapper.profitsDetailList(profitsDetailQuery)) ;
    }

    @Override
    public List<ProfitsDetailVO> profitsDetailList(ProfitsDetailQuery profitsDetailQuery) {
        return this.mapper.profitsDetailList(profitsDetailQuery);
    }

    @Override
    public PageInfo<MemberProfitsVO> queryMemberProfitsList(MemberProfitsQuery query) throws BusinessException {
        // 目前仅支持查找3个月跨度的账单
        /*Date startDate = DateUtil.parseDefaultDate(query.getStartDate());
        Date endDate = DateUtil.parseDefaultDate(query.getEndDate());
        if(DateUtil.differentDays(startDate,endDate) > 90){
            throw new BusinessException("目前仅支持查找3个月跨度的账单，请重新选择");
        }*/
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<MemberProfitsVO> list = this.profitsDetailMapper.listMemberProfitsPage(query);
        return new PageInfo<MemberProfitsVO>(list);
    }

    /**
     * 根据条件查询分润明细数据
     * @param parmas
     * @return
     */
    @Override
    public List<ProfitsDetailEntity> findProfitsDetailByParams(Map<String,Object> parmas){
        return mapper.findProfitsDetailByParams(parmas);
    }

    /**
     * 分润结算
     * @param entity
     * @return
     */
    @Override
    @Transactional
    public int updateProfitsDetailStatus(ProfitsDetailEntity entity,String updateUser){
        //修改分润明细数据状态
        entity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
        if(entity.getProfitsAmount()>1) {
            UserCapitalChangeRecordEntity record = new UserCapitalChangeRecordEntity();
            record.setAddTime(new Date());
            record.setUpdateUser(updateUser);
            record.setChangesAmount(entity.getProfitsAmount());
            record.setOemCode(entity.getOemCode());
            record.setOrderNo(entity.getOrderNo());
            record.setOrderType(entity.getOrderType());
            record.setUserId(entity.getUserId());
            Integer userType = 1;
            if(entity.getUserType() != 1){
                userType = 2;
                record.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
            }else{
                record.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());
            }
            record.setUserType(userType);
            record.setChangesType(CapitalChangeTypeEnum.INCOME.getValue());
            record.setDetailDesc("订单[" + entity.getOrderNo() + "]分润结算");
            int result = userCapitalAccountService.updateUserAmount(record);
            if(result == 2){
                entity.setRemark("资金账户不可用");
                entity.setProfitsStatus(ProfitsDetailStatusEnum.CANCELLED.getValue());
            }
        }
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(updateUser);
        return editByIdSelective(entity);
    }

    /**
     * 根据订单号保存分润明细
     * @param orderNo
     * @param updateAccount
     * @return
     */
    @Override
    @SuppressWarnings("all")
    @Transactional(propagation = Propagation.NESTED)
    public boolean saveProfitsDetailByOrderNo(String orderNo,String updateAccount) throws BusinessException{
        log.info("订单[" + orderNo + "开始分润]");
        OrderEntity entity = orderService.queryByOrderNo(orderNo);
        OemEntity oemEntity = new OemEntity();
        oemEntity.setOemCode(entity.getOemCode());
        oemEntity = oemService.selectOne(oemEntity);

        //oem机构 使用方式为纯api方式的请求 不分润
        if (oemEntity != null && "4".equals(oemEntity.getUseWay())) {
            return true;
        }
        //工商注册已签收和开票订单已完成
        if ((OrderTypeEnum.REGISTER.getValue().equals(entity.getOrderType()) && RegOrderStatusEnum.COMPLETED.getValue().equals(entity.getOrderStatus()))
                || OrderTypeEnum.UPGRADE.getValue().equals(entity.getOrderType())
                || (OrderTypeEnum.CUSTODY_FEE_RENEWAL.getValue().equals(entity.getOrderType()) && ContOrderStatusEnum.COMPLETED.getValue().equals(entity.getOrderStatus()))
                || (OrderTypeEnum.INVOICE.getValue().equals(entity.getOrderType()) && InvoiceOrderStatusEnum.SIGNED.getValue().equals(entity.getOrderStatus()))
                || (OrderTypeEnum.CANCELLATION.getValue().equals(entity.getOrderType()) && CompCancelOrderStatusEnum.COMPANY_CANCEL_COMPLETED.getValue().equals(entity.getOrderStatus()))) {
            //查询会员订单关系
            MemberOrderRelaEntity memberOrderRelaEntity = new MemberOrderRelaEntity();
            memberOrderRelaEntity.setOemCode(entity.getOemCode());
            memberOrderRelaEntity.setId(entity.getRelaId());
            List<MemberOrderRelaEntity> list = memberOrderRelaService.select(memberOrderRelaEntity);
            ProfitsDetailEntity profitsDetailEntity=null;
            //获取机构的总分润出账金额
            Long totalAmount = 0L;
            //保存分润明细数据
            for(MemberOrderRelaEntity vo : list){
                //判断用户是否可用，不可用则不添加数据
                //一级推广
                if(vo.getAccountFirstId() != null) {
                    MemberAccountEntity memberAccountEntity = memberAccountService.findById(vo.getAccountFirstId());
                    if (memberAccountEntity != null) {
                        profitsDetailEntity = setProfitsDetailEntity(entity, memberAccountEntity, vo.getLevelFirst(), updateAccount,
                                vo.getOemName(), vo.getLevelFirstProfitsRate(), new ProfitsDetailEntity());
                        if(profitsDetailEntity != null   && (2 != memberAccountEntity.getStatus().intValue() || MemberLevelEnum.MEMBER.getValue().equals(vo.getLevelFirst()))) {
                            if ((MemberLevelEnum.BRONZE.getValue().equals(vo.getLevelFirst())
                                    || MemberLevelEnum.GOLD.getValue().equals(vo.getLevelFirst()))
                                    && profitsDetailEntity.getProfitsAmount() > 0 ) { //如果是VIP到铂金城市合伙人则直接进行分润
                                profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                                userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                        profitsDetailEntity.getProfitsAmount(), 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                totalAmount += profitsDetailEntity.getProfitsAmount();
                            } else if ((MemberLevelEnum.DIAMOND.getValue().equals(vo.getLevelFirst())
                                    || MemberLevelEnum.MEMBER.getValue().equals(vo.getLevelFirst())) && profitsDetailEntity.getProfitsAmount() > 0) {
                                if (oemEntity.getSettlementType() == 3) { //实时计算
                                    profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                            profitsDetailEntity.getProfitsAmount(), 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                } else {
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                            0L, profitsDetailEntity.getProfitsAmount(), 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                }
                                totalAmount += profitsDetailEntity.getProfitsAmount();
                            } else {
                                profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                            }
                            profitsDetailEntity.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());
                            mapper.insertSelective(profitsDetailEntity);
                        }
                    }
                }
                //二级推广
                if(vo.getAccountTwoId()!= null ) {
                    MemberAccountEntity memberAccountEntity = memberAccountService.findById(vo.getAccountTwoId());
                    if (memberAccountEntity != null) {
                        profitsDetailEntity = setProfitsDetailEntity(entity, memberAccountEntity, vo.getLevelTwo(), updateAccount,
                                vo.getOemName(), vo.getLevelTwoProfitsRate(), new ProfitsDetailEntity());
                        if(profitsDetailEntity != null   && (2 != memberAccountEntity.getStatus().intValue() || MemberLevelEnum.MEMBER.getValue().equals(vo.getLevelTwo()))) {
                            if ((MemberLevelEnum.BRONZE.getValue().equals(vo.getLevelTwo())
                                    || MemberLevelEnum.GOLD.getValue().equals(vo.getLevelTwo()))
                                    && profitsDetailEntity.getProfitsAmount() > 0) { //如果是税务顾问则直接进行分润
                                profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                                userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                        profitsDetailEntity.getProfitsAmount(), 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                totalAmount += profitsDetailEntity.getProfitsAmount();
                            } else if ((MemberLevelEnum.DIAMOND.getValue().equals(vo.getLevelTwo())
                                    || MemberLevelEnum.MEMBER.getValue().equals(vo.getLevelTwo()))
                                    && profitsDetailEntity.getProfitsAmount() > 0) {
                                if (oemEntity.getSettlementType() == 3) { //实时计算
                                    profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                            profitsDetailEntity.getProfitsAmount(), 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                } else {
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                            0L, profitsDetailEntity.getProfitsAmount(), 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                }
                                totalAmount += profitsDetailEntity.getProfitsAmount();
                            } else {
                                profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                            }
                            profitsDetailEntity.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());
                            mapper.insertSelective(profitsDetailEntity);
                        }
                    }
                }
                //三级推广
                if(vo.getAccountThreeId()!=null ) {
                    MemberAccountEntity memberAccountEntity = memberAccountService.findById(vo.getAccountThreeId());
                    if (memberAccountEntity != null) {
                        profitsDetailEntity = setProfitsDetailEntity(entity, memberAccountEntity, vo.getLevelThree(), updateAccount,
                                vo.getOemName(), vo.getLevelThreeProfitsRate(), new ProfitsDetailEntity());
                        if(profitsDetailEntity != null  && (2 != memberAccountEntity.getStatus().intValue() || MemberLevelEnum.MEMBER.getValue().equals(vo.getLevelThree()))) {
                            if ((MemberLevelEnum.BRONZE.getValue().equals(vo.getLevelThree())
                                    || MemberLevelEnum.GOLD.getValue().equals(vo.getLevelThree()))
                                    && profitsDetailEntity.getProfitsAmount() > 0) { //如果是税务顾问会员则直接进行分润
                                profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                                userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                        profitsDetailEntity.getProfitsAmount(), 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                totalAmount += profitsDetailEntity.getProfitsAmount();
                            } else if ((MemberLevelEnum.DIAMOND.getValue().equals(vo.getLevelThree())
                                    || MemberLevelEnum.MEMBER.getValue().equals(vo.getLevelThree()))
                                    && profitsDetailEntity.getProfitsAmount() > 0) {
                                if (oemEntity.getSettlementType() == 3) { //实时计算
                                    profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                            profitsDetailEntity.getProfitsAmount(), 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                } else {
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                            0L, profitsDetailEntity.getProfitsAmount(), 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                }
                                totalAmount += profitsDetailEntity.getProfitsAmount();
                            } else {
                                profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                            }
                            profitsDetailEntity.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());
                            mapper.insertSelective(profitsDetailEntity);
                        }
                    }
                }
                //四级推广
                if(vo.getAccountFourId()!= null) {
                    MemberAccountEntity memberAccountEntity = memberAccountService.findById(vo.getAccountFourId());
                    if (memberAccountEntity != null) {
                        profitsDetailEntity = setProfitsDetailEntity(entity, memberAccountEntity, vo.getLevelFour(), updateAccount,
                                vo.getOemName(), vo.getLevelFourProfitsRate(), new ProfitsDetailEntity());
                        if(profitsDetailEntity != null  && (2 != memberAccountEntity.getStatus().intValue() || MemberLevelEnum.MEMBER.getValue().equals(vo.getLevelFour()))) {
                            if ((MemberLevelEnum.BRONZE.getValue().equals(vo.getLevelFour())
                                    || MemberLevelEnum.GOLD.getValue().equals(vo.getLevelFour()))
                                    && profitsDetailEntity.getProfitsAmount() > 0) { //如果是税务顾问会员则直接进行分润
                                profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                                userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                        profitsDetailEntity.getProfitsAmount(), 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                totalAmount += profitsDetailEntity.getProfitsAmount();
                            } else if ((MemberLevelEnum.DIAMOND.getValue().equals(vo.getLevelFour())
                                    || MemberLevelEnum.MEMBER.getValue().equals(vo.getLevelFour()))
                                    && profitsDetailEntity.getProfitsAmount() > 0) {
                                if (oemEntity.getSettlementType() == 3) { //实时计算
                                    profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                            profitsDetailEntity.getProfitsAmount(), 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                } else {
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), profitsDetailEntity.getUserId(), 1, profitsDetailEntity.getProfitsAmount(),
                                            0L, profitsDetailEntity.getProfitsAmount(), 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.COMMISSION_WALLET.getValue());
                                }
                                totalAmount += profitsDetailEntity.getProfitsAmount();
                            } else {
                                profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                            }
                            profitsDetailEntity.setWalletType(WalletTypeEnum.COMMISSION_WALLET.getValue());
                            mapper.insertSelective(profitsDetailEntity);
                        }
                    }
                }
                //城市合伙人
                if(vo.getCityProvidersId()!= null) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setOemCode(entity.getOemCode());
                    userEntity.setId(vo.getCityProvidersId());
                    userEntity = userService.selectOne(userEntity);
                    if (userEntity!=null && userEntity.getStatus()!= null && userEntity.getStatus() != 2) {
                        profitsDetailEntity = setProfitsDetailEntity(entity, null, null, updateAccount,
                                vo.getOemName(), vo.getCityProvidersProfitsRate(), new ProfitsDetailEntity());
                        if(profitsDetailEntity != null) {
                            profitsDetailEntity.setProfitsNo(OrderNoFactory.getProfitsNo(userEntity.getId()));
                            profitsDetailEntity.setUserId(userEntity.getId());
                            profitsDetailEntity.setUserAccount(userEntity.getUsername());
                            profitsDetailEntity.setUserType(UserTypeEnum.SERVER.getValue());
                            if (profitsDetailEntity.getProfitsAmount() > 0) { //如果是税务顾问则直接进行分润
                                if (oemEntity.getSettlementType() == 3) { //实时计算
                                    profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), userEntity.getId(), 2, profitsDetailEntity.getProfitsAmount(),
                                            profitsDetailEntity.getProfitsAmount(), 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.CONSUMER_WALLET.getValue());
                                } else {
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), userEntity.getId(), 2, profitsDetailEntity.getProfitsAmount(),
                                            0L, profitsDetailEntity.getProfitsAmount(), 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.CONSUMER_WALLET.getValue());
                                }
                                totalAmount += profitsDetailEntity.getProfitsAmount();
                            } else {
                                profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                            }
                            profitsDetailEntity.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
                            mapper.insertSelective(profitsDetailEntity);
                        }
                    }
                }
                //高级合伙人
                if(vo.getCityPartnerId() != null) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setOemCode(entity.getOemCode());
                    userEntity.setId(vo.getCityPartnerId());
                    userEntity = userService.selectOne(userEntity);
                    if (userEntity!=null &&  userEntity.getStatus()!= null && userEntity.getStatus() != 2) {
                        profitsDetailEntity = setProfitsDetailEntity(entity, null, null, updateAccount,
                                vo.getOemName(), vo.getCityPartnerProfitsRate(), new ProfitsDetailEntity());
                        if(profitsDetailEntity != null) {
                            profitsDetailEntity.setProfitsNo(OrderNoFactory.getProfitsNo(userEntity.getId()));
                            profitsDetailEntity.setUserId(userEntity.getId());
                            profitsDetailEntity.setUserAccount(userEntity.getUsername());
                            profitsDetailEntity.setUserType(UserTypeEnum.PARTENER.getValue());
                            if (profitsDetailEntity.getProfitsAmount() > 0) { //如果是税务顾问则直接进行分润
                                if (oemEntity.getSettlementType() == 3) { //实时计算
                                    profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), userEntity.getId(), 2, profitsDetailEntity.getProfitsAmount(),
                                            profitsDetailEntity.getProfitsAmount(), 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.CONSUMER_WALLET.getValue());
                                } else {
                                    userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), userEntity.getId(), 2, profitsDetailEntity.getProfitsAmount(),
                                            0L, profitsDetailEntity.getProfitsAmount(), 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.CONSUMER_WALLET.getValue());
                                }
                                totalAmount += profitsDetailEntity.getProfitsAmount();
                            } else {
                                profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                            }
                            profitsDetailEntity.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
                            mapper.insertSelective(profitsDetailEntity);
                        }
                    }
                }
                //平台账号
                if(vo.getPlatformAccountId() != null ) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(vo.getPlatformAccountId());
                    userEntity = userService.selectOne(userEntity);
                    if (userEntity!=null &&  userEntity.getStatus()!= null && userEntity.getStatus() != 2) {
                        profitsDetailEntity = setProfitsDetailEntity(entity, null, null, updateAccount,
                                vo.getOemName(), vo.getPlatformAccountProfitsRate(), new ProfitsDetailEntity());
                        if(profitsDetailEntity != null) {
                            profitsDetailEntity.setProfitsNo(OrderNoFactory.getProfitsNo(userEntity.getId()));
                            profitsDetailEntity.setUserId(userEntity.getId());
                            profitsDetailEntity.setUserAccount(userEntity.getUsername());
                            profitsDetailEntity.setUserType(UserTypeEnum.PALTFORM.getValue());
                            if (profitsDetailEntity.getProfitsAmount() > 0) {
                                userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), userEntity.getId(), 2, profitsDetailEntity.getProfitsAmount(),
                                        profitsDetailEntity.getProfitsAmount(), 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 1, WalletTypeEnum.CONSUMER_WALLET.getValue());
                                totalAmount += profitsDetailEntity.getProfitsAmount();
                            }
                            profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT.getValue());
                            profitsDetailEntity.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
                            mapper.insertSelective(profitsDetailEntity);
                        }
                    }
                }
            }
            //机构资金账号变动
            //根据机构编码获取机构管理员资金账号
            if(totalAmount>0) {
                UserEntity userEntity = new UserEntity();
                userEntity.setPlatformType(2);
                userEntity.setAccountType(1);
                userEntity.setOemCode(entity.getOemCode());
                userEntity = userService.selectOne(userEntity);
                userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), userEntity.getId(), 2, totalAmount,
                        totalAmount, 0L, 0L, "订单[" + entity.getOrderNo() + "分润]", updateAccount, new Date(), 0,WalletTypeEnum.CONSUMER_WALLET.getValue());
            }
            entity.setProfitStatus(2);
            entity.setIsShareProfit(1);
            entity.setRemark("");
            entity.setUpdateTime(new Date());
            entity.setUpdateUser("admin");
            orderService.editByIdSelective(entity);
        }
        log.info("订单["+orderNo+"分润完成]");
        return true;
    }

    @Override
    public MemberProfitsRecordVO queryMemberProfitsListNew(MemberProfitsQuery query) throws BusinessException {
        // --------------------------------------------处理查询条件------------------------------------------------------
        String day = "";
        String month = "";
        String startDate = "";
        String endDate = "";
        if (StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isNotBlank(query.getEndDate())) { // 按开始和结束时间查询
            startDate = query.getStartDate() + " 00:00:00";
            endDate = query.getEndDate() + " 23:59:59";
        } else if (StringUtils.isNotBlank(query.getMonth())) { // 按月查询
            month = query.getMonth();
        } else if (StringUtils.isNotBlank(query.getStartDate())) {// 开始时间
            day = query.getStartDate();
        } else if (StringUtils.isNotBlank(query.getEndDate())) {// 结束时间
            day = query.getEndDate();
        }

        // 查询会员分润记录
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<MemberProfitsVO> list = this.profitsDetailMapper.listMemberProfitsPageNew(query.getUserId(), query.getOemCode(), query.getProfitsType(), day, month, startDate, endDate, query.getLevelNo());
        MemberProfitsRecordVO profitsRecord = new MemberProfitsRecordVO();
        // 查询总分润
        Long totalProfitsAmount = this.profitsDetailMapper.queryTotalProfitsAmount(query.getUserId(), query.getOemCode(), query.getProfitsType(), day, month, startDate, endDate, query.getLevelNo());
        profitsRecord.setTotalProfitsAmount(totalProfitsAmount);
        profitsRecord.setProfitsPageData(new PageInfo<MemberProfitsVO>(list));
        return profitsRecord;
    }

    @Override
    public MemberOrUserProfitsVO queryEarningsByUserId(MemberProfitsGateWayQuery query) {
        if(query.getUserType()==0 ||query.getUserType()==1){
            query.setUserType(1);
        }else{
            query.setUserType(2);
        }
        MemberOrUserProfitsVO memberOrUserProfitsVO=new MemberOrUserProfitsVO();
        DateTime dateTime = DateTime.now().minusDays(1);
        query.setStartDate(DateUtil.formatTimesTampDate(dateTime.withTimeAtStartOfDay().toDate()));
        query.setEndDate(DateUtil.formatTimesTampDate(dateTime.millisOfDay().withMaximumValue().toDate()));
        Long yesterdayProfits= mapper.queryEarningsByUserId(query);
        query.setStartDate(DateUtil.getMonFirstDay()+" 00:00:00");
        query.setEndDate( DateUtil.getMonLastDay()+" 23:59:59");
        Long monthProfits= mapper.queryEarningsByUserId(query);
        query.setStartDate(null);
        query.setEndDate(null);
        Long totalProfits= mapper.queryEarningsByUserId(query);
        memberOrUserProfitsVO.setYesterdayProfits(yesterdayProfits);
        memberOrUserProfitsVO.setMonthProfits(monthProfits);
        memberOrUserProfitsVO.setTotalProfits(totalProfits);
        return memberOrUserProfitsVO;
    }

    @Override
    public void updateMemberAccountByMemberId(String memberAccount, String remark, Long memberId) {
        mapper.updateMemberAccountByMemberId(memberAccount,remark,memberId);
    }

    @Override
    public PageResultVo profitsDetailForWithdraw(Long memberId, String oemCode, Integer pageSize, Integer pageNumber) {
        PageHelper.startPage(null == pageNumber ? 1 : pageNumber, null == pageSize ? 10 : pageSize);
        List<ProfitDetailVO> list = mapper.profitsDetailForWithdraw(memberId, oemCode, null, null);
        return PageResultVo.restPage(list);
    }

    @Override
    public Long countProfitDetailAmount(Long memberId, String oemCode, Long maximalProfitDetailId, List<String> profitDetailIdList) {
        List<ProfitDetailVO> profitDetailVOS = mapper.profitsDetailForWithdraw(memberId, oemCode, maximalProfitDetailId, profitDetailIdList);
        if (CollectionUtil.isEmpty(profitDetailVOS)) {
            return 0L;
        }
        return profitDetailVOS.stream().mapToLong(ProfitDetailVO::getProfitsAmount).sum();
    }

    @Override
    public void batchUpdateProfitsDetail(Long memberId, String oemCode, Long maximalProfitDetailId, List<String> profitDetailIdList, String withdrawOrderNo) {
        mapper.batchUpdateProfitsDetail(memberId, oemCode, maximalProfitDetailId, profitDetailIdList, withdrawOrderNo);
    }

    @Override
    public List<AchievementExcelVo> getByWithdrawOrderNo(String orderNo) {
        return mapper.getByWithdrawOrderNo(orderNo);
    }

    /**
     * 设置分润明细实体数据
     * @param entity
     * @param memberAccountEntity
     * @param userLevel
     * @param addAccount
     * @param oemName
     * @param profitsRate
     * @param profitsDetailEntity
     * @return
     */
    private ProfitsDetailEntity setProfitsDetailEntity(OrderEntity entity,MemberAccountEntity memberAccountEntity,Integer userLevel,String addAccount,String oemName,BigDecimal profitsRate,
                                                       ProfitsDetailEntity profitsDetailEntity){
        profitsDetailEntity.setOrderNo(entity.getOrderNo());
        profitsDetailEntity.setOrderAmount(entity.getOrderAmount());
        profitsDetailEntity.setPayAmount(entity.getPayAmount());
        profitsDetailEntity.setAvailableProfitsAmount(entity.getProfitAmount());
        if(OrderTypeEnum.UPGRADE.getValue().equals(entity.getOrderType() ) ){  //会员升级
            profitsDetailEntity.setOrderType(1);
        }else if(OrderTypeEnum.REGISTER.getValue().equals(entity.getOrderType())){ //工商注册
            profitsDetailEntity.setOrderType(2);
        }else if(OrderTypeEnum.INVOICE.getValue().equals(entity.getOrderType())){ //开票
            profitsDetailEntity.setOrderType(3);
        }else if(OrderTypeEnum.CANCELLATION.getValue().equals(entity.getOrderType())){ //工商注销
            profitsDetailEntity.setOrderType(4);
        }else if(OrderTypeEnum.CUSTODY_FEE_RENEWAL.getValue().equals(entity.getOrderType())){ //托管费续费订单
            profitsDetailEntity.setOrderType(6);
        }
        if(memberAccountEntity != null) {
            if(userLevel == null){
                return null;
            }
            if(userLevel == -1 ){ //如果是员工，则取员工上级的城市合伙人用户作为分润对象
                //设置所属员工信息
                profitsDetailEntity.setAttributionEmployeesId(memberAccountEntity.getId());
                profitsDetailEntity.setAttributionEmployeesAccount(memberAccountEntity.getMemberAccount());
                if(memberAccountEntity.getParentMemberId() == null){
                    return null;
                }
                MemberAccountEntity memberAccountEntity1 =  new MemberAccountEntity();
                memberAccountEntity1.setId(memberAccountEntity.getParentMemberId());
                memberAccountEntity1.setOemCode(memberAccountEntity.getOemCode());
                memberAccountEntity = memberAccountService.selectOne(memberAccountEntity1);
                if(memberAccountEntity.getStatus().intValue() == 2){
                    return null;
                }
                userLevel = MemberLevelEnum.DIAMOND.getValue();
            }
            profitsDetailEntity.setProfitsNo(OrderNoFactory.getProfitsNo(memberAccountEntity.getId()));
            profitsDetailEntity.setUserId(memberAccountEntity.getId());
            profitsDetailEntity.setUserAccount(memberAccountEntity.getMemberAccount());
            profitsDetailEntity.setUserType(1);
            profitsDetailEntity.setUserLevel(userLevel);
        }

        if(profitsRate!= null && profitsRate.compareTo(new BigDecimal(0))>0) {
            profitsDetailEntity.setProfitsRate(profitsRate);
            profitsRate = profitsRate.divide(new BigDecimal("100"));
            profitsDetailEntity.setProfitsAmount(profitsRate.multiply(new BigDecimal(entity.getProfitAmount())).setScale(0, BigDecimal.ROUND_DOWN).longValue());
        }else{
            profitsDetailEntity.setProfitsAmount(0L);
            profitsDetailEntity.setProfitsRate(new BigDecimal("0.0"));
        }
        profitsDetailEntity.setOemCode(entity.getOemCode());
        profitsDetailEntity.setOemName(oemName);
        profitsDetailEntity.setProfitsStatus(ProfitsDetailStatusEnum.PROFITS_SETTLEMENT_WAIT.getValue());
        profitsDetailEntity.setProfitsTime(new Date());
        profitsDetailEntity.setAddTime(new Date());
        profitsDetailEntity.setAddUser(addAccount);
        profitsDetailEntity.setProfitsType(profitsDetailEntity.getOrderType());
        // 分润金额为0 ，不保存分润记录 add ni.jiang v3.3
        if(profitsDetailEntity.getProfitsAmount()<=0){
            return null;
        }
        return profitsDetailEntity;
    }
}

