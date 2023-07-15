package com.yuqian.itax.capital.dao;

import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.entity.dto.UserCapitalAccountDTO;
import com.yuqian.itax.capital.entity.vo.ProfitDetailVO;
import com.yuqian.itax.capital.entity.vo.UserCapitalAccountVO;
import com.yuqian.itax.capital.entity.query.UserCapitalAccountQuery;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 用户资金账号表dao
 *
 * @Date: 2019年12月07日 20:54:06
 * @author 蒋匿
 */
@Mapper
public interface UserCapitalAccountMapper extends BaseMapper<UserCapitalAccountEntity> {

    List <UserCapitalAccountVO> queryUserCapitalAccountList(UserCapitalAccountQuery userCapitalAccountQuery);


    UserCapitalAccountEntity getUserCapitalAccountByUserId(@Param("userId")Long userId);

    /**
     * 查询用户资金
     * @param userId
     * @param userType 用户类型 1-会员 2-系统用户
     * @param oemCode  机构编码
     */
	UserCapitalAccountEntity queryByUserIdAndType(@Param("userId")Long userId, @Param("userType")Integer userType,@Param("oemCode")String oemCode,
                                                  @Param("walletType")Integer walletType);

    /**
     * 添加可用资金
     * @param userId
     * @param userType
     * @param amount
     * @param updateUser
     * @param updateTime
     */
    void addBalance(@Param("userId")Long userId, @Param("userType")Integer userType,@Param("walletType")Integer walletType,
                    @Param("amount")Long amount, @Param("updateUser")String updateUser, @Param("updateTime")Date updateTime);

    /**
     * 资金变动
     * @param userId
     * @param userType
     * @param amount
     * @param availableAmount  可用金额
     * @param outstandingAmount 有待结算金额则可用金额不操作，没有待结算金额则操作可用金额
     * @param blockAmount  冻结金额
     * @param updateUser
     * @param updateTime
     * @param isAdd
     * @param oemCode
     * @param walletType 钱包类型
     */
    void addBalanceByProfits(@Param("userId")Long userId, @Param("userType")Integer userType,
                    @Param("amount")Long amount, @Param("availableAmount") Long availableAmount,@Param("outstandingAmount") Long outstandingAmount,
                    @Param("blockAmount") Long blockAmount, @Param("updateUser")String updateUser, @Param("updateTime")Date updateTime,
                    @Param("isAdd")Integer isAdd,@Param("oemCode")String oemCode,@Param("walletType") Integer walletType);

    /**
     * 查询余额
     * @param query
     * @return
     */
    List<UserCapitalAccountVO> listAccountBalance(UserCapitalAccountQuery query);

    /**
     * 资金变动
     */
    void updateAmount(UserCapitalAccountDTO dto);

    /**
     * @Description 资金账户出账
     * @Author  Kaven
     * @Date   2020/1/7 11:42
     * @Param
     * @Return
     * @Exception
    */
    void minusBalance(@Param("userId")Long userId, @Param("userType")Integer userType,@Param("walletType")Integer walletType,
                      @Param("amount")Long amount, @Param("updateUser")String updateUser, @Param("updateTime")Date updateTime);

    /**
     * 查询平台余额
     * @param query
     * @return
     */
    List<UserCapitalAccountVO> listPlatformAccountBalance(UserCapitalAccountQuery query);

    /**
     * 获取佣金提现分润列表
     * @param userId
     * @param oemCode
     * @return
     */
    List<ProfitDetailVO> getProfitDetailListForWithdraw(@Param("userId") Long userId, @Param("oemCode") String oemCode);
}

