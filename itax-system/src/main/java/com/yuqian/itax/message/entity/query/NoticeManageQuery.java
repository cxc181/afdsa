package com.yuqian.itax.message.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NoticeManageQuery extends BaseQuery {

    /**
     * 通知类型  1-短信通知 2-站内通知
     */
    Integer noticeType;

    /**
     * 通知标题
     */
    String noticeTitle;

    /**
     * 发布状态 0-待发布 1-已发布 2-已下线 3-已取消
     */
    Integer sendStatus;

    /**
     * 添加人
     */
    String addUser;

    /**
     * 所属机构
     */
    String oemName;

    /**
     * 下线时间
     */
    String outTime;
}
