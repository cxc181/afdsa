package com.yuqian.itax.corporateaccount.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.corporateaccount.dao.CorporateAccountContOrderMapper;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountContOrderEntity;
import com.yuqian.itax.corporateaccount.entity.vo.CorporateAccountContOrderVO;
import com.yuqian.itax.corporateaccount.query.CorporateAccountContOrderQuery;
import com.yuqian.itax.corporateaccount.vo.CorpAccUnpaidContOrderVO;
import com.yuqian.itax.order.entity.vo.CreatCorpAccContOrderVO;

import java.util.List;

/**
 * 对公户续费订单表service
 * 
 * @Date: 2021年09月06日 16:10:35 
 * @author HZ
 */
public interface CorporateAccountContOrderService extends IBaseService<CorporateAccountContOrderEntity, CorporateAccountContOrderMapper> {

    /**
     * 分页查询
     * @param query
     * @return
     */
    PageInfo<CorporateAccountContOrderVO> getContOrderListPage(CorporateAccountContOrderQuery query);

    /**
     * 导出数据
     * @param query
     * @return
     */
    List<CorporateAccountContOrderVO> listPages(CorporateAccountContOrderQuery query);

    /**
     * 创建对公户续费订单
     * @param companyCorpAccId
     * @param currUserId
     * @return
     */
    CreatCorpAccContOrderVO creatContOrder(Long companyCorpAccId, Long currUserId);

    /**
     * 根据对公户id查询未支付订单列表
     * @param companyCorpAccId
     * @return
     */
    List<CorpAccUnpaidContOrderVO> queryUnpaidByCompanyCorpAccId(Long companyCorpAccId);

    /**
     * 取消订单
     * @param orderNo
     * @param updateAccount
     */
    void cancelOrder(String orderNo, String updateAccount);
}

