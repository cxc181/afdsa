package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.UserRelaEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户关系表dao
 * 
 * @Date: 2019年12月18日 14:04:55 
 * @author 蒋匿
 */
@Mapper
public interface UserRelaMapper extends BaseMapper<UserRelaEntity> {
    UserRelaEntity queryUserRelaEntityByUserId(@Param("userId") Long userId,@Param("userClass") Integer userClass);

    /**
     * 根据userID，和用户树查询用户关系表
     */
    UserRelaEntity queryUserRelaEntity(@Param("userId")Long userId, @Param("userTree")String userTree, @Param("userClass")Integer userClass);

    List<UserRelaEntity> queryUserRelaEntityByUserTree(@Param("userTree")String userTree);

    void updateUserRela(@Param("userTree")String userTree,@Param("id")Long id);

    /**
     * 查询系统用户关系
     * @param userId
     * @param userTree
     * @param notUserClass 排除的用户层级
     * @return
     */
    UserRelaEntity querySystemUserRelaEntity(@Param("userId")Long userId, @Param("userTree")String userTree, @Param("notUserClass")Integer notUserClass);
}

