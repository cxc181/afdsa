package com.yuqian.itax.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.dao.CommonProblemsMapper;
import com.yuqian.itax.system.entity.CommonProblemsEntity;
import com.yuqian.itax.system.entity.dto.CommonProblemsPO;
import com.yuqian.itax.system.entity.query.CommonProblemsQuery;
import com.yuqian.itax.system.entity.vo.CommonProblemsVO;
import com.yuqian.itax.system.service.CommonProblemsService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;


@Service("commonProblemsService")
public class CommonProblemsServiceImpl extends BaseServiceImpl<CommonProblemsEntity,CommonProblemsMapper> implements CommonProblemsService {

    /**
     * 根据机构编码获取常见问题列表
     * @param oemCode
     * @return
     */
    @Override
    public List<CommonProblemsEntity> getCommomProbleListByOemCode(String oemCode){
        return mapper.getCommomProbleListByOemCode(oemCode);
    }

    @Override
    public PageInfo<CommonProblemsVO> getCommomProblePageInfo(CommonProblemsQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(mapper.getCommomProbleList(query));
    }

    @Override
    public CommonProblemsEntity addCommonProblemsEntity(CommonProblemsPO po,String account) {
        CommonProblemsEntity commonProblemsEntity=new CommonProblemsEntity();
        commonProblemsEntity.setOrderNum(po.getOrderNum());
        commonProblemsEntity.setOemCode(po.getOemCode());
        List<CommonProblemsEntity> list=mapper.select(commonProblemsEntity);
        if(CollectionUtil.isNotEmpty(list)){
            throw  new BusinessException("当前排序已存在，请调整后再试！");
        }
        commonProblemsEntity.setProblem(po.getProblem());
        commonProblemsEntity.setAnswer(po.getAnswer());
        commonProblemsEntity.setAddTime(new Date());
        commonProblemsEntity.setAddUser(account);
        mapper.insert(commonProblemsEntity);
        return commonProblemsEntity;
    }

    @Override
    public CommonProblemsEntity updateCommonProblemsEntity(CommonProblemsPO po, String account) {
       CommonProblemsEntity commonProblemsEntity=mapper.selectByPrimaryKey(po.getId());
       if(null == commonProblemsEntity){
           throw  new BusinessException("查询常见问题失败常见问题不存在！");
       }
        CommonProblemsEntity commonProblemsEntityByorderNumNotId=mapper.findCommonProblemsEntityByorderNumNotId(po.getOrderNum(),po.getId(),po.getOemCode());
        if(commonProblemsEntityByorderNumNotId!=null){
            throw  new BusinessException("当前排序已存在，请调整后再试！");
        }
        commonProblemsEntity.setOrderNum(po.getOrderNum());
        commonProblemsEntity.setOemCode(po.getOemCode());
        commonProblemsEntity.setProblem(po.getProblem());
        commonProblemsEntity.setAnswer(po.getAnswer());
        commonProblemsEntity.setUpdateTime(new Date());
        commonProblemsEntity.setUpdateUser(account);
        mapper.updateByPrimaryKey(commonProblemsEntity);
       return commonProblemsEntity;
    }

    @Override
    public void batchInsertCommonProblems(List<CommonProblemsEntity> list ,String oemCode,String account) {
        mapper.batchInsertCommonProblems(list,oemCode,new Date(),account);
    }

    @Override
    public CommonProblemsVO getCommonProblemsById(Long id) {
        return mapper.getCommonProblemsById(id);
    }
}

