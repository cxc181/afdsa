package com.yuqian.itax.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.enums.SourceTypeEnum;
import com.yuqian.itax.agent.service.OemConfigService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.system.service.CityService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.DistrictService;
import com.yuqian.itax.system.service.ProvinceService;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

/**
 *  @Date: 2019/12/19 10:30
 *  @Description: 基础控制器
 */
@RestController
@RequestMapping("/")
@Slf4j
public class IndexController extends BaseController {
    @Autowired
    private OemService oemService;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private CityService cityService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private OemConfigService oemConfigService;

    /**
     * 查看全部的机构信息，用于做数据缓存
     * @return
     */
    @PostMapping("/allOemInfos")
    public ResultVo allOemInfos(){
        OemEntity entity = new OemEntity();
        entity.setOemStatus(1);
        List<OemEntity> list =  oemService.select(entity);
        list.forEach(vo -> redisService.set(RedisKey.OEM_CODE_KEY + vo.getOemCode(),vo));
        return ResultVo.Success(list);
    }

    /**
     * 根据机构编码获取机构信息
     * @param oemCode
     * @return
     */
    @PostMapping("/getOemByCode")
    public ResultVo getOemByCode(@JsonParam  String oemCode){
        if(StringUtils.isEmpty(oemCode)){
            return ResultVo.Fail("机构编码不能为空");
        }
        OemEntity entity = oemService.getOem(oemCode);
        if(entity == null) {
            return ResultVo.Fail("未找到机构信息");
        }
        Map<String,Object> result = new HashMap<>();
        result.put("customerServiceTel",entity.getCustomerServiceTel()); //获取机构客服电话
        result.put("oemSecret",entity.getOemSecret());
        result.put("oemName",entity.getOemName());
        result.put("isInviterCheck",entity.getIsInviterCheck());
        result.put("isOpenPromotion",entity.getIsOpenPromotion());
        result.put("companyName",entity.getCompanyName());
        result.put("oemStatus",entity.getOemStatus());
        result.put("oemLogo",entity.getOemLogo());
        result.put("versionCode",entity.getVersionCode());
        result.put("isBigCustomer",entity.getIsBigCustomer());
        result.put("isOtherOemPay", entity.getIsOtherOemPay());
        result.put("otherPayOemcode", entity.getOtherPayOemcode());
        String sourceType = getRequestHeadParams("sourceType");
        String dictCode = "itax_wechatpay_switch";
        if (Objects.equals(SourceTypeEnum.ALIPAY.getValue(), sourceType)) {
            dictCode = "itax_alipay_switch";
        }else if (Objects.equals(SourceTypeEnum.BYTEDANCE.getValue(), sourceType)) {
            dictCode = "itax_bytedance_switch";
        }
        String weChatPaySwitch = Optional.ofNullable(dictionaryService.getValueByCode(dictCode)).orElse("0");
        result.put("weChatPaySwitch", weChatPaySwitch);
        //测试mq
//        this.rabbitTemplate.convertAndSend("test", oemCode);

        // 获取oem机构配置信息
        OemConfigEntity configEntity = oemConfigService.queryOemConfigByCode(oemCode, "is_open_channel");
        result.put("isOpenChannel",Integer.valueOf(configEntity.getParamsValue()));
        //添加收银台的appID
        if( entity.getIsOtherOemPay()!=null && entity.getIsOtherOemPay() == 1 && StringUtil.isNotBlank(entity.getOtherPayOemcode())) {
            entity = oemService.getOem(entity.getOtherPayOemcode());
            result.put("oemAppid", entity!=null?entity.getOemAppid():null);
        }else{
            result.put("oemAppid", null);
        }
        return ResultVo.Success(result);
    }

    /**
     * 自动升级补单
     * userId需要自动升级账号的下级ID
     */
    @PostMapping("/memberAutoUpdate")
    public ResultVo memberAutoUpdate(@JsonParam  Long userId){
        if(userId ==null){
            return ResultVo.Fail("用户ID不能为空");
        }
        JSONObject json =new JSONObject();
        json.put("userId",userId);
        rabbitTemplate.convertAndSend("memberAutoUpdate", json);
        return ResultVo.Success();
    }
    /**
     * 日快照补单
     * userId orderNo为空时必穿
     * orderNo type!=0的时候必穿
     * type  0=注册 1=订单
     */

    @PostMapping("/statisticsMemberGeneralize")
    public ResultVo statisticsMemberGeneralize(@JsonParam  Long userId,@JsonParam String orderNo,@JsonParam int type){
        if(userId ==null){
            return ResultVo.Fail("用户ID不能为空");
        }
        if(StringUtils.isEmpty(orderNo)&&type!=0){
            return ResultVo.Fail("请输入订单号");
        }
        com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
        json.put("orderNo",orderNo);
        json.put("type",type);
        json.put("userId",userId);
        //发MQ
        rabbitTemplate.convertAndSend("statisticsMemberGeneralize", json);

        return ResultVo.Success();
    }

    /**
     * oss文件下载
     * @param file
     * @return
     * @throws IOException
     */
//    @PostMapping("oss/download")
//    public ResultVo ossDownload(@RequestParam("file") MultipartFile file) throws IOException {
//        if (file == null || file.isEmpty()) {
//            return ResultVo.Fail("请求参数有误");
//        }
//        String accessId = sysDictionaryService.getByCode("oss_accessKeyId").getDictValue();
//        String accessKey = sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue();
//        String endpoint = sysDictionaryService.getByCode("oss_endpoint").getDictValue();
//        String bucket = sysDictionaryService.getByCode("oss_privateBucketName").getDictValue();
//        String path = "D://down";
//        String filePath;
//        File file1;
//        String[] split;
//        String str;
//        BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream()));
//        while (StringUtils.isNotBlank(str = bf.readLine())) {
//            split = str.split(",");
//            filePath = path + "/";
//            file1 = new File(filePath);
//            if (!file1.exists()) {
//                file1.mkdirs();
//            }
//            OssUtil.download(endpoint, accessId, accessKey, bucket, split[2], filePath + split[1] + "_正面.png");
//            OssUtil.download(endpoint, accessId, accessKey, bucket, split[3], filePath + split[1] + "_反面.png");
//        }
//
//        return ResultVo.Success();
//    }
//    /**
//     * 全省
//     * @return
//     */
//    @GetMapping("/allProvince")
//    public ResultVo allProvince(){
//        List<ProvinceEntity> list = provinceService.selectAll();
//        List<Map<String,String>> result = new ArrayList<>();
//        Map<String,String> province = null;
//        for(ProvinceEntity vo : list){
//            province = new HashMap<>();
//            province.put("label",vo.getName());
//            province.put("value",vo.getCode());
//            result.add(province);
//        }
//        return ResultVo.Success(result);
//    }
//
//    /**
//     * 全市
//     * @return
//     */
//    @GetMapping("/allCity")
//    public ResultVo allCity(){
//        List<ProvinceEntity> list = provinceService.selectAll();
//        List<List<Map<String,String>>> result = new ArrayList<>();
//        Map<String,String> city = null;
//        List<Map<String,String>> prov = null;
//        for(ProvinceEntity vo : list) {
//            prov = new ArrayList<>();
//            CityEntity entity = new CityEntity();
//            entity.setProvinceCode(vo.getCode());
//            List<CityEntity> clist = cityService.select(entity);
//            for (CityEntity cityEntity : clist) {
//                city = new HashMap<>();
//                city.put("label", cityEntity.getName());
//                city.put("value", cityEntity.getCode());
//                prov.add(city);
//            }
//            result.add(prov);
//        }
//        return ResultVo.Success(result);
//    }
//
//    /**
//     * 全区
//     * @return
//     */
//    @GetMapping("/allDictionary")
//    public ResultVo allDictionary(){
//        List<ProvinceEntity> list = provinceService.selectAll();
//        List<List<List<Map<String,String>>>> result = new ArrayList<>();
//        List<List<Map<String,String>>> plist = null;
//        Map<String,String> districtMap = null;
//        List<Map<String,String>> prov = null;
//        for(ProvinceEntity vo : list) {
//            plist = new ArrayList<>();
//            CityEntity entity = new CityEntity();
//            entity.setProvinceCode(vo.getCode());
//            List<CityEntity> clist = cityService.select(entity);
//            for (CityEntity cityEntity : clist) {
//                prov = new ArrayList<>();
//                DistrictEntity de = new DistrictEntity();
//                de.setCityCode(cityEntity.getCode());
//                List<DistrictEntity> dlist = districtService.select(de);
//                for (DistrictEntity dvo: dlist){
//                    districtMap = new HashMap<>();
//                    districtMap.put("label", dvo.getName());
//                    districtMap.put("value", dvo.getCode());
//                    prov.add(districtMap);
//                }
//                plist.add(prov);
//            }
//            result.add(plist);
//        }
//        return ResultVo.Success(result);
//    }
}