package com.yuqian.itax.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.CrowdLabelChangeMapper;
import com.yuqian.itax.user.entity.CrowdLabelChangeEntity;
import com.yuqian.itax.user.entity.CrowdLabelEntity;
import com.yuqian.itax.user.entity.vo.CrowdLabelChangeVO;
import com.yuqian.itax.user.service.CrowdLabelChangeService;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service("crowdLabelChangeService")
public class CrowdLabelChangeServiceImpl extends BaseServiceImpl<CrowdLabelChangeEntity,CrowdLabelChangeMapper> implements CrowdLabelChangeService {

    @Override
    public void addCrowdLabelChange(CrowdLabelEntity crowdLabelEntity, String addUser,String remark) {
        CrowdLabelChangeEntity crowdLabelChangeEntity = new CrowdLabelChangeEntity();
        crowdLabelChangeEntity.setCrowdLabelId(crowdLabelEntity.getId());
        crowdLabelChangeEntity.setCrowdLabelName(crowdLabelEntity.getCrowdLabelName());
        crowdLabelChangeEntity.setOemCode(crowdLabelEntity.getOemCode());
        crowdLabelChangeEntity.setStatus(crowdLabelEntity.getStatus());
        if (crowdLabelEntity.getMemberUserNum() != null){
            crowdLabelChangeEntity.setMemberUserNum(crowdLabelEntity.getMemberUserNum());
        }
        if (crowdLabelEntity.getCrowdLabelDesc() != null){
            crowdLabelChangeEntity.setCrowdLabelDesc(crowdLabelEntity.getCrowdLabelDesc());
        }
        crowdLabelChangeEntity.setAddTime(new Date());
        crowdLabelChangeEntity.setAddUser(addUser);
        crowdLabelChangeEntity.setRemark(remark);
        this.mapper.insertSelective(crowdLabelChangeEntity);
    }

    @Override
    public PageInfo<CrowdLabelChangeVO> listPageCrowdLabelChange(CrowdLabelChangeVO query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(mapper.listPageCrowdLabelChange(query.getCrowdLabelId()));
    }
}

