package com.yuqian.itax.tax.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.tax.dao.ParkTaxBillMapper;
import com.yuqian.itax.tax.entity.TParkTaxBillEntity;
import com.yuqian.itax.tax.entity.query.ParkTaxBillQuery;
import com.yuqian.itax.tax.entity.query.ParkTaxBillUploadVoucherQuery;
import com.yuqian.itax.tax.entity.vo.ParkTaxBillVO;
import com.yuqian.itax.tax.entity.vo.ParkTaxBillXXJOBVO;

import java.io.IOException;
import java.util.List;

/**
 * 园区税单查询service
 * 
 * @Date: 2020年12月03日 10:36:14 
 * @author 蒋匿
 */
public interface ParkTaxBillService extends IBaseService<TParkTaxBillEntity, ParkTaxBillMapper> {

    /**
     *
     */
    public List<ParkTaxBillXXJOBVO> queryParkTaxBillByTime(ParkTaxBillQuery query) ;
    /**
     *根据企业税单统计园区税单
     */
    public List<ParkTaxBillXXJOBVO> queryParkTaxBillByCompanyTaxBill(ParkTaxBillQuery query) ;
    /**
     * 立即确认
     */
    void confirmation(Long id, String account);
    /**
     * 立即推送
     */
    void send(Long id, String account);
    /**
     * 园区税单分页(后台)
     */
    PageInfo<ParkTaxBillVO> queryParkTaxBillPageInfo(ParkTaxBillQuery query);


    List<ParkTaxBillVO> queryParkTaxBillList(ParkTaxBillQuery query);
    /**
     * MQ业务封装
     */
    void mq(JSONObject json);

    /**
     * 上传完税凭证
     * @param query
     */
    void uploadVouchers(ParkTaxBillUploadVoucherQuery query,String account);

    /**
     * mq解析完税凭证
     * @param json
     */
    void uploadVouchersMq(JSONObject json) throws IOException;
    /**
     * MXXJOB封装生成税单
     */
    void addTaxBillXXJOB(int type,Long parkId,String remark,String account);
}

