package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 我的企业
 *
 * @Date: 2019年12月06日 10:42:12
 * @author yejian
 */
@Getter
@Setter
public class IndividualVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 机构名称
	 */
	@Excel(name = "OEM机构", width = 20)
	private String oemName;

	/**
	 * 有效个体总数
	 */
	@Excel(name = "有效个体总数", width = 20)
	private Integer totalNum = 0;

	/**
	 * 新增个体数
	 */
	@Excel(name = "新增个体数", width = 20)
	private Integer addNum = 0;

	/**
	 * 审核中订单总数
	 */
	@Excel(name = "审核中订单总数", width = 20)
	private Integer auditNum = 0;

	/**
	 * 待领证订单总数
	 */
	@Excel(name = "待领证订单总数", width = 20)
	private Integer certifyNum = 0;

	/**
	 * 注销个体数总数
	 */
	@Excel(name = "注销个体数总数", width = 20)
	private Integer offNum = 0;
}