package com.yuqian.itax.xxljob.jobhandler.order;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.snapshot.service.*;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 快照跑批
 *
 * @Author 蒋匿
 * @Date 2020/10/28 14:28
 */
@JobHandler(value = "allSnapshotHandler")
@Component
@Logger
public class AllSnapshotHandler extends IJobHandler {
    @Autowired
    InvoiceOrderSnapshotService invoiceOrderSnapshotService;
    @Autowired
    OrderSnapshotService orderSnapshotService;
    @Autowired
    UserSnapshotService userSnapshotService;
    @Autowired
    MemberSnapshotService memberSnapshotService;
    @Autowired
    EmployeesSnapshotService employeesSnapshotService;
    @Autowired
    UserCapitalSnapshotService userCapitalSnapshotService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        long t1 = System.currentTimeMillis(); // 代码执行前时间
        XxlJobLogger.log("=========快照跑批任务启动");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String type = null;
        Date yesterday = sdf.parse(DateUtil.stampToDate(DateUtil.getYesterdayEndTime()));
        String startDate = sdf.format(yesterday);
        String endDate = sdf.format(yesterday);
        Long userId = null;
        String oemCode = null;
        JSONObject jsonObject = null;
        if(StringUtil.isNotBlank(param)) {
            jsonObject = JSONObject.parseObject(param);
            type = jsonObject.getString("type");
            //取xxjob参数，没有就默认是每日跑批取昨天

            startDate = jsonObject.getString("startDate");
            userId = jsonObject.getLong("userId");
            oemCode = jsonObject.getString("oemCode");
        }
        if(StringUtils.isBlank(type)){
            if(jsonObject!=null && StringUtils.isNotBlank(jsonObject.getString("startDate"))){
                //删除startDate日期以后的所有数据
                invoiceOrderSnapshotService.deleteInvoiceOrderSnapshotByDate(startDate,null,userId,oemCode);
                //删除startDate日期以后的所有数据
                orderSnapshotService.deleteOrderSnapshotByDate(startDate,null,userId,oemCode);
                //删除startDate日期以后的所有数据
                userSnapshotService.deleteUserSnapshotByDate(startDate,null,oemCode);
                //删除startDate日期以后的所有数据
                userSnapshotService.deleteUserSnapshotByDate(startDate,null,oemCode);
                //删除startDate日期以后的所有数据
                memberSnapshotService.deleteMemberSnapshotByDate(startDate,null,oemCode);
                //删除startDate日期以后的所有数据
                userCapitalSnapshotService.deleteUserCapitalSnapshotSnapshotByDate(startDate, endDate, oemCode);
            }
            //开票订单快照
            invoiceOrderSnapshotService.updateOrInsertInvoiceOrderSnapshot(startDate,endDate,null,null);
            //订单快照
            orderSnapshotService.updateOrInsertOrderSnapshot(startDate,endDate,null,null);
            //系统用户快照
            userSnapshotService.updateOrUserSnapshot(startDate,endDate,null);
            //会员快照
            memberSnapshotService.updateOrMemberSnapshot(startDate,endDate,null);
            //员工
            employeesSnapshotService.updateOrEmployeeSnapshot(startDate,endDate,null,null);
            //用户资金快照
            userCapitalSnapshotService.updateOrUserCapitalSnapshot(startDate,endDate,null);
        }else {
            if (type != null && "1".equals(type)) { //开票订单快照
                //删除startDate日期以后的所有数据
                invoiceOrderSnapshotService.deleteInvoiceOrderSnapshotByDate(startDate, endDate, userId, oemCode);
                //开票订单快照（手动）
                invoiceOrderSnapshotService.updateOrInsertInvoiceOrderSnapshot(startDate, endDate, userId, oemCode);
            }
            if (type != null && "2".equals(type)) { //订单快照
                //删除startDate日期以后的所有数据
                orderSnapshotService.deleteOrderSnapshotByDate(startDate, endDate, userId, oemCode);
                //订单快照（手动）
                orderSnapshotService.updateOrInsertOrderSnapshot(startDate, endDate, userId, oemCode);
            }

            if (type != null && "3".equals(type)) { //系统用户
                //删除startDate日期以后的所有数据
                userSnapshotService.deleteUserSnapshotByDate(startDate, endDate, oemCode);
                //系统用户快照
                userSnapshotService.updateOrUserSnapshot(startDate, endDate, oemCode);
            }

            if (type != null && "4".equals(type)) { //会员
                //删除startDate日期以后的所有数据
                memberSnapshotService.deleteMemberSnapshotByDate(startDate, endDate, oemCode);
                //会员快照
                memberSnapshotService.updateOrMemberSnapshot(startDate, endDate, oemCode);
            }

            if (type != null && "5".equals(type)) { //员工
                //删除startDate日期以后的所有数据
                employeesSnapshotService.deleteEmployeeSnapshotByDate(startDate, endDate, userId, oemCode);
                //员工快照
                employeesSnapshotService.updateOrEmployeeSnapshot(startDate, endDate, userId, oemCode);
            }
            if (type != null) {
                //删除startDate日期以后的所有数据
                userCapitalSnapshotService.deleteUserCapitalSnapshotSnapshotByDate(startDate, endDate, oemCode);
                //员工快照
                userCapitalSnapshotService.updateOrUserCapitalSnapshot(startDate, endDate, oemCode);
            }
        }
        XxlJobLogger.log("=========快照跑批任务结束");
        long t2 = System.currentTimeMillis(); // 代码执行后时间
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(t2 - t1);
        XxlJobLogger.log("耗时:" + c.get(Calendar.MINUTE) + "分" + c.get(Calendar.SECOND) + "秒" + c.get(Calendar.MILLISECOND) + "微秒");
        return SUCCESS;    }
}
