package com.yuqian.itax.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.dao.PosterOemRelaMapper;
import com.yuqian.itax.system.dao.PromotionPosterMapper;
import com.yuqian.itax.system.entity.PosterOemRelaEntity;
import com.yuqian.itax.system.entity.PromotionPosterEntity;
import com.yuqian.itax.system.entity.dto.PromotionPosterPO;
import com.yuqian.itax.system.entity.query.PromotionPosterQuery;
import com.yuqian.itax.system.entity.vo.PromotionPosterVO;
import com.yuqian.itax.system.service.PosterOemRelaService;
import com.yuqian.itax.system.service.PromotionPosterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;


@Service("promotionPosterService")
public class PromotionPosterServiceImpl extends BaseServiceImpl<PromotionPosterEntity,PromotionPosterMapper> implements PromotionPosterService {

    @Autowired
    PosterOemRelaService posterOemRelaService;
    @Resource
    PosterOemRelaMapper posterOemRelaMapper;
    @Autowired
    OemService oemService;

    @Override
    public PageInfo<PromotionPosterVO> queryPromotionPosterPage(PromotionPosterQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(this.mapper.queryPromotionPosterList(query));
    }

    @Override
    @Transactional
    public PromotionPosterEntity addPromotionPosterPage(PromotionPosterPO po,String account) {
        //判断序号有没有重复
        PromotionPosterEntity posterEntity=new PromotionPosterEntity();
        posterEntity.setPosterSn(po.getPosterSn());
        List<PromotionPosterEntity> postList=mapper.select(posterEntity);
        if(!CollectionUtils.isEmpty(postList)){
            throw  new BusinessException("该序号已经存在，请更换");
        }
        //新增推广海波
        PromotionPosterEntity promotionPosterEntity= new PromotionPosterEntity();
        promotionPosterEntity.setPosterName(po.getPosterName());
        promotionPosterEntity.setPosterSn(po.getPosterSn());
        promotionPosterEntity.setPosterAddress(po.getPosterAddress());
        promotionPosterEntity.setQrLeftMargin(po.getQrLeftMargin());
        promotionPosterEntity.setQrTopMargin(po.getQrTopMargin());
        promotionPosterEntity.setQrWidth(po.getQrWidth());
        promotionPosterEntity.setQrHeight(po.getQrHeight());
        promotionPosterEntity.setAddTime(new Date());
        promotionPosterEntity.setAddUser(account);
        mapper.insertSelective(promotionPosterEntity);
        //新增推广海报与oem机构的关系表
        List<String> list=po.getOemCodes();
        for(int i=0;i<list.size();i++){
            String oemCode=list.get(i);
            PosterOemRelaEntity posterOemRelaEntity=new PosterOemRelaEntity();
            posterOemRelaEntity.setPosterId(promotionPosterEntity.getId());
            posterOemRelaEntity.setOemCode(oemCode);
            posterOemRelaEntity.setAddTime(new Date());
            posterOemRelaEntity.setAddUser(account);
            posterOemRelaService.insertSelective(posterOemRelaEntity);
        }

        return promotionPosterEntity;
    }

    @Override
    public PromotionPosterEntity updatePromotionPoster(PromotionPosterPO po, String account) {
        //查询
        //新增推广海波
        PromotionPosterEntity promotionPosterEntity= mapper.selectByPrimaryKey(po.getId());
        List<PromotionPosterEntity> postList=mapper.selectPromotionPosterByNotId(po.getPosterSn(),po.getId());
        if(!CollectionUtils.isEmpty(postList)){
            throw  new BusinessException("该序号已经存在，请更换");
        }
        promotionPosterEntity.setPosterName(po.getPosterName());
        promotionPosterEntity.setPosterSn(po.getPosterSn());
        promotionPosterEntity.setPosterAddress(po.getPosterAddress());
        promotionPosterEntity.setQrLeftMargin(po.getQrLeftMargin());
        promotionPosterEntity.setQrTopMargin(po.getQrTopMargin());
        promotionPosterEntity.setQrWidth(po.getQrWidth());
        promotionPosterEntity.setQrHeight(po.getQrHeight());
        promotionPosterEntity.setUpdateTime(new Date());
        promotionPosterEntity.setUpdateUser(account);
        mapper.updateByPrimaryKeySelective(promotionPosterEntity);
        //新增推广海报与oem机构的关系表
        PosterOemRelaEntity posterOem=new PosterOemRelaEntity();
        posterOem.setPosterId(po.getId());
        posterOemRelaMapper.delete(posterOem);

        List<String> list=po.getOemCodes();
        for(int i=0;i<list.size();i++){
            String oemCode=list.get(i);
            PosterOemRelaEntity posterOemRelaEntity=new PosterOemRelaEntity();
            posterOemRelaEntity.setPosterId(promotionPosterEntity.getId());
            posterOemRelaEntity.setOemCode(oemCode);
            posterOemRelaEntity.setAddTime(new Date());
            posterOemRelaEntity.setAddUser(account);
            posterOemRelaService.insertSelective(posterOemRelaEntity);
        }
        return null;
    }

    @Override
    public PromotionPosterVO queryPromotionPosterDetail(Long id) {

        PromotionPosterVO promotionPosterVO=new PromotionPosterVO();

        PosterOemRelaEntity posterOemRelaEntity=new PosterOemRelaEntity();
        posterOemRelaEntity.setPosterId(id);
        List<PosterOemRelaEntity> posterOemRelaEntities=posterOemRelaService.select(posterOemRelaEntity);
        List<Map<String,Object>> list=new ArrayList<>();
        for ( PosterOemRelaEntity posterOem:posterOemRelaEntities) {
            Map<String,Object> map=new HashMap<>();
            map.put("oemName",oemService.getOem(posterOem.getOemCode()).getOemName());
            map.put("oemCode",posterOem.getOemCode());
            list.add(map);
        }
        PromotionPosterEntity promotionPosterEntity= mapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(promotionPosterEntity, promotionPosterVO);
        promotionPosterVO.setOemList(list);

        return promotionPosterVO;
    }

    @Override
    public Integer getMaxPosterSn() {
        return mapper.getMaxPosterSn();
    }

    @Override
    public List<PromotionPosterEntity> getPromotionPosterList(String oemCode) {
        return this.mapper.getPromotionPosterList(oemCode);
    }
}

