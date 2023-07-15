package com.yuqian.itax.message.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class NoticeManageVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 机构编码
     */
    private String oemCode;
    /**
     * 所属机构
     */
    private String oemName;

    /**
     * 通知类型  1-短信通知 2-站内通知
     */
    private Integer noticeType;



    /**
     * 通知标题
     */
    private String noticeTitle;



    /**
     * 通知对象  1-所有小程序用户 2-指定小程序用户
     */
    private Integer noticeObj;



    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 下线时间
     */
    private Date outTime;

    /**
     * 发布状态 0-待发布 1-已发布 2-已下线 3-已取消
     */
    private Integer sendStatus;

    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 添加人
     */
    private String addUser;
    /**
     * 通知位置(多个通知位置之间用逗号分割)  1-消息中心 2-首页弹窗
     */
    private String noticePosition;
    /**
     * 打开方式 1-通知详情 2-h5地址链接 3-小程序功能
     */
    private Integer openMode;
    /**
     * 通知内容
     */
    private String noticeContent;
    /**
     * 通知副标题
     */
    private String noticeSubtitle;
    /**
     * 跳转地址
     */
    private String jumpUrl;
    /**
     * 用户手机号 多个账号之间用逗号分割
     */
    private String userPhones;
    /**
     * 发布方式 1-立即发送 2-定时发送
     */
    private Integer releaseWay;
    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 备注
     */
    private String remark;
}
