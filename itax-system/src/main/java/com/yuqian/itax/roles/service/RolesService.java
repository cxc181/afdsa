package com.yuqian.itax.roles.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.roles.entity.RolesEntity;
import com.yuqian.itax.roles.dao.RolesMapper;
import com.yuqian.itax.roles.entity.po.RolePO;
import com.yuqian.itax.roles.entity.query.RoleQuery;
import com.yuqian.itax.roles.entity.vo.SysRoleVO;

/**
 * 角色管理service
 * 
 * @Date: 2019年12月08日 20:58:54 
 * @author 蒋匿
 */
public interface RolesService extends IBaseService<RolesEntity,RolesMapper> {

    /**
     * 根据组织ID查询分页角色列表
     * @author HZ
     */
    PageInfo<RolesEntity> pageRolesEntity( RoleQuery roleQuery);

    /**
     * 新增角色
     * @author HZ
     */
    RolesEntity addRolesEntity( RolePO rolePO,String oemCode,Long userId);

    /**
     * 修改角色
     * @author HZ
     */
    RolesEntity updateRolesEntity( RolePO rolePO,Long userId) throws BusinessException;

    /**
     * 角色权限配置
     * @author HZ
     */
    void updateRoleMenuRelayEntity( RolePO rolePO,Long userId);

    SysRoleVO findSysRoleVOById(Long id);

}

