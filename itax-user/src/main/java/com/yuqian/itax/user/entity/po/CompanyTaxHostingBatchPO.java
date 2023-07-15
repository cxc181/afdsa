package com.yuqian.itax.user.entity.po;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.io.Serializable;

@Setter
@Getter
@ApiModel(description = "托管信息(批量修改)-传递excel参数")
public class CompanyTaxHostingBatchPO implements Serializable {

    private static final long serialVersionUID = -1L;

    @Excel(name = "企业名称")
    private String companyName;

    @Excel(name = "税号")
    private String ein;

    // 0-未托管 1-已托管
    @Excel(name = "托管状态")
    private String hostingStatus;

    @Excel(name = "失败原因")
    private String failed;

}
