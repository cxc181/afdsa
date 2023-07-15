package com.yuqian.itax.coupons.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 用户优惠券VO
 * @Author  liumenghao
 * @Date   2021/4/8
*/
@Getter
@Setter
public class CouponsIssueVOAdmin implements Serializable {

	private static final long serialVersionUID = -1L;

    /**
     * 优惠券发放记录id
     */
    private Long id;

    /**
     * 账号
     */
    @Excel( name = "账号")
    private String memberAccount;

    /**
     * 名称
     */
    @Excel( name = "名称")
    private String realName;

    /**
     * 优惠券名称
     */
    @Excel( name = "优惠券名称")
    private String couponsName;

    /**
     * 优惠券编码
     */
    @Excel( name = "优惠券编码")
    private String couponsCode;

    /**
     * 优惠券使用状态 状态  0-未使用 1-已使用 2-已过期 3-已撤回
     */
    @Excel(name = "优惠券状态" , replace = { "未使用_0","已使用_1","已过期_2","已撤回_3" })
    private Integer status;
    /**
     * 发放方式 0-批量发放 1-兑换码
     */
    @Excel(name = "发放方式" , replace = { "批量发放_0","兑换码_1" })
    private Integer issueType;
    /**
     * OEM机构
     */
    @Excel( name = "OEM机构")
    private String oemName;
    /**
     * 发放时间
     */
    @Excel(name = "发放时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date issueTime;
    /**
     * 操作人
     */
    @Excel( name = "操作人")
    private String operUser;
}
