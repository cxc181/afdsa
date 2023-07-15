package com.yuqian.itax.pay.service;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.pay.entity.RepayDetailVO;


/**
 * 建行代付接口
 * @author：pengwei
 * @Date：2019/10/22 14:20
 * @version：1.0
 */
public interface CCBRepayService {

    /**
     * 代付接口
     * @param oemCode 机构编码
     * @param batchNo 出款批次号
     * @param totalAmount 出款总额(元，保留两位小数)
     * @param repayDetailVO 出款明细
     * @param paramsEntity 代付配置参数
     * @return
     */
    JSONObject repay(String oemCode, String batchNo, String totalAmount, RepayDetailVO repayDetailVO,OemParamsEntity paramsEntity);

    /**
     * 代付查询
     * @param oemCode 机构编码
     * @param batchNo 出款批次号
     * @param paramsEntity 代付配置参数
     * @return
     */
    JSONObject repayQuery(String oemCode, String batchNo,OemParamsEntity paramsEntity);

    /**
     * 商户账户查询
     * @param oemCode 机构编码
     * @param paramsEntity 代付配置参数
     * @return
     */
    JSONObject merchantBalance(String oemCode,OemParamsEntity paramsEntity);
}
