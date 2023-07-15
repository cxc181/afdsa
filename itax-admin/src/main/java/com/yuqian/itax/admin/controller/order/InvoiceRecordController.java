package com.yuqian.itax.admin.controller.order;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.entity.InvoiceRecordChangeEntity;
import com.yuqian.itax.order.entity.query.InvoiceRecordQuery;
import com.yuqian.itax.order.entity.vo.ConfirmInvoiceRecordVo;
import com.yuqian.itax.order.entity.vo.InvoiceRecordDetailVO;
import com.yuqian.itax.order.entity.vo.InvoiceRecordVO;
import com.yuqian.itax.order.service.InvoiceRecordChangeService;
import com.yuqian.itax.order.service.InvoiceRecordService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开票记录controller
 * @author：ni.jiang
 * @Date：2020/12/31 10:12
 * @version：1.0
 */
@Api(tags = "开票记录Controller")
@RestController
@RequestMapping("invoiceRecord")
@Slf4j
public class InvoiceRecordController extends BaseController {

    @Autowired
    private InvoiceRecordService invoiceRecordService;

    @Autowired
    private InvoiceRecordChangeService invoiceRecordChangeService;

    @ApiOperation("开票记录列表页")
    @PostMapping("/page")
    public ResultVo listPage(@RequestBody InvoiceRecordQuery query){
        if(query==null){
            return ResultVo.Fail("参数不正确");
        }
        if(query != null && query.getStatus() != null && query.getStatus()<0){
            return ResultVo.Fail("非法状态值");
        }
        if(query != null && query.getInvoiceMountMin() != null && query.getInvoiceMountMin()<0){
            return ResultVo.Fail("开票金额最小值不能为负数");
        }
        if(query != null && query.getInvoiceMountMax() != null && query.getInvoiceMountMax()<0){
            return ResultVo.Fail("开票金额最大值不能为负数");
        }
        if(query != null && query.getInvoiceMountMin() != null && query.getInvoiceMountMax() != null && query.getInvoiceMountMax()< query.getInvoiceMountMin()){
            return ResultVo.Fail("开票金额最大值不能小于最小值");
        }
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try {
            if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
                query.setParkId(userEntity.getParkId());
            } else {
                query.setTree(getOrgTree());
            }
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        PageInfo<InvoiceRecordVO> page = invoiceRecordService.listPage(query);
        return ResultVo.Success(page);
    }

    @ApiOperation("开票记录导出")
    @PostMapping("/export")
    public ResultVo export(@RequestBody InvoiceRecordQuery query){
        if(query==null){
            return ResultVo.Fail("参数不正确");
        }
        if(query != null && query.getStatus() != null && query.getStatus()<0){
            return ResultVo.Fail("非法状态值");
        }
        if(query != null && query.getInvoiceMountMin() != null && query.getInvoiceMountMin()<0){
            return ResultVo.Fail("开票金额最小值不能为负数");
        }
        if(query != null && query.getInvoiceMountMax() != null && query.getInvoiceMountMax()<0){
            return ResultVo.Fail("开票金额最大值不能为负数");
        }
        if(query != null && query.getInvoiceMountMin() != null && query.getInvoiceMountMax() != null && query.getInvoiceMountMax()< query.getInvoiceMountMin()){
            return ResultVo.Fail("开票金额最大值不能小于最小值");
        }
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try {
            if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
                query.setParkId(userEntity.getParkId());
            } else {
                query.setTree(getOrgTree());
            }
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
        List<InvoiceRecordVO> lists = invoiceRecordService.querylistInvoiceRecord(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("开票记录", "开票记录", InvoiceRecordVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("开户订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    @ApiOperation("开票记录历史")
    @PostMapping("/history")
    public ResultVo invoiceRecordChanges(@JsonParam String invoiceRecordNo){
        getCurrUser();
        if(StringUtil.isBlank(invoiceRecordNo)){
            return ResultVo.Fail("开票记录编码不能为空");
        }
        Example example = new Example(InvoiceRecordChangeEntity.class);
        example.createCriteria().andEqualTo("invoiceRecordNo",invoiceRecordNo);
        example.orderBy("addTime").desc().orderBy("id").desc();
        List<InvoiceRecordChangeEntity> list = invoiceRecordChangeService.selectByExample(example);
        return ResultVo.Success(list);
    }

    @ApiOperation("确认开票详情")
    @PostMapping("/gotoConfirmInvoiceRecord")
    public ResultVo gotoConfirmInvoiceRecord(@JsonParam String invoiceRecordNo){
       getCurrUser();
        if(StringUtil.isBlank(invoiceRecordNo)){
            return ResultVo.Fail("开票记录编码不能为空");
        }
        ConfirmInvoiceRecordVo vo = invoiceRecordService.gotoConfirmInvoiceRecord(invoiceRecordNo);
        return ResultVo.Success(vo);
    }

    /**
     * 立即开票、继续开票、重新提交、强制开票
     *
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @param isForce 是否强制 强制开票 该值必须为 “1”
     * @return
     */
    @ApiOperation("立即开票")
    @PostMapping("/openInvoice")
    public ResultVo openInvoice(@JsonParam String invoiceRecordNo,@JsonParam String orderNo,@JsonParam String isForce){
        CurrUser currUser = getCurrUser();
        if(StringUtil.isBlank(invoiceRecordNo)){
            return ResultVo.Fail("开票记录编码不能为空");
        }
        if(StringUtil.isBlank(orderNo)){
            return ResultVo.Fail("开票订单号不能为空");
        }
        if(StringUtils.isBlank(isForce)){
            isForce = "0";
        }
        String messsage = invoiceRecordService.openInvoice(invoiceRecordNo,orderNo,currUser.getUseraccount(),isForce);
        if(StringUtil.isNotBlank(messsage)){
            return ResultVo.Fail(messsage);
        }
        return ResultVo.Success();
    }

    /**
     * 线下开票 ，只有开票记录为全部失败时才能使用
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @return
     */
    @ApiOperation("线下开票")
    @PostMapping("/offlineInvoice")
    public ResultVo offlineInvoice(@JsonParam String invoiceRecordNo,@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if(StringUtil.isBlank(invoiceRecordNo)){
            return ResultVo.Fail("开票记录编码不能为空");
        }
        if(StringUtil.isBlank(orderNo)){
            return ResultVo.Fail("开票订单号不能为空");
        }

        invoiceRecordService.offlineInvoice(invoiceRecordNo,orderNo,currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 强制成功
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @return
     */
    @ApiOperation("强制成功")
    @PostMapping("/forceSuccess")
    public ResultVo forceSuccess(@JsonParam String invoiceRecordNo,@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if(StringUtil.isBlank(invoiceRecordNo)){
            return ResultVo.Fail("开票记录编码不能为空");
        }
        if(StringUtil.isBlank(orderNo)){
            return ResultVo.Fail("开票订单号不能为空");
        }
        invoiceRecordService.forceSuccess(invoiceRecordNo,orderNo,currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 重新推送
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @return
     */
    @ApiOperation("重新推送")
    @PostMapping("/sendAgain")
    public ResultVo sendAgain(@JsonParam String invoiceRecordNo,@JsonParam String orderNo){
        CurrUser currUser = getCurrUser();
        if(StringUtil.isBlank(invoiceRecordNo)){
            return ResultVo.Fail("开票记录编码不能为空");
        }
        if(StringUtil.isBlank(orderNo)){
            return ResultVo.Fail("开票订单号不能为空");
        }
        invoiceRecordService.sendAgain(invoiceRecordNo,orderNo,currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 开票记录明细
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @return
     */
    @ApiOperation("开票记录明细")
    @PostMapping("/invoiceRecordDetailList")
    public ResultVo invoiceRecordDetailList(@JsonParam String invoiceRecordNo,@JsonParam String orderNo){
        getCurrUser();
        if(StringUtil.isBlank(invoiceRecordNo)){
            return ResultVo.Fail("开票记录编码不能为空");
        }
        if(StringUtil.isBlank(orderNo)){
            return ResultVo.Fail("开票订单号不能为空");
        }
        List<InvoiceRecordDetailVO> list = invoiceRecordService.invoiceRecordDetailList(invoiceRecordNo,orderNo);
        return ResultVo.Success(list);
    }

    /**
     * 打印开票明细
     * @param detailId 明细id
     * @param invoiceRecordNo 开票记录编号
     * @return
     */
    @ApiOperation("打印开票明细")
    @PostMapping("/invoiceDetailPrint")
    public ResultVo invoiceDetailPrint(@JsonParam Long detailId,@JsonParam String invoiceRecordNo,@JsonParam String printType){
        CurrUser currUser =getCurrUser();
        if(detailId == null){
            return ResultVo.Fail("开票记录明细id不能为空");
        }
        if(StringUtils.isBlank(printType)){
            printType = "0";
        }else if("0".equals(printType) && "1".equals(printType) ){
            return ResultVo.Fail("打印类型错误，只支持发票和清单的打印类型");
        }
        String message = invoiceRecordService.invoiceDetailPrint(detailId,invoiceRecordNo,printType,currUser.getUseraccount());
        if(StringUtils.isNotBlank(message)){
            return ResultVo.Fail(message);
        }
        return ResultVo.Success();
    }

    /**
     * 作废开票明细
     * @param detailIds 明细ids，多个开票记录id之间用 逗号分隔
     * @return
     */
    @ApiOperation("作废开票明细")
    @PostMapping("/invoiceDetailInvalid")
    public ResultVo invoiceDetailInvalid(@JsonParam String detailIds){
        CurrUser currUser =getCurrUser();
        if(detailIds.endsWith(",")){
            detailIds = detailIds.substring(0,detailIds.length()-1);
        }
        if(StringUtils.isBlank(detailIds)){
            return ResultVo.Fail("开票记录明细id不能为空");
        }
        invoiceRecordService.invoiceDetailInvalid(detailIds,currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 取消开票记录
     * @param orderNo 订单号
     * @param type 1-作废不重开 2-作废重开
     * @return
     */
    @ApiOperation("取消开票记录")
    @PostMapping("/invoiceRecordCancel")
    public ResultVo invoiceRecordCancel(@JsonParam String orderNo,@JsonParam String type,@JsonParam String remark){
        CurrUser currUser =getCurrUser();
        if(StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("订单号不能为空");
        }
        if(StringUtils.isBlank(type)){
            return ResultVo.Fail("处理方式不能为空");
        }else if(!"1".equals(type) && !"2".equals(type)){
            return ResultVo.Fail("处理方式不正确");
        }
        if(StringUtils.isBlank(remark)){
            return ResultVo.Fail("取消原因不能为空");
        }else if(remark.trim().length()>100){
            return ResultVo.Fail("取消原因不能超过100个字符");
        }
        invoiceRecordService.invoiceRecordCancel(orderNo,type,remark,currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 根据日期获取所属季度
     * @param date 日期
     * @return
     */
    @ApiOperation("根据日期获取所属季度")
    @PostMapping("/getQuarterByDate")
    public ResultVo getQuarterByDate(@JsonParam String date){
        if(StringUtil.isBlank(date)){
            return ResultVo.Fail("日期不能为空");
        }
        Date dateTime = DateUtil.parseDefaultDate(date);
        int year = DateUtil.getYear(dateTime);
        String quarter = DateUtil.getQuarter(dateTime);
        Map<String,String> map = new HashMap<>();
        map.put("year",year+"");
        map.put("quarter",quarter);
        return ResultVo.Success(map);
    }
}
