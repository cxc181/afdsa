package com.yuqian.itax.admin.controller.system;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.service.OemInvoiceCategoryRelaService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.dao.ClassificationCodeVatMapper;
import com.yuqian.itax.system.dao.InvoiceCategoryBaseMapper;
import com.yuqian.itax.system.entity.ClassificationCodeVatEntity;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.InvoiceCategoryBaseEntity;
import com.yuqian.itax.system.entity.dto.InvoiceCategoryBaseDTO;
import com.yuqian.itax.system.entity.dto.InvoiceCategoryRateDto;
import com.yuqian.itax.system.entity.query.InvoiceCategoryBaseQuery;
import com.yuqian.itax.system.entity.vo.GroupSelectVO;
import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseVO;
import com.yuqian.itax.system.service.ClassificationCodeVatService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.InvoiceCategoryBaseService;
import com.yuqian.itax.system.service.InvoiceCategoryService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.CompanyInvoiceCategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.assertj.core.util.Lists;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
* 基础类目库controller
* @version：1.0
*/
@Slf4j
@RestController
@RequestMapping("invoiceCategoryBase")
public class InvoiceCategoryBaseController extends BaseController {

    @Autowired
    InvoiceCategoryBaseService invoiceCategoryBaseService;
    @Autowired
    DictionaryService dictionaryService;
    @Autowired
    CompanyInvoiceCategoryService companyInvoiceCategoryService;
    @Autowired
    InvoiceCategoryService invoiceCategoryService;
    @Autowired
    OemInvoiceCategoryRelaService oemInvoiceCategoryRelaService;
    @Autowired
    ClassificationCodeVatService classificationCodeVatService;
    @Resource
    ClassificationCodeVatMapper classificationCodeVatMapper;
    @Resource
    private InvoiceCategoryBaseMapper invoiceCategoryBaseMapper;

    /**
     * 基础类目库列表查询（分页）
     */
    @PostMapping("page")
    public ResultVo queryInvoiceCategoryBasePageInfo(@RequestBody InvoiceCategoryBaseQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(null!=userEntity.getPlatformType()&& (userEntity.getPlatformType()!=1 && userEntity.getPlatformType()!=3)){
            return ResultVo.Fail("该用户不允许查看");
        }
        PageInfo<InvoiceCategoryBaseVO> page = invoiceCategoryBaseService.queryInvoiceCategoryBasePageInfo(query);
        return ResultVo.Success(page);
    }

    /**
     * 基础类目库导出
     */
    @PostMapping("export")
    public ResultVo exportInvoiceCategoryBase(@RequestBody InvoiceCategoryBaseQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(null!=userEntity.getPlatformType()&& userEntity.getPlatformType()!=1){
            return ResultVo.Fail("不是平台用户不允许查看");
        }
        List<InvoiceCategoryBaseVO> lists = invoiceCategoryBaseService.queryInvoiceCategoryBaseList(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("类目库", "类目库", InvoiceCategoryBaseVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("类目库导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }
    /**
     * 批量导入
     */
    @PostMapping("batch/import")
    public ResultVo batchImport(@RequestParam("file") MultipartFile file) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<InvoiceCategoryBaseDTO> list;
        List<InvoiceCategoryBaseDTO> failed = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            list = ExcelImportUtil.importExcel(file.getInputStream(), InvoiceCategoryBaseDTO.class, params);
        } catch (Exception e) {
            log.error("批量导入异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("文件内容为空");
        }
        InvoiceCategoryBaseEntity invoiceCategoryBaseEntity;
        for (InvoiceCategoryBaseDTO dto : list) {
            if (StringUtils.isBlank(dto.getTaxClassificationAbbreviation())) {
                dto.setFailed("企业类型不能为空");
                failed.add(dto);
                continue;
            }
            if (StringUtils.isBlank(dto.getTaxClassificationCode())) {
                dto.setFailed("企业类型不能为空");
                failed.add(dto);
                continue;
            } if (StringUtils.isBlank(dto.getTaxClassificationName())) {
                dto.setFailed("企业类型不能为空");
                failed.add(dto);
                continue;
            } if (StringUtils.isBlank(dto.getGoodsName())) {
                dto.setFailed("企业类型不能为空");
                failed.add(dto);
                continue;
            }
            String regex = "^[\\u4E00-\\u9FA5]+$";
            boolean isMatch =dto.getTaxClassificationAbbreviation().matches(regex);
            if (!isMatch) {
                dto.setFailed("发票税收分类简称格式不正确");
                failed.add(dto);
                continue;
            }
            String regexb = "^[*\\u4E00-\\u9FA5]+$";
            boolean isMatchb =dto.getGoodsName().matches(regexb);
            if (!isMatchb) {
                dto.setFailed("发票商品名称格式不正确");
                failed.add(dto);
                continue;
            }
            String regexc = "^[a-zA-Z0-9]+$";
            boolean isMatchc =dto.getTaxClassificationCode().matches(regexc);
            if (!isMatchc) {
                dto.setFailed("发票税收分类编码格式不正确");
                failed.add(dto);
                continue;
            }
            try {
                invoiceCategoryBaseEntity = new InvoiceCategoryBaseEntity();
                invoiceCategoryBaseEntity.setTaxClassificationAbbreviation(dto.getTaxClassificationAbbreviation());
                invoiceCategoryBaseEntity.setGoodsName(dto.getGoodsName());
                invoiceCategoryBaseEntity = invoiceCategoryBaseService.selectOne(invoiceCategoryBaseEntity);
                if (invoiceCategoryBaseEntity != null) {
                    dto.setFailed("服务类别已存在");
                    failed.add(dto);
                    continue;
                }
                invoiceCategoryBaseEntity = new InvoiceCategoryBaseEntity();
                invoiceCategoryBaseEntity.setTaxClassificationAbbreviation(dto.getTaxClassificationAbbreviation());
                invoiceCategoryBaseEntity.setTaxClassificationCode(dto.getTaxClassificationCode());
                invoiceCategoryBaseEntity.setTaxClassificationName(dto.getTaxClassificationName());
                invoiceCategoryBaseEntity.setGoodsName(dto.getGoodsName());
                invoiceCategoryBaseEntity.setAddTime(new Date());
                invoiceCategoryBaseEntity.setAddUser(currUser.getUseraccount());
                invoiceCategoryBaseEntity.setUpdateTime(new Date());
                invoiceCategoryBaseEntity.setUpdateUser(currUser.getUseraccount());
                invoiceCategoryBaseEntity.setRemark("管理后台批量插入");
                invoiceCategoryBaseService.insertSelective(invoiceCategoryBaseEntity);
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
        String fileName = "invoiceCategoryBase_" + System.currentTimeMillis() + ".xls";
        BufferedOutputStream bos = null;
        try {
            File downLoadFile = new File(bag, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(downLoadFile));
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), InvoiceCategoryBaseDTO.class, failed);
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
     * 类目编辑
     */
    @PostMapping("update")
    public ResultVo updateInvoiceCategoryBase(@RequestBody InvoiceCategoryBaseQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        String regexb = "^[*\\u4E00-\\u9FA5]+$";
        boolean isMatchb =query.getGoodsName().matches(regexb);
        if (!isMatchb) {
            return ResultVo.Fail("发票商品名称格式不正确");
        }
        InvoiceCategoryBaseEntity invoiceCategoryBaseEntity=invoiceCategoryBaseService.findById(query.getId());
        Example example=new Example(InvoiceCategoryBaseEntity.class);
        example.createCriteria().andEqualTo("taxClassificationAbbreviation",invoiceCategoryBaseEntity.getTaxClassificationAbbreviation()).andEqualTo("goodsName",query.getGoodsName()).andNotEqualTo("id",invoiceCategoryBaseEntity.getId());
        List<InvoiceCategoryBaseEntity> list=invoiceCategoryBaseService.selectByExample(example);
        if(CollectionUtil.isNotEmpty(list)){
            return ResultVo.Fail("商品名称已存在,请确定");
        }
        invoiceCategoryBaseEntity.setGoodsName(query.getGoodsName());
        invoiceCategoryBaseEntity.setUpdateTime(new Date());
        invoiceCategoryBaseEntity.setUpdateUser(getCurrUseraccount());
        invoiceCategoryBaseService.editByIdSelective(invoiceCategoryBaseEntity);
        //编辑另外3张表（t_e_company_invoice_category，t_e_invoice_category，t_r_oem_invoice_category）
        companyInvoiceCategoryService.batchUpdateByCategoryBaseId(invoiceCategoryBaseEntity.getId());
        invoiceCategoryService.batchUpdateByCategoryBaseId(invoiceCategoryBaseEntity.getId());
        oemInvoiceCategoryRelaService.batchUpdateByCategoryBaseId(invoiceCategoryBaseEntity.getId());
        return ResultVo.Success();
    }
    /**
     * 类目删除
     */
    @PostMapping("delete")
    public ResultVo deleteInvoiceCategoryBase(@JsonParam Long id){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        invoiceCategoryBaseService.delById(id);
        //删除另外4张表（t_e_company_invoice_category，t_e_invoice_category，t_e_invoice_category_group，t_r_oem_invoice_category）
        companyInvoiceCategoryService.deleteByCategoryBaseId(id);
        invoiceCategoryService.deleteByCategoryBaseId(id);
        oemInvoiceCategoryRelaService.deleteByCategoryBaseId(id);
        return ResultVo.Success();
    }

    /**
     * 基础类目库列表查询
     */
    @PostMapping("list")
    public ResultVo queryInvoiceCategoryBaseList(@RequestBody InvoiceCategoryBaseQuery query){

        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
      /*  if(null!=userEntity.getPlatformType()&& userEntity.getPlatformType()!=1){
            return ResultVo.Fail("不是平台用户不允许查看");
        }*/
        List<InvoiceCategoryBaseVO> list = invoiceCategoryBaseService.queryInvoiceCategoryBaseList(query);
        return ResultVo.Success(list);
    }

    /**
     * 基础类目库税收分类简称列表查询
     */
    @PostMapping("queryTaxClassificationAbbreviationList")
    public ResultVo queryTaxClassificationAbbreviationList(@RequestBody InvoiceCategoryBaseQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        /*if(null!=userEntity.getPlatformType()&& userEntity.getPlatformType()!=1){
            return ResultVo.Fail("不是平台用户不允许查看");
        }*/
        List<String> list = invoiceCategoryBaseService.queryTaxClassificationAbbreviationList(query);
        return ResultVo.Success(list);
    }

    /**
     * 根据类目id获取增值税率列表
     */
    @PostMapping("findVatFeeRateByCategoryId")
    public ResultVo findVatFeeRateByCategoryId(@JsonParam Long categoryId){
        getCurrUser();
       if(categoryId==null){
           return ResultVo.Fail("类目id不能为空");
       }
       List<BigDecimal> vatList = invoiceCategoryBaseService.findVatFeeRateByCategoryId(categoryId);
       Map<String,Object> data= new HashMap<>();
       data.put("vatFeeList",vatList);
        return ResultVo.Success(vatList);
    }

    /**
     * 集团开票下拉专用类目查询接口
     */
    @PostMapping("queryInvoiceCategoryBaseGroupSelect")
    public ResultVo queryInvoiceCategoryBaseGroupSelect(){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        List<GroupSelectVO> list = invoiceCategoryBaseService.queryInvoiceCategoryBaseGroupSelect();
        return ResultVo.Success(list);
    }


    @ApiOperation(value="导入税率", notes="导入税率")
    @PostMapping("importTaxRate")
    @Transactional(rollbackFor = Exception.class)
    public ResultVo importTaxRate(@RequestParam(name = "file",required = true) MultipartFile file) throws Exception{
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("模板文件为空!");
        }

        //2、定义正确表头有序集合
        List<String> bList = new ArrayList<>();
        Collections.addAll(bList, "税收分类编码", "增值税率(%)");
        //3、开始校验  检查 Excel 文件表头信息
        boolean headerFlag =  checkExcelHeaders(file, bList);
        if (!headerFlag){
            return ResultVo.Fail("上传失败，模板错误，请核对后重新上传");
        }

        // 正确的文件
        List<InvoiceCategoryRateDto> list;
        // 记录失败的文件
        List<InvoiceCategoryRateDto> failed = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            list = ExcelImportUtil.importExcel(file.getInputStream(), InvoiceCategoryRateDto.class, params);
        } catch (Exception e) {
            log.error("导入文件异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("文件内容为空");
        }

        List<InvoiceCategoryRateDto> newList = new ArrayList<>();
        Set<String> set= new HashSet<>();
        // 先判断 excel里面 税收分类编码是否有重复的数据
        for(InvoiceCategoryRateDto dto:list){
            if(StringUtils.isBlank(dto.getTaxClassificationCode())){
                dto.setFailed("税收分类编码不能为空");
                failed.add(dto);
                continue;
            }
            if(!set.add(dto.getTaxClassificationCode())){
                dto.setFailed("税收分类编码重复:"+dto.getTaxClassificationCode());
                failed.add(dto);
                continue;
            }else{
                newList.add(dto);
            }
        }

        // 开始循环数据
        for(InvoiceCategoryRateDto vo:newList) {
            if (StringUtils.isBlank(vo.getTaxClassificationCode())) {
                vo.setFailed("税收分类编码不能为空");
                failed.add(vo);
                continue;
            }
            if (StringUtils.isBlank(vo.getVatFeeRate())) {
                vo.setFailed("增值税率不能为空");
                failed.add(vo);
                continue;
            }
            Integer count = invoiceCategoryBaseMapper.getInvoiceCategoryOne(vo.getTaxClassificationCode());
            if (count == 0) {
                vo.setFailed("该税收分类编码在数据库不存在");
                failed.add(vo);
                continue;
            }

            // 验证增值税率格式，只能输入0-100的整数,只能用,分隔
            String vatFeeRate = vo.getVatFeeRate();
            List<ClassificationCodeVatEntity> newCodeVatList = new ArrayList<>();
            // 记录错误的信息
            List<InvoiceCategoryRateDto> errorList = new ArrayList<>();
            try {
                // 将税率  中文逗号 变成英文逗号
                String replace = vatFeeRate.replace("，", ",");
                String[] split = replace.split(",");

                for (String rate : split) {
                    // 0-100 的正整数   正则表达式
                    String reg = "^(0|[1-9]\\d?|100)$";
                    boolean isMatch = Pattern.compile(reg).matcher(rate).matches();
                    if (!isMatch) {
                        vo.setFailed("增值税率只能输入0-100的正整数");
                        failed.add(vo);
                        errorList.add(vo);
                        break;
                    }

                    ClassificationCodeVatEntity vatEntity = new ClassificationCodeVatEntity();
                    // 税收分类编码
                    vatEntity.setTaxClassificationCode(vo.getTaxClassificationCode());
                    // 增值税率
                    vatEntity.setVatFeeRate(new BigDecimal(rate));
                    vatEntity.setAddTime(new Date());
                    vatEntity.setAddUser(currUser.getUseraccount());
                    newCodeVatList.add(vatEntity);
                }
            } catch (Exception e) {
                vo.setFailed("保存增值税率格式错误,需要以逗号,分隔");
                failed.add(vo);
                continue;
            }

            // 文件里没有错误的信息在进行数据修改
            if (CollectionUtils.isEmpty(errorList)) {
                if (CollectionUtils.isNotEmpty(newCodeVatList)) {
                    // 根据税收分类编码 查询有没有对应的 增值税率,有的话先删除原来的，在重新新增
                    List<ClassificationCodeVatEntity> rateList = classificationCodeVatMapper.queryVatFeeRateList(vo.getTaxClassificationCode());
                    if (CollectionUtils.isNotEmpty(rateList)) {
                        rateList.forEach(e -> {
                            classificationCodeVatService.delById(e.getId());
                        });
                    }
                    newCodeVatList.forEach(e -> {
                        classificationCodeVatService.insertSelective(e);
                    });
                    // t_r_classification_code_vat  关系表新增数据
                    //classificationCodeVatMapper.insertList(newCodeVatList);
                }
            }
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
        String fileName = "importTaxRate_" + System.currentTimeMillis() + ".xls";
        BufferedOutputStream bos = null;
        try {
            File downLoadFile = new File(bag, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(downLoadFile));
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), InvoiceCategoryRateDto.class, failed);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("导入增值税率，失败记录保存服务器异常：" + e.getMessage(), e);
            return ResultVo.Fail("导入增值税率，失败记录保存失败");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }


    /**
     * 检查 Excel 文件表头信息
     * @param file
     * @param dataList
     * @return
     */
    public boolean checkExcelHeaders(MultipartFile file, List<String> dataList) throws IOException {
        InputStream inputStream = null;
        try {
            byte [] byteArr=file.getBytes();
            inputStream = new ByteArrayInputStream(byteArr);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println(sheet.getLastRowNum());
            //获取 excel 第一行数据（表头）
            Row row = sheet.getRow(0);
            //存放表头信息
            List<String> headerList = new ArrayList<>();
            //算下有多少列
            int colCount = sheet.getRow(0).getLastCellNum();
            // 这里的5默认第一行为5列
            for (int j = 0; j <5; j++) {
                Cell cell = row.getCell(j);
                String cellValue = cell.getStringCellValue().trim();
                System.out.println("返回的值:"+cellValue);
                if(StringUtils.isNotBlank(cellValue)) {
                    headerList.add(cellValue);
                }
            }
            if(headerList.size() != dataList.size()){
                return false;
            }else{
                Collections.sort(dataList);
                Collections.sort(headerList);
                for(int i=0;i<dataList.size();i++){
                    if(!dataList.get(i).equals(headerList.get(i)))
                        return false;
                }
                return true;
            }
            //return dataList.equals(headerList);
        }catch (Exception e){
            log.error("模版表头解析错误：", e);
            return false;
        }finally {
            if (inputStream != null){
                inputStream.close();
            }
        }
    }


}
