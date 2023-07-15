package com.yuqian.itax.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeBatchVO;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeVO;
import com.yuqian.itax.park.entity.vo.ParkBusinessScopeWithTaxCodeVO;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.system.dao.BusinessscopeTaxcodeMapper;
import com.yuqian.itax.system.dao.ParkBusinessscopeMapper;
import com.yuqian.itax.system.entity.BusinessscopeTaxcodeEntity;
import com.yuqian.itax.system.entity.ParkBusinessscopeEntity;
import com.yuqian.itax.system.entity.ParkBusinessscopeTaxcodeRelaEntity;
import com.yuqian.itax.system.entity.query.ParkBusinessScopeQuery;
import com.yuqian.itax.system.entity.vo.BusinessScopeTaxCodeVO;
import com.yuqian.itax.system.service.BusinessScopeService;
import com.yuqian.itax.system.service.BusinessscopeTaxcodeService;
import com.yuqian.itax.system.service.ParkBusinessscopeService;
import com.yuqian.itax.system.service.ParkBusinessscopeTaxcodeRelaService;
import com.yuqian.itax.util.util.StringUtil;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;


@Service("parkBusinessscopeService")
public class ParkBusinessscopeServiceImpl extends BaseServiceImpl<ParkBusinessscopeEntity,ParkBusinessscopeMapper> implements ParkBusinessscopeService {

    @Autowired
    private ParkBusinessscopeService parkBusinessscopeService;
    @Autowired
    private BusinessscopeTaxcodeMapper businessscopeTaxcodeMapper;
    @Autowired
    private ParkBusinessscopeTaxcodeRelaService parkBusinessscopeTaxcodeRelaService;
    @Autowired
    private BusinessscopeTaxcodeService businessscopeTaxcodeService;
    @Autowired
    private BusinessScopeService businessScopeService;
    @Override
    public PageResultVo<String> findByParkId(ParkBusinessScopeQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<String> list =  mapper.queryByParkId(query);
        return PageResultVo.restPage(list);
    }

    @Override
    public PageInfo<ParkBusinessScopeVO> queryByfindByParkIdAndbusinessScopeName(ParkBusinessScopeQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(mapper.queryByFindByParkIdAndBusinessScopeName(query));
    }

    @Override
    public List<ParkBusinessScopeVO> parkBusinessScopeExport(ParkBusinessScopeQuery query) {
        return mapper.queryByFindByParkIdAndBusinessScopeName(query);
    }

    @Override
    public ParkBusinessscopeEntity getParkBusinessscopeByParkIdAndName(Long parkId, String scopeName) {
        return mapper.getParkBusinessscopeByParkIdAndName(parkId,scopeName);
    }

    @Override
    @Transactional
    public Map<String, Object> checkParkBusinessScope(List<ParkBusinessScopeBatchVO> list, Long parkId,String userName) {
        List<ParkBusinessScopeBatchVO> failList = Lists.newArrayList();
        Map<String,Object> resultMap = new HashMap<>();
        //  去除无效行
        for (int i = 0;i<list.size();i++){
            if(StringUtil.isEmpty(list.get(i).getBusinessScopeName())){
                list.get(i).setFalg(false);
                list.get(i).setMsg("经营范围不能为空");
                failList.add( list.get(i));
            }else if (list.get(i).getBusinessScopeName().length()>100){
                list.get(i).setFalg(false);
                list.get(i).setMsg("经营范围长度不能超过100个字符");
                failList.add( list.get(i));
            }
        }
        List<String> scopeName = new ArrayList<>();
        // 去除表中数据重复
        for (int i=0;i<list.size();i++){
            if (!list.get(i).isFalg()){
                continue;
            }
            for (int j=i+1;j<list.size();j++){
                if (list.get(i).getBusinessScopeName().equals(list.get(j).getBusinessScopeName())){
                    list.get(j).setFalg(false);
                    list.get(j).setMsg("表中数据重复");
                    failList.add( list.get(j));
                }
            }
            scopeName.add(list.get(i).getBusinessScopeName());
        }
        // 去除数据库重复数据
        if (CollectionUtil.isNotEmpty(scopeName)){
            List<ParkBusinessscopeEntity> entityList = mapper.getParkBusinessscopeByScopeName(scopeName,parkId);
            if (CollectionUtil.isNotEmpty(entityList)){
                scopeName = new ArrayList<>();
                for (int i=0;i<list.size();i++){
                    if (!list.get(i).isFalg()){
                        continue;
                    }
                    for (ParkBusinessscopeEntity entity:entityList){
                        if (entity.getBusinessscopeName().equals(list.get(i).getBusinessScopeName())){
                            list.get(i).setFalg(false);
                            list.get(i).setMsg("数据库已有该数据");
                            failList.add( list.get(i));
                        }
                    }
                    if (list.get(i).isFalg()){
                        scopeName.add(list.get(i).getBusinessScopeName());
                    }
                }
            }
        }
        //校验数据是否在经营范围基础库存在
        if (CollectionUtil.isNotEmpty(scopeName)){
            List<BusinessscopeTaxcodeEntity> baseScopeList = businessscopeTaxcodeMapper.getBusinessScopeByConcatName(scopeName);
            if (CollectionUtil.isNotEmpty(baseScopeList)){
                for (int i=0;i<list.size();i++){
                    if (!list.get(i).isFalg()){
                        continue;
                    }
                    boolean flag = false;
                    List<Long> baseIdList = new ArrayList<>();
                    for (BusinessscopeTaxcodeEntity entity:baseScopeList) {
                        //  经营范围存在设置为true
                       if (entity.getBusinessScopName().equals(list.get(i).getBusinessScopeName())){
                           flag = true;
                           baseIdList.add(entity.getId());
                       }
                        list.get(i).setBusinessscopeBaseId(baseIdList);
                   }
                   if (!flag){
                       list.get(i).setFalg(false);
                       list.get(i).setMsg("经营范围基础库不存在该经营范围");
                       failList.add( list.get(i));
                   }
                   list.get(i).setFalg(flag);
                }
            }else{
                // 经营范围基础库不存在表中数据
                for (int i=0;i<list.size();i++) {
                    if (!list.get(i).isFalg()) {
                        continue;
                    }else{
                        list.get(i).setFalg(false);
                        list.get(i).setMsg("经营范围基础库不存在该经营范围");
                        failList.add( list.get(i));
                    }
                }
            }
        }
        // 插入数据库的数据
        Date addTime = new Date();
        List<ParkBusinessscopeEntity> insertList = new ArrayList<>();
        for (ParkBusinessScopeBatchVO vo:list){
            if (!vo.isFalg()){
                continue;
            }
            ParkBusinessscopeEntity entity = new  ParkBusinessscopeEntity();
            entity.setParkId(parkId);
            entity.setBusinessscopeName(vo.getBusinessScopeName());
            entity.setAddTime(addTime);
            entity.setAddUser(userName);
            entity.setIsDelete(1);
            entity.setRemark("批量导入");
            insertList.add(entity);
            mapper.insert(entity);
            if (CollectionUtil.isNotEmpty(vo.getBusinessscopeBaseId())){
                for (Long baseId:vo.getBusinessscopeBaseId()){
                    ParkBusinessscopeTaxcodeRelaEntity relaEntity = new ParkBusinessscopeTaxcodeRelaEntity();
                    relaEntity.setBusinessscopeBaseId(baseId);
                    relaEntity.setParkBusinessscopeId(entity.getId());
                    relaEntity.setAddTime(addTime);
                    relaEntity.setAddUser(userName);
                    parkBusinessscopeTaxcodeRelaService.insertSelective(relaEntity);
                }
            }

        }
        resultMap.put("success",insertList);
        resultMap.put("fail",failList);
        return resultMap;
    }

    @Override
    public List<ParkBusinessScopeWithTaxCodeVO> queryByTaxCode(Set<String> taxCodeSet, Long parkId) {
        return mapper.queryByTaxCode(taxCodeSet, parkId);
    }

    @Override
    public Void addParkBusinessScope(ParkBusinessscopeEntity entity) {
        if (entity == null ){
            return null;
        }
        List<BusinessScopeTaxCodeVO> list = businessscopeTaxcodeService.getBusinessScopeByScopeName(entity.getBusinessscopeName());
        if (CollectionUtils.isEmpty(list)){
          throw new BusinessException("经营范围在基础库不存在");
        }
        mapper.insert(entity);
        for(BusinessScopeTaxCodeVO vo:list){
            ParkBusinessscopeTaxcodeRelaEntity relaEntity = new ParkBusinessscopeTaxcodeRelaEntity();
            relaEntity.setBusinessscopeBaseId(vo.getId());
            relaEntity.setParkBusinessscopeId(entity.getId());
            relaEntity.setAddTime(new Date());
            relaEntity.setAddUser(entity.getAddUser());
            parkBusinessscopeTaxcodeRelaService.insertSelective(relaEntity);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteParkBusinessScope(ParkBusinessscopeEntity entity,String userName) {
        //  0为删除
        entity.setIsDelete(0);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(userName);
        parkBusinessscopeService.editByIdSelective(entity);
        businessScopeService.deleteScopeBusinessByContentAndParkId(entity.getParkId(),entity.getBusinessscopeName());
    }
}

