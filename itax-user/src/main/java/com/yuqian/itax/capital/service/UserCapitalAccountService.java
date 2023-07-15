package com.yuqian.itax.capital.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.po.OemPO;
import com.yuqian.itax.capital.dao.UserCapitalAccountMapper;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.entity.UserCapitalChangeRecordEntity;
import com.yuqian.itax.capital.entity.query.UserCapitalAccountQuery;
import com.yuqian.itax.capital.entity.vo.MemberCapitalAccountApiVO;
import com.yuqian.itax.capital.entity.vo.MemberCapitalAccountVO;
import com.yuqian.itax.capital.entity.vo.UserCapitalAccountVO;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.entity.ParkPO;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.po.AgentPO;
import com.yuqian.itax.user.entity.po.UserPO;

import java.util.Date;
import java.util.List;

/**
 * 用户资金账号表service
 * 
 * @Date: 2019年12月07日 20:54:06 
 * @author 蒋匿
 */
public interface UserCapitalAccountService extends IBaseService<UserCapitalAccountEntity,UserCapitalAccountMapper> {

    /**
     * 分页查询资金账号
     * @author  HZ
     * @Data 2019/12/16
     */
     PageInfo<UserCapitalAccountVO>  queryUserCapitalAccountPageInfo(UserCapitalAccountQuery userCapitalAccountQuery);
    /**
     * 查询资金账号列表
     * @author  HZ
     * @Data 2019/12/16
     */
     List<UserCapitalAccountVO> queryUserCapitalAccountList(UserCapitalAccountQuery userCapitalAccountQuery);

    /**
     * 根据userId查询账号信息
     * @author HZ
     * @date 2019/12/10
     */
     UserCapitalAccountEntity getUserCapitalAccountByUserId(Long userId);

    /**
     * 增加代理商资金账号
     * @author  HZ
     * @date 2019/12/11
     */
     void addAgentCapitalAccount(AgentPO agentPO, String userAccount) throws BusinessException;

    /**
     * 增加OEM机构资金账号
     * @author  HZ
     * @date 2019/12/11
     */
    void addOemCapitalAccount(OemPO oemPO, String userAccount) throws BusinessException;

    /**
     * 增加园区资金账号
     * @author  HZ
     * @date 2019/12/11
     */
    void addParkCapitalAccount(ParkPO parkPO, String userAccount) throws BusinessException;


    /**
     * 增加资金账号
     * @author  HZ
     * @date 2019/12/11
     */
     UserEntity addUserCapitalAccount(UserPO userPO, String userAccount) throws BusinessException;
    /**
     * 添加可用资金
     * @param oemCode
     * @param orderNo
     * @param orderType
     * @param userId
     * @param userType
     * @param amount
     * @param updateUser
     * @param updateTime
     * @param walletType 钱包类型 1-消费钱包 2-佣金钱包
     */
    void addBalance(String oemCode, String orderNo, Integer orderType, Long userId, Integer userType, Long amount, String detailDesc, String updateUser, Date updateTime,Integer walletType) throws BusinessException;

    /** 减除可用资金
     * @param oemCode
     * @param orderNo
     * @param orderType
     * @param userId
     * @param userType
     * @param amount
     * @param updateUser
     * @param updateTime
     * @param walletType 钱包类型 1-消费钱包 2-佣金钱包
     */
    void minusBalance(String oemCode, String orderNo, Integer orderType, Long userId, Integer userType, Long amount, String detailDesc, String updateUser, Date updateTime,Integer walletType) throws BusinessException;

    /**
     * 订单分润
     * @param oemCode
     * @param userId
     * @param userType
     * @param amount
     * @param availableAmount 可用金额
     * @param outstandingAmount 有待结算金额则可用金额不操作，没有待结算金额则操作可用金额
     * @param blockAmount 冻结金额
     * @param updateUser
     * @param updateTime
     * @param isAdd 是否添加资金  1- 添加资金 0-减少资金
     * @param walletType 钱包类型 1-消费钱包 2-佣金钱包
     */
    void addBalanceByProfits(String oemCode, String orderNo, Integer orderType, Long userId, Integer userType, Long amount,Long availableAmount, Long outstandingAmount,Long blockAmount, String detailDesc, String updateUser, Date updateTime,Integer isAdd,Integer walletType);
    /**
     * 查询余额
     * @param query
     * @return
     */
    List<UserCapitalAccountVO> listAccountBalance(UserCapitalAccountQuery query);
    /**
     * 查询平台余额
     * @param query
     * @return
     */
    List<UserCapitalAccountVO> listPlatformAccountBalance(UserCapitalAccountQuery query);

    UserBankCardEntity insertBankCardEntity(UserBankCardEntity userBankCardEntityPO, String userAccount, UserEntity userEntity);

    /**
     * @Description 查询我的钱包金额
     * @Author  Kaven
     * @Date   2019/12/25 11:19
     * @Param  userId oemCode
     * @Return MemberCapitalAccountVO
     * @Exception BusinessException
    */
    MemberCapitalAccountVO getBalance(Long userId, String oemCode);


    /**
     * 用户资金结算（将待结算金额添加到可用金额）
     * @param userCapitalChangeRecordEntity
     * @return
     */
    int updateUserAmount(UserCapitalChangeRecordEntity userCapitalChangeRecordEntity);

    /**
     * 可用余额冻结
     * @param entity
     * @param orderNo
     * @param orderType
     * @param amount
     * @param updateUser
     */
    void freezeBalance(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser);

    /**
     * 可用余额冻结
     * @param entity
     * @param orderNo
     * @param orderType
     * @param amount
     * @param updateUser
     */
    void freezeBalanceByRecharge(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser);

    /**
     * 可用余额解冻
     * @param entity
     * @param orderNo
     * @param orderType
     * @param amount
     * @param updateUser
     */
    void unfreezeBalance(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser);
    /**
     * 可用余额解冻（微信不增加余额）
     * @param entity
     * @param orderNo
     * @param orderType
     * @param amount
     * @param updateUser
     */
    void unfreezeBalanceWetchat(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser);

    /**
     * 根据用户id、钱包类型和用户类型查询资金账户
     * @param userId
     * @param userType
     * @param walletType
     * @param oemCode
     * @return
     */
    UserCapitalAccountEntity queryByUserIdAndType(Long userId, Integer userType, String oemCode,Integer walletType);

    /**
     * 冻结金额支出
     * @param entity
     * @param orderNo
     * @param orderType
     * @param amount
     * @param updateUser
     */
    void delFreezeBalance(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser);

    /**
     * 修改资金账户
     *
     * @param entity
     * @param remark     变更备注
     * @param updateUser
     * @param updateTime
     */
    void updateUserCapitalAccountEntity(UserCapitalAccountEntity entity, String remark, String updateUser, Date updateTime);

    /**
     * @Description 查询我的钱包金额
     * @Author yejian
     * @Date 2020/11/13 11:19
     * @Param userId
     * @Param levelNo
     * @Param oemCode
     * @Return MemberCapitalAccountApiVO
     * @Exception BusinessException
     */
    MemberCapitalAccountApiVO getBalanceApi(Long userId, Integer levelNo, String oemCode) throws BusinessException;

}

