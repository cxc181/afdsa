package com.yuqian.itax.system.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.dao.DictionaryMapper;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.dto.DictionaryPO;
import com.yuqian.itax.system.entity.query.DictionaryQuery;
import com.yuqian.itax.system.entity.vo.DictionaryVO;

import java.util.List;

/**
 * 字典表service
 * 
 * @Date: 2019年12月06日 12:08:39 
 * @author Kaven
 */
public interface DictionaryService extends IBaseService<DictionaryEntity,DictionaryMapper> {
    /**
     * @Author Kaven
     * @Description 根据字典编码获取字典值
     * @Date 12:05 2019/12/06
     * @Param [dictCode]
     * @return com.yuqian.mpos.system.entity.SysDictionaryEntity
     **/
    DictionaryEntity getByCode(String dictCode);
    /**
     * @Author HZ
     * @Description 根据条件 获取字典
     * @Date 12:05 2020/2/10
     * @return com.yuqian.mpos.system.entity.SysDictionaryEntity
     **/
    PageInfo<DictionaryVO> querySysDictionaryPageInfo(DictionaryQuery query);
    /**
     * @Author HZ
     * @Description 新增字典
     * @Date 12:05 2020/2/10
     * @return com.yuqian.mpos.system.entity.SysDictionaryEntity
     **/
    void addSysDictionaryList(DictionaryPO po,String account);
    /**
     * @Author HZ
     * @Description 编辑字典
     * @Date 12:05 2020/2/10
     * @return com.yuqian.mpos.system.entity.SysDictionaryEntity
     **/
    void updateSysDictionaryList(DictionaryPO po,String account);

    /**
     * 根据字典编码获取字典值
     * @param dictCode 字典key
     * @return
     */
    String getValueByCode(String dictCode);

    /**
     * 根据字典编码获取字典值
     * @param dictCode 字典key
     * @param defaultValue 默认值
     * @return
     */
    String getValueByCode(String dictCode, String defaultValue);

    /**
     * 根据字典编码获取字典值
     * @param dictCode 字典key
     * @param throwException 是否抛出异常，true，没有值会抛出异常，否则返回null
     * @param exceptionDesc 抛异常信息
     * @param defaultValue 默认值
     * @return
     */
    String getValueByCode(String dictCode, boolean throwException, String exceptionDesc, String defaultValue);
}

