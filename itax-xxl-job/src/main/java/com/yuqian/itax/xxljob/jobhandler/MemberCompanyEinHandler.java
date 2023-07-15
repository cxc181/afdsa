package com.yuqian.itax.xxljob.jobhandler;


import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.yuqian.itax.system.entity.CreditCodeEntity;
import com.yuqian.itax.system.service.CreditCodeService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.service.MemberCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 每天凌晨2点批量调企查查接口查询没有税号的个体户
 * @author：hz
 * @Date：2020-12-1
 * @version：1.0
 */
@JobHandler(value="memberCompanyEinHandler")
@Component
public class MemberCompanyEinHandler extends IJobHandler {

    @Autowired
    CreditCodeService creditCodeService;
    @Autowired
    MemberCompanyService memberCompanyService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("=========调企查查接口查询没有税号的个体户任务启动");
        List<MemberCompanyEntity> list=memberCompanyService.queryMemberCompanyListByEin();

            for (MemberCompanyEntity memberCompanyEntity:list) {
                try {
                    CreditCodeEntity creditCodeEntity = creditCodeService.getCreditCode(memberCompanyEntity.getOemCode(), memberCompanyEntity.getCompanyName());
                    if(creditCodeEntity!=null){
                        memberCompanyEntity.setEin(creditCodeEntity.getCreditCode());
                        memberCompanyEntity.setUpdateTime(new Date());
                        memberCompanyEntity.setUpdateUser("xxjob-admin");
                        memberCompanyService.editByIdSelective(memberCompanyEntity);
                    }
                }catch (Exception e){
                    XxlJobLogger.log("XXXXXXXXXXXXXXXXXX"+e.getMessage());
                }
            }
        XxlJobLogger.log("=========调企查查接口查询没有税号的个体户任务结束");
        return SUCCESS;
    }
}
