package com.yuqian.itax.api.controller.system;

import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.vo.SearchInvoiceCategoryBaseVO;
import com.yuqian.itax.system.service.InvoiceCategoryBaseService;
import com.yuqian.itax.user.entity.CompanyInvoiceCategoryEntity;
import com.yuqian.itax.user.entity.vo.CompanyInvoiceCategoryVO;
import com.yuqian.itax.user.service.CompanyInvoiceCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: InvoiceCategoryController
 * @Description: 开票类目控制器
 * @Author: yejian
 * @Date: Created in 2019/12/9
 * @Version: 1.0
 * @Modified By:
 */
@Api(tags = "开票类目控制器")
@RestController
@RequestMapping("/invoiceCategory")
@Slf4j
public class InvoiceCategoryController extends BaseController {

    @Autowired
    private CompanyInvoiceCategoryService CompanyInvoiceCategoryService;

    @Autowired
    private InvoiceCategoryBaseService invoiceCategoryBaseService;

    @ApiOperation("查询指定公司的开票类目列表")
    @ApiImplicitParam(name="companyId",value="公司id",dataType="Long",required = true)
    @PostMapping("/findList")
    public ResultVo findInvoiceCategoryList(@JsonParam Long companyId) {
        CompanyInvoiceCategoryEntity companyInvoiceCategoryEntity = new CompanyInvoiceCategoryEntity();
        companyInvoiceCategoryEntity.setCompanyId(companyId);
        List<CompanyInvoiceCategoryEntity> list = CompanyInvoiceCategoryService.select(companyInvoiceCategoryEntity);
        Map<String, Object> map = new HashMap<>();
        map.put("list", CompanyInvoiceCategoryVO.toListVO(list));
        return ResultVo.Success(map);
    }

    @ApiOperation("根据关键词搜索开票类目列表")
    @PostMapping("getCategoryByKeyWord")
    public ResultVo getCategoryByKeyWord(@JsonParam String keyWord) {
        List<SearchInvoiceCategoryBaseVO> list = invoiceCategoryBaseService.getCategoryByKeyWord(keyWord);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        return ResultVo.Success(map);
    }
}
