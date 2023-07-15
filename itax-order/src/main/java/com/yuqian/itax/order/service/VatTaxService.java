package com.yuqian.itax.order.service;

import com.yuqian.itax.agent.entity.CalculateEntity;
import com.yuqian.itax.agent.entity.dto.VatIncomeDTO;
import com.yuqian.itax.agent.entity.vo.VatIncomeVO;
import com.yuqian.itax.common.base.exception.BusinessException;

import java.util.Map;

/**
 * 小程序计算器service
 *
 * @author yejian
 * @Date: 2020年11月12日 11:45:12
 */
public interface VatTaxService {
    /**
     * @Description 计算所得税
     * @Author Kaven
     * @Date 2020/3/27 10:38
     * @Param oemCode VatIncomeDTO
     * @Return VatIncomeVO
     * @Exception BusinessException
     */
    VatIncomeVO calculateIncomeTax(String oemCode, VatIncomeDTO dto) throws BusinessException;

    /**
     * @Description 计算增值税
     * @Author yejian
     * @Date 2020/3/27 10:38
     * @Param oemCode VatIncomeDTO
     * @Return VatIncomeVO
     * @Exception BusinessException
     */
    VatIncomeVO calculateVatTax(String oemCode, VatIncomeDTO dto) throws BusinessException;

    /**
     * @Description 个人所得税阶梯计算
     * @Author yejian
     * @Date 2020/03/27 14:54
     * @Return
     */
    Map<String, Object> calculateIncomeTax(CalculateEntity entity) throws BusinessException;

    /**
     * @Description 增值税阶梯计算
     * @Author yejian
     * @Date 2020/03/27 14:54
     * @Return
     */
    Map<String, Object> calculateVatTax(CalculateEntity entity) throws BusinessException;
}
