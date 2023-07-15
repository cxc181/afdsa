package com.yuqian.itax.api.controller.user;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.user.entity.CompanyResoucesApplyRecordEntity;
import com.yuqian.itax.user.entity.CompanyResourcesUseRecordEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.dto.CompResApplyRecordDTO;
import com.yuqian.itax.user.entity.dto.CompanyResourcesUseRecordDTO;
import com.yuqian.itax.user.entity.query.CompResApplyRecordQuery;
import com.yuqian.itax.user.entity.query.CompanyResourcesUseRecordQuery;
import com.yuqian.itax.user.entity.vo.ComResApplyRecordDetailVO;
import com.yuqian.itax.user.entity.vo.CompanyResourcesUseRecordVO;
import com.yuqian.itax.user.enums.AuditStateEnum;
import com.yuqian.itax.user.enums.MemberCompanyTypeEnum;
import com.yuqian.itax.user.service.CompanyResoucesApplyRecordService;
import com.yuqian.itax.user.service.CompanyResourcesUseRecordService;
import com.yuqian.itax.user.service.MemberCompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/14 14:10
 *  @Description: 企业资源使用记录控制器
 */
@Api(tags = "企业资源使用记录控制器")
@RestController
@RequestMapping("/company/resource")
@Slf4j
public class ComRsrcUseRcdController extends BaseController {

    @Autowired
    private CompanyResourcesUseRecordService companyResourcesUseRecordService;
    @Autowired
    private CompanyResoucesApplyRecordService companyResoucesApplyRecordService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private OrderService orderService;

    /**
     * @Description 分页查询用章申请列表
     * @Author  Kaven
     * @Date   2019/12/14 14:13
     * @Return com.yuqian.itax.common.base.vo.ResultVo
    */
    @ApiOperation("分页查询用章申请列表")
    @PostMapping("/findPage")
    public ResultVo<PageInfo> findComRsrcRcdPage(@RequestBody CompanyResourcesUseRecordQuery query) {
        query.setUserId(getCurrUserId());
        PageInfo<CompanyResourcesUseRecordVO> pages = companyResourcesUseRecordService.listPage(query);
        return ResultVo.Success(pages);
    }

    /**
     * @Description 用章申请详情查询
     * @Author  Kaven
     * @Date   2019/12/14 14:26
     * @Param  id
     * @Return ResultVo
    */
    @ApiOperation("用章申请详情查询")
    @ApiImplicitParam(name="id",value="主键ID",dataType="Long",required = true)
    @PostMapping("/getDetail")
    public ResultVo<CompanyResourcesUseRecordVO> getDetail(@JsonParam Long id) {
        if(null == id){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        CompanyResourcesUseRecordEntity entity = this.companyResourcesUseRecordService.findById(id);
        CompanyResourcesUseRecordVO record = new CompanyResourcesUseRecordVO();
        BeanUtils.copyProperties(entity,record);
        // 查询公司名和企业类型名称等
        MemberCompanyEntity company = this.memberCompanyService.findById(entity.getCompanyId());
        record.setCompanyName(company.getCompanyName());
        record.setCompanyType(company.getCompanyType());
        record.setCompanyTypeName(MemberCompanyTypeEnum.getByValue(company.getCompanyType()).getMessage());
        return ResultVo.Success(record);
    }

    /**
     * @Description 用章申请审批
     * @Author  Kaven
     * @Date   2019/12/14 14:51
     * @Param  crurDto
     * @Return ResultVo
     * @Exception BusinessException
    */
    @ApiOperation("用章申请审批")
    @PostMapping("/approve")
    public ResultVo<CompanyResourcesUseRecordEntity> approveApplication(@RequestBody @Validated CompanyResourcesUseRecordDTO crurDto, BindingResult result) {
        if(null == crurDto){
            return ResultVo.Fail("传入参数对象不能为空");
        }

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        if(null == crurDto.getId() || null == crurDto.getAuditStatus()){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }

        if(AuditStateEnum.APPROVE_NO_PASS.getValue().equals(crurDto.getAuditStatus()) && StringUtils.isBlank(crurDto.getAuditDesc())){
            return ResultVo.Fail("审批不通过原因不能为空");
        }
        this.companyResourcesUseRecordService.approval(getCurrUser().getUserId(),crurDto.getId(),crurDto.getAuditStatus(),
                crurDto.getAuditDesc(),crurDto.getImgAddr());
        return ResultVo.Success();
    }

    /**
     * @Description 分页查询企业资源申请列表
     * @Author  yejian
     * @Date   2020/03/25 10:13
     * @Return ResultVo<PageResultVo<CompanyResoucesApplyRecordEntity>>
     */
    @ApiOperation("分页查询企业资源申请列表")
    @PostMapping("/findCertPage")
    public ResultVo<PageResultVo<CompanyResoucesApplyRecordEntity>> findCertPage(@RequestBody @Valid CompResApplyRecordQuery query, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        String oemCode = getRequestHeadParams("oemCode");
        List<CompanyResoucesApplyRecordEntity> compResApplyRecordList = companyResoucesApplyRecordService.listCompResApplyRecord(getCurrUserId(), oemCode, query);
        return ResultVo.Success(PageResultVo.restPage(compResApplyRecordList));
    }

    /**
     * @Description 查询企业资源申请详情
     * @Author  yejian
     * @Date   2020/03/25 10:13
     * @Return ResultVo<ComResApplyRecordDetailVO>
     */
    @ApiOperation("查询企业资源申请详情")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",required = true)
    @PostMapping("/getCertDetail")
    public ResultVo<ComResApplyRecordDetailVO> getCertDetail(@JsonParam String orderNo) {
        if(null == orderNo){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        String oemCode = getRequestHeadParams("oemCode");
        ComResApplyRecordDetailVO comResApplyRecordDetailVO = companyResoucesApplyRecordService.queryCompResApplyRecordDetail(getCurrUserId(), oemCode, orderNo);
        return ResultVo.Success(comResApplyRecordDetailVO);
    }

    /**
     * @Description 企业资源申请
     * @Author  yejian
     * @Date   2020/03/25 10:13
     * @Return ResultVo<Map<String,Object>>
     */
    @ApiOperation("企业资源申请")
    @PostMapping("/certUseOrReturn")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.CERT_USE_OR_RETURN_ORDER, lockTime = 10)
    public ResultVo<Map<String,Object>> certUseOrReturn(@RequestBody @Valid CompResApplyRecordDTO entity, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String orderNo = orderService.certUseOrReturnOrder(
                getCurrUserId(),
                getRequestHeadParams("oemCode"),
                entity,
                org.apache.commons.lang.StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType"));
        resultMap.put("orderNo", orderNo);
        return ResultVo.Success(resultMap);
    }

    /**
     * @Author yejian
     * @Date 2020/03/26 10:40
     * @param orderNo
     * @return ResultVo
     */
    @ApiOperation("取消企业资源申请")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",required = true)
    @PostMapping(value = "/cancelCertOrder")
    public ResultVo cancelCertOrder(@JsonParam String orderNo){
        orderService.cancelCertOrder(getCurrUserId(), getRequestHeadParams("oemCode"), orderNo);
        return ResultVo.Success();
    }

    /**
     * @Author yejian
     * @Date 2020/03/26 14:40
     * @param orderNo
     * @return ResultVo
     */
    @ApiOperation("证件领用确认收货")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",required = true)
    @PostMapping(value = "/certUseConfirm")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.CERT_USE_CONFIRM, lockTime = 10)
    public ResultVo certUseConfirm(@JsonParam String orderNo){
        orderService.certUseConfirm(getCurrUserId(), getRequestHeadParams("oemCode"), orderNo);
        return ResultVo.Success();
    }

    /**
     * @Author yejian
     * @Date 2020/03/26 14:40
     * @param orderNo
     * @return ResultVo
     */
    @ApiOperation("企业资源申请进度查询")
    @ApiImplicitParam(name="orderNo",value="订单编号",dataType="String",required = true)
    @PostMapping(value = "/certOrderProgress")
    public ResultVo certOrderProgress(@JsonParam String orderNo){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap = orderService.certOrderProgress(getCurrUserId(), orderNo);
        return ResultVo.Success(resultMap);
    }
}
