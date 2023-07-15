package com.yuqian.itax.admin.controller.company;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.corporateaccount.service.CorporateAccountApplyOrderService;
import com.yuqian.itax.order.enums.InvoiceWayEnum;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.InvoiceRecordService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.RatifyTaxEntity;
import com.yuqian.itax.system.entity.vo.BusinessScopeTaxCodeVO;
import com.yuqian.itax.system.service.BusinessscopeTaxcodeService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.system.service.RatifyTaxService;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.query.PendingTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.PendingTaxBillVO;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.user.dao.CompanyTaxHostingMapper;
import com.yuqian.itax.user.dao.MemberCompanyMapper;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.entity.po.CompanyResourcesAddressPO;
import com.yuqian.itax.user.entity.po.CompanyTaxHostingBatchPO;
import com.yuqian.itax.user.entity.po.CompanyTaxHostingPO;
import com.yuqian.itax.user.entity.po.MemberCompanyPO;
import com.yuqian.itax.user.entity.query.MemberCompanyQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.*;

/**
 * 企业管理
 * @author：pengwei
 * @Date：2020/01/08 15:12
 * @version：1.0
 */
@RestController
@RequestMapping("member/company")
@Slf4j
public class MemberCompanyController extends BaseController {

    @Autowired
    OrderService orderService;
    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    private CompanyResourcesAddressService companyResourcesAddressService;

    @Autowired
    private RatifyTaxService ratifyTaxService;

    @Autowired
    private OssService ossService;
    @Autowired
    private CompanyResourcesAddressHistoryService companyResourcesAddressHistoryService;
    @Autowired
    CompanyInvoiceCategoryService companyInvoiceCategoryService;
    @Autowired
    CompanyTaxHostingService companyTaxHostingService;
    @Autowired
    InvoiceOrderService invoiceOrderService;
    @Autowired
    InvoiceRecordService invoiceRecordService;
    @Autowired
    CompanyTaxBillService companyTaxBillService;
    @Autowired
    CompanyCorporateAccountService companyCorporateAccountService;
    @Autowired
    CorporateAccountApplyOrderService corporateAccountApplyOrderService;
    @Autowired
    private MemberCompanyChangeService memberCompanyChangeService;
    @Autowired
    private BusinessscopeTaxcodeService businessscopeTaxcodeService;
    @Autowired
    private DictionaryService dictionaryService;
    @Resource
    private MemberCompanyMapper memberCompanyMapper;
    @Autowired
    private CompanyCorePersonnelService companyCorePersonnelService;
    @Resource
    private CompanyTaxHostingMapper companyTaxHostingMapper;

    /**
     * 企业列表
     * @param query
     * @return
     */
    @PostMapping("page")
    public ResultVo page(@RequestBody MemberCompanyQuery query){
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        //带登陆验证
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
            query.setParkId(userEntity.getParkId());
        }
        query.setOemCode(userEntity.getOemCode());

        PageInfo<MemberCompanyVo> page = memberCompanyService.listPageMemberCompany(query);
        return ResultVo.Success(page);
    }

    /**
     * 导出企业信息
     * @param query
     * @return
     */
    @PostMapping("batchCompany")
    public ResultVo batchCompany(@RequestBody MemberCompanyQuery query){
        //带登陆验证
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        // 机构账号
        if (userEntity.getPlatformType() == 2){
            query.setOemCode(userEntity.getOemCode());
        }
        // 园区账号
        if (userEntity.getPlatformType() == 3){
            query.setParkId(userEntity.getParkId());
        }
        List<MemberCompanyVo> list = memberCompanyService.listMemberCompanyInfo(query);
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("企业列表"+ DateUtil.format(new Date(),"yyyy-MM-dd"), "企业列表"+ DateUtil.format(new Date(),"yyyy-MM-dd"), MemberCompanyVo.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("企业列表导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * 根据企业id获取历史记录
     * @param
     * @return
     */
    @PostMapping("history")
    public ResultVo history(@RequestBody MemberCompanyQuery query){
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        PageInfo<CompanyChangeVo> page =  memberCompanyChangeService.queryMemberCompanyChangeByCompanyId(query);
        return ResultVo.Success(page);
    }

    /**
     * 添加税号
     * @param companyId
     * @param ein
     * @return
     */
    @PostMapping("update/ein")
    public ResultVo updateEin(@JsonParam Long companyId,@JsonParam String ein){
        //带登陆验证
        UserEntity userEntity = userService.findById(getCurrUserId());
        boolean falg = ein.matches("^[0-9a-zA-Z]+$");
        if (ein == null || ein.length()<15 ||ein.length()>20){
            return ResultVo.Fail("税号格式不正确");
        }
        if (!falg){
            return ResultVo.Fail("税号格式不正确");
        }
        MemberCompanyEntity entity = memberCompanyService.findById(companyId);
        if (StringUtil.isNotBlank(entity.getEin())){
            return ResultVo.Fail("该企业已有税号");
        }
        List<MemberCompanyEntity> list = memberCompanyService.queryMemberCompanyByEinStatusNotCancellation(ein);
        if (list.size()>0){
            return ResultVo.Fail("税号已使用");
        }
        entity.setEin(ein);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(userEntity.getUsername());
        memberCompanyService.editByIdSelective(entity);
        //  添加企业变更记录
        memberCompanyChangeService.insertChangeInfo(entity,userEntity.getUsername(),"添加税号");
        return ResultVo.Success();
    }

    /**
     * 修改状态
     * @param id 企业id
     * @param status，1冻结，2解冻
     * @return
     */
    @PostMapping("update/status")
    public ResultVo updateStatus(@JsonParam Long id, @JsonParam Integer status){
        if (status == null || status < 1 || status > 2) {
            return ResultVo.Fail("操作有误");
        }
        //带登陆验证
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        MemberCompanyEntity entity = memberCompanyService.findById(id);
        if (entity == null) {
            return ResultVo.Fail("企业不存在");
        }
        if (userEntity.getPlatformType() != 1 && userEntity.getPlatformType() != 3) {
            if (!StringUtils.equals(entity.getOemCode(), userEntity.getOemCode())) {
                return ResultVo.Fail("企业不属于当前OEM机构");
            }
        }
        if (MemberCompanyStatusEnum.TAX_CANCELLED.getValue().equals(entity.getStatus()) || MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue().equals(entity.getStatus())) {
            return ResultVo.Fail("企业已注销");
        }
        if (Objects.equals(status, 1)) {
            if (Objects.equals(entity.getStatus(), MemberCompanyStatusEnum.PROHIBIT.getValue())) {
                return ResultVo.Fail("企业已经冻结，请刷新页面");
            }
            status = MemberCompanyStatusEnum.PROHIBIT.getValue();
        } else if (Objects.equals(status, 2)) {
            if (Objects.equals(entity.getStatus(), MemberCompanyStatusEnum.NORMAL.getValue())) {
                return ResultVo.Fail("企业已经解冻，请刷新页面");
            }
            status = MemberCompanyStatusEnum.NORMAL.getValue();
        }
        memberCompanyService.updateStatus(id, status, userEntity.getNickname());
        //添加企业变更记录
        entity = memberCompanyService.findById(id);
        String remark = "企业解冻";
        if (Objects.equals(entity.getStatus(), MemberCompanyStatusEnum.PROHIBIT.getValue())) {
            remark = "企业冻结";
        }
        memberCompanyChangeService.insertChangeInfo(entity,userEntity.getUsername(),remark);
        return ResultVo.Success();
    }

    /**
     * 企业详情
     * @param id
     * @return
     */
    @PostMapping("detail")
    public ResultVo detail(@JsonParam Long id){
        //带登陆验证
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        MemberCompanyDetailAdminVO vo = memberCompanyService.queryDetailById(id);
        if (vo == null) {
            return ResultVo.Fail("企业不存在");
        }
   /*     if (userEntity.getPlatformType() != 1) {
            if (!StringUtils.equals(vo.getOemCode(), userEntity.getOemCode())) {
                return ResultVo.Fail("企业不属于当前OEM机构");
            }
        }*/
        //  历史数据经营范围还是”,“隔开
        String scope = vo.getBusinessScope();
        scope = scope.replace(",",";");
        vo.setBusinessScope(scope);
        vo.setBusinessLicenseFileName(vo.getBusinessLicense());
        vo.setBusinessLicense(ossService.getPrivateImgUrl(vo.getBusinessLicense()));
        vo.setBusinessLicenseCopyFileName(vo.getBusinessLicenseCopy());
        vo.setBusinessLicenseCopy(ossService.getPrivateImgUrl(vo.getBusinessLicenseCopy()));
        //注销凭证
        if(vo.getCancelCredentials()!=null){
            String[] cancelCredentialsList=vo.getCancelCredentials().split(",");
            String credentials="";
            for (String cancelCredentials:cancelCredentialsList) {
                credentials=credentials+","+ossService.getPrivateImgUrl(cancelCredentials);
            }
            credentials=credentials.substring(1);
            vo.setCancelCredentials(credentials);
        }
        if(StringUtil.isNotBlank(vo.getUserAgreementImgs())){
            // 委托注册协议图片  转换成https链接
            String[] userAgreementImgsList = vo.getUserAgreementImgs().split(",");
            if(userAgreementImgsList!=null && userAgreementImgsList.length>0) {
                List<String> userAgreementImgList = new ArrayList<>();
                for (String imgs : userAgreementImgsList) {
                    userAgreementImgList.add(ossService.getPrivateImgUrl(imgs));
                }
                vo.setUserAgreementImgList(userAgreementImgList);
            }
        }

        //获取开票类目
        CompanyInvoiceCategoryEntity t = new CompanyInvoiceCategoryEntity();
        t.setCompanyId(vo.getId());
        List<CompanyInvoiceCategoryEntity>  companyInvoiceCategoryEntities = companyInvoiceCategoryService.select(t);
        vo.setCategories(companyInvoiceCategoryEntities);
        for (CompanyInvoiceCategoryEntity c:companyInvoiceCategoryEntities ) {
            vo.setCategoriesString((vo.getCategoriesString()==null?"":vo.getCategoriesString()) +" \\n "+c.getCategoryName());
        }
        //获取核定税种
        List<RatifyTaxEntity> ratifyTaxList = ratifyTaxService.listRatifyTax(vo.getIndustryId());
        vo.setRatifies(ratifyTaxList);
        //查询证件所在地
        List<CompanyResourcesAddressEntity> addresses = companyResourcesAddressService.listCompanyResourcesAddress(id, vo.getOemCode());
        vo.setAddresses(addresses);
        //获取企业成员信息
        List<CompanyCorePersonnelVO> list = companyCorePersonnelService.getCompanyCorePersonnelByCompanyIdOrOrderNo(vo.getId(),null);
        if(list!=null){
            vo.setCompanyCorePersonnelList(list);
        }
        return ResultVo.Success(vo);
    }

    /**
     * 企业编辑
     * @return
     */
    @PostMapping("update")
    public ResultVo update(@RequestBody MemberCompanyPO po){
        //带登陆验证
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(StringUtil.isBlank(po.getCompanyName())){
            return ResultVo.Fail("企业名称不能为空");
        }
        if(StringUtil.isBlank(po.getOperatorTel())){
            return ResultVo.Fail("经营者联系方式不能为空");
        }
        if(StringUtil.isBlank(po.getBusinessAddress())){
            return ResultVo.Fail("经营地址不能为空");
        }
        if(po.getApprovedTurnover()!=null&&
                ( po.getApprovedTurnover()<= 0 ||
                po.getApprovedTurnover() > 500000000)){
            return ResultVo.Fail("核定经营额只支持输入1~500w数字");
        }
        MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(po.getId());
        //  校验经营范围
        if (StringUtils.isNotBlank(po.getBusinessScope())){
            String scope = po.getBusinessScope().replaceAll("；",";");
            String[] scopes = scope.split(";");
            for (String name:scopes){
                List<BusinessScopeTaxCodeVO> list = businessscopeTaxcodeService.getBusinessScopeByScopeName(name);
                if (CollectionUtil.isEmpty(list)){
                    throw new BusinessException(name+"不存在!");
                }
            }
        }
        po.setBusinessScope(po.getBusinessScope().trim());
/*        String regex = "^[a-z0-9A-Z]{15,20}$";
        if(po.getEin()!=null && !po.getEin().matches(regex)){
            return ResultVo.Fail("税号格式不对");
        }*/
        try {
            memberCompanyService.update(po,getCurrUseraccount());
        } catch (ParseException e) {
            return ResultVo.Fail(e.getMessage());
        }
        //添加企业变更记录
        memberCompanyChangeService.insertChangeInfo(memberCompanyEntity,getCurrUseraccount(),"企业信息修改");
        return ResultVo.Success();
    }


    /**
     * 证件管理
     */
    @PostMapping("queryCompanyResourcesAddress")
    public ResultVo queryCompanyResourcesAddress(@JsonParam Long companyId,@JsonParam String oemCode){
        //验证登陆
        getCurrUser();
        List<CompanyResourcesAddressEntity> list=companyResourcesAddressService.listCompanyResourcesAddress(companyId,oemCode);
        return ResultVo.Success(list);
    }

    /**
     * 证件管理详情
     */
    @PostMapping("queryCompanyResourcesAddressDetail")
    public ResultVo queryCompanyResourcesAddressDetail(@JsonParam Long id){
        //验证登陆
        getCurrUser();
        return ResultVo.Success(companyResourcesAddressService.queryCompanyResourcesAddressDetail(id));
    }

    /**
     * 修改企业资源所在地
     */
    @PostMapping("updateCompanyResourcesAddress")
    public ResultVo updateCompanyResourcesAddress(@RequestBody CompanyResourcesAddressPO companyResourcesAddressPO){
        //验证登陆
        getCurrUser();
        companyResourcesAddressService.updateCompanyResourcesAddress(companyResourcesAddressPO,getCurrUseraccount());
        return ResultVo.Success();
    }
    /**
     * 证件流转历史
     */
    @PostMapping("queryCompanyResourcesAddressHistory")
    public ResultVo queryCompanyResourcesAddressHistory(@JsonParam Long companyId,@JsonParam String oemCode,@JsonParam Integer resourcesType){
        //验证登陆
        getCurrUser();
        List<CompanyResourcesAddressHistoryEntity> list=companyResourcesAddressHistoryService.queryCompanyResourcesAddressHistoryList(companyId,oemCode,resourcesType);
        return ResultVo.Success(list);
    }

    /**
     * 校验企业是否存在未补缴的税单
     * @param companyId
     * @return
     */
    @PostMapping("checkTaxBill")
    public ResultVo checkTaxBill(@JsonParam Long companyId){
        //验证登陆
        getCurrUser();
        if(companyId==null){
            return ResultVo.Fail("请选择企业");
        }
        Map<String,Object> map = new HashMap<>();
        // 企业税单
        List<CompanyTaxBillEntity> list = companyTaxBillService.queryCompanyTaxByCompanyId(companyId);
        if (list != null && list.size()>0){
            map.put("type",2);
            map.put("content","该企业存在未补缴的税单，注销后将无法追缴，请确定是否继续注销？");
        }
        // 查询企业
        MemberCompanyEntity company = memberCompanyService.findById(companyId);
        if (null == company) {
            return ResultVo.Fail("未查询到企业信息");
        }
        PendingTaxBillQuery query = new PendingTaxBillQuery();
        query.setEin(company.getEin());
        query.setCompanyId(companyId);
        query.setStatusRange(5);
        List<PendingTaxBillVO> pendingTaxBillList = companyTaxBillService.pendingTaxBill(query);
        if (CollectionUtil.isNotEmpty(pendingTaxBillList)){
            map.put("type",1);
            map.put("content","该企业存在未处理的税单，请处理完后再注销！");
        }
        query.setStatusRange(3);
        pendingTaxBillList = new ArrayList<>();
        pendingTaxBillList = companyTaxBillService.pendingTaxBill(query);
        if (CollectionUtil.isNotEmpty(pendingTaxBillList)){
            map.put("type",2);
            map.put("content","该企业存在未确认成本的税单，注销后税单将变成已作废，请确定是否继续注销？");
        }
        return ResultVo.Success(map);
    }

    /**
     * 企业注销
     * @param cancelType 注销类型 1-税务注销 2-工商注销
     */
    @PostMapping("cancel")
    public ResultVo cancel(@JsonParam Long companyId,@JsonParam String cancelCredentials,@JsonParam Integer cancelType){
        //验证登陆
        getCurrUser();
        if(companyId==null){
            return ResultVo.Fail("请选择企业");
        }
        if (null == cancelType) {
            return ResultVo.Fail("请指定注销类型");
        }
        if(cancelType == 2 && StringUtil.isBlank(cancelCredentials)){
            return ResultVo.Fail("请上传注销凭证");
        }
        try{
            orderService.cancelAdmin(companyId,cancelCredentials,getCurrUseraccount(),cancelType);
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        if (cancelType == 1) {
            return ResultVo.Success();
        }
        // 对公户账户
        CompanyCorporateAccountEntity cor = companyCorporateAccountService.queryCorpByCompanyId(companyId);
        if (cor != null){
            //  存在对公户账户
            return ResultVo.Success("该企业存在对公户，请记得提醒客户去银行注销！");
        }else{
            return ResultVo.Success();
        }
    }

    /**
     * 托管信息(修改)
     */
    @PostMapping("hostingInfo")
    public ResultVo hostingInfo(@Valid @RequestBody CompanyTaxHostingPO po){
        //验证登陆
        getCurrUser();
        try{
            // 通过企业id查询 该企业信息
            MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(po.getCompanyId());
            if(memberCompanyEntity == null){
                return ResultVo.Fail("该企业不存在");
            }
            CompanyTaxHostingEntity entity = new CompanyTaxHostingEntity();
            entity.setCompanyId(po.getCompanyId());
            entity.setStatus(1);
            // 企业税务托管表
            List<CompanyTaxHostingEntity> list = companyTaxHostingService.select(entity);
            if(CollectionUtil.isNotEmpty(list) && po.getHostingStatus()!=1){
                // 根据企业id查询带创建，待付款，待审核，出票中的订单数量
//                int count = this.invoiceOrderService.querySomeStatusOrderByCompanyId(po.getCompanyId(), null);
//                if (count > 0) {
//                    // 存在未完成的开票订单记录
//                    return ResultVo.Fail(ErrorCodeEnum.ORDER_NOT_COMPLETED.getText());
//                }
            } else if(po.getHostingStatus() == 1){
                if(po.getChannel() == null){
                    return ResultVo.Fail("请先选择通道方");
                }
                // 托管状态为 已托管
                switch (po.getChannel()){
                    case 1:
                        // 百旺
                        if(po.getTaxDiscType() == null){
                            return ResultVo.Fail("税务盘类型不能为空");
                        }
                        if(po.getTaxDiscCode() == null){
                            return ResultVo.Fail("税务盘编号不能为空");
                        }
                        if(po.getFaceAmount() == null){
                            return ResultVo.Fail("面额不能为空");
                        }
                        // 获取百旺电子发票配置参数
                        Map<String,String> oemParams = invoiceRecordService.getBWInvoiceParams(memberCompanyEntity.getOemCode());
                        // 随便传一个发票类型
                        Map<String,Object> map = invoiceRecordService.queryInventory(oemParams,memberCompanyEntity.getEin(),po.getTaxDiscCode(), InvoiceWayEnum.ELECTRON.getValue());
                        if(!map.containsKey("totalSurplusNo")){
                            return ResultVo.Fail("税务盘配置错误");
                        }
                        break;
                    case 2:
                        // 园区
                        break;
                }
            }
            //需校验该企业是否存在未完成的开票订单，若存在，则托管状态不允许编辑
            companyTaxHostingService.addOrUpdate(po,getCurrUseraccount());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }


    @ApiOperation(value="托管信息(批量修改)", notes="托管信息(批量修改)")
    @PostMapping("updateHostingInfoBatch")
    public ResultVo updateHostingInfoBatch(@RequestParam(name = "file",required = true) MultipartFile file){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("模板文件为空!");
        }
        // 正确的文件
        List<CompanyTaxHostingBatchPO> list;
        // 记录失败的文件
        List<CompanyTaxHostingBatchPO> failed = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            list = ExcelImportUtil.importExcel(file.getInputStream(), CompanyTaxHostingBatchPO.class, params);
        } catch (Exception e) {
            log.error("导入文件异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("文件内容为空");
        }

        // 开始循环数据
        for(CompanyTaxHostingBatchPO vo:list){
            if(StringUtils.isBlank(vo.getCompanyName())){
                vo.setFailed("企业名称不能为空");
                failed.add(vo);
                continue;
            }
            if(StringUtils.isBlank(vo.getHostingStatus())){
                vo.setFailed("托管状态不能为空");
                failed.add(vo);
                continue;
            }
            if(!vo.getHostingStatus().equals("已托管") && !vo.getHostingStatus().equals("未托管")){
                vo.setFailed("托管状态不正确(已托管,未托管)");
                failed.add(vo);
                continue;
            }
            // 通过企业名称查询 该企业信息
            MemberCompanyEntity memberCompanyOne = memberCompanyMapper.getMemberCompanyOne(vo.getCompanyName());
            if(memberCompanyOne == null){
                vo.setFailed("该企业不存在");
                failed.add(vo);
                continue;
            }
            if(StringUtils.isNotBlank(vo.getEin())){
                // 税号不为空,判断税号和企业名称是否一致
                MemberCompanyEntity memberCompanyInfo = memberCompanyMapper.getMemberCompanyOne(vo.getCompanyName());
                if(memberCompanyInfo != null && memberCompanyInfo.getEin() != null){
                    if(!vo.getEin().equals(memberCompanyInfo.getEin())){
                        vo.setFailed("该税号对应的数据与页面企业数据展示的不一致,请检查企业名称和税号是否一致");
                        failed.add(vo);
                        continue;
                    }
                    if(!vo.getCompanyName().equals(memberCompanyInfo.getCompanyName())){
                        vo.setFailed("该税号对应的数据与页面企业数据展示的不一致,请检查企业名称和税号是否一致");
                        failed.add(vo);
                        continue;
                    }
                }

            }
            // 根据企业id查询税务托管
            CompanyTaxHostingEntity companyTaxHostingEntity = companyTaxHostingMapper.queryByCompanyId(memberCompanyOne.getId());
            if(companyTaxHostingEntity != null){
                if(vo.getHostingStatus().equals("未托管")){
                    // 如果文件里企业是未托管，数据库也是未托管，抛出异常
                    Integer hostingStatus = 0;
                    if(companyTaxHostingEntity.getStatus() != null && companyTaxHostingEntity.getStatus().equals(hostingStatus)){
                        vo.setFailed("托管状态没有变化，无需修改");
                        failed.add(vo);
                        continue;
                    }
                }
            }
            CompanyTaxHostingEntity entity = new CompanyTaxHostingEntity();
            entity.setCompanyId(memberCompanyOne.getId());
            entity.setStatus(1);
            // 企业税务托管表
            List<CompanyTaxHostingEntity> HostingList = companyTaxHostingService.select(entity);
            if(CollectionUtil.isNotEmpty(HostingList) && vo.getHostingStatus().equals("未托管")){
//                // 根据企业id查询带创建，待付款，待审核，出票中的订单数量
//                int count = this.invoiceOrderService.querySomeStatusOrderByCompanyId(memberCompanyOne.getId(), null);
//                if (count > 0) {
//                    // 存在未完成的开票订单记录
//                   // return ResultVo.Fail(ErrorCodeEnum.ORDER_NOT_COMPLETED.getText());
//                    vo.setFailed("该企业存在未完成的开票订单记录");
//                    failed.add(vo);
//                    continue;
//                }
            }else if(vo.getHostingStatus().equals("已托管")){

            }
            // 需校验该企业是否存在未完成的开票订单，若存在，则托管状态不允许编辑
            CompanyTaxHostingPO po = new CompanyTaxHostingPO();
            // 企业id
            po.setCompanyId(memberCompanyOne.getId());
            if(vo.getHostingStatus().equals("未托管")){
                po.setHostingStatus(0);
            }
            if(vo.getHostingStatus().equals("已托管")){
                po.setHostingStatus(1);
            }
            // 批量修改通道默认为  园区
            po.setChannel(2);
            companyTaxHostingService.addOrUpdate(po,getCurrUseraccount());
        }

        HashMap<Object, Object> result = CollUtil.newHashMap();
        // 成功多少条
        result.put("success", list.size() - failed.size());
        // 错误多少条
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
        String fileName = "updateHostingInfoBatchError_" + System.currentTimeMillis() + ".xls";
        BufferedOutputStream bos = null;
        try {
            File downLoadFile = new File(bag, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(downLoadFile));
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), CompanyTaxHostingBatchPO.class, failed);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("托管信息(批量修改)，失败记录保存服务器异常：" + e.getMessage(), e);
            return ResultVo.Fail("托管信息(批量修改)，失败记录保存失败");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

    /**
     * 根据税号查询状态为正常且未过期的企业信息
     */
    @PostMapping("getCompanyInfoByEin")
    public ResultVo getCompanyInfoByEin(@JsonParam String ein){
        //验证登陆
        getCurrUser();
        List<MemberCompanyBasicVo> list=memberCompanyService.queryCompanyBasicInfoByEinOrId(null,ein,1,null);
        if(list==null || list.size()<1){
            return ResultVo.Fail("企业税号不存在或企业状态异常");
        }else if(list.size()>1){
            return ResultVo.Fail("存在多个税号相同的正常企业");
        }else {
            return ResultVo.Success(list.get(0));
        }
    }

    /**
     * 企业迁移
     */
    @PostMapping("companyTransfer")
    public ResultVo companyTransfer(@JsonParam String companyId,@JsonParam String newOemCode,@JsonParam String newMemberAccount){
        //验证登陆
        getCurrUser();
        Long id = null;
        try{
            id = Long.parseLong(companyId);
        }catch (Exception e){
            return ResultVo.Fail( "企业Id格式错误");
        }
        if(id == null){
            return ResultVo.Fail("需要迁移的企业Id不能为空");
        }
        //参数校验
        String message = checkCompanyTransferParams(id,newOemCode,newMemberAccount);
        if(StringUtils.isNotBlank(message)){
            return ResultVo.Fail(message);
        }
        //企业迁移
        memberCompanyService.companyTransfer(id,newOemCode,newMemberAccount,getCurrUser().getUseraccount());
        return ResultVo.Success();
    }
    //校验个体户迁移参数
    private String checkCompanyTransferParams(Long companyId,String newOemCode,String newMemberAccount){
        if(StringUtils.isBlank(newOemCode)){
            return "新OEM机构不能为空";
        }
        if(StringUtils.isBlank(newMemberAccount)){
            return "新会员账号不能为空";
        }

        MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(companyId);
        if(memberCompanyEntity==null){
            return "未找到需要迁移的企业信息";
        }
        if(memberCompanyEntity.getCompanyType()!=1){
            return "非个体企业不支持迁移操作";
        }
        if(!ObjectUtil.equal(memberCompanyEntity.getStatus(),1) || ObjectUtil.equal(memberCompanyEntity.getOverdueStatus(),3)){
            return "需要企业的状态异常或已过期";
        }
        // 查询该企业是否还有未完成的开票订单
        int count = this.invoiceOrderService.queryNotFinishOrderByCompanyId(memberCompanyEntity.getId(), memberCompanyEntity.getOemCode());
        if (count > 0) {
            return "该企业存在进行中的开票订单，请完成或取消订单后再试";
        }
        //查询税单状态是否正常
        Example example = new Example(CompanyTaxBillEntity.class);
        List<Integer> taxBillStatusList = new ArrayList<>();
        taxBillStatusList.add(0);
        taxBillStatusList.add(1);
        taxBillStatusList.add(2);
        taxBillStatusList.add(6);
        taxBillStatusList.add(7);
        taxBillStatusList.add(8);
        taxBillStatusList.add(10);
        taxBillStatusList.add(11);
        example.createCriteria().andEqualTo("companyId",companyId).andIn("taxBillStatus",taxBillStatusList).andGreaterThan("invoiceMoney",0);
        List list = companyTaxBillService.selectByExample(example);
        if(list!=null && list.size()>0){
            return "该企业存在未处理完成的税单，请优先处理税单";
        }
        //查询是否存在对公户信息
        CompanyCorporateAccountEntity companyCorporateAccountEntity = companyCorporateAccountService.queryCorpByCompanyId(memberCompanyEntity.getId());
        if(companyCorporateAccountEntity!=null){
            return "该企业存在对公户信息，无法进行迁移";
        }
        //查询是否存在对公户申请
        int applyingCount = corporateAccountApplyOrderService.queryCorAccApplyingOrder(memberCompanyEntity.getMemberId(), memberCompanyEntity.getOemCode(), memberCompanyEntity.getId());
        if (applyingCount > 0) {
            throw new BusinessException("该企业存在对公户申请订单，无法进行迁移");
        }
        return null;
    }
}
