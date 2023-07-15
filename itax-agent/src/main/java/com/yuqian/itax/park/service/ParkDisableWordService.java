package com.yuqian.itax.park.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.dao.ParkDisableWordMapper;
import com.yuqian.itax.park.entity.ParkDisableWordEntity;
import com.yuqian.itax.park.entity.query.ParkDisableWordQuery;
import com.yuqian.itax.park.entity.vo.ParkDisableWordInsertVO;
import com.yuqian.itax.park.entity.vo.ParkDisableWordVO;

import java.util.List;
import java.util.Map;

/**
 * 园区禁用字service
 * 
 * @Date: 2021年10月19日 14:31:08 
 * @author 蒋匿
 */
public interface ParkDisableWordService extends IBaseService<ParkDisableWordEntity,ParkDisableWordMapper> {

    /**
     * 分页查询禁用字号
     * @param query
     * @return
     */
    PageInfo<ParkDisableWordVO> queryParkDisableWord(ParkDisableWordQuery query);

    /**
     * 查询禁用字号数据
     * @param query
     * @return
     */
    List<ParkDisableWordVO>  listParkDisableWord(ParkDisableWordQuery query);


    /**
     * 根据园区id和禁用字号名称查询数据
     * @param parkId
     * @param disableWords
     * @return
     */
    ParkDisableWordEntity getDisableWordByParkIdAndDisableWord(Long parkId, String disableWords);

    /**
     * 根据园区id查询禁用字号
     * @param parkId
     * @return
     */
    List<ParkDisableWordEntity> getDisableWordByParkId(Long parkId);

    /**
     * 校验数据
     * @param excelList
     * @param parkId
     * @param userName
     * @return
     */
    Map<String,Object> checkDisableWord(List<ParkDisableWordInsertVO> excelList ,Long parkId,String userName);

    /**
     * 批量插入
     * @param list
     */
    void batchAddDisableWord(List<ParkDisableWordEntity> list);

    /**
     * 禁用字校验
     * @param parkId
     * @param shopName
     * @return
     */
    void checkoutDisableWord(Long parkId, String shopName, String shopNameOne, String shopNameTwo);
}

