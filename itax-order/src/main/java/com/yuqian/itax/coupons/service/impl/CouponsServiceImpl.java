package com.yuqian.itax.coupons.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.coupons.dao.CouponsMapper;
import com.yuqian.itax.coupons.entity.CouponExchangeCodeEntity;
import com.yuqian.itax.coupons.entity.CouponsEntity;
import com.yuqian.itax.coupons.entity.CouponsIssueRecordEntity;
import com.yuqian.itax.coupons.entity.enums.CouponExchangeCodeStatusEnum;
import com.yuqian.itax.coupons.entity.enums.CouponsIssueRecordStatusEnum;
import com.yuqian.itax.coupons.entity.enums.CouponsIssueTypeEnum;
import com.yuqian.itax.coupons.entity.enums.CouponsStatusEnum;
import com.yuqian.itax.coupons.entity.po.CouponPO;
import com.yuqian.itax.coupons.entity.query.CouponQuery;
import com.yuqian.itax.coupons.entity.vo.CouponVO;
import com.yuqian.itax.coupons.entity.vo.CouponsBatchIssueVO;
import com.yuqian.itax.coupons.service.CouponExchangeCodeService;
import com.yuqian.itax.coupons.service.CouponsIssueRecordService;
import com.yuqian.itax.coupons.service.CouponsService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service("couponsService")
@Slf4j
public class CouponsServiceImpl extends BaseServiceImpl<CouponsEntity, CouponsMapper> implements CouponsService {
    @Resource
    CouponsIssueRecordService couponsIssueRecordService;
    @Resource
    MemberAccountService memberAccountService;
    @Resource
    CouponExchangeCodeService couponExchangeCodeService;
    @Autowired
    private RedisService redisService;

    @Override
    public PageInfo<CouponVO> queryCouponPageInfo(CouponQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(mapper.queryCouponList(query));
    }

    @Override
    public CouponVO queryCouponsByCode(CouponQuery query) {
        return mapper.queryCouponsByCode(query);
    }

    @Override
    public List<CouponVO> queryCouponList(CouponQuery query) {
        return mapper.queryCouponList(query);
    }

    @Override
    public CouponsEntity add(CouponPO po,String account) throws ParseException {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        Date endDate=sdf.parse(po.getEndDate());
        if(endDate.compareTo(sdf.parse(sdf.format(new Date())))<0){
            throw  new BusinessException("截至日期不能早于当前时间");
        }
        CouponsEntity couponsEntity=new CouponsEntity();
        couponsEntity.setCouponsName(po.getCouponsName());
        SimpleDateFormat sdfC =new SimpleDateFormat("yyyyMMddHHmmss");
        couponsEntity.setCouponsCode("Q"+sdfC.format(new Date()));
        couponsEntity.setOemCode(po.getOemCode());
        couponsEntity.setFaceAmount(po.getFaceAmount().multiply(new BigDecimal(100)).longValue());
        couponsEntity.setUsableRange(po.getUsableRange());
        couponsEntity.setStartDate(sdf.parse(po.getStartDate()));
        couponsEntity.setEndDate(sdf.parse(po.getEndDate()));
        Date startDate=sdf.parse(po.getStartDate());
        if(startDate.compareTo(new Date())>0){
            couponsEntity.setStatus(CouponsStatusEnum.INEFFICIENT.getValue());
        }else{
            couponsEntity.setStatus(CouponsStatusEnum.EFFICIENT.getValue());
        }
        couponsEntity.setDescription(po.getDescription());
        couponsEntity.setAddTime(new Date());
        couponsEntity.setAddUser(account);
        couponsEntity.setRemark("新增");
        mapper.insertSelective(couponsEntity);
        return couponsEntity;
    }

    @Override
    public CouponsEntity update(CouponPO po, String account) throws ParseException {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        Date endDate=sdf.parse(po.getEndDate());
        if(endDate.compareTo(new Date())<0){
            throw  new BusinessException("截至日期不能早于当前时间");
        }
        Integer count = 0;
        //若该券存在发放记录，则不能再编辑
        CouponsIssueRecordEntity couponsIssueRecordEntity= new CouponsIssueRecordEntity();
        couponsIssueRecordEntity.setCouponsId(po.getId());
        List<CouponsIssueRecordEntity> list =couponsIssueRecordService.select(couponsIssueRecordEntity);
        if(CollectionUtil.isNotEmpty(list)){
            count = 1;
//            throw  new BusinessException("存在发放记录不允许编辑");
        }
        //若存在兑换码绑定了该优惠券，则不能再编辑
        CouponExchangeCodeEntity couponExchangeCodeEntity= new CouponExchangeCodeEntity();
        couponExchangeCodeEntity.setCouponsId(po.getId());
        List<CouponExchangeCodeEntity> couponExchangeCodeEntities =couponExchangeCodeService.select(couponExchangeCodeEntity);
        if(CollectionUtil.isNotEmpty(couponExchangeCodeEntities)){
            count = 1;
//            throw  new BusinessException("存在兑换码绑定不允许编辑");
        }
        //更新
        CouponsEntity couponsEntity=mapper.selectByPrimaryKey(po.getId());
        if(count == 0){
            couponsEntity.setOemCode(po.getOemCode());
            couponsEntity.setCouponsName(po.getCouponsName());
            couponsEntity.setFaceAmount(po.getFaceAmount().multiply(new BigDecimal(100)).longValue());
            couponsEntity.setUsableRange(po.getUsableRange());
            couponsEntity.setStartDate(sdf.parse(po.getStartDate()+" 00:00:00"));
            couponsEntity.setEndDate(sdf.parse(po.getEndDate()+" 23:59:59"));
            Date startDate=sdf.parse(po.getStartDate());
            if(startDate.compareTo(new Date())>0){
                couponsEntity.setStatus(CouponsStatusEnum.INEFFICIENT.getValue());
            }else{
                couponsEntity.setStatus(CouponsStatusEnum.EFFICIENT.getValue());
            }
        }
        couponsEntity.setDescription(po.getDescription());
        couponsEntity.setUpdateTime(new Date());
        couponsEntity.setUpdateUser(account);
        mapper.updateByPrimaryKeySelective(couponsEntity);
        return couponsEntity;
    }



    @Override
    public List<CouponsBatchIssueVO> batchIssue(List<CouponsBatchIssueVO> list, List<CouponsBatchIssueVO> failedMsg ,String account) {
        //循环发放
        for (CouponsBatchIssueVO couponsBatchIssueVO:list) {
            CouponsEntity entity=new CouponsEntity();
            entity.setCouponsCode(couponsBatchIssueVO.getCouponsCode());
            CouponsEntity couponsEntity =mapper.selectOne(entity);
            if(couponsEntity==null){
                couponsBatchIssueVO.setFailedMsg("优惠券编码不存在");
                failedMsg.add(couponsBatchIssueVO);
                continue;
            }
            if(couponsEntity.getStatus()!=0 &&couponsEntity.getStatus()!=1){
                couponsBatchIssueVO.setFailedMsg("优惠券状态不对，请确认");
                failedMsg.add(couponsBatchIssueVO);
                continue;
            }
            LocalDate endDate = DateUtil.date2LocalDate(couponsEntity.getEndDate());
            LocalDate now = DateUtil.date2LocalDate(new Date());
            if(endDate.isBefore(now)){
                couponsBatchIssueVO.setFailedMsg("优惠券已过期");
                failedMsg.add(couponsBatchIssueVO);
                continue;
            }
            if(couponsBatchIssueVO.getNumber()>10){
                couponsBatchIssueVO.setFailedMsg("优惠券单次发放不允许超过10张");
                failedMsg.add(couponsBatchIssueVO);
                continue;
            }
            List<MemberAccountEntity> memberAccountEntityList=memberAccountService.queryMemberByPhoneAndOemCode(couponsBatchIssueVO.getMemberAccount(),couponsEntity.getOemCode());
            if(CollectionUtil.isEmpty(memberAccountEntityList)){
                couponsBatchIssueVO.setFailedMsg("发放账号不存在");
                failedMsg.add(couponsBatchIssueVO);
                continue;
            }
            if(couponsBatchIssueVO.getNumber()==null||couponsBatchIssueVO.getNumber()<1){
                couponsBatchIssueVO.setFailedMsg("发放张数未填或者格式不正确");
                failedMsg.add(couponsBatchIssueVO);
                continue;
            }
            try{
                //添加发放记录
                for(int i=0;i<couponsBatchIssueVO.getNumber();i++){
                    CouponsIssueRecordEntity couponsIssueRecordEntity=new CouponsIssueRecordEntity();
                    couponsIssueRecordEntity.setCouponsId(couponsEntity.getId());
                    couponsIssueRecordEntity.setCouponsCode(couponsEntity.getCouponsCode());
                    couponsIssueRecordEntity.setMemberId(memberAccountEntityList.get(0).getId());
                    couponsIssueRecordEntity.setOemCode(couponsEntity.getOemCode());
                    couponsIssueRecordEntity.setIssueTime(new Date());
                    couponsIssueRecordEntity.setOperUser(account);
                    couponsIssueRecordEntity.setStatus(CouponsIssueRecordStatusEnum.UNUSED.getValue());
                    couponsIssueRecordEntity.setAddTime(new Date());
                    couponsIssueRecordEntity.setAddUser(account);
                    couponsIssueRecordEntity.setIssueType(CouponsIssueTypeEnum.BATCH.getValue());
                    couponsIssueRecordEntity.setRemark("发放优惠卷");
                    couponsIssueRecordService.insertSelective(couponsIssueRecordEntity);
                }
              /*  //发通知
                MessageNoticeEntity messageNoticeEntity=new MessageNoticeEntity();
                messageNoticeEntity.setNoticeId(null);
                messageNoticeEntity.setOemCode(couponsEntity.getOemCode());
                messageNoticeEntity.setNoticeType(2);
                messageNoticeEntity.setNoticePosition("1");
                messageNoticeEntity.setOpenMode(1);
                messageNoticeEntity.setBusinessType(10);
                messageNoticeEntity.setNoticeTitle("您有"+couponsBatchIssueVO.getNumber()+"张优惠券已到账");
                messageNoticeEntity.setNoticeSubtitle("您有"+couponsBatchIssueVO.getNumber()+"张优惠券已到账，可前往“我的优惠券”查看");
                messageNoticeEntity.setNoticeContent("您有"+couponsBatchIssueVO.getNumber()+"张优惠券已到账，可前往“我的优惠券”查看");
                messageNoticeEntity.setUserPhones(memberAccountEntityList.get(0).getMemberPhone());
                messageNoticeEntity.setStatus(0);
                messageNoticeEntity.setUserId(memberAccountEntityList.get(0).getId());
                messageNoticeEntity.setUserType(1);
                messageNoticeEntity.setIsAlert(0);
                messageNoticeEntity.setAddTime(new Date());
                messageNoticeEntity.setAddUser(account);
                messageNoticeService.saveMessageNotice(messageNoticeEntity);*/
            }catch (Exception e){
                couponsBatchIssueVO.setFailedMsg("系统错误："+e.getMessage());
                failedMsg.add(couponsBatchIssueVO);
                continue;
            }
        }
        return failedMsg;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exchange(Long currUserId, String oemCode, String exchangeCode) {
        // 校验参数
        if (StringUtil.isBlank(exchangeCode)) {
            throw new BusinessException("兑换码不能为空");
        }
        // 兑换码小写字母转大写
        exchangeCode =  exchangeCode.toUpperCase();

        // 查询用户
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(currUserId)).orElseThrow(() -> new BusinessException("未查询到用户信息"));

        String registRedisTime = (System.currentTimeMillis() + 30000) + "";
        // 防连续误点
        boolean lock = redisService.lock(RedisKey.EXCHANGE_REDIS_KEY + oemCode + "_" + member.getMemberAccount(), registRedisTime, 2);
        if (!lock) {
            throw new BusinessException("操作频繁，请稍后");
        }

        // 查询兑换码信息
        CouponExchangeCodeEntity entity = Optional.ofNullable(couponExchangeCodeService.findByExchangeCode(exchangeCode)).orElseThrow(() -> new BusinessException("兑换码不存在"));

        // 校验兑换码
        Integer status = entity.getStatus();
        Date date = new Date();
        LocalDate localDate = DateUtil.date2LocalDate(date);
        LocalDate endDate = DateUtil.date2LocalDate(entity.getEndDate());
        if (CouponExchangeCodeStatusEnum.INEFFICIENT.getValue().equals(status) || CouponExchangeCodeStatusEnum.OBSOLETE.getValue().equals(status)
                || CouponExchangeCodeStatusEnum.PAUSED.getValue().equals(status) || entity.getLimitedNumber() <= entity.getHasExchangeNumber()) {
            throw new BusinessException("兑换码不存在");
        } else if (CouponExchangeCodeStatusEnum.STALE.getValue().equals(status) || endDate.isBefore(localDate)) {
            throw new BusinessException("兑换码已过期");
        }

        // 查询当前用户已兑换次数
        int number = couponsIssueRecordService.getExchangeNumber(member.getId(), oemCode, entity.getCouponsId(), entity.getId());
        if (number >= entity.getExchangeNumberPerson()) {
            throw new BusinessException("已超过兑换次数");
        }

        // 校验优惠券
        CouponsEntity couponsEntity = Optional.ofNullable(this.findById(entity.getCouponsId())).orElseThrow(() -> new BusinessException("未查询到优惠券信息"));
        if (CouponsStatusEnum.OBSOLETE.getValue().equals(couponsEntity.getStatus()) || CouponsStatusEnum.STALE.getValue().equals(couponsEntity.getStatus())
                || couponsEntity.getEndDate().compareTo(DateUtil.parseDefaultDate(DateUtil.format(date,"yyyy-MM-dd"))) < 0) {
            throw new BusinessException("优惠券不存在");

        }

        // 获取redis锁
        boolean flag = redisService.lock(RedisKey.EXCHANGE_REDIS_KEY + member.getOemCode() + "_" + entity.getId(), registRedisTime, 60);
        if (!flag) {
            throw new BusinessException("系统繁忙，请稍后");
        }

        // 兑换优惠券
        try {
            log.info("开始兑换优惠券");
            // 修改兑换码已兑换张数
            entity.setHasExchangeNumber(entity.getHasExchangeNumber() + 1);
            couponExchangeCodeService.editByIdSelective(entity);
            // 添加发放记录
            CouponsIssueRecordEntity recordEntity = new CouponsIssueRecordEntity();
            recordEntity.setCouponsId(couponsEntity.getId());
            recordEntity.setCouponsExchangeId(entity.getId());
            recordEntity.setCouponsCode(couponsEntity.getCouponsCode());
            recordEntity.setMemberId(member.getId());
            recordEntity.setOemCode(oemCode);
            recordEntity.setIssueType(1);
            recordEntity.setIssueTime(date);
            recordEntity.setOperUser(member.getMemberAccount());
            recordEntity.setStatus(CouponsIssueRecordStatusEnum.UNUSED.getValue());
            recordEntity.setAddTime(date);
            recordEntity.setAddUser(member.getMemberAccount());
            recordEntity.setUpdateTime(date);
            recordEntity.setUpdateUser(member.getMemberAccount());
            recordEntity.setRemark("兑换优惠券");
            couponsIssueRecordService.insertSelective(recordEntity);
        } catch (Exception e) {
            log.error("【兑换失败】" + e.getMessage());
            throw new BusinessException("兑换失败!");
        } finally {
            // 释放redis锁
            redisService.unlock(RedisKey.EXCHANGE_REDIS_KEY + member.getOemCode() + "_" + entity.getId(), registRedisTime);
        }
    }

    @Override
    public List<CouponsEntity> queryOverTimeCouponsEntity() {
        return mapper.queryOverTimeCouponsEntity();
    }

    @Override
    public List<CouponsEntity> queryStartTimeCouponsEntity() {
        return mapper.queryStartTimeCouponsEntity();
    }
}

