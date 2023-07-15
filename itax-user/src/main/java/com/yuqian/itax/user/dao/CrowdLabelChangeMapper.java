package com.yuqian.itax.user.dao;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.CrowdLabelChangeEntity;
import com.yuqian.itax.user.entity.vo.CrowdLabelChangeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 人群标签变更表dao
 * 
 * @Date: 2021年07月15日 15:49:04 
 * @author 蒋匿
 */
@Mapper
public interface CrowdLabelChangeMapper extends BaseMapper<CrowdLabelChangeEntity> {

    /**
     * 分页查询
     * @param crowdLabelId
     * @return
     */
    List<CrowdLabelChangeVO> listPageCrowdLabelChange(Long crowdLabelId);
	
}

