package com.yuqian.itax.agent.entity.vo;

import com.google.common.collect.Lists;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.common.util.CollectionUtil;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.GET;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * oem机构参数配置表
 * 
 * @Date: 2020年07月21日 11:19:36 
 * @author 蒋匿
 */
@Getter
@Setter
public class OemConfigVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 参数编码
	 */
	private String paramsCode;
	
	/**
	 * 参数值
	 */
	private String paramsValue;

	/**
	 * 参数描述
	 */
	private String paramsDesc;

	/**
	 * 备注
	 */
	private String remark;

	public OemConfigVO() {
	}

	public OemConfigVO(OemConfigEntity entity) {
		this.oemCode = entity.getOemCode();
		this.paramsCode = entity.getParamsCode();
		this.paramsValue = entity.getParamsValue();
		this.paramsDesc = entity.getParamsDesc();
		this.remark = entity.getRemark();
	}

	public static List<OemConfigVO> getList(List<OemConfigEntity> lists) {
		List<OemConfigVO> result = Lists.newArrayList();
		if (CollectionUtil.isEmpty(lists)) {
			return result;
		}
		for (OemConfigEntity entity : lists) {
			result.add(new OemConfigVO(entity));
		}
		return result;
	}
}
