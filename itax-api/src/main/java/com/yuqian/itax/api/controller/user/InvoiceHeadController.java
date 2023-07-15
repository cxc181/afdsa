package com.yuqian.itax.api.controller.user;

import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.entity.query.BaseQuery;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.CreditCodeEntity;
import com.yuqian.itax.system.service.CreditCodeService;
import com.yuqian.itax.user.entity.InvoiceHeadEntity;
import com.yuqian.itax.user.entity.dto.EditInvoiceHeadDTO;
import com.yuqian.itax.user.entity.dto.InvoiceHeadDTO;
import com.yuqian.itax.user.service.InvoiceHeadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName: invoiceHeadController
 * @Description: 发票抬头控制器
 * @Author: yejian
 * @Date: Created in 2019/12/9
 * @Version: 1.0
 * @Modified By:
 */
@Api(tags = "发票抬头控制器")
@RestController
@RequestMapping("/invoiceHead")
@Slf4j
public class InvoiceHeadController extends BaseController {

    @Autowired
    private InvoiceHeadService invoiceHeadService;
    @Autowired
    private CreditCodeService creditCodeService;

    /**
     * @Description 添加发票抬头
     * @Author yejian
     * @Date 2019/12/9 10:40
     * @param entity
     * @return ResultVo
     */
    @ApiOperation("添加发票抬头")
    @PostMapping("/insert")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.INVOICE_HEAD_INSERT, lockTime = 10)
    public ResultVo insertInvoiceHead(@RequestBody @Valid InvoiceHeadDTO entity, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        invoiceHeadService.insertInvoiceHead(getCurrUserId(), entity);
        return ResultVo.Success();
    }

    /**
     * @Description 编辑发票抬头
     * @Author yejian
     * @Date 2019/12/9 10:40
     * @param entity
     * @return ResultVo
     */
    @ApiOperation("编辑发票抬头")
    @PostMapping(value = "/edit")
    public ResultVo editInvoiceHead(@RequestBody @Valid EditInvoiceHeadDTO entity, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        invoiceHeadService.editInvoiceHead(getCurrUserId(), entity);
        return ResultVo.Success();
    }

    /**
     * @Description 删除发票抬头
     * @Author yejian
     * @Date 2019/12/9 10:40
     * @param id
     * @return ResultVo
     */
    @ApiOperation("删除发票抬头")
    @ApiImplicitParam(name="id",value="主键id",dataType="Long",required = true)
    @PostMapping(value = "/del")
    public ResultVo delInvoiceHead(@JsonParam Long id) {
        if(null == id){
            return ResultVo.Fail("发票抬头id不能为空！");
        }
        invoiceHeadService.updateInvHeadStatus(id, getCurrUserId());
        return ResultVo.Success();
    }

    /**
     * @Description 分页查询发票抬头列表
     * @Author yejian
     * @Date 2019/12/9 10:20
     * @param
     * @return ResultVo<PageResultVo<InvoiceHeadEntity>>
     */
    @ApiOperation("分页查询发票抬头列表")
    @PostMapping("/findPage")
    public ResultVo<PageResultVo<InvoiceHeadEntity>> findInvoiceHeadPage(@RequestBody BaseQuery query) {
        List<InvoiceHeadEntity> invoiceHeadList = invoiceHeadService.listInvoiceHead(getCurrUserId(), query);
        return ResultVo.Success(PageResultVo.restPage(invoiceHeadList));
    }

    /**
     * @Description 查询发票抬头详情
     * @Author yejian
     * @Date 2019/12/9 10:40
     * @param id
     * @return ResultVo<InvoiceHeadEntity>
     */
    @ApiOperation("查询发票抬头详情")
    @ApiImplicitParam(name="id",value="主键id",dataType="Long",required = true)
    @PostMapping("/detail")
    public ResultVo<InvoiceHeadEntity> getInvoiceHeadById(@JsonParam Long id) {
        if(null == id){
            return ResultVo.Fail("发票抬头id不能为空！");
        }
        InvoiceHeadEntity entity = invoiceHeadService.findByMemberId(id, getCurrUserId());
        return ResultVo.Success(entity);
    }

    /**
     * @Description 税务登记号查询
     * @Author yejian
     * @Date 2020/02/20 12:40
     * @param keyWord
     * @return ResultVo<CreditCodeEntity>
     */
    @ApiOperation("税务登记号查询")
    @ApiImplicitParam(name="keyWord",value="公司名称、信用代码",dataType="String",required = true)
    @PostMapping("/getCreditCode")
    public ResultVo<CreditCodeEntity> getCreditCode(@JsonParam String keyWord) {
        if(StringUtils.isBlank(keyWord)){
            return ResultVo.Fail("公司名称、信用代码不能为空！");
        }
        CreditCodeEntity creditCodeEntity = creditCodeService.getCreditCode(getRequestHeadParams("oemCode"), keyWord);
        return ResultVo.Success(creditCodeEntity);
    }

}
