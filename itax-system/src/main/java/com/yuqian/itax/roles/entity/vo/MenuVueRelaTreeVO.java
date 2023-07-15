package com.yuqian.itax.roles.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MenuVueRelaTreeVO implements Serializable {

    private static final long serialVersionUID = 1363978702063968091L;
    private String path;
    private String component;
    private String redirect;
    private String name;
    private Integer orderNum;
    private Boolean hidden;
    private Integer type;
    private Map<String,Object> meta;
    private List<MenuVueRelaTreeVO> children;
}
