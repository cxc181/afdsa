package com.yuqian.itax.park.entity;

import com.yuqian.itax.util.validator.Phone;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ParkPO implements Serializable {

    /**
     * 园区id
     */
    private  Long parkId;

    /**
     * 园区名称
     */
    @NotBlank(message = "请输入园区名称")
    private  String parkName;

    /**
     * 园区编码
     */
    @NotBlank(message = "请输入园区编码")
    @Size(max = 12, message = "园区编码不能超过12位字符")
    private  String parkCode;
    /**
     * 账号
     */
    @NotBlank(message = "请输入园区账号")
    @Size(min = 6, max = 20, message = "账号不能小于6位字符，超过20位字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]{8,16}$", message = "请输入8~16位，支持字母/数字/下划线组合")
    private  String username;

    /**
     * 园区所在地
     */
    @NotBlank(message = "请选择园区所在地")
    private  String parkCity;

    /**
     *园区简介
     */
    private  String parkRecommend;
    /**
     * 绑定手机
     */
    @NotBlank(message = "请输入园区绑定手机")
    private  String phone;

    /**
     * 服务内容
     */
    private  String serviceContent;
    /**
     *支持企业类型LIST
     */
    //@NotNull(message = "请选择园区支持企业")
    private List<TaxPolicyEntity> taxPolicyList;
    /**
     *邮寄费金额
     */
    @NotNull(message = "请输入园区邮寄费")
    private Long postageFees;
    /**
     *园区状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
     */
    private Integer status;
    /**
     *用户账号ID
     */
    private Long userId;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 所属企业名称
     */
    @NotBlank(message = "请输入所属企业名称")
    @Size(max = 64, message = "所属企业名称不能超过64个字")
    private String belongsCompanyName;

    /**
     * 所属企业地址
     */
    @NotBlank(message = "请输入所属企业地址")
    @Size(max = 128, message = "所属企业地址不能超过128个字")
    private String belongsCompanyAddress;

    /**
     * 统一社会信用代码
     */
    @NotBlank(message = "请输入统一社会信用代码")
    @Size(max = 32, message = "统一社会信用代码不能超过32个字")
    private String ein;

    /**
     * 收件人姓名
     */
    @NotBlank(message = "请输入收件人姓名")
    @Size(max = 32, message = "收件人姓名不能超过32个字")
    private String recipient;

    /**
     * 收件人手机号
     */
    @NotBlank(message = "请输入收件人手机号")
    @Size(max = 16, message = "收件人手机号不能超过16个字")
    @Phone(message = "收件人手机号码格式错误")
    private String recipientPhone;

    /**
     * 收件人省编码
     */
    @NotBlank(message = "请输入收件人省编码")
    @Size(max = 16, message = "收件人省编码不能超过16个字")
    private String provinceCode;

    /**
     * 收件人省名称
     */
    private String provinceName;

    /**
     * 收件人市编码
     */
    @NotBlank(message = "请输入收件人市编码")
    @Size(max = 16, message = "收件人市编码不能超过16个字")
    private String cityCode;

    /**
     * 收件人市名称
     */
    private String cityName;

    /**
     * 收件人区编码
     */
    @NotBlank(message = "请输入收件人区编码")
    @Size(max = 16, message = "收件人区编码不能超过16个字")
    private String districtCode;

    /**
     * 收件人区名称
     */
    private String districtName;

    /**
     * 收件人详细地址
     */
    @NotBlank(message = "请输入收件人详细地址")
    @Size(max = 128, message = "收件人详细地址不能超过128个字")
    private String recipientAddress;

    /**
     * 授权文件图片
     */
    //@NotBlank(message = "请上传授权文件图片")
    @Size(max = 256, message = "授权文件图片不能超过256个字")
    private String authorizationFile;

    /**
     * 园区详细地址
     */
    @NotBlank(message = "请输入园区详细地址")
    @Size(max = 256, message = "园区详细地址不能超过256个字")
    private String parkAddress;

//    /**
//     * 核定说明
//     */
//    @Size(max = 100, message = "核定说明不能超过100个字")
//    private String verifyDesc;
    /**
     * 开票人
     */
    @NotBlank (message = "请输入开票人")
    private String drawer;
    /**
     * 收款人
     */
    @NotBlank (message = "请输入收款人")
    private String payee;
    /**
     * 复核人
     */
    @NotBlank (message = "请输入复核人")
    private String reviewer;

//    /**
//     * 特殊事项说明
//     */
//    @Size(max = 100, message = "特殊事项说明不能超过100个字")
//    private String specialConsiderations;

    /**
     * 流程标记（1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程）
     */
//    @NotNull(message = "园区注册流程不能为空")
    private Integer processMark;

    /**
     * 模板id
     */
    private List<Long> agreementTemplateId;

//    /**
//     * 所得税征收方式 1-查账征收 2-核定征收
//     */
//    @NotNull(message = "征收方式不能为空")
//    private Integer incomeLevyType;

    /**
     * 公章图片地址
     */
    private String officialSealImg;

    /**
     * 园区类型 1-自营园区  2-合作园区 3-外部园区
     */
    @NotNull(message = "园区类型不能为空")
    private Integer parkType;



    /**
     * 园区预览图
     */
   // @NotBlank(message = "园区预览图不能为空")
    private String parkThumbnail;

    /**
     * 园区详情顶部banner 图片,逗号分隔
     */
    //@NotBlank(message = "园区详情顶部banner 图片不能为空")
    private String parkImgs;

    /**
     * 税收政策说明
     */
    private String taxPolicyDesc;


    /**
     * 工商注册说明
     */
    private String registerDesc;

    /**
     * 税务办理说明
     */
    private String taxHandleDesc;

    /**
     * 对公户办理说明
     */
    private String corporateAccountHandleDesc;

    /**
     * 是否企业注册分润 0-否 1-是
     */
    private Integer isRegisterProfit;

    /**
     * 是否托管续费分润 0-否 1-是
     */
    private Integer isRenewProfit;
}
