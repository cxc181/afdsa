package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.MemberAddressEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.query.MemberAddressQuery;
import com.yuqian.itax.user.entity.vo.MemberAddressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 会员收件地址表dao
 * 
 * @Date: 2020年12月25日 14:35:49 
 * @author 蒋匿
 */
@Mapper
public interface MemberAddressMapper extends BaseMapper<MemberAddressEntity> {

    /**
     * 分页查询收件地址
     * @param query
     * @return
     */
    List<MemberAddressVO> listMemberAddress(MemberAddressQuery query);

    /**
     * 修改默认收货地址
     * @param memberId
     * @param oemCode
     * @param isDefault 是否默认 0-不默认 1-默认
     * @param updateUser
     * @param updateTime
     * @param oriDefault 查询默认状态 0-不默认 1-默认
     * @return
     */
    void updateDefault(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("isDefault") Integer isDefault,
                       @Param("updateUser")String updateUser, @Param("updateTime")Date updateTime, @Param("oriDefault") Integer oriDefault);

    /**
     * 获取默认收货地址
     * @param memberId
     * @param oemCode
     * @param isDefault
     * @param status
     * @return
     */
    MemberAddressVO queryDefaultAddress(@Param("memberId") Long memberId, @Param("oemCode") String oemCode,
                                            @Param("isDefault") Integer isDefault, @Param("status") Integer status);
}

