package com.yuqian.itax.error.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.error.entity.ErrorCodeEntity;
import com.yuqian.itax.error.entity.query.ErrorCodeQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 错误代码管理dao
 * 
 * @Date: 2019年12月08日 20:41:09 
 * @author 蒋匿
 */
@Mapper
public interface ErrorCodeMapper extends BaseMapper<ErrorCodeEntity> {

    /**
     * 查询错误代码
     * @param query
     * @return
     */
    List<ErrorCodeEntity> listErrorCode(ErrorCodeQuery query);

    /**
     * 根据code查询错误代码
     * @param errorCode
     * @param notId
     * @return
     */
    ErrorCodeEntity selectByCode(@Param("errorCode")String errorCode, @Param("notId")Long notId);
}

