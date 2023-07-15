package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CompanyPushVo implements Serializable {

    private static final long serialVersionUID = -1L;


    private Long id;

    /**
     * 映射国金用户ID
      */
    private Integer userId;

    /**
     * 产品编码，验签用
     */
    private String productCode;

    /**
     * 身份证开始有效期(yyyy-mm-dd)
     */
    private String beginDate;

    /**
     * 身份证结束有效期(yyyy-mm-dd),若长期则直接传长期
     */
    private String endDate;

    /**
     * 身份证有效期
     */
    private String expireDate;

    /**
     * 身份证住址
     */
    private String idCardAddr;

    /**
     *身份证号码
     */
    private String idCardNo;

    /**
     * 用户姓名
     */
    private String realName;

    /**
     * 身份证正面地址
     */
    private String idCardFront;

    /**
     * 身份证反面地址
     */
    private String idCardBack;

    /**
     * 推送状态
     */
    private Integer authPushState;

    private String oemCode;

    /**
     * 渠道机构编码
     */
    private String channelCode;
}
