package com.yuqian.itax.api.controller.system;

import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.system.entity.vo.DistrictVO;
import com.yuqian.itax.system.entity.vo.SysCityVO;
import com.yuqian.itax.system.entity.vo.SysProvinceVO;
import com.yuqian.itax.system.service.CityService;
import com.yuqian.itax.system.service.DistrictService;
import com.yuqian.itax.system.service.ProvinceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 17:09
 *  @Description: 省市区域信息控制器
 */
@Api(tags = "省市区域信息控制器")
@RestController
@Slf4j
@RequestMapping("/system/region")
public class SysRegionController extends BaseController {
    @Autowired
    private ProvinceService sysProvinceService;
    @Autowired
    private CityService sysCityService;
    @Autowired
    private DistrictService districtService;

    /**
     * @Description 获取省信息
     * @Author  Kaven
     * @Date   2019/12/9 17:15
     * @Return ResultVo
    */
    @ApiOperation(value = "获取省信息")
    @PostMapping("listProvince")
    public ResultVo<List<SysProvinceVO>> listProvince(){
        List<SysProvinceVO> list = this.sysProvinceService.getProvinceList();
        return ResultVo.Success(list);
    }

    /**
     * @Description 根据省级编码获取市级信息
     * @Author  Kaven
     * @Date   2019/12/9 17:16
     * @Param  provinceCode
     * @Return ResultVo
    */
    @ApiOperation(value = "根据省级编码获取市级信息")
    @ApiImplicitParam(name="provinceCode",value="省编号",dataType="String",required = true)
    @PostMapping("listCity")
    public ResultVo<List<SysCityVO>> listCity(@JsonParam String provinceCode){
        if(StringUtils.isEmpty(provinceCode)){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        List<SysCityVO> list = this.sysCityService.getCityList(provinceCode);
        return ResultVo.Success(list);
    }

    /**
     * @Description 获取区信息
     * @Author  yejian
     * @Date   2019/12/23 9:50
     * @Return ResultVo
     */
    @ApiOperation(value = "根据市级编码获取区信息")
    @ApiImplicitParam(name="cityCode",value="市编码",dataType="String",required = true)
    @PostMapping("listDistrict")
    public ResultVo<List<DistrictVO>> listProvince(@JsonParam String cityCode){
        if(StringUtils.isEmpty(cityCode)){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        List<DistrictVO> list = districtService.getDistrictList(cityCode);
        return ResultVo.Success(list);
    }
}