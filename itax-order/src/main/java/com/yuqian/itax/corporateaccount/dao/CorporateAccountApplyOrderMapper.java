package com.yuqian.itax.corporateaccount.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountApplyOrderEntity;
import com.yuqian.itax.corporateaccount.entity.vo.CorporateAccountApplyOrderVO;
import com.yuqian.itax.corporateaccount.query.CorporateAccountApplyOrderQuery;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountApplyOrdeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 对公户申请订单dao
 *
 * @Date: 2020年09月07日 09:11:04 
 * @author 蒋匿
 */
@Mapper
public interface CorporateAccountApplyOrderMapper extends BaseMapper<CorporateAccountApplyOrderEntity> {

    List<CorporateAccountApplyOrdeVO> queryCorporateAccountApplyOrderList(CorporateAccountApplyOrderQuery query);

    /**
     * 对公户申请-分页查询申请订单列表
     *
     * @return List<CorporateAccountApplyOrderVO>
     * @Author yejian
     * @Date 2020/9/8 15:28
     * @Param memberId
     * @Param oemCode
     */
    List<CorporateAccountApplyOrderVO> queryApplyOrderList(@Param("memberId") Long memberId, @Param("oemCode") String oemCode);

    /**
     * @Description 根据订单号查询记录
     * @Author yejian
     * @Date 2020/09/09 16:38
     * @Param orderNo
     * @Return CorporateAccountApplyOrderEntity
     */
    CorporateAccountApplyOrderEntity queryByOrderNo(String orderNo);

    /**
     * 对公户申请-查询申请中的订单数量
     *
     * @return int
     * @Author yejian
     * @Date 2020/9/29 15:28
     * @Param memberId
     * @Param oemCode
     * @Param companyId
     */
    int queryCorAccApplyingOrder(@Param("memberId") Long memberId, @Param("oemCode") String oemCode,
                                 @Param("companyId") Long companyId);
}

