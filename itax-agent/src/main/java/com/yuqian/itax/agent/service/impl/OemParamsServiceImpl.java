package com.yuqian.itax.agent.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.po.OemParamsPO;
import com.yuqian.itax.agent.entity.query.OemParamsQuery;
import com.yuqian.itax.agent.entity.vo.OemParamsVO;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.agent.dao.OemParamsMapper;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("oemParamsService")
public class OemParamsServiceImpl extends BaseServiceImpl<OemParamsEntity,OemParamsMapper> implements OemParamsService {
    @Resource
    private OemParamsMapper oemParamsMapper;

    @Override
    public OemParamsEntity getParams(String oemCode, int paramsType) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("oemCode",oemCode);
        params.put("paramsType",paramsType);
        return this.oemParamsMapper.getParams(params);
    }

    @Override
    public PageInfo<OemParamsVO> querySysOemParamsPageInfo(OemParamsQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(mapper.querySysOemParamsList(query));
    }

    @Override
    public void addSysOemParams(OemParamsPO po, String account) {
        //查询是否又该种类型
        OemParamsEntity entity= new OemParamsEntity();
        entity.setOemCode(po.getOemCode());
        entity.setParamsType(po.getParamsType());
        entity.setStatus(1);
        List<OemParamsEntity> list=mapper.select(entity);
        if(list.size()>0){
            throw  new BusinessException("已经存在该类型得有效数据，不允许新增。");
        }
        OemParamsEntity oemParamsEntity=new OemParamsEntity();
        if(null!=po.getOemCode() && !"".equals(po.getOemCode())){
            oemParamsEntity.setOemCode(po.getOemCode());
        }
        if(null!=po.getParamsType() ){
            oemParamsEntity.setParamsType(po.getParamsType());
        }
        if(null!=po.getAccount() && !"".equals(po.getAccount())){
            oemParamsEntity.setAccount(po.getAccount());
        }
        if(null!=po.getSecKey() && !"".equals(po.getSecKey())){
            oemParamsEntity.setSecKey(po.getSecKey());
        }
        if(null!=po.getUrl() && !"".equals(po.getUrl())){
            oemParamsEntity.setUrl(po.getUrl());
        }
        if(null!=po.getParamsValues() && !"".equals(po.getParamsValues())){
            oemParamsEntity.setParamsValues(po.getParamsValues());
        }
        if(null!=po.getPublicKey() && !"".equals(po.getPublicKey())){
            oemParamsEntity.setPublicKey(po.getPublicKey());
        }
        if(null!=po.getPrivateKey() && !"".equals(po.getPrivateKey())){
            oemParamsEntity.setPrivateKey(po.getPrivateKey());
        }
        if(null!=po.getRemark() && !"".equals(po.getRemark())){
            oemParamsEntity.setRemark(po.getRemark());
        }
        oemParamsEntity.setStatus(1);
        oemParamsEntity.setAddTime(new Date());
        oemParamsEntity.setAddUser(account);
        mapper.insert(oemParamsEntity);
    }

    @Override
    public void updateSysDictionaryList(OemParamsPO po, String account) {
        //查询是不是存在修改后得有效类型
        OemParamsEntity entity= new OemParamsEntity();
        entity.setOemCode(po.getOemCode());
        entity.setParamsType(po.getParamsType());
        entity.setStatus(1);
        List<OemParamsEntity> list=mapper.select(entity);
        if(list.size()>0){
            for(int i=0;i< list.size();i++){
                OemParamsEntity oemParamsEntity=list.get(i);
                if(oemParamsEntity.getId()!=null && !oemParamsEntity.getId().equals(po.getId())){
                    throw  new BusinessException("已经存在该类型得有效数据。");
                }
            }
        }
        OemParamsEntity oemParamsEntity=mapper.selectByPrimaryKey(po.getId());
        if(null!=po.getOemCode() && !"".equals(po.getOemCode())){
            oemParamsEntity.setOemCode(po.getOemCode());
        }
        if(null!=po.getParamsType() ){
            oemParamsEntity.setParamsType(po.getParamsType());
        }
        if(null!=po.getAccount() && !"".equals(po.getAccount())){
            oemParamsEntity.setAccount(po.getAccount());
        }
        if(null!=po.getSecKey() && !"".equals(po.getSecKey())){
            oemParamsEntity.setSecKey(po.getSecKey());
        }
        if(null!=po.getUrl() && !"".equals(po.getUrl())){
            oemParamsEntity.setUrl(po.getUrl());
        }
        if(null!=po.getParamsValues() && !"".equals(po.getParamsValues())){
            oemParamsEntity.setParamsValues(po.getParamsValues());
        }
        if(null!=po.getPublicKey() && !"".equals(po.getPublicKey())){
            oemParamsEntity.setPublicKey(po.getPublicKey());
        }
        if(null!=po.getPrivateKey() && !"".equals(po.getPrivateKey())){
            oemParamsEntity.setPrivateKey(po.getPrivateKey());
        }
        if(null!=po.getStatus() ){
            oemParamsEntity.setStatus(po.getStatus());
        }
        if(null!=po.getRemark() && !"".equals(po.getRemark())){
            oemParamsEntity.setRemark(po.getRemark());
        }
        oemParamsEntity.setId(po.getId());
        oemParamsEntity.setUpdateTime(new Date());
        oemParamsEntity.setUpdateUser(account);
        mapper.updateByPrimaryKey(oemParamsEntity);
    }

    @Override
    public OemParamsEntity getYishuiParam(String merchantCode) {
        Example example = new Example(OemParamsEntity.class);
        example.createCriteria().andEqualTo("paramsType", OemParamsTypeEnum.YI_SHUI.getValue())
                .andLike("paramsValues", "%\"merchantCode\":\"" + merchantCode + "\"%");
        List<OemParamsEntity> list = this.oemParamsMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }
}

