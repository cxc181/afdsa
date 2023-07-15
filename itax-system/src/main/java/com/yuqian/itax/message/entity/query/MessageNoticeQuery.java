package com.yuqian.itax.message.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import com.yuqian.itax.util.validator.Add;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/6 11:12
 *  @Description: 消息列表查询参数Bean
 */
@Getter
@Setter
public class MessageNoticeQuery extends BaseQuery implements Serializable {
    /**
     * 状态 0-未读 1-全部
     */
    @NotNull(message="消息类型不能为空")
    private Integer status;

    private Long userId;// 用户ID

}