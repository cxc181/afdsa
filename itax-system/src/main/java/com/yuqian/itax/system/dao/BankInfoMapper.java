package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.BankInfoEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.vo.BankInfoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 银行信息dao
 * 
 * @Date: 2019年12月14日 11:35:19 
 * @author 蒋匿
 */
@Mapper
public interface BankInfoMapper extends BaseMapper<BankInfoEntity> {

    /**
     *
     * 支持银行列表
     * @return
     */
    List<BankInfoVO> listBankInfo();
}

