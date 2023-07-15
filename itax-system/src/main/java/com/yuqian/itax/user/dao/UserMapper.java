package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.query.AgentQuery;
import com.yuqian.itax.user.entity.query.UserQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统用户dao
 * 
 * @Date: 2019年12月08日 20:51:44 
 * @author 蒋匿
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

	List<UserPageInfoVO> getUserInfoList (UserQuery userQuery);

	List<AgentPageInfoVO> getAgentInfoList (AgentQuery agentQuery);


	List<UserEntity> getPartnerByCityCode(@Param("oemCode") String oemCode, @Param("cityCode")String cityCode);

	List<UserEntity> getCityAgentByCityCode(@Param("oemCode") String oemCode, @Param("cityCode")String cityCode);

	UserEntity getOemAccount(@Param("oemCode") String oemCode);

	UserEntity getParkAccountByOrgId(@Param("orgId") Long orgId);

	UserEntity getParkByParkId(@Param("parkId") Long parkId);


	UserEntity getUserByPlatformTypeAndAccountType(@Param("oemCode") String oemCode,@Param("accountType")Integer accountType ,@Param("platformType")Integer platformType);

	UserEntity getAgentAccount(@Param("oemCode") String oemCode,@Param("platformType") Integer platformType,@Param("orgId")Long orgId);


	List<UserEntity> getUserByUserName(@Param("oemCode") String oemCode ,@Param("username") String username);

	List<UserEntity> getUserByNickname(@Param("oemCode") String oemCode ,@Param("nickname") String username);

	List<CustomerServiceWorkVO> getCustomerServiceByOemCode(@Param("oemCode") String oemCode,@Param("id") Long id);

	UserEntity getOrgAdminAccount(@Param("userId")Long userId);

	UserEntity qeruyUserByAccountAndOemCode(@Param("oemCode")String oemCode,@Param("account")String account);

	UserAndExtendVO qeruyUserByUsernameAndOemCode(@Param("oemCode")String oemCode, @Param("username")String username,@Param("userPhone")String userPhone);
	/**
	 * 根据手机号查询后台用户
	 */
	AccountVO queryAccount(@Param("oemCode")String oemCode, @Param("phone") String phone);

	/**
	 * 根据oemCode account_type platform_type 查询用户
	 * @param oemCode
	 * @param accountType
	 * @param platformType
	 * @return
	 */
	List<UserEntity> queryAccountByOemCodeAndAccountTypeAndPlatformType(@Param("oemCode") String oemCode,@Param("accountType") Integer accountType,@Param("platformType") Integer platformType,@Param("id")Long id);
}

