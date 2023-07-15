package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.dao.MemberCrowdLabelRelaMapper;
import com.yuqian.itax.user.entity.MemberCrowdLabelRelaEntity;
import com.yuqian.itax.user.entity.vo.CrowdAccoutVO;
import com.yuqian.itax.user.entity.vo.CrowdLabelInsertVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人群标签会员关系表service
 * 
 * @Date: 2021年07月15日 15:49:17 
 * @author 蒋匿
 */
public interface MemberCrowdLabelRelaService extends IBaseService<MemberCrowdLabelRelaEntity,MemberCrowdLabelRelaMapper> {

   void addBatch(@Param("list") List<CrowdLabelInsertVO> list, @Param ("oemCode")String oemCode, @Param ("crowdLabelId")Long crowdLabelId, @Param ("updateUser")String updateUser);

   List<CrowdAccoutVO>  queryCrowdAccountById(Long crowdLabelId);

   /**
    * 根据标签id删除该标签下的用户
    * @param crowdLabelId
    */
   void deleteAccountByCrowdLabelId(Long crowdLabelId);

   /**
    * 根据用户id删除
    * @param list
    */
   void deleteAccountByMemberId( List<CrowdLabelInsertVO> list,Long crowdLabelId,String userAccount);

   /**
    * 根据会员id获取人群标签id
    * @param memberId
    * @param oemCode
    * @return
    */
   Long getCrowLabelIdByMemberId(Long memberId,String oemCode);

   /**
    * 根据H5接入方添加人群标签用户
    * @param list
    * @param oemCode
    * @param crowdLabelId
    * @param addUser
    */
   void addUserByH5Access(@Param("list") List<Long> list,@Param ("oemCode")String oemCode, @Param ("crowdLabelId")Long crowdLabelId, @Param ("addUser")String addUser);
}

