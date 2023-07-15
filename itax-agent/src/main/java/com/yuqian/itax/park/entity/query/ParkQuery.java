package com.yuqian.itax.park.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 园区管理查询
 * @author：pengwei
 * @Date：2019/12/16 20:12
 * @version：1.0
 */
@Getter
@Setter
public class ParkQuery extends BaseQuery implements Serializable {
    /**
     * 机构状态 0-不可用 1-可用
     */
    private Integer oemStatus;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 园区状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
     */
    private Integer status;

    /**
     * 园区id
     */
    private String parkIds;
    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 园区名称
     */
    private String parkName;

    /**
     * 园区类型 1-自营园区  2-合作园区 3-外部园区
     */
    private Integer parkType;

    /**
     * 产品类型，用于产品新增编辑的园区列表数据获取
     */
    private Integer prodType;

    /**
     * 企业类型，用于产品新增编辑的园区列表数据获取
     */
    private Integer companyType;
}