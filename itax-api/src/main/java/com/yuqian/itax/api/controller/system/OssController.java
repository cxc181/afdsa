package com.yuqian.itax.api.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.system.entity.OssCallbackResult;
import com.yuqian.itax.system.entity.OssPolicyResult;
import com.yuqian.itax.system.entity.dto.UploadFileDTO;
import com.yuqian.itax.system.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Oss控制器
 * @Date: 2019/12/10 14:22
 */
@Api(tags = "Oss控制器")
@RestController
@RequestMapping("/aliyun/oss")
@Slf4j
public class OssController extends BaseController {

	@Autowired
	private OssService ossService;

	/**
	 * @Description 开户文件上传，小视频/身份证正反面-带OCR识别/交易流水截图（小程序端使用）
	 * @Author  Kaven
	 * @Date   2019/12/10 14:22
	 * @Param  fileBase64 fileName type
	 * @Return  ResultVo
	 */
	@ApiOperation(value = "身份证OCR识别")
	@PostMapping("/ocrIdentify")
	public ResultVo ocrIdentify(@RequestBody UploadFileDTO fileDto) throws Exception {
		if(null == fileDto){
			return ResultVo.Fail("传入对象为空");
		}
	    if(StringUtils.isBlank(fileDto.getFileUrl()) || null == fileDto.getType()){
	        return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
	    }

		fileDto.setUserId(getCurrUserId());
		fileDto.setOemCode(this.getRequestHeadParams("oemCode"));
		Map<String,Object> resultMap = this.ossService.ocrIdentify(getRequestHeadParams("oemCode"), fileDto);
	    return  ResultVo.Success(resultMap);
	}

	@ApiOperation(value = "oss上传签名生成")
	@PostMapping(value = "/policy")
	public ResultVo<OssPolicyResult> policy(@JsonParam String isPublicBucket) {
		String oemCode = getRequestHeadParams("oemCode");
		OssPolicyResult result = ossService.policy(oemCode, isPublicBucket);
		return ResultVo.Success(result);
	}

	@ApiOperation(value = "oss上传成功回调")
	@PostMapping(value = "/callback")
	public OssCallbackResult callback(HttpServletRequest request) {
		OssCallbackResult ossCallbackResult = ossService.callback(request);
		return ossCallbackResult;
	}

	/**
	 * 获取oss私有域图片访问地址
	 */
	@ApiOperation("获取oss私有域图片访问地址")
	@ApiImplicitParam(name="key",value="路径+文件名(YCS/20191228/123.jpg)",dataType="String",required = true)
	@PostMapping("/getUrl")
	public ResultVo getUrl(@JsonParam String key) {
		if(StringUtils.isBlank(key)) {
			return ResultVo.Fail("参数不能为空");
		}
		JSONObject params = new JSONObject();
		params.put("key", key);
		String url = ossService.getUrl(key, 0);
		return ResultVo.Success(url);
	}

	/**
	 * 删除oss私有域图片（单个）
	 */
	@ApiOperation("删除oss私有域图片（单个）")
	@ApiImplicitParam(name="key",value="路径+文件名(YCS/20191228/123.jpg)",dataType="String",required = true)
	@PostMapping("/deleteObject")
	public ResultVo deleteObject(@JsonParam String key) {
		if(StringUtils.isBlank(key)) {
			return ResultVo.Fail("参数不能为空");
		}
		JSONObject params = new JSONObject();
		params.put("key", key);
		ossService.deleteObject(key);
		return ResultVo.Success();
	}

	/**
	 * 营业执照识别接口
	 * @param fileUrl
	 * @param
	 * @return
	 */
	@PostMapping("ocrBusinessLicense")
	public ResultVo ocrBusinessLicense(@JsonParam String fileUrl){
		Map<String,Object> infoMap = new HashMap<>();
		String oemCode = getRequestHeadParams("oemCode");
		try {
			Map<String,Object> resultMap = ossService.ocrBusinessLicense(oemCode, fileUrl);
			JSONObject object = JSONObject.parseObject((String)resultMap.get("ocrData"));
			infoMap.put("companyName",object.get("companyName"));
			infoMap.put("creditCode",object.get("creditCode"));
			infoMap.put("legalPerson",object.get("legalPerson"));
			infoMap.put("validPeriod", object.get("validPeriod"));
			infoMap.put("businessAddress", object.get("businessAddress"));
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				return ResultVo.Fail(e.getMessage());
			}
			log.error(e.getMessage(), e);
			return ResultVo.Fail("OCR身份识别失败");
		}
		return ResultVo.Success(infoMap);
	}
}
