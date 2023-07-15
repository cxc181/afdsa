package com.yuqian.itax.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateSqlVO;
import com.yuqian.itax.agreement.service.AgreementTemplateService;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.gateway.annotation.JsonParam;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    /**
     * 获取模板参数信息
     * @param
     * @param
     * @param
     * @param
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
        if (parkId == null){
            return  ResultVo.Fail("园区id不能为空");
        }
        if (StringUtil.isEmpty(oemCode)){
            return  ResultVo.Fail("机构编码不能为空");
        }
        AgreementTemplateSqlVO vo = agreementTemplateService.getTableInfo(orderNo,memberId,companyId,parkId,oemCode,1);
        return ResultVo.Success(vo);
    }
}
