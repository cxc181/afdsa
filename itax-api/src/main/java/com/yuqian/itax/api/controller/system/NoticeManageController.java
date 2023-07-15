package com.yuqian.itax.api.controller.system;

import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.message.entity.NoticeManageEntity;
import com.yuqian.itax.message.entity.vo.NoticeManageListVO;
import com.yuqian.itax.message.service.NoticeManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: yejian
 * @Date: 2020/10/23 09:04
 * @Description: 通知管理控制器
 */
@Api(tags = "通知管理控制器")
@RestController
@RequestMapping("/system/noticeManage")
@Slf4j
public class NoticeManageController extends BaseController {
    @Autowired
    private NoticeManageService noticeManageService;

    /**
     * @return ResultVo<List < NoticeManageListVO>>
     * @Description 查询悬浮公告列表
     * @Author yejian
     * @Date 2020/10/23 10:40
     */
    @ApiOperation("查询悬浮公告列表")
    @PostMapping("/list")
    public ResultVo<List<NoticeManageListVO>> getList() {
        return ResultVo.Success(noticeManageService.getPublishedList(getRequestHeadParams("oemCode")));
    }

    /**
     * @param id
     * @return ResultVo<NoticeManageEntity>
     * @Description 查询发票抬头详情
     * @Author yejian
     * @Date 2020/10/23 10:40
     */
    @ApiOperation("查询悬浮公告详情")
    @ApiImplicitParam(name = "id", value = "主键id", dataType = "Long", required = true)
    @PostMapping("/detail")
    public ResultVo<NoticeManageEntity> getDetail(@JsonParam Long id) {
        if (null == id) {
            return ResultVo.Fail("主键id不能为空");
        }
        NoticeManageEntity noticeManage = noticeManageService.getDetail(getRequestHeadParams("oemCode"), id);
        return ResultVo.Success(noticeManage);
    }

}