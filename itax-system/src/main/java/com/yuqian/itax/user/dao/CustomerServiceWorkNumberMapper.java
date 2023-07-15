package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.CustomerServiceWorkNumberEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.query.CustomerServiceWorkQuery;
import com.yuqian.itax.user.entity.vo.CustomerServiceWorkVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 坐席客服工号表dao
 * 
 * @Date: 2019年12月12日 15:14:27 
 * @author 蒋匿
 */
@Mapper
public interface CustomerServiceWorkNumberMapper extends BaseMapper<CustomerServiceWorkNumberEntity> {
    /**
     * 更具USERid查询
     */
    List<CustomerServiceWorkNumberEntity> queryCustomerServiceWorkNumberEntityByuserId(@Param("userId") Long userId);

    /**
     * 根据OEM查询工号列表
     */
    List<CustomerServiceWorkVO> getCustomerServiceWorkList(CustomerServiceWorkQuery customerServiceWorkQuery);
}

