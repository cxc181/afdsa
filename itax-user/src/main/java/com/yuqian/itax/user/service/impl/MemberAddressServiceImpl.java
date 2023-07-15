package com.yuqian.itax.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.user.dao.MemberAccountMapper;
import com.yuqian.itax.user.dao.MemberAddressMapper;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberAddressEntity;
import com.yuqian.itax.user.entity.query.MemberAddressQuery;
import com.yuqian.itax.user.entity.vo.MemberAddressVO;
import com.yuqian.itax.user.enums.MemberAddressDefaultEnum;
import com.yuqian.itax.user.enums.MemberAddressStatusEnum;
import com.yuqian.itax.user.service.MemberAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;


@Service("memberAddressService")
public class MemberAddressServiceImpl extends BaseServiceImpl<MemberAddressEntity,MemberAddressMapper> implements MemberAddressService {

    @Resource
    private MemberAccountMapper memberAccountMapper;

    @Override
    public PageInfo<MemberAddressVO> listPageMemberAddress(MemberAddressQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(mapper.listMemberAddress(query));
    }

    @Override
    @Transactional
    public void add(MemberAddressEntity memberAddressEntity) {
        MemberAccountEntity memberAccountEntity = memberAccountMapper.selectByPrimaryKey(memberAddressEntity.getMemberId());
        if (memberAccountEntity == null) {
            throw new BusinessException("用户不存在");
        }
        //修改默认收货地址
        updateAddressNotDefault(memberAddressEntity, memberAccountEntity);

        memberAddressEntity.setStatus(MemberAddressStatusEnum.YES.getValue());
        memberAddressEntity.setAddTime(new Date());
        memberAddressEntity.setAddUser(memberAccountEntity.getMemberAccount());
        mapper.insertSelective(memberAddressEntity);
    }

    @Override
    @Transactional
    public void edit(MemberAddressEntity memberAddressEntity) {
        MemberAccountEntity memberAccountEntity = memberAccountMapper.selectByPrimaryKey(memberAddressEntity.getMemberId());
        if (memberAccountEntity == null) {
            throw new BusinessException("用户不存在");
        }
        MemberAddressEntity entity = mapper.selectByPrimaryKey(memberAddressEntity.getId());
        if (entity == null) {
            throw new BusinessException("编辑的收货地址不存在");
        }
        if (!Objects.equals(memberAddressEntity.getMemberId(), entity.getMemberId())) {
            throw new BusinessException("编辑的收货地址不属于当前登录用户");
        }
        //修改默认收货地址
        updateAddressNotDefault(memberAddressEntity, memberAccountEntity);
        memberAddressEntity.setMemberId(null);
        memberAddressEntity.setOemCode(null);
        memberAccountEntity.setStatus(null);
        memberAddressEntity.setUpdateTime(new Date());
        memberAccountEntity.setUpdateUser(memberAccountEntity.getMemberAccount());
        mapper.updateByPrimaryKeySelective(memberAddressEntity);
    }

    @Transactional
    public void updateAddressNotDefault(MemberAddressEntity memberAddressEntity, MemberAccountEntity memberAccountEntity) {
        if (Objects.equals(MemberAddressDefaultEnum.NO.getValue(), memberAddressEntity.getIsDefault())) {
            return;
        }
        mapper.updateDefault(memberAddressEntity.getMemberId(), memberAddressEntity.getOemCode(), MemberAddressDefaultEnum.NO.getValue()
                , memberAccountEntity.getMemberAccount(), new Date(), MemberAddressDefaultEnum.YES.getValue());
    }

    @Override
    public void delete(Long memberId, Long id) {
        MemberAccountEntity memberAccountEntity = memberAccountMapper.selectByPrimaryKey(memberId);
        if (memberAccountEntity == null) {
            throw new BusinessException("用户不存在");
        }
        MemberAddressEntity entity = new MemberAddressEntity();
        entity.setMemberId(memberId);
        entity.setOemCode(memberAccountEntity.getOemCode());
        entity.setId(id);
        mapper.delete(entity);
    }

    @Override
    public MemberAddressVO queryDefaultAddress(Long memberId, String oemCode) {
        return mapper.queryDefaultAddress(memberId, oemCode, MemberAddressDefaultEnum.YES.getValue(), MemberAddressStatusEnum.YES.getValue());
    }
}

