package com.yuqian.itax.admin.controller.park;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemParkRelaEntity;
import com.yuqian.itax.agent.service.OemParkRelaService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.entity.RegisterOrderEntity;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.park.entity.ParkAgentAccountEntity;
import com.yuqian.itax.park.entity.query.ParkAgentAccountQuery;
import com.yuqian.itax.park.entity.vo.ParkAgentAccountPO;
import com.yuqian.itax.park.entity.vo.ParkAgentAccountVO;
import com.yuqian.itax.park.service.ParkAgentAccountService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 园区经办人Controll
 * auth: HZ
 * time: 2019/12/17
 */
@RestController
@RequestMapping("/park/agent")
public class ParkAgentAccountController extends BaseController {

    @Autowired
    ParkAgentAccountService parkAgentAccountService;
    @Autowired
    RegisterOrderService registerOrderService;

    @Autowired
    private OssService ossService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private OemParkRelaService oemParkRelaService;

    /**
     * 查询经办人账号(分页)
     * @auth hz
     * @Date 2020/03/05
     */
    @PostMapping("/queryParkAgentAccount")
    public ResultVo queryParkAgentAccount(@RequestBody ParkAgentAccountQuery query){

        //登陆校验
        getCurrUser();
        PageInfo<ParkAgentAccountEntity> list=parkAgentAccountService.queryParkAgentAccount(query);
        return ResultVo.Success(list);
    }

    /**
     * 查询状态正常经办人账号(下拉框使用)
     * @auth hz
     * @Date 2020/03/05
     */
    @PostMapping("/querySelectParkAgentAccount")
    public ResultVo querySelectParkAgentAccount(@RequestBody ParkAgentAccountQuery query){

        //登陆校验
        getCurrUser();
        query.setStatus(1);//设置状态为1-正常
        List<ParkAgentAccountEntity> list=parkAgentAccountService.queryParkAgentAccountList(query);
        return ResultVo.Success(list);
    }

    /**
     * 新增经办人账号
     * @auth hz
     * @Date 2020/03/05
     */
    @PostMapping("/addParkAgentAccount")
    public ResultVo addParkAgentAccount(@RequestBody @Validated ParkAgentAccountPO po, BindingResult result) {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        String bucket = Optional.ofNullable(dictionaryService.getByCode("oss_privateBucketName")).map(DictionaryEntity::getDictValue).orElse(null);
        if (!ossService.doesObjectExist(po.getIdCardFront(), bucket)) {
            return ResultVo.Fail("身份证正面照在oss服务器不存在");
        }
        if (!ossService.doesObjectExist(po.getIdCardBack(), bucket)) {
            return ResultVo.Fail("身份证反面照在oss服务器不存在");
        }
        //登陆校验
        getCurrUser();
        try{
            parkAgentAccountService.addParkAgentAccount(po,getCurrUseraccount());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 详情
     * @return
     */
    @PostMapping("detail")
    public ResultVo detail(@JsonParam Long parkId, @JsonParam Long id) {
        if (parkId == null) {
            return ResultVo.Fail("园区id不能为空");
        }
        if (id == null) {
            return ResultVo.Fail("经办人id不能为空");
        }
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (StringUtils.isNotBlank(userEntity.getOemCode())) {
            OemParkRelaEntity oemParkRelaEntity = new OemParkRelaEntity();
            oemParkRelaEntity.setOemCode(userEntity.getOemCode());
            oemParkRelaEntity.setParkId(parkId);
            List<OemParkRelaEntity> list = oemParkRelaService.select(oemParkRelaEntity);
            if (CollectionUtil.isEmpty(list)) {
                return ResultVo.Fail("当前后台登录人员归属OEM机构与园区关系不存在");
            }
        }
        ParkAgentAccountEntity entity = new ParkAgentAccountEntity();
        entity.setId(id);
        entity.setParkId(parkId);
        entity = parkAgentAccountService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("经办人信息不存在");
        }
        ParkAgentAccountVO vo = new ParkAgentAccountVO(entity);
        vo.setIdCardFrontImg(ossService.getPrivateImgUrl(entity.getIdCardFront()));
        vo.setIdCardBackImg(ossService.getPrivateImgUrl(entity.getIdCardBack()));
        return ResultVo.Success(vo);
    }

    /**
     * 修改经办人账号
     * @auth hz
     * @Date 2020/03/05
     */
    @PostMapping("updateParkAgentAccount")
    public ResultVo updateParkAgentAccount(@JsonParam Long parkId, @JsonParam Long id, @JsonParam String idCardFront, @JsonParam String idCardBack) {
        if (parkId == null) {
            return ResultVo.Fail("园区id不能为空");
        }
        if (id == null) {
            return ResultVo.Fail("经办人id不能为空");
        }
        if (StringUtils.isBlank(idCardFront)) {
            return ResultVo.Fail("身份证正面照不能为空");
        }
        if (StringUtils.isBlank(idCardBack)) {
            return ResultVo.Fail("身份证反面照不能为空");
        }
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (StringUtils.isNotBlank(userEntity.getOemCode())) {
            OemParkRelaEntity oemParkRelaEntity = new OemParkRelaEntity();
            oemParkRelaEntity.setOemCode(userEntity.getOemCode());
            oemParkRelaEntity.setParkId(parkId);
            List<OemParkRelaEntity> list = oemParkRelaService.select(oemParkRelaEntity);
            if (CollectionUtil.isEmpty(list)) {
                return ResultVo.Fail("当前后台登录人员归属OEM机构与园区关系不存在");
            }
        }

        ParkAgentAccountEntity entity = new ParkAgentAccountEntity();
        entity.setId(id);
        entity.setParkId(parkId);
        entity = parkAgentAccountService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("经办人信息不存在");
        }
        String bucket = Optional.ofNullable(dictionaryService.getByCode("oss_privateBucketName")).map(DictionaryEntity::getDictValue).orElse(null);
        if (!ossService.doesObjectExist(idCardFront, bucket)) {
            return ResultVo.Fail("身份证正面照在oss服务器不存在");
        }
        if (!ossService.doesObjectExist(idCardBack, bucket)) {
            return ResultVo.Fail("身份证反面照在oss服务器不存在");
        }
        entity.setIdCardFront(idCardFront);
        entity.setIdCardBack(idCardBack);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(currUser.getUseraccount());
        parkAgentAccountService.editByIdSelective(entity);
        return ResultVo.Success();
    }

    /**
     * 经办人状态变更
     * @auth hz
     * @Date 2020/03/05
     */
    @PostMapping("/updateParkAgentAccountStatus")
    public ResultVo updateParkAgentAccountStatus(@JsonParam Long id,@JsonParam Integer status){

        //登陆校验
        getCurrUser();
        try {
            if(Objects.equals(2,status)){
                RegisterOrderEntity registerOrderEntity=new RegisterOrderEntity();
                registerOrderEntity.setAgentAccount(parkAgentAccountService.findById(id).getAgentAccount());
                List<RegisterOrderEntity> list= registerOrderService.select(registerOrderEntity);
                if(list.size()>0){
                    return ResultVo.Fail("该经办人账号已被使用，无法删除！");
                }
            }
            parkAgentAccountService.updateParkAgentAccountStatus(id,status,getCurrUseraccount());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 删除经办人
     * @auth hz
     * @Date 2020/03/05
     */
    @PostMapping("/deleteParkAgentAccount")
    public ResultVo deleteParkAgentAccount(@JsonParam Long id){
        //登陆校验
        getCurrUser();
        try {
                RegisterOrderEntity registerOrderEntity=new RegisterOrderEntity();
                registerOrderEntity.setAgentAccount(parkAgentAccountService.findById(id).getAgentAccount());
                List<RegisterOrderEntity> list= registerOrderService.select(registerOrderEntity);
                if(list.size()>0){
                    return ResultVo.Fail("该经办人账号已被使用，无法删除！");
                }
            parkAgentAccountService.delById(id);
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }
}
