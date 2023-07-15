package com.yuqian.itax.nabei.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Files;
import com.yuqian.itax.nabei.entity.*;
import com.yuqian.itax.util.util.HttpUtil;
import com.yuqian.itax.util.util.HttpsUtil;
import com.yuqian.itax.util.util.Utils;
import com.yuqian.itax.util.util.nabei.NabeiAPIUtil;
import com.yuqian.itax.util.util.nabei.SM2Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Base64;
import java.util.TreeMap;

@Slf4j
public class NabeiApiTest {
	/** 平台系统公钥 */
	public static final String RPY_PUBLIC_KEY = "043E3F3AB5433CC46970DE61674CDD99DC42D763295F4FA0DEBAACC878EE9CE2BC1B54A7DD57F0473E71F9DB2522EC3C2A50FA814FE1170BE4C2E94B4E9F9664B4";
	/** 商户公钥 需上传到代付平台 */
	public static final String MCHNT_PUBLIC_KEY = "04D55A888DCC556CA442D03ADE495D44FB52F3255AED8186FE23C9C64FA2A962950DEA621E51716338535A51EF64B8CD1930F41CBD200DBB420F14015DA15DB832";
	/** 商户私钥 */
	public static final String MCHNT_PRIVATE_KEY = "00EAE114A8661FDA385FFFBBC9F8242CA48EA400859D2FABAC7F8897286AABA632";
	/** 商户签名密钥 */
	public static final String APP_KEY = "TOXRyU0HrmOmRDKWA5QKmzlX8oFrG0J9";
	/** API接入的企业编号 **/
	public static final String APP_ID = "M190724072";
	/** 服务商编号 **/
	public static final String SERVER_ID = "T190623007";

	private static final String API_ADDRESS = "https://ceshi.inabei.cn/gateway";
//	private static final String API_ADDRESS = "http://172.16.100.42:9093/gateway";

	/** 2.1 企业账户查询 */
	private static final String URL_MCHNT_BAL = API_ADDRESS + "/v1/merchantAccount";
	/** 2.2 签约注册 */
	private static final String URL_SIGNREG = API_ADDRESS+"/v1/signRegister";
	/** 2.3 签约注册查询 */
	private static final String URL_SIGNQRY = API_ADDRESS+"/v1/signQuery";
	/** 2.4 用户额度查询 */
	private static final String URL_PERSONBAL = API_ADDRESS+"/v1/personBal";
	/** 2.5 单笔出款申请 */
	private static final String URL_SINGLEPAY = API_ADDRESS +"/v1/singlePay";
	/** 2.6 单笔出款查询 */
	private static final String URL_SINGLEPAY_QUERY = API_ADDRESS +"/v1/singlePayQuery";
	/** 2.7 修改签约账户 */
	private static final String URL_CHANGE_BINDCARD = API_ADDRESS+"/v1/changeBindCard";
	/** 2.8 对账文件下载 */
	private static final String URL_REMIT = API_ADDRESS+"/v1/remit";
	
	public static void main(String[] args){
		try {
			//merchantAccount();
			//signRegister();
			signQuery();
			//personBal();
			//singlePay();
			//singlePayQuery();
			//changeBindCard();
			//remit();
		}catch (Exception e){
			log.error(e.getMessage());
		}
	}

	/**
	 * 企业账户查询
	 */
	public static void merchantAccount() throws Exception{
		//发起请求
		String resJson = callApi(URL_MCHNT_BAL, null);
		MerchantBalanceQueryRespVo resVo = JSONObject.parseObject(resJson, MerchantBalanceQueryRespVo.class);
		System.out.println(JSON.toJSONString(resVo));
		//验证签名
		boolean b = NabeiAPIUtil.verify(resVo,APP_KEY,resVo.getP7_hmac());
		System.out.println("响应签名验证状态 ："+b);
	}

	/**
	 * 签约注册
	 */
	public static void signRegister()throws Exception{
		APIRegisterRequestVo requestVo = new APIRegisterRequestVo();
		requestVo.setP1_taxNo(SERVER_ID);
		requestVo.setP2_name("朱诗");
		requestVo.setP3_idcardNo("513436201801012901");
		requestVo.setP4_accountNo("6226660203757382");
		requestVo.setP5_mobile("15523452345");
		
		//签名
		String hmac = NabeiAPIUtil.sign(requestVo, APP_KEY);
		requestVo.setP6_hmac(hmac);
		String jsonStr = JSONObject.toJSONString(requestVo);
		System.out.println("请求参数JSON:"+jsonStr);
		//发起请求
		String resJson = callApi(URL_SIGNREG, jsonStr);
		APIRegisterRespVo resVo = JSONObject.parseObject(resJson, APIRegisterRespVo.class);
		System.out.println(JSON.toJSONString(resVo));
		//验证签名
		boolean b = NabeiAPIUtil.verify(resVo,APP_KEY,resVo.getP9_hmac());
		System.out.println("响应签名验证状态 ："+b);
	}

	/**
	 * 签约注册查询
	 */
	public static void signQuery()throws Exception{
		APISignQueryRequest requestVo = new APISignQueryRequest();
		requestVo.setP1_taxNo(SERVER_ID);
		requestVo.setP2_idcardNo("430602198311152530");

		//签名
		String hmac = NabeiAPIUtil.sign(requestVo, APP_KEY);
		requestVo.setP3_hmac(hmac);
		requestVo.setExtAccountNo("6217002920138327755");
		String jsonStr = JSONObject.toJSONString(requestVo);
		System.out.println("请求参数JSON:"+jsonStr);
		//发起请求
		String resJson = callApi(URL_SIGNQRY, jsonStr);
		APISignQueryRespVo resVo = JSONObject.parseObject(resJson, APISignQueryRespVo.class);
		System.out.println(JSON.toJSONString(resVo));
		//验证签名
		boolean b = NabeiAPIUtil.verify(resVo,APP_KEY,resVo.getP9_hmac());
		System.out.println("响应签名验证状态 ："+b);
	}

	/**
	 * 用户额度查询
	 */
	public static void personBal()throws Exception{
		APIPersonBalQueryRequestVo requestVo = new APIPersonBalQueryRequestVo();
		requestVo.setP1_taxNo(SERVER_ID);
		requestVo.setP2_idcardNo("430528198411243358");
		
		//签名
		String hmac = NabeiAPIUtil.sign(requestVo, APP_KEY);
		requestVo.setP3_hmac(hmac);
		String jsonStr = JSONObject.toJSONString(requestVo);
		System.out.println("请求参数JSON:"+jsonStr);
		//发起请求
		String resJson = callApi(URL_PERSONBAL, jsonStr);
		APIPersonBalQueryRespVo resVo = JSONObject.parseObject(resJson, APIPersonBalQueryRespVo.class);
		System.out.println(JSON.toJSONString(resVo));
		//验证签名
		boolean b = NabeiAPIUtil.verify(resVo,APP_KEY,resVo.getP10_hmac());
		System.out.println("响应签名验证状态 ："+b);
	}

	/**
	 * 单笔出款申请
	 */
	public static void singlePay()throws Exception{
		SinglePayRequestVo requestVo = new SinglePayRequestVo();
		requestVo.setP1_taxNo(SERVER_ID);
		requestVo.setP2_orderNo("TEST2019121200000002");
		requestVo.setP3_accountName("朱诗");
		requestVo.setP4_idcardNo("513436201801012901");
		requestVo.setP5_accountNo("6226660203757382");
		requestVo.setP6_mobile("15523452345");
		requestVo.setP7_amount("1.00");
		requestVo.setP8_remark("备注");
//		requestVo.setP9_notifyUrl("");

		//签名
		String hmac = NabeiAPIUtil.sign(requestVo, APP_KEY);
		requestVo.setP10_hmac(hmac);
		String jsonStr = JSONObject.toJSONString(requestVo);
		System.out.println("请求参数JSON:"+jsonStr);
		//发起请求
		String resJson = callApi(URL_SINGLEPAY, jsonStr);
		SinglePayRespVo resVo = JSONObject.parseObject(resJson, SinglePayRespVo.class);
		System.out.println(JSON.toJSONString(resVo));
		//验证签名
		boolean b = NabeiAPIUtil.verify(resVo,APP_KEY,resVo.getP6_hmac());
		System.out.println("响应签名验证状态 ："+b);
	}

	/**
	 * 单笔出款查询
	 */
	public static void singlePayQuery()throws Exception{
		SinglePayQueryRequestVo requestVo = new SinglePayQueryRequestVo();
		requestVo.setP1_taxNo(SERVER_ID);
		requestVo.setP2_orderNo("TEST2019121200000002");
		//签名
		String hmac = NabeiAPIUtil.sign(requestVo, APP_KEY);
		requestVo.setP3_hmac(hmac);
		String jsonStr = JSONObject.toJSONString(requestVo);
		System.out.println("请求参数JSON:"+jsonStr);
		//发起请求
		String resJson = callApi(URL_SINGLEPAY_QUERY, jsonStr);
		SinglePayQueryRespVo resVo = JSONObject.parseObject(resJson, SinglePayQueryRespVo.class);
		System.out.println(JSON.toJSONString(resVo));
		//验证签名
		boolean b = NabeiAPIUtil.verify(resVo,APP_KEY,resVo.getP10_hmac());
		System.out.println("响应签名验证状态 ："+b);
	}

	/**
	 * 修改签约账户
	 */
	public static void changeBindCard()throws Exception{
		ChangeBindCardRequestVo requestVo = new ChangeBindCardRequestVo();
		requestVo.setP1_taxNo(SERVER_ID);
		requestVo.setP4_orgAccountNo("6226660203757386");
		requestVo.setP5_accountNo("6226660203757385");
		
		requestVo.setP2_accountName("朱诗");
		requestVo.setP3_idcardNo("513436201801012901");
		requestVo.setP4_orgAccountNo("6226660203757382");
		requestVo.setP5_accountNo("6226660203757380");
		requestVo.setP6_mobile("15523452345");
		
		//签名
		String hmac = NabeiAPIUtil.sign(requestVo, APP_KEY);
		requestVo.setP7_hmac(hmac);
		String jsonStr = JSONObject.toJSONString(requestVo);
		System.out.println("请求参数JSON:"+jsonStr);
		//发起请求
		String resJson = callApi(URL_CHANGE_BINDCARD, jsonStr);
		ChangeBindCardRespVo resVo = JSONObject.parseObject(resJson, ChangeBindCardRespVo.class);
		System.out.println(JSON.toJSONString(resVo));
		//验证签名
		boolean b = NabeiAPIUtil.verify(resVo,APP_KEY,resVo.getP3_hmac());
		System.out.println("响应签名验证状态 ："+b);
	}

	/**
	 * 对账文件下载
	 */
	public static void remit()throws Exception{
		APIRemitRequestVo requestVo = new APIRemitRequestVo();
		requestVo.setP1_taxNo(SERVER_ID);
		requestVo.setP2_checkDate("2019-12-04");

		//签名
		String hmac = NabeiAPIUtil.sign(requestVo, APP_KEY);
		requestVo.setP3_hmac(hmac);
		String jsonStr = JSONObject.toJSONString(requestVo);
		System.out.println("请求参数JSON:"+jsonStr);
		//发起请求
		String resJson = callApi(URL_REMIT, jsonStr);
		APIRemitRespVo resVo = JSONObject.parseObject(resJson, APIRemitRespVo.class);
		System.out.println(JSON.toJSONString(resVo));
		//验证签名
		boolean b = NabeiAPIUtil.verify(resVo,APP_KEY,resVo.getP4_hmac());
		System.out.println("响应签名验证状态 ："+b);
		if(!b) {
			return ;
		}
		byte[] data = Base64.getDecoder().decode(resVo.getP3_data());
		Files.write(data, new File("/apps/remit.zip"));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static String callApi(String url,String data) throws Exception{
		TreeMap<String, String> paramMap = new TreeMap();
		paramMap.put("appId", APP_ID);
		//加密
		if(StringUtils.isNotBlank(data)) {
			data = SM2Utils.encrypt(Utils.hexToByte(RPY_PUBLIC_KEY), data.getBytes());
			paramMap.put("data", data);
		}
		//发起请求
		String res = null;
		if(url.toLowerCase().startsWith("https")) {
			res = HttpsUtil.httpMethodPost(url, paramMap,null,false);
		}else {
			res = HttpUtil.httpPost(url, paramMap);
		}
		System.out.println("res:"+res);
		if(res == null){
			return null;
		}
		if(res.indexOf("p1_resCode")!=-1){
			//此情况为商户密钥信息在平台不存在，无法进行签名加密，故返回的是明文返回。
			System.out.println("处理异常!!!");
			return null;
		}
		//数据解密
		byte[] pk = Utils.hexToByte(MCHNT_PRIVATE_KEY);
		byte[] resb = Utils.hexToByte(res.trim());
		String resJson = new String(SM2Utils.decrypt(pk, resb));
		return resJson;
	}

	/**
	 * 通知处理方法
	 * res获取方式：接受地址以注解@RequestBody接收参数，例如：
	  	     @RequestMapping("/callBack")
			 @ResponseBody
			 public String callBack(@RequestBody String data) {
			  logger.info("接收到回调通知:"+data);
			  //业务处理，根据处理结果，返回“SUCCESS”表示已经接收到地址，无需再次接收结果。
			  return "SUCCESS";
			 }
	 * @return
	 * @throws Exception
	 */
	protected static String callBack()throws Exception{
		String res = "通知数据";
		//数据解密
		byte[] pk = Utils.hexToByte(MCHNT_PRIVATE_KEY);
		byte[] resb = Utils.hexToByte(res.trim());
		String resJson = new String(SM2Utils.decrypt(pk, resb));
		return resJson ;
	}
}
