package com.yuqian.itax.util.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @ClassName: Node
 * @Description: 关系树实体类
 * @Author: yejian
 * @Date: Created in 2020/6/3
 * @Version: 1.0
 * @Modified By:
 */
@Getter
@Setter
public class Node {

    /**
     * 节点id
     */
    private String id;

    /**
     * 父节点id
     */
    private String parentId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型
     */
    private Integer nodeType;

    /**
     * 节点等级
     */
    private Long nodeLevel;

    /**
     * 子节点
     */
    private List<Node> children;

    /**
     * 层级
     */
    private int level = 0;

    public Node addChild(Node node) {
        if (children == null) {
            this.children = new LinkedList<Node>();
        }
        children.add(node);
        return this;
    }

    public Node() {
        super();
    }

    public Node(String id, String nodeName, Integer nodeType, Long nodeLevel, String parentId) {
        super();
        this.id = id;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.nodeLevel = nodeLevel;
        this.parentId = parentId;
    }
}
