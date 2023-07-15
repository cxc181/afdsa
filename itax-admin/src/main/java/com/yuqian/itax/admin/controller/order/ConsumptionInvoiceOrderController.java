package com.yuqian.itax.admin.controller.order;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.google.common.collect.Maps;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.InvoiceInfoByOemEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.InvoiceInfoByOemService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderChangeEntity;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderEntity;
import com.yuqian.itax.order.entity.MemberConsumptionRecordEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.query.ConsumptionInvoiceOrderQuery;
import com.yuqian.itax.order.entity.query.MemberConsumptionRecordQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.service.ConsumptionInvoiceOrderService;
import com.yuqian.itax.order.service.MemberConsumptionRecordService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.system.entity.LogisCompanyEntity;
import com.yuqian.itax.system.service.LogisCompanyService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("consumption")
@Slf4j
public class ConsumptionInvoiceOrderController extends BaseController {

    @Autowired
    private ConsumptionInvoiceOrderService consumptionInvoiceOrderService;
    @Autowired
    private MemberConsumptionRecordService memberConsumptionRecordService;
    @Autowired
    private OssService ossService;
    @Autowired
    private LogisCompanyService logisCompanyService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private OemService oemService;
    @Autowired
    InvoiceInfoByOemService invoiceInfoByOemService;
    /**
     * 消费发票申请分页查询
     * @param query
     * @return
     */
    @PostMapping("invoice/page")
    public ResultVo invoicePage(@RequestBody ConsumptionInvoiceOrderQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(userEntity.getPlatformType()!=1){
            query.setOemCode(userEntity.getOemCode());
        }
        PageInfo<ConsumptionInvoiceOrderVO> page = consumptionInvoiceOrderService.invoicePage(query);
        return ResultVo.Success(page);
    }
    /**
     * 查看发票
     */
    @PostMapping("invoice")
    public ResultVo invoice(@JsonParam Long id){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        String  [] url=null;
        ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity=consumptionInvoiceOrderService.findById(id);
        if (StringUtils.isNotBlank(consumptionInvoiceOrderEntity.getInvoiceImgs())) {
            String[] imgs = consumptionInvoiceOrderEntity.getInvoiceImgs().split(",");
            for (int a = 0; a < imgs.length; a++) {
                imgs[a] = ossService.getPrivateImgUrl(imgs[a]);
            }
            url=imgs;
        } else if (StringUtils.isNotBlank(consumptionInvoiceOrderEntity.getInvoicePdfUrl())) {
            String[] imgs = new String[1];
            imgs[0] = FileUtil.pdfUrl2pngBase64(consumptionInvoiceOrderEntity.getInvoicePdfUrl());
            url=imgs;
        }
        return ResultVo.Success(url);
    }
    /**
     * 消费发票申请订单导出
     * @param query
     * @return
     */
    @PostMapping("invoice/export")
    public ResultVo invoiceExport(@RequestBody ConsumptionInvoiceOrderQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(userEntity.getPlatformType()!=1){
            query.setOemCode(userEntity.getOemCode());
        }
        List<ConsumptionInvoiceOrderVO> lists = consumptionInvoiceOrderService.invoiceList(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("消费发票申请订单", "消费发票申请订单", ConsumptionInvoiceOrderVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("消费发票申请订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }

    /**
     * 消费订单（分页）
     * @param query
     * @return
     */
    @PostMapping("member/consumption/page")
    public ResultVo orderPage(@RequestBody MemberConsumptionRecordQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        PageInfo<MemberConsumptionRecordVO> page = memberConsumptionRecordService.queryMemberConsumptionRecordPage(query);
        return ResultVo.Success(page);
    }

    /**
     * 消费订单导出
     * @param query
     * @return
     */
    @PostMapping("member/consumption/export")
    public ResultVo orderExport(@RequestBody MemberConsumptionRecordQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        List<MemberConsumptionRecordVO> lists = memberConsumptionRecordService.queryMemberConsumptionRecordList(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("消费订单", "消费订单", MemberConsumptionRecordVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("消费订单导出出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }

    /**
     * 立即开票
     */
    @PostMapping("apply/invoice")
    public ResultVo applyInvoice(@JsonParam Long id){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(id==null){
            return ResultVo.Fail("请选择消费申请订单");
        }
        try{
            consumptionInvoiceOrderService.applyInvoice(id,getCurrUseraccount());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success("出票中");
    }

    /**
     * 出票失败
     */
    @PostMapping("apply/invoice/fail")
    public ResultVo applyInvoiceFail(@JsonParam Long id,@JsonParam String remark){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(id==null){
            return ResultVo.Fail("请选择消费申请订单");
        }
        if(StringUtil.isEmpty(remark)){
            return ResultVo.Fail("请输入失败原因");
        }
        try{
            consumptionInvoiceOrderService.applyInvoiceFail(id,getCurrUseraccount(),remark);
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success("成功");
    }

    /**
     * 获取收件信息
     * @param id
     * @return
     */
    @PostMapping("receivingInfo")
    public ResultVo receivingInfo(@JsonParam Long id){
        CurrUser currUser = getCurrUser();
        if (id == null){
            return ResultVo.Fail("请选择消费申请订单");
        }
        ConsumptionInvoiceReceivingVO vo  = consumptionInvoiceOrderService.getReceivingInfoById(id);
        if (vo == null){
            return ResultVo.Fail("消费订单错误");
        }
        return ResultVo.Success(vo);
    }

    /**
     * 立即发货
     * @param id
     * @param courierNumber
     * @param courierCompanyName
     * @return
     */
    @PostMapping("deliverGoods")
    public ResultVo deliverGoods(@JsonParam Long id,@JsonParam String courierNumber,@JsonParam String courierCompanyName){
        CurrUser currUser = getCurrUser();
        LogisCompanyEntity logisCompanyEntity = logisCompanyService.queryByCompanyName(courierCompanyName);
        if (logisCompanyEntity == null){
            return ResultVo.Fail("不支持此快递公司");
        }
        if (courierNumber.length()>30){
            return ResultVo.Fail("快递单号不能超过30个字符");
        }
        consumptionInvoiceOrderService.updateConsumptionInvoiceOrder(id,courierNumber,courierCompanyName,currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 查询详情
     * @param orderNo
     * @return
     */
    @PostMapping("detail")
    public ResultVo detail(@JsonParam String orderNo){
        if (StringUtils.isBlank(orderNo)) {
            return ResultVo.Fail("订单编号不能为空");
        }
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNo(orderNo);
        orderEntity = orderService.selectOne(orderEntity);
        if (orderEntity == null) {
            return ResultVo.Fail("订单不存在");
        }
        MemberAccountEntity accEntity = memberAccountService.findById(orderEntity.getUserId());
        if (accEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OemEntity oemEntity = oemService.getOem(orderEntity.getOemCode());
        if (oemEntity == null) {
            return ResultVo.Fail("机构不存在");
        }
        Map<String, Object> map = Maps.newHashMap();
        OrderVO orderVO = new OrderVO(orderEntity,accEntity,oemEntity);
        map.put("orderInfo", orderVO);
        //开票信息
        InvoiceInfoByOemEntity invoiceInfoByOemEntity = new InvoiceInfoByOemEntity();
        invoiceInfoByOemEntity.setOemCode(orderEntity.getOemCode());
        invoiceInfoByOemEntity = invoiceInfoByOemService.selectOne(invoiceInfoByOemEntity);
        if (invoiceInfoByOemEntity == null){
            return ResultVo.Fail("增值税率未配置");
        }
        ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity = new ConsumptionInvoiceOrderEntity();
        consumptionInvoiceOrderEntity.setOrderNo(orderNo);
        consumptionInvoiceOrderEntity.setOemCode(userEntity.getOemCode());
        consumptionInvoiceOrderEntity = consumptionInvoiceOrderService.selectOne(consumptionInvoiceOrderEntity);
        InvoiceDetailOrderVO invOrder = new InvoiceDetailOrderVO(consumptionInvoiceOrderEntity, invoiceInfoByOemEntity);
        invOrder.setGeneralTaxpayerQualification(ossService.getPrivateVideoUrl(invOrder.getGeneralTaxpayerQualification()));
        map.put("invOrder", invOrder);
        MemberConsumptionRecordQuery query = new MemberConsumptionRecordQuery();
        query.setOrderNo(orderNo);
        query.setId(consumptionInvoiceOrderEntity.getId());
        List<MemberConsumptionRecordVO> consumptionRecordList = memberConsumptionRecordService.queryMemberConsumptionRecordList(query);
        map.put("consumptionRecordList",consumptionRecordList);
        map.put("invoiceImgsList",getOssImages(consumptionInvoiceOrderEntity.getInvoiceImgs()));
        return ResultVo.Success(map);
    }

    /**
     * 获取oss上图片集合
     * @param url
     * @return
     */
    private List<String> getOssImages(String url) {
        List<String> list = Lists.newArrayList();
        if (StringUtils.isBlank(url)) {
            return list;
        }
        //oss获取银行流水
        String[] split = url.split(",");
        for (String s : split) {
            list.add(ossService.getPrivateVideoUrl(s));
        }
        return list;
    }

}
