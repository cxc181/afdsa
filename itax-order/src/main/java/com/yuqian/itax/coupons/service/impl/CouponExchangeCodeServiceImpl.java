package com.yuqian.itax.coupons.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.coupons.dao.CouponExchangeCodeMapper;
import com.yuqian.itax.coupons.entity.CouponExchangeCodeEntity;
import com.yuqian.itax.coupons.entity.CouponsEntity;
import com.yuqian.itax.coupons.entity.enums.CouponExchangeCodeStatusEnum;
import com.yuqian.itax.coupons.entity.po.CouponExchangeCodePO;
import com.yuqian.itax.coupons.entity.query.CouponExchangeCodeQuery;
import com.yuqian.itax.coupons.entity.vo.CouponExchangeCodeVO;
import com.yuqian.itax.coupons.service.CouponExchangeCodeService;
import com.yuqian.itax.coupons.service.CouponsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


@Service("couponExchangeCodeService")
@Slf4j
public class CouponExchangeCodeServiceImpl extends BaseServiceImpl<CouponExchangeCodeEntity, CouponExchangeCodeMapper> implements CouponExchangeCodeService {

    @Resource
    private CouponsService couponsService;

    @Override
    public PageInfo<CouponExchangeCodeVO> queryCouponExchangeCodePageInfo(CouponExchangeCodeQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(mapper.queryCouponExchangeCodeList(query));
    }

    @Override
    public List<CouponExchangeCodeVO> queryCouponExchangeCodeList(CouponExchangeCodeQuery query) {
        return mapper.queryCouponExchangeCodeList(query);
    }

    @Override
    public CouponExchangeCodeEntity add(CouponExchangeCodePO po, String account) throws ParseException {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        Date  endDate = sdf.parse(po.getEndDate());
        Date  startDate = sdf.parse(po.getStartDate());
        if(endDate.compareTo( sdf.parse( sdf.format(new Date())))<0){
            throw  new BusinessException("截至日期不能早于当前时间");
        }

        CouponsEntity couponsEntity=new CouponsEntity();
        couponsEntity.setCouponsCode(po.getCouponsCode());
        CouponsEntity entity=couponsService.selectOne(couponsEntity);
        if(entity == null){
            throw  new BusinessException("输入优惠券编码不正确，请重新输入");
        }
        if(entity.getStatus()==2||entity.getStatus()==3){
            throw  new BusinessException("优惠卷已过期/已作废");
        }
        if(entity.getStartDate().compareTo(sdf.parse(po.getStartDate()))>0||entity.getEndDate().compareTo(sdf.parse(po.getEndDate()))<0){
            throw  new BusinessException("兑换码有效期需在优惠券有效内！");
        }
        if(startDate.compareTo(entity.getStartDate())<0){
            throw  new BusinessException("生效日期不能早于优惠券生效时间");
        }
        if(po.getExchangeNumberPerson()>po.getLimitedNumber()){
            throw  new BusinessException("每人可兑换张数不可以大于限量兑换总张数");
        }
        CouponExchangeCodeEntity couponExchangeCodeEntity= new CouponExchangeCodeEntity();
        String exchangeCode="";
        CouponExchangeCodeEntity q = null;
        List<CouponExchangeCodeEntity> list =null;
        while (true){
            exchangeCode= getStringRandom(6);
            q=new CouponExchangeCodeEntity();
            q.setExchangeCode(exchangeCode);
            list =mapper.select(q);
            if(CollectionUtil.isEmpty(list)){
                break;
            }
        }
        couponExchangeCodeEntity.setExchangeCode(exchangeCode);
        couponExchangeCodeEntity.setExchangeName(po.getExchangeName());
        couponExchangeCodeEntity.setLimitedNumber(po.getLimitedNumber());
        couponExchangeCodeEntity.setExchangeNumberPerson(po.getExchangeNumberPerson());
        couponExchangeCodeEntity.setHasExchangeNumber(0);
        if(startDate.compareTo(new Date())>0){
            couponExchangeCodeEntity.setStatus(CouponExchangeCodeStatusEnum.INEFFICIENT.getValue());
        }else{
            couponExchangeCodeEntity.setStatus(CouponExchangeCodeStatusEnum.EFFICIENT.getValue());
        }
        couponExchangeCodeEntity.setCouponsId(entity.getId());
        couponExchangeCodeEntity.setStartDate(sdf.parse(po.getStartDate()));
        couponExchangeCodeEntity.setEndDate(sdf.parse(po.getEndDate()));
        couponExchangeCodeEntity.setAddTime(new Date());
        couponExchangeCodeEntity.setAddUser(account);
        couponExchangeCodeEntity.setRemark(po.getRemark());
        mapper.insertSelective(couponExchangeCodeEntity);
        return couponExchangeCodeEntity;
    }

    @Override
    public CouponExchangeCodeEntity update(CouponExchangeCodePO po, String account) throws ParseException {
        CouponExchangeCodeEntity couponExchangeCodeEntity=mapper.selectByPrimaryKey(po.getId());
        if(couponExchangeCodeEntity==null){
            throw new BusinessException("兑换码不存在");
        }
        //校验限量兑换张数不能小于当前已兑张数
        if(po.getLimitedNumber()<couponExchangeCodeEntity.getHasExchangeNumber()){
            throw new BusinessException("量兑换张数不能小于当前已兑张数");
        }
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");

        CouponsEntity entity=couponsService.findById(couponExchangeCodeEntity.getCouponsId());
        if(entity == null){
            throw  new BusinessException("未找到优惠卷信息");
        }
        if(entity.getStartDate().compareTo(sdf.parse(po.getStartDate()))>0||entity.getEndDate().compareTo(sdf.parse(po.getEndDate()))<0){
            throw  new BusinessException("兑换码有效期需在优惠券有效内！");
        }
        if(entity.getStatus()==2||entity.getStatus()==3){
            throw  new BusinessException("优惠卷已过期/已作废");
        }
        couponExchangeCodeEntity.setStartDate(sdf.parse(po.getStartDate()));
        couponExchangeCodeEntity.setEndDate(sdf.parse(po.getEndDate()));
        couponExchangeCodeEntity.setLimitedNumber(po.getLimitedNumber());
        couponExchangeCodeEntity.setExchangeNumberPerson(po.getExchangeNumberPerson());
        couponExchangeCodeEntity.setUpdateTime(new Date());
        couponExchangeCodeEntity.setUpdateUser(account);
        couponExchangeCodeEntity.setRemark(po.getRemark());
        mapper.updateByPrimaryKeySelective(couponExchangeCodeEntity);
        return couponExchangeCodeEntity;
    }

    @Override
    public List<CouponExchangeCodeEntity> queryOverTimeCouponExchangeCodeEntity() {
        return mapper.queryOverTimeCouponExchangeCodeEntity();
    }

    @Override
    public List<CouponExchangeCodeEntity> queryStartTimeCouponExchangeCodeEntity() {
        return mapper.queryStartTimeCouponExchangeCodeEntity();
    }

    @Override
    public List<CouponExchangeCodeEntity> queryCouponExchangeCodeByCouponsId(Long couponsId) {
        return mapper.queryCouponExchangeCodeByCouponsId(couponsId);
    }

    //生成随机数字和字母,
    public  String getStringRandom(int length) {
        long startTime = System.currentTimeMillis();
        String val = "";
        Random random = new SecureRandom();

        //参数length，表示生成几位随机数
        String charOrNum = "char";
        for(int i = 0; i < length; i++) {
            charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                val += (char)(random.nextInt(26) + 65);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        long endTime = System.currentTimeMillis();
        log.debug("结果："+val+",耗时："+(endTime-startTime));
        return val;
    }



    @Override
    public CouponExchangeCodeEntity findByExchangeCode(String exchangeCode) {
        return mapper.queryByExchangeCode(exchangeCode);
    }
}

