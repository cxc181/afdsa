package com.yuqian.itax.user.entity.vo;

import com.yuqian.itax.util.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class CompanyCorePersonnelExportVO implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;

    /**
     * 订单号
     */
    @Excel(name = "订单编号")
    private String orderNo;

    /**
     * 成员类型  1-经理 2- 监事 3-执行董事 4-财务 5-无职务 多个类型之间用逗号分割
     */
//    @Excel(name = "人员类型", replace = {"股东_1","监事_2","财务_3","-_null" })
    private String personnelType;

    /**
     * 人员类型，多个标签之间用逗号分割
     */
    @Excel(name = "人员类型")
    private String personnelTypeName;

    /**
     * 身份类型 1-自然人 2-企业
     */
    @Excel(name = "身份类型", replace = {"自然人_1","企业_2","-_null" })
    private Integer identityType;

    /**
     * 姓名
     */
    @Excel(name = "名称")
    private String personnelName;

    /**
     * 证件号
     */
    @Excel(name = "证件号")
    private String certificateNo;

    /**
     * 证件地址
     */
    @Excel(name = "证件地址")
    private String certificateAddr;

    /**
     * 证件有效期
     */
    @Excel(name = "证件有效期")
    private String expireDate;

    /**
     * 联系电话
     */
    @Excel(name = "联系电话")
    private String contactPhone;

    /**
     * 投资金额(万元)
     */
//    @Excel(name = "投资金额")
    @Excel(name = "投资金额(万元)")
    private BigDecimal investmentAmount;

    /**
     * 占股比例
     */
    @Excel(name = "占股比例%" )
    private BigDecimal shareProportion;

    /**
     * 企业类型
     */
    private Integer companyType;

    /**
     * 合伙人类型 1-普通 2-有限合伙
     */
    private Integer partnerType;

    /**
     * 是否法人 0-否 1-是
     */
    private Integer isLegalPerson;

    /**
     * 是否执行事务合伙人 0-否 1-是
     */
    private Integer isExecutivePartner;

    /**
     * 是否股东 0-否 1-是
     */
    private Integer isShareholder;

    public void setShareProportion(BigDecimal shareProportion){
        this.shareProportion = shareProportion.multiply(new BigDecimal(100)).setScale(2);
    }

    public String getPersonnelTypeName(){
       StringBuffer typeLabel = new StringBuffer();
       if(getCompanyType()==3){
           if(getIsExecutivePartner() !=null && getIsExecutivePartner() == 1){
               typeLabel.append("、执行事务合伙人");
           }else{
               if(getPartnerType()!=null && getPartnerType() == 1){
                   typeLabel.append("、普通合伙人");
               }else if(getPartnerType() !=null && getPartnerType() == 2){
                   typeLabel.append("、有限合伙人");
               }
           }
       }else {
           if(getIsShareholder()!=null && getIsShareholder() == 1){
               typeLabel.append("、股东");
           }
       }
       if(getCompanyType()==1 && getIsLegalPerson() != null && getIsLegalPerson() == 1){
           typeLabel.append("、经营者");
       }else {
           if (getCompanyType()!=1 && getIsLegalPerson() != null && getIsLegalPerson() == 1) {
               typeLabel.append("、法人");
           }
       }
       if(StringUtil.isNotBlank(getPersonnelType())){
           String[] types = getPersonnelType().split(",");
           for (String t :types){
               if("1".equals(t)){
                   typeLabel.append("、经理");
               }else if("2".equals(t)){
                   typeLabel.append("、监事");
               }else if("3".equals(t)){
                   typeLabel.append("、执行董事");
               }else if("4".equals(t)){
                   typeLabel.append("、财务");
               }
           }
       }

       if(typeLabel.length()>0){
           this.personnelTypeName = typeLabel.substring(1).toString();
       }else{
           this.personnelTypeName = "";
       }
       return personnelTypeName;
    }
}
