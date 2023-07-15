package com.yuqian.itax.gateway.controller;

import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.dto.IndustryApiDTO;
import com.yuqian.itax.system.entity.vo.IndustryApiVO;
import com.yuqian.itax.system.service.IndustryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 *  @Author: yejian
 *  @Date: 2020/07/20 13:41
 *  @Description: 行业接口控制器
 */
@Api(tags = "行业服务API")
@RestController
@RequestMapping("/industry")
@Slf4j
public class IndustryController extends BaseController {

    @Autowired
    private IndustryService industryService;

    /**
     * @Author yejian
     * @Date 2020/07/20 13:41
     * @param entity
     * @return ResultVo<List<IndustryApiVO>>
     */
    @ApiOperation("行业列表查询")
    @PostMapping("getList")
    public ResultVo<List<IndustryApiVO>> list(@RequestBody @Valid IndustryApiDTO entity, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        entity.setOemCode(oemCode);
        List<IndustryApiVO> industryList = industryService.getIndustryList(entity);
        return ResultVo.Success(industryList);
    }

}