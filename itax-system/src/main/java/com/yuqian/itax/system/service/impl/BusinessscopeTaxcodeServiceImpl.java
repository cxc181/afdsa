package com.yuqian.itax.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.dao.BusinessscopeTaxcodeMapper;
import com.yuqian.itax.system.entity.BusinessScopeEntity;
import com.yuqian.itax.system.entity.BusinessscopeTaxcodeEntity;
import com.yuqian.itax.system.entity.query.BusinessScopeTaxCodeQuery;
import com.yuqian.itax.system.entity.vo.BusinessScopeBatchVO;
import com.yuqian.itax.system.entity.vo.BusinessScopeTaxCodeVO;
import com.yuqian.itax.system.service.BusinessscopeTaxcodeService;
import com.yuqian.itax.util.util.StringUtil;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service("businessscopeTaxcodeService")
public class BusinessscopeTaxcodeServiceImpl extends BaseServiceImpl<BusinessscopeTaxcodeEntity,BusinessscopeTaxcodeMapper> implements BusinessscopeTaxcodeService {

    @Override
    public PageResultVo<BusinessScopeTaxCodeVO> list(BusinessScopeTaxCodeQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return PageResultVo.restPage(mapper.queryPageList(query));
    }

    @Override
    public List<BusinessScopeTaxCodeVO> queryBusinessScopeTaxcode(BusinessScopeTaxCodeQuery query) {
        return mapper.queryPageList(query);
    }

    @Override
    public BusinessscopeTaxcodeEntity getVBusinessScopeByScopNameAndCode(String businessScopName, String taxClassificationCode) {
        return mapper.getVBusinessScopeByScopNameAndCode(businessScopName,taxClassificationCode);
    }

    @Override
    public Map<String, Object> checkScopeTaxCode(List<BusinessScopeBatchVO> list, String userName) {
        List<BusinessScopeBatchVO> failList = Lists.newArrayList();
        Map<String,Object> resultMap = new HashMap<>();
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        //  去除无效行
        for (int i = 0;i<list.size();i++){
            if(StringUtil.isEmpty(list.get(i).getBusinessScopName())){
                list.get(i).setFalg(false);
                list.get(i).setMsg("经营范围不能为空");
                failList.add( list.get(i));
                continue;
            }else{
                if (list.get(i).getBusinessScopName().length()>100){
                    list.get(i).setFalg(false);
                    list.get(i).setMsg("经营范围长度超出100个字符");
                    failList.add( list.get(i));
                    continue;
                }
            }
            if(StringUtil.isEmpty(list.get(i).getTaxClassificationCode())){
                list.get(i).setFalg(false);
                list.get(i).setMsg("税收分类编码不能为空");
                failList.add( list.get(i));
                continue;
            }else{
                if (list.get(i).getTaxClassificationCode().length()>50) {
                    list.get(i).setFalg(false);
                    list.get(i).setMsg("税收分类编码长度超出50个字符");
                    failList.add(list.get(i));
                    continue;
                }else{
                    // 税收分类编码只能为数字
                    Matcher isNum = pattern.matcher(list.get(i).getTaxClassificationCode());
                    if (!isNum.matches()){
                        list.get(i).setFalg(false);
                        list.get(i).setMsg("税收分类编码只支持数字");
                        failList.add(list.get(i));
                        continue;
                    }
                }
            }
            if(StringUtil.isEmpty(list.get(i).getTaxClassificationName())){
                list.get(i).setFalg(false);
                list.get(i).setMsg("税收分类名称不能为空");
                failList.add( list.get(i));
                continue;
            }else{
                if (list.get(i).getTaxClassificationName().length()>50){
                    list.get(i).setFalg(false);
                    list.get(i).setMsg("税收分类名称长度超出50个字符");
                    failList.add( list.get(i));
                    continue;
                }
            }
            if(StringUtil.isNotBlank(list.get(i).getRemark()) &&  list.get(i).getRemark().length()>100){
                list.get(i).setFalg(false);
                list.get(i).setMsg("备注不能超过100个字");
                failList.add( list.get(i));
                continue;
            }
        }
        List<String> nameAndCode = new ArrayList<>();
        // 去除表中数据重复
        for (int i=0;i<list.size()-1;i++){
            if (!list.get(i).isFalg()){
                continue;
            }
            String data = list.get(i).getBusinessScopName()+list.get(i).getTaxClassificationCode();
            for (int j=i+1;j<list.size();j++){
                String newData = list.get(j).getBusinessScopName()+list.get(j).getTaxClassificationCode();
                if (data.equals(newData)){
                    list.get(j).setFalg(false);
                    list.get(j).setMsg("表中数据重复");
                    failList.add( list.get(j));
                }
            }
            nameAndCode.add(data);
        }
        if (CollectionUtil.isNotEmpty(nameAndCode)){
            //  根据经营范围和税收分类编码查询数据
            List<BusinessscopeTaxcodeEntity> entityList =  mapper.getBusinessScopeByConcatNameAndCode(nameAndCode);
            // 去除数据库重复数据
            if (CollectionUtil.isNotEmpty(entityList)){
                for (int i=0;i<list.size()-1;i++){
                    if (!list.get(i).isFalg()){
                        continue;
                    }
                    String data = list.get(i).getBusinessScopName()+list.get(i).getTaxClassificationCode();
                    for (BusinessscopeTaxcodeEntity entity:entityList){
                        String newData = entity.getBusinessScopName()+entity.getTaxClassificationCode();
                        if (data.equals(newData)){
                            list.get(i).setFalg(false);
                            list.get(i).setMsg("数据库已有该数据");
                            failList.add( list.get(i));
                        }
                    }
                }
            }
        }
        // 插入数据库的数据
        List<BusinessscopeTaxcodeEntity> insertList = new ArrayList<>();
        for (BusinessScopeBatchVO vo:list){
            if (!vo.isFalg()){
                continue;
            }
            BusinessscopeTaxcodeEntity entity = new BusinessscopeTaxcodeEntity();
            entity.setBusinessScopName(vo.getBusinessScopName());
            entity.setTaxClassificationCode(vo.getTaxClassificationCode());
            entity.setTaxClassificationName(vo.getTaxClassificationName());
            if (StringUtil.isNotBlank(vo.getRemark())){
                entity.setRemark(vo.getRemark());
            }
            entity.setAddTime(new Date());
            entity.setAddUser(userName);
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(userName);
            insertList.add(entity);
        }
        resultMap.put("success",insertList);
        resultMap.put("fail",failList);
        return resultMap;
    }

    @Override
    public void batchAddBusinessScopeTaxCode(List<BusinessscopeTaxcodeEntity> list) {
        mapper.batchAddBusinessScopeTaxCode(list);
    }

    @Override
    public List<String> checkByBusiness(String businessScope) {
        if (StringUtil.isBlank(businessScope)) {
            return Lists.newArrayList();
        }
        List<String> list = Arrays.asList(businessScope.split(";"));
        return mapper.checkByBusiness(list);
    }

    @Override
    public List<BusinessScopeTaxCodeVO> getBusinessScopeByScopeName(String scopName) {
        return mapper.getBusinessScopeByScopName(scopName);
    }
}

