package com.yuqian.itax.agent.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName OemAccessPartyQuery
 * @Description 接入方查询query
 * @Author lmh
 * @Date 2021/8/6 9:24
 * @Version 1.0
 */
@Setter
@Getter
public class OemAccessPartyQuery extends BaseQuery {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 接入方名称
     */
    private String accessPartyName;

    /**
     * 接入方编号
     */
    private String accessPartyCode;

    /**
     * 所属oem机构
     */
    private String oemCode;

    /**
     * 秘钥
     */
    private String accessPartySecret;

    /**
     * 状态 1-上架 2-下级
     */
    private Integer status;
}
