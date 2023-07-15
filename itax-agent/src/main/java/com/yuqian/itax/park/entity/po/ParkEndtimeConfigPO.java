package com.yuqian.itax.park.entity.po;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 开票截至时间接收实体
 */

@Setter
@Getter
public class ParkEndtimeConfigPO implements Serializable {

    /**
     * 园区操作截止时间配置表主键ID
     */
    private  Long id;

    /**
     * 园区id
     */
    private  Long parkId;
    /**
     * 发票方式 1-纸质发票 2-电子发票
     */
    private  Long invoiceWay;

    /**
     * 纸质提示开始时间
     */
    private Date zzStartTime;
    /**
     * 纸质提示结束时间
     */
    private Date zzEndTime;
    /**
     * 纸质提示文案
     */
    private String zzcontent;

    /**
     * 电子提示开始时间
     */
    private Date dzStartTime;
    /**
     * 电子提示结束时间
     */
    private Date dzEndTime;
    /**
     * 电子提示文案
     */
    private String dzcontent;

    /**
     * 编辑的年份
     */
    private Integer year;

    /**
     * 季度截至时间
     */
    private List<Date> quarterTime;
}
