package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.InvoiceHeadEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 发票抬头dao
 * 
 * @Date: 2019年12月07日 20:48:40 
 * @author yejian
 */
@Mapper
public interface InvoiceHeadMapper extends BaseMapper<InvoiceHeadEntity> {

    /**
     * 查询发票抬头列表
     * @return
     */
    List<InvoiceHeadEntity> listInvoiceHead(Long memberId);

    /**
     * 根据主键id和会员id查询发票抬头
     * @return
     */
    InvoiceHeadEntity findByMemberId(Map<String, Object> params);

}

