package com.yuqian.itax.message.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.message.dao.NoticeManageMapper;
import com.yuqian.itax.message.entity.NoticeManageEntity;
import com.yuqian.itax.message.entity.po.NoticeManagePO;
import com.yuqian.itax.message.entity.query.NoticeManageQuery;
import com.yuqian.itax.message.entity.vo.NoticeManageListVO;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

/**
 * 通知管理service
 *
 * @Date: 2020年03月04日 09:32:20
 * @author 蒋匿
 */
public interface NoticeManageService extends IBaseService<NoticeManageEntity,NoticeManageMapper> {

    /**
     * 通知管理分页
     */
    public PageInfo<NoticeManageEntity> getNoticeManage(NoticeManageQuery query);

    /**
     * 新增通知管理
     */
    NoticeManageEntity addNoticeManage(NoticeManagePO po, String account, List<Map<String, Object>> mapList, String oemCode);


    public boolean sendNotice(NoticeManageEntity noticeManageEntity, String account, List<Map<String, Object>> mapList);

    /**
     * 查询已发布的悬浮公告列表
     */
    List<NoticeManageListVO> getPublishedList(String oemCode);

    /**
     * 查询悬浮公告详情
     */
    NoticeManageEntity getDetail(String oemCode, Long id);

    /**
     * 获取用户文件
     * @param fileName
     */
    Workbook getUserFile(String fileName);
}

