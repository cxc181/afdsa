package com.yuqian.itax.roles.dao;

import com.yuqian.itax.roles.entity.RoleMenuRelaEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色对应菜单dao
 * 
 * @Date: 2019年12月08日 20:59:24 
 * @author 蒋匿
 */
@Mapper
public interface RoleMenuRelaMapper extends BaseMapper<RoleMenuRelaEntity> {
    List<RoleMenuRelaEntity> queryRoleMenu(@Param("roleId") Long roleId);

    /**
     * 根据角色id删除角色菜单授权信息
     * @param id
     */
    void deleteByRoleId(Long id);

    List<RoleMenuRelaEntity> selectByRoleId(Long id);
}

