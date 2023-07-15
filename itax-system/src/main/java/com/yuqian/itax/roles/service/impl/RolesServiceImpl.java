package com.yuqian.itax.roles.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.dao.OemMapper;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.orgs.entity.OrgEntity;
import com.yuqian.itax.orgs.service.OrgService;
import com.yuqian.itax.roles.dao.RoleMenuRelaMapper;
import com.yuqian.itax.roles.dao.RolesMapper;
import com.yuqian.itax.roles.entity.RoleMenuRelaEntity;
import com.yuqian.itax.roles.entity.RolesEntity;
import com.yuqian.itax.roles.entity.UserRoleRelaEntity;
import com.yuqian.itax.roles.entity.po.RolePO;
import com.yuqian.itax.roles.entity.query.RoleQuery;
import com.yuqian.itax.roles.entity.vo.SysRoleVO;
import com.yuqian.itax.roles.service.RolesService;
import com.yuqian.itax.roles.service.UserRoleRelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;


@Service("rolesService")
public class RolesServiceImpl extends BaseServiceImpl<RolesEntity,RolesMapper> implements RolesService {

    @Autowired
    OemMapper oemMapper;
    @Autowired
    RoleMenuRelaMapper roleMenuRelaMapper;
    @Autowired
    UserRoleRelaService userRoleRelaService;
    @Autowired
    OrgService orgService;

    @Override
    public PageInfo<RolesEntity> pageRolesEntity( RoleQuery roleQuery) {
        PageHelper.startPage(roleQuery.getPageNumber(), roleQuery.getPageSize());
        return new PageInfo<RolesEntity>(mapper.queryListByOrgId(roleQuery));
    }

    @Override
    public RolesEntity addRolesEntity(RolePO rolePO,String oemCode,Long userId) {
        RolesEntity rolesEntity= new RolesEntity();
        rolesEntity.setRoleName(rolePO.getRoleName());
        rolesEntity.setOrgId(rolePO.getOrgId());
        OrgEntity orgEntity=orgService.findById(rolePO.getOrgId());
        rolesEntity.setOrgName(orgEntity.getOrgName());
        rolesEntity.setStatus(1);
        List<RolesEntity> rolesEntities=mapper.select(rolesEntity);
        if(!CollectionUtils.isEmpty(rolesEntities)){
            throw  new BusinessException(orgEntity.getOrgName()+"组织已经存在"+rolePO.getRoleName()+"该角色。");
        }
        rolesEntity.setOemCode(oemCode);
        //查出OEM机构名称
        OemEntity oemEntity=oemMapper.getOem(oemCode);
        rolesEntity.setOemName(oemEntity.getOemName());
        rolesEntity.setRoleCode(rolePO.getRoleCode());

        rolesEntity.setType(1);
        rolesEntity.setStatus(1);
        rolesEntity.setAddTime(new Date());
        rolesEntity.setAddUser(String.valueOf(userId));
        rolesEntity.setRemark(rolePO.getRemark());
        mapper.insert(rolesEntity);
        return rolesEntity;
    }

    @Override
    public RolesEntity updateRolesEntity(RolePO rolePO,  Long userId) throws BusinessException {
        RolesEntity rolesEntity=mapper.selectByPrimaryKey(rolePO.getId());
        if(rolesEntity.getStatus()==0){
            throw  new BusinessException("角色已删除");
        }
        if(!"".equals(rolePO.getRoleName())&&null!=(rolePO.getRoleName())){
            rolesEntity.setRoleName(rolePO.getRoleName());
        }
        if(!"".equals(rolePO.getRoleCode())&&null!=(rolePO.getRoleCode())){
            rolesEntity.setRoleCode(rolePO.getRoleCode());
        }
        if(!"".equals(rolePO.getRemark())&&null!=(rolePO.getRemark())){
            rolesEntity.setRemark(rolePO.getRemark());
        }
        if(null != rolePO.getStatus()){
            //如果是删除 判断角色是否有绑定未注销的账户
            if(rolePO.getStatus()==0){
                List<UserRoleRelaEntity> list=userRoleRelaService.queryUserRoleRelaEntityByRoleId(rolePO.getId());
                if(list.size()>0){
                    throw  new BusinessException("该角色下存在有效账户，不允许删除！");
                }
            }
            rolesEntity.setStatus(rolePO.getStatus());
        }
        rolesEntity.setUpdateTime(new Date());
        rolesEntity.setUpdateUser(String.valueOf(userId));
        mapper.updateByPrimaryKey(rolesEntity);

        return rolesEntity;
    }

    @Override
    public void updateRoleMenuRelayEntity(RolePO rolePO, Long userId) {
        //修改菜单权限
        //遍历menuIdList修改角色菜单对应关系
        List<Long> menuIdList = rolePO.getMenuIdList();
        roleMenuRelaMapper.deleteByRoleId(rolePO.getId());
        for (Long id:menuIdList) {
            RoleMenuRelaEntity roleMenuRelaEntity = new RoleMenuRelaEntity();
            roleMenuRelaEntity.setAddUser(String.valueOf(userId));
            roleMenuRelaEntity.setAddTime(new Date());
            roleMenuRelaEntity.setRoleId(rolePO.getId());
            roleMenuRelaEntity.setMenuId(id);
            roleMenuRelaMapper.insert(roleMenuRelaEntity);
        }
    }

    @Override
    public SysRoleVO findSysRoleVOById(Long id) {
        RolesEntity sysRoleEntity = mapper.selectByPrimaryKey(id);
        List<RoleMenuRelaEntity> sysRoleMenuEntities = roleMenuRelaMapper.selectByRoleId(id);
        for(int i =0 ;i<sysRoleMenuEntities.size() ;i++){
            if (sysRoleMenuEntities.get(i).getMenuId()==1) {
                sysRoleMenuEntities.remove(i);
            }
        }
        Long[] menuIdList = new Long[sysRoleMenuEntities.size()];
        for (int i = 0;i<menuIdList.length;i++){
            menuIdList[i]=sysRoleMenuEntities.get(i).getMenuId();
        }
        SysRoleVO sysRoleVO = new SysRoleVO();
        sysRoleVO.setId(id);
        sysRoleVO.setName(sysRoleEntity.getRoleName());
        sysRoleVO.setStatus(sysRoleEntity.getStatus());
        sysRoleVO.setType(sysRoleEntity.getType());
        sysRoleVO.setRemark(sysRoleEntity.getRemark());
        sysRoleVO.setMenuIdList(menuIdList);
        return sysRoleVO;
    }
}

