package com.yuqian.itax.orgs.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.orgs.entity.OrgEntity;
import com.yuqian.itax.orgs.dao.OrgMapper;
import com.yuqian.itax.orgs.entity.vo.OrgVO;

import java.util.List;

/**
 * 组织管理service
 * 
 * @Date: 2019年12月08日 20:57:08 
 * @author 蒋匿
 */
public interface OrgService extends IBaseService<OrgEntity,OrgMapper> {
    /**
     * 获取全部菜单树（包含按钮）
     * @author Karen
     * @return
     */
    List<OrgVO> queryOrgTreeAll(Long orgId);

    /**
     * 根据USerID获取组织
     */
    OrgEntity queryOrgEntityByUserId(Long userId);
}

