package com.yuqian.itax.pay.service;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.pay.entity.RepayDetailVO;

import java.util.List;

/**
 * 代付接口
 * @author：pengwei
 * @Date：2019/10/22 14:20
 * @version：1.0
 */
public interface PartiallyRepayService {

    /**
     * 代付接口
     * @param oemCode 机构编码
     * @param batchNo 出款批次号
     * @param totalAmount 出款总额(元，保留两位小数)
     * @param totalCount 出款笔数
     * @param orderTime 出款时间
     * @param detailList 出款明细
     * @param notifyUrl 出款结果通知地址
     * @param paramsEntity 代付配置参数
     * @return
     */
    JSONObject repay(String oemCode, String batchNo, String totalAmount, String totalCount, String orderTime, List<RepayDetailVO> detailList, String notifyUrl,OemParamsEntity paramsEntity);

    /**
     * 代付查询
     * @param oemCode 机构编码
     * @param batchNo 出款批次号
     * @param orderNo 出款订单号
     * @param orderTime 出款时间
     * @return
     */
    JSONObject repayQuery(String oemCode, String batchNo, String orderNo, String orderTime);

    /**
     * 申请额度
     * @param oemCode 机构编码
     * @param amount 金额
     * @return
     */
    JSONObject applyLimit(String oemCode, String amount);

    /**
     * 商户账户查询
     * @param oemCode 机构编码
     * @return
     */
    JSONObject merchantBalance(String oemCode);
}
