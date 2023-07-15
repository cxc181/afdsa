package com.yuqian.itax.common.base.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.util.ObjectUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

//@Transactional
public abstract class BaseServiceImpl<T, K extends Mapper<T>> implements IBaseService<T, K>, InitializingBean {

	protected K mapper;

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public T selectOne(T t) {
		return mapper.selectOne(t);
	}

	@Override
	public List<T> select(T t) {
		return mapper.select(t);
	}

	@Override
	public List<T> selectAll() {
		return mapper.selectAll();
	}

	@Override
	public int insertSelective(T t) {
		return mapper.insertSelective(t);
	}
	
	@Override
	public int editByIdSelective(T t) {
		return mapper.updateByPrimaryKeySelective(t);
	}

	@Override
	public T findById(Long id) {
		return mapper.selectByPrimaryKey(id);
	}

	@Override
	public int delById(Long id) {
		return mapper.deleteByPrimaryKey(id);
	}

	@Override
	public int delByExample(Object example) {
		return mapper.deleteByExample(example);
	}

	@Override
	public PageResultVo<T> findByPage(T t, PageInfo<T> page) {
		PageHelper.startPage(page.getPageNum(), page.getPageSize());
		PageInfo<T> pageInfo = new PageInfo<T>(mapper.select(t));

		PageResultVo<T> result = new PageResultVo<T>();
		result.setList(pageInfo.getList());
		result.setTotal(pageInfo.getTotal());
		result.setPages(pageInfo.getPages());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		List<Class<?>> list = ObjectUtil.genericClass(this.getClass(), BaseServiceImpl.class);
		String mapperName = list.get(1).getSimpleName();
		mapper = (K) applicationContext.getBean(mapperName.substring(0, 1).toLowerCase() + mapperName.substring(1, mapperName.length()));
	}

	@Override
	public List<T> selectByExample(Object example) {
		return mapper.selectByExample(example);
	}
}
