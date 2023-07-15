package com.yuqian.itax.message.entity.dto;

import com.yuqian.itax.message.enums.SendResultCodeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/6 15:36
 *  @Description: 短信发送消息结果
 */
@Setter
@Getter
public class SendResultDto implements Serializable {

    /**  */
    private static final long serialVersionUID = 2573157439484831637L;

    /**
     * 是否成功
     */
    private boolean           isSuccess;

    /**
     * 处理结果
     */
    private SendResultCodeEnum resultCode;

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * @return
     */
    public boolean isSuccess() {
        if (SendResultCodeEnum.SUCCESS.equals(resultCode)) {
            return true;
        }
        return isSuccess;
    }

    public SendResultCodeEnum getResultCode() {
        return resultCode;
    }

    public void setResultCode(SendResultCodeEnum resultCode) {
        this.resultCode = resultCode;
    }

}

