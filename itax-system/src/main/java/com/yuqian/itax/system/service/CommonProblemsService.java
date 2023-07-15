package com.yuqian.itax.system.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.entity.CommonProblemsEntity;
import com.yuqian.itax.system.dao.CommonProblemsMapper;
import com.yuqian.itax.system.entity.dto.CommonProblemsPO;
import com.yuqian.itax.system.entity.query.CommonProblemsQuery;
import com.yuqian.itax.system.entity.vo.CommonProblemsVO;

import java.util.List;

/**
 * 常见问题service
 * 
 * @Date: 2019年12月08日 21:40:04 
 * @author 蒋匿
 */
public interface CommonProblemsService extends IBaseService<CommonProblemsEntity,CommonProblemsMapper> {

    /**
     * 根据机构编码获取常见问题列表
     * @param oemCode
     * @return
     */
    List<CommonProblemsEntity> getCommomProbleListByOemCode(String oemCode);

    /**
     * 根据参数获取常见问题列表（分页）
     * @param query
     * @return
     */
    PageInfo<CommonProblemsVO> getCommomProblePageInfo(CommonProblemsQuery query);

    /**
     * 添加常见问题
     * @param po
     * @param account
     * @return
     */
    CommonProblemsEntity addCommonProblemsEntity(CommonProblemsPO po,String account);

    /**
     * 修改常见问题
     * @param po
     * @param account
     * @return
     */
    CommonProblemsEntity updateCommonProblemsEntity(CommonProblemsPO po,String account);

    /**
     * 批量插入
     */
    void batchInsertCommonProblems(List<CommonProblemsEntity> list ,String oemCode,String account);

    CommonProblemsVO getCommonProblemsById(Long id);
}

