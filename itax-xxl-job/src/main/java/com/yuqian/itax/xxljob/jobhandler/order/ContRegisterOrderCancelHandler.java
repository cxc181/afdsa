package com.yuqian.itax.xxljob.jobhandler.order;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.entity.ContRegisterOrderEntity;
import com.yuqian.itax.order.enums.ContOrderStatusEnum;
import com.yuqian.itax.order.service.ContRegisterOrderService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * 跑批取消未支付托管费续费订单
 * @author：pengwei
 * @Date：2021/2/5 10:09
 * @version：1.0
 */
@JobHandler(value = "contRegisterOrderCancelHandler")
@Component
public class ContRegisterOrderCancelHandler extends IJobHandler {

    @Autowired
    private ContRegisterOrderService contRegisterOrderService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("=========跑批取消未支付托管费续费订单启动=========");
        XxlJobLogger.log("请求参数：{}", param);

        Example example = new Example(ContRegisterOrderEntity.class);
        example.createCriteria()
                .andEqualTo("orderStatus", ContOrderStatusEnum.TO_BE_PAY.getValue())
                .andLessThan("addTime", DateTime.now().withTimeAtStartOfDay().toDate());

        if(StringUtils.isNotBlank(param)){
            JSONObject jsonObject = JSONObject.parseObject(param);
            String orderNo = jsonObject.getString("orderNo");
            example.createCriteria().andEqualTo("orderNo", orderNo);
        }

        List<ContRegisterOrderEntity> list = contRegisterOrderService.selectByExample(example);
        if (CollectionUtil.isEmpty(list)) {
            XxlJobLogger.log("=========没有未支付订单=========");
            XxlJobLogger.log("=========跑批取消未支付托管费续费订单结束=========");
            return SUCCESS;
        }
        Date updateTime = new Date();
        for (ContRegisterOrderEntity entity : list) {
            entity.setUpdateTime(updateTime);
            entity.setUpdateUser("admin");
            entity.setOrderStatus(ContOrderStatusEnum.CANCELLED.getValue());
            entity.setRemark("跑批取消未支付订单");
            contRegisterOrderService.cancelOrder(entity);
        }
        XxlJobLogger.log("=========跑批取消未支付托管费续费订单结束=========");
        return SUCCESS;
    }

}
