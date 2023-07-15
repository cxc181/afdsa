package com.yuqian.itax.user.entity.vo;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/26 15:17
 *  @Description: 已邀请未开户用户信息展示类
 */
@Getter
@Setter
public class InvitedRegUserVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 累积待注册企业数
     */
    private Integer totalCount;

    /**
     * 用户列表
     */
    private PageInfo<MemberVO> memberPageList;
}
