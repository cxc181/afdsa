package com.yuqian.itax.user.entity.po;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UpdateMemberPhonePO  {
    private static final long serialVersionUID = -1L;
    /**
     * 会员id
     */
    @NotNull(message = "操作会员主键不能为空")
    private Long id;
    /**
     * 新手机号
     */
    @NotBlank(message="新手机号不能为空")
    @Pattern(regexp = "^0?1\\d{10}$")
    private String phone;
    /**
     * 修改原因
     */
    @NotBlank(message = "请输入修改原因")
    private String remark;
    /**
     * 上传附件
     */
    @NotBlank(message = "请上传附件")
    private String fileUrl;

}
