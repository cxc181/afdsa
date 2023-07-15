package com.yuqian.itax.agent.enums;

/**
 * 参数类型 1- 短信 2-微信支付 3-快捷支付 4-北京代付
 * @author：pengwei
 * @Date：2019/12/17 14:12
 * @version：1.0
 */
public enum OemParamsTypeEnum {

    SMS(1, "短信"),
    WECHAT(2, "微信支付"),
    WECHATQUERY(3, "微信支付订单查询"),
    REPAY(4, "北京代付/建行网金代付/建行专线代付"),
    AUTH2(5, "二要素"),
    AUTH4(6, "二要素"),
    OCR(7, "OCR身份证识别"),
    WECHAT_QR_CODE(8, "微信二维码配置"),
    SN_QUERY(9, "税务登记号相关配置"),
    NABEI_CHANNEL(10, "呐呗通道"),
    REPAY_QUERY_ORDER(11, "代付订单查询"),
    REPAY_QUERY_AMOUNT(12, "代付商户余额查询"),
    BW_INVOICE_CONFIG(23, "百旺电子发票配置"),
    SMS_NUMBER(25, "近两小时允许发送短信数"),
    EMAIL_CONFIG(24, "邮件发送配置"),
    GUOJIN_CHANNEL_CONFIG(26, "国金助手配置"),
    YI_SHUI(32, "易税相关配置");

    private Integer value;

    private String message = null;

    private OemParamsTypeEnum(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    public Integer getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 通过枚举<code>value</code>获得枚举
     * 
     * @param value
     * @return
     */
    public static OemParamsTypeEnum getByValue(Integer value) {
        for (OemParamsTypeEnum statusEnum : values()) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value + "|" + message;
    }

}

