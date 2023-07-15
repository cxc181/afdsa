package com.yuqian.itax.api.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.agreement.entity.query.ParkAgreementsQuery;
import com.yuqian.itax.agreement.entity.vo.ParkAgreementsVO;
import com.yuqian.itax.agreement.service.AgreementTemplateService;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.enums.ParkStatusEnum;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.dto.ProductDiscountActivityAPIDTO;
import com.yuqian.itax.product.entity.vo.ProductDetailVO;
import com.yuqian.itax.product.entity.vo.ProductDiscountActivityVO;
import com.yuqian.itax.product.service.ProductDiscountActivityService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.IndustryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.IndustryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.enums.MemberCompanyTypeEnum;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.user.service.MemberProfitsRulesService;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
 *  @Date: 2019/12/10 10:49
 *  @Description: 产品管理控制器
 */
@Api(tags = "产品管理控制器")
@Slf4j
@RestController
@RequestMapping("/agent/product")
public class ProductController extends BaseController {
    @Autowired
    private ProductService productService;
    @Autowired
    private MemberProfitsRulesService memberProfitsRulesService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private AgreementTemplateService agreementTemplateService;

    @Autowired
    private IndustryService industryService;

    @Autowired
    private OssService ossService;

    @Autowired
    private ProductDiscountActivityService productDiscountActivityService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private OemService oemService;

    /**
     * @Description 查询oem开通的开户类型的产品列表
     * @Author  Kaven
     * @Date  2019/12/10 10:51
     * @Param
     * @Return ResultVo
    */
    @ApiOperation("查询oem开通的开户类型的产品列表")
    @PostMapping("listProduct")
    public ResultVo listProduct(){
        List<ProductEntity> productList = this.productService.queryProductList(getRequestHeadParams("oemCode"));
        return ResultVo.Success(productList);
    }

    /**
     * @Description 产品详情，获取oem下全部的园区列表
     * @Author  Kaven
     * @Date   2019/12/10 11:09
     * @Param productId
     * @Return ResultVo
    */
    @ApiOperation("产品详情，获取oem下全部的园区列表")
    @ApiImplicitParam(name="prodcutId",value="产品id",dataType="Long",required = true)
    @PostMapping("procudtDetail")
    public ResultVo<ProductDetailVO> getProcudtDetail(@JsonParam Long productId){
        if(null == productId){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        ProductDetailVO productDetail = this.productService.queryProductDetail(productId);
        // 根据当前会员等级计算产品优惠价（会员价）
        String oemCode = this.getRequestHeadParams("oemCode");
        Long discount = this.memberProfitsRulesService.queryMemberDiscount(getCurrUserId(),productId,oemCode,null);
        productDetail.setVipAmount(discount);
        return ResultVo.Success(productDetail);
    }

    /**
     * @Description 根据园区编码获取发票样例
     * @Author  Kaven
     * @Date   2020/7/14 16:52
     * @Param   parkCode
     * @Return  ResultVo<String>
     * @Exception  
    */
    @ApiOperation("根据园区编码获取发票样例")
    @PostMapping("getInvoiceExample")
    public ResultVo<String> getInvoiceExByParkCode(@JsonParam String parkCode, @JsonParam Long industryId) {
        if(StringUtils.isBlank(parkCode)){
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
                if (StringUtils.isNotBlank(exampleInvoice)) {
                    return ResultVo.Success(ossService.getPrivateVideoUrl(exampleInvoice));
                }
            }
        }
        return ResultVo.Success(park.getInvoiceExample());
    }

    /**
     * @Description 根据园区编码获取协议列表
     * @Author  Kaven
     * @Date   2020/7/14 17:07
     * @Param  parkCode
     * @Return  List<ParkAgreementEntity>
     * @Exception
    */
    @ApiOperation("根据园区编码获取协议列表")
    @PostMapping("getParkAgreements")
    public ResultVo getParkAgreements(@RequestBody @Validated ParkAgreementsQuery query, BindingResult result){
        if (result.hasErrors()) {
            return ResultVo.Fail(result.getAllErrors().get(0).getDefaultMessage());
        }

        // 查询园区
        ParkEntity park = parkService.findById(query.getParkId());
        if (null == park || !ParkStatusEnum.ON_SHELF.getValue().equals(park.getStatus())) {
            return ResultVo.Fail("园区不存在或不可用");
        }

        // 查询协议模板
        query.setOemCode(getRequestHeadParams("oemCode"));
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
                    + "?oemCode=" + query.getOemCode()
                    + "&parkId=" + query.getParkId()
                    + "&memberId=" + getCurrUserId()
                    + "&type=" + agreementTemplateType
                    + "&companyType=" + query.getCompanyType();
            vo.setTemplateHtmlUrl(url);
        }

        // 合伙企业需要增加企业章程
        if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(query.getCompanyType())
                || MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(query.getCompanyType())) {
            String code = "articles_of_incorporation_yyhh";
            if (MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(query.getCompanyType())) {
                code = "articles_of_incorporation_yyzr";
            }
            ParkAgreementsVO parkAgreementsVO = new ParkAgreementsVO();
            DictionaryEntity incorporationYyhh = dictionaryService.getByCode(code);
            if (null == incorporationYyhh) {
                return ResultVo.Fail("未查询到公司章程配置");
            }
            parkAgreementsVO.setTemplateHtmlUrl(incorporationYyhh.getDictValue());
            parkAgreementsVO.setTemplateName(incorporationYyhh.getDictDesc());
            parkAgreementsVO.setTemplateType(4);
            // 获取机构客服电话
            OemEntity oem = oemService.getOem(getRequestHeadParams("oemCode"));
            if (null != oem) {
                parkAgreementsVO.setCustomerServiceTel(oem.getCustomerServiceTel());
            }
            vos.add(parkAgreementsVO);
        }

        return ResultVo.Success(vos);
    }

    /**
     * @Description 获取产品特价活动信息
     *
     * @Exception
     */
    @ApiOperation("获取产品特价活动信息")
    @PostMapping("getProductDiscountActivityByProductType")
    public ResultVo getProductDiscountActivityByProductType(@RequestBody ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO){
        productDiscountActivityAPIDTO.setMemberId(getCurrUserId());
        String oemCode = getRequestHeadParams("oemCode");
        if(StringUtils.isBlank(oemCode)){
            return ResultVo.Fail("oemCode不能为空");
        }
        productDiscountActivityAPIDTO.setOemCode(oemCode);
        if(productDiscountActivityAPIDTO.getProductType() == null){
            return ResultVo.Fail("产品类型不能为空");
        }
        if(productDiscountActivityAPIDTO.getProductType() != null
                && productDiscountActivityAPIDTO.getProductType() < 5){
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
}