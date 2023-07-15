package com.yuqian.itax.admin.controller.system;

import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.annotation.OperatorLog;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.roles.entity.MenuEntity;
import com.yuqian.itax.roles.entity.query.MenuQuery;
import com.yuqian.itax.roles.entity.vo.MenuDetailVO;
import com.yuqian.itax.roles.entity.vo.MenuTreeByUserIdVO;
import com.yuqian.itax.roles.entity.vo.MenuTreeVO;
import com.yuqian.itax.roles.entity.vo.MenuVueRelaTreeVO;
import com.yuqian.itax.roles.service.MenuService;
import com.yuqian.itax.roles.service.MenuVueRelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单管理模块
 * @author Karen
 * @Date 9:36 2019/8/29
 */
@RestController
@RequestMapping("/system/menu")
public class MenuController extends BaseController {

	@Autowired
	private MenuService menuService;

	@Autowired
	private MenuVueRelaService menuVueRelaService;
	/**
	 * 查询菜单列表
	 * @param menuQuery
	 * @return
	 */
	@PostMapping("/queryMenuList")
	//@OperatorLog(module="菜单管理模块",operDes="查询菜单列表",oprType=0)
	public ResultVo queryMenuList(@RequestBody MenuQuery menuQuery) {
		if (null == menuQuery) {
			return ResultVo.Fail("参数错误");
		}
		PageResultVo<MenuEntity> menuList = menuService.queryMenuList(menuQuery);
		return ResultVo.Success(menuList);

	}
	/**
	 * 查询菜单详情
	 * @author Karen
	 * @param id
	 * @return
	 */
	@PostMapping("/getMenuInfo")
	@OperatorLog(module="菜单管理模块",operDes="查询菜单详情",oprType=0)
	public ResultVo getMenuById(@JsonParam Long id) {
		if (null == id) {
			return ResultVo.Fail("参数错误");
		}
		return menuService.queryMenuDetail(id);
	}

	/**
	 * 新增菜单
	 * @param sysMenu
	 * @return
	 */
	@PostMapping("/addSysMenu")
	@OperatorLog(module="菜单管理模块",operDes="新增菜单按钮",oprType=1)
	public ResultVo addSysMenu(@RequestBody MenuDetailVO sysMenu) {
		if (null == sysMenu) {
			return ResultVo.Fail("参数错误");
		}
		//CurrUser currUser = getCurrUser();
		try{
			sysMenu.setAddUser(1L);
			menuService.addSysMenu(sysMenu);
			return  ResultVo.Success();
		}catch (BusinessException e){
			return ResultVo.Fail(e.getMessage());
		}
	}
	/**
	 * 修改菜单信息
	 * @author Karen
	 * @param sysMenu
	 * @return
	 */
	@PostMapping("/updateSysMenu")
	@OperatorLog(module="菜单管理模块",operDes="修改菜单信息",oprType=2)
	public ResultVo updateSysMenu(@RequestBody MenuDetailVO sysMenu) {
		if (null == sysMenu || null == sysMenu.getId()) {
			return ResultVo.Fail("参数错误");
		}
		CurrUser currUser = getCurrUser();
		sysMenu.setAddUser(currUser.getUserId());
		return menuService.updateSysMenu(sysMenu);
	}
	/**
	 * 删除菜单
	 * @author Karen
	 * @param id
	 * @return
	 */
	@PostMapping("/deleteSysMenu")
	@OperatorLog(module="菜单管理模块",operDes="删除菜单",oprType=3)
	public ResultVo deleteSysMenu(@JsonParam Long id) {
		if (null == id) {
			return ResultVo.Fail("参数错误");
		}
		return menuService.deleteSysMenu(id);
	}
	/**
	 * 查询菜单树（不包含按钮）
	 * @author Karen
	 * @return
	 */
	@PostMapping("/menuTreeList")
	@OperatorLog(module="菜单管理模块",operDes="查询菜单树（不包含按钮）",oprType=0)
	public ResultVo menuTreeList() {
	    List<MenuTreeVO>  menuTree = menuService.queryMenuTree();
	    if (null != menuTree) {
            return ResultVo.Success(menuTree);
        }
	    return ResultVo.Fail();
	}

	/**
	 * 获取用户角色树信息
	 * @author Karen
	 * @return
	 */
	@PostMapping("/getMenuTreeByRole")
	@OperatorLog(module="菜单管理模块",operDes="获取用户角色树信息",oprType=0)
	public ResultVo roleTreeList() {
	    CurrUser currUser = getCurrUser();
	    try{
			MenuVueRelaTreeVO routeTree = menuVueRelaService.queryMenuTreeByRole(currUser.getUserId());
			if (null != routeTree) {
				return ResultVo.Success(routeTree);
			}
		}catch (BusinessException e){
			return ResultVo.Fail(e.getMessage());
		}
	    return ResultVo.Fail();
	}
	/**
	 * 获取登陆用户菜单信息
	 * @author HZ
	 * @return
	 */
	@PostMapping("/menuTreeListByUserId")
	@OperatorLog(module="菜单管理模块",operDes="获取用户角色树信息",oprType=0)
	public ResultVo menuTreeListByUserId() {
		CurrUser currUser = getCurrUser();
		try{
			List<MenuTreeByUserIdVO> list= new ArrayList<>();
			if(currUser.getUsertype()!=null &&"1".equals(currUser.getUsertype())){
				 list = menuVueRelaService.menuTreeListByUserId(null);
			}else{
				 list = menuVueRelaService.menuTreeListByUserId(currUser.getUserId());
			}
			if (null != list) {
				return ResultVo.Success(list);
			}
		}catch (BusinessException e){
			return ResultVo.Fail(e.getMessage());
		}
		return ResultVo.Fail();
	}

	/**
	 * 查询所有菜单树（包含按钮）
	 * @author Karen
	 * @return
	 */
	@PostMapping("/menuTreeListAll")
	@OperatorLog(module="菜单管理模块",operDes="查询所有菜单树（包含按钮）",oprType=0)
	public ResultVo menuTreeListAll() {
	    List<MenuTreeVO>  menuTree = menuService.queryMenuTreeAll();
	    if (null != menuTree) {
	        return ResultVo.Success(menuTree);
	    }
	    return ResultVo.Fail();
	}

}