package com.yuqian.itax.admin.controller.company;


import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.entity.LogisticsInfoEntity;
import com.yuqian.itax.order.entity.MemberOrderRelaEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.service.LogisticsInfoService;
import com.yuqian.itax.order.service.MemberOrderRelaService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.LogisCompanyEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.LogisCompanyService;
import com.yuqian.itax.user.entity.CompanyResoucesApplyRecordEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.query.CompanyResoucesApplyRecordQuery;
import com.yuqian.itax.user.entity.vo.CompanyResoucesApplyRecordExportVO;
import com.yuqian.itax.user.entity.vo.CompanyResoucesApplyRecordVO;
import com.yuqian.itax.user.enums.CompanyResoucesApplyRecordStatusEnum;
import com.yuqian.itax.user.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.util.Lists;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;


/**
 * 企业资源申请记录ontroll
 * auth: HZ
 * time: 2019/12/09
 */
@Slf4j
@RestController
@RequestMapping("/company/apply")
public class CompanyResoucesApplyRecordController  extends BaseController {

    @Autowired
    CompanyResoucesApplyRecordService companyResoucesApplyRecordService;
    @Autowired
    ParkService parkService;
    @Autowired
    OrderService orderService;
    @Autowired
    MemberOrderRelaService memberOrderRelaService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    DictionaryService dictionaryService;
    @Autowired
    SmsService smsService;
    @Autowired
    LogisCompanyService logisCompanyService;
    @Autowired
    LogisticsInfoService logisticsInfoService;
    @Autowired
    CompanyResourcesAddressService companyResourcesAddressService;
    @Autowired
    MemberCompanyService memberCompanyService;



    /**
     * 企业资源申请记录列表（分页）查询
     * auth: HZ
     * time:2020/03/25
     */
    @PostMapping("/companyResoucesApplyRecordPageInfo")
    public ResultVo companyResoucesApplyRecordPageInfo(@RequestBody CompanyResoucesApplyRecordQuery query){
        //验证登陆
        getCurrUser();
        query.setOemCode(getRequestHeadParams("oemCode"));
        UserEntity userEntity=userService.findById(getCurrUser().getUserId());
        if(null == userEntity){
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(userEntity.getPlatformType()==3){
            query.setParkId(userEntity.getParkId());
        }
        if(userEntity.getPlatformType()==4||userEntity.getPlatformType()==5){
            return ResultVo.Success();
        }
        //分页查询
        PageInfo<CompanyResoucesApplyRecordVO> mList=companyResoucesApplyRecordService.companyResoucesApplyRecordPageInfo(query);

        return ResultVo.Success(mList);
    }

    /**
     * 企业资源申请批量出库弹框
     */
    @PostMapping("batch/stock/open")
    public ResultVo batchExporOpen(@RequestBody CompanyResoucesApplyRecordQuery query){
        //登陆校验
        query.setOemCode(getRequestHeadParams("oemCode"));
        UserEntity userEntity=userService.findById(getCurrUser().getUserId());
        if(null == userEntity){
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(userEntity.getPlatformType()==3){
            query.setParkId(userEntity.getParkId());
        }
        query.setStatuss(CompanyResoucesApplyRecordStatusEnum.TO_BE_SHIPPED.getValue() + "," + CompanyResoucesApplyRecordStatusEnum.OUT_OF_STOCK.getValue());
        Map<String, Integer> map = companyResoucesApplyRecordService.sumCompanyResoucesApplyRecordList(query);
        if (map == null) {
            return ResultVo.Fail("暂无企业资源申请导出");
        }
        return ResultVo.Success(map);
    }

    /**
     * 企业资源申请批量出库
     */
    @PostMapping("/batch/stock")
    public ResultVo applyBatchStock(@RequestBody CompanyResoucesApplyRecordQuery query){
        query.setOemCode(getRequestHeadParams("oemCode"));
        UserEntity userEntity=userService.findById(getCurrUser().getUserId());
        if(null == userEntity){
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(userEntity.getPlatformType()==3){
            query.setParkId(userEntity.getParkId());
        }
        query.setStatuss(CompanyResoucesApplyRecordStatusEnum.TO_BE_SHIPPED.getValue() + "," + CompanyResoucesApplyRecordStatusEnum.OUT_OF_STOCK.getValue());
        List<CompanyResoucesApplyRecordExportVO> lists = companyResoucesApplyRecordService.companyResoucesApplyRecordExportList(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无企业资源申请导出");
        }
        //修改导出数据状态
        orderService.updateCompangApplyStatusBatch(lists,getCurrUseraccount());
        try {
            exportExcel("企业资源申请记录", "企业资源申请", CompanyResoucesApplyRecordExportVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("出库订单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }

    }

    /**
     * 企业资源申请批量发货
     */
    @PostMapping("/batch/send")
    public ResultVo applyyBatchSend(@RequestParam("file") MultipartFile file) {
        CurrUser currUser = getCurrUser();
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<CompanyResoucesApplyRecordExportVO> list;
        List<CompanyResoucesApplyRecordExportVO> failed = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            list = ExcelImportUtil.importExcel(file.getInputStream(), CompanyResoucesApplyRecordExportVO.class, params);
        } catch (Exception e) {
            log.error("批量发货异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("文件内容为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        Date updateTime = new Date();
        String orgTree = getOrgTree();
        Map<String, Object> map = Maps.newHashMap();
        for (CompanyResoucesApplyRecordExportVO apply : list) {
            if (StringUtils.isBlank(apply.getCourierNumber())) {
                apply.setFailed("快递单号不能为空");
                failed.add(apply);
                continue;
            }
            if (StringUtils.isBlank(apply.getCourierCompanyName())) {
                apply.setFailed("快递公司编号不能为空");
                failed.add(apply);
                continue;
            }
            LogisCompanyEntity logis = new LogisCompanyEntity();
            logis.setCompanyName(apply.getCourierCompanyName());
            logis = logisCompanyService.selectOne(logis);
            if (null == logis) {
                apply.setFailed("快递公司编号错误。");
                failed.add(apply);
                continue;
            }
            OrderEntity entity = orderService.queryByOrderNo(apply.getOrderNo());
            if (entity == null) {
                apply.setFailed("订单不存在");
                failed.add(apply);
                continue;
            }
            if (userEntity.getPlatformType() != 1 && !StringUtils.equals(entity.getOemCode(), userEntity.getOemCode())) {
                apply.setFailed("订单不属于当前OEM机构");
                failed.add(apply);
                continue;
            }
            if (!Objects.equals(entity.getOrderStatus(), CompanyResoucesApplyRecordStatusEnum.OUT_OF_STOCK.getValue())) {
                apply.setFailed("订单状态不是出库中");
                failed.add(apply);
                continue;
            }
            MemberOrderRelaEntity relaEntity = memberOrderRelaService.findById(entity.getRelaId());
            if (relaEntity == null) {
                apply.setFailed("会员关系订单不存在");
                failed.add(apply);
                continue;
            }
            MemberAccountEntity accEntity = memberAccountService.findById(relaEntity.getMemberId());
            if (accEntity == null) {
                apply.setFailed(ResultConstants.USER_NOT_EXISTS.getRetMsg());
                failed.add(apply);
                continue;
            }
            if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
                if (!Objects.equals(userEntity.getParkId(), entity.getParkId())) {
                    apply.setFailed("订单不属于当前园区");
                    failed.add(apply);
                    continue;
                }
            } else {
                boolean belongAdmin = belongAdmin(relaEntity.getMemberId(), orgTree, 5);
                if (belongAdmin) {
                    apply.setFailed("订单不属于当前登录用户组织");
                    failed.add(apply);
                    continue;
                }
            }
            CompanyResoucesApplyRecordEntity companyResoucesApplyRecordEntity=new CompanyResoucesApplyRecordEntity();
            companyResoucesApplyRecordEntity.setOrderNo(apply.getOrderNo());
            CompanyResoucesApplyRecordEntity resoucesApplyRecordEntity =companyResoucesApplyRecordService.selectOne(companyResoucesApplyRecordEntity);
            if (resoucesApplyRecordEntity == null) {
                apply.setFailed("企业资源申请记录不存在");
                failed.add(apply);
                continue;
            }
            resoucesApplyRecordEntity.setStatus(CompanyResoucesApplyRecordStatusEnum.TO_SIGN.getValue());
            resoucesApplyRecordEntity.setCourierCompanyName(apply.getCourierCompanyName().trim());
            resoucesApplyRecordEntity.setCourierNumber(apply.getCourierNumber().trim());
            resoucesApplyRecordEntity.setUpdateTime(updateTime);
            resoucesApplyRecordEntity.setUpdateUser(currUser.getUseraccount());
            entity.setOrderStatus(CompanyResoucesApplyRecordStatusEnum.TO_SIGN.getValue());
            entity.setUpdateUser(currUser.getUseraccount());
            entity.setUpdateTime(updateTime);
            try {
                MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(resoucesApplyRecordEntity.getCompanyId());
                log.info("企业ID==========================="+String.valueOf(resoucesApplyRecordEntity.getCompanyId()));
                if(null == memberCompanyEntity){
                    throw  new BusinessException("会员企业不存在");
                }
                orderService.updateCompangApplyStatus(resoucesApplyRecordEntity, entity);
                map.put("companyName",memberCompanyEntity.getCompanyName());
                map.put("courierCompanyName", apply.getCourierCompanyName());
                map.put("courierNumber", apply.getCourierNumber());
                smsService.sendTemplateSms(accEntity.getMemberPhone(), accEntity.getOemCode(), VerifyCodeTypeEnum.COMPANY_APPLY_RECORD_SEND.getValue(), map, 1);
            } catch (Exception e) {
                log.error("批量发货修改状态异常：" + e.getMessage(), e);
                apply.setFailed(e.getMessage());
                failed.add(apply);
                continue;
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", list.size() - failed.size());
        result.put("failed", failed.size());
        //如果无失败则不生成下载文件
        if (CollectionUtil.isEmpty(failed)){
            result.put("downLoadUrl", "");
            return ResultVo.Success(result);
        }
        DictionaryEntity dicEntity = dictionaryService.getByCode("file_download_path");
        if (dicEntity == null) {
            return ResultVo.Fail("字典数据未配置");
        }
        File bag = new File(dicEntity.getDictValue() + "/" + currUser.getUseraccount());
        if(!bag.exists()){
            bag.mkdirs();//如果路径不存在就先创建路径
        }
        String fileName = System.currentTimeMillis() + ".xls";
        BufferedOutputStream bos = null;
        try {
            File downLoadFile = new File(bag, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(downLoadFile));
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), CompanyResoucesApplyRecordExportVO.class, failed);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("批量发货失败记录，保存服务器异常：" + e.getMessage(), e);
            return ResultVo.Fail("批量发货失败记录，保存异常");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

    /**
     * 企业资源申请取消
     * auth: HZ
     * time:2020/03/25
     */
    @PostMapping("/cancel")
    public ResultVo cancel(@JsonParam Long id){
        //验证登陆
        getCurrUser();
        try{
            CompanyResoucesApplyRecordEntity companyResoucesApplyRecordEntity=companyResoucesApplyRecordService.findById(id);
            if(null == companyResoucesApplyRecordEntity){
                return ResultVo.Fail("企业证件申请不存在");
            }
            OrderEntity order = orderService.queryByOrderNo(companyResoucesApplyRecordEntity.getOrderNo());
            if(null == order){
                return ResultVo.Fail("订单不存在");
            }
            orderService.cancelCertOrderAdmin(order.getUserId(),companyResoucesApplyRecordEntity.getOemCode(),companyResoucesApplyRecordEntity.getOrderNo());
        }catch (BusinessException e){
            return  ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 企业资源申请详情
     */
    @PostMapping("/companyResoucesApplyRecordDetail")
    public ResultVo companyResoucesApplyRecordDetail(@JsonParam Long id) {
        getCurrUseraccount();
        return  ResultVo.Success(companyResoucesApplyRecordService.findById(id));
    }
    /**
     * 调用快递100接口获取快递信息
     */
    @PostMapping("/getExpress")
    public ResultVo getExpress(@JsonParam String courierCompanyName,@JsonParam  String courierNumber,@JsonParam String orderNo) {
        CurrUser currUser = getCurrUser();

        /*if(StringUtils.isBlank(courierCompanyName)){
            return ResultVo.Fail("快递名称不能为空");
        }
        if(StringUtils.isBlank(courierNumber)){
            return ResultVo.Fail("快递单号不能为空");
        }
        if(StringUtils.isBlank(orderNo)){
            return ResultVo.Fail("订单号不能为空");
        }*/
        try{
           List<LogisticsInfoEntity> list =logisticsInfoService.queryLogisticsInfoList(courierCompanyName.trim(),courierNumber.trim(),orderNo.trim(),currUser.getUseraccount() );
           if(list.isEmpty()){
               return ResultVo.Success("暂无物流信息，可根据快递单号在快递公司官网查询。");
           }
           return  ResultVo.Success(list);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 确认签收弹窗数据
     */
    @PostMapping("/confirmReceipt/open")
    public ResultVo confirmReceiptOpen(@JsonParam Long id,@JsonParam String applyResouces) {
        //登路校验
        getCurrUseraccount();
        CompanyResoucesApplyRecordEntity companyResoucesApplyRecordEntity=companyResoucesApplyRecordService.findById(id);
        if(companyResoucesApplyRecordEntity.getStatus()!=3){
            return ResultVo.Fail("只有待签收得订单可以确认签收。");
        }
        int  applyReturnNumber=companyResoucesApplyRecordEntity.getApplyResouces().split(",").length;
        int  actualReturnNumber=applyResouces.split(",").length;
        Map<String ,Object> map=new HashMap<>();
        map.put("applyReturnNumber",applyReturnNumber);
        map.put("actualReturnNumber",actualReturnNumber);
        return  ResultVo.Success(map);
    }
    /**
     * 企业资源申请确认签收
     */
    @PostMapping("/confirmReceipt")
    public ResultVo confirmReceipt(@JsonParam Long id,@JsonParam String applyResouces) {
        //登路校验
        getCurrUseraccount();
        CompanyResoucesApplyRecordEntity companyResoucesApplyRecordEntity=companyResoucesApplyRecordService.findById(id);
        if(companyResoucesApplyRecordEntity.getStatus()!=3){
            return ResultVo.Fail("只有待签收得订单可以确认签收。");
        }
        OrderEntity entity = orderService.queryByOrderNo(companyResoucesApplyRecordEntity.getOrderNo());
        //修改订单状态
        companyResoucesApplyRecordEntity.setStatus(CompanyResoucesApplyRecordStatusEnum.SIGNED.getValue());
        companyResoucesApplyRecordEntity.setUpdateTime(new Date());
        companyResoucesApplyRecordEntity.setUpdateUser(getCurrUseraccount());
        entity.setOrderStatus(CompanyResoucesApplyRecordStatusEnum.SIGNED.getValue());
        entity.setUpdateUser(getCurrUseraccount());
        entity.setUpdateTime(new Date());
        orderService.updateCompangApplyStatus(companyResoucesApplyRecordEntity, entity);


        //修改对应证件的归属地
        companyResourcesAddressService.updateBatchCompanyResourcesAddress(applyResouces,id,getCurrUseraccount());

        return  ResultVo.Success();
    }

}
