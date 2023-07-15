package com.yuqian.itax.util.util;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.*;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.StringUtils;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@Component
@Slf4j
public class OssUtil {

	/**
	 * 获取OSS图片访问权限
	 * @param accessId OSS配置ID
	 * @param accessKey	OSS配置key
	 * @param endpoint	OSS区域信息
	 * @param bucket	OSS桶名
	 * @param expireTime	有效期时间单位秒
	 * @param fileName		文件名
	 * @param style		文件样式（缩略）
	 * @return
	 */
	public static String getPrivateImgUrl(String accessId,String accessKey,String endpoint, String bucket,long expireTime,String fileName,String style,String host) {
		String imgUrl = "";
		OSS ossClient = null;
		try {
			ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
			Date expiration = new Date(System.currentTimeMillis() + expireTime*1000 );
			GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, fileName, HttpMethod.GET);
			req.setExpiration(expiration);
			req.setProcess(style);
			URL signedUrl = ossClient.generatePresignedUrl(req);
			imgUrl = host + signedUrl.getFile();
		} catch (Exception e) {
			log.error("OSS获取图片访问权限出错：{}",e.getMessage());
		}finally {
			if(null != ossClient) {
				// 关闭OSSClient。
				ossClient.shutdown();
			}
		}
		return imgUrl;
	}

	/**
	 * 判断oss文件是否存在
	 * @param accessId
	 * @param accessKey
	 * @param endpoint
	 * @param bucket
	 * @param fileName
	 * @return
	 */
	public static boolean doesObjectExist(String accessId, String accessKey, String endpoint, String bucket, String fileName) {
		OSS ossClient = null;
		try {
			ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
			return ossClient.doesObjectExist(bucket, fileName);
		} catch (Exception e) {
			log.error("OSS获取图片访问权限出错：{}",e.getMessage());
		}finally {
			if(null != ossClient) {
				// 关闭OSSClient。
				ossClient.shutdown();
			}
		}
		return false;
	}

	/**
	 * 获取OSS签名
	 * @param accessId oss配置ID
	 * @param accessKey	oss配置key
	 * @param endpoint	oss区域信息
	 * @param bucket	oss桶名
	 * @param expireTime 超时时间单位秒
	 * @param maxFileSize 文件最大值单位M
	 * @return
	 */
	public static JSONObject getSign(String accessId,String accessKey, String endpoint,String bucket,long expireTime,long maxFileSize,String fileName) {
		OSSClient client = new OSSClient(endpoint, accessId, accessKey);
		JSONObject respMap = new JSONObject();
		try {
			long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
			Date expiration = new Date(expireEndTime);
			PolicyConditions policyConds = new PolicyConditions();
			policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxFileSize*1024*1024);
			policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, fileName);
			String postPolicy = client.generatePostPolicy(expiration, policyConds);
			byte[] binaryData = postPolicy.getBytes("utf-8");
			String encodedPolicy = BinaryUtil.toBase64String(binaryData);
			String postSignature = client.calculatePostSignature(postPolicy);
			respMap.put("accessId", accessId);
			respMap.put("policy", encodedPolicy);
			respMap.put("signature", postSignature);
		}catch(Exception e) {
			log.info("OSS获取签名错误：{}", e.getMessage());
		}finally {
			if(null != client){
				client.shutdown();
			}
		}
		return respMap;
	}

	/**
	 * 图片下载
	 */
	public static byte[] dowlond(String endpoint,String accessKeyId,String accessKeySecret,String bucketName,String objectName){
		OSS ossClient = null;
		//OSSObject ossObject = ossClient.getObject(bucketName, objectName);
		BufferedReader reader = null;
		StringBuffer fileBuffer = new StringBuffer();
		try {
			ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
			OSSObject ossObject = ossClient.getObject(new GetObjectRequest(bucketName, objectName));
			reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
			while (true) {
				fileBuffer.append(reader.readLine());
				if (null == reader.readLine()) {
					break;
				}
			}
		}catch(Exception e) {
			log.info("OSS文件下载出现异常：{}",e.getMessage());
		}finally {
			if(null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
			if(null != ossClient) {
				ossClient.shutdown();
			}
		}
		return fileBuffer.toString().getBytes();

	}

	/**
	 * 图片上传
	 * @param saveFileName
	 * @param data
	 *
	 */
	public static boolean upload(String endpoint,String accessKeyId, String accessKeySecret,String bucketName, String pageName, String saveFileName, byte[] data){
		boolean flag = false;
		OSS client = null;
		try {
			while (!StringUtils.isNullOrEmpty(pageName) && StringUtils.beginsWithIgnoreCase(pageName, "/")) {
				pageName = pageName.substring(1);
			}
			client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
			client.putObject(bucketName, pageName+saveFileName, new ByteArrayInputStream(data));
			flag = true;
		}catch (Exception e){
			log.info("OSS图片上传失败",e.getMessage());
		}finally {
			if (null != client){
				client.shutdown();
			}
		}
		return flag;
	}

	/**
	 * @Description Base64文件上传
	 * @Author  Kaven
	 * @Date   2019/12/11 11:23
	 * @Param  endpoint accessKeyId accessKeySecret base64String bucketName filePath fileName
	 * @Return boolean
	 */
	public static boolean uploadBase64(String endpoint,String accessKeyId,String accessKeySecret,String base64String ,String bucketName ,String filePath,String fileName) {
		boolean flag = false;
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		String firstKey = filePath + fileName;
		InputStream in = null;
		try {
			// 判断Bucket是否存在。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
			// 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
			if (ossClient.doesBucketExist(bucketName)) {
				log.info("您已经创建Bucket：" + bucketName + "。");
			} else {
				log.info("您的Bucket不存在，创建Bucket：" + bucketName + "。");
				// 创建Bucket。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
				// 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
				ossClient.createBucket(bucketName);
			}

			// 查看Bucket信息。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
			// 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
			BucketInfo info = ossClient.getBucketInfo(bucketName);
			log.info("Bucket " + bucketName + "的信息如下：");
			log.info("\t数据中心：" + info.getBucket().getLocation());
			log.info("\t创建时间：" + info.getBucket().getCreationDate());
			log.info("\t用户标志：" + info.getBucket().getOwner());

			// 把字符串存入OSS，Object的名称为firstKey。详细请参看“SDK手册 > Java-SDK > 上传文件”。
			// 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/upload_object.html?spm=5176.docoss/user_guide/upload_object
			byte[] bytes = new BASE64Decoder().decodeBuffer(base64String);
			in = new ByteArrayInputStream(bytes);
			ossClient.putObject(bucketName, firstKey, in);
			in.close();
			log.info("Object：" + firstKey + "存入OSS成功。");
			flag = true;
		} catch ( OSSException oe) {
//            oe.printStackTrace();
			log.error(oe.getMessage());
		} catch (ClientException ce) {
//            ce.printStackTrace();
			log.error(ce.getMessage());
		} catch (Exception e) {
//            e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			if(in != null){
				try {
					in.close();
				}catch (IOException e){
					log.error(e.getMessage());
				}
			}
			if(ossClient != null) {
				ossClient.shutdown();
			}
		}
		return flag;
	}

	/**
	 * oss文件下载到本地
	 * @param endpoint
	 * @param accessKeyId
	 * @param accessKeySecret
	 * @param bucketName
	 * @param ossFileKey oss文件名称
	 * @param fileLocalPath 下载本地路径
	 * @return
	 */
	public static boolean download(String endpoint, String accessKeyId, String accessKeySecret, String bucketName, String ossFileKey, String fileLocalPath) {
		boolean flag = false;
		OSS ossClient = null;
		try {
			ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
			ossClient.getObject(new GetObjectRequest(bucketName, ossFileKey), new File(fileLocalPath));
			flag = true;
		} catch (Exception e) {
			log.error("AliyunOSS[{}]文件下载异常==>{}", bucketName, e.getMessage());
		} finally {
			if (null != ossClient) {
				ossClient.shutdown();
			}
		}
		return flag;
	}

	public static boolean downloadImgToLocal(String imgUrl,String localUrl, String fileName){
		if (StringUtil.isBlank(fileName)) {
			fileName = imgUrl.substring(imgUrl.lastIndexOf("/")+1,imgUrl.lastIndexOf("?"));
		}
		try {
			URL url = new URL(imgUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty(
					"Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
							+ "application/x-shockwave-flash, application/xaml+xml, "
							+ "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
							+ "application/x-ms-application, application/vnd.ms-excel, "
							+ "application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Charset", "UTF-8");
			InputStream inStream = conn.getInputStream();
			int len = 0;
			byte[] bytes = new byte[1024];
			File file = new File(localUrl+"/"+fileName);
			if(!file.exists()){
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			OutputStream out = new FileOutputStream(file);
			while ((len=inStream.read(bytes)) != -1){
				out.write(bytes,0,len);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return true;
	}

	/**
	 * 文件下载
	 */
	public static Workbook downloudExcel(String endPoint, String accessKeyId, String accessKeySecret, String bucketName, String fileName) {
		OSS ossClient = null;
		InputStream inputStream = null;
		Workbook workbook = null;
		try {
			ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
			OSSObject ossObject = ossClient.getObject(new GetObjectRequest(bucketName, fileName));
			inputStream = ossObject.getObjectContent();
			if (fileName.endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(inputStream);
			} else if (fileName.endsWith(".xls")) {
				workbook = new HSSFWorkbook(inputStream);
			}
		}catch(Exception e) {
			log.info("OSS文件下载出现异常：{}",e.getMessage());
		}finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
				if (null != ossClient) {
					ossClient.shutdown();
				}
			}
		}
		return workbook;
	}

	/**
	 * 文件下载
	 */
	public static byte[] downFile(String endPoint, String accessKeyId, String accessKeySecret, String bucketName, String fileName) {
		OSS ossClient = null;
		InputStream inputStream = null;

		byte[] bytes = null;
		try {
			ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
			OSSObject ossObject = ossClient.getObject(new GetObjectRequest(bucketName, fileName));
			inputStream = ossObject.getObjectContent();
			byte[] buffer = new byte[1024];
			int length;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			while ((length = inputStream.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			bytes = output.toByteArray();
		}catch(Exception e) {
			log.info("OSS文件下载出现异常：{}",e.getMessage());
		}finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
				if (null != ossClient) {
					ossClient.shutdown();
				}
			}
		}
		return bytes;
	}
}
