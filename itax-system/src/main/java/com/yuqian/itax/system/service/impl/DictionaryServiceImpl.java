package com.yuqian.itax.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.DictionaryMapper;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.dto.DictionaryPO;
import com.yuqian.itax.system.entity.query.DictionaryQuery;
import com.yuqian.itax.system.entity.vo.DictionaryVO;
import com.yuqian.itax.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service("sysDictionaryService")
public class DictionaryServiceImpl extends BaseServiceImpl<DictionaryEntity,DictionaryMapper> implements DictionaryService {

    @Override
    public DictionaryEntity getByCode(String dictCode) {
        log.debug("获取系统数据字典{}",dictCode);
        return mapper.getDictByCode(dictCode);
    }

    @Override
    public PageInfo<DictionaryVO> querySysDictionaryPageInfo(DictionaryQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(mapper.querySysDictionaryList(query));
    }

    @Override
    public void addSysDictionaryList(DictionaryPO po,String account) {
        DictionaryEntity dictionaryEntity=new DictionaryEntity();
        if(null!=po.getDictCode() && !"".equals(po.getDictCode())){
            dictionaryEntity.setDictCode(po.getDictCode());
        }
        if(null!=po.getDictValue() && !"".equals(po.getDictValue())){
            dictionaryEntity.setDictValue(po.getDictValue());
        }
        if(null!=po.getParentDictId()){
            dictionaryEntity.setParentDictId(po.getParentDictId());
        }
        if(null!=po.getDictDesc() && !"".equals(po.getDictDesc())){
            dictionaryEntity.setDictDesc(po.getDictDesc());
        }
        dictionaryEntity.setAddTime(new Date());
        dictionaryEntity.setAddUser(account);
        mapper.insert(dictionaryEntity);
    }

    @Override
    public void updateSysDictionaryList(DictionaryPO po, String account) {
        DictionaryEntity dictionaryEntity=mapper.selectByPrimaryKey(po.getId());
        if(null!=po.getDictCode() && !"".equals(po.getDictCode())){
            dictionaryEntity.setDictCode(po.getDictCode());
        }
        if(null!=po.getDictValue() && !"".equals(po.getDictValue())){
            dictionaryEntity.setDictValue(po.getDictValue());
        }
        if(null!=po.getParentDictId()){
            dictionaryEntity.setParentDictId(po.getParentDictId());
        }
        if(null!=po.getDictDesc() && !"".equals(po.getDictDesc())){
            dictionaryEntity.setDictDesc(po.getDictDesc());
        }
        dictionaryEntity.setId(po.getId());
        dictionaryEntity.setUpdateTime(new Date());
        dictionaryEntity.setUpdateUser(account);
        mapper.updateByPrimaryKey(dictionaryEntity);
    }

    @Override
    public String getValueByCode(String dictCode) {
        return getValueByCode(dictCode, false, null, null);
    }

    @Override
    public String getValueByCode(String dictCode, String defaultValue) {
        return getValueByCode(dictCode, false, null, defaultValue);
    }

    @Override
    public String getValueByCode(String dictCode, boolean throwException, String exceptionDesc, String defaultValue) {
        Optional<String> value = Optional.ofNullable(mapper.getDictByCode(dictCode)).map(DictionaryEntity::getDictValue);
        if (throwException) {
            String str = StringUtils.isBlank(exceptionDesc) ? "字典表未配置，key：" + dictCode : exceptionDesc;
            return value.orElseThrow(()-> new BusinessException(str));
        }
        return value.orElse(defaultValue);
    }
}

