package com.yuqian.itax.point.service.impl;

import com.itextpdf.text.log.SysoCounter;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.point.dao.BuriedPointMapper;
import com.yuqian.itax.point.entity.BuriedPointEntity;
import com.yuqian.itax.point.service.BuriedPointService;
import com.yuqian.itax.util.util.StringUtil;
import org.apache.ibatis.jdbc.Null;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service("buriedPointService")
public class BuriedPointServiceImpl extends BaseServiceImpl<BuriedPointEntity,BuriedPointMapper> implements BuriedPointService {

    @Override
    @Transactional
    public void add(BuriedPointEntity buriedPointEntity, CurrUser currUser) {
        Date time = new Date();
        buriedPointEntity.setAddTime(time);
        buriedPointEntity.setOperTime(time);
        if(currUser != null){
            if(StringUtil.isNotBlank(currUser.getUseraccount())){
                buriedPointEntity.setAddUser(currUser.getUseraccount());
            }
        }
        mapper.insertSelective(buriedPointEntity);
    }
}

