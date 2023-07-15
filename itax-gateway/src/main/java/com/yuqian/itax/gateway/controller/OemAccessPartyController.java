package com.yuqian.itax.gateway.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duob.encrypt.EncryptUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.*;
import com.yuqian.itax.agent.enums.OemAccessPartyStatusEnum;
import com.yuqian.itax.agent.enums.OemStatusEnum;
import com.yuqian.itax.agent.enums.SourceTypeEnum;
import com.yuqian.itax.agent.service.*;
import com.yuqian.itax.agreement.entity.query.ParkAgreementsQuery;
import com.yuqian.itax.agreement.entity.vo.ParkAgreementsVO;
import com.yuqian.itax.agreement.service.AgreementTemplateService;
import com.yuqian.itax.capital.entity.dto.UserRechargeDTO;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.gateway.annotation.JsonParam;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.RegisterOrderEntity;
import com.yuqian.itax.order.entity.dto.*;
import com.yuqian.itax.order.entity.query.AccessPartyOrderQuery;
import com.yuqian.itax.order.entity.query.RegOrderQuery;
import com.yuqian.itax.order.entity.vo.RegisterOrderOfAccessPartyVO;
import com.yuqian.itax.order.entity.vo.RegisterOrderVO;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.enums.RegOrderStatusEnum;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.query.GetUsableParkIndustryQuery;
import com.yuqian.itax.park.entity.vo.ParkProcessMarkVO;
import com.yuqian.itax.park.enums.ParkProcessMarkEnum;
import com.yuqian.itax.park.enums.ParkStatusEnum;
import com.yuqian.itax.park.service.ParkAgreementService;
import com.yuqian.itax.park.service.ParkDisableWordService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxPolicyService;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.ProductParkRelaEntity;
import com.yuqian.itax.product.entity.dto.ProductDiscountActivityAPIDTO;
import com.yuqian.itax.product.entity.vo.ProductDetailVO;
import com.yuqian.itax.product.entity.vo.ProductDiscountActivityVO;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ChargeStandardService;
import com.yuqian.itax.product.service.ProductDiscountActivityService;
import com.yuqian.itax.product.service.ProductParkRelaService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.IndustryEntity;
import com.yuqian.itax.system.entity.OssPolicyResult;
import com.yuqian.itax.system.entity.dto.UploadFileDTO;
import com.yuqian.itax.system.entity.query.ParkBusinessScopeQuery;
import com.yuqian.itax.system.entity.vo.IndustryApiVO;
import com.yuqian.itax.system.entity.vo.IndustryInfoVO;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.IndustryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.system.service.ParkBusinessscopeService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.dto.AccessPartyLoginDTO;
import com.yuqian.itax.user.entity.dto.UserAuthDTO;
import com.yuqian.itax.user.entity.query.AccessPartyCompanyQuery;
import com.yuqian.itax.user.entity.vo.CompanyListOfAccessPartyVO;
import com.yuqian.itax.user.entity.vo.MemberCompanyDetailVo;
import com.yuqian.itax.user.enums.*;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.user.service.MemberProfitsRulesService;
import com.yuqian.itax.util.util.*;
import com.yuqian.itax.util.util.channel.DESUtils;
import com.yuqian.itax.util.util.channel.RSA2Util;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * @ClassName OemAccessPartyControllers
 * @Description oem接入方控制器
 * @Author lmh
 * @Date 2021/8/5 9:53
 * @Version 1.0
 */
@RestController
@RequestMapping("/accessParty")
@Slf4j
public class OemAccessPartyController  extends BaseController {

    @Resource
    private OemAccessPartyService oemAccessPartyService;

    @Resource
    private MemberAccountService memberAccountService;

    @Resource
    private RegisterOrderService registerOrderService;

    @Resource
    private MemberCompanyService memberCompanyService;

    @Resource
    private ParkService parkService;

    @Resource
    private OssService ossService;

    @Resource
    private ProductService productService;

    @Resource
    private IndustryService industryService;

    @Resource
    private ProductDiscountActivityService productDiscountActivityService;

    @Resource
    private ParkAgreementService parkAgreementService;

    @Resource
    private ChargeStandardService chargeStandardService;

    @Resource
    private OrderService orderService;

    @Resource
    private TaxPolicyService taxPolicyService;

    @Resource
    private OemParamsService oemParamsService;

    @Resource
    private OemService oemService;

    @Resource
    private InvoiceOrderService invoiceOrderService;

    @Resource
    private DictionaryService dictionaryService;

    @Resource
    private OemConfigService oemConfigService;

    @Resource
    private ParkDisableWordService parkDisableWorkService;

    @Resource
    private ParkBusinessscopeService parkBusinessScopeService;

    @Resource
    private OemParkRelaService oemParkService;

    @Autowired
    private AgreementTemplateService agreementTemplateService;

    @Autowired
    private MemberProfitsRulesService memberProfitsRulesService;

    @Autowired
    private ProductParkRelaService productParkRelaService;

    /**
     * 用户自动注册登录接口，返回登录token及用户id
     * @param dto
     * @param bindingResult
     * @return
     */
    @ApiOperation("自动注册/登录")
    @PostMapping("/login")
    public ResultVo login(@RequestBody @Validated AccessPartyLoginDTO dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResultVo.Fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        if (StringUtil.isBlank(dto.getAccount())) {
            throw new BusinessException("手机号不能为空");
        }

        dto.setOemCode(getRequestHeadParams("oemCode"));
        dto.setAccessPartyCode(getRequestHeadParams("accessPartyCode"));
        Map<String, Object> dateMap = memberAccountService.loginOfAccessParty(dto);

        return ResultVo.Success(dateMap);
    }

    /**
     * @Description 查询用户信息接口，用于构造接入方首页页面信息,包括用户基本信息（含未支付、未完成注册订单数）及企业列表信息
     * @return
     */
    @ApiOperation("用户信息")
    @PostMapping("/memberInfo")
    public ResultVo memberInfo(@JsonParam Integer pageNumber, @JsonParam Integer pageSize) {
        HashMap<String, Object> dateMap = new HashMap<>();

        // 查询当前登录用户信息
        MemberAccountEntity member = memberAccountService.findById(getCurrUserId());
        if (null == member || !MemberStateEnum.STATE_ACTIVE.getValue().equals(member.getStatus())) {
            throw new BusinessException("未查询到用户信息");
        }

        // 查询机构信息
        OemEntity oemEntity = Optional.ofNullable(oemService.getOem(getRequestHeadParams("oemCode"))).orElseThrow(() -> new BusinessException("未查询到机构信息"));

        // 获取用户接入方信息
        OemAccessPartyEntity accessParty = Optional.ofNullable(oemAccessPartyService.findById(member.getAccessPartyId())).orElseThrow(() -> new BusinessException("未查询到接入方信息"));
        if (!getRequestHeadParams("accessPartyCode").equals(accessParty.getAccessPartyCode())) {
            throw new BusinessException("接入方信息不一致！");
        }

        // 构造用户基本信息
        HashMap<String, Object> memberBaseInfo = new HashMap<>();
        memberBaseInfo.put("memberId", member.getId());
        memberBaseInfo.put("memberAccount", member.getMemberAccount());
        memberBaseInfo.put("oemCode", member.getOemCode());
        memberBaseInfo.put("oemMobile", oemEntity.getOemPhone());
        memberBaseInfo.put("accessPartyId", member.getAccessPartyId());
        memberBaseInfo.put("authStatus", member.getAuthStatus());
        memberBaseInfo.put("realName", member.getRealName());
        memberBaseInfo.put("idCardNo", member.getIdCardNo());
        memberBaseInfo.put("idCardFront", member.getIdCardFront());
        memberBaseInfo.put("idCardBack", member.getIdCardBack());
        memberBaseInfo.put("idCardAddr", member.getIdCardAddr());
        memberBaseInfo.put("expireDate", member.getExpireDate());
        // 查询用户是否存在未支付注册订单
        AccessPartyOrderQuery query = new AccessPartyOrderQuery();
        query.setOemCode(member.getOemCode());
        query.setUserId(member.getId());
        query.setOrderStatisticsStatus(2);
        List<RegisterOrderOfAccessPartyVO> registerOrderUnpaid = registerOrderService.listByMemberId(query);
        memberBaseInfo.put("unPaidOrder", null == registerOrderUnpaid ? 0 : registerOrderUnpaid.size());
        // 查询用户是否存在未完成注册订单
        query.setOrderStatisticsStatus(3);
        List<RegisterOrderOfAccessPartyVO> registerOrderUnfinished = registerOrderService.listByMemberId(query);
        memberBaseInfo.put("registerOrderUnfinished", null == registerOrderUnfinished ? 0 : registerOrderUnfinished.size());
        dateMap.put("memberBaseInfo", memberBaseInfo);

        // 构造企业列表信息
        PageHelper.startPage(null == pageNumber ? query.getPageNumber() : pageNumber, null == pageSize ? query.getPageSize() : pageSize);
        ArrayList<Map> companyList = Lists.newArrayList();
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setMemberId(member.getId());
        List<MemberCompanyEntity> companyEntityList = memberCompanyService.select(company);
        if (companyEntityList.isEmpty() || null == companyEntityList) {
            dateMap.put("companyListInfo", PageResultVo.restPage(companyList));
            return ResultVo.Success(dateMap);
        }
        for (MemberCompanyEntity companyEntity : companyEntityList) {
            HashMap<String, Object> companyMap = new HashMap<>();
            companyMap.put("companyId", companyEntity.getId());
            companyMap.put("companyName", companyEntity.getCompanyName());
            companyMap.put("companyType", companyEntity.getCompanyType());
            companyMap.put("operatorName", companyEntity.getOperatorName());
            companyMap.put("endTime", DateUtil.formatDefaultDate(companyEntity.getEndTime()));
            companyMap.put("overdueStatus", companyEntity.getOverdueStatus());
            // 查询园区
            ParkEntity park = Optional.ofNullable(parkService.findById(companyEntity.getParkId())).orElseThrow(() -> new BusinessException("未查询到园区信息"));
            companyMap.put("parkName", park.getParkName());
            companyList.add(companyMap);
        }
        dateMap.put("companyListInfo", PageResultVo.restPage(companyList));

        return ResultVo.Success(dateMap);
    }

    /**
     * 获取企业详情接口
     * @param companyId
     * @return
     */
    @ApiOperation("企业详情")
    @PostMapping("/companyDetail")
    public ResultVo detail(@JsonParam Long companyId){
        if(null == companyId){
            return ResultVo.Fail("企业ID不能为空");
        }
        MemberCompanyDetailVo memberCompanyDetailVo = memberCompanyService.getMemberCompanyDetail(getCurrUserId(), getRequestHeadParams("oemCode"), companyId);
        return ResultVo.Success(memberCompanyDetailVo);
    }

    /**
     * 身份证正反面ocr识别
     * @param fileDto
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "身份证OCR识别")
    @PostMapping("/ocrIdentify")
    public ResultVo ocrIdentify(@RequestBody UploadFileDTO fileDto) throws Exception {
        if(null == fileDto){
            return ResultVo.Fail("传入对象为空");
        }
        if(StringUtils.isBlank(fileDto.getFileUrl()) || null == fileDto.getType()){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }

        fileDto.setUserId(getCurrUserId());
        fileDto.setOemCode(this.getRequestHeadParams("oemCode"));
        Map<String,Object> resultMap = this.ossService.ocrIdentify(getRequestHeadParams("oemCode"), fileDto);
        return  ResultVo.Success(resultMap);
    }

    @ApiOperation("接入方用户实名认证（二要素验证）")
    @PostMapping("/userAuth")
    public ResultVo userAuth(@RequestBody @Valid UserAuthDTO dto, BindingResult results) throws IOException {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        // 已实名用户不允许重复实名（V3.12 接入方用户只拦截h5入口，不拦截对外接口）
        MemberAccountEntity member = memberAccountService.findById(getCurrUserId());
        if (null == member) {
            return ResultVo.Fail("用户不存在");
        }
        if (MemberAuthStatusEnum.AUTH_SUCCESS.getValue().equals(member.getAuthStatus())) {
            return ResultVo.Fail("请勿重复认证");
        }
        dto.setIsOther(0);// 用户本人实名认证
        memberAccountService.userAuth(getCurrUser().getUserId(),oemCode,dto,0);
        return ResultVo.Success();
    }

    @ApiOperation("查询oem开通的开户类型的产品列表")
    @PostMapping("/listProduct")
    public ResultVo listProduct(){
        List<ProductEntity> productList = this.productService.queryProductList(getRequestHeadParams("oemCode"));
        return ResultVo.Success(productList);
    }

    @ApiOperation("产品详情，获取oem下全部的园区列表")
    @ApiImplicitParam(name="productId",value="产品id",dataType="Long",required = true)
    @PostMapping("/productDetail")
    public ResultVo<ProductDetailVO> getProductDetail(@JsonParam Long productId){
        if(null == productId){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        ProductDetailVO productDetail = this.productService.queryProductDetail(productId);
        return ResultVo.Success(productDetail);
    }

    @ApiOperation("获取oss私有域图片访问地址")
    @ApiImplicitParam(name="key",value="路径+文件名(YCS/20191228/123.jpg)",dataType="String",required = true)
    @PostMapping("/getUrl")
    public ResultVo getUrl(@JsonParam String key) {
        if(StringUtils.isBlank(key)) {
            return ResultVo.Fail("参数不能为空");
        }
        JSONObject params = new JSONObject();
        params.put("key", key);
        String url = ossService.getUrl(key, 0);
        return ResultVo.Success(url);
    }

    @ApiOperation(value = "oss上传签名生成")
    @PostMapping(value = "/policy")
    public ResultVo<OssPolicyResult> policy() {
        String oemCode = getRequestHeadParams("oemCode");
        OssPolicyResult result = ossService.policy(oemCode,"");
        return ResultVo.Success(result);
    }

    @ApiOperation("行业列表")
    @PostMapping("/industryList")
    public ResultVo<List<IndustryApiVO>> industryList(@JsonParam Long parkId, @JsonParam Integer companyType){
        if(null == parkId){
            return ResultVo.Fail("园区ID不能为空");
        }
        if(null == companyType){
            companyType = 1;
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        List<IndustryApiVO> list = this.industryService.selectIndustry(oemCode, parkId, companyType, null);
        return ResultVo.Success(list);
    }

    @ApiOperation("根据行业获取注册信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="industryId",value="行业ID",dataType="Long",required = true),
            @ApiImplicitParam(name="parkId",value="园区ID",dataType="Long",required = true)
    })
    @PostMapping("/getLogonMessageByIndustry")
    public ResultVo<IndustryInfoVO> getLogonMessageByIndustry(@JsonParam Long industryId, @JsonParam Long parkId){
        if(null == industryId){
            return ResultVo.Fail("行业ID不能为空");
        }
        if(null == parkId){
            return ResultVo.Fail("园区ID不能为空");
        }
        IndustryInfoVO industryVo = this.industryService.getById(industryId,parkId);
        return ResultVo.Success(industryVo);
    }

    @ApiOperation("获取产品特价活动信息")
    @PostMapping("/getProductDiscountActivityByProductType")
    public ResultVo getProductDiscountActivityByProductType(@RequestBody ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO) {
        productDiscountActivityAPIDTO.setMemberId(getCurrUserId());
        String oemCode = getRequestHeadParams("oemCode");
        if(org.apache.commons.lang.StringUtils.isBlank(oemCode)){
            return ResultVo.Fail("oemCode不能为空");
        }
        productDiscountActivityAPIDTO.setOemCode(oemCode);
        if(productDiscountActivityAPIDTO.getProductType() == null){
            return ResultVo.Fail("产品类型不能为空");
        }
        if(productDiscountActivityAPIDTO.getProductType() != null
                && ObjectUtil.equal(productDiscountActivityAPIDTO.getProductType(),1)){
            if(productDiscountActivityAPIDTO.getIndustryId() == null){
                return ResultVo.Fail("行业id不能为空");
            }
            if(productDiscountActivityAPIDTO.getParkId() == null){
                return ResultVo.Fail("园区id不能为空");
            }
        }else{
            if(productDiscountActivityAPIDTO.getCompanyId() == null){
                return ResultVo.Fail("企业id不能为空");
            }
            MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(productDiscountActivityAPIDTO.getCompanyId());
            if(memberCompanyEntity == null){
                return ResultVo.Fail("未找到企业信息");
            }else if(ObjectUtil.equal(memberCompanyEntity.getStatus(),4)){
                return ResultVo.Fail("企业已注销，不能进行该操作");
            }
            productDiscountActivityAPIDTO.setParkId(memberCompanyEntity.getParkId());
            productDiscountActivityAPIDTO.setIndustryId(memberCompanyEntity.getIndustryId());
        }
        ProductDiscountActivityVO productDiscountActivityVO =  productDiscountActivityService.getProductDiscountActivityByProductType(productDiscountActivityAPIDTO);
        return ResultVo.Success(productDiscountActivityVO);
    }

    @ApiOperation("根据园区编码获取协议列表")
    @PostMapping("/getParkAgreements")
    public ResultVo getParkAgreements(@JsonParam Long parkId){
        if(null == parkId){
            return ResultVo.Fail("园区id不能为空");
        }
        // 查询园区
        ParkEntity park = parkService.findById(parkId);
        if (null == park || !ParkStatusEnum.ON_SHELF.getValue().equals(park.getStatus())) {
            return ResultVo.Fail("园区不存在或不可用");
        }
        ParkAgreementsQuery query = new ParkAgreementsQuery();
        String oemCode = getRequestHeadParams("oemCode");
        query.setOemCode(oemCode);
        query.setParkId(parkId);
        // 查询产品
        ProductEntity productEntity = productService.queryProductByProdType(ProductTypeEnum.INDIVIDUAL.getValue(), oemCode, parkId);
        if (null == productEntity) {
            return ResultVo.Fail("未查询到产品信息");
        }
        query.setProductId(productEntity.getId());
        List<ParkAgreementsVO> vos = agreementTemplateService.queryParkAgreements(query);
        if (CollectionUtil.isEmpty(vos)) {
            return ResultVo.Success(vos);
        }
        // 获取oss请求参数
        String reqHead = dictionaryService.getValueByCode("oss_req_head");
        String host = dictionaryService.getValueByCode("oss_access_public_host");
        String reqPrefix = reqHead + host;
        // 获取模板全地址并附带参数
        for (ParkAgreementsVO vo : vos) {
            if (StringUtil.isBlank(vo.getTemplateHtmlUrl())) {
                return ResultVo.Fail("模板地址不存在");
            }
            String agreementTemplateType = dictionaryService.getValueByCode("agreement_template_type");
            String url = reqPrefix + vo.getTemplateHtmlUrl()
                    + "?oemCode=" + oemCode
                    + "&parkId=" + parkId
                    + "&memberId=" + getCurrUserId()
                    + "&type=" + agreementTemplateType
                    + "&companyType=1";
            vo.setTemplateHtmlUrl(url);
        }
        return ResultVo.Success(vos);
    }

    /**
     * 注册协议列表查询接口（对外接口）
     * @param parkId
     * @return
     */
    @ApiOperation("注册协议列表查询接口")
    @PostMapping("/findParkAgreements")
    public ResultVo findParkAgreements(@JsonParam Long parkId){
        if (null == parkId) {
            return ResultVo.Fail("园区id不能为空");
        }

        // 查询园区
        ParkEntity park = Optional.ofNullable(parkService.findById(parkId)).orElseThrow(() -> new BusinessException("未查询到园区信息"));
        if (!ParkStatusEnum.ON_SHELF.getValue().equals(park.getStatus())) {
            return ResultVo.Fail("园区不可用");
        }
        List<Map> list = Lists.newArrayList();
        ParkAgreementsQuery query = new ParkAgreementsQuery();
        String oemCode = getRequestHeadParams("oemCode");
        query.setOemCode(oemCode);
        query.setParkId(parkId);
        // 查询产品
        ProductEntity productEntity = productService.queryProductByProdType(ProductTypeEnum.INDIVIDUAL.getValue(), oemCode, parkId);
        if (null == productEntity) {
            return ResultVo.Fail("未查询到产品信息");
        }
        query.setProductId(productEntity.getId());
        List<ParkAgreementsVO> vos = agreementTemplateService.queryParkAgreements(query);
        if (CollectionUtil.isEmpty(vos)) {
            return ResultVo.Success(vos);
        }
        // 获取oss请求参数
        String reqHead = dictionaryService.getValueByCode("oss_req_head");
        String host = dictionaryService.getValueByCode("oss_access_public_host");
        String reqPrefix = reqHead + host;
        // 获取模板全地址并附带参数
        for (ParkAgreementsVO vo : vos) {
            Map<String, String> map = Maps.newHashMap();
            if (StringUtil.isBlank(vo.getTemplateHtmlUrl())) {
                return ResultVo.Fail("模板地址不存在");
            }
            String agreementTemplateType = dictionaryService.getValueByCode("agreement_template_type");
            String url = reqPrefix + vo.getTemplateHtmlUrl()
                    + "?oemCode=" + oemCode
                    + "&parkId=" + parkId
                    + "&memberId=" + getCurrUserId()
                    + "&type=" + agreementTemplateType;
            map.put("agreementName", vo.getTemplateName());
            map.put("agreementViewUrl", url);
            list.add(map);
        }
        return ResultVo.Success(list);
    }

    @ApiOperation("获取平台收费标准")
    @ApiImplicitParam(name="productId",value="产品id",dataType="Long",required = true)
    @PostMapping("/getChargeStandard")
    public ResultVo getChargeStandard(@JsonParam Long productId, @JsonParam Long parkId){
        if(null == productId){
            return ResultVo.Fail("产品ID不能为空！");
        }
        JSONObject params = new JSONObject();
        params.put("productId", productId);
        Map<String,Object> dataMap = this.chargeStandardService.queryChargeStandards(productId, parkId, getCurrUserId(), null);
        return ResultVo.Success(dataMap);
    }

    @ApiOperation("创建工商注册订单")
    @PostMapping("/createRegOrder")
    public ResultVo createRegOrder(@RequestBody @Validated RegisterOrderDTO entity, BindingResult result){
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        if (null == entity) {
            return ResultVo.Fail("操作失败，订单对象不能为空！");
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        String oemCode = this.getRequestHeadParams("oemCode");
        entity.setOemCode(oemCode);

        // 前置校验
        this.regPreCheck(oemCode, getCurrUserId(), getRequestHeadParams("accessPartyCode"));

        String registerRedisTime = (System.currentTimeMillis() + 60000) + "";
        boolean lock = redisService.lock(RedisKey.REGIST_REDIS_KEY+ "_" + entity.getOemCode() + "_" + getCurrUser().getUseraccount(), registerRedisTime, 120);
        if (!lock) {
            throw new BusinessException("请勿重复提交！");
        }
        String orderNo = null;
        try {
            entity.setCompanyType(MemberCompanyTypeEnum.INDIVIDUAL.getValue());
            entity.setTaxpayerType(CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue());
            entity.setIsSupplyShopName(1);
            orderNo = this.orderService.createIndustryOrder(getCurrUserId(), entity);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        } finally {
            redisService.unlock(RedisKey.REGIST_REDIS_KEY+ "_" + entity.getOemCode() + "_" + getCurrUser().getUseraccount(), registerRedisTime);
        }
        resultMap.put("orderNo",orderNo);
        return ResultVo.Success(resultMap);
    }

    /**
     * 注册前置校验
     * @param oemCode
     * @param userId
     */
    private void regPreCheck(String oemCode, Long userId, String accessPartyCode) {
        // 查询用户是否存在未支付注册订单
        AccessPartyOrderQuery orderQuery = new AccessPartyOrderQuery();
        orderQuery.setOemCode(oemCode);
        orderQuery.setUserId(userId);
        orderQuery.setOrderStatisticsStatus(2);
        orderQuery.setAccessPartyCode(accessPartyCode);
        List<RegisterOrderOfAccessPartyVO> registerOrderUnpaid = registerOrderService.listByMemberId(orderQuery);
        if (null != registerOrderUnpaid && registerOrderUnpaid.size() > 0) {
            throw new BusinessException("存在未支付的订单!");
        }
        // 查询用户是否存在未完成注册订单
        orderQuery.setOrderStatisticsStatus(3);
        List<RegisterOrderOfAccessPartyVO> registerOrderUnfinished = registerOrderService.listByMemberId(orderQuery);
        if (null != registerOrderUnfinished && registerOrderUnfinished.size() > 0) {
            throw new BusinessException("存在未完成的订单!");
        }
        // 校验接入方账户是否存在个体户
        AccessPartyCompanyQuery companyQuery = new AccessPartyCompanyQuery();
        companyQuery.setAccessPartyCode(accessPartyCode);
        companyQuery.setUserId(userId);
        companyQuery.setIsIncludeCancelled(0);
        List<CompanyListOfAccessPartyVO> companyList = memberCompanyService.listByMemberIds(companyQuery);
        if (companyList.size() > 0) {
            throw new BusinessException("您已在其他渠道注册！");
        }
    }

    /**
     * 接入方选定园区行业自主创建注册订单（对外接口）
     * @return
     */
    @ApiOperation("接入方创建注册订单")
    @PostMapping("/createRegisterOrder")
    public ResultVo createRegisterOrder(@RequestBody @Validated AccessPartyRegisterOrderDTO entity, BindingResult result){
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        if (null == entity) {
            return ResultVo.Fail("操作失败，订单对象不能为空！");
        }

        String oemCode = this.getRequestHeadParams("oemCode");
        entity.setOemCode(oemCode);
        if (StringUtil.isBlank(entity.getIndustryBusinessScope())) {
            throw new BusinessException("行业经营范围不能为空");
        }

        // 前置校验
        this.regPreCheck(oemCode, getCurrUserId(), getRequestHeadParams("accessPartyCode"));

        String registerRedisTime = (System.currentTimeMillis() + 60000) + "";
        boolean lock = redisService.lock(RedisKey.REGIST_REDIS_KEY+ "_" + entity.getOemCode() + "_" + getCurrUser().getUseraccount(), registerRedisTime, 120);
        if (!lock) {
            throw new BusinessException("请勿重复提交！");
        }
        String orderNo = null;
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            resultMap = this.orderService.createRegisterOrder(getCurrUserId(), entity);
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        } finally {
            redisService.unlock(RedisKey.REGIST_REDIS_KEY+ "_" + entity.getOemCode() + "_" + getCurrUser().getUseraccount(), registerRedisTime);
        }
        return ResultVo.Success(resultMap);
    }

    @ApiOperation("查询视频认证阅读内容")
    @PostMapping("/getReadContent")
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
        dataMap.put("readContent",tpe.getReadContent());
        return ResultVo.Success(dataMap);
    }

    @ApiOperation("更新注册订单文件（签字/认证视频/补充资料）")
    @PostMapping("/updateRegOrderFile")
    public ResultVo updateRegOrderFile(@RequestBody @Validated RegOrderFileDTO uploadDto, BindingResult result){
        if(null == uploadDto){
            return ResultVo.Fail("参数不能为空！");
        }

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        if(StringUtils.isBlank(uploadDto.getFileUrl()) || StringUtils.isBlank(uploadDto.getOrderNo())
                || null == uploadDto.getStep()){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        Map<String,Object> resultMap = this.orderService.updateRegOrderFile(getCurrUser().getUserId(),uploadDto.getFileUrl(),uploadDto.getOrderNo(),uploadDto.getStep(),uploadDto.getVersionCode());
        return ResultVo.Success(resultMap);
    }

    @ApiOperation("工商注册订单支付")
    @PostMapping("/payOrder")
    public ResultVo payRegOrder(@RequestBody @Validated RegOrderPayDTO payDto, BindingResult result) {
        log.info("收到支付请求：{}", JSON.toJSONString(payDto));

        String registerRedisTime = (System.currentTimeMillis() + 60000) + "";

        ResultVo resultVo = ResultVo.Success();
        if(null == payDto){
            return ResultVo.Fail("订单支付失败，参数不能为空！");
        }

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        // 工商订单注册订单支付时，订单号必传
        if(OrderTypeEnum.REGISTER.getValue().equals(payDto.getOrderType()) && StringUtils.isBlank(payDto.getOrderNo())){
            return ResultVo.Fail("订单号不能为空");
        }
        payDto.setCurrUserId(getCurrUserId());
        if(StringUtils.isBlank(payDto.getPayChannel())){
            payDto.setPayChannel("1");// 向下兼容，默认微信支付
        }
        payDto.setOemCode(this.getRequestHeadParams("oemCode"));
        payDto.setSourceType(3);

        // 防重复支付
        boolean lock = redisService.lock(RedisKey.REGISTER_ORDER_PAY_LOCK_KEY + "_" + payDto.getOrderNo(), registerRedisTime, 2);
        if (!lock) {
            throw new BusinessException("请勿重复支付！");
        }
        JSONObject data = null;
        try {
            data = this.registerOrderService.orderPay(payDto);
        } catch (Exception e) {
            log.info("支付失败：{}", e.getMessage());
            throw new BusinessException(e.getMessage());
        } finally {
            redisService.unlock(RedisKey.REGISTER_ORDER_PAY_LOCK_KEY + "_" + payDto.getOrderNo(), registerRedisTime);
        }
        resultVo.setData(data);
        return resultVo;
    }

    @ApiOperation("渠道端支付回调地址")
    @PostMapping("/weChatNotify")
    public String weChatNotify(@RequestParam Map<String,String> reqData) throws SQLException {
        if(null == reqData){
            log.error("未收到渠道微信/支付宝回调请求参数...");
            return null;
        }

        boolean flag = false;// 新旧渠道标识，默认旧渠道
        String payNo = null;// 交易流水号
        String orderStatus = null; // 订单状态

        //读取渠道支付相关配置，对数据进行解密
        OemParamsEntity t = new OemParamsEntity();
        if(StringUtils.isNotBlank(reqData.get("agentNo"))){ // 旧渠道返回
            t.setAccount(reqData.get("agentNo"));
        } else if(StringUtils.isNotBlank(reqData.get("merNo"))){ // 新渠道返回
            t.setAccount(reqData.get("merNo"));
            flag = true;// 新渠道
        } else {
            throw new BusinessException("微信/支付宝回调处理失败，未知的商户信息");
        }
//        t.setStatus(1);
        t.setParamsType(2);
        OemParamsEntity paramsEntity = this.oemParamsService.selectOne(t);
        if(null == paramsEntity){
            throw new BusinessException("微信/支付宝回调处理失败，未找到对应商户号的支付配置信息！");
        }

        // 数据解密
        String keyNumSrc = reqData.get("keyNum").replaceAll(" ","+");// keyNum密文（防止+号被替换为空格）
        String dataStr = reqData.get("data").replaceAll(" ","+"); // 数据密文
        String deckeyNum = null;
        Map<String, String> data = null;// 旧渠道解密后数据
        JSONObject jsonData = null;// 新渠道解密后数据
        try{
            // 解密keyNum和data数据
            if(flag){
                // 新渠道解密
                deckeyNum = new String(RSA2Util.decryptByPrivateKey(cn.hutool.core.codec.Base64.decode(keyNumSrc), paramsEntity.getPrivateKey().trim()));
                String decDataStr = new String(DESUtils.des3DecodeECB(deckeyNum.getBytes(), Base64.decode(dataStr)));
                jsonData = JSONObject.parseObject(decDataStr);
                log.info("解密渠道回调data数据：{}",JSON.toJSONString(jsonData));
                payNo = jsonData.getString("orderNo");
                orderStatus = jsonData.getString("status");
            } else {
                // 旧渠道解密
                deckeyNum = EncryptUtil.decSm2SecretKey(keyNumSrc, paramsEntity.getPrivateKey());
                data = EncryptUtil.decSm4Data(dataStr, deckeyNum);
                log.info("解密渠道回调data数据：{}",JSON.toJSONString(data));
                payNo = data.get("orderNo");
                orderStatus = data.get("status");
            }
        }catch (Exception e){
            log.error("参数解密错误：{}",e.getMessage());
            return "参数解密错误";
        }

        Date payDate = new Date();
        if("5".equals(orderStatus)) { // 订单支付成功
            // 更新订单状态
            this.orderService.updateOrderStatus(payNo, null, payDate, MessageEnum.SUCCESS.getValue(),orderStatus,MessageEnum.SUCCESS.getMessage());
        } else {//订单支付失败
            // 更新订单状态
            this.orderService.updateOrderStatus(payNo, null, payDate, MessageEnum.SYSTEM_ERROR.getValue() ,orderStatus, "支付失败");
        }
        log.info("渠道微信/支付宝支付回调成功...");
        return "SUCCESS";
    }

    /**
     * @Description 渠道端支付手工回调地址（仅在渠道端回调失败时使用）
     * @Author  Kaven
     * @Date   2020/1/13 17:19
     * @Param  payNo 支付流水号  orderStatus 订单状态=5
     * @Return
     * @Exception
     */
    @ApiOperation("渠道端支付手工回调地址")
    @PostMapping("/weChatNotifyByHand")
    public String weChatNotifyByHand(@JsonParam String payNo, @JsonParam String orderStatus) throws SQLException {
        Date payDate = new Date();

        if("5".equals(orderStatus)) { // 订单支付成功
            // 更新订单状态
            this.orderService.updateOrderStatus(payNo, null, payDate, MessageEnum.SUCCESS.getValue() ,orderStatus,MessageEnum.SUCCESS.getMessage());
        } else {//订单支付失败
            // 更新订单状态
            this.orderService.updateOrderStatus(payNo, null, payDate, MessageEnum.SYSTEM_ERROR.getValue(), orderStatus, "支付失败");
        }
        log.info("渠道端微信手工回调成功...");
        return JSON.toJSONString(ResultVo.Success("手工回调成功"));
    }

    /**
     * 已唤起微信支付但未支付时取消订单
     * @param orderNo
     * @return
     */
    @ApiOperation("取消订单")
    @PostMapping("/cancelOrder")
    public ResultVo cancelOrder(@JsonParam String orderNo){
        if(StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("取消订单失败，订单号不能为空！");
        }
        this.registerOrderService.cancelOrder(getCurrUser().getUserId(),orderNo);
        return ResultVo.Success();
    }

    @ApiOperation("长沙园区-确认已开启(根据订单号修改订单状态并进行自动派单)")
    @PostMapping("/ensureValidate")
    public ResultVo<Map<String,Object>> ensureValidate(@JsonParam String orderNo){
        log.info("收到长沙园区-确认已开启请求：{}",orderNo);

        if(StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("操作失败，订单号不能为空！");
        }
        this.registerOrderService.ensureValidate(orderNo,getCurrUser().getUserId(),getRequestHeadParams("oemCode"));
        return ResultVo.Success();
    }

    @ApiOperation("开户订单取消")
    @ApiImplicitParam(name="orderNo",value="订单号",dataType="String",required = true)
    @PostMapping("/cancelRegOrder")
    public ResultVo cancelRegOrder(@JsonParam String orderNo){
        try {
            registerOrderService.cancelUnpaidOrder(orderNo, getRequestHeadParams("oemCode"), getCurrUserId());
        } catch (BusinessException e) {
            if (100 == e.getErrCode()) {
                return ResultVo.Fail(e.getErrorCode(), e.getMessage());
            } else {
                return ResultVo.Fail(e.getMessage());
            }
        }
        return ResultVo.Success();
    }

    @ApiOperation("第三方取消未支付订单")
    @PostMapping("/cancelRegisterOrder")
    public ResultVo cancelRegisterOrder(@JsonParam String orderNo) {
        try {
            registerOrderService.cancelUnpaidOrder(orderNo, getRequestHeadParams("oemCode"), getCurrUserId());
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    @ApiOperation("重新提交企业注册订单")
    @PostMapping("/resubmitRegOrder")
    public ResultVo resubmitRegOrder(@RequestBody @Valid ResubmitRegOrderDTO entity, BindingResult result){
        log.info("收到重新提交企业注册订单请求：{}",JSON.toJSONString(entity));

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        if(null == entity){
            return ResultVo.Fail("操作失败，对象不能为空！");
        }
        entity.setOemCode(this.getRequestHeadParams("oemCode"));
        this.registerOrderService.resubmitRegOrder(getCurrUser().getUserId(),entity);
        return ResultVo.Success();
    }

    @ApiOperation("提交签名（浏阳园区）")
    @PostMapping("/open/submit/sign")
    public ResultVo openSubmitSign(@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNo(orderNo);
        orderEntity.setUserId(currUser.getUserId());
        orderEntity.setOrderType(OrderTypeEnum.REGISTER.getValue());
        orderEntity = orderService.selectOne(orderEntity);
        if (orderEntity == null) {
            throw new BusinessException("订单不存在");
        }
        ParkEntity parkEntity = parkService.findById(orderEntity.getParkId());
        if (parkEntity == null) {
            throw new BusinessException("订单归属园区不存在");
        }
        // 查询注册订单
        RegisterOrderEntity regOrder = registerOrderService.queryByOrderNo(orderNo);
        if (null == regOrder) {
            return ResultVo.Fail("未查询到注册订单");
        }
        // 查询园区产品注册流程
        ProductParkRelaEntity productParkRelaEntity = new ProductParkRelaEntity();
        productParkRelaEntity.setProductId(orderEntity.getProductId());
        productParkRelaEntity.setCompanyType(regOrder.getCompanyType());
        productParkRelaEntity.setParkId(orderEntity.getParkId());
        ProductParkRelaEntity entity = productParkRelaService.selectOne(productParkRelaEntity);
        if (null == entity) {
            return ResultVo.Fail("未查询到园区产品注册流程");
        }
        if (!Objects.equals(entity.getProcessMark(), ParkProcessMarkEnum.SIGN.getValue())) {
            throw new BusinessException("当前园区不需要提交签名");
        }
        if (!Objects.equals(orderEntity.getOrderStatus(), RegOrderStatusEnum.SIGNATURE_TO_BE_SUBMITTED.getValue())) {
            return ResultVo.Fail("当前订单状态不允许提交签名");
        }
        registerOrderService.openSubmitSign(orderEntity, RegOrderStatusEnum.SIGNATURE_CONFIRMATION.getValue(), currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 分页查询企业注册订单列表
     * @param query
     * @param result
     * @return
     */
    @ApiOperation("分页查询企业注册订单列表")
    @PostMapping("/listOrderPage")
    public ResultVo listOrderPage(@RequestBody RegOrderQuery query, BindingResult result){
        if(null == query){
            return ResultVo.Fail("查询参数不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        query.setUserId(getCurrUserId());
        query.setOemCode(this.getRequestHeadParams("oemCode"));
        PageInfo<RegisterOrderVO> pages = this.orderService.getOrderListPage(query);
        return ResultVo.Success(pages);
    }

    /**
     * 接入方查询个体户注册订单
     * @param query
     * @return
     */
    @ApiOperation("注册订单查询")
    @PostMapping("/findRegisterOrder")
    public ResultVo findRegisterOrder(@RequestBody AccessPartyOrderQuery query) {
        query.setOemCode(getRequestHeadParams("oemCode"));
        query.setAccessPartyCode(getRequestHeadParams("accessPartyCode"));

        // 查询oem机构
        OemEntity oem = Optional.ofNullable(oemService.getOem(query.getOemCode())).orElseThrow(() -> new BusinessException(ErrorCodeEnum.OEM_ACCESSPARTYCODE_ERROR));
        if (!OemStatusEnum.YES.getValue().equals(oem.getOemStatus())) {
            return ResultVo.Fail("机构未上架！");
        }
        // 查询接入方信息
        OemAccessPartyEntity oemAccessParty = Optional.ofNullable(oemAccessPartyService.queryByAccessPartyCode(query.getAccessPartyCode())).orElseThrow(() -> new BusinessException(ErrorCodeEnum.OEMCODE_ERROR));
        if (!OemAccessPartyStatusEnum.YES.getValue().equals(oemAccessParty.getStatus())) {
            return ResultVo.Fail("接入方已下架！");
        }

        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        log.info("接入方查询注册订单开始：{}", query);
        List<RegisterOrderOfAccessPartyVO> list;
        try {
            list = registerOrderService.listByMemberId(query);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultVo.Fail();
        }

        log.info("接入方查询注册订单结束。");
        return ResultVo.Success(PageResultVo.restPage(list));
    }

    /**
     * 接入方查询企业列表
     * @param query
     * @return
     */
    @ApiOperation("企业列表查询")
    @PostMapping("/findCompanyList")
    public ResultVo findCompanyList(@RequestBody AccessPartyCompanyQuery query) {
        query.setOemCode(getRequestHeadParams("oemCode"));
        query.setAccessPartyCode(getRequestHeadParams("accessPartyCode"));

        // 查询oem机构
        OemEntity oem = Optional.ofNullable(oemService.getOem(query.getOemCode())).orElseThrow(() -> new BusinessException(ErrorCodeEnum.OEM_ACCESSPARTYCODE_ERROR));
        if (!OemStatusEnum.YES.getValue().equals(oem.getOemStatus())) {
            return ResultVo.Fail("机构未上架！");
        }
        // 查询接入方信息
        OemAccessPartyEntity oemAccessParty = Optional.ofNullable(oemAccessPartyService.queryByAccessPartyCode(query.getAccessPartyCode())).orElseThrow(() -> new BusinessException(ErrorCodeEnum.OEMCODE_ERROR));
        if (!OemAccessPartyStatusEnum.YES.getValue().equals(oemAccessParty.getStatus())) {
            return ResultVo.Fail("接入方已下架！");
        }

        log.info("接入方查询企业列表开始：{}", query);
        List<CompanyListOfAccessPartyVO> list = null;
        try {
            query.setIsIncludeCancelled(1);
            list = memberCompanyService.listByMemberIds(query);
            for (int i = 0; i < list.size(); i++) {
                CompanyListOfAccessPartyVO vo = list.get(i);
                // 营业执照转base64
                String businessLicenseCopy = ossService.getPrivateImgUrl(vo.getBusinessLicenseCopy());
                if (StringUtil.isBlank(businessLicenseCopy)) {
                    throw new BusinessException("营业执照图片不存在");
                }
                String image = ImageUtils.netImageToBase64(businessLicenseCopy);
                if (StringUtil.isBlank(image)) {
                    throw new BusinessException("营业执照图片格式转换失败");
                }
                if(image.contains("\r\n")){
                    image = image.replace("\r\n","");
                }else if(image.contains("\r")){
                    image = image.replace("\r","");
                }else if(image.contains("\n")){
                    image = image.replace("\n","");
                }
                String suffix = vo.getBusinessLicenseCopy().substring(vo.getBusinessLicenseCopy().lastIndexOf(".") + 1);
                vo.setBusinessLicenseCopy("data:image/"+suffix + ";base64," + image);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultVo.Fail();
        }
        list.forEach(vo -> {
            if (MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue().equals(vo.getStatus())) {
                vo.setStatus(MemberCompanyStatusEnum.TAX_CANCELLED.getValue());
            }
        });

        log.info("接入方查询企业列表结束。");
        return ResultVo.Success(PageResultVo.restPage(list));
    }

    @ApiOperation("获取秘钥")
    @PostMapping("/getSecretKey")
    public ResultVo getSecretKey() {
        String oemCode = getRequestHeadParams("oemCode");
        String otherPayOemcode = getRequestHeadParams("otherPayOemcode");
        if(StringUtil.isNotBlank(otherPayOemcode)){
            oemCode= otherPayOemcode;
        }
        String accessPartyCode = getRequestHeadParams("accessPartyCode");
        if (StringUtil.isBlank(oemCode)) {
            throw new BusinessException("机构编码不能为空");
        }
        if (StringUtil.isBlank(accessPartyCode)) {
            throw new BusinessException("接入方编码不能为空");
        }

        Map<String, String> map = oemService.getSecretKey(oemCode, accessPartyCode);
        return ResultVo.Success(map);
    }

    @ApiOperation("更新用户openId或alipayUserId")
    @PostMapping("/updateMemberOpenIdOrUserId")
    public ResultVo updateMemberOpenIdOrUserId(@RequestBody AccessPartyLoginDTO loginDto) {
        if (null == loginDto.getSourceType()) {
            throw new BusinessException("操作来源不能为空");
        }
        String oemCode = getRequestHeadParams("oemCode");
        String otherPayOemcode = getRequestHeadParams("otherPayOemcode");
        if(StringUtil.isNotBlank(otherPayOemcode)){
            oemCode= otherPayOemcode;
        }
        MemberAccountEntity t = new MemberAccountEntity();
        t.setId(getCurrUserId());
        t.setUpdateTime(new Date());
        t.setUpdateUser(getCurrUser().getUseraccount());
        try {
            if(loginDto.getSourceType().intValue() == 1){ // 微信
                OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode,2);
                if(null == paramsEntity){
                    throw new BusinessException("获取用户openId失败：未配置微信支付相关信息！");
                }
                // 解析paramValues，配置样例：{"appId": "wxb884fccbb878f5b8","keyNum": "bb9aa8f2499c329b88f37567dd9aab31","signKey": "c4ac143ecafba42f528d1fcbec8c531f"}
                JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
                String appId = params.getString("appId");
                String appSecret = paramsEntity.getSecKey();
                String openId = WechatPayUtils.getWxOpenId(appId,appSecret,loginDto.getJsCode());
                t.setOpenId(openId);
            } else if (loginDto.getSourceType().intValue() == 2){ // 支付宝
                OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode,20);
                if(null == paramsEntity){
                    throw new BusinessException("获取用户支付宝用户ID失败：未配置支付宝支付相关信息！");
                }
                // 解析paramValues
                JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
                String appId = params.getString("appId");
                String privateKey = paramsEntity.getPrivateKey();
                String alipayUserId = AliPayUtils.getAccessTokenAndUserId(appId,privateKey,loginDto.getAuthCode());
                t.setAlipayUserId(alipayUserId);
            } else {
                throw new BusinessException("未知操作来源");
            }
            memberAccountService.editByIdSelective(t);
        } catch (BusinessException e) {
            log.info("更新用户openId或alipayUserId失败：{}", e.getMessage());
            ResultVo.Fail("更新失败");
        }

        return ResultVo.Success("更新成功");
    }

    @ApiOperation("接入方跳转小程序支付页面")
    @PostMapping("/pageOfPayFromAccessParty")
    @Transactional(rollbackFor = Exception.class)
    public ResultVo pageOfPayFromAccessParty(@JsonParam String orderNo, @JsonParam Long payAmount) {
        if (StringUtil.isBlank(orderNo)) {
            throw new BusinessException("订单号不能为空！");
        }
        if (null == payAmount) {
            throw new BusinessException("支付金额不能为空");
        }

        // 查询订单
        OrderEntity order = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单信息"));
        // 校验订单所属用户
        if (!getCurrUserId().equals(order.getUserId())) {
            throw new BusinessException("该订单不属于当前登录用户");
        }
        // 校验订单类型（目前支持充值、注册、升级、续费）
        if (!(OrderTypeEnum.ENCHARGE.getValue().equals(order.getOrderType()) || OrderTypeEnum.REGISTER.getValue().equals(order.getOrderType())
                || OrderTypeEnum.UPGRADE.getValue().equals(order.getOrderType()) || OrderTypeEnum.CUSTODY_FEE_RENEWAL.getValue().equals(order.getOrderType()))) {
            throw new BusinessException("不支持的订单类型");
        }
        // 校验订单状态，除注册订单待支付状态为3，其余订单待支付状态都为0
        if ((OrderTypeEnum.REGISTER.getValue().equals(order.getOrderType()))){
            if (3 != order.getOrderStatus()) {
                throw new BusinessException("订单状态不正确，无法支付！");
            }
        } else if (1 == order.getOrderStatus() && OrderTypeEnum.ENCHARGE.getValue().equals(order.getOrderType())) {
//            throw new BusinessException("订单状态不正确，无法支付！");
        }else if (0 != order.getOrderStatus()){
            throw new BusinessException("订单状态不正确，无法支付！");
        }

        HashMap<String, Object> dateMap = Maps.newHashMap();
        // 服务内容（产品名称）
        String serviceItem = "";
        if (OrderTypeEnum.ENCHARGE.getValue().equals(order.getOrderType())) {
            serviceItem = "充值";
        } else if (OrderTypeEnum.UPGRADE.getValue().equals(order.getOrderType())) {
            serviceItem = "会员升级";
        } else {
            serviceItem = order.getProductName();
        }
        dateMap.put("serviceItem", serviceItem);

        // 订单支付金额
        if (!payAmount.equals(order.getPayAmount())) {
            throw new BusinessException("支付金额不正确，无法支付！");
        }
        dateMap.put("payAmount", order.getPayAmount());

        // 查询是否开启挡板支付或支付金额为0
        DictionaryEntity itaxWechatpaySwitch = dictionaryService.getByCode("itax_wechatpay_switch");
        if (Objects.equals(0L, payAmount) || (null != itaxWechatpaySwitch && "1".equals(itaxWechatpaySwitch.getDictValue()))) {
            RegOrderPayDTO payDto = new RegOrderPayDTO();
            // 工商订单注册订单支付时，订单号必传
            payDto.setOrderNo(order.getOrderNo());
            payDto.setAmount(order.getPayAmount());
            payDto.setOrderType(order.getOrderType());
            payDto.setProductId(order.getProductId());
            payDto.setProductName(order.getProductName());
            payDto.setCurrUserId(getCurrUserId());
            payDto.setPayChannel("1");// 向下兼容，默认微信支付
            payDto.setOemCode(this.getRequestHeadParams("oemCode"));
            try {
                // 模拟支付
                // 模拟出payNo
                JSONObject jsonObject = this.registerOrderService.orderPay(payDto);
                // 挡板支付时需要手工回调修改订单状态
                if (StringUtil.isNotBlank(jsonObject.getString("payNo"))) {
                    this.orderService.updateOrderStatus(jsonObject.getString("payNo"), null, new Date(), MessageEnum.SUCCESS.getValue() , "5", MessageEnum.SUCCESS.getMessage());
                }
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.info("支付失败：{}", e.getMessage());
                throw new BusinessException(e.getMessage());
            }
            return ResultVo.Success(dateMap);
        }

        // 查询用户信息
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(getCurrUserId())).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        //正常支付校验是否存在支付中的订单
        JSONObject jsonObject = registerOrderService.orderCheckBeforePay(order, member);
        if (null != jsonObject) {
            String isPaid = jsonObject.getString("isPaid");//是否已支付标识，0 未支付 1已支付
            if (StringUtil.isNotBlank(isPaid) && "1".equals(isPaid)) {
                return ResultVo.Success(dateMap);
            }
        }

        // 获取接入方跳转小程序地址
        String accessPartyCode = getRequestHeadParams("accessPartyCode");
        OemAccessPartyEntity accessParty = Optional.ofNullable(oemAccessPartyService.queryByAccessPartyCode(accessPartyCode)).orElseThrow(() -> new BusinessException("未查询到接入方信息"));
        if (!OemAccessPartyStatusEnum.YES.getValue().equals(accessParty.getStatus())) {
            throw new BusinessException("接入方不可用！");
        }
        if (StringUtil.isBlank(accessParty.getAppletAddress())) {
            throw new BusinessException("未配置跳转小程序地址");
        }
        dateMap.put("appletAddress", accessParty.getAppletAddress());

        return ResultVo.Success(dateMap);
    }

    /**
     * @Description 用户充值
     * @Author  Kaven
     * @Date   2019/12/16 19:16
     * @Param UserRechargeDTO
     * @Return ResultVo
     * @Exception BusinessException
     */
    @ApiOperation("用户充值")
    @PostMapping("recharge")
    public ResultVo recharge(@RequestBody @Validated UserRechargeDTO dto, BindingResult result) {
        ResultVo resp = ResultVo.Success();
        if (null == dto) {
            return ResultVo.Fail("传入参数对象不能为空");
        }
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        String sourceType = StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType");
        ;// 请求来源:支付宝or微信or其他
        if (com.github.pagehelper.StringUtil.isEmpty(sourceType)) {
            sourceType = "1";// 默认来源微信小程序，向下兼容
        }
        if (!("1".equals(sourceType) || "2".equals(sourceType) || "3".equals(sourceType) || "4".equals(sourceType))) {
            return ResultVo.Fail("未知操作来源，请联系管理员");
        }
        dto.setSourceType(Integer.parseInt(sourceType));
        String oemCode = this.getRequestHeadParams("oemCode");
        dto.setCurrUserId(getCurrUserId());
        dto.setOemCode(oemCode);
        resp = this.orderService.userRecharge(dto);
        return resp;
    }

    @ApiOperation("根据订单号查询订单状态信息")
    @PostMapping("/queryByOrderNo")
    public ResultVo queryByOrderNo(@JsonParam String orderNo){
        if(StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("查询失败，订单号不能为空！");
        }
        Map<String,Object> respData = Maps.newHashMap();
        OrderEntity order = this.orderService.queryByOrderNo(orderNo);
        respData.put("id",order.getId());
        respData.put("orderStatus",order.getOrderStatus());
        respData.put("orderNo",order.getOrderNo());
        respData.put("orderType",order.getOrderType());
        respData.put("isSelfPaying", order.getIsSelfPaying()); //
        // 如果是开户订单，补充返回接单客服电话
        if(OrderTypeEnum.REGISTER.getValue().equals(order.getOrderType())){
            RegisterOrderEntity t = new RegisterOrderEntity();
            t.setOrderNo(orderNo);
            RegisterOrderEntity regOrder = this.registerOrderService.selectOne(t);
            respData.put("customerServicePhone",regOrder.getCustomerServicePhone());
        } else if(OrderTypeEnum.INVOICE.getValue().equals(order.getOrderType())){ // 开票订单
            InvoiceOrderEntity t = new InvoiceOrderEntity();
            t.setOrderNo(orderNo);
            InvoiceOrderEntity invOrder = invoiceOrderService.selectOne(t);
            respData.put("customerServicePhone",invOrder.getCustomerServicePhone());
        }
        return ResultVo.Success(respData);
    }

    /**
     * 根据机构编码获取机构信息
     * @param oemCode
     * @return
     */
    @PostMapping("/getOemByCode")
    public ResultVo getOemByCode(@JsonParam  String oemCode){
        if(StringUtils.isEmpty(oemCode)){
            return ResultVo.Fail("机构编码不能为空");
        }
        OemEntity entity = oemService.getOem(oemCode);
        if(entity == null) {
            return ResultVo.Fail("未找到机构信息");
        }
        Map<String,Object> result = new HashMap<>();
        result.put("customerServiceTel",entity.getCustomerServiceTel()); //获取机构客服电话
        result.put("oemSecret",entity.getOemSecret());
        result.put("oemName",entity.getOemName());
        result.put("isInviterCheck",entity.getIsInviterCheck());
        result.put("isOpenPromotion",entity.getIsOpenPromotion());
        result.put("companyName",entity.getCompanyName());
        result.put("oemStatus",entity.getOemStatus());
        result.put("oemLogo",entity.getOemLogo());
        result.put("versionCode",entity.getVersionCode());
        result.put("isBigCustomer",entity.getIsBigCustomer());
        String sourceType = getRequestHeadParams("sourceType");
        String dictCode = "itax_wechatpay_switch";
        if (Objects.equals(SourceTypeEnum.ALIPAY.getValue(), sourceType)) {
            dictCode = "itax_alipay_switch";
        }
        String weChatPaySwitch = Optional.ofNullable(dictionaryService.getValueByCode(dictCode)).orElse("0");
        result.put("weChatPaySwitch", weChatPaySwitch);
        //测试mq
//        this.rabbitTemplate.convertAndSend("test", oemCode);

        // 获取oem机构配置信息
        OemConfigEntity configEntity = oemConfigService.queryOemConfigByCode(oemCode, "is_open_channel");
        result.put("isOpenChannel",Integer.valueOf(configEntity.getParamsValue()));
        return ResultVo.Success(result);
    }

    @ApiOperation("字号查询")
    @PostMapping("/queryShopName")
    public ResultVo<Map> queryShopName(@JsonParam String orderNo){
        if(StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("订单号不能为空");
        }
        Map<String,Object> dataMap = this.registerOrderService.queryShopName(orderNo, getCurrUserId());
        return ResultVo.Success(dataMap);
    }

    @ApiOperation("根据园区编码获取发票样例")
    @PostMapping("/getInvoiceExample")
    public ResultVo<String> getInvoiceExByParkCode(@JsonParam String parkCode, @JsonParam Long industryId) {
        if(org.apache.commons.lang.StringUtils.isBlank(parkCode)){
            return ResultVo.Fail("园区编码不能为空");
        }
        ParkEntity t = new ParkEntity();
        t.setParkCode(parkCode);
        ParkEntity park = this.parkService.selectOne(t);
        if (park == null) {
            return ResultVo.Fail("园区不存在");
        }
        if (industryId != null) {
            IndustryEntity industryEntity = new IndustryEntity();
            industryEntity.setParkId(park.getId());
            industryEntity.setId(industryId);
            List<IndustryEntity> list = industryService.select(industryEntity);
            if (CollectionUtil.isNotEmpty(list)) {
                String exampleInvoice = list.get(0).getExampleInvoice();
                if (org.apache.commons.lang.StringUtils.isNotBlank(exampleInvoice)) {
                    return ResultVo.Success(ossService.getPrivateVideoUrl(exampleInvoice));
                }
            }
        }
        return ResultVo.Success(park.getInvoiceExample());
    }

    /**
     * 删除oss私有域图片（单个）
     */
    @ApiOperation("删除oss私有域图片（单个）")
    @ApiImplicitParam(name="key",value="路径+文件名(YCS/20191228/123.jpg)",dataType="String",required = true)
    @PostMapping("/deleteObject")
    public ResultVo deleteObject(@JsonParam String key) {
        if(StringUtils.isBlank(key)) {
            return ResultVo.Fail("参数不能为空");
        }
        JSONObject params = new JSONObject();
        params.put("key", key);
        ossService.deleteObject(key);
        return ResultVo.Success();
    }

    @ApiOperation("查询园区特殊事项说明")
    @PostMapping("/specialConsiderations")
    public ResultVo specialConsiderations(@JsonParam Long parkId, @JsonParam Long productId) {
        if (null == parkId) {
            return ResultVo.Fail("园区id不能为空");
        }
        if (null == productId) {
            return ResultVo.Fail("产品id不能为空");
        }
        Map<String, Object> map = parkService.getSpecialConsiderations(parkId, getRequestHeadParams("oemCode"), MemberCompanyTypeEnum.INDIVIDUAL.getValue());
        // 根据当前会员等级计算产品优惠价（会员价）
        String oemCode = this.getRequestHeadParams("oemCode");
        Long discount = this.memberProfitsRulesService.queryMemberDiscount(getCurrUserId(),productId,oemCode,parkId);
        map.put("vipAmount", discount);
        return ResultVo.Success(map);
    }

    @ApiOperation("校验字号")
    @ApiImplicitParam(name="parkId",value="园区id",dataType="Long",required = true)
    @PostMapping("/checkShopName")
    public ResultVo checkShopName(@RequestBody @Validated RegOrderCheckShopNameDTO dto, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }

        // 禁用字号校验
        parkDisableWorkService.checkoutDisableWord(dto.getParkId(), dto.getShopName(), dto.getShopNameOne(), dto.getShopNameTwo());
        return ResultVo.Success();
    }

    /**
     * 查询可选园区/行业（对外接口）
     * @param query
     * @return
     */
    @ApiOperation("查询可选园区/行业")
    @PostMapping("/getUsableParkIndustry")
    public ResultVo getUsableParkIndustry(@RequestBody GetUsableParkIndustryQuery query) {
        query.setTaxpayerType(CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue());
        Map<String, Object> map = parkService.getUsableParkBusinessScope(getRequestHeadParams("oemCode"), getRequestHeadParams("accessPartyCode"), query);
        return ResultVo.Success(map);
    }

    /**
     * 查询行业列表
     * @param parkId
     * @return
     */
    @ApiOperation("查询行业列表")
    @PostMapping("/getIndustryList")
    public ResultVo getIndustryList(@JsonParam Long parkId, @JsonParam Integer companyType){
        if(null == parkId){
            return ResultVo.Fail("园区ID不能为空");
        }
        List<IndustryApiVO> list = industryService.selectIndustry(getRequestHeadParams("oemCode"), parkId, null == companyType ? MemberCompanyTypeEnum.INDIVIDUAL.getValue() : companyType, null);
        return ResultVo.Success(list);
    }

    @ApiOperation("根据行业获取注册信息")
    @PostMapping("/getIndustryBusinessScope")
    public ResultVo getIndustryBusinessScope(@JsonParam Long industryId, @JsonParam Long parkId){
        if(null == parkId){
            return ResultVo.Fail("园区ID不能为空");
        }
        if(null == industryId){
            return ResultVo.Fail("行业ID不能为空");
        }
        IndustryInfoVO industryVo = this.industryService.getById(industryId,parkId);
        return ResultVo.Success(industryVo.getBusinessContent());
    }

    /**
     * 接入方新增修改实名用接口
     * @param dto
     * @param results
     * @return
     * @throws IOException
     */
    @ApiOperation("用户实名/修改实名")
    @PostMapping("/updateAuth")
    public ResultVo updateAuth(@RequestBody @Validated UserAuthDTO dto, BindingResult results) throws IOException {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        if (null == dto.getMemberId()) {
            throw new BusinessException("用户Id不能为空");
        }
        // 查询用户
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(dto.getMemberId())).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        // 查询接入方
        OemAccessPartyEntity accessPartyEntity = Optional.ofNullable(oemAccessPartyService.queryByAccessPartyCode(getRequestHeadParams("accessPartyCode"))).orElseThrow(() -> new BusinessException("未查询到接入方信息"));
        if (!Objects.equals(member.getAccessPartyId(), accessPartyEntity.getId())) {
            throw new BusinessException("用户不属于当前接入方");
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        dto.setIsOther(0);// 用户本人实名认证

        //上传图片到oss
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String  dir = oemCode + "/" + sdf.format(new Date());
        String bucket = sysDictionaryService.getByCode("oss_privateBucketName").getDictValue();
        String page = sysDictionaryService.getByCode("oss_page").getDictValue();
        if(org.apache.commons.lang.StringUtils.isNotBlank(page) && StringUtils.startsWith(page, "/")){ //oss包名不能以/开头
            page = page.substring(1);
        }
        dto.setIdCardFront(uploadImg(dto.getIdCardFront(), page, dir, bucket));
        dto.setIdCardBack(uploadImg(dto.getIdCardBack(), page, dir, bucket));
        memberAccountService.userAuth(dto.getMemberId(),oemCode,dto,0);
        return ResultVo.Success();
    }
    private String uploadImg(String img, String page, String dir, String bucket) {
        //base64图片 需要去除前缀
        String idCardFrontImgBase64 = img.substring(img.indexOf( "," ) + 1 );
        String fileName = UUID.randomUUID().toString();
        boolean flag = ossService.uploadBase64(fileName+".png",page+dir+"/",bucket,idCardFrontImgBase64);
        String name = (dir+"/"+fileName+".png");
        if(!flag){
            throw new BusinessException("身份证照片上传失败，请重试");
        }
        return name;
    }

    @ApiOperation("添加支付凭证")
    @PostMapping("/addPaymentVoucher")
    public ResultVo addPaymentVoucher(@RequestBody @Validated ThirdPartyAddPaymentVoucherDTO dto, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        dto.setOemCode(getRequestHeadParams("oemCode"));
        dto.setAccessPartyCode(getRequestHeadParams("accessPartyCode"));
        try {
            registerOrderService.addPaymentVoucher(dto);
        } catch (BusinessException e) {
            log.info("注册订单添加支付凭证失败：" + e.getMessage());
            if ("0001".equals(e.getErrorCode())) {
                return ResultVo.Fail(e.getMessage());
            } else {
                return ResultVo.Fail(e.getErrorCode(), e.getMessage());
            }
        } catch (Exception e) {
            log.info("注册订单添加支付凭证失败：" + e.getMessage());
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    @ApiOperation("注册流程信息查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "parkId", value = "园区id", required = true),
            @ApiImplicitParam(name = "orderNo", value = "订单编号", required = true)})
    @PostMapping("/getProcessMsg")
    public ResultVo getProcessMsg(@JsonParam Long parkId, @JsonParam String orderNo) {
        ParkProcessMarkVO vo = parkService.getParkProcessMark(parkId);
        String oemCode = getRequestHeadParams("oemCode");
        // 查询机构
        OemEntity oem = Optional.ofNullable(oemService.getOem(oemCode)).orElseThrow(() -> new BusinessException("未查询到机构信息"));
        // 查询订单
        OrderEntity orderEntity = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单信息"));
        vo.setPaymentAmount(orderEntity.getPayAmount());
        // 查询产品id
        ProductEntity productEntity = productService.queryProductByProdType(ProductTypeEnum.INDIVIDUAL.getValue(), oemCode, null);
        if (null == productEntity) {
            throw new BusinessException("未查询到产品信息");
        }
        vo.setProductId(productEntity.getId());
        vo.setMobile(oem.getCustomerServiceTel());
        return ResultVo.Success(vo);
    }

    @ApiOperation("查询园区经营范围")
    @PostMapping("/getBusinessScopeByParkId")
    public ResultVo getBusinessScopeByParkId(@RequestBody @Validated ParkBusinessScopeQuery query, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }

        // 校验机构所属园区
        Example example = new Example(OemParkRelaEntity.class);
        example.createCriteria().andEqualTo("oemCode", getRequestHeadParams("oemCode")).andEqualTo("parkId", query);
        Optional.ofNullable(oemParkService.selectByExample(example)).orElseThrow(() -> new BusinessException("所属机构下未找到要查询的园区"));

        // 查询园区
        ParkEntity park = Optional.ofNullable(parkService.findById(query.getParkId())).orElseThrow(() -> new BusinessException("未查询到园区信息"));
        if (!ParkStatusEnum.ON_SHELF.getValue().equals(park.getStatus())) {
            throw new BusinessException("园区不可用");
        }

        PageResultVo<String> page = parkBusinessScopeService.findByParkId(query);
        return ResultVo.Success(page);
    }

    @ApiOperation("同步商品编码")
    @PostMapping("/synchronousTaxCode")
    public ResultVo synchronousTaxCode(@RequestBody @Validated SynchronousTaxCodeDTO dto, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        registerOrderService.synchronousTaxCode(dto, getRequestHeadParams("accessPartyCode"));
        return ResultVo.Success();
    }
}
