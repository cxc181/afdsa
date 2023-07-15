package com.yuqian.itax.api.controller.system;

import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.system.entity.DictionaryEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/10 18:51
 *  @Description: 系统字典控制器
 */
@Api(tags = "系统字典控制器")
@RestController
@RequestMapping("/system/dict")
@Slf4j
public class SysDictController extends BaseController {

    /**
     * @Description 根据编码获取用户字典
     * @Author  Kaven
     * @Date   2019/12/10 18:52
     * @Param  dictCode
     * @Return ResultVo
    */
    @ApiOperation("根据编码获取用户字典")
    @ApiImplicitParam(name="dictCode",value="编码",dataType="String",required = true)
    @PostMapping("getDictByCode")
    public ResultVo<DictionaryEntity> getDictByCode(@JsonParam String dictCode){
        if(StringUtils.isEmpty(dictCode)){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        DictionaryEntity entity = sysDictionaryService.getByCode(dictCode);
        return ResultVo.Success(entity);
    }
}
