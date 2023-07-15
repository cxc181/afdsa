package com.yuqian.itax.api.controller.system;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.entity.query.MessageNoticeQuery;
import com.yuqian.itax.message.service.MessageNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/4 11:04
 *  @Description: 用户消息中心控制器
 */
@Api(tags = "用户消息中心控制器")
@RestController
@RequestMapping("/system/message")
@Slf4j
public class MessageNoticeController extends BaseController {
    @Autowired
    private MessageNoticeService messageNoticeService;

    /**
     * @Description 获取消息数量
     * @Author  Kaven
     * @Date   2020/3/4 11:07
     * @Return ResultVo<Map>
    */
    @ApiOperation("获取消息数量")
    @PostMapping("getMessageCount")
    public ResultVo<Map> getMessageCount(){
        Map<String,Object> dataMap = Maps.newHashMap();
        int count = this.messageNoticeService.getUnReadCount(getCurrUser().getUserId(), getRequestHeadParams("oemCode"));
        dataMap.put("count",count);
        return ResultVo.Success(dataMap);
    }

    /**
     * @Description 根据消息状态获取消息列表
     * @Author  Kaven
     * @Date   2020/3/4 11:21
     * @Param  status:状态 0-未读 1-全部
     * @Return ResultVo<PageInfo<MessageNoticeEntity>>
    */
    @ApiOperation("根据消息状态获取消息列表")
    @PostMapping("listMessage")
    public ResultVo<PageInfo<MessageNoticeEntity>> listMessage(@RequestBody MessageNoticeQuery query, BindingResult result){
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        Map<String,Object> dataMap = Maps.newHashMap();
        query.setOemCode(getRequestHeadParams("oemCode"));
        query.setUserId(getCurrUser().getUserId());
        PageInfo<MessageNoticeEntity> pages = this.messageNoticeService.listMessage(query);
        return ResultVo.Success(pages);
    }

    /**
     * @Description 全部标记为已读
     * @Author  Kaven
     * @Date   2020/3/4 11:21
     * @Param  status:状态 0-未读1-已读 2-已下线 3-已取消
     * @Return ResultVo<List<MessageNoticeEntity>>
     */
    @ApiOperation("全部标记为已读")
    @PostMapping("readAll")
    public ResultVo<List<MessageNoticeEntity>> readAll(){
        this.messageNoticeService.readAll(getCurrUser().getUserId(),getCurrUseraccount(), getRequestHeadParams("oemCode"));
        return ResultVo.Success();
    }

    /**
     * @Description 根据通知id和机构编号获取通知详情
     * @Author  Kaven
     * @Date   2020/3/4 11:40
     * @Param  id
     * @Return ResultVo<MessageNoticeEntity>
    */
    @ApiOperation("根据通知id和机构编号获取通知详情")
    @PostMapping("detail")
    public ResultVo<MessageNoticeEntity> listMessage(@JsonParam Long id){
        if(null == id){
            return ResultVo.Fail("主键id不能为空");
        }
        MessageNoticeEntity t = new MessageNoticeEntity();
        t.setId(id);
        t.setOemCode(getRequestHeadParams("oemCode"));
        MessageNoticeEntity message = this.messageNoticeService.selectOne(t);
        return ResultVo.Success(message);
    }
}