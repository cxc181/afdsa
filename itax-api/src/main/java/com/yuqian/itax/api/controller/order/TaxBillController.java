package com.yuqian.itax.api.controller.order;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.vo.RecoverableTaxVO;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.park.enums.IncomeLevyTypeEnum;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.CompanyTaxCostItemEntity;
import com.yuqian.itax.tax.entity.ParkTaxBillChangeEntity;
import com.yuqian.itax.tax.entity.dto.FillCostDTO;
import com.yuqian.itax.tax.entity.dto.SignConfirmDTO;
import com.yuqian.itax.tax.entity.query.CompanyTaxBillQuery;
import com.yuqian.itax.tax.entity.query.PendingTaxBillQuery;
import com.yuqian.itax.tax.entity.query.PrepareCompanyTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.*;
import com.yuqian.itax.tax.enums.ParkTaxBillStatusEnum;
import com.yuqian.itax.tax.enums.TaxBillStatusEnum;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.tax.service.CompanyTaxCostItemService;
import com.yuqian.itax.tax.service.ParkTaxBillChangeService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 企业税单Controller
 * @author：pengwei
 * @Date：2020/12/3 12:46
 * @version：1.0
 */
@RestController
@RequestMapping("tax/bill")
@Slf4j
public class TaxBillController extends BaseController {

    @Autowired
    private CompanyTaxBillService companyTaxBillService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OssService ossService;

    @Autowired
    private ParkTaxBillChangeService parkTaxBillChangeService;

    @Autowired
    private CompanyTaxCostItemService companyTaxCostItemService;

    /**
     * 税单列表
     * @param query
     * @param result
     * @return
     */
    @PostMapping("page")
    public ResultVo listPage(@RequestBody @Validated CompanyTaxBillQuery query, BindingResult result) {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        query.setMemberId(getCurrUserId());
        query.setOemCode(getRequestHeadParams("oemCode"));
        String overtime = dictionaryService.getValueByCode("tax_bill_overtime");
        query.setOverTime(Integer.valueOf(overtime));
        PageInfo<CompanyTaxBillListVO> pages = companyTaxBillService.listPageCompanyTaxBill(query);
        return ResultVo.Success(pages);
    }

    /**
     * 统计待处理数量
     * @param query
     * @param result
     * @return
     */
    @PostMapping("count/pending")
    public ResultVo countPending(@RequestBody @Validated CompanyTaxBillQuery query, BindingResult result) {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        int pendingSize = 0;
        query.setMemberId(getCurrUserId());
        query.setOemCode(getRequestHeadParams("oemCode"));
        query.setTaxBillType(2);
        List<CompanyTaxBillListVO> list = companyTaxBillService.listCompanyTaxBill(query);
        if (CollectionUtil.isNotEmpty(list)) {
            pendingSize = list.size();
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("pendingSize", pendingSize);
        return ResultVo.Success(map);
    }

    /**
     * 税单详情
     * @param companyTaxBillId 企业税单id
     * @return
     */
    @PostMapping("detail")
    public ResultVo detail(@JsonParam Long companyTaxBillId) {
        if (companyTaxBillId == null) {
            return ResultVo.Fail("企业税单主键不能为空");
        }
        CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(companyTaxBillId);
        if (companyTaxBillEntity == null) {
            return ResultVo.Fail("企业税单不存在");
        }
        MemberCompanyEntity companyEntity = memberCompanyService.findById(companyTaxBillEntity.getCompanyId());
        if (companyEntity == null) {
            return ResultVo.Fail("税单对应的企业不存在");
        }
        CompanyTaxBillVO vo = new CompanyTaxBillVO(companyTaxBillEntity, companyEntity);
        // 查账征收方式的税单，所得税需要使用年度数据
        if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
            if (TaxBillStatusEnum.TO_FINANCIAL_AUDIT.getValue().equals(vo.getTaxBillStatus())
                    || TaxBillStatusEnum.AUDIT_FAILED.getValue().equals(vo.getTaxBillStatus())) {
                vo.setFrozenTax(vo.getFrozenTax() + vo.getIncomeTaxRefundAmount());
                vo.setIncomeTaxRefundAmount(0L);
            }
            companyTaxBillService.detailOfAuditCollection(vo);
        }
        // 查询是否有税单推送记录
        if (null != companyTaxBillEntity.getParkTaxBillId()) {
            ParkTaxBillChangeEntity changeEntity = new ParkTaxBillChangeEntity();
            changeEntity.setParkBillsId(companyTaxBillEntity.getParkTaxBillId());
            changeEntity.setTaxBillStatus(ParkTaxBillStatusEnum.PUSH.getValue());
            List<ParkTaxBillChangeEntity> taxBillChangeEntities = parkTaxBillChangeService.select(changeEntity);
            if (CollectionUtil.isEmpty(taxBillChangeEntities)) {
                return ResultVo.Fail("未查询到税单推送记录");
            }
            taxBillChangeEntities = taxBillChangeEntities.stream().sorted(Comparator.comparing(ParkTaxBillChangeEntity::getAddTime).reversed()).collect(Collectors.toList());
            vo.setCreateTime(DateUtil.formatDefaultDate(taxBillChangeEntities.get(0).getAddTime()));
        }
        return ResultVo.Success(vo);
    }

    /**
     * 税单补缴
     * @param companyTaxBillId 企业税单id
     * @return
     */
    @PostMapping("payment")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.TAX_TO_BE_PAID_LOCK, lockTime = 10)
    public ResultVo payment(@JsonParam Long companyTaxBillId) {
        if (companyTaxBillId == null) {
            return ResultVo.Fail("企业税单主键不能为空");
        }
        CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(companyTaxBillId);
        if (companyTaxBillEntity == null) {
            return ResultVo.Fail("企业税单不存在");
        }
        if (!Objects.equals(companyTaxBillEntity.getTaxBillStatus(), TaxBillStatusEnum.TAX_TO_BE_PAID.getValue())) {
            return ResultVo.Fail("当前税单不是待补税税单");
        }
        String oemCode = getRequestHeadParams("oemCode");
        String sourceType = getRequestHeadParams("sourceType");
        OrderEntity orderEntity = orderService.saveTaxSupplement(oemCode, getCurrUserId(), sourceType, companyTaxBillEntity);
        return ResultVo.Success(new RecoverableTaxVO(orderEntity));
    }

    /**
     * 退税
     * @param companyTaxBillId 企业税单id
     * @return
     */
    @PostMapping("refund")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.TAX_TO_BE_REFUNDED_LOCK, lockTime = 10)
    public ResultVo refund(@JsonParam Long companyTaxBillId) {
        if (companyTaxBillId == null) {
            return ResultVo.Fail("企业税单主键不能为空");
        }
        CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(companyTaxBillId);
        if (companyTaxBillEntity == null) {
            return ResultVo.Fail("企业税单不存在");
        }
        if (!Objects.equals(companyTaxBillEntity.getTaxBillStatus(), TaxBillStatusEnum.TAX_TO_BE_REFUNDED.getValue())) {
            return ResultVo.Fail("当前税单不是待退税税单");
        }
        String oemCode = getRequestHeadParams("oemCode");
        Long userId = getCurrUserId();
        MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(companyTaxBillEntity.getCompanyId());
        if (memberCompanyEntity == null) {
            return ResultVo.Fail("我的企业不存在");
        }
        if (!Objects.equals(memberCompanyEntity.getMemberId(), userId)) {
            return ResultVo.Fail("当前企业不是登录用户的企业");
        }
        String sourceType = getRequestHeadParams("sourceType");
        orderService.saveTaxRefund(oemCode, userId, sourceType, companyTaxBillEntity);
        return ResultVo.Success();
    }


    /**
     * 获取完税凭证
     * @param companyTaxBillId 企业税单id
     * @return
     */
    @PostMapping("query/vouchers/pic")
    public ResultVo queryVouchersPic(@JsonParam Long companyTaxBillId) {
        if (companyTaxBillId == null) {
            return ResultVo.Fail("企业税单主键不能为空");
        }
        CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(companyTaxBillId);
        if (companyTaxBillEntity == null) {
            return ResultVo.Fail("企业税单不存在");
        }
        List<CompanyTaxBillVouchersVO> list = Lists.newArrayList();
        CompanyTaxBillVouchersVO vo;
        if (StringUtils.isNotBlank(companyTaxBillEntity.getIitVoucherPic())) {
            vo = new CompanyTaxBillVouchersVO();
            vo.setName("个人所得税凭证图片");
            vo.setPicUrl(ossService.getPrivateVideoUrl(companyTaxBillEntity.getIitVoucherPic()));
            list.add(vo);
        }
        if (StringUtils.isNotBlank(companyTaxBillEntity.getVatVoucherPic())) {
            vo = new CompanyTaxBillVouchersVO();
            vo.setName("增值税凭证图片");
            vo.setPicUrl(ossService.getPrivateVideoUrl(companyTaxBillEntity.getVatVoucherPic()));
            list.add(vo);
        }
        if (StringUtils.isNotBlank(companyTaxBillEntity.getTicketPic())) {
            vo = new CompanyTaxBillVouchersVO();
            vo.setName("罚款凭证图片");
            vo.setPicUrl(ossService.getPrivateVideoUrl(companyTaxBillEntity.getTicketPic()));
            list.add(vo);
        }
        return ResultVo.Success(list);
    }

    /**
     * 预税单列表
     * @param query
     * @return
     */
    @PostMapping("pre/page")
    public ResultVo prePage(@RequestBody PrepareCompanyTaxBillQuery query) {
        query.setMemberId(getCurrUserId());
        PageInfo<CompanyTaxBillPrepareListVO> pages = companyTaxBillService.listPagePrepareCompanyTaxBill(query);
        return ResultVo.Success(pages);
    }

    /**
     * 预税单详情
     * @param companyId 企业id
     * @return
     */
    @PostMapping("pre/detail")
    public ResultVo preDetail(@JsonParam Long companyId) {
        if (companyId == null) {
            return ResultVo.Fail("预税单企业id不能为空");
        }
        CompanyTaxBillPrepareVO vo = companyTaxBillService.prePareDetail(companyId, getCurrUserId());
        return ResultVo.Success(vo);
    }

    /**
     * 获取填报成本详情
     * @param companyTaxBillId
     * @return
     */
    @PostMapping("getFillCostDetail")
    public ResultVo getFillCostDetail(@JsonParam Long companyTaxBillId) {
        if (null == companyTaxBillId) {
            ResultVo.Fail("税单id不能为空");
        }
        TaxBillFillCostDetailVO vo = companyTaxBillService.getFillCostDetail(companyTaxBillId);
        Map<String, Object> map = Maps.newHashMap();
        map.put("companyName", vo.getCompanyName());
        map.put("operatorName", vo.getOperatorName());
        map.put("taxBillTime", vo.getTaxBillTime());
        map.put("yearInvoiceAmount", vo.getYearInvoiceAmount());
        map.put("historyCostAmount", vo.getHistoryCostAmount());
        return ResultVo.Success(map);
    }

    /**
     * 根据企业税单获取本年历史成本汇总
     * @param companyTaxBillId
     * @return
     */
    @PostMapping("getCostItemByCompanyBillsYear")
    public ResultVo getCostItemByCompanyBillsYear(@JsonParam Long companyTaxBillId) {
        if (null == companyTaxBillId) {
            ResultVo.Fail("税单id不能为空");
        }
        TaxBilHistoryCostItemVO vo = companyTaxBillService.getCostItemByCompanyBillsYear(companyTaxBillId);
        return ResultVo.Success(vo);
    }

    /**
     * 填报成本
     * @param dto
     * @return
     */
    @PostMapping("/fillCost")
    @Transactional(rollbackFor = Exception.class)
    public ResultVo confirmDetail(@RequestBody @Validated FillCostDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(dto.getCompanyTaxBillId());
        if (companyTaxBillEntity == null) {
            return ResultVo.Fail("企业税单不存在");
        }
        // 校验企业税单
        if (!TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(companyTaxBillEntity.getTaxBillStatus())) {
            return ResultVo.Fail("税单状态已变化，请从税单列表重新进入");
        }
        MemberCompanyEntity company = memberCompanyService.findById(companyTaxBillEntity.getCompanyId());
        if (null == company) {
            return ResultVo.Fail("未查询到企业信息");
        }
        if (MemberCompanyStatusEnum.PROHIBIT.getValue().equals(company.getStatus())) {
            return ResultVo.Fail("企业已冻结，暂不支持填报成本");
        }
        // 填报成本
        dto.setOperator(getCurrUseraccount());
        dto.setSourceOfOperating(1);
        companyTaxBillEntity = companyTaxBillService.fillCost(dto);
        TaxBillConfirmDetailVO vo = new TaxBillConfirmDetailVO(companyTaxBillEntity, company);
        // 查账征收方式的税单，所得税需要使用年度数据
        if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
            CompanyTaxBillVO companyTaxBillVO = new CompanyTaxBillVO();
            companyTaxBillVO.setCompanyTaxBillId(companyTaxBillEntity.getId());
            companyTaxBillService.detailOfAuditCollection(companyTaxBillVO);
            vo.setAlreadyTaxMoney(companyTaxBillVO.getAlreadyTaxMoney());
            vo.setShouldTaxMoney(companyTaxBillVO.getShouldTaxMoney());
            vo.setSupplementTaxMoney(companyTaxBillVO.getSupplementTaxMoney());
            vo.setRecoverableTaxMoney(companyTaxBillVO.getRecoverableTaxMoney());
        }
        return ResultVo.Success(vo);
    }

    /**
     * 签名确认税单
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("/signConfirm")
    public ResultVo signConfirm(@RequestBody @Validated SignConfirmDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        String currUseraccount = getCurrUseraccount();
        CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(dto.getCompanyTaxBillId());
        if (companyTaxBillEntity == null) {
            return ResultVo.Fail("企业税单不存在");
        }
        // 校验企业税单
        if (!TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(companyTaxBillEntity.getTaxBillStatus())) {
            return ResultVo.Fail("税单状态已变化，请从税单列表重新进入");
        }
        int status;
        // 判断是否需要补税
        Long supplementTaxMoney = companyTaxBillEntity.getSupplementTaxMoney();
        // 查账征收方式的税单，所得税需要使用年度数据
        if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(companyTaxBillEntity.getIncomeLevyType())) {
            CompanyTaxBillVO companyTaxBillVO = new CompanyTaxBillVO();
            companyTaxBillVO.setCompanyTaxBillId(companyTaxBillEntity.getId());
            companyTaxBillService.detailOfAuditCollection(companyTaxBillVO);
            supplementTaxMoney = companyTaxBillVO.getSupplementTaxMoney();
        }
        if (supplementTaxMoney > 0L) {
            status = TaxBillStatusEnum.TAX_TO_BE_PAID.getValue();
        } else {
            status = TaxBillStatusEnum.TO_BE_DECLARE.getValue();
        }
        companyTaxBillEntity.setTaxBillStatus(status);
        companyTaxBillEntity.setSignImg(dto.getSignImg());
        companyTaxBillEntity.setCostItemImgs(dto.getCostItemImgs());
        companyTaxBillEntity.setUpdateUser(currUseraccount);
        companyTaxBillEntity.setUpdateTime(new Date());
        // 更新税单
        try {
            companyTaxBillService.updateBillAndInsertChange(companyTaxBillEntity,"用户填报成本并签字");
        } catch (Exception e) {
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 成本明细
     * @param companyTaxBillId
     * @return
     */
    @PostMapping("/costDetail")
    public ResultVo costDetail(@JsonParam Long companyTaxBillId) {
        if (companyTaxBillId == null) {
            return ResultVo.Fail("企业税单主键不能为空");
        }
        CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(companyTaxBillId);
        if (companyTaxBillEntity == null) {
            return ResultVo.Fail("企业税单不存在");
        }
        TaxBillCostItemDetailVO vo = new TaxBillCostItemDetailVO();
        // 查询税单成本项
        CompanyTaxCostItemEntity entity = new CompanyTaxCostItemEntity();
        entity.setCompanyTaxId(companyTaxBillId);
        List<CompanyTaxCostItemEntity> costItemEntityList = companyTaxCostItemService.select(entity);
        if (CollectionUtil.isEmpty(costItemEntityList)) {
            return ResultVo.Success(vo);
        }
        vo.setCostItemEntityList(costItemEntityList);
        // 查询成本项票据图片
        if (StringUtil.isBlank(companyTaxBillEntity.getCostItemImgs())) {
            return ResultVo.Success(vo);
        }
        List<String> costItemImgList = Lists.newArrayList();
        JSONArray imgList = JSONObject.parseArray(companyTaxBillEntity.getCostItemImgs());
        for (Object img : imgList) {
            costItemImgList.add(ossService.getPrivateVideoUrl(img.toString()));
        }
        vo.setCostItemImgList(costItemImgList);
        return ResultVo.Success(vo);
    }

    /**
     * 待确认成本税单查询
     * @param companyId
     * @return
     */
    @PostMapping("/confirmCostTaxBill")
    public ResultVo confirmCostTaxBill(@JsonParam Long companyId, @JsonParam Long companyTaxId) {
        if (null == companyId) {
            return ResultVo.Fail("企业id不能为空");
        }
        Map<String, Integer> map = Maps.newHashMap();
        // 查询企业
        MemberCompanyEntity company = memberCompanyService.findById(companyId);
        if (null == company) {
            return ResultVo.Fail("未查询到企业信息");
        }
        if (null != companyTaxId) {
            // 查询企业税单
            CompanyTaxBillEntity taxBill = companyTaxBillService.findById(companyTaxId);
            if (null == taxBill) {
                return ResultVo.Fail("未查询到企业税单");
            }
            // 校验是否有更早的未确认成本的税单
            Example example = new Example(CompanyTaxBillEntity.class);
            example.createCriteria().andEqualTo("companyId", company.getId())
                    .andIn("taxBillStatus", Arrays.asList(2,7));
            List<CompanyTaxBillEntity> list = companyTaxBillService.selectByExample(example);
            if (CollectionUtil.isNotEmpty(list)) {
                List<CompanyTaxBillEntity> collect = list.stream()
                        .filter(x -> x.getTaxBillYear().compareTo(taxBill.getTaxBillYear()) < 0
                                || (x.getTaxBillYear().compareTo(taxBill.getTaxBillYear()) == 0
                                && x.getTaxBillSeasonal().compareTo(taxBill.getTaxBillSeasonal()) < 0)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect)) {
                    map.put("isPioneerTax", 0);
                } else {
                    map.put("isPioneerTax", 1);
                }
            }
        }
        PendingTaxBillQuery query = new PendingTaxBillQuery();
        query.setEin(company.getEin());
        query.setCompanyId(companyId);
        query.setStatusRange(1);
        List<PendingTaxBillVO> list = companyTaxBillService.pendingTaxBill(query);
        if (CollectionUtil.isEmpty(list)) {
            map.put("taxToBePaid", 0);
            map.put("toBeWriteCost", 0);
            map.put("isOvertime", 0);
            return ResultVo.Success(map);
        }
        // 是否超时未处理
        List<PendingTaxBillVO> isOvertimes = list.stream().filter(x -> x.getTimeDifference() < 0).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(isOvertimes)) {
            map.put("isOvertime", 1);
        } else {
            map.put("isOvertime", 0);
        }
        // 待补税订单
        List<PendingTaxBillVO> taxToBePaid = list.stream().filter(x -> TaxBillStatusEnum.TAX_TO_BE_PAID.getValue().equals(x.getTaxBillStatus())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(taxToBePaid)) {
            map.put("taxToBePaid", 1);
        } else {
            map.put("taxToBePaid", 0);
        }
        // 待填报成本
        List<PendingTaxBillVO> toBeWriteCost = list.stream().filter(x -> TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(x.getTaxBillStatus())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(toBeWriteCost)) {
            map.put("toBeWriteCost", 1);
        } else {
            map.put("toBeWriteCost", 0);
        }
        return ResultVo.Success(map);
    }

    /**
     * 校验是否为最早未处理税单
     * @param companyTaxId
     * @return
     */
    @PostMapping("/checkPioneerTax")
    public ResultVo checkPioneerTax(@JsonParam Long companyTaxId) {
        if (null == companyTaxId) {
            return ResultVo.Fail("企业税单id不能为空");
        }
        Map<String, Integer> map = Maps.newHashMap();

        // 查询企业税单
        CompanyTaxBillEntity taxBill = companyTaxBillService.findById(companyTaxId);
        if (null == taxBill) {
            return ResultVo.Fail("未查询到企业税单");
        }
        // 校验是否有更早的未确认成本的税单
        Example example = new Example(CompanyTaxBillEntity.class);
        example.createCriteria().andEqualTo("companyId", taxBill.getCompanyId())
                .andIn("taxBillStatus", Arrays.asList(2, 7));
        List<CompanyTaxBillEntity> list = companyTaxBillService.selectByExample(example);
        if (CollectionUtil.isNotEmpty(list)) {
            List<CompanyTaxBillEntity> collect = list.stream()
                    .filter(x -> x.getTaxBillYear().compareTo(taxBill.getTaxBillYear()) < 0
                            || (x.getTaxBillYear().compareTo(taxBill.getTaxBillYear()) == 0
                            && x.getTaxBillSeasonal().compareTo(taxBill.getTaxBillSeasonal()) < 0)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect)) {
                map.put("isPioneerTax", 0);
            } else {
                map.put("isPioneerTax", 1);
            }
        }

        return ResultVo.Success(map);
    }
}