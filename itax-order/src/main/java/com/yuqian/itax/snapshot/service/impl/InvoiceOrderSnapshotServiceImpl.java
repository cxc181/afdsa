package com.yuqian.itax.snapshot.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.snapshot.dao.InvoiceOrderSnapshotMapper;
import com.yuqian.itax.snapshot.entity.InvoiceOrderSnapshotEntity;
import com.yuqian.itax.snapshot.entity.query.InvoiceOrderSnapshotParkQuery;
import com.yuqian.itax.snapshot.entity.query.InvoiceSnapshotQuery;
import com.yuqian.itax.snapshot.entity.vo.InvoiceOrderSnapshotParkVO;
import com.yuqian.itax.snapshot.entity.vo.InvoiceSnapshotVO;
import com.yuqian.itax.snapshot.entity.vo.RechargeWithdrawSnapshotVO;
import com.yuqian.itax.snapshot.service.InvoiceOrderSnapshotService;
import com.yuqian.itax.util.util.MoneyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service("invoiceOrderSnapshotService")
public class InvoiceOrderSnapshotServiceImpl extends BaseServiceImpl<InvoiceOrderSnapshotEntity,InvoiceOrderSnapshotMapper> implements InvoiceOrderSnapshotService {

    @Override
    public List<InvoiceOrderSnapshotParkVO> queryInvoiceOrderSnapshotPark(InvoiceOrderSnapshotParkQuery query) {
        return mapper.queryInvoiceOrderSnapshotPark(query);
    }

    @Override
    public List<InvoiceSnapshotVO> invoiceCount(InvoiceSnapshotQuery query) {
        List<InvoiceSnapshotVO> list = mapper.invoiceCount(query);
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        if (StringUtils.isNotBlank(query.getOemCode())) {
            return list;
        }
        InvoiceSnapshotVO vo = new InvoiceSnapshotVO();
        vo.setOemName("汇总");
        for (InvoiceSnapshotVO v : list) {
            vo.setAddTotalAmt(MoneyUtil.moneyAdd(vo.getAddTotalAmt(), v.getAddTotalAmt()));
            vo.setAddZpAmt(MoneyUtil.moneyAdd(vo.getAddZpAmt(), v.getAddZpAmt()));
            vo.setAddPpAmt(MoneyUtil.moneyAdd(vo.getAddPpAmt(), v.getAddPpAmt()));
            vo.setUserNum(MoneyUtil.numAdd(vo.getUserNum(), v.getUserNum()));
            vo.setCompanyNum(MoneyUtil.numAdd(vo.getCompanyNum(), v.getCompanyNum()));
            vo.setOrderNum(MoneyUtil.numAdd(vo.getOrderNum(), v.getOrderNum()));
            vo.setTotalAmt(MoneyUtil.moneyAdd(vo.getTotalAmt(), v.getTotalAmt()));
            vo.setZpAmt(MoneyUtil.moneyAdd(vo.getZpAmt(), v.getZpAmt()));
            vo.setPpAmt(MoneyUtil.moneyAdd(vo.getPpAmt(), v.getPpAmt()));
        }
        list.add(vo);
        return list;
    }

    @Override
    public void updateOrInsertInvoiceOrderSnapshot(String startDate,String endDate ,Long userId,String oemCode) {
        mapper.updateOrInsertInvoiceOrderSnapshot(startDate,endDate,userId,oemCode);
    }

    @Override
    public void deleteInvoiceOrderSnapshotByDate(String startDate, String endDate,Long userId,String oemCode) {
        mapper.deleteInvoiceOrderSnapshotByDate(startDate,endDate,userId,oemCode);
    }
}

