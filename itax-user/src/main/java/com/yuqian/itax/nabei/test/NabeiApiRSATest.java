package com.yuqian.itax.nabei.test;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Files;
import com.yuqian.itax.nabei.entity.*;
import com.yuqian.itax.util.util.nabei.NabeiAPIUtil;
import com.yuqian.itax.util.util.nabei.RSA2Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class NabeiApiRSATest {
	
	/**
	 * 商户测试公私钥
	 publicKey:MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr2amHnDPcQ3BoVps+Yu99cOXSm8wcf2OPbXMJK+xpG0Hb22yiPYnwRriO/R3/wBc8934TMO5hSF28wKXY/IpaRrBNbRYx2rCL0kNH7QTqJPFyO1FbEu+o10wPwG1Ag2rR/s3PMEgFAAXHDncEw2PHKH3S62hE25uRaXcbTQsBa0dK2H5w+xktu/PNQBdZ94UwZ9A8NQIdfPpaZpEfowuJ7e87W/zBw1Yar1+q5G8SaNi9+9TsffVQIuwG4guauQujpUzmLTKA15c3SfDOlJ9WqNW+TfHX7Ry4woa3NUt1sVgNdWL6pkGxgtEoqDYDASUjYJA60sv85IRrtml0GtrcQIDAQAB
	 privateKey:MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCvZqYecM9xDcGhWmz5i731w5dKbzBx/Y49tcwkr7GkbQdvbbKI9ifBGuI79Hf/AFzz3fhMw7mFIXbzApdj8ilpGsE1tFjHasIvSQ0ftBOok8XI7UVsS76jXTA/AbUCDatH+zc8wSAUABccOdwTDY8cofdLraETbm5FpdxtNCwFrR0rYfnD7GS27881AF1n3hTBn0Dw1Ah18+lpmkR+jC4nt7ztb/MHDVhqvX6rkbxJo2L371Ox99VAi7AbiC5q5C6OlTOYtMoDXlzdJ8M6Un1ao1b5N8dftHLjChrc1S3WxWA11YvqmQbGC0SioNgMBJSNgkDrSy/zkhGu2aXQa2txAgMBAAECggEAecetQqlUfcvtSlf8HW62nzIDkryMSDihT5svd7E8h0rjxKvHL2yowVwnAzWDMttPrknJIZMq9ZH8glCkR9JUihL3XpAuq5Yl6Kqxn/dpRrwCsLR8ZOpaHBaRbs2CnUBcUYfMHDSCTyCY6xIJCHl7iYeyqSqOEW20xkqYNoHopq8ZtlRXxGhxEl4ECnGCgIIFKOSqltw2DKdPi9bhILrCOKrGS2G71vpNZQR+Y+aM8J3rA6po/UM6+2StMPeboWFMop8jEwLsK36jSQdsSPjUjLEP8xjd5RIVgKzFuS63xzeJ4PtyY6L2NRrgrAheKhzmovHLDEvRMnVbE3XhtmFxOQKBgQDmJuXtW0PIWb+oyFlWK+knSgthwQEH7deZiDLD3lrS/sSFmOOSyoiISh/LEz8DjfiLLHwHW+En5s5qjdC5/C8iQ9yD0OAT7JfXpB59DyvKCIYurUAdz3h30DzHmJRIrhqcRLr/3JLLkB0nCthvFOjxKr7jfhXiZjDAY62d9QTCDwKBgQDDGZrPxCZHZMnag57uvOp3Am5wQOaQi124YotElf+3kRKjZWGtkbKS5X9QAqbQ894WeNyxwTKR8qMKFmHJvdELJQQzxMgUYWg43ew5ge77ZOVmHaBFk4Ujwd/SXG6ecFYkbUxxb+ZsC4lroibN1skWZVwoyb3rXRI0oSrmHCx6fwKBgGHvy4flfkKTbmt0Vg/FP8HoUE+YwceN/mDnBDM1O5DtuNLg7HZamR7l7AGOfxIUJ9+RXnYDMxy3qr7M+jUqiyQZnIy7JZ9U3oh03kR5YDjox7C1KMOGtczGYBbPZspGzCwTUz/trMnS2ZTzmIYsb7OwbOqBjO56beRwhkDhMtUhAoGBAIBea4dC53DYNxxIuB0bPiZZ+jJv8YSvvlWguUPCckPk9199SFy83cNbr34EkKAxpTIiCgwHM2/mNzJe8a6c71um2nH5izAluz6XfmOEQ6szguDCo37EBrk1SkCOQtaK0ZMcO0z0NKdom92vDlsndJ4AyTepLgYb8euhE6neZcGfAoGBAOS9HLW9TQ7Kw39UsS27Cv7hhWXXfPd81ZNPmXHD5JRjXenmy0+hKeXhCPP164p39fBQ2dClRH9axzx/WgFaPnF3LVll84/EtgRj/gS4ceCkUPECvJcwRNQYUxxDWiZ+zk+lOY4sba1aJKSJgoIIN2V4+WMua2pEPkjm2U3ddUTV
	 */
	/** 平台系统公钥 */
	public static final String RPY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzDtazP9J2urd9U1D10ZhuzHzDVN6sSdCU/3o2UNmiA+1HQA0eJXTsElwgJoPlnpHW2gS0BhbdRjRpnUPLPclWzwvZyiWzmRY+0i/k/r2yOR6P9xPoJV2ayqfKIxmcghznJOovGVvhPqjgJYRD71Bnkq2i2pVjeWtvvXnxbcdnZGv/UIj2UVvktsoNCBXILEU/zLnoYZE7iOAAeN4kp711rNqcTc6puv1Ej3DwPFojylAbyLRpcGQpt+DNhCW74BfGhImjlKP0/UeMR0Ur65C60GZMn+PGOZpMRNQP4oppxWRcsUP8thantpC9xQfNMIFfnnYSdffX92PRPt+d2z2nwIDAQAB";
	/** 商户私钥 */
	public static final String MCHNT_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCvZqYecM9xDcGhWmz5i731w5dKbzBx/Y49tcwkr7GkbQdvbbKI9ifBGuI79Hf/AFzz3fhMw7mFIXbzApdj8ilpGsE1tFjHasIvSQ0ftBOok8XI7UVsS76jXTA/AbUCDatH+zc8wSAUABccOdwTDY8cofdLraETbm5FpdxtNCwFrR0rYfnD7GS27881AF1n3hTBn0Dw1Ah18+lpmkR+jC4nt7ztb/MHDVhqvX6rkbxJo2L371Ox99VAi7AbiC5q5C6OlTOYtMoDXlzdJ8M6Un1ao1b5N8dftHLjChrc1S3WxWA11YvqmQbGC0SioNgMBJSNgkDrSy/zkhGu2aXQa2txAgMBAAECggEAecetQqlUfcvtSlf8HW62nzIDkryMSDihT5svd7E8h0rjxKvHL2yowVwnAzWDMttPrknJIZMq9ZH8glCkR9JUihL3XpAuq5Yl6Kqxn/dpRrwCsLR8ZOpaHBaRbs2CnUBcUYfMHDSCTyCY6xIJCHl7iYeyqSqOEW20xkqYNoHopq8ZtlRXxGhxEl4ECnGCgIIFKOSqltw2DKdPi9bhILrCOKrGS2G71vpNZQR+Y+aM8J3rA6po/UM6+2StMPeboWFMop8jEwLsK36jSQdsSPjUjLEP8xjd5RIVgKzFuS63xzeJ4PtyY6L2NRrgrAheKhzmovHLDEvRMnVbE3XhtmFxOQKBgQDmJuXtW0PIWb+oyFlWK+knSgthwQEH7deZiDLD3lrS/sSFmOOSyoiISh/LEz8DjfiLLHwHW+En5s5qjdC5/C8iQ9yD0OAT7JfXpB59DyvKCIYurUAdz3h30DzHmJRIrhqcRLr/3JLLkB0nCthvFOjxKr7jfhXiZjDAY62d9QTCDwKBgQDDGZrPxCZHZMnag57uvOp3Am5wQOaQi124YotElf+3kRKjZWGtkbKS5X9QAqbQ894WeNyxwTKR8qMKFmHJvdELJQQzxMgUYWg43ew5ge77ZOVmHaBFk4Ujwd/SXG6ecFYkbUxxb+ZsC4lroibN1skWZVwoyb3rXRI0oSrmHCx6fwKBgGHvy4flfkKTbmt0Vg/FP8HoUE+YwceN/mDnBDM1O5DtuNLg7HZamR7l7AGOfxIUJ9+RXnYDMxy3qr7M+jUqiyQZnIy7JZ9U3oh03kR5YDjox7C1KMOGtczGYBbPZspGzCwTUz/trMnS2ZTzmIYsb7OwbOqBjO56beRwhkDhMtUhAoGBAIBea4dC53DYNxxIuB0bPiZZ+jJv8YSvvlWguUPCckPk9199SFy83cNbr34EkKAxpTIiCgwHM2/mNzJe8a6c71um2nH5izAluz6XfmOEQ6szguDCo37EBrk1SkCOQtaK0ZMcO0z0NKdom92vDlsndJ4AyTepLgYb8euhE6neZcGfAoGBAOS9HLW9TQ7Kw39UsS27Cv7hhWXXfPd81ZNPmXHD5JRjXenmy0+hKeXhCPP164p39fBQ2dClRH9axzx/WgFaPnF3LVll84/EtgRj/gS4ceCkUPECvJcwRNQYUxxDWiZ+zk+lOY4sba1aJKSJgoIIN2V4+WMua2pEPkjm2U3ddUTV";
	/** 商户签名密钥 */
	public static final String APP_KEY = "cerGsKrjPiNpdGqceXG6kw3pehCoipFM";
	public static final String APP_ID = "M190630004";
	public static final String SERVER_ID = "T190623007";

	private static final String API_ADDRESS = "https://ceshi.inabei.cn/gateway";
//	private static final String API_ADDRESS = "http://172.16.100.42:9093/gateway";
	
	private static final String URL_SIGNREG = API_ADDRESS+"/v1/signRegister";
	private static final String URL_SIGNQRY = API_ADDRESS+"/v1/signQuery";
	private static final String URL_PERSONBAL = API_ADDRESS+"/v1/personBal";
	private static final String URL_REMIT = API_ADDRESS+"/v1/remit";
	private static final String URL_CHANGE_BINDCARD = API_ADDRESS+"/v1/changeBindCard";
	private static final String URL_MCHNT_BAL = API_ADDRESS + "/v1/merchantAccount";
	private static final String URL_SINGLEPAY = API_ADDRESS +"/v1/singlePay";
	private static final String URL_SINGLEPAY_QUERY = API_ADDRESS +"/v1/singlePayQuery";
	
	public static void main(String[] args){
		try {
			merchantAccount();
//		signRegister();
//		signQuery();
//		personBal();
//		remit();
//		changeBindCard();
//		singlePay();
//		singlePayQuery();
//		callBack();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
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
	
	public static void signQuery()throws Exception{
		APISignQueryRequest requestVo = new APISignQueryRequest();
		requestVo.setP1_taxNo(SERVER_ID);
		requestVo.setP2_idcardNo("513436201801012901");
		
		//签名
		String hmac = NabeiAPIUtil.sign(requestVo, APP_KEY);
		requestVo.setP3_hmac(hmac);
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
	
	public static void merchantAccount() throws Exception{
		//发起请求
		String resJson = callApi(URL_MCHNT_BAL, null);
		MerchantBalanceQueryRespVo resVo = JSONObject.parseObject(resJson, MerchantBalanceQueryRespVo.class);
		System.out.println(JSON.toJSONString(resVo));
		//验证签名
		boolean b = NabeiAPIUtil.verify(resVo,APP_KEY,resVo.getP7_hmac());
		System.out.println("响应签名验证状态 ："+b);
	}
	
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

	
	protected static String callApi(String url,String data) throws Exception{
//		TreeMap<String, String> paramMap = new TreeMap();
//		paramMap.put("appId", APP_ID);
//		//加密 - 平台公钥加密
//		if(StringUtils.isNotBlank(data)) {
//			data = RSA2Utils.encrypt(data, RPY_PUBLIC_KEY);
//			paramMap.put("data", data);
//		}
//		System.out.println("请求密文:"+data);
//		//发起请求
//		String res = null;
//		if(url.toLowerCase().startsWith("https")) {
//			res = HttpsUtil.httpMethodPost(url, paramMap,null,false);
//		}else {
//			res = HttpUtil.httpPost(url, paramMap);
//		}
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appId", APP_ID);
		//加密 - 平台公钥加密
		if(StringUtils.isNotBlank(data)) {
			data = RSA2Utils.encrypt(data, RPY_PUBLIC_KEY);
			paramMap.put("data", data);
		}
		System.out.println("请求密文:"+data);
		HttpResponse response = HttpRequest.post(url).form(paramMap).timeout(15000).charset("utf-8").execute();
		String res = response.body();
		System.out.println("res:"+res+"|");
		if(res == null){
			return null;
		}
		if(res.indexOf("p1_resCode")!=-1){
			//此情况为商户密钥信息在平台不存在，无法进行签名加密，故返回的是明文返回。
			System.out.println("处理异常!!!");
			return null;
		}
		//数据解密
		String resJson = RSA2Utils.decrypt(res.trim(), MCHNT_PRIVATE_KEY);
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
		String res  = "XwhLdlDmwk18Z2VldEl_YS64dmgdGprClignn2jsa9XaBGXSWWobCeDrVUVfTZobzuSH8WZDlP8acssHG_Ty-FDfqt_abFkm2B3xorJQRHYQ9P6GMkQ_xEUtN9CHM8tzWocmhIQmV56a3jY32Neu0dBh8sTHdlb1saopYWvJ8PgRzjUjWJc0_oPWxS8bNdFk8mPe1ann2qrtFSLWUgpOb86mdNVR6QBVAr2b8LWkKPLYAN0GvjrWYjBK7J2U7N4QukEVqMzTYV3SB5oZ0O87RMb2W3DZTabdAoY7XhTLTCOttp35dCuMj3MosPL-bPO7Yipu-XkftYGKyouFlDxXuA";
		//数据解密
		String resJson = RSA2Utils.decrypt(res, MCHNT_PRIVATE_KEY);
		return resJson ;
	}
}
