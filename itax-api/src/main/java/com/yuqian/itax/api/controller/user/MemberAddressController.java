package com.yuqian.itax.api.controller.user;

import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberAddressEntity;
import com.yuqian.itax.user.entity.dto.MemberAddressDTO;
import com.yuqian.itax.user.entity.query.MemberAddressQuery;
import com.yuqian.itax.user.entity.vo.MemberAddressVO;
import com.yuqian.itax.user.enums.MemberAddressStatusEnum;
import com.yuqian.itax.user.service.MemberAddressService;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 会员收件地址
 * @author：pengwei
 * @Date：2020/12/25 16:57
 * @version：1.0
 */
@RestController
@RequestMapping("member/address")
@Slf4j
public class MemberAddressController extends BaseController {

    @Autowired
    private MemberAddressService memberAddressService;

    @ApiOperation("分页查询收货地址列表")
    @PostMapping("page")
    public ResultVo page(@RequestBody MemberAddressQuery query) {
        CurrUser currUser = getCurrUser();
        MemberAccountEntity memberAccountEntity = memberAccountService.findById(currUser.getUserId());
        if (memberAccountEntity == null) {
            throw new BusinessException("用户不存在");
        }
        query.setMemberId(currUser.getUserId());
        query.setOemCode(currUser.getOemCode());
        query.setStatus(MemberAddressStatusEnum.YES.getValue());
        return ResultVo.Success(memberAddressService.listPageMemberAddress(query));
    }

    @ApiOperation("添加收货地址")
    @PostMapping("add")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.MEMBER_ADDRESS_EDIT, lockTime = 10)
    public ResultVo add(@RequestBody @Validated(Add.class) MemberAddressDTO dto, BindingResult result) {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        CurrUser currUser = getCurrUser();
        memberAddressService.add(dto.toEntity(currUser.getUserId(), currUser.getOemCode()));
        return ResultVo.Success();
    }

    @ApiOperation("修改收货地址")
    @PostMapping(value = "edit")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.MEMBER_ADDRESS_EDIT, lockTime = 10)
    public ResultVo edit(@RequestBody @Validated(Update.class) MemberAddressDTO dto, BindingResult result) {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        CurrUser currUser = getCurrUser();
        MemberAddressEntity memberAddressEntity = dto.toEntity(currUser.getUserId(), currUser.getOemCode());
        memberAddressEntity.setId(dto.getId());
        memberAddressService.edit(memberAddressEntity);
        return ResultVo.Success();
    }

    @ApiOperation("删除收货地址")
    @PostMapping(value = "delete")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.MEMBER_ADDRESS_EDIT, lockTime = 10)
    public ResultVo delete(@JsonParam Long id) {
        if(null == id){
            return ResultVo.Fail("收货地址id不能为空！");
        }
        CurrUser currUser = getCurrUser();
        memberAddressService.delete(currUser.getUserId(), id);
        return ResultVo.Success();
    }

    @ApiOperation("查询收货地址详情")
    @PostMapping("detail")
    public ResultVo getInvoiceHeadById(@JsonParam Long id) {
        if(null == id){
            return ResultVo.Fail("收货地址id不能为空！");
        }
        MemberAddressEntity memberAddressEntity = memberAddressService.findById(id);
        if (memberAddressEntity == null) {
            return ResultVo.Fail("收货地址不存在");
        }
        if (!Objects.equals(memberAddressEntity.getMemberId(), getCurrUserId())) {
            return ResultVo.Fail("收货地址不属于当前登录用户");
        }
        return ResultVo.Success(new MemberAddressVO(memberAddressEntity));
    }

    @ApiOperation("获取默认收件地址")
    @PostMapping("query/default")
    public ResultVo<MemberAddressVO> queryDefaultAddress() {
        CurrUser currUser = getCurrUser();
        MemberAddressVO vo = memberAddressService.queryDefaultAddress(currUser.getUserId(), currUser.getOemCode());
        return ResultVo.Success(vo);
    }
}
