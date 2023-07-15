package com.yuqian.itax.agreement.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yuqian.itax.agent.service.OemParkRelaService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.agreement.dao.AgreementTemplateMapper;
import com.yuqian.itax.agreement.entity.AgreementTemplateEntity;
import com.yuqian.itax.agreement.entity.query.AgreementTemplateQuery;
import com.yuqian.itax.agreement.entity.query.ParkAgreementsQuery;
import com.yuqian.itax.agreement.entity.vo.AgreementTemlateListVO;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateDetailVO;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateSqlVO;
import com.yuqian.itax.agreement.entity.vo.ParkAgreementsVO;
import com.yuqian.itax.agreement.enums.TemplateStatusEnums;
import com.yuqian.itax.agreement.enums.TemplateTypeEnums;
import com.yuqian.itax.agreement.service.AgreementTemplateService;
import com.yuqian.itax.agreement.service.ParkAgreementTemplateRelaService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.util.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("agreementTemplateService")
public class AgreementTemplateServiceImpl extends BaseServiceImpl<AgreementTemplateEntity,AgreementTemplateMapper> implements AgreementTemplateService {

    @Autowired
    private OemParkRelaService oemParkRelaService;
    @Autowired
    private ParkAgreementTemplateRelaService parkAgreementTemplateRelaService;
    @Autowired
    private OemService oemService;

    @Override
    public AgreementTemplateSqlVO getTableInfo(String orderNo, Long memberId, Long companyId, Long parkId,String oemCode, Integer companyType) {
        if (memberId == null){
            memberId = 0L;
        }
        if (companyId == null){
            companyId =0L;
        }
        if (StringUtil.isEmpty(orderNo)){
            orderNo = "";
        }
        return mapper.getTableInfo(orderNo,memberId,companyId,parkId,oemCode, companyType);
    }

    @Override
    public String showTemplateUrl(Long id) {
        return mapper.showTemplateUrl(id);
    }

    @Override
    public String copyTemplate(Long id) {
        return mapper.copyTemplate(id);
    }

    @Override
    public PageInfo<AgreementTemlateListVO> listAgreementTemplate(AgreementTemplateQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(mapper.listAgreementTemplate(query));
    }

    @Override
    public List<AgreementTemlateListVO> getListAgreementTemplate(AgreementTemplateQuery query) {
        return mapper.listAgreementTemplate(query);
    }

    @Override
    public void updateStatus(Long id,Integer status) {
        if (status.equals(2)){
            List<Long> oemParkList = oemParkRelaService.getOemParkIdByAgreementTemplateId(id);
            List<Long> parkList = parkAgreementTemplateRelaService.getParkAgreementIdByAgreementTemplateId(id);
            List<Long> oemList = oemService.getOemIdByAgreementTemplateId(id);
            if (CollectionUtil.isNotEmpty(oemParkList) ||CollectionUtil.isNotEmpty(parkList) || CollectionUtil.isNotEmpty(oemList)){
                throw new BusinessException("未下架的oem机构或园区绑定了该模板，不允许禁用");
            }
        }
        mapper.updateStatus(id,status);
    }

    @Override
    public AgreementTemplateDetailVO TemplateDetail(Long id) {
        return mapper.TemplateDetail(id);
    }

    @Override
    public List<Long> checkTemplateName(Long id, Integer templateType,String name) {
        return mapper.checkTemplateName(id,templateType,name);
    }

    @Override
    public List<ParkAgreementsVO> queryParkAgreements(ParkAgreementsQuery query) {
        if (null == query) {
            throw new BusinessException("查询参数为空");
        }
        String oemCode = query.getOemCode();
        if (StringUtil.isBlank(oemCode)) {
            throw new BusinessException("机构编码为空");
        }
        if (null == query.getParkId()) {
            throw new BusinessException("园区id未空");
        }
        query.setTemplateStatus(TemplateStatusEnums.ENABLE.getValue());

        List<ParkAgreementsVO> vos = Lists.newArrayList();

        // 查询指定委托注册类型模板
        if (null != query.getTemplateType() && TemplateTypeEnums.REGISTRATION_AGREEMENT.getValue().equals(query.getTemplateType())) {
            // 查询机构专属协议
            List<ParkAgreementsVO> parkAgreementsVOS = mapper.queryParkAgreements(query);
            if (CollectionUtil.isEmpty(parkAgreementsVOS)) {
                // 查询园区委托注册协议
                query.setOemCode(null);
                List<ParkAgreementsVO> vos2 = mapper.queryParkAgreements(query);
                if (CollectionUtil.isNotEmpty(vos2)) {
                    vos.add(vos2.get(0));
                }
            } else {
                vos.add(parkAgreementsVOS.get(0));
            }
            return vos;
        }

        // 未指定模板类型时查所有
        // 委托注册协议
        query.setTemplateType(TemplateTypeEnums.REGISTRATION_AGREEMENT.getValue());
        // 查询机构专属协议
        List<ParkAgreementsVO> vos1 = mapper.queryParkAgreements(query);
        if (CollectionUtil.isEmpty(vos1)) {
            // 查询园区委托注册协议
            query.setOemCode(null);
            List<ParkAgreementsVO> vos2 = mapper.queryParkAgreements(query);
            if (CollectionUtil.isNotEmpty(vos2)) {
                vos.add(vos2.get(0));
            }
        } else if (vos1.size() > 0) {
            vos.add(vos1.get(0));
        }

        // 收费标准
        query.setOemCode(oemCode);
        query.setTemplateType(TemplateTypeEnums.RATES.getValue());
        // 先查询园区单独配置的收费标准（查询园区与协议模板的关系表）
        query.setOemCode(null);
        List<ParkAgreementsVO> vos3 = mapper.queryParkAgreements(query);
        if (CollectionUtil.isNotEmpty(vos3)) {
            vos.add(vos3.get(0));
        } else {
            // 园区未单独配置收费标准时，取产品统一配置的收费标准（产品表中保存的收费标准模板id）
            query.setOemCode(oemCode);
            List<ParkAgreementsVO> vos4 = mapper.queryParkAgreements(query);
            if (CollectionUtil.isNotEmpty(vos4)) {
                vos.add(vos4.get(0));
            }
        }

        // 园区办理协议
        query.setOemCode(null);
        query.setTemplateType(TemplateTypeEnums.PARK_MANAGEMENT_AGREEMENT.getValue());
        List<ParkAgreementsVO> vos5 = mapper.queryParkAgreements(query);
        if (CollectionUtil.isNotEmpty(vos5)) {
            vos.addAll(vos5);
        }

        return vos;
    }


    @Override
    public String htmlUpload(String content,String typeName) {
        String htmlStr = "<!DOCTYPE html>\n" +
                "<html lang='zh-cn'>\n" +
                "<head>\n" +
                "  <meta charset='utf-8'></meta>\n" +
                "  <meta http-equiv='X-UA-Compatible' content='IE=edge'></meta>\n" +
                "  <meta name=\"renderer\" content=\"webkit\"></meta>\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"></meta>\n" +
                "  <title>"+typeName+"</title>\n" +
                "  <script src=\"https://cdn.staticfile.org/jquery/3.4.1/jquery.min.js\"></script>\n" +
                "  <link rel='stylesheet' href='https://oss-itax-public.inabei.cn/page/agreement/css/css.css' type='text/css' media='screen' />\n" +
                "  <link rel='stylesheet' href='https://oss-itax-public.inabei.cn/page/agreement/css/css.css' type='text/css' media='print' />\n" +
                "</head><body>\n" +
                "<div id=\"content\" style=\"width:100%;height:100%\">";
        htmlStr+= content;
        htmlStr += "</div>\n" +
                "<script src=\"https://cdn.staticfile.org/jquery/3.4.1/jquery.min.js\"></script>\n" +
                "<script src=\"https://oss-itax-public.inabei.cn/page/agreement/js/js.js\"></script>\n" +
                "<script>\n" +
                "$(document).ready(function(){\n" +
                "  isShow();\n" +
                "})\n" +
                "\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>\n";

        return htmlStr;
    }
}

