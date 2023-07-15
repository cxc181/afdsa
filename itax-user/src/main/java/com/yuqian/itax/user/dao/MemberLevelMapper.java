package com.yuqian.itax.user.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.MemberLevelEntity;
import com.yuqian.itax.user.entity.vo.MemberLevelVO;
import com.yuqian.itax.user.entity.vo.MemberUpgradeRulesVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员等级管理dao
 * 
 * @Date: 2019年12月07日 20:48:00 
 * @author 蒋匿
 */
@Mapper
public interface MemberLevelMapper extends BaseMapper<MemberLevelEntity> {

    /**
     * @Description 根据条件查询会员等级
     * @Author  Kaven
     * @Date   2019/12/9 12:18
     * @Param  params
     * @Return MemberLevelEntity
    */
    MemberLevelEntity queryMemberLevel(Map<String, Object> params);

    void addBatch(@Param("list") List<MemberLevelEntity> list, @Param("oemCode") String oemCode, @Param("addTime") Date addTime, @Param("account") String account);

    /**
     * @Description 查询会员升级等级信息
     * @Author  Kaven
     * @Date   2020/6/3 17:06
     * @Param   oemCode
     * @Return  List<MemberUpgradeRulesVO>
     * @Exception
    */
    List<MemberUpgradeRulesVO> selectUpgradeInfo(@Param("oemCode") String oemCode);

    /**
     * 查询高于当前id的等级列表
     * @param id
     * @param oemCode
     * @param orderBy  ase 升序 DESC降序
     */
    List<MemberLevelVO> selectCanUpgradeList(@Param("id")Long id, @Param("oemCode")String oemCode,@Param("orderBy")String orderBy);
}

