package com.yuqian.itax.admin.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.system.entity.dto.UploadFileDTO;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * OCR识别controller
 * @author：pengwei
 * @Date：2020/07/07 17:12
 * @version：1.0
 */
@RestController
@RequestMapping("ocr")
@Slf4j
public class OcrController extends BaseController {

	@Autowired
	private OssService ossService;
	@Autowired
	private MemberCompanyService memberCompanyService;
	@Autowired
	private OrderService orderService;

	@ApiOperation(value = "身份证OCR识别")
	@PostMapping("identify")
	public ResultVo ocrIdentify(@RequestBody @Validated UploadFileDTO fileDto, BindingResult result){
		if(result.hasErrors()) {
			return ResultVo.Fail(result);
		}
	    if(StringUtils.isBlank(fileDto.getFileUrl()) || null == fileDto.getType()){
	        return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
	    }
		CurrUser currUser = getCurrUser();
		fileDto.setUserId(currUser.getUserId());
		fileDto.setOemCode(fileDto.getOemCode());
		Map<String,Object> resultMap;
		try {
			resultMap = ossService.ocrIdentify(fileDto.getOemCode(), fileDto);
		} catch (Exception e) {
			if (e instanceof BusinessException) {
				return ResultVo.Fail(e.getMessage());
			}
			log.error(e.getMessage(), e);
			return ResultVo.Fail("OCR身份识别失败");
		}
	    return ResultVo.Success(resultMap);
	}

	/**
	 * 营业执照识别接口
	 * @param fileUrl
	 * @param
	 * @return
	 */
	@PostMapping("ocrBusinessLicense")
	public ResultVo ocrBusinessLicense(@JsonParam String fileUrl,@JsonParam Long companyId,@JsonParam String orderNo){
		Map<String,Object> infoMap = new HashMap<>();
		String oemCode = "";
		if (companyId != null){
			MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(companyId);
			oemCode = memberCompanyEntity.getOemCode();
		}
		if (StringUtil.isNotBlank(orderNo)){
			OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
			oemCode = orderEntity.getOemCode();
		}
		try {
			Map<String,Object> resultMap = ossService.ocrBusinessLicense(oemCode, fileUrl);
			JSONObject object = JSONObject.parseObject((String)resultMap.get("ocrData"));
			infoMap.put("companyName",object.get("companyName"));
			infoMap.put("creditCode",object.get("creditCode"));
			infoMap.put("legalPerson",object.get("legalPerson"));
			infoMap.put("businessAddress",object.get("businessAddress"));
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
