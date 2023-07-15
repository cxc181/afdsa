package com.yuqian.itax.roles.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.roles.entity.MenuVueRelaEntity;
import com.yuqian.itax.roles.dao.MenuVueRelaMapper;
import com.yuqian.itax.roles.entity.vo.MenuDetailVO;
import com.yuqian.itax.roles.entity.vo.MenuTreeByUserIdVO;
import com.yuqian.itax.roles.entity.vo.MenuTreeVO;
import com.yuqian.itax.roles.entity.vo.MenuVueRelaTreeVO;

import java.util.List;

/**
 * 菜单对应的vue组件service
 * 
 * @Date: 2019年12月08日 20:59:10 
 * @author 蒋匿
 */
public interface MenuVueRelaService extends IBaseService<MenuVueRelaEntity,MenuVueRelaMapper> {
    ResultVo insertMenuVue(MenuDetailVO sysMenu, String meta);

    Integer updateMenuVue(MenuDetailVO sysMenu, String meta) throws BusinessException;

    Integer deleteMenuVueByMenuId(Long id) throws BusinessException;
    /**
     * 根据角色获取菜单树
     * @author Karen
     * @param userId
     * @return
     */
    MenuVueRelaTreeVO queryMenuTreeByRole(Long userId);

    /**
     * 根据用户获取菜单（不包含按钮）
     * @author Karen
     * @param userId
     * @return
     */
    List<MenuTreeByUserIdVO> menuTreeListByUserId(Long userId);

}

