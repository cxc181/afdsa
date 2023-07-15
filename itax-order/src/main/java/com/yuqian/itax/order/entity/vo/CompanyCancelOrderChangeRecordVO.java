package com.yuqian.itax.order.entity.vo;

import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.entity.CompanyCancelOrderChangeRecordEntity;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.util.Lists;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 企业注销订单变更记录
 * 
 * @Date: 2020年02月13日 15:33:43 
 * @author 蒋匿
 */
@Getter
@Setter
public class CompanyCancelOrderChangeRecordVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 订单状态 0-待付款 1-注销处理中 2-注销成功 3-已取消
	 */
	private Integer orderStatus;

	/**
		 * 操作人类型  0-用户本人 1-系统  2-经办人
	 */
	private Integer operUserType;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 备注
	 */
	private String remark;

	public CompanyCancelOrderChangeRecordVO() {

	}
	public CompanyCancelOrderChangeRecordVO(CompanyCancelOrderChangeRecordEntity entity) {
		this.orderNo = entity.getOrderNo();
		this.oemCode = entity.getOemCode();
		this.orderStatus = entity.getOrderStatus();
		this.operUserType = entity.getOperUserType();
		this.addTime = entity.getAddTime();
		this.addUser = entity.getAddUser();
		this.remark = entity.getRemark();
	}

	public static List<CompanyCancelOrderChangeRecordVO> getList(List<CompanyCancelOrderChangeRecordEntity> list) {
		List<CompanyCancelOrderChangeRecordVO> result = Lists.newArrayList();
		if (CollectionUtil.isEmpty(list)) {
			return result;
		}
		for (CompanyCancelOrderChangeRecordEntity entity : list) {
			result.add(new CompanyCancelOrderChangeRecordVO(entity));
		}
		return result;
	}
}
