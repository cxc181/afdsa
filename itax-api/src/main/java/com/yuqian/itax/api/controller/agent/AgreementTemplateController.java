package com.yuqian.itax.api.controller.agent;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agreement.entity.vo.AgreementPreviewVO;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateSqlVO;
import com.yuqian.itax.agreement.service.AgreementTemplateService;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;

/**
 * 协议模板
 */
@Api(tags = "协议模板标准控制器")
@Slf4j
@RestController
@RequestMapping("/agreementTemplate")
public class AgreementTemplateController extends BaseController {


    @Autowired
    private AgreementTemplateService agreementTemplateService;

    @Autowired
    private OssService ossService;
    /**
     * 获取模板参数信息
     * @param jsonObject
     * @return
     */
    @ApiOperation("获取模板参数信息")
    @PostMapping("/getAgreementTemplateInfo")
    public ResultVo getAgreementTemplateInfo(@RequestBody JSONObject jsonObject){
       String  orderNo= jsonObject.getString("orderNo");
       Long memberId = jsonObject.getLong("memberId");
       Long companyId = jsonObject.getLong("companyId");
       Long parkId = jsonObject.getLong("parkId");
       String oemCode = jsonObject.getString("oemCode");
       Integer companyType = jsonObject.getInteger("companyType");
       if (StringUtil.isBlank(orderNo) && null == companyId && null == companyType) {
           return ResultVo.Fail("企业类型不能为空");
       }
        if (parkId == null){
            return  ResultVo.Fail("园区id不能为空");
        }
        if (StringUtil.isEmpty(oemCode)){
            return  ResultVo.Fail("机构编码不能为空");
        }
        AgreementTemplateSqlVO vo = agreementTemplateService.getTableInfo(orderNo,memberId,companyId,parkId,oemCode,companyType);
        if(vo == null){
            return ResultVo.Success(vo);
        }
        if(companyId==null && StringUtil.isBlank(orderNo)){
            AgreementPreviewVO agreementPreviewVO = new AgreementPreviewVO();
            ObjectUtil.copyNotBlankObject(vo,agreementPreviewVO);
            vo = new AgreementTemplateSqlVO();
            ObjectUtil.copyNotBlankObject(agreementPreviewVO,vo);
            return ResultVo.Success(vo);
        }
        if(StringUtil.isNotBlank(vo.getIdCardFront())){
            vo.setIdCardFront(ossService.getPrivateImgUrl(vo.getIdCardFront()));
        }
        if(StringUtil.isNotBlank(vo.getIdCardBack())){
            vo.setIdCardBack(ossService.getPrivateImgUrl(vo.getIdCardBack()));
        }
        if(StringUtil.isNotBlank(vo.getLegalIdCardFront())){
            vo.setLegalIdCardFront(ossService.getPrivateImgUrl(vo.getLegalIdCardFront()));
        }
        if(StringUtil.isNotBlank(vo.getLegalIdCardReverse())){
            vo.setLegalIdCardReverse(ossService.getPrivateImgUrl(vo.getLegalIdCardReverse()));
        }
        if(StringUtil.isNotBlank(vo.getAgentIdCardFront())){
            vo.setAgentIdCardFront(ossService.getPrivateImgUrl(vo.getAgentIdCardFront()));
        }
        if(StringUtil.isNotBlank(vo.getAgentIdCardBack())){
            vo.setAgentIdCardBack(ossService.getPrivateImgUrl(vo.getAgentIdCardBack()));
        }
        if(StringUtil.isNotBlank(vo.getSignImg())){
            vo.setSignImg(ossService.getPrivateImgUrl(vo.getSignImg()));
        }
        if(StringUtil.isNotBlank(vo.getParkOfficialSealImg())){
            vo.setParkOfficialSealImg(ossService.getPrivateImgUrl(vo.getParkOfficialSealImg()));
        }
        if(StringUtil.isNotBlank(vo.getOemOfficialSealImg())){
            vo.setOemOfficialSealImg(ossService.getPrivateImgUrl(vo.getOemOfficialSealImg()));
        }
        vo.setAgentStartTime(vo.getSignTime());
        if(StringUtil.isNotBlank(vo.getSignTime())) {
            Date signDate = DateUtil.parseDate(vo.getSignTime(), "yyyy年MM月dd日");
            Calendar date = Calendar.getInstance();
            date.setTime(signDate);
            date.add(Calendar.YEAR, 1);
            vo.setAgentEndTime(DateUtil.format(date.getTime(), "yyyy年MM月dd日"));
        }
        return ResultVo.Success(vo);
    }
}
