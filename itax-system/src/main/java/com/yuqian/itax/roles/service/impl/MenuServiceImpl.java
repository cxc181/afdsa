package com.yuqian.itax.roles.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.enums.SysMenuTypeEnum;
import com.yuqian.itax.roles.dao.MenuMapper;
import com.yuqian.itax.roles.entity.MenuEntity;
import com.yuqian.itax.roles.entity.query.MenuQuery;
import com.yuqian.itax.roles.entity.vo.MenuDetailVO;
import com.yuqian.itax.roles.entity.vo.MenuTreeVO;
import com.yuqian.itax.roles.service.MenuService;
import com.yuqian.itax.roles.service.MenuVueRelaService;
import com.yuqian.itax.roles.service.RoleMenuRelaService;
import com.yuqian.itax.roles.service.UserRoleRelaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;


@Service("menuService")
@Slf4j
public class MenuServiceImpl extends BaseServiceImpl<MenuEntity,MenuMapper> implements MenuService {
    @Resource
    private MenuMapper menuMapper;

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuVueRelaService menuVueRelaService;

    @Autowired
    private RoleMenuRelaService roleMenuRelaService;
    /**
     * 查询菜单分页列表
     * @author Karen
     */
    @Override
    public PageResultVo<MenuEntity> queryMenuList(MenuQuery menuQuery) {
        //默认查询目录
        if (null == menuQuery.getId()) {
            menuQuery.setId(1L);
        }
        PageHelper.startPage(menuQuery.getPageNumber(), menuQuery.getPageSize());
        PageInfo<MenuEntity> pageInfo = new PageInfo<MenuEntity>(menuMapper.queryMenuList(menuQuery));
        PageResultVo<MenuEntity> result = new PageResultVo<MenuEntity>();
        result.setList(pageInfo.getList());
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        return result;
    }
    /**
     * 新增菜单
     * @author Karen
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo addSysMenu(MenuDetailVO sysMenu) {
        if (!StringUtils.isEmpty(sysMenu.getUrl().trim())){
            MenuEntity sysMenuEntity = menuMapper.queryMenuByUrl(sysMenu.getUrl(),sysMenu.getParentId());
            if (null != sysMenuEntity) {
                throw  new BusinessException("菜单路径已存在");
            }
        }
        MenuEntity parentMenu = null;
        String level = "1";
        if (!sysMenu.getParentId().equals(0L)) {
            parentMenu = menuMapper.selectByPrimaryKey(sysMenu.getParentId());
            if (null == parentMenu) {
                throw  new BusinessException("父菜单不存在");
            }
            level = ""+(Integer.parseInt(parentMenu.getLevel()) + 1);
        }
        sysMenu.setLevel(level);
        Date createTime = new Date();
        sysMenu.setAddTime(createTime);
        menuMapper.insertMenu(sysMenu);
        Long menuId = menuMapper.queryLastInsertMenu();
        sysMenu.setId(menuId);
        //新增菜单VUE
        Map<String, Object> meta = new HashMap<>();
        meta.put("title", sysMenu.getTitle());
        meta.put("icon", sysMenu.getIcon());
        String vueMeta = JSON.toJSONString(meta);
        ResultVo result = menuVueRelaService.insertMenuVue(sysMenu,vueMeta);
        if (!"0000".equals(result.getRetCode())) {
            throw  new BusinessException(result.getRetMsg());
        }
        //如菜单类型为按钮，新增菜单权限
        if (sysMenu.getType().equals(SysMenuTypeEnum.BUTTON.getValue())&&sysMenu.getApiIdList().size()>0) {
          /*  ResultVo resultVo = sysMenuApiService.saveMenuApi(sysMenu);
            if (!resultVo.getRetCode().equals("0000")) {
                return ResultVo.Fail(resultVo.getRetMsg());
            }*/
        }
        return ResultVo.Success();
    }

    /**
     * 修改菜单
     * @author Karen
     * @throws BusinessException
     */
    @Transactional
    @Override
    public ResultVo updateSysMenu(MenuDetailVO sysMenu) {
        try {
            MenuEntity menu = menuMapper.selectByPrimaryKey(sysMenu.getId());
            if (null == menu) {
                throw new BusinessException("不存在菜单");
            }
            if (!StringUtils.isEmpty(sysMenu.getUrl())) {
                MenuEntity sysMenuEntity = menuMapper.queryMenuByUrl(sysMenu.getUrl(),sysMenu.getParentId());
                if (null != sysMenuEntity && !sysMenuEntity.getId().equals(sysMenu.getId())) {
                    throw new BusinessException("菜单路径已存在");
                }
            }
            MenuEntity parentMenu = null;
            String level = "1";
            if (sysMenu.getParentId()!=0) {
                parentMenu = menuMapper.selectByPrimaryKey(sysMenu.getParentId());
                if (ObjectUtils.isEmpty(parentMenu)) {
                    throw new BusinessException("父菜单不存在");
                }
                level = ""+(Integer.parseInt(parentMenu.getLevel()) + 1);
            }
            menu.setLevel(level);
            menu.setIcon(sysMenu.getIcon());
            menu.setName(sysMenu.getTitle());
            menu.setOrderNum(sysMenu.getOrderNum());
            menu.setType(sysMenu.getType());
            menu.setUrl(sysMenu.getUrl());
            menu.setParentId(sysMenu.getParentId());
            menu.setUpdateTime(new Date());
            menu.setUpdateUser(String.valueOf(sysMenu.getAddUser()));
            menuMapper.updateByPrimaryKey(menu);
            //修改菜单VUE
            Map<String, Object> meta = new HashMap<>();
            meta.put("title", sysMenu.getTitle());
            meta.put("icon", sysMenu.getIcon());
            String vueMeta = JSON.toJSONString(meta);
            menuVueRelaService.updateMenuVue(sysMenu,vueMeta);
            //如果菜单为按钮则修改菜单权限关系
            if (sysMenu.getType().equals(SysMenuTypeEnum.BUTTON.getValue())) {
               /* ResultVo resultVo = sysMenuApiService.updateMenuApi(sysMenu);
                if (!resultVo.getRetCode().equals("0000")) {
                    throw new BusinessException(resultVo.getRetMsg());
                }*/
            }
            return ResultVo.Success();
        } catch (Exception e) {
            return ResultVo.Fail(e.getMessage());
        }
    }
    /**
     * 删除菜单
     * @author Karen
     */
    @Transactional
    @Override
    public ResultVo deleteSysMenu(Long id) {
        try {
            menuMapper.deleteByPrimaryKey(id);
            menuVueRelaService.deleteMenuVueByMenuId(id);
            //sysMenuApiService.deleteByMenuId(id);
            roleMenuRelaService.deleteByMenuId(id);
            deleteChild(id);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultVo.Fail(e.getMessage());
        }
    }
    /**
     * 删除子菜单
     * @param parentId
     */
    private void deleteChild(Long parentId) throws BusinessException{
        Example example = new Example(MenuEntity.class);
        example.createCriteria().andEqualTo("parentId",parentId);
        List<MenuEntity> menuList = menuMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(menuList)) {
            for (MenuEntity sysMenuEntity : menuList) {
                Long id = sysMenuEntity.getId();
                menuMapper.deleteByPrimaryKey(id);
                menuVueRelaService.deleteMenuVueByMenuId(id);
                //sysMenuApiService.deleteByMenuId(id);
                roleMenuRelaService.deleteByMenuId(id);
                deleteChild(id);
            }
        }
    }
    /**
     * 查询菜单树
     * @author Karen
     */
    @Override
    public List<MenuTreeVO>  queryMenuTree() {
        //查询出不包含按钮的所有菜单
        List<MenuTreeVO> menuTreeVOList = menuMapper.queryAllMenuWithoutButton();
        List<MenuTreeVO> menuList = new ArrayList<>();
        for (MenuTreeVO treeMenu: menuTreeVOList) {
            if (treeMenu.getParentId().equals(0L)) {
                menuList.add(treeMenu);
            }
        }
        for (MenuTreeVO menu: menuList) {
            menu.setChildren(getChildren(menu.getId(),menuTreeVOList));
        }
        return menuList;
    }
    /**
     * 获取全部菜单树（包含按钮）
     * @author Karen
     * @return
     */
    @Override
    public List<MenuTreeVO> queryMenuTreeAll() {
        List<MenuTreeVO> menuTreeVOList = menuMapper.queryAllMenu();
        List<MenuTreeVO> menuList = new ArrayList<>();
        for (MenuTreeVO treeMenu: menuTreeVOList) {
            if (treeMenu.getParentId().equals(0L)) {
                menuList.add(treeMenu);
            }
        }
        for (MenuTreeVO menu: menuList) {
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
    private List<MenuTreeVO> getChildren(Long id, List<MenuTreeVO> menuList) {
        List<MenuTreeVO> childList = new ArrayList<>();
        for (MenuTreeVO menu: menuList) {
            if (menu.getParentId().equals(id)) {
                childList.add(menu);
            }
        }
        for (MenuTreeVO childMenu: childList) {
            childMenu.setChildren(getChildren(childMenu.getId(),menuList));
        }
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }

    /**
     * 查询菜单信息详情
     * @author Karen
     * @param id
     * @return
     */
    @Override
    public ResultVo queryMenuDetail(Long id) {
        try {
            MenuDetailVO menuDetail = menuMapper.queryMenuDetail(id);
            if (null == menuDetail) {
                throw new BusinessException("菜单为空");
            }
            if (menuDetail.getType().equals(SysMenuTypeEnum.BUTTON.getValue())) {

            }
            return ResultVo.Success(menuDetail);
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResultVo.Fail(e.getMessage());
        }
    }
}

