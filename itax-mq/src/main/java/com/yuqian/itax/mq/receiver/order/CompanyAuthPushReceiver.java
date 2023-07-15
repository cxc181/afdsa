package com.yuqian.itax.mq.receiver.order;

import com.yuqian.itax.user.dao.MemberAccountMapper;
import com.yuqian.itax.user.entity.vo.CompanyPushVo;
import com.yuqian.itax.user.service.MemberAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 实名信息推送国金
 * @author：
 * @Date：2021/6/23 15:12
 * @version：1.0
 */
@Component
@Slf4j
public class CompanyAuthPushReceiver {
    //创建十个线程
    private static ExecutorService taskExecutor  = Executors.newFixedThreadPool(10);

    @Resource
    private MemberAccountMapper memberAccountMapper;

    @Autowired
    MemberAccountService memberAccountService;


    @RabbitHandler
    @RabbitListener(queues = "companyAuthPush", priority="100")
    public void process(List<CompanyPushVo> list) {
        if(taskExecutor == null){
            taskExecutor  = Executors.newFixedThreadPool(10);
        }
        log.info("修改状态为同步中");
        memberAccountMapper.batchUpdateAuthPushState(list,1);

        if (list != null && list.size()>0){
            for(CompanyPushVo ov:list){
                if(ov ==null){
                    continue;
                }
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            memberAccountService.companyPushGJ(ov);
                        }catch (Exception ex){
                            log.error("数据错误，推送失败!"+ex.getMessage());
                            if (ov.getId() != null){
                                //  设置推送状态为推送失败
                                memberAccountMapper.updateAuthPushStateById(ov.getId(),3);
                            }
                        }
                    }
                });
            }
        }else{
            log.info("暂无可推送的订单!");
        }


        return ;
    }
}
