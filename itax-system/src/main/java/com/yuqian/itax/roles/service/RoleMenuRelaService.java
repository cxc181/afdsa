package com.yuqian.itax.roles.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.roles.entity.RoleMenuRelaEntity;
import com.yuqian.itax.roles.dao.RoleMenuRelaMapper;

import java.util.List;

/**
 * 角色对应菜单service
 * 
 * @Date: 2019年12月08日 20:59:24 
 * @author 蒋匿
 */
public interface RoleMenuRelaService extends IBaseService<RoleMenuRelaEntity,RoleMenuRelaMapper> {
    /**
     * 按菜单ID删除
     * @author Karen
     * @param id
     * @return
     */
    Integer deleteByMenuId(Long id);
    /**
     * 根据roleId查询菜单
     * @author Karen
     * @param roleId
     * @return
     */
    List<RoleMenuRelaEntity> queryRoleMenu(Long roleId);

    Long[] findByRoleId(Long id);
}

