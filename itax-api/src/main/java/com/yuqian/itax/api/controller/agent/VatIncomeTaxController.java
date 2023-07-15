package com.yuqian.itax.api.controller.agent;

import com.yuqian.itax.agent.entity.dto.VatIncomeDTO;
import com.yuqian.itax.agent.entity.vo.VatIncomeVO;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.order.service.VatTaxService;
import com.yuqian.itax.park.entity.vo.TaxRulesConfigVO;
import com.yuqian.itax.park.service.TaxRulesConfigService;
import com.yuqian.itax.user.enums.CompanyTaxPayerTypeEnum;
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

import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/27 10:27
 *  @Description: 增值税所得税计算器
 */
@Api(tags = "税费计算控制器")
@Slf4j
@RestController
@RequestMapping("/agent/vatincometax")
public class VatIncomeTaxController extends BaseController {
    @Autowired
    private VatTaxService vatTaxService;
    @Autowired
    private TaxRulesConfigService taxRulesConfigService;

    /**
     * @Description 计算所得税
     * @Author  Kaven
     * @Date   2020/3/27 10:35
     * @Param   VatIncomeDTO BindingResult
     * @Return  ResultVo<VatIncomeVO>
     * @Exception  BusinessException
    */
    @ApiOperation("计算所得税")
    @PostMapping("calculateIncomeTax")
    public ResultVo<VatIncomeVO> calculateIncomeTax(@RequestBody @Validated VatIncomeDTO dto, BindingResult result){
        if(null == dto){
            return ResultVo.Fail("参数不能为空！");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        VatIncomeVO vatIncomeVO = vatTaxService.calculateIncomeTax(getRequestHeadParams("oemCode"), dto);
        return ResultVo.Success(vatIncomeVO);
    }

    /**
     * @Description 计算增值税
     * @Author  yejian
     * @Date   2020/3/30 15:35
     * @Param   VatIncomeDTO BindingResult
     * @Return  ResultVo<VatIncomeVO>
     * @Exception  BusinessException
     */
    @ApiOperation("计算增值税")
    @PostMapping("calculateVatTax")
    public ResultVo<VatIncomeVO> calculateVatTax(@RequestBody @Validated VatIncomeDTO dto, BindingResult result){
        if(null == dto){
            return ResultVo.Fail("参数不能为空！");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        VatIncomeVO vatIncomeVO = vatTaxService.calculateVatTax(getRequestHeadParams("oemCode"), dto);
        return ResultVo.Success(vatIncomeVO);
    }

    /**
     * @Description 税费计算规则查询
     * @Author  Kaven
     * @Date   2020/3/27 14:41
     * @Param parkId 园区ID
     * @Param taxType 税种类型 1-所得税 2-增值税
     * @Param companyType 企业类型 1-个体工商 2-个体独资 3-有限责任 4-有限合伙
     * @Param calcType 计算类型 1-按月 2-按季
     * @Return List<TaxRulesConfigVO>
     * @Exception BusinessException
    */
    @ApiOperation("查询税费计算规则")
    @PostMapping("queryTaxRules")
    public ResultVo<List<TaxRulesConfigVO>> queryTaxRules(@JsonParam Long parkId, @JsonParam Integer taxType, @JsonParam Integer companyType, @JsonParam Integer calType){
        if(null == parkId){
            return ResultVo.Fail("园区ID不能为空！");
        }
        if(null == taxType){
            return ResultVo.Fail("税种类型不能为空！");
        }
        if(null == companyType){
            return ResultVo.Fail("企业类型不能为空！");
        }
        if(null == calType){
            return ResultVo.Fail("计算类型不能为空！");
        }

        List<TaxRulesConfigVO> list = taxRulesConfigService.queryTaxRules(parkId, taxType, companyType, calType, CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue());
        return ResultVo.Success(list);
    }
}