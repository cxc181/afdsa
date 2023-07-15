package com.yuqian.itax.error.dao;

import com.yuqian.itax.error.entity.ErrorInfoEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.error.entity.query.ErrorInfoQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 错误信息dao
 * 
 * @Date: 2019年12月08日 20:41:20 
 * @author 蒋匿
 */
@Mapper
public interface ErrorInfoMapper extends BaseMapper<ErrorInfoEntity> {

    /**
     * 查询错误信息列表
     * @param query
     * @return
     */
    List<ErrorInfoEntity> listErrorInfo(ErrorInfoQuery query);
}

