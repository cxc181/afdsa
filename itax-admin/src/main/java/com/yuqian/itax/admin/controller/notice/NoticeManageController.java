package com.yuqian.itax.admin.controller.notice;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.google.common.collect.Maps;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.enums.OemStatusEnum;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.entity.NoticeManageEntity;
import com.yuqian.itax.message.entity.dto.NoticeManageUserDTO;
import com.yuqian.itax.message.entity.po.NoticeManagePO;
import com.yuqian.itax.message.entity.query.NoticeManageQuery;
import com.yuqian.itax.message.entity.vo.NoticeManageVO;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.message.service.NoticeManageService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.service.MemberAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.assertj.core.util.Lists;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通知管理
 */
@RestController
@RequestMapping("/notice/manage")
@Slf4j
public class NoticeManageController extends BaseController {


    @Autowired
    NoticeManageService manageService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    OemService oemService;
    @Autowired
    MessageNoticeService messageNoticeService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private OssService ossService;

    /**
     * 获取通知管理（分页）
     */
    @PostMapping("/getNoticeManage")
    public ResultVo getNoticeManage(@RequestBody NoticeManageQuery query){
        //登陆校验
        getCurrUser();
        query.setOemCode(getRequestHeadParams("oemCode"));
        PageInfo<NoticeManageEntity> pageInfo=manageService.getNoticeManage(query);

        return ResultVo.Success(pageInfo);
    }

    /**
     * 新增通知管理
     */
    @PostMapping("/addNoticeManage")
    public ResultVo addNoticeManage(@RequestBody NoticeManagePO po){
        //登陆校验
        getCurrUser();
        try{
            String [] users=null;
            List<Map<String ,Object>> mapList=new ArrayList<>();
            if(po.getNoticeObj()!=null && po.getNoticeObj()==2){ // 2-指定小程序用户
                if (po.getOemCode().contains(",")) {
                    return ResultVo.Fail("不允许选择多机构");
                }
                if (StringUtil.isEmpty(po.getFileName())) {
                    return ResultVo.Fail("未上传用户文件");
                }

                Workbook wb = manageService.getUserFile(po.getFileName());
                Sheet sheetAt = wb.getSheetAt(0);
                int rows = sheetAt.getPhysicalNumberOfRows();
                if (!"手机号".equals(sheetAt.getRow(0).getCell(0).getStringCellValue())) {
                    return ResultVo.Fail("文件格式不正确");
                }
                try {
                    wb.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
                StringBuilder sb = new StringBuilder();
                List<NoticeManageUserDTO> failed = Lists.newArrayList();
                for (int i = 1; i < rows; i++) {
                    Row row = sheetAt.getRow(i);
                    Cell cell = null;
                    String phone = null;
                    if (null != row) {
                        cell = row.getCell(0);
                    }
                    if (null != cell) {
                        cell.setCellType(CellType.STRING);
                        phone = cell.getStringCellValue();
                    }
                    if (StringUtil.isEmpty(phone)) {
                        return ResultVo.Fail("导入用户手机号存在空数据");
                    }
                    // 数据校验
                    try {
                        this.checkoutUser(phone, po.getOemCode(), sb);
                        MemberAccountEntity memberAccountEntity=memberAccountService.queryByAccount(phone,po.getOemCode());
                        if(null == memberAccountEntity){
                            continue;
                        }
                        Map<String ,Object>  map=new HashMap<>();
                        map.put("phone",memberAccountEntity.getMemberPhone());
                        map.put("userId",memberAccountEntity.getId());
                        mapList.add(map);
                    } catch (BusinessException e) {
                        NoticeManageUserDTO dto = new NoticeManageUserDTO();
                        dto.setMemberPhone(phone);
                        dto.setFailed(e.getLocalizedMessage());
                        failed.add(dto);
                    }
                }
                if (failed.size() > 0) {
                    Map<String, Object> result = Maps.newHashMap();
                    result.put("failed", failed.size());
                    result.put("success", rows-1-failed.size());
                    DictionaryEntity dicEntity = dictionaryService.getByCode("file_download_path");
                    if (dicEntity == null) {
                        return ResultVo.Fail("字典数据未配置");
                    }
                    File bag = new File(dicEntity.getDictValue() + "/" + getCurrUseraccount());
                    if(!bag.exists()){
                        bag.mkdirs();//如果路径不存在就先创建路径
                    }
                    String fileName = po.getOriginalFileName().split("\\.")[0] + "失败文件.xls";
                    BufferedOutputStream bos = null;
                    try {
                        File downLoadFile = new File(bag, fileName);
                        bos = new BufferedOutputStream(new FileOutputStream(downLoadFile));
                        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), NoticeManageUserDTO.class, failed);
                        workbook.write(bos);
                        workbook.close();
                    } catch (Exception e) {
                        log.error("导入用户失败记录,保存服务器异常：" + e.getMessage(), e);
                        return ResultVo.Fail("导入用户失败记录,保存失败");
                    }finally {
                        IOUtils.closeQuietly(bos);
                    }
                    result.put("downLoadUrl", fileName);
                    return new ResultVo<>(ResultConstants.ADD_NOTICE_FAILED, result);
                } else {
                    if (sb.length() > 0) {
                        po.setUserPhones(sb.deleteCharAt(0).toString());
                        manageService.addNoticeManage(po,getCurrUseraccount(),mapList,po.getOemCode());
                    } else {
                        return ResultVo.Fail("新增通知失败，导入用户为空");
                    }
                }
            }

            if(po.getNoticeObj()!=null&&po.getNoticeObj()==1){ // 1-所有小程序用户
                List<String> oemList = Lists.newArrayList();
                if("".equals(po.getOemCode())) { //空字符串默认选择所有oem机构
                    // 查询所有已上线的oem机构
                    OemEntity oemEntity = new OemEntity();
                    oemEntity.setOemStatus(OemStatusEnum.YES.getValue());
                    List<OemEntity> select = oemService.select(oemEntity);
                    if (CollectionUtil.isNotEmpty(select)) {
                        oemList = select.stream().map(OemEntity::getOemCode).collect(Collectors.toList());
                    }
                } else {
                    oemList = Lists.newArrayList(po.getOemCode().split(","));
                }
                for (String oemCode : oemList) {
                    List<MemberAccountEntity>  list=memberAccountService.queryMemberByStatus(oemCode);
                    if (CollectionUtil.isEmpty(list)) {
                        continue;
                    }
                    for (MemberAccountEntity memberAccountEntity:list) {
                        Map<String ,Object>  map=new HashMap<>();
                        map.put("phone",memberAccountEntity.getMemberPhone());
                        map.put("userId",memberAccountEntity.getId());
                        mapList.add(map);
                    }
                    NoticeManageEntity entity=manageService.addNoticeManage(po,getCurrUseraccount(),mapList,oemCode);
                }
            }
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 编辑通知管理
     */
   /* @PostMapping("/updateNoticeManage")
    public ResultVo updateNoticeManage(@RequestBody NoticeManagePO po){
        //登陆校验
        getCurrUser();
        return ResultVo.Success();
    }*/
    /**
     * 通知管理详情
     */
    @PostMapping("/queryNoticeManageDetail")
    public ResultVo queryNoticeManageDetail(@JsonParam Long id){
        //登陆校验
        getCurrUser();
        NoticeManageEntity entity= manageService.findById(id);
        NoticeManageVO noticeManageVO=new NoticeManageVO();
        BeanUtils.copyProperties(entity,noticeManageVO);
        OemEntity oemEntity=oemService.getOem(entity.getOemCode());
        noticeManageVO.setOemName(oemEntity.getOemName());


        return ResultVo.Success(noticeManageVO);
    }

    /**
     * 修改通知状态
     */
    @PostMapping("/updateNoticeManageStatus")
    public ResultVo updateNoticeManageStatus(@JsonParam Long id,@JsonParam Integer status){
        //登陆校验
        getCurrUser();
        NoticeManageEntity entity= manageService.findById(id);
        if(status==2&&entity.getSendStatus()!=1){
            return ResultVo.Fail("已发布的通知才能下线，请确认");
        }
        if(status==3&&entity.getSendStatus()!=0){
            return ResultVo.Fail("只有待发布的通知才能取消，请确认");
        }
        if(status==1&&entity.getSendStatus()!=0){
            return ResultVo.Fail("只有待发布的通知才能立即上线，请确认");
        }
        if(status==4&&entity.getSendStatus()!=1){
            return ResultVo.Fail("只有待发布的通知才能撤销，请确认");
        }
        if(entity.getNoticeType()==3){
            if(status==1){
                entity.setSendTime(new Date());
            }
            if(status==2){
                entity.setOutTime(new Date());
            }
        }
        entity.setSendStatus(status);
        manageService.editByIdSelective(entity);
        //修改对应用户得消息
        if(status==2&&entity.getNoticeType()!=3){
            messageNoticeService.updateStatusByNoticeId(id,status);
        }

        return ResultVo.Success();
    }

    /**
     * 导入用户校验
     */
    private void checkoutUser(String memberPhone, String oemCode, StringBuilder sb) throws BusinessException{
        if (StringUtil.isEmpty(oemCode)) {
            throw new BusinessException("机构编码为空");
        }

        if (StringUtil.isEmpty(memberPhone)) {
            throw new BusinessException("手机号为空");
        }
        // 查询用户
        MemberAccountEntity member = memberAccountService.queryByAccount(memberPhone, oemCode);
        if (null == member) {
            throw new BusinessException("用户不存在");
        }
        // 是否重复添加
        if (sb.indexOf(memberPhone) > -1) {
            throw new BusinessException("重复添加");
        }
        sb.append(",").append(memberPhone);

    }

    /**
     * 下载用户列表
     */
    @PostMapping("/downMemberList")
    public ResultVo downMemberList(@JsonParam Long id) {
        if (null == id) {
            return ResultVo.Fail("通知管理id不能为空");
        }
        // 查询通知
        NoticeManageEntity manageEntity = manageService.findById(id);
        if (null == manageEntity) {
            return ResultVo.Fail("通知不存在");
        }
        if (StringUtil.isEmpty(manageEntity.getUserListUrl())) {
            return ResultVo.Fail("用户文件不存在");
        }

        DictionaryEntity dicEntity = dictionaryService.getByCode("file_download_path");
        if (dicEntity == null) {
            return ResultVo.Fail("字典数据未配置");
        }
        File bag = new File(dicEntity.getDictValue() + "/" + getCurrUseraccount());
        if(!bag.exists()){
            bag.mkdirs();//如果路径不存在就先创建路径
        }
        String fileName = getCurrUseraccount() + System.currentTimeMillis() + "." + manageEntity.getUserListUrl().split("\\.")[1];
        BufferedOutputStream bos = null;
        try {
            File downLoadFile = new File(bag, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(downLoadFile));
            Workbook workbook = manageService.getUserFile(manageEntity.getUserListUrl());
            HttpServletResponse response = getResponse();
            response.setContentType("application/vnd.ms-excel");
            String codedFileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + codedFileName);
            OutputStream output = response.getOutputStream();
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(output);
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
            output.close();
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("下载用户列表，保存服务器异常：" + e.getMessage(), e);
            return ResultVo.Fail("下载用户列表，保存失败");
        }finally {
            IOUtils.closeQuietly(bos);
        }

        return ResultVo.Success();
    }
}
