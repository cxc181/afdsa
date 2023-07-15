package com.yuqian.itax.park.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.park.dao.ParkRewardPolicyLabelMapper;
import com.yuqian.itax.park.dao.RewardPolicyLabelBaseMapper;
import com.yuqian.itax.park.entity.ParkRewardPolicyLabelEntity;
import com.yuqian.itax.park.entity.ParkRewardPolicyLabelPO;
import com.yuqian.itax.park.entity.query.ParkRewardPolicyLabelQuery;
import com.yuqian.itax.park.entity.vo.ParkRewardPolicyLabelVO;
import com.yuqian.itax.park.entity.vo.RewardPolicyLabelBaseVO;
import com.yuqian.itax.park.service.ParkRewardPolicyLabelService;
import com.yuqian.itax.util.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@Service("parkRewardPolicyLabelService")
public class ParkRewardPolicyLabelServiceImpl extends BaseServiceImpl<ParkRewardPolicyLabelEntity,ParkRewardPolicyLabelMapper> implements ParkRewardPolicyLabelService {

    @Resource
    private ParkRewardPolicyLabelMapper parkRewardPolicyLabelMapper;
    @Resource
    private RewardPolicyLabelBaseMapper rewardPolicyLabelBaseMapper;


    /**
     * 查询园区奖励政策列表-分页
     * @param query
     * @return
     */
    @Override
    public PageInfo<ParkRewardPolicyLabelVO> queryPageList(ParkRewardPolicyLabelQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<ParkRewardPolicyLabelVO> list = parkRewardPolicyLabelMapper.queryList(query);
        return new PageInfo<>(list);
    }


    /**
     * 查询园区奖励政策详情
     * @param id
     * @return
     */
    @Override
    public ParkRewardPolicyLabelVO info(Long id) {
        ParkRewardPolicyLabelEntity entity = parkRewardPolicyLabelMapper.selectByPrimaryKey(id);
        if(entity == null){
            throw  new BusinessException("该园区奖励政策不存在,请知悉!");
        }
        ParkRewardPolicyLabelVO vo = new ParkRewardPolicyLabelVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 新增/修改园区奖励政策
     * @param po
     * @param currUser
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ParkRewardPolicyLabelPO po, CurrUser currUser) {
        if(po.getId() != null){
            // 执行修改方法
            ParkRewardPolicyLabelEntity entity = parkRewardPolicyLabelMapper.selectByPrimaryKey(po.getId());
            if(entity == null){
                throw  new BusinessException("该园区奖励政策不存在,请知悉!");
            }
            if(!po.getRewardLabel().equals(entity.getRewardLabel())) {
                int count = parkRewardPolicyLabelMapper.verifyRewardLabelPark(po.getParkId(), po.getRewardLabel());
                if (count > 0) {
                    throw new BusinessException("该园区内已存在此奖励政策!");
                }
            }
            BeanUtils.copyProperties(po, entity);
            // 根据奖励标签名称查询是否有该数据
            RewardPolicyLabelBaseVO rewardPolicyLabelOne = rewardPolicyLabelBaseMapper.getRewardPolicyLabelOne(po.getRewardLabel());
            if(rewardPolicyLabelOne != null){
                // 有的话自动取标签id
                entity.setRewardLabelBaseId(rewardPolicyLabelOne.getId());
            }
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(currUser.getUseraccount());
            parkRewardPolicyLabelMapper.updateByPrimaryKey(entity);
        }else {
           // 验证园区里奖励标签唯一
            int count = parkRewardPolicyLabelMapper.verifyRewardLabelPark(po.getParkId(), po.getRewardLabel());
            if(count > 0){
                throw  new BusinessException("该园区内已存在此奖励政策!");
            }
            // 否则执行新增方法
            ParkRewardPolicyLabelEntity entity = new ParkRewardPolicyLabelEntity();
            BeanUtils.copyProperties(po, entity);
            // 根据奖励标签名称查询是否有该数据
            RewardPolicyLabelBaseVO rewardPolicyLabelOne = rewardPolicyLabelBaseMapper.getRewardPolicyLabelOne(po.getRewardLabel());
            if(rewardPolicyLabelOne != null){
                // 有的话自动取标签id
                entity.setRewardLabelBaseId(rewardPolicyLabelOne.getId());
            }
            entity.setAddTime(new Date());
            entity.setAddUser(currUser.getUseraccount());
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(currUser.getUseraccount());
            parkRewardPolicyLabelMapper.insertSelective(entity);
        }
    }

    /**
     * 删除园区奖励政策
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ParkRewardPolicyLabelEntity parkRewardPolicyLabelEntity = parkRewardPolicyLabelMapper.selectByPrimaryKey(id);
        if(parkRewardPolicyLabelEntity == null){
            throw  new BusinessException("该园区奖励政策不存在,请知悉!");
        }
        // 执行删除操作
        parkRewardPolicyLabelMapper.delete(parkRewardPolicyLabelEntity);
    }

    @Override
    public List<String> queryLabelByOemCode(String oemCode) {
        if (StringUtil.isBlank(oemCode)) {
            throw new BusinessException("机构编码为空");
        }
        return mapper.queryLabelByOemCode(oemCode);
    }


}

