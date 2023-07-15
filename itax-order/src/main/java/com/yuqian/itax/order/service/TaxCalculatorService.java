package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.dao.InvoiceOrderMapper;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.dto.CalculateTaxDTO;
import com.yuqian.itax.order.entity.query.ParkRewardQuery;
import com.yuqian.itax.order.entity.vo.CalculateTaxVO;
import com.yuqian.itax.order.entity.vo.ParkRewardVO;

import java.util.List;
import java.util.Map;

/**
 * 税费测算service
 * 
 * @Date: 2022/09/26
 * @author lmh
 */
public interface TaxCalculatorService extends IBaseService<InvoiceOrderEntity,InvoiceOrderMapper> {

    /**
     * 计算税费
     * @param dto
     * @return
     */
    CalculateTaxVO calculateTax(CalculateTaxDTO dto);

    /**
     * 查看园区奖励
     * @param dto
     * @return
     */
    List<ParkRewardVO> queryParkReward(ParkRewardQuery dto);

    /**
     * 自定义分享
     * @param jsapi_ticket
     * @param url
     * @return
     */
    Map<String, String> customSharing(String jsapi_ticket, String url);
}