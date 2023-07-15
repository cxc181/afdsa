package com.yuqian.itax.message.dao;

import com.yuqian.itax.message.entity.SmsTemplateEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 短信模板配置dao
 *
 * @Date: 2019年12月06日 14:59:14
 * @author Kaven
 */
@Mapper
public interface SmsTemplateMapper extends BaseMapper<SmsTemplateEntity> {

    /**
     * @Description 根据模板类型和OCEM机构码查询短信模板
     * @Author  Kaven
     * @Date   2019/12/6 15:32
     * @Param  templateType
     * @Return SmsTemplateEntity
     */
    SmsTemplateEntity getByTemplateType(@Param("templateType") String templateType,@Param("oemCode") String oemCode);

    void addBatch(@Param("list") List<SmsTemplateEntity> list, @Param ("oemCode")String oemCode, @Param("addTime") Date addTime, @Param("account")String account);
}