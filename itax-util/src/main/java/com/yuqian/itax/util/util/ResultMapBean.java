package com.yuqian.itax.util.util;

/**
 * @description 接口返回对象Bean
 * @author Kaven
 * @date 2019-03-21
 */
public class ResultMapBean<T> {
    private String retCode;// 返回码
    private String retMsg;// 返回消息
    private T data;// 返回数据

    public ResultMapBean(){
        super();
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
