package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.po.OemPO;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.entity.ParkPO;
import com.yuqian.itax.user.dao.UserMapper;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.UserRelaEntity;
import com.yuqian.itax.user.entity.po.AgentPO;
import com.yuqian.itax.user.entity.po.AgentUpdatePO;
import com.yuqian.itax.user.entity.po.UserMenuPO;
import com.yuqian.itax.user.entity.po.UserPO;
import com.yuqian.itax.user.entity.query.AgentQuery;
import com.yuqian.itax.user.entity.query.UserQuery;
import com.yuqian.itax.user.entity.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 系统用户service
 * 
 * @Date: 2019年12月08日 20:51:44 
 * @author 蒋匿
 */
public interface UserService extends IBaseService<UserEntity,UserMapper> {
    /**
     * 用户分页
     * @param userQuery
     * @return
     */
    PageInfo<UserPageInfoVO> userPageInfo(UserQuery userQuery);

    /**
     * 代理商列表分页
     * @param agentQuery
     * @return
     */
    PageInfo<AgentPageInfoVO> agentPageInfo(AgentQuery agentQuery);
    /**
     * 代理商详情
     * @param id
     * @return
     */
    AgentDetailVO agentDetail(Long id);

    /**
     * 新增代理商
     */
    UserEntity addAgent(AgentPO agentPO, String userAccount) throws BusinessException;
    /**
     * 新增OEM机构
     */
    UserEntity addOem(OemPO oemPO, String userAccount) throws BusinessException;

    /**
     * 新增园区
     */
    UserEntity addPark(ParkPO parkPO, String userAccount) throws BusinessException;
    /**
     * 新增系统用户
     */
    UserEntity addUser(UserPO userPO,String userAccount) throws BusinessException;

    /**
     * 修改系统用户
     */
    void updateUserEntityById(UserPO userPO , String userAccount) throws BusinessException;
    /**
     * 编辑代理商
     */
    UserEntity updateAgentEntityById(AgentUpdatePO agentPO , String userAccount);

    /**
     * 编辑OEM机构
     */
    UserEntity updateOemtEntityById(OemPO oemPO , String userAccount);
    /**
     * 编辑园区
     */
    UserEntity updateParkPOById (ParkPO  parkPO , String userAccount);
    /**
     * 重置密码
     * @author  hz
     * date 2019/12/16
     */
    UserEntity resetPassword(Long operatorId,String userAccount);
    /**
     * 根据平台类型和用户类型获取用户
     */
    UserEntity getUserByPlatformTypeAndAccountType(String oemCode,Integer accountType ,Integer platformType);

    /**
     * 新增用户关系
     * @param id
     * @param level
     * @param pId
     * @param pLevel
     * @param  type 1-高级合伙人新增城市服务商
     * @return
     */
    UserRelaEntity insertUserRelaEntity(String oemCode,Long id, Integer level, Long pId, Integer pLevel, String userAccount,Integer type,String cityCode);

    /**
     * 判断所在城市是否有高级合伙人
     * @athu HZ
     * @param cityCode
     * @param provinceCode
     * @param oemCode
     * @return
     */
    public List<UserEntity> getPartner (String cityCode, String provinceCode, String oemCode);
    /**
     * 获取客服坐席列表
     */
    List<CustomerServiceWorkVO>  getCustomerServiceByOemCode(String oemCode,Long id);

    /**
     * 用户注销
     */
    UserEntity cancelUser(Long id ,Integer status,String userAccount);

    /**
     * 根据用户id获取用户所在组织得管理员账号
     */
    UserEntity getOrgAdminAccount(Long userId);
    /**
     * 代理商注销
     */
    UserEntity cancelAgent(Long id ,Integer status,String userAccount);

    UserEntity qeruyUserByAccountAndOemCode (String oemCode ,String account);

    UserAndExtendVO qeruyUserByUsernameAndOemCode (String oemCode ,String username,String userPhone);

    /**
     * 用户登陆
     */
    Map<String,String> login(String oemCode, String username , String pwd,String vCode);

    /**
     * 用户发送短信登陆验证
     */
    boolean loginSms(String oemCode, String username , String pwd);

    UserEntity getOemAccount (String oemCode);


    UserEntity getParkAccount (Long parkId);

    /**
     * 根据账号查询用户
     * @param userName
     * @param oemCode
     */
    UserEntity getUserByUserName(String userName, String oemCode);


    SysUserVO findSysUserVOByUserId(Long userId);

    void updateUserMenuRelayEntity(UserMenuPO userMenuPO, Long userId);


    /**
     * 根据手机号查询后台用户（合伙人）
     */
    AccountVO queryAccount(String oemCode,String phone);

    /**
     * 根据oemCode account_type platform_type 查询用户
     * @param oemCode
     * @param accountType
     * @param platformType
     * @return
     */
    List<UserEntity> queryAccountByOemCodeAndAccountTypeAndPlatformType(String oemCode,Integer accountType,Integer platformType,Long id);


}

