package com.yuqian.itax.park.entity.vo;

import com.yuqian.itax.agreement.entity.vo.AgreementTemplateInfoVO;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ParkDetailVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 园区名称
     */
    private String parkName;
    /**
     * 机构账号
     */
    private String username;

    /**
     * 园区编码
     */
    private  String parkCode;
    /**
     * 园区所在地
     */
    private String parkCity;
    /**
     * 园区介绍
     */
    private String parkRecommend;

    /**
     * 绑定手机号
     */
    private String phone;

    /**
     * 支持企业类型
     */
    private List<TaxPolicySelectVO> taxPolicyList;

    /**
     * 发票邮寄费
     */
    private Long postageFees;

    /**
     * 所属企业名称
     */
    private String belongsCompanyName;

    /**
     * 所属企业地址
     */
    private String belongsCompanyAddress;

    /**
     * 统一社会信用代码
     */
    private String ein;

    /**
     * 收件人姓名
     */
    private String recipient;

    /**
     * 收件人手机号
     */
    private String recipientPhone;

    /**
     * 收件人省编码
     */
    private String provinceCode;

    /**
     * 收件人省名称
     */
    private String provinceName;

    /**
     * 收件人市编码
     */
    private String cityCode;

    /**
     * 收件人市名称
     */
    private String cityName;

    /**
     * 收件人区编码
     */
    private String districtCode;

    /**
     * 收件人区名称
     */
    private String districtName;

    /**
     * 收件人详细地址
     */
    private String recipientAddress;

    /**
     * 授权文件图片
     */
    private String authorizationFile;

    /**
     * 园区详细地址
     */
    private String parkAddress;

    /**
     * 核定说明
     */
    private String verifyDesc;
    /**
     * 开票人
     */
    private String drawer;
    /**
     * 收款人
     */
    private String payee;
    /**
     * 复核人
     */
    private String reviewer;

    /**
     * 特殊事项说明
     */
    private String specialConsiderations;

    /**
     * 流程标记（1：需视频认证流程（同江西园区），2：需确认开启流程（同岳麓园区），3：电子化注册流程（同浏阳园区））
     */
    private Integer processMark;

    /**
     * 模板信息
     */
    List<AgreementTemplateInfoVO> agreementTemplateInfoVOList;

    /**
     * 1-查账征收 2-核定征收
     */
    private Integer incomeLevyType;

    /**
     * 公章图片地址
     */
    private String officialSealImg;

    private String httpOfficialSealImg;

    /**
     * 园区类型 1-自营园区  2-合作园区 3-外部园区
     */
    private Integer parkType;

    /**
     * 园区预览图
     */
    private String parkThumbnail;

    /**
     * 园区预览图 可访问的地址
     */
    private String httpParkThumbnail;

    /**
     * 园区详情顶部banner 图片,逗号分隔
     */
    private String parkImgs;

    /**
     * 园区详情顶部banner 图片,逗号分隔  可访问的地址
     */
    private List<String>  httpParkImgs;

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

    /**
     * 状态 0-待上线 1-已上架 2-已下线 3-已暂停 4-已删除
     */
    private Integer status;
}
