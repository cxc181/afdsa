package com.yuqian.itax.park.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.park.dao.ParkCommentsMapper;
import com.yuqian.itax.park.entity.ParkCommentsEntity;
import com.yuqian.itax.park.entity.ParkCommentsPO;
import com.yuqian.itax.park.entity.dto.ParkCommentDTO;
import com.yuqian.itax.park.entity.query.ParkCommentsQuery;
import com.yuqian.itax.park.entity.vo.ParkCommentListVO;
import com.yuqian.itax.park.entity.vo.ParkCommentsVO;

/**
 * 园区评价表service
 * 
 * @Date: 2022年10月11日 11:09:51 
 * @author 蒋匿
 */
public interface ParkCommentsService extends IBaseService<ParkCommentsEntity,ParkCommentsMapper> {


    /**
     * 查询园区评价列表  分页查询
     * @param query
     * @return
     */
    PageInfo<ParkCommentsVO> queryPageList(ParkCommentsQuery query);

    /**
     * 根据主键id查询园区评价详情
     * @param id
     * @return
     */
    ParkCommentsVO queryParkCommentsInfo(Long id);

    /**
     * 修改园区评价状态(1-可见 2-屏蔽)
     * @param id
     * @param status
     * @param useraccount
     */
    void updateStatus(Long id,Integer status,String useraccount);

    /**
     * 园区评价回复
     * @param po
     * @param currUser
     */
    void reply(ParkCommentsPO po, CurrUser currUser);

    /**
     * 获取园区评论列表
     * @param query
     */
    PageInfo<ParkCommentListVO> getCommentList(ParkCommentsQuery query);

    /**
     * 添加用户评论
     * @param dto
     */
    void addComment(ParkCommentDTO dto);
}

