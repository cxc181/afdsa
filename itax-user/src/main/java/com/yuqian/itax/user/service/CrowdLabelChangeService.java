package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.dao.CrowdLabelChangeMapper;
import com.yuqian.itax.user.entity.CrowdLabelChangeEntity;
import com.yuqian.itax.user.entity.CrowdLabelEntity;
import com.yuqian.itax.user.entity.vo.CrowdLabelChangeVO;

/**
 * 人群标签变更表service
 * 
 * @Date: 2021年07月15日 15:49:04 
 * @author 蒋匿
 */
public interface CrowdLabelChangeService extends IBaseService<CrowdLabelChangeEntity,CrowdLabelChangeMapper> {

    void addCrowdLabelChange(CrowdLabelEntity crowdLabelEntity,String addUser,String remark);

    PageInfo<CrowdLabelChangeVO> listPageCrowdLabelChange(CrowdLabelChangeVO query);
	
}

