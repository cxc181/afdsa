package com.yuqian.itax.system.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.OssCallbackParam;
import com.yuqian.itax.system.entity.OssCallbackResult;
import com.yuqian.itax.system.entity.OssPolicyResult;
import com.yuqian.itax.system.entity.dto.UploadFileDTO;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OcrService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.util.util.ImageUtils;
import com.yuqian.itax.util.util.OssUtil;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * OSS 文件service impl
 */
@Slf4j
@Service("ossService")
public class OssServiceImpl implements OssService {
	@Autowired
	protected DictionaryService sysDictionaryService;
	@Autowired
	private OcrService ocrService;
	@Autowired
	private OemParamsService oemParamsService;

	/**
	 * 签名生成
	 */
	@Override
	public OssPolicyResult policy(String oemCode, String isPublicBucket) {
		OSSClient ossClient = new OSSClient(sysDictionaryService.getByCode("oss_endpoint").getDictValue(), sysDictionaryService.getByCode("oss_accessKeyId").getDictValue(), sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue());
		OssPolicyResult result = new OssPolicyResult();
		// 存储目录
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dir = oemCode+"/"+sdf.format(new Date());
		// 签名有效期
		long expireEndTime = System.currentTimeMillis() + Integer.valueOf(sysDictionaryService.getByCode("oss_expireTime").getDictValue()) * 1000;
		Date expiration = new Date(expireEndTime);
		// 文件大小
		long maxSize = Integer.valueOf(sysDictionaryService.getByCode("oss_maxFileSize").getDictValue()) * 1024L * 1024L;
		// 回调
		OssCallbackParam callback = new OssCallbackParam();
		callback.setCallbackUrl(sysDictionaryService.getByCode("oss_callback").getDictValue());
		callback.setCallbackBody("filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
		callback.setCallbackBodyType("application/x-www-form-urlencoded");
		// 提交节点
		String bucketName = "oss_privateBucketName";
		if (StringUtil.isNotBlank(isPublicBucket) && "1".equals(isPublicBucket)) {
			bucketName = "oss_publicBucketName";
		}
		String action = "https://" + sysDictionaryService.getByCode(bucketName).getDictValue() + "." + sysDictionaryService.getByCode("oss_endpoint").getDictValue();
		try {
			PolicyConditions policyConds = new PolicyConditions();
			policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxSize);
			policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
			String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
			byte[] binaryData = postPolicy.getBytes("utf-8");
			String policy = BinaryUtil.toBase64String(binaryData);
			String signature = ossClient.calculatePostSignature(postPolicy);
			String callbackData = BinaryUtil.toBase64String(JSONUtil.parse(callback).toString().getBytes("utf-8"));
			// 返回结果
			result.setAccessKeyId(ossClient.getCredentialsProvider().getCredentials().getAccessKeyId());
			result.setPolicy(policy);
			result.setSignature(signature);
			result.setDir(dir);
			result.setCallback(callbackData);
			result.setHost(action);
		} catch (Exception e) {
			log.error("签名生成失败", e.getMessage());
		} finally {
			// 关闭OSSClient
			if(null != ossClient){
				ossClient.shutdown();
			}
		}
		return result;
	}

	@Override
	public OssCallbackResult callback(HttpServletRequest request) {
		// 获取访问地址
		OSSClient ossClient = new OSSClient(sysDictionaryService.getByCode("oss_endpoint").getDictValue(), sysDictionaryService.getByCode("oss_accessKeyId").getDictValue(), sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue());
		OssCallbackResult result= new OssCallbackResult();
		String filename = request.getParameter("filename");
		try {
			// 签名有效期
			long expireEndTime = System.currentTimeMillis() + Integer.valueOf(sysDictionaryService.getByCode("oss_expireTime").getDictValue()) * 1000;
			Date expiration = new Date(expireEndTime);
			GeneratePresignedUrlRequest generatePresignedUrlRequest ;
			generatePresignedUrlRequest =new GeneratePresignedUrlRequest(sysDictionaryService.getByCode("oss_privateBucketName").getDictValue(), filename);
			generatePresignedUrlRequest.setExpiration(expiration);
			URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
			// 返回结果
			result.setFilename(url.toString());
			result.setSize(request.getParameter("size"));
			result.setMimeType(request.getParameter("mimeType"));
			result.setWidth(request.getParameter("width"));
			result.setHeight(request.getParameter("height"));
		} catch (Exception e) {
			log.error("上传成功回调失败", e.getMessage());
		} finally {
			// 关闭OSSClient
			if(null != ossClient){
				ossClient.shutdown();
			}
		}

		return result;
	}

	@Override
	public String getUrl(String key, int isPublicBucket) {
		String bucketName = "oss_privateBucketName";
		if (isPublicBucket == 1) {
			bucketName = "oss_publicBucketName";
		}
		// 获取访问地址
		OSSClient ossClient = new OSSClient("https://" + sysDictionaryService.getByCode("oss_endpoint").getDictValue(), sysDictionaryService.getByCode("oss_accessKeyId").getDictValue(), sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue());
		String filename = "";
		try {
			String[] split = key.split(",");
			for (int i = 0; i < split.length; i++) {
				long expireEndTime = System.currentTimeMillis() + Integer.valueOf(sysDictionaryService.getByCode("oss_expireTime").getDictValue()) * 1000;
				Date expiration = new Date(expireEndTime);
				GeneratePresignedUrlRequest generatePresignedUrlRequest ;
				generatePresignedUrlRequest =new GeneratePresignedUrlRequest(sysDictionaryService.getByCode(bucketName).getDictValue(), split[i]);
				generatePresignedUrlRequest.setExpiration(expiration);
				URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
				filename += url.toString() + ",";
			}
		} catch (Exception e) {
			log.error("获取私有域图片访问地址失败", e.getMessage());
		} finally {
			// 关闭OSSClient
			if(null != ossClient){
				ossClient.shutdown();
			}
		}
		return filename;
	}

	@Override
	public void deleteObject(String key) {
		// 获取访问地址
		OSSClient ossClient = new OSSClient("https://" + sysDictionaryService.getByCode("oss_endpoint").getDictValue(), sysDictionaryService.getByCode("oss_accessKeyId").getDictValue(), sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue());
		try {
			ossClient.deleteObject(sysDictionaryService.getByCode("oss_privateBucketName").getDictValue(), key);
		} catch (Exception e){
			log.error("删除单个文件失败", e.getMessage());
		} finally {
			if(null != ossClient){
				ossClient.shutdown();
			}
		}
	}

	@Override
	public boolean upload(String saveFileName, byte[] data) {
		String accessId = sysDictionaryService.getByCode("oss_accessKeyId").getDictValue();
		String accessKey = sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue();
		String endpoint = sysDictionaryService.getByCode("oss_endpoint").getDictValue();
		String bucket = sysDictionaryService.getByCode("oss_privateBucketName").getDictValue();
		String page = sysDictionaryService.getByCode("oss_page").getDictValue();
		return OssUtil.upload(endpoint, accessId, accessKey, bucket, page, saveFileName, data);
	}

	@Override
	public boolean uploadBase64(String saveFileName,String filePath,String bucketName, String base64Str) {
		String accessId = sysDictionaryService.getByCode("oss_accessKeyId").getDictValue();
		String accessKey = sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue();
		String endpoint = sysDictionaryService.getByCode("oss_endpoint").getDictValue();
		return OssUtil.uploadBase64(endpoint, accessId, accessKey, base64Str,bucketName, filePath, saveFileName);
	}

	@Override
	public Map<String, Object> ocrIdentify(String oemCode, UploadFileDTO fileDto) throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(StringUtils.isBlank(fileDto.getFileUrl())){
			throw new BusinessException("OCR识别失败，文件路径不能为空");
		}

		// 读取OCR识别渠道相关配置 paramsType=7
		OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 7);
		if (null == paramsEntity) {
			throw new BusinessException("未配置OCR识别渠道相关信息！");
		}

		// 调用OCR识别接口
		String result = null;
		if(fileDto.getType() == 1){
			result = this.ocrService.idCardFront(paramsEntity, ImageUtils.encodeImageToBase64(new URL(fileDto.getFileUrl())));
		}else if(fileDto.getType() == 2){
			result = this.ocrService.idCardBack(paramsEntity, ImageUtils.encodeImageToBase64(new URL(fileDto.getFileUrl())));
		}else{
			throw new BusinessException("OCR识别失败，未知识别类型");
		}

		// 解析并返回结果
		if(StringUtils.isNotBlank(result)){
			JSONObject resultObj = JSON.parseObject(result);
			if("00".equals(resultObj.getString("code"))){
				resultMap.put("ocrData",resultObj.getString("data"));
			} else {
				throw new BusinessException("身份证OCR识别失败：" + resultObj.getString("msg"));
			}
		} else {
			throw new BusinessException("身份证OCR识别失败：识别结果为空");
		}
		return resultMap;
	}

	@Override
	public Map<String, Object> ocrBusinessLicense(String oemCode, String fileUrl) throws Exception {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(StringUtils.isBlank(fileUrl)){
			throw new BusinessException("OCR识别失败，文件路径不能为空");
		}
		// 读取OCR识别渠道相关配置 paramsType=31
		OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 31);
		if (null == paramsEntity) {
			throw new BusinessException("未配置OCR识别渠道相关信息！");
		}
		String result = this.ocrService.ocrBusinessLicense(paramsEntity, fileUrl);
		// 解析并返回结果
		if(StringUtils.isNotBlank(result)){
			JSONObject resultObj = JSON.parseObject(result);
			if("00".equals(resultObj.getString("code"))){
				resultMap.put("ocrData",resultObj.getString("data"));
			} else {
				throw new BusinessException("营业执照OCR识别失败：" + resultObj.getString("msg"));
			}
		} else {
			throw new BusinessException("营业执照OCR识别失败：识别结果为空");
		}
		return resultMap;
	}

	@Override
	public String getPrivateImgUrl(String fileName) {
		if (StringUtils.isBlank(fileName)) {
			return null;
		}
		String accessId = sysDictionaryService.getByCode("oss_accessKeyId").getDictValue();
		String accessKey = sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue();
		String endpoint = sysDictionaryService.getByCode("oss_endpoint").getDictValue();
		String bucket = sysDictionaryService.getByCode("oss_privateBucketName").getDictValue();
		String page = sysDictionaryService.getByCode("oss_page").getDictValue();
		String expireTime = sysDictionaryService.getByCode("oss_expireTime").getDictValue();
//		String style = sysDictionaryService.getByCode("oss_style").getDictValue();
		String host = sysDictionaryService.getByCode("oss_accessHost").getDictValue();
		String reqHead = sysDictionaryService.getByCode("oss_req_head").getDictValue();
		while (StringUtils.isNotBlank(page) && StringUtils.startsWithIgnoreCase(page, "/")) {
			page = page.substring(1);
		}
		return OssUtil.getPrivateImgUrl(accessId, accessKey, endpoint, bucket, Long.parseLong(expireTime), page + fileName, null, reqHead + host);
	}

	@Override
	public String getPrivateVideoUrl(String fileName) {
		if (StringUtils.isBlank(fileName)) {
			return null;
		}
		String accessId = sysDictionaryService.getByCode("oss_accessKeyId").getDictValue();
		String accessKey = sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue();
		String endpoint = sysDictionaryService.getByCode("oss_endpoint").getDictValue();
		String bucket = sysDictionaryService.getByCode("oss_privateBucketName").getDictValue();
		String page = sysDictionaryService.getByCode("oss_page").getDictValue();
		String expireTime = sysDictionaryService.getByCode("oss_expireTime").getDictValue();
		String style = "";
		String host = sysDictionaryService.getByCode("oss_accessHost").getDictValue();
		String reqHead = sysDictionaryService.getByCode("oss_req_head").getDictValue();
		while (StringUtils.isNotBlank(page) && StringUtils.startsWithIgnoreCase(page, "/")) {
			page = page.substring(1);
		}
		return OssUtil.getPrivateImgUrl(accessId, accessKey, endpoint, bucket, Long.parseLong(expireTime), page + fileName, style, reqHead + host);
	}

	@Override
	public boolean uploadPublic(String saveFileName, byte[] data) {
		String accessId = sysDictionaryService.getByCode("oss_accessKeyId").getDictValue();
		String accessKey = sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue();
		String endpoint = sysDictionaryService.getByCode("oss_endpoint").getDictValue();
		String bucket = sysDictionaryService.getByCode("oss_publicBucketName").getDictValue();
		String page = sysDictionaryService.getByCode("oss_page").getDictValue();
		return OssUtil.upload(endpoint, accessId, accessKey, bucket, page, saveFileName, data);
	}

	@Override
	public void deletePublicObject(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		String pubHost = sysDictionaryService.getByCode("oss_access_public_host").getDictValue();
		if (key.contains(pubHost)) {
			key = key.substring(key.lastIndexOf(pubHost) + pubHost.length());
		}
		deleteObject(key, sysDictionaryService.getByCode("oss_publicBucketName").getDictValue());
	}

	@Override
	public void deleteObject(String key, String bucketName) {
		// 获取访问地址
		String accessId = sysDictionaryService.getByCode("oss_accessKeyId").getDictValue();
		String accessKey = sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue();
		String endpoint = sysDictionaryService.getByCode("oss_endpoint").getDictValue();
		OSS ossClient = null;
		try {
			ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
			ossClient.deleteObject(bucketName, key);
		} catch (Exception e){
			log.error("删除单个文件失败");
			log.error(e.getMessage(), e);
		} finally {
			if(null != ossClient){
				ossClient.shutdown();
			}
		}
	}

	@Override
	public boolean doesObjectExist(String key, String bucketName) {
		String accessId = sysDictionaryService.getByCode("oss_accessKeyId").getDictValue();
		String accessKey = sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue();
		String endpoint = sysDictionaryService.getByCode("oss_endpoint").getDictValue();
		return OssUtil.doesObjectExist(accessId, accessKey, endpoint, bucketName, key);
	}

	@Override
	public boolean doesObjectExistPublic(String key) {
		String bucket = Optional.ofNullable(sysDictionaryService.getByCode("oss_publicBucketName")).map(DictionaryEntity::getDictValue).orElse(null);
		return doesObjectExist(key, bucket);
	}

	@Override
	public boolean doesObjectExistPrivate(String key) {
		String bucket = Optional.ofNullable(sysDictionaryService.getByCode("oss_privateBucketName")).map(DictionaryEntity::getDictValue).orElse(null);
		return doesObjectExist(key, bucket);
	}

	@Override
	public byte[] download(String key, String bucketName) {
		String accessId = sysDictionaryService.getByCode("oss_accessKeyId").getDictValue();
		String accessKey = sysDictionaryService.getByCode("oss_accessKeySecret").getDictValue();
		String endpoint = sysDictionaryService.getByCode("oss_endpoint").getDictValue();
		String bucket = sysDictionaryService.getByCode(bucketName).getDictValue();
		return OssUtil.downFile(endpoint, accessId, accessKey, bucket, key);
	}

}