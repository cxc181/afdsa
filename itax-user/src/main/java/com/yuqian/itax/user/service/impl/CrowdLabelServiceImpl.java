package com.yuqian.itax.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.vo.OemAccessPartyDetailVO;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.user.dao.CrowdLabelMapper;
import com.yuqian.itax.user.dao.MemberAccountMapper;
import com.yuqian.itax.user.entity.CrowdLabelEntity;
import com.yuqian.itax.user.entity.vo.CrowdLabelVO;
import com.yuqian.itax.user.service.CrowdLabelChangeService;
import com.yuqian.itax.user.service.CrowdLabelService;
import com.yuqian.itax.user.service.MemberCrowdLabelRelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service("crowdLabelService")
public class CrowdLabelServiceImpl extends BaseServiceImpl<CrowdLabelEntity,CrowdLabelMapper> implements CrowdLabelService {

    @Autowired
    private MemberCrowdLabelRelaService memberCrowdLabelRelaService;

    @Autowired
    private CrowdLabelChangeService crowdLabelChangeService;

    @Resource
    private MemberAccountMapper memberAccountMapper;

    @Override
    public List<Long> queryMemberIdByOemCodeAndStatus(String oemCode) {
        return this.mapper.queryMemberIdByOemCodeAndStatus(oemCode);
    }

    @Override
    public PageInfo<CrowdLabelVO> listPageCrowdLabel(CrowdLabelVO query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(mapper.listPageCrowdLabel(query));
    }

    @Override
    public List<CrowdLabelVO> listCrowdLabel(CrowdLabelVO query) {
        return mapper.listPageCrowdLabel(query);
    }

    @Override
    @Transactional(rollbackFor={Exception.class})
    public void updateStatusByid(Long id,String userName) {
        CrowdLabelEntity crowdLabelEntity = new CrowdLabelEntity();
        crowdLabelEntity.setId(id);
        crowdLabelEntity = mapper.selectOne(crowdLabelEntity);
        crowdLabelEntity.setStatus(2);
        crowdLabelEntity.setMemberUserNum(0);
        crowdLabelEntity.setUpdateUser(userName);
        crowdLabelEntity.setUpdateTime(new Date());
        mapper.updateByPrimaryKey(crowdLabelEntity);
        memberCrowdLabelRelaService.deleteAccountByCrowdLabelId(id);
    }

    @Override
    public CrowdLabelEntity queryCrowdLabelByLabelName(String labelName,String oemCode) {
        return this.mapper.queryCrowdLabelByLabelName(labelName,oemCode);
    }

    @Override
    public CrowdLabelEntity queryCrowdLabelByLabelNameNotId(String labelName, String oemCode, Long id) {
        return mapper.queryCrowdLabelByLabelNameNotId(labelName,oemCode,id);
    }

    @Override
    public List<CrowdLabelEntity> getCrowdLabelByLabelName(String labelName, String oemCode) {
        return this.mapper.getCrowdLabelByLabelName(labelName,oemCode);
    }

    @Override
    public List<CrowdLabelEntity> queryCrowdLabelByOemCode(String oemCode, Integer status) {
        return this.mapper.queryCrowdLabelByOemCode(oemCode,status);
    }

    @Override
    public List<CrowdLabelEntity> queryByAccessPartyId(Long accessPartyId) {
        return mapper.queryByAccessPartyId(accessPartyId);
    }

    @Override
    public Integer getActivityByCrowdId(Long crowdId) {
        return mapper.getActivityByCrowdId(crowdId);
    }

    @Override
    @Transactional(rollbackFor={Exception.class})
    public void addCrowd(CrowdLabelEntity crowdLabelEntity) {
        List<Long> accessUserList = new ArrayList<>();
        if (crowdLabelEntity.getAccessPartyId() != null){
            accessUserList = check(crowdLabelEntity.getAccessPartyId(),crowdLabelEntity.getOemCode());
        }
        this.insertSelective(crowdLabelEntity);
        CrowdLabelEntity entity = this.queryCrowdLabelByLabelName(crowdLabelEntity.getCrowdLabelName(),crowdLabelEntity.getOemCode());
        if (crowdLabelEntity.getAccessPartyId() != null){
            if (accessUserList != null && accessUserList.size()>0){
                memberCrowdLabelRelaService.addUserByH5Access(accessUserList,entity.getOemCode(),entity.getId(),entity.getAddUser());
                //跟新用户数
                entity.setMemberUserNum(accessUserList.size());
                this.editByIdSelective(entity);
            }
        }
        crowdLabelChangeService.addCrowdLabelChange(crowdLabelEntity,crowdLabelEntity.getAddUser(),"新增人群标签");

    }

    @Override
    @Transactional(rollbackFor={Exception.class})
    public void updateCrowd(CrowdLabelEntity crowdLabelEntity) {
        //  指定接入方
        if (crowdLabelEntity.getAccessPartyId() != null){
            CrowdLabelEntity  entity = this.findById(crowdLabelEntity.getId());
            // 接入方没改变
            if (crowdLabelEntity.getAccessPartyId().equals(entity.getAccessPartyId())){
                this.editByIdSelective(crowdLabelEntity);
                crowdLabelEntity = this.findById(crowdLabelEntity.getId());
                crowdLabelChangeService.addCrowdLabelChange(crowdLabelEntity,crowdLabelEntity.getUpdateUser(),"修改人群标签名称");
            }else{
                List<CrowdLabelEntity> crowdList = queryByAccessPartyId(crowdLabelEntity.getAccessPartyId());
                if (crowdList != null && crowdList.size()>0){
                    throw new BusinessException("该接入方已绑定人群标签");
                }
                List<Long> accessUserList = check(crowdLabelEntity.getAccessPartyId(),crowdLabelEntity.getOemCode());
                //删除用户与标签的关系表
                memberCrowdLabelRelaService.deleteAccountByCrowdLabelId(crowdLabelEntity.getId());
                if (accessUserList != null){
                    //新增
                    if (accessUserList.size()>0){
                        memberCrowdLabelRelaService.addUserByH5Access(accessUserList,entity.getOemCode(),entity.getId(),crowdLabelEntity.getUpdateUser());
                    }
                    //跟新用户数
                    crowdLabelEntity.setMemberUserNum(accessUserList.size());
                    this.editByIdSelective(crowdLabelEntity);
                    crowdLabelEntity = this.findById(crowdLabelEntity.getId());
                    crowdLabelChangeService.addCrowdLabelChange(crowdLabelEntity,crowdLabelEntity.getAddUser(),"修改接入方");
                }

            }
        }else{
            this.editByIdSelective(crowdLabelEntity);
            crowdLabelEntity = this.findById(crowdLabelEntity.getId());
            crowdLabelChangeService.addCrowdLabelChange(crowdLabelEntity,crowdLabelEntity.getUpdateUser(),"修改人群标签名称");
        }



    }

    @Override
    public OemAccessPartyDetailVO getDetailInfo(Long crowdLabelId) {
        return mapper.getDetailInfo(crowdLabelId);
    }

    /**
     * 校验该H5接入方下的用户是否在该oem机构下的人群标签中添加过
     * @param id
     * @param oemCode
     * @return
     */
    public List<Long> check(Long id,String oemCode){
        List<Long> accessUserList = memberAccountMapper.queryIdByAccessPartyId(id);
        List<Long> memberIdList = this.queryMemberIdByOemCodeAndStatus(oemCode);
        // 验证H5下用户是否在其他人群标签中
        if (accessUserList != null && accessUserList.size()>0){
            for(int i = 0;i < accessUserList.size();i++){
                if (CollectionUtil.isNotEmpty(memberIdList)){
                    for (int j=0;j<memberIdList.size();j++){
                        if (memberIdList.get(j).equals(accessUserList.get(i))){
                            throw new BusinessException("该H5下有用户已存在于其他人群标签，请删除后重试");
                        }
                    }
                }
            }
        }
        return accessUserList;
    }

    @Override
    public void updateMemberNumber(CrowdLabelEntity crowdLabel) {
        if (null == crowdLabel) {
            throw new BusinessException("更新数据为空");
        }
        mapper.updateByPrimaryKeySelective(crowdLabel);
    }
}

