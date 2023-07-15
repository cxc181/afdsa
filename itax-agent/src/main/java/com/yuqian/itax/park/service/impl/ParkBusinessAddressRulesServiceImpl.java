package com.yuqian.itax.park.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.StringUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.park.dao.ParkBusinessAddressRulesMapper;
import com.yuqian.itax.park.entity.ParkBusinessAddressRulesEntity;
import com.yuqian.itax.park.entity.ParkBusinessAddressVO;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.service.ParkBusinessAddressRulesService;
import com.yuqian.itax.park.service.ParkService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;


@Service("parkBusinessAddressRulesService")
public class ParkBusinessAddressRulesServiceImpl extends BaseServiceImpl<ParkBusinessAddressRulesEntity,ParkBusinessAddressRulesMapper> implements ParkBusinessAddressRulesService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ParkService parkService;

    /**
     * 根据园区生成经营地址
     * @param parkId
     * @return
     */
    @Override
    public String builderBusinessAddressByPark(Long parkId){
        String redisTime = (System.currentTimeMillis() + 3000) + "";
        boolean lockResult = redisService.lock(RedisKey.LOCK_BUSINESS_ADDRESS_BY_PARK + parkId, redisTime, 60);
        if(!lockResult){
            throw new BusinessException("请勿重复提交！");
        }
        try {
            //判断园区是否存在且状态为正常
            ParkEntity parkEntity = parkService.findById(parkId);
            if (null == parkEntity || 1 != parkEntity.getStatus()){
                throw new BusinessException("园区不存在或已下架！");
            }
            ParkBusinessAddressRulesEntity parkBusinessAddressRulesEntity = new ParkBusinessAddressRulesEntity();
            parkBusinessAddressRulesEntity.setParkId(parkId);
            List<ParkBusinessAddressRulesEntity> list =  mapper.select(parkBusinessAddressRulesEntity);
            if(null == list || list.size()!= 1){
                throw new BusinessException("园区经营地址规则配置错误！");
            }

            parkBusinessAddressRulesEntity = list.get(0);
            return builderBusinessAddress(parkBusinessAddressRulesEntity);
        }catch (Exception e){
            throw new BusinessException(e.getMessage());
        }finally {
            redisService.unlock(RedisKey.LOCK_BUSINESS_ADDRESS_BY_PARK + parkId, redisTime);
        }
    }

    @Override
    public ParkBusinessAddressVO queryByParkId(Long parkId) {
        return mapper.queryByParkId(parkId);
    }

    @Override
    public ParkBusinessAddressRulesEntity checkAddress(ParkBusinessAddressVO parkBusinessAddressVO) {
        ParkBusinessAddressRulesEntity entity = new ParkBusinessAddressRulesEntity();
        entity.setParkId(parkBusinessAddressVO.getParkId());
        // 按房间号自动递增
        if (parkBusinessAddressVO.getAddressType() == 2){
            if (parkBusinessAddressVO.getRegistPrefix().length()>100){
                throw new BusinessException("地址前缀不能超过100个字符");
            }
            //  楼栋格式验证
            if (StringUtil.isNotEmpty(parkBusinessAddressVO.getRegistAreaMin()) && StringUtil.isNotEmpty(parkBusinessAddressVO.getRegistAreaMax())){
                boolean isupper = allUpperCase(parkBusinessAddressVO.getRegistAreaMin());
                if (isupper){
                    if (parkBusinessAddressVO.getRegistAreaMin().length() == 1 && parkBusinessAddressVO.getRegistAreaMax().length() == 1){
                        if (!StrUtil.isUpperCase(parkBusinessAddressVO.getRegistAreaMax())){
                            throw new BusinessException("楼栋前后必须保持一致，如都为数字或则都为大写字母");
                        }else{
                            byte[] min = parkBusinessAddressVO.getRegistAreaMin().getBytes(StandardCharsets.UTF_8);
                            byte[] max = parkBusinessAddressVO.getRegistAreaMax().getBytes(StandardCharsets.UTF_8);
                            int i = (int) min[0];
                            int j = (int) max[0];
                            if (j < i){
                                throw new BusinessException("楼栋最大值不能小于最小值");
                            }
                        }
                    }else{
                        throw new BusinessException("大写字母只能为一位！");
                    }
                }else if(StringUtils.isNumeric(parkBusinessAddressVO.getRegistAreaMin())){
                    if (!StringUtils.isNumeric(parkBusinessAddressVO.getRegistAreaMax())){
                        throw new BusinessException("楼栋前后必须保持一致，如都为数字或则都为大写字母");
                    }else{
                        int min = Integer.parseInt(parkBusinessAddressVO.getRegistAreaMin());
                        int max = Integer.parseInt(parkBusinessAddressVO.getRegistAreaMax());
                        if (min < 0 || max > 1000){
                            throw new BusinessException("楼栋区间不正确，数字只能输入0~1000区间的数字");
                        }
                        if (max < min ){
                            throw new BusinessException("楼栋最大值不能小于最小值");
                        }
                    }
                }else{
                    throw new BusinessException("楼栋必须为大写字母或数字，只能包含一种");
                }
                if (parkBusinessAddressVO.getRegistPostfix() != null && parkBusinessAddressVO.getRegistPostfix().length()> 20){
                    throw new BusinessException("地址后缀不能超过20个字符");
                }else if(parkBusinessAddressVO.getRegistPostfix() != null){
                    entity.setRegistPostfix(parkBusinessAddressVO.getRegistPostfix());
                }
                entity.setRegistAreaMin(parkBusinessAddressVO.getRegistAreaMin());
                entity.setRegistAreaMax(parkBusinessAddressVO.getRegistAreaMax());
                entity.setCurrentRegistArea(parkBusinessAddressVO.getRegistAreaMin());
            }else if ((StringUtil.isEmpty(parkBusinessAddressVO.getRegistAreaMin()) && StringUtil.isNotEmpty(parkBusinessAddressVO.getRegistAreaMax())) || (StringUtil.isNotEmpty(parkBusinessAddressVO.getRegistAreaMin()) && StringUtil.isEmpty(parkBusinessAddressVO.getRegistAreaMax()))){
                throw new BusinessException("请填写完整的楼栋区间");
            }else{
                if (parkBusinessAddressVO.getRegistPostfix() != null && parkBusinessAddressVO.getRegistPostfix().length()> 20){
                    throw new BusinessException("地址后缀不能超过20个字符");
                }else if(parkBusinessAddressVO.getRegistPostfix() != null){
                    entity.setRegistPostfix(parkBusinessAddressVO.getRegistPostfix());
                }
                entity.setRegistAreaMin("");
                entity.setRegistAreaMax("");
            }
            //  房间号验证
            if (StringUtils.isNumeric(parkBusinessAddressVO.getAreaRegistNumMin()) && StringUtils.isNumeric(parkBusinessAddressVO.getAreaRegistNumMax())){
                if (parkBusinessAddressVO.getAreaRegistNumMin().length() != parkBusinessAddressVO.getAreaRegistNumMax().length()){
                    throw new BusinessException("房间号位数必须一致");
                }
                int min = Integer.parseInt(parkBusinessAddressVO.getAreaRegistNumMin());
                int max = Integer.parseInt(parkBusinessAddressVO.getAreaRegistNumMax());
                if (min < 0 || max > 9999){
                    throw new BusinessException("房间号区间不正确");
                }
                if (max < min ){
                    throw new BusinessException("最大值不能小于最小值");
                }
            }else{
                throw new BusinessException("房间号只能为数字");
            }
            entity.setRegistPrefix(parkBusinessAddressVO.getRegistPrefix());
            if (parkBusinessAddressVO.getRegistUnit() != null){
                entity.setRegistUnit(parkBusinessAddressVO.getRegistUnit());
            }
            entity.setAddressType(2);
            entity.setAreaRegistNumMin(Integer.parseInt(parkBusinessAddressVO.getAreaRegistNumMin()));
            entity.setAreaRegistNumMax(Integer.parseInt(parkBusinessAddressVO.getAreaRegistNumMax()));
            entity.setCurrentRegistNum(Integer.parseInt(parkBusinessAddressVO.getAreaRegistNumMin()));
            entity.setAreaRegistNumLen(parkBusinessAddressVO.getAreaRegistNumMax().length());
            //固定地址
        }else{
            if (parkBusinessAddressVO.getRegistPrefix() != null){
                if(parkBusinessAddressVO.getRegistPrefix().length()>100){
                    throw new BusinessException("经营地址不能超过100个字符");
                }
            }else{
                throw new BusinessException("经营地址不能为空");
            }
            entity.setAddressType(1);
            entity.setRegistPrefix(parkBusinessAddressVO.getRegistPrefix());
        }
        return entity;
    }

    @Override
    public String getUseAddress(ParkBusinessAddressVO parkBusinessAddressVO) {
        String useAddress = "";
        if (parkBusinessAddressVO.getAddressType() == null){
            throw new BusinessException("数据错误，请联系管理员");
        }
        if (parkBusinessAddressVO.getAddressType() == 1){
            useAddress = parkBusinessAddressVO.getRegistPrefix();
        }else{
            if (parkBusinessAddressVO.getCurrentRegistNum() == 0){
                //  如果楼栋不为空
                if (StringUtils.isNotBlank(parkBusinessAddressVO.getRegistAreaMin())){
                    useAddress = parkBusinessAddressVO.getRegistPrefix() + parkBusinessAddressVO.getRegistAreaMin() + parkBusinessAddressVO.getRegistUnit() +  getAreaRegistNumMin(parkBusinessAddressVO.getAreaRegistNumMin(),parkBusinessAddressVO.getAreaRegistNumMax());
                    if (StringUtils.isNotBlank(parkBusinessAddressVO.getRegistPostfix())){
                        useAddress += parkBusinessAddressVO.getRegistPostfix();
                    }
                }else{
                    useAddress = parkBusinessAddressVO.getRegistPrefix()  + getAreaRegistNumMin(parkBusinessAddressVO.getAreaRegistNumMin(),parkBusinessAddressVO.getAreaRegistNumMax());
                    if (StringUtils.isNotBlank(parkBusinessAddressVO.getRegistPostfix())){
                        useAddress += parkBusinessAddressVO.getRegistPostfix();
                    }
                }
            }else{
                //区域注册数总数
                int total = Integer.parseInt(parkBusinessAddressVO.getAreaRegistNumMax()) - Integer.parseInt(parkBusinessAddressVO.getAreaRegistNumMin()) + 1;
                int areaNum = parkBusinessAddressVO.getCurrentRegistNum() / total;
                int regist_num = parkBusinessAddressVO.getCurrentRegistNum() % total;
                int minNum = Integer.parseInt(parkBusinessAddressVO.getAreaRegistNumMin())+regist_num;
                //  如果楼栋为空
                if (StringUtils.isBlank(parkBusinessAddressVO.getRegistAreaMin())){
                    useAddress = parkBusinessAddressVO.getRegistPrefix()  + getAreaRegistNumMin(regist_num+"",parkBusinessAddressVO.getAreaRegistNumMax());
                    if (StringUtils.isNotBlank(parkBusinessAddressVO.getRegistPostfix())){
                        useAddress += parkBusinessAddressVO.getRegistPostfix();
                    }
                }else{
                    // 注册区域是字母
                    if (allUpperCase(parkBusinessAddressVO.getRegistAreaMin())){
                        byte[] min = parkBusinessAddressVO.getRegistAreaMin().getBytes(StandardCharsets.UTF_8);
                        byte[] max = parkBusinessAddressVO.getRegistAreaMax().getBytes(StandardCharsets.UTF_8);
                        int i = (int) min[0];
                        int j = (int) max[0];
                        //  判断是否超过区间
                        char area =  (char)(i+areaNum);
                        useAddress = parkBusinessAddressVO.getRegistPrefix() + area + parkBusinessAddressVO.getRegistUnit() + getAreaRegistNumMin(minNum+"",parkBusinessAddressVO.getAreaRegistNumMax()) ;
                        if (StringUtils.isNotBlank(parkBusinessAddressVO.getRegistPostfix())){
                            useAddress += parkBusinessAddressVO.getRegistPostfix();
                        }
                        // 是数字
                    }else{

                        useAddress = parkBusinessAddressVO.getRegistPrefix() + (Integer.parseInt(parkBusinessAddressVO.getRegistAreaMin()) + areaNum) + parkBusinessAddressVO.getRegistUnit() + getAreaRegistNumMin(minNum+"",parkBusinessAddressVO.getAreaRegistNumMax()) ;
                        if (StringUtils.isNotBlank(parkBusinessAddressVO.getRegistPostfix())){
                            useAddress += parkBusinessAddressVO.getRegistPostfix();
                        }
                    }
                }
            }
        }
        return useAddress;
    }

    @Override
    public String getAreaRegistNumMin(String min, String max) {
        int maxLeng = max.length();
        int minLeng = min.length();
        if (maxLeng > minLeng){
            String stringAreaRegistNumMin = "";
            for (int i = 0;i<maxLeng-minLeng;i++){
                stringAreaRegistNumMin += "0";
            }
            stringAreaRegistNumMin += min;
           return  stringAreaRegistNumMin;
        }else{
           return min;
        }
    }

    /**
     *  生成经营地址
     * @param parkBusinessAddressRulesEntity
     * @return
     */
    private String builderBusinessAddress(ParkBusinessAddressRulesEntity parkBusinessAddressRulesEntity){
        if (parkBusinessAddressRulesEntity.getAddressType() == 1){
            return parkBusinessAddressRulesEntity.getRegistPrefix();
        }else{
            Integer areaRegistNumLen = parkBusinessAddressRulesEntity.getAreaRegistNumLen(); //区域注册数长度
            String currentRegistArea = getRegisArea(parkBusinessAddressRulesEntity);
            if(StringUtil.isEmpty(currentRegistArea)){
                //currentRegistArea = parkBusinessAddressRulesEntity.getCurrentRegistArea();
                currentRegistArea = "";
            }
            //获取当前注册数
            Integer currentRegistNum =  getRegistNum(parkBusinessAddressRulesEntity);
            StringBuilder stringBuilder = new StringBuilder(parkBusinessAddressRulesEntity.getRegistPrefix());
            stringBuilder.append(currentRegistArea);
            if (StrUtil.isNotBlank(parkBusinessAddressRulesEntity.getRegistUnit())){
                stringBuilder.append(parkBusinessAddressRulesEntity.getRegistUnit());
            }
            stringBuilder.append(getAreaRegistNumMin(currentRegistNum+"",parkBusinessAddressRulesEntity.getAreaRegistNumMax()+""));
            if (StrUtil.isNotBlank(parkBusinessAddressRulesEntity.getRegistPostfix())){
                stringBuilder.append(parkBusinessAddressRulesEntity.getRegistPostfix());
            }
                    //.append(String.format("%0"+areaRegistNumLen+"d", currentRegistNum));
            //更新园区规则数据
            if (StrUtil.isNotBlank(currentRegistArea)){
                parkBusinessAddressRulesEntity.setCurrentRegistArea(currentRegistArea);
            }
            parkBusinessAddressRulesEntity.setCurrentRegistNum(currentRegistNum);
            mapper.updateByPrimaryKeySelective(parkBusinessAddressRulesEntity);
            return stringBuilder.toString();
        }
    }

    /**
     * 获取园区注册区域
     * @param parkBusinessAddressRulesEntity
     * @return
     */
    private String getRegisArea(ParkBusinessAddressRulesEntity parkBusinessAddressRulesEntity){
        if (StrUtil.isBlank(parkBusinessAddressRulesEntity.getCurrentRegistArea())){
            return null;
        }
        Integer currentRegistNum = parkBusinessAddressRulesEntity.getCurrentRegistNum(); //当前注册数
        Integer areaRegistNumMax = parkBusinessAddressRulesEntity.getAreaRegistNumMax(); //最大区域注册数
        String currentRegistArea = parkBusinessAddressRulesEntity.getCurrentRegistArea(); //当前注册区域
        String registAreaMax = parkBusinessAddressRulesEntity.getRegistAreaMax(); //注册区域最大
        Integer currentRegistAreaI = 0;
        Integer registAreaMaxI = 0;
        try{
            currentRegistAreaI =Integer.parseInt(currentRegistArea);
            registAreaMaxI =Integer.parseInt(registAreaMax);
            if(currentRegistAreaI < registAreaMaxI){
                if(currentRegistNum.equals(areaRegistNumMax)){
                    currentRegistAreaI ++ ;
                }
            }
            return currentRegistAreaI.intValue() + "";
        }catch (NumberFormatException e){
            try {
                currentRegistAreaI = (int)currentRegistArea.toCharArray()[0];
                registAreaMaxI = (int)registAreaMax.toCharArray()[0];
                if(currentRegistAreaI < registAreaMaxI){
                    if(currentRegistNum.equals(areaRegistNumMax)){
                        currentRegistAreaI ++ ;
                    }
                }
                return (char)currentRegistAreaI.intValue() + "";
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取当前区域注册数
     * @param parkBusinessAddressRulesEntity
     * @return
     */
    private Integer getRegistNum(ParkBusinessAddressRulesEntity parkBusinessAddressRulesEntity){
        Integer currentRegistNum = parkBusinessAddressRulesEntity.getCurrentRegistNum(); //当前注册数
        Integer areaRegistNumMin = parkBusinessAddressRulesEntity.getAreaRegistNumMin(); //最小区域注册数
        Integer areaRegistNumMax = parkBusinessAddressRulesEntity.getAreaRegistNumMax(); //最大区域注册数
        String currentRegistArea = parkBusinessAddressRulesEntity.getCurrentRegistArea(); //当前注册区域
        String registAreaMax = parkBusinessAddressRulesEntity.getRegistAreaMax(); //注册区域最大
        if(currentRegistNum.equals(areaRegistNumMax)){
            if(currentRegistArea.equals(registAreaMax)){
                currentRegistNum = areaRegistNumMax;
            }else{
                currentRegistNum = areaRegistNumMin;
            }
        }else{
            currentRegistNum ++ ;
        }
        return currentRegistNum;
    }

    private boolean allUpperCase(String str){
        boolean flag = true;
        for (int i = 0;i<str.length();i++){
            char c = str.charAt(i);
            flag = Character.isUpperCase(c);
        }
        return flag;
    }
}

