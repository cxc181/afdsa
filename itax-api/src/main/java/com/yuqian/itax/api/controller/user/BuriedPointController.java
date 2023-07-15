package com.yuqian.itax.api.controller.user;

import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.point.entity.dto.BuriedPointDTO;
import com.yuqian.itax.point.service.BuriedPointService;
import com.yuqian.itax.util.validator.Add;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: buriedPointController
 * @Description: 埋点统计控制器
 * @Author: wangkail
 * @Date: Created in 2021/4/9
 */
@Api(tags = "埋点统计控制器")
@RestController
@RequestMapping("/point")
@Slf4j
public class BuriedPointController extends BaseController {

    @Autowired
    private BuriedPointService buriedPointService;

    @ApiOperation("埋点点击保存")
    @PostMapping("add")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.MEMBER_ADDRESS_EDIT, lockTime = 10)
    public ResultVo add(@RequestBody @Validated(Add.class) BuriedPointDTO dto  , BindingResult result) {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        Long userId = null;
        CurrUser currUser = null;
        String token = getRequestHeadParams("token");
        if(token != null && !token.equals("")){
            currUser = getCurrUser();
            userId = currUser.getUserId();
            dto.setUserType(1);
        }else{
            //  没有token则未登录，为非会员
            dto.setUserType(2);
        }
        buriedPointService.add(dto.toEntity(userId),currUser);
        return ResultVo.Success();
    }

}
