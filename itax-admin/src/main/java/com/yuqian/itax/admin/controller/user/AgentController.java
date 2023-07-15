package com.yuqian.itax.admin.controller.user;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.orgs.service.UserOrgRelaService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.po.AgentPO;
import com.yuqian.itax.user.entity.po.AgentUpdatePO;
import com.yuqian.itax.user.entity.query.AgentQuery;
import com.yuqian.itax.user.entity.vo.AgentDetailVO;
import com.yuqian.itax.user.entity.vo.AgentPageInfoVO;
import com.yuqian.itax.user.service.MemberAccountService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/agent")
public class AgentController extends BaseController {

    @Autowired
    UserBankCardService userBankCardService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    UserCapitalAccountService userCapitalAccountService;
    @Autowired
    UserOrgRelaService userOrgRelaService;
    /**
     * 代理商列表
     * @author  HZ
     * @date 2019/12/10
     */
    @PostMapping("/agentPageInfo")
    public ResultVo agentPageInfo(@RequestBody AgentQuery agentQuery){
        //带登陆验证
        getCurrUserId();
        if(!"1".equals(getCurrUser().getUsertype()) &&!"3".equals(getCurrUser().getUsertype())){
            agentQuery.setOemCode(getRequestHeadParams("oemCode"));
        }
        try {

            agentQuery.setTree(getOrgTree());
            agentQuery.setUserId(getCurrUserId());
            PageInfo<AgentPageInfoVO> agentPageInfo=userService.agentPageInfo(agentQuery);
             return ResultVo.Success(agentPageInfo);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 代理商详情
     * @author  HZ
     * @date 2019/12/10
     */
    @PostMapping("/agentDetail")
    public ResultVo agentDetail(@JsonParam Long id){
        //带登陆验证
        getCurrUserId();
        String oemCode=getRequestHeadParams("oemCode");
        try {
            AgentDetailVO agentDetailVO=userService.agentDetail(id);
            userBankCardService.getBankCardInfo(agentDetailVO,oemCode);
            return ResultVo.Success(agentDetailVO);
        }catch (BusinessException e){
            return  ResultVo.Fail(e.getMessage());
        }
    }


    /**
     * 代理商新增
     * @author HZ
     * date 2019/12/10
     */
    @PostMapping("/addagent")
    public ResultVo addagent(@RequestBody @Validated AgentPO agentPO, BindingResult result)  {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        //带登陆验证
        getCurrUser();
        if(null == agentPO.getOemCode()){
            return  ResultVo.Fail("请选择所属机构");
        }
        UserEntity userEntity=userService.findById(getCurrUserId());
        if(userEntity.getPlatformType()==3){
            return  ResultVo.Fail("园区账号不能新增代理商。");
        }
        try {
            MemberAccountEntity memberAccountEntity = memberAccountService.queryByAccount(agentPO.getBindingAccount(),agentPO.getOemCode());
            if(memberAccountEntity!=null){
                throw  new BusinessException("推广账号手机号已存在，请更换！");
            }
            //新增账号
            userCapitalAccountService.addAgentCapitalAccount(agentPO,getCurrUseraccount());
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 代理商编辑
     * @author HZ
     * date 2019/12/10
     */
    @PostMapping("/updateAgent")
    public ResultVo updateAgent(@RequestBody @Validated AgentUpdatePO agentPO, BindingResult result) throws BusinessException {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        //带登陆验证
        getCurrUser();
        try{
            //修改用户
            UserEntity userEntity=userService.updateAgentEntityById(agentPO,getCurrUseraccount());
            agentPO.setOemCode(userEntity.getOemCode());
            if(Objects.equals(1,agentPO.getIsBank())){
                UserBankCardEntity userBankCardEntity =new UserBankCardEntity();
                BeanUtils.copyProperties(agentPO, userBankCardEntity);
                userBankCardEntity.setUserName(agentPO.getBankUserName());
                userBankCardEntity.setPhone(agentPO.getBankPhone());
                userBankCardEntity.setUserId(userEntity.getId());
                //修改银行卡
                userBankCardService.updateBankCardEntity(userBankCardEntity,getCurrUseraccount(),agentPO.getId());
            }
            return ResultVo.Success();
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 代理商注销
     * @author HZ
     * date 2019/12/10
     */
    @PostMapping("/updateAgentStatus")
    public ResultVo updateAgentStatus(@JsonParam Long id) throws BusinessException {
        //带登陆验证
        getCurrUser();
        try{
            UserEntity userEntity=userService.findById(id);
            memberAccountService.canleMember(id,userEntity.getBindingAccount(),2,getCurrUseraccount());
//            memberAccountEntity.setStatus(2);
//            memberAccountService.editByIdSelective(memberAccountEntity);
            //清除代理商token
            String oemCode = userEntity.getOemCode();
            if(StringUtils.isBlank(oemCode)){
                oemCode = "";
            }
            String token =redisService.get(RedisKey.LOGIN_TOKEN_KEY+oemCode+"_" + "userId_2_" + userEntity.getUsername());
            if(!org.apache.commons.lang3.StringUtils.isBlank(token)){
                redisService.delete(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_"+ token);
            }
        }catch ( BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }
}
