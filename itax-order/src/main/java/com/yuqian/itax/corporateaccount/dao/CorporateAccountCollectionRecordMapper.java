package com.yuqian.itax.corporateaccount.dao;

import com.yuqian.itax.corporateaccount.entity.CorporateAccountCollectionRecordEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.query.CorporateAccountCollectionRecordQuery;
import com.yuqian.itax.user.entity.vo.CorporateAccountCollectionRecordVO;
import com.yuqian.itax.user.entity.vo.CorporateAccountVO;
import com.yuqian.itax.user.entity.vo.CorporateAccountVOAdmin;
import com.yuqian.itax.user.entity.vo.IncomeAndExpenseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  对公户银行收款记录表dao
 * 
 * @Date: 2020年09月07日 09:12:46 
 * @author 蒋匿
 */
@Mapper
public interface CorporateAccountCollectionRecordMapper extends BaseMapper<CorporateAccountCollectionRecordEntity> {

    /**
     * @Description 查询对公户银行收款记录
     * @Author  HZ
     * @Date   2020/9/10 10:41
     * @Param   CorporateAccountCollectionRecordQuery
     * @Exception
    */
    List<CorporateAccountVOAdmin> queryCorpAccountCollectionRecordsAdmin(CorporateAccountCollectionRecordQuery query);


    /**
     * @Description 查询对公户银行收款记录
     * @Author  Kaven
     * @Date   2020/9/8 10:41
     * @Param   CorporateAccountCollectionRecordQuery
     * @Return  List<CorporateAccountCollectionRecordVO>
     * @Exception
     */
    List<CorporateAccountCollectionRecordVO> queryCorpAccountCollectionRecords(CorporateAccountCollectionRecordQuery query);

    /**
     * @Description 对公户银行收款记录收支统计
     * @Author  Kaven
     * @Date   2020/9/8 11:11
     * @Param   CorporateAccountCollectionRecordQuery
     * @Return  IncomeAndExpenseVO
     * @Exception
    */
    IncomeAndExpenseVO queryIncomeAndExpense(CorporateAccountCollectionRecordQuery query);

    /**
     * @Description 更新剩余可提现额度
     * @Author  Kaven
     * @Date   2020/9/9 14:48
     * @Param   recordId withdrawalAmount flag
     * @Return
     * @Exception
    */
    void updateRemainingWithdrawAmount(@Param("recordId") Long recordId,@Param("withdrawalAmount") Long withdrawalAmount, @Param("flag")Integer flag);
}

