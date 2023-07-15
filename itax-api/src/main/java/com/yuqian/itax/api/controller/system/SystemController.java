package com.yuqian.itax.api.controller.system;

import com.google.common.collect.Maps;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.api.controller.user.InvoiceHeadController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.message.entity.vo.WechatMessageTemplateSimpleVO;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.message.service.WechatMessageTemplateService;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.vo.ParkVO;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxPolicyService;
import com.yuqian.itax.system.entity.*;
import com.yuqian.itax.system.entity.vo.BannerVO;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/10 14:22
 *  @Description: 系统相关控制器
 */
@Slf4j
@RestController
@Api(tags = "系统相关控制器")
@RequestMapping("/system/info")
public class SystemController extends BaseController {
    @Autowired
    private CommonProblemsService commonProblemsService;
    @Autowired
    private BannerService bannerService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private TaxPolicyService taxPolicyService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private LogisCompanyService logisCompanyService;
    @Autowired
    private WechatMessageTemplateService wechatMessageTemplateService;
    @Autowired
    private CreditCodeService creditCodeService;

    /**
     * @Description 常见问题列表
     * @Author  Kaven
     * @Date   2019/12/13 14:44
     * @Param
    */
    @ApiOperation("常见问题列表")
    @PostMapping("common/getProblems")
    public ResultVo<List<CommonProblemsEntity>> getProblems(){
        List<CommonProblemsEntity> list = this.commonProblemsService.getCommomProbleListByOemCode(getRequestHeadParams("oemCode"));
        return ResultVo.Success(list);
    }

    /**
     * @Description 问题详情
     * @Author  Kaven
     * @Date   2019/12/17 15:16
     * @Param  id
     * @Return ResultVo
    */
    @ApiOperation("问题详情")
    @ApiImplicitParam(name="id",value="主键id",dataType="Long",required = true)
    @PostMapping("common/getProblem")
    public ResultVo<CommonProblemsEntity> getProblem(@JsonParam Long id){
        if(null == id){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        CommonProblemsEntity t = new CommonProblemsEntity();
        t.setId(id);
        CommonProblemsEntity entity = this.commonProblemsService.selectOne(t);
        return ResultVo.Success(entity);
    }

    /**
     * @Description banner图列表查询
     * @Author  Kaven
     * @Date   2019/12/17 15:16
     * @Return ResultVo
    */
    @ApiOperation("banner图列表查询")
    @PostMapping("common/getBanners")
    public ResultVo<List<BannerEntity>> getBanners(){
        Example example = new Example(BannerEntity.class);
        // 条件使用的是属性名
        example.createCriteria().andEqualTo("oemCode", getRequestHeadParams("oemCode"));
        // 注意：排序使用的是列名
        example.setOrderByClause("ORDER_NUM ASC");
        List<BannerEntity> list = this.bannerService.selectByExample(example);
        return ResultVo.Success(list);
    }

    /**
     * @Description 查询视频认证阅读内容
     * @Author  Kaven
     * @Date   2020/4/26 11:27
     * @Param companyType:企业类型 1-个体工商 2-个体独资 3-有限责任 4-有限合伙
     * @Param parkId:园区ID
     * @Return
     * @Exception
    */
    @ApiOperation("查询视频认证阅读内容")
    @PostMapping("getReadContent")
    public ResultVo<Map<String,Object>> getReadContent(@JsonParam Long parkId, @JsonParam Integer companyType){
        log.info("收到查询视频认证阅读内容请求：{}，{}",parkId,companyType);

        if(null == parkId){
            return ResultVo.Fail("园区ID不能为空");
        }

        if(null == companyType){
            return ResultVo.Fail("企业类型不能为空");
        }

        Map<String,Object> dataMap = Maps.newHashMap();
        TaxPolicyEntity t = new TaxPolicyEntity();
        t.setCompanyType(companyType);
        t.setParkId(parkId);
        TaxPolicyEntity tpe = this.taxPolicyService.selectOne(t);
        dataMap.put("readContent", null == tpe.getReadContent() ? "" : tpe.getReadContent());
        return ResultVo.Success(dataMap);
    }

    /**
     * @Description banner详情查询
     * @Author  Kaven
     * @Date   2020/3/4 9:45
     * @Param  bannerId
     * @Return
     * @Exception
    */
    @ApiOperation("banner详情查询")
    @PostMapping("common/banner/detail")
    public ResultVo<BannerVO> getBannerDetail(@JsonParam Long bannerId){
        if(null == bannerId){
            return ResultVo.Fail("bannerId不能为空");
        }
        BannerVO banner = this.bannerService.getDetail(bannerId, getRequestHeadParams("oemCode"));
        return ResultVo.Success(banner);
    }

    /**
     * @Description 添加意见反馈
     * @Author  Kaven
     * @Date   2019/12/17 15:17
     * @Return ResultVo
    */
    @ApiOperation("添加意见反馈")
    @ApiImplicitParam(name="content",value="意见内容",dataType="String", required = true)
    @PostMapping("feedback")
    public ResultVo feedback(@JsonParam String content){
        if(StringUtils.isBlank(content)){
            return ResultVo.Fail("传入意见不能为空");
        }
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setAddTime(new Date());
        feedback.setAddUser(getCurrUseraccount());
        feedback.setFeedbackContent(content);
        feedback.setMemberId(getCurrUserId());
        feedback.setSource(1);//来源  1-小程序 2-安卓 3-ios
        feedback.setHandingState(0);// 待处理
        feedback.setOemCode(this.getRequestHeadParams("oemCode"));
        this.feedbackService.insertSelective(feedback);
        return ResultVo.Success();
    }

    /**
     * @Author yejian
     * @Date 2020/1/1 10:33
     * @param companyType
     * @return ResultVo<List<ParkVO>>
     */
    @ApiOperation("查询园区列表")
    @ApiImplicitParam(name="companyType",value="企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任",dataType="Integer", required = true)
    @PostMapping("/findParkList")
    public ResultVo<List<ParkVO>> queryParkList(@JsonParam Integer companyType){
        if(null == companyType){
            return ResultVo.Fail("企业类型不能为空");
        }
        List<ParkVO> parkList = parkService.getAllParkAndPolicy(companyType, getRequestHeadParams("oemCode"));
        return ResultVo.Success(parkList);
    }

    /**
     * @Author yejian
     * @Date 2020/03/26 09:33
     * @param companyId
     * @return ResultVo<ParkEntity>
     */
    @ApiOperation("查询园区详情")
    @ApiImplicitParam(name="companyId",value="企业id",dataType="Long", required = true)
    @PostMapping("/getParkDetail")
    public ResultVo<ParkEntity> getParkDetail(@JsonParam Long companyId)  {
        if(null == companyId){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(companyId);
        company.setOemCode(getRequestHeadParams("oemCode"));
        company.setMemberId(getCurrUserId());
        company = memberCompanyService.selectOne(company);
        if(null == company){
            return ResultVo.Fail("未查询到企业");
        }

        // 查询园区
        ParkEntity park = new ParkEntity();
        park.setId(company.getParkId());
        park.setStatus(1);//状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        park = parkService.selectOne(park);
        if(null == park){
            return ResultVo.Fail("园区不存在或未上架");
        }
        return ResultVo.Success(park);
    }

    /**
     * @Author yejian
     * @Date 2020/03/26 14:03
     * @param
     * @return ResultVo<List<LogisCompanyEntity>>
     */
    @ApiOperation("查询快递公司列表")
    @PostMapping("/findLogisCompany")
    public ResultVo<List<LogisCompanyEntity>> findLogisCompany() {
        List<LogisCompanyEntity> logisCompanyList = logisCompanyService.logisCompanyList();
        return ResultVo.Success(logisCompanyList);
    }

    /**
     * @Description 根据机构编码和模板类型查询微信订阅消息模板
     * @Author  Kaven
     * @Date   2020/6/19 9:46 上午
     * @Param  oemCode templateType-模板类型：1-工商开户审核
     * @Return ResultVo<WechatMessageTemplateEntity>
     * @Exception
    */
    @ApiOperation("查询微信订阅消息模板")
    @PostMapping("/wechatmsg/getWechatMsgTemplate")
    public ResultVo<List<WechatMessageTemplateSimpleVO>> getWechatMsgTemplate(@JsonParam String templateType){
        if(StringUtils.isBlank(templateType)){
            return ResultVo.Fail("模板ID不能为空");
        }
        int[] templateTypes;
        try {
            templateTypes = Arrays.stream(templateType.split(",")).filter(a -> StringUtils.isNotBlank(a)).mapToInt(Integer::parseInt).toArray();
            if (templateTypes == null || templateTypes.length == 0) {
                return ResultVo.Fail("模板ID不能为空");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultVo.Fail("模板ID有误");
        }
        List<WechatMessageTemplateSimpleVO> list = wechatMessageTemplateService.getByTemplateTypes(templateTypes, getRequestHeadParams("oemCode"));
        return ResultVo.Success(list);
    }

    /**
     * 企查查
     * @param name
     * @return
     */
    @PostMapping("/creditCode")
    public ResultVo creditCode(@JsonParam String name) {
        if (StringUtil.isBlank(name)) {
            return ResultVo.Fail("企业名称为空");
        }
        CreditCodeEntity creditCodeEntity = creditCodeService.getCreditCode(getRequestHeadParams("oemCode"), name);
        return ResultVo.Success(creditCodeEntity);
    }
}