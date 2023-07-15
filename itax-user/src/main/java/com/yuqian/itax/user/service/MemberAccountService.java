package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.system.entity.vo.PosterBase64VO;
import com.yuqian.itax.user.dao.MemberAccountMapper;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.dto.*;
import com.yuqian.itax.user.entity.query.*;
import com.yuqian.itax.user.entity.vo.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员账号表service
 *
 * @Date: 2019年12月06日 10:48:28 
 * @author Kaven
 */
public interface MemberAccountService extends IBaseService<MemberAccountEntity,MemberAccountMapper> {
    /**
     * @Description 根据账号查询记录
     * @Author  Kaven
     * @Date   2019/12/6 14:35
     * @Param  account
     * @Param  oemCode
     * @Return MemberAccountEntity
     */
    MemberAccountEntity queryByAccount(String account, String oemCode);

    /**
     * 根据层级账号获取会员层级信息
     * @param account
     * @param memberId
     * @return
     */
    MemberAccountEntity findMemberTreeByAccount(String account,Long memberId);

    /**
     * @Description 用户注册
     * @Author  Kaven
     * @Date   2019/12/9 11:16
     * @Param  account oemCode inviterAccount memberName regChannel:0-小程序普通用户（普通会员或员工）注册 1-后台城市服务商注册
     * @Param userEntity 当高级合伙人和城市合伙人生成城市服务商的时候传入身份信息
     * @Return MemberAccountEntity
     */
    MemberAccountEntity registerAccount(String account, String oemCode, String inviterAccount, Integer memberlevel,
                                        UserEntity userEntity, String memberName, Integer regChannel, Integer sourceType,String channelCode) throws BusinessException;

    /**
     * @Description 更新用户省市信息
     * @Author  Kaven
     * @Date   2019/12/10 10:17
     * @Param  userId provinceCode cityCode cityName
     * @Exception BusinessException
     */
    void updateUserRegion(Long userId, String provinceCode, String cityCode, String cityName) throws BusinessException;

    /**
     * @Description 根据条件查询会员列表
     * @Author  HZ
     * @Date   2019/12/6 14:35
     * @Param  account
     * @Return MemberAccountEntity
     */
    PageInfo<MemberPageInfoVO> memberPageInfo(MemberQuery memberQuery);

    /**
     * @Description 根据条件查询会员列表
     * @Author  HZ
     * @Date   2019/12/6 14:35
     * @Param  account
     * @Return MemberAccountEntity
     */
    List<BatchMenberInfoExportVO> batchMemberInfo(MemberQuery memberQuery);

    /**
     * @Description 小程序端用户登录（注册）
     * @Author  Kaven
     * @Date   2019/12/11 10:19
     * @Param
     * oemCode机构编码
     * account 登录账号
     * memberName 会员昵称，员工注册时必传
     * verifyCode 验证码
     * inviterAccount 邀请人账号
     * memberType 注册会员类型 0普通会员 -1员工
     * jsCode 获取微信openId参数
     * @Return Map<String,Object>
     * @Exception BusinessException
     */
    Map<String,Object> userLogin(MemberLoginDTO loginDto) throws BusinessException;

    /**
     * @Description 修改支付密码
     * @Author yejian
     * @Date 2019/12/11 15:54
     * @param param
     * @return
     */
    void updatePayPassword(UpdatePayPasswordDTO param) throws BusinessException;

    /**
     * 更新会员状态
     * @param id
     * @param status
     * @param updateUser
     */
    void updateStatus(Long id, Integer status, String updateUser);

    /**
     * @Description 获取会员个人基本信息
     * @Author  Kaven
     * @Date   2019/12/16 9:59
     * @Param  userId
     * @Return MemberBaseInfoVO
     * @Exception BusinessException
     */
    MemberBaseInfoVO getMemberBaseInfo(Long userId) throws BusinessException;

    /**
     * @Description 用户昵称修改
     * @Author  Kaven
     * @Date   2019/12/16 10:18
     * @Param  userId nickName
     * @Exception BusinessException
     */
    void updateNickname(Long userId, String nickName) throws BusinessException;

    /**
     * @Description 推广中心-会员统计信息
     * @Author  Kaven
     * @Date   2019/12/18 15:44
     * @Param  query
     * @Return MemberExtendVO
     * @Exception BusinessException
     */
    MemberExtendVO memberExtendStats(MemberExtendQuery query) throws BusinessException;

    /**
     * @Author yejian
     * @Description 员工管理<团队概况(含本人推广业绩)>
     * @Date   2020/06/03 09:18
     * @param memberId
     * @return
     **/
    EmployeeManageOfTeamVO getEmployeeManageOfTeam(Long memberId, String oemCode) throws BusinessException;

    /**
     * @Author yejian
     * @Description 员工管理<业绩概况(含本人推广业绩)>
     * @Date   2020/06/05 14:18
     * @param entity
     * @return
     **/
    EmployeeManageOfSurveyVO getEmployeeManageOfSurvey(EmployeeManageOfSurveyDTO entity) throws BusinessException;

    /**
     * @Author yejian
     * @Description 员工管理<员工列表>
     * @Date   2020/06/05 14:18
     * @param query
     * @return
     **/
    List<EmployeeListVO> getEmployeeManageOfList(EmployeeManageOfListQuery query);

    /**
     * @Author yejian
     * @Description 员工管理<员工列表(员工业绩明细)>
     * @Date   2020/06/06 14:18
     * @param entity
     * @return
     **/
    EmployeeManageOfListDetailVO getEmployeeManageOfListDetail(EmployeeManageOfListDetailDTO entity) throws BusinessException;

    /**
     * @Author yejian
     * @Description 员工管理<下拉选择直推用户列表>
     * @Date   2020/06/06 14:18
     * @param memberId
     * @param empId
     * @param keyword
     * @return
     **/
    SelectEmpPushListVO selectEmpPushList(Long memberId, Long empId,String keyword) throws BusinessException;

    /**
     * @Author yejian
     * @Description 员工管理<员工直推用户列表>
     * @Date   2020/06/09 13:18
     * @param query
     * @return
     **/
    List<EmpManageOfPushListVO> getEmployeeManageOfPushList(EmployeeManageOfPushListQuery query) throws BusinessException;

    /**
     * @Author yejian
     * @Description 推广中心-直推用户列表-直推用户推广业绩
     * @Date   2020/06/10 13:18
     * @param userId
     * @return
     **/
    PushExtendResultVO queryPushExtendResult(Long userId) throws BusinessException;

    /**
     * 获取海报列表
     *
     * @param oemCode
     * @param entity
     * @param sourceType 操作小程序来源 1-微信小程序 2-支付宝小程序 4-字节跳动小程序
     * @return list
     * @Date 2020/08/07 10:39
     */
    List<PosterBase64VO> getPoster(String oemCode, GenqrcodeDTO entity, String sourceType);

    /**
     * 更新会员等级
     * @param id
     * @param memberLevel
     * @param levelName
     * @param updateUser
     * @param updateTime
     */
    void updateLevel(Long id, Long memberLevel, String levelName, String updateUser, Date updateTime);

    /**
     * 会员注销
     */
    MemberAccountEntity canleMember(Long id, String memberAccount, Integer status, String account);

    /**
     * @Description 用户实名认证（二要素验证）
     * @Author  Kaven
     * @Date   2020/2/13 10:18
     * @Param  userId-用户id
     * @Param oemCode-oem机构编码
     * @Param userName-姓名
     * @Param idCardNo-身份证号码
     * @Param expireDate-身份证有效期
     * @Param idCardFront-身份证正面照地址
     * @Param idCardBack-身份证反面照地址
     * @Param idCardAddr-身份证地址
     * @Param source-认证来源：0-内部 1-外部
     * @Return
     * @Exception BusinessException
     */
    void userAuth(Long userId, String oemCode, UserAuthDTO authDTO, Integer source) throws BusinessException, IOException;

    /**
     * @Description 更新用户实名信息
     * @Author  Kaven
     * @Date   2020/2/19 11:12
     * @Param  userId name idCardNo authStatus expireDate idCardAddr idCardFront idCardBack remark
     * @Return
     * @Exception BusinessException
     */
    void updateUserAuth(Long userId, String name, String idCardNo, String idCardFront,String idCardBack,String expireDate, String idCardAddr, Integer authStatus,String remark) throws BusinessException;

    /**
     * 查询除开注销得会员信息
     * @return
     */
    List<MemberAccountEntity> queryMemberByStatus(String oemCode);

    /**
     * @Description 用户注册信息查询-拓展宝
     * @Author  Kaven
     * @Date   2020/3/25 10:18
     * @Param   MemberRegisterQuery
     * @Return  PageResultVo
     * @Exception
     */
    PageResultVo<MemberRegisterVO> queryRegisterData(MemberRegisterQuery query);

    /**
     * @Description 小程序推广二维码获取-拓展宝
     * @Author  Kaven
     * @Date   2020/3/26 11:19
     * @Param   GenExtQrcodeDTO
     * @Return  String
     * @Exception
     */
    String generalizedQrCode(GenExtQrcodeDTO dto) throws BusinessException;

    /**
     * @Description 查询已邀请注册但未开户的用户列表
     * @Author  Kaven
     * @Date   2020/3/26 15:22
     * @Param   MemberExtendQuery
     * @Return  InvitedRegUserVO
     * @Exception  BusinessException
     */
    InvitedRegUserVO queryInvitedRegUser(MemberExtendQuery query) throws BusinessException;

    /**
     * @Description 查询员工数量（邀请上限和当前员工总数）
     * @Author  Kaven
     * @Date   2020/4/20 11:31
     * @Param memberId  oemCode
     * @Return  MemberCountVO
     * @Exception
     */
    MemberCountVO getStaffCount(Long memberId, String oemCode) throws BusinessException;

    /**
     * @Description 员工注销
     * @Author  Kaven
     * @Date   2020/4/20 11:54
     * @Param   memberId staffId oemCode
     * @Return  void
     * @Exception  BusinessException
     */
    void cancelStaff(Long memberId, Long staffId, String oemCode) throws BusinessException;

    /**
     * @Description 对外用户注册接口
     * @Author  Kaven
     * @Date   2020/5/13 9:53
     * @Param   mobile-注册手机号 oemCode-结构编码
     * @Return  MemberAccountEntity
     * @Exception  BusinessException
     */
    MemberAccountEntity extUserRegister(String mobile,String oemCode) throws BusinessException;

    /**
     * @Description 第三方用户实名认证（OCR/二要素认证）
     * @Author  Kaven
     * @Date   2020/5/13 11:23
     * @Param  userId-用户id
     * @Param oemCode-oem机构编码
     * @Param userName-姓名
     * @Param idCardNo-身份证号码
     * @Param expireDate-身份证有效期
     * @Param idCardFront-身份证正面照地址
     * @Param idCardBack-身份证反面照地址
     * @Return
     * @Exception BusinessException
     */
    void extUserAuth(String oemCode, String mobile, String userName, String idCardNo, String idCardFront, String idCardBack);

    /**
     * @Description 修改用户备注
     * @Author  Kaven
     * @Date   2020/6/5 9:13
     * @Param  currUserId userId remark
     * @Return
     * @Exception  BusinessException
     */
    int updateUserRemark(Long currUserId,Long userId, String remark,String oemCode) throws BusinessException;

    /**
     * @Description 推广中心-会员企业列表查询
     * @Author  Kaven
     * @Date   2020/6/5 9:46
     * @Param
     * @Return
     * @Exception  BusinessException
     */
    MemberCoStatisticVO listMemberCompany(ExtendUserQuery query) throws BusinessException;

    /**
     * @Description 推广中心-业绩总览查询
     * @Author  Kaven
     * @Date   2020/6/5 15:04
     * @Param   currUserId  oemCode
     * @Return  AchievementStatVO
     * @Exception  BusinessException
     */
    AchievementStatVO queryAchievementStatistic(Long currUserId, String oemCode) throws BusinessException;

    /**
     * @Description 查询会员账号等级分润信息
     * @Author  yejian
     * @Date   2020/6/8 13:05
     * @Param   userId
     * @Return  MemberAccountLevelProfitsRuleVO
     */
    MemberAccountLevelProfitsRuleVO queryMemberAccLevelProfit(Long userId);

    /**
     * @Description 推广中心-企业注册进度跟进
     * @Author  Kaven
     * @Date   2020/6/6 3:48 下午
     * @Param   MemberExtendQuery
     * @Return  CompanyRegProgressVO
     * @Exception  BusinessException
     */
    CompanyRegProgressVO queryCompanyRegProgress(MemberExtendQuery query) throws BusinessException;


    /**
     * @Description 推广中心-企业注册进度跟进
     * @Author  HZ
     * @Date   2021/4/30 3:48 下午
     * @Param   MemberExtendQuery
     * @Return  CompanyRegProgressVO
     * @Exception  BusinessException
     */
    CompanyRegProgressVO queryCompanyRegProgressByChannelServiceId(MemberExtendQuery query) throws BusinessException;

    /**
     * @Description 推广中心-企业开票进度跟进
     * @Author  Kaven
     * @Date   2020/6/6 4:36 下午
     * @Param   MemberExtendQuery
     * @Return  CompanyInvoiceProgressVO
     * @Exception  BusinessException
     */
    CompanyInvoiceProgressVO queryCompanyInvoiceProgress(MemberExtendQuery query) throws BusinessException;
    /**
     * @Description 推广中心-企业开票进度跟进
     * @Author  HZ
     * @Date   2021/4/30 4:36 下午
     * @Param   MemberExtendQuery
     * @Return  CompanyInvoiceProgressVO
     * @Exception  BusinessException
     */
    CompanyInvoiceProgressVO queryCompanyInvoiceProgressByChannelServiceId(MemberExtendQuery query) throws BusinessException;

    /**
     * @Description 会员中心-查询达标（开票金额大于10000）开票数（按企业分组）
     * @Author  Kaven
     * @Date   2020/6/19 4:07 下午
     * @Param   MemberExtendQuery
     * @Return  List<Long>
     * @Exception
     */
    List<Long> queryCompanyInvoiceCount(MemberExtendQuery query);

    /**
     * @Description 会员注册实名（云租）
     * @Author  Kaven
     * @Date   2020/6/23 3:58 下午
     * @Param   MemberRegisterDTO
     * @Return  Map<String, Object>
     * @Exception  BusinessException
     */
    Map<String, Object> userRegister(MemberRegisterDTO registerDto) throws BusinessException, IOException;

    /**
     * @Description 更新用户openId
     * @Author  jiangni
     * @Param   user 当前用户信息
     * @Param jsCode 微信jsCode
     * @Return Map<String, Object>
     */
    void updateMemberWechatOpenId(CurrUser user, String jsCode);

    /**
     * 修改会员的全部下级信息
     *
     * @param memberAccountEntity
     */
    void updateMemberAccount(MemberAccountEntity memberAccountEntity, String updateUser);

    /**
     * @Description 获取会员个人基本信息
     * @Author yejian
     * @Date 2020/11/12 14:30
     * @Param entity
     * @Return MemberBaseInfoApiVO
     * @Exception BusinessException
     */
    MemberBaseInfoApiVO getMemberBaseInfoApi(MemberBaseInfoApiDTO entity) throws BusinessException;

    /**
     * @Description 根据手机号查询记录（只包括税务顾问和城市服务商）
     * @Return MemberAccountEntity
     */
    AccountVO queryMemberByPhone(String phone, String oemCode);

    /**
     * @Description 根据手机号查询记录
     * @Return MemberAccountEntity
     */
    List<MemberAccountEntity> queryMemberByPhoneAndOemCode(String phone, String oemCode);
    /**
     * @Description 推广中心-业绩总览查询
     * @Author yejian
     * @Date 2020/11/18 15:04
     * @Param userId  会员id
     * @Param oemCode 机构编码
     * @Param levelNo 会员等级 0-税务顾问 1-城市服务商 2-城市合伙人 3-高级城市合伙人
     * @Return AchievementStatVO
     * @Exception BusinessException
     */
    AchievementStatVO queryAchievementStatisticApi(Long userId, String oemCode, Integer levelNo) throws BusinessException;

    //===============国金助手
    /**
     * 根据渠道用户id查询会员信息
     * @param gjMemberQuery
     * @return
     */
    List<GjMemberInfoVO> getMemberInfoByChannelServiceId(GjMemberQuery gjMemberQuery);

    /**
     * 根据渠道用户id查询会员信息
     * @param gjMemberQuery
     * @return
     */
    PageInfo<GjMemberInfoVO> getMemberInfoByChannelServiceIdPageInfo(GjMemberQuery gjMemberQuery);
    /**
     * 国金推广用户业绩查询（分页）
     * @param query
     * @return
     */
    PageInfo<EmpManageOfPushListVO> getUserPushResultPageByChannelUser(GjMemberQuery query);

    /**
     * 国金推广用户业绩查询
     * @param query
     * @return
     */
    List<EmpManageOfPushListVO> getUserPushResultListByChannelUser(GjMemberQuery query);
    /**
     *推广用户查询接口
     */
    PageInfo<AgentMemberVO> queryAgentMemberPageInfo(AgentMemberQuery query);
    /**
     * 修改推广关系
     */
    void updateChannelServiceIdMember(AgentMemberQuery query);

    /**
     * 修改推广关系
     */
    void updateChannelServiceIdMember(List<AgentMemberQuery> list);

    /**
     * 根据渠道服务商统计推广数据
     * @param channelUsers
     * @param oemCode
     * @return
     */
    List<GjPushStatisInfoVO> userPushResultStatisByChannelUser(List<Long> channelUsers,String oemCode);

    /**
     * 国金同步注册会员信息
     * @param gjUserAuthDTO
     * @param oemCode
     * @return
     */
    MemberAccountEntity syncAccountFromGJ(GJUserAuthDTO gjUserAuthDTO,String oemCode);
    /**
     * 国金账号登录
     * @param query
     * @return
     */
    String loginFormGJ(GjMemberQuery query);

    /**
     * 国金同步实名信息
     * @param gjUserAuthDTO
     * @param memberAccountEntity
     */
    String updateAuthFromGj(GJUserAuthDTO gjUserAuthDTO,MemberAccountEntity memberAccountEntity);

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
    List<CompanyPushVo> queryCompanyInfoByAuthPushState();

    /**
     * 推送国金
     * @param vo
     */
    void companyPushGJ(CompanyPushVo vo);

    /**
     * 国金数据统计
     * @param channelDirectServicesUserIds 直推服务商直推用户列表
     * @param channelFissionServicesUserIds 直推服务商裂变用户列表
     * @param channelDirectUserIds  直推用户列表
     * @param channelFissionUserIds 裂变用户列表
     * @param oemCode  云财oemcode
     * @return
     */
    Map<String,Object> gjDataStatisByChannelUser(List<Long> channelDirectServicesUserIds,List<Long> channelFissionServicesUserIds,
                                                 List<Long> channelDirectUserIds,List<Long> channelFissionUserIds,String oemCode);

    /**
     * h5页面用户注册/登录
     * @param dto
     * @return
     */
    Map<String,Object> loginOfAccessParty(AccessPartyLoginDTO dto);

    /**
     * 根据接入方Id查询用户
     * @param accessPartyId
     * @return
     */
    List<MemberAccountEntity> findByAccessPartyId(Long accessPartyId);

    /**
     * 查询用户注册订单
     * @param orderNo
     * @return
     */
    RegPreOrderVO queryRegOrderByOrderNo(String orderNo);

    /**
     * 税费测算用户注册/登录
     * @param dto
     * @return
     */
    Map<String, Object> loginOfTaxCalculator(TaxCalculatorLoginDTO dto);
}

