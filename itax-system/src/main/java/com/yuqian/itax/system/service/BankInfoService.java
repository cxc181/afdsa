package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.BankInfoEntity;
import com.yuqian.itax.system.dao.BankInfoMapper;
import com.yuqian.itax.system.entity.vo.BankInfoVO;

import java.util.List;

/**
 * 银行信息service
 * 
 * @Date: 2019年12月14日 11:35:19 
 * @author 蒋匿
 */
public interface BankInfoService extends IBaseService<BankInfoEntity,BankInfoMapper> {

    /**
     *
     * 支持银行列表
     * @return
     */
    List<BankInfoVO> listBankInfo();

    /**
     * 四要素要整
     */
    boolean check4ToBankCard(String oemCode,String bankUserName,String idCard,String bankNumber,String BankPhone);
}

