package com.yuqian.itax.tax.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description 签名确认税单DTO
 * @Author  lmh
 * @Date   2022/3/11
 */
@Getter
@Setter
public class SignConfirmDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 签名图片
     */
    @NotBlank(message = "签名图片不能为空")
    private String signImg;

    /**
     *  企业税单id
     */
    @NotNull(message = "企业税单id不能为空")
    private Long companyTaxBillId;

    /**
     * 成本项图片
     */
    private String costItemImgs;
}
