package com.yuqian.itax.util.util.channel;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BWElectricInvoiceUtil {
	
	public static void main(String[] args) {
		//发票开具
		invoiceIssue(null,null);
		//领用存信息查询
		queryInventory(null,null);
		//发票打印
		printInvoice(null,null);
		//版式文件生成
		formatCreate(null,null);
		//订单查询
		queryOrder("fdafdsf",null);
		
	}

	/**
	 *  订单查询
	 * @param orderNo 订单号
	 * @param oemParams oem参数列表 account、secKey、url、keyNum、publicKey
	 */
	public static JSONObject queryOrder(String orderNo,Map<String,String> oemParams) {
		Map<String, Object> orderMap = new HashMap<String, Object>();
		orderMap.put("orderNo", orderNo);
		//解决url被覆盖拼接的问题
		Map<String,String> oemParamsNew = new HashMap<>();
		oemParamsNew.putAll(oemParams);
		oemParamsNew.put("url",oemParams.get("url")+ElectricInvoiceConfigConstants.QUERYINVOICEORDER);
		try {
			String result = requestWithSign(orderMap, oemParamsNew);
			return JSONObject.parseObject(result);
		} catch (Exception e) {			
			log.error(e.getMessage());
			return getFailResult();
		}
	}

	/**
	 *  版式文件生成
	 * @param orderMap 订单数据 taxNo、serialNo、email
	 * @param oemParams oem参数列表 account、secKey、url、keyNum、publicKey
	 */
	public static JSONObject formatCreate(Map<String, Object> orderMap,Map<String,String> oemParams) {
		//解决url被覆盖拼接的问题
		Map<String,String> oemParamsNew = new HashMap<>();
		oemParamsNew.putAll(oemParams);
		oemParamsNew.put("url",oemParams.get("url")+ElectricInvoiceConfigConstants.BWFORMATCREATE);
		try {
			String result = requestWithSign(orderMap, oemParamsNew);
			return JSONObject.parseObject(result);
		} catch (Exception e) {
			log.error(e.getMessage());
			return getFailResult();
		}
	}

	/**
	 * 打印发票
	 * @param orderMap 订单数据 taxNo、invoiceTerminalCode、invoiceTypeCode、invoiceCode、invoiceNo
	 * @param oemParams oem参数列表 account、secKey、url、keyNum、publicKey
	 */
	public static JSONObject printInvoice(Map<String, Object> orderMap,Map<String,String> oemParams) {
		//解决url被覆盖拼接的问题
		Map<String,String> oemParamsNew = new HashMap<>();
		oemParamsNew.putAll(oemParams);
		oemParamsNew.put("url",oemParams.get("url")+ElectricInvoiceConfigConstants.BWPRINTINVOICE);
		try {
			String result = requestWithSign(orderMap, oemParamsNew);
			return JSONObject.parseObject(result);
		} catch (Exception e) {
			log.error(e.getMessage());
			return getFailResult();
		}
	}

	/**
	 * 领用存信息查询
	 * @param orderMap 订单数据 taxNo、invoiceTerminalCode、invoiceTypeCode
	 * @param oemParams oem参数列表 account、secKey、url、keyNum、publicKey
	 */
	public static JSONObject queryInventory(Map<String, Object> orderMap,Map<String,String> oemParams) {
		//解决url被覆盖拼接的问题
		Map<String,String> oemParamsNew = new HashMap<>();
		oemParamsNew.putAll(oemParams);
		oemParamsNew.put("url",oemParams.get("url")+ElectricInvoiceConfigConstants.BWQUERYINVENTORY);
		try {
			String result = requestWithSign(orderMap, oemParamsNew);
			return JSONObject.parseObject(result);
		} catch (Exception e) {
			log.error(e.getMessage());
			return getFailResult();
		}
	}

	/**
	 * 发票开具
	 * @param orderMap 订单数据 taxNo、invoiceTerminalCode、invoiceTypeCode
	 * @param oemParams oem参数列表 account、secKey、url、keyNum、publicKey
	 */
	public static JSONObject invoiceIssue(Map<String, Object> orderMap,Map<String,String> oemParams) {
		//解决url被覆盖拼接的问题
		Map<String,String> oemParamsNew = new HashMap<>();
		oemParamsNew.putAll(oemParams);
		oemParamsNew.put("url",oemParams.get("url")+ElectricInvoiceConfigConstants.BWINVOICEISSUE);
		try {
			String result = requestWithSign(orderMap, oemParamsNew);
			return JSONObject.parseObject(result);
		} catch (Exception e) {
			log.error(e.getMessage());
			return getFailResult();
		}
	}

    private static String requestWithSign(Map<String, Object> orderMap,Map<String, String> oemParams) throws Exception {
        log.info("data数据明文：" + JSONUtil.toJsonPrettyStr(orderMap));
        String data = JSONUtil.toJsonStr(orderMap);
		log.info("data加密数据：" + data);
        String enckeyNum = Base64.encode(RSA2Util.encryptByPublicKey(oemParams.get("keyNum").getBytes(), oemParams.get("publicKey")));

        Map<String, Object> pubParamMap = new HashMap<>();
        pubParamMap.put("data", data);
        pubParamMap.put("merNo",oemParams.get("account"));
        pubParamMap.put("keyNum", enckeyNum);
        pubParamMap.put("timestamp", System.currentTimeMillis() + "");
        pubParamMap.put("version", "1.0.0");
        pubParamMap.put("signType", "MD5");

        String sign = RSA2Util.md5sign(pubParamMap, oemParams.get("secKey"));

        pubParamMap.put("sign", sign);
        String jsonParams = JSONUtil.toJsonStr(pubParamMap);
		log.info("平台请求数据:" + JSONUtil.toJsonPrettyStr(jsonParams),"请求地址："+oemParams.get("url"));
		HttpRequest httpRequest = HttpRequest.post(oemParams.get("url"));
		httpRequest.setConnectionTimeout(30000);
		httpRequest.setReadTimeout(30000);
        HttpResponse response = httpRequest.body(jsonParams, "application/json").charset("utf-8").execute();
        String result = response.body();
		log.info("平台返回结果：" + JSONUtil.toJsonPrettyStr(result));

        return result;
    }

    private static JSONObject getFailResult(){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code","-1");
		jsonObject.put("message","请求上游失败，请稍后再试");
		return jsonObject;
	}
}