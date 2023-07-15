package com.yuqian.itax.product.enums;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 14:49
 *  @Description: 产品类型枚举
 *  产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-黄金会员（废弃） 10-钻石会员 （废弃）
 *  11-个体注销 12-个独注销 13-有限合伙注销 14-有限责任注销 15-公户申请和托管 16-个体托管费续费
 */
public enum ProductTypeEnum {

    INDIVIDUAL(1, "个体开户"),
    INDEPENDENTLY(2, "个独开户"),
    LIMITED_PARTNER(3, "有限合伙开户"),
    LIMITED_LIABILITY(4, "有限责任开户"),
    INDIVIDUAL_INVOICE(5, "个体开票"),
    INDEPENDENTLY_INVOICE(6, "个独开票"),
    LIMITED_PARTNER_INVOICE(7, "有限合伙开票"),
    LIMITED_LIABILITY_INVOICE(8, "有限责任开票"),
    GOLDEN(9, "税务顾问"),
    DIAMOND(10, "城市服务商"),
    INDIVIDUAL_CANCEL(11, "个体注销"),
    INDEPENDENTLY_CANCEL(12, "个独注销"),
    LIMITED_PARTNER_CANCEL(13, "有限合伙注销"),
    LIMITED_LIABILITY_CANCEL(14, "有限责任注销"),
    CORPORATE_ACCOUNT_APPLY(15, "公户申请和托管"),
    INDIVIDUAL_RENEWALS(16, "个体托管费续费"),
    CORPORATE_ACCOUNT_RENEW(17, "对公户续费"),
    INDEPENDENTLY_RENEWALS(18, "个独托管费续费"),
    LIMITED_PARTNER_RENEWALS(19, "有限合伙托管费续费"),
    LIMITED_LIABILITY_RENEWALS(20, "有限责任托管费续费"),
    ;

    private Integer value;

    private String message = null;

    private ProductTypeEnum(Integer value, String message) {
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
    public static ProductTypeEnum getByValue(Integer value) {
        for (ProductTypeEnum statusEnum : values()) {
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

