package com.yuqian.itax.user.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.CustomerServiceWorkNumberEntity;
import com.yuqian.itax.user.dao.CustomerServiceWorkNumberMapper;
import com.yuqian.itax.user.entity.po.CustomerServiceWorkPO;
import com.yuqian.itax.user.entity.query.CustomerServiceWorkQuery;
import com.yuqian.itax.user.entity.vo.CustomerServiceWorkVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 坐席客服工号表service
 * 
 * @Date: 2019年12月12日 15:14:27 
 * @author 蒋匿
 */
public interface CustomerServiceWorkNumberService extends IBaseService<CustomerServiceWorkNumberEntity,CustomerServiceWorkNumberMapper> {

    /**
     * 工号分页
     * @author  hz
     */
    PageInfo<CustomerServiceWorkVO> customerServiceWorkPageInfo(CustomerServiceWorkQuery customerServiceWorkQuery);

    /**
    * 新增工号
    * @author hz
    */

    CustomerServiceWorkNumberEntity addCustomerServiceWorkNumberEntity(CustomerServiceWorkPO customerServiceWorkPO,Long userId);


    /**
     * 更具USERid查询
     */
    List<CustomerServiceWorkNumberEntity> queryCustomerServiceWorkNumberEntityByuserId(@Param("userId") Long userId);

}

