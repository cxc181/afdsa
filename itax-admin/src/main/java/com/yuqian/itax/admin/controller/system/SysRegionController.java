package com.yuqian.itax.admin.controller.system;

import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.vo.DistrictVO;
import com.yuqian.itax.system.entity.vo.SysCityVO;
import com.yuqian.itax.system.entity.vo.SysProvinceVO;
import com.yuqian.itax.system.service.CityService;
import com.yuqian.itax.system.service.DistrictService;
import com.yuqian.itax.system.service.ProvinceService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
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
    @PostMapping("/listProvince")
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
    @PostMapping("/listCity")
    public ResultVo<List<SysCityVO>> listCity(@JsonParam String provinceCode){
        List<SysCityVO> list = this.sysCityService.getCityList(provinceCode);
        return ResultVo.Success(list);
    }

    /**
     * @Description 根据市级编码获取区信息
     * @Author  Kaven
     * @Date   2019/12/9 17:16
     * @Param  provinceCode
     * @Return ResultVo
     */
    @PostMapping("/listDistrict")
    public ResultVo listDistrict(@JsonParam String cityCode){
        List<DistrictVO> list = this.districtService.getDistrictList(cityCode);
        return ResultVo.Success(list);
    }
}