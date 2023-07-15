package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class BatchMenberInfoExportVO implements Serializable {

    /**
     * 会员账号
     */
    @Excel(name = "注册账号")
    private String memberAccount;

    /**
     * 会员昵称
     */
    @Excel(name = "会员昵称")
    private String memberName;

    /**
     * 用户名
     */
    @Excel(name = "用户姓名")
    private String realName;

    /**
     * 用户身份
     */
    @Excel(name = "用户身份", replace = { "-_null","未实名_0","个人_1","企业_2" }, height = 10, width = 22)
    private String memberAuthType;

    /**
     * 所在省市
     */
    @Excel(name = "所在省市")
    private String areaName;

    /**
     * 会员等级
     */
    @Excel(name = "会员等级")
    private String levelName;

    /**
     * 注册时间
     */
    @Excel(name = "注册时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date addTime;

    /**
     * 产品邀请人
     */
    //@Excel(name = "产品邀请人")
    private String parentMemberId;

    /**
     * 邀请人账号
     */
    @Excel(name = "产品邀请人")
    private String parentMemberAccount;

    /**
     * 渠道名称
     */
    @Excel(name = "渠道来源")
    private String channelName;

    /**
     * 机构名称
     */
    @Excel(name = "OEM机构")
    private String oemName;

    /**
     * 账户状态  1-正常 0-禁用 2-注销
     */
    @Excel(name = "账户状态", replace = { "-_null","禁用_0","正常_1","注销_2"})
    private Integer status;

    /**
     * 人群标签名称
     */
    @Excel(name = "人群标签名称")
    private String crowdLabelName;

    /**
     * 接入方名称
     */
    @Excel(name = "接入方")
    private String accessPartyName;

    /**
     * 实名同步状态
     */
    @Excel(name = "实名同步状态", replace = { "-_null","待同步_0","同步中_1","已同步_2","同步失败_3","无需同步_4" }, height = 10, width = 22)
    private Integer authPushState;
}
