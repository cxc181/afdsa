package com.yuqian.itax.park.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.park.dao.ParkCommentsMapper;
import com.yuqian.itax.park.entity.ParkCommentsEntity;
import com.yuqian.itax.park.entity.ParkCommentsPO;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.dto.ParkCommentDTO;
import com.yuqian.itax.park.entity.query.ParkCommentsQuery;
import com.yuqian.itax.park.entity.vo.ParkCommentListVO;
import com.yuqian.itax.park.entity.vo.ParkCommentsVO;
import com.yuqian.itax.park.service.ParkCommentsService;
import com.yuqian.itax.park.service.ParkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


@Service("parkCommentsService")
@Slf4j
public class ParkCommentsServiceImpl extends BaseServiceImpl<ParkCommentsEntity,ParkCommentsMapper> implements ParkCommentsService {

    @Resource
    private ParkCommentsMapper parkCommentsMapper;
    @Resource
    private ParkService parkService;

    /**
     * 查询园区评价列表  分页查询
     * @param query
     * @return
     */
    @Override
    public PageInfo<ParkCommentsVO> queryPageList(ParkCommentsQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<ParkCommentsVO> list = this.mapper.queryCommentsList(query);
        return new PageInfo<>(list);
    }

    /**
     * 根据主键id查询园区评价详情
     * @param id
     * @return
     */
    @Override
    public ParkCommentsVO queryParkCommentsInfo(Long id) {
        ParkCommentsEntity parkCommentsEntity = parkCommentsMapper.selectByPrimaryKey(id);
        if(parkCommentsEntity == null){
            throw  new BusinessException("该园区评价不存在,请知悉!");
        }
        ParkEntity parkEntity = parkService.findById(parkCommentsEntity.getParkId());
        if(parkEntity == null){
            throw  new BusinessException("该评价对应的园区数据不存在,请知悉!");
        }

//        ParkCommentsVO vo = new ParkCommentsVO();
//        BeanUtils.copyProperties(parkCommentsEntity,vo);
//        vo.setParkName(parkEntity.getParkName());
        ParkCommentsVO vo = parkCommentsMapper.getParkCommentsInfo(id);
        return vo;
    }


    /**
     * 修改园区评价状态(1-可见 2-屏蔽)
     * @param id
     * @param status
     * @param useraccount
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status, String useraccount) {
        ParkCommentsEntity parkCommentsEntity = parkCommentsMapper.selectByPrimaryKey(id);
        if(parkCommentsEntity == null){
            throw  new BusinessException("该园区评价不存在,请知悉!");
        }
        parkCommentsEntity.setStatus(status);
        parkCommentsEntity.setUpdateTime(new Date());
        parkCommentsEntity.setUpdateUser(useraccount);
        parkCommentsMapper.updateByPrimaryKey(parkCommentsEntity);

        //  更新园区表的评分
        // 园区评分是园区平均分（=园区所有可见评分总分/园区所有可见评论数） 保留一位小数向上取整
        // 根据状态查询  该园区下可见的总评论数据
        BigDecimal sumCount = parkCommentsMapper.getSumCount(parkCommentsEntity.getParkId(), 1);
        // 查询园区所有可见评分总分
        BigDecimal sumUserRatings = parkCommentsMapper.getSumUserRatings(parkCommentsEntity.getParkId(), 1);

        //sumUserRatings / sumCount;
        ParkEntity parkEntity = parkService.findById(parkCommentsEntity.getParkId());
        if(parkEntity == null){
            throw  new BusinessException("该园区不存在,请知悉!");
        }
        // 可见评分总分大于0，在做判断
        if(sumUserRatings.compareTo(BigDecimal.ZERO) > 0) {
            // 保留一位小数向上取整
            BigDecimal bigDecimal = sumUserRatings.divide(sumCount, 2).setScale(1, RoundingMode.HALF_UP);
            parkEntity.setUserRatings(bigDecimal);
        }else{
            parkEntity.setUserRatings(new BigDecimal("5.00"));
        }
        // 修改园区表的  园区评分字段
        parkService.editByIdSelective(parkEntity);

    }

    /**
     * 园区评价回复
     * @param po
     * @param currUser
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reply(ParkCommentsPO po, CurrUser currUser) {
        ParkCommentsEntity parkCommentsEntity = parkCommentsMapper.selectByPrimaryKey(po.getId());
        if(parkCommentsEntity == null){
            throw  new BusinessException("该园区评价不存在,请知悉!");
        }
        parkCommentsEntity.setReplyContent(po.getReplyContent());
        parkCommentsEntity.setUpdateTime(new Date());
        parkCommentsEntity.setUpdateUser(currUser.getUseraccount());
        parkCommentsMapper.updateByPrimaryKey(parkCommentsEntity);
    }

    @Override
    public PageInfo<ParkCommentListVO> getCommentList(ParkCommentsQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<ParkCommentListVO> list = mapper.getCommentList(query);
        return new PageInfo<>(list);
    }

    @Override
    public void addComment(ParkCommentDTO dto) {
        if (null == dto) {
            throw new BusinessException("请求参数缺失");
        }

        if (null == dto.getUserRatings()) {
            throw new BusinessException("用户评分为空");
        }
        Pattern pattern = Pattern.compile("^([1-4]\\.[05]|0\\.5|5\\.0)$");
        if (!pattern.matcher(dto.getUserRatings().toString()).matches()) {
            throw new BusinessException("用户评分格式错误");
        }

        // 查询园区
        ParkEntity park = Optional.ofNullable(parkService.findById(dto.getParkId())).orElseThrow(() -> new BusinessException("未查询到园区"));

        ParkCommentsEntity entity = new ParkCommentsEntity();
        ObjectUtil.copyObject(dto, entity);
        entity.setStatus(1);
        entity.setAddTime(new Date());
        entity.setAddUser(dto.getMemberAccount());
        try {
            this.insertSelective(entity);
        } catch (Exception e) {
            log.error("添加评论失败，失败原因：" + e.getMessage());
            throw new BusinessException("添加评论失败");
        }
        // 修改园区评分
        Example example = new Example(ParkCommentsEntity.class);
        example.createCriteria().andEqualTo("parkId", dto.getParkId()).andEqualTo("status", 1);
        List<ParkCommentsEntity> parkCommentsEntities = this.selectByExample(example);
        if (CollectionUtil.isNotEmpty(parkCommentsEntities)) {
            BigDecimal ratings = parkCommentsEntities.stream().map(ParkCommentsEntity::getUserRatings).reduce(BigDecimal.ZERO, BigDecimal::add);
            ratings = ratings.divide(BigDecimal.valueOf(parkCommentsEntities.size()),2,BigDecimal.ROUND_UP).setScale(1, BigDecimal.ROUND_UP);
            park.setUserRatings(ratings);
            parkService.editByIdSelective(park);
        }
    }

}

