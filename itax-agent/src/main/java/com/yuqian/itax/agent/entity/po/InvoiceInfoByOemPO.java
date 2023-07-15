package com.yuqian.itax.agent.entity.po;

import com.yuqian.itax.agent.entity.vo.InvoiceCategoryBaseStringAgentVO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
public class InvoiceInfoByOemPO {

    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 机构编码
     */
    @NotNull(message="请选择所属机构")
    private String oemCode;


    /**
     * 公司名称
     */
    @NotNull(message="请输入抬头公司名称")
    private String companyName;

    /**
     * 公司地址
     */
    @NotNull(message="请输入抬头公司地址")
    private String companyAddress;

    /**
     * 税号
     */
    @NotNull(message="请输入统一社会信用代码")
    private String ein;

    /**
     * 电话号码
     */
    @NotNull(message="请输入联系电话")
    private String phone;


    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankNumber;

    /**
     * 收件人
     */
    //@NotNull(message="请输入收件人")
    private String recipient;

    /**
     * 联系电话
     */
    //@NotNull(message="请输入收件人手机号")
    private String recipientPhone;

    /**
     * 详细地址
     */
    //@NotNull(message="请输入收件地址")
    private String recipientAddress;



    /**
     * 开票类目名称
     */
    @NotNull(message="请输入开票类目")
    private List<InvoiceCategoryBaseStringAgentVO> categoryList;
    /**
     *省编码
     */
    //@NotBlank(message = "请选择收件地区")
    String provinceCode;
    /**
     *市编码
     */
    //@NotBlank(message = "请选择收件地区")
    String cityCode;

    /**
     *区编码
     */
    //@NotBlank(message = "请选择收件地区")
    String districtCode;
    /**
     *收票邮箱
     */
    //@NotBlank(message = "请输入收票邮箱")
    @Size(max = 30, message = "收票邮箱不能超过30位字符")
    //@Email(message = "收票邮箱格式有误", regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
    String email;

    /**
     * 托管状态 0-未托管 1-已托管
     */
    @NotNull(message = "托管状态不能为空")
    private Integer hostingStatus;

    /**
     * 税务盘类型 1-ukey 2-税控盘
     */
    private Integer taxDiscType;

    /**
     * 税务盘编号
     */
    private String taxDiscCode;

    /**
     * 票面金额类型 1-1w 2-10w 3-100w
     */
    private Integer faceAmountType;

    /**
     * 票面金额(分)
     */
    private Long faceAmount;

    /**
     * 通道方 1-百旺
     */
    private Integer channel;

    /**
     * 是否立即开票 0-否 1-是
     */
    @NotNull(message = "是否立即开票不能为空")
    private Integer isImmediatelyInvoice;
}
