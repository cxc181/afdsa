package com.yuqian.itax.park.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.park.dao.ParkRewardPolicyLabelMapper;
import com.yuqian.itax.park.entity.ParkRewardPolicyLabelEntity;
import com.yuqian.itax.park.entity.ParkRewardPolicyLabelPO;
import com.yuqian.itax.park.entity.query.ParkRewardPolicyLabelQuery;
import com.yuqian.itax.park.entity.vo.ParkRewardPolicyLabelVO;

import java.util.List;

/**
 * service
 * 
 * @Date: 2022年10月11日 11:08:41 
 * @author 蒋匿
 */
public interface ParkRewardPolicyLabelService extends IBaseService<ParkRewardPolicyLabelEntity,ParkRewardPolicyLabelMapper> {

    /**
     * 查询园区奖励政策列表-分页
     * @param query
     * @return
     */
    PageInfo<ParkRewardPolicyLabelVO> queryPageList(ParkRewardPolicyLabelQuery query);

    /**
     * 查询园区奖励政策详情
     * @param id
     * @return
     */
    ParkRewardPolicyLabelVO info(Long id);

    /**
     * 新增/修改园区奖励政策
     * @param po
     * @param currUser
     */
    void add(ParkRewardPolicyLabelPO po, CurrUser currUser);

    /**
     * 删除园区奖励政策
     * @param id
     */
    void delete(Long id);

    /**
     * 根据机构编码查询园区奖励标签
     * @param oemCode
     * @return
     */
    List<String> queryLabelByOemCode(String oemCode);
}

