package com.yuqian.itax.product.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.product.dao.*;
import com.yuqian.itax.product.entity.DiscountActivityChargeStandardRelaEntity;
import com.yuqian.itax.product.entity.ProductDiscountActivityEntity;
import com.yuqian.itax.product.entity.dto.ProductDiscountActivityAPIDTO;
import com.yuqian.itax.product.entity.query.ProductDiscountActivityQuery;
import com.yuqian.itax.product.entity.vo.*;
import com.yuqian.itax.product.service.ProductDiscountActivityService;
import com.yuqian.itax.util.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


@Service("productDiscountActivityService")
public class ProductDiscountActivityServiceImpl extends BaseServiceImpl<ProductDiscountActivityEntity,ProductDiscountActivityMapper> implements ProductDiscountActivityService {

    @Autowired
    private DiscountActivityCrowdLabelRelaMapper discountActivityCrowdLabelRelaMapper;
    @Autowired
    private DiscountActivityParkRelaMapper discountActivityParkRelaMapperd;
    @Autowired
    private DiscountActivityIndustryRelaMapper discountActivityIndustryRelaMapper;
    @Autowired
    private DiscountActivityChargeStandardRelaMapper discountActivityChargeStandardRelaMapper;
    /**
     * 根据产品类型获取产品特价活动
     * @param productDiscountActivityAPIDTO
     * @return
     */
    public ProductDiscountActivityVO getProductDiscountActivityByProductType(ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO){
        //非开票的类型
        if(productDiscountActivityAPIDTO.getProductType() != null
                && !ObjectUtil.equal(productDiscountActivityAPIDTO.getProductType(),5)){
            ProductDiscountActivityVO vo = mapper.getProductDiscountActivityByProductType(productDiscountActivityAPIDTO);
            if(vo != null && vo.getSpecialPriceAmount()!= null && vo.getConsumptionDiscount() != null){
                Long payAmount =  (new BigDecimal(vo.getSpecialPriceAmount()))
                        .multiply(((new BigDecimal(100)).subtract(vo.getConsumptionDiscount())).divide(new BigDecimal(100)))
                        .setScale(0, BigDecimal.ROUND_UP).longValue();
                vo.setPayAmount(payAmount);
            } else if (null != vo && null != vo.getSpecialPriceAmount()){
                vo.setPayAmount(vo.getSpecialPriceAmount());
            }
            return vo;
        }
        return null;
    }

    @Override
    public PageInfo<ProductDiscountActivityListVO> listPageProductDiscountActivity(ProductDiscountActivityQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(mapper.listPageProductDiscountActivity(query));
    }

    @Override
    public List<ProductDiscountActivityListVO> listProductDiscountActivity(ProductDiscountActivityQuery query) {
        return mapper.listPageProductDiscountActivity(query);
    }

    @Override
    public void updateStatusById(Long id, Integer status,String account) {
        ProductDiscountActivityEntity productDiscountActivityEntity=mapper.selectByPrimaryKey(id);
        if(productDiscountActivityEntity==null){
            throw  new BusinessException("产品特价活动不存在");

        }
        if(status==1){
            if(productDiscountActivityEntity.getStatus()!=0&&productDiscountActivityEntity.getStatus()!=3){
                throw  new BusinessException("只有已暂停和待上架状态才可以上架");
            }
        }
        if(status==2){
            if(productDiscountActivityEntity.getStatus()==2){
                throw  new BusinessException("活动已下架");
            }
        }
        if(status==3){
            if(productDiscountActivityEntity.getStatus()==2){
                throw  new BusinessException("活动已下架");
            }
            if(productDiscountActivityEntity.getStatus()==0){
                throw  new BusinessException("活动还没有上架，不能暂停");
            }
        }
        productDiscountActivityEntity.setStatus(status);
        productDiscountActivityEntity.setUpdateTime(new Date());
        productDiscountActivityEntity.setUpdateUser(account);
        mapper.updateByPrimaryKeySelective(productDiscountActivityEntity);
    }

    @Override
    public  List<ProductDiscountActivityOnCrowdLabelVO> getCrowdLabelAndParkByProductType(String oemCode, Integer productType) {
        return mapper.getCrowdLabelAndParkByProductType(oemCode,productType);
    }

    @Override
    @Transactional
    public void addProductDiscountActivity(ProductCrowdParkVO productCrowdParkVO,String addUser) {
        ProductDiscountActivityEntity productDiscountActivityEntity= new ProductDiscountActivityEntity();
        if  (productCrowdParkVO.getActivityId() != null ){
            //  编辑先删除
            productDiscountActivityEntity =  mapper.selectByPrimaryKey(productCrowdParkVO.getActivityId());
            discountActivityChargeStandardRelaMapper.deleteBydiscountActivityId(productCrowdParkVO.getActivityId());
            discountActivityIndustryRelaMapper.deleteBydiscountActivityId(productCrowdParkVO.getActivityId());
            discountActivityParkRelaMapperd.deleteBydiscountActivityId(productCrowdParkVO.getActivityId());
            discountActivityCrowdLabelRelaMapper.deleteBydiscountActivityId(productCrowdParkVO.getActivityId());
        }
        //  做新增判断
        ProductDiscountActivityListVO productVo = queryProductDiscountActivityByactivityNameAndType(productCrowdParkVO.getActivityName().trim(),productCrowdParkVO.getProductType(),productCrowdParkVO.getActivityId());
        if (productVo != null ){
            throw new BusinessException("该产品下已有该活动");
        }
        // 查询出同产品 同oem机构下所有活动的人群标签
        List<ProductDiscountActivityOnCrowdLabelVO> list = getCrowdLabelAndParkByProductType(productCrowdParkVO.getOemCode(),productCrowdParkVO.getProductType());
        for (Long crowdLabelId :productCrowdParkVO.getCrowdLabelIds()){
            for (ProductDiscountActivityOnCrowdLabelVO VO:list){
                if (Objects.equals(crowdLabelId,VO.getCrowdLabelId())){
                    throw new BusinessException(VO.getCrowdLabelName()+"人群已配置相同的产品特价活动");
                }
            }
        }
        //  保存产品特价活动表
        productDiscountActivityEntity.setActivityName(productCrowdParkVO.getActivityName());
        productDiscountActivityEntity.setProductType(productCrowdParkVO.getProductType());
        if (productCrowdParkVO.getSpecialPriceAmount() != null){
            productDiscountActivityEntity.setSpecialPriceAmount(productCrowdParkVO.getSpecialPriceAmount());
        }
        if (productDiscountActivityEntity.getStatus() == null){
            productDiscountActivityEntity.setStatus(0);
        }
        productDiscountActivityEntity.setOemCode(productCrowdParkVO.getOemCode());
        productDiscountActivityEntity.setActivityStartDate(productCrowdParkVO.getActivityStartDate());
        productDiscountActivityEntity.setActivityEndDate(productCrowdParkVO.getActivityEndDate());
        if (productCrowdParkVO.getProductType() == 15 && productCrowdParkVO.getProcessingFee() != null){
            productDiscountActivityEntity.setProcessingFee(productCrowdParkVO.getProcessingFee());
        }
        if (productCrowdParkVO.getCancelTotalLimit() != null){
            productDiscountActivityEntity.setCancelTotalLimit(productCrowdParkVO.getCancelTotalLimit());
        }
        productDiscountActivityEntity.setAddTime(new Date());
        //  编辑操作保存修改人与新增人
        if (productCrowdParkVO.getActivityId() != null){
            productDiscountActivityEntity.setUpdateUser(addUser);
            productDiscountActivityEntity.setUpdateTime(new Date());
            mapper.updateByPrimaryKey(productDiscountActivityEntity);
        }else{
            productDiscountActivityEntity.setAddTime(new Date());
            productDiscountActivityEntity.setAddUser(addUser);
            mapper.insertSelective(productDiscountActivityEntity);
        }

        ProductDiscountActivityListVO vo =  mapper.queryProductDiscountActivityByactivityNameAndType(productDiscountActivityEntity.getActivityName(),productDiscountActivityEntity.getProductType(),null);
        //产品特价活动人群关系表
        discountActivityCrowdLabelRelaMapper.batchAdd(productCrowdParkVO.getCrowdLabelIds(),vo.getId(),productDiscountActivityEntity.getOemCode(),new Date(),addUser);
        //  产品特价活动园区关系表
        discountActivityParkRelaMapperd.batchAddPark(productCrowdParkVO.getParkIds(),vo.getId(),productDiscountActivityEntity.getOemCode(),new Date(),addUser);
        //产品特价活动行业关系表
        if (productCrowdParkVO.getIndustryIds() != null && productCrowdParkVO.getIndustryIds().size()>0){
            discountActivityIndustryRelaMapper.batchAddIndusty(productCrowdParkVO.getIndustryIds(),vo.getId(),productDiscountActivityEntity.getOemCode(),new Date(),addUser);

        }
        if (productCrowdParkVO.getProductType() == 5 && productCrowdParkVO.getChargeStandard() !=null && productCrowdParkVO.getChargeStandard().size() != 0){
            List<DiscountActivityChangeVO> charge = new ArrayList<DiscountActivityChangeVO>();
            for (DiscountActivityChangeVO changeVO:productCrowdParkVO.getChargeStandard()){
                // 最小值必须小于Long 的最大值/1000
                if (changeVO.getChargeMin() <Long.MAX_VALUE/1000){
                    // 最大值为空，则设置为Long的最大值
                    if (changeVO.getChargeMax() == null){
                        BigDecimal chargeMax = new BigDecimal(Long.MAX_VALUE/100);
                        changeVO.setChargeMax(chargeMax.multiply(new BigDecimal(100)).longValue());
                        changeVO.setChargeMin(changeVO.getChargeMin()*100);
                        //  最大值比Long的最大值还大 也设置为Long的最大值
                    }else if (changeVO.getChargeMax()*100>Long.MAX_VALUE/100){
                        BigDecimal chargeMax = new BigDecimal(Long.MAX_VALUE/100);
                        changeVO.setChargeMax(chargeMax.multiply(new BigDecimal(100)).longValue());
                        changeVO.setChargeMin(changeVO.getChargeMin()*100);
                    }else{
                        changeVO.setChargeMin(changeVO.getChargeMin()*100);
                        changeVO.setChargeMax(changeVO.getChargeMax()*100);
                    }
                    charge.add(changeVO);
                }else{
                    throw new BusinessException("开票服务费的最小值太大了，请核对后重新输入");
                }

            }
            discountActivityChargeStandardRelaMapper.addChargeStandard(charge,productCrowdParkVO.getOemCode(),vo.getId(),1,new Date(),addUser);
        }

    }

    @Override
    public ProductActivityDetailVO getDetail(Long discountActivityId) {
        ProductActivityDetailVO vo = mapper.getDetail(discountActivityId);
        if (StringUtil.isNotBlank(vo.getChargeStandard())){
            String id[] = vo.getChargeStandard().split(",");
            List<String>  idList = new ArrayList<String>();
            Collections.addAll(idList, id);
            List<DiscountActivityChargeStandardRelaEntity> list = discountActivityChargeStandardRelaMapper.queryChargeStandarById(idList);
            if (list != null){
                vo.setCharge(list);
            }
        }
        return vo;
    }

    @Override
    public ProductDiscountActivityEntity queryByCrowdLabel(Long crowdLabelId, Integer productType) {
        return mapper.queryByCrowdLabel(crowdLabelId, productType);
    }

    @Override
    public ProductDiscountActivityEntity getByAccessPartyCode(String accessPartyCode, Integer productType, Long parkId) {
        return mapper.queryByAccessPartyCode(accessPartyCode, productType, parkId);
    }

    @Override
    public ProductDiscountActivityListVO queryProductDiscountActivityByactivityNameAndType(String activityName, Integer productType,Long activityId) {
        return mapper.queryProductDiscountActivityByactivityNameAndType(activityName,productType,activityId);
    }

}

