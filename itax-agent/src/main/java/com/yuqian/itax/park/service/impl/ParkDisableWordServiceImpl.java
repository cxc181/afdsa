package com.yuqian.itax.park.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.dao.ParkDisableWordMapper;
import com.yuqian.itax.park.entity.ParkDisableWordEntity;
import com.yuqian.itax.park.entity.query.ParkDisableWordQuery;
import com.yuqian.itax.park.entity.vo.ParkDisableWordInsertVO;
import com.yuqian.itax.park.entity.vo.ParkDisableWordVO;
import com.yuqian.itax.park.service.ParkDisableWordService;
import com.yuqian.itax.util.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("parkDisableWordService")
public class ParkDisableWordServiceImpl extends BaseServiceImpl<ParkDisableWordEntity,ParkDisableWordMapper> implements ParkDisableWordService {

    @Override
    public PageInfo<ParkDisableWordVO> queryParkDisableWord(ParkDisableWordQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(this.mapper.queryParkDisableWord(query));
    }

    @Override
    public List<ParkDisableWordVO> listParkDisableWord(ParkDisableWordQuery query) {
        return mapper.queryParkDisableWord(query);
    }

    @Override
    public ParkDisableWordEntity getDisableWordByParkIdAndDisableWord(Long parkId, String disableWord) {
        return mapper.getDisableWordByParkIdAndDisableWord(parkId,disableWord);
    }

    @Override
    public void checkoutDisableWord(Long parkId, String shopName, String shopNameOne, String shopNameTwo) {
        if (null == parkId) {
            throw new BusinessException("园区id不能为空");
        }

        List<String> disableWordList = mapper.queryByShopName(parkId, shopName, shopNameOne, shopNameTwo);
        if (null != disableWordList && !disableWordList.isEmpty()) {
            StringBuffer s = new StringBuffer();
            for (String disableWord : disableWordList) {
                s.append("“").append(disableWord).append("”").append("、");
            }
            throw new BusinessException(s.delete(s.length()-1, s.length()) + "为禁用字号");
        }
    }

    @Override
    public List<ParkDisableWordEntity> getDisableWordByParkId(Long parkId) {
        return mapper.getDisableWordByParkId(parkId);
    }

    @Override
    public Map<String, Object> checkDisableWord(List<ParkDisableWordInsertVO> excelList, Long parkId, String userName) {
        List<ParkDisableWordInsertVO> failList = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();
        for (int i = 0;i<excelList.size();i++){
            //  禁用字段不能为空
            if (StringUtil.isEmpty(excelList.get(i).getDisableWord().trim())){
                excelList.get(i).setFlag(false);
            }
            //  不超过6位验证
            if (excelList.get(i).getDisableWord().length()>6){
                excelList.get(i).setFlag(false);
                excelList.get(i).setReg("禁用字号不能超过6位");
                failList.add(excelList.get(i));
            }
            // 中文验证
            if (excelList.get(i).isFlag()){
                String reg = "[\\u4e00-\\u9fa5]+";
                boolean flag = excelList.get(i).getDisableWord().matches(reg);
                if (!flag){
                    excelList.get(i).setFlag(false);
                    excelList.get(i).setReg("禁用字号必须为中文");
                    failList.add(excelList.get(i));
                }
            }
        }
        // 该园区的禁用字号
        List<ParkDisableWordEntity> dataList = mapper.getDisableWordByParkId(parkId);
        for (int i = 0;i<excelList.size();i++){
            if (!excelList.get(i).isFlag()){
                continue;
            }
            // 去掉列表重复数据
            for (int j = i+1;j<excelList.size();j++){
                if (excelList.get(j).getDisableWord().equals(excelList.get(i).getDisableWord())){
                    excelList.get(j).setFlag(false);
                    excelList.get(j).setReg("列表禁用字号重复");
                    failList.add(excelList.get(j));
                }
            }
            // 去掉与数据库重复数据
            if (CollectionUtil.isNotEmpty(dataList)){
                for (int y=0;y<dataList.size();y++){
                    if (excelList.get(i).getDisableWord().equals(dataList.get(y).getDisableWord())){
                        excelList.get(i).setReg("该禁用字号数据库已存在");
                        excelList.get(i).setFlag(false);
                        failList.add(excelList.get(i));
                    }
                }
            }
        }
        //  插入数据库的数据
        List<ParkDisableWordEntity> insertList = new ArrayList<>();
        for (int i=0;i<excelList.size();i++){
            if (!excelList.get(i).isFlag()){
                continue;
            }
            ParkDisableWordEntity parkDisableWordEntity = new ParkDisableWordEntity();
            parkDisableWordEntity.setParkId(parkId);
            parkDisableWordEntity.setDisableWord(excelList.get(i).getDisableWord());
            parkDisableWordEntity.setAddUser(userName);
            parkDisableWordEntity.setAddTime(new Date());
            insertList.add(parkDisableWordEntity);
        }
        resultMap.put("success",insertList);
        resultMap.put("fail",failList);
        return resultMap;
    }

    @Override
    public void batchAddDisableWord(List<ParkDisableWordEntity> list) {
        mapper.batchAddDisableWord(list);
    }


}

