package com.yuqian.itax.nabei.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.nabei.entity.*;
import com.yuqian.itax.nabei.service.NabeiApiService;
import com.yuqian.itax.util.util.*;
import com.yuqian.itax.util.util.nabei.Cipher;
import com.yuqian.itax.util.util.nabei.NabeiAPIUtil;
import com.yuqian.itax.util.util.nabei.SM2;
import com.yuqian.itax.util.util.nabei.SM2Utils;
import javassist.bytecode.ByteArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.util.Lists;
import org.bouncycastle.math.ec.ECPoint;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * @ClassName: NabeiApiServiceImpl
 * @Description: 纳呗平台接口service 实现
 * @Author: yejian
 * @Date: Created in 2020/6/23
 * @Version: 1.0
 */
@Slf4j
@Service("nabeiApiService")
public class NabeiApiServiceImpl implements NabeiApiService {

    @Autowired
    private OemParamsService oemParamsService;

    /** 2.1 企业账户查询 */
    private static final String URL_MCHNT_BAL = "/v1/merchantAccount";
    /** 2.2 签约注册 */
    private static final String URL_SIGNREG = "/v1/signRegister";
    /** 2.3 签约注册查询 */
    private static final String URL_SIGNQRY = "/v1/signQuery";
    /** 2.4 用户额度查询 */
    private static final String URL_PERSONBAL = "/v1/personBal";
    /** 2.5 单笔出款申请 */
    private static final String URL_SINGLEPAY = "/v1/singlePay";
    /** 2.6 单笔出款查询 */
    private static final String URL_SINGLEPAY_QUERY = "/v1/singlePayQuery";
    /** 2.7 修改签约账户 */
    private static final String URL_CHANGE_BINDCARD = "/v1/changeBindCard";
    /** 2.8 对账文件下载 */
    private static final String URL_REMIT = "/v1/remit";
    /** 2.9 签约申请 */
    private static final String URL_H5_SIGN = "/v1/h5Sign";
    /** 2.10 提交付款业绩成果凭证 */
    private static final String URL_ACHIEVEMENTS = "/v1/achievements";

    @Override
    public APIRegisterRespVo signRegister(OemParamsEntity paramsEntity, String name, String idcardNo, String accountNo, String mobile){
        APIRegisterRespVo resVo = new APIRegisterRespVo();
        try {
            // 解析paramValues，配置样例：{"appId": "M190724072"}
            JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
            String appId = params.getString("appId");

            //组装请求参数
            APIRegisterRequestVo requestVo = new APIRegisterRequestVo();
            requestVo.setP1_taxNo(paramsEntity.getAccount());
            requestVo.setP2_name(name);
            requestVo.setP3_idcardNo(idcardNo);
            requestVo.setP4_accountNo(accountNo);
            requestVo.setP5_mobile(mobile);

            //签名
            String hmac = NabeiAPIUtil.sign(requestVo, paramsEntity.getSecKey());
            requestVo.setP6_hmac(hmac);
            String jsonStr = JSONObject.toJSONString(requestVo);
            log.info("请求参数JSON:"+jsonStr);

            String resJson = callApi(paramsEntity.getUrl()+URL_SIGNREG, jsonStr, appId, paramsEntity.getPublicKey(), paramsEntity.getPrivateKey());
            resVo = JSONObject.parseObject(resJson, APIRegisterRespVo.class);
            log.info("返回结果JSON:"+JSON.toJSONString(resVo));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("纳呗签约注册请求异常{}",e.getMessage());
        }
        return resVo;
    }

    @Override
    public APISignQueryRespVo signQuery(OemParamsEntity paramsEntity, String idcardNo, String extAccountNo) {
        APISignQueryRespVo resVo = new APISignQueryRespVo();
        try{
            // 解析paramValues，配置样例：{"appId": "M190724072"}
            JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
            String appId = params.getString("appId");

            //组装请求参数
            APISignQueryRequest requestVo = new APISignQueryRequest();
            requestVo.setP1_taxNo(paramsEntity.getAccount());
            requestVo.setP2_idcardNo(idcardNo);

            //签名
            String hmac = NabeiAPIUtil.sign(requestVo, paramsEntity.getSecKey());
            requestVo.setP3_hmac(hmac);
            requestVo.setExtAccountNo(extAccountNo);// 银行卡号不参与签名
            String jsonStr = JSONObject.toJSONString(requestVo);
            log.info("请求参数JSON:"+jsonStr);

            //发起请求
            String resJson = callApi(paramsEntity.getUrl()+URL_SIGNQRY, jsonStr, appId, paramsEntity.getPublicKey(), paramsEntity.getPrivateKey());
            resVo = JSONObject.parseObject(resJson, APISignQueryRespVo.class);
            log.info("返回结果JSON:"+JSON.toJSONString(resVo));
        }catch (Exception e){
            log.error("纳呗签约注册查询请求异常{}",e.getMessage());
            e.printStackTrace();
        }
        return resVo;
    }

    @Override
    public ChangeBindCardRespVo changeBindCard(OemParamsEntity paramsEntity, String accountName, String idcardNo, String orgAccountNo, String accountNo, String mobile) {
        ChangeBindCardRespVo resVo = new ChangeBindCardRespVo();
        try{
            // 解析paramValues，配置样例：{"appId": "M190724072"}
            JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
            String appId = params.getString("appId");

            //组装请求参数
            ChangeBindCardRequestVo requestVo = new ChangeBindCardRequestVo();
            requestVo.setP1_taxNo(paramsEntity.getAccount());
            requestVo.setP2_accountName(accountName);
            requestVo.setP3_idcardNo(idcardNo);
            requestVo.setP4_orgAccountNo(orgAccountNo);
            requestVo.setP5_accountNo(accountNo);
            requestVo.setP6_mobile(mobile);

            //签名
            String hmac = NabeiAPIUtil.sign(requestVo, paramsEntity.getSecKey());
            requestVo.setP7_hmac(hmac);
            String jsonStr = JSONObject.toJSONString(requestVo);
            log.info("请求参数JSON:"+jsonStr);

            //发起请求
            String resJson = callApi(paramsEntity.getUrl()+URL_CHANGE_BINDCARD, jsonStr, appId, paramsEntity.getPublicKey(), paramsEntity.getPrivateKey());
            resVo = JSONObject.parseObject(resJson, ChangeBindCardRespVo.class);
            log.info("返回结果JSON:"+JSON.toJSONString(resVo));
        }catch (Exception e){
            log.error("纳呗修改签约账户请求异常{}",e.getMessage());
            e.printStackTrace();
        }
        return resVo;
    }

    @Override
    public SinglePayRespVo singlePay(OemParamsEntity paramsEntity, String orderNo, String accountName, String idcardNo, String accountNo, String mobile, String amount, String remark) {
        SinglePayRespVo resVo = new SinglePayRespVo();
        try{
            // 解析paramValues，配置样例：{"appId": "M190724072"}
            JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
            String appId = params.getString("appId");

            //组装请求参数
            SinglePayRequestVo requestVo = new SinglePayRequestVo();
            requestVo.setP1_taxNo(paramsEntity.getAccount());
            requestVo.setP2_orderNo(orderNo);
            requestVo.setP3_accountName(accountName);
            requestVo.setP4_idcardNo(idcardNo);
            requestVo.setP5_accountNo(accountNo);
            requestVo.setP6_mobile(mobile);
            requestVo.setP7_amount(MoneyUtil.moneydiv(amount, "100"));
            requestVo.setP8_remark(remark);

            //签名
            String hmac = NabeiAPIUtil.sign(requestVo, paramsEntity.getSecKey());
            requestVo.setP10_hmac(hmac);
            String jsonStr = JSONObject.toJSONString(requestVo);
            log.info("请求参数JSON:"+jsonStr);

            //发起请求
            String resJson = callApi(paramsEntity.getUrl()+URL_SINGLEPAY, jsonStr, appId, paramsEntity.getPublicKey(), paramsEntity.getPrivateKey());
            resVo = JSONObject.parseObject(resJson, SinglePayRespVo.class);
            log.info("返回结果JSON:"+JSON.toJSONString(resVo));
        }catch (Exception e){
            log.error("纳呗单笔出款申请请求异常{}",e.getMessage());
            e.printStackTrace();
        }
        return resVo;
    }

    @Override
    public SinglePayQueryRespVo singlePayQuery(OemParamsEntity paramsEntity, String orderNo) {
        SinglePayQueryRespVo resVo = new SinglePayQueryRespVo();
        try{
            // 解析paramValues，配置样例：{"appId": "M190724072"}
            JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
            String appId = params.getString("appId");

            //组装请求参数
            SinglePayQueryRequestVo requestVo = new SinglePayQueryRequestVo();
            requestVo.setP1_taxNo(paramsEntity.getAccount());
            requestVo.setP2_orderNo(orderNo);

            //签名
            String hmac = NabeiAPIUtil.sign(requestVo, paramsEntity.getSecKey());
            requestVo.setP3_hmac(hmac);
            String jsonStr = JSONObject.toJSONString(requestVo);
            log.info("请求参数JSON:"+jsonStr);

            //发起请求
            String resJson = callApi(paramsEntity.getUrl()+URL_SINGLEPAY_QUERY, jsonStr, appId, paramsEntity.getPublicKey(), paramsEntity.getPrivateKey());
            resVo = JSONObject.parseObject(resJson, SinglePayQueryRespVo.class);
            log.info("返回结果JSON:"+JSON.toJSONString(resVo));
        }catch (Exception e){
            log.error("纳呗单笔出款查询请求异常{}",e.getMessage());
            e.printStackTrace();
        }
        return resVo;
    }

    @Override
    public APIH5SignRespVo h5Sign(OemParamsEntity paramsEntity, String orderNo, String accountName, String idcardNo, int accountType, String accountNo) {
        APIH5SignRespVo resVo = new APIH5SignRespVo();
        try{
            // 解析paramValues，配置样例：{"appId": "M190724072"}
            JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
            String appId = params.getString("appId");

            //组装请求参数
            H5SignRequestVo requestVo = new H5SignRequestVo();
            requestVo.setP1_taxNo(paramsEntity.getAccount());
            requestVo.setP2_orderNo(orderNo);
            requestVo.setP3_accountName(accountName);
            requestVo.setP4_idcardNo(idcardNo);
            requestVo.setP5_accountType("1");
            requestVo.setP6_accountNo(accountNo);
            // requestVo.setP7_mobile(); 签约时验证短信的手机号，不传时用户可在签约界面手动输入可接收短信的手机号
            // requestVo.setP8_notifyUrl(""); 签约成功后接收通知结果的地址
            requestVo.setP9_platType("wxApp");

            //签名
            String hmac = NabeiAPIUtil.sign(requestVo, paramsEntity.getSecKey());
            requestVo.setP_hmac(hmac);
            String jsonStr = JSONObject.toJSONString(requestVo);
            log.info("请求参数JSON:"+jsonStr);

            //发起请求
            String resJson = callApi(paramsEntity.getUrl()+URL_H5_SIGN, jsonStr, appId, paramsEntity.getPublicKey(), paramsEntity.getPrivateKey());
            resVo = JSONObject.parseObject(resJson, APIH5SignRespVo.class);
            log.info("返回结果JSON:"+JSON.toJSONString(resVo));
        }catch (Exception e){
            log.error("纳呗签约申请请求异常{}",e.getMessage());
            e.printStackTrace();
        }
        return resVo;
    }

    @Override
    public NabeiAPIBaseResp submitAchievements(OemParamsEntity paramsEntity, String realName, String idCardNo, String orderNo, String amount, List<AchievementExcelVo> profitsDetailList) {
        NabeiAPIBaseResp resVo = new NabeiAPIBaseResp();
        try{
            // 解析paramValues，配置样例：{"appId": "M190724072"}
            JSONObject params = JSONObject.parseObject(paramsEntity.getParamsValues());
            String appId = params.getString("appId");
            String taskNo = params.getString("taskNo");

            //组装请求参数
            NabeiAchievementsRequestVo requestVo = new NabeiAchievementsRequestVo();
            requestVo.setP1_taxNo(paramsEntity.getAccount());
            requestVo.setP2_name(realName);
            requestVo.setP3_idcardNo(idCardNo);
            requestVo.setP4_payOrderNo(orderNo);
            requestVo.setP5_taskNo(taskNo);
            requestVo.setP6_submitCount(profitsDetailList.size() + "");
            requestVo.setP7_submitAmount(amount);
            requestVo.setP8_submitDate(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            requestVo.setP9_remark("");

            //签名
            String hmac = NabeiAPIUtil.sign(requestVo, paramsEntity.getSecKey());
            requestVo.setP_hmac(hmac);
            String jsonStr = JSONObject.toJSONString(requestVo);
            log.info("请求参数JSON:"+jsonStr);

            // 生成业绩证明excel文件
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(),AchievementExcelVo.class,profitsDetailList);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            baos.close();
            byte[] bytes = baos.toByteArray();
            String achievements = Base64.getEncoder().encodeToString(bytes);
            JSONObject fileObj = new JSONObject();
            fileObj.put("fileName", "业务证据.xls");
            fileObj.put("achievements", achievements);
            //发起请求
            String resJson = callApi(paramsEntity.getUrl()+URL_ACHIEVEMENTS, jsonStr, appId, paramsEntity.getPublicKey(), paramsEntity.getPrivateKey(), fileObj.toJSONString());
            resVo = JSONObject.parseObject(resJson, NabeiAPIBaseResp.class);
            log.info("返回结果JSON:"+JSON.toJSONString(resVo));
        }catch (Exception e){
            log.error("纳呗提交付款业绩成果凭证请求异常{}",e.getMessage());
            e.printStackTrace();
        }
        return resVo;
    }

    protected static String callApi(String url, String data, String appId, String rpyPublicKey, String mchntPrivateKey) throws Exception{
        TreeMap<String, String> paramMap = new TreeMap();
        paramMap.put("appId", appId);
        //加密
        if(StringUtils.isNotBlank(data)) {
            data = SM2Utils.encrypt(Utils.hexToByte(rpyPublicKey), data.getBytes());
            paramMap.put("data", data);
        }
        //发起请求
        String res = null;
        if(url.toLowerCase().startsWith("https")) {
            res = HttpsUtil.httpMethodPost(url, paramMap,null,false);
        }else {
            res = HttpUtil.httpPost(url, paramMap);
        }
        log.info("res:"+res);
        if(res == null){
            return null;
        }
        if(res.indexOf("p1_resCode")!=-1){
            //此情况为商户密钥信息在平台不存在，无法进行签名加密，故返回的是明文返回。
            log.info("处理异常!!!");
            return null;
        }
        //数据解密
        byte[] pk = Utils.hexToByte(mchntPrivateKey);
        byte[] resb = Utils.hexToByte(res.trim());
        String resJson = new String(SM2Utils.decrypt(pk, resb));
        return resJson;
    }

    protected static String callApi(String url, String data, String appId, String rpyPublicKey, String mchntPrivateKey, String file) throws Exception{
        TreeMap<String, String> paramMap = new TreeMap();
        paramMap.put("appId", appId);
        //加密
        if(StringUtils.isNotBlank(data)) {
            data = SM2Utils.encrypt(Utils.hexToByte(rpyPublicKey), data.getBytes());
            paramMap.put("data", data);
        }
        if (StringUtil.isNotBlank(file)) {
            paramMap.put("files", file);
        }
        //发起请求
        String res = null;
        if(url.toLowerCase().startsWith("https")) {
            res = HttpsUtil.httpMethodPost(url, paramMap,null,false);
        }else {
            res = HttpUtil.httpPost(url, paramMap);
        }
        log.info("res:"+res);
        if(res == null){
            return null;
        }
        if(res.indexOf("p1_resCode")!=-1){
            //此情况为商户密钥信息在平台不存在，无法进行签名加密，故返回的是明文返回。
            log.info("处理异常!!!");
            return null;
        }
        //数据解密
        byte[] pk = Utils.hexToByte(mchntPrivateKey);
        byte[] resb = Utils.hexToByte(res.trim());
        String resJson = new String(SM2Utils.decrypt(pk, resb));
        return resJson;
    }
}
