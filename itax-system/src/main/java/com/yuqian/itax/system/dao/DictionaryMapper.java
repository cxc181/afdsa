package com.yuqian.itax.system.dao;

import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.query.DictionaryQuery;
import com.yuqian.itax.system.entity.vo.DictionaryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字典表dao
 * 
 * @Date: 2019年12月06日 12:08:39 
 * @author Kaven
 */
@Mapper
public interface DictionaryMapper extends BaseMapper<DictionaryEntity> {

    /**
     * @Author Kaven
     * @Description 根据字典编码获取数据值
     * @Date 11:08 2019/12/06
     * @Param [dictCode]
     * @return com.yuqian.itax.system.entity.DictionaryEntity
     **/
    DictionaryEntity getDictByCode(String dictCode);

    /**
     * 查询字典信息
     */
    List<DictionaryVO> querySysDictionaryList(DictionaryQuery query);
}

