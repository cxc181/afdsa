package com.yuqian.itax.orgs.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.orgs.dao.OrgMapper;
import com.yuqian.itax.orgs.entity.OrgEntity;
import com.yuqian.itax.orgs.entity.vo.OrgVO;
import com.yuqian.itax.orgs.service.OrgService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("orgService")
public class OrgServiceImpl extends BaseServiceImpl<OrgEntity,OrgMapper> implements OrgService {
    /**
     * 获取全部菜单树（包含按钮）
     * @author Karen
     * @return
     */
    @Override
    public List<OrgVO> queryOrgTreeAll(Long orgId) {
        List<OrgVO> orgTreeVOList = mapper.queryAllOrg();
        List<OrgVO> orgList = new ArrayList<>();
        for (OrgVO treeOrg: orgTreeVOList) {
            if (treeOrg.getId().equals(orgId)) {
                orgList.add(treeOrg);
            }
        }
        for (OrgVO org: orgList) {
            org.setChildren(getChildren(org.getId(),orgTreeVOList));
        }
        return orgList;
    }

    @Override
    public OrgEntity queryOrgEntityByUserId(Long userId) {
        return mapper.queryOrgEntityByUserId(userId);
    }

    /**
     * 获取菜单树的子菜单
     * @author Karen
     * @param id
     * @param orgList
     * @return
     */
    private List<OrgVO> getChildren(Long id, List<OrgVO> orgList) {
        List<OrgVO> childList = new ArrayList<>();
        for (OrgVO org: orgList) {
            if (id.equals(org.getParentOrgId())) {
                childList.add(org);
            }
        }
        for (OrgVO childOrg: childList) {
            childOrg.setChildren(getChildren(childOrg.getId(),orgList));
        }
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }
}

