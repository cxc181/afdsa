package com.yuqian.itax.park.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemParkRelaEntity;
import com.yuqian.itax.agent.service.OemParkRelaService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.dao.ParkMapper;
import com.yuqian.itax.park.entity.*;
import com.yuqian.itax.park.entity.query.GetUsableParkIndustryQuery;
import com.yuqian.itax.park.entity.query.ParkListOfMenuQuery;
import com.yuqian.itax.park.entity.query.ParkQuery;
import com.yuqian.itax.park.entity.query.SearchParkQuery;
import com.yuqian.itax.park.entity.vo.*;
import com.yuqian.itax.park.enums.IncomeLevyTypeEnum;
import com.yuqian.itax.park.enums.ParkStatusEnum;
import com.yuqian.itax.park.service.ParkRewardPolicyLabelService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxPolicyService;
import com.yuqian.itax.park.service.TaxRulesConfigService;
import com.yuqian.itax.product.entity.ProductDiscountActivityEntity;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.ProductParkRelaEntity;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ProductDiscountActivityService;
import com.yuqian.itax.product.service.ProductParkRelaService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("parkService")
public class ParkServiceImpl extends BaseServiceImpl<ParkEntity,ParkMapper> implements ParkService {
    @Autowired
    private ProductService productService;
    @Autowired
    private TaxRulesConfigService taxRulesConfigService;
    @Autowired
    private OemService oemService;
    @Autowired
    private OemParkRelaService oemParkRelaService;
    @Autowired
    private TaxPolicyService taxPolicyService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private ProductDiscountActivityService productDiscountActivityService;
    @Autowired
    private ParkRewardPolicyLabelService parkRewardPolicyLabelService;
    @Autowired
    private ProductParkRelaService productParkRelaService;

    @Override
    public List<ParkListVO> queryParkList(ParkQuery parkQuery) {
        return mapper.getParkList(parkQuery);
    }


    @Override
    public List<ParkListVO> getOemParkList(ParkQuery parkQuery) {
        return mapper.getOemParkList(parkQuery);
    }
    @Override
    public PageInfo<ParkListVO> queryOemPageInfo(ParkQuery parkQuery) {
        PageHelper.startPage(parkQuery.getPageNumber(), parkQuery.getPageSize());
        return new PageInfo<>(this.mapper.getParkList(parkQuery));
    }

    @Override
    @Transactional
    public ParkEntity addParkEntity(ParkPO parkPO, String userAccount) {
        List<ParkEntity> parkEntities =mapper.getParkByParkCode(parkPO.getParkCode(),null);
        if(parkEntities.size()>0){
            throw  new BusinessException("该园区编码已经存在不允许新增。");
        }
        ParkEntity parkEntity =new ParkEntity();
        parkEntity.setParkName(parkPO.getParkName());
        parkEntity.setParkCode(parkPO.getParkCode());
        parkEntity.setParkCity(parkPO.getParkCity());
        parkEntity.setParkRecommend(parkPO.getParkRecommend());
        parkEntity.setPostageFees(parkPO.getPostageFees());
        parkEntity.setServiceContent("工商注册、开票");
        parkEntity.setStatus(0);
        parkEntity.setAddTime(new Date());
        parkEntity.setAddUser(userAccount);
        parkEntity.setBelongsCompanyName(parkPO.getBelongsCompanyName());
        parkEntity.setBelongsCompanyAddress(parkPO.getBelongsCompanyAddress());
        parkEntity.setEin(parkPO.getEin());
        parkEntity.setRecipient(parkPO.getRecipient());
        parkEntity.setRecipientPhone(parkPO.getRecipientPhone());
        parkEntity.setProvinceCode(parkPO.getProvinceCode());
        parkEntity.setProvinceName(parkPO.getProvinceName());
        parkEntity.setCityCode(parkPO.getCityCode());
        parkEntity.setCityName(parkPO.getCityName());
        parkEntity.setDistrictCode(parkPO.getDistrictCode());
        parkEntity.setDistrictName(parkPO.getDistrictName());
        parkEntity.setRecipientAddress(parkPO.getRecipientAddress());
        parkEntity.setAuthorizationFile(parkPO.getAuthorizationFile());
        parkEntity.setParkAddress(parkPO.getParkAddress());
        //parkEntity.setVerifyDesc(parkPO.getVerifyDesc());
        parkEntity.setDrawer(parkPO.getDrawer());
        parkEntity.setPayee(parkPO.getPayee());
        parkEntity.setReviewer(parkPO.getReviewer());
        //parkEntity.setSpecialConsiderations(parkPO.getSpecialConsiderations());
        parkEntity.setParkType(parkPO.getParkType());
//        parkEntity.setProcessMark(parkPO.getProcessMark());
        //parkEntity.setIncomeLevyType(parkPO.getIncomeLevyType());
        if (StringUtil.isNotBlank(parkPO.getOfficialSealImg())){
            parkEntity.setOfficialSealImg(parkPO.getOfficialSealImg());
        }
        parkEntity.setIsRegisterProfit(parkPO.getIsRegisterProfit());
        parkEntity.setIsRenewProfit(parkPO.getIsRenewProfit());

        // 4.1版本新增5个字段
        // 园区预览图
        if(StringUtil.isNotBlank(parkPO.getParkThumbnail())){
            parkEntity.setParkThumbnail(parkPO.getParkThumbnail());
        }
        if(StringUtil.isNotBlank(parkPO.getParkImgs())){
            parkEntity.setParkImgs(parkPO.getParkImgs());
        }

        // 税收政策说明
        if(StringUtil.isNotBlank(parkPO.getTaxPolicyDesc())){
            parkEntity.setTaxPolicyDesc(parkPO.getTaxPolicyDesc());
        }
        // 工商注册说明
        if(StringUtil.isNotBlank(parkPO.getRegisterDesc())){
            parkEntity.setRegisterDesc(parkPO.getRegisterDesc());
        }
        // 税务办理说明
        if(StringUtil.isNotBlank(parkPO.getTaxHandleDesc())){
            parkEntity.setTaxHandleDesc(parkPO.getTaxHandleDesc());
        }
        // 对公户办理说明
        if(StringUtil.isNotBlank(parkPO.getCorporateAccountHandleDesc())){
            parkEntity.setCorporateAccountHandleDesc(parkPO.getCorporateAccountHandleDesc());
        }
        mapper.insertSelective(parkEntity);
        //新增园区税费政策
        List<TaxPolicyEntity> list=parkPO.getTaxPolicyList();
        TaxPolicyEntity taxPolicyEntity = null;
        for(int i=0;list !=null && i<list.size();i++){
            taxPolicyEntity = list.get(i);
            if(list.get(i) !=null) {
                taxPolicyEntity.setParkId(parkEntity.getId());
                taxPolicyService.addTaxPolicy(taxPolicyEntity,userAccount);
            }
        }
        return parkEntity;
    }

    @Override
    public ParkEntity updateParkStatus(ParkPO parkPO, String userAccount) {
        ParkEntity parkEntity=mapper.selectByPrimaryKey(parkPO.getParkId());
        if(parkEntity.getStatus()==2){
            throw  new BusinessException("注销园区不允许操作。");
        }
        parkEntity.setStatus(parkPO.getStatus());
        parkEntity.setUpdateTime(new Date());
        parkEntity.setUpdateUser(userAccount);
        mapper.updateByPrimaryKey(parkEntity);
        return parkEntity;
    }

    @Override
    public List<ParkEntity> queryOemParkList(ParkQuery query) {
        return mapper.queryOemParkList(query);
    }

    @Override
    public List<ParkVO> queryProductParkList(Long productId) {
        return mapper.queryProductParkList(productId);
    }

    @Override
    public List<ParkEntity> getAllPark() {
        return mapper.getAllPark();
    }

    @Override
    public List<ParkVO> getAllParkAndPolicy(Integer companyType, String oemCode) {
        return mapper.getAllParkAndPolicy(companyType, oemCode);
    }

    @Override
    public ParkDetailVO getParkDteatailById(Long id) {
        ParkDetailVO parkDetailVO=new ParkDetailVO();
        ParkEntity parkEntity=mapper.selectByPrimaryKey(id);
        parkDetailVO.setParkCode(parkEntity.getParkCode());
        parkDetailVO.setParkName(parkEntity.getParkName());
        parkDetailVO.setParkCity(parkEntity.getParkCity());
        parkDetailVO.setParkRecommend(parkEntity.getParkRecommend());
        List<TaxPolicySelectVO> list= taxPolicyService.queryTaxPolicySelectByParkId(parkEntity.getId());
        parkDetailVO.setTaxPolicyList(list);
        parkDetailVO.setPostageFees(parkEntity.getPostageFees());
        parkDetailVO.setBelongsCompanyName(parkEntity.getBelongsCompanyName());
        parkDetailVO.setBelongsCompanyAddress(parkEntity.getBelongsCompanyAddress());
        parkDetailVO.setEin(parkEntity.getEin());
        parkDetailVO.setRecipient(parkEntity.getRecipient());
        parkDetailVO.setRecipientPhone(parkEntity.getRecipientPhone());
        parkDetailVO.setProvinceCode(parkEntity.getProvinceCode());
        parkDetailVO.setProvinceName(parkEntity.getProvinceName());
        parkDetailVO.setCityCode(parkEntity.getCityCode());
        parkDetailVO.setCityName(parkEntity.getCityName());
        parkDetailVO.setDistrictCode(parkEntity.getDistrictCode());
        parkDetailVO.setDistrictName(parkEntity.getDistrictName());
        parkDetailVO.setRecipientAddress(parkEntity.getRecipientAddress());
        parkDetailVO.setAuthorizationFile(parkEntity.getAuthorizationFile());
        parkDetailVO.setParkAddress(parkEntity.getParkAddress());
        parkDetailVO.setVerifyDesc(parkEntity.getVerifyDesc());
        parkDetailVO.setPayee(parkEntity.getPayee());
        parkDetailVO.setReviewer(parkEntity.getReviewer());
        parkDetailVO.setDrawer(parkEntity.getDrawer());
        parkDetailVO.setSpecialConsiderations(parkEntity.getSpecialConsiderations());
//        parkDetailVO.setProcessMark(parkEntity.getProcessMark());
        parkDetailVO.setIncomeLevyType(parkEntity.getIncomeLevyType());
        parkDetailVO.setParkType(parkEntity.getParkType());
        if (StringUtil.isNotBlank(parkEntity.getOfficialSealImg())){
            parkDetailVO.setOfficialSealImg(parkEntity.getOfficialSealImg());
        }
        parkDetailVO.setIsRegisterProfit(parkEntity.getIsRegisterProfit());
        parkDetailVO.setIsRenewProfit(parkEntity.getIsRenewProfit());
        parkDetailVO.setStatus(parkEntity.getStatus());

        // 园区预览图
        if(StringUtil.isNotBlank(parkEntity.getParkThumbnail())){
            parkDetailVO.setParkThumbnail(parkEntity.getParkThumbnail());
        }
        // 园区详情顶部banner 图片
        if(StringUtil.isNotBlank(parkEntity.getParkImgs())){
            parkDetailVO.setParkImgs(parkEntity.getParkImgs());
        }

        // 税收政策说明
        if(StringUtil.isNotBlank(parkEntity.getTaxPolicyDesc())){
            parkDetailVO.setTaxPolicyDesc(parkEntity.getTaxPolicyDesc());
        }
        // 工商注册说明
        if(StringUtil.isNotBlank(parkEntity.getRegisterDesc())) {
            parkDetailVO.setRegisterDesc(parkEntity.getRegisterDesc());
        }
        // 税务办理说明
        if(StringUtil.isNotBlank(parkEntity.getTaxHandleDesc())){
            parkDetailVO.setTaxHandleDesc(parkEntity.getTaxHandleDesc());
        }
        // 对公户办理说明
        if(StringUtil.isNotBlank(parkEntity.getCorporateAccountHandleDesc())){
            parkDetailVO.setCorporateAccountHandleDesc(parkEntity.getCorporateAccountHandleDesc());
        }

        return parkDetailVO;
    }

    @Override
    public List<ParkEntity> getParkByParkCode(String parkCode, Long id) {
        return mapper.getParkByParkCode(parkCode,id);
    }

    @Override
    public List<Park4OutVO> listParks(String oemCode,Integer companyType) throws BusinessException {
        log.info("园区列表查询：{},{}",oemCode,companyType);

        // 查询产品
        ProductEntity t = new ProductEntity();
        t.setOemCode(oemCode);
        t.setProdType(companyType);
        ProductEntity product = this.productService.selectOne(t);
        if(null == product){
            throw new BusinessException(ErrorCodeEnum.PRODUCT_NOT_EXISTS);
        }

        // 查询当前产品下的所有园区列表
        List<ParkVO> parkList = this.mapper.queryParkList(product.getId(), companyType);
        // 遍历园区列表，封装返回信息
        List<Park4OutVO> list = Lists.newArrayList();
        parkList.stream().forEach(park -> {
            Park4OutVO park4Out = new Park4OutVO();
            BeanUtils.copyProperties(park,park4Out);// 属性拷贝
            // 查询增值税、附加税和所得税率
            TaxRulesConfigEntity tt = new TaxRulesConfigEntity();
            tt.setCompanyType(product.getProdType());
            tt.setParkId(park.getParkId());
            tt.setTaxType(1);// 所得税
            List<TaxRulesConfigEntity> psersonalIncomeTaxRatesList = this.taxRulesConfigService.select(tt);
            List<TaxRatesRulesVO> personalIncomeTaxRates = Lists.newArrayList();
            psersonalIncomeTaxRatesList.stream().forEach(personalIncomeTaxRate -> {
                TaxRatesRulesVO personalRate = new TaxRatesRulesVO();
                BeanUtils.copyProperties(personalIncomeTaxRate,personalRate);
                personalIncomeTaxRates.add(personalRate);
            });
            park4Out.setPersonalIncomeTaxRates(personalIncomeTaxRates);

            tt.setTaxType(2);// 增值税
            List<TaxRulesConfigEntity> vatFeeRatesList = this.taxRulesConfigService.select(tt);
            List<TaxRatesRulesVO> vatFeeRates = Lists.newArrayList();
            vatFeeRatesList.stream().forEach(vatFeeRate -> {
                TaxRatesRulesVO vatRate = new TaxRatesRulesVO();
                BeanUtils.copyProperties(vatFeeRate,vatRate);
                vatFeeRates.add(vatRate);
            });
            park4Out.setVatFeeRates(vatFeeRates);

            tt.setTaxType(3);// 附加税
            TaxRulesConfigEntity rules = this.taxRulesConfigService.selectOne(tt);
            park4Out.setSurchargeRate(null == rules ? null : rules.getRate());
            list.add(park4Out);
        });
        return list;
    }

    @Override
    public List<SearchParkListVO> searchPark(SearchParkQuery query) {
        // 查询产品id
        ProductEntity productEntity = productService.queryProductByProdType(query.getCompanyType(), query.getOemCode(), null);
        if (null == productEntity) {
            throw new BusinessException("未查询到产品信息");
        }
        query.setProductId(productEntity.getId());
        return mapper.queryParkListByParam(query);
    }

    @Override
    public Map<String, Object> getSpecialConsiderations(Long parkId, String oemCode, Integer companyType) {
        // 参数校验
        if (null == parkId) {
            throw new BusinessException("园区id不能为空");
        }
        if (StringUtil.isBlank(oemCode)) {
            throw new BusinessException("机构编码不能为空");
        }

        // 查询机构
        Optional.ofNullable(oemService.getOem(oemCode)).orElseThrow(() -> new BusinessException("未查询到机构信息"));

        // 校验机构园区关系
        OemParkRelaEntity oemParkRelaEntity = new OemParkRelaEntity();
        oemParkRelaEntity.setOemCode(oemCode);
        oemParkRelaEntity.setParkId(parkId);
        Optional.ofNullable(oemParkRelaService.select(oemParkRelaEntity)).orElseThrow(() -> new BusinessException("当前机构未开通所查园区"));

        Map<String, Object> map = Maps.newHashMap();

        // 查询园区政策(由于当前仅个体户可查看园区介绍，故默认纳税人类型为小规模纳税人)
        TaxPolicyEntity taxPolicyEntity = taxPolicyService.queryTaxPolicyByParkId(parkId, companyType, 1);
        if (null != taxPolicyEntity && StringUtil.isNotBlank(taxPolicyEntity.getSpecialConsiderations())) {
            map.put("specialConsiderations", taxPolicyEntity.getSpecialConsiderations());
        }

        // 查询园区托管费
        ProductEntity productEntity = productService.queryProductByProdType(companyType, oemCode, parkId);
        if (null == productEntity) {
            throw new BusinessException("未查询到注册产品信息");
        }
        map.put("trusteeFee", productEntity.getProdAmount());
        //获取园区注册流程
        ProductParkRelaEntity productParkRelaEntity = new ProductParkRelaEntity();
        productParkRelaEntity.setParkId(parkId);
        productParkRelaEntity.setProductId(productEntity.getId());
        List<ProductParkRelaEntity>  productParkRelaList=productParkRelaService.select(productParkRelaEntity);
        if(productParkRelaList!=null && productParkRelaList.size()>0){
            map.put("processMark",productParkRelaList.get(0).getProcessMark());
        }
        //获取园区code
        ParkEntity parkEntity = this.findById(parkId);
        if(parkEntity!=null){
            map.put("parkCode",parkEntity.getParkCode());
            map.put("affiliatingArea", parkEntity.getAffiliatingArea());
        }

        // 查询托管费续费
        Integer renewalProductType = ProductTypeEnum.INDIVIDUAL_RENEWALS.getValue();
        if(companyType == 2){ //个独
            renewalProductType = ProductTypeEnum.INDEPENDENTLY_RENEWALS.getValue();
        }else if(companyType == 3){ //有限合伙
            renewalProductType = ProductTypeEnum.LIMITED_PARTNER_RENEWALS.getValue();
        }else if(companyType == 4){ //有限责任
            renewalProductType = ProductTypeEnum.LIMITED_LIABILITY_RENEWALS.getValue();
        }
        ProductEntity renewalProduct = productService.queryProductByProdType(renewalProductType, oemCode, parkId);
        if (null != renewalProduct) {
            map.put("renewalAmount", renewalProduct.getProdAmount());
        }

        // 优惠金额
        return map;
    }

    @Override
    public Map<String, Object> getUsableParkBusinessScope(String oemCode, String accessPartyCode, GetUsableParkIndustryQuery query) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("result", 1); // 先默认匹配到了园区，无论是否传税收编码
        List<UsableParkBusinessScopeVO> vo = null;
        List<Long> parkList = query.getParkList();
        ArrayList<Map<String, Object>> list = Lists.newArrayList();

        // 校验所有园区是否可用
        ParkQuery parkQuery = new ParkQuery();
        parkQuery.setOemCode(oemCode);
        parkQuery.setStatus(1);// 园区状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        if (null != parkList && !parkList.isEmpty()) {
            parkQuery.setParkIds(parkList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        parkQuery.setOemStatus(1);// 机构状态 0-不可用 1-可用
        List<ParkEntity> parkEntities = parkService.queryOemParkList(parkQuery);
        // 所有（意向）园区不可用
        if (null == parkEntities || parkEntities.isEmpty()) {
            map.put("result", 3);
            map.put("usableParkList", list);
            return map;
        }
        parkList = parkEntities.stream().map(ParkEntity::getId).collect(Collectors.toList());

        // 根据税收分类编码匹配（意向园区范围内的）可选园区经营范围
        if (!CollectionUtils.isEmpty(query.getTaxCodeList()) && StringUtil.isNotBlank(query.getTaxCodeList().get(0))) {
            // 使用税收分类编码匹配，默认结果为“未匹配到园区”
            map.put("result", 2);
            vo = mapper.getUsableParkBusinessScope(oemCode, query.getTaxCodeList(), parkList, query.getTaxCodeList().size(), query.getTaxpayerType());
            // 筛选完全匹配结果（所有编码在同一园区可匹配到对应经营范围）
            List<UsableParkBusinessScopeVO> collect = vo.stream().filter(x -> x.getIsSuited().equals(1)).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                vo = collect;
                map.put("result", 1);
            }
        }

        // 获取匹配后的园区
        if (null != vo && !vo.isEmpty()) {
            parkList.clear();
            parkList.addAll(vo.stream().map(UsableParkBusinessScopeVO::getParkId).collect(Collectors.toList()));
        }

        // 查询园区可选行业以及行业默认经营范围
        List<UsableParkBusinessScopeVO> usableParkIndustry = mapper.getUsableParkIndustry(oemCode, parkList, query.getResponseType(), query.getTaxpayerType());
        if (null == vo || vo.isEmpty()) {
            vo = usableParkIndustry;
        }
        if (1 == query.getResponseType()) {
            // 整合行业经营范围与税收编码匹配到的经营范围
            for (UsableParkBusinessScopeVO parkBusinessScopeVO : vo) {
                List<UsableParkBusinessScopeVO> parkIndustry = usableParkIndustry.stream().filter(x -> x.getParkId().equals(parkBusinessScopeVO.getParkId())).collect(Collectors.toList());
                parkBusinessScopeVO.setParkIndustryList(parkIndustry.get(0).getParkIndustryList());
            }
        }


        // 封装结果集
        for (UsableParkBusinessScopeVO businessScopeVO : vo) {
            HashMap<String, Object> businessScopeMap = Maps.newHashMap();
            businessScopeMap.put("parkId", businessScopeVO.getParkId());
            businessScopeMap.put("parkName", businessScopeVO.getParkName());
            businessScopeMap.put("vATBreaksAmount", businessScopeVO.getVATBreaksAmount());
            businessScopeMap.put("incomeTaxBreaksAmount", businessScopeVO.getIncomeTaxBreaksAmount());
            businessScopeMap.put("taxCodeBusinessScope", businessScopeVO.getTaxCodeBusinessScope());
            businessScopeMap.put("parkPolicyDesc",businessScopeVO.getParkPolicyDesc());
            businessScopeMap.put("parkIndustryList", businessScopeVO.getParkIndustryList());
            // 查询机构注册个体户特价活动
            ProductDiscountActivityEntity activityEntity = productDiscountActivityService.getByAccessPartyCode(accessPartyCode, ProductTypeEnum.INDIVIDUAL.getValue(), businessScopeVO.getParkId());
            if (null != activityEntity) {
                businessScopeMap.put("trusteeFee", activityEntity.getSpecialPriceAmount());
            } else {
                // 查询机构下注册托管费
                ProductEntity productEntity = Optional.ofNullable(productService.queryProductByProdType(ProductTypeEnum.INDIVIDUAL.getValue(), oemCode, businessScopeVO.getParkId())).orElseThrow(() -> new BusinessException("未查询到产品信息"));
                businessScopeMap.put("trusteeFee", productEntity.getProdAmount());
            }
            // 查询园区信息
            ParkEntity park = parkService.findById(businessScopeVO.getParkId());
            businessScopeMap.put("verifyDesc", park.getVerifyDesc());
            businessScopeMap.put("parkCity", park.getParkCity());
            list.add(businessScopeMap);
        }
        map.put("usableParkList", list);
        return map;
    }

    @Override
    public ParkProcessMarkVO getParkProcessMark(Long parkId) {
        return mapper.queryParkProcessMark(parkId);
    }

    @Override
    public List<TaxCalculatorParkVO> getAllParkList(Integer companyType, String oemCode) {
        return mapper.getAllParkList(companyType,oemCode);
    }

    @Override
    public List<String> getParkPolicyLabel(String oemCode) {
        List<String> list = Lists.newArrayList();
        // 企业类型标签
        List<TaxPolicyEntity> taxPolicyEntities = taxPolicyService.queryByIncomeLevyType(oemCode, IncomeLevyTypeEnum.VERIFICATION_COLLECTION.getValue());
        if (CollectionUtil.isNotEmpty(taxPolicyEntities)) {
            list.add("核定征收");
        }
        // 园区标签
        List<String> parkLabels = parkRewardPolicyLabelService.queryLabelByOemCode(oemCode);
        if (CollectionUtil.isNotEmpty(parkLabels)) {
            list.addAll(parkLabels);
        }
        return list;
    }

    @Override
    public List<ParkListOfMenuVO> getParkListOfMenu(ParkListOfMenuQuery query) {
        if (StringUtil.isNotBlank(query.getPolicyLabels())) {
            List<String> list = Arrays.asList(query.getPolicyLabels().split(","));
            query.setPolicyLabelList(list);
            if (list.contains("核定征收")) {
                query.setIncomeLevyType(IncomeLevyTypeEnum.VERIFICATION_COLLECTION.getValue());
            }
        }
        if (StringUtil.isNotBlank(query.getCompanyTypes())) {
            query.setCompanyTypeList(Arrays.asList(query.getCompanyTypes().split(",")));
        }
        List<ParkListOfMenuVO> list = mapper.getParkListOfMenu(query);
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        for (ParkListOfMenuVO vo : list) {
            if (StringUtil.isNotBlank(vo.getIncomeLevyTypeLabel())) {
                vo.setPolicyLabels(vo.getIncomeLevyTypeLabel().replace(",","")
                        + (StringUtil.isBlank(vo.getPolicyLabels()) ? "" : ("," + vo.getPolicyLabels())));
            }
        }
        return list;
    }

    @Override
    public ParkDetailOfMenuVO getParkDetailOfMenu(Long parkId,String oemCode) {
        if (null == parkId) {
            throw new BusinessException("园区id为空");
        }
        // 查询园区
        ParkEntity park = Optional.ofNullable(this.findById(parkId)).orElseThrow(() -> new BusinessException("未查询到园区信息"));
        if (!ParkStatusEnum.ON_SHELF.getValue().equals(park.getStatus()) && !ParkStatusEnum.PAUSED.getValue().equals(park.getStatus())) {
            throw new BusinessException("园区不可用");
        }
        // 查询园区详情
        ParkDetailOfMenuVO vo = mapper.getParkDetailOfMenu(parkId,oemCode);
        List<ParkRewardPolicyLabelEntity> list = vo.getPolicyLabels();
        if (CollectionUtil.isNotEmpty(list) && list.size() == 1 && null == list.get(0).getId()) {
            vo.setPolicyLabels(null);
        }
        return vo;
    }
}

