package com.yuqian.itax.admin.controller.system;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.entity.query.FeedbackQuery;
import com.yuqian.itax.system.entity.vo.FeedbackVO;
import com.yuqian.itax.system.service.FeedbackService;
import com.yuqian.itax.user.entity.UserEntity;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 意见反馈
 * @author：pengwei
 * @Date：2020/4/21 9:50
 * @version：1.0
 */
@RestController
@RequestMapping("feed/back")
@Slf4j
public class FeedBackController extends BaseController {

    @Autowired
    private FeedbackService feedbackService;

    @ApiOperation("列表页")
    @PostMapping("page")
    public ResultVo listPageProduct(@RequestBody FeedbackQuery query) {
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setOemCode(userEntity.getOemCode());
        PageInfo<FeedbackVO> page = feedbackService.listPage(query);
        return ResultVo.Success(page);
    }

    @ApiOperation("导出")
    @PostMapping("export")
    public ResultVo export(@RequestBody FeedbackQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setOemCode(userEntity.getOemCode());
        List<FeedbackVO> lists = feedbackService.listFeedback(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("意见反馈记录", "意见反馈", FeedbackVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("意见反馈导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }
}
