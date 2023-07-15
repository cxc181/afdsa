package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: yejian
 *  @Date: 2020/03/25 10:08
 *  @Description: 企业资源申请记录查询参数Bean
 */
@Getter
@Setter
public class CompResApplyRecordQuery extends BaseQuery implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 企业id
	 */
	@NotNull(message="企业id不能为空")
	private Long companyId;

}
