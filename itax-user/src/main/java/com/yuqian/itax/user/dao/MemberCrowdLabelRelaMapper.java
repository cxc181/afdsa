package com.yuqian.itax.user.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.MemberCrowdLabelRelaEntity;
import com.yuqian.itax.user.entity.vo.CrowdAccoutVO;
import com.yuqian.itax.user.entity.vo.CrowdLabelInsertVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 人群标签会员关系表dao
 * 
 * @Date: 2021年07月15日 15:49:17 
 * @author 蒋匿
 */
@Mapper
public interface MemberCrowdLabelRelaMapper extends BaseMapper<MemberCrowdLabelRelaEntity> {

    void addBatch(@Param("list")List<CrowdLabelInsertVO> list, @Param ("oemCode")String oemCode, @Param ("crowdLabelId")Long crowdLabelId, @Param ("updateUser")String updateUser, @Param ("addTime")Date addTime);

    List< CrowdAccoutVO>  queryCrowdAccountById(Long crowdLabelId);

    /**
     * 根据标签id删除该标签下的用户
     * @param crowdLabelId
     */
    void deleteAccountByCrowdLabelId(Long crowdLabelId);

    /**
     * 根据用户id删除
     * @param list
     */
    void deleteAccountByMemberId( List<CrowdLabelInsertVO> list);

    /**
     * 根据H5接入方添加人群标签用户
     * @param list
     * @param oemCode
     * @param crowdLabelId
     * @param addUser
     */
    void addUserByH5Access(@Param("list") List<Long> list,@Param ("oemCode")String oemCode, @Param ("crowdLabelId")Long crowdLabelId, @Param ("addUser")String addUser,@Param ("addTime")Date addTime);
	
}

