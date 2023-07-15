package com.yuqian.itax.xxljob.jobhandler.order;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderChangeEntity;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.query.ConsumptionInvoiceOrderQuery;
import com.yuqian.itax.order.entity.vo.ConsumptionInvoiceOrderVO;
import com.yuqian.itax.order.service.ConsumptionInvoiceOrderChangeService;
import com.yuqian.itax.order.service.ConsumptionInvoiceOrderService;
import com.yuqian.itax.order.service.MemberConsumptionRecordService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.service.DaifuApiService;
import com.yuqian.itax.user.service.MemberAccountService;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 定时查询消费开票订单结果（5分钟执行一次）
 *
 * @Author HZ
 * @Date 2020/09/27 14:28
 */
@JobHandler(value = "consumptionInvoiceOrderHandler")
@Component
@Logger
public class ConsumptionInvoiceOrderHandler extends IJobHandler {
    @Autowired
    ConsumptionInvoiceOrderService consumptionInvoiceOrderService;
    @Autowired
    ConsumptionInvoiceOrderChangeService consumptionInvoiceOrderChangeService;
    @Autowired
    DaifuApiService daifuApiService;
    @Autowired
    OemParamsService oemParamsService;
    @Autowired
    SmsService smsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    MemberConsumptionRecordService memberConsumptionRecordService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long t1 = System.currentTimeMillis(); // 代码执行前时间
        XxlJobLogger.log("=========定时查询消费开票查询结果任务启动");

        // 查询消费发票为出票中得订单
        ConsumptionInvoiceOrderQuery query=new ConsumptionInvoiceOrderQuery();
        query.setOrderStatus(1);
        List<ConsumptionInvoiceOrderVO> accountList = consumptionInvoiceOrderService.invoiceList(query);
        if (CollectionUtil.isEmpty(accountList)) {
            return SUCCESS;
        }

        // 循环查询渠道
        accountList.stream().forEach(account -> {
            // 读取开票订单渠道相关配置 paramsType=16
            OemParamsEntity paramsEntity = this.oemParamsService.getParams(account.getOemCode(), 16);
            if (null == paramsEntity) {
                throw new BusinessException("未配置开票查询渠道相关信息！");
            }
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("orderNo",account.getOrderNo());
            JSONObject jsonObject =daifuApiService.queryInvoiceInfo(paramsEntity,dataMap);
            if(jsonObject!=null){
                JSONObject object=jsonObject.getJSONObject("data");
                //失败
                if(object.getString("bizCode").equals("00")&& (object.getString("status").equals("-1"))){
                    OrderEntity orderEntity=orderService.queryByOrderNo(account.getOrderNo());
                    MemberAccountEntity memberAccountEntity=memberAccountService.findById(orderEntity.getUserId());
                    //修改订单状态为待出票
                    orderService.updateOrderStatus("admin-xxjob", account.getOrderNo(), 0);
                    //增加消费开票订单变更记录
                    ConsumptionInvoiceOrderChangeEntity consumptionInvoiceOrderChangeEntity=new ConsumptionInvoiceOrderChangeEntity();
                    ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity=consumptionInvoiceOrderService.findById(account.getId());
                    BeanUtils.copyProperties(consumptionInvoiceOrderEntity,consumptionInvoiceOrderChangeEntity);
                    consumptionInvoiceOrderChangeEntity.setStatus(0);
                    consumptionInvoiceOrderChangeEntity.setId(null);
                    consumptionInvoiceOrderChangeService.add(consumptionInvoiceOrderChangeEntity);

                    //记录失败原因
                    consumptionInvoiceOrderEntity.setRemark( object.getString("bizCodeMsg"));
                    consumptionInvoiceOrderEntity.setUpdateUser("admin-xxjob");
                    consumptionInvoiceOrderEntity.setUpdateTime(new Date());
                    consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);
                    /*//释放关联消费发票
                    String consumptionOrderRela=consumptionInvoiceOrderEntity.getConsumptionOrderRela();
                    String []consumptionOrderRelas=consumptionOrderRela.split(",");
                    for (String orderNo:consumptionOrderRelas) {
                        MemberConsumptionRecordEntity entity=new MemberConsumptionRecordEntity();
                        entity.setOrderNo(orderNo);
                        MemberConsumptionRecordEntity memberConsumptionRecordEntity=memberConsumptionRecordService.selectOne(entity);
                        memberConsumptionRecordEntity.setIsOpenInvoice(0);
                        memberConsumptionRecordEntity.setUpdateTime(new Date());
                        memberConsumptionRecordEntity.setUpdateUser("admin-xxjob");
                        memberConsumptionRecordService.editByIdSelective(memberConsumptionRecordEntity);
                    }
                    //发送短信
                    Map<String, Object> map = new HashMap();
                    map.put("msg", object.getString("bizCodeMsg"));
                    this.smsService.sendTemplateSms(memberAccountEntity.getMemberPhone(), account.getOemCode(), VerifyCodeTypeEnum.INVOICE_APPLY_FAIL.getValue(), map, 1);
                    XxlJobLogger.log("开票查询失败发送通知给【" + memberAccountEntity.getMemberPhone() + "】成功");*/
                }else if(object.getString("bizCode").equals("00")&& (object.getString("status").equals("5"))){
                    //成功
                    //增加消费开票订单变更记录
                    ConsumptionInvoiceOrderChangeEntity consumptionInvoiceOrderChangeEntity=new ConsumptionInvoiceOrderChangeEntity();
                    ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity=consumptionInvoiceOrderService.findById(account.getId());
                    BeanUtils.copyProperties(consumptionInvoiceOrderEntity,consumptionInvoiceOrderChangeEntity);
                    consumptionInvoiceOrderChangeEntity.setStatus(2);
                    consumptionInvoiceOrderChangeEntity.setId(null);
                    consumptionInvoiceOrderChangeService.add(consumptionInvoiceOrderChangeEntity);
                    //记录返回的invoicePdfUrl
                    consumptionInvoiceOrderEntity.setInvoicePdfUrl(object.getString("pdfUrl"));
                    consumptionInvoiceOrderEntity.setCompleteTime(new Date());
                    consumptionInvoiceOrderEntity.setUpdateTime(new Date());
                    consumptionInvoiceOrderEntity.setUpdateUser("admin-xxjob");
                    consumptionInvoiceOrderEntity.setRemark( object.getString("bizCodeMsg"));
                    consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);
                    //修改订单状态为已出票
                    orderService.updateOrderStatus("admin-xxjob", account.getOrderNo(), 2);
                }else if("2022".equals(object.getString("bizCode"))){
                    //没有查到订单
                    //修改订单状态为待出票
                    orderService.updateOrderStatus("admin-xxjob", account.getOrderNo(), 0);
                    // 保存消费开票订单变更记录
                    ConsumptionInvoiceOrderChangeEntity changeEntity = new ConsumptionInvoiceOrderChangeEntity();
                    BeanUtils.copyProperties(account,changeEntity);
                    changeEntity.setStatus(0);
                    changeEntity.setId(null);
                    changeEntity.setAddTime(new Date());
                    changeEntity.setAddUser("admin-xxjob");
                    this.consumptionInvoiceOrderChangeService.insertSelective(changeEntity);
                    /*//释放关联消费发票
                    String consumptionOrderRela=account.getConsumptionOrderRela();
                    String []consumptionOrderRelas=consumptionOrderRela.split(",");
                    for (String orderNo:consumptionOrderRelas) {
                        MemberConsumptionRecordEntity entity=new MemberConsumptionRecordEntity();
                        entity.setOrderNo(orderNo);
                        MemberConsumptionRecordEntity memberConsumptionRecordEntity=memberConsumptionRecordService.selectOne(entity);
                        memberConsumptionRecordEntity.setIsOpenInvoice(0);
                        memberConsumptionRecordEntity.setUpdateTime(new Date());
                        memberConsumptionRecordEntity.setUpdateUser("admin-xxjob");
                        memberConsumptionRecordService.editByIdSelective(memberConsumptionRecordEntity);
                    }*/
                    //记录失败原因
                    ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity=consumptionInvoiceOrderService.findById(account.getId());
                    consumptionInvoiceOrderEntity.setRemark( object.getString("bizCodeMsg"));
                    consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);
                } else{
                    //记录中间原因
                    ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity=consumptionInvoiceOrderService.findById(account.getId());
                    consumptionInvoiceOrderEntity.setRemark( object.getString("bizCodeMsg"));
                    consumptionInvoiceOrderEntity.setUpdateTime(new Date());
                    consumptionInvoiceOrderEntity.setUpdateUser("admin-xxjob");
                    consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);
                }
            }

          });
        XxlJobLogger.log("=========定时查询消费开票查询结果任务结束");
        long t2 = System.currentTimeMillis(); // 代码执行后时间
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(t2 - t1);
        XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
        return SUCCESS;
    }
}
