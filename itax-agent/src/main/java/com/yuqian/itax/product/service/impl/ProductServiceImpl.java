package com.yuqian.itax.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.dto.ProductagreementTemplateDTO;
import com.yuqian.itax.agreement.entity.ParkAgreementTemplateRelaEntity;
import com.yuqian.itax.agreement.service.ParkAgreementTemplateRelaService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.dao.ParkMapper;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.vo.ParkVO;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.product.dao.ChargeStandardMapper;
import com.yuqian.itax.product.dao.ProductMapper;
import com.yuqian.itax.product.dao.ProductParkRelaMapper;
import com.yuqian.itax.product.entity.ProductByParkEntity;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.ProductParkRelaEntity;
import com.yuqian.itax.product.entity.dto.ProductAndParkDTO;
import com.yuqian.itax.product.entity.dto.ProductDTO;
import com.yuqian.itax.product.entity.query.ProductQuery;
import com.yuqian.itax.product.entity.vo.ProductDetailVO;
import com.yuqian.itax.product.entity.vo.ProductOemVO;
import com.yuqian.itax.product.entity.vo.ProductOfTaxCalculatorVO;
import com.yuqian.itax.product.enums.ProductStatusEnum;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ProductByParkService;
import com.yuqian.itax.product.service.ProductParkRelaService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.util.util.MoneyUtil;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("productService")
public class ProductServiceImpl extends BaseServiceImpl<ProductEntity,ProductMapper> implements ProductService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ParkMapper parkMapper;
    @Resource
    private ProductParkRelaMapper productParkRelaMapper;
    @Resource
    private ChargeStandardMapper chargeStandardMapper;
    @Autowired
    private ProductByParkService productByParkService;
    @Autowired
    private ParkAgreementTemplateRelaService parkAgreementTemplateRelaService;
    @Autowired
    private ProductParkRelaService productParkRelaService;
    @Autowired
    private ParkService parkService;

    @Override
    public List<ProductEntity> queryProductList(String oemCode) {
        return this.productMapper.queryProductList(oemCode);
    }

    @Override
    public List<ProductEntity> queryProductListByOemCodeAndType(String oemCode) {
        return this.productMapper.queryProductListByOemCodeAndType(oemCode);
    }

    @Override
    public ProductDetailVO queryProductDetail(Long productId) throws BusinessException {
        if(null == productId){
            throw new BusinessException("产品ID不能为空！");
        }
        // 查询产品信息是否存在
        ProductEntity product = this.productMapper.selectByPrimaryKey(productId);
        if(null == product){
            throw new BusinessException("查询失败，产品不存在！");
        }
        // 查询当前产品下的所有园区列表
        List<ParkVO> parkList = this.parkMapper.queryParkList(productId, product.getCompanyType());
        ProductDetailVO productDetail = new ProductDetailVO();
        productDetail.setProductId(productId);
        productDetail.setStatus(product.getStatus());
        productDetail.setProdName(product.getProdName());
        productDetail.setProdType(product.getProdType());
        productDetail.setCompanyType(product.getProdType());
        productDetail.setProdAmount(product.getProdAmount());
        productDetail.setProdDesc(product.getProdDesc());
        productDetail.setOemCode(product.getOemCode());
        productDetail.setParkList(parkList);

        // 查询开户类型产品时，同时查询托管费续费产品金额
        if (!ProductTypeEnum.INDIVIDUAL.getValue().equals(product.getProdType())) {
            return productDetail;
        }
        Integer renewalProductType = ProductTypeEnum.INDIVIDUAL_RENEWALS.getValue();
        if(product.getCompanyType() == 2){ //个独
            renewalProductType = ProductTypeEnum.INDEPENDENTLY_RENEWALS.getValue();
        }else if(product.getCompanyType() == 3){ //有限合伙
            renewalProductType = ProductTypeEnum.LIMITED_PARTNER_RENEWALS.getValue();
        }else if(product.getCompanyType() == 4){ //有限责任
            renewalProductType = ProductTypeEnum.LIMITED_LIABILITY_RENEWALS.getValue();
        }
        ProductEntity productEntity = this.queryProductByProdType(renewalProductType, product.getOemCode(), null);
        if (null != productEntity) {
            productDetail.setRenewalAmount(productEntity.getProdAmount());
        }
        return productDetail;
    }

    @Override
    public PageInfo<ProductOemVO> listPageProduct(ProductQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(mapper.listProduct(query));
    }

    @Override
    public void updateStatus(Long id, Integer status, String updateUser) {
        mapper.updateStatus(id, status, updateUser, new Date());
    }

    @Override
    public ProductEntity queryProductByProdType(Integer prodType, String oemCode, Long parkId) {
        return mapper.queryProductByProdType(prodType, oemCode, ProductStatusEnum.OFF_SHELF.getValue(), parkId);
    }

    @Override
    @Transactional
    public void addProduct(ProductDTO dto) {
        ProductEntity proEntity = new ProductEntity();
        proEntity.setOemCode(dto.getOemCode());
        proEntity.setProdName(dto.getProdName());
        proEntity.setProdCode(dto.getProdCode());
        proEntity.setRemark(dto.getRemark());
        proEntity.setProdDesc(dto.getProdDesc());
        proEntity.setProdType(dto.getProdType());
        proEntity.setAddTime(dto.getOperationTime());
        proEntity.setAddUser(dto.getOperator());
        proEntity.setCompanyType(getCompanyTypeByProdType(dto.getProdType()));
        proEntity.setStatus(ProductStatusEnum.STAY_SHELF.getValue());
        if (StringUtil.isNotBlank(dto.getParkIds())){
            String parkIds = dto.getParkIds().replaceAll("[^\\d,]+", "").replaceAll("(,)\\1+", "$1");
            if (StringUtil.isNotBlank(parkIds)){
                String[] parkIdArray = parkIds.split(",");
                List<Long> parIdList = new ArrayList<>();
                for (String s:parkIdArray){
                    parIdList.add(Long.valueOf(s));
                }
                dto.setParkIdList(parIdList);
            }
        }
        if (Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue())) {
            // 个体开户   个独开户  有限合伙开户  有限责任开户 公户申请和托管
            if (dto.getChargeStandardTemplateId() == null) {
                proEntity.setAgreementTemplateId(dto.getChargeStandardTemplateId());
            }
            proEntity.setAmountName(dto.getAmountName());
            proEntity.setAmountWay(1);
            proEntity.setProdAmount(MoneyUtil.yuan2fen(dto.getProdAmount()).longValue());
            proEntity.setProcessingFee(MoneyUtil.yuan2fen(dto.getProcessingFee()).longValue());
            if (null != dto.getChargeStandardTemplateId()) {
                proEntity.setAgreementTemplateId(dto.getChargeStandardTemplateId());
            }
            mapper.insertSelective(proEntity);

        }else if (Objects.equals(dto.getProdType(), ProductTypeEnum.GOLDEN.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue())) {
            // 税务顾问  个体托管费续费  个独托管费续费  有限合伙托管费续费  有限责任托管费续费  对公户续费
            proEntity.setAmountName(dto.getAmountName());
            proEntity.setAmountWay(1);
            proEntity.setProdAmount(MoneyUtil.yuan2fen(dto.getProdAmount()).longValue());
            mapper.insertSelective(proEntity);
        } else if (Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_INVOICE.getValue())) {
            // 个体开票   个独开票
            //开票产品
            proEntity.setAmountWay(2);
            mapper.insertSelective(proEntity);
            chargeStandardMapper.addBatch(dto.getCharges(), proEntity.getId(), proEntity.getAddUser(), proEntity.getAddTime(),proEntity.getOemCode(),null,null);
        } else if (dto.getProdType() >= ProductTypeEnum.INDIVIDUAL_CANCEL.getValue()
                && dto.getProdType() <= ProductTypeEnum.LIMITED_LIABILITY_CANCEL.getValue()) {
            // 个体注销  有限责任注销
            //企业注销产品
            proEntity.setAmountName(dto.getAmountName());
            proEntity.setAmountWay(1);
            proEntity.setProdAmount(MoneyUtil.yuan2fen(dto.getProdAmount()).longValue());
            proEntity.setCancelTotalLimit(MoneyUtil.yuan2fen(dto.getCancelTotalLimit()).longValue());
            mapper.insertSelective(proEntity);
        }
        //园区单独定价
        if (Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_RENEWALS.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue())
                ||(dto.getProdType() >= ProductTypeEnum.INDIVIDUAL_CANCEL.getValue()
                && dto.getProdType() <= ProductTypeEnum.LIMITED_LIABILITY_CANCEL.getValue())){
            // 个体开户 个独开户 有限合伙开户  有限责任开户  个体托管费续费
            // 个独托管费续费 有限合伙托管费续费 有限责任托管费续费  个体开票 个独开票 个体注销 有限责任注销
            if (CollectionUtil.isNotEmpty(dto.getProductByParkList())){
                for (ProductAndParkDTO parkDTO:dto.getProductByParkList()){
                    if (null == parkDTO.getProdParkAmount() && null == parkDTO.getProcessingFee()) {
                        // 单独定价金额为空时，不保存数据，以免造成单独定价为0元的情况
                        // 对公户申请多一个办理费，两个金额都为空时，不保存数据
                        continue;
                    }
                    ProductByParkEntity productByParkEntity = new ProductByParkEntity();
                    productByParkEntity.setParkId(parkDTO.getParkId());
                    productByParkEntity.setProductId(proEntity.getId());
                    productByParkEntity.setProdType(dto.getProdType());
                    productByParkEntity.setAmountName(dto.getAmountName());
                    productByParkEntity.setProdAmount(MoneyUtil.yuan2fen(parkDTO.getProdParkAmount()).longValue());
                    // 对公户申请设置单独定价时，办理费与托管费都必填
                    if (ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue().equals(dto.getProdType())) {
                        // 查询园区名称
                        ParkEntity park = parkService.findById(parkDTO.getParkId());
                        if (null == park) {
                            throw new BusinessException("未查询到园区信息");
                        }
                        if (null == parkDTO.getProdParkAmount()) {
                            throw new BusinessException(park.getParkName() + "单独定价托管费不能为空");
                        }
                        if (null == parkDTO.getProcessingFee()) {
                            throw new BusinessException(park.getParkName() + "单独定价办理费不能为空");
                        }
                        productByParkEntity.setProcessingFee(MoneyUtil.yuan2fen(parkDTO.getProcessingFee()).longValue());
                    }
                    if(Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL_INVOICE.getValue())
                            || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY_INVOICE.getValue())
                            || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER_INVOICE.getValue())
                            || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_INVOICE.getValue())){
                        productByParkEntity.setAmountWay(2);
                    }else{
                        productByParkEntity.setAmountWay(1);
                    }
                    productByParkEntity.setOemCode(dto.getOemCode());
                    if(dto.getParkIdList().contains(parkDTO.getParkId())) {
                        productByParkEntity.setIsDelete(0);
                    }else{
                        productByParkEntity.setIsDelete(1);
                    }
                    productByParkEntity.setAddTime(new Date());
                    productByParkEntity.setAddUser(dto.getOperator());
                    productByParkService.insertSelective(productByParkEntity);
                    if((Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL_INVOICE.getValue())
                            || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY_INVOICE.getValue())
                            || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER_INVOICE.getValue())
                            || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_INVOICE.getValue()))&&
                            (CollectionUtil.isNotEmpty(parkDTO.getChargesByPark()) && dto.getParkIds().contains(parkDTO.getParkId().toString()))) {
                        chargeStandardMapper.addBatch(parkDTO.getChargesByPark(), proEntity.getId(), proEntity.getAddUser(), proEntity.getAddTime(), proEntity.getOemCode(), productByParkEntity.getId(), parkDTO.getParkId());
                    }
                }
            }
        }

        if(CollectionUtil.isNotEmpty(dto.getParkIdList())) {
            productParkRelaMapper.addBatch(dto.getParkIdList(), proEntity.getId(), proEntity.getAddUser(), proEntity.getAddTime());
        }
        // 保存园区与协议模板
        if (CollectionUtil.isNotEmpty(dto.getAgreementTemplateInfoList())
                    &&( Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL.getValue())
                    || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY.getValue())
                    ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER.getValue())
                    ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY.getValue())
            )){
            ProductParkRelaEntity productParkRelaEntity=null;
            for (ProductagreementTemplateDTO productagreementTemplateDTO:dto.getAgreementTemplateInfoList()){
                productParkRelaEntity = new ProductParkRelaEntity();
                productParkRelaEntity.setParkId(productagreementTemplateDTO.getParkId());
                productParkRelaEntity.setProductId(proEntity.getId());
                productParkRelaEntity = productParkRelaService.selectOne(productParkRelaEntity);
                productParkRelaEntity.setProcessMark(productagreementTemplateDTO.getProcessMark());
                productParkRelaEntity.setCompanyType(dto.getProdType());
                productParkRelaService.editByIdSelective(productParkRelaEntity);
                if (CollectionUtil.isNotEmpty(productagreementTemplateDTO.getAgreementTemplateId())){
                    for (Long agreementTemplateId:productagreementTemplateDTO.getAgreementTemplateId()){
                        ParkAgreementTemplateRelaEntity parkAgreementTemplateRelaEntity = new ParkAgreementTemplateRelaEntity();
                        parkAgreementTemplateRelaEntity.setParkId(productagreementTemplateDTO.getParkId());
                        parkAgreementTemplateRelaEntity.setAgreementTemplateId(agreementTemplateId);
                        parkAgreementTemplateRelaEntity.setAddTime(new Date());
                        parkAgreementTemplateRelaEntity.setAddUser( proEntity.getAddUser());
                        parkAgreementTemplateRelaEntity.setProductId(proEntity.getId());
                        parkAgreementTemplateRelaEntity.setCompanyType(proEntity.getCompanyType());
                        parkAgreementTemplateRelaService.insertSelective(parkAgreementTemplateRelaEntity);
                    }
                }
            }
        }
    }

    @Override
    public Integer getCompanyTypeByProdType(Integer prodType){
        Integer companyType = 1;
        if(prodType == null){
            return null;
        }
        if(prodType.equals(ProductTypeEnum.INDIVIDUAL.getValue())
                || prodType.equals(ProductTypeEnum.INDIVIDUAL_INVOICE.getValue())
                || prodType.equals(ProductTypeEnum.INDIVIDUAL_CANCEL.getValue())
                || prodType.equals(ProductTypeEnum.INDIVIDUAL_RENEWALS.getValue())
                || prodType.equals(ProductTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue())
                ){
            companyType = 1;
        }else if(prodType.equals(ProductTypeEnum.INDEPENDENTLY.getValue())
                || prodType.equals( ProductTypeEnum.INDEPENDENTLY_INVOICE.getValue())
                || prodType.equals(ProductTypeEnum.INDEPENDENTLY_CANCEL.getValue())
                || prodType.equals(ProductTypeEnum.INDEPENDENTLY_RENEWALS.getValue())
                ){
            companyType = 2;
        }else if(prodType.equals(ProductTypeEnum.LIMITED_PARTNER.getValue())
                || prodType.equals(ProductTypeEnum.LIMITED_PARTNER_INVOICE.getValue())
                || prodType.equals(ProductTypeEnum.LIMITED_PARTNER_CANCEL.getValue())
                || prodType.equals(ProductTypeEnum.LIMITED_PARTNER_RENEWALS.getValue())
                ){
            companyType = 3;
        }else if(prodType.equals(ProductTypeEnum.LIMITED_LIABILITY.getValue())
                || prodType.equals( ProductTypeEnum.LIMITED_LIABILITY_INVOICE.getValue())
                || prodType.equals( ProductTypeEnum.LIMITED_LIABILITY_CANCEL.getValue())
                || prodType.equals( ProductTypeEnum.LIMITED_LIABILITY_RENEWALS.getValue())
                ){
            companyType = 4;
        }
        return companyType;
    }

    @Override
    public List<ProductOfTaxCalculatorVO> getTaxCalculatorProductList(String oemCode) {
        return mapper.getTaxCalculatorProductList(oemCode);
    }

    @Override
    public void editProduct(ProductDTO dto) {
        ProductEntity proEntity = new ProductEntity();
        proEntity.setId(dto.getId());
        proEntity.setProdName(dto.getProdName());
        proEntity.setRemark(dto.getRemark());
        proEntity.setProdDesc(dto.getProdDesc());
        proEntity.setUpdateTime(dto.getOperationTime());
        proEntity.setUpdateUser(dto.getOperator());
        proEntity.setOemCode(dto.getOemCode());
        proEntity.setCompanyType(getCompanyTypeByProdType(dto.getProdType()));
        if (StringUtil.isNotBlank(dto.getParkIds())){
            String parkIds = dto.getParkIds().replaceAll("[^\\d,]+", "").replaceAll("(,)\\1+", "$1");
            if (StringUtil.isNotBlank(parkIds)){
                String[] parkIdArray = parkIds.split(",");
                List<Long> parIdList = new ArrayList<>();
                for (String s:parkIdArray){
                    parIdList.add(Long.valueOf(s));
                }
                dto.setParkIdList(parIdList);
               /* List<ProductAndParkDTO> listDto = new ArrayList<>();
                // 编辑筛选数据
                for (Long parkId:parIdList){
                    for (ProductAndParkDTO parkDTO:dto.getProductByParkList()){
                        if (parkDTO.getParkId().equals(parkId)){
                            listDto.add(parkDTO);
                            break;
                        }
                    }
                }
                dto.setProductByParkList(listDto);*/
            }
        }
        // 校验园区是否都配置了园区注册流程
        if (CollectionUtil.isNotEmpty(dto.getAgreementTemplateInfoList())){
            String tips = "";
            for (ProductagreementTemplateDTO productagreementTemplateDTO:dto.getAgreementTemplateInfoList()){
                if (productagreementTemplateDTO.getProcessMark() == null ){
                    tips += " "+productagreementTemplateDTO.getParkName();
                }
                if (StringUtil.isNotBlank(tips)){
                    throw  new BusinessException(tips+"未配置注册流程");
                }
            }
        }
        if (Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY.getValue())) {
            //开户产品
            if (dto.getChargeStandardTemplateId() != null){
                proEntity.setAgreementTemplateId(dto.getChargeStandardTemplateId());
            }else{
                throw  new BusinessException("收费标准模板不能为空");
            }
        }

        if (Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue())) {
            proEntity.setProdAmount(MoneyUtil.yuan2fen(dto.getProdAmount()).longValue());
            proEntity.setProcessingFee(MoneyUtil.yuan2fen(dto.getProcessingFee()).longValue());
            mapper.updateByPrimaryKeySelective(proEntity);
            //园区单独定价
            if (Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL.getValue())
                    || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY.getValue())
                    ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER.getValue())
                    ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY.getValue())
                    || Objects.equals(dto.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue())){
                if (CollectionUtil.isNotEmpty(dto.getProductByParkList())){
                    ProductByParkEntity productByParkEntity;
                    for (ProductAndParkDTO parkDTO:dto.getProductByParkList()){
                        // 对公户申请编辑单独定价时，办理费与托管费都必填或都不填
                        if (ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue().equals(dto.getProdType())) {
                            // 查询园区名称
                            ParkEntity park = parkService.findById(parkDTO.getParkId());
                            if (null == park) {
                                throw new BusinessException("未查询到园区信息");
                            }
                            if (null != parkDTO.getProcessingFee() && null == parkDTO.getProdParkAmount()) {
                                throw new BusinessException(park.getParkName() + "单独定价托管费不能为空");
                            }
                            if (null == parkDTO.getProcessingFee() && null != parkDTO.getProdParkAmount()) {
                                throw new BusinessException(park.getParkName() + "单独定价办理费不能为空");
                            }
//                            if (parkDTO.getProcessingFee().compareTo(BigDecimal.ZERO) < 0) {
//                                throw new BusinessException("金额配置不正确");
//                            }
                        }
//                        if (parkDTO.getProdParkAmount().compareTo(BigDecimal.ZERO) < 0) {
//                            throw new BusinessException("金额配置不正确");
//                        }
                        productByParkEntity = new ProductByParkEntity();
                        productByParkEntity.setParkId(parkDTO.getParkId());
                        productByParkEntity.setProductId(proEntity.getId());
                        productByParkEntity.setIsDelete(0);
                        productByParkEntity = productByParkService.selectOne(productByParkEntity);
                        if (productByParkEntity != null
                                && (parkDTO.getProdParkAmount() == null || !dto.getParkIds().contains(parkDTO.getParkId().toString())) ){
                            productByParkService.updateIsDelete(productByParkEntity.getId());
                            continue;
                        }
                        if (productByParkEntity == null){
                            // 新增单独定价，金额为空时不入库
                            if (ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue().equals(dto.getProdType())
                                    && null == parkDTO.getProcessingFee() && null == parkDTO.getProdParkAmount()) {
                                continue;
                            } else if (null == parkDTO.getProdParkAmount()) {
                                continue;
                            }
                            productByParkEntity = new ProductByParkEntity();
                            productByParkEntity.setParkId(parkDTO.getParkId());
                            productByParkEntity.setProductId(proEntity.getId());
                            productByParkEntity.setProdType(dto.getProdType());
                            productByParkEntity.setAmountName(dto.getAmountName());
                            if (parkDTO.getProdParkAmount() != null){
                                productByParkEntity.setProdAmount(MoneyUtil.yuan2fen(parkDTO.getProdParkAmount()).longValue());
                            }
                            if (parkDTO.getProcessingFee() != null) {
                                productByParkEntity.setProcessingFee(MoneyUtil.yuan2fen(parkDTO.getProcessingFee()).longValue());
                            }
                            productByParkEntity.setAmountWay(1);
                            productByParkEntity.setOemCode(dto.getOemCode());
                            productByParkEntity.setIsDelete(0);
                            productByParkEntity.setAddTime(new Date());
                            productByParkEntity.setAddUser(dto.getOperator());
                            productByParkService.insertSelective(productByParkEntity);
                        }else{
                            productByParkEntity.setProdType(dto.getProdType());
                            productByParkEntity.setAmountName(dto.getAmountName());
                            productByParkEntity.setProdAmount(MoneyUtil.yuan2fen(parkDTO.getProdParkAmount()).longValue());
                            productByParkEntity.setProcessingFee(MoneyUtil.yuan2fen(parkDTO.getProcessingFee()).longValue());
                            productByParkEntity.setUpdateTime(new Date());
                            productByParkEntity.setUpdateUser(dto.getOperator());
                            productByParkService.editByIdSelective(productByParkEntity);
                        }
                    }
                }
            }
        }else if (Objects.equals(dto.getProdType(), ProductTypeEnum.GOLDEN.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue())) {
            //税务顾问产品
            proEntity.setProdAmount(MoneyUtil.yuan2fen(dto.getProdAmount()).longValue());
            mapper.updateByPrimaryKeySelective(proEntity);
            if (Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL_RENEWALS.getValue())
                    || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY_RENEWALS.getValue())
                    || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER_RENEWALS.getValue())
                    || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_RENEWALS.getValue())
                    || Objects.equals(dto.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue())) {
                if (CollectionUtil.isNotEmpty(dto.getProductByParkList())){
                    for (ProductAndParkDTO parkDTO:dto.getProductByParkList()){
//                        if (parkDTO.getProdParkAmount().compareTo(BigDecimal.ZERO) < 0) {
//                            throw new BusinessException("金额配置不正确");
//                        }
                        ProductByParkEntity productByParkEntity = new ProductByParkEntity();
                        productByParkEntity.setParkId(parkDTO.getParkId());
                        productByParkEntity.setProductId(proEntity.getId());
                        productByParkEntity.setIsDelete(0);
                        ProductByParkEntity newProductByParkEntity = productByParkService.selectOne(productByParkEntity);
                        // 删除
                        if ((newProductByParkEntity != null && parkDTO.getProdParkAmount() == null) || !dto.getParkIds().contains(parkDTO.getParkId().toString())){
                            productByParkService.updateIsDelete(newProductByParkEntity.getId());
                            continue;
                        }
                        // 修改或新增
                        if (newProductByParkEntity != null){
                            productByParkEntity = newProductByParkEntity;
                        }else{
                            // 新增单独定价，金额为空时不入库
                            if (null == parkDTO.getProdParkAmount()) {
                                continue;
                            }
                            productByParkEntity.setProdType(dto.getProdType());
                            productByParkEntity.setAmountName(dto.getAmountName());
                            productByParkEntity.setAmountWay(dto.getAmountWay());
                            productByParkEntity.setOemCode(dto.getOemCode());
                            productByParkEntity.setIsDelete(0);
                            productByParkEntity.setAddUser(dto.getOperator());
                            productByParkEntity.setAddTime(new Date());
                            productByParkEntity.setProdAmount(MoneyUtil.yuan2fen(parkDTO.getProdParkAmount()).longValue());
                            if (parkDTO.getCancelTotalLimit() !=null){
                                productByParkEntity.setCancelTotalLimit(MoneyUtil.yuan2fen(parkDTO.getCancelTotalLimit()).longValue());
                            }
                            productByParkService.insertSelective(productByParkEntity);
                            continue;
                        }
                        productByParkEntity.setProdType(dto.getProdType());
                        productByParkEntity.setAmountName(dto.getAmountName());
                        productByParkEntity.setProdAmount(MoneyUtil.yuan2fen(parkDTO.getProdParkAmount()).longValue());
                        productByParkEntity.setUpdateTime(new Date());
                        productByParkEntity.setUpdateUser(dto.getOperator());
                        productByParkService.editByIdSelective(productByParkEntity);
                    }
                }
            }
        } else if (Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_INVOICE.getValue())) {
            //开票产品
            mapper.updateByPrimaryKeySelective(proEntity);
            chargeStandardMapper.deleteByProductId(proEntity.getId());
            chargeStandardMapper.addBatch(dto.getCharges(), proEntity.getId(), dto.getOperator(), new Date(),proEntity.getOemCode(),null,null);
            if (CollectionUtil.isNotEmpty(dto.getProductByParkList())){
                ProductByParkEntity productByParkEntity;
                for (ProductAndParkDTO parkDTO:dto.getProductByParkList()){
                    productByParkEntity = new ProductByParkEntity();
                    productByParkEntity.setParkId(parkDTO.getParkId());
                    productByParkEntity.setProductId(proEntity.getId());
                    productByParkEntity.setIsDelete(0);
                    ProductByParkEntity newProductByParkEntity = productByParkService.selectOne(productByParkEntity);
                    // 删除
                    if ((newProductByParkEntity != null && CollectionUtil.isEmpty(parkDTO.getChargesByPark())) ||  !dto.getParkIds().contains(parkDTO.getParkId().toString())){
                        if ((newProductByParkEntity != null && parkDTO.getProdParkAmount() == null) || !dto.getParkIds().contains(parkDTO.getParkId().toString())){
                            productByParkService.updateIsDelete(newProductByParkEntity.getId());
                            continue;
                        }
                    }
                    // 修改或新增
                    if (newProductByParkEntity != null){
                        productByParkEntity = newProductByParkEntity;
                        productByParkEntity.setUpdateTime(new Date());
                        productByParkEntity.setUpdateUser(dto.getOperator());
                        productByParkService.editByIdSelective(productByParkEntity);
                    }else{
                        productByParkEntity.setProdType(dto.getProdType());
                        productByParkEntity.setAmountName(dto.getAmountName());
                        productByParkEntity.setAmountWay(dto.getAmountWay());
                        productByParkEntity.setOemCode(dto.getOemCode());
                        productByParkEntity.setIsDelete(0);
                        productByParkEntity.setAddUser(dto.getOperator());
                        productByParkEntity.setAddTime(new Date());
                        productByParkEntity.setProdAmount(MoneyUtil.yuan2fen(parkDTO.getProdParkAmount()).longValue());
                        if (parkDTO.getCancelTotalLimit() !=null){
                            productByParkEntity.setCancelTotalLimit(MoneyUtil.yuan2fen(parkDTO.getCancelTotalLimit()).longValue());
                        }
                        productByParkService.insertSelective(productByParkEntity);
                    }
                    if (CollectionUtil.isNotEmpty(parkDTO.getChargesByPark()) && dto.getParkIds().contains(parkDTO.getParkId().toString())){
                        chargeStandardMapper.addBatch(parkDTO.getChargesByPark(), proEntity.getId(), dto.getOperator(), new Date(),proEntity.getOemCode(),productByParkEntity.getId(),parkDTO.getParkId());
                    }
                }
            }
        } else if (dto.getProdType() >= ProductTypeEnum.INDIVIDUAL_CANCEL.getValue()
                && dto.getProdType() <= ProductTypeEnum.LIMITED_LIABILITY_CANCEL.getValue()) {
            //企业注销产品
            proEntity.setProdAmount(MoneyUtil.yuan2fen(dto.getProdAmount()).longValue());
            proEntity.setCancelTotalLimit(MoneyUtil.yuan2fen(dto.getCancelTotalLimit()).longValue());
            mapper.updateByPrimaryKeySelective(proEntity);
            if (CollectionUtil.isNotEmpty(dto.getProductByParkList())){
                for (ProductAndParkDTO parkDTO:dto.getProductByParkList()){
//                    if (parkDTO.getProdParkAmount().compareTo(BigDecimal.ZERO) < 0) {
//                        throw new BusinessException("金额配置不正确");
//                    }
                    ProductByParkEntity productByParkEntity = new ProductByParkEntity();
                    productByParkEntity.setParkId(parkDTO.getParkId());
                    productByParkEntity.setProductId(proEntity.getId());
                    productByParkEntity.setIsDelete(0);
                    ProductByParkEntity newProductByParkEntity = productByParkService.selectOne(productByParkEntity);
                    if (dto.getParkIds().contains(parkDTO.getParkId().toString()) && ((parkDTO.getProdParkAmount() == null && parkDTO.getCancelTotalLimit() != null) || (parkDTO.getProdParkAmount() != null && parkDTO.getCancelTotalLimit() == null ))){
                        throw new BusinessException("注销手续费或手续费免费方式为空");
                    }
                    // 删除
                    if ((newProductByParkEntity != null && parkDTO.getProdParkAmount() == null) || !dto.getParkIds().contains(parkDTO.getParkId().toString())){
                        if (newProductByParkEntity != null){
                            productByParkService.updateIsDelete(newProductByParkEntity.getId());
                        }
                        continue;
                    }
                    // 修改或新增
                    if (newProductByParkEntity != null){
                        productByParkEntity = newProductByParkEntity;
                    }else{
                        // 新增单独定价，金额为空时不入库
                        if (null == parkDTO.getProdParkAmount() && null == parkDTO.getCancelTotalLimit()) {
                            continue;
                        }
                        productByParkEntity.setProdType(dto.getProdType());
                        productByParkEntity.setAmountName(dto.getAmountName());
                        productByParkEntity.setAmountWay(dto.getAmountWay());
                        productByParkEntity.setOemCode(dto.getOemCode());
                        productByParkEntity.setIsDelete(0);
                        productByParkEntity.setAddUser(dto.getOperator());
                        productByParkEntity.setAddTime(new Date());
                        productByParkEntity.setProdAmount(MoneyUtil.yuan2fen(parkDTO.getProdParkAmount()).longValue());
                        if (parkDTO.getCancelTotalLimit() !=null){
                            productByParkEntity.setCancelTotalLimit(MoneyUtil.yuan2fen(parkDTO.getCancelTotalLimit()).longValue());
                        }
                        productByParkService.insertSelective(productByParkEntity);
                        return;
                    }
                    productByParkEntity.setProdAmount(MoneyUtil.yuan2fen(parkDTO.getProdParkAmount()).longValue());
                    if (parkDTO.getCancelTotalLimit() !=null){
                        productByParkEntity.setCancelTotalLimit(MoneyUtil.yuan2fen(parkDTO.getCancelTotalLimit()).longValue());
                    }
                    productByParkEntity.setUpdateTime(new Date());
                    productByParkEntity.setUpdateUser(dto.getOperator());
                    productByParkService.editByIdSelective(productByParkEntity);
                }
            }
        }
        productParkRelaMapper.deleteByProductId(proEntity.getId());
        if(CollectionUtil.isNotEmpty(dto.getParkIdList())){
            productParkRelaMapper.addBatch(dto.getParkIdList(), proEntity.getId(), dto.getOperator(), dto.getOperationTime());
        }
        // 更新园区与协议模板
        if (CollectionUtil.isNotEmpty(dto.getAgreementTemplateInfoList())
                &&( Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL.getValue())
                    || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY.getValue())
                    ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER.getValue())
                    ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY.getValue())
                )){
            ProductParkRelaEntity productParkRelaEntity = null;
            for (ProductagreementTemplateDTO productagreementTemplateDTO:dto.getAgreementTemplateInfoList()){
                productParkRelaEntity = new ProductParkRelaEntity();
                productParkRelaEntity.setParkId(productagreementTemplateDTO.getParkId());
                productParkRelaEntity.setProductId(proEntity.getId());
                productParkRelaEntity = productParkRelaService.selectOne(productParkRelaEntity);
                productParkRelaEntity.setProcessMark(productagreementTemplateDTO.getProcessMark());
                productParkRelaEntity.setCompanyType(dto.getProdType());
                productParkRelaService.editByIdSelective(productParkRelaEntity);
                if (CollectionUtil.isNotEmpty(productagreementTemplateDTO.getAgreementTemplateId())){
                    // 删除
                    parkAgreementTemplateRelaService.deleteByParkIdAndProductId(productagreementTemplateDTO.getParkId(),proEntity.getId());
                    for (Long agreementTemplateId:productagreementTemplateDTO.getAgreementTemplateId()){
                        ParkAgreementTemplateRelaEntity parkAgreementTemplateRelaEntity = new ParkAgreementTemplateRelaEntity();
                        parkAgreementTemplateRelaEntity.setParkId(productagreementTemplateDTO.getParkId());
                        parkAgreementTemplateRelaEntity.setAgreementTemplateId(agreementTemplateId);
                        parkAgreementTemplateRelaEntity.setAddTime(new Date());
                        parkAgreementTemplateRelaEntity.setAddUser( proEntity.getAddUser());
                        parkAgreementTemplateRelaEntity.setProductId(proEntity.getId());
                        parkAgreementTemplateRelaEntity.setCompanyType(dto.getCompanyType());
                        parkAgreementTemplateRelaService.insertSelective(parkAgreementTemplateRelaEntity);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        mapper.deleteByPrimaryKey(id);
        productParkRelaMapper.deleteByProductId(id);
        chargeStandardMapper.deleteByProductId(id);
    }

    @Override
    public ProductEntity queryProductByRelation(Long memberId, String oemCode, Integer prodType) {
        if (null == memberId) {
            throw new BusinessException("用户id为空");
        }
        if (StringUtil.isBlank(oemCode)) {
            throw new BusinessException("机构编码不能为空");
        }
        if (null == prodType) {
            throw new BusinessException("产品类型不能为空");
        }
        return mapper.queryProductByRelation(memberId, oemCode, prodType);
    }
}

