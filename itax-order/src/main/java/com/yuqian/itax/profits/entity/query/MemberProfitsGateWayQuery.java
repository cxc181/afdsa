package com.yuqian.itax.profits.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
public class MemberProfitsGateWayQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;

    Long userId;

    int userType;
}
