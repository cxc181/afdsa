package com.yuqian.itax.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.yuqian.itax.agent.dao.OemMapper;
import com.yuqian.itax.agent.dao.OemParkRelaMapper;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParkRelaEntity;
import com.yuqian.itax.agent.entity.dto.OemParkIndustryAdminDTO;
import com.yuqian.itax.agent.entity.po.OemPO;
import com.yuqian.itax.agent.service.OemConfigService;
import com.yuqian.itax.agent.service.OemParkRelaService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.agreement.service.ParkAgreementTemplateRelaService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.orgs.dao.OrgMapper;
import com.yuqian.itax.orgs.dao.UserOrgRelaMapper;
import com.yuqian.itax.orgs.entity.OrgEntity;
import com.yuqian.itax.orgs.entity.UserOrgRelaEntity;
import com.yuqian.itax.orgs.service.UserOrgRelaService;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.ParkPO;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.TaxRulesConfigEntity;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxPolicyService;
import com.yuqian.itax.park.service.TaxRulesConfigService;
import com.yuqian.itax.roles.dao.RolesMapper;
import com.yuqian.itax.roles.dao.UserMenuRelaMapper;
import com.yuqian.itax.roles.dao.UserRoleRelaMapper;
import com.yuqian.itax.roles.entity.RolesEntity;
import com.yuqian.itax.roles.entity.UserMenuRelaEntity;
import com.yuqian.itax.roles.entity.UserRoleRelaEntity;
import com.yuqian.itax.roles.service.UserRoleRelaService;
import com.yuqian.itax.system.entity.CityEntity;
import com.yuqian.itax.system.entity.ProvinceEntity;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.user.dao.*;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.po.AgentPO;
import com.yuqian.itax.user.entity.po.AgentUpdatePO;
import com.yuqian.itax.user.entity.po.UserMenuPO;
import com.yuqian.itax.user.entity.po.UserPO;
import com.yuqian.itax.user.entity.query.AgentQuery;
import com.yuqian.itax.user.entity.query.UserQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.enums.UserLevelEnum;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.util.util.Md5Util;
import com.yuqian.itax.util.util.MemberPsdUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<UserEntity,UserMapper> implements UserService {

    @Autowired
    TaxPolicyService taxPolicyService;
    @Autowired
    TaxRulesConfigService taxRulesConfigService;
    @Resource
    OemMapper oemMapper;
    @Resource
    UserExtendMapper userExtendMapper;
    @Resource
    UserCityRelaMapper userCityRelaMapper;
    @Resource
    UserRoleRelaMapper userRoleRelaMapper;
    @Resource
    RolesMapper rolesMapper;
    @Resource
    AgentProfitsRulesMapper agentProfitsRulesMapper;
    @Resource
    UserOrgRelaMapper userOrgRelaMapper;
    @Resource
    OrgMapper orgMapper;
    @Autowired
    ParkService parkService;
    @Autowired
    OemService oemService;
    @Resource
    OemParkRelaMapper oemParkRelaMapper;
    @Resource
    UserRelaMapper userRelaMapper;
    @Autowired
    CityService cityService;
    @Autowired
    ProvinceService provinceService;
    @Resource
    CustomerServiceWorkNumberMapper customerServiceWorkNumberMapper;
    @Autowired
    UserOrgRelaService userOrgRelaService;
    @Autowired
    UserRoleRelaService userRoleRelaService;
    @Autowired
    RedisService redisService;
    @Autowired
    SmsService smsService;
    @Autowired
    DictionaryService sysDictionaryService;
    @Resource
    UserMenuRelaMapper userMenuRelaMapper;
    @Autowired
    OemParkIndustryBlacklistRelaService oemParkIndustryBlacklistRelaService;
    @Autowired
    OemConfigService oemConfigService;
    @Autowired
    UserService userService;
    @Autowired
    private ParkAgreementTemplateRelaService parkAgreementTemplateRelaService;
    @Autowired
    OemParkRelaService oemParkRelaService;
    @Autowired
    private OssService ossService;

    @Override
    public PageInfo<UserPageInfoVO> userPageInfo(UserQuery userQuery) {
        PageHelper.startPage(userQuery.getPageNumber(), userQuery.getPageSize());
        return new PageInfo<>(this.mapper.getUserInfoList(userQuery));
    }

    @Override
    public PageInfo<AgentPageInfoVO> agentPageInfo(AgentQuery agentQuery) {
        PageHelper.startPage(agentQuery.getPageNumber(), agentQuery.getPageSize());
        return new PageInfo<>(this.mapper.getAgentInfoList(agentQuery));
    }

    @Override
    public AgentDetailVO agentDetail(Long id) {
        AgentDetailVO agentDetailVO=new AgentDetailVO();
        UserEntity userEntity=mapper.selectByPrimaryKey(id);
        if(null == userEntity || userEntity.getAccountType()!=1||(userEntity.getPlatformType()!=5&&userEntity.getPlatformType()!=4)){
            throw  new BusinessException("代理商不存在");
        }
        BeanUtils.copyProperties(userEntity,agentDetailVO);
        //用户拓展表信息
        UserExtendEntity userExtendEntity=userExtendMapper.getUserExtendByUserId(id);
        agentDetailVO.setPhone(userExtendEntity.getPhone());
        //城市信息
        UserCityRelaEntity userCityRelaEntity=userCityRelaMapper.queryUserCityRelaEntityByUserId(id);
        CityEntity cityEntity= cityService.getByCode(userCityRelaEntity.getCityCode());
        if(null == cityEntity){
            throw new BusinessException("代理商城市不存在！");
        }
        ProvinceEntity provinceEntity=provinceService.getByCode(userCityRelaEntity.getProvinceCode());
        if(null == provinceEntity){
            throw new BusinessException("代理商省不存在！");
        }
        agentDetailVO.setCityName(cityEntity.getName());
        agentDetailVO.setProvinceName(provinceEntity.getName());

       //代理商分润表数据
        AgentProfitsRulesEntity agentProfitsRulesEntity=agentProfitsRulesMapper.getAgentProfitsRulesEntityByAgentId(id);
        if(agentProfitsRulesEntity!=null){
            agentDetailVO.setMembershipFee(agentProfitsRulesEntity.getMembershipFee());
            agentDetailVO.setInvoiceFee(agentProfitsRulesEntity.getInvoiceFee());
            agentDetailVO.setRegisterFee(agentProfitsRulesEntity.getRegisterFee());
            agentDetailVO.setCancelCompanyFee(agentProfitsRulesEntity.getCancelCompanyFee());
        }
        return agentDetailVO;
    }

    @Override
    @Transactional
    public UserEntity addAgent(AgentPO agentPO, String userAccount) throws BusinessException {
        if(this.mapper.getUserByUserName(agentPO.getOemCode(),agentPO.getUsername()).size()>0){
            throw new BusinessException("该账号已经被注册，请确认.");
        }
        /*List<UserExtendEntity> userExtendEntityList=userExtendMapper.getUserExtendByPhoneAndOemCode(agentPO.getPhone(),agentPO.getOemCode());
        if(userExtendEntityList.size()>0){
            throw new BusinessException("该手机号已经被注册，请确认.");
        }*/
        if(agentPO.getPlatformType()==4){
            List<UserEntity> userEntities=getPartner(agentPO.getCityCode(),agentPO.getProvinceCode(),agentPO.getOemCode());
            if(userEntities.size()>0){
                throw new BusinessException("该城市已经有高级城市合伙人，请确认后再新建.");
            }
        }

        //新增主表信息sys_e_user
        UserEntity userEntityPO=new UserEntity();
        BeanUtils.copyProperties(agentPO,userEntityPO);
        userEntityPO.setAccountType(1);
        UserEntity userEntity=insertUser(userEntityPO,userAccount);
        //增加sys_e_user_extend
        UserExtendEntity userExtendEntity=insertUserExtend(null,agentPO.getPhone(),userAccount,agentPO.getCityCode(),agentPO.getProvinceCode(),userEntity);
        //新增创建账号需要创建的组织
        OrgEntity orgEntity=insertOrg(agentPO.getCityCode(),agentPO.getProvinceCode(),userAccount,userEntity);
        //新用用户和组织的关系
        insertUserOrgRelaEntity(orgEntity.getId(),userAccount,userEntity);

        //新增代理商分润表
        AgentProfitsRulesEntity agentProfitsRulesEntityPO=new AgentProfitsRulesEntity();
        BeanUtils.copyProperties(agentPO,agentProfitsRulesEntityPO);

        insertAgentProfitsRulesEntity(agentProfitsRulesEntityPO,agentPO.getCityCode(),userAccount,userEntity);
        //增加用户和城市的关系表sys_r_user_city。
        insertUserCityRelaEntity(agentPO.getCityCode(),agentPO.getProvinceCode(),userAccount,userEntity);

        //增加t_e_user_rela用户关系表
        //新增高级合伙人和服务商角色
        Long roleId=null;
        Integer level=0;
        Integer pLevel=0;
        if(userEntity.getPlatformType()==4){
            //rolesEntity= insertRolesEntity(orgEntity,agentPO.getNickname()+"高级城市合伙人","CITYPARTNER",userAccount,userEntity);
            roleId=3L;
            level=UserLevelEnum.PARTNER.getValue();
            pLevel=UserLevelEnum.OEMANDPARK.getValue();
            insertUserRelaEntity(userEntity.getOemCode(),userEntity.getId(), level,userExtendEntity.getParentUserId(),pLevel,userAccount,null,agentPO.getCityCode());
        }else  if(userEntity.getPlatformType()==5){
            //rolesEntity=insertRolesEntity(orgEntity,agentPO.getNickname()+"城市合伙人","CITYAGENT",userAccount,userEntity);
            roleId=4L;
            level=UserLevelEnum.CITYPARTNER.getValue();
            pLevel=UserLevelEnum.PARTNER.getValue();
            insertUserRelaEntity(userEntity.getOemCode(),userEntity.getId(), level,userExtendEntity.getParentUserId(),pLevel,userAccount,null,agentPO.getCityCode());
        }
        //增加sys_r_user_role （角色管理）
        insertUserRoleEntity(roleId,userAccount,userEntity);
        return  userEntity;
    }

    @Override
    @Transactional
    public UserEntity addOem(OemPO oemPO, String userAccount) throws BusinessException {
        if(this.mapper.getUserByUserName(oemPO.getOemCode(),oemPO.getUsername()).size()>0){
            throw new BusinessException("该账号已经被注册，请确认后再新建.");
        }
        if(getOemAccount(oemPO.getOemCode())!=null){
            throw new BusinessException("该机构号已经被注册，请确认后再新建.");
        }
        /*List<UserExtendEntity> userExtendEntityList=userExtendMapper.getUserExtendByPhoneAndOemCode(oemPO.getPhone(),oemPO.getOemCode());
        if(userExtendEntityList.size()>0){
            throw new BusinessException("该手机号已经被注册，请确认.");
        }*/
        if(mapper.getUserByNickname(oemPO.getOemCode(),oemPO.getOemName()).size()>0){
            throw new BusinessException("该机构名称已经被注册，请确认后再新建.");
        }
        UserEntity userEntityPO=new UserEntity();
        BeanUtils.copyProperties(oemPO,userEntityPO);
        userEntityPO.setNickname(oemPO.getOemName());
        userEntityPO.setAccountType(1);
        userEntityPO.setPlatformType(2);
        //新增主表信息sys_e_user
        UserEntity userEntity=insertUser(userEntityPO,userAccount);

        //增加sys_e_user_extend
        UserExtendEntity userExtendEntity=insertUserExtend(null,oemPO.getPhone(),userAccount,null,null,userEntity);
        //新增创建账号需要创建的组织
        OrgEntity orgEntity=insertOrg(null,null,userAccount,userEntity);
        //新用用户和组织的关系
        insertUserOrgRelaEntity(orgEntity.getId(),userAccount,userEntity);
        //RolesEntity rolesEntity=insertRolesEntity(orgEntity,oemPO.getOemName(),"ADMINARTNER",userAccount,userEntity);
        //新增代理商分润表
        AgentProfitsRulesEntity agentProfitsRulesEntity=new AgentProfitsRulesEntity();
        BeanUtils.copyProperties(oemPO,agentProfitsRulesEntity);
        insertAgentProfitsRulesEntity(agentProfitsRulesEntity,null,userAccount,userEntity);

        //传递新建的用户ID
        oemPO.setId(userEntity.getId());
        //园区管理员新增园区信息
        handlePublicSealImg(oemPO);
        oemService.addOemEntity(oemPO,userAccount);
        //增加组织和园区的关系
        insertOemParkRelaEntity(oemPO,userAccount);
        //绑定系统预设关系（oem机构管理员绑定预设角色）roled=1 OEM机构角色
        insertUserRoleEntity(1L,userAccount,userEntity);
        //增加t_e_user_rela用户关系表
        Integer level=0;
        Integer pLevel=0;
        level=UserLevelEnum.OEMANDPARK.getValue();
        pLevel=UserLevelEnum.PLATFORM.getValue();
        insertUserRelaEntity(userEntity.getOemCode(),userEntity.getId(), level,userExtendEntity.getParentUserId(),pLevel,userAccount,null,null);

        return  userEntity;
    }

    void handlePublicSealImg(OemPO oemPO) {
        if (StringUtil.isEmpty(oemPO.getOfficialSealImg())) {
            return;
        }
        // 获取公章并上传至oss公有域
        byte[] download = ossService.download(oemPO.getOfficialSealImg(), "oss_privateBucketName");
        ossService.uploadPublic(oemPO.getOfficialSealImg(), download);
        oemPO.setOfficialSealImgPublic(oemPO.getOfficialSealImg());
    }


    RolesEntity insertRolesEntity(OrgEntity orgEntity,String roleName,String roleCode,String userAccount, UserEntity userEntity){
        RolesEntity rolesEntity= new RolesEntity();
        rolesEntity.setOemCode(userEntity.getOemCode());
        rolesEntity.setRoleName(roleName);
        rolesEntity.setRoleCode(roleCode);
        rolesEntity.setOrgId(orgEntity.getParentOrgId());
        rolesEntity.setOrgName(orgEntity.getOrgName());
        rolesEntity.setType(0);
        rolesEntity.setStatus(1);
        rolesEntity.setAddTime(new Date());
        rolesEntity.setAddUser(userAccount);
        rolesMapper.insert(rolesEntity);

        return rolesEntity;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserEntity addPark(ParkPO parkPO, String userAccount) throws BusinessException {
        if(this.mapper.getUserByUserName(parkPO.getOemCode(),parkPO.getUsername()).size()>0){
            throw new BusinessException("该账号已经被注册，请确认后再新建.");
        }

        /*List<UserExtendEntity> userExtendEntityList=userExtendMapper.getUserExtendByPhoneAndOemCode(parkPO.getPhone(),parkPO.getOemCode());
        if(userExtendEntityList.size()>0){
            throw new BusinessException("该手机号已经被注册，请确认.");
        }*/
        UserEntity userEntityPO=new UserEntity();
        BeanUtils.copyProperties(parkPO,userEntityPO);
        userEntityPO.setNickname(parkPO.getParkName());
        userEntityPO.setAccountType(1);
        userEntityPO.setPlatformType(3);
        //新增主表信息sys_e_user
        UserEntity userEntity=insertUser(userEntityPO,userAccount);

        //增加sys_e_user_extend
        UserExtendEntity userExtendEntity=insertUserExtend(null,parkPO.getPhone(),userAccount,null,null,userEntity);
        //新增创建账号需要创建的组织
        OrgEntity orgEntity=insertOrg(null,null,userAccount,userEntity);
        //新用用户和组织的关系
        insertUserOrgRelaEntity(orgEntity.getId(),userAccount,userEntity);

        //新增园区角色(1.0.5以后不需要新增角色)
        //RolesEntity rolesEntity=insertRolesEntity(orgEntity,parkPO.getParkName(),"PARKADMIN",userAccount,userEntity);

        //传递新建的用户ID
        parkPO.setUserId(userEntity.getId());
        //园区管理员新增园区信息
        ParkEntity parkEntity=parkService.addParkEntity(parkPO,userAccount);
        //更新用户PARKID
        userEntity.setParkId(parkEntity.getId());
        mapper.updateByPrimaryKey(userEntity);

        //增加sys_r_user_role （角色管理）
        insertUserRoleEntity(2L,userAccount,userEntity);
        //增加t_e_user_rela用户关系表
        Integer level=0;
        Integer pLevel=0;
        level=UserLevelEnum.OEMANDPARK.getValue();
        pLevel=UserLevelEnum.PLATFORM.getValue();

        insertUserRelaEntity(userEntity.getOemCode(),userEntity.getId(), level,userExtendEntity.getParentUserId(),pLevel,userAccount,null,null);
        return  userEntity;
    }

    @Override
    @Transactional
    public UserEntity addUser(UserPO userPO, String userAccount) throws BusinessException {
        if(this.mapper.getUserByUserName(userPO.getOemCode(),userPO.getUsername()).size()>0){
            throw new BusinessException("该账号已经被注册，请确认后再新建.");
        }
        /*    List<UserExtendEntity> userExtendEntityList=userExtendMapper.getUserExtendByPhoneAndOemCode(userPO.getPhone(),userPO.getOemCode());
        if(userExtendEntityList.size()>0){
            throw new BusinessException("该手机号已经被注册，请确认.");
        }*/
        //所选组织
        OrgEntity orgEntity=orgMapper.selectByPrimaryKey(userPO.getOrgId());
        if(null == orgEntity){
            throw new BusinessException("机构不存在。");
        }
        UserEntity userEntityPO=new UserEntity();
        BeanUtils.copyProperties(userPO,userEntityPO);
        if (userPO.getIsCustomer() == null) {
            userPO.setIsCustomer(0);
        }
        if(userPO.getIsCustomer()==0) {
            userEntityPO.setAccountType(3);
        }else{
            userEntityPO.setAccountType(2);
        }
        if(orgEntity.getOrgType()==3){
            UserEntity userPark =mapper.getParkAccountByOrgId(userPO.getOrgId());
            userEntityPO.setParkId(userPark.getParkId());
        }
        userEntityPO.setPlatformType(orgEntity.getOrgType());
        //新增主表信息sys_e_user
        UserEntity userEntity=insertUser(userEntityPO,userAccount);


        //增加sys_e_user_extend
        UserExtendEntity userExtendEntity=insertUserExtend(userPO.getOrgId(),userPO.getPhone(),userAccount,null,null,userEntity);

        //新用用户和组织的关系
        UserOrgRelaEntity userOrgRelaEntity=insertUserOrgRelaEntity(userPO.getOrgId(),userAccount,userEntity);
        //增加sys_r_user_role （角色管理）
        insertUserRoleEntity(userPO.getRoleId(),userAccount,userEntity);
        //增加t_e_user_rela用户关系表
        Integer level=0;
        Integer pLevel=0;
        if(userEntity.getPlatformType()==1){
            level=UserLevelEnum.PLATFORMUSER.getValue();
            pLevel=UserLevelEnum.PLATFORM.getValue();
        }
        if(userEntity.getPlatformType()==2){
            level=UserLevelEnum.OEMUSER.getValue();
            pLevel=UserLevelEnum.OEMANDPARK.getValue();
        }else if(userEntity.getPlatformType()==3){
            level=UserLevelEnum.PARKUSER.getValue();
            pLevel=UserLevelEnum.OEMANDPARK.getValue();
        }else if(userEntity.getPlatformType()==4){
            level=UserLevelEnum.PARTNERUSER.getValue();
            pLevel=UserLevelEnum.PARTNER.getValue();
        }else if(userEntity.getPlatformType()==5){
            level=UserLevelEnum.CITYPARTNERUSER.getValue();
            pLevel=UserLevelEnum.CITYPARTNER.getValue();
        }

        insertUserRelaEntity(userEntity.getOemCode(),userEntity.getId(),level,userExtendEntity.getParentUserId(),pLevel,userAccount,null,null);
        return  userEntity;
    }


    /**
     * 用户关系表新增
     * @auth hz
     * @param id
     * @param level
     * @param pId
     * @param pLevel
     * @@param  type 1-高级合伙人新增城市服务商
     */
    @Override
    public UserRelaEntity insertUserRelaEntity(String oemCode,Long id,Integer level,Long pId,Integer pLevel,String userAccount,Integer type,String cityCode){
        UserRelaEntity userRelaEntity=new UserRelaEntity();
        userRelaEntity.setUserId(id);
        userRelaEntity.setUserClass(level);
        userRelaEntity.setParentUserId(pId);
        userRelaEntity.setParentUserType(pLevel);
        userRelaEntity.setAddTime(new Date());
        userRelaEntity.setAddUser(userAccount);
        UserRelaEntity pUserRelaEntity =userRelaMapper.queryUserRelaEntityByUserId(pId,pLevel);
        if(level==4){//服务商情况下
            //查询当前城市有没有高级合伙人
            List<UserEntity> userPartnerEntity=mapper.getPartnerByCityCode(oemCode,cityCode);
            if (userPartnerEntity.size()>0){
                userRelaEntity.setUserTree(pUserRelaEntity.getUserTree()+"/"+id);
            }else{
                //没有就补0
                UserRelaEntity entity =userRelaMapper.queryUserRelaEntityByUserId(pId,2);
                userRelaEntity.setUserTree(entity.getUserTree()+"/0/"+id);
            }
        }else if(level==3){//高级合伙人情况下
            //查询当前城市有没有服务商情况下
            List<UserEntity> userCityList=mapper.getCityAgentByCityCode(oemCode,cityCode);
            String userTree="";
            for(UserEntity userCity :userCityList){
                //有服务商修改服务商
                UserRelaEntity userCityRela=userRelaMapper.queryUserRelaEntityByUserId(userCity.getId(),4);
                userTree=userCityRela.getUserTree();
                userCityRela.setUserTree(userTree.replace("/0/","/"+id+"/"));
                userRelaMapper.updateByPrimaryKey(userCityRela);
                //修改服务商所有下级关系关系树
                userRelaMapper.updateUserRela(userTree,id);
            }
            userRelaEntity.setUserTree(pUserRelaEntity.getUserTree()+"/"+id);
        }else if(null!=type&&type==1){//如果是高级合伙人推广的用户或者
            userRelaEntity.setUserTree(pUserRelaEntity.getUserTree()+"/0/"+id);
        }else if (pUserRelaEntity == null){
            OemEntity oem = this.oemService.getOem(oemCode);
            if(null == oem){
                throw new BusinessException("操作失败，OEM机构不存在");
            }
            //判断是否接入国金助手，如果接入则查询国金助手，否则提示邀请码错误
            OemConfigEntity oemConfigEntity = new OemConfigEntity();
            oemConfigEntity.setOemCode(oemCode);
            oemConfigEntity.setParamsCode("is_open_channel");
            List<OemConfigEntity> configList = oemConfigService.select(oemConfigEntity);
            if(oem.getIsInviterCheck() == 0 || (configList!=null && configList.size() == 1)){ //如果不需要邀请码校验，则将邀请码推广的用户默认挂载到几个下面
                UserEntity oemUserEntity = mapper.getOemAccount(oem.getOemCode()); //获取机构管理员的用户id
                //没有就补0
                UserRelaEntity entity =userRelaMapper.queryUserRelaEntityByUserId(oemUserEntity.getId(),2);
                userRelaEntity.setUserTree(entity.getUserTree()+"/0/0/"+id);
            }
        }else{
            userRelaEntity.setUserTree(pUserRelaEntity.getUserTree()+"/"+id);
        }
        userRelaMapper.insert(userRelaEntity);
        return userRelaEntity;
    }

    /**
     * 增加OEM和园区关系表
     */
    public void insertOemParkRelaEntity(OemPO oemPO,String userAccount){
       //先删除所有机构和园区的关系
        if(!"".equals(oemPO.getOemCode())&&oemPO.getOemCode()!=null){
            oemParkRelaMapper.deleteByOemCode(oemPO.getOemCode());
        }
        List<OemParkIndustryAdminDTO> parkIdList = oemPO.getParkIdList();
        if(CollectionUtil.isNotEmpty(parkIdList)){
            for (OemParkIndustryAdminDTO dto: parkIdList){
                //再新增机构和园区的关系
                OemParkRelaEntity oemParkRelaEntity=new OemParkRelaEntity();
                oemParkRelaEntity.setOemCode(oemPO.getOemCode());
                oemParkRelaEntity.setParkId(dto.getParkId());
                oemParkRelaEntity.setAddTime(new Date());
                oemParkRelaEntity.setAddUser(userAccount);
                oemParkRelaEntity.setUpdateTime(new Date());
                oemParkRelaEntity.setUpdateUser(userAccount);
                oemParkRelaEntity.setAgreementTemplateId(dto.getAgreementTemplateId());
                oemParkRelaMapper.insert(oemParkRelaEntity);
                List<Long> industryIds = dto.getIndustryIds();
                //industryIds为null则不修改，为空则表示删除原来的例外行业
                if (industryIds != null) {
                    //保存行业黑名单数据
                    oemParkIndustryBlacklistRelaService.addBatch(oemPO.getOemCode(), dto.getParkId(), industryIds, userAccount);
                }
            }
        }
    }

    /**
     * 用户和组织的关系表
     * @param userEntity
     * @return
     */
    public UserOrgRelaEntity insertUserOrgRelaEntity(Long orgId,String userAccount,UserEntity userEntity) {
        UserOrgRelaEntity userOrgRelaEntity= new UserOrgRelaEntity();
        userOrgRelaEntity.setUserId(userEntity.getId());
        userOrgRelaEntity.setOrgId(orgId);
        if(userEntity.getAccountType()==1&&userEntity.getPlatformType()==2){
            userOrgRelaEntity.setOrgType(2);
        }else if(userEntity.getAccountType()==1&&userEntity.getPlatformType()==3){
            userOrgRelaEntity.setOrgType(3);
        }else if(userEntity.getAccountType()==1&&userEntity.getPlatformType()==4){
            userOrgRelaEntity.setOrgType(4);
        }else if(userEntity.getAccountType()==1&&userEntity.getPlatformType()==5){
            userOrgRelaEntity.setOrgType(5);
        }
        userOrgRelaEntity.setAddTime(new Date());
        userOrgRelaEntity.setAddUser(userAccount);
        userOrgRelaEntity.setOrgType(userEntity.getPlatformType());
        userOrgRelaMapper.insert(userOrgRelaEntity);
        return userOrgRelaEntity;
    }
        /**
         * 增加角色关系
         * @param userEntity
         * @return
         */

    public UserRoleRelaEntity insertUserRoleEntity(Long roleId,String userAccount,UserEntity userEntity){
        UserRoleRelaEntity userRoleRelaEntity=new UserRoleRelaEntity();
        userRoleRelaEntity.setUserId(userEntity.getId());
        userRoleRelaEntity.setRoleId(roleId);
        userRoleRelaEntity.setAddTime(new Date());
        userRoleRelaEntity.setAddUser(userAccount);
        userRoleRelaMapper.insert(userRoleRelaEntity);
        return userRoleRelaEntity;
    }

    /**
     * 新增用户和城市关系表
     * @param userEntity
     * @return
     */
    public  UserCityRelaEntity insertUserCityRelaEntity(String cityCode,String provinceCode,String userAccount,UserEntity userEntity) {
        UserCityRelaEntity userCityRelaEntity=new UserCityRelaEntity();
        userCityRelaEntity.setUserId(userEntity.getId());
        userCityRelaEntity.setOemCode(userEntity.getOemCode());
        userCityRelaEntity.setProvinceCode(provinceCode);
        userCityRelaEntity.setCityCode(cityCode);
        userCityRelaEntity.setAddTime(new Date());
        userCityRelaEntity.setAddUser(userAccount);
        userCityRelaMapper.insert(userCityRelaEntity);
        return userCityRelaEntity;
    }

        /**
         * 新增代理商分润规则表
         * @param userEntity
         * @return
         */
    public  AgentProfitsRulesEntity insertAgentProfitsRulesEntity(AgentProfitsRulesEntity agentProfitsRulesEntityPO,String cityCode,String userAccount,UserEntity userEntity) throws BusinessException {
        AgentProfitsRulesEntity agentProfitsRulesEntity =new AgentProfitsRulesEntity();
        //新增代理商分润规则表
        if(userEntity.getPlatformType()==4){
            agentProfitsRulesEntity.setAgentType(1);
        }else if(userEntity.getPlatformType()==5){
            agentProfitsRulesEntity.setAgentType(2);
        }else if(userEntity.getPlatformType()==2){
            agentProfitsRulesEntity.setAgentType(0);
            agentProfitsRulesEntity.setAgentId(1L);
        }
        if(userEntity.getPlatformType()!=2){
            agentProfitsRulesEntity.setAgentId(userEntity.getId());
        }
        if(userEntity.getPlatformType()==5){
            //查询是否有高级城市合伙人
            List<UserEntity> userPartnerEntity=mapper.getPartnerByCityCode(userEntity.getOemCode(),cityCode);
            if(userPartnerEntity.size()>0){
                //有高级城市合伙人，则判断服务商利率是不是大于高级城市合伙人利率
                UserEntity userPartner=userPartnerEntity.get(0);
                AgentProfitsRulesEntity agentPartnerProfitsRulesEntity=agentProfitsRulesMapper.getAgentProfitsRulesEntityByAgentId(userPartner.getId());
                if(null == agentPartnerProfitsRulesEntity){
                    throw  new BusinessException(userPartner.getUsername()+"分润规则不存在，新增失败。");
                }
                if(agentProfitsRulesEntityPO.getMembershipFee().compareTo(agentPartnerProfitsRulesEntity.getMembershipFee())>0 || agentProfitsRulesEntityPO.getInvoiceFee().compareTo(agentPartnerProfitsRulesEntity.getInvoiceFee())>0 || agentProfitsRulesEntityPO.getRegisterFee().compareTo(agentPartnerProfitsRulesEntity.getRegisterFee())>0){
                    throw  new BusinessException(userEntity.getNickname()+"城市合伙人分润率不能大于"+userPartner.getNickname()+"高级城市合伙人，请确定后再新增");
                }
            }
            agentProfitsRulesEntity.setAgentType(2);
        }
        agentProfitsRulesEntity.setOemCode(userEntity.getOemCode());
        agentProfitsRulesEntity.setAgentAccount(userEntity.getUsername());
        agentProfitsRulesEntity.setMembershipFee(agentProfitsRulesEntityPO.getMembershipFee());
        agentProfitsRulesEntity.setRegisterFee(agentProfitsRulesEntityPO.getRegisterFee());
        agentProfitsRulesEntity.setInvoiceFee(agentProfitsRulesEntityPO.getInvoiceFee());
        agentProfitsRulesEntity.setCancelCompanyFee(agentProfitsRulesEntityPO.getCancelCompanyFee());
        agentProfitsRulesEntity.setStatus(1);
        agentProfitsRulesEntity.setAddTime(new Date());
        agentProfitsRulesEntity.setAddUser(userAccount);
        agentProfitsRulesMapper.insertSelective(agentProfitsRulesEntity);
        return  agentProfitsRulesEntity;
    }

    /**
     * 新增组织
     * @param userEntity
     * @return
     */
    public OrgEntity insertOrg(String cityCode,String provinceCode,String userAccount,UserEntity userEntity) throws BusinessException {
        OrgEntity orgEntity =new OrgEntity();
        if(userEntity.getAccountType()==1 &&userEntity.getPlatformType()==4){
            //创建代理商组织sys_e_org
            orgEntity.setOemCode(userEntity.getOemCode());
            orgEntity.setOrgName(userEntity.getNickname());
            //获取OME组织信息
            UserEntity userOemEntity=mapper.getOemAccount(userEntity.getOemCode());
            UserOrgRelaEntity userOrgRelaEntity=userOrgRelaMapper.getUserOrgRelaByUserId(userOemEntity.getId());
           /* if(null == userOrgRelaEntity){
                throw  new BusinessException("OEM组织关系不存在，新增失败。");
            }*/
            OrgEntity orgOemEntity=orgMapper.selectByPrimaryKey(userOrgRelaEntity.getOrgId());
            if(null == orgOemEntity){
                throw  new BusinessException("OEM组织不存在，新增失败。");
            }
            orgEntity.setParentOrgId(orgOemEntity.getId());
            orgEntity.setAddTime(new Date());
            orgEntity.setAddUser(userAccount);
            orgEntity.setOrgType(4);
            orgMapper.insert(orgEntity);
            orgEntity.setOrgTree(orgOemEntity.getOrgTree()+"/"+orgEntity.getId());
            //修改名下城市合伙人组织的上级为新建高级城市合伙人组织
            List<UserEntity> userCityAgentEntities=getCityAgent(userEntity.getOemCode(),provinceCode,cityCode);
            for(int i=0;i<userCityAgentEntities.size();i++){
                UserEntity userCityAgent=userCityAgentEntities.get(i);
                UserOrgRelaEntity userCityAgentOrgRelaEntity=userOrgRelaMapper.getUserOrgRelaByUserId(userCityAgent.getId());
                OrgEntity orgCityAgentEntity=orgMapper.selectByPrimaryKey(userCityAgentOrgRelaEntity.getOrgId());
                if(null == orgCityAgentEntity){
                    throw  new BusinessException(userCityAgent.getUsername()+"上级组织不存在，新增失败。");
                }

                orgCityAgentEntity.setParentOrgId(orgEntity.getId());

                //更改上级userId
                orgMapper.updateByPrimaryKey(orgCityAgentEntity);
                //更新城市合伙人组织树
                String orgTreeString=orgCityAgentEntity.getOrgTree().replace("/00",String.valueOf(orgEntity.getId()));
                orgCityAgentEntity.setOrgTree(orgTreeString);
                orgMapper.updateByPrimaryKey(orgCityAgentEntity);
            }

        }else if(userEntity.getAccountType()==1 &&userEntity.getPlatformType()==5){
            String tree="";
            //创建城市合伙人组织sys_e_org
            orgEntity.setOemCode(userEntity.getOemCode());
            orgEntity.setOrgName(userEntity.getNickname());
            orgEntity.setOrgType(5);
            //获取高级城市合伙人组织
            List<UserEntity> userPartnerEntity=mapper.getPartnerByCityCode(userEntity.getOemCode(),cityCode);
            if(userPartnerEntity.size()>0){
                //有高级城市合伙人，上级设置为高级城市合伙人组织
                UserEntity userPartner=userPartnerEntity.get(0);
                UserOrgRelaEntity userPartneOrgRelaEntity=userOrgRelaMapper.getUserOrgRelaByUserId(userPartner.getId());
                OrgEntity orgPartneEntity=orgMapper.selectByPrimaryKey(userPartneOrgRelaEntity.getOrgId());
                if(null == orgPartneEntity){
                    throw  new BusinessException(userPartner.getUsername()+"组织不存在，新增失败。");
                }
                orgEntity.setParentOrgId(orgPartneEntity.getId());
                //设置tree
                tree=orgPartneEntity.getOrgTree();
            }else{
                //没有高级城市合伙人,上级设置为OEM机构组织
                //查询单签OEM机构账号
                UserEntity userOemEntity =getOemAccount(userEntity.getOemCode());
                UserOrgRelaEntity userOEMOrgRelaEntity=userOrgRelaMapper.getUserOrgRelaByUserId(userOemEntity.getId());
                OrgEntity orgOemEntity=orgMapper.selectByPrimaryKey(userOEMOrgRelaEntity.getOrgId());
                if(null == orgOemEntity){
                    throw  new BusinessException(userOemEntity.getUsername()+"组织不存在，新增失败。");
                }
                orgEntity.setParentOrgId(orgOemEntity.getId());
                tree=orgOemEntity.getOrgTree()+"/0";
            }
            orgEntity.setOrgTree("");
            orgEntity.setAddTime(new Date());
            orgEntity.setAddUser(userAccount);
            orgMapper.insert(orgEntity);
            orgEntity.setOrgTree(tree+"/"+orgEntity.getId());

        }else if(userEntity.getAccountType()==1&&(userEntity.getPlatformType()==3||userEntity.getPlatformType()==2)){
            if(userEntity.getPlatformType()==2){
                orgEntity.setOrgType(2);
            }else if(userEntity.getPlatformType()==3){
                orgEntity.setOrgType(3);
            }
            orgEntity.setOemCode(userEntity.getOemCode());
            orgEntity.setOrgName(userEntity.getNickname());
            orgEntity.setParentOrgId(1L);
            orgEntity.setAddTime(new Date());
            orgEntity.setAddUser(userAccount);
            orgMapper.insert(orgEntity);
            orgEntity.setOrgTree("1/"+orgEntity.getId());
        }
        orgMapper.updateByPrimaryKey(orgEntity);//更新组织树
        return  orgEntity;
    }

    /**
     * 新增系统用户拓展表
     * @return
     */
    public UserExtendEntity insertUserExtend(Long orgId,String phone,String userAccount,String cityCode,String getProvinceCode,UserEntity userEntity){
        UserExtendEntity userExtendEntity=new UserExtendEntity();
        userExtendEntity.setUserId(userEntity.getId());

        if(userEntity.getAccountType()==1&&userEntity.getPlatformType()==4){
            UserEntity userOemEntity =getOemAccount(userEntity.getOemCode());
            UserExtendEntity userOemExtendEntity=userExtendMapper.getUserExtendByUserId(userOemEntity.getId());

            //查询单签OEM机构账号
            userExtendEntity.setParentUserId(userOemEntity.getId());
            userExtendEntity.setUserTree(userOemExtendEntity.getUserTree()+"/"+userEntity.getId());

            //修改该地区所有城市合伙人的上级为新建高级城市合伙人
            List<UserEntity> userCityAgentEntities=getCityAgent(userEntity.getOemCode(),getProvinceCode,cityCode);

            for(int i=0;i<userCityAgentEntities.size();i++){
                UserEntity userCityAgent=userCityAgentEntities.get(i);
                UserExtendEntity userCityAgentExtendEntity=userExtendMapper.getUserExtendByUserId(userCityAgent.getId());
                if(userCityAgentExtendEntity != null && StringUtil.isNotEmpty(userCityAgentExtendEntity.getUserTree())) {
                    userCityAgentExtendEntity.setParentUserId(userEntity.getId());
                    String newTree = userCityAgentExtendEntity.getUserTree().replace("/0/", "/" + userEntity.getId() + "/");
                    userCityAgentExtendEntity.setUserTree(newTree);
                    //更改上级userId
                    userExtendMapper.updateByPrimaryKey(userCityAgentExtendEntity);
                }
            }

        }else if(userEntity.getAccountType()==1&&userEntity.getPlatformType()==5){
            //查询所在城市是否有高级合伙人
            List<UserEntity> userEntities=getPartner(cityCode,getProvinceCode,userEntity.getOemCode());
            if(userEntities.size()>0){
                //有高级城市合伙人
                UserEntity userPartner=userEntities.get(0);
                UserExtendEntity userPartnerEntity=userExtendMapper.getUserExtendByUserId(userPartner.getId());

                userExtendEntity.setParentUserId(userPartner.getId());
                userExtendEntity.setUserTree(userPartnerEntity.getUserTree()+"/"+userEntity.getId());

            }else{
                UserEntity userOemEntity =getOemAccount(userEntity.getOemCode());
                UserExtendEntity userOemExtendEntity=userExtendMapper.getUserExtendByUserId(userOemEntity.getId());

                //没有高级城市合伙人
                userExtendEntity.setParentUserId(userOemEntity.getId());
                userExtendEntity.setUserTree(userOemExtendEntity.getUserTree()+"/0/"+userEntity.getId());
            }

        }else if (userEntity.getAccountType()==1 && (userEntity.getPlatformType()==2||( userEntity.getPlatformType()==3))){
            //oem机构和园区
            userExtendEntity.setParentUserId(1L);
            userExtendEntity.setUserTree("1/"+userEntity.getId());
        }else if(userEntity.getAccountType()!=1 && userEntity.getPlatformType()==2){
            UserEntity userOemEntity =getOemAccount(userEntity.getOemCode());
            UserExtendEntity userOemExtendEntity=userExtendMapper.getUserExtendByUserId(userOemEntity.getId());
            //设置上级ID是OEM机构账号
            userExtendEntity.setParentUserId(userOemEntity.getId());
            userExtendEntity.setUserTree(userOemExtendEntity.getUserTree()+"/"+userEntity.getId());
        }else if(userEntity.getAccountType()!=1 && userEntity.getPlatformType()==3){
            //设置上级ID是园区账号
            UserEntity userPark=mapper.getParkAccountByOrgId(orgId);
            UserExtendEntity userParkExtend=userExtendMapper.getUserExtendByUserId(userPark.getId());
            userExtendEntity.setParentUserId(userPark.getId());
            userExtendEntity.setUserTree(userParkExtend.getUserTree()+"/"+userEntity.getId());
        }else if(userEntity.getAccountType()!=1 && userEntity.getPlatformType()==1){
            //设置上级ID是平台账号
            userExtendEntity.setParentUserId(1L);
            userExtendEntity.setUserTree("1/"+userEntity.getId());
        }else{
            //代理商组织的账号
            //根据组织ID查询代理商账号
            OrgEntity orgAgentEntity=orgMapper.selectByPrimaryKey(orgId);
            if(userEntity.getPlatformType()==4){
                //查出组织高级合伙人账号
                UserEntity userParentAgent=mapper.getAgentAccount(userEntity.getOemCode(),4,orgId);
                UserExtendEntity userParentAgentExtend=userExtendMapper.getUserExtendByUserId(userParentAgent.getId());
                userExtendEntity.setParentUserId(userParentAgent.getId());
                userExtendEntity.setUserTree(userParentAgentExtend.getUserTree()+"/"+userEntity.getId());

            }
            if(userEntity.getPlatformType()==5){
                //查出组织高级合伙人账号
                UserEntity userCityAgent=mapper.getAgentAccount( orgAgentEntity.getOemCode(),5,orgId);
                UserExtendEntity userCityAgentExtend=userExtendMapper.getUserExtendByUserId(userCityAgent.getId());
                userExtendEntity.setParentUserId(userCityAgent.getId());
                userExtendEntity.setUserTree(userCityAgentExtend.getUserTree()+"/"+userEntity.getId());
            }

        }
        userExtendEntity.setPhone(phone);
        userExtendEntity.setAddTime(new Date());
        userExtendEntity.setAddUser(userAccount);
        userExtendMapper.insert(userExtendEntity);
        return userExtendEntity;
    }

    /**
     * 根据组织ID查询oem和园区的管理账号
     */

    /**
     * 新增主表信息
     * @param userEntityPO
     * @return
     */
    public UserEntity insertUser(UserEntity userEntityPO,String userAccount){
        //增加user主表信息
        userEntityPO.setUsername(userEntityPO.getUsername());
        String pwd=Md5Util.MD5(userEntityPO.getUsername().substring(userEntityPO.getUsername().length()-6)).toLowerCase();
        String slat=UUID.randomUUID().toString().replaceAll("-","");
        pwd=MemberPsdUtil.encrypt(pwd, userEntityPO.getUsername(),slat);
        userEntityPO.setPassword(pwd);
        userEntityPO.setSlat(slat);
        userEntityPO.setOemCode(userEntityPO.getOemCode());
        OemEntity oemEntity=oemMapper.getOem(userEntityPO.getOemCode());
        if(oemEntity!=null){
            userEntityPO.setOemName(oemEntity.getOemName());
        }
        userEntityPO.setStatus(1);
        userEntityPO.setAccountType(userEntityPO.getAccountType());
        userEntityPO.setBindingAccount(userEntityPO.getBindingAccount());
        userEntityPO.setAddTime(new Date());
        userEntityPO.setAddUser(userAccount);
        userEntityPO.setNickname(userEntityPO.getNickname());
        userEntityPO.setRemark(userEntityPO.getRemark());
        userEntityPO.setParkId(userEntityPO.getParkId());
        this.mapper.insert(userEntityPO);
        return userEntityPO;
    }


    /**
     * 根据OEM机构查询系统账号
     */
    @Override
    public UserEntity getOemAccount (String oemCode){
        UserEntity userEntity= mapper.getOemAccount(oemCode);
        return userEntity;
    }

    @Override
    public UserEntity getParkAccount(Long parkId) {
        return mapper.getParkByParkId(parkId);
    }

    /**
     * 判断所在城市是否有高级合伙人
     * @athu HZ
     * @param cityCode
     * @param provinceCode
     * @param oemCode
     * @return
     */
    @Override
    public List<UserEntity> getPartner (String cityCode,String provinceCode,String oemCode){
       List<UserEntity> userEntities= mapper.getPartnerByCityCode(oemCode,cityCode);
        return userEntities;
    }

    @Override
    public List<CustomerServiceWorkVO> getCustomerServiceByOemCode(String oemCode,Long id) {
        return mapper.getCustomerServiceByOemCode(oemCode,id);
    }

    @Override
    @Transactional
    public UserEntity cancelUser(Long id,Integer status,String userAccount) {
        UserEntity userEntity=mapper.selectByPrimaryKey(id);
        if(userEntity.getStatus()==2){
            throw  new BusinessException("注销状态不能修改状态");
        }
        UserExtendEntity userExtendEntity=userExtendMapper.getUserExtendByUserId(userEntity.getId());

        userEntity.setStatus(status);
        if(status==2){
            userEntity.setUsername(userEntity.getUsername()+"_1");
            userExtendEntity.setPhone(userExtendEntity.getPhone()+"_1");
        }
        userEntity.setUpdateTime(new Date());
        userEntity.setUpdateUser(userAccount);
        mapper.updateByPrimaryKey(userEntity);
        //手机号码
        userExtendEntity.setUpdateTime(new Date());
        userExtendEntity.setUpdateUser(userAccount);
        userExtendMapper.updateByPrimaryKey(userExtendEntity);
        return  userEntity;
    }

    @Override
    public UserEntity getOrgAdminAccount(Long userId) {
        return mapper.getOrgAdminAccount(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity cancelAgent(Long id, Integer status, String userAccount) {
        //查询其系统用户、城市服务商账号
        UserOrgRelaEntity userOrgRelaEntity=new UserOrgRelaEntity();
        userOrgRelaEntity.setUserId(id);
        userOrgRelaEntity=userOrgRelaService.selectOne(userOrgRelaEntity);
        UserOrgRelaEntity entity=new UserOrgRelaEntity();
        entity.setOrgId(userOrgRelaEntity.getOrgId());
        List<UserOrgRelaEntity> list=userOrgRelaService.select(entity);
        for (UserOrgRelaEntity userOrg:list ) {
            UserEntity user=mapper.selectByPrimaryKey(userOrg.getUserId());
            if(user.getStatus()!=2 &&user.getAccountType()!=1){
                throw  new BusinessException("该代理商下面有未注销得用户，不能注销");
            }
        }
        //注销账号
        UserEntity userEntity=cancelUser(id,status,userAccount);

        return userEntity;
    }

    @Override
    public UserEntity qeruyUserByAccountAndOemCode(String oemCode, String account) {
        return mapper.qeruyUserByAccountAndOemCode(oemCode,account);
    }

    @Override
    public UserAndExtendVO qeruyUserByUsernameAndOemCode(String oemCode, String username,String userPhone) {
        return mapper.qeruyUserByUsernameAndOemCode(oemCode,username,userPhone);
    }
    /**
     * 生成登录token 先生成uuid+5位随机数 散列后 在md5加密
     *
     * @author LiuXianTing
     * @return
     */
    //@OperatorLog(module="登录",operDes="生成登录token 先生成uuid+5位随机数 散列后 在md5加密",oprType=4)
    private String createToken() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }


    @Override
    public Map<String,String> login(String oemCode, String username, String pwd,String vCode) {
        //通过用户名获取用户信息

        List<UserEntity> list = mapper.getUserByUserName(oemCode,username);
        if (list.size()<=0) {
            throw new BusinessException("账号不存在");
        }
        UserEntity userEntity=list.get(0);

        UserExtendEntity userExtendEntity=userExtendMapper.getUserExtendByUserId(userEntity.getId());

        OemEntity oemEntity=oemMapper.getOem(userEntity.getOemCode());
        if(null == oemEntity){
            throw new BusinessException("账号所属机构不存在！");
        }

        if(userEntity.getPlatformType()==2&& userEntity.getOemCode()!=null){
            if(oemEntity.getOemStatus()!=1){
                throw new BusinessException("机构已经禁用，不允许登陆");
            }
        }
        Map<String,String> result = new HashMap<>();
        if(userEntity.getPlatformType()==3 && userEntity.getParkId()!=null){
            ParkEntity parkEntity=parkService.findById(userEntity.getParkId());
            if(null == parkEntity){
                throw new BusinessException("园区不存在");
            }
            if(parkEntity.getStatus()!=1){
                throw new BusinessException("园区已经禁用，不允许登陆");
            }
            result.put("parkType",parkEntity.getParkType().toString());
        }
        String lock=redisService.get(RedisKey.USER_LOGIN_LOCK_KEY+oemCode+"_"+ username);
        if(lock!=null && "1".equals(lock)){
            throw new BusinessException("账号已经暂时锁定，请等待解锁后再试！");
        }
        String verficationCode=redisService.get(RedisKey.SMS_USER_LOGIN_KEY_SUFFER+oemCode+"_"+username);
        if(!vCode.equals(verficationCode)){
            throw new BusinessException("验证码不正确或者已过期，请稍后再试！");
        }
        String loginFailNumber=redisService.get(RedisKey.USER_LOGIN_FAIL_KEY+oemCode+"_"+ username);
        if(null == loginFailNumber||"".equals(loginFailNumber)){
            loginFailNumber="0";
        }
        int failNumber=Integer.parseInt(loginFailNumber);

        pwd= MemberPsdUtil.encrypt(pwd, userEntity.getUsername(),userEntity.getSlat());

        if (!userEntity.getPassword().equals(pwd) ) {
            //增加失败次数
            failNumber=failNumber+1;
            redisService.set(RedisKey.USER_LOGIN_FAIL_KEY+oemCode+"_"+ username,String.valueOf(failNumber),60*60*24);
            //添加锁定状态
            Map<String, Object> map = new HashMap<>();
            map.put("name",oemEntity.getOemName());
            if(failNumber==5){
                redisService.set(RedisKey.USER_LOGIN_LOCK_KEY+oemCode+"_"+ username,"1",60*10);
                //发送通知短信
                map.put("time","10分钟");
                smsService.sendTemplateSms(userExtendEntity.getPhone(), userEntity.getOemCode(), VerifyCodeTypeEnum.LOCK_ACCOUNT.getValue(), map, 2);
                throw new BusinessException("密码连续错误过多，账号锁定10分钟");
            }
            if(failNumber==10){
                redisService.set(RedisKey.USER_LOGIN_LOCK_KEY+oemCode+"_"+ username,"1",60*60*24);
                //发送通知短信
                map.put("time","24小时");
                smsService.sendTemplateSms(userExtendEntity.getPhone(), userEntity.getOemCode(), VerifyCodeTypeEnum.LOCK_ACCOUNT.getValue(), map, 2);
                throw new BusinessException("您本日密码输错次数已达上限，账号将锁定24小时");
            }
            throw new BusinessException("账号或密码错误");
        }else if(userEntity.getStatus()==2){
            throw new BusinessException("账号已被锁定，请联系管理员");
        }else if(userEntity.getStatus()==0){
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

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
        if(StringUtil.isEmpty(oemCode) && !"3".equals(currUser.getUsertype()) && !"1".equals(currUser.getUsertype())){
            throw new BusinessException("平台账号或者密码错误");
        }else if(StringUtil.isNotEmpty(oemCode) && !oemCode.equals(currUser.getOemCode()) ){
            throw new BusinessException("机构账号或者密码错误");
        }
        String token = createToken();
        result.put("token",token);


        // 登录成功，查询出用户名返回

        result.put("username",userEntity.getUsername());
        result.put("nickname",userEntity.getNickname());
        result.put("accountType",String.valueOf(userEntity.getAccountType()));
        result.put("platformType",String.valueOf(userEntity.getPlatformType()));
        result.put("oemName",oemEntity.getOemName());

        String outTime = sysDictionaryService.getByCode("redis_token_outtime_sys").getDictValue();
        redisService.delete(RedisKey.USER_LOGIN_FAIL_KEY+oemCode+"_"+ username); //登陆成功，删除密码错误次数
        redisService.delete(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_"+redisService.get(RedisKey.LOGIN_TOKEN_KEY+oemCode+"_" + "userId_2_" + username));
        redisService.set(RedisKey.LOGIN_TOKEN_KEY+ oemCode +"_" + "userId_2_" + username,token,Integer.parseInt(outTime));
        redisService.set(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_"+ token,currUser,Integer.parseInt(outTime)); //失效时间

        return result;
    }

    @Override
    public boolean loginSms(String oemCode, String username, String pwd) {
        //通过用户名获取用户信息

        List<UserEntity> list = mapper.getUserByUserName(oemCode,username);
        if (list.size()<=0) {
            throw new BusinessException("账号不存在");
        }
        UserEntity userEntity=list.get(0);

        UserExtendEntity userExtendEntity=userExtendMapper.getUserExtendByUserId(userEntity.getId());

        OemEntity oemEntity=oemMapper.getOem(userEntity.getOemCode());
        if(null == oemEntity){
            throw new BusinessException("账号所属机构不存在！");
        }

        if(userEntity.getPlatformType()==2&& userEntity.getOemCode()!=null){
            if(oemEntity.getOemStatus()!=1){
                throw new BusinessException("机构已经禁用，不允许登陆");
            }
        }
        if(userEntity.getPlatformType()==3 && userEntity.getParkId()!=null){
            ParkEntity parkEntity=parkService.findById(userEntity.getParkId());
            if(null == parkEntity){
                throw new BusinessException("园区不存在");
            }
            if(parkEntity.getStatus()!=1){
                throw new BusinessException("园区已经禁用，不允许登陆");
            }
        }
        String lock=redisService.get(RedisKey.USER_LOGIN_LOCK_KEY+oemCode+"_"+ username);
        if(lock!=null && "1".equals(lock)){
            throw new BusinessException("账号已经暂时锁定，请等待解锁后再试！");
        }

        String loginFailNumber=redisService.get(RedisKey.USER_LOGIN_FAIL_KEY+oemCode+"_"+ username);
        if(null == loginFailNumber||"".equals(loginFailNumber)){
            loginFailNumber="0";
        }
        int failNumber=Integer.parseInt(loginFailNumber);

        pwd= MemberPsdUtil.encrypt(pwd, userEntity.getUsername(),userEntity.getSlat());

        if (!userEntity.getPassword().equals(pwd) ) {
            //增加失败次数
            failNumber=failNumber+1;
            redisService.set(RedisKey.USER_LOGIN_FAIL_KEY+oemCode+"_"+ username,String.valueOf(failNumber),60*60*24);
            //添加锁定状态
            Map<String, Object> map = new HashMap<>();
            map.put("name",oemEntity.getOemName());
            if(failNumber==5){
                redisService.set(RedisKey.USER_LOGIN_LOCK_KEY+oemCode+"_"+ username,"1",60*10);
                //发送通知短信
                map.put("time","10分钟");
                smsService.sendTemplateSms(userExtendEntity.getPhone(), userEntity.getOemCode(), VerifyCodeTypeEnum.LOCK_ACCOUNT.getValue(), map, 2);
                throw new BusinessException("密码连续错误过多，账号锁定10分钟");
            }
            if(failNumber==10){
                redisService.set(RedisKey.USER_LOGIN_LOCK_KEY+oemCode+"_"+ username,"1",60*60*24);
                //发送通知短信
                map.put("time","24小时");
                smsService.sendTemplateSms(userExtendEntity.getPhone(), userEntity.getOemCode(), VerifyCodeTypeEnum.LOCK_ACCOUNT.getValue(), map, 2);
                throw new BusinessException("您本日密码输错次数已达上限，账号将锁定24小时");
            }
            throw new BusinessException("账号或密码错误");
        }else if(userEntity.getStatus()==2){
            throw new BusinessException("账号已被锁定，请联系管理员");
        }else if(userEntity.getStatus()==0){
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        if(StringUtil.isEmpty(oemCode) && !ObjectUtils.equals(userEntity.getPlatformType(),3) && !ObjectUtils.equals(userEntity.getPlatformType(),1)){
            throw new BusinessException("平台账号或者密码错误");
        }else if(StringUtil.isNotEmpty(oemCode) && !oemCode.equals(userEntity.getOemCode()) ){
            throw new BusinessException("机构账号或者密码错误");
        }
        return true;
    }
    /**
     * 所在城市所有有城市合伙人
     * @athu HZ
     * @param cityCode
     * @param provinceCode
     * @param oemCode
     * @return
     */
    public List<UserEntity> getCityAgent (String cityCode,String provinceCode,String oemCode){
        List<UserEntity> userEntities= mapper.getCityAgentByCityCode(oemCode,cityCode);
        return userEntities;
    }

    @Override
    @Transactional
    public void updateUserEntityById(UserPO userPO ,String userAccount) throws BusinessException {
        //修改系统用户表
        UserEntity userEntity= mapper.selectByPrimaryKey(userPO.getId());

        if(userPO.getIsCustomer()==0){
            //是否为客服坐席 如果还有工号未注销不允许更改
            List<CustomerServiceWorkNumberEntity> customerServiceWorkNumberEntity=customerServiceWorkNumberMapper.queryCustomerServiceWorkNumberEntityByuserId(userEntity.getId());
            if(customerServiceWorkNumberEntity.size()>0){
                throw  new BusinessException("客服坐席下的工号未删除！");
            }
            //  如果是平台客服坐席改为否
            if (userEntity.getAccountType() == 2 && userPO.getIsCustomer()==0 && userEntity.getPlatformType() == 1){
                List<OemEntity> oemList = oemService.queryOemInfoByOemCodeAndWorkAuditWay(null,1);
                if (oemList != null && oemList.size()>0){
                    List<UserEntity> userList = userService.queryAccountByOemCodeAndAccountTypeAndPlatformType(null,2,1 ,userEntity.getId());
                    if (userList == null || userList.isEmpty()){
                        throw  new BusinessException("平台可用坐席至少要保留一个！");
                    }
                }
            }
            //  oem机构客服坐席改否
            if (userEntity.getAccountType() == 2 && userPO.getIsCustomer()==0 && userEntity.getPlatformType() == 2){
                List<OemEntity> oemList = oemService.queryOemInfoByOemCodeAndWorkAuditWay(userEntity.getOemCode(),2);
                if (oemList != null && oemList.size()>0){
                    List<UserEntity> userList = userService.queryAccountByOemCodeAndAccountTypeAndPlatformType(userEntity.getOemCode(),2,2,userEntity.getId());
                    if (userList == null || userList.size() < 1){
                        throw  new BusinessException("机构可用坐席至少要保留一个！");
                    }
                }
            }
            userEntity.setAccountType(3);
        }else {
            userEntity.setAccountType(2);
        }
        userEntity.setRemark(userPO.getRemark());
        userEntity.setNickname(userPO.getNickname());
        userEntity.setUpdateUser(userAccount);
        userEntity.setUpdateTime(new Date());
        mapper.updateByPrimaryKey(userEntity);
        //修改用户拓展表
        UserExtendEntity userExtendEntity=userExtendMapper.getUserExtendByUserId(userEntity.getId());
        userExtendEntity.setPhone(userPO.getPhone());
        userExtendEntity.setUpdateUser(userAccount);
        userExtendEntity.setUpdateTime(new Date());
        userExtendMapper.updateByPrimaryKey(userExtendEntity);
        //修改角色
        UserRoleRelaEntity userRoleRelaEntity=userRoleRelaMapper.queryUserRoleRelaEntityByUserId(userEntity.getId());
        userRoleRelaEntity.setRoleId(userPO.getRoleId());
        userRoleRelaEntity.setUpdateUser(userAccount);
        userRoleRelaEntity.setUpdateTime(new Date());
        userRoleRelaMapper.updateByPrimaryKey(userRoleRelaEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity updateAgentEntityById(AgentUpdatePO agentPO, String userAccount) {
        //修改系统用户表

        UserEntity userEntity= mapper.selectByPrimaryKey(agentPO.getId());
        if(null == userEntity){
            throw new BusinessException(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        userEntity.setRemark(agentPO.getRemark());
        mapper.updateByPrimaryKey(userEntity);

        //修改用户拓展表
        UserExtendEntity userExtendEntity=userExtendMapper.getUserExtendByUserId(userEntity.getId());
        if(null == userExtendEntity){
            throw new BusinessException("用户拓展信息不正确。");
        }
        userExtendEntity.setPhone(agentPO.getPhone());
        userExtendEntity.setUpdateTime(new Date());
        userExtendEntity.setUpdateUser(userAccount);
        userExtendMapper.updateByPrimaryKey(userExtendEntity);

        //修改代理商分润表
        AgentProfitsRulesEntity agentProfitsRulesEntity=agentProfitsRulesMapper.getAgentProfitsRulesEntityByAgentId(agentPO.getId());
        if(null == agentProfitsRulesEntity){
            throw new BusinessException("代理商分润信息不存在。");
        }
        agentProfitsRulesEntity.setInvoiceFee(agentPO.getInvoiceFee());
        agentProfitsRulesEntity.setRegisterFee(agentPO.getRegisterFee());
        agentProfitsRulesEntity.setMembershipFee(agentPO.getMembershipFee());
        agentProfitsRulesEntity.setCancelCompanyFee(agentPO.getCancelCompanyFee());
        agentProfitsRulesEntity.setUpdateTime(new Date());
        agentProfitsRulesEntity.setUpdateUser(userAccount);
        agentProfitsRulesMapper.updateByPrimaryKey(agentProfitsRulesEntity);
        return  userEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity updateOemtEntityById(OemPO oemPO, String userAccount) {
        //修改系统用户表

        UserEntity userEntity= mapper.getOemAccount(oemPO.getOemCode());
        userEntity.setRemark(oemPO.getRemark());
        userEntity.setNickname(oemPO.getOemName());
        userEntity.setOemName(oemPO.getOemName());
        userEntity.setUpdateUser(userAccount);
        userEntity.setUpdateTime(new Date());
        mapper.updateByPrimaryKey(userEntity);

        //修改用户拓展表
        UserExtendEntity userExtendEntity=userExtendMapper.getUserExtendByUserId(userEntity.getId());
        userExtendEntity.setPhone(oemPO.getPhone());
        userExtendEntity.setUpdateUser(userAccount);
        userExtendEntity.setUpdateTime(new Date());
        userExtendMapper.updateByPrimaryKey(userExtendEntity);
        //修改机构表信息
        OemEntity oemEntity=oemMapper.getOem(oemPO.getOemCode());
        oemEntity.setOemName(oemPO.getOemName());
        oemEntity.setOemLogo(oemPO.getOemLogo());
        oemEntity.setCompanyName(oemPO.getCompanyName());
        oemEntity.setCustomerServiceTel(oemPO.getCustomerServiceTel());
        oemEntity.setNetAddress(oemPO.getNetAddress());
        oemEntity.setUpdateUser(userAccount);
        oemEntity.setUpdateTime(new Date());
        oemEntity.setEin(oemPO.getEin());
        oemEntity.setBelongsCompanyAddress(oemPO.getBelongsCompanyAddress());
        oemEntity.setRemark(oemPO.getRemark());
//        oemEntity.setAgreementTemplateId(oemPO.getAgreementTemplateId());
        oemEntity.setOfficialSealImg(oemPO.getOfficialSealImg());
        handlePublicSealImg(oemPO);
        oemEntity.setOfficialSealImgPublic(oemPO.getOfficialSealImgPublic());
        oemEntity.setIsSendAuditBillsMessage(oemPO.getIsSendAuditBillsMessage());
        if (StringUtils.isNotBlank(oemPO.getOemAppid()) && oemEntity.getIsCheckstand() == 1){
            oemEntity.setOemAppid(oemPO.getOemAppid());
        }
        if (StringUtils.isNotBlank(oemPO.getOtherPayOemcode()) && oemPO.getIsOtherOemPay() == 1){
            oemEntity.setOtherPayOemcode(oemPO.getOtherPayOemcode());
        }
        // 收款银行账号
        if(StringUtils.isNotBlank(oemPO.getReceivingBankAccount())){
            oemEntity.setReceivingBankAccount(oemPO.getReceivingBankAccount());
        }
        // 收款账号开户行
        if(StringUtils.isNotBlank(oemPO.getReceivingBankAccountBranch())){
            oemEntity.setReceivingBankAccountBranch(oemPO.getReceivingBankAccountBranch());
        }
        oemEntity.setIsOtherOemPay(oemPO.getIsOtherOemPay());
        oemMapper.updateByPrimaryKey(oemEntity);
        //去掉未勾选园区行业黑名单数据
        oemParkIndustryBlacklistRelaService.deleteByParkIds(oemPO.getOemCode(), oemPO.getParkIdList().stream().map(OemParkIndustryAdminDTO::getParkId).collect(Collectors.toList()));
        insertOemParkRelaEntity(oemPO,userAccount);
        return userEntity;
    }

    @Override
    @Transactional
    public UserEntity updateParkPOById(ParkPO parkPO, String userAccount) {
        UserEntity userEntity = mapper.getParkByParkId(parkPO.getParkId());
        List<ParkEntity> parkEntities = parkService.getParkByParkCode(parkPO.getParkCode(),parkPO.getParkId());
        if(parkEntities.size()>0){
            throw  new BusinessException("该园区编码已经存在。");
        }
        UserExtendEntity userExtendEntity=userExtendMapper.getUserExtendByUserId(userEntity.getId());
        userExtendEntity.setPhone(parkPO.getPhone());
        userExtendEntity.setUpdateUser(userAccount);
        userExtendEntity.setUpdateTime(new Date());
        userExtendMapper.updateByPrimaryKey(userExtendEntity);

        ParkEntity parkEntity = parkService.findById(parkPO.getParkId());
        if(parkEntity ==null){
            throw  new BusinessException("未找到园区信息。");
        }
        if(parkEntity.getStatus().intValue() == 1){
            throw  new BusinessException("已上架园区不允许进行编辑操作。");
        }

        //新增或删除园区税费政策
        List<TaxPolicyEntity> list=parkPO.getTaxPolicyList();
        TaxPolicyEntity taxPolicyEntity = new TaxPolicyEntity();
        TaxPolicyEntity taxPolicyPo = null;
        List<TaxPolicyEntity> policyList = null;
        List<Integer> companyTypeList = new ArrayList<>(4);
        for(int i=0;i<list.size();i++){
            taxPolicyPo = list.get(i);
            taxPolicyPo.setParkId(parkEntity.getId());
            companyTypeList.add(taxPolicyPo.getCompanyType());
            //查询是否已配置企业类型对应的园区政策
            taxPolicyEntity.setParkId(parkEntity.getId());
            taxPolicyEntity.setCompanyType(taxPolicyPo.getCompanyType());
            policyList = taxPolicyService.select(taxPolicyEntity);
            if(policyList!=null&&policyList.size()>0){
                // 主键id
                taxPolicyPo.setId(policyList.get(0).getId());
                if(StringUtils.isNotBlank(taxPolicyPo.getPolicyFileUrl())){
                    taxPolicyPo.setPolicyFileUrl(taxPolicyPo.getPolicyFileUrl());
                }
                taxPolicyPo.setUpdateTime(new Date());
                taxPolicyPo.setUpdateUser(userAccount);
                taxPolicyPo.setRemark("园区编辑更新政策配置");
                taxPolicyService.editByIdSelective(taxPolicyPo);
            }else{
                //新增企业类型园区政策
                taxPolicyService.addTaxPolicy(taxPolicyPo,userAccount);
            }
        }
        //将多余的企业政策类型删除
        Example example = new Example(TaxPolicyEntity.class);
        example.createCriteria().andEqualTo("parkId",parkEntity.getId()).andNotIn("companyType",companyTypeList);
        policyList = taxPolicyService.selectByExample(example);
        if(policyList!=null &&policyList.size()>0){
            policyList.forEach(vo->{
                Example taxRulesConfigExample = new Example(TaxRulesConfigEntity.class);
                taxRulesConfigExample.createCriteria().andEqualTo("policyId",vo.getId());
                //删除税费规则配置
                taxRulesConfigService.delByExample(taxRulesConfigExample);
                //删除园区政策
                taxPolicyService.delById(vo.getId());
            });
        }
        parkEntity.setParkCode(parkPO.getParkCode());
        parkEntity.setPostageFees(parkPO.getPostageFees());
        parkEntity.setRecipientAddress(parkPO.getRecipientAddress());
        parkEntity.setProvinceName(parkPO.getProvinceName());
        parkEntity.setProvinceCode(parkPO.getProvinceCode());
        parkEntity.setRecipient(parkPO.getRecipient());
        parkEntity.setDistrictName(parkPO.getDistrictName());
        parkEntity.setDistrictCode(parkPO.getDistrictCode());
        parkEntity.setCityName(parkPO.getCityName());
        parkEntity.setCityCode(parkPO.getCityCode());
        parkEntity.setRecipientPhone(parkPO.getRecipientPhone());
        parkEntity.setEin(parkPO.getEin());
        parkEntity.setBelongsCompanyAddress(parkPO.getBelongsCompanyAddress());
        parkEntity.setBelongsCompanyName(parkPO.getBelongsCompanyName());
        parkEntity.setUpdateTime(new Date());
        parkEntity.setUpdateUser(userAccount);
        parkEntity.setParkAddress(parkPO.getParkAddress());
        parkEntity.setAuthorizationFile(parkPO.getAuthorizationFile());
        parkEntity.setDrawer(parkPO.getDrawer());
        parkEntity.setPayee(parkPO.getPayee());
        parkEntity.setReviewer(parkPO.getReviewer());
        parkEntity.setOfficialSealImg(parkPO.getOfficialSealImg());
        parkEntity.setParkType(parkPO.getParkType());
        parkEntity.setIsRegisterProfit(parkPO.getIsRegisterProfit());
        parkEntity.setIsRenewProfit(parkPO.getIsRenewProfit());

        // 4.1版本新增5个字段
        // 园区预览图
        parkEntity.setParkThumbnail(parkPO.getParkThumbnail());

        parkEntity.setParkImgs(parkPO.getParkImgs());

        // 税收政策说明
        parkEntity.setTaxPolicyDesc(parkPO.getTaxPolicyDesc());

        // 工商注册说明
        parkEntity.setRegisterDesc(parkPO.getRegisterDesc());

        // 税务办理说明
        parkEntity.setTaxHandleDesc(parkPO.getTaxHandleDesc());

        // 对公户办理说明
        parkEntity.setCorporateAccountHandleDesc(parkPO.getCorporateAccountHandleDesc());

        parkService.editByIdSelective(parkEntity);

        return userEntity;
    }

    @Override
    public UserEntity resetPassword(Long operatorId,String userAccount) {
        UserEntity userEntity=mapper.selectByPrimaryKey(operatorId);
        //重置为账号后6位
        String pwd= Md5Util.MD5(userEntity.getUsername().substring(userEntity.getUsername().length()-6)).toLowerCase();
        pwd= MemberPsdUtil.encrypt(pwd,userEntity.getUsername(),userEntity.getSlat());
        userEntity.setPassword(pwd);
        userEntity.setUpdateTime(new Date());
        userEntity.setUpdateUser(userAccount);
        mapper.updateByPrimaryKey(userEntity);
        //解锁密码登录次数限制
        redisService.delete(RedisKey.USER_LOGIN_LOCK_KEY+userEntity.getOemCode()+"_"+ userEntity.getUsername());
        redisService.delete(RedisKey.USER_LOGIN_FAIL_KEY+userEntity.getOemCode()+"_"+ userEntity.getUsername());

        return userEntity;
    }

    @Override
    public UserEntity getUserByPlatformTypeAndAccountType(String oemCode,Integer accountType ,Integer platformType) {
        return mapper.getUserByPlatformTypeAndAccountType(oemCode,accountType,platformType);
    }

    @Override
    public UserEntity getUserByUserName(String userName, String oemCode) {
        List<UserEntity> list = mapper.getUserByUserName(oemCode, userName);
        if (CollectionUtil.isEmpty(list)) {
            throw new BusinessException("用户名不存在");
        }
        if (list.size() > 1) {
            throw new BusinessException("用户名存在多个");
        }
        return list.get(0);
    }

    @Override
    public SysUserVO findSysUserVOByUserId(Long userId) {
        UserEntity userEntity = mapper.selectByPrimaryKey(userId);
        List<UserMenuRelaEntity> sysUserMenuEntities = userMenuRelaMapper.selectByUserId(userId);
        for(int i =0 ;i<sysUserMenuEntities.size() ;i++){
            if (sysUserMenuEntities.get(i).getMenuId()==1) {
                sysUserMenuEntities.remove(i);
            }
        }
        Long[] menuIdList = new Long[sysUserMenuEntities.size()];
        for (int i = 0;i<menuIdList.length;i++){
            menuIdList[i]=sysUserMenuEntities.get(i).getMenuId();
        }
        SysUserVO sysUserVO = new SysUserVO();
        sysUserVO.setUserId(userId);
        sysUserVO.setName(userEntity.getUsername());
        sysUserVO.setStatus(userEntity.getStatus());
        sysUserVO.setRemark(userEntity.getRemark());
        sysUserVO.setMenuIdList(menuIdList);
        return sysUserVO;
    }

    @Override
    public void updateUserMenuRelayEntity(UserMenuPO userMenuPO, Long userId) {
        //修改菜单权限
        //遍历menuIdList修改角色菜单对应关系
        List<Long> menuIdList = userMenuPO.getMenuIdList();
        userMenuRelaMapper.deleteByUserId(userMenuPO.getUserId());
        for (Long id:menuIdList) {
            UserMenuRelaEntity userMenuRelaEntity = new UserMenuRelaEntity();
            userMenuRelaEntity.setAddUser(String.valueOf(userId));
            userMenuRelaEntity.setAddTime(new Date());
            userMenuRelaEntity.setUserId(userMenuPO.getUserId());
            userMenuRelaEntity.setMenuId(id);
            userMenuRelaMapper.insert(userMenuRelaEntity);
        }
    }

    @Override
    public AccountVO queryAccount(String oemCode,String phone) {
        return mapper.queryAccount(oemCode,phone);
    }

    @Override
    public List<UserEntity> queryAccountByOemCodeAndAccountTypeAndPlatformType(String oemCode, Integer accountType, Integer platformType,Long id) {
        return mapper.queryAccountByOemCodeAndAccountTypeAndPlatformType(oemCode,accountType,platformType,id);
    }
}

