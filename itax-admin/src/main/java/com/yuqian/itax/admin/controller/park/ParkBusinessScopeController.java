package com.yuqian.itax.admin.controller.park;

import cn.hutool.core.convert.Convert;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.entity.RegisterOrderEntity;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.dto.ParkBusinessScopeDTO;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeBatchVO;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeVO;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.system.entity.BusinessScopeEntity;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.ParkBusinessscopeEntity;
import com.yuqian.itax.system.entity.query.ParkBusinessScopeQuery;
import com.yuqian.itax.system.entity.vo.BusinessScopeTaxCodeVO;
import com.yuqian.itax.system.entity.vo.IndustryAndParkInfoVo;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.util.util.DateUtil;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * 园区经营范围Controll
 * auth: wangKaiLing
 * time: 2021/12/31
 */
@RestController
@RequestMapping("/parkBusinessScope")
@Slf4j
public class ParkBusinessScopeController extends BaseController {

    @Autowired
    private ParkBusinessscopeService parkBusinessscopeService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private BusinessscopeTaxcodeService businessscopeTaxcodeService;
    @Autowired
    DictionaryService dictionaryService;
    @Autowired
    private BusinessScopeService businessScopeService;
    @Autowired
    private RegisterOrderService registerOrderService;
    @Autowired
    private IndustryService industryService;

    /**
     * 园区经营范围列表查询
     * @param query
     * @return
     */
    @PostMapping("/queryParkBusinessScope")
    public ResultVo queryParkBusinessScopeList(@RequestBody ParkBusinessScopeQuery query ){
        //验证登陆
        getCurrUser();
        UserEntity userEntity=userService.findById(getCurrUserId());
        if(userEntity.getPlatformType()!=null&& (userEntity.getPlatformType()!=1 && userEntity.getPlatformType() !=3)){
            return ResultVo.Fail("不是平台或园区账号不允许查看园区列表");
        }
        PageInfo<ParkBusinessScopeVO> list =  parkBusinessscopeService.queryByfindByParkIdAndbusinessScopeName(query);
        return ResultVo.Success(list);
    }

    /**
     * 园区经营范围导出
     * @param query
     * @return
     */
    @PostMapping("/parkBusinessScopeExport")
    public ResultVo parkBusinessScopeExport(@RequestBody ParkBusinessScopeQuery query ){
        getCurrUser();
        UserEntity userEntity=userService.findById(getCurrUserId());
        if(userEntity.getPlatformType()!=null&& (userEntity.getPlatformType()!=1 && userEntity.getPlatformType() !=3)){
            return ResultVo.Fail("不是平台或园区账号不允许查看园区列表");
        }
        List<ParkBusinessScopeVO> list = parkBusinessscopeService.parkBusinessScopeExport(query);
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无数据导出");
        }
        ParkEntity parkEntity = parkService.findById(query.getParkId());
        try {
            exportExcel(parkEntity.getParkName()+"经营范围"+ DateUtil.format(new Date(),"yyyy-MM-dd"), parkEntity.getParkName()+"经营范围"+ DateUtil.format(new Date(),"yyyy-MM-dd"), ParkBusinessScopeVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("园区经营范围：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * 添加经营范围
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("/addBusinessScope")
    public ResultVo addBusinessScope(@RequestBody @Valid ParkBusinessScopeDTO dto, BindingResult result){
        getCurrUser();
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        ParkBusinessscopeEntity parkBusinessscopeEntity = parkBusinessscopeService.getParkBusinessscopeByParkIdAndName(dto.getParkId(),dto.getBusinessScopeName());
        if (parkBusinessscopeEntity != null){
            return ResultVo.Fail("园区已存在该经营范围");
        }
        parkBusinessscopeEntity = new ParkBusinessscopeEntity();
        parkBusinessscopeEntity.setBusinessscopeName(dto.getBusinessScopeName());
        parkBusinessscopeEntity.setParkId(dto.getParkId());
        parkBusinessscopeEntity.setAddTime(new Date());
        parkBusinessscopeEntity.setAddUser(getCurrUser().getUseraccount());
        // 是否删除  正常为1
        parkBusinessscopeEntity.setIsDelete(1);
        parkBusinessscopeService.addParkBusinessScope(parkBusinessscopeEntity);
        return ResultVo.Success();
    }

    /**
     * 批量添加经营范围
     * @param file
     * @param parkId
     * @return
     */
    @PostMapping("/batchAddBusinessScope")
    public ResultVo batchAddBusinessScope(@RequestParam("file") MultipartFile file, @RequestParam Long parkId){
        CurrUser currUser = getCurrUser();
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<ParkBusinessScopeBatchVO> excelList = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            excelList = ExcelImportUtil.importExcel(file.getInputStream(), ParkBusinessScopeBatchVO.class, params);
        } catch (Exception e) {
            log.error("添加园区经营范围异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(excelList)) {
            return ResultVo.Fail("文件内容为空");
        }
        Map<String,Object> map = parkBusinessscopeService.checkParkBusinessScope(excelList,parkId,currUser.getUseraccount());
        Object fail = map.get("fail");
        Object success = map.get("success");
        List<ParkBusinessScopeBatchVO> failList = Convert.toList(ParkBusinessScopeBatchVO.class,fail);
        List<ParkBusinessscopeEntity> successList = Convert.toList(ParkBusinessscopeEntity.class,success);
        Map<String, Object> result = new HashMap<>();
        result.put("success", successList.size());
        result.put("failed", failList.size());
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
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), ParkBusinessScopeBatchVO.class, failList);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("园区经营范围添加异常：" + e.getMessage(), e);
            return ResultVo.Fail("园区经营范围添加异常");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

    /**
     * 校验经营范围是否可以删除
     * @param id
     * @return
     */
    @PostMapping("/checkDelete")
    public ResultVo checkDelete(@JsonParam Long id){
        ParkBusinessscopeEntity entity = parkBusinessscopeService.findById(id);
        List<BusinessScopeEntity> list = businessScopeService.getScopeByBusinessByParkId(entity.getParkId(),entity.getBusinessscopeName());
        if (CollectionUtil.isNotEmpty(list)){
            return ResultVo.Success("将同时删除行业数据管理的经营范围，是否确定？");
        }else{
            return  ResultVo.Success();
        }
    }

    /**
     * 删除经营范围
     * @param id
     * @return
     */
    @PostMapping("/deleteBusinessScope")
    public ResultVo deleteBusinessScope(@JsonParam Long id){
        CurrUser currUser = getCurrUser();
        ParkBusinessscopeEntity entity = parkBusinessscopeService.findById(id);
        if (entity == null){
            return ResultVo.Fail("选择删除的经营范围不存在");
        }
        parkBusinessscopeService.deleteParkBusinessScope(entity,currUser.getUseraccount());
        return ResultVo.Success();
    }

}
