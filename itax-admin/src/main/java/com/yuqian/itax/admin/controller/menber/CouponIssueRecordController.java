package com.yuqian.itax.admin.controller.menber;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.coupons.entity.CouponsEntity;
import com.yuqian.itax.coupons.entity.CouponsIssueRecordEntity;
import com.yuqian.itax.coupons.entity.enums.CouponsIssueRecordStatusEnum;
import com.yuqian.itax.coupons.entity.po.CouponPO;
import com.yuqian.itax.coupons.entity.query.CouponQuery;
import com.yuqian.itax.coupons.entity.query.CouponsIssueRecordQueryAdmin;
import com.yuqian.itax.coupons.entity.vo.CouponVO;
import com.yuqian.itax.coupons.entity.vo.CouponsBatchIssueVO;
import com.yuqian.itax.coupons.entity.vo.CouponsIssueVOAdmin;
import com.yuqian.itax.coupons.service.CouponsIssueRecordService;
import com.yuqian.itax.coupons.service.CouponsService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.util.Lists;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
@RequestMapping("/couponIssueRecord")
@Slf4j
public class CouponIssueRecordController extends BaseController {

    @Resource
    CouponsIssueRecordService couponsIssueRecordService;

    /**
     * 优惠券发放记录列表（分页）
     */
    @PostMapping("/couponIssueRecordPageInfo")
    public ResultVo couponIssueRecordPageInfo(@RequestBody CouponsIssueRecordQueryAdmin query){
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
        PageInfo<CouponsIssueVOAdmin> couponsIssueVOAdminPageInfo=couponsIssueRecordService.queryCouponIssueRecordPageInfo(query);
        return ResultVo.Success(couponsIssueVOAdminPageInfo);
    }

    /**
     * 优惠券发放记录导出
     */
    @PostMapping("/export")
    public ResultVo export(@RequestBody CouponsIssueRecordQueryAdmin query){
        //验证登陆
        getCurrUser();
        //获取账号信息
        UserEntity userEntity=userService.getOrgAdminAccount(getCurrUserId());
        if(userEntity.getPlatformType()!=1&&userEntity.getPlatformType()!=3){
            return ResultVo.Fail("不是平台和园区用户不能下载优惠券发放记录");
        }
        List<CouponsIssueVOAdmin> lists=couponsIssueRecordService.queryCouponIssueRecordList(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("优惠券发放记录", "优惠券发放记录", CouponsIssueVOAdmin.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("优惠券发放记录导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }

    /**
     * 优惠券发放记录撤回
     */
    @PostMapping("/revocation")
    public ResultVo revocation(@RequestBody CouponsIssueRecordQueryAdmin query){
        //验证登陆
        getCurrUser();
        //获取账号信息
        UserEntity userEntity=userService.getOrgAdminAccount(getCurrUserId());
        if(userEntity.getPlatformType()!=1&&userEntity.getPlatformType()!=3){
            return ResultVo.Fail("不是平台和园区用户不能操作优惠券发放记录");
        }
        CouponsIssueRecordEntity couponsIssueRecordEntity=couponsIssueRecordService.findById(query.getId());
        if(couponsIssueRecordEntity==null){
            return ResultVo.Fail("优惠券发放记录不存在");
        }
        if (couponsIssueRecordEntity.getStatus()!=0){
            return ResultVo.Fail("优惠券以使用或者已失效不能撤回");
        }
        couponsIssueRecordEntity.setStatus(CouponsIssueRecordStatusEnum.WITHDRAWN.getValue());
        couponsIssueRecordEntity.setUpdateTime(new Date());
        couponsIssueRecordEntity.setUpdateUser(getCurrUseraccount());
        couponsIssueRecordEntity.setRemark("优惠券撤回");
        couponsIssueRecordService.editByIdSelective(couponsIssueRecordEntity);
        return ResultVo.Success("");
    }


}
