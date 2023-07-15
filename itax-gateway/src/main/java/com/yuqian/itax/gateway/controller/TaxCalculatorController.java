package com.yuqian.itax.gateway.controller;

import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.gateway.annotation.JsonParam;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.entity.dto.CalculateTaxDTO;
import com.yuqian.itax.order.entity.query.ParkRewardQuery;
import com.yuqian.itax.order.entity.vo.CalculateTaxVO;
import com.yuqian.itax.order.entity.vo.ParkRewardVO;
import com.yuqian.itax.order.service.TaxCalculatorService;
import com.yuqian.itax.park.entity.vo.TaxCalculatorParkVO;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.product.entity.vo.ProductOfTaxCalculatorVO;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.system.service.IndustryBaseService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.dto.GenqrcodeDTO;
import com.yuqian.itax.user.entity.dto.TaxCalculatorLoginDTO;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.util.util.ActivationCode;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.wechat.WeChatService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TaxCalculatorController
 * @Description 税费测算控制器
 * @Author lmh
 * @Date 2022/9/26 15:32
 * @Version 1.0
 */
@RestController
@RequestMapping("/taxCalculator")
@Slf4j
public class TaxCalculatorController extends BaseController {

    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private OemService oemService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private TaxCalculatorService taxCalculatorService;
    @Autowired
    private IndustryBaseService industryBaseService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private WeChatService weChatService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private ProductService productService;

    /**
     * 获取机构秘钥
     */
    @ApiOperation("获取机构秘钥")
    @PostMapping("/getOemSecret")
    public ResultVo getOemSecret() {
        String oemCode = getRequestHeadParams("oemCode");
        OemEntity oem = oemService.getOem(oemCode);
        if (null == oem) {
            return ResultVo.Fail("未找到机构信息");
        }
        HashMap<String, String> map = Maps.newHashMap();
        map.put("oemSecret", oem.getOemSecret());
        // 获取appId
        OemParamsEntity paramsEntity = oemParamsService.getParams(oemCode, 8);
        if (null == paramsEntity) {
            return ResultVo.Fail("未配置微信相关信息！");
        }
        map.put("appId", paramsEntity.getAccount());
        return ResultVo.Success(map);
    }

    /**
     * 发送短信验证码
     */
    @ApiOperation("发送短信验证码")
    @PostMapping("/sendVerification")
    public ResultVo sendVerification(@JsonParam String phone){

        String oemCode = this.getRequestHeadParams("oemCode");
        OemEntity oemEntity =  oemService.getOem(oemCode);
        String verficationCode = null; //验证码
        Map<String, Object> resultMap = Maps.newHashMap();    //获取返回结果

        String templateType = "2";// 登录短信模板
        // 判断手机号是否已经注册过
        MemberAccountEntity memberAccountEntity = memberAccountService.queryByAccount(phone, oemCode);
        //判断手机号码是否已经注册
        if (null == memberAccountEntity) {
            // 手机号码未注册，短信模板使用注册模板
            templateType = "1";
        }

        if (oemEntity != null && StringUtils.isNotBlank(oemEntity.getDefaultSmsCode())) {
            verficationCode = oemEntity.getDefaultSmsCode();
        } else {
            //获取验证码并发送
            verficationCode = ActivationCode.getActivationCode();
        }
        Map<String, Object> map = new HashMap();
        map.put("verficationCode", verficationCode);

        Map<String, Object> sendMap = smsService.sendTemplateSms(phone, oemCode, templateType, map, 1);
        resultMap.putAll(sendMap);// 合并结果集
        if (!"SUCCESS".equals(resultMap.get("code"))) {
            return ResultVo.Fail((String) resultMap.get("message"));
        }

        // 设置redis缓存
        redisService.set(RedisKey.SMS_USER_LOGIN_KEY_SUFFER + phone,verficationCode,300);//timeout秒为单位

        resultMap.remove("code");
        resultMap.remove("message");
        return ResultVo.Success(resultMap);
    }

    /**
     * 用户自动注册登录接口，返回登录token及用户id
     * @param
     * @return
     */
    @ApiOperation("自动注册/登录")
    @PostMapping("/login")
    public ResultVo login(@RequestBody @Validated TaxCalculatorLoginDTO dto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResultVo.Fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        if (StringUtil.isBlank(dto.getAccount())) {
            throw new BusinessException("手机号不能为空");
        }

        // 校验验证码
        String verificationCode = redisService.get(RedisKey.SMS_USER_LOGIN_KEY_SUFFER + dto.getAccount());
        // 校验验证码是否错误或过期
        if (verificationCode == null || "".equals(verificationCode) || !dto.getVerificationCode().equals(verificationCode)) {
            throw new BusinessException(MessageEnum.PASSWORD_RESET_CODE_IS_EXPIRED.getMessage());
        }

        dto.setOemCode(getRequestHeadParams("oemCode"));
        Map<String, Object> dateMap = memberAccountService.loginOfTaxCalculator(dto);
        return ResultVo.Success(dateMap);
    }

    /**
     * 税费计算
     */
    @ApiOperation("税费计算")
    @PostMapping("/calculateTax")
    public ResultVo calculate(@RequestBody @Validated CalculateTaxDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResultVo.Fail(result.getAllErrors().get(0).getDefaultMessage());
        }
        dto.setOemCode(getRequestHeadParams("oemCode"));
        CalculateTaxVO calculateTaxVO = taxCalculatorService.calculateTax(dto);
        return ResultVo.Success(calculateTaxVO);
    }

    /**
     * 查看园区奖励
     */
    @ApiOperation("查看园区奖励")
    @PostMapping("/getParkReward")
    public ResultVo getParkReward(@RequestBody @Validated ParkRewardQuery query, BindingResult result) {
        if (result.hasErrors()) {
            return ResultVo.Fail(result.getAllErrors().get(0).getDefaultMessage());
        }
        String token = getRequestHeadParams("token");
        if (StringUtil.isBlank(token)) {
            throw new BusinessException("未登录");
        }
        List<ParkRewardVO> list = taxCalculatorService.queryParkReward(query);
        return ResultVo.Success(list);
    }

    /**
     * 查询行业列表及其增值税税率
     * @param
     * @return
     */
    @ApiOperation("查询行业列表及其增值税税率")
    @PostMapping("/queryIndustryBase")
    public ResultVo queryIndustryBase() {
        return ResultVo.Success(industryBaseService.selectAll());
    }

    /**
     * 获取园区列表
     * @param
     * @return
     */
    @ApiOperation("获取园区列表")
    @ApiImplicitParam(name="companyType",value="企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任",dataType="Integer", required = true)
    @PostMapping("/queryParkList")
    public ResultVo queryParkList(@JsonParam Integer companyType){
        if(null == companyType){
            return ResultVo.Fail("企业类型不能为空");
        }
        List<TaxCalculatorParkVO> parkList = parkService.getAllParkList(companyType, getRequestHeadParams("oemCode"));
        return ResultVo.Success(parkList);
    }

    /**
     * 自定义分享
     */
    @ApiOperation("自定义分享")
    @PostMapping("customSharing")
    public ResultVo customSharing(@JsonParam String inviterAccount) {
        if (StringUtil.isBlank(inviterAccount)) {
            return ResultVo.Fail("邀请人不能为空");
        }

        String oemCode = getRequestHeadParams("oemCode");
        String jsApiTicket = weChatService.getJsApiTicket(oemCode);

        String shareUrl = sysDictionaryService.getValueByCode("measuring_tool_share_url");
        if (StringUtil.isBlank(shareUrl)) {
            return ResultVo.Fail("未配置测算工具分享url");
        }
        String url = shareUrl + oemCode + "&inviterAccount=" + inviterAccount;
        Map<String, String> map = taxCalculatorService.customSharing(jsApiTicket, url);
        return ResultVo.Success(map);
    }

    /**
     * 获取企业经营性质列表
     */
    @ApiOperation("获取企业经营性质列表")
    @PostMapping("/companyTypeList")
    public ResultVo companyTypeList() {
        String oemCode = getRequestHeadParams("oemCode");
        List<ProductOfTaxCalculatorVO> list = productService.getTaxCalculatorProductList(oemCode);
        return ResultVo.Success(list);
    }

    @ApiOperation("获取小程序二维码")
    @PostMapping("/getQRCode")
    public ResultVo<String> getQRCode(@RequestBody @Valid GenqrcodeDTO entity, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        String base64QRCode = weChatService.getQRCode(
                getRequestHeadParams("oemCode"),
                entity.getScene(),
                entity.getWidth(),
                entity.getPage(),
                1,
                org.apache.commons.lang.StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType"));
        return ResultVo.Success(base64QRCode);
    }
}
