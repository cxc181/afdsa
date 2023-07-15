package com.yuqian.itax.user.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.query.AccessPartyCompanyQuery;
import com.yuqian.itax.user.entity.query.ExtendUserQuery;
import com.yuqian.itax.user.entity.query.IndividualQuery;
import com.yuqian.itax.user.entity.query.MemberCompanyQuery;
import com.yuqian.itax.user.entity.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 我的企业dao
 *
 * @Date: 2019年12月06日 10:42:12
 * @author yejina
 */
@Mapper
public interface MemberCompanyMapper extends BaseMapper<MemberCompanyEntity> {

    /**
     * 已过期的企业状态修改
     * @return
     */
   int updateOverdueCompanyStatus();

    /**
     * 查询我的企业列表
     * @param memberId 会员id
     * @return
     */
    List<MemberCompanyVo> listMemberCompany(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("type") Long type);

    /**
     * 查询我的企业详情
     * @param memberId 会员id
     * @param id 企业id
     * @return
     */
    MemberCompanyDetailVo getMemberCompanyDetail(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("id") Long id);

    /**
     * 查询企业信息
     * @param query
     * @return
     */
    List listMemberCompanyInfo(MemberCompanyQuery query);


    MemberCompanyDetailH5VO getCompanyInfoH5ById(@Param("id") Long id);
    /**
     * 更新状态
     * @param id
     * @param status
     * @param updateUser
     * @param updateTime
     */
    void updateStatus(@Param("id") Long id, @Param("status") int status, @Param("updateUser") String updateUser, @Param("updateTime") Date updateTime);

    /**
     * 查询详情
     * @param id
     * @return
     */
    MemberCompanyDetailAdminVO queryDetailById(@Param("id") Long id);

    /**
     * 查询我的所有企业列表
     * @return
     */
    List<MemberCompanyEntity> allMemberCompanyList(@Param("memberId") Long memberId, @Param("oemCode") String oemCode);

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
    List<MemberCompanyVo>  getMemberCompanyByIdCard(@Param("idCardNumber") String idCardNumber, @Param("oemCode") String oemCode, @Param("status") Integer status,
                                                    @Param("invoiceCompanyName") String invoiceCompanyName, @Param("remainInvoiceAmount") Long remainInvoiceAmount,
                                                    @Param("categoryBaseId") Long categoryBaseId, @Param("vatFeeRate") BigDecimal vatFeeRate,
                                                    @Param("orderBy") String orderBy);

   /**
    * 统计资格
    * @param idCardNumber
    * @param oemCode
    * @param status
    * @param invoiceCompanyName
    * @param remainInvoiceAmount
    * @param vatFeeRate
    * @return
    */
    Integer countMemberCompanyByIdCard(@Param("idCardNumber") String idCardNumber, @Param("oemCode") String oemCode, @Param("status") Integer status,
                                       @Param("invoiceCompanyName") String invoiceCompanyName, @Param("remainInvoiceAmount") Long remainInvoiceAmount,
                                       @Param("vatFeeRate") BigDecimal vatFeeRate);

    /**
     * 查询我的企业证件列表
     * @param id 企业id
     * @return
     */
    List<MemberCompanyCertListVo> getMemberCompanyCertList(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("id") Long id);

    /**
     * 查询我的企业证件是否在园区列表
     * @param companyId 企业id
     * @param isInPark 是否在园区
     * @return
     */
    List<MemberCompanyCertInParkVo> getMemberCompanyCertInParkList(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("companyId") Long companyId, @Param("isInPark") Integer isInPark);

    /**
     * 排除某个状态，统计企业信息条数
     * @param memberId
     * @param oemCode
     * @param status
     * @return
     */
    Integer countMemberCompany(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("status") Integer status);

    /**
     * @Description 根据memberId查询企业列表
     * @Author  Kaven
     * @Date   2020/6/17 5:01 下午
     * @Param   ExtendUserQuery
     * @Return  List<MemberComVO>
     * @Exception
    */
     List<MemberComVO> queryMemberCompanyByMemberId(ExtendUserQuery query);

     /**
      * @Description 佣金钱包-提现-开票主体企业列表查询
      * @Author  Kaven
      * @Date   2020/6/23 11:26 上午
      * @Param   userId oemCode categoryName
      * @Return  List<MemberCompanyVo>
      * @Exception
     */
     List<MemberCompanyVo> listMemberCompanyForCommission(@Param("userId") Long userId, @Param("oemCode") String oemCode, @Param("categoryBaseIds") List<String> categoryBaseIds);

    /**
     * 校验字号是否有重名企业
     * @param oemCode 机构编码
     * @param shopName 字号
     * @param shopNameOne 字号一
     * @param shopNameTwo 字号二
     * @return
     */
     String checkCompanyNameByShopName(@Param("oemCode") String oemCode, @Param("shopName") String shopName, @Param("shopNameOne") String shopNameOne, @Param("shopNameTwo") String shopNameTwo);

     /**
      * 校验是否有重名企业
      * @param oemCode
      * @param companyName
      */
     int checkCompanyName(@Param("oemCode") String oemCode, @Param("companyName") String companyName);


    /**
     * 校验是否有重名企业
     * @param companyName
     */
    int checkCompanyNameNotOemCode(@Param("companyName") String companyName);

    /**
      * 查询我的企业列表
      * @param oemCode
      * @param regPhone 经营者手机号
      * @param companyName 企业名称
      * @param idCard 身份证号码
      * @param status 状态
      * @return
      */
     List<CompanyListApiVO> getCompanyListByQuery(@Param("oemCode") String oemCode, @Param("regPhone") String regPhone,
                                                  @Param("companyName") String companyName, @Param("idCard") String idCard,
                                                  @Param("status") Integer status);

 /**
  * 查询用户企业列表是否配置对公户参数(产品已勾选园区，机构已勾选园区)
  *
  * @param memberId 会员id
  * @param oemCode
  * @return List<MemberCompanyEntity>
  */
 List<MemberCompanyEntity> listCorpAccCompany(@Param("memberId") Long memberId, @Param("oemCode") String oemCode);

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
     * 通过税号或企业id查询企业信息
     * @param id
     * @param ein
     * @param status
     * @param overdueStatus
     * @return
     */
    List<MemberCompanyBasicVo> queryCompanyBasicInfoByEinOrId(@Param("id") Long id, @Param("ein") String ein, @Param("status") Integer status, @Param("overdueStatus") Integer overdueStatus);

    /**
     * 通过税号查询非注销的企业
     */
    List<MemberCompanyEntity> queryMemberCompanyByEinStatusNotCancellation(@Param("ein") String ein);


    /**
     * 通过税号查询正常或者最新的企业
     * @param ein
     * @return
     */
    List<Long> queryMemberCompanyIdByEin(@Param("ein") String ein);


    /**
     * 通过税号查询不是某个id得企业
     */
    MemberCompanyEntity queryMemberCompanyByEinAndNotId(@Param("ein") String ein, @Param("id") Long id);

    /**
     * 查询托管费续费详情
     * @param companyId
     */
    TrusteeFeeRenewDetailVO queryTrusteeFeeRenewDetail(@Param("memberId") Long memberId, @Param("companyId") Long companyId);

    /**
     * 即将过期的企业状态修改
     */
    int updateCompanyOverdueStatus(Integer surplusDays);

    /**
     * 根据memberId查询会员已过期企业信息
     */
    List<OverdueCompanyInfoVO> queryOverdueByMemberId(Integer overdueDays);

    /**
     * 根据memberId查询即将过期企业信息
     */
    List<OverdueCompanyInfoVO> queryWillExpireByMemberId(Integer surplusDays);

    /**
     * 更新企业过期状态
     * @param id
     * @param endTime
     */
    void updateOverdueStatus(Long id, Date endTime, Integer surplusDays);

    /**
     * @Description 根据id和用户id查询
     * @Author  shudu
     * @Date   2021/4/30 11:26 上午
     * @Return  List<MemberCompanyVo>
     * @Exception
     */
    List<MemberCompanyVo> getMemberCompanyBymemberId(@Param("userId") Long userId, @Param("oemCode") String oemCode, @Param("categoryBaseIds") List<String> categoryBaseIds, @Param("operatorIdCardNo") String operatorIdCardNo);

    /**
     * 根据国金用户id查询企业列表
     * @param channelUserId
     * @param channelOemCode
     * @param oemCode
     * @return
     */
    List<GJUserCompanyVo> findCompanyListByChannelUserId(@Param("channelUserId") Long channelUserId, @Param("channelOemCode") String channelOemCode, @Param("oemCode") String oemCode);

    //================个体户迁移 start

    /**
     * 迁移企业年开票记录
     * @param newCompanyId
     * @param newOemCode
     * @param operUser
     * @param oldCompanyId
     */
    void transferCompanyInvoiceRecord(@Param("newCompanyId") Long newCompanyId, @Param("newOemCode") String newOemCode, @Param("operUser") String operUser, @Param("oldCompanyId") Long oldCompanyId);

    /**
     * 迁移企业资源所在地
     * @param newCompanyId
     * @param newOemCode
     * @param operUser
     * @param oldCompanyId
     */
    void transferCompanyResourcesAddress(@Param("newCompanyId") Long newCompanyId, @Param("newOemCode") String newOemCode, @Param("operUser") String operUser, @Param("oldCompanyId") Long oldCompanyId);

    /**
     * 迁移企业开票类目
     * @param newCompanyId
     * @param newOemCode
     * @param operUser
     * @param oldCompanyId
     */
    void transferCompanyInvoiceategory(@Param("newCompanyId") Long newCompanyId, @Param("newOemCode") String newOemCode, @Param("operUser") String operUser, @Param("oldCompanyId") Long oldCompanyId);

    /**
     * 迁移企业税单
     * @param newCompanyId
     * @param operUser
     * @param oldCompanyId
     */
    void transferCompanyTaxBill(@Param("newCompanyId") Long newCompanyId, @Param("operUser") String operUser, @Param("oldCompanyId") Long oldCompanyId);
    //================个体户迁移 end

    /**
     * 接入方查询企业详情
     * @param memberId
     * @param oemCode
     * @param id
     * @return
     */
    CompanyDetailOfAccessPartyVo queryCompanyDetailOfAccessParty(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("id") Long id);

    /**
     * 根据用户id集查询企业列表
     * @param query
     * @return
     */
    List<CompanyListOfAccessPartyVO> queryListByMemberIds(AccessPartyCompanyQuery query);

    /**
     * 根据订单号查询注册企业
     * @param orderNo
     * @return
     */
    MemberCompanyEntity queryByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据企业id查询企业支持的税收分类编码
     * @param companyId
     * @return
     */
    List<String> findCompanyTaxCodeByCompanyId(@Param("companyId") Long companyId);

    /**
     * 根据经营范围基础库里的经营范围去查找经营范围表中是否存在该经营范围
     * @param content
     * @return
     */
    List<MemberCompanyEntity> getCompanyByBusinssContent(@Param("content") String content);

    /**
     * 根据企业名称查询该企业信息
     * @param companyName
     * @return
     */
    MemberCompanyEntity getMemberCompanyOne(@Param("companyName")String companyName);

    /**
     * 通过税号查询企业信息
     * @param ein
     * @return
     */
    MemberCompanyEntity getMemberCompanyInfo(@Param("ein")String ein);

}

