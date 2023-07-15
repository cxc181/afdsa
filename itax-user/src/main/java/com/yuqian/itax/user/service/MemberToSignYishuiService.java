package com.yuqian.itax.user.service;

import com.itextpdf.text.BadElementException;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.dao.MemberToSignYishuiMapper;
import com.yuqian.itax.user.entity.MemberToSignYishuiEntity;

import java.io.IOException;

/**
 * 会员签约易税表service
 * 
 * @Date: 2023/02/20
 * @author lmh
 */
public interface MemberToSignYishuiService extends IBaseService<MemberToSignYishuiEntity, MemberToSignYishuiMapper> {
    /**
     * 是否签约易税平台
     * @param currUserId
     * @return
     */
    boolean yishuiSignQuery(Long currUserId, String oemCode) throws BusinessException;

    /**
     * 校验补全银行卡易税id（已签约情况下）
     * @return
     */
    void verifyAndSetBankYiShuiId(UserBankCardEntity userBankCardEntity, Long professionalId) throws BusinessException;

    /**
     * 易税签约
     * @param fileKey
     * @param currUserId
     */
    void yishuiSign(String fileKey, Long currUserId, String oemCode) throws BusinessException, IOException, BadElementException;
}

