package com.yuqian.itax.admin.controller.agent;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.dto.ProductagreementTemplateDTO;
import com.yuqian.itax.agent.enums.OemStatusEnum;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import com.yuqian.itax.agreement.service.ParkAgreementTemplateRelaService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.query.ParkQuery;
import com.yuqian.itax.park.entity.vo.ParkVO;
import com.yuqian.itax.park.enums.ParkStatusEnum;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.product.entity.ChargeStandardEntity;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.ProductParkRelaEntity;
import com.yuqian.itax.product.entity.dto.ProductAndParkDTO;
import com.yuqian.itax.product.entity.dto.ProductDTO;
import com.yuqian.itax.product.entity.query.ProductQuery;
import com.yuqian.itax.product.entity.vo.*;
import com.yuqian.itax.product.enums.ProductStatusEnum;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ChargeStandardService;
import com.yuqian.itax.product.service.ProductByParkService;
import com.yuqian.itax.product.service.ProductParkRelaService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.enums.UserAccountTypeEnum;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 产品管理
 * @author：pengwei
 * @Date：2019/12/16 20:12
 * @version：1.0
 */
@Api(tags = "产品管理")
@RestController
@RequestMapping("product")
@Slf4j
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ParkService parkService;

    @Autowired
    private ChargeStandardService chargeStandardService;

    @Autowired
    private OemService oemService;
    @Autowired
    private ProductByParkService productByParkService;
    @Autowired
    private ParkAgreementTemplateRelaService parkAgreementTemplateRelaService;
    @Autowired
    private ProductParkRelaService productParkRelaService;

    @ApiOperation("列表页")
    @PostMapping("page")
    public ResultVo listPageProduct(@RequestBody ProductQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setOemStatus(null);
        //query.setOemCode(userEntity.getOemCode());
        PageInfo<ProductOemVO> page = productService.listPageProduct(query);
        return ResultVo.Success(page);
    }

    /**
     * 修改产品状态
     * @param data 请求数据
     * @return
     */
    @PostMapping("update/status")
    public ResultVo updateStatus(@RequestBody String data){
        CurrUser currUser = getCurrUser();
        Long id = getParameter(data, "id", Long.class);
        Integer status = getParameter(data, "status", Integer.class);
        if (id == null || status == null || status < 1 || status > 3){
            return ResultVo.Fail("请求参数错误");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        ProductEntity entity = productService.findById(id);
        if (entity == null) {
            return ResultVo.Fail(ResultConstants.PRODUCT_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() != 1 && !StringUtils.equals(entity.getOemCode(), userEntity.getOemCode())) {
            return ResultVo.Fail("产品不属于当前OEM机构");
        }
        if (Objects.equals(ProductStatusEnum.OFF_SHELF.getValue(), entity.getStatus())) {
            return ResultVo.Fail("已下架产品不支持其他操作");
        }
        if (Objects.equals(ProductStatusEnum.STAY_SHELF.getValue(), entity.getStatus())
                && Objects.equals(ProductStatusEnum.PAUSED.getValue(), status)) {
            return ResultVo.Fail("待上架产品不支持暂停");
        }else if (Objects.equals(ProductStatusEnum.ON_SHELF.getValue(), entity.getStatus())
                && !Objects.equals(ProductStatusEnum.PAUSED.getValue(), status)) {
            return ResultVo.Fail("已上架产品只支持暂停");
        }else if (Objects.equals(ProductStatusEnum.PAUSED.getValue(), entity.getStatus())
                && Objects.equals(ProductStatusEnum.PAUSED.getValue(), status)) {
            return ResultVo.Fail("已暂停产品只不支持暂停");
        }
        productService.updateStatus(id, status, currUser.getUseraccount());
        return ResultVo.Success();
    }

    @ApiOperation("可选园区列表")
    @PostMapping("list/park")
    public ResultVo listProductPark(@RequestBody JSONObject jsonObject){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(jsonObject == null){
            return ResultVo.Fail("参数不能为空");
        }
        String oemCode = jsonObject.getString("oemCode");
        ParkQuery query = new ParkQuery();
        if (userEntity.getPlatformType() == 1) {
            if (StringUtils.isBlank(oemCode)) {
                return ResultVo.Fail("请选择oem机构");
            }
            query.setOemCode(oemCode);
        } else {
            query.setOemCode(userEntity.getOemCode());
        }
        if(!jsonObject.containsKey("prodType")){
            return ResultVo.Fail("请选择产品类型");
        }
        Integer prodType = jsonObject.getInteger("prodType");
        if(prodType==null){
            return ResultVo.Fail("产品类型不能为空");
        }
        query.setCompanyType(productService.getCompanyTypeByProdType(prodType));
        query.setStatus(ParkStatusEnum.ON_SHELF.getValue());
        List<ParkEntity> list = parkService.queryOemParkList(query);
        return ResultVo.Success(list);
    }

    @ApiOperation("产品新增")
    @PostMapping("add")
    public ResultVo add(@RequestBody @Validated(Add.class) ProductDTO dto, BindingResult result) {
        CurrUser currUser = getCurrUser();
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() == 1 && Objects.equals(UserAccountTypeEnum.ADMIN.getValue(), userEntity.getAccountType())) {
            if (StringUtils.isBlank(dto.getOemCode())) {
                return ResultVo.Fail("请选择产品归属的OEM机构");
            }
            OemEntity oem = oemService.getOem(dto.getOemCode());
            if (oem == null) {
                return ResultVo.Fail("OEM机构不存在");
            }
            if (Objects.equals(OemStatusEnum.NO.getValue(), oem.getOemStatus())) {
                return ResultVo.Fail("OEM机构已下架，不能添加产品");
            }
        } else if(StringUtils.isNotBlank(userEntity.getOemCode())) {
            dto.setOemCode(userEntity.getOemCode());
        }
        ProductEntity entity = productService.queryProductByProdType(dto.getProdType(), dto.getOemCode(), null);
        if (entity != null) {
            return ResultVo.Fail("已有同类型产品，不能新增");
        }
        try {
            //校验数据信息
            validData(dto, userEntity);

            dto.setOperationTime(new Date());
            dto.setOperator(currUser.getUseraccount());
            productService.addProduct(dto);
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error("新增产品失败，请求参数：{}", JSONObject.toJSONString(dto));
            log.error(e.getMessage(), e);
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 校验产品
     * @param dto
     * @param userEntity
     * @return
     */
    private void validData(ProductDTO dto, UserEntity userEntity) throws BusinessException {
        if (StringUtils.isNotBlank(dto.getRemark()) && dto.getRemark().length() > 60) {
            throw new BusinessException("备注不能超过60位字符");
        }
        if (Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL.getValue()) ||
                Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY.getValue())||
                Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER.getValue())||
                Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY.getValue())) {
            //校验金额信息
            validAmount(dto);
            //校验园区
            validPark(dto);
        }else if (Objects.equals(dto.getProdType(), ProductTypeEnum.GOLDEN.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER_RENEWALS.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_RENEWALS.getValue())) {
            //校验金额信息
            validAmount(dto);
        } else if (Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER_INVOICE.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_INVOICE.getValue())) {
            // 个体开票  个人独资开票
            // 4.0版本 新增 有限合伙开票, 有限责任开票
            if (StringUtils.isBlank(dto.getJsonStr())) {
                throw new BusinessException("开票服务费列表不能为空");
            }
            try {
                List<JSONArray> jsonArrayList = new ArrayList<>();
                JSONObject json;
                List<ProductAndParkDTO> dtoList = new ArrayList<>();
                JSONArray arrayByProduct = JSONObject.parseArray(dto.getJsonStr());
                if (CollectionUtil.isEmpty(arrayByProduct)) {
                    throw new BusinessException("开票服务费列表不能为空");
                }
                jsonArrayList.add(arrayByProduct);
                if (dto.getProductByParkList() != null){
                    for (ProductAndParkDTO pdto:dto.getProductByParkList()){
                        if (CollectionUtil.isNotEmpty(pdto.getChargesByPark())){
                            dtoList.add(pdto);
                            JSONArray arrayByPark = JSONArray.parseArray(JSON.toJSONString(pdto.getChargesByPark()));
                            jsonArrayList.add(arrayByPark);
                        }
                    }
                }
                for (int j = 0;j<jsonArrayList.size();j++){
                    JSONArray array = jsonArrayList.get(j);
                    List<ChargeStandardEntity> charges = Lists.newArrayList();
                    ChargeStandardEntity entity;
                    Date date = new Date();
                    for (int i = 0; i < array.size(); i++) {
                        //  如果List<Object>转JSONArray属性值为空  则在json中会没有这个键
                        json = array.getJSONObject(i);
                        if (!json.containsKey("chargeMin")){
                            json.put("chargeMin","");
                        }
                        if(!json.containsKey("chargeMax")){
                            json.put("chargeMax","");
                        }
                        entity = new ChargeStandardEntity();
                        BigDecimal chargeMin = json.getBigDecimal("chargeMin");
                        if (chargeMin == null || BigDecimal.ZERO.compareTo(chargeMin) > 0) {
                            throw new BusinessException("开票服务费第" + (i+1) + "列最小金额有误");
                        }
                        BigDecimal chargeMax = json.getBigDecimal("chargeMax");
                        if (i == array.size() - 1 ) {
                            chargeMax = new BigDecimal(Long.MAX_VALUE/100);
                        }

                        if (chargeMax == null || BigDecimal.ZERO.compareTo(chargeMax) > 0) {
                            throw new BusinessException("开票服务费第" + (i+1) + "列最大金额有误");
                        }

                        if (chargeMax.compareTo(chargeMin) <= 0) {
                            throw new BusinessException("开票服务费第" + (i+1) + "列最大金额小于或等于最小金额");
                        }
                        BigDecimal chargeRate = json.getBigDecimal("chargeRate");
                        if (chargeRate == null || BigDecimal.ZERO.compareTo(chargeRate) > 0) {
                            throw new BusinessException("开票服务费第" + (i+1) + "列费率有误");
                        }
                        entity.setOemCode(userEntity.getOemCode());
                        entity.setChargeMin(chargeMin.multiply(new BigDecimal(100)).longValue());
                        entity.setChargeMax(chargeMax.multiply(new BigDecimal(100)).longValue());
                        entity.setChargeRate(chargeRate);
                        entity.setChargeType(1);
                        entity.setAddTime(date);
                        entity.setAddUser(userEntity.getUsername());
                        entity.setUpdateTime(date);
                        entity.setUpdateUser(userEntity.getUsername());
                        entity.setOrderSn((i + 1));
                        charges.add(entity);
                        if (i == 0) {
                            continue;
                        }
                        if (chargeMin.compareTo(array.getJSONObject(i-1).getBigDecimal("chargeMax")) != 0) {
                            throw new BusinessException("开票服务费第" + (i+1) + "列最小金额不等于上一列最大金额");
                        }
                    }
                    if (j == 0){
                        dto.setCharges(charges);
                    }else{
             /*           ProductAndParkDTO productAndParkDTO = new ProductAndParkDTO();
                        productAndParkDTO = dto.getProductByParkList().get(j-1);
                        productAndParkDTO.setChargesByPark(charges);
                        dtoList.set(j-1,productAndParkDTO);*/
                        dtoList.get(j-1).setChargesByPark(charges);
                        dto.setProductByParkList(dtoList);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new BusinessException("开票服务费列表转换失败");
            }
        } else if (
                dto.getProdType() == ProductTypeEnum.INDIVIDUAL_CANCEL.getValue()
                || dto.getProdType() == ProductTypeEnum.LIMITED_LIABILITY_CANCEL.getValue()
                        || dto.getProdType() == ProductTypeEnum.INDEPENDENTLY_CANCEL.getValue()
                        || dto.getProdType() == ProductTypeEnum.LIMITED_PARTNER_CANCEL.getValue()) {
            // 个体注销   有限责任注销
            // 4.0版本新增   个人独资注销   有限合伙注销
            //校验金额信息
            validAmount(dto);
            if (dto.getCancelTotalLimit() == null) {
                throw new BusinessException("手续费免费方式金额不能为空");
            }
            if (dto.getCancelTotalLimit().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("手续费免费方式金额必须大于等于0");
            }
        } else if (Objects.equals(dto.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue())) {
            //校验金额信息
            validAmount(dto);
            if (dto.getProcessingFee() == null) {
                throw new BusinessException("办理费不能为空");
            }
            if (dto.getProcessingFee().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("办理费金额必须大于等于0");
            }
            //校验园区
            validPark(dto);
        } else if(Objects.equals(dto.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue())){
            //校验金额信息
            validAmount(dto);
        }else{
            throw new BusinessException("产品类型有误");
        }
    }

    /**
     * 校验金额信息
     * @param dto
     * @return
     */
    public void validAmount(ProductDTO dto) throws BusinessException {
        if (StringUtils.isBlank(dto.getAmountName())) {
            throw new BusinessException("金额名称不能为空");
        }
        if (dto.getProdAmount() == null) {
            throw new BusinessException("金额不能为空");
        }
        if (dto.getProdAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("金额必须大于等于0");
        }
    }

    /**
     * 校验园区信息
     * @param dto
     * @return
     */
    public void validPark(ProductDTO dto) throws BusinessException {
        if (StringUtils.isBlank(dto.getParkIds())) {
            throw new BusinessException("支持园区不能为空");
        }
        String parkIds = dto.getParkIds().replaceAll("[^\\d,]+", "").replaceAll("(,)\\1+", "$1");
        if (parkIds.startsWith(",")) {
            parkIds = parkIds.substring(1);
        }
        if (StringUtils.isBlank(parkIds)) {
            throw new BusinessException("支持园区不能为空");
        }
        dto.setParkIds(parkIds);
        ParkQuery query = new ParkQuery();
        query.setParkIds(dto.getParkIds());
        query.setOemCode(dto.getOemCode());
//            query.setOemStatus(OemStatusEnum.YES.getValue());
        query.setStatus(ParkStatusEnum.ON_SHELF.getValue());
        List<ParkEntity> list = parkService.queryOemParkList(query);
        if (CollectionUtil.isEmpty(list)) {
            throw new BusinessException("支持园区不存在");
        }
        if (list.size() != dto.getParkIds().split(",").length) {
            throw new BusinessException("支持园区信息有误");
        }
        dto.setParkIdList(list.stream().map(ParkEntity::getId).collect(Collectors.toList()));
        //开户产品参数校验
        if(Objects.equals(dto.getProdType(), ProductTypeEnum.INDIVIDUAL.getValue())
                || Objects.equals(dto.getProdType(), ProductTypeEnum.INDEPENDENTLY.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_PARTNER.getValue())
                ||Objects.equals(dto.getProdType(), ProductTypeEnum.LIMITED_LIABILITY.getValue())){
            //收费标准模板
            if (dto.getChargeStandardTemplateId() == null){
                throw  new BusinessException("收费标准模板不能为空");
            }
            if(CollectionUtil.isEmpty(dto.getAgreementTemplateInfoList())){
                throw  new BusinessException("园区未配置注册流程");
            }

            //去除无需的园区协议列表数据
            List<ProductagreementTemplateDTO> agreementTemplateInfoList =new ArrayList<>(dto.getAgreementTemplateInfoList().size());
            agreementTemplateInfoList.addAll(dto.getAgreementTemplateInfoList());
            for (ProductagreementTemplateDTO tdto : dto.getAgreementTemplateInfoList()){
                if(!dto.getParkIdList().contains(tdto.getParkId())){
                    agreementTemplateInfoList.remove(tdto);
                }
            }
            //校验园区是否都配置了园区注册流程
            if (agreementTemplateInfoList==null && list!=null ){
                throw  new BusinessException("园区未配置注册流程");
            }else if(agreementTemplateInfoList!=null && list!=null){
                List<String> parkNameList = new ArrayList<>();
                Long parkId = null;
                for(ParkEntity parkEntity: list){
                    parkId = parkEntity.getId();
                    for (ProductagreementTemplateDTO tdto:agreementTemplateInfoList){
                        if(ObjectUtil.equal(tdto.getParkId(),parkId) && tdto.getProcessMark()==null){
                            parkNameList.add(parkEntity.getParkName());
                        }
                        if(ObjectUtil.equal(tdto.getParkId(),parkId)){
                            parkId = null;
                            break;
                        }
                    }
                    if(parkId!=null){
                        parkNameList.add(parkEntity.getParkName());
                    }
                }
                if(parkNameList!=null && parkNameList.size()>0){
                    throw  new BusinessException("园区["+String.join(",",parkNameList)+"]未配置注册流程");
                }
            }
            dto.setAgreementTemplateInfoList(agreementTemplateInfoList);
        }
    }

    @ApiOperation("产品详情")
    @PostMapping("detail")
    public ResultVo detail(@JsonParam Long id) {
        CurrUser currUser = getCurrUser();
        if (id == null) {
            return ResultVo.Fail("产品主键不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        ProductEntity entity = productService.findById(id);
        if (entity == null) {
            return ResultVo.Fail(ResultConstants.PRODUCT_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() != 1) {
            if (!StringUtils.equals(entity.getOemCode(), userEntity.getOemCode())) {
                return ResultVo.Fail("产品不属于当前OEM机构");
            }
        }
        ProductDetailAdminVO vo = new ProductDetailAdminVO(entity);
        List<ParkVO> lists = parkService.queryProductParkList(entity.getId());
        if (CollectionUtil.isNotEmpty(lists)) {
            vo.setParkIds(lists.stream().map(ParkVO::getParkId).collect(Collectors.toList()));
        }
        List<ProductByParkVO> productByParkByProductIdList = productByParkService.getProductByParkByProductId(entity.getId());
        if (Objects.equals(entity.getProdType(), ProductTypeEnum.INDIVIDUAL.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.INDEPENDENTLY.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.LIMITED_PARTNER.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.LIMITED_LIABILITY.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue())) {
           /* if (CollectionUtil.isEmpty(lists)) {
                return ResultVo.Success(vo);
            }*/
            List<ProductAndParkDTO> productByParkList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(productByParkByProductIdList)){
                for (ProductByParkVO ProductByParkvo:productByParkByProductIdList){
                    ProductAndParkDTO parkDTO = new ProductAndParkDTO();
                    parkDTO.setParkId(ProductByParkvo.getParkId());
                    parkDTO.setProdParkAmount(new BigDecimal(ProductByParkvo.getProdAmount()));
                    parkDTO.setProcessingFee(new BigDecimal(ProductByParkvo.getProcessingFee()));
                    if (ProductByParkvo.getCancelTotalLimit() != null){
                        parkDTO.setCancelTotalLimit(new BigDecimal(ProductByParkvo.getCancelTotalLimit()));
                    }
                    productByParkList.add(parkDTO);
                }
            }
            if (CollectionUtil.isNotEmpty(productByParkList)){
                vo.setProductByParkList(productByParkList);
            }
          /*  return ResultVo.Success(vo);*/
        }
        if (Objects.equals(entity.getProdType(), ProductTypeEnum.INDIVIDUAL_INVOICE.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.INDEPENDENTLY_INVOICE.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.LIMITED_PARTNER_INVOICE.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_INVOICE.getValue())) {
            try {
                if (CollectionUtil.isNotEmpty(productByParkByProductIdList)){
                    List<ProductAndParkDTO> productByParkList = new ArrayList<>();
                    for (ProductByParkVO ProductByParkvo:productByParkByProductIdList){
                        List<ChargeStandardEntity> chargeStandardEntityList = chargeStandardService.getChargeStandardsByParkProductId(ProductByParkvo.getId());
                        ProductAndParkDTO parkDTO = new ProductAndParkDTO();
                        parkDTO.setId(ProductByParkvo.getId());
                        parkDTO.setParkId(ProductByParkvo.getParkId());
                        parkDTO.setChargesByPark(chargeStandardEntityList);
                        productByParkList.add(parkDTO);
                    }
                    if (CollectionUtil.isNotEmpty(productByParkList)){
                        vo.setProductByParkList(productByParkList);
                    }
                }
                List<ChargeStandardEntity> charges = chargeStandardService.getChargeStandards(entity.getId(), null, 0);
                if (CollectionUtil.isEmpty(charges)) {
                    return ResultVo.Success(vo);
                }
                List<ChargeStandardVO> results = Lists.newArrayList();
                for (ChargeStandardEntity charge : charges) {
                    results.add(new ChargeStandardVO(charge));
                }
                vo.setJsonStr(results);
                return ResultVo.Success(vo);
            } catch (BusinessException e) {
                return ResultVo.Fail(e.getMessage());
            }
        }
        if (Objects.equals(entity.getProdType(), ProductTypeEnum.INDIVIDUAL_CANCEL.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.INDEPENDENTLY_CANCEL.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.LIMITED_PARTNER_CANCEL.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_CANCEL.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.INDIVIDUAL_RENEWALS.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.INDEPENDENTLY_RENEWALS.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.LIMITED_PARTNER_RENEWALS.getValue())
                || Objects.equals(entity.getProdType(), ProductTypeEnum.LIMITED_LIABILITY_RENEWALS.getValue())) {
            if (CollectionUtil.isNotEmpty(productByParkByProductIdList)){
                List<ProductAndParkDTO> productByParkList = new ArrayList<>();
                for (ProductByParkVO ProductByParkvo:productByParkByProductIdList){
                    ProductAndParkDTO parkDTO = new ProductAndParkDTO();
                    parkDTO.setParkId(ProductByParkvo.getParkId());
                    parkDTO.setProdParkAmount(new BigDecimal(ProductByParkvo.getProdAmount()));
                    if (ProductByParkvo.getCancelTotalLimit() != null){
                        parkDTO.setCancelTotalLimit(new BigDecimal(ProductByParkvo.getCancelTotalLimit()));
                    }
                    productByParkList.add(parkDTO);
                }
                if (CollectionUtil.isNotEmpty(productByParkList)){
                    vo.setProductByParkList(productByParkList);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(vo.getParkIds())){
            List<ProductParkAgreementTemplateVO> list = new ArrayList<>();
            ProductParkAgreementTemplateVO productParkAgreementTemplateVO = null;
            ProductParkRelaEntity productParkRelaEntity = null;
            for (Long parkId:vo.getParkIds()){
                productParkAgreementTemplateVO  = new ProductParkAgreementTemplateVO();
                //设置注册流程标记
                productParkRelaEntity = new ProductParkRelaEntity();
                productParkRelaEntity.setParkId(parkId);
                productParkRelaEntity.setProductId(id);
                productParkRelaEntity = productParkRelaService.selectOne(productParkRelaEntity);
                if(productParkRelaEntity==null){
                    continue;
                }
                productParkAgreementTemplateVO.setProcessMark(productParkRelaEntity.getProcessMark());
                productParkAgreementTemplateVO.setParkId(parkId);
                List<AgreementTemplateInfoVO> agreementTemplateInfoVOList = parkAgreementTemplateRelaService.getTemplateInfoByParkIdAndProductId(parkId,id);
                if (CollectionUtil.isNotEmpty(agreementTemplateInfoVOList)){
                    ParkEntity parkEntity = parkService.findById(parkId);
                    productParkAgreementTemplateVO.setParkId(parkId);
                    productParkAgreementTemplateVO.setParkName(parkEntity.getParkName());
                    productParkAgreementTemplateVO.setAgreementTemplateInfoVOList(agreementTemplateInfoVOList);
                }else{
                    productParkAgreementTemplateVO.setAgreementTemplateInfoVOList(null);
                }
                list.add(productParkAgreementTemplateVO);
            }
            vo.setTemplateVOList(list);
        }
        return ResultVo.Success(vo);
    }

    @ApiOperation("产品修改")
    @PostMapping("edit")
    public ResultVo edit(@RequestBody @Validated(Update.class) ProductDTO dto, BindingResult result) {
        CurrUser currUser = getCurrUser();
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        ProductEntity entity = productService.findById(dto.getId());
        if (entity == null) {
            return ResultVo.Fail(ResultConstants.PRODUCT_NOT_EXISTS.getRetMsg());
        }
        if (Objects.equals(ProductStatusEnum.OFF_SHELF.getValue(), entity.getStatus())) {
            return ResultVo.Fail("已下架产品不支持编辑");
        }
        dto.setOemCode(entity.getOemCode());
        dto.setProdType(entity.getProdType());
        dto.setProdCode(entity.getProdCode());
        dto.setCompanyType(entity.getCompanyType());
        try {
            //校验数据信息
            validData(dto, userEntity);

            dto.setOperationTime(new Date());
            dto.setOperator(currUser.getUseraccount());
            productService.editProduct(dto);
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error("编辑产品失败，请求参数：{}", JSONObject.toJSONString(dto));
            log.error(e.getMessage(), e);
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    @ApiOperation("产品删除")
    @PostMapping("delete")
    public ResultVo delete(@JsonParam Long id) {
        CurrUser currUser = getCurrUser();
        if (id == null) {
            return ResultVo.Fail("产品主键不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        ProductEntity entity = productService.findById(id);
        if (entity == null) {
            return ResultVo.Fail(ResultConstants.PRODUCT_NOT_EXISTS.getRetMsg());
        }
        if (!Objects.equals(ProductStatusEnum.STAY_SHELF.getValue(), entity.getStatus())) {
            return ResultVo.Fail("只有待上架产品可以删除");
        }
        productService.deleteProductById(id);
        return ResultVo.Success();
    }

}
