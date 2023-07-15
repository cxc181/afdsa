package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.MemberAddressEntity;
import com.yuqian.itax.user.dao.MemberAddressMapper;
import com.yuqian.itax.user.entity.query.MemberAddressQuery;
import com.yuqian.itax.user.entity.vo.MemberAddressVO;

import java.util.List;

/**
 * 会员收件地址表service
 * 
 * @Date: 2020年12月25日 14:35:49 
 * @author 蒋匿
 */
public interface MemberAddressService extends IBaseService<MemberAddressEntity,MemberAddressMapper> {

    /**
     * 分页查询收件地址
     * @param query
     * @return
     */
    PageInfo<MemberAddressVO> listPageMemberAddress(MemberAddressQuery query);

    /**
     * 添加
     * @param memberAddressEntity
     */
    void add(MemberAddressEntity memberAddressEntity);

    /**
     * 修改
     * @param memberAddressEntity
     */
    void edit(MemberAddressEntity memberAddressEntity);

    /**
     * 删除
     * @param memberId
     * @param id
     */
    void delete(Long memberId, Long id);

    /**
     * 获取默认收货地址
     * @param memberId
     * @param oemCode
     * @return
     */
    MemberAddressVO queryDefaultAddress(Long memberId, String oemCode);
}

