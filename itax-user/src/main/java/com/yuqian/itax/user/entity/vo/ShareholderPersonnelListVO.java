package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Setter
@Getter
public class ShareholderPersonnelListVO implements Serializable {

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 姓名
     */
    private String personnelName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 证件号
     */
    private String certificateNo;

    /**
     * 证件地址
     */
    private String certificateAddr;

    /**
     * 证件有效期
     */
    private String expireDate;

    /**
     * 身份证正面
     */
    private String idCardFront;

    /**
     * 身份证正面可访问地址
     */
    private String idCardFrontUrl;

    /**
     * 身份证反面
     */
    private String idCardReverse;

    /**
     * 身份证反面可访问地址
     */
    private String idCardReverseUrl;

}
