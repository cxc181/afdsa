package com.yuqian.itax.api.controller.order;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.entity.query.BaseQuery;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.order.entity.dto.ConsumptionInvOrderDTO;
import com.yuqian.itax.order.entity.query.ConsumptionRecordDetailQuery;
import com.yuqian.itax.order.entity.vo.ConsumptionInvoiceOrderDetailVO;
import com.yuqian.itax.order.entity.vo.ConsumptionInvoiceOrderPageVO;
import com.yuqian.itax.order.entity.vo.ConsumptionRelaOrderVO;
import com.yuqian.itax.order.service.ConsumptionInvoiceOrderService;
import com.yuqian.itax.order.service.LogisticsInfoService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.util.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 消费开票订单控制器
 * @Author  Kaven
 * @Date   2020/9/27 14:09
*/
@Api(tags = "消费开票订单控制器")
@RestController
@RequestMapping("/consumptioninvorder")
@Slf4j
public class ConsumptionInvOrderController extends BaseController {
    @Autowired
    private ConsumptionInvoiceOrderService consumptionInvoiceOrderService;

    @Autowired
    private OssService ossService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    /**
     * @Description 创建消费开票订单
     * @Author  Kaven
     * @Date   2020/9/27 14:10
     * @Param
     * @Return
     * @Exception
     */
    @ApiOperation("创建消费开票订单")
    @PostMapping("createOrder")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.CONSUMPTION_INVOICE_CREATE_ORDER, lockTime = 10)
    public ResultVo createOrder(@RequestBody @Validated ConsumptionInvOrderDTO dto, BindingResult result){
        if(null == dto){
            return ResultVo.Fail("传入参数对象不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        dto.setOemCode(oemCode);
        dto.setCurrUserId(getCurrUserId());
        dto.setSourceType(Integer.parseInt(StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType")));
        String orderNo = this.consumptionInvoiceOrderService.createOrder(dto);
        // 返回值
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("orderNo", orderNo);
        return ResultVo.Success(dataMap);
    }

    /**
     * @return ResultVo<PageInfo>
     * @Description 分页查询消费开票订单列表
     * @Author yejian
     * @Date 2020/09/28 13:33
     * @Param query
     */
    @ApiOperation("分页查询消费开票订单列表")
    @PostMapping("/findPage")
    public ResultVo<PageInfo> findPage(@RequestBody BaseQuery query) {
        List<ConsumptionInvoiceOrderPageVO> conInvOrderList = consumptionInvoiceOrderService.findConsumptionInvoiceOrderList(getCurrUserId(), getRequestHeadParams("oemCode"));
        PageInfo pageInfo = PageResultVo.listToPage(query.getPageNumber(), query.getPageSize(), conInvOrderList);
        List<ConsumptionInvoiceOrderPageVO> list = pageInfo.getList();
        if(list!=null){
            list.forEach(vo->{
                if(StringUtils.isNotBlank(vo.getInvoiceImgs())){
                    String[] imgs = vo.getInvoiceImgs().split(",");
                    for(int a = 0;a <imgs.length;a++){
                        imgs[a] = ossService.getPrivateImgUrl(imgs[a]);
                    }
                    vo.setInvoiceImgList(imgs);
                }else if(StringUtils.isNotBlank(vo.getInvoicePdfUrl())){
                    String[] imgs = new String[1];
                    imgs[0] = FileUtil.pdfUrl2pngBase64(vo.getInvoicePdfUrl());
                    vo.setInvoiceImgList(imgs);
                }
            });
        }
        return ResultVo.Success(pageInfo);
    }

    /**
     * @return ResultVo<String>
     * @Description 查看电子发票
     * @Author ni.jiang
     * @Date 2020/09/28 13:33
     * @Param query
     */
    @ApiOperation("查看电子发票")
    @PostMapping("/electronicInvoiceView")
    public ResultVo electronicInvoiceView(@JsonParam String pdfUrl) {
        if(StringUtils.isBlank(pdfUrl)){
            return ResultVo.Fail("暂无电子发票");
        }
        String base64 = FileUtil.pdfUrl2pngBase64(pdfUrl);
        if(StringUtils.isBlank(base64)){
            log.error("获取电子发票失败");
            return ResultVo.Fail();
        }
        Map<String,String> result = new HashMap<>();
        result.put("base64",base64);
        return ResultVo.Success(result);
    }

    /**
     * @param orderNo
     * @return ResultVo<ConsumptionInvoiceOrderDetailVO>
     * @Description 查询消费开票订单详情
     * @Author yejian
     * @Date 2020/09/28 13:33
     */
    @ApiOperation("查询消费开票订单详情")
    @ApiImplicitParam(name = "orderNo", value = "订单号", dataType = "String", required = true)
    @PostMapping("/getDetail")
    public ResultVo<ConsumptionInvoiceOrderDetailVO> getDetail(@JsonParam String orderNo) {
        ConsumptionInvoiceOrderDetailVO conInvOrderDetail = consumptionInvoiceOrderService.getDetailByOrderNo(getCurrUserId(), getRequestHeadParams("oemCode"), orderNo);
        return ResultVo.Success(conInvOrderDetail);
    }

    /**
     * @Description 分页查询消费开票订单对应的消费订单列表
     * @Author yejian
     * @Date 2020/09/28 14:44
     * @Param ConsumptionRecordDetailQuery
     * @Return ResultVo<PageInfo>
     */
    @ApiOperation("分页查询消费开票订单对应的消费订单列表")
    @PostMapping("/findRelaOrderPage")
    public ResultVo<PageInfo> findRelaOrderPage(@RequestBody @Valid ConsumptionRecordDetailQuery query, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        List<ConsumptionRelaOrderVO> relaOrderList = consumptionInvoiceOrderService.findConsumptionRelaOrderList(getCurrUserId(), getRequestHeadParams("oemCode"), query.getConsumptionOrderRela());
        PageInfo pageInfo = PageResultVo.listToPage(query.getPageNumber(), query.getPageSize(), relaOrderList);
        return ResultVo.Success(pageInfo);
    }

    @ApiOperation("")
    @PostMapping("/checkTheLogistics")
    public ResultVo checkTheLogistics(@JsonParam String orderNo) {
        Map<String, Object> map = consumptionInvoiceOrderService.checkTheLogistics(orderNo, getCurrUserId());
        return ResultVo.Success(map);
    }
}
