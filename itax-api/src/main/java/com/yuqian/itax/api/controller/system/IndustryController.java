package com.yuqian.itax.api.controller.system;

import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.vo.IndustryApiVO;
import com.yuqian.itax.system.entity.vo.IndustryInfoVO;
import com.yuqian.itax.system.entity.vo.ParkIndustryPresentationVO;
import com.yuqian.itax.system.service.IndustryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/10 14:22
 *  @Description: 行业类型控制器
 */
@Api(tags = "行业类型控制器")
@Slf4j
@RestController
@RequestMapping("/system/industry")
public class IndustryController extends BaseController {
    @Autowired
    private IndustryService industryService;

    /**
     * @Description 行业类型列表查询
     * @Author  Kaven
     * @Param parkId-园区ID
     * @Param companyType-企业类型：1-个体开户 2-个独开户 3-有限合伙 4-有限责任
     * @Date   2019/12/10 14:47
     * @Return ResultVo
    */
    @ApiOperation("行业类型列表查询")
    @PostMapping("list")
    public ResultVo<List<IndustryApiVO>> list(@JsonParam Long parkId,@JsonParam Integer companyType,@JsonParam String industryName){
        if(null == parkId){
            return ResultVo.Fail("园区ID不能为空");
        }
        if(null == companyType){
            companyType = 1;
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        List<IndustryApiVO> list = this.industryService.selectIndustry(oemCode,parkId,companyType,industryName);
        return ResultVo.Success(list);
    }

    /**
     * @Description 根据行业类型选择核定税种、开票类目、经营范围
     * @Author  Kaven
     * @Date   2019/12/19 10:41
     * @Param  industryId
     * @Return ResultVo<IndustryInfoVO>
     * @Exception BusinessException
    */
    @ApiOperation("根据行业类型选择核定税种、开票类目、经营范围并生成名称示例")
    @ApiImplicitParams({
            @ApiImplicitParam(name="industryId",value="行业ID",dataType="Long",required = true),
            @ApiImplicitParam(name="parkId",value="园区ID",dataType="Long",required = true)
    })
    @PostMapping("/getById")
    public ResultVo<IndustryInfoVO> getByIndustryId(@JsonParam Long industryId, @JsonParam Long parkId){
        if(null == industryId){
            return ResultVo.Fail("行业ID不能为空");
        }
        if(null == parkId){
            return ResultVo.Fail("园区ID不能为空");
        }
        IndustryInfoVO industryVo = this.industryService.getById(industryId,parkId);
        return ResultVo.Success(industryVo);
    }

    @ApiOperation("园区行业介绍")
    @PostMapping("/presentation")
    public ResultVo presentation(@JsonParam Long industryId) {
        if (null == industryId) {
            return ResultVo.Fail("行业id不能为空");
        }
        ParkIndustryPresentationVO vo = industryService.presentation(industryId);
        return ResultVo.Success(vo);
    }
}