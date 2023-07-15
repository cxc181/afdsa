package com.yuqian.itax.roles.dao;

import com.yuqian.itax.roles.entity.RolesEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.roles.entity.query.RoleQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色管理dao
 * 
 * @Date: 2019年12月08日 20:58:54 
 * @author 蒋匿
 */
@Mapper
public interface RolesMapper extends BaseMapper<RolesEntity> {
    /**
     * 根据roleCode查询角色信息
     */
    RolesEntity getRolesEntityByRoleCode(@Param("roleCode")String roleCode,@Param("oemCode")String oemCode);

    List<RolesEntity> queryListByOrgId( RoleQuery roleQuery);
}

