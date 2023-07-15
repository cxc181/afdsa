package com.yuqian.itax.admin.controller.user;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.admin.controller.user.vo.UserVO;
import com.yuqian.itax.capital.entity.vo.UserCapitalAccountVO;
import com.yuqian.itax.capital.entity.query.UserCapitalAccountQuery;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.order.entity.ReceiveOrderEntity;
import com.yuqian.itax.order.service.ReceiveOrderService;
import com.yuqian.itax.orgs.entity.OrgEntity;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.user.entity.CustomerServiceWorkNumberEntity;
import com.yuqian.itax.user.entity.po.UserMenuPO;
import com.yuqian.itax.user.entity.po.UserPO;
import com.yuqian.itax.user.entity.query.UserQuery;
import com.yuqian.itax.user.entity.vo.SysUserVO;
import com.yuqian.itax.user.entity.vo.UserPageInfoVO;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.enums.UserAccountTypeEnum;
import com.yuqian.itax.user.enums.UserTypeEnum;
import com.yuqian.itax.user.service.CustomerServiceWorkNumberService;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.UserExtendService;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.workorder.entity.WorkOrderEntity;
import com.yuqian.itax.workorder.service.WorkOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    UserCapitalAccountService userCapitalAccountService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    UserBankCardService userBankCardService;
    @Autowired
    ReceiveOrderService receiveOrderService;
    @Autowired
    WorkOrderService workOrderService;
    @Autowired
    UserExtendService userExtendService;
    @Autowired
    UserService userService;
    @Autowired
    CustomerServiceWorkNumberService customerServiceWorkNumberService;

    /**
     * 用户余额
     * @author HZ
     * @date 2019/12/10
     */
    @PostMapping("/accountBalance")
    public ResultVo accountBalance(){
        //带登陆验证
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("userInfo", new UserVO(userEntity));

        UserCapitalAccountQuery query = new UserCapitalAccountQuery();
        List<UserCapitalAccountVO> list = null;
        query.setUserTypeNotIn(UserTypeEnum.MEMBER.getValue() + "");
        if (userEntity.getPlatformType() == 1 && Objects.equals(UserAccountTypeEnum.ADMIN.getValue(), userEntity.getAccountType())) {
            query.setPlatformType(1);
            query.setAccountType(1);
            list = userCapitalAccountService.listPlatformAccountBalance(query);
        } else {
            query.setOemCode(userEntity.getOemCode());
            query.setUserId(userEntity.getId());
            list = userCapitalAccountService.listAccountBalance(query);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            for (UserCapitalAccountVO vo : list) {
                vo.setAvailableAmount(vo.getAvailableAmount().divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN));
                vo.setOutstandingAmount(vo.getOutstandingAmount().divide(new BigDecimal("100"), 2, BigDecimal.ROUND_DOWN));
            }
        }
        map.put("balanceList", list);
        return ResultVo.Success(map);
    }

    /**
     * 系统用户列表
     * @author  HZ
     * @date 2019/12/10
     */
    @PostMapping("/userPageInfo")
    public ResultVo userPageInfo(@RequestBody  UserQuery userQuery){

        //带登陆验证
        getCurrUserId();
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            userQuery.setOemCode(getRequestHeadParams("oemCode"));
        }
        userQuery.setTree(getOrgTree());

        userQuery.setOemCode(getRequestHeadParams("oemCode"));
        PageInfo<UserPageInfoVO> userPageInfo=userService.userPageInfo(userQuery);

        return ResultVo.Success(userPageInfo);
    }
    /**
     * 系统用户新增
     * @author HZ
     * date 2019/12/10
     */
    @PostMapping("/addUser")
    public ResultVo addUser(@RequestBody @Validated UserPO userPO, BindingResult result)  {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        //带登陆验证
        getCurrUser();
        try {
            String oemCode=getRequestHeadParams("oemCode");
            if(StringUtils.isBlank(oemCode)){
                //查询选择登陆账号所在组织
                OrgEntity orgEntity=orgService.findById(userPO.getOrgId());
                userPO.setOemCode(orgEntity.getOemCode());
                //  V3.3新增  只有平台或oem才显示是否为坐席客服
                if (StringUtils.isBlank(orgEntity.getOemCode()) && userPO.getOrgId() != 1){
                    userPO.setIsCustomer(null);
                    //  如果是oem机构 客服坐席为必填
                }else if (StringUtils.isNotBlank(orgEntity.getOemCode())){
                    if (userPO.getIsCustomer() == null){
                        return ResultVo.Fail("请选择是否为客服坐席");
                    }
                }
            }else{
                userPO.setOemCode(oemCode);
            }
            //新增账号
            UserEntity userEntity=userCapitalAccountService.addUserCapitalAccount(userPO,getCurrUseraccount());
            //新增接单表数据
            if(userPO.getIsCustomer()==1){
                insertReceiveOrderEntity(userEntity,getCurrUseraccount());
            }
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }


    /**
     * 插入接单表数据
     */
    ReceiveOrderEntity insertReceiveOrderEntity(UserEntity userEntity,String userAccount){
        ReceiveOrderEntity receiveOrderEntity=new ReceiveOrderEntity();
        receiveOrderEntity.setUserId(userEntity.getId());
        receiveOrderEntity.setReceiveOrderNum(0);
        receiveOrderEntity.setOemCode(userEntity.getOemCode());
        receiveOrderEntity.setStatus(1);
        receiveOrderEntity.setAddTime(new Date());
        receiveOrderEntity.setAddUser(userAccount);
        receiveOrderService.insertSelective(receiveOrderEntity);
        return  receiveOrderEntity;
    }
    /**
     * 系统用户编辑
     * @author HZ
     * date 2019/12/10
     */
    @PostMapping("/updateUser")
    public ResultVo updateUser(@RequestBody UserPO userPO) throws BusinessException {
        //带登陆验证
        getCurrUser();
        ReceiveOrderEntity receiveOrderEntity = receiveOrderService.queryByUserId(userPO.getId());
        if (userPO.getIsCustomer() == 0){
            if (receiveOrderEntity != null){
                List<CustomerServiceWorkNumberEntity> customerServiceWorkNumberEntity=customerServiceWorkNumberService.queryCustomerServiceWorkNumberEntityByuserId(userPO.getId());
                if(customerServiceWorkNumberEntity.size()>0){
                   return ResultVo.Fail("客服坐席下的工号未删除！");
                }
                receiveOrderEntity.setStatus(0);
                receiveOrderService.editByIdSelective(receiveOrderEntity);
            }
        }else if(userPO.getIsCustomer() == 1){
            if (receiveOrderEntity == null){
                UserEntity userEntity= userService.findById(userPO.getId());
                insertReceiveOrderEntity(userEntity,getCurrUser().getUseraccount());
            }else{
                receiveOrderEntity.setStatus(1);
                receiveOrderService.editByIdSelective(receiveOrderEntity);
            }

        }
        //修改用户
        userService.updateUserEntityById(userPO,getCurrUseraccount());
        return ResultVo.Success();
    }
    /**
     * 系统用户状态变更 status
     * @author HZ
     * date 2019/12/14
     * status 0禁用1 可用 2注销
     */
    @PostMapping("/updateUserStatus")
    public ResultVo updateUserStatus(@JsonParam Long id,@JsonParam Integer status )  {
        //带登陆验证
        getCurrUser();
        try{
            UserEntity userEntity=userService.findById(id);
            if(userEntity.getStatus()==2){
                return ResultVo.Fail("用户已注销");
            }
            if(userEntity.getAccountType()==2){
                WorkOrderEntity workOrderEntity=new WorkOrderEntity();
                workOrderEntity.setCustomerServiceAccount(userEntity.getUsername());
                workOrderEntity.setWorkOrderStatus(1);
                List<WorkOrderEntity> list=workOrderService.select(workOrderEntity);
                if(list.size()>0){
                    return ResultVo.Fail("该用户还有未处理得工单，不能禁用或注销！");
                }
            }
            userService.cancelUser(id,status,getCurrUseraccount());

            //清除代理商token
            String oemCode = userEntity.getOemCode();
            if(StringUtils.isBlank(oemCode)){
                oemCode = "";
            }
            String token =redisService.get(RedisKey.LOGIN_TOKEN_KEY+oemCode+"_" + "userId_2_" + userEntity.getUsername());
            if(!org.apache.commons.lang3.StringUtils.isBlank(token)){
                redisService.delete(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_"+ token);
            }
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 系统用户重置密码
     * @author HZ
     * date 2019/12/14
     */
    @PostMapping("/resetPassword")
    public ResultVo resetPassword(@JsonParam Long id)  {
        //带登陆验证
        getCurrUser();
        userService.resetPassword(id,getCurrUseraccount());
        return ResultVo.Success();
    }

    /**
     * 根据Userid查询用户菜单详情
     * @param userId
     * @return
     */
    @PostMapping("selectSysUserById")
    public ResultVo selectByUserId(@JsonParam Long userId) {
        if(userId == null && userId == 0){
            return ResultVo.Fail("参数错误");
        }
        SysUserVO sysUserVO = userService.findSysUserVOByUserId(userId);
        return ResultVo.Success(sysUserVO);
    }

    /**
     * 用户权限配置
     * @athuor HZ
     */
    @PostMapping("/updateRoleMenuRela")
    public ResultVo updateRoleMenuRela(@RequestBody UserMenuPO userMenuPO){
        userService.updateUserMenuRelayEntity(userMenuPO,getCurrUserId());
        return ResultVo.Success();
    }
}
