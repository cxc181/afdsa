package com.yuqian.itax.profits.entity.vo;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 分润记录返回VO
 * @Author  Kaven
 * @Date   2020/6/10 3:35 下午
*/
@Getter
@Setter
public class MemberProfitsRecordVO implements Serializable {

    private static final long serialVersionUID = -1L;

    // 总分润
    private Long totalProfitsAmount;

    // 分润裂变（分页）
    private PageInfo<MemberProfitsVO> profitsPageData;
}
