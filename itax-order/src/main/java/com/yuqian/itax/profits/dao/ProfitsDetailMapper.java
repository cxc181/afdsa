package com.yuqian.itax.profits.dao;

import com.yuqian.itax.capital.entity.vo.ProfitDetailVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.nabei.entity.AchievementExcelVo;
import com.yuqian.itax.profits.entity.ProfitsDetailEntity;
import com.yuqian.itax.profits.entity.query.MemberProfitsGateWayQuery;
import com.yuqian.itax.profits.entity.query.MemberProfitsQuery;
import com.yuqian.itax.profits.entity.query.ProfitsDetailQuery;
import com.yuqian.itax.profits.entity.vo.MemberProfitsVO;
import com.yuqian.itax.profits.entity.vo.ProfitsDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 分润明细表dao
 * 
 * @Date: 2019年12月07日 20:16:04 
 * @author 蒋匿
 */
@Mapper
public interface ProfitsDetailMapper extends BaseMapper<ProfitsDetailEntity> {
    /**
     * 根据查询条件查询
     * @return
     */
    List<ProfitsDetailVO> profitsDetailList(ProfitsDetailQuery profitsDetailQuery);

    /**
     * @Description 查询会员分润记录（小程序端使用）
     * @Author  Kaven
     * @Date   2019/12/20 10:35
     * @Param  MemberProfitsQuery
     * @Return List<MemberProfitsVO>
     * @Exception
    */
    List<MemberProfitsVO> listMemberProfitsPage(MemberProfitsQuery query);

    /**
     * 根据条件查询分润明细数据
     * @param parmas
     * @return
     */
    List<ProfitsDetailEntity> findProfitsDetailByParams(Map<String,Object> parmas);

    /**
     * @Description 推广中心-会员分润记录查询
     * @Author Kaven
     * @Date 2020/6/5 17:24
     * @Param MemberProfitsQuery
     * @Return List<MemberProfitsVO>
     * @Exception
     */
    List<MemberProfitsVO> listMemberProfitsPageNew(@Param("userId") Long userId, @Param("oemCode") String oemCode,
                                                   @Param("profitsType") Integer profitsType, @Param("day") String day,
                                                   @Param("month") String month, @Param("startDate") String startDate,
                                                   @Param("endDate") String endDate, @Param("levelNo") Integer levelNo);

    /**
     * @Description 推广中心-分润记录-查询总分润
     * @Author Kaven
     * @Date 2020/6/10 3:40 下午
     * @Param
     * @Return
     * @Exception
     */
    Long queryTotalProfitsAmount(@Param("userId") Long userId, @Param("oemCode") String oemCode,
                                 @Param("profitsType") Integer profitsType, @Param("day") String day,
                                 @Param("month") String month, @Param("startDate") String startDate,
                                 @Param("endDate") String endDate, @Param("levelNo") Integer levelNo);

    /**
     *根据用户ID查询用户收益情况
     */
    Long queryEarningsByUserId(MemberProfitsGateWayQuery query);

    /**
     * 修改会员账号
     */
    void updateMemberAccountByMemberId( @Param("memberAccount")String memberAccount, @Param("remark") String remark, @Param("memberId") Long memberId);

    /**
     * 佣金提现分润记录
     * @param memberId
     * @param oemCode
     * @return
     */
    List<ProfitDetailVO> profitsDetailForWithdraw(@Param("memberId") Long memberId, @Param("oemCode") String oemCode,
                                                  @Param("maximalProfitDetailId") Long maximalProfitDetailId,
                                                  @Param("profitDetailIdList") List<String> profitDetailIdList);

    /**
     * 批量更新分润记录
     * @param memberId
     * @param oemCode
     * @param maximalProfitDetailId
     * @param profitDetailIdList
     */
    void batchUpdateProfitsDetail(@Param("memberId") Long memberId, @Param("oemCode") String oemCode,
                                  @Param("maximalProfitDetailId") Long maximalProfitDetailId,
                                  @Param("profitDetailIdList") List<String> profitDetailIdList,
                                  @Param("withdrawOrderNo") String withdrawOrderNo);

    /**
     * 根据提现订单号查询分润记录
     * @param orderNo
     * @return
     */
    List<AchievementExcelVo> getByWithdrawOrderNo(String orderNo);
}

