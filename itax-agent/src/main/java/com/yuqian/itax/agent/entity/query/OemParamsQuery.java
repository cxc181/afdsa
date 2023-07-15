package com.yuqian.itax.agent.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OemParamsQuery extends BaseQuery {


    Long id;
    /**
     * 参数类型 1- 短信 2-微信支付 3-快捷支付 4-北京代付
     */
    Integer paramsType;
    /**
     * 账号
     */
    String account;
    /**
     * 秘钥
     */
    String secKey;
    /**
     * url地址
     */
    String url;
    /**
     * 参数值
     */
    String paramsValues;
    /**
     * 公钥
     */
    String publicKey;
    /**
     * 私钥
     */
    String privateKey;
    /**
     *状态 0-不可用 1-可用
     */
    Integer status;

}
