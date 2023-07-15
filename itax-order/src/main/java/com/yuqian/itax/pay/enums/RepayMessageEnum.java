package com.yuqian.itax.pay.enums;

/**
 * 代付返回结果枚举
 * @author：pengwei
 * @Date：2019/12/16 11:12
 * @version：1.0
 */
public enum RepayMessageEnum {

    RPY0000("RPY0000","成功/受理成功"),
    BASE005("BASE.005","参数{0}不能为空"),
    BASE006("BASE.006","参数{0}格式错误"),
    RPY1100("RPY1100","商户不存在"),
    RPY1101("RPY1101","商户状态异常"),
    RPY1102("RPY1102","商户黑名单"),
    RPY1103("RPY1103","账户黑名单"),
    RPY1104("RPY1104","商户未配置或没有设置有效费率信息"),
    RPY1105("RPY1105","费率信息设置异常"),
    RPY1106("RPY1106","商户信息不完整"),
    RPY1107("RPY1107","非标准商户无需申请/撤销额度"),
    RPY1108("RPY1108","申请额度次数超限[单日不超过10次]"),
    RPY2200("RPY2200","账户资金不足"),
    RPY2201("RPY2201","账户不存在"),
    RPY2202("RPY2202","资金账户调账异常"),
    RPY2203("RPY2203","资金账户变动异常"),
    RPY2204("RPY2204","单笔代付资金太小"),
    RPY2205("RPY2205","单笔代付资金太大"),
    RPY2206("RPY2206","账户日代付出款超限"),
    RPY2207("RPY2207","不允许跨日期订单(订单时间需与系统时间在同一天内)"),
    RPY3009("RPY3009","处理失败，通道返回：{0}"),
    RPY3010("RPY3010","代付失败：{0}"),
    RPY3100("RPY3100","商户请求数据解密异常"),
    RPY3101("RPY3101","商户秘钥信息异常"),
    RPY3102("RPY3102","接口数据签名不匹配"),
    RPY3103("RPY3103","系统数据签名不匹配"),
    RPY3104("RPY3104","批次订单条数大于500"),
    RPY3105("RPY3105","批量代付总数与实际打款明细数量不一致"),
    RPY3106("RPY3106","单次查询订单记录数超过30条"),
    RPY3107("RPY3107","代付订单已存在"),
    RPY3108("RPY3108","代付批次已存在"),
    RPY3109("RPY3109","批次订单不存在"),
    RPY3110("RPY3110","代付请求未知,请使用查询接口确认代付状态"),
    RPY3111("RPY3111","代付查询请求未知,请稍后重试"),
    RPY3112("RPY3112","商户资金查询请求未知，请稍后重试"),
    RPY3113("RPY3113","商户额度申请结果未知，请使用资金查询接口查看申请情况"),
    RPY3114("RPY3114","商户取消额度申请结果未知，请使用资金查询接口查看申请情况");

    private String value;

    private String message = null;

    private RepayMessageEnum(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
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
    public static RepayMessageEnum getByValue(String value) {
        for (RepayMessageEnum statusEnum : values()) {
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

