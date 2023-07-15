package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.CommonProblemsEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.query.CommonProblemsQuery;
import com.yuqian.itax.system.entity.vo.CommonProblemsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 常见问题dao
 * 
 * @Date: 2019年12月08日 21:40:04 
 * @author 蒋匿
 */
@Mapper
public interface CommonProblemsMapper extends BaseMapper<CommonProblemsEntity> {

    List<CommonProblemsEntity> getCommomProbleListByOemCode(@Param("oemCode") String oemCode);

    List<CommonProblemsVO> getCommomProbleList(CommonProblemsQuery query);

    CommonProblemsEntity findCommonProblemsEntityByorderNumNotId(@Param("orderNum")  Integer orderNum,@Param("id")Long id ,@Param("oemCode")  String oemCode);

    void batchInsertCommonProblems(@Param("list") List<CommonProblemsEntity> list , @Param("oemCode")String oemCode, @Param("addTime") Date addTime, @Param("account")String account);

    CommonProblemsVO getCommonProblemsById(@Param("id") Long id);
}

