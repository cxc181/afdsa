package com.yuqian.itax.admin.controller.crowdLabel;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.vo.OemAccessPartyDetailVO;
import com.yuqian.itax.agent.entity.vo.OemAccessPartyInfoVO;
import com.yuqian.itax.agent.service.OemAccessPartyService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.CrowdLabelEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.vo.CrowdAccoutVO;
import com.yuqian.itax.user.entity.vo.CrowdLabelChangeVO;
import com.yuqian.itax.user.entity.vo.CrowdLabelInsertVO;
import com.yuqian.itax.user.entity.vo.CrowdLabelVO;
import com.yuqian.itax.user.service.CrowdLabelChangeService;
import com.yuqian.itax.user.service.CrowdLabelService;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCrowdLabelRelaService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
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
 * 人群标签controller
 */
@RestController
@RequestMapping("/crowLabel")
@Slf4j
public class CrowdLabelController extends BaseController {

    @Autowired
    private CrowdLabelService crowdLabelService;

    @Autowired
    private MemberAccountService memberAccountService;

    @Autowired
    private MemberCrowdLabelRelaService memberCrowdLabelRelaService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private CrowdLabelChangeService crowdLabelChangeService;

    @Autowired
    private OemAccessPartyService oemAccessPartyService;

    @Autowired
    private OemService oemService;


    @ApiOperation("人群标签列表页")
    @PostMapping("page")
    public ResultVo listPageCrowdLabel(@RequestBody CrowdLabelVO query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() == 2){
            query.setOemCode(userEntity.getOemCode());
        }
        PageInfo<CrowdLabelVO> page = crowdLabelService.listPageCrowdLabel(query);
        return  ResultVo.Success(page);
    }

    @ApiOperation("历史记录")
    @PostMapping("change/page")
    public ResultVo listPageCrowdLabelChange(@RequestBody CrowdLabelChangeVO query){
        if (query.getCrowdLabelId() == null){
            return ResultVo.Fail("人群标签id不能为空");
        }
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        PageInfo<CrowdLabelChangeVO> page = crowdLabelChangeService.listPageCrowdLabelChange(query);
        return  ResultVo.Success(page);
    }

    @ApiOperation("导出人群数据")
    @PostMapping("batch/crowdLabel")
    public ResultVo batchCrowdLabel(@RequestBody CrowdLabelVO query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() == 2){
            query.setOemCode(userEntity.getOemCode());
        }
        List<CrowdLabelVO> list = crowdLabelService.listCrowdLabel(query);
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("人群标签列表"+ DateUtil.format(new Date(),"yyyy-MM-dd"), "人群标签列表"+ DateUtil.format(new Date(),"yyyy-MM-dd"), CrowdLabelVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("人群标签列表：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    @ApiOperation("导出人群标签用户")
    @PostMapping("batch/crowdAccount")
    public ResultVo batchCrowdAccount(@JsonParam Long crowdLabelId,@JsonParam String crowdLabelName){
        List<CrowdAccoutVO> list = memberCrowdLabelRelaService.queryCrowdAccountById(crowdLabelId);
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel(crowdLabelName+ DateUtil.format(new Date(),"yyyy-MM-dd"), crowdLabelName+ DateUtil.format(new Date(),"yyyy-MM-dd"), CrowdAccoutVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error(crowdLabelName + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    @ApiOperation("人群标签作废")
    @PostMapping("crowdToVoid")
    public ResultVo crowdToVoid(@JsonParam Long crowdLabelId){
        CurrUser currUser = getCurrUser();
        if(crowdLabelId == null) {
            return ResultVo.Fail("人群标签错误");
        }
        CrowdLabelEntity crowdLabelEntity = crowdLabelService.findById(crowdLabelId);
        if (crowdLabelEntity == null){
            return ResultVo.Fail("人群标签错误");
        }
        Integer count = crowdLabelService.getActivityByCrowdId(crowdLabelId);
        if (count == null || count == 0){
            //  删除人群标签以及人群标签下的用户
            crowdLabelService.updateStatusByid(crowdLabelEntity.getId(),currUser.getUseraccount());
            crowdLabelEntity.setStatus(2);
            crowdLabelChangeService.addCrowdLabelChange(crowdLabelEntity,currUser.getUseraccount(),"标签作废");
            return ResultVo.Success();
        }else{
            return ResultVo.Fail("该人群标签已配置在活动中，不可作废");
        }
    }

    /**
     * 根据oemCode 获取H5接入方信息
     * @param oemCode
     * @return
     */
    @PostMapping("queryAccessPartyByOemCode")
    public ResultVo queryAccessPartyByOemCode(@JsonParam String oemCode){
        CurrUser currUser = getCurrUser();
        if (StringUtil.isBlank(oemCode)){
            return ResultVo.Fail("oem机构不能为空");
        }
        List<OemAccessPartyInfoVO> list = oemAccessPartyService.queryByOemCode(oemCode,1);
        return ResultVo.Success(list);
    }

    @ApiOperation("新增人群标签")
    @PostMapping("add/crowdLabel")
    public ResultVo addCrowdLabel(@RequestBody CrowdLabelVO query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() == 2){
            if (query.getOemCode() == null || !query.getOemCode().equals(userEntity.getOemCode())){
                return ResultVo.Fail("oem机构错误");
            }
        }
        if (query.getCrowdLabelName().trim() == null || query.getCrowdLabelName().trim().equals("")){
            return ResultVo.Fail("人群标签名称不能为空");
        }
        CrowdLabelEntity crowdLabelEntity;
        crowdLabelEntity = crowdLabelService.queryCrowdLabelByLabelName(query.getCrowdLabelName().trim(),query.getOemCode());
        if (crowdLabelEntity != null ){
            return ResultVo.Fail("人群标签名称已存在");
        }
        crowdLabelEntity = new CrowdLabelEntity();
        crowdLabelEntity.setCrowdLabelName(query.getCrowdLabelName().trim());
        crowdLabelEntity.setOemCode(query.getOemCode());
        crowdLabelEntity.setStatus(1);
        crowdLabelEntity.setAddUserMode(query.getAddUserMode());
        crowdLabelEntity.setMemberUserNum(0);
        // 为H5接入方
        if (query.getAddUserMode() != null && query.getAddUserMode() == 2){
            OemAccessPartyEntity oemAccessPartyEntity =  oemAccessPartyService.findById(query.getAccessPartyId());
            if (oemAccessPartyEntity.getStatus() != 1){
                return ResultVo.Fail("接入方状态不正确");
            }
            List<CrowdLabelEntity> crowdList = crowdLabelService.queryByAccessPartyId(query.getAccessPartyId());
            if (crowdList != null && crowdList.size()>0){
                return ResultVo.Fail("该接入方已绑定人群标签");
            }
            crowdLabelEntity.setAccessPartyId(query.getAccessPartyId());
            List<Long> accessUserList = crowdLabelService.check(crowdLabelEntity.getAccessPartyId(),crowdLabelEntity.getOemCode());
            if (accessUserList != null && accessUserList.size()>0){
                crowdLabelEntity.setMemberUserNum(accessUserList.size());
            }
        }
        crowdLabelEntity.setAddUser(userEntity.getUsername());
        crowdLabelEntity.setAddTime(new Date());
        if (query.getCrowdLabelDesc() != null){
            crowdLabelEntity.setCrowdLabelDesc(query.getCrowdLabelDesc());
        }
        crowdLabelService.addCrowd(crowdLabelEntity);
        return ResultVo.Success();
    }

    @ApiOperation("编辑人群标签")
    @PostMapping("editCrowd")
    public ResultVo editCrowd(@JsonParam Long crowdLabelId){
        if (crowdLabelId == null){
            return ResultVo.Fail("人群标签id不能为空");
        }
        OemAccessPartyDetailVO vo = crowdLabelService.getDetailInfo(crowdLabelId);
        if (vo == null){
            return ResultVo.Fail("人群标签id错误");
        }
        OemEntity oemEntity = oemService.getOem(vo.getOemCode());
        if (oemEntity.getOemStatus() != 1){
            return ResultVo.Fail("该人群标签所属于的oem机构已下架");
        }
        return ResultVo.Success(vo);
    }

    @ApiOperation("修改人群标签")
    @PostMapping("updateCrowd")
    public ResultVo updateCrowd(@RequestBody CrowdLabelVO query){
        CurrUser currUser = getCurrUser();
        CrowdLabelEntity crowdLabelEntity = new CrowdLabelEntity();
        if (query.getCrowdLabelName() != null){
            if (query.getCrowdLabelName().length()>10){
                return ResultVo.Fail("人群标签长度超过10个字符");
            }else{
                CrowdLabelEntity entity = crowdLabelService.queryCrowdLabelByLabelNameNotId(query.getCrowdLabelName(),query.getOemCode(),query.getId());
                if (entity != null){
                    return ResultVo.Fail("该oem机构下已有该标签");
                }
                crowdLabelEntity.setCrowdLabelName(query.getCrowdLabelName());
            }
        }
        if (query.getCrowdLabelDesc() != null){
            crowdLabelEntity.setCrowdLabelDesc(query.getCrowdLabelDesc());
        }
        crowdLabelEntity.setId(query.getId());
        if (query.getAddUserMode() == 2 && query.getAccessPartyId() != null){
            crowdLabelEntity.setAccessPartyId(query.getAccessPartyId());
        }
        crowdLabelEntity.setUpdateTime(new Date());
        crowdLabelEntity.setUpdateUser(currUser.getUseraccount());
        crowdLabelService.updateCrowd(crowdLabelEntity);
        return ResultVo.Success();
    }

    @ApiOperation("删除用户")
    @PostMapping("delete/customer")
    public ResultVo deleteCustomer(@RequestParam("file") MultipartFile file,@RequestParam Long crowdLabelId){
        CurrUser currUser = getCurrUser();
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<CrowdLabelInsertVO> list = Lists.newArrayList();
        List<CrowdLabelInsertVO> failed = Lists.newArrayList();
        List<CrowdLabelInsertVO> success = Lists.newArrayList();
        List<CrowdLabelInsertVO> excelList = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            excelList = ExcelImportUtil.importExcel(file.getInputStream(), CrowdLabelInsertVO.class, params);
        } catch (Exception e) {
            log.error("添加用户异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(excelList)) {
            return ResultVo.Fail("文件内容为空");
        }
        //  去除无效行
        for (CrowdLabelInsertVO vo:excelList){
            if (StringUtil.isNotBlank(vo.getPhoneNumber())){
                list.add(vo);
            }
        }
        if (list.size()<1){
            return ResultVo.Fail("导入模板不正确");
        }
        List<CrowdAccoutVO> accoutList = memberCrowdLabelRelaService.queryCrowdAccountById(crowdLabelId);
        // 校验数据
        for(int i=0;i<list.size();i++){
            // 去除重复
            if (list.get(i).getStatus() == null){
                continue;
            }
            for(int j=i+1;j<list.size();j++){
                if (list.get(i).getPhoneNumber().equals(list.get(j).getPhoneNumber())){
                    list.get(j).setReg("该人群标签下无该用户");
                    failed.add(list.get(j));
                    list.get(j).setStatus(null);
                }
            }
            //  判断删除的用户在标签下是否存在
            boolean flag = false;
            for(CrowdAccoutVO vo:accoutList){
                if (vo.getMemberAccount().equals(list.get(i).getPhoneNumber().trim())){
                    flag = true;
                    break;
                }
            }
            if (!flag){
                list.get(i).setReg("该人群标签下无该用户");
                failed.add(list.get(i));
                continue;
            }
            success.add(list.get(i));
        }

        if (success.size()>0){
            memberCrowdLabelRelaService.deleteAccountByMemberId(success,crowdLabelId,currUser.getUseraccount());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", success.size());
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
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), CrowdLabelInsertVO.class, failed);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("人群标签删除用户异常：" + e.getMessage(), e);
            return ResultVo.Fail("人群标签删除用户异常");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

    @ApiOperation("添加用户")
    @PostMapping("add/customer")
    public ResultVo addCustomer(@RequestParam("file") MultipartFile file,@RequestParam Long crowdLabelId,@RequestParam String oemCode) {
        CurrUser currUser = getCurrUser();
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<CrowdLabelInsertVO> list = Lists.newArrayList();
        List<CrowdLabelInsertVO> failed = Lists.newArrayList();
        List<CrowdLabelInsertVO> success = Lists.newArrayList();
        List<CrowdLabelInsertVO> excelList = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            excelList = ExcelImportUtil.importExcel(file.getInputStream(), CrowdLabelInsertVO.class, params);
        } catch (Exception e) {
            log.error("添加用户异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(excelList)) {
            return ResultVo.Fail("文件内容为空");
        }
        //  去除无效行
        for (CrowdLabelInsertVO vo:excelList){
            if (StringUtil.isNotBlank(vo.getPhoneNumber())){
                list.add(vo);
            }
        }
        if (list.size()<1){
            return ResultVo.Fail("导入模板不正确");
        }
        for(int i=0;i<list.size();i++){
            // 去除重复
            if (list.get(i).getStatus() == null){
                continue;
            }
            for(int j=i+1;j<list.size();j++){
                if (list.get(i).getPhoneNumber().trim().equals(list.get(j).getPhoneNumber().trim())){
                    list.get(j).setReg("该用户已添加");
                    failed.add(list.get(j));
                    list.get(j).setStatus(null);
                }
            }
            // 判断该用户是否存在且为非注销状态
            MemberAccountEntity memberAccountEntity = memberAccountService.queryByAccount(list.get(i).getPhoneNumber().trim(),oemCode);
            if (memberAccountEntity == null || memberAccountEntity.getStatus() == 2){
                list.get(i).setReg("用户不存在或已注销");
                failed.add(list.get(i));
                continue;
            }
            list.get(i).setMemberId(memberAccountEntity.getId());
            //判断用户是否在同一oem机构下存在
            List<Long> memberIdList = crowdLabelService.queryMemberIdByOemCodeAndStatus(oemCode);
            if (CollectionUtil.isNotEmpty(memberIdList)){
                boolean falg = false;
                for(int j=0;j<memberIdList.size();j++){
                    if (list.get(i).getMemberId().equals(memberIdList.get(j))){
                        list.get(i).setReg("该用户已添加人群");
                        failed.add(list.get(i));
                        falg = true;
                        break;
                    }
                }
                if (falg){
                    continue;
                }
            }
            success.add(list.get(i));
        }
        if (success.size()>0){
            memberCrowdLabelRelaService.addBatch(success,oemCode,crowdLabelId,currUser.getUseraccount());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", success.size());
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
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), CrowdLabelInsertVO.class, failed);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("人群标签添加用户异常：" + e.getMessage(), e);
            return ResultVo.Fail("人群标签添加用户异常");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }

}
