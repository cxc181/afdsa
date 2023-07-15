package com.yuqian.itax.admin.controller.order;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.enums.IncomeLevyTypeEnum;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxPolicyService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.BusinessIncomeRuleService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.ParkTaxBillChangeEntity;
import com.yuqian.itax.tax.entity.TParkTaxBillEntity;
import com.yuqian.itax.tax.entity.dto.FillCostDTO;
import com.yuqian.itax.tax.entity.dto.TaxAuditDTO;
import com.yuqian.itax.tax.entity.query.*;
import com.yuqian.itax.tax.entity.vo.*;
import com.yuqian.itax.tax.enums.ParkTaxBillStatusEnum;
import com.yuqian.itax.tax.enums.TaxBillStatusEnum;
import com.yuqian.itax.tax.service.*;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.util.Lists;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("taxBill")
@Slf4j
public class TaxBillController extends BaseController {

    @Autowired
    ParkTaxBillService parkTaxBillService;
    @Autowired
    ParkTaxBillFileRecordService parkTaxBillFileRecordService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    CompanyTaxBillService companyTaxBillService;
    @Autowired
    MemberCompanyService memberCompanyService;
    @Autowired
    TaxPolicyService taxPolicyService;
    @Autowired
    OssService ossService;
    @Autowired
    TaxBillCredentialsRecordService taxBillCredentialsRecordService;
    @Autowired
    BusinessIncomeRuleService businessIncomeRuleService;
    @Autowired
    ParkTaxBillChangeService parkTaxBillChangeService;
    @Autowired
    CompanyTaxCostItemService  companyTaxCostItemService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private  CompanyTaxBillChangeService companyTaxBillChangeService;

    /**
     * 园区税单列表查询（分页）
     */
    @PostMapping("park/page")
    public ResultVo queryParkTaxBillPageInfo(@RequestBody ParkTaxBillQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(null!=userEntity.getPlatformType()&&(userEntity.getPlatformType()!=3 && userEntity.getPlatformType()!=1)){
            return ResultVo.Fail("不是园区用户不允许查看");
        }
        if(null!=userEntity.getPlatformType()&&userEntity.getPlatformType()==3){
            query.setParkId(userEntity.getParkId());
        }
        PageInfo<ParkTaxBillVO> page = parkTaxBillService.queryParkTaxBillPageInfo(query);
        return ResultVo.Success(page);
    }
    /**
     * 企业税单列表（分页）
     */
    @PostMapping("company/page")
    public ResultVo queryCompanyTaxBillPageInfo(@RequestBody CompanyTaxBillQueryAdmin query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(StringUtil.isNotBlank(getRequestHeadParams("oemCode"))){
            query.setOemCode(getRequestHeadParams("oemCode"));
        }
        if(null!=userEntity.getPlatformType()&&userEntity.getPlatformType()==3){
            query.setParkId(userEntity.getParkId());
        }
        if(query.getIitVouchersStatus()!=null||query.getVatVouchersStatus()!=null){
            if(query.getTaxBillYear()==null&&query.getTaxBillSeasonal()==null){
                return ResultVo.Fail("请先选择税期！");
            }
        }
        if(query.getInvoiceFlag()!=null&&query.getTaxBillYear()==null&&query.getTaxBillSeasonal()==null){
            return ResultVo.Fail("请先选择税期！");
        }
        PageInfo<CompanyTaxBillListVOAdmin> page = companyTaxBillService.queryCompanyTaxBillPageInfo(query);
        for (CompanyTaxBillListVOAdmin vo : page.getList()) {
            vo.setIitVoucherPic(ossService.getPrivateImgUrl(vo.getIitVoucherPic()));
            vo.setVatVoucherPic(ossService.getPrivateImgUrl(vo.getVatVoucherPic()));
            if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(vo.getIncomeLevyType())) {
                CompanyTaxBillVO taxBillVO = new CompanyTaxBillVO();
                taxBillVO.setCompanyTaxBillId(vo.getId());
                companyTaxBillService.detailOfAuditCollection(taxBillVO);
                if (TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(vo.getTaxBillStatus())) { // 待填报成本状态数据库可能存有所得税计算的数据，需要清0展示
                    vo.setShouldTaxMoney(BigDecimal.ZERO);
                    vo.setSupplementTaxMoney(BigDecimal.ZERO);
                    vo.setRecoverableTaxMoney(BigDecimal.ZERO);
                } else if (!TaxBillStatusEnum.TO_BE_CHECK.getValue().equals(vo.getTaxBillStatus())) {
                    vo.setShouldTaxMoney(BigDecimal.valueOf(taxBillVO.getShouldTaxMoney()).divide(new BigDecimal(100)));
                    vo.setSupplementTaxMoney(BigDecimal.valueOf(taxBillVO.getSupplementTaxMoney()).divide(new BigDecimal(100)));
                    vo.setRecoverableTaxMoney(BigDecimal.valueOf(taxBillVO.getRecoverableTaxMoney()).divide(new BigDecimal(100)));
                }
                vo.setAlreadyTaxMoney(BigDecimal.valueOf(taxBillVO.getAlreadyTaxMoney()).divide(new BigDecimal(100)));
            }
        }
        return ResultVo.Success(page);

    }

    /**
     * 企业税单列表（导出）
     */
    @PostMapping("company/export")
    public ResultVo queryCompanyTaxBillExport(@RequestBody CompanyTaxBillQueryAdmin query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(null!=userEntity.getPlatformType()&&userEntity.getPlatformType()==3){
            query.setParkId(userEntity.getParkId());
        }
        if (userEntity.getPlatformType() == 2){
            query.setOemCode(userEntity.getOemCode());
        }
        List<CompanyTaxBillListVOAdmin> lists = companyTaxBillService.queryCompanyTaxBillList(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        for (CompanyTaxBillListVOAdmin vo : lists) {
            vo.setIitVoucherPic(ossService.getPrivateImgUrl(vo.getIitVoucherPic()));
            vo.setVatVoucherPic(ossService.getPrivateImgUrl(vo.getVatVoucherPic()));
            if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(vo.getIncomeLevyType())
                    && !TaxBillStatusEnum.TO_BE_CHECK.getValue().equals(vo.getTaxBillStatus())) {
                CompanyTaxBillVO taxBillVO = new CompanyTaxBillVO();
                taxBillVO.setCompanyTaxBillId(vo.getId());
                companyTaxBillService.detailOfAuditCollection(taxBillVO);
                vo.setShouldTaxMoney(BigDecimal.valueOf(taxBillVO.getShouldTaxMoney()).divide(new BigDecimal(100)));
                vo.setAlreadyTaxMoney(BigDecimal.valueOf(taxBillVO.getAlreadyTaxMoney()).divide(new BigDecimal(100)));
                vo.setSupplementTaxMoney(BigDecimal.valueOf(taxBillVO.getSupplementTaxMoney()).divide(new BigDecimal(100)));
                vo.setRecoverableTaxMoney(BigDecimal.valueOf(taxBillVO.getRecoverableTaxMoney()).divide(new BigDecimal(100)));
            }
            if (TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(vo.getTaxBillStatus())) { // 待填报成本状态数据库可能存有所得税计算的数据，需要清0展示
                vo.setShouldTaxMoney(BigDecimal.ZERO);
                vo.setSupplementTaxMoney(BigDecimal.ZERO);
                vo.setRecoverableTaxMoney(BigDecimal.ZERO);
            }
        }
        try {
            exportExcel("企业列表", "企业税单", CompanyTaxBillListVOAdmin.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("企业列表导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }


    /**
     *  园区税单上传文件模板
     */
    @PostMapping("park/upload")
    public ResultVo uploadFile(@RequestBody ParkTaxBillQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(query.getFileUrl()==null){
            return ResultVo.Fail("请上传文件");
        }
        if(query.getTaxBillSeasonal()==null){
            return ResultVo.Fail("请选择园区税单");
        }
        if(query.getTaxBillYear()==null){
            return ResultVo.Fail("请选择园区税单");
        }
        if(query.getId()==null){
            return ResultVo.Fail("请选择园区税单");
        }
        TParkTaxBillEntity tParkTaxBillEntity=parkTaxBillService.findById(query.getId());
        if(tParkTaxBillEntity==null){
            return  ResultVo.Fail("园区税单不存在");
        }
        if(tParkTaxBillEntity.getTaxBillStatus()!=0 && tParkTaxBillEntity.getTaxBillStatus()!=2){
            return ResultVo.Fail("园区税单状态不为待上传和待确认，请确认是否需要上传文件");
        }

        //获取excel文件
        List<ParkTaxBillUploadVO> list = getInputStream(ossService.getPrivateImgUrl(query.getFileUrl()));
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("存在字段格式错误，解析失败");
        }
        JSONObject json = new JSONObject();
        json.put("list", list);
        json.put("taxBillYear", query.getTaxBillYear());
        json.put("fileUrl", query.getFileUrl());
        json.put("parkTaxBill", query.getId());
        json.put("taxBillSeasonal", query.getTaxBillSeasonal());
        json.put("account", getCurrUseraccount());
        json.put("parkId", tParkTaxBillEntity.getParkId());
        rabbitTemplate.convertAndSend("parkTaxBill", json);

        tParkTaxBillEntity.setTaxBillStatus(1);
        parkTaxBillService.editByIdSelective(tParkTaxBillEntity);
        return ResultVo.Success("文件上传成功！正在进行文件解析，请稍后");
    }

    /**
     * 立即确认
     */
    @PostMapping("park/confirmation")
    public ResultVo confirmation(@JsonParam Long id ){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        boolean flag = true;
        String redisTime = (System.currentTimeMillis() + 10000) + ""; // redis标识值
        try{
            boolean lockResult = redisService.lock(RedisKey.LOCK_BILL_CONFIRMATION + id, redisTime, 120);
            if(!lockResult){
                flag = false;
                throw new BusinessException("请勿重复提交！");
            }
            parkTaxBillService.confirmation(id,getCurrUseraccount());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }finally {
            if(flag) {
                redisService.unlock(RedisKey.LOCK_INVOICE_CREATER_MEMBER_KEY + id, redisTime);
            }
        }
        return ResultVo.Success("操作成功");
    }

    /**
     * 立即推送
     */
    @PostMapping("park/send")
    public ResultVo send(@JsonParam Long id ){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        TParkTaxBillEntity tParkTaxBillEntity=parkTaxBillService.findById(id);
        if (tParkTaxBillEntity != null && tParkTaxBillEntity.getTaxBillStatus().equals(ParkTaxBillStatusEnum.PUSHING.getValue())){
            return ResultVo.Fail("请勿重复推送");
        }
        tParkTaxBillEntity.setTaxBillStatus(ParkTaxBillStatusEnum.PUSHING.getValue());
        parkTaxBillService.editByIdSelective(tParkTaxBillEntity);

        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("account", getCurrUseraccount());
        rabbitTemplate.convertAndSend("parkSend", json);

        //增加历史记录
        ParkTaxBillChangeEntity parkTaxBillChangeEntity=new ParkTaxBillChangeEntity();

        BeanUtils.copyProperties(tParkTaxBillEntity,parkTaxBillChangeEntity);
        parkTaxBillChangeEntity.setId(null);
        parkTaxBillChangeEntity.setParkBillsId(tParkTaxBillEntity.getId());
        parkTaxBillChangeEntity.setRemark("操作立即推送");
        parkTaxBillChangeEntity.setAddTime(new Date());
        parkTaxBillChangeEntity.setAddUser(getCurrUseraccount());
        parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
        return ResultVo.Success("操作成功");
    }

    /**
     * 企业税单详情
     */
    @PostMapping("company/detail")
    public ResultVo detail(@JsonParam Long id ){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        CompanyTaxBillEntity companyTaxBillEntity=companyTaxBillService.findById(id);
        if(companyTaxBillEntity==null){
            return ResultVo.Fail("企业税单不存在");
        }
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(companyTaxBillEntity.getCompanyId());
        CompanyTaxBillVO vo=new CompanyTaxBillVO(companyTaxBillEntity,memberCompanyEntity);
        // 查账征收方式的税单，所得税需要使用年度数据
        if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(vo.getIncomeLevyType())) {
            companyTaxBillService.detailOfAuditCollection(vo);
            if (TaxBillStatusEnum.TO_BE_WRITE_COST.getValue().equals(vo.getTaxBillStatus())) { // 待填报成本状态数据库可能存有所得税计算的数据，需要清0展示
                vo.setIncomeShouldTaxMoney(0L);
                vo.setIncomeSupplementTaxMoney(0L);
                vo.setIncomeRecoverableTaxMoney(0L);
                vo.setIncomeRate(BigDecimal.ZERO);
                vo.setIncomeTaxableIncomeAmount(0L);
                vo.setTaxableIncomeRate(BigDecimal.ZERO);
                vo.setShouldTaxMoney(0L);
                vo.setSupplementTaxMoney(0L);
                vo.setRecoverableTaxMoney(0L);
                vo.setYearCostAmount(null == vo.getYearCostAmount() ? 0L : (null == vo.getQuarterCostAmount() ? vo.getYearCostAmount() : vo.getYearCostAmount() - vo.getQuarterCostAmount()));
                vo.setQuarterCostAmount(0L);
            }
        }
        vo.setLevyWay(companyTaxBillEntity.getIncomeLevyWay());
        vo.setSignImg(ossService.getPrivateImgUrl(vo.getSignImg()));
        return ResultVo.Success(vo);
    }
    /**
     * 下载解析结果
     */
    @PostMapping("park/download/results")
    public ResultVo downloadResults(@RequestBody ParkTaxBillFileRecordQuery query ){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        List<ParkTaxBillUploadVO> lists=parkTaxBillFileRecordService.queryparkTaxBillFileRecordByParkTaxBillId(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("解析结果", "解析结果", ParkTaxBillUploadVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("解析结果导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }

    /**
     * 服务器获取excel转成集合
     * @param uri
     * @return
     */
    public static List<ParkTaxBillUploadVO> getInputStream(String uri) {
        URL url;
        HttpURLConnection con = null;
        try {
            url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();
            int httpResult = con.getResponseCode();
            if(httpResult == HttpURLConnection.HTTP_OK){
                ImportParams params = new ImportParams();
                params.setTitleRows(0);
                params.setHeadRows(1);
                return ExcelImportUtil.importExcel(con.getInputStream(), ParkTaxBillUploadVO.class, params);
            }
        } catch (Exception e) {
            log.error("读取oss上文件异常");
            log.error(e.getMessage(), e);
        } finally {
            if(con != null) {
                con.disconnect();
            }
        }
        return null;
    }

    /**
     * 上传完税凭证
     */
    @PostMapping("vouchers/upload")
    public ResultVo uploadVoucherFile(@RequestBody ParkTaxBillUploadVoucherQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try {
            parkTaxBillService.uploadVouchers(query,getCurrUseraccount());
        }catch (Exception e){
            log.info(e.getMessage());
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 下载失败文件
     */
    @PostMapping("vouchers/download/results")
    public ResultVo vouchersDownloadResults(@RequestBody ParkTaxBillUploadVoucherQuery query ){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        List<TaxBillCredentialsRecordVO> lists=taxBillCredentialsRecordService.queryTaxBillCredentialsRecordByStatus(1,query.getId());
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("本期上传无失败数据");
        }
        try {
            exportExcel("失败文件", "失败文件", TaxBillCredentialsRecordVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("失败文件导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }

    /**
     * 重新生成税单
     */
    @PostMapping("rebuild")
    public ResultVo rebuild(@JsonParam Long id ){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(id==null){
            return ResultVo.Fail("请选择园区税单");
        }
        // 查询园区税单
        TParkTaxBillEntity parkTaxBill = parkTaxBillService.findById(id);
        if (null == parkTaxBill) {
            return ResultVo.Fail("未查询到园区税单信息");
        }

        // 发送mq消息
        JSONObject json = new JSONObject();
        json.put("type", 0);
        json.put("id",id);
        json.put("remark", "重新生成税单");
        json.put("account", getCurrUseraccount());
        rabbitTemplate.convertAndSend("rebuildParkTaxBill", json);
        return ResultVo.Success();
    }

    /**
     * 下载税单
     */
    @PostMapping("download")
    public ResultVo download(@RequestBody CompanyTaxBillQueryAdmin query ){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (query.getParkId() == null){
            return ResultVo.Fail("园区id不能为空");
        }
        TParkTaxBillEntity parkTaxBillEntity = parkTaxBillService.findById(query.getParkTaxBillId());
        if (parkTaxBillEntity != null){
            query.setTaxBillYear(parkTaxBillEntity.getTaxBillYear());
            query.setTaxBillSeasonal(parkTaxBillEntity.getTaxBillSeasonal());
        }
        ParkEntity entity = parkService.findById(query.getParkId());
        List<DownloadCompanyTaxBillByAccountsVO> list = new ArrayList<>();
        List<DownloadCompanyTaxBillVOAdmin> lists = new ArrayList<>();
        if (entity.getIncomeLevyType() != null && entity.getIncomeLevyType().equals(1)){
             list = companyTaxBillService.queryDownloadCompanyTaxBillListByAccounts(query);
        }else{
             lists = companyTaxBillService.queryDownloadCompanyTaxBillList(query);
        }
        if (CollectionUtil.isEmpty(lists) && CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        if(query.getParkTaxBillId()==null){
            return ResultVo.Fail("请选择园区税单下载对应得企业税单");
        }
        try {
            if (entity.getIncomeLevyType() != null && entity.getIncomeLevyType().equals(1)){
                exportExcel("税单", "税单", DownloadCompanyTaxBillByAccountsVO.class, list);
            }else {
                exportExcel("税单", "税单", DownloadCompanyTaxBillVOAdmin.class, lists);
            }
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("税单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }


    /**
     * 历史记录
     */
    @PostMapping("change")
    public ResultVo change(@RequestBody @JsonParam CompanyTaxBillQueryAdmin queryAdmin ){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        PageInfo<ParkTaxBillChangeEntity> pageInfo=parkTaxBillChangeService.queryPageInfo(queryAdmin);
        return ResultVo.Success(pageInfo);
    }

    /**
     * 企业税单成本明细
     */
    @PostMapping("companyTaxCostItems")
    public ResultVo companyTaxCostItems(@JsonParam Long companyTaxId){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(companyTaxId== null){
            return ResultVo.Fail("企业税单id不能为空");
        }
        CompanyTaxCostVo vo=companyTaxCostItemService.getCoseItemsByCompanyTaxId(companyTaxId);
        return ResultVo.Success(vo);
    }

    /**
     * 罚款凭证
     * @param companyTaxId
     * @return
     */
    @PostMapping("getTicketPic")
    public ResultVo getTicketPic(@JsonParam Long companyTaxId){
        if (companyTaxId == null){
            return ResultVo.Fail("企业税单id不能为空");
        }
        CompanyTaxBillEntity entity =  companyTaxBillService.findById(companyTaxId);
        if (entity == null){
            return ResultVo.Fail("企业税单id错误");
        }
        String picUrl = ossService.getPrivateImgUrl(entity.getTicketPic());
        return ResultVo.Success(picUrl);
    }
    /**
     * 批量确认报税
     */
    @PostMapping("batchTaxDeclaration")
    public ResultVo batchTaxDeclaration(@RequestParam("file") MultipartFile file,@RequestParam Long parkTaxBillId){
        CurrUser currUser = getCurrUser();
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<TaxDeclarationVO> excelList = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            excelList = ExcelImportUtil.importExcel(file.getInputStream(), TaxDeclarationVO.class, params);
        } catch (Exception e) {
            log.error("批量确认报税异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(excelList)) {
            return ResultVo.Fail("文件内容为空");
        }
        if (excelList.size()<1){
            return ResultVo.Fail("导入模板不正确");
        }
        Map<String,Object> map = companyTaxBillService.handleBill(excelList,parkTaxBillId,currUser.getUseraccount());
        Object fail = map.get("fail");
        Object success = map.get("success");
        List<TaxDeclarationVO> failList = Convert.toList(TaxDeclarationVO.class,fail);
        List<CompanyTaxBillEntity> successList = Convert.toList(CompanyTaxBillEntity.class,success);
        Map<String, Object> result = new HashMap<>();
        result.put("success", successList.size());
        result.put("failed", failList.size());
        //如果无失败则不生成下载文件
        if (CollectionUtil.isEmpty(failList)){
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
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), TaxDeclarationVO.class, failList);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("批量确认报税异常：" + e.getMessage(), e);
            return ResultVo.Fail("批量确认报税异常");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

    /**
     * 核对税单下载
     * @param parkTaxBillId
     * @return
     */
    @PostMapping("downloadApprovedTaxBill")
    public ResultVo downloadApprovedTaxBill(@JsonParam Long parkTaxBillId){
        if (parkTaxBillId == null){
            return ResultVo.Fail("园区税单id不能为空");
        }
        List<ApprovedTaxBillVO> list = companyTaxBillService.getApprovedTaxBill(parkTaxBillId);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("核对税单", "核对税单", ApprovedTaxBillVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("税单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }

    /**
     * 添加扣除金额
     * @param file
     * @param parkTaxBillId
     * @return
     */
    @PostMapping("deductionAmount")
    public ResultVo deductionAmount(@RequestParam("file") MultipartFile file,@RequestParam Long parkTaxBillId){
        CurrUser currUser = getCurrUser();
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<DeductionAmountVO> excelList = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            excelList = ExcelImportUtil.importExcel(file.getInputStream(), DeductionAmountVO.class, params);
        } catch (Exception e) {
            log.error("添加扣除金额异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(excelList)) {
            return ResultVo.Fail("文件内容为空");
        }
        if (excelList.size()<1){
            return ResultVo.Fail("导入模板不正确");
        }
        Map<String,Object> map =  companyTaxBillService.batchAddAmount(excelList,parkTaxBillId,currUser.getUseraccount());
        Map<String, Object> result = new HashMap<>();
        Object fail = map.get("fail");
        Object success = map.get("success");
        List<DeductionAmountVO> failList = Convert.toList(DeductionAmountVO.class,fail);
        List<DeductionAmountVO> successList = Convert.toList(DeductionAmountVO.class,success);
        result.put("success", successList.size());
        result.put("failed", failList.size());
        //如果无失败则不生成下载文件
        if (CollectionUtil.isEmpty(failList)){
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
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), DeductionAmountVO.class, failList);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("添加扣除金额异常：" + e.getMessage(), e);
            return ResultVo.Fail("添加扣除金额异常");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

    /**
     * 充值税单校验接口
     * @param companyTaxBillId
     * @return
     */
    @PostMapping("rechargeBillInspect")
    public ResultVo rechargeBillInspect(@JsonParam  Long companyTaxBillId){
        if (companyTaxBillId == null){
            return ResultVo.Fail("企业税单id不能为空");
        }
        CompanyTaxBillEntity entity =  companyTaxBillService.findById(companyTaxBillId);
        if (entity == null){
            return ResultVo.Fail("企业税单id错误");
        }
        if (entity.getIncomeLevyType() != null && entity.getIncomeLevyType().equals(2)){
            return ResultVo.Fail("当前园区征收方式不支持重置税单");
        }
        if(entity.getTaxBillStatus() != 2 && entity.getTaxBillStatus() != 7 && entity.getTaxBillStatus() != 8){
            return ResultVo.Fail("当前税单状态不允许重置税单");
        }
        Map<String,Object> map = new HashMap<>();
        List<CompanyTaxBillEntity> list = companyTaxBillService.getCompanyTaxBillByCompanyIdAndAddTime(entity.getCompanyId(),entity.getAddTime());
        if (CollectionUtil.isNotEmpty(list)){
            map.put("content","该企业存在已计算税费并签字确认的后续税期的税单，请把后续的税单重置后再操作！");
            map.put("type",1);
        }else{
            List<CompanyTaxBillEntity> companyTaxBillEntityList = companyTaxBillService.getCompanyTaxBillByStatus(entity.getCompanyId());
            if (CollectionUtil.isNotEmpty(companyTaxBillEntityList)){
                for (CompanyTaxBillEntity entity1:companyTaxBillEntityList){
                    if (entity1.getAddTime().compareTo(entity.getAddTime()) > 0){
                        map.put("content","该企业存在待填报成本的后续税期的税单，若客户正在填报成本，重置后可能影响后续税单税费计算，请确定是否继续？");
                        map.put("type",2);
                        break;
                    }
                }
            }
        }
        return ResultVo.Success(map);
    }

    /**
     * 重置税单
     * @param companyTaxBillId
     * @param
     * @param iitDeductionAmount
     * @return
     */
    @PostMapping("rechargeBill")
        public ResultVo rechargeBill(@JsonParam Long companyTaxBillId,@JsonParam  Long vatShouldTaxMoney,@JsonParam Long additionalShouldTaxMoney,@JsonParam Long iitDeductionAmount){
        CurrUser currUser = getCurrUser();
        if (companyTaxBillId == null){
            return ResultVo.Fail("参数不正确");
        }
        if (null == vatShouldTaxMoney) {
            return ResultVo.Fail("应缴增值税不能为空");
        }
        if (null == additionalShouldTaxMoney) {
            return ResultVo.Fail("应缴附加税不能为空");
        }
        if (null == iitDeductionAmount) {
            return ResultVo.Fail("所得税扣除金额不能为空");
        }
        CompanyTaxBillEntity entity = companyTaxBillService.findById(companyTaxBillId);
        if (entity == null){
            return ResultVo.Fail("企业税单id不正确");
        }
        if (entity.getInvoiceMoney() == null || entity.getInvoiceMoney().equals(0L)){
            if (additionalShouldTaxMoney >0){
                return ResultVo.Fail("该企业本季无开票，不能添加大于0的应缴附加税");
            }
            if (vatShouldTaxMoney >0){
                return ResultVo.Fail("该企业本季无开票，不能添加大于0的应缴增值税");
            }
        }

        String remark = "重置税单，应缴增值税为"+vatShouldTaxMoney.doubleValue()/100+"元，应缴附加税为"
                +additionalShouldTaxMoney.doubleValue()/100+"元，个税扣除金额为"
                +iitDeductionAmount.doubleValue()/100+"元。";
        if (!Objects.equals(vatShouldTaxMoney, entity.getVatShouldTaxMoney())) {
            entity.setVatShouldTaxMoney(vatShouldTaxMoney);
            Long quarterIncomeAmount = entity.getInvoiceMoney() - vatShouldTaxMoney;
            // 季度不含税收入
            entity.setQuarterIncomeAmount(quarterIncomeAmount > 0L ? quarterIncomeAmount : 0L);
            // 年累计不含税收入
            Example example = new Example(CompanyTaxBillEntity.class);
            example.createCriteria().andEqualTo("companyId", entity.getCompanyId())
                    .andEqualTo("taxBillYear", entity.getTaxBillYear())
                    .andEqualTo("taxBillSeasonal", entity.getTaxBillSeasonal() - 1)
                    .andNotEqualTo("taxBillStatus", TaxBillStatusEnum.CANCELLED.getValue());
            List<CompanyTaxBillEntity> list = companyTaxBillService.selectByExample(example);
            if (CollectionUtil.isEmpty(list)) {
                entity.setYearIncomeAmount(quarterIncomeAmount);
            } else {
                entity.setYearIncomeAmount(quarterIncomeAmount + list.get(0).getYearIncomeAmount());
            }
            // 增值税应退应补
            Long l = vatShouldTaxMoney - entity.getVatAlreadyTaxMoney();
            if (l >= 0L) {
                entity.setVatSupplementTaxMoney(l);
                entity.setVatRecoverableTaxMoney(0L);
            } else {
                entity.setVatSupplementTaxMoney(0L);
                entity.setVatRecoverableTaxMoney(-l);
            }
        }
        if (!Objects.equals(additionalShouldTaxMoney, entity.getAdditionalShouldTaxMoney())) {
            entity.setAdditionalShouldTaxMoney(additionalShouldTaxMoney);
            Long l = additionalShouldTaxMoney - entity.getAdditionalAlreadyTaxMoney();
            if (l >= 0L) {
                entity.setAdditionalSupplementTaxMoney(l);
                entity.setAdditionalRecoverableTaxMoney(0L);
            } else {
                entity.setAdditionalSupplementTaxMoney(0L);
                entity.setAdditionalRecoverableTaxMoney(-l);
            }
        }

        entity.setIitDeductionAmount(iitDeductionAmount);

        entity.setCostItemImgs(null);
        entity.setTicketFreeIncomeAmount(null);
        entity.setTaxBillStatus(7);
        entity.setTaxableIncomeRate(new BigDecimal(0.00));
        entity.setIncomeRate(new BigDecimal(0.00));
        entity.setIncomeShouldTaxMoney(0L);
        entity.setRecoverableTaxMoney(0L);
        entity.setSupplementTaxMoney(0L);
        entity.setIncomeRecoverableTaxMoney(0L);
        entity.setIncomeSupplementTaxMoney(0L);
        entity.setShouldTaxMoney(0L);
        entity.setQuarterCostAmount(0L);
        entity.setYearCostAmount(0L);
        entity.setSignImg("");
        entity.setIncomeTaxYearFreezeAmount(0L);
        entity.setIncomeTaxRefundAmount(0L);
        companyTaxBillService.rechargeCompanyBill(entity,currUser.getUseraccount(),remark);
        return ResultVo.Success();
    }

    /**
     * 企业报税
     * @param
     * @return
     */
    @PostMapping("declareTaxByCompany")
    public ResultVo declareTaxByCompany(@RequestBody JSONObject jsonObject){
        CurrUser currUser = getCurrUser();
        Long companyTaxBillId = jsonObject.getLong("companyTaxBillId");
        if (companyTaxBillId == null){
            return ResultVo.Fail("企业税单id不能为空");
        }
        String remark = jsonObject.getString("remark");
        if (StringUtil.isNotBlank(remark) && remark.length()>50){
            return ResultVo.Fail("备注不能超过50个字符");
        }
        CompanyTaxBillEntity entity =  companyTaxBillService.findById(companyTaxBillId);
        if (entity == null){
            return ResultVo.Fail("企业税单id错误");
        }
        // 查询企业
        MemberCompanyEntity company = memberCompanyService.findById(entity.getCompanyId());
        if (null == company) {
            return ResultVo.Fail("未查询到企业信息");
        }
        //  个税可退税额不能超过实际应退金额
        Long incomeTaxRefundAmount = jsonObject.getLong("incomeTaxRefundAmount");
        if (incomeTaxRefundAmount != null){
            Pattern pattern=Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
            Matcher match=pattern.matcher(incomeTaxRefundAmount.toString());
            if (!match.matches()){
                return ResultVo.Fail("个税可退税额只支持两位小数");
            }
            if (incomeTaxRefundAmount > entity.getIncomeTaxYearFreezeAmount()){
                return ResultVo.Fail("个税可退税额不能超过实际应退金额");
            }
            entity.setIncomeTaxRefundAmount(incomeTaxRefundAmount);
            entity.setIncomeTaxYearFreezeAmount(entity.getIncomeTaxYearFreezeAmount() - incomeTaxRefundAmount);
        }

        PendingTaxBillQuery query = new PendingTaxBillQuery();
        query.setEin(company.getEin());
        query.setCompanyId(entity.getCompanyId());
        query.setRange(2);
        query.setStatusRange(2);
        List<PendingTaxBillVO> taxBillVOList = companyTaxBillService.pendingTaxBill(query);
        if (CollectionUtil.isNotEmpty(taxBillVOList)){
            for (PendingTaxBillVO vo:taxBillVOList){
                if(isBeforeByBill(vo,entity)){
                    return ResultVo.Fail("该企业存在未完成申报的历史税期的税单");
                }
            }
        }
        if (StringUtil.isNotBlank(remark)){
            entity.setRemark(remark);
        }
        companyTaxBillService.declareTax(entity,currUser.getUseraccount());
        return ResultVo.Success();
    }
    private boolean isBeforeByBill(PendingTaxBillVO vo,CompanyTaxBillEntity entity){
        if (vo == null || entity == null){
            return false;
        }
        if (entity.getTaxBillYear() > vo.getTaxBillYear()){
            return true;
        }else if(entity.getTaxBillYear() == vo.getTaxBillYear()){
            if (entity.getTaxBillSeasonal() > vo.getTaxBillSeasonal()){
                return true;
            }
        }
        return false;
    }

    /**
     * 上传凭证
     * @param companyTaxBillId
     * @param vatVoucherPicUrl
     * @param iitVoucherPic
     * @param ticketPicUrl
     * @return
     */
    @PostMapping("updatevouchers")
    public ResultVo updatevouchers(@JsonParam Long companyTaxBillId,@JsonParam String vatVoucherPicUrl,@JsonParam String iitVoucherPic,@JsonParam String ticketPicUrl){
        CurrUser currUser = getCurrUser();
        if (companyTaxBillId == null){
            return ResultVo.Fail("企业税单id不能为空");
        }
        CompanyTaxBillEntity entity =  companyTaxBillService.findById(companyTaxBillId);
        if (!(entity.getTaxBillStatus().equals(8)
                ||entity.getTaxBillStatus().equals(1)
                ||entity.getTaxBillStatus().equals(3)
                ||entity.getTaxBillStatus().equals(4)
                ||entity.getTaxBillStatus().equals(5)
                ||entity.getTaxBillStatus().equals(10))){
            return ResultVo.Fail("非待申报状态的税单不允许该操作");
        }
        if (entity.getGenerateType() != 2){
            return ResultVo.Fail("非企业注销生成的税单不允许该操作");
        }

        if (StringUtil.isNotBlank(vatVoucherPicUrl)){
            entity.setVatVoucherPic(vatVoucherPicUrl);
            entity.setVatVouchersStatus(2);
        }
        if (StringUtil.isNotBlank(iitVoucherPic)){
            entity.setIitVoucherPic(iitVoucherPic);
            entity.setIitVouchersStatus(2);
        }
       if (StringUtil.isNotBlank(ticketPicUrl)){
           entity.setTicketPic(ticketPicUrl);
       }
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(currUser.getUseraccount());
        companyTaxBillService.updateBillAndInsertChange(entity,"上传凭证");
        return ResultVo.Success();
    }

    /**
     * 获取企业税单历史记录
     * @param
     * @return
     */
    @PostMapping("getCompanyTaxBillChange")
    public ResultVo getCompanyTaxBillChange(@RequestBody CompanyTaxBillChangeQuery query){
        PageInfo<CompanyTaxBillChangeVO> page = companyTaxBillChangeService.getCompanyTaxBillChange(query);
        return ResultVo.Success(page);
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
        return ResultVo.Success(vo);
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
     * 企业税单填报成本
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("fillCost")
    public ResultVo fillCost(@RequestBody @Validated FillCostDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        // 填报成本
        dto.setOperator(getCurrUseraccount());
        dto.setSourceOfOperating(2);
        companyTaxBillService.fillCost(dto);
        return ResultVo.Success();
    }

    /**
     * 批量填报成本
     * @param file
     * @param parkTaxBillId
     * @return
     */
    @PostMapping("batchFillCost")
    public ResultVo batchFillCost(@RequestParam("file") MultipartFile file,@RequestParam Long parkTaxBillId){
        CurrUser currUser = getCurrUser();
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<TaxFillCostVO> excelList;
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            excelList = ExcelImportUtil.importExcel(file.getInputStream(), TaxFillCostVO.class, params);
        } catch (Exception e) {
            log.error("批量填报成本异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(excelList)) {
            return ResultVo.Fail("文件内容为空");
        }
        if (excelList.size()<1){
            return ResultVo.Fail("导入模板不正确");
        }
        Map<String,Object> map = companyTaxBillService.batchFillCost(excelList, parkTaxBillId, getCurrUseraccount());
        Object fail = map.get("fail");
        Object success = map.get("success");
        List<TaxFillCostVO> failList = Convert.toList(TaxFillCostVO.class,fail);
        List<CompanyTaxBillEntity> successList = Convert.toList(CompanyTaxBillEntity.class,success);
        Map<String, Object> result = new HashMap<>();
        result.put("success", successList.size());
        result.put("failed", failList.size());
        //如果无失败则不生成下载文件
        if (CollectionUtil.isEmpty(failList)){
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
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), TaxFillCostVO.class, failList);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("批量填报成本异常：" + e.getMessage(), e);
            return ResultVo.Fail("批量填报成本异常");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

    /**
     * 税单审核
     * @param
     * @return
     */
    @PostMapping("taxAudit")
    public ResultVo taxAudit(@RequestBody @Validated TaxAuditDTO dto, BindingResult result) {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        dto.setOperator(getCurrUseraccount());
        companyTaxBillService.taxAudit(dto);
        return ResultVo.Success();
    }

    /**
     * 批量审核税单
     * @param file
     * @param parkTaxBillId
     * @return
     */
    @PostMapping("batchTaxAudit")
    public ResultVo batchTaxAudit(@RequestParam("file") MultipartFile file,@RequestParam Long parkTaxBillId) {
        CurrUser currUser = getCurrUser();
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<TaxAuditVO> excelList;
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            excelList = ExcelImportUtil.importExcel(file.getInputStream(), TaxAuditVO.class, params);
        } catch (Exception e) {
            log.error("批量填报成本异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(excelList)) {
            return ResultVo.Fail("文件内容为空");
        }
        if (excelList.size()<1){
            return ResultVo.Fail("导入模板不正确");
        }
        Map<String,Object> map = companyTaxBillService.batchTaxAudit(excelList, parkTaxBillId, getCurrUseraccount());
        Object fail = map.get("fail");
        Object success = map.get("success");
        List<TaxAuditVO> failList = Convert.toList(TaxAuditVO.class,fail);
        List<CompanyTaxBillEntity> successList = Convert.toList(CompanyTaxBillEntity.class,success);
        Map<String, Object> result = new HashMap<>();
        result.put("success", successList.size());
        result.put("failed", failList.size());
        //如果无失败则不生成下载文件
        if (CollectionUtil.isEmpty(failList)){
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
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), TaxAuditVO.class, failList);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("批量财务审核异常：" + e.getMessage(), e);
            return ResultVo.Fail("批量财务审核异常");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

    /**
     * 重新提交税单
     * @param companyTaxBillId
     * @param incomeTaxRefundAmount
     * @return
     */
    @PostMapping("resubmitTaxBill")
    public ResultVo resubmitTaxBill(@JsonParam Long companyTaxBillId, @JsonParam Long incomeTaxRefundAmount) {
        companyTaxBillService.resubmitTaxBill(companyTaxBillId, incomeTaxRefundAmount, getCurrUseraccount());
        return ResultVo.Success();
    }
}
