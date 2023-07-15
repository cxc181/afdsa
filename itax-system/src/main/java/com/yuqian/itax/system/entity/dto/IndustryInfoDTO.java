package com.yuqian.itax.system.entity.dto;

import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.entity.BusinessScopeEntity;
import com.yuqian.itax.system.entity.IndustryEntity;
import com.yuqian.itax.system.entity.RatifyTaxEntity;
import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseStringVO;
import com.yuqian.itax.system.enums.IndustryStatusEnum;
import com.yuqian.itax.system.service.BusinessscopeTaxcodeService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/19 10:35
 *  @Description: 核定税种/开票类目/经营范围合并DTO
 */
@Getter
@Setter
public class IndustryInfoDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 企业类型中文名称
     */
    @Excel(name = "企业类型")
    private String companyTypeName;

    @Excel(name = "服务类别")
    @NotBlank(message="服务类别不能为空")
    private String industryName;

    @Excel(name = "商户名称")
    @NotBlank(message="商户名称不能为空")
    private String exampleName;

    @Excel(name = "经营范围")
    @NotBlank(message="经营范围不能为空")
    private String businessContent;

    @Excel(name = "核定税种")
    @NotBlank(message="核定税种不能为空")
    private String taxName;

    @Excel(name = "开票类目")
    private String categoryName;

    private  List<String> categoryNames;
    /**
     * 发票类目
     */
    private List<InvoiceCategoryBaseStringVO> categoryList;

    @Excel(name = "其他说明")
    @Size(max = 100, message = "其他说明不能超过100个字")
    private String orderDesc;

    @Excel(name = "失败原因")
    private String failed;

    /**
     * 发票样例
     */
    private String exampleInvoice;

    /**
     * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
     */
    private Integer companyType;

    //产品于2020年9月9日上午9点24分说都不做校验，有问题后果自负
//    public void setCompanyTypeName(String companyTypeName) {
//        this.companyTypeName = replace(companyTypeName.replaceAll("[^,，a-zA-Z0-9\\u4E00-\\u9FA5]", ""));
//    }
//
//    public void setIndustryName(String industryName) {
//        this.industryName = replace(industryName.replaceAll("[^,，a-zA-Z0-9\\u4E00-\\u9FA5]", ""));
//    }
//
//    public void setExampleName(String exampleName) {
//        this.exampleName = replace(exampleName.replaceAll("[^,，*＊a-zA-Z0-9\\u4E00-\\u9FA5]", ""));
//    }
//
//    public void setBusinessContent(String businessContent) {
//        this.businessContent = replace(businessContent.replaceAll("[^\\n;；,，*＊a-zA-Z0-9\\u4E00-\\u9FA5]", ""));
//    }
//
//    public void setTaxName(String taxName) {
//        this.taxName = replace(taxName.replaceAll("[^\\n,，*＊a-zA-Z0-9\\u4E00-\\u9FA5]", ""));
//    }
//
//    public void setCategoryName(String categoryName) {
//        this.categoryName = replace(categoryName.replaceAll("[^\\n,，*＊a-zA-Z0-9\\u4E00-\\u9FA5]", ""));
//    }

    public String replace(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return str.replace("＊", "*").replace("，", ",").replace("；", ";");
    }
    /**
     * 园区id
     */
    @NotNull(message="园区主键不能为空")
    private Long parkId;

    /**
     * 行业id主键
     */
    @NotNull(message="行业主键不能为空")
    private Long industryId;

    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 添加时间
     */
    private String addUser;

    /**
     * 转行业数据信息实体
     * @return
     */
    public IndustryEntity getIndustryEntity(){
        IndustryEntity entity = new IndustryEntity();
        entity.setIndustryName(industryName);
        entity.setCompanyType(companyType);
        entity.setExampleName(exampleName);
        entity.setStatus(IndustryStatusEnum.YES.getValue());
        entity.setOrderDesc(orderDesc);
        entity.setParkId(parkId);
        entity.setAddTime(addTime);
        entity.setAddUser(addUser);
        return entity;
    }

    /**
     * 转核定税种实体
     * @return
     */
    public List<RatifyTaxEntity> getRatifyTaxEntity() {
        String[] split = taxName.split("\\n");
        if (split == null || split.length <= 0) {
            return null;
        }
        List<String> collect = Arrays.asList(split).stream().filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(collect)) {
            return null;
        }
        List<RatifyTaxEntity> lists = Lists.newArrayList();
        RatifyTaxEntity entity;
        for (String s : collect) {
            entity = new RatifyTaxEntity();
            entity.setIndustryId(industryId);
            entity.setTaxName(s);
            entity.setStatus(1);
            entity.setAddTime(addTime);
            entity.setAddUser(addUser);
            lists.add(entity);
        }
        return lists;
    }

    /**
     * 转经营范围实体
     * @return
     */
    public List<BusinessScopeEntity> getBusinessScopeEntity() {
        String[] split = businessContent.split("\\n");
        if (split == null || split.length <= 0) {
            return null;
        }
        List<String> collect = Arrays.asList(split).stream().filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(collect)) {
            return null;
        }
        List<BusinessScopeEntity> lists = Lists.newArrayList();
        BusinessScopeEntity entity;
        for (String s : collect) {
            s = s.trim().replace("；", ";");
            entity = new BusinessScopeEntity();
            entity.setIndustryId(industryId);
            entity.setBusinessContent(s);
            entity.setAddTime(addTime);
            entity.setAddUser(addUser);
            lists.add(entity);
        }
        return lists;
    }
}