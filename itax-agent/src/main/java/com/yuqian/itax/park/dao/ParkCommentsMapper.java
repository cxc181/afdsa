package com.yuqian.itax.park.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.ParkCommentsEntity;
import com.yuqian.itax.park.entity.query.ParkCommentsQuery;
import com.yuqian.itax.park.entity.vo.ParkCommentListVO;
import com.yuqian.itax.park.entity.vo.ParkCommentsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 园区评价表dao
 * 
 * @Date: 2022年10月11日 11:09:51 
 * @author 蒋匿
 */
@Mapper
public interface ParkCommentsMapper extends BaseMapper<ParkCommentsEntity> {

    /**
     * 查询园区评价列表
     * @param query
     * @return
     */
    List<ParkCommentsVO> queryCommentsList(@Param("query") ParkCommentsQuery query);

    /**
     * 根据主键id查询园区评价详情
     * @param id
     * @return
     */
    ParkCommentsVO getParkCommentsInfo(@Param("id")Long id);

    /**
     * 根据状态查询  该园区下可见的总评论数据
     * @param parkId
     * @param status
     * @return
     */
    BigDecimal getSumCount(@Param("parkId")Long parkId,@Param("status")Integer status);

    /**
     * 查询园区所有可见评分总分
     * @param parkId
     * @param status
     * @return
     */
    BigDecimal getSumUserRatings(@Param("parkId")Long parkId,@Param("status")Integer status);

    /**
     * 获取园区评论列表
     * @param query
     * @return
     */
    List<ParkCommentListVO> getCommentList(ParkCommentsQuery query);
}

