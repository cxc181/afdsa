package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerServiceWorkQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;
    /**
     * 坐席账号
     */
    private  String username;
    /**
     * 姓名
     */
    private  String nickname;
    /**
     * 工号
     */
    private  String workNumber;

    /**
     * 所属OEM
     */
    private  String oemName;

    /**
     * 所属OEM
     */
    private  String oemCode;

    /**
     * 状态 0-禁用 1-正常 2-已注销
     */
    private  Integer status;

}
