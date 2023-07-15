package com.yuqian.itax.api.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.park.entity.ParkAgreementEntity;
import com.yuqian.itax.park.service.ParkAgreementService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.query.CompanyListQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.service.MemberCompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: companyController
 * @Description: 我的企业控制器
 * @Author: yejian
 * @Date: Created in 2019/12/6
 * @Version: 1.0
 * @Modified By:
 */
@Api(tags = "我的企业控制器")
@RestController
@RequestMapping("/company")
@Slf4j
public class CompanyController extends BaseController {

    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private ParkAgreementService parkAgreementService;

    /**
     * @Author yejian
     * @Date 2019/12/6 17:03
     * @param
     * @return ResultVo<PageResultVo<MemberCompanyVo>>
     */
    @ApiOperation("分页查询企业列表")
    @PostMapping("/findPage")
    public ResultVo<PageResultVo<MemberCompanyVo>> findCompanyPage(@RequestBody @Valid CompanyListQuery query, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        List<MemberCompanyVo> companyList = invoiceOrderService.listMemberCompany(getCurrUserId(), getRequestHeadParams("oemCode"), query);
        return ResultVo.Success(PageResultVo.restPage(companyList));
    }

    /**
     * @Author yejian
     * @Date 2019/12/6 15:03
     * @param id
     * @return ResultVo<MemberCompanyDetailVo>
     */
    @ApiOperation("查询企业详情")
    @ApiImplicitParam(name="id",value="主键id",dataType="Long",required = true)
    @PostMapping("/detail")
    public ResultVo<MemberCompanyDetailVo> getCompanyById(@JsonParam Long id) {
        if(null == id){
            return ResultVo.Fail("企业ID不能为空");
        }
        MemberCompanyDetailVo memberCompanyDetailVo = memberCompanyService.getMemberCompanyDetail(getCurrUserId(), getRequestHeadParams("oemCode"), id);
        return ResultVo.Success(memberCompanyDetailVo);
    }

    /**
     * @Description 企业注销
     * @Author Kaven
     * @Date 2020/2/14 9:07
     * @Param id-企业ID
     * @Return ResultVo
     * @Exception
     */
    @ApiOperation("企业注销")
    @ApiImplicitParam(name = "id", value = "主键id", dataType = "Long", required = true)
    @PostMapping("cancellation")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.COMPANY_CANCELLATION_CREATE_ORDER, lockTime = 10)
    public ResultVo cancellation(@JsonParam Long id) {
        if (null == getCurrUser()) {
            return ResultVo.Fail("操作失败，用户未登录！");
        }
        if (null == id) {
            return ResultVo.Fail("企业ID不能为空");
        }
        Map<String, Object> map = this.orderService.createComCancelOrder(
                getCurrUser().getUserId(),
                getRequestHeadParams("oemCode"), id,
                StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType"));
        return ResultVo.Success(map);
    }

    /**
     * @Author yejian
     * @Date 2020/02/25 11:24
     * @param id
     * @return ResultVo
     */
    @ApiOperation("查询企业状态是否正常")
    @ApiImplicitParam(name="id",value="主键id",dataType="Long",required = true)
    @PostMapping("/checkStatus")
    public ResultVo checkStatus(@JsonParam Long id) {
        if(null == id){
            return ResultVo.Fail("企业ID不能为空");
        }
        String oemCode = getRequestHeadParams("oemCode");
        Integer status = memberCompanyService.checkStatus(getCurrUserId(), id, oemCode);
        return ResultVo.Success(status);
    }

    /**
     * @Author yejian
     * @Date 2020/03/03 14:03
     * @param
     * @return ResultVo<List<MemberCompanyEntity>>
     */
    @ApiOperation("查询所有企业列表")
    @PostMapping("/findAllCompany")
    public ResultVo<List<MemberCompanyEntity>> findAllCompany() {
        List<MemberCompanyEntity> allCompanyList = memberCompanyService.allMemberCompanyList(getCurrUserId(), getRequestHeadParams("oemCode"));
        return ResultVo.Success(allCompanyList);
    }

    /**
     * 查询所有正常状态企业列表
     * @return
     */
    @PostMapping("findOnlineCompany")
    public ResultVo findOnlineCompany() {
        Example example = new Example(MemberCompanyEntity.class);
        example.createCriteria()
                .andEqualTo("memberId", getCurrUserId())
                .andEqualTo("oemCode", getRequestHeadParams("oemCode"))
                .andNotEqualTo("status", 4)
                ;
        example.orderBy("addTime").desc();
        List<MemberCompanyEntity> list = memberCompanyService.selectByExample(example);
        return ResultVo.Success(MemberCompanySimpleVO.getList(list));
    }

    /**
     * @Author yejian
     * @Date 2020/03/25 09:53
     * @param id
     * @return ResultVo<MemberCompanyCertVo>
     */
    @ApiOperation("查询企业证件列表")
    @ApiImplicitParam(name="id",value="企业id",dataType="Long",required = true)
    @PostMapping("/companyCert")
    public ResultVo<MemberCompanyCertVo> getCompanyCertList(@JsonParam Long id) {
        MemberCompanyCertVo memberCompanyCertVo = memberCompanyService.getMemberCompanyCertList(getCurrUserId(), getRequestHeadParams("oemCode"), id);
        return ResultVo.Success(memberCompanyCertVo);
    }

    /**
     * @Author yejian
     * @Date 2020/03/30 14:53
     * @param companyId
     * @param isInPark
     * @return ResultVo<List<MemberCompanyCertInParkVo>>
     */
    @ApiOperation("查询企业证件是否在园区列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="companyId",value="企业id",dataType="Long"),
            @ApiImplicitParam(name="isInPark",value="是否在园区 0-不在园区 1-在园区",dataType="Integer")
    })
    @PostMapping("/companyCertInPark")
    public ResultVo<List<MemberCompanyCertInParkVo>> getCompanyCertInParkList(@JsonParam Long companyId, @JsonParam Integer isInPark) {
        String oemCode = getRequestHeadParams("oemCode");
        List<MemberCompanyCertInParkVo> memberCompanyCertInParkList = memberCompanyService.getMemberCompanyCertInParkList(getCurrUserId(), oemCode, companyId, isInPark);
        return ResultVo.Success(memberCompanyCertInParkList);
    }

    /**
     * @Description 查看协议列表
     * @Author  Kaven
     * @Date   2020/9/7 11:00
     * @Param   companyId
     * @Return  ResultVo<List<ParkAgreementEntity>>
     * @Exception
    */
    @ApiOperation("查看协议列表")
    @PostMapping("listParkAgreement")
    public ResultVo<List<ParkAgreementEntity>> listParkAgreement(@JsonParam Long companyId){
        if(null == companyId){
            return ResultVo.Fail("企业ID不能为空");
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        List<ParkAgreementEntity> agreements = this.parkAgreementService.listParkAgreement(companyId,oemCode);
        return ResultVo.Success(agreements);
    }

    /**
     * @Author HZ
     * @Date 2020/9/14
     * @return ResultVo<MemberCompanyDetailVo>
     */
    @ApiOperation("查询企业信息（H5用）")
    @PostMapping("/detailH5")
    public ResultVo<MemberCompanyDetailH5VO> getCompanyInfoH5ById(@JsonParam Long companyId) {
        if(null == companyId){
            return ResultVo.Fail("企业ID不能为空");
        }
        MemberCompanyDetailH5VO vo;
        try {
            vo = memberCompanyService.getCompanyInfoH5ById(getCurrUserId(),companyId);
            JSONObject jsonObject= new JSONObject();
            orderService.getSignDate(orderService.queryByOrderNo(vo.getOrderNo()),jsonObject);
            vo.setSignTime(jsonObject.getJSONObject("signDate").getString("name"));
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }

        return ResultVo.Success(vo);
    }

    /**
     * @Description 查看托管费续费详情
     * @Author  LiuMenghao
     * @Date   2021/2/4 16:20
     * @Param   companyId
     * @Return
     * @Exception
     */
    @ApiOperation("查看托管费续费详情")
    @PostMapping("/trusteeFeeRenewDetail")
    public ResultVo<TrusteeFeeRenewDetailVO> getTrusteeFeeRenewDetail(@JsonParam Long companyId){
        if (null == getCurrUser()) {
            return ResultVo.Fail("操作失败，用户未登录！");
        }
        if (null == companyId) {
            return ResultVo.Fail("企业ID不能为空");
        }
        TrusteeFeeRenewDetailVO renewDetailVO = memberCompanyService.getTrusteeFeeRenewDetail(getCurrUserId() , companyId);
        return ResultVo.Success(renewDetailVO);
    }
}