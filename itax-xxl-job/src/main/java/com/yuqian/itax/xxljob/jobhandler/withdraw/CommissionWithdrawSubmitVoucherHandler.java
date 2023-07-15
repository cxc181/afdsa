package com.yuqian.itax.xxljob.jobhandler.withdraw;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.nabei.entity.AchievementExcelVo;
import com.yuqian.itax.nabei.entity.NabeiAPIBaseResp;
import com.yuqian.itax.nabei.service.NabeiApiService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.util.util.MoneyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Objects;


/**
 * @Description 佣金钱包提现提交业绩凭证
 * @Author  Kaven
 * @Date   2020/6/29 12:01 下午
*/
@JobHandler(value="CommissionWithdrawSubmitVoucherHandler")
@Component
public class CommissionWithdrawSubmitVoucherHandler extends IJobHandler {

	@Autowired
	private OrderService orderService;
	@Autowired
    private ProfitsDetailService profitsDetailService;
	@Autowired
    private NabeiApiService nabeiApiService;
	@Autowired
    private OemParamsService oemParamsService;
	@Autowired
    private MemberAccountService memberAccountService;

    @Override
	public ReturnT<String> execute(String param) throws Exception {
		XxlJobLogger.log("佣金钱包提现提交业绩凭证任务启动");

		// 查询待提交佣金钱包提现业绩凭证提现订单
        Example example = new Example(OrderEntity.class);
        example.createCriteria().andEqualTo("isSubmitPerformanceVoucher", 1)
                .andEqualTo("orderType", OrderTypeEnum.WITHDRAW.getValue())
                .andEqualTo("orderStatus", 2);
        List<OrderEntity> list = orderService.selectByExample(example);
        if (CollectionUtil.isEmpty(list)) {
            XxlJobLogger.log("无待提交业绩凭证的佣金提现订单，任务结束");
            return SUCCESS;
        }

        for (OrderEntity order : list) {
            // 获取佣金提现所选分润记录列表
            List<AchievementExcelVo> excelVos = profitsDetailService.getByWithdrawOrderNo(order.getOrderNo());
            if (CollectionUtil.isEmpty(excelVos)) {
                XxlJobLogger.log("未查询到佣金提现订单所选分润记录，orderNo:{}",order.getOrderNo());
                continue;
            }

            // 读取纳呗平台API相关配置 paramsType=10
            OemParamsEntity paramsEntity = this.oemParamsService.getParams(order.getOemCode(), 10);
            if (null == paramsEntity) {
                XxlJobLogger.log("未配置纳呗平台API相关信息，oemCOde:{}",order.getOemCode());
                continue;
            }

            // 查询用户信息
            MemberAccountEntity member = memberAccountService.findById(order.getUserId());
            if (null == member) {
                XxlJobLogger.log("未查询到用户信息，userId:{}",order.getUserId());
                continue;
            }
            if (member.getAuthStatus() != 1) {
                XxlJobLogger.log("用户未实名，userId:{}",order.getUserId());
                continue;
            }
            NabeiAPIBaseResp nabeiAPIBaseResp = nabeiApiService.submitAchievements(paramsEntity, member.getRealName(), member.getIdCardNo(), order.getOrderNo(), MoneyUtil.moneydiv(order.getOrderAmount().toString(),"100") , excelVos);
            if(null != nabeiAPIBaseResp){
                XxlJobLogger.log("纳呗单笔出款查询接口返回：{}", JSON.toJSONString(nabeiAPIBaseResp));
                if(Objects.equals(nabeiAPIBaseResp.getP1_resCode(), "0000")) {
                    // 更新订单“是否提交佣金提现业绩凭证”为已提交
                    order.setIsSubmitPerformanceVoucher(2);
                    orderService.editByIdSelective(order);
                } else {
                    XxlJobLogger.log("提交失败，原因：{}", nabeiAPIBaseResp.getP2_resMsg());
                }
            }else{
                XxlJobLogger.log("纳呗提交付款业绩成果凭证接口异常");
                continue;
            }
        }

        XxlJobLogger.log("佣金钱包提现提交业绩凭证任务结束");
		return SUCCESS;
	}
}
