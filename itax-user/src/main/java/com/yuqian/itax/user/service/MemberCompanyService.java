package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.BusinessScopeEntity;
import com.yuqian.itax.user.dao.MemberCompanyMapper;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.po.MemberCompanyPO;
import com.yuqian.itax.user.entity.query.AccessPartyCompanyQuery;
import com.yuqian.itax.user.entity.query.ExtendUserQuery;
import com.yuqian.itax.user.entity.query.IndividualQuery;
import com.yuqian.itax.user.entity.query.MemberCompanyQuery;
import com.yuqian.itax.user.entity.vo.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 我的企业service
 *
 * @Date: 2019年12月06日 10:42:12
 * @author yejian
 */
public interface MemberCompanyService extends IBaseService<MemberCompanyEntity, MemberCompanyMapper> {

    /**
     * 已过期的企业状态修改
     * @return
     */
    int updateOverdueCompanyStatus();

    /**
     * 查询我的企业详情
     * @return
     */
    MemberCompanyDetailVo getMemberCompanyDetail(Long memberId, String oemCode, Long id) throws BusinessException;

    /**
     * 查询企业详情(H5)
     * @return
     */
    MemberCompanyDetailH5VO getCompanyInfoH5ById(Long userId, Long id) throws BusinessException;
    /**
     * 分页查询企业信息
     * @param query
     * @return
     */
    PageInfo<MemberCompanyVo> listPageMemberCompany(MemberCompanyQuery query);

    /**
     * 查询企业信息
     * @param query
     * @return
     */
    List<MemberCompanyVo> listMemberCompanyInfo(MemberCompanyQuery query);

    /**
     * 更新状态
     * @param id
     * @param status
     * @param nickname
     */
    void updateStatus(Long id, int status, String nickname);

    /**
     * 查询详情
     * @param id
     * @return
     */
    MemberCompanyDetailAdminVO queryDetailById(Long id);

    /**
     * 查询企业状态是否正常
     * @param id
     * @return
     */
    Integer checkStatus(long memberId, Long id, String oemCode) throws BusinessException;

    /**
     * 查询我的所有企业列表
     * @return
     */
    List<MemberCompanyEntity> allMemberCompanyList(Long memberId, String oemCode);


    /**
     * 根据身份证查询可开票企业列表
     * @param idCardNumber
     * @param oemCode
     * @param status
     * @param invoiceCompanyName
     * @param remainInvoiceAmount
     * @param categoryBaseId
     * @param vatFeeRate
     * @return
     */
    List<MemberCompanyVo> getMemberCompanyByIdCard(String idCardNumber, String oemCode, Integer status, String invoiceCompanyName, Long remainInvoiceAmount, Long categoryBaseId, BigDecimal vatFeeRate,String orderBy);

    /**
     * 根据身份证统计可开票企业列表
     * @param idCardNumber
     * @param oemCode
     * @param status
     * @param invoiceCompanyName
     * @param remainInvoiceAmount
     * @param vatFeeRate
     * @return
     */
    Integer countMemberCompanyByIdCard(String idCardNumber, String oemCode, Integer status, String invoiceCompanyName, Long remainInvoiceAmount, BigDecimal vatFeeRate);

    /**
     * 查询我的企业证件列表
     * @param id 企业id
     * @return
     */
    MemberCompanyCertVo getMemberCompanyCertList(Long memberId, String oemCode, Long id);

    /**
     * 查询我的企业证件是否在园区列表
     * @param companyId 企业id
     * @param isInPark 是否在园区
     * @return
     */
    List<MemberCompanyCertInParkVo> getMemberCompanyCertInParkList(Long memberId, String oemCode, Long companyId, Integer isInPark);

    /**
     * 排除某个状态，统计企业信息条数
     * @param memberId
     * @param oemCode
     * @param status
     * @return
     */
    Integer countMemberCompany(Long memberId, String oemCode, Integer status);

    /**
     * @Description 根据会员ID查询企业列表
     * @Author  Kaven
     * @Date   2020/6/17 5:00 下午
     * @Param   ExtendUserQuery
     * @Return  List<MemberComVO>
     * @Exception
    */
    List<MemberComVO> queryMemberCompanyByMemberId(ExtendUserQuery query);

    /**
     * 校验是否有重名企业
     * @param oemCode 机构编码
     * @param prefix 前缀（园区所属城市）
     * @param suffix 后缀（行业示例名称）
     * @param shopName 字号
     * @param shopNameOne 字号一
     * @param shopNameTwo 字号二
     * @return
     */
    void checkCompanyName(String oemCode, String prefix, String suffix, String shopName, String shopNameOne, String shopNameTwo);

    /**
     * 校验是否有重名企业
     *
     * @param oemCode     机构编码
     * @param companyName 企业名称
     * @return
     */
    void checkCompanyName(String oemCode, String companyName);

    /**
     * 校验是否有重名企业
     *
     * @param companyName 企业名称
     * @return
     */
    void checkCompanyName( String companyName);

    /**
     * 查询用户企业列表是否配置对公户参数
     *
     * @param memberId 会员id
     * @param oemCode
     * @return List<MemberCompanyEntity>
     */
    List<MemberCompanyEntity> listCorpAccCompany(Long memberId, String oemCode);

    /**
     * 个体数据统计
     * @param query
     * @return
     */
    List<IndividualVO> individualCount(IndividualQuery query);
    /**
     * 查询税号为空得所有企业
     */
    List<MemberCompanyEntity> queryMemberCompanyListByEin();

    /**
     * 编辑企业
     * @param po
     */
    void update(MemberCompanyPO po,String account) throws ParseException;

    /**
     * 通过税号或企业id查询企业信息
     * @param id
     * @param ein
     * @param status
     * @param overdueStatus
     * @return
     */
    List<MemberCompanyBasicVo> queryCompanyBasicInfoByEinOrId(Long id,String ein,Integer status,Integer overdueStatus);

    /**
     * 通过税号查询非注销的企业
     */
    List<MemberCompanyEntity> queryMemberCompanyByEinStatusNotCancellation(@Param("ein") String ein);

    /**
     * 通过税号查询企业（查询正常或者最新的注销企业）
     */
    List<Long> queryMemberCompanyIdByEin(String ein);

    /**
     * 查询托管费续费详情
     * @param companyId
     */
    TrusteeFeeRenewDetailVO getTrusteeFeeRenewDetail(Long memberId , Long companyId);

    /**
     * 即将过期的企业状态修改
     * @return
     */
    int updateCompanyOverdueStatus(Integer surplusDays);

    /**
     * 根据memberId查询会员已过期企业信息
     */
    List<OverdueCompanyInfoVO> getOverdueByMemberId(Integer overdueDays);

    /**
     * 根据memberId查询即将过期企业信息
     */
    List<OverdueCompanyInfoVO> getWillExpireByMemberId(Integer surplusDays);

    /**
     * 更新企业过期状态
     * @param id
     * @param endTime
     */
    void updateOverdueStatus(Long id, Date endTime);

    /**
     * @Description 根据id和用户id查询
     * @Author  shudu
     * @Date   2021/4/30 11:26 上午
     * @Return  List<MemberCompanyVo>
     * @Exception
     */
    List<MemberCompanyVo> getMemberCompanyBymemberId(Long userId,String oemCode, List<String> categoryBaseIds,String operatorIdCardNo);

    void companyTransfer(Long companyId,String newOemCode,String newMemberAccount,String operUser);

    /**
     * 根据国金用户id查询企业列表
     * @param channelUserId
     * @param channelOemCode
     * @param oemCode
     * @return
     */
    List<GJUserCompanyVo> findCompanyListByChannelUserId(Long channelUserId,String channelOemCode,String oemCode);

    /**
     * 接入方查询企业详情
     * @param memberId
     * @param oemCode
     * @param id
     * @return
     * @throws BusinessException
     */
    CompanyDetailOfAccessPartyVo getCompanyDetailOfAccessParty(Long memberId, String oemCode, Long id) throws BusinessException;

    /**
     * 根据用户id集查询企业列表
     * @param query
     * @return
     */
    List<CompanyListOfAccessPartyVO> listByMemberIds(AccessPartyCompanyQuery query);

    /**
     * 根据企业id查询企业支持的税收分类编码
     * @param companyId
     * @return
     */
    List<String> findCompanyTaxCodeByCompanyId(Long companyId);

    /**
     * 根据经营范围基础库里的经营范围去查找经营范围表中是否存在该经营范围
     * @param content
     * @return
     */
    List<MemberCompanyEntity> getCompanyByBusinssContent(String content);
}