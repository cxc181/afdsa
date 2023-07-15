package com.yuqian.itax.roles.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.roles.entity.UserMenuRelaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户菜单关系表dao
 * 
 * @Date: 2020年04月20日 14:03:36 
 * @author 蒋匿
 */
@Mapper
public interface UserMenuRelaMapper extends BaseMapper<UserMenuRelaEntity> {

    List<UserMenuRelaEntity> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据角色id删除角色菜单授权信息
     * @param userId
     */
    void deleteByUserId(@Param("userId") Long userId);
}

