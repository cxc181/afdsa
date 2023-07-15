package com.yuqian.itax.user.dao;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.query.*;
import com.yuqian.itax.user.entity.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员账号表dao
 *
 * @Date: 2019年12月06日 10:48:28
 * @author Kaven
 */
@Mapper
public interface MemberAccountMapper extends BaseMapper<MemberAccountEntity> {
    /**
     * @Author Kaven
     * @Description 根据会员账号查询会员账号信息
     * @Date 14:39 2019/12/06
     * @Param params
     * @return MemberAccountEntity
     **/
    MemberAccountEntity queryMemberByAccount(Map<String, Object> params);


    /**
     * 根据层级账号获取会员层级信息
     * @param account
     * @param memberId
     * @return
     */
    MemberAccountEntity findMemberTreeByAccount(@Param(value = "account") String account,@Param(value = "memberId") Long memberId);
    /**
     * @Author HZ
     * @Description 根据条件差寻会员列表
     * @Date 14:39 2019/12/09
     **/
    List<MemberPageInfoVO> queryMemberList(MemberQuery memberQuery);

    /**
     * @Author wangkailing
     * @Description 根据条件差寻会员列表
     * @Date 14:39 2021/4/29
     **/
    List<BatchMenberInfoExportVO> batchMemberList(MemberQuery memberQuery);

    /**
     * 修改会员状态
     * @param id
     * @param status
     * @param updateUser
     * @param updateTime
     */
    void updateStatus(@Param("id")Long id, @Param("status")Integer status,
                      @Param("updateUser")String updateUser, @Param("updateTime") Date updateTime);

    /**
     * @Author yejian
     * @Description 查询员工列表
     * @Date 2020/06/03 10:18
     * @param memberId
     * @param oemCode
     * @param memberType 1-会员 2-员工
     * @param pushMemberId
     * @param nameOrPhone
     * @param status 状态  1-正常 0-禁用 2-注销
     **/
    List<MemberAccountEntity> queryMemberEmployeeList(@Param("memberId")Long memberId, @Param("oemCode")String oemCode,
                                                      @Param("memberType")Integer memberType, @Param("pushMemberId")Long pushMemberId,
                                                      @Param("nameOrPhone")String nameOrPhone, @Param("status")Integer status);

    /**
     * @Author yejian
     * @Description 查询城市服务商下所有推广的用户
     * @Date 2020/06/03 10:18
     **/
    List<MemberAccountEntity> getAllSubNodes(@Param("memberId") Long memberId);

    /**
     * @Description 根据订单状态查询下级推广用户列表
     * @Author  Kaven
     * @Date   2019/12/18 10:39
     * @Param  userId oemCode status
     * @Return List
    */
    List<MemberVO> getMemberListByStatus(MemberExtendQuery query);

    /**
     * @Description 查询会员推广统计信息
     * @Author  Kaven
     * @Date   2019/12/18 10:58
     * @Param  userId oemCode memberType
     * @Return MemberExtendVO
     * @Exception
    */
    MemberExtendVO queryRegOrderStats(MemberExtendQuery query);

    /**
     * 统计会员推广累计数
     * @param query
     * @return
     */
    MemberExtendVO  statisRegOrderByUserId(MemberExtendQuery query);

    /**
     * 更新会员等级
     * @param id
     * @param memberLevel
     * @param levelName
     * @param updateUser
     * @param updateTime
     */
    void updateLevel(@Param("id")Long id, @Param("memberLevel")Long memberLevel, @Param("levelName")String levelName,
                     @Param("updateUser")String updateUser, @Param("updateTime")Date updateTime);

     /**
     * @Author yejian
     * @Description 查询员工一级和二级推广人
     * @Date 16:39 2020/03/12
     **/
    List<MemberAccountEntity> queryStaffExtendList(String memberTree);


    /**
     * @Author HZ
     * @Description 查询会员信息根据tree
     * @Date 16:39 2021/05/17
     **/
    List<MemberAccountEntity> queryMemberAccountListByMemberTree(String memberTree);

    /**
     * 统计员工本月累计开户数
     * @param memberId
     * @return
     */
    Long statisRegOrderByStaff(Long memberId);

    /**
     * 统计员工本月分润
     * @param upMemberId 城市服务商id
     * @param memberId 一级和二级推广人id
     * @return
     */
    Long statisProfitsByStaff(Long upMemberId, Long memberId);

    /**
     *查询除开注销得会员信息
     * @return
     */
    List<MemberAccountEntity> queryMemberByStatus(@Param("oemCode") String oemCode);

    /**
     * @Description 用户注册信息查询-拓展宝
     * @Author  Kaven
     * @Date   2020/3/25 10:29
     * @Param MemberRegisterQuery
     * @Return  List<MemberRegisterVO>
     * @Exception
    */
    List<MemberRegisterVO> queryRegistMemberList(MemberRegisterQuery query);

    /**
     * @Description 查询已邀请注册但未开户的用户列表
     * @Author  Kaven
     * @Date   2020/3/26 15:59
     * @Param   MemberExtendQuery
     * @Return  List<MemberVO>
     * @Exception
    */
    List<MemberVO> getInvitedRegUserList(MemberExtendQuery query);

    /**
     * @Description 查询一级推广用户数
     * @Author  Kaven
     * @Date   2020/5/18 11:01
     * @Param MemberExtendQuery
     * @Return  Long
     * @Exception
     */
    Long queryExtendUserCount(MemberExtendQuery query);

    /**
     * @Description 查询二级推广用户数
     * @Author  Kaven
     * @Date   2020/5/18 17:33
     * @Param MemberExtendQuery
     * @Return  Long
     * @Exception
     */
    Long querySecondExtendUserCount(MemberExtendQuery query);

    /**
     * @Description 查询会员个体企业统计信息
     * @Author  Kaven
     * @Date   2020/6/5 10:05
     * @Param   userId oemCode
     * @Return  MemberCoStatisticVO
     * @Exception
    */
    MemberCoStatisticVO queryMemberCoStatistic(ExtendUserQuery query);

    /**
     * @Description 查询企业开票统计列表
     * @Author  Kaven
     * @Date   2020/6/5 14:26
     * @Param   userId oemCode
     * @Return  MemberComVO
     * @Exception
    */
    MemberComVO queryComInvoiceStatistic(ExtendUserQuery query);

    /**
     * @Description 推广中心-业绩总览查询
     * @Author Kaven
     * @Date 2020/6/5 15:08
     * @Param userId oemCode
     * @Return AchievementStatVO
     * @Exception
     */
    AchievementStatVO queryAchievementStatistic(@Param("userId") Long userId, @Param("oemCode") String oemCode,
                                                @Param("extendType") Integer extendType, @Param("flag") Integer flag,
                                                @Param("invoiceMinMoney") Long invoiceMinMoney);

    /**
     * @Description 查询会员账号等级分润信息
     * @Author  yejian
     * @Date   2020/6/8 13:05
     * @Param   userId
     * @Return  MemberAccountLevelProfitsRuleVO
     */
    MemberAccountLevelProfitsRuleVO queryMemberAccLevelProfit(@Param("userId") Long userId);

    /**
     * @Description 查询企业注册进度统计信息
     * @Author  Kaven
     * @Date   2020/6/6 4:14 下午
     * @Param   query
     * @Return  CompanyRegProgressVO
     * @Exception
    */
    CompanyRegProgressVO queryCompanyRegProgress(MemberExtendQuery query);
    /**
     * @Description 查询企业注册进度统计信息
     * @Author  Kaven
     * @Date   2020/6/6 4:14 下午
     * @Param   query
     * @Return  CompanyRegProgressVO
     * @Exception
     */
    CompanyRegProgressVO queryCompanyRegProgressByChannelServiceId(MemberExtendQuery query);

    /**
     * @Description 查询企业开票进度统计信息
     * @Author  Kaven
     * @Date   2020/6/6 4:43 下午
     * @Param   MemberExtendQuery
     * @Return  CompanyInvoiceProgressVO
     * @Exception
    */
    CompanyInvoiceProgressVO queryCompanyInvoiceProgress(MemberExtendQuery query);


    /**
     * @Description 查询企业开票进度统计信息
     * @Author  HZ
     * @Date   2021/4/30 4:43 下午
     * @Param   MemberExtendQuery
     * @Return  CompanyInvoiceProgressVO
     * @Exception
     */
    CompanyInvoiceProgressVO queryCompanyInvoiceProgressByChannelServiceId(MemberExtendQuery query);

    /**
     * @Description 根据订单状态查询推广用户订单列表（新推广中心）
     * @Author  Kaven
     * @Date   2020/6/8 9:30 上午
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception
    */
    List<ExtendMemberVO> queryRegOrderListByStatus(MemberExtendQuery query);

    /**
     * @Description 根据订单状态查询推广用户订单列表（新推广中心）
     * @Author  HZ
     * @Date   2021/4/30 9:30 上午
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception
     */
    List<ExtendMemberVO> queryRegOrderListByStatusByChannelServiceId(MemberExtendQuery query);
    /**
     * @Description 查询未注册企业的推广用户列表
     * @Author  Kaven
     * @Date   2020/6/8 9:59 上午
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception
    */
    List<ExtendMemberVO> queryUnRegMemberList(MemberExtendQuery query);

    /**
     * @Description 查询未注册企业的推广用户列表
     * @Author  HZ
     * @Date   2021/4/30 9:59 上午
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception
     */
    List<ExtendMemberVO> queryUnRegMemberListByChannelServiceId(MemberExtendQuery query);

    /**
     * @Description 查询未注册企业的推广用户列表
     * @Author  HZ
     * @Date   2021/4/30 9:59 上午
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception
     */
    Integer queryUnRegCompanyCountByChannelServiceId(MemberExtendQuery query);

    /**
     * @Description 根据订单状态查询总托管费
     * @Author  Kaven
     * @Date   2020/6/8 12:22 下午
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception
    */
    Long queryTotalRegistFee(MemberExtendQuery query);

    /**
     * @Description 根据订单状态查询总托管费
     * @Author  HZ
     * @Date   2021/4/30 12:22 下午
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception
     */
    Long queryTotalRegistFeeByChannelSreviceId(MemberExtendQuery query);

    /**
     * 统计开票额
     * @param memberId
     * @param oemCode
     * @param month 按月
     * @param year 按年
     * @return
     */
    Long queryInvoiceAmountByDate(@Param("memberId") Long memberId, @Param("oemCode") String oemCode,
                                  @Param("month") String month, @Param("year")String year);

    /**
     * 查询最近一次开票记录
     * @param memberId
     * @param oemCode
     * @return
     */
    JSONObject queryLastInvoice(@Param("memberId") Long memberId, @Param("oemCode") String oemCode);

    /**
     * 更新当前用户下级数据
     * @param memEntity
     */
    void updateSubordinate(MemberAccountEntity memEntity);

    /**
     * 更新当前用户下级数据的服务商信息
     * @param memEntity
     */
    void updateChannelInfoByMemberTree(MemberAccountEntity memEntity);
    /**
     * @Description 根据手机号或名字模糊查询会员账号信息
     * @Param nameOrPhone
     * @return MemberAccountEntity
     **/
    MemberAccountEntity queryMemberByNameOrPhone(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("nameOrPhone")String nameOrPhone);

    /**
     * @Description 查询直推用户数
     * @Author  Kaven
     * @Date   2020/6/18 9:25 上午
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception
    */
    Long queryExtendUserCountNew(MemberExtendQuery eQuery);

    /**
     * @Description 推广中心-修改备注信息
     * @Author  Kaven
     * @Date   2020/6/18 2:32 下午
     * @Param   MemberAccountEntity
     * @Return
     * @Exception
    */
    int updateRemark(MemberAccountEntity t);

    /**
     * @Description 会员中心-查询达标（开票金额大于10000）开票数（按企业分组）
     * @Author Kaven
     * @Date 2020/6/19 4:09 下午
     * @Param
     * @Return
     * @Exception
     */
    List<Long> queryCompanyInvoiceCount(MemberExtendQuery query);

    /**
     * 统计会员直推达标个体数
     *
     * @param ids             城市服务商自己，城市服务商直推，城市服务商员工
     * @param oemCode
     * @param invoiceMinMoney 开票最低金额
     * @return Long
     */
    Long statExtendStdComCount(@Param("ids") String ids, @Param("oemCode") String oemCode, @Param("invoiceMinMoney") Long invoiceMinMoney);

    //########################员注销相关账号修改 start

    /**
     * 修改上级账号
     * @param memberId
     * @return
     */
    int updateParentMemberAccountByMemberId(@Param(value = "id") Long memberId);

    /**
     * 所属员工账号
     * @param memberId
     * @return
     */
    int updateAttributionEmployeesAccountByMemberId(@Param(value = "id") Long memberId);

    /**
     * 上级钻石会员账号
     * @param memberId
     * @return
     */
    int updateUpDiamondAccountByMemberId(@Param(value = "id") Long memberId);

    /**
     * 上上级钻石会员账号
     * @param memberId
     * @return
     */
    int updateSuperDiamondAccountByMemberId(@Param(value = "id") Long memberId);

    /**
     * 上上级员工账号
     * @param memberId
     * @return
     */
    int updateSuperEmployeesAccountByMemberId(@Param(value = "id") Long memberId);
    //########################员注销相关账号修改 end


    AccountVO queryMemberByPhone(Map<String, Object> params);


    List<MemberAccountEntity> queryMemberByPhoneAndOemCode(Map<String, Object> params);
    /**
     * 根据用户ID查询用户累计分润
     */
    Long queryProfitsByUserId(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("flag") Integer flag);


//===============国金助手
    /**
     * 根据渠道用户id查询会员信息
     * @param gjMemberQuery
     * @return
     */
    List<GjMemberInfoVO> getMemberInfoByChannelServiceId(GjMemberQuery gjMemberQuery);

    /**
     * 国金推广用户业绩查询
     * @param query
     * @return
     */
    List<EmpManageOfPushListVO> getUserPushResultListByChannelUser(GjMemberQuery query);

    /**
     * 根据查询条件查询会员信息
     * @param query
     * @return
     */
    List<AgentMemberVO> queryAgentMemberList(AgentMemberQuery query);

    /**
     * 根据渠道服务商统计推广数据
     * @param channelServicerUsers
     * @param channelEmployeesUsers
     * @param oemCode
     * @return
     */
    List<GjPushStatisInfoVO> userPushResultStatisByChannelUser(@Param(value = "channelServicerUsers") List<Long> channelServicerUsers,@Param(value = "channelEmployeesUsers") List<Long> channelEmployeesUsers,@Param(value = "oemCode") String oemCode);

    /**
     * 根据会员id获取该企业的oem机构是否接入国金
     * @param id
     * @return
     */
    MemberAccountOemInfoVO queryMemberOemInfo(Long id);

    /**
     * 获取推送失败的数据
     * @return
     */
    List<CompanyPushVo> queryCompanyInfoByAuthPushState(Integer pushState);

    /**
     * 批量修改推送状态
     * @param list
     * @param pushState
     */
    void batchUpdateAuthPushState(List<CompanyPushVo> list, Integer pushState);

    void updateAuthPushStateById(Long id,Integer state);

    /**
     * 根据渠道映射id修改所属渠道合伙人和员工
     * @param query
     */
    void updateChannelServiceIdByChannelUserId(AgentMemberQuery query);

    /**
     * 国金数据统计
     * @param channelServiceIds
     * @param channelUserIds
     * @param oemCode
     * @return
     */
    Map<String,Object> gjCompanyStatisByChannelUser(@Param("channelServiceIds") List<Long> channelServiceIds,
                                                 @Param("channelUserIds") List<Long> channelUserIds,
                                                 @Param("oemCode") String oemCode);

    /**
     * 根据接入方id查询用户
     * @param accessPartyId
     * @return
     */
    List<Long> queryIdByAccessPartyId(@Param("accessPartyId") Long accessPartyId);

    /**
     * 根据接入方id查询用户
     * @param accessPartyId
     * @return
     */
    List<MemberAccountEntity> queryByAccessPartyId(Long accessPartyId);

    /**
     * 查询用户注册预订单
     * @param orderNo
     * @return
     */
    RegPreOrderVO queryRegOrderByOrderNo(String orderNo);
}

