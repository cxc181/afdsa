package com.yuqian.itax.admin.controller.menber;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.coupons.entity.CouponExchangeCodeEntity;
import com.yuqian.itax.coupons.entity.CouponsEntity;
import com.yuqian.itax.coupons.entity.CouponsIssueRecordEntity;
import com.yuqian.itax.coupons.entity.enums.CouponExchangeCodeStatusEnum;
import com.yuqian.itax.coupons.entity.enums.CouponsStatusEnum;
import com.yuqian.itax.coupons.entity.po.CouponExchangeCodePO;
import com.yuqian.itax.coupons.entity.po.CouponPO;
import com.yuqian.itax.coupons.entity.query.CouponExchangeCodeQuery;
import com.yuqian.itax.coupons.entity.query.CouponQuery;
import com.yuqian.itax.coupons.entity.vo.CouponExchangeCodeVO;
import com.yuqian.itax.coupons.entity.vo.CouponVO;
import com.yuqian.itax.coupons.entity.vo.CouponsBatchIssueVO;
import com.yuqian.itax.coupons.service.CouponExchangeCodeService;
import com.yuqian.itax.coupons.service.CouponsIssueRecordService;
import com.yuqian.itax.coupons.service.CouponsService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.util.Lists;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券
 * auth: HZ
 * time: 2021/4/12
 */
@RestController
@RequestMapping("/coupons")
@Slf4j
public class CouponsController extends BaseController {

    @Resource
    CouponsService couponsService;
    @Resource
    DictionaryService dictionaryService;
    @Resource
    CouponExchangeCodeService couponExchangeCodeService;
    @Autowired
    CouponsIssueRecordService couponsIssueRecordService;

    /**
     * 优惠券列表（分页）
     */
    @PostMapping("/couponsPageInfo")
    public ResultVo couponsPageInfo(@RequestBody CouponQuery couponQuery){
        //验证登陆
        getCurrUser();
        //获取账号信息
        UserEntity userEntity=userService.getOrgAdminAccount(getCurrUserId());
        if(userEntity.getPlatformType()==3){
            return ResultVo.Fail("园区用户不能查看优惠券管理");
        }
        if(StringUtils.isNotBlank(getRequestHeadParams("oemCode"))){
            couponQuery.setOemCode(getRequestHeadParams("oemCode"));
        }
        //分页查询
        PageInfo<CouponVO> couponsEntityPageInfo=couponsService.queryCouponPageInfo(couponQuery);
        return ResultVo.Success(couponsEntityPageInfo);
    }

    /**
     * 优惠券导出
     */
    @PostMapping("/export")
    public ResultVo couponsExport(@RequestBody CouponQuery query){
        //验证登陆
        getCurrUser();
        //获取账号信息
        UserEntity userEntity=userService.getOrgAdminAccount(getCurrUserId());
        if(userEntity.getPlatformType()==3){
            return ResultVo.Fail("园区用户不能查看优惠券管理");
        }
        if(StringUtils.isNotBlank(getRequestHeadParams("oemCode"))){
            query.setOemCode(getRequestHeadParams("oemCode"));
        }
        //分页查询
        List<CouponVO> lists=couponsService.queryCouponList(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("优惠券", "优惠券", CouponVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("优惠券导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }

    /**
     * 优惠券新增
     */
    @PostMapping("/add")
    public ResultVo add(@RequestBody @Valid  CouponPO po, BindingResult results){
        //验证登陆
        getCurrUser();
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        //获取账号信息
        try {
            couponsService.add(po,getCurrUseraccount());
            return ResultVo.Success();
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 优惠券管理详情
     */
    @PostMapping("/detail")
    public ResultVo detail(@JsonParam Long id){
        //验证登陆
        getCurrUser();
        CouponQuery query=new CouponQuery();
        query.setId(id);
        List<CouponVO>  list=couponsService.queryCouponList(query);
        if(CollectionUtil.isEmpty(list)){
            return ResultVo.Fail("优惠券不存在");
        }
        Integer count = 0;
        //若该券存在发放记录，则不能再编辑
        CouponsIssueRecordEntity couponsIssueRecordEntity= new CouponsIssueRecordEntity();
        couponsIssueRecordEntity.setCouponsId(id);
        List<CouponsIssueRecordEntity> couponsIssueRecordList =couponsIssueRecordService.select(couponsIssueRecordEntity);
        if(CollectionUtil.isNotEmpty(couponsIssueRecordList)){
            count = 1;
        }
        //若存在兑换码绑定了该优惠券，则不能再编辑
        CouponExchangeCodeEntity couponExchangeCodeEntity= new CouponExchangeCodeEntity();
        couponExchangeCodeEntity.setCouponsId(id);
        List<CouponExchangeCodeEntity> couponExchangeCodeEntities =couponExchangeCodeService.select(couponExchangeCodeEntity);
        if(CollectionUtil.isNotEmpty(couponExchangeCodeEntities)){
            count = 1;
        }
        CouponVO vo = list.get(0) ;
        vo.setIsUse(count);
        return ResultVo.Success(vo);
    }

    /**
     * 优惠券编辑
     */
    @PostMapping("/update")
    public ResultVo update(@RequestBody CouponPO po){
        //验证登陆
        getCurrUser();
        //获取账号信息
        try {
            couponsService.update(po,getCurrUseraccount());
            return ResultVo.Success();
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 优惠券作废
     */
    @PostMapping("/status/update")
    public ResultVo statusUpdate(@RequestBody CouponPO po){
        //验证登陆
        getCurrUser();
        if(po.getId()==null){
            return ResultVo.Fail("参数错误");
        }
        CouponsEntity couponsEntity=couponsService.findById(po.getId());
        if(couponsEntity==null){
            return ResultVo.Fail("优惠券不存在");
        }
        List<CouponExchangeCodeEntity> list=couponExchangeCodeService.queryCouponExchangeCodeByCouponsId(po.getId());
        if(CollectionUtil.isNotEmpty(list)){
            return ResultVo.Fail("已绑定兑换码的优惠券需先作废兑换码，才能作废优惠券");
        }
        couponsEntity.setStatus(CouponsStatusEnum.OBSOLETE.getValue());
        couponsEntity.setUpdateTime(new Date());
        couponsEntity.setUpdateUser(getCurrUseraccount());
        couponsService.editByIdSelective(couponsEntity);
        return ResultVo.Success();
    }
    /**
     * 批量发放
     */
    @PostMapping("/batch/issue")
    public ResultVo batchIssue(@RequestParam("file") MultipartFile file){
        //验证登陆
        getCurrUser();
        List<CouponsBatchIssueVO> list;
        List<CouponsBatchIssueVO> failed = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            list = ExcelImportUtil.importExcel(file.getInputStream(), CouponsBatchIssueVO.class, params);
        } catch (Exception e) {
            log.error("批量导入异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("文件内容为空");
        }
        //批量发放
        couponsService.batchIssue(list, failed,getCurrUseraccount());
        Map<String ,Object> map =new HashMap<>();
        map.put("success",list.size()-failed.size());
        map.put("failed",failed.size());
        //如果无失败则不生成下载文件
        if (CollectionUtil.isEmpty(failed)){
            map.put("downLoadUrl", "");
            return ResultVo.Success(map);
        }
        DictionaryEntity dicEntity = dictionaryService.getByCode("file_download_path");
        if (dicEntity == null) {
            return ResultVo.Fail("字典数据未配置");
        }
        File bag = new File(dicEntity.getDictValue() + "/" + getCurrUseraccount());
        if(!bag.exists()){
            bag.mkdirs();//如果路径不存在就先创建路径
        }
        String fileName = "coupons_issue_record_" + System.currentTimeMillis() + ".xls";
        BufferedOutputStream bos = null;
        try {
            File downLoadFile = new File(bag, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(downLoadFile));
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), CouponsBatchIssueVO.class, failed);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("批量发放，失败记录保存服务器异常：" + e.getMessage(), e);
            return ResultVo.Fail("批量发放，失败记录保存失败");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        map.put("downLoadUrl", fileName);
        return ResultVo.Success(map);
    }


    /**
     * 更具优惠券编码查询
     */
    @PostMapping("/queryCouponsByCode")
    public ResultVo queryCouponsByCode(@RequestBody CouponQuery couponQuery){
        //验证登陆
        getCurrUser();
        //获取账号信息
        UserEntity userEntity=userService.getOrgAdminAccount(getCurrUserId());
        if(userEntity.getPlatformType()==3){
            return ResultVo.Fail("园区用户不能查看优惠券管理");
        }
        //分页查询
       CouponVO couponVO=couponsService.queryCouponsByCode(couponQuery);
        if(couponVO==null){
            return ResultVo.Fail("优惠券不存在");
        }
        return ResultVo.Success(couponVO);
    }


    /**
     * 优惠券兑换码列表（分页）
     */
    @PostMapping("/couponExchangeCodePageInfo")
    public ResultVo couponExchangeCodePageInfo(@RequestBody CouponExchangeCodeQuery query){
        //验证登陆
        getCurrUser();
        //获取账号信息
        UserEntity userEntity=userService.getOrgAdminAccount(getCurrUserId());
        if(userEntity.getPlatformType()==3){
            return ResultVo.Fail("园区用户不能查看优惠券管理");
        }
        if(StringUtils.isNotBlank(getRequestHeadParams("oemCode"))){
            query.setOemCode(getRequestHeadParams("oemCode"));
        }
        //分页查询
        PageInfo<CouponExchangeCodeVO> pageInfo=couponExchangeCodeService.queryCouponExchangeCodePageInfo(query);
        return ResultVo.Success(pageInfo);
    }
    /**
     * 优惠券兑换码导出
     */
    @PostMapping("/couponExchangeCode/excel")
    public ResultVo couponExchangeCodeExcel(@RequestBody CouponExchangeCodeQuery query){
        //验证登陆
        getCurrUser();
        //获取账号信息
        UserEntity userEntity=userService.getOrgAdminAccount(getCurrUserId());
        if(StringUtils.isNotBlank(getRequestHeadParams("oemCode"))){
            query.setOemCode(getRequestHeadParams("oemCode"));
        }
        //分页查询
        List<CouponExchangeCodeVO> lists=couponExchangeCodeService.queryCouponExchangeCodeList(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("优惠券", "优惠券", CouponExchangeCodeVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("优惠券导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }
    /**
     * 优惠券兑换码新增
     */
    @PostMapping("/couponExchangeCode/add")
    public ResultVo couponExchangeCodeAdd(@RequestBody @Validated(Add.class) CouponExchangeCodePO po, BindingResult results){
        //验证登陆
        getCurrUser();
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        try {
            couponExchangeCodeService.add(po,getCurrUseraccount());
            return ResultVo.Success();
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
    }
    /**
     * 优惠券兑换码详情
     */
    @PostMapping("/couponExchangeCode/detail")
    public ResultVo querycouponExchangeCodeDeatil(@RequestBody  CouponExchangeCodeQuery query){
        //验证登陆
        getCurrUser();
        if(query.getId()==null){
            return ResultVo.Fail("请输入兑换码id");
        }
        try {
            List<CouponExchangeCodeVO> list=couponExchangeCodeService.queryCouponExchangeCodeList(query);
            if(CollectionUtil.isEmpty(list)){
                return ResultVo.Fail("优惠券d兑换码不存在");
            }
            return ResultVo.Success(list.get(0));
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
    }
    /**
     * 优惠券兑换码编辑
     */
    @PostMapping("/couponExchangeCode/update")
    public ResultVo couponExchangeCodeUpdate(@RequestBody   @Validated(Update.class)  CouponExchangeCodePO po, BindingResult results){
        //验证登陆
        getCurrUser();
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        try {
            couponExchangeCodeService.update(po,getCurrUseraccount());
            return ResultVo.Success();
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
    }
    /**
     * 优惠券兑换码作废/暂停、启用
     */
    @PostMapping("couponExchangeCode/status/update")
    public ResultVo couponExchangeCodeStatusUpdate(@RequestBody CouponExchangeCodePO po) throws ParseException {
        //验证登陆
        getCurrUser();
        if(po.getId()==null ||po.getStatus()==null){
            return ResultVo.Fail("参数错误");
        }
        CouponExchangeCodeEntity couponExchangeCodeEntity=couponExchangeCodeService.findById(po.getId());
        if(couponExchangeCodeEntity==null){
            return ResultVo.Fail("优惠券兑换码不存在");
        }
        if(couponExchangeCodeEntity.getStatus().equals(CouponExchangeCodeStatusEnum.STALE.getValue())){
            return ResultVo.Fail("优惠券兑换码已过期");
        }
        if(po.getStatus().equals(CouponExchangeCodeStatusEnum.EFFICIENT.getValue())||po.getStatus().equals(CouponExchangeCodeStatusEnum.PAUSED.getValue())){
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");

            if(couponExchangeCodeEntity.getEndDate().compareTo( sdf.parse(sdf.format(new Date())))<0){
                couponExchangeCodeEntity.setStatus(CouponExchangeCodeStatusEnum.STALE.getValue());
                couponExchangeCodeEntity.setUpdateUser(getCurrUseraccount());
                couponExchangeCodeEntity.setUpdateTime(new Date());
                couponExchangeCodeEntity.setRemark("兑换码已过期修改状态");
                couponExchangeCodeService.editByIdSelective(couponExchangeCodeEntity);
                return ResultVo.Fail("优惠券兑换码已过期");
            }
        }

        couponExchangeCodeEntity.setStatus(po.getStatus());
        couponExchangeCodeEntity.setUpdateTime(new Date());
        couponExchangeCodeEntity.setUpdateUser(getCurrUseraccount());
        couponExchangeCodeService.editByIdSelective(couponExchangeCodeEntity);
        return ResultVo.Success();
    }
}
