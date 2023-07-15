package com.yuqian.itax.group.entity.po;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Setter
@Getter
public class InvoiceHeadGroupPO implements Serializable {

    Long id;

    @NotBlank(message = "请选择机构")
    String oemCode;
    /**
     *公司名称
     */
    @NotBlank(message = "请输入公司名称")
    String companyName;
    /**
     *公司地址
     */
    @NotBlank(message = "请输入公司地址")
    String companyAddress;
    /**
     *税号
     */
    @NotBlank(message = "请输入税号")
    String ein;
    /**
     *电话号码
     */
    @NotBlank(message = "请输入电话号码")
    String phone;
    /**
     *开户银行
     */
    @NotBlank(message = "请输入开户银行")
    String bankName;

    /**
     *银行账号
     */
    @NotBlank(message = "请输入银行账号")
    String bankNumber;
    /**
     *收件人
     */
    @NotBlank(message = "请输入收件人")
    String recipient;
    /**
     *收件人联系电话
     */
    @NotBlank(message = "请输入收件人联系电话")
    String recipientPhone;
    /**
     *省编码
     */
    @NotBlank(message = "请选择收件地区")
    String provinceCode;
    /**
     *市编码
     */
    @NotBlank(message = "请选择收件地区")
    String cityCode;

    /**
     *区编码
     */
    @NotBlank(message = "请选择收件地区")
    String districtCode;

    /**
     *收件人详细地址
     */
    @NotBlank(message = "请输入收件人详细地址")
    String recipientAddress;
    /**
     *收票邮箱
     */
    @NotBlank(message = "请输入收票邮箱")
    @Email(message = "收票邮箱格式有误", regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
    String email;
}
