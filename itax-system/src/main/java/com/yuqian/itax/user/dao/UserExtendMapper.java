package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.UserExtendEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户扩展表dao
 * 
 * @Date: 2019年12月08日 20:51:30 
 * @author 蒋匿
 */
@Mapper
public interface UserExtendMapper extends BaseMapper<UserExtendEntity> {

    /**
     * 更具userID获取用户拓展信息
     */
    UserExtendEntity getUserExtendByUserId(@Param("userId") Long userId);

    /**
     * 更具phone和oemCode获取用户拓展信息
     */
    List<UserExtendEntity> getUserExtendByPhoneAndOemCode(@Param("phone") String phone, @Param("oemCode")String oemCode);

    /**
     * 更具phone和oemCode获取用户拓展信息 不包过传入ID
     */
    List<UserExtendEntity> getUserExtendByPhoneAndOemCodeNotId(@Param("phone") String phone, @Param("oemCode")String oemCode,@Param("id")Long id);

}

