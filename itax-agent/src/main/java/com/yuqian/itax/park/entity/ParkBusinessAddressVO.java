package com.yuqian.itax.park.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Setter
@Getter
public class ParkBusinessAddressVO implements Serializable {

    private static final long serialVersionUID = -1L;


    private Long id;
    /**
     * 园区id
     */
    private  Long parkId;

    /**
     *注册前缀
     */
    private String registPrefix;

    /**
     * 注册单位
     */
    private String registUnit;

    /**
     * 当前注册数
     */
    private Integer currentRegistNum;

    /**
     * 注册区域最小值
     */
    private String registAreaMin;

    /**
     * 注册区域最大值
     */
    private String registAreaMax;

    /**
     * 区域注册数最小值
     */
    private String areaRegistNumMin;

    /**
     * 区域注册数最大值
     */
    private String areaRegistNumMax;

    /**
     * 地址类型1-固定经营地址 2-按房间号自动递增
     */
    private Integer addressType;

    /**
     * 地址后缀
     */
    private String registPostfix;

    /**
     * 最新使用地址
     */
    private String useAddress;

    //  区域最小值显示
    private String stringAreaRegistNumMin;
}
