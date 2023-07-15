package com.yuqian.itax.admin.controller.agent;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.park.entity.query.ParkQuery;
import com.yuqian.itax.park.entity.vo.ParkListVO;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.query.ProductDiscountActivityQuery;
import com.yuqian.itax.product.entity.vo.*;
import com.yuqian.itax.product.service.ProductDiscountActivityService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.system.entity.IndustryEntity;
import com.yuqian.itax.system.entity.vo.IndustryAndParkInfoVo;
import com.yuqian.itax.system.service.IndustryService;
import com.yuqian.itax.user.entity.CrowdLabelEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.CrowdLabelService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/productDiscountActivity")
@Slf4j
public class ProductDiscountActivityController extends BaseController {

    @Autowired
    private ProductDiscountActivityService productDiscountActivityService;

    @Autowired
    private CrowdLabelService crowdLabelService;

    @Autowired
    private ParkService parkService;

    @Autowired
    private IndustryService industryService;

    @Autowired
    private ProductService productService;

    @ApiOperation("产品特价活动页")
    @PostMapping("page")
    public ResultVo listPageProductDiscountActivity(@RequestBody ProductDiscountActivityQuery query){
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
        PageInfo<ProductDiscountActivityListVO>  page = productDiscountActivityService.listPageProductDiscountActivity(query);
        return  ResultVo.Success(page);
    }

    @ApiOperation("导出产品特价活动列表")
    @PostMapping("batch/productDiscountActivity")
    public ResultVo batchProductDiscountActivity(@RequestBody ProductDiscountActivityQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() == 2){
            query.setOemCode(userEntity.getOemCode());
        }
        List<ProductDiscountActivityListVO> list = productDiscountActivityService.listProductDiscountActivity(query);
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("产品特价活动列表"+ DateUtil.format(new Date(),"yyyy-MM-dd"), "产品特价活动列表"+ DateUtil.format(new Date(),"yyyy-MM-dd"), ProductDiscountActivityListVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("产品特价活动列表：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    @ApiOperation("根据oemCode获取人群标签和园区")
    @PostMapping("getCrowdAndParkInfo")
    public ResultVo getCrowdLabelAndParkInfo(@JsonParam String oemCode){
        CurrUser currUser = getCurrUser();
        Map<String,Object> map = new HashMap<String,Object>();
        if (StringUtil.isEmpty(oemCode)) {
            return ResultVo.Fail("oem机构不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() == 2){
            if (!oemCode.equals(userEntity.getOemCode())){
                return ResultVo.Fail("oem机构错误");
            }
        }
        //   获取人群标签
        List<CrowdLabelEntity> crowdLabellist = crowdLabelService.queryCrowdLabelByOemCode(oemCode,1);
        //  获取园区
        ParkQuery query=new ParkQuery();
        query.setOemCode(oemCode);
        List<ParkListVO> parkListVO=parkService.getOemParkList(query);
        List<ProductEntity> prodList =  productService.queryProductListByOemCodeAndType(oemCode);
        map.put("crowdLabellist",crowdLabellist);
        map.put("parkListVO",parkListVO);
        map.put("prodList",prodList);
        return ResultVo.Success(map);
    }

    @ApiOperation("根据标签名称模糊查询标签")
    @PostMapping("getCrowdLabelByCrowdName")
    public ResultVo getCrowdLabelByCrowdName(@JsonParam String labelName,@JsonParam String oemCode){
        CurrUser currUser = getCurrUser();
        List<CrowdLabelEntity> list = crowdLabelService.getCrowdLabelByLabelName(labelName,oemCode);
        return ResultVo.Success(list);
    }

    @ApiOperation("根据园区id获取行业信息")
    @PostMapping("getIndustryInfo")
    public ResultVo getIndustryByParkIds(@JsonParam  String parkIds){
        if (StringUtil.isBlank(parkIds)){
            return  ResultVo.Fail("园区不能为空");
        }
        String[] parkId = parkIds.split(",");
        List<String> parklist = new ArrayList<>();
        Collections.addAll(parklist, parkId);
        List<IndustryAndParkInfoVo> list = industryService.queryIndustryByParkIds(parklist);
        return ResultVo.Success(list);
    }

    @ApiOperation("新增活动")
    @PostMapping("addProductDiscountActivity")
    public ResultVo addProductDiscountActivity(@RequestBody ProductCrowdParkVO productCrowdParkVO){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() == 2){
            if (productCrowdParkVO.getOemCode() == null || !productCrowdParkVO.getOemCode().equals(userEntity.getOemCode())){
                return ResultVo.Fail("oem机构错误");
            }
        }
        if (productCrowdParkVO.getActivityName().trim() == null || productCrowdParkVO.getActivityName().trim().equals("")){
            return ResultVo.Fail("活动名称不能为空");
        }
        if (productCrowdParkVO.getActivityName().trim().length()>20){
            return ResultVo.Fail("活动名称不能超过20个字符");
        }
        if (!productCrowdParkVO.getActivityStartDate().equals(productCrowdParkVO.getActivityEndDate())){
            if (!productCrowdParkVO.getActivityStartDate().before(productCrowdParkVO.getActivityEndDate())){
                return ResultVo.Fail("结束时间不能早于开始时间");
            }
        }
        if (productCrowdParkVO.getCrowdLabelIds() == null || productCrowdParkVO.getCrowdLabelIds().size() == 0){
            return ResultVo.Fail("人群标签不能为空");
        }
        if (productCrowdParkVO.getParkIds() == null || productCrowdParkVO.getParkIds().size() == 0){
            return ResultVo.Fail("园区不能为空");
        }
        //   获取人群标签
        List<CrowdLabelEntity> crowdLabelList = crowdLabelService.queryCrowdLabelByOemCode(productCrowdParkVO.getOemCode(),1);
        for (Long id:productCrowdParkVO.getCrowdLabelIds()){
            boolean flag = false;
            for(CrowdLabelEntity entity:crowdLabelList){
                if (Objects.equals(id, entity.getId())) {
                    flag = true;
                    break;
                }
            }
            if(!flag){
                return ResultVo.Fail("人群标签不属于选择的oem机构");
            }
        }
        // 添加活动
        productDiscountActivityService.addProductDiscountActivity(productCrowdParkVO,userEntity.getUsername());
        return ResultVo.Success();
    }

    @ApiOperation("详情")
    @PostMapping("activityDetail")
    public ResultVo activityDetail(@JsonParam Long discountActivityId){
        if (discountActivityId == null ){
            return  ResultVo.Fail("活动id不能为空");
        }
        ProductActivityDetailVO vo = productDiscountActivityService.getDetail(discountActivityId);
        return ResultVo.Success(vo);
    }


    @ApiOperation("状态修改")
    @PostMapping("update/status")
    public ResultVo updateStatus(@JsonParam Long id , @JsonParam Integer status) {
        //登录验证
        getCurrUser();
        try {
            productDiscountActivityService.updateStatusById(id,status,getCurrUseraccount());
        }catch ( Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

}
