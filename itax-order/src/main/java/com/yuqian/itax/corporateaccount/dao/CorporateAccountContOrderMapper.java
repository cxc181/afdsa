package com.yuqian.itax.corporateaccount.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountContOrderEntity;
import com.yuqian.itax.corporateaccount.entity.vo.CorporateAccountContOrderVO;
import com.yuqian.itax.corporateaccount.query.CorporateAccountContOrderQuery;
import com.yuqian.itax.corporateaccount.vo.CorpAccUnpaidContOrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 对公户续费订单表dao
 * 
 * @Date: 2021年09月06日 16:10:35 
 * @author HZ
 */
@Mapper
public interface CorporateAccountContOrderMapper extends BaseMapper<CorporateAccountContOrderEntity> {

    /**
     *分页查询
     * @param query
     * @return
     */
    List<CorporateAccountContOrderVO> listPages(CorporateAccountContOrderQuery query);

    /**
     * 根据对公户id查询未支付订单列表
     * @param companyCorpAccId
     * @return
     */
    List<CorpAccUnpaidContOrderVO> queryUnpaidByCompanyCorpAccId(Long companyCorpAccId);
}

