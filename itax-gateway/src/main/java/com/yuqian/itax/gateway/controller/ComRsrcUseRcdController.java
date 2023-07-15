package com.yuqian.itax.gateway.controller;

import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.user.entity.dto.CompanyCertReturnApiDTO;
import com.yuqian.itax.user.entity.dto.CompanyCertUseApiDTO;
import com.yuqian.itax.user.entity.query.CompanyCertListApiQuery;
import com.yuqian.itax.user.entity.vo.CompanyCertListApiVO;
import com.yuqian.itax.user.service.CompanyResoucesApplyRecordService;
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
 *  @Date: 2020/07/21 09:11
 *  @Description: 企业资源使用接口控制器
 */
@Api(tags = "企业资源服务API")
@RestController
@RequestMapping("/resouce")
@Slf4j
public class ComRsrcUseRcdController extends BaseController {

    @Autowired
    private CompanyResoucesApplyRecordService companyResoucesApplyRecordService;
    @Autowired
    private OrderService orderService;

    /**
     * @Description 企业证件申请
     * @Author  yejian
     * @Date   2020/07/21 10:13
     * @Return ResultVo<Map<String,Object>>
     */
    @ApiOperation("企业证件申请")
    @PostMapping("/certUse")
    public ResultVo<Map<String,Object>> certUse(@RequestBody @Valid CompanyCertUseApiDTO entity, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String orderNo = orderService.certUseOrder(getRequestHeadParams("oemCode"), entity);
        resultMap.put("orderNo", orderNo);
        return ResultVo.Success(resultMap);
    }

    /**
     * @Description 企业证件归还
     * @Author  yejian
     * @Date   2020/07/21 10:13
     * @Return ResultVo<Map<String,Object>>
     */
    @ApiOperation("企业证件归还")
    @PostMapping("/certReturn")
    public ResultVo<Map<String,Object>> certReturn(@RequestBody @Valid CompanyCertReturnApiDTO entity, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        Map<String,Object> resultMap = new HashMap<String,Object>();
        String orderNo = orderService.certReturnOrder(getRequestHeadParams("oemCode"), entity);
        resultMap.put("orderNo", orderNo);
        return ResultVo.Success(resultMap);
    }

    /**
     * @Description 企业证件申请、归还列表查询
     * @Author yejian
     * @Date 2020/03/25 10:13
     * @Return ResultVo<PageResultVo>
     */
    @ApiOperation("企业证件申请、归还列表查询")
    @PostMapping("/getCertList")
    public ResultVo<PageResultVo> getCertList(@RequestBody CompanyCertListApiQuery query) {
        String oemCode = getRequestHeadParams("oemCode");
        List<CompanyCertListApiVO> companyCertList = companyResoucesApplyRecordService.getCertListByQuery(oemCode, query);
        PageResultVo pageResult = PageResultVo.listToPageResult(query.getPageNumber(), query.getPageSize(), companyCertList);
        return ResultVo.Success(pageResult);
    }
}