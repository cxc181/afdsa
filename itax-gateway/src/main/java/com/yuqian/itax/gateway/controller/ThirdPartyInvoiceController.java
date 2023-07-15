package com.yuqian.itax.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.service.OemAccessPartyService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.gateway.annotation.JsonParam;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.InvoiceorderGoodsdetailRelaEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.dto.ThirdPartyCreateInoiveIDTO;
import com.yuqian.itax.order.entity.dto.ThirdPartyInvoiceConfirmDTO;
import com.yuqian.itax.order.entity.dto.ThirdPartyQueryInoiveInfoDTO;
import com.yuqian.itax.order.entity.query.ThirdPartyQueryInoiveInfoQuery;
import com.yuqian.itax.order.entity.vo.OrderNoVO;
import com.yuqian.itax.order.entity.vo.ThirdPartyCreateInvoiceOrderVO;
import com.yuqian.itax.order.entity.vo.ThirdPartyQueryInoiveInfoVO;
import com.yuqian.itax.order.enums.InvoiceOrderStatusEnum;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.park.entity.vo.TaxRulesVatRateVO;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.vo.CompanyInvoiceCategoryJdVO;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.ImageUtils;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

/**
 * @Description 接入方开票API对外接口
 * @Author  Hz
 * @Date   2021/9/7
 */
@Api(tags = "接入方开票API")
@RestController
@Slf4j
@RequestMapping("/thirdPartyInvoice")
public class ThirdPartyInvoiceController extends BaseController {

    @Resource
    OrderService orderService;
    @Resource
    InvoiceOrderService invoiceOrderService;
    @Resource
    OemAccessPartyService oemAccessPartyService;
    @Resource
    MemberAccountService memberAccountService;
    @Resource
    MemberCompanyService memberCompanyService;
    @Autowired
    private OssService ossService;

    /**
     * 开票信息查询接口
     */
    @PostMapping("queryInvoiceInfo")
    public ResultVo queryInvoiceInfo(@RequestBody @Valid ThirdPartyQueryInoiveInfoQuery query,BindingResult results){
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        query.setAccessPartyCode(getRequestHeadParams("accessPartyCode"));
        query.setOemCode(getRequestHeadParams("oemCode"));
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(query.getCompanyId());
        if(memberCompanyEntity==null){
            return ResultVo.Fail("该企业不存在");
        }
        if(memberCompanyEntity.getStatus()==4||memberCompanyEntity.getOverdueStatus()==3||memberCompanyEntity.getStatus()==2||memberCompanyEntity.getStatus()==5||memberCompanyEntity.getStatus()==6){
            return ResultVo.Fail("该企业已注销/托管费已过期/禁用/注销中");
        }
        if(StringUtils.isBlank(memberCompanyEntity.getEin())){
            return ResultVo.Fail("该企业税号不存在");
        }
        ThirdPartyQueryInoiveInfoDTO thirdPartyQueryInoiveInfoDTO=orderService.queryThirdPartyInvoiceInfo(query);
        return ResultVo.Success(thirdPartyQueryInoiveInfoDTO);
    }

    /**
     * 根据税收分类编码查询企业是否可以开票
     */
    @PostMapping("checkCompanyTaxCodeByTaxCode")
    public ResultVo checkCompanyTaxCodeByTaxCode(@RequestBody JSONObject jsonObject){
        if(jsonObject == null){
            return ResultVo.Fail("必传参数不能为空");
        }
        if(!jsonObject.containsKey("taxCodes")){
            return ResultVo.Fail("税收分类编码不能为空");
        }
        if(!jsonObject.containsKey("companyId")){
            return ResultVo.Fail("企业id不能为空");
        }

        String taxCodes = jsonObject.getString("taxCodes");
        if(StringUtils.isBlank(taxCodes)){
            return ResultVo.Fail("税收分类编码格式错误");
        }
        String[] taxCodeArr = taxCodes.split(";");
        Set<String> taxCodeSet = new HashSet<>();
        for(int i = 0 ;i<taxCodeArr.length;i++){
            taxCodeSet.add(taxCodeArr[i]);
        }
        Map<String, Object> map = Maps.newHashMap();
        List<String> companyTaxCodes = memberCompanyService.findCompanyTaxCodeByCompanyId(jsonObject.getLong("companyId"));
        if(companyTaxCodes == null || companyTaxCodes.size() == 0){
            map.put("result", 0); // 是否可开票 0-否 1-是
            map.put("unusableCode", taxCodeSet);
            return ResultVo.Success(map);
        }

        taxCodeSet.removeAll(companyTaxCodes);
        if(taxCodeSet!=null && taxCodeSet.size()>0){
            map.put("result", 0); // 是否可开票 0-否 1-是
            map.put("unusableCode", taxCodeSet);
            return ResultVo.Success(map);
        }
        map.put("result", 1);
        return ResultVo.Success(map);
    }

    /**
     * 个体户接入方开票提交接口
     * @Author HZ
     * @Date 2021/09/7
     * @return orderNo开票订单号
     */
    @PostMapping("createInvoiceOrderByThirdParty")
    public ResultVo createInvoiceOrderByThirdParty(@RequestBody ThirdPartyCreateInoiveIDTO dto){
        dto.setAccessPartyCode(getRequestHeadParams("accessPartyCode"));
        dto.setOemCode(getRequestHeadParams("oemCode"));
        try{
            vaild(dto);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        try{
            vaildDto(dto);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        //做个sign防重复提交

        //接入方是否是用户的接入方
        OemAccessPartyEntity oemAccessPartyEntity=oemAccessPartyService.queryByAccessPartyCode(dto.getAccessPartyCode());
        if(oemAccessPartyEntity==null){
            return ResultVo.Fail("接入方不存在");
        }
        MemberAccountEntity memberAccountEntity=memberAccountService.findById(dto.getUserId());
        if(memberAccountEntity==null){
            return ResultVo.Fail("用户不存在");
        }
        if(!Objects.equals(memberAccountEntity.getAccessPartyId(),oemAccessPartyEntity.getId())){
            return ResultVo.Fail("操作用户不属于该接入方");
        }
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(dto.getCompanyId());
        if(memberCompanyEntity==null){
            return ResultVo.Fail("该企业不存在");
        }
        if(!Objects.equals(memberAccountEntity.getId(),memberCompanyEntity.getMemberId())){
            return ResultVo.Fail("操作用户不属于该企业");
        }
        if(!memberAccountEntity.getOemCode().equals(dto.getOemCode())){
            return ResultVo.Fail("操作用户不属于oem机构");
        }
        //是否存在未支付的订单
        List<OrderNoVO> unpaidList = invoiceOrderService.getUnpaidList(dto.getUserId(), dto.getOemCode(), dto.getCompanyId());
        if(CollectionUtil.isNotEmpty(unpaidList)){
            return ResultVo.Fail("存在未支付订单:"+unpaidList.get(0).getOrderNo());
        }

        //开票
        InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.createInvoiceOrderByThirdParty(dto,"接入方开票，操作人："+dto.getAccount());
        ThirdPartyCreateInvoiceOrderVO vo =new ThirdPartyCreateInvoiceOrderVO();
        vo.setOrderNo(invoiceOrderEntity.getOrderNo());
        vo.setPayAmount(invoiceOrderEntity.getVatFee()+invoiceOrderEntity.getSurcharge()+invoiceOrderEntity.getPersonalIncomeTax());
        vo.setServiceFee(invoiceOrderEntity.getServiceFee());
        vo.setPostageFees(invoiceOrderEntity.getPostageFees());
        vo.setExternalOrderNo(dto.getExternalOrderNo());
        return ResultVo.Success(vo);
    }
    private void vaildDto( ThirdPartyCreateInoiveIDTO dto){
        ThirdPartyQueryInoiveInfoQuery query =new ThirdPartyQueryInoiveInfoQuery();
        query.setOemCode(dto.getOemCode());
        query.setAccessPartyCode(dto.getAccessPartyCode());
        query.setCompanyId(dto.getCompanyId());
        query.setUserId(dto.getUserId());
        ThirdPartyQueryInoiveInfoDTO thirdPartyQueryInoiveInfoDTO=orderService.queryThirdPartyInvoiceInfo(query);
        boolean flag=false;
        for(TaxRulesVatRateVO vo:thirdPartyQueryInoiveInfoDTO.getRateList()){
            if(vo.getVatRate().compareTo(dto.getVatRate())==0){
                flag=true;
            }
        }
        if(!flag){
            throw  new BusinessException("增值税率数据非法");
        }
        if(!thirdPartyQueryInoiveInfoDTO.getInvoiceWayList().contains(dto.getInvoiceWay())){
            throw  new BusinessException("发票类型数据非法");
        }
        if(!thirdPartyQueryInoiveInfoDTO.getInvoiceTypeList().contains(dto.getInvoiceType())){
            throw  new BusinessException("开票类型数据非法");
        }
        if(StringUtils.isNotBlank(dto.getCategoryName())) { //开票类目不为空
            if (StringUtils.isNotBlank(dto.getGoodsDetails())) {
                throw new BusinessException("开票类目与商品明细需二选一");
            }
            boolean cateflag = false;
            for (CompanyInvoiceCategoryJdVO dvo : thirdPartyQueryInoiveInfoDTO.getCategoryList()) {
                if (dvo.getCategoryName().equals(dto.getCategoryName())) {
                    cateflag = true;
                }
            }
            if (!cateflag) {
                throw new BusinessException("开票类目数据非法");
            }
        }else if(StringUtils.isNotBlank(dto.getGoodsDetails())){ //商品明细不为空
            List<String> companyTaxCodes = thirdPartyQueryInoiveInfoDTO.getTaxCodes();
            if(companyTaxCodes == null){
                throw new BusinessException("该企业没有可开票的税收分类编码，请联系客服确认");
            }
            try {
                List<InvoiceorderGoodsdetailRelaEntity> list = JSONObject.parseArray(dto.getGoodsDetails(),InvoiceorderGoodsdetailRelaEntity.class);
                if (list == null) {
                    throw new BusinessException("商品明细不能为空");
                }
                Set<String> taxCodeSet = new HashSet<>();
                list.forEach(vo->{
                    checkInvoiceorderGoodsdetail(vo);
                    taxCodeSet.add(vo.getTaxClassificationCode());
                });
                taxCodeSet.removeAll(companyTaxCodes);
                if(taxCodeSet!=null && taxCodeSet.size()>0){
                    throw new BusinessException("该企业不支持 "+ StringUtils.join(taxCodeSet.toArray(),",")+" 税收分类编码");
                }
            }catch (Exception e){
                log.info("商品明细数据格式错误：" + e.getMessage());
                throw new BusinessException("商品明细数据格式错误"+ e.getMessage());
            }
        }
        if(StringUtils.isNotBlank(dto.getInvoiceRemark())&&dto.getInvoiceRemark().length()>100){
            throw  new BusinessException("发票备注不能超过100个字符");
        }
        if(StringUtils.isNotBlank(dto.getRemark())&&dto.getRemark().length()>100){
            throw  new BusinessException("订单备注不能超过100个字符");
        }
    }
    private void vaild( ThirdPartyCreateInoiveIDTO dto){
        if(StringUtil.isBlank(dto.getOemCode())){
            throw  new BusinessException("oemCode不能为空");
        }
        if(StringUtil.isBlank(dto.getAccessPartyCode())){
            throw  new BusinessException("接入方编码不能为空");
        }
        if(dto.getCompanyId()==null){
            throw  new BusinessException("企业ID不能为空");
        }
        if(dto.getUserId()==null){
            throw  new BusinessException("用户ID不能为空");
        }
        if(dto.getVatRate()==null){
            throw  new BusinessException("增值税税率不能为空");
        }
        if(dto.getAmount()==null){
            throw  new BusinessException("开票金额不能为空");
        }
        if(dto.getAmount()<=0){
            throw  new BusinessException("开票金额不能小于等于0");
        }
        if(dto.getInvoiceWay()==null){
            throw  new BusinessException("发票类型不能为空");
        }
        if(dto.getInvoiceWay()!=1&&dto.getInvoiceWay()!=2&&dto.getInvoiceWay()!=3&&dto.getInvoiceWay()!=4){
            throw  new BusinessException("无效的发票类型");
        }
        if(dto.getInvoiceType()==null){
            throw  new BusinessException("开票类型不能为空");
        }
        if(dto.getInvoiceType()!=1&&dto.getInvoiceType()!=2){
            throw  new BusinessException("无效的开票类型");
        }
        if(StringUtil.isBlank(dto.getCategoryName()) && StringUtil.isBlank(dto.getGoodsDetails())){
            throw  new BusinessException("开票类目和开票商品明细不能同时为空");
        }
        if(StringUtil.isBlank(dto.getCompanyName())){
            throw  new BusinessException("发票抬头公司不能为空");
        }
        if(dto.getInvoiceType()==2&&StringUtil.isBlank(dto.getCompanyAddress())){
            throw  new BusinessException("发票抬头公司地址不能为空");
        }
        if(StringUtil.isBlank(dto.getEin())){
            throw  new BusinessException("发票抬头公司税号不能为空");
        }
        if(dto.getInvoiceType()==2&&StringUtil.isBlank(dto.getPhone())){
            throw  new BusinessException("发票抬头电话不能为空");
        }
        if(dto.getInvoiceType()==2&&StringUtil.isBlank(dto.getBankName())){
            throw  new BusinessException("发票抬头开户行不能为空");
        }
        if(dto.getInvoiceType()==2&&StringUtil.isBlank(dto.getBankNumber())){
            throw  new BusinessException("发票抬头银行账号不能为空");
        }
      if(dto.getInvoiceWay()==1&&StringUtil.isBlank(dto.getRecipient())){
            throw  new BusinessException("收件人不能为空");
        }
        if(dto.getInvoiceWay()==1&&StringUtil.isBlank(dto.getRecipientPhone())){
            throw  new BusinessException("收件人电话不能为空");
        }
        if(dto.getInvoiceWay()==1&&StringUtil.isNotBlank(dto.getRecipientPhone())){
            String regex = "^[0-9]{11}$";
            if( !dto.getRecipientPhone().matches(regex)){
                throw  new BusinessException("收件人电话格式不对");
            }
        }
        if(dto.getInvoiceWay()==1&&StringUtil.isBlank(dto.getRecipientAddress())){
            throw  new BusinessException("抬头详细地址不能为空");
        }
        if(dto.getInvoiceWay()==1&&StringUtil.isBlank(dto.getProvinceName())){
            throw  new BusinessException("省信息不能为空");
        }
        if(dto.getInvoiceWay()==1&&StringUtil.isBlank(dto.getCityName())){
            throw  new BusinessException("市信息不能为空");
        }
        if(dto.getInvoiceWay()==1&&StringUtil.isBlank(dto.getDistrictName())){
            throw  new BusinessException("区信息不能为空");
        }
        if(dto.getInvoiceWay()==2&&StringUtil.isBlank(dto.getEmail())){
            throw  new BusinessException("收票邮箱不能为空");
        }
        if (StringUtil.isBlank(dto.getExternalOrderNo())) {
            throw new BusinessException("业务来源单号不能为空");
        }
        if (StringUtil.isNotBlank(dto.getExternalOrderNo()) && dto.getExternalOrderNo().trim().length() > 32) {
            throw new BusinessException("业务来源单号限制长度为32个字符");
        }
        if (StringUtil.isBlank(dto.getEin())) {
            throw new BusinessException("企业税号不能为空");
        }
    }

    /**
     * 校验商品明细数据
     * @param entity
     * @return
     */
    private void checkInvoiceorderGoodsdetail(InvoiceorderGoodsdetailRelaEntity entity){
        if(StringUtils.isBlank(entity.getGoodsName())){
            throw new BusinessException("商品名称不能为空");
        }
        if(StringUtils.isBlank(entity.getTaxClassificationCode())){
            throw new BusinessException("税收分类编码不能为空");
        }
        if(entity.getGoodsQuantity() == null){
            throw new BusinessException("商品数量不能为空");
        }
        if(entity.getGoodsPrice() == null){
            throw new BusinessException("商品单价不能为空");
        }
        if(entity.getGoodsTotalPrice() == null){
            throw new BusinessException("总金额不能为空");
        }
//        Long totalPrice = entity.getGoodsQuantity().multiply(new BigDecimal(entity.getGoodsPrice())).setScale(0, BigDecimal.ROUND_UP).longValue();
//        if(Math.abs(totalPrice - entity.getGoodsTotalPrice())>1L){
//            throw new BusinessException("总金额不等于商品单价*商品数量");
//        }
//        if(entity.getGoodsTaxRate() == null){
//            throw new BusinessException("税率不能为空");
//        }
    }


    /**
     *  接入方开票确认
     *
     */
    @PostMapping("/thirdPartyInvoiceConfirm")
    public ResultVo thirdPartyInvoiceConfirm(@RequestBody@Valid ThirdPartyInvoiceConfirmDTO dto,BindingResult result){
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        if(dto.getConfirmResult() == null){
            return ResultVo.Fail("确认结果不能为空");
        }else if(dto.getConfirmResult() != 1 && dto.getConfirmResult()!=2){
            return ResultVo.Fail("确认结果不正确");
        }
        dto.setAccessPartyCode(getRequestHeadParams("accessPartyCode"));
        dto.setOemCode(getRequestHeadParams("oemCode"));
        OemAccessPartyEntity oemAccessPartyEntity=oemAccessPartyService.queryByAccessPartyCode(dto.getAccessPartyCode());
        if(oemAccessPartyEntity==null){
            return ResultVo.Fail("接入方不存在");
        }
        MemberAccountEntity memberAccountEntity=memberAccountService.findById(dto.getUserId());
        if(memberAccountEntity==null){
            return ResultVo.Fail("用户不存在");
        }
        if(!Objects.equals(memberAccountEntity.getAccessPartyId(),oemAccessPartyEntity.getId()) ){
            return ResultVo.Fail("操作用户不属于该接入方");
        }
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(dto.getCompanyId());
        if(memberCompanyEntity==null){
            return ResultVo.Fail("该企业不存在");
        }
        if(!Objects.equals(memberAccountEntity.getId(),memberCompanyEntity.getMemberId()) ){
            return ResultVo.Fail("操作用户不属于该企业");
        }
        if(!memberAccountEntity.getOemCode().equals(dto.getOemCode())){
            return ResultVo.Fail("操作用户不属于oem机构");
        }
        OrderEntity entity = orderService.queryByOrderNo(dto.getOrderNo());
        if(entity==null){
            return ResultVo.Fail("订单号不存在，请检查");
        }
        if(!Objects.equals(entity.getUserId(),memberAccountEntity.getId()) ){
            return ResultVo.Fail("订单号不属于操作用户");
        }
        if(6==entity.getOrderType()&&1==entity.getOrderStatus()){
                if(ObjectUtils.equals(1,dto.getConfirmResult())) { //确认订单
                    if(StringUtils.isBlank(dto.getPaymentVoucher())){
                        return ResultVo.Fail("支付凭证不能为空");
                    }
                    invoiceOrderService.invoiceConfirmGatewaySuccess(dto.getOrderNo(),dto.getPaymentVoucher(), "接入方开票确认出票中","接入方："+dto.getAccessPartyCode());
                }else if (ObjectUtils.equals(2,dto.getConfirmResult())){ //取消订单
                    invoiceOrderService.invoiceConfirmGateway(dto.getOrderNo(),"接入方开票确认取消订单","接入方："+dto.getAccessPartyCode());
                }
        }else{
            return ResultVo.Fail("DQ0001", "订单已确认，请勿重复操作");
        }

        return ResultVo.Success();
    }

    /**
     *  接入方开票取消
     *
     */
    @PostMapping("/thirdPartyInvoiceCancel")
    public ResultVo thirdPartyInvoiceCancel( @RequestBody@Valid ThirdPartyInvoiceConfirmDTO dto,BindingResult result){
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        dto.setAccessPartyCode(getRequestHeadParams("accessPartyCode"));
        dto.setOemCode(getRequestHeadParams("oemCode"));
        OemAccessPartyEntity oemAccessPartyEntity=oemAccessPartyService.queryByAccessPartyCode(dto.getAccessPartyCode());
        if(oemAccessPartyEntity==null){
            return ResultVo.Fail("接入方不存在");
        }
        MemberAccountEntity memberAccountEntity=memberAccountService.findById(dto.getUserId());
        if(memberAccountEntity==null){
            return ResultVo.Fail("用户不存在");
        }
        if(!Objects.equals(memberAccountEntity.getAccessPartyId(),oemAccessPartyEntity.getId()) ){
            return ResultVo.Fail("操作用户不属于该接入方");
        }
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(dto.getCompanyId());
        if(memberCompanyEntity==null){
            return ResultVo.Fail("该企业不存在");
        }
        if(!Objects.equals(memberAccountEntity.getId(),memberCompanyEntity.getMemberId()) ){
            return ResultVo.Fail("操作用户不属于该企业");
        }
        if(!memberAccountEntity.getOemCode().equals(dto.getOemCode())){
            return ResultVo.Fail("操作用户不属于oem机构");
        }
        OrderEntity entity = orderService.queryByOrderNo(dto.getOrderNo());
        if(entity==null){
            return ResultVo.Fail("订单号不存在，请检查");
        }
        if(!Objects.equals(entity.getUserId(),memberAccountEntity.getId()) ){
            return ResultVo.Fail("订单号不属于操作用户");
        }
        if(6==entity.getOrderType()&&1==entity.getOrderStatus()){
            invoiceOrderService.invoiceConfirmGateway(dto.getOrderNo(),"接入方开票确认取消订单","接入方："+dto.getAccessPartyCode());
        }else{
            return ResultVo.Fail("订单状态不为待出款，无法取消订单");
        }

        return ResultVo.Success();
    }

    /**
     *  接入方开票签收
     *
     */
    @PostMapping("/thirdPartyInvoiceComplete")
    public ResultVo thirdPartyInvoiceComplete( @RequestBody@Valid ThirdPartyInvoiceConfirmDTO dto,BindingResult result){
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        dto.setAccessPartyCode(getRequestHeadParams("accessPartyCode"));
        dto.setOemCode(getRequestHeadParams("oemCode"));
        OemAccessPartyEntity oemAccessPartyEntity=oemAccessPartyService.queryByAccessPartyCode(dto.getAccessPartyCode());
        if(oemAccessPartyEntity==null){
            return ResultVo.Fail("接入方不存在");
        }
        MemberAccountEntity memberAccountEntity=memberAccountService.findById(dto.getUserId());
        if(memberAccountEntity==null){
            return ResultVo.Fail("用户不存在");
        }
        if(!Objects.equals(memberAccountEntity.getAccessPartyId(),oemAccessPartyEntity.getId()) ){
            return ResultVo.Fail("操作用户不属于该接入方");
        }
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(dto.getCompanyId());
        if(memberCompanyEntity==null){
            return ResultVo.Fail("该企业不存在");
        }
        if(!Objects.equals(memberAccountEntity.getId(),memberCompanyEntity.getMemberId()) ){
            return ResultVo.Fail("操作用户不属于该企业");
        }
        if(!memberAccountEntity.getOemCode().equals(dto.getOemCode())){
            return ResultVo.Fail("操作用户不属于oem机构");
        }
        OrderEntity entity = orderService.queryByOrderNo(dto.getOrderNo());
        if(entity==null){
            return ResultVo.Fail("订单号不存在，请检查");
        }
        if(!Objects.equals(entity.getUserId(),memberAccountEntity.getId()) ){
            return ResultVo.Fail("订单号不属于操作用户");
        }
        if(6==entity.getOrderType()&&6==entity.getOrderStatus()){
            invoiceOrderService.thirdPartyInvoiceComplete(entity,"接入方："+dto.getAccessPartyCode(), memberAccountEntity.getAccessPartyId());
        }else{
            return ResultVo.Fail("订单状态不是" + InvoiceOrderStatusEnum.TO_BE_RECEIVED.getMessage()+",不能进行签收");
        }

        return ResultVo.Success();
    }

    /**
     * 开票订单查询接口
     */
    @ApiImplicitParam(name = "orderNo", value = "订单编号", required = true, dataType = "string")
    @PostMapping("/thirdPartyQueryInvoice")
    public ResultVo thirdPartyQueryInvoice(@JsonParam String orderNo, @JsonParam String externalOrderNo) {

        if (StringUtil.isNotBlank(orderNo) && StringUtil.isNotBlank(externalOrderNo)) {
            if (externalOrderNo.length() > 32) {
                throw new BusinessException("业务来源单号限长32个字符");
            }
            OrderEntity orderEntity = Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单信息"));
            if (!externalOrderNo.equals(orderEntity.getExternalOrderNo())) {
                throw new BusinessException("订单号与业务来源单号不匹配");
            }
        }
        ThirdPartyQueryInoiveInfoVO vo = invoiceOrderService.thirdPartyQueryInvoiceDetail(orderNo, externalOrderNo, getRequestHeadParams("accessPartyCode"));

        // 发票图片转base64
        if (null == vo) {
            return ResultVo.Success(new ThirdPartyQueryInoiveInfoVO());
        }
        if (StringUtil.isBlank(vo.getInvoiceImgs())) {
            return ResultVo.Success(vo);
        }
        String[] imgList = vo.getInvoiceImgs().split(",");
        List<String> invImgList = Lists.newArrayList();
        for (String s : imgList) {
            String img = ImageUtils.netImageToBase64(ossService.getPrivateImgUrl(s));
            if (StringUtil.isBlank(img)) {
                throw new BusinessException("发票图片不存在");
            }
            if(img.contains("\r\n")){
                img = img.replace("\r\n","");
            }else if(img.contains("\r")){
                img = img.replace("\r","");
            }else if(img.contains("\n")){
                img = img.replace("\n","");
            }
            String suffix = s.substring(s.lastIndexOf(".") + 1);
            invImgList.add("data:image/"+suffix + ";base64," + img);
        }
        vo.setInvImgList(invImgList);
        return ResultVo.Success(vo);
    }

}
