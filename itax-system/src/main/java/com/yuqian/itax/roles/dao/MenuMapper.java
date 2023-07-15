package com.yuqian.itax.roles.dao;

import com.yuqian.itax.roles.entity.MenuEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.roles.entity.query.MenuQuery;
import com.yuqian.itax.roles.entity.vo.MenuDetailVO;
import com.yuqian.itax.roles.entity.vo.MenuTreeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单信息dao
 * 
 * @Date: 2019年12月08日 20:58:38 
 * @author 蒋匿
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuEntity> {

    List<MenuEntity> queryMenuList(MenuQuery menuQuery);

    Integer insertMenu(MenuDetailVO sysMenu);

    List<MenuTreeVO> queryAllMenu();

    List<MenuTreeVO> queryAllMenuWithoutButton();

    MenuEntity queryMenuByUrl(@Param("url") String url, @Param("parentId") Long parentId);

    MenuDetailVO queryMenuDetail(Long id);

    Long queryLastInsertMenu();
}

