package com.yuqian.itax.gateway.controller;

import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.gateway.annotation.JsonParam;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.park.entity.vo.Park4OutVO;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.dto.BizScopeDTO;
import com.yuqian.itax.system.entity.vo.DistrictVO;
import com.yuqian.itax.system.entity.vo.ScopeCategoryVO;
import com.yuqian.itax.system.entity.vo.SysCityVO;
import com.yuqian.itax.system.entity.vo.SysProvinceVO;
import com.yuqian.itax.system.service.CityService;
import com.yuqian.itax.system.service.DistrictService;
import com.yuqian.itax.system.service.IndustryService;
import com.yuqian.itax.system.service.ProvinceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Description 公共数据（行业、园区等）相关控制器
 * @Author  Kaven
 * @Date   2020/7/15 14:50
*/
@Api(tags = "公共数据服务API")
@RestController
@Slf4j
@RequestMapping("/system")
public class SystemController extends BaseController {
    @Autowired
    private ProvinceService sysProvinceService;
    @Autowired
    private CityService sysCityService;
    @Autowired
    private DistrictService districtService;
    @Autowired
    private IndustryService industryService;
    @Autowired
    private ParkService parkService;

    /**
     * @Description 获取省信息
     * @Author  Kaven
     * @Date   2020/7/15 14:50
     * @Param
     * @Return
     * @Exception
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
     * @Date   2020/7/15 14:50
     * @Param
     * @Return
     * @Exception
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
     * @Description 根据市级编码获取区信息
     * @Author  Kaven
     * @Date   2020/7/15 14:51
     * @Param
     * @Return
     * @Exception
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

    /**
     * @Description 根据行业id获取开票类目和经营范围
     * @Author  Kaven
     * @Date   2020/7/15 16:52
     * @Param   industryId
     * @Return  ResultVo<IndustryInfoVO>
     * @Exception
     */
    @ApiOperation(value = "根据行业id获取开票类目和经营范围")
    @PostMapping("/getBizScopeAndInvCategory")
    public ResultVo<ScopeCategoryVO> getBizScopeAndInvCategory(@RequestBody @Valid BizScopeDTO dto, BindingResult result){
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        if(null == dto){
            return ResultVo.Fail("操作失败，参数不能为空！");
        }
        ScopeCategoryVO scv = this.industryService.getBizScopeAndInvCategory(dto);
        return ResultVo.Success(scv);
    }

    /**
     * @Description 园区列表查询
     * @Author  Kaven
     * @Date   2020/7/16 09:18
     * @Param   companyType 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
     * @Return
     * @Exception
     */
    @ApiOperation(value = "园区列表查询")
    @PostMapping("listParks")
    public ResultVo<List<Park4OutVO>> listParks(@JsonParam Integer companyType) {
        if (null == companyType) {
            companyType = 1;// 默认为1
        }
        if (companyType <= 0 || companyType > 4) {
            return ResultVo.Fail(ErrorCodeEnum.COMPANY_TYPE_ERROR.getText());
        }
        List<Park4OutVO> list = this.parkService.listParks(this.getRequestHeadParams("oemCode"), companyType);
        return ResultVo.Success(list);
    }

    /**
     * @Description 根据编码获取用户字典
     * @Author Kaven
     * @Date 2019/12/10 18:52
     * @Param dictCode
     * @Return ResultVo
     */
    @ApiOperation("根据编码获取用户字典")
    @ApiImplicitParam(name = "dictCode", value = "编码", dataType = "String", required = true)
    @PostMapping("getDictByCode")
    public ResultVo<DictionaryEntity> getDictByCode(@JsonParam String dictCode) {
        if (StringUtils.isEmpty(dictCode)) {
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        DictionaryEntity entity = sysDictionaryService.getByCode(dictCode);
        return ResultVo.Success(entity);
    }
}