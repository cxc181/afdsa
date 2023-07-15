package com.yuqian.itax.orgs.dao;

import com.yuqian.itax.orgs.entity.OrgEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.orgs.entity.vo.OrgVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 组织管理dao
 * 
 * @Date: 2019年12月08日 20:57:08 
 * @author 蒋匿
 */
@Mapper
public interface OrgMapper extends BaseMapper<OrgEntity> {
    /**
     * 根据OEM获取组织信息
     */
    OrgEntity getOemInfo(@Param("oemCode")String oemCode);

    List<OrgVO> queryAllOrg();

    OrgEntity queryOrgEntityByUserId(@Param( "userId") Long userId);
}

