package com.yuqian.itax.message.entity.dto;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
@Setter
@Getter
public class NoticeManageUserDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 用户手机号
     */
    @Excel(name = "手机号")
    @NotBlank(message = "用户手机号为空")
    private String memberPhone;

    @Excel(name = "失败原因")
    private String failed;




}
