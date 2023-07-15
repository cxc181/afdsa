package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class UserOrderStatisticsDayMemberQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 等级
     */
    private Integer levelNo;
    /**
     * 姓名
     */
    private String realName;
    /**
     * 注册账号
     */
    private String memberAccount;
    /**
     * 绑定手机号
     */
    private String memberPhone;
    /**
     * 海星会员标签 0-普通 1-海星
     */
    private Integer sign;
}
