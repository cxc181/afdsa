package com.yuqian.itax.admin.controller.order;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.enums.OemStatusEnum;
import com.yuqian.itax.agent.service.OemConfigService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.group.entity.GroupPaymentAnalysisRecordEntity;
import com.yuqian.itax.group.entity.InvoiceHeadGroupEntity;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import com.yuqian.itax.group.entity.dto.InvoiceOrderGroupDTO;
import com.yuqian.itax.group.entity.query.InvoiceOrderGroupQuery;
import com.yuqian.itax.group.entity.vo.InvoiceOrderGroupAuditVo;
import com.yuqian.itax.group.entity.vo.InvoiceOrderGroupListVO;
import com.yuqian.itax.group.enums.InvoiceOrderGroupStatusEnum;
import com.yuqian.itax.group.service.GroupPaymentAnalysisRecordService;
import com.yuqian.itax.group.service.InvoiceHeadGroupService;
import com.yuqian.itax.group.service.InvoiceOrderGroupService;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.InvoiceRecordEntity;
import com.yuqian.itax.order.entity.vo.InvoiceOrderByGroupOrderNoVO;
import com.yuqian.itax.order.enums.InvoiceRecordStatusEnum;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.InvoiceRecordService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.system.entity.InvoiceCategoryBaseEntity;
import com.yuqian.itax.system.service.InvoiceCategoryBaseService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.OrderNoFactory;
import com.yuqian.itax.util.validator.Add;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 集团开票控制器
 * @author：pengwei
 * @Date：2020/3/6 10:12
 * @version：1.0
 */
@RestController
@RequestMapping("invoice/group")
@Slf4j
public class InvoiceOrderGroupController extends BaseController {

    @Autowired
    private InvoiceOrderGroupService invoiceOrderGroupService;

    @Autowired
    private InvoiceCategoryBaseService invoiceCategoryBaseService;

    @Autowired
    private InvoiceHeadGroupService invoiceHeadGroupService;

    @Autowired
    private OemService oemService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GroupPaymentAnalysisRecordService groupPaymentAnalysisRecordService;

    @Autowired
    private InvoiceOrderService invoiceOrderService;

    @Autowired
    private OemConfigService oemConfigService;
    @Resource
    private  InvoiceRecordService invoiceRecordService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    OssService ossService;

    @ApiOperation("添加")
    @PostMapping("add")
    public ResultVo add(@RequestBody @Validated(Add.class) InvoiceOrderGroupDTO dto, BindingResult result){
        CurrUser currUser = getCurrUser();
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() == 1) {
            if (StringUtils.isBlank(dto.getOemCode())) {
                return ResultVo.Fail("机构编码不能为空");
            }
        } else {
            dto.setOemCode(userEntity.getOemCode());
        }
        OemEntity oem = oemService.getOem(dto.getOemCode());
        if (oem == null) {
            return ResultVo.Fail("机构不存在");
        }
        if (!Objects.equals(oem.getOemStatus(), OemStatusEnum.YES.getValue())) {
            return ResultVo.Fail("当前OEM机构状态异常，无法处理！");
        }
        InvoiceCategoryBaseEntity categoryEntity = invoiceCategoryBaseService.findById(dto.getCategoryBaseId());
        if (categoryEntity == null) {
            return ResultVo.Fail("开票类目不存在");
        }
        if("*".equals(categoryEntity.getGoodsName())){
            return ResultVo.Fail("集团开票不允许选择商品名称带*得类目!");
        }

        InvoiceHeadGroupEntity headGroupEntity = invoiceHeadGroupService.findById(dto.getHeadGroupId());
        if (headGroupEntity == null) {
            return ResultVo.Fail("集团发票抬头不存在");
        }
        OemConfigEntity oemConfigEntity = new OemConfigEntity();
        oemConfigEntity.setOemCode(dto.getOemCode());
        oemConfigEntity.setParentParamsCode("vat_rate");
        oemConfigEntity.setParamsValue(dto.getVatFeeRate().toString());
        List<OemConfigEntity> list = oemConfigService.select(oemConfigEntity);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("当前集团不支持填写的增值税税率");
        }
        InvoiceOrderGroupEntity entity = dto.toEntity(categoryEntity, headGroupEntity, userEntity);
        entity.setOrderNo(OrderNoFactory.getGroupOrderCode(userEntity.getId()));
        entity.setEmail(headGroupEntity.getEmail());
        entity.setInvoiceWay(dto.getInvoiceWay());
        invoiceOrderGroupService.insertSelective(entity);
        //发送数据到解析mq队列
        JSONObject json = new JSONObject();
        json.put("groupId", entity.getId());
        rabbitTemplate.convertAndSend("createGroupPaymentAnalysisRecord", json);
        return ResultVo.Success();
    }

    @ApiOperation("列表页")
    @PostMapping("page")
    public ResultVo listPageProduct(@RequestBody InvoiceOrderGroupQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        //园区组织下的账号看不到数据
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            return ResultVo.Success(new PageInfo<InvoiceOrderGroupListVO>());
        }
        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
        PageInfo<InvoiceOrderGroupListVO> page = invoiceOrderGroupService.listPage(query);
        return ResultVo.Success(page);
    }

    @ApiOperation("财务审核页")
    @PostMapping("financialAudit")
    public ResultVo financialAudit(@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        InvoiceOrderGroupAuditVo vo= invoiceOrderGroupService.queryInvoiceOrderByOrderNo(orderNo);
        if (vo ==null || vo.getOrderStatus() != 4){
            return ResultVo.Fail("数据不正确");
        }
        if (vo.getVatFee() != null && vo.getSurcharge() != null && vo.getPersonalIncomeTax() != null){
            BigDecimal total = vo.getPersonalIncomeTax().add(vo.getSurcharge());
            vo.setTotal(total.add(vo.getVatFee()));
        }
        return ResultVo.Success(vo);
    }

    @ApiOperation("财务审核")
    @PostMapping("approved")
    public ResultVo approved(@RequestBody InvoiceOrderGroupQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (query == null || query.getLikeOrderNo() == null){
            return ResultVo.Fail("数据不正确");
        }
        invoiceOrderGroupService.approved(query);
        return ResultVo.Success();
    }

    @ApiOperation("列表页导出")
    @PostMapping("list/export")
    public ResultVo listExport(@RequestBody InvoiceOrderGroupQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        //园区组织下的账号看不到数据
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            return ResultVo.Fail("暂无数据导出");
        }
        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
        List<InvoiceOrderGroupListVO> lists = invoiceOrderGroupService.listInvoiceOrderGroup(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("集团开票订单记录", "集团开票", InvoiceOrderGroupListVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("集团开票订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    @ApiOperation("详情")
    @PostMapping("detail")
    public ResultVo detail(@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        //园区组织下的账号看不到数据
        HashMap<String, Object> map = Maps.newHashMap();
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            map.put("base", null);
            map.put("analysis", null);
            return ResultVo.Success(map);
        }
        InvoiceOrderGroupEntity entity = invoiceOrderGroupService.queryByOrderNo(orderNo, userEntity.getOemCode());
        if (entity == null) {
            return ResultVo.Fail("集团代开订单不存在");
        }
        //基础信息
        map.put("base", new InvoiceOrderGroupListVO(entity, oemService.getOem(entity.getOemCode())));
        // 付款截图
        if (entity.getPayImgUrl() != null){
            map.put("payImgUrl",ossService.getPrivateImgUrl(entity.getPayImgUrl()));
        }
        //流水解析情况
        Map<String,Object> analysis=groupPaymentAnalysisRecordService.sumByGroupOrderNo(orderNo, entity.getOemCode());
        List<Map<String,Object>> mapList =invoiceOrderService.sumByGroupOrderNo(orderNo);
        analysis.put("eleInvComNum",0);
        analysis.put("parInvComNum",0);
        for ( Map<String,Object> m:mapList) {
            Integer way= (Integer) m.get("invoice_way");
            if (way==1){
                analysis.put("parInvComNum",m.get("number"));
            }
            if(way==2){
                analysis.put("eleInvComNum",m.get("number"));

            }
        }
        map.put("analysis", analysis);
        return ResultVo.Success(map);
    }

    @ApiOperation("开票明细")
    @PostMapping("detail/list")
    public ResultVo detailList(@JsonParam String orderNo, @JsonParam Integer pageNumber, @JsonParam Integer pageSize) {
        CurrUser currUser = getCurrUser();
        if (pageNumber == null) {
            pageNumber = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        //园区组织下的账号看不到数据
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            return ResultVo.Success();
        }
        InvoiceOrderGroupEntity entity = invoiceOrderGroupService.queryByOrderNo(orderNo, userEntity.getOemCode());
        if (entity == null) {
            return ResultVo.Fail("集团代开订单不存在");
        }
        PageInfo<InvoiceOrderByGroupOrderNoVO> page = invoiceOrderService.pageListByGroupOrderNo(entity.getOrderNo(), entity.getOemCode(), pageNumber, pageSize);
        return ResultVo.Success(page);
    }

    @ApiOperation("开票明细导出")
    @PostMapping("detail/export")
    public ResultVo detailExport(@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        //园区组织下的账号看不到数据
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            return ResultVo.Fail("暂无数据导出");
        }
        InvoiceOrderGroupEntity entity = invoiceOrderGroupService.queryByOrderNo(orderNo, userEntity.getOemCode());
        if (entity == null) {
            return ResultVo.Fail("集团代开订单不存在");
        }
        List<InvoiceOrderByGroupOrderNoVO> lists = invoiceOrderService.listByGroupOrderNo(orderNo, entity.getOemCode());
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("开票明细订单记录", "开票明细", InvoiceOrderByGroupOrderNoVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("集团开票订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    @ApiOperation("下载解析结果")
    @PostMapping("download/analysis")
    public ResultVo downloadAnalysisRecord(@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        //园区组织下的账号看不到数据
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            return ResultVo.Fail("园区组织不支持此操作");
        }
        InvoiceOrderGroupEntity entity = invoiceOrderGroupService.queryByOrderNo(orderNo, userEntity.getOemCode());
        if (entity == null) {
            return ResultVo.Fail("集团代开订单不存在");
        }
        List<GroupPaymentAnalysisRecordEntity> lists = groupPaymentAnalysisRecordService.queryByOrderNo(orderNo, entity.getOemCode());
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("集团开票解析结果记录", "集团开票解析结果", GroupPaymentAnalysisRecordEntity.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("解析结果导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }
    /**
     * 查询是否存在开票记录状态为待补票、出票中断、待确认、已完成、推送失败,出票中的子订单
     */
    @PostMapping("isHave")
    public ResultVo isHave(@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        InvoiceOrderGroupEntity entity = invoiceOrderGroupService.queryByOrderNo(orderNo, userEntity.getOemCode());
        if (entity == null) {
            return ResultVo.Fail("集团代开订单不存在");
        }
        Map<String,Object> map=new HashMap<>();
        //判断该批量开票订单下的开票记录状态是否都为已完成
        List<InvoiceRecordEntity> invoiceRecordEntityList=invoiceRecordService.queryGroupInvoiceOrderByGroupOrderNo(entity.getOrderNo(),InvoiceRecordStatusEnum.FOR_TICKET.getValue()+","+InvoiceRecordStatusEnum.NTERRUPTION_OF_THE_DRAWER.getValue()+","+InvoiceRecordStatusEnum.FAILED_TO_PUSH.getValue()+","+InvoiceRecordStatusEnum.TO_BE_CONFIRMED.getValue()+","+InvoiceRecordStatusEnum.COMPLETED.getValue()+","+InvoiceRecordStatusEnum.INVOICING.getValue(),null);
        if(CollectionUtil.isNotEmpty(invoiceRecordEntityList)){
            map.put("flag",1);
            map.put("msg","该集团开票订单正在出票中，若强制取消，则需要把已出票的订单发票线下作废，请确定是否继续？");
        }else{
            map.put("flag",0);
            map.put("msg","");
        }
        return ResultVo.Success(map);
    }
    /**
     * 修改状态
     * @param orderNo
     * @param status，1签收，2取消
     * @return
     */
    @PostMapping("update/status")
    public ResultVo updateStatus(@JsonParam String orderNo, @JsonParam Integer status) {
        CurrUser currUser = getCurrUser();
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        if (status == null || status < 1 || status > 2) {
            return ResultVo.Fail("修改状态有误");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        //园区组织下的账号看不到数据
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            return ResultVo.Fail("园区组织不支持此操作");
        }
        InvoiceOrderGroupEntity entity = invoiceOrderGroupService.queryByOrderNo(orderNo, userEntity.getOemCode());
        if (entity == null) {
            return ResultVo.Fail("集团代开订单不存在");
        }
        // V3.1修改 待财务审核状态为4
        if (!InvoiceOrderGroupStatusEnum.EXAMINE.getValue().equals(entity.getOrderStatus()) && entity.getOrderStatus() > InvoiceOrderGroupStatusEnum.IN_TICKETING.getValue()) {
            return ResultVo.Fail("集团代开状态有误");
        }
        if (Objects.equals(entity.getOrderStatus(), InvoiceOrderGroupStatusEnum.CREATED.getValue())
            && Objects.equals(status,1)) {
            return ResultVo.Fail("流水解析中订单不能被签收");
        }
        try{
            invoiceOrderGroupService.updateStatus(entity, status, userEntity);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    @ApiOperation("查询增值税税率")
    @PostMapping("query/rate")
    public ResultVo queryRate(@JsonParam String oemCode) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() == 1) {
            if (StringUtils.isBlank(oemCode)) {
                return ResultVo.Fail("机构编码不能为空");
            }
        } else {
            oemCode = userEntity.getOemCode();
        }
        OemEntity oem = oemService.getOem(oemCode);
        if (oem == null) {
            return ResultVo.Fail("机构不存在");
        }
        return ResultVo.Success(oemConfigService.queryOemRate(oemCode, "vat_rate", 2));
    }

    /**
     * 集团开票取消是否有弹窗校验
     * @param groupOrderNo
     * @return
     */
    @PostMapping("groupCancelNotice")
    public ResultVo groupCancelNotice(@JsonParam String groupOrderNo){
        getCurrUser();
        InvoiceOrderGroupEntity entity = invoiceOrderGroupService.queryByOrderNo(groupOrderNo,null);
        if (entity == null){
            return ResultVo.Fail("集团开票订单不正确");
        }
        List<InvoiceOrderByGroupOrderNoVO> list = invoiceOrderService.listByGroupOrderNo(groupOrderNo,entity.getOemCode());
        if (CollectionUtil.isEmpty(list)){
            return ResultVo.Fail();
        }
        List<Long> companyId = new ArrayList<>();
        for (InvoiceOrderByGroupOrderNoVO vo:list){
            companyId.add(vo.getCompanyId());
        }
        List<InvoiceOrderEntity> orderEntityList = invoiceOrderService.findExistPendingOrder(companyId, entity.getAddTime(),groupOrderNo);
        Map<String,Object> map = new HashMap<>();
        if (CollectionUtil.isNotEmpty(orderEntityList)){
            map.put("type",2);
            map.put("content","存在个体户在该订单后已自主创建开票订单，取消订单可能造成服务费少收,建议核对后联系客户补缴或取消后续订单后再操作。");
        }
        return ResultVo.Success(map);
    }
}
