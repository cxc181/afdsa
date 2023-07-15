package com.yuqian.itax.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agreement.entity.query.ParkAgreementsQuery;
import com.yuqian.itax.agreement.entity.vo.ParkAgreementsVO;
import com.yuqian.itax.agreement.enums.TemplateTypeEnums;
import com.yuqian.itax.agreement.service.AgreementTemplateService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.dto.ProductDiscountActivityAPIDTO;
import com.yuqian.itax.product.entity.vo.ProductDiscountActivityVO;
import com.yuqian.itax.product.enums.ProductStatusEnum;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ProductDiscountActivityService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.InvoiceCategoryBaseEntity;
import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseStringVO;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.InvoiceCategoryBaseService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.dao.CompanyInvoiceRecordMapper;
import com.yuqian.itax.user.dao.CompanyResoucesApplyRecordMapper;
import com.yuqian.itax.user.dao.CompanyTaxHostingMapper;
import com.yuqian.itax.user.dao.MemberCompanyMapper;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.po.MemberCompanyPO;
import com.yuqian.itax.user.entity.query.AccessPartyCompanyQuery;
import com.yuqian.itax.user.entity.query.ExtendUserQuery;
import com.yuqian.itax.user.entity.query.IndividualQuery;
import com.yuqian.itax.user.entity.query.MemberCompanyQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.enums.MemberCompanyOverdueStatusEnum;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.enums.MemberCompanyTypeEnum;
import com.yuqian.itax.user.enums.MemberLevelEnum;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.util.MoneyUtil;
import com.yuqian.itax.util.util.StringHandleUtil;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 我的企业service impl
 *
 * @Date: 2019年12月06日 10:42:12
 * @author yejian
 */
@Service("memberCompanyService")
@Slf4j
public class MemberCompanyServiceImpl extends BaseServiceImpl<MemberCompanyEntity, MemberCompanyMapper> implements MemberCompanyService {

    @Resource
    private CompanyInvoiceRecordMapper companyInvoiceRecordMapper;
    @Resource
    private CompanyResoucesApplyRecordMapper companyResoucesApplyRecordMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private CompanyInvoiceCategoryService companyInvoiceCategoryService;
    @Resource
    private CompanyTaxHostingMapper companyTaxHostingMapper;
    @Resource
    private InvoiceCategoryBaseService invoiceCategoryBaseService;
    @Resource
    private MemberCompanyMapper memberCompanyMapper;
    @Resource
    private MemberProfitsRulesService memberProfitsRulesService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    OssService ossService;
    @Autowired
    MemberCompanyChangeService memberCompanyChangeService;
    @Autowired
    private ProductDiscountActivityService productDiscountActivityService;
    @Autowired
    private AgreementTemplateService agreementTemplateService;

    private static final Integer DEFAULT_SURPLUS_DAYS = 30;// 到期时间剩余天数

    /**
     * 已过期的企业状态修改,同时改变是否发送过期提醒通知的值为0-未发送
     * @return
     */
    @Override
    public int updateOverdueCompanyStatus(){
       return mapper.updateOverdueCompanyStatus();
    }

    @Override
    public MemberCompanyDetailVo getMemberCompanyDetail(Long memberId, String oemCode, Long id) throws BusinessException{
        MemberCompanyDetailVo companyDetail = mapper.getMemberCompanyDetail(memberId, oemCode, id);
        if(null == companyDetail){
            throw new BusinessException("企业不存在");
        }
        // 查询企业证件列表
        List<MemberCompanyCertListVo> memberCompanyCertList = mapper.getMemberCompanyCertList(memberId, oemCode, id);
        companyDetail.setCertList(memberCompanyCertList);

        // 查询企业累计已完成开票订单额度
        Long useInvoiceAmount = companyInvoiceRecordMapper.sumUseInvoiceAmount(id);
        companyDetail.setUseInvoiceAmount(useInvoiceAmount);

        MemberCompanyEntity company = findById(companyDetail.getId());

        // 查询产品表获取注销累计开票额度和注销服务费（当前版本不支持非个体注销）
        if (MemberCompanyTypeEnum.INDIVIDUAL.getValue().equals(companyDetail.getCompanyType())) {
            ProductEntity product = new ProductEntity();
            product.setOemCode(oemCode);
            product.setProdType(companyTypeTransferOfCancel(companyDetail.getCompanyType()));
            product = productService.selectOne(product);
            if(null == product){
                throw new BusinessException("产品不存在或未上架");
            }
            companyDetail.setProdName(product.getProdName());
            companyDetail.setCancelTotalLimit(product.getCancelTotalLimit());
            // 如果大于等于注销累计开票额度,则支付金额为0
            if(useInvoiceAmount >= product.getCancelTotalLimit()){
                companyDetail.setCancelFee(0L);
            }else{
                companyDetail.setCancelFee(product.getProdAmount());
            }
            /**
             *  判断是否存在特价活动，
             *  如果存在，订单金额 = 特价活动金额，支付金额取 = 特价活动*折扣， 优惠金额 = 订单金额 - 支付金额取
             */

            ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO = new ProductDiscountActivityAPIDTO();
            productDiscountActivityAPIDTO.setOemCode(oemCode);
            productDiscountActivityAPIDTO.setMemberId(memberId);
            productDiscountActivityAPIDTO.setIndustryId(company.getIndustryId());
            productDiscountActivityAPIDTO.setParkId(company.getParkId());
            productDiscountActivityAPIDTO.setProductType(product.getProdType());
            ProductDiscountActivityVO productDiscountActivityVO = productDiscountActivityService.getProductDiscountActivityByProductType(productDiscountActivityAPIDTO);
            if(productDiscountActivityVO!=null) {
                // 1.累积开票额度大于阈值可免注销服务费 2.若产品金额为0，也视为免注销服务费
                if (useInvoiceAmount >= productDiscountActivityVO.getCancelTotalLimit() || productDiscountActivityVO.getSpecialPriceAmount() == 0) {
                    companyDetail.setCancelFee(0L);
                } else {
                    companyDetail.setCancelFee(productDiscountActivityVO.getSpecialPriceAmount());
                }
                companyDetail.setCancelTotalLimit(productDiscountActivityVO.getCancelTotalLimit());
                companyDetail.setDiscountActivityId(productDiscountActivityVO.getDiscountActivityId());
            }
        }

        //查询企业托管状态
        Integer manageStatus = Optional.ofNullable(companyTaxHostingMapper.queryByCompanyId(id)).map(CompanyTaxHostingEntity::getStatus).orElse(0);
        companyDetail.setManageStatus(manageStatus);
        // 如果存在注销凭证获取图片路径
        if(StringUtil.isNotBlank(companyDetail.getCancelCredentials())){
            String urls = "";
            String[] img = companyDetail.getCancelCredentials().split(",");
            for(String url : img){
                urls += ossService.getPrivateImgUrl(url);
                // 多张图片逗号分割
                urls += ",";
            }
            companyDetail.setCancelCredentials(urls);
        }
        // 查询企业委托注册协议
        ParkAgreementsQuery query = new ParkAgreementsQuery();
        query.setParkId(company.getParkId());
        query.setOemCode(oemCode);
        query.setTemplateType(TemplateTypeEnums.REGISTRATION_AGREEMENT.getValue());
        query.setTemplateStatus(1);

        // 查询企业委托注册协议图片
        List<String> userAgreementImgsList = new ArrayList<>();
        if(StringUtil.isNotBlank(companyDetail.getUserAgreementImgs())){
            String[] img = companyDetail.getUserAgreementImgs().split(",");
            for(String url : img){
                String urlShow = ossService.getPrivateImgUrl(url);
                userAgreementImgsList.add(urlShow);
            }
        }
        companyDetail.setUserAgreementImgsList(userAgreementImgsList);

        // 查询产品
        ProductEntity productEntity = productService.queryProductByProdType(ProductTypeEnum.INDIVIDUAL.getValue(), oemCode, company.getParkId());
        if (null == productEntity) {
            throw new BusinessException("未查询到注册产品信息");
        }
        query.setProductId(productEntity.getId());
        List<ParkAgreementsVO> vos = agreementTemplateService.queryParkAgreements(query);
        if (CollectionUtil.isEmpty(vos)) {
            return companyDetail;
        }
        ParkAgreementsVO parkAgreementsVO = vos.get(0);
        companyDetail.setTemplateName(parkAgreementsVO.getTemplateName());
        // 获取oss请求参数
        String reqHead = dictionaryService.getValueByCode("oss_req_head");
        String host = dictionaryService.getValueByCode("oss_access_public_host");
        String agreementTemplateType = dictionaryService.getValueByCode("agreement_template_type");
        String registrationAgreement = reqHead + host + parkAgreementsVO.getTemplateHtmlUrl()
                + "?oemCode=" + oemCode
                + "&parkId=" + company.getParkId()
                + "&memberId=" + memberId
                + "&companyId=" + company.getId()
                + "&orderNo=" + company.getOrderNo()
                + "&type=" + agreementTemplateType
                + "&t=" + System.currentTimeMillis();
        companyDetail.setRegistrationAgreement(registrationAgreement);
        return companyDetail;
    }

    @Override
    public MemberCompanyDetailH5VO getCompanyInfoH5ById(Long userId, Long id) throws BusinessException {
        MemberCompanyDetailH5VO memberCompanyDetailH5VO=new MemberCompanyDetailH5VO();
        memberCompanyDetailH5VO=mapper.getCompanyInfoH5ById(id);
        if(!Objects.equals(memberCompanyDetailH5VO.getMemberId(),userId)){
            throw  new BusinessException("该用户不能查询其他用户得企业");
        }
        memberCompanyDetailH5VO.setSign(ossService.getPrivateImgUrl(memberCompanyDetailH5VO.getSign()));
        return  memberCompanyDetailH5VO;
    }



    @Override
    public PageInfo<MemberCompanyVo> listPageMemberCompany(MemberCompanyQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(mapper.listMemberCompanyInfo(query));
    }

    @Override
    public List<MemberCompanyVo> listMemberCompanyInfo(MemberCompanyQuery query) {
        return mapper.listMemberCompanyInfo(query);
    }

    @Override
    public void updateStatus(Long id, int status, String updateUser) {
        mapper.updateStatus(id, status, updateUser, new Date());
    }

    @Override
    public MemberCompanyDetailAdminVO queryDetailById(Long id) {
        return mapper.queryDetailById(id);
    }

    @Override
    public Integer checkStatus(long memberId, Long id, String oemCode) throws BusinessException{
        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(id);
        company.setOemCode(oemCode);
        company.setMemberId(memberId);
        company = mapper.selectOne(company);
        if(null == company){
            throw new BusinessException("未查询到企业");
        }

        // 校验公司状态(状态：1->正常；2->禁用；3->过期 4->已注销 5->注销中)
        if(Objects.equals(company.getStatus(), MemberCompanyStatusEnum.PROHIBIT.getValue())) {
            throw new BusinessException("该企业已冻结，无法操作！如有疑问，请联系客服！");
        }else if(Objects.equals(company.getOverdueStatus(), MemberCompanyOverdueStatusEnum.OVERDUE.getValue())) {
            throw new BusinessException("该企业注册年费已过期，请您续费后再试！");
        }else if(Objects.equals(company.getStatus(), MemberCompanyStatusEnum.TAX_CANCELLED.getValue())
                || Objects.equals(company.getStatus(), MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue())) {
            throw new BusinessException("该企业已注销，无法开票！");
        }else if(Objects.equals(company.getStatus(), MemberCompanyStatusEnum.CANCELLING.getValue())) {
            throw new BusinessException("该企业正在注销中，无法开票！");
        }

        return company.getStatus();
    }

    @Override
    public List<MemberCompanyEntity> allMemberCompanyList(Long memberId, String oemCode) {
        return mapper.allMemberCompanyList(memberId, oemCode);
    }

    @Override
    public List<MemberCompanyVo> getMemberCompanyByIdCard(String idCardNumber, String oemCode, Integer status, String invoiceCompanyName, Long remainInvoiceAmount, Long categoryBaseId, BigDecimal vatFeeRate,String orderBy) {
        return mapper.getMemberCompanyByIdCard(idCardNumber, oemCode, status, invoiceCompanyName, remainInvoiceAmount, categoryBaseId, vatFeeRate,orderBy);
    }

    @Override
    public Integer countMemberCompanyByIdCard(String idCardNumber, String oemCode, Integer status, String invoiceCompanyName, Long remainInvoiceAmount, BigDecimal vatFeeRate) {
        return mapper.countMemberCompanyByIdCard(idCardNumber, oemCode, status, invoiceCompanyName, remainInvoiceAmount, vatFeeRate);
    }

    @Override
    public MemberCompanyCertVo getMemberCompanyCertList(Long memberId, String oemCode, Long id) {
        MemberCompanyCertVo memberCompanyCertVo = new MemberCompanyCertVo();
        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(id);
        company.setOemCode(oemCode);
        company.setMemberId(memberId);
        company = mapper.selectOne(company);
        if(null == company){
            throw new BusinessException("未查询到企业");
        }
        memberCompanyCertVo.setId(company.getId());
        memberCompanyCertVo.setCompanyName(company.getCompanyName());

        // 查询企业证件列表
        List<MemberCompanyCertListVo> memberCompanyCertList = mapper.getMemberCompanyCertList(memberId, oemCode, id);
        int orderCount = 0;
        Integer applyType;// 申请类型 1-领用 2-归还
        for(MemberCompanyCertListVo cert : memberCompanyCertList){

            // 参数转义
            if(cert.getIsInPark() == 0){//是否在园区 0-不在园区 1-在园区
                applyType = 2;
            }else{
                applyType = 1;
            }

            // 查询证件是否在申请中
            orderCount = companyResoucesApplyRecordMapper.checkCertOrder(memberId, oemCode, id, applyType, cert.getResourcesType().toString());
            if(orderCount > 0){
                cert.setIsInApply(1);
            }else{
                cert.setIsInApply(0);
            }
        }
        memberCompanyCertVo.setCertList(memberCompanyCertList);
        return memberCompanyCertVo;
    }

    @Override
    public List<MemberCompanyCertInParkVo> getMemberCompanyCertInParkList(Long memberId, String oemCode, Long companyId, Integer isInPark) {
        List<MemberCompanyCertInParkVo> certInParkList = mapper.getMemberCompanyCertInParkList(memberId, oemCode, companyId, isInPark);
        if(certInParkList == null){
            return new ArrayList<MemberCompanyCertInParkVo>();
        }
        // 参数转义
        Integer applyType;// 申请类型 1-领用 2-归还
        if(isInPark == 0){//是否在园区 0-不在园区 1-在园区
            applyType = 2;
        }else{
            applyType = 1;
        }
        List<MemberCompanyCertInParkVo> newCertInParkList = new ArrayList<>();
        newCertInParkList.addAll(certInParkList);
        int orderCount = 0;
        for(MemberCompanyCertInParkVo cert : certInParkList){
            // 查询证件是否在申请中
            orderCount = companyResoucesApplyRecordMapper.checkCertOrder(memberId, oemCode, companyId, applyType, cert.getResourcesType().toString());
            if(orderCount > 0){
                newCertInParkList.remove(cert);
            }
        }
        return newCertInParkList;
    }

    /**
     * @Description 企业类型转换为产品类型（注销）
     * @Author  yejian
     * @Date   2019/12/20 16:34
     * @Param  companyType
     * @Return Integer
     */
    public Integer companyTypeTransferOfCancel(Integer companyType) {
        if(companyType == 1) {
            return ProductTypeEnum.INDIVIDUAL_CANCEL.getValue();
        }else if(companyType == 2) {
            return ProductTypeEnum.INDEPENDENTLY_CANCEL.getValue();
        }else if(companyType == 3) {
            return ProductTypeEnum.LIMITED_PARTNER_CANCEL.getValue();
        }else if(companyType == 4) {
            return ProductTypeEnum.LIMITED_LIABILITY_CANCEL.getValue();
        }else{
            return null;
        }
    }

    /**
     * @Description 企业类型转换为产品类型（续费）
     * @Author  yejian
     * @Date   2019/12/20 16:34
     * @Param  companyType
     * @Return Integer
     */
    public Integer companyTypeTransferOfRenew(Integer companyType) {
        if(companyType == 1) {
            return ProductTypeEnum.INDIVIDUAL_RENEWALS.getValue();
        }else if(companyType == 2) {
            return ProductTypeEnum.INDEPENDENTLY_RENEWALS.getValue();
        }else if(companyType == 3) {
            return ProductTypeEnum.LIMITED_PARTNER_RENEWALS.getValue();
        }else if(companyType == 4) {
            return ProductTypeEnum.LIMITED_LIABILITY_RENEWALS.getValue();
        }else{
            return null;
        }
    }

    @Override
    public Integer countMemberCompany(Long memberId, String oemCode, Integer status) {
        return mapper.countMemberCompany(memberId, oemCode, status);
    }

    @Override
    public List<MemberComVO> queryMemberCompanyByMemberId(ExtendUserQuery query) {
        return this.mapper.queryMemberCompanyByMemberId(query);
    }

    @Override
    public void checkCompanyName(String oemCode, String prefix, String suffix, String shopName, String shopNameOne, String shopNameTwo) {
        if (StringUtils.isBlank(shopName) && StringUtils.isBlank(shopNameOne) && StringUtils.isBlank(shopNameTwo)) {
            return;
        }
        //后缀去*
        if (StringUtils.isNotBlank(suffix)) {
            suffix = StringHandleUtil.removeStar(suffix);
        }
        if (StringUtils.isNotBlank(shopName)) {
            shopName = prefix + shopName + suffix;
        }
        if (StringUtils.isNotBlank(shopNameOne)) {
            shopNameOne = prefix + shopNameOne + suffix;
        }
        if (StringUtils.isNotBlank(shopNameTwo)) {
            shopNameTwo = prefix + shopNameTwo + suffix;
        }
        String result = mapper.checkCompanyNameByShopName(oemCode, shopName, shopNameOne, shopNameTwo);
        if (StringUtils.isNotBlank(result)) {
            throw new BusinessException(result);
        }
    }

    @Override
    public void checkCompanyName(String oemCode, String companyName) {
        if (mapper.checkCompanyName(oemCode, companyName) > 0) {
            throw new BusinessException("已存在同名企业，请重新选择个体名称，或者修改字号");
        }
    }
    @Override
    public void checkCompanyName( String companyName) {
        if (mapper.checkCompanyNameNotOemCode( companyName) > 0) {
            throw new BusinessException("已存在同名企业，请重新选择个体名称，或者修改字号");
        }
    }
    @Override
    public List<MemberCompanyEntity> listCorpAccCompany(Long memberId, String oemCode) {
        return mapper.listCorpAccCompany(memberId, oemCode);
    }

    @Override
    public List<IndividualVO> individualCount(IndividualQuery query) {
        List<IndividualVO> list = mapper.individualCount(query);
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        if (StringUtils.isNotBlank(query.getOemCode())) {
            return list;
        }
        IndividualVO vo = new IndividualVO();
        vo.setOemName("汇总");
        for (IndividualVO v : list) {
            vo.setAddNum(MoneyUtil.numAdd(vo.getAddNum(), v.getAddNum()));
            vo.setAuditNum(MoneyUtil.numAdd(vo.getAuditNum(), v.getAuditNum()));
            vo.setCertifyNum(MoneyUtil.numAdd(vo.getCertifyNum(), v.getCertifyNum()));
            vo.setOffNum(MoneyUtil.numAdd(vo.getOffNum(), v.getOffNum()));
            vo.setTotalNum(MoneyUtil.numAdd(vo.getTotalNum(), v.getTotalNum()));
        }
        list.add(vo);
        return list;
    }

    @Override
    public List<MemberCompanyEntity> queryMemberCompanyListByEin() {
        return mapper.queryMemberCompanyListByEin();
    }

    @Override
    public void update(MemberCompanyPO po,String account) throws ParseException {
        MemberCompanyEntity memberCompanyEntity=mapper.selectByPrimaryKey(po.getId());
        if(memberCompanyEntity==null){
            throw new BusinessException("企业不存在");
        }
/*        MemberCompanyEntity memberCompany=mapper.queryMemberCompanyByEinAndNotId(po.getEin(),po.getId());
        if(memberCompany!=null){
            throw new BusinessException("税号已存在");
        }*/
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        Date taxRegDate = null;
        if(StringUtils.isNotBlank(po.getTaxRegDate())){
            try {
                taxRegDate = sdf.parse(po.getTaxRegDate());
            }catch (Exception e){
                throw new BusinessException("税务登记日期格式错误，正确格式为：yyyy-MM-dd ");
            }
        }
        if(taxRegDate!=null && sdf.parse(sdf.format(memberCompanyEntity.getAddTime())).compareTo(taxRegDate)>0){
            throw new BusinessException("税务登记日期不能早于出证日期");
        }
        memberCompanyEntity.setCompanyName(po.getCompanyName());
        memberCompanyEntity.setOperatorTel(po.getOperatorTel());
        memberCompanyEntity.setOperatorEmail(po.getOperatorEmail());
        if(StringUtils.isNotBlank(po.getEin())) {
            memberCompanyEntity.setEin(po.getEin());
        }
        memberCompanyEntity.setBusinessScope(po.getBusinessScope());
        memberCompanyEntity.setBusinessAddress(po.getBusinessAddress());
        if(taxRegDate!=null){
            memberCompanyEntity.setTaxRegDate(taxRegDate);
        }
        if(po.getApprovedTurnover()!=null&&
                po.getApprovedTurnover()>0 &&
                po.getApprovedTurnover()<= 500000000){
            memberCompanyEntity.setApprovedTurnover(po.getApprovedTurnover());
        }else{
            memberCompanyEntity.setApprovedTurnover(null);
        }
        memberCompanyEntity.setUpdateUser(account);
        memberCompanyEntity.setUpdateTime(new Date());
        memberCompanyEntity.setBusinessLicense(po.getBusinessLicense());
        memberCompanyEntity.setBusinessLicenseCopy(po.getBusinessLicenseCopy());
        if(StringUtil.isNotBlank(po.getUserAgreementImgs())){
            // 委托注册协议图片,多个文件用逗号分隔 ,
            memberCompanyEntity.setUserAgreementImgs(po.getUserAgreementImgs());
        }
        mapper.updateByPrimaryKey(memberCompanyEntity);
        //修改开票类目
        if (po.getCategories().size()<1) {
            throw new BusinessException("开票类目不能为空");
        }
        //删除原来得开票类目
        companyInvoiceCategoryService.deleteByCompanyId(memberCompanyEntity.getId());

        //新增开票类目
        CompanyInvoiceCategoryEntity entity;
        for (InvoiceCategoryBaseStringVO s : po.getCategories()) {
            entity = new CompanyInvoiceCategoryEntity();
            entity.setOrderNo(memberCompanyEntity.getOrderNo());
            entity.setOemCode(memberCompanyEntity.getOemCode());
            entity.setCompanyId(memberCompanyEntity.getId());
            InvoiceCategoryBaseEntity invoiceCategoryBaseEntity=new InvoiceCategoryBaseEntity();
            invoiceCategoryBaseEntity.setTaxClassificationAbbreviation(s.getTaxClassificationAbbreviation());
            invoiceCategoryBaseEntity.setGoodsName(s.getGoodsName());
            List<InvoiceCategoryBaseEntity> invoiceCategoryBaseEntities=invoiceCategoryBaseService.select(invoiceCategoryBaseEntity);
            if(CollectionUtil.isEmpty(invoiceCategoryBaseEntities)){
                continue;
            }
            entity.setCategoryBaseId(invoiceCategoryBaseEntities.get(0).getId());
            entity.setIndustryId(memberCompanyEntity.getIndustryId());
            entity.setCategoryName(s.getTaxClassificationAbbreviation()+"*"+s.getGoodsName());
            entity.setAddTime(new Date());
            entity.setAddUser(account);
            companyInvoiceCategoryService.insertSelective(entity);
        }
    }

    @Override
    public List<MemberCompanyBasicVo> queryCompanyBasicInfoByEinOrId(Long id,String ein,Integer status,Integer overdueStatus) {
        return mapper.queryCompanyBasicInfoByEinOrId(id,ein,status,overdueStatus);
    }

    @Override
    public List<MemberCompanyEntity> queryMemberCompanyByEinStatusNotCancellation(String ein) {
        return mapper.queryMemberCompanyByEinStatusNotCancellation(ein);
    }

    @Override
    public List<Long> queryMemberCompanyIdByEin(String ein) {
        return mapper.queryMemberCompanyIdByEin(ein);
    }

    /**
     * 查询托管费续费详情
     * @param companyId
     * @return
     */
    @Override
    public TrusteeFeeRenewDetailVO getTrusteeFeeRenewDetail(Long memberId , Long companyId) {
        MemberCompanyEntity company = this.memberCompanyMapper.selectByPrimaryKey(companyId);

        if (null == company) {
            throw new BusinessException("操作失败，企业信息不存在");
        }

        MemberAccountEntity member = this.memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("操作失败，用户信息不存在");
        }

        // 判断当前续费企业是否属于当前用户
        if (!memberId.equals(company.getMemberId())) {
            throw new BusinessException("操作失败，待续费企业不属于当前用户");
        }
        //获取机构编码
        String oemCode = member.getOemCode();

        // 获取企业对应注册产品类型
        Integer productType = this.companyTypeTransferOfRenew(company.getCompanyType());
        if (null == productType) {
            throw new BusinessException("未知产品类型");
        }
        //获取产品信息
        ProductEntity product = productService.queryProductByProdType(productType, oemCode, company.getParkId());
        if (Objects.isNull(product)) {
            throw new BusinessException("操作失败，产品信息不存在");
        }
        if (ProductStatusEnum.PAUSED.getValue().equals(product.getStatus())) {
            throw new BusinessException("该功能已暂停，若有疑问可联系客服~");
        } else if (!Objects.equals(product.getStatus(), ProductStatusEnum.ON_SHELF.getValue())){
            throw new BusinessException("操作失败，产品不可用");
        }

        //查询托管费续费详情信息
        TrusteeFeeRenewDetailVO trusteeFeeRenewDetailVO = memberCompanyMapper.queryTrusteeFeeRenewDetail(memberId, companyId);
        //查询设置托管费续费费用
        trusteeFeeRenewDetailVO.setTrusteeFee(product.getProdAmount());
        //设置会员优惠价
        if (!Objects.equals(member.getLevelName(),MemberLevelEnum.NORMAL.getMessage())){
            Long discount = this.memberProfitsRulesService.queryMemberDiscount(memberId,product.getId(),oemCode, company.getParkId());
            trusteeFeeRenewDetailVO.setMemberDiscount(discount);
        }

        /**
         *  判断是否存在特价活动，
         *  如果存在，订单金额 = 特价活动金额，支付金额取 = 特价活动*折扣， 优惠金额 = 订单金额 - 支付金额取
         */
        ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO = new ProductDiscountActivityAPIDTO();
        productDiscountActivityAPIDTO.setOemCode(oemCode);
        productDiscountActivityAPIDTO.setMemberId(memberId);
        productDiscountActivityAPIDTO.setIndustryId(company.getIndustryId());
        productDiscountActivityAPIDTO.setParkId(company.getParkId());
        productDiscountActivityAPIDTO.setProductType(product.getProdType());
        ProductDiscountActivityVO productDiscountActivityVO = productDiscountActivityService.getProductDiscountActivityByProductType(productDiscountActivityAPIDTO);
        if(productDiscountActivityVO!=null) {
            //查询设置托管费续费费用
            trusteeFeeRenewDetailVO.setTrusteeFee(productDiscountActivityVO.getSpecialPriceAmount());
            trusteeFeeRenewDetailVO.setMemberDiscount(productDiscountActivityVO.getPayAmount());
            trusteeFeeRenewDetailVO.setDiscountActivityId(productDiscountActivityVO.getDiscountActivityId());
        }
        return trusteeFeeRenewDetailVO;
    }

    /**
     * 即将过期的企业状态修改
     * @return
     */
    @Override
    public int updateCompanyOverdueStatus(Integer surplusDays) {
        return mapper.updateCompanyOverdueStatus(surplusDays);
    }

    @Override
    public List<OverdueCompanyInfoVO> getOverdueByMemberId(Integer overdueDays) {
        return mapper.queryOverdueByMemberId(overdueDays);
    }

    @Override
    public List<OverdueCompanyInfoVO> getWillExpireByMemberId(Integer surplusDays) {
        return mapper.queryWillExpireByMemberId(surplusDays);
    }

    @Override
    public void updateOverdueStatus(Long id, Date endTime) {
        Integer surplusDays = DEFAULT_SURPLUS_DAYS;

        // 查询系统配置到期有效提醒天数
        DictionaryEntity dict = this.dictionaryService.getByCode("expire_surplus_days");
        if (Objects.nonNull(dict)){
            // 取系统配置天数
            surplusDays = Integer.parseInt(dict.getDictValue());
        }
        mapper.updateOverdueStatus(id,endTime,surplusDays);
    }

    @Override
    public List<MemberCompanyVo> getMemberCompanyBymemberId(Long userId,String oemCode, List<String> categoryBaseIds,String operatorIdCardNo) {
        return mapper.getMemberCompanyBymemberId(userId,oemCode,categoryBaseIds,operatorIdCardNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void companyTransfer(Long companyId,String newOemCode,String newMemberAccount,String operUser){
        List<MemberCompanyBasicVo> list = queryCompanyBasicInfoByEinOrId(companyId,null,1,null);
        if(list == null || list.size() == 0){
            throw new BusinessException("未找到需要迁移的企业信息");
        }else if (list.size()> 1){
            throw new BusinessException("存在多个企业id相同的数据");
        }else{
            MemberCompanyBasicVo memberCompanyBasicVo = list.get(0);
            if(newMemberAccount.equals(memberCompanyBasicVo.getMemberAccount()) && newOemCode.equals(memberCompanyBasicVo.getOemCode())){
                throw new BusinessException("同一用户不能迁移,请重新选择");
            }
            MemberAccountEntity newMemberAccountEntity = memberAccountService.queryByAccount(newMemberAccount,newOemCode);
            if(newMemberAccountEntity == null){
                throw new BusinessException("新OEM机构的用户不存在或已注销");
            }else if(newMemberAccountEntity.getAuthStatus() == null || !ObjectUtil.equal(newMemberAccountEntity.getAuthStatus(),1)) {
                throw new BusinessException("新OEM机构的用户未进行实名认证，不能进行迁移");
            }
            //添加新的企业
            MemberCompanyEntity oldCompanyEntity  = this.findById(companyId);
            MemberCompanyEntity newCompanyEntity = setNewMemberCompanyEntityParams(oldCompanyEntity,newMemberAccountEntity,operUser);
            this.insertSelective(newCompanyEntity);
            //添加变动记录
            MemberCompanyChangeEntity memberCompanyChangeEntity = new MemberCompanyChangeEntity();
            BeanUtils.copyProperties(newCompanyEntity,memberCompanyChangeEntity);
            memberCompanyChangeEntity.setId(null);
            memberCompanyChangeEntity.setUpdateTime(new Date());
            memberCompanyChangeEntity.setUpdateUser(operUser);
            memberCompanyChangeEntity.setRemark("个体户迁移");
            memberCompanyChangeEntity.setCompanyId(newCompanyEntity.getId());
            memberCompanyChangeService.insertSelective(memberCompanyChangeEntity);
            //迁移企业相关数据
            this.mapper.transferCompanyInvoiceRecord(newCompanyEntity.getId(),newCompanyEntity.getOemCode(),operUser,oldCompanyEntity.getId());
            this.mapper.transferCompanyResourcesAddress(newCompanyEntity.getId(),newCompanyEntity.getOemCode(),operUser,oldCompanyEntity.getId());
            this.mapper.transferCompanyInvoiceategory(newCompanyEntity.getId(),newCompanyEntity.getOemCode(),operUser,oldCompanyEntity.getId());
            this.mapper.transferCompanyTaxBill(newCompanyEntity.getId(),operUser,oldCompanyEntity.getId());
            //注销原有个体户
            oldCompanyEntity.setStatus(MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue());
            oldCompanyEntity.setUpdateTime(new Date());
            oldCompanyEntity.setUpdateUser(operUser);
            oldCompanyEntity.setRemark("个体户迁移，原企业注销");
            this.editByIdSelective(oldCompanyEntity);
            //添加变动记录
            MemberCompanyChangeEntity oldMemberCompanyChangeEntity = new MemberCompanyChangeEntity();
            BeanUtils.copyProperties(oldCompanyEntity,oldMemberCompanyChangeEntity);
            oldMemberCompanyChangeEntity.setId(null);
            oldMemberCompanyChangeEntity.setAddTime(new Date());
            oldMemberCompanyChangeEntity.setAddUser(operUser);
            oldMemberCompanyChangeEntity.setCompanyId(oldCompanyEntity.getId());
            memberCompanyChangeService.insertSelective(oldMemberCompanyChangeEntity);
        }
    }

    /**
     * 设置迁移后个体户的信息
     * @param oldCompanyEntity
     * @param newMemberAccountEntity
     * @param operUser
     * @return
     */
    private MemberCompanyEntity setNewMemberCompanyEntityParams(MemberCompanyEntity oldCompanyEntity,MemberAccountEntity newMemberAccountEntity,String operUser){
        MemberCompanyEntity newCompanyEntity = new MemberCompanyEntity();
        BeanUtils.copyProperties(oldCompanyEntity,newCompanyEntity);
        newCompanyEntity.setId(null);
        newCompanyEntity.setMemberId(newMemberAccountEntity.getId());
        newCompanyEntity.setOemCode(newMemberAccountEntity.getOemCode());
        newCompanyEntity.setAddTime(oldCompanyEntity.getAddTime());
        newCompanyEntity.setAddUser(operUser);
        newCompanyEntity.setUpdateTime(new Date());
        newCompanyEntity.setUpdateUser(null);
        newCompanyEntity.setIsOther(1);
        newCompanyEntity.setOrderNo(null);
        newCompanyEntity.setRemark("个体户迁移");
        return newCompanyEntity;
    }

    /**
     * 根据国金用户id查询企业列表
     * @param channelUserId
     * @param channelOemCode
     * @param oemCode
     * @return
     */
    public List<GJUserCompanyVo> findCompanyListByChannelUserId(Long channelUserId,String channelOemCode,String oemCode){
        return this.mapper.findCompanyListByChannelUserId(channelUserId,channelOemCode,oemCode);
    }

    @Override
    public CompanyDetailOfAccessPartyVo getCompanyDetailOfAccessParty(Long memberId, String oemCode, Long id) throws BusinessException {
        CompanyDetailOfAccessPartyVo companyDetail = mapper.queryCompanyDetailOfAccessParty(memberId, oemCode, id);
        if(null == companyDetail){
            throw new BusinessException("企业不存在");
        }

        // 查询企业证件列表
        List<MemberCompanyCertListVo> memberCompanyCertList = mapper.getMemberCompanyCertList(memberId, oemCode, id);
        companyDetail.setCertList(memberCompanyCertList);

        // 如果存在注销凭证获取图片路径
        if(StringUtil.isNotBlank(companyDetail.getCancelCredentials())){
            String urls = "";
            String[] img = companyDetail.getCancelCredentials().split(",");
            for(String url : img){
                urls += ossService.getPrivateImgUrl(url);
                // 多张图片逗号分割
                urls += ",";
            }
            companyDetail.setCancelCredentials(urls);
        }
        return companyDetail;
    }

    @Override
    public List<CompanyListOfAccessPartyVO> listByMemberIds(AccessPartyCompanyQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return mapper.queryListByMemberIds(query);
    }

    /**
     * 根据企业id查询企业支持的税收分类编码
     * @param companyId
     * @return
     */
    @Override
    public List<String> findCompanyTaxCodeByCompanyId(Long companyId){
        return mapper.findCompanyTaxCodeByCompanyId(companyId);
    }

    @Override
    public List<MemberCompanyEntity> getCompanyByBusinssContent(String content) {
        return mapper.getCompanyByBusinssContent(content);
    }
}

