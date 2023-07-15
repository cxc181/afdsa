package com.yuqian.itax.util.exception;

/**
 * @ClassName ExceptionEnum
 * @Description 自定义异常枚举
 * @Author jiangni
 * @Date 2019/7/15
 * @Version 1.0
 */
public enum ExceptionEnum {

    APPLY_SUCCESS("0", "请求成功"),
    APPLY_FAIL("-1", "网络开小差了，请稍后再试"),
    NO_LOGIN("98", "您尚未登录"),
    NO_WORK_LOGIN("90", "您尚未工号登录"),


    BUSY_NETWORK("94", "网络繁忙，请稍后再试"),
    REMOTE_LOGIN("95", "您的账号在异地登录"),
    REQUEST_PROCESSING("96", "请求处理中，请勿重复操作"),
    BALANCE_LESS("97", "账户余额不足"),

    PHONE_NO_BIND("99", "手机号未绑定"),
    PERMISSIONS_ERROR("100", "权限不足，请联系管理员开通权限"),
    // Member
    USERNAME_EXIST("01_01", "用户名已存在"),
    PHONE_EXIST("01_02", "手机号被使用，绑定无效"),
    PHONE_BIND_DONE("01_03", "手机号已绑定"),
    SMS_CODE_ISNULL("01_04", "手机号或验证码不能为空"),
    SMS_CODE_FAIL("01_05", "验证码效验失败"),
    USERNAME_PASSWORD_ISNULL("01_06", "用户名或密码不能为空"),
    USERNAME_PASSWORD_ERROR("01_07", "用户名或密码错误"),
    USERNAME_DISABLE("01_08", "账户被冻结"),
    USERNAME_PAUSE("01_09", "账户暂停使用"),
    PAY_PASSWORD_SET("01_10", "已设置支付密码"),
    PAY_PASSWORD_NO_SAME("01_11", "支付密码不能与登录密码一致"),
    PAY_PASSWORD_OLD_ERROR("01_12", "原支付密码错误"),
    PASSWORD_OLD_ERROR("01_13", "原密码错误"),
    USERNAME_NO_EXIST("01_14", "账户不存在"),
    USERNAME_NO_ALL_NUMBER("01_15", "用户名不能为纯数字"),
    INVITE_CODE_NOT_FIND("01_16", "邀请码无效"),
    STARTTIME_ERROR("01_17", "开始时间不能大于结束时间"),
    MAXUPLOADSIZ_ERROR("01_18", "上传文件大小超过最大限制");


    private String code;

    private String msg;

    ExceptionEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }
}
