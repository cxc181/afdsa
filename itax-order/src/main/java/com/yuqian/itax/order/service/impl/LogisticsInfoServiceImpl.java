package com.yuqian.itax.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.LogisticsInfoMapper;
import com.yuqian.itax.order.entity.*;
import com.yuqian.itax.order.enums.InvoiceOrderStatusEnum;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.system.entity.LogisCompanyEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.LogisCompanyService;
import com.yuqian.itax.user.service.CompanyResoucesApplyRecordService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.DeliveryUtils;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("logisticsInfoService")
public class LogisticsInfoServiceImpl extends BaseServiceImpl<LogisticsInfoEntity,LogisticsInfoMapper> implements LogisticsInfoService {


    @Autowired
    LogisCompanyService logisCompanyService;
    @Autowired
    DictionaryService dictionaryService;
    @Autowired
    private OrderService orderService;
    @Autowired
    CompanyResoucesApplyRecordService companyResoucesApplyRecordService;
    @Autowired
    private LogisticsInfoMapper logisticsInfoMapper;
    @Autowired
    private ConsumptionInvoiceOrderService consumptionInvoiceOrderService;
    @Autowired
    private ConsumptionInvoiceOrderChangeService consumptionInvoiceOrderChangeService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;

    @Override
    public List<LogisticsInfoEntity> queryLogisticsInfoList(String courierCompanyName,String courierNumber,String orderNo,String userName) {

        //先查看订单是否已签收如果是已签收则查询数据库否则就查询快递100
       /* CompanyResoucesApplyRecordEntity entity=new CompanyResoucesApplyRecordEntity();
        entity.setOrderNo(orderNo);
         CompanyResoucesApplyRecordEntity companyResoucesApplyRecordEntity=companyResoucesApplyRecordService.selectOne(entity);
       if(companyResoucesApplyRecordEntity!=null&&null!=companyResoucesApplyRecordEntity.getStatus()&&companyResoucesApplyRecordEntity.getStatus()==4){
            LogisticsInfoEntity logisticsInfoEntity=new LogisticsInfoEntity();
            logisticsInfoEntity.setCourierNumber(courierNumber);
            logisticsInfoEntity.setOrderNo(orderNo);
            logisticsInfoEntity.setCourierCompanyName(courierCompanyName);
            return mapper.select(logisticsInfoEntity);
        }*/
        if(StringUtils.isEmpty(courierCompanyName)){
            //throw new BusinessException("快递公司名称不能为空");
            return  new ArrayList<LogisticsInfoEntity>();
        }
        if(StringUtils.isEmpty(courierNumber)){
            //throw new BusinessException("快递号不能为空");
            return  new ArrayList<LogisticsInfoEntity>();
        }
        //获取快递编码
        LogisCompanyEntity logis = new LogisCompanyEntity();
        logis.setCompanyName(courierCompanyName);
        logis = logisCompanyService.selectOne(logis);
        if (null == logis) {
            log.info("快递公司名称有误-->" + courierCompanyName);
            return  new ArrayList<LogisticsInfoEntity>();
        }
        log.info("courierNumber=========="+courierNumber);
        log.info("companyCode=========="+logis.getCompanyCode());

        List<LogisticsInfoEntity> logisticsInfolist = logisticsInfoMapper.queryByOrderNo(orderNo);
        if (logisticsInfolist != null && logisticsInfolist.size()>0){
            return logisticsInfolist;
        }

        String result = DeliveryUtils.synQueryData(dictionaryService.getByCode("kuaidi100_key").getDictValue(),
                dictionaryService.getByCode("kuaidi100_companyno").getDictValue(), logis.getCompanyCode(),
                courierNumber, null, null, null, 1);

        List<LogisticsInfoEntity> logisticsInfoEntities=new ArrayList<>();
        if (StringUtil.isBlank(result)) {
            return logisticsInfoEntities;
        }

        //解析快递100返回结果
        JSONObject resultObj = JSONObject.parseObject(result);
        log.info(resultObj.toJSONString());
        if ("ok".equals(resultObj.getString("message")) && "200".equals(resultObj.getString("status"))) {

            // 查询快递公司名称
            logis = new LogisCompanyEntity();
            logis.setCompanyCode(resultObj.getString("com"));
            logis = logisCompanyService.selectOne(logis);

            //依次取出json对象中data数组并保存到数据库
            JSONArray array = resultObj.getJSONArray("data");
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                LogisticsInfoEntity logisInfo = new LogisticsInfoEntity();
                logisInfo.setOrderNo(orderNo);
                logisInfo.setCourierNumber(resultObj.getString("nu"));
                logisInfo.setCourierCompanyName(logis.getCompanyName());
                logisInfo.setLogisticsInfo(object.getString("context"));
                logisInfo.setLogisticsTime(DateUtil.parseTimesTampDate(object.getString("ftime")));
                logisInfo.setLogisticsStatus(tranStatus(object.getString("status")));
                logisInfo.setAddTime(new Date());
                logisInfo.setAddUser("xxljob");
                logisInfo.setRemark("快递100接口获取快递信息为已签收");
                if ("3".equals(resultObj.getString("state"))) {
                    mapper.insert(logisInfo);
                }
                logisticsInfoEntities.add(logisInfo);
            }

            //state	快递单当前状态，包括0在途，1揽收，2疑难，3签收，4退签，5派件，6退回等7个状态
            //如果是签收状态则修改订单状态为已完成
            if ("3".equals(resultObj.getString("state"))) {
                //  如果位消费发票待签收，则修改订单状态，添加消费发票完成时间，添加消费发票
                OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
                if (OrderTypeEnum.CONSUMPTION_INVOICE.getValue().equals(orderEntity.getOrderType()) && orderEntity.getOrderStatus() == 5){
                    ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity = new ConsumptionInvoiceOrderEntity();
                    consumptionInvoiceOrderEntity.setOrderNo(orderNo);
                    consumptionInvoiceOrderEntity = consumptionInvoiceOrderService.selectOne(consumptionInvoiceOrderEntity);
                    Date date =  new Date();
                    consumptionInvoiceOrderEntity.setUpdateTime(date);
                    consumptionInvoiceOrderEntity.setUpdateUser(userName);
                    consumptionInvoiceOrderEntity.setCompleteTime(date);
                    ConsumptionInvoiceOrderChangeEntity consumptionInvoiceOrderChangeEntity=new ConsumptionInvoiceOrderChangeEntity();
                    BeanUtils.copyProperties(consumptionInvoiceOrderEntity,consumptionInvoiceOrderChangeEntity);
                    consumptionInvoiceOrderChangeEntity.setStatus(2);
                    consumptionInvoiceOrderChangeEntity.setId(null);
                    consumptionInvoiceOrderChangeEntity.setAddUser(userName);
                    consumptionInvoiceOrderChangeEntity.setAddTime(new Date());
                    consumptionInvoiceOrderChangeEntity.setUpdateTime(null);
                    consumptionInvoiceOrderChangeEntity.setUpdateUser(null);
                    consumptionInvoiceOrderChangeService.add(consumptionInvoiceOrderChangeEntity);
                    //修改订单状态为已完成
                    orderService.updateOrderStatus(userName, consumptionInvoiceOrderEntity.getOrderNo(), 2);
                    consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);
                }

                // 开票自动签收
                if(Objects.equals(orderEntity.getOrderStatus(), InvoiceOrderStatusEnum.TO_BE_RECEIVED.getValue()) && OrderTypeEnum.INVOICE.getValue().equals(orderEntity.getOrderType())){
                    invoiceOrderService.confirmReceipt(orderEntity.getUserId(), orderEntity.getOemCode(), orderEntity.getOrderNo(),"admin");
                }
            }
        }else{
            //测试不让抛错误
            // throw  new BusinessException(resultObj.getString("message"));
        }
        log.info("logisticsInfoEntities:================"+logisticsInfoEntities.toString());
        return logisticsInfoEntities;
    }

    @Override
    public void deleteByOrderNo(String orderNo) {
        mapper.deleteByOrderNo(orderNo);
    }

    /**
     * 快递100明细状态转换
     * @param status
     * 0-待发货 1-已揽货 2-运输中 3-派送中 4-待取件  5-已签收 6-已收货 7-退货
     */
    public Integer tranStatus(String status){
        if("在途".equals(status)){
            return 2;
        }else if("揽收".equals(status)){
            return 1;
        }else if("疑难".equals(status)){
            return 0;
        }else if("签收".equals(status)){
            return 5;
        }else if("退签".equals(status)){
            return 7;
        }else if("派件".equals(status)){
            return 3;
        }else if("退回".equals(status)){
            return 7;
        }else{
            return 1;
        }
    }
}

