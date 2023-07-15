package com.yuqian.itax.orgs.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class OrgVO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3577243811639686258L;

    private Long id; //菜单ID
    /**
     * 机构代码
     */
    private String oemCode;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 机构树
     */
    private String orgTree;
    /**
     *父组织ID，一级菜单为1
     */
    private Long parentOrgId;
    /**
     * 子机构
     */
    private List<OrgVO> children;
}
