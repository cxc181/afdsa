package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.LogisCompanyEntity;
import com.yuqian.itax.system.dao.LogisCompanyMapper;

import java.util.List;

/**
 * 物流公司信息service
 * 
 * @Date: 2020年02月13日 13:40:54 
 * @author 蒋匿
 */
public interface LogisCompanyService extends IBaseService<LogisCompanyEntity,LogisCompanyMapper> {
	/**
	 * 查询快递公司列表
     * @Author yejian
	 * @Date 2020/3/26 10:01
	 * @return
	 */
    List<LogisCompanyEntity> logisCompanyList();

	/**
	 * 根据快递公司名称查询快递公司
	 * @param companyName
	 * @return
	 */
	LogisCompanyEntity queryByCompanyName(String companyName);

	/**
	 * 根据快递公司名称模糊查询快递公司
	 * @param companyName
	 * @return
	 */
	List<LogisCompanyEntity> queryByLikeCompanyName(String companyName);
}

