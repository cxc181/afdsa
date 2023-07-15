package com.yuqian.itax.order.entity.query;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
public class ThirdPartyQueryInoiveInfoQuery implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 机构编码
     */
    String oemCode;
    /**
     * 接入方编码
     */
    String accessPartyCode;
    /**
     * 企业id
     */
    @NotNull(message = "请输入企业Id")
    Long companyId;
    /**
     * 用户id
     */
    @NotNull(message = "请输入用户id")
    Long userId;
}
