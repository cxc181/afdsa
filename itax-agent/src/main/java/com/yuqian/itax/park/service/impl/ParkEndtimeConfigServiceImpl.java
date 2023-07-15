package com.yuqian.itax.park.service.impl;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.dao.ParkEndtimeConfigMapper;
import com.yuqian.itax.park.entity.ParkEndtimeConfigEntity;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.po.ParkEndtimeConfigPO;
import com.yuqian.itax.park.service.ParkEndtimeConfigService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service("parkEndtimeConfigService")
public class ParkEndtimeConfigServiceImpl extends BaseServiceImpl<ParkEndtimeConfigEntity,ParkEndtimeConfigMapper> implements ParkEndtimeConfigService {

    @Autowired
    ParkService parkService;

    @Autowired
    TaxPolicyService taxPolicyService;

    @Override
    public ParkEndtimeConfigEntity findEndtimeConfig(Long parkId, Integer operType, Integer invoiceWay) {
        return this.mapper.queryEndtimeConfig(parkId,operType,invoiceWay);
    }

    @Override
    @Transactional
    public void insertParkEndtimeConfigInfo(ParkEndtimeConfigPO po,String userName) {
        ParkEndtimeConfigEntity entityZp=new ParkEndtimeConfigEntity();
        entityZp.setParkId(po.getParkId());
        entityZp.setInvoiceWay(1);
        ParkEndtimeConfigEntity parkEndtimeConfigEntityZp =this.selectOne(entityZp);
        if(parkEndtimeConfigEntityZp!=null && po.getZzStartTime() != null && po.getZzEndTime()!= null){
            parkEndtimeConfigEntityZp.setStartTime(po.getZzStartTime());
            parkEndtimeConfigEntityZp.setEndTime(po.getZzEndTime());
            parkEndtimeConfigEntityZp.setContent(po.getZzcontent());
            parkEndtimeConfigEntityZp.setUpdateTime(new Date());
            parkEndtimeConfigEntityZp.setUpdateUser(userName);
            this.editByIdSelective(parkEndtimeConfigEntityZp);
        }else if(parkEndtimeConfigEntityZp!=null && po.getZzStartTime() == null && po.getZzEndTime() == null){
            // 删除
            this.delById(parkEndtimeConfigEntityZp.getId());
        }else if(parkEndtimeConfigEntityZp == null && po.getZzStartTime() != null && po.getZzEndTime() != null)  {
            parkEndtimeConfigEntityZp=new ParkEndtimeConfigEntity();
            parkEndtimeConfigEntityZp.setParkId(po.getParkId());
            parkEndtimeConfigEntityZp.setOperType(1);
            parkEndtimeConfigEntityZp.setInvoiceWay(1);
            parkEndtimeConfigEntityZp.setStartTime(po.getZzStartTime());
            parkEndtimeConfigEntityZp.setEndTime(po.getZzEndTime());
            parkEndtimeConfigEntityZp.setContent(po.getZzcontent());
            parkEndtimeConfigEntityZp.setAddTime(new Date());
            parkEndtimeConfigEntityZp.setAddUser(userName);
            this.insertSelective(parkEndtimeConfigEntityZp);
        }

        ParkEndtimeConfigEntity entityDp=new ParkEndtimeConfigEntity();
        entityDp.setParkId(po.getParkId());
        entityDp.setInvoiceWay(2);
        ParkEndtimeConfigEntity parkEndtimeConfigEntityDp =this.selectOne(entityDp);
        if(parkEndtimeConfigEntityDp!=null && po.getDzStartTime()!= null && po.getDzEndTime() != null){
            parkEndtimeConfigEntityDp.setStartTime(po.getDzStartTime());
            parkEndtimeConfigEntityDp.setEndTime(po.getDzEndTime());
            parkEndtimeConfigEntityDp.setContent(po.getDzcontent());
            parkEndtimeConfigEntityDp.setUpdateTime(new Date());
            parkEndtimeConfigEntityDp.setUpdateUser(userName);
            this.editByIdSelective(parkEndtimeConfigEntityDp);
        }else if(parkEndtimeConfigEntityDp!=null && po.getDzStartTime() == null && po.getDzEndTime() == null){
            // 删除
            this.delById(parkEndtimeConfigEntityDp.getId());
        }else if(parkEndtimeConfigEntityDp == null && po.getDzStartTime() != null && po.getDzEndTime() != null){
            parkEndtimeConfigEntityDp=new ParkEndtimeConfigEntity();
            parkEndtimeConfigEntityDp.setParkId(po.getParkId());
            parkEndtimeConfigEntityDp.setOperType(1);
            parkEndtimeConfigEntityDp.setInvoiceWay(2);
            parkEndtimeConfigEntityDp.setStartTime(po.getDzStartTime());
            parkEndtimeConfigEntityDp.setEndTime(po.getDzEndTime());
            parkEndtimeConfigEntityDp.setContent(po.getDzcontent());
            parkEndtimeConfigEntityDp.setAddTime(new Date());
            parkEndtimeConfigEntityDp.setAddUser(userName);
            this.insertSelective(parkEndtimeConfigEntityDp);
        }
        //  核定征收没有成本确认时间配置
        TaxPolicyEntity taxPolicyEntity = taxPolicyService.queryTaxPolicyByParkId(po.getParkId(),1,1);
        if(taxPolicyEntity == null || taxPolicyEntity.getIncomeLevyType() == null || taxPolicyEntity.getIncomeLevyType().equals(2)){
            return ;
        }

        Calendar cal = Calendar.getInstance();
        Integer year = cal.get(Calendar.YEAR);
        if (!year.equals(po.getYear())){
            ParkEndtimeConfigEntity entity = queryByOperTypeAndParkIdAndYearAndQuarter(po.getParkId(),3,year,null);
            if (entity == null){
                throw new BusinessException("请先配置今年的成本确认时间");
            }
        }
        if (CollectionUtil.isEmpty(po.getQuarterTime())){
            return;
        }
        for (int i=1;i<5;i++){
            ParkEndtimeConfigEntity entity = queryByOperTypeAndParkIdAndYearAndQuarter(po.getParkId(),3,po.getYear(),i);
            if (entity == null){
                entity = new ParkEndtimeConfigEntity();
                entity.setParkId(po.getParkId());
                entity.setOperType(3);
                entity.setEndTime(po.getQuarterTime().get(i-1));
                entity.setYear(po.getYear());
                entity.setQuarter(i);
                entity.setAddTime(new Date());
                entity.setAddUser(userName);
                this.insertSelective(entity);
            }else{
                entity.setEndTime(po.getQuarterTime().get(i-1));
                entity.setYear(po.getYear());
                entity.setQuarter(i);
                entity.setUpdateTime(new Date());
                entity.setUpdateUser(userName);
                this.editByIdSelective(entity);
            }
        }
    }

    @Override
    public ParkEndtimeConfigEntity queryByOperTypeAndParkIdAndYearAndQuarter(Long parkId, Integer OperType, Integer year, Integer quarter) {
        // 查询指定税期年和季度的截止日配置
        List<ParkEndtimeConfigEntity> list = mapper.queryByOperTypeAndParkIdAndYearAndQuarter(parkId, OperType, year, quarter);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        // 不指定税期年，仅指定季度查询
/*        list = mapper.queryByOperTypeAndParkIdAndYearAndQuarter(parkId, OperType, null, quarter);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }else{
            return null;
        }*/
        return null;
    }
}

