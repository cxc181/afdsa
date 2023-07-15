package com.yuqian.itax.snapshot.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.snapshot.dao.OrderSnapshotMapper;
import com.yuqian.itax.snapshot.entity.OrderSnapshotEntity;
import com.yuqian.itax.snapshot.entity.query.OemCapitalQuery;
import com.yuqian.itax.snapshot.entity.query.OrderSnapshotQuery;
import com.yuqian.itax.snapshot.entity.vo.OemCapitalVO;
import com.yuqian.itax.snapshot.entity.vo.RechargeWithdrawSnapshotVO;
import com.yuqian.itax.snapshot.service.OrderSnapshotService;
import com.yuqian.itax.util.util.MoneyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service("orderSnapshotService")
public class OrderSnapshotServiceImpl extends BaseServiceImpl<OrderSnapshotEntity,OrderSnapshotMapper> implements OrderSnapshotService {

    @Override
    public List<RechargeWithdrawSnapshotVO> rechargeWithdraw(OrderSnapshotQuery query) {
        List<RechargeWithdrawSnapshotVO> list = mapper.rechargeWithdraw(query);
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        if (StringUtils.isNotBlank(query.getOemCode())) {
            return list;
        }
        RechargeWithdrawSnapshotVO vo = new RechargeWithdrawSnapshotVO();
        vo.setOemName("汇总");
        for (RechargeWithdrawSnapshotVO v : list) {
            vo.setRechargeAmt(MoneyUtil.moneyAdd(vo.getRechargeAmt(), v.getRechargeAmt()));
            vo.setWithdrawAmt(MoneyUtil.moneyAdd(vo.getWithdrawAmt(), v.getWithdrawAmt()));
        }
        list.add(vo);
        return list;
    }

    @Override
    public List<OemCapitalVO> oemCapital(OemCapitalQuery query) {
        List<OemCapitalVO> list = mapper.oemCapital(query);
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        if (StringUtils.isNotBlank(query.getOemCode())) {
            return list;
        }
        OemCapitalVO vo = new OemCapitalVO();
        vo.setOemName("汇总");
        for (OemCapitalVO v : list) {
            vo.setRegisterAmt(MoneyUtil.moneyAdd(vo.getRegisterAmt(), v.getRegisterAmt()));
            vo.setServiceFee(MoneyUtil.moneyAdd(vo.getServiceFee(), v.getServiceFee()));
            vo.setPostageFees(MoneyUtil.moneyAdd(vo.getPostageFees(), v.getPostageFees()));
            vo.setTotalTaxFee(MoneyUtil.moneyAdd(vo.getTotalTaxFee(), v.getTotalTaxFee()));
            vo.setUpgradeAmt(MoneyUtil.moneyAdd(vo.getUpgradeAmt(), v.getUpgradeAmt()));
            vo.setCorporateAmt(MoneyUtil.moneyAdd(vo.getCorporateAmt(), v.getCorporateAmt()));
            vo.setAddProfitAmt(MoneyUtil.moneyAdd(vo.getAddProfitAmt(), v.getAddProfitAmt()));
            vo.setTotalProfitAmt(MoneyUtil.moneyAdd(vo.getTotalProfitAmt(), v.getTotalProfitAmt()));
        }
        list.add(vo);
        return list;
    }

    @Override
    public void updateOrInsertOrderSnapshot(String startDate, String endDate, Long userId,String oemCode) {
        mapper.updateOrInsertOrderSnapshot(startDate,endDate,userId,oemCode);
    }

    @Override
    public void deleteOrderSnapshotByDate(String startDate, String endDate, Long userId,String oemCode) {
        mapper.deleteOrderSnapshotByDate(startDate,endDate,userId,oemCode);
    }

}

