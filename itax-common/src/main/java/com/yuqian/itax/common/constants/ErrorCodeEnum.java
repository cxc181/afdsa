package com.yuqian.itax.common.constants;

public enum ErrorCodeEnum {
    //****************************系统级****************************
    SUCCESS("0000","成功"),
    FAILED("0001","失败！"),
    REQUIRED_PARAM_NULL("P0001","必传参数为空！"),
    PARAM_FORMAT_ERROR("P0002","参数格式错误！"),
    SIGNATURE_ERROR("S0001","签名错误！"),
    OEMCODE_ERROR("S0002","oem机构错误或不存在！"),
    SYS_SETTING_ERROR("S0004","未配置二要素相关信息"),
    OEM_INVITOR_ERROR("S0003","OEM机构未配置邀请人信息！"),
    PASSWORD_RESET_CODE_IS_EXPIRED("S0005","验证码输入错误或验证码已过期"),
    OEM_ACCESSPARTYCODE_ERROR("S0006","接入方已下架或不存在！"),

    //****************************业务部分***************************
    // 用户
    USER_NOT_EXISTS("U0001","用户不存在"),
    IDCARD_OUT_OF_DATE("U0002","身份证已过期"),
    IDCARD_DATE_ERROR("U0003","身份证有效期格式有误，格式要求：yyyy.MM.dd-yyyy.MM.dd"),
    USER_AUTH_FAIL("U0004","用户二要素认证失败"),
    INCONSISTENT_INFORMATION("U0005", "认证失败，账号拥有者不能更改，请修改后重试！"),

    // 产品
    PRODUCT_NOT_CONFIGURED("P0003","未配置产品相关信息"),
    PRODUCT_NOT_EXISTS("P0004","产品信息不存在"),

    // 订单
    EXTERNAL_ORDER_IS_EXIST("D0001","外部订单号已存在"),
    ORDER_NO_EXISTS_ERROR("D0002","订单号已存在"),
    ORDER_NO_NOT_EXIST("D0003","订单号错误或不存在"),
    ORDER_STATUS_ERROR("D0004","订单状态错误"),
    WORK_ORDER_ERROR("D0005","自动派单失败，未找到接单客服拓展信息"),
    ORDER_NOT_BELONG_REG("D0006","订单不属于当前用户"),
    INVALID_COMPANY_TYPE("D0006","企业类型不支持，请确认"),
    INDUSTRY_NOT_SUPPORT("D0007","行业不存在或该园区不支持所选行业"),
    SHOP_NAME_ONE_INVALID("D0008","备选字号一格式不正确，请输入2-6个汉字"),
    SHOP_NAME_TWO_INVALID("D0009","备选字号二格式不正确，请输入2-6个汉字"),
    SHOP_NAME_REPEATED("D0010","字号名称有重复，请检查！"),

    // 园区
    PARK_NOT_EXIST("Y0001","园区编码错误或不存在"),
    PARK_STAUS_ERROR("Y0002","园区为非正常状态"),
    PARK_RULE_NOT_CONFIGURED("Y0003","未配置园区规则相关信息"),
    PARK_STATUS_ERROR("Y0004","园区状态不正确，只有已上架的园区才允许开户"),
    PARK_NOT_EXISTS("Y0005","园区不存在"),
    PARK_OEM_ERROR("Y0006","所选园区不属于当前OEM机构"),
    PARK_BUSINESSADDR_ERROR("Y0007","园区经营地址规则配置错误"),
    PARK_PROD_RELA_NOT_EXISTS("Y0008","未找到所选产品和园区对应关系"),

    // 企业
    COMPANY_TYPE_ERROR("Q0001","企业类型错误"),
    COMPANY_NOT_EXIST("Q0002","企业id错误或不存在"),
    COMPANY_STATUS_ERROR("Q0003","企业为非正常状态"),

    // 文件上传
    OSS_FILE_EXIST("F1000","无效的文件地址"),
    OSS_VIDEO_NOT_EXIST("F1001","无效的视频文件地址"),
    OSS_SIGNIMG_NOT_EXIST("F1002","无效的签名图片地址"),
    OSS_IDCARDFRONT_NOT_EXIST("F1003","无效的身份证正面图片地址"),
    OSS_IDCARDBACK_NOT_EXIST("F1004","无效的身份证反面图片地址"),
    OSS_FILE_NUM_LIMIT("F0010","上传的文件个数已达上限"),
    OSS_FILE_SINGLE_NUM_LIMIT("F0011","单次最多上传12个文件"),
    OSS_FILE_TYPE_ERROR("F0002","未知的文件类型"),
    OSS_IMG_FORMAT_ERROR("F0003","不支持的文件类型，仅支持JPG/JPEG/PNG/BMP格式的图片上传"),
    OSS_IMG_SIZE_ERROR("F0004","图片文件过大不支持上传"),
    OSS_VIDEO_FORMAT_ERROR("F0005","仅支持MP4格式的视频上传"),
    OSS_VIDEO_SIZE_ERROR("F0006","文件上传失败，视频文件过大不支持上传"),
    OSS_IMG_FILENAME_ERROR("F0007","文件名格式不正确，必须包含文件拓展名"),

    // 企业开票
    INVOICE_AMOUNT_MIN_LIMIT("I0001", "未达到单次开票金额最小限制"),
    INVOICE_REMAIN_AMOUNT_ERROR("I0002", "可开票金额不足"),
    INVOICE_VAT_RATE_ERROR("I0003", "增值税率错误或不支持"),
    INVOICE_MISS_BANKINFO("I0004", "缺少开户行信息无法申请增值税专票"),
    INVOICE_NEED_BANK_WATER("I0005", "开票金额一万元及以上请上传银行流水截图"),
    INVOICE_COMPANY_TOP_UP("I0006", "企业开票已满额"),
    INVOICE_COMPANY_NOT_CONFIGURED("I0007", "未配置企业开票额度相关信息"),
    COMPANY_NOT_FIND_CATEGORY("I0008", "开票类目不支持"),
    INVOICE_NEED_BUSINESS_CONTRACT_IMG("I0009", "开票金额一万元及以上请上传业务合同截图"),
    BUSINESS_CONTRACT_IMG_NUM_LIMIT("I0010", "业务合同截图个数超过最大限制"),
    BANK_WATER_IMG_EXIST("I0011", "银行流水截图文件地址已存在"),
    BUSINESS_CONTRACT_IMG_EXIST("I0012", "业务合同截图文件地址已存在"),

    // 省市区
    PROVINCE_CODE_ERROR("C0001", "省编码不正确"),
    CITY_CODE_ERROR("C0002", "市编码不正确"),
    DISTRICT_CODE_ERROR("C0003", "区编码不正确"),

    // 补传收款凭证
    BANK_STATEMENT_IS_UPLOAD("BC0001", "订单已补传银行流水截图"),
    BANK_STATEMENT_NUM_LIMIT("BC0002", "银行流水截图个数超过最大限制"),
    BANK_STATEMENT_NOT_SIGNED("BC0003", "未签收的订单无法申请补传"),

    // 企业注销
    COMPANY_NOT_BELONG_REG("Z0001","企业不属于当前用户"),
    ORDER_NOT_COMPLETED("Z0002","存在未完成的开票订单记录"),
    BILL_TAX_NO_PAYMENT("Z0003", "企业存在未补缴的税单，请先补缴后再注销"),

    // 企业证件申请、归还
    CERT_USE_TYPE_ERROR("R0001","申请的资源类型暂为开放"),
    CERT_NOT_IN_PARK("R0002","申请的资源不在园区，无法领用"),
    CERT_NOT_RETURN("R0003","判断证件的所在地，如果当前在园区则不能进行归还"),
    CERT_IN_USE("R0004","申请的资源在使用中"),
    CERT_IN_PARK("R0005","申请的资源已在园区，无法归还"),

    // 微信退款
    REFUND_FAILED("T0001", "微信退款失败，未收到退款下单结果"),

    //****************************接入方接口业务***************************
    // 商品同步赋码
    HAVE_SYNCHRONOUS("TP0001", "该订单商品已全部赋码"),
    HAVE_VOUCHER("TP0002", "已添加支付凭证，请勿重复添加"),
    ;

    private String code;
    private String text;

    private ErrorCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {

        this.text = text;
    }

    public static void main(String[] args) {

        ErrorCodeEnum error = ErrorCodeEnum.FAILED;
        try {
            error = testFun(100);    //测试方法。
        } catch (Exception e) {
            System.err.println("e: " + e);
            String errorMsg = e.getMessage();
            System.err.println("errorMsg: " + errorMsg);
            System.err.println("errorMsg2: " + e.getLocalizedMessage());

            if(errorMsg != null){
                String[] msg = errorMsg.split(",");
                if(msg != null && msg.length > 0){
                    ErrorCodeEnum.FAILED.setCode(msg[0]);
                    ErrorCodeEnum.FAILED.setText(msg[1]);
                }
            }
            //返回错误码
            error = ErrorCodeEnum.FAILED;
            System.err.println("code: " + error.getCode() );
            System.err.println("text: " + error.getText() );
        }
    }

    private static ErrorCodeEnum testFun(int i) throws Exception {
        System.err.println("接收到的参数值：" + i);
        ErrorCodeEnum error = ErrorCodeEnum.SUCCESS;
        String errorMsg = error.getCode() + "," + error.getText();  //将异常信息返回。
        throw new Exception(errorMsg);      //手动抛出异常。
    }

}
