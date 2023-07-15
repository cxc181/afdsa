package com.yuqian.itax.capital.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.capital.dao.UserCapitalChangeRecordMapper;
import com.yuqian.itax.capital.entity.UserCapitalChangeRecordEntity;
import com.yuqian.itax.capital.entity.query.UserCapitalChangeRecordQuery;
import com.yuqian.itax.capital.entity.vo.UserCapitalChangeRecordVO;
import com.yuqian.itax.capital.enums.CapitalChangeTypeEnum;
import com.yuqian.itax.capital.service.UserCapitalChangeRecordService;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("userCapitalChangeRecordService")
public class UserCapitalChangeRecordServiceImpl extends BaseServiceImpl<UserCapitalChangeRecordEntity,UserCapitalChangeRecordMapper> implements UserCapitalChangeRecordService {
    @Resource
    private UserCapitalChangeRecordMapper userCapitalChangeRecordMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveChangeRecord(Map<String,Object> params) {
        // 计算变动前后的金额
        Long beforeChangeAmount = (Long) params.get("beforeChangeAmount");
        Long changeAmount = (Long) params.get("changeAmount");
        Long changeAfterIn = beforeChangeAmount + changeAmount;
        Long changeAfterOut = changeAfterIn - changeAmount;

        // 保存资金账户入账记录
        UserCapitalChangeRecordEntity uccreIn = new UserCapitalChangeRecordEntity();
        uccreIn.setAddTime(new Date());
        uccreIn.setAddUser(params.get("memberId").toString());
        uccreIn.setUpdateTime(new Date());
        uccreIn.setUpdateUser(params.get("memberId").toString());
        uccreIn.setCapitalAccountId((Long) params.get("capitalAccountId"));
        uccreIn.setUserId((Long) params.get("memberId"));
        uccreIn.setUserType((Integer) params.get("userType"));
        uccreIn.setOemCode(params.get("oemCode").toString());
        uccreIn.setChangesAmount(changeAmount);
        uccreIn.setChangesBeforeAmount(beforeChangeAmount);
        uccreIn.setChangesAfterAmount(changeAfterIn);
        uccreIn.setChangesType(CapitalChangeTypeEnum.INCOME.getValue());
        uccreIn.setOrderType((Integer) params.get("orderType"));
        uccreIn.setOrderNo(params.get("orderNo").toString());
        this.userCapitalChangeRecordMapper.insert(uccreIn);

        // 保存资金账户出账记录
        UserCapitalChangeRecordEntity uccreOut = new UserCapitalChangeRecordEntity();
        uccreOut.setAddTime(new Date());
        uccreOut.setAddUser(params.get("memberId").toString());
        uccreOut.setUpdateTime(new Date());
        uccreOut.setUpdateUser(params.get("memberId").toString());
        uccreOut.setCapitalAccountId((Long) params.get("capitalAccountId"));
        uccreOut.setUserId((Long) params.get("memberId"));
        uccreOut.setUserType((Integer) params.get("userType"));
        uccreOut.setOemCode(params.get("oemCode").toString());
        uccreOut.setChangesAmount(changeAmount);
        uccreOut.setChangesBeforeAmount(changeAfterIn);
        uccreOut.setChangesAfterAmount(changeAfterOut);
        uccreOut.setOrderType((Integer) params.get("orderType"));

        if(uccreOut.getOrderType() == 5){// 工商注册订单，置为“冻结”状态
            uccreOut.setChangesType(CapitalChangeTypeEnum.FROZEN.getValue());
        }else{// 会员升级订单，置为“支出”状态
            uccreOut.setChangesType(CapitalChangeTypeEnum.EXPENDITURE.getValue());
        }
        uccreOut.setOrderNo(params.get("orderNo").toString());
        this.userCapitalChangeRecordMapper.insert(uccreOut);
    }

    @Override
    public PageInfo<UserCapitalChangeRecordVO> queryUserCapitalChangeRecordEntityPageInfo(UserCapitalChangeRecordQuery userCapitalChangeRecordQuery) {
        PageHelper.startPage(userCapitalChangeRecordQuery.getPageNumber(),userCapitalChangeRecordQuery.getPageSize());
        return new PageInfo<>(mapper.queryUserCapitalChangeRecordEntityPageInfo(userCapitalChangeRecordQuery));
    }
    @Override
    public List<UserCapitalChangeRecordVO> queryUserCapitalChangeRecordEntityList(UserCapitalChangeRecordQuery userCapitalChangeRecordQuery) {
        return mapper.queryUserCapitalChangeRecordEntityPageInfo(userCapitalChangeRecordQuery);
    }
}

