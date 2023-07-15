package com.yuqian.itax.profits.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.nabei.entity.AchievementExcelVo;
import com.yuqian.itax.profits.dao.ProfitsDetailMapper;
import com.yuqian.itax.profits.entity.ProfitsDetailEntity;
import com.yuqian.itax.profits.entity.query.MemberProfitsGateWayQuery;
import com.yuqian.itax.profits.entity.query.MemberProfitsQuery;
import com.yuqian.itax.profits.entity.query.ProfitsDetailQuery;
import com.yuqian.itax.profits.entity.vo.MemberOrUserProfitsVO;
import com.yuqian.itax.profits.entity.vo.MemberProfitsRecordVO;
import com.yuqian.itax.profits.entity.vo.MemberProfitsVO;
import com.yuqian.itax.profits.entity.vo.ProfitsDetailVO;

import java.util.List;
import java.util.Map;

/**
 * 分润明细表service
 *
 * @Date: 2019年12月07日 20:16:04 
 * @author 蒋匿
 */
public interface ProfitsDetailService extends IBaseService<ProfitsDetailEntity,ProfitsDetailMapper> {

    PageInfo<ProfitsDetailVO> profitsDetailPageInfo(ProfitsDetailQuery profitsDetailQuery);

    List<ProfitsDetailVO> profitsDetailList(ProfitsDetailQuery profitsDetailQuery);

    /**
     * @Description 分页查询会员分润记录（小程序端）
     * @Author  Kaven
     * @Date   2019/12/20 10:11
     * @Param  MemberProfitsQuery
     * @Return PageInfo<MemberProfitsVO>
     * @Exception BusinessException
     */
    PageInfo<MemberProfitsVO> queryMemberProfitsList(MemberProfitsQuery query) throws BusinessException;

    /**
     * 根据条件查询分润明细数据
     * @param parmas
     * @return
     */
    List<ProfitsDetailEntity> findProfitsDetailByParams(Map<String,Object> parmas);

    /**
     * 分润结算
     * @param entity
     * @return
     */
    int updateProfitsDetailStatus(ProfitsDetailEntity entity, String updateUser);

    /**
     * 根据订单号保存分润明细
     * @param orderNo
     * @param updateAccount
     * @return
     */
    boolean saveProfitsDetailByOrderNo(String orderNo, String updateAccount) throws BusinessException;

    /**
     * @Description 推广中心-会员分润记录查询
     * @Author Kaven
     * @Date 2020/6/5 17:23
     * @Param MemberProfitsQuery
     * @Return MemberProfitsRecordVO
     * @Exception BusinessException
     */
    MemberProfitsRecordVO queryMemberProfitsListNew(MemberProfitsQuery query) throws BusinessException;

    /**
     * 根据用户ID查询用户收益情况
     */
    MemberOrUserProfitsVO  queryEarningsByUserId(MemberProfitsGateWayQuery query);
    /**
     * 修改会员账号
     */
    void updateMemberAccountByMemberId(String memberAccount,String remark,Long memberId);

    /**
     * 佣金提现分润记录
     * @param memberId
     * @param oemCode
     * @return
     */
    PageResultVo profitsDetailForWithdraw(Long memberId, String oemCode, Integer pageSize, Integer pageNumber);

    /**
     * 计算佣金提现分润记录总金额
     * @param memberId
     * @param oemCode
     * @param maximalProfitDetailId
     * @param profitDetailIdList
     * @return
     */
    Long countProfitDetailAmount(Long memberId, String oemCode, Long maximalProfitDetailId, List<String> profitDetailIdList);

    void batchUpdateProfitsDetail(Long memberId, String oemCode, Long maximalProfitDetailId, List<String> profitDetailIdList, String withdrawOrderNo);

    /**
     * 根据提现订单号查询分润记录
     * @param orderNo
     * @return
     */
    List<AchievementExcelVo> getByWithdrawOrderNo(String orderNo);
}

