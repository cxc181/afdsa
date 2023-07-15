package com.yuqian.itax.roles.service.impl;

import com.alibaba.fastjson.JSON;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.enums.AccountTypeEnum;
import com.yuqian.itax.roles.dao.MenuVueRelaMapper;
import com.yuqian.itax.roles.entity.MenuVueRelaEntity;
import com.yuqian.itax.roles.entity.vo.*;
import com.yuqian.itax.roles.service.MenuVueRelaService;
import com.yuqian.itax.user.dao.UserMapper;
import com.yuqian.itax.user.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("menuVueRelaService")
public class MenuVueRelaServiceImpl extends BaseServiceImpl<MenuVueRelaEntity,MenuVueRelaMapper> implements MenuVueRelaService {
    @Resource
    private MenuVueRelaMapper  menuVueRelaMapper;

    @Resource
    private UserMapper userMapper;
    /**
     * 新增菜单VUE
     * @author Karen
     * @param sysMenu
     * @return
     */
    @Override
    public ResultVo insertMenuVue(MenuDetailVO sysMenu, String meta) {
        try {
            Integer result = menuVueRelaMapper.insertMenuVue(sysMenu,meta);
            if (null == result) {
                throw new BusinessException("新增菜单VUE失败");
            }
            return ResultVo.Success();
        } catch (Exception e) {
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 修改菜单VUE
     * @author Karen
     * @param sysMenu
     * @return
     */
    @Override
    public Integer updateMenuVue(MenuDetailVO sysMenu,String meta) throws BusinessException {
        MenuVueRelaEntity menuVue = menuVueRelaMapper.getSysMenuVueByMenuId(sysMenu.getId());
        //如果不存在菜单vue就新增
        if (null == menuVue) {
            this.insertMenuVue(sysMenu,meta);
        }
        Integer rows = menuVueRelaMapper.updateMenuVue(sysMenu,meta);
        if (null == rows) {
            throw new BusinessException("菜单vue修改失败");
        }
        return rows;
    }

    /**
     * 删除菜单VUE
     * @param id
     * @return
     * @throws BusinessException
     */
    @Override
    public Integer deleteMenuVueByMenuId(Long id) throws BusinessException {
        Integer rows = menuVueRelaMapper.deleteByMenuId(id);
        if (null == rows) {
            throw new BusinessException("菜单vue删除失败");
        }
        return rows;
    }
    /**
     * 根据角色查询菜单树
     * @param userId
     * @return
     */
    @Override
    public MenuVueRelaTreeVO queryMenuTreeByRole(Long userId) {
        MenuVueRelaTreeVO menuVueEleTreeVO = new MenuVueRelaTreeVO();
        UserEntity sysUser = userMapper.selectByPrimaryKey(userId);
        if(null == sysUser){
            throw  new BusinessException("会员不存在");
        }
        if (sysUser.getPlatformType()!=null&& sysUser.getPlatformType()==1&& sysUser.getAccountType()==1) {
            List<MenuVueRelaVO> menuVueEleList = menuVueRelaMapper.queryMenuVueEleByUserId(null);
            _getMenuTreeList(null, menuVueEleList, menuVueEleTreeVO);
        }else {
            List<MenuVueRelaVO> menuVueEleList = menuVueRelaMapper.queryMenuVueEleByUserId(userId);
            _getMenuTreeList(null, menuVueEleList, menuVueEleTreeVO);
        }
        return menuVueEleTreeVO;
    }

    /**
     * 根据角色查询菜单树
     * @param userId
     * @return
     */
    @Override
    public List<MenuTreeByUserIdVO> menuTreeListByUserId(Long userId) {
        //查询出不包含按钮的所有菜单
        List<MenuTreeByUserIdVO> menuTreeVOList = menuVueRelaMapper.menuTreeListByUserId(userId);
        List<MenuTreeByUserIdVO> menuList = new ArrayList<>();
        for (MenuTreeByUserIdVO treeMenu: menuTreeVOList) {
            if (treeMenu.getParentId()!=null &&treeMenu.getParentId().equals(0L)) {
                menuList.add(treeMenu);
            }
        }
        for (MenuTreeByUserIdVO menu: menuList) {
            menu.setChildren(getChildren(menu.getId(),menuTreeVOList));
        }
        return menuList;
    }

    /**
     * 获取菜单树的子菜单
     * @author Karen
     * @param id
     * @param menuList
     * @return
     */
    private List<MenuTreeByUserIdVO> getChildren(Long id, List<MenuTreeByUserIdVO> menuList) {
        List<MenuTreeByUserIdVO> childList = new ArrayList<>();
        for (MenuTreeByUserIdVO menu: menuList) {
            if (menu.getParentId().equals(id)) {
                childList.add(menu);
            }
        }
        for (MenuTreeByUserIdVO childMenu: childList) {
            childMenu.setChildren(getChildren(childMenu.getId(),menuList));
        }
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }


    /**
     * 构建树节点递归
     */
    private void _getMenuTreeList(MenuVueRelaVO parentMenuVueEle, List<MenuVueRelaVO> menuList, MenuVueRelaTreeVO menuVueEleBean) {
        if (null != parentMenuVueEle) {
            _bulidMenuVueEleBean(menuVueEleBean, parentMenuVueEle);
        }
        for (MenuVueRelaVO menuVueEle : menuList) {
            // 如果没有指定父节点,默认从 0 开始查找
            if (null == parentMenuVueEle) {
                if (menuVueEle.getParentMenuId().equals(0L)) {
                    _getMenuTreeList(menuVueEle, menuList, menuVueEleBean);
                }
            } else if (menuVueEle.getParentMenuId().equals(parentMenuVueEle.getMenuId())) {
                MenuVueRelaTreeVO childTreeMenuBean = new MenuVueRelaTreeVO();
                // 建立父子关系
                if (null == menuVueEleBean.getChildren()) {
                    List<MenuVueRelaTreeVO> children = new ArrayList<>();
                    menuVueEleBean.setChildren(children);
                }
                menuVueEleBean.getChildren().add(childTreeMenuBean);
                _getMenuTreeList(menuVueEle, menuList, childTreeMenuBean);
            }
        }
    }

    private void _bulidMenuVueEleBean(MenuVueRelaTreeVO menuVueEleBean, MenuVueRelaVO menuVueEle) {
        menuVueEleBean.setPath(menuVueEle.getPath());
        menuVueEleBean.setOrderNum(menuVueEle.getOrderNum());
        if (menuVueEle.getHidden().equals(0)) {
            menuVueEleBean.setHidden(false);
        } else {
            menuVueEleBean.setHidden(true);
        }
        menuVueEleBean.setRedirect(menuVueEle.getRedirect());
        menuVueEleBean.setComponent(menuVueEle.getComponent());
        menuVueEleBean.setName(menuVueEle.getName());
        menuVueEleBean.setType(menuVueEle.getType());
        Map<String, Object> meta = new HashMap<>();
        if (!StringUtils.isEmpty(menuVueEle.getMeta())) {
            meta = JSON.parseObject(menuVueEle.getMeta());
            menuVueEleBean.setMeta(meta);
        }

    }
}

