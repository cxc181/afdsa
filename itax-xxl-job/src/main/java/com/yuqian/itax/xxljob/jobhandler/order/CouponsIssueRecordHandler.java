package com.yuqian.itax.xxljob.jobhandler.order;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.coupons.entity.CouponExchangeCodeEntity;
import com.yuqian.itax.coupons.entity.CouponsEntity;
import com.yuqian.itax.coupons.entity.CouponsIssueRecordEntity;
import com.yuqian.itax.coupons.entity.enums.CouponExchangeCodeStatusEnum;
import com.yuqian.itax.coupons.entity.enums.CouponsIssueRecordStatusEnum;
import com.yuqian.itax.coupons.entity.enums.CouponsStatusEnum;
import com.yuqian.itax.coupons.service.CouponExchangeCodeService;
import com.yuqian.itax.coupons.service.CouponsIssueRecordService;
import com.yuqian.itax.coupons.service.CouponsService;
import com.yuqian.itax.order.entity.InvoiceRecordDetailEntity;
import com.yuqian.itax.util.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 定时更新过期优惠卷
 */
@JobHandler(value = "couponsIssueRecordHandler")
@Component
public class CouponsIssueRecordHandler extends IJobHandler {

    @Resource
    CouponsIssueRecordService couponsIssueRecordService;
    @Resource
    CouponsService couponsService;
    @Resource
    CouponExchangeCodeService couponExchangeCodeService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("=========更新过期/生效优惠卷启动");

        //1.定期更新过期优惠券状态
        List<CouponsEntity> couponsEntities=couponsService.queryOverTimeCouponsEntity();
        for (CouponsEntity c:couponsEntities) {
            c.setStatus(CouponsStatusEnum.STALE.getValue());
            c.setUpdateUser("XX-job");
            c.setUpdateTime(new Date());
            c.setRemark("定时修改过期数据");
            couponsService.editByIdSelective(c);
        }
        //1.1定期更新已生效优惠券状态
        List<CouponsEntity> couponsEntityList=couponsService.queryStartTimeCouponsEntity();
        for (CouponsEntity c:couponsEntityList) {
            c.setStatus(CouponsStatusEnum.EFFICIENT.getValue());
            c.setUpdateUser("XX-job");
            c.setUpdateTime(new Date());
            c.setRemark("定时修改生效数据");
            couponsService.editByIdSelective(c);
        }
        //2.定期更新发放记录状态
        List<CouponsIssueRecordEntity> couponsIssueRecordEntities=couponsIssueRecordService.queryOverTimeCouponsIssueRecordEntity();
        for (CouponsIssueRecordEntity c:couponsIssueRecordEntities) {
            c.setStatus(CouponsIssueRecordStatusEnum.STALE.getValue());
            c.setUpdateUser("XX-job");
            c.setUpdateTime(new Date());
            c.setRemark("定时修改过期数据");
            couponsIssueRecordService.editByIdSelective(c);
        }
        //3.定期更新兑换码状态
        List<CouponExchangeCodeEntity> couponExchangeCodeEntities=couponExchangeCodeService.queryOverTimeCouponExchangeCodeEntity();
        for (CouponExchangeCodeEntity c:couponExchangeCodeEntities) {
            c.setStatus(CouponsIssueRecordStatusEnum.STALE.getValue());
            c.setUpdateUser("XX-job");
            c.setUpdateTime(new Date());
            c.setRemark("定时修改过期数据");
            couponExchangeCodeService.editByIdSelective(c);
        }
        List<CouponExchangeCodeEntity> couponExchangeCodeEntityList=couponExchangeCodeService.queryStartTimeCouponExchangeCodeEntity();
        for (CouponExchangeCodeEntity c:couponExchangeCodeEntityList) {
            c.setStatus(CouponExchangeCodeStatusEnum.EFFICIENT.getValue());
            c.setUpdateUser("XX-job");
            c.setUpdateTime(new Date());
            c.setRemark("定时修改生效数据");
            couponExchangeCodeService.editByIdSelective(c);
        }
        XxlJobLogger.log("=========更新过期/生效优惠卷结束");
        return SUCCESS;
    }
}
