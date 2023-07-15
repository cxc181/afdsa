package com.yuqian.itax.common.base.vo;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

public class PageResultVo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> list;

    /**
     * 页码，从1开始
     */
    @ApiModelProperty(value = "页码")
    private int pageNum;
    /**
     * 页面大小
     */
    @ApiModelProperty(value = "每页数量")
    private int pageSize;

    /**
     * 总数
     */
    @ApiModelProperty(value = "总数")
    private long total;
    /**
     * 总页数
     */
    @ApiModelProperty(value = "总页数")
    private int pages;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private String orderBy;

    public int getPages() {
        return pages;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotal() {
        return total;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> result) {
        this.list = result;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public PageResultVo() {
    }

    public PageResultVo(List<T> result, long total) {
        this.setTotal(total == -1 ? result.size() : total);
        this.setList(result);
    }

    /**
     * 将PageHelper分页后的list转为分页信息
     */
    public static <T> PageResultVo<T> restPage(List<T> list) {
        PageResultVo<T> result = new PageResultVo<T>();
        PageInfo<T> pageInfo = new PageInfo<T>(list);
        result.setPages(pageInfo.getPages());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setList(pageInfo.getList());
        return result;
    }

    public static <T> PageInfo listToPage(int pageNum, int pageSize, List<T> list){
        //创建Page类
        Page page = new Page(pageNum, pageSize);
        //为Page类中的total属性赋值
        int total = list.size();
        page.setTotal(total);
        //计算当前需要显示的数据下标起始值
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        //从链表中截取需要显示的子链表，并加入到Page
        page.addAll(list.subList(startIndex, endIndex));
        //以Page创建PageInfo
        PageInfo pageInfo = new PageInfo<>(page);
        return pageInfo;
    }

    public static <T> PageResultVo listToPageResult(int pageNum, int pageSize, List<T> list) {
        //创建Page类
        Page page = new Page(pageNum, pageSize);
        //为Page类中的total属性赋值
        int total = list.size();
        page.setTotal(total);
        //计算当前需要显示的数据下标起始值
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        //从链表中截取需要显示的子链表，并加入到Page
        page.addAll(list.subList(startIndex, endIndex));
        //以Page创建PageInfo
        PageInfo pageInfo = new PageInfo<>(page);

        // 返回分页信息
        PageResultVo<T> pageResult = new PageResultVo<T>();
        pageResult.setList(pageInfo.getList());
        pageResult.setTotal(pageInfo.getTotal());
        pageResult.setPages(pageInfo.getPages());
        pageResult.setPageSize(pageSize);
        pageResult.setPageNum(pageNum);
        return pageResult;
    }

}
