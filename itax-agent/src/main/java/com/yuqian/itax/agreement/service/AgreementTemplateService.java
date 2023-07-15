package com.yuqian.itax.agreement.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agreement.dao.AgreementTemplateMapper;
import com.yuqian.itax.agreement.entity.AgreementTemplateEntity;
import com.yuqian.itax.agreement.entity.query.AgreementTemplateQuery;
import com.yuqian.itax.agreement.entity.query.ParkAgreementsQuery;
import com.yuqian.itax.agreement.entity.vo.AgreementTemlateListVO;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateDetailVO;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateSqlVO;
import com.yuqian.itax.agreement.entity.vo.ParkAgreementsVO;
import com.yuqian.itax.common.base.service.IBaseService;

import java.util.List;

/**
 * 协议模板表service
 * 
 * @Date: 2022年02月11日 17:13:24 
 * @author 蒋匿
 */
public interface AgreementTemplateService extends IBaseService<AgreementTemplateEntity,AgreementTemplateMapper> {

    /**
     * 获取表格信息
     * @param orderNo
     * @param memberId
     * @param companyId
     * @param parkId
     * @param oemCode
     * @return
     */
    AgreementTemplateSqlVO getTableInfo(String orderNo, Long memberId, Long companyId, Long parkId,String oemCode, Integer companyType);

    /**
     * 根据模板id获取模板oss地址
     * @param id
     * @return
     */
    String showTemplateUrl(Long id);

    /**
     * 根据模板id复制模板
     * @param id
     * @return
     */
    String copyTemplate(Long id);

    /**
     * 富文本转html并返回
     * @param content
     * @return
     */
    String htmlUpload(String content,String typeName);

    /**
     * 分页查询
     * @param query
     * @return
     */
    PageInfo<AgreementTemlateListVO> listAgreementTemplate(AgreementTemplateQuery query);

    /**
     * 查询页面信息
     * @param query
     * @return
     */
    List<AgreementTemlateListVO> getListAgreementTemplate(AgreementTemplateQuery query);

    /**
     * 禁/启用
     * @param status
     */
    void updateStatus(Long id,Integer status);

    /**
     * 协议模板详情
     * @param id
     * @return
     */
    AgreementTemplateDetailVO TemplateDetail(Long id);

    /**
     * 校验模板名称是否重复
     * @param id
     * @param
     * @return
     */
    List<Long> checkTemplateName(Long id,Integer templateType,String name);


    /**
     * 查询园区协议
     */
    List<ParkAgreementsVO> queryParkAgreements(ParkAgreementsQuery query);
}

