package com.yuqian.itax.capital.service;

import com.yuqian.itax.capital.dao.UserBankCardMapper;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.dto.BankCardDTO;
import com.yuqian.itax.capital.entity.vo.BankCardVO;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.vo.AgentDetailVO;

import java.util.List;
import java.util.Map;

/**
 * 银行卡管理service
 * 
 * @Date: 2019年12月07日 20:54:44 
 * @author 蒋匿
 */
public interface UserBankCardService extends IBaseService<UserBankCardEntity,UserBankCardMapper> {


    /**
     * 代理商查询银行卡信息
     */
    AgentDetailVO getBankCardInfo(AgentDetailVO agentDetailVO,String oemCode);

    /**
     * 查询银行卡信息
     * @param userId 用户id
     * @param userType 用户类型
     * @param oemCode 机构编码
     * @author HZ
     * @Date 2020/2/12
     * @return
     */
    UserBankCardEntity getBankCardInfoByUserIdAndUserType(Long userId, Integer userType,String oemCode);

    /**
     * 修改银行卡
     */
    UserBankCardEntity updateBankCardEntity(UserBankCardEntity userBankCardEntity, String userAccount, Long id);

    /**
     * @Description 查询用户绑定银行卡列表
     * @Author  Kaven
     * @Date   2019/12/16 11:20
     * @Param  userId oemCode
     * @Return List
     * @Exception BusinessException
    */
    List<BankCardVO> listBankCards(Long userId,String oemCode,Integer walletType) throws BusinessException;

    /**
     * @Description 解绑银行卡
     * @Author  Kaven
     * @Date   2019/12/16 11:42
     * @Param  userId id verifyCode
     * @Exception BusinessException
    */
    void unbind(Long userId,Long id,String verifyCode) throws BusinessException;

    /**
     * @Description 银行卡绑定
     * @Author  Kaven
     * @Date   2019/12/16 13:59
     * @Param   userId 用户ID
     * @Param   name 用户姓名
     * @Param   idCardNo 身份证号码
     * @Param   bankNumber 银行卡号
     * @Param   reserveMobile 预留手机号
     * @Param   verifyCode 验证码
     * @Param   expireDate 身份证有效期
     * @Param   idCardAddr 身份证地址
     * @Exception BusinessException
    */
    void bindCard(BankCardDTO dto);

    /**
     * 根据银行卡号和机构编码获取系统银行号
     * @param bankNumber 银行卡号
     * @param oemCode
     * @return
     */
    String getBankNoByBankAccount(String bankNumber, String oemCode);

    /**
     * 纳呗签约注册查询
     * @param oemCode 机构编码
     * @return Map
     */
    Map<String,Object> nabeiSignQuery(Long userId, String oemCode) throws BusinessException;

    /**
     * 纳呗签约注册
     * @param userId  用户id
     * @param oemCode 机构编码
     * @return
     * */
    void nabeiSignRegister(Long userId, String oemCode) throws BusinessException;

    /**
     * 纳呗签约申请
     * @param userId
     * @param oemCode
     * @return
     */
    Map<String, Object> nabeiH5Sign(Long userId, String oemCode);
}

