package com.yuqian.itax.admin.controller.system;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.system.entity.*;
import com.yuqian.itax.system.entity.dto.IndustryInfoDTO;
import com.yuqian.itax.system.entity.query.IndustryQuery;
import com.yuqian.itax.system.entity.query.InvoiceCategoryBaseQuery;
import com.yuqian.itax.system.entity.vo.IndustryInfoVO;
import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseStringVO;
import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseVO;
import com.yuqian.itax.system.enums.IndustryStatusEnum;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.MemberCompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 获取行业信息
 * @author：pengwei
 * @Date：2019/12/23 11:17
 * @version：1.0
 */
@Slf4j
@RestController
@RequestMapping("industry")
@Api(tags = "行业")
public class IndustryController extends BaseController {

    @Autowired
    private IndustryService industryService;
    @Autowired
    private InvoiceCategoryService invoiceCategoryService;
    @Autowired
    private RatifyTaxService ratifyTaxService;
    @Autowired
    private BusinessScopeService businessScopeService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    InvoiceCategoryBaseService invoiceCategoryBaseService;
    @Autowired
    private BusinessscopeTaxcodeService businessscopeTaxcodeService;
    @Autowired
    private ParkBusinessscopeService parkBusinessscopeService;

    @ApiOperation("行业类型列表查询")
    @PostMapping("list")
    @ResponseBody
    public ResultVo list(@JsonParam Long parkId,@JsonParam Integer companyType){
        IndustryEntity industryEntity=new IndustryEntity();
        industryEntity.setStatus(IndustryStatusEnum.YES.getValue());
        if(null!=parkId){
            industryEntity.setParkId(parkId);
        }
        if(null!=companyType){
            industryEntity.setCompanyType(companyType);
        }
        List<IndustryEntity> list = this.industryService.select(industryEntity);
        return ResultVo.Success(list);
    }

    @ApiOperation("根据行业类型选择核定税种、开票类目、经营范围")
    @ApiImplicitParam(name="industryId",value="行业id",dataType="Long",required = true)
    @PostMapping("/getById")
    @ResponseBody
    public ResultVo getByIndustryId(@JsonParam Long industryId){
        if(null == industryId){
            return ResultVo.Fail("行业ID不能为空");
        }
        IndustryInfoVO industryVo = new IndustryInfoVO();
        try{
            InvoiceCategoryEntity t = new InvoiceCategoryEntity();
            t.setIndustryId(industryId);
            List<InvoiceCategoryEntity> invoiceCategoryList = this.invoiceCategoryService.select(t);
            List<RatifyTaxEntity> ratifyTaxList = ratifyTaxService.listRatifyTax(industryId);
            List<BusinessScopeEntity> businessScopeList = businessScopeService.listBusinessScope(industryId);
            industryVo.setBusinessScopelist(businessScopeList);
            industryVo.setRatifyTaxList(ratifyTaxList);
            industryVo.setInvoiceCategoryList(invoiceCategoryList);
        }catch (BusinessException e){
            log.error("查找异常：{}",e.getMessage());
            return ResultVo.Fail(e.getMessage());
        }catch (Exception e){
            log.error("系统未知异常：{}",e.getMessage());
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success(industryVo);
    }

    @ApiOperation("根据园区获取行业数据")
    @PostMapping("page")
    public ResultVo listPageProduct(@RequestBody IndustryQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        if (query.getParkId() == null) {
            return ResultVo.Fail("请选择园区");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setOemCode(userEntity.getOemCode());
        PageInfo<IndustryInfoVO> page = industryService.listPage(query);
        return ResultVo.Success(page);
    }

    /**
     * 根据园区获取行业数据导出
     * @param query
     * @return
     */
    @PostMapping("export")
    public ResultVo listPageProductExport(@RequestBody IndustryQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getParkId() == null) {
            return ResultVo.Fail("请选择园区");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setOemCode(userEntity.getOemCode());
        List<IndustryInfoVO> lists = industryService.queryIndustryList(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("园区行业数据", "园区行业数据", IndustryInfoVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("园区行业数据导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }


    /**
     * 导入服务类别
     * @param file
     * @param parkId
     * @return
     */
    @PostMapping("batch/import")
    public ResultVo batchImport(@RequestParam("file") MultipartFile file, @RequestParam("parkId") Long parkId) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<IndustryInfoDTO> list;
        List<IndustryInfoDTO> failed = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            list = ExcelImportUtil.importExcel(file.getInputStream(), IndustryInfoDTO.class, params);
        } catch (Exception e) {
            log.error("导入服务类别异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("文件内容为空");
        }
        ParkEntity parkEntity = parkService.findById(parkId);
        if (parkEntity == null) {
            return ResultVo.Fail("园区不存在");
        }

        IndustryEntity industryEntity;
        for (IndustryInfoDTO dto : list) {
            if (StringUtils.isBlank(dto.getCompanyTypeName())) {
                dto.setFailed("企业类型不能为空");
                failed.add(dto);
                continue;
            }
            Integer companyType = getCompanyType(dto.getCompanyTypeName());
            if (companyType == null) {
                dto.setFailed("企业类型填写有误");
                failed.add(dto);
                continue;
            }
            if (StringUtils.isBlank(dto.getIndustryName())) {
                dto.setFailed("服务类别不能为空");
                failed.add(dto);
                continue;
            }
            if (StringUtils.isNotBlank(dto.getOrderDesc()) && dto.getOrderDesc().length() > 100) {
                dto.setFailed("其他说明不能超过100字");
                failed.add(dto);
                continue;
            }
            if (StringUtils.isBlank(dto.getExampleName())) {
                dto.setFailed("商户名称不能为空");
                failed.add(dto);
                continue;
            }
            if (StringUtils.isBlank(dto.getBusinessContent())) {
                dto.setFailed("经营范围不能为空");
                failed.add(dto);
                continue;
            }
            if (StringUtils.isBlank(dto.getTaxName())) {
                dto.setFailed("核定税种不能为空");
                failed.add(dto);
                continue;
            }
            String categoryName = dto.getCategoryName();
            if (StringUtils.isBlank(categoryName)) {
                dto.setFailed("开票类目不能为空");
                failed.add(dto);
                continue;
            }
            categoryName=categoryName.trim().replace("）",")");
            categoryName=categoryName.replace("（","(");
            categoryName=categoryName.replace("，",",");
            String[] split = categoryName.replace("(","").split("\\)");
            if (split == null || split.length <= 0) {
                dto.setFailed("发票类目不能为空");
                failed.add(dto);
                continue;
            }
            String regex = "^[(),*\\u4E00-\\u9FA5]+$";
            boolean isMatch =categoryName.trim().matches(regex);
            if (!isMatch) {
                dto.setFailed("发票类目格式不正确");
                failed.add(dto);
                continue;
            }
            List<String> categoryNames = Arrays.asList(split).stream().filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(categoryNames)) {
                dto.setFailed("发票类目不能为空");
                failed.add(dto);
                continue;
            }
            List<String> categoryNamesList=categoryNames.stream().distinct().collect(Collectors.toList());
            if(categoryNamesList.size()<categoryNames.size()){
                dto.setFailed("发票类目存在重复");
                failed.add(dto);
                continue;
            }
            dto.setCategoryNames(categoryNames);
            // 经营范围在基础库中是否存在校验
            List<BusinessScopeEntity> businesses = dto.getBusinessScopeEntity();
            BusinessScopeEntity business = businesses.get(0);
            String[] scopes = business.getBusinessContent().split(";");
            boolean flag = false;
            for (String name:scopes){
                ParkBusinessscopeEntity parkBusinessscopeEntity = parkBusinessscopeService.getParkBusinessscopeByParkIdAndName(parkId,name);
                if (parkBusinessscopeEntity == null) {
                    dto.setFailed("经营范围{" + name + "}不存在");
                    failed.add(dto);
                    flag = true;
                    break;
                }
            }
            if (flag){
                continue;
            }
            List<String> industryBusinessList = Arrays.asList(business.getBusinessContent().split(";"));
            HashSet<String> industryBusinessSet = Sets.newHashSet(industryBusinessList);
            if (!Objects.equals(industryBusinessList.size(), industryBusinessSet.size())) {
                dto.setFailed("经营范围重复");
                failed.add(dto);
                continue;
            }

            List<InvoiceCategoryBaseStringVO> categoryList=new ArrayList<>();
            InvoiceCategoryBaseStringVO vo=null;
            dto.setFailed(null);
            try {
                for (String invoiceCategoryName:categoryNames) {
                    vo=new InvoiceCategoryBaseStringVO();
                    String [] i=invoiceCategoryName.split(",");
                    String taxClassificationAbbreviation=i[0];
                    String goodsName=i[1];
                    InvoiceCategoryBaseQuery query=new InvoiceCategoryBaseQuery();
                    query.setTaxClassificationAbbreviation(taxClassificationAbbreviation);
                    query.setGoodsName(goodsName);
                    List<InvoiceCategoryBaseVO> invoiceCategoryBaseList= invoiceCategoryBaseService.queryInvoiceCategoryBaseList(query);
                    if (CollectionUtil.isEmpty(invoiceCategoryBaseList)) {
                        dto.setFailed(taxClassificationAbbreviation+"*"+goodsName+"开票类目不存在.");
                        continue;
                    }
                    BeanUtils.copyProperties(invoiceCategoryBaseList.get(0),vo);
                    categoryList.add(vo);
                }
            } catch (Exception e) {
                dto.setFailed("模板格式不正确");
                failed.add(dto);
                continue;
            }
            if(dto.getFailed()!=null){
                failed.add(dto);
                continue;
            }
            dto.setCategoryList(categoryList);
            try {
                industryEntity = new IndustryEntity();
                industryEntity.setParkId(parkId);
                industryEntity.setCompanyType(companyType);
                industryEntity.setIndustryName(dto.getIndustryName());
                industryEntity = industryService.selectOne(industryEntity);
                if (industryEntity != null) {
                    dto.setFailed("服务类别已存在");
                    failed.add(dto);
                    continue;
                }
                dto.setParkId(parkId);
                dto.setCompanyType(companyType);
                dto.setAddTime(new Date());
                dto.setAddUser(currUser.getUseraccount());
                industryService.add(dto);
            } catch (Exception e) {
                dto.setFailed("保存服务类别失败");
                failed.add(dto);
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
        String fileName = "industry_" + System.currentTimeMillis() + ".xls";
        BufferedOutputStream bos = null;
        try {
            File downLoadFile = new File(bag, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(downLoadFile));
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), IndustryInfoDTO.class, failed);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("导入服务类别，失败记录保存服务器异常：" + e.getMessage(), e);
            return ResultVo.Fail("导入服务类别，失败记录保存失败");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

    /**
     * 获取企业类型
     * @param companyTypeName
     * @return
     */
    public Integer getCompanyType(String companyTypeName) {
        if ("个体户".equals(companyTypeName)||"个体工商户".equals(companyTypeName)) {
            return 1;
        }
        if ("个人独资企业".equals(companyTypeName)) {
            return 2;
        }
        if ("有限合伙公司".equals(companyTypeName)) {
            return 3;
        }
        if ("有限责任公司".equals(companyTypeName)) {
            return 4;
        }
        return null;
    }

    @ApiOperation("编辑")
    @PostMapping("edit")
    public ResultVo edit(@RequestBody @Validated IndustryInfoDTO dto, BindingResult result) {
        CurrUser currUser = getCurrUser();
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (CollectionUtil.isEmpty(dto.getCategoryList())) {
            return ResultVo.Fail("发票类目不能为空");
        }
        IndustryEntity industryEntity = industryService.findById(dto.getIndustryId());
        if (industryEntity == null) {
            return ResultVo.Fail("行业不存在");
        }
        if (!Objects.equals(industryEntity.getParkId(), dto.getParkId())) {
            return ResultVo.Fail("园区选择有误");
        }
        Example example = new Example(IndustryEntity.class);
        example.createCriteria().andEqualTo("industryName",dto.getIndustryName())
                .andEqualTo("parkId",dto.getParkId())
                .andEqualTo("companyType",dto.getCompanyType())
                .andNotEqualTo("id", dto.getIndustryId());// 非注销状态
        List<IndustryEntity> lists = industryService.selectByExample(example);
        if (CollectionUtil.isNotEmpty(lists)) {
            return ResultVo.Fail("服务类别已存在");
        }
        List<InvoiceCategoryBaseStringVO> categoryList=dto.getCategoryList().stream().distinct().collect(Collectors.toList());
        if(categoryList.size()<dto.getCategoryList().size()){
            return ResultVo.Fail("发票类目存在重复");
        }
        // 校验经营范围在基础库中是否存在
        List<BusinessScopeEntity> businessScopeEntity = dto.getBusinessScopeEntity();
        BusinessScopeEntity scopeEntity = businessScopeEntity.get(0);
        List<String> list = Arrays.asList(scopeEntity.getBusinessContent().split(";"));
        list = Lists.newArrayList(list);
        for (String name:list){
            ParkBusinessscopeEntity parkBusinessscopeEntity = parkBusinessscopeService.getParkBusinessscopeByParkIdAndName(dto.getParkId(),name);
            if (parkBusinessscopeEntity == null){
                throw new BusinessException(name+"不存在!");
            }
        }
        // 经营范围不能重复
        List<String> industryBusinessList = Arrays.asList(scopeEntity.getBusinessContent().split(";"));
        HashSet<String> industryBusinessSet = Sets.newHashSet(industryBusinessList);
        if (!Objects.equals(industryBusinessList.size(), industryBusinessSet.size())) {
            throw new BusinessException("经营范围重复");
        }
        dto.setAddUser(currUser.getUseraccount());
        dto.setAddTime(new Date());
        industryService.edit(industryEntity, dto);
        return ResultVo.Success();
    }

    @ApiOperation("删除")
    @PostMapping("delete")
    public ResultVo delete(@JsonParam Long industryId) {
        CurrUser currUser = getCurrUser();
        if (industryId == null) {
            return ResultVo.Fail("请选择要删除的行业");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        IndustryEntity industryEntity = industryService.findById(industryId);
        if (industryEntity == null) {
            return ResultVo.Fail("行业不存在");
        }
        MemberCompanyEntity memberCompanyEntity = new MemberCompanyEntity();
        memberCompanyEntity.setIndustryId(industryId);
        List<MemberCompanyEntity> list = memberCompanyService.select(memberCompanyEntity);
        if (CollectionUtil.isNotEmpty(list)) {
            return ResultVo.Fail("该行业已存在企业，不允许删除！");
        }
        industryService.delete(industryId);
        return ResultVo.Success();
    }

    @ApiOperation("详情")
    @PostMapping("detail")
    public ResultVo detail(@JsonParam Long industryId) {
        CurrUser currUser = getCurrUser();
        if (industryId == null) {
            return ResultVo.Fail("请选择要查看详情的行业");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        IndustryQuery query = new IndustryQuery();
        query.setIndustryId(industryId);
        PageInfo<IndustryInfoVO> page = industryService.listPage(query);
        if (CollectionUtil.isEmpty(page.getList())) {
            return ResultVo.Fail("行业不存在");
        }
        IndustryInfoVO industryInfoVO=page.getList().get(0);
        industryInfoVO.setExampleInvoiceUrl(ossService.getPrivateImgUrl(industryInfoVO.getExampleInvoice()));
        return ResultVo.Success(industryInfoVO);
    }
    @Autowired
    OssService ossService;
}
