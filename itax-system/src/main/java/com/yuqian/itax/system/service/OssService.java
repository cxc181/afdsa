package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.system.entity.OssCallbackResult;
import com.yuqian.itax.system.entity.OssPolicyResult;
import com.yuqian.itax.system.entity.dto.UploadFileDTO;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * OSS 文件service
 */
public interface OssService {

	/**
	 * oss上传策略生成
	 * @param oemCode
	 * @return
	 */
	OssPolicyResult policy(String oemCode, String isPublicBucket);
	/**
	 * oss上传成功私有域回调
	 * @return
	 */
	OssCallbackResult callback(HttpServletRequest request);

	/**
	 * 获取oss图片访问地址
	 * @param key 路径+文件名(YCS/20191228/123.jpg)
	 * @param isPublicBucket 0-私有域bucket 1-公有域bucket
	 * @return
	 */
	String getUrl(String key, int isPublicBucket);

	/**
	 * 删除单个文件
	 * @param key 路径+文件名(YCS/20191228/123.jpg)
	 * @return
	 */
	void deleteObject(String key);

	/**
	 * 图片上传
     * @param saveFileName
     * @param data
     */
	boolean upload(String saveFileName, byte[] data);


	/**
	 * @Description Base64文件上传
	 * @Author  Kaven
	 * @Date   2019/12/11 11:26
	 * @Param  saveFileName filePath bucketName base64Str
	 * @Return boolean
	*/
	boolean uploadBase64(String saveFileName,String filePath,String bucketName,String base64Str);

	/**
	 * 获取oss私有图片地址
	 *
	 * @param fileName
	 * @return
	 */
	String getPrivateImgUrl(String fileName);

	/**
	 * @Description 身份证OCR识别
	 * @Author  Kaven
	 * @Date   2019/12/20 11:41
	 * @Param  UploadFileDTO
	 * @Return Map<String, Object>
	 * @Exception BusinessException
	 */
	Map<String, Object> ocrIdentify(String oemCode, UploadFileDTO fileDto) throws Exception;

	/**
	 * 营业执照ocr识别
	 * @param oemCode
	 * @param fileUrl
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> ocrBusinessLicense(String oemCode,String fileUrl) throws Exception;

	String getPrivateVideoUrl(String fileName);

	/**
	 * 图片上传（公有域）
	 * @param name
	 * @param bytes
	 */
	boolean uploadPublic(String name, byte[] bytes);

	/**
	 * 删除公有域单个文件
	 * @param key 路径+文件名(YCS/20191228/123.jpg)
	 * @return
	 */
	void deletePublicObject(String key);

	/**
	 * 删除单个文件
	 * @param key 路径+文件名(YCS/20191228/123.jpg)
	 * @return
	 */
	void deleteObject(String key, String bucketName);

	/**
	 * 判断oss文件是否存在
	 * @param key
	 * @param bucketName
	 * @return true：存在，false：不存在
	 */
	boolean doesObjectExist(String key, String bucketName);

	/**
	 * 判断oss文件在公有域是否存在
	 * @param key
	 * @return true：存在，false：不存在
	 */
	boolean doesObjectExistPublic(String key);

	/**
	 * 判断oss文件在私有域是否存在
	 * @param key
	 * @return true：存在，false：不存在
	 */
	boolean doesObjectExistPrivate(String key);

	/**
	 * 下载oss文件
	 * @param key
	 * @return
	 */
	byte[] download(String key, String bucketName);
}
