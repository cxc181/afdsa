package com.yuqian.itax.message.entity.po;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class NoticeManagePO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 机构编码
     */
    @NotNull(message = "请选择所属OEM机构")
    String oemCode;

    /**
     * 通知类型  1-短信通知 2-站内通知 3-悬浮公告
     */
    @NotNull(message = "请选择通知类型")
    Integer noticeType;

    /**
     * 通知位置(多个通知位置之间用逗号分割)  1-消息中心 2-首页弹窗
     */
    String noticePosition;

    /**
     * 通知标题
     */
    @NotBlank(message = "请输入通知标题")
    String noticeTitle;

    /**
     * 打开方式 1-通知详情 2-h5地址链接 3-小程序功能
     */
    Integer openMode;

    /**
     * 通知内容
     */
    String noticeContent;
    /**
     * 通知副标题
     */
    String noticeSubtitle;
    /**
     * 跳转地址
     */
    String jumpUrl;

    /**
     * 通知对象  1-所有小程序用户 2-指定小程序用户
     */
    @NotNull(message = "请选择通知对象")
    Integer noticeObj;

    /**
     * 用户手机号 多个账号之间用逗号分割
     */
    String userPhones;

    /**
     * 发布方式 1-立即发送 2-定时发送
     */
    @NotNull(message = "请选择发布方式")
    Integer releaseWay;

    /**
     * 发送时间
     */
    Date sendTime;
    /**
     * 下线时间
     */
    Date outTime;

    /**
     * 用户文件
     */
    String fileName;

    /**
     * 文件名称
     */
    String originalFileName;
}
