package com.yuqian.itax.order.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import com.yuqian.itax.util.validator.Add;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/23 15:19
 *  @Description: 开户订单查询实体类
 */
@Getter
@Setter
public class RegOrderQuery extends BaseQuery implements Serializable {
    /**
     * 机构编码
     */
    @NotBlank(message="oemCode不能为空", groups={Add.class})
    private String oemCode;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 添加时间开始
     */
    private Date addTimeBeg;

    /**
     * 添加时间结束
     */
    private Date addTimeEnd;

    /**
     * 订单状态   订单类型： 工商注册  0-待提交  1-审核中  2-待付款 3-待名称核准 4-待设立登记 5-待用户签名 6-待经办人签名 7-待领证 8-已完成
     开票： 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收
     */
    private Integer orderStatus;

    /**
     * 产品类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任 5-开票 6-税务顾问 7-城市服务商
     */
    private Integer prodType;
}