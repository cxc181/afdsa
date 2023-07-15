package com.yuqian.itax.api.controller.user;

import com.google.common.collect.Maps;
import com.itextpdf.text.BadElementException;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.user.service.MemberToSignYishuiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;


/**
 *  @Author: lmh
 *  @Date: 2023/02/20
 *  @Description: 易税管理控制器
 */
@Api(tags = "易税管理控制器")
@Slf4j
@RestController
@RequestMapping("/user/ys")
public class YiShuiController extends BaseController {

    @Autowired
    private MemberToSignYishuiService memberToSignYishuiService;

    @ApiOperation("签约查询")
    @PostMapping("/signQuery")
    public ResultVo signQuery() {
        boolean b = memberToSignYishuiService.yishuiSignQuery(getCurrUserId(), getRequestHeadParams("oemCode"));
        HashMap<String, Integer> map = Maps.newHashMap();
        map.put("signStatus", 0);
        if (b) {
            map.put("signStatus", 1);
        }
        return ResultVo.Success(map);
    }

    @ApiOperation("易税签约")
    @PostMapping("/sign")
    public ResultVo signAgreement(@JsonParam String fileId) {
        String redisLockKey = RedisKey.YISHUI_SIGN_KEY_SUFFER + getCurrUserId();
        String redisTime = (System.currentTimeMillis() + 60000) + "";
        try {
            // 加入redis锁机制，防重复提交
            boolean flag = redisService.lock(redisLockKey, redisTime,60);
            if(!flag){
                throw new BusinessException("请勿重复提交！");
            }
            memberToSignYishuiService.yishuiSign(fileId, getCurrUserId(), getRequestHeadParams("oemCode"));
        } catch (BadElementException e) {
            return ResultVo.Fail("签约失败");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 释放redis锁
            redisService.unlock(redisLockKey,redisTime);
        }
        return ResultVo.Success();
    }
}