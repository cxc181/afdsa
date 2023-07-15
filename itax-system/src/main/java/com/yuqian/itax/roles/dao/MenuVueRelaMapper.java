package com.yuqian.itax.roles.dao;

import com.yuqian.itax.roles.entity.MenuVueRelaEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.roles.entity.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单对应的vue组件dao
 * 
 * @Date: 2019年12月08日 20:59:10 
 * @author 蒋匿
 */
@Mapper
public interface MenuVueRelaMapper extends BaseMapper<MenuVueRelaEntity> {
    Integer insertMenuVue(@Param("vue") MenuDetailVO sysMenu, @Param("meta")String meta);

    MenuVueRelaEntity getSysMenuVueByMenuId(@Param("menuId") Long menuId);

    Integer updateMenuVue(@Param("vue") MenuDetailVO sysMenu, @Param("meta")String meta);

    Integer deleteByMenuId(@Param("menuId") Long menuId);

    List<MenuVueRelaVO> queryMenuVueEleByUserId(@Param("userId") Long userId);

    List<MenuTreeByUserIdVO> menuTreeListByUserId(@Param("userId") Long userId);

}

