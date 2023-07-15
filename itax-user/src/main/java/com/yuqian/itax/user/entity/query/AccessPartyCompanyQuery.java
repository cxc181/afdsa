package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: lmh
 *  @Date: 2021/8/12
 *  @Description: 企业查询实体类-接入方使用
 */
@Getter
@Setter
public class AccessPartyCompanyQuery extends BaseQuery implements Serializable {
    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 接入方编码
     */
    private String accessPartyCode;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 是否包含已注销企业 0-不包括 1-已包括
     */
    private int isIncludeCancelled;
}