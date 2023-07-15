package com.yuqian.itax.admin.controller.park;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.service.OemParkRelaService;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import com.yuqian.itax.agreement.service.ParkAgreementTemplateRelaService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.enums.OperTypeEnum;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.park.entity.*;
import com.yuqian.itax.park.entity.dto.IndustryPolicyDTO;
import com.yuqian.itax.park.entity.po.ParkEndtimeConfigPO;
import com.yuqian.itax.park.entity.po.TaxPolicyPO;
import com.yuqian.itax.park.entity.query.IndustryPolicyQuery;
import com.yuqian.itax.park.entity.query.ParkDisableWordQuery;
import com.yuqian.itax.park.entity.query.ParkQuery;
import com.yuqian.itax.park.entity.query.TaxPolicyChangeQuery;
import com.yuqian.itax.park.entity.vo.*;
import com.yuqian.itax.park.enums.IncomeLevyTypeEnum;
import com.yuqian.itax.park.enums.LevyWayEnum;
import com.yuqian.itax.park.service.*;
import com.yuqian.itax.system.entity.*;
import com.yuqian.itax.system.enums.IndustryStatusEnum;
import com.yuqian.itax.system.service.*;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.UserExtendEntity;
import com.yuqian.itax.user.service.UserExtendService;
import com.yuqian.itax.util.util.StringUtil;
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
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 园区Controll
 * auth: HZ
 * time: 2019/12/17
 */
@RestController
@RequestMapping("/park")
@Slf4j
public class ParkController extends BaseController {
    @Autowired
    ParkService parkService;
    @Autowired
    TaxPolicyService taxPolicyService;
    @Autowired
    TaxRulesConfigService taxRulesConfigService;
    @Autowired
    UserCapitalAccountService userCapitalAccountService;
    @Autowired
    OemParkRelaService oemParkRelaService;
    @Autowired
    UserExtendService userExtendService;
    @Autowired
    ProvinceService provinceService;
    @Autowired
    CityService cityService;
    @Autowired
    DistrictService districtService;
    @Autowired
    private IndustryService industryService;
    @Autowired
    private OssService ossService;
    @Resource
    ParkEndtimeConfigService parkEndtimeConfigService;
    @Autowired
    ParkBusinessAddressRulesService parkBusinessAddressRulesService;
    @Autowired
    RegisterOrderService registerOrderService;
    @Autowired
    ParkDisableWordService parkDisableWordService;
    @Autowired
    DictionaryService dictionaryService;
    @Autowired
    private ParkAgreementTemplateRelaService parkAgreementTemplateRelaService;
    @Autowired
    private TaxPolicyChangeService taxPolicyChangeService;
    /**
     * 园区列表
     * @author  hz
     * @date 2019/12/17
     */
    @PostMapping("/queryParkList")
    public ResultVo queryParkList(@RequestBody  ParkQuery parkQuery){
        //验证登陆
        getCurrUser();


        UserEntity userEntity=userService.findById(getCurrUserId());
        if(userEntity.getPlatformType()!=null&& (userEntity.getPlatformType()!=1 && userEntity.getPlatformType() !=3)){
            return ResultVo.Fail("不是平台或园区账号不允许查看园区列表");
        }
        //  园区账号只查看本园区数据
        if (userEntity.getPlatformType() == 3){
            parkQuery.setParkId(userEntity.getParkId());
        }
        //分页查询
        PageInfo<ParkListVO> list=parkService.queryOemPageInfo(parkQuery);
        return ResultVo.Success(list);
    }

    /**
     * 园区下拉列表
     * @author  hz
     * @date 2019/12/17
     */
    @PostMapping("/queryPark")
    public ResultVo queryPark(){
        //验证登陆
        getCurrUser();
        ParkQuery query=new ParkQuery();
        UserEntity userEntity=userService.findById(getCurrUserId());
        List<ParkListVO> list=null;
        //如果是平台账号
        if(null!=userEntity.getPlatformType()&&userEntity.getPlatformType()==1){
             list=parkService.queryParkList(null);
            return ResultVo.Success(list);
        }
        //如果是园区账号
        if(null!=userEntity.getPlatformType()&&userEntity.getPlatformType()==3){
            query.setParkId(userEntity.getParkId());
            list=parkService.queryParkList(query);
            return ResultVo.Success(list);
        }
        //剩下的全是机构账户
        query.setOemCode(userEntity.getOemCode());
        List<ParkListVO> listVO=parkService.getOemParkList(query);
        return ResultVo.Success(listVO);
    }

    /**
     * 园区状态修改
     * @author  hz
     * @date 2019/12/17
     */
    @PostMapping("/updateParkStatus")
    public ResultVo updateParkStatus(@RequestBody  ParkPO parkPO){
        if(null == parkPO.getParkId() || null == parkPO.getStatus()){
            return ResultVo.Fail("参数不正确！");
        }
        //验证登陆
        getCurrUser();
        try{
            if(parkPO.getStatus()==2){
                //修改所有改园区下得账号为注销状态
                UserEntity userEntity=new UserEntity();
                userEntity.setParkId(parkPO.getParkId());
                List<UserEntity> list= userService.select(userEntity);
                for (UserEntity entity:list) {
                    if(entity.getStatus()!=2){
                        return ResultVo.Fail("名下有未注销的账号，请注销以后再注销园区");
                    }
                }
            }else  if (parkPO.getStatus() == 1){ //  上架需校验经营地址是否配置
                ParkBusinessAddressVO parkBusinessAddressVO = parkBusinessAddressRulesService.queryByParkId(parkPO.getParkId());
                if (parkBusinessAddressVO == null){
                    return ResultVo.Fail("请先配置经营地址");
                }
                IndustryEntity industryEntity = new IndustryEntity();
                industryEntity.setStatus(IndustryStatusEnum.YES.getValue());
                industryEntity.setParkId(parkPO.getParkId());
                List<IndustryEntity> list = industryService.select(industryEntity);
                if (CollectionUtil.isEmpty(list)) {
                    return ResultVo.Fail("请先配置园区行业数据");
                }
                // 个体户查账征收需要配置成本确认时间
                TaxPolicyEntity taxPolicyEntity = taxPolicyService.queryTaxPolicyByParkId(parkPO.getParkId(),1,1);
                if (taxPolicyEntity!=null && IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(taxPolicyEntity.getIncomeLevyType())) {
                    ParkEndtimeConfigEntity parkEndtimeConfigEntity = parkEndtimeConfigService.queryByOperTypeAndParkIdAndYearAndQuarter(parkPO.getParkId(),3,null,null);
                    if (parkEndtimeConfigEntity == null){
                        return ResultVo.Fail("请先配置成本确认时间");
                    }
                }
                //校验其他政策是否全部已配置
                Example example = new Example(TaxPolicyEntity.class);
                example.createCriteria().andEqualTo("parkId",parkPO.getParkId()).andEqualTo("taxpayerType",1).andEqualTo("totalInvoiceAmount",0);
                List result = taxPolicyService.selectByExample(example);
                if(result!=null && result.size()>0){
                    return ResultVo.Fail("存在已勾选企业类型对应的近12个月可开票额度为0，请先进行政策配置");
                }
                example = new Example(TaxPolicyEntity.class);
                example.createCriteria().andEqualTo("parkId",parkPO.getParkId()).andEqualTo("taxpayerType",1).andGreaterThan("totalInvoiceAmount",0);
                result = taxPolicyService.selectByExample(example);
                //校验税费政策配置是否全部已配置
                List<Map<String,Object>> companyTypeList=taxPolicyService.queryTaxPolicyCompanyTypeByParkId(parkPO.getParkId());
                if(result==null || companyTypeList==null || (result!=null && companyTypeList!=null && companyTypeList.size() != result.size())){
                    return ResultVo.Fail("存在已勾选企业类型对应的园区政策未配置，请先进行政策配置");
                }
                TaxRulesConfigEntity rulesConfigEntity = null;
                List<TaxRulesConfigEntity> taxRulesList = null;
                Integer companType = 1;
                for(Map<String,Object> vo: companyTypeList){
                    companType = Integer.parseInt(vo.get("companyType").toString());
                    rulesConfigEntity = new TaxRulesConfigEntity();
                    rulesConfigEntity.setParkId(parkPO.getParkId());
                    rulesConfigEntity.setCompanyType(companType);
                    rulesConfigEntity.setTaxType(3);
                    taxRulesList = taxRulesConfigService.select(rulesConfigEntity);
                    if(companType==1 && (taxRulesList ==null || taxRulesList.size() < 1)){
                        return ResultVo.Fail("存在已勾选企业类型对应的园区政策未配置，请先进行政策配置");
                    }else if (companType!=1 && (taxRulesList == null || taxRulesList.size() != 2)){
                        return ResultVo.Fail("存在已勾选企业类型对应的园区政策未配置，请先进行政策配置");
                    }
                }
            }
            ParkEntity parkEntity=parkService.updateParkStatus(parkPO,getCurrUseraccount());

            //清除机构账号的token
            UserEntity userEntity=userService.getParkAccount(parkEntity.getId());
            String oemCode = userEntity.getOemCode();
            if(StringUtils.isBlank(oemCode)){
                oemCode = "";
            }
            String token =redisService.get(RedisKey.LOGIN_TOKEN_KEY+oemCode+"_" + "userId_2_" + userEntity.getUsername());
            if(!org.apache.commons.lang3.StringUtils.isBlank(token)){
                redisService.delete(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_"+ token);
            }
        }catch (BusinessException e){
            return  ResultVo.Fail(e.getMessage());
        }

        return ResultVo.Success();
    }

    /**
     * 园区政策产品类型
     */
    @PostMapping("/parkTaxRulesCompanyType")
    public ResultVo parkTaxRulesCompanyType(@JsonParam Long parkId){
        //验证登陆
        getCurrUser();
        List<Map<String,Object>> companyTypeList=taxPolicyService.queryTaxPolicyCompanyTypeByParkId(parkId);
        return ResultVo.Success(companyTypeList);
    }

    /**
     *  根据企业类型和园区id获取园区政策配置列表
     * @param parkId
     * @param companyType
     * @return
     */
    @PostMapping("/taxRulesList")
    public ResultVo taxRulesList(@JsonParam Long parkId,@JsonParam Integer companyType){
        //验证登陆
        getCurrUser();
        if(parkId==null){
            return ResultVo.Fail("园区id不能为空");
        }
        if(companyType==null){
           return ResultVo.Fail("企业类型不能为空");
        }
        //分页查询
        List<TaxPolicyVO> taxPolicyVO=taxPolicyService.queryTaxPolicyByParkIdAndCompanyType(parkId,companyType,null);
        return ResultVo.Success(taxPolicyVO);
    }

    /**
     * 园区政策配置编辑
     */
    @PostMapping("/updateTaxRules")
    public ResultVo updateTaxRules(@RequestBody @Valid TaxPolicyPO taxPolicyPO,BindingResult result){
        //验证登陆
        getCurrUser();
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        if(taxPolicyPO.getCompanyType() == 1) {
            if(taxPolicyPO.getLevyWay() == null || taxPolicyPO.getLevyWay() < 1 || taxPolicyPO.getLevyWay() > 2) {
                return ResultVo.Fail("计税方式有误");
            }
            if(taxPolicyPO.getVatBreaksCycle() != taxPolicyPO.getIncomeTaxBreaksCycle()) {
                return ResultVo.Fail("个体户的增值税申报周期必须与所得税申报周期一致");
            }
        }
        if(taxPolicyPO.getCompanyType() != 1){
            if(taxPolicyPO.getStampDutyBreaksCycle() == null || taxPolicyPO.getStampDutyBreaksCycle()<1 || taxPolicyPO.getStampDutyBreaksCycle() > 2){
                return ResultVo.Fail("印花税申报周期错误或为空");
            }
            if(taxPolicyPO.getWaterConservancyFundBreaksCycle() == null || taxPolicyPO.getWaterConservancyFundBreaksCycle()<1 || taxPolicyPO.getWaterConservancyFundBreaksCycle() > 2){
                return ResultVo.Fail("水利建设基金申报周期错误或为空");
            }
        }
        try {
            //编辑税率政策
            taxPolicyService.updateTaxRules(taxPolicyPO,getCurrUseraccount());
            return ResultVo.Success();
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 园区政策其他配置编辑
     */
    @PostMapping("/updateOtherTaxRules")
    public ResultVo updateOtherTaxRules(@RequestBody JSONObject jsonObject){
        //验证登陆
        CurrUser currUser = getCurrUser();
        if(jsonObject == null){
            return ResultVo.Fail("参数不能为空");
        }
        Long policyId = jsonObject.getLong("policyId");
        Long totalInvoiceAmount = jsonObject.getLong("totalInvoiceAmount");
        Long quarterInvoiceAmount = jsonObject.getLong("quarterInvoiceAmount");
        String parkPolicyDesc = jsonObject.getString("parkPolicyDesc");
        String specialConsiderations = jsonObject.getString("specialConsiderations");
        Long monthInvoiceAmount = jsonObject.getLong("monthInvoiceAmount");
        if(policyId==null){
            return ResultVo.Fail("园区政策id不能为空");
        }
        taxPolicyService.updateOtherTaxRules(policyId,totalInvoiceAmount,quarterInvoiceAmount,parkPolicyDesc,specialConsiderations,currUser.getUseraccount(),monthInvoiceAmount);
        return ResultVo.Success();
    }

    /**
     *  园区政策配置历史记录
     * @param
     * @return
     */
    @PostMapping("/getTaxPolicyChangeList")
    public ResultVo getTaxPolicyChangeList(@RequestBody TaxPolicyChangeQuery query){
        PageInfo<TaxPolicyChangeVO> page = taxPolicyChangeService.getTaxPolicyChangeList(query);
        return ResultVo.Success(page);
    }


    /**
     * 园区新增
     * @author HZ
     * date 2019/12/10
     */
    @PostMapping("/addPark")
    public ResultVo addPark(@RequestBody @Validated ParkPO parkPO,BindingResult result)  {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        //带登陆验证
        getCurrUser();

        try {
            if(!"1".equals(getCurrUser().getUsertype())){
                return ResultVo.Fail("只有平台账号允许新增园区。");
            }
            //获取省市区
            getCityInfo(parkPO);
            if(Objects.equals(parkPO.getDrawer(),parkPO.getReviewer())||Objects.equals(parkPO.getPayee(),parkPO.getReviewer())||Objects.equals(parkPO.getDrawer(),parkPO.getPayee())){
                return ResultVo.Fail("开票人、复核人、收款人必须不同！");
            }
            //新增账号
            userCapitalAccountService.addParkCapitalAccount(parkPO,getCurrUseraccount());
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 园区编辑
     * @author HZ
     * date 2019/12/10
     */
    @PostMapping("/updatePark")
    public ResultVo updatePark(@RequestBody @Validated  ParkPO parkPO, BindingResult result)  {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        //带登陆验证
        getCurrUser();
        try {
            if(!"1".equals(getCurrUser().getUsertype())){
                return ResultVo.Fail("只有平台账号允许修改园区。");
            }
            parkPO.setOemCode(getRequestHeadParams("oemCode"));
            if(Objects.equals(parkPO.getDrawer(),parkPO.getReviewer())||Objects.equals(parkPO.getPayee(),parkPO.getReviewer())||Objects.equals(parkPO.getDrawer(),parkPO.getPayee())){
                return ResultVo.Fail("开票人、复核人、收款人必须不同！");
            }
            //获取省市区
            getCityInfo(parkPO);
            //修改用户
            UserEntity userEntity=userService.updateParkPOById(parkPO,getCurrUseraccount());

        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 园区详情
     * @author HZ
     * date 2019/12/10
     */
    @PostMapping("/queryParkDetail")
    public ResultVo queryParkDetail(@JsonParam Long id)  {
        //带登陆验证
        getCurrUser();
        try {
            if(id == null){
                return ResultVo.Fail("参数不正确，id必须");
            }
            ParkDetailVO parkDetailVO=parkService.getParkDteatailById(id);
            //设置账号
            UserEntity userEntity=userService.getParkAccount(id);
            if(null == userEntity){
                return ResultVo.Fail("园区不存在！");
            }
            parkDetailVO.setUsername(userEntity.getUsername());
            UserExtendEntity userExtendEntity=userExtendService.getUserExtendByUserId(userEntity.getId());
            parkDetailVO.setPhone(userExtendEntity.getPhone());
//             List<AgreementTemplateInfoVO> list = parkAgreementTemplateRelaService.getTemplateInfo(id,null);
//            parkDetailVO.setAgreementTemplateInfoVOList(list);
            if (StringUtil.isNotBlank(parkDetailVO.getOfficialSealImg())){
                parkDetailVO.setHttpOfficialSealImg(ossService.getPrivateImgUrl(parkDetailVO.getOfficialSealImg()));
            }
            // 园区预览图
//            if(StringUtil.isNotBlank(parkDetailVO.getParkThumbnail())){
//                parkDetailVO.setHttpParkThumbnail(ossService.getPrivateImgUrl(parkDetailVO.getParkThumbnail()));
//            }
            // 园区详情顶部banner 图片,逗号分隔
//            if(StringUtil.isNotBlank(parkDetailVO.getParkImgs())){
//                List<String>  httpParkImgs = new ArrayList<>();
//                String parkImgs = parkDetailVO.getParkImgs();
//                String[] split = parkImgs.split(",");
//                for(String img:split){
//                    String privateImgUrl = ossService.getPrivateImgUrl(img);
//                    httpParkImgs.add(privateImgUrl);
//                }
//                parkDetailVO.setHttpParkImgs(httpParkImgs);
//            }
            return ResultVo.Success(parkDetailVO);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 获取园区专属协议
     * @param id
     * @param
     * @return
     */
    @PostMapping("/getAgreementInfoByParkId")
    public ResultVo getAgreementInfoByParkId(@JsonParam Long id,@JsonParam String oemCode){
        if (null == id){
            return ResultVo.Fail("参数不正确，id必须");
        }
        if (StringUtil.isEmpty(oemCode)){
            return ResultVo.Success();
        }
        AgreementTemplateInfoVO vo = oemParkRelaService.getAgreementTemplateByOemCodeAndParkId(oemCode,id);
        return ResultVo.Success(vo);
    }

    /**
     * 获取省市区
     * @param parkPO
     */
    public void getCityInfo(ParkPO parkPO) {
        ProvinceEntity provinceEntity = provinceService.getByCode(parkPO.getProvinceCode());
        parkPO.setProvinceName(Optional.ofNullable(provinceEntity).map(ProvinceEntity::getName).orElse(null));
        CityEntity cityEntity = cityService.getByCode(parkPO.getCityCode());
        parkPO.setCityName(Optional.ofNullable(cityEntity).map(CityEntity::getName).orElse(null));
        DistrictEntity districtEntity = districtService.getByCode(parkPO.getDistrictCode());
        parkPO.setDistrictName(Optional.ofNullable(districtEntity).map(DistrictEntity::getName).orElse(null));
    }

    /**
     * 列外行业税率
     * @param query
     * @return
     */
    @PostMapping("industry/rules/page")
    public ResultVo listPageIndustryRules(@RequestBody IndustryPolicyQuery query, BindingResult result) {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        if(null == query.getCompanyType() || query.getCompanyType()!=1){
            return ResultVo.Fail("非个体企业类型不支持例外行业税率的配置");
        }
        //验证登陆
        getCurrUser();
        query.setIndustryId(null);
        query.setTaxType(1);
        PageInfo<IndustryPolicyVO> page = taxPolicyService.listPageIndustryRules(query);
        return ResultVo.Success(page);
    }

    /**
     * 列外行业税率详情
     */
    @PostMapping("industry/rules/detail")
    public ResultVo industryRulesDetail(@RequestBody IndustryPolicyQuery query, BindingResult result) {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        if (query.getIndustryId() == null) {
            return ResultVo.Fail("请选择列外行业");
        }
        query.setIndustryName(null);
        //验证登陆
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setTaxType(1);
        PageInfo<IndustryPolicyVO> page = taxPolicyService.listPageIndustryRules(query);
        List<IndustryPolicyVO> list = page.getList();
        if (CollectionUtil.isNotEmpty(list)) {
            IndustryPolicyVO vo = list.get(0);
            vo.setRules(taxRulesConfigService.queryByIndustryId(vo.getPolicyId(), vo.getIndustryId(), 1));
            return ResultVo.Success(list);
        }
        return ResultVo.Success();
    }

    /**
     * 更新列外行业税率
     */
    @PostMapping("update/industry/rules")
    public ResultVo updateIndustryRules(@RequestBody IndustryPolicyDTO dto, BindingResult result) {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        //验证登陆
        CurrUser currUser = getCurrUser();
        try {
            TaxPolicyEntity taxPolicyEntity = taxPolicyService.findById(dto.getPolicyId());
            if (taxPolicyEntity == null) {
                return ResultVo.Fail("税费政策不存在");
            }
            if(taxPolicyEntity.getCompanyType()!=1){
                return ResultVo.Fail("非个体企业类型不支持例外行业税率的配置");
            }
            //校验数据
            validateIndustryRulesDTO(dto, taxPolicyEntity);
            IndustryEntity industryEntity = industryService.findById(dto.getIndustryId());
            if (industryEntity == null) {
                return ResultVo.Fail("行业不存在");
            }
            taxRulesConfigService.insertIndustryRules(dto, taxPolicyEntity, currUser.getUseraccount());
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultVo.Fail("更新列外行业税率失败");
        }
        return ResultVo.Success();
    }

    /**
     * 校验列外行业税率
     * @param dto
     * @param taxPolicyEntity
     */
    private void validateIndustryRulesDTO(IndustryPolicyDTO dto, TaxPolicyEntity taxPolicyEntity) throws BusinessException {
        List<TaxRatesRulesVO> rules = dto.getRules();
        if (CollectionUtil.isEmpty(rules)) {
            throw new BusinessException("税费阶梯不能为空");
        }
        if (Objects.equals(taxPolicyEntity.getLevyWay(), LevyWayEnum.TAXABLE_INCOME_RATE.getValue())) {
            TaxRatesRulesVO taxRatesRulesVO = rules.get(0);
            taxRatesRulesVO.setMinAmount(0L);
            taxRatesRulesVO.setMaxAmount(Long.MAX_VALUE);
            rules.clear();
            rules.add(taxRatesRulesVO);
        }
        TaxRatesRulesVO vo;
        for (int i = 0; i < rules.size(); i++) {
            vo = rules.get(i);
            if (vo.getMinAmount() == null || vo.getMinAmount().longValue() < 0) {
                throw new BusinessException("税费阶梯第" + (i+1) + "列最小金额有误");
            }
            if (i == rules.size() - 1 ) {
                vo.setMaxAmount(Long.MAX_VALUE);
            }
            if (vo.getMaxAmount() == null || vo.getMaxAmount().longValue() <= 0) {
                throw new BusinessException("税费阶梯第" + (i+1) + "列最大金额有误");
            }
            if (vo.getMaxAmount().compareTo(vo.getMinAmount()) <= 0) {
                throw new BusinessException("税费阶梯第" + (i+1) + "列最大金额小于或等于最小金额");
            }
            BigDecimal rate = vo.getRate();
            if (rate == null || BigDecimal.ZERO.compareTo(rate) > 0 || new BigDecimal("100").compareTo(rate) < 0) {
                throw new BusinessException("税费阶梯第" + (i+1) + "列税率有误");
            }
            BigDecimal bigDecimal = rate.setScale(4, BigDecimal.ROUND_DOWN);
            if (bigDecimal.compareTo(rate) != 0) {
                throw new BusinessException("税费阶梯第" + (i+1) + "列税率，请保留4位小数");
            }
            if (i == 0) {
                continue;
            }
            if (vo.getMinAmount().compareTo(rules.get(i-1).getMaxAmount()) != 0) {
                throw new BusinessException("税费阶梯第" + (i+1) + "列最小金额不等于上一列最大金额");
            }
        }
        if (dto.getType() == 1) {
            //新增验重
            IndustryPolicyQuery query = new IndustryPolicyQuery();
            query.setPageSize(1);
            query.setTaxType(1);
            query.setPolicyId(dto.getPolicyId());
            query.setIndustryId(dto.getIndustryId());
            PageInfo<IndustryPolicyVO> page = taxPolicyService.listPageIndustryRules(query);
            if (CollectionUtil.isNotEmpty(page.getList())) {
                throw new BusinessException("当前行业已经存在列外行业税率");
            }
        } else {
            //编辑验重
            //查询原来行业id
            Long configId = dto.getRules().stream().filter(a -> a.getId() != null && a.getId() > 0).map(TaxRatesRulesVO::getId).collect(Collectors.toList()).get(0);
            TaxRulesConfigEntity entity = taxRulesConfigService.findById(configId);
            if (entity == null) {
                throw new BusinessException("被编辑的列外行业税率不存在");
            }
            if (!Objects.equals(dto.getIndustryId(), entity.getIndustryId())) {
                //行业id不同，校验新的行业id是不是已经存在
                IndustryPolicyQuery query = new IndustryPolicyQuery();
                query.setPageSize(1);
                query.setTaxType(1);
                query.setPolicyId(dto.getPolicyId());
                query.setIndustryId(dto.getIndustryId());
                PageInfo<IndustryPolicyVO> page = taxPolicyService.listPageIndustryRules(query);
                if (CollectionUtil.isNotEmpty(page.getList())) {
                    throw new BusinessException("当前行业已经存在列外行业税率");
                }
                dto.setChangeIndustryId(entity.getIndustryId());
            }
        }
    }

    /**
     * 删除例外行业政策配置
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("delete/industry/rules")
    public ResultVo deleteIndustryRules(@RequestBody IndustryPolicyDTO dto, BindingResult result) {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        //验证登陆
        CurrUser currUser = getCurrUser();
        try {
            UserEntity userEntity = userService.findById(currUser.getUserId());
            if (userEntity == null) {
                return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
            }
            TaxPolicyEntity taxPolicyEntity = taxPolicyService.findById(dto.getPolicyId());
            if (taxPolicyEntity == null) {
                return ResultVo.Fail("税费政策不存在");
            }
            IndustryEntity industryEntity = industryService.findById(dto.getIndustryId());
            if (industryEntity == null) {
                return ResultVo.Fail("行业不存在");
            }
            Example example = new Example(TaxRulesConfigEntity.class);
            example.createCriteria().andEqualTo("industryId", dto.getIndustryId())
                    .andEqualTo("policyId", dto.getPolicyId());
            taxRulesConfigService.delByExample(example);
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResultVo.Fail("删除列外行业税率失败");
        }
        return ResultVo.Success();
    }


    /**
     * 开票截至详情
     */
    @PostMapping("endTime/detail")
    public ResultVo queryEndTimeDetail(@JsonParam Long parkId) {
        //验证登陆
        getCurrUser();
        try {
            Map<String,Object> map = new HashMap<>();
            ParkEndtimeConfigEntity parkEndtimeConfigEntity = new ParkEndtimeConfigEntity();
            parkEndtimeConfigEntity.setParkId(parkId);
            parkEndtimeConfigEntity.setOperType(OperTypeEnum.CTEAT.getValue());
            List<ParkEndtimeConfigEntity> list = parkEndtimeConfigService.select(parkEndtimeConfigEntity);
            ParkEntity parkEntity = parkService.findById(parkId);
            TaxPolicyEntity taxPolicyEntity = taxPolicyService.queryTaxPolicyByParkId(parkId,1,1);
            if(taxPolicyEntity == null || taxPolicyEntity.getIncomeLevyType() == null){
                map.put("list",list);
                return ResultVo.Success(map);
            }
            if (parkEntity != null && taxPolicyEntity.getIncomeLevyType().equals(1)){
                Calendar cal = Calendar.getInstance();
                Integer year = cal.get(Calendar.YEAR);
                List<ParkEndtimeConfigEntity> timeList =null;
                // 查询去年，今年，明年的数据
                for (int j = year-1;j<year+2;j++){
                    timeList = new ArrayList<>();
                    for (int i=1;i<5;i++){
                        ParkEndtimeConfigEntity entity = parkEndtimeConfigService.queryByOperTypeAndParkIdAndYearAndQuarter(parkId,OperTypeEnum.COST.getValue(),j,i);
                        timeList.add(entity);
                    }
                    map.put(j+"",timeList);
                }
            }
            map.put("list",list);
            return ResultVo.Success(map);
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 开票截至设置
     */
    @PostMapping("endTime/updateOrAdd")
    public ResultVo endTimeUpdateOrAdd(@RequestBody ParkEndtimeConfigPO po) {
        //验证登陆
        CurrUser currUser = getCurrUser();
        if (po.getParkId() == null){
            return ResultVo.Fail("园区id不能为空");
        }
        if(po.getDzEndTime()!=null&&po.getDzStartTime()!=null&&po.getZzEndTime()!=null&&po.getZzStartTime()!=null){
            if(po.getZzEndTime().compareTo(po.getZzEndTime())<0) {
                return ResultVo.Fail("纸票截止开始不能早于开始时间");
            }
            if(po.getDzEndTime().compareTo(po.getDzEndTime())<0) {
                return ResultVo.Fail("电票截止开始不能早于开始时间");
            }
        }
       if (po.getQuarterTime() == null || po.getQuarterTime().size() != 4){
           return ResultVo.Fail("成本确认时间参数错误");
       }
       parkEndtimeConfigService.insertParkEndtimeConfigInfo(po,currUser.getUseraccount());
        return  ResultVo.Success();
    }

    /**
     * 新增经营地址
     * @param parkBusinessAddressVO
     * @return
     */
    @PostMapping("add/parkBusinessAddress")
    public ResultVo addParkBusinessAddress(@RequestBody ParkBusinessAddressVO parkBusinessAddressVO){
        //验证登陆
        CurrUser currUser = getCurrUser();
        if (parkBusinessAddressVO == null || parkBusinessAddressVO.getAddressType() == null){
            return ResultVo.Fail("请选择地址类型");
        }
        if (parkBusinessAddressVO.getParkId() == null){
            return ResultVo.Fail("园区错误");
        }
        //  校验参数
        ParkBusinessAddressRulesEntity entity= parkBusinessAddressRulesService.checkAddress(parkBusinessAddressVO);
        entity.setAddTime(new Date());
        entity.setAddUser(currUser.getUseraccount());
        parkBusinessAddressRulesService.insertSelective(entity);
        return ResultVo.Success();
    }

    /**
     * 经营地址详情
     * @param parkId
     * @return
     */
    @PostMapping("parkBusinessAddressDetail")
    public ResultVo parkBusinessAddressDetail(@JsonParam Long parkId){
        //验证登陆
        CurrUser currUser = getCurrUser();
        if (parkId == null){
            return ResultVo.Fail("园区选择错误");
        }
        ParkBusinessAddressVO vo = parkBusinessAddressRulesService.queryByParkId(parkId);
        if (vo != null){
            String address = registerOrderService.getBusinessAddress(parkId);
            if (StringUtil.isEmpty(address)){
                address = "该园区地址暂未使用";
            }
            vo.setUseAddress(address);
            if (StringUtil.isNotBlank(vo.getAreaRegistNumMin()) && StringUtil.isNotBlank(vo.getAreaRegistNumMax())){
                vo.setStringAreaRegistNumMin(parkBusinessAddressRulesService.getAreaRegistNumMin(vo.getAreaRegistNumMin(),vo.getAreaRegistNumMax()));
            }
        } else {
            vo = null;
        }
        return ResultVo.Success(vo);
    }

    /**
     * 经营地址修改
     * @param parkBusinessAddressVO
     * @return
     */
    @PostMapping("update/parkBusinessAddress")
    public ResultVo updateParkBusinessAddress(@RequestBody ParkBusinessAddressVO parkBusinessAddressVO){
        //验证登陆
        CurrUser currUser = getCurrUser();
        if (parkBusinessAddressVO == null || parkBusinessAddressVO.getAddressType() == null){
            return ResultVo.Fail("请选择地址类型");
        }
        if (parkBusinessAddressVO.getParkId() == null){
            return ResultVo.Fail("园区错误");
        }
        //  校验参数
        ParkBusinessAddressRulesEntity entity= parkBusinessAddressRulesService.checkAddress(parkBusinessAddressVO);
        entity.setId(parkBusinessAddressVO.getId());
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(currUser.getUseraccount());
        parkBusinessAddressRulesService.editByIdSelective(entity);
        return ResultVo.Success();
    }

    /**
     * 禁用字号列表查询
     * @param query
     * @return
     */
    @PostMapping("parkDisableWord/page")
    public ResultVo queryParkDisableWord(@RequestBody ParkDisableWordQuery query){
        //验证登陆
        CurrUser currUser = getCurrUser();
        if (query == null || query.getParkId() == null){
            return ResultVo.Fail("园区id不正确");
        }
        PageInfo<ParkDisableWordVO> pageInfo = parkDisableWordService.queryParkDisableWord(query);
        return ResultVo.Success(pageInfo);
    }

    /**
     * 导出禁用字段
     * @param query
     * @return
     */
    @PostMapping("parkDisableWord/batch")
    public ResultVo batchParkDisaleWord(@RequestBody ParkDisableWordQuery query){
        //验证登陆
        CurrUser currUser = getCurrUser();
        if (query == null || query.getParkId() == null){
            return ResultVo.Fail("园区id不正确");
        }
        List<ParkDisableWordVO> list = parkDisableWordService.listParkDisableWord(query);
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无数据导出");
        }
       for (int i = 1;i <= list.size();i++){
           list.get(i-1).setId((long)i);
       }
        ParkEntity parkEntity = parkService.findById(query.getParkId());
        try {
            exportExcel(parkEntity.getParkName()+"禁用字号", parkEntity.getParkName()+"禁用字号", ParkDisableWordVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error(parkEntity.getParkName()+"禁用字号:" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * 添加单个字号
     * @param parkId
     * @param disableWord
     * @return
     */
    @PostMapping("parkDisableWord/add")
    public ResultVo addParkDisableWork(@JsonParam Long parkId,@JsonParam String disableWord){
        //验证登陆
        CurrUser currUser = getCurrUser();
        if (parkId == null ){
            return ResultVo.Fail("园区id不能为空");
        }
        if ( StringUtil.isBlank(disableWord)){
            return ResultVo.Fail("禁用字号不能为空");
        }
        if(disableWord.length()>6){
            return ResultVo.Fail("禁用字号长度不能超过6位");
        }
        String reg = "[\\u4e00-\\u9fa5]+";
        boolean flag = disableWord.matches(reg);
        if (!flag){
            return ResultVo.Fail("禁用字号只能为中文");
        }

        ParkDisableWordEntity parkDisableWordEntity = parkDisableWordService.getDisableWordByParkIdAndDisableWord(parkId,disableWord);
        if (parkDisableWordEntity != null){
            return ResultVo.Fail("该园区已存在该禁用字段");
        }
        parkDisableWordEntity = new ParkDisableWordEntity();
        parkDisableWordEntity.setDisableWord(disableWord);
        parkDisableWordEntity.setParkId(parkId);
        parkDisableWordEntity.setAddTime(new Date());
        parkDisableWordEntity.setAddUser(currUser.getUseraccount());
        parkDisableWordService.insertSelective(parkDisableWordEntity);
        return ResultVo.Success();
    }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @PostMapping("parkDisableWord/delete")
    public ResultVo deleteDisableWork(@JsonParam Long id){
        //验证登陆
        CurrUser currUser = getCurrUser();
        if (id == null){
            return ResultVo.Fail("序号不正确");
        }
        int result = parkDisableWordService.delById(id);
        return ResultVo.Success();
    }

    /**
     * 批量添加
     * @param file
     * @param parkId
     * @return
     */
    @PostMapping("parkDisableWord/batchAdd")
    public ResultVo batchAddDisableWork(@RequestParam("file") MultipartFile file, @RequestParam Long parkId){
        //验证登陆
        CurrUser currUser = getCurrUser();
        if (parkId == null){
            return ResultVo.Fail("园区id不能为空");
        }
        if (file == null || file.isEmpty()) {
            return ResultVo.Fail("请求参数有误");
        }
        List<ParkDisableWordInsertVO> excelList = Lists.newArrayList();
        List<ParkDisableWordInsertVO> list = Lists.newArrayList();
        try {
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            excelList = ExcelImportUtil.importExcel(file.getInputStream(), ParkDisableWordInsertVO.class, params);
            if (excelList.size() <1){
                return ResultVo.Fail("文件不能为空");
            }
        } catch (Exception e) {
            log.error("添加禁用字段异常：" + e.getMessage(), e);
            return ResultVo.Fail("你的文件格式不对");
        }
        //  去除无效行
        for (ParkDisableWordInsertVO vo : excelList){
            if (StringUtil.isNotBlank(vo.getDisableWord())){
                list.add(vo);
            }
        }
        if (CollectionUtil.isEmpty(list)){
            return ResultVo.Fail("模板错误");
        }
        Map<String,Object> map = parkDisableWordService.checkDisableWord(list,parkId,currUser.getUseraccount());
        Object success = map.get("success");
        Object fail = map.get("fail");
        List<ParkDisableWordEntity> successList = Convert.toList(ParkDisableWordEntity.class,success);
        List<ParkDisableWordInsertVO> failList =  Convert.toList(ParkDisableWordInsertVO.class,fail);
        Map<String, Object> result = new HashMap<>();
        result.put("success", successList.size());
        result.put("failed", failList.size());
        if (CollectionUtil.isNotEmpty(successList)){
            parkDisableWordService.batchAddDisableWord(successList);
        }
        if (CollectionUtil.isEmpty(failList)){
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
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), ParkDisableWordInsertVO.class, failList);
            workbook.write(bos);
            workbook.close();
        } catch (Exception e) {
            log.error("禁用字号添加异常：" + e.getMessage(), e);
            return ResultVo.Fail("禁用字号添加异常");
        }finally {
            IOUtils.closeQuietly(bos);
        }
        result.put("downLoadUrl", fileName);
        return ResultVo.Success(result);
    }
}
