package com.yuqian.itax.admin.controller.system;


import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.orgs.entity.vo.OrgVO;
import com.yuqian.itax.orgs.service.OrgService;
import com.yuqian.itax.roles.entity.RolesEntity;
import com.yuqian.itax.roles.entity.po.RolePO;
import com.yuqian.itax.roles.entity.query.RoleQuery;
import com.yuqian.itax.roles.entity.vo.SysRoleVO;
import com.yuqian.itax.roles.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @Author HZ
 * @Description 角色管理模块
 * @Date 2019-08-28 09:41
 * @return
 **/
@CrossOrigin
@RestController
@RequestMapping("/sys/role/")
public class SysRoleController extends BaseController {

    @Autowired
    private RolesService rolesServicee;

    /**
     * 更具组织Id分页查询角色
     * @athuor HZ
     * @return
     */
    @PostMapping("/queryRolesPage")
    //@OperatorLog(module = "角色管理模块",operDes = "分页查询角色",oprType=0)
    public ResultVo queryRolesPage(@RequestBody RoleQuery roleQuery){
        PageInfo<RolesEntity> page =(rolesServicee.pageRolesEntity(roleQuery));
        return ResultVo.Success(page);
    }


    /**
     * 新增角色
     * @athuor HZ
     */
    @PostMapping("/addRole")
    //@OperatorLog(module = "角色管理模块",operDes = "分页查询角色",oprType=0)
    public ResultVo addRole(@RequestBody RolePO rolePO){
        String oemCode=getRequestHeadParams("oemCode");
        try{
            rolesServicee.addRolesEntity(rolePO,oemCode,getCurrUserId());
            return ResultVo.Success();
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 角色编辑
     * @athuor HZ
     */
    @PostMapping("/updateRole")
    //@OperatorLog(module = "角色管理模块",operDes = "分页查询角色",oprType=0)
    public ResultVo updateRole(@RequestBody RolePO rolePO){
        String oemCode=getRequestHeadParams("oemCode");
        try {
            rolesServicee.updateRolesEntity(rolePO,getCurrUserId());
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 角色状态编辑
     * @athuor HZ
     */
    @PostMapping("/updateRoleStatus")
    //@OperatorLog(module = "角色管理模块",operDes = "分页查询角色",oprType=0)
    public ResultVo updateRoleStatus(@RequestBody RolePO rolePO){
        String oemCode=getRequestHeadParams("oemCode");
        try {
            rolesServicee.updateRolesEntity(rolePO,getCurrUserId());
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 角色权限配置
     * @athuor HZ
     */
    @PostMapping("/updateRoleMenuRela")
    //@OperatorLog(module = "角色管理模块",operDes = "分页查询角色",oprType=0)
    public ResultVo updateRoleMenuRela(@RequestBody RolePO rolePO){
        String oemCode=getRequestHeadParams("oemCode");
        rolesServicee.updateRoleMenuRelayEntity(rolePO,getCurrUserId());
        return ResultVo.Success();
    }

    /**
     * 角色详情
     * @athuor HZ
     */
    @PostMapping("/roleDetail")
    //@OperatorLog(module = "角色管理模块",operDes = "分页查询角色",oprType=0)
    public ResultVo roleDetail(@JsonParam Long id){
        try {
            RolesEntity rolesEntity=rolesServicee.findById(id);
            return ResultVo.Success(rolesEntity);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
    }





    /**
     * 修改角色
     * @param sysRoleQuery
     * @return
     */
    /*@PostMapping("editRole")
    @OperatorLog(module = "角色管理模块",operDes = "修改角色",oprType=2)
    public ResultVo editRole(@RequestBody SysRoleQuery sysRoleQuery){
        if(   sysRoleQuery.getId() == 0||
                StringUtils.isEmpty(sysRoleQuery.getName()) ||
                sysRoleQuery.getMenuIdList().size() == 0
     ){
            return ResultVo.Fail("id,角色名,授权,状态皆不能为空");
        }
        //角色唯一校验
        List<SysRoleEntity> sysRoleEntitys = sysRoleService.selectSysRoleByNameAndId(sysRoleQuery.getName(),sysRoleQuery.getId());
        if (sysRoleEntitys.size() != 1 ){
            return ResultVo.Fail("此角色名已经存在,修改失败");
        }
//        sysRoleQuery.setCreateUserId(getCurrUserId());
        sysRoleQuery.setCreateTime(new Date());
        sysRoleService.editSysRole(sysRoleQuery);
        return ResultVo.Success();
    }*/


    /**
     * 根据id查询角色详情
     * @param id
     * @return
     */
    @PostMapping("selectSysRoleById")
    //@OperatorLog(module = "角色管理模块",operDes = "根据id查询角色详情",oprType=0)
    public ResultVo selectById(@JsonParam Long id) {
        if(id == null && id == 0){
            return ResultVo.Fail("参数错误");
        }
        SysRoleVO sysRoleVO = rolesServicee.findSysRoleVOById(id);
        return ResultVo.Success(sysRoleVO);
    }


    /**
     * 预设角色列表（分页）
     * @athuor HZ
     * @return
     */
    @PostMapping("/querySysRolesPage")
    //@OperatorLog(module = "角色管理模块",operDes = "分页查询角色",oprType=0)
    public ResultVo querySysRolesPage(@RequestBody RoleQuery roleQuery){
        getCurrUserId();//登路校验
        roleQuery.setType(0);//查询系统预设角色
        PageInfo<RolesEntity> page =(rolesServicee.pageRolesEntity(roleQuery));
        return ResultVo.Success(page);
    }


}

