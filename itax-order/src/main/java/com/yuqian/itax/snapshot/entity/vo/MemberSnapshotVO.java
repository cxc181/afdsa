package com.yuqian.itax.snapshot.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;


/**
 * 用户数统计(已完成)
 * @Date: 2020年10月26日 10:48:25
 * @author HZ
 */
@Setter
@Getter
public class MemberSnapshotVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * OEM机构
     */
    @Excel( name = "OEM机构")
    private String oemName;
    /**
     * 会员总数
     */
    @Excel( name = "会员总数")
    private int totalMember;
    /**
     * 普通会员总数
     */
    @Excel( name = "普通会员总数")
    private int totalPtMember;
    /**
     * VIP用户总数
     */
    @Excel( name = "VIP用户总数")
    private int totalVipMember;
    /**
     * 税务顾问总数
     */
    @Excel( name = "税务顾问总数")
    private int totalSwgwMember;
    /**
     * 城市服务商总数
     */
    @Excel( name = "城市服务商总数")
    private int totalCsfwsMember;
    /**
     * 员工总数
     */
    @Excel( name = "员工总数")
    private int totalEmpMember;
    /**
     * 城市合伙人总数
     */
    @Excel( name = "城市合伙人总数")
    private int totalHhr;
    /**
     * 高级城市合伙人总数
     */
    @Excel( name = "高级城市合伙人总数")
    private int totalGjhhr;
    /**
     * 新增会员数
     */
    @Excel( name = "新增会员数")
    private int addMember;

    /**
     * 新增VIP用户数
     */
    @Excel( name = "新增VIP用户数")
    private int addVipMember;
    /**
     * 新增税务顾问数
     */
    @Excel( name = "新增税务顾问数")
    private int addSwgwMember;
    /**
     * 新增城市服务商数
     */
    @Excel( name = "新增城市服务商数")
    private int addCsfwsMember;
    /**
     * 新增员工数
     */
    @Excel( name = "新增员工数")
    private int addEmpMember;
    /**
     * 新增城市合伙人数
     */
    @Excel( name = "新增员工数")
    private int addHhr;
    /**
     * 新增高级城市合伙人数
     */
    @Excel( name = "新增高级城市合伙人数")
    private int addGjhhr;
}
