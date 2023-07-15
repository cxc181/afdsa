package com.yuqian.itax.user.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业注销api返回VO
 * @Date: 2020年07月17日 09:42
 * @author yejian
 */
@Getter
@Setter
public class CompanyCancelApiVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号 ")
	private String orderNo;

	/**
	 * 订单状态 0-待付款 1-注销处理中 2-注销成功 3-已取消
	 */
	@ApiModelProperty(value = "订单状态 0-待付款 1-注销处理中 2-注销成功 3-已取消")
	private Integer orderStatus;

}