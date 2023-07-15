package com.yuqian.itax.admin.controller.agreement;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agreement.entity.AgreementTemplateEntity;
import com.yuqian.itax.agreement.entity.dto.AgreementTemplateDTO;
import com.yuqian.itax.agreement.entity.query.AgreementTemplateQuery;
import com.yuqian.itax.agreement.entity.vo.AgreementTemlateListVO;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateDetailVO;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateVO;
import com.yuqian.itax.agreement.enums.TemplateTypeEnums;
import com.yuqian.itax.agreement.service.AgreementTemplateService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * 协议模板
 */
@RestController
@RequestMapping("/agreementTemplate")
@Slf4j
public class AgreementTemplateController extends BaseController {

    @Autowired
    private OssService ossService;
    @Autowired
    private AgreementTemplateService agreementTemplateService;

    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 获取表格属性
     * @return
     */
    @PostMapping("getTableParameter")
    public ResultVo getTableParameter(){
        AgreementTemplateVO vo = new AgreementTemplateVO();
        return ResultVo.Success(vo);
    }

    /**
     * 新增协议模板
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("addTableParameter")
    public ResultVo addAgreementTemplate(@RequestBody AgreementTemplateDTO dto, BindingResult result){
        CurrUser currUser = getCurrUser();
        if(result.hasErrors()){
            return  ResultVo.Fail(result);
        }
        if (dto.getTemplateName().length()>30){
            return  ResultVo.Fail("模板名称不能超过30个字符");
        }
        if (StringUtil.isNotBlank(dto.getTemplateDesc()) && dto.getTemplateDesc().length()>100){
            return  ResultVo.Fail("模板说明不能超过100个字符");
        }
        List<Long> templateId = agreementTemplateService.checkTemplateName(null,dto.getTemplateType(),dto.getTemplateName());
        if (!CollectionUtils.isEmpty(templateId)){
            return  ResultVo.Fail("模板名称在数据库已存在");
        }
        if (StringUtil.isNotBlank(dto.getTemplateShowName()) && dto.getTemplateShowName().length()>30){
            return  ResultVo.Fail("模板展示名称不能超过30个字符");
        }
        AgreementTemplateEntity entity = new AgreementTemplateEntity();
        String content =agreementTemplateService.htmlUpload(dto.getTemplateContent(), TemplateTypeEnums.getByMessage(dto.getTemplateType()));
        boolean flag = ossService.uploadPublic("page/"+dto.getTemplateName()+".html",content.getBytes(StandardCharsets.UTF_8));
        if (flag){
            entity.setTemplateHtmlUrl("page/"+dto.getTemplateName()+".html");
        }
        entity.setTemplateCode(dto.getTemplateCode());
        entity.setTemplateDesc(dto.getTemplateDesc());
        entity.setTemplateName(dto.getTemplateName());
        entity.setTemplateContent(dto.getTemplateContent());
        entity.setTemplateType(dto.getTemplateType());
        if (StringUtil.isNotBlank(dto.getTemplateShowName())){
            entity.setTemplateShowName(dto.getTemplateShowName());
        }
        entity.setTemplateStatus(1);
        entity.setAddTime(new Date());
        entity.setAddUser(currUser.getUseraccount());
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(currUser.getUseraccount());
        agreementTemplateService.insertSelective(entity);
       return ResultVo.Success();
    }

    /**
     * 查询协议模板信息
     * @param query
     * @return
     */
    @PostMapping("listAgreementTemplate")
    public ResultVo listAgreementTemplate(@RequestBody AgreementTemplateQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<AgreementTemlateListVO> list = agreementTemplateService.getListAgreementTemplate(query);
        if (CollectionUtil.isNotEmpty(list)){
            String http = dictionaryService.getByCode("oss_req_head").getDictValue();
            String host = http + dictionaryService.getByCode("oss_access_public_host").getDictValue();
            for(AgreementTemlateListVO vo :list){
                vo.setTemplateHtmlUrl(host+vo.getTemplateHtmlUrl()+"?t="+System.currentTimeMillis());
            }
        }
        return ResultVo.Success(new PageInfo(list));
    }

    /**
     * 复制模板
     * @param id
     * @return
     */
    @PostMapping("copyAgreementTemplate")
    public ResultVo copyAgreementTemplate(@JsonParam Long id){
        String content = agreementTemplateService.copyTemplate(id);
        if (StringUtil.isEmpty(content)){
            return ResultVo.Fail("模板数据错误");
        }
        return ResultVo.Success(content);
    }

    /**
     * 禁用、启用
     * @param id
     * @param status
     * @return
     */
    @PostMapping("updateStatus")
    public ResultVo updateStatus(@JsonParam Long id,@JsonParam Integer status){
        agreementTemplateService.updateStatus(id,status);
        return ResultVo.Success();
    }

    /**
     * 协议模板详情
     * @param id
     * @return
     */
    @PostMapping("agreementTemplateDetail")
    public ResultVo agreementTemplateDetail(@JsonParam Long id){
        AgreementTemplateDetailVO vo = agreementTemplateService.TemplateDetail(id);
        if (vo == null){
            return ResultVo.Fail("数据错误");
        }
        return ResultVo.Success(vo);
    }

    /**
     * 协议模板编辑提交
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("updateAgreementTemplate")
    public ResultVo updateAgreementTemplate(@RequestBody AgreementTemplateDTO dto, BindingResult result){
        CurrUser currUser = getCurrUser();
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        if (dto.getId() == null){
            return ResultVo.Fail("模板id不能为空");
        }
        AgreementTemplateEntity entity = agreementTemplateService.findById(dto.getId());
        if (entity == null){
            return ResultVo.Fail("模板不存在");
        }
        if (dto.getTemplateName().length()>30){
            return  ResultVo.Fail("模板名称不能超过30个字符");
        }
        if (StringUtil.isNotBlank(dto.getTemplateDesc()) && dto.getTemplateDesc().length()>100){
            return  ResultVo.Fail("模板说明不能超过100个字符");
        }
        if (StringUtil.isNotBlank(dto.getTemplateShowName()) && dto.getTemplateShowName().length()>30){
            return  ResultVo.Fail("模板展示名称不能超过30个字符");
        }
        List<Long> templateId = agreementTemplateService.checkTemplateName(dto.getId(),dto.getTemplateType(),dto.getTemplateName());
        if (!CollectionUtils.isEmpty(templateId)){
            return  ResultVo.Fail("模板名称在数据库已存在");
        }
        String content =agreementTemplateService.htmlUpload(dto.getTemplateContent(),TemplateTypeEnums.getByMessage(dto.getTemplateType()));
        boolean flag = ossService.uploadPublic("page/"+dto.getTemplateName()+".html",content.getBytes(StandardCharsets.UTF_8));
        if (!flag){
            return ResultVo.Fail("模板上传失败");
        }
        entity.setTemplateContent(dto.getTemplateContent());
        entity.setTemplateCode(dto.getTemplateCode());
        entity.setTemplateName(dto.getTemplateName());
        entity.setTemplateType(dto.getTemplateType());
        if (StringUtil.isNotBlank(dto.getTemplateShowName())){
            entity.setTemplateShowName(dto.getTemplateShowName());
        }
        if (StringUtil.isNotBlank(dto.getTemplateDesc())){
            entity.setTemplateDesc(dto.getTemplateDesc());
        }else{
            entity.setTemplateDesc(null);
        }
        entity.setTemplateHtmlUrl("page/"+dto.getTemplateName()+".html");
        entity.setUpdateUser(currUser.getUseraccount());
        entity.setUpdateTime(new Date());
        agreementTemplateService.editByIdSelective(entity);
        return ResultVo.Success();
    }

}
