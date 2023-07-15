package com.yuqian.itax.common.base.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.vo.PageResultVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName IBaseService
 * @Description Service接口
 * @Author jiangni
 * @Date 2019/7/15
 * @Version 1.0
 */
public interface IBaseService<T, K extends Mapper<T>> {
	/**
	 * 根据条件查找单个对象
	 * 
	 * @param t
	 * @return
	 */
	public T selectOne(T t);

	/**
	 * 根据条件查找对象
	 * 
	 * @param t
	 * @return
	 */
	public List<T> select(T t);

	/**
	 * 查询所有记录
	 * 
	 * @param
	 * @return
	 */
	public List<T> selectAll();


	/**
	 * 保存记录，不包括空值，使用数据库默认字段
	 * 
	 * @param t
	 * @return
	 */
	public int insertSelective(T t);

	/**
	 * 根据ID修改
	 * 
	 * @param t
	 * @return
	 */
	public int editByIdSelective(T t);
	
	/**
	 * 根据ID查询
	 * 
	 * @param id
	 * @return
	 */
	public T findById(Long id);

	/**
	 * 根据ID删除
	 * 
	 * @param id
	 * @return
	 */
	public int delById(Long id);
	
	/**
	 * 根据条件删除
	 * @param example
	 * @return
	 */
	public int delByExample(Object example);
	
	/**
	 * 分页查询
	 * @param t
	 * @param page
	 * @return
	 */
	public PageResultVo<T> findByPage(T t, PageInfo<T> page);

	/**
	 * @Description 按照指定排序查询集合
	 * @Author  Kaven
	 * @Date   2020/3/19 11:41
	 * @Param   example
	 * @Return List<T>
	 * @Exception
	*/
	List<T> selectByExample(Object example);

}
