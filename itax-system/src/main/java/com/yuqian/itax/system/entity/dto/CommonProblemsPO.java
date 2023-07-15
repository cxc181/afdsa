package com.yuqian.itax.system.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 常见问题数据接受实体
 */
@Getter
@Setter
public class CommonProblemsPO  implements Serializable {
    private static final long serialVersionUID = -1L;

    private Long id ;

    /**
     * 机构编码
     */
    @NotBlank(message = "请选择机构")
    private String oemCode;

    /**
     * 顺序
     */
    @NotNull(message = "请输入序号")
    private Integer orderNum;
    /**
     * 问题
     */
    @NotBlank(message = "请输入问题")
    private String problem;
    /**
     * 答案
     */
    @NotBlank(message = "请输入答案")
    private String answer;
}
