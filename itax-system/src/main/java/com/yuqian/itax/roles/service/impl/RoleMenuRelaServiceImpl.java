package com.yuqian.itax.roles.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.roles.dao.RoleMenuRelaMapper;
import com.yuqian.itax.roles.entity.RoleMenuRelaEntity;
import com.yuqian.itax.roles.service.RoleMenuRelaService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Service("roleMenuRelaService")
public class RoleMenuRelaServiceImpl extends BaseServiceImpl<RoleMenuRelaEntity,RoleMenuRelaMapper> implements RoleMenuRelaService {

    /**
     * 按菜单ID删除
     * @author Karen
     */
    @Override
    public Integer deleteByMenuId(Long id) {
        Example example = new Example(RoleMenuRelaEntity.class);
        example.createCriteria().andEqualTo("menuId",id);
        return mapper.deleteByExample(example);
    }
    @Override
    public List<RoleMenuRelaEntity> queryRoleMenu(Long userId) {
        return mapper.queryRoleMenu(userId);
    }

    @Override
    public Long[] findByRoleId(Long id) {
        List<RoleMenuRelaEntity> byRoleId = mapper.selectByRoleId(id);
        List<Long>  roleId = new ArrayList<>(byRoleId.size());
        for (RoleMenuRelaEntity sysRoleMenuEntity:byRoleId
        ) {roleId.add(sysRoleMenuEntity.getMenuId());
        }
        Long[] ids = new Long[roleId.size()];
        ids= roleId.toArray(ids);
        return ids;
    }
}

