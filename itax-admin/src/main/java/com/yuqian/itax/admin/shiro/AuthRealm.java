package com.yuqian.itax.admin.shiro;

import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.roles.entity.UserRoleRelaEntity;
import com.yuqian.itax.roles.service.UserRoleRelaService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.util.util.MemberPsdUtil;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * shiro 认证realm
 * 
 * @author LiuXianTing
 */
public class AuthRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;
	@Autowired
	private OemService oemService;
	@Autowired
	ParkService parkService;


	@Autowired
	private UserRoleRelaService userRoleRelaService;


	/**
	 * 权限key
	 */
	private static final String PERMISSION_KEYS = "permissionKeys";

	/**
	 * 登录认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
			throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;// 获取用户输入的token
		String useraccount = token.getUsername();
		//通过用户名获取用户信息
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername(useraccount);
		userEntity = userService.selectOne(userEntity);
		if (null == userEntity) {
			throw new AccountException("账号不存在");
		}
		if(userEntity.getPlatformType()==2&& userEntity.getOemCode()!=null){
			OemEntity oemEntity=oemService.getOem(userEntity.getOemCode());
			if(oemEntity.getOemStatus()!=1){
				throw new AccountException("机构已经禁用，不允许登陆");
			}
		}
		if(userEntity.getPlatformType()==3 && userEntity.getParkId()!=null){
			ParkEntity parkEntity=parkService.findById(userEntity.getParkId());
			if(null == parkEntity){
				throw new AccountException("园区不存在");
			}
			if(parkEntity.getStatus()!=1){
				throw new AccountException("园区已经禁用，不允许登陆");
			}
		}

		String pwd = String.valueOf(token.getPassword());
		pwd= MemberPsdUtil.encrypt(pwd, userEntity.getUsername(),userEntity.getSlat());

		if (!userEntity.getPassword().equals(pwd) ) {
			throw new AccountException("账号或密码错误");
		}else if(2 == userEntity.getStatus()){
			throw new AccountException("账号已被锁定，请联系管理员");
		}else if(0 == userEntity.getStatus()){
			throw new AccountException("账号已被禁用，请联系管理员");
		}
		token.setPassword(pwd.toCharArray());

		UserRoleRelaEntity sysUserRoleEntity = new UserRoleRelaEntity();
		sysUserRoleEntity.setUserId(sysUserRoleEntity.getId());
		List<UserRoleRelaEntity> roles = userRoleRelaService.select(sysUserRoleEntity);
		List<Long> roleIds = new ArrayList<>();
		if(roles!=null && CollectionUtil.isNotEmpty(roles)) {
			roles.forEach(vo->roleIds.add(vo.getRoleId()));
		}
		CurrUser currUser = new CurrUser(userEntity.getId(),userEntity.getUsername(),userEntity.getOemCode(),null);
		currUser.setUsertype(String.valueOf(userEntity.getPlatformType())); //设置用户类型
		currUser.setRoleIds(roleIds);
		return new SimpleAuthenticationInfo(currUser, userEntity.getPassword(),this.getClass().getName());// 放入shiro.调用
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Subject subject = SecurityUtils.getSubject();
		if (null == subject) { // 判断用户是否登录 如果未登录 返回null
			return null;
		}
		CurrUser currUser = (CurrUser) subject.getPrincipal(); // 获取当前用户信息
		if(currUser == null){
			return null;
		}
//		if (info == null) { // 如果session中没有当前用户信息 查询数据库 如果有 返回信息
//			info = new SimpleAuthorizationInfo();
//			List<SysApiEntity> apiList = sysApiService.findAllApiByUserId(currUser.getUserId());
//			Set<String> permissionSet = Sets.newHashSet();
//			if (CollectionUtil.isNotEmpty(apiList)) {
//				for (SysApiEntity entity : apiList) {
//					permissionSet.add(entity.getUrl());
//				}
//			}
//			info.setStringPermissions(permissionSet);
//			subject.getSession().setAttribute(PERMISSION_KEYS + currUser.getUserId(), info);
//		}
		return (SimpleAuthorizationInfo) subject.getSession().getAttribute(PERMISSION_KEYS + currUser.getUserId());
	}

	/**
	 * 设定Password校验.
	 */
	@PostConstruct
	public void initCredentialsMatcher() {
		//该句作用是重写shiro的密码验证，让shiro用我自己的验证
		setCredentialsMatcher(new CustomCredentialsMatcher());
	}
}

class CustomCredentialsMatcher extends SimpleCredentialsMatcher {
	@Override
	public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

		Object tokenCredentials = String.valueOf(token.getPassword());
		Object accountCredentials = getCredentials(info);
		//将密码加密与系统加密后的密码校验，内容一致就返回true,不一致就返回false
		return equals(tokenCredentials, accountCredentials);
	}

}
