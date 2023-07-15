package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/4 15:17
 *  @Description: 超时未处理用章申请记录返回结果BEAN
 */
@Getter
@Setter
public class ComRscTimeoutRecordVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 未处理条数
	 */
	private Integer dealCount;

	/**
	 * 审核人
	 */
	private Long auditUser;

	/**
	 * 是否通知 0-未通知 1-24小时通知 2-72小时通知
	 */
	private Integer isNotice;
}
