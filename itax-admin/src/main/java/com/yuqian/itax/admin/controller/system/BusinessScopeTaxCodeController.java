package com.yuqian.itax.admin.controller.system;

import cn.hutool.core.convert.Convert;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.entity.BusinessScopeEntity;
import com.yuqian.itax.system.entity.BusinessscopeTaxcodeEntity;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.ParkBusinessscopeEntity;
import com.yuqian.itax.system.entity.dto.BusinessScopeTaxcodeDTO;
import com.yuqian.itax.system.entity.query.BusinessScopeTaxCodeQuery;
import com.yuqian.itax.system.entity.vo.BusinessScopeBatchVO;
import com.yuqian.itax.system.entity.vo.BusinessScopeTaxCodeVO;
import com.yuqian.itax.system.service.BusinessScopeService;
import com.yuqian.itax.system.service.BusinessscopeTaxcodeService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.ParkBusinessscopeService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.DateUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.util.Lists;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BusinessScopeTaxCodeController
 * @Description 经营范围税收分类编码Controller
 * @Author Administrator
 * @Date 2021/11/16 10:37
 * @Version 1.0
 */
@RestController
@RequestMapping("/businessScopeTaxCode")
@Slf4j
public class BusinessScopeTaxCodeController extends BaseController {

    @Autowired
    private BusinessscopeTaxcodeService businessscopeTaxcodeService;

    @Autowired
    private BusinessScopeService businessScopeService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    DictionaryService dictionaryService;

    @Autowired
    private ParkBusinessscopeService parkBusinessscopeService;

    @ApiOperation("经营范围基础库列表页")
    @PostMapping("/page")
    public ResultVo listPageBusinessScopeTaxcode(@RequestBody BusinessScopeTaxCodeQuery query){
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        PageResultVo<BusinessScopeTaxCodeVO> vo = businessscopeTaxcodeService.list(query);
        return ResultVo.Success(vo);
    }

    /**
     * 导出经营范围
     * @param query
     * @return
     */
    @ApiOperation("导出经营范围")
    @PostMapping("/batch")
    public ResultVo exportBusinessScope(@RequestBody BusinessScopeTaxCodeQuery query){
        List<BusinessScopeTaxCodeVO> list = businessscopeTaxcodeService.queryBusinessScopeTaxcode(query);
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("经营范围"+ DateUtil.format(new Date(),"yyyy-MM-dd"), "经营范围"+ DateUtil.format(new Date(),"yyyy-MM-dd"), BusinessScopeTaxCodeVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("下载经营范围异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * 经营范围添加
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("/addBusinessScope")
    public ResultVo addBusinessScope(@RequestBody  @Validated BusinessScopeTaxcodeDTO dto, BindingResult result){
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        CurrUser currUser = getCurrUser();
        BusinessscopeTaxcodeEntity entity = businessscopeTaxcodeService.getVBusinessScopeByScopNameAndCode(dto.getBusinessScopName(),dto.getTaxClassificationCode());
        if (entity != null){
            return ResultVo.Fail("数据库以存在该条记录");
        }
        entity = new BusinessscopeTaxcodeEntity();
        entity.setBusinessScopName(dto.getBusinessScopName());
        entity.setTaxClassificationCode(dto.getTaxClassificationCode());
        entity.setTaxClassificationName(dto.getTaxClassificationName());
        entity.setRemark(dto.getRemark());
        entity.setAddTime(new Date());
        entity.setAddUser(currUser.getUseraccount());
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(currUser.getUseraccount());
        businessscopeTaxcodeService.insertSelective(entity);
        return ResultVo.Success();
    }

    /**
     * 编辑经营范围
     * @param id
     * @param remark
     * @return
     */
    @PostMapping("/edit")
    public ResultVo edit(@JsonParam Long id, @JsonParam String remark){
        CurrUser currUser = getCurrUser();
        if (id == null){
            return ResultVo.Fail("id不能为空");
        }
        if (remark.length()>100){
            return ResultVo.Fail("备注不能超过100个字");
        }
        BusinessscopeTaxcodeEntity entity = businessscopeTaxcodeService.findById(id);
        if (entity == null){
            return ResultVo.Fail("数据错误");
        }
        entity.setRemark(remark);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(currUser.getUseraccount());
        businessscopeTaxcodeService.editByIdSelective(entity);
        return ResultVo.Success();
    }

    /**
     * 删除经营范围
     * @param id
     * @return
     */
    @PostMapping("/deleteScope")
    public ResultVo deleteScope(@JsonParam Long id){
        if (id == null){
            return ResultVo.Fail("id不能为空");
        }
        BusinessscopeTaxcodeEntity entity = businessscopeTaxcodeService.findById(id);
        if (entity == null){
            return ResultVo.Fail("数据错误");
        }
       /* List<BusinessScopeEntity> businessScopeEntity = businessScopeService.getScopeByBusinessContent(entity.getBusinessScopName(), null);
        if (CollectionUtil.isNotEmpty(businessScopeEntity)){
            return ResultVo.Fail("园区绑定了该经营范围，请前往删除后再操作");
        }*/
        ParkBusinessscopeEntity parkBusinessscopeEntity = parkBusinessscopeService.getParkBusinessscopeByParkIdAndName(null,entity.getBusinessScopName());
        if (parkBusinessscopeEntity != null){
            return ResultVo.Fail("园区绑定了该经营范围，请前往删除后再操作");
        }
        List<MemberCompanyEntity> memberCompanyEntity = memberCompanyService.getCompanyByBusinssContent(entity.getBusinessScopName());
        if (CollectionUtil.isNotEmpty(memberCompanyEntity)){
            return ResultVo.Fail("企业绑定了该经营范围，请前往删除后再操作");
        }
        businessscopeTaxcodeService.delById(id);
        return ResultVo.Success();
    }

    /**
     * 批量添加经营范围
     * @param file
     * @return
     */
    @PostMapping("/batchAddScope")
    public ResultVo batchAddScope(@RequestParam("file") MultipartFile file){
        CurrUser currUser = getCurrUser();
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<BusinessScopeBatchVO> excelList = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            excelList = ExcelImportUtil.importExcel(file.getInputStream(), BusinessScopeBatchVO.class, params);
        } catch (Exception e) {
            log.error("添加基础经营范围异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(excelList)){
            return ResultVo.Fail("文件不能为空");
        }
        Map<String,Object> map =businessscopeTaxcodeService.checkScopeTaxCode(excelList,currUser.getUseraccount());
        Object success = map.get("success");
        Object fail = map.get("fail");
        List<BusinessscopeTaxcodeEntity> successList = Convert.toList(BusinessscopeTaxcodeEntity.class,success);
        List<BusinessScopeBatchVO> failList =  Convert.toList(BusinessScopeBatchVO.class,fail);
        Map<String, Object> result = new HashMap<>();
        result.put("success", successList.size());
        result.put("failed", failList.size());
        if (CollectionUtil.isNotEmpty(successList)){
            // 新增
            businessscopeTaxcodeService.batchAddBusinessScopeTaxCode(successList);
        }
        if (CollectionUtil.isEmpty(failList)){
            result.put("downLoadUrl", "");
            return ResultVo.Success(result);
        }
        DictionaryEntity dicEntity = dictionaryService.getByCode("file_download_path");
        if (dicEntity == null) {
            return ResultVo.Fail("字典数据未配置");
        }
        File bag = new File(dicEntity.getDictValue() + "/" +  getCurrUser().getUseraccount());
        if(!bag.exists()){
            bag.mkdirs();//如果路径不存在就先创建路径
        }
        String fileName = System.currentTimeMillis() + ".xls";
        BufferedOutputStream bos = null;
        try {
            File downLoadFile = new File(bag, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(downLoadFile));
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), BusinessScopeBatchVO.class, failList);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("禁用商品添加异常：" + e.getMessage(), e);
            return ResultVo.Fail("禁用商品添加异常");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

}
