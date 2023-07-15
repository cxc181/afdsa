package com.yuqian.itax.park.entity.query;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ParkListOfMenuQuery implements Serializable {

    private static final long serialVersionUID = -3273521067150573775L;

    /**
     * 搜索关键字
     */
    private String searchKey;

    /**
     * 排序方式 0-综合排序 1-评分降序 2-单量降序
     */
    private int sort;

    /**
     * 省编码
     */
    private String provinceCode;

    /**
     * 市编码
     */
    private String cityCode;

    /**
     * 企业类型
     */
    private String companyTypes;

    /**
     * 企业类型列表
     */
    private List<String> companyTypeList;

    /**
     *  政策标签
     */
    private String policyLabels;

    /**
     * 政策标签列表
     */
    private List<String> policyLabelList;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 征收方式 1-查账征收 2-核定征收
     */
    private Integer incomeLevyType = 3; // 默认查不到数据
}
