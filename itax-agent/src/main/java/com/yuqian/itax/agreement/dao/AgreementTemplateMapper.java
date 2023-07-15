package com.yuqian.itax.agreement.dao;

import com.yuqian.itax.agreement.entity.AgreementTemplateEntity;
import com.yuqian.itax.agreement.entity.query.AgreementTemplateQuery;
import com.yuqian.itax.agreement.entity.query.ParkAgreementsQuery;
import com.yuqian.itax.agreement.entity.vo.AgreementTemlateListVO;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateDetailVO;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateSqlVO;
import com.yuqian.itax.agreement.entity.vo.ParkAgreementsVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 协议模板表dao
 * 
 * @Date: 2022年02月11日 17:13:24 
 * @author 蒋匿
 */
@Mapper
public interface AgreementTemplateMapper extends BaseMapper<AgreementTemplateEntity> {


    /**
     * 获取表格信息
     * @param orderNo
     * @param memberId
     * @param companyId
     * @param parkId
     * @return
     */
    AgreementTemplateSqlVO getTableInfo(@Param("orderNo")String orderNo, @Param("memberId")Long memberId, @Param("companyId") Long companyId,
                                        @Param("parkId") Long parkId,@Param("oemCode") String oemCode, @Param("companyType") Integer companyType);


    /**
     * 查询页面信息
     * @param query
     * @return
     */
    List<AgreementTemlateListVO> listAgreementTemplate(AgreementTemplateQuery query);

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
     * 禁/启用
     * @param status
     */
    void updateStatus(@Param("id") Long id,@Param("status") Integer status);

    /**
     * 协议模板详情
     * @param id
     * @return
     */
    AgreementTemplateDetailVO TemplateDetail(Long id);

    /**
     * 校验模板名称是否重复
     * @param id
     * @param name
     * @return
     */
    List<Long> checkTemplateName(Long id,Integer templateType,@Param("name") String name);
    /**
     * 查询模板
     * @param query
     * @return
     */
    List<ParkAgreementsVO> queryParkAgreements(ParkAgreementsQuery query);
}

