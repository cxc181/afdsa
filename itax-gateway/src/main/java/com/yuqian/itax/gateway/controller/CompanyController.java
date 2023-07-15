package com.yuqian.itax.gateway.controller;

import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.order.entity.dto.CompanyInvoiceApiDTO;
import com.yuqian.itax.order.entity.dto.CompanyInvoiceTaxCalcApiDTO;
import com.yuqian.itax.order.entity.dto.InvOrderBankWaterApiDTO;
import com.yuqian.itax.order.entity.query.InvoiceWaterOrderApiQuery;
import com.yuqian.itax.order.entity.vo.CompanyInvoiceApiVO;
import com.yuqian.itax.order.entity.vo.CompanyInvoiceTaxCalcApiVO;
import com.yuqian.itax.order.entity.vo.InvoiceWaterOrderApiVO;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.user.entity.dto.CompanyCancelApiDTO;
import com.yuqian.itax.user.entity.query.CompanyListApiQuery;
import com.yuqian.itax.user.entity.vo.CompanyCancelApiVO;
import com.yuqian.itax.user.entity.vo.CompanyListApitVO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  @Author: yejian
 *  @Date: 2020/07/15 14:11
 *  @Description: 企业接口控制器
 */
@Api(tags = "企业服务API")
@RestController
@RequestMapping("/company")
@Slf4j
public class CompanyController extends BaseController {

    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private OrderService orderService;

    /**
     * @param query
     * @return ResultVo<PageResultVo>
     * @Author yejian
     * @Date 2020/07/15 14:03
     */
    @ApiOperation("会员企业列表查询")
    @PostMapping("/getList")
    public ResultVo<PageResultVo> findCompanyPage(@RequestBody @Valid CompanyListApiQuery query, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        List<CompanyListApitVO> companyList = invoiceOrderService.getCompanyListByQuery(getRequestHeadParams("oemCode"), query);
        PageResultVo pageResult = PageResultVo.listToPageResult(query.getPageNumber(), query.getPageSize(), companyList);
        return ResultVo.Success(pageResult);
    }


    /**
     * @Author yejian
     * @Date 2020/07/16 10:03
     * @param entity
     * @return ResultVo<CompanyCancelApiVO>
     */
    @ApiOperation("企业注销")
    @PostMapping("/cancellation")
    public ResultVo<CompanyCancelApiVO> cancellation(@RequestBody @Valid CompanyCancelApiDTO entity, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        CompanyCancelApiVO companyCancelApiVO = orderService.createComCancelOrder(getRequestHeadParams("oemCode"), entity);
        return ResultVo.Success(companyCancelApiVO);
    }

    /**
     * @Description 企业开票
     * @Author  yejian
     * @Date   2020/07/17 13:57
     * @param  entity
     * @Return ResultVo<CompanyInvoiceApiVO>
     */
    @ApiOperation("企业开票")
    @PostMapping("/invoice")
    public ResultVo<CompanyInvoiceApiVO> companyInvoice(@RequestBody @Valid CompanyInvoiceApiDTO entity, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        CompanyInvoiceApiVO companyInvoiceApiVO = invoiceOrderService.companyInvoice(getRequestHeadParams("oemCode"), entity);
        return ResultVo.Success(companyInvoiceApiVO);
    }

    /**
     * @Description 企业开票税费计算
     * @Author  yejian
     * @Date   2020/07/20 09:37
     * @param  entity
     * @Return ResultVo<CompanyInvoiceTaxCalcApiVO>
     */
    @ApiOperation("企业开票税费计算")
    @PostMapping("/invoiceTaxCalc")
    public ResultVo<CompanyInvoiceTaxCalcApiVO> invoiceTaxCalc(@RequestBody @Valid CompanyInvoiceTaxCalcApiDTO entity, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        CompanyInvoiceTaxCalcApiVO companyInvoiceApiVO = invoiceOrderService.invoiceTaxCalc(getRequestHeadParams("oemCode"), entity);
        return ResultVo.Success(companyInvoiceApiVO);
    }

    /**
     * @param entity
     * @return ResultVo<Map < String, Object>>
     * @Author yejian
     * @Date 2020/07/20 14:09
     */
    @ApiOperation("补传收款凭证申请")
    @PostMapping("/patchBankWater")
    public ResultVo<Map<String, Object>> patchBankWater(@RequestBody @Valid InvOrderBankWaterApiDTO entity, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Integer bankWaterStatus = invoiceOrderService.patchBankWater(getRequestHeadParams("oemCode"), entity);
        resultMap.put("status", bankWaterStatus);
        return ResultVo.Success(resultMap);
    }

    /**
     * @param query
     * @return ResultVo<PageResultVo>
     * @Author yejian
     * @Date 2020/07/20 14:10
     */
    @ApiOperation("补传收款凭证审核查询")
    @PostMapping("/getInvWaterList")
    public ResultVo<PageResultVo> findInvWaterPage(@RequestBody InvoiceWaterOrderApiQuery query) {
        List<InvoiceWaterOrderApiVO> invWaterOrderList = invoiceOrderService.getInvWaterOrderListByQuery(getRequestHeadParams("oemCode"), query);
        PageResultVo pageResult = PageResultVo.listToPageResult(query.getPageNumber(), query.getPageSize(), invWaterOrderList);
        return ResultVo.Success(pageResult);
    }

}