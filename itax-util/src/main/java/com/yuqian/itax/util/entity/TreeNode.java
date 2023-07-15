package com.yuqian.itax.util.entity;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @ClassName: TreeNode
 * @Description: 关系树解析
 * @Author: yejian
 * @Date: Created in 2020/6/3
 * @Version: 1.0
 */
public class TreeNode {
    private LinkedList<Node> list = new LinkedList<Node>();
    private Map<String, Node> nodeMap = new HashMap<String, Node>();

    public LinkedList<Node> getList() {
        return list;
    }
    public void setList(LinkedList<Node> list) {
        this.list = list;
    }
    public Map<String, Node> getNodeMap() {
        return nodeMap;
    }
    public void setNodeMap(Map<String, Node> nodeMap) {
        this.nodeMap = nodeMap;
    }

    // 组装树
    public TreeNode addNode(Node node, String parentId){
        nodeMap.put(node.getId(), node);

        if (StringUtils.isEmpty(parentId)) {
            list.add(node);
        } else {
            Node treenode = nodeMap.get(parentId);
            if (treenode == null) {

            } else {
                treenode.addChild(node);
            }
        }
        return this;
    }

    // 初始化层级
    public void initLevel() {
        for (Node node : list) {
            initNodeLevel(node);
        }
    }

    public void  initNodeLevel(Node node){

        String parentId = node.getParentId();
        if(StringUtils.isEmpty(parentId)){
            node.setLevel(1);
        }
        List<Node> childrenlist = node.getChildren();
        if(childrenlist!=null&&childrenlist.size()>0){
            for (Node nodechildren : childrenlist) {
                nodechildren.setLevel(node.getLevel()+1);
                List<Node> children = nodechildren.getChildren();
                if(children!=null&&children.size()>0){
                    initNodeLevel(nodechildren);
                }
            }
        }
    }

    // 获取最大层级
    public int maxLevel() {
        int m0 = 0;
        Iterator<Node> it = nodeMap.values().iterator();
        while(it.hasNext()){
            Node node = it.next();
            int level = node.getLevel();
            m0 = Math.max(m0, level);
        }
        return m0;
    }

    // 循环获取当前节点层级的所有节点
    public List<Node>  getListWithlevel(int level){
        List<Node> list =new ArrayList<Node>();
        Iterator<Node> it = nodeMap.values().iterator();
        while(it.hasNext()){
            Node node = it.next();
            int nodelevel = node.getLevel();
            if(nodelevel==level){
                list.add(node);
            }
        }
        return list;
    }

    // 循环获取当前节点下的子节点
    public List<Node> getsonNode(String id){
        Node node = nodeMap.get(id);
        List<Node> children = node.getChildren();
        if(children!=null && children.size()>0){
            return children;
        }else{
            return null;
        }
    }

    /**
     * 解决Collectors.groupingBy元素无法映射为空键问题
     */
    public static <T, A> Collector<T, ?, Map<A, List<T>>> groupingBy_WithNullKeys(Function<? super T, ? extends A> classifier) {
        return Collectors.toMap(
                classifier,
                Collections::singletonList,
                (List<T> oldList, List<T> newEl) -> {
                    List<T> newList = new ArrayList<>(oldList.size() + 1);
                    newList.addAll(oldList);
                    newList.addAll(newEl);
                    return newList;
                });
    }

    // 按父节点分组
    public Map<String, List<Node>> groupByParentId(List<Node> sources){
        Map<String, List<Node>> listGroupby = sources.parallelStream().collect(groupingBy_WithNullKeys(Node::getParentId));
        return listGroupby;
    }

    // 根据父节点获取所有节点
    public void getLevel(String parentId, Map<String, List<Node>> parentMap, Map<String, List<Node>> levelMap, int count){

        List<Node> nextLevel = parentMap.get(parentId);
        if(CollectionUtils.isEmpty(nextLevel)){
            return;
        }

        String countStr = String.valueOf(count);
        List<Node> thisLevel = levelMap.get(countStr);
        if (CollectionUtils.isEmpty(thisLevel)) {
            levelMap.put(countStr, nextLevel);
        } else {
            thisLevel.addAll(nextLevel);
            levelMap.put(countStr, thisLevel);
        }

        count++;
        if(count > maxLevel()){
            return;
        }

        for (Node Node : nextLevel) {
            String tempParentId = Node.getId();
            getLevel(tempParentId, parentMap, levelMap, count);
        }
    }
}
