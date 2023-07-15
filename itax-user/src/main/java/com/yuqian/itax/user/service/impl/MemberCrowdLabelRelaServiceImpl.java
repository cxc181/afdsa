package com.yuqian.itax.user.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.MemberCrowdLabelRelaMapper;
import com.yuqian.itax.user.entity.CrowdLabelEntity;
import com.yuqian.itax.user.entity.MemberCrowdLabelRelaEntity;
import com.yuqian.itax.user.entity.vo.CrowdAccoutVO;
import com.yuqian.itax.user.entity.vo.CrowdLabelInsertVO;
import com.yuqian.itax.user.service.CrowdLabelChangeService;
import com.yuqian.itax.user.service.CrowdLabelService;
import com.yuqian.itax.user.service.MemberCrowdLabelRelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service("memberCrowdLabelRelaService")
public class MemberCrowdLabelRelaServiceImpl extends BaseServiceImpl<MemberCrowdLabelRelaEntity,MemberCrowdLabelRelaMapper> implements MemberCrowdLabelRelaService {

    @Autowired
    private CrowdLabelService crowdLabelService;

    @Autowired
    private CrowdLabelChangeService crowdLabelChangeService;



    @Override
    @Transactional
    public void addBatch(List<CrowdLabelInsertVO> list, String oemCode, Long crowdLabelId, String updateUser) {
        this.mapper.addBatch(list,oemCode,crowdLabelId,updateUser,new Date());
        CrowdLabelEntity crowdLabelEntity = crowdLabelService.findById(crowdLabelId);
        //  更新用户数
        Integer num = 0;
        if (crowdLabelEntity.getMemberUserNum() != null){
            num = crowdLabelEntity.getMemberUserNum()+list.size();
            crowdLabelEntity.setMemberUserNum(num);
        }
        crowdLabelService.editByIdSelective(crowdLabelEntity);
        crowdLabelChangeService.addCrowdLabelChange(crowdLabelEntity,updateUser,"添加用户");
    }

    @Override
    public List<CrowdAccoutVO> queryCrowdAccountById(Long crowdLabelId) {
        return this.mapper.queryCrowdAccountById(crowdLabelId);
    }

    @Override
    public void deleteAccountByCrowdLabelId(Long crowdLabelId) {
        this.mapper.deleteAccountByCrowdLabelId(crowdLabelId);
    }

    @Override
    public void deleteAccountByMemberId(List<CrowdLabelInsertVO> list,Long crowdLabelId,String userAccount) {
        this.mapper.deleteAccountByMemberId(list);
        CrowdLabelEntity crowdLabelEntity = crowdLabelService.findById(crowdLabelId);
        //  更新用户数
        Integer num = 0;
        if (crowdLabelEntity.getMemberUserNum() != null){
            num = crowdLabelEntity.getMemberUserNum()-list.size();
            crowdLabelEntity.setMemberUserNum(num);
        }
        crowdLabelService.editByIdSelective(crowdLabelEntity);
        crowdLabelChangeService.addCrowdLabelChange(crowdLabelEntity,userAccount,"删除用户");
    }

    /**
     * 根据会员id获取人群标签id
     * @param memberId
     * @param oemCode
     * @return
     */
    @Override
    public Long getCrowLabelIdByMemberId(Long memberId,String oemCode){
        MemberCrowdLabelRelaEntity memberCrowdLabelRelaEntity = new MemberCrowdLabelRelaEntity();
        memberCrowdLabelRelaEntity.setMemberId(memberId);
        memberCrowdLabelRelaEntity.setOemCode(oemCode);
        List<MemberCrowdLabelRelaEntity> list = select(memberCrowdLabelRelaEntity);
        if(list!=null && list.size()>0){
            return list.get(0).getCrowdLabelId();
        }
        return null;
    }

    @Override
    public void addUserByH5Access(List<Long> list, String oemCode, Long crowdLabelId, String addUser) {
        mapper.addUserByH5Access(list,oemCode,crowdLabelId,addUser,new Date());
    }
}

