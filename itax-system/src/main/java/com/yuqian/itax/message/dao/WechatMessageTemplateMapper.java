package com.yuqian.itax.message.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.message.entity.WechatMessageTemplateEntity;
import com.yuqian.itax.message.entity.vo.WechatMessageTemplateSimpleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 微信订阅消息模板配置dao
 * 
 * @Date: 2020年06月04日 16:39:53 
 * @author 蒋匿
 */
@Mapper
public interface WechatMessageTemplateMapper extends BaseMapper<WechatMessageTemplateEntity> {

    /**
     * 根据模板类型和机构编码查询微信订阅消息模板
     * @param templateType 模板类型 1-工商开户审核 2-邀请签名 3-签名确认结果
     * @param oemCode
     * @return
     */
    WechatMessageTemplateEntity getByTemplateType(@Param("templateType") Integer templateType, @Param("oemCode") String oemCode);

    /**
     * 根据模板类型和机构编码查询微信订阅消息模板
     * @param templateTypes 模板类型 1-工商开户审核 2-邀请签名 3-签名确认结果
     * @param oemCode
     * @return
     */
    List<WechatMessageTemplateSimpleVO> getByTemplateTypes(@Param("templateTypes") int[] templateTypes, @Param("oemCode") String oemCode);
}

