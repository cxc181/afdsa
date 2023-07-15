package com.yuqian.itax.agent.dao;

import com.yuqian.itax.agent.entity.OemParkRelaEntity;
import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 机构与园区的关系dao
 *
 * @Date: 2019年12月07日 20:35:24 
 * @author 蒋匿
 */
@Mapper
public interface OemParkRelaMapper extends BaseMapper<OemParkRelaEntity> {

   void deleteByOemCode(@Param("oemCode") String oemCode);

   List<Long> queryOemParkIdList(@Param("oemCode") String oemCode);

   Map<String, String> queryOemParkCorporate(@Param("oemCode") String oemCode);

   /**
    * 根据oemCode和园区id获取数据
    * @param oemCode
    * @param parkId
    * @return
    */
   OemParkRelaEntity queryOemParkByOemCodeAndParkId(@Param("oemCode") String oemCode,@Param("parkId") Long parkId);


   /**
    * 根据oemCode 和园区id获取模板数据
    * @param oemCode
    * @param parkId
    * @return
    */
   AgreementTemplateInfoVO getAgreementTemplateByOemCodeAndParkId(@Param("oemCode") String oemCode,@Param("parkId") Long parkId);

   /**
    * 根据协议模板id查询机构与园区的关系表id
    * @param agreementTemplateId
    * @return
    */
   List<Long> getOemParkIdByAgreementTemplateId(@Param("agreementTemplateId") Long agreementTemplateId);
}

