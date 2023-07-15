package com.yuqian.itax.roles.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.roles.entity.MenuEntity;
import com.yuqian.itax.roles.dao.MenuMapper;
import com.yuqian.itax.roles.entity.query.MenuQuery;
import com.yuqian.itax.roles.entity.vo.MenuDetailVO;
import com.yuqian.itax.roles.entity.vo.MenuTreeVO;

import java.util.List;

/**
 * 菜单信息service
 * 
 * @Date: 2019年12月08日 20:58:38 
 * @author 蒋匿
 */
public interface MenuService extends IBaseService<MenuEntity,MenuMapper> {
    /**
     * 分页查询菜单
     * @author Karen
     * @param menuQuery
     * @return
     */
    PageResultVo<MenuEntity> queryMenuList(MenuQuery menuQuery);
    /**
     * 新增菜单按钮
     * @author Karen
     * @param sysMenu
     * @return
     */
    ResultVo addSysMenu(MenuDetailVO sysMenu);
    /**
     * 修改菜单
     * @author Karen
     * @param sysMenu
     */
    ResultVo updateSysMenu(MenuDetailVO sysMenu);
    /**
     * 删除菜单
     * @author Karen
     * @param id
     * @return
     */
    ResultVo deleteSysMenu(Long id);
    /**
     * 获取菜单树
     * @author Karen
     * @return
     */
    List<MenuTreeVO> queryMenuTree();

    /**
     * 查询菜单详情
     * @param id
     * @return
     */
    ResultVo queryMenuDetail(Long id);
    /**
     * 获取全部菜单树（包含按钮）
     * @author Karen
     * @return
     */
    List<MenuTreeVO> queryMenuTreeAll();
}

