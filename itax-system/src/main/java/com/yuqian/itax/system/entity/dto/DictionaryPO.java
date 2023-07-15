package com.yuqian.itax.system.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class DictionaryPO implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * id
     */
    Long id;
    /**
     * 编码
     */
    @NotBlank(message="字典编码不能为空")
    String dictCode;

    /**
     * 值
     */
    @NotBlank(message="字典值不能为空")
    String dictValue;
    /**
     * 上级ID
     */
    Long parentDictId;
    /**
     * 描述
     */
    String dictDesc;
}
