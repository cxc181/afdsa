package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AgentMemberQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;
    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 渠道用户列表ii
     */
    private List<Long> channelUserIds;
    /**
     * 用户名
     */
    private String memberName;
    /**
     * 手机号
     */
    private String memberPhone;
    /**
     * 会员等级 1、普通会员；2、VIP
     */
    private Integer memberLevel;
    /**
     * 云财状态：1-正常 0-禁用 2-注销
     */
    private Integer status;
    /**
     * 服务商id
     */
    private Long channelServiceId;
    /**
     * 员工id
     */
    private Long channelEmployeesId;
    /**
     * 服务商id
     */
    private List<Long> channelServiceIdList;
    /**
     * 员工id
     */
    private  List<Long> channelEmployeesIdList;
    /**
     * 是否裂变 0-直推 1-裂变
     */
    private Integer isFission;
    /**
     * 渠道编码
     */
    private String channelCode;
    /**
     * 所属OEM
     */
    private String oemCode;
    /**
     * 渠道产品编码
     */
    private String channelProductCode;

}
