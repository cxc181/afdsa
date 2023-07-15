package com.yuqian.itax.capital.dao;

import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.vo.BankCardVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 银行卡管理dao
 * 
 * @Date: 2019年12月07日 20:54:44 
 * @author 蒋匿
 */
@Mapper
public interface UserBankCardMapper extends BaseMapper<UserBankCardEntity> {

    /**
     * 更具userId获取银行卡信息
     */
    UserBankCardEntity getBankCardInfoByUserIdAndUserType(@Param("userId") Long userId ,@Param("userType")Integer userType, @Param("oemCode") String oemCode);

    /**
     * @Description 获取用户绑定的银行卡列表
     * @Author  Kaven
     * @Date   2019/12/16 11:26
     * @Param  userId oemCode
     * @Return List
    */
    List<BankCardVO> listBankCards(@Param("userId") Long userId ,@Param("oemCode") String oemCode);

    /**
     * 根据银行卡号和机构编码获取系统银行号
     * @param bankNumber 银行卡号
     * @param oemCode
     * @return
     */
    String getBankNoByBankAccount(@Param("bankNumber") String bankNumber ,@Param("oemCode") String oemCode);
}

