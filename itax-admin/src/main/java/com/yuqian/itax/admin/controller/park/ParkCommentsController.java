package com.yuqian.itax.admin.controller.park;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.park.entity.ParkCommentsPO;
import com.yuqian.itax.park.entity.query.ParkCommentsQuery;
import com.yuqian.itax.park.entity.vo.ParkCommentsVO;
import com.yuqian.itax.park.service.ParkCommentsService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.user.entity.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;


/**
 * 园区评价管理
 * auth: cxz
 * time: 2022/10/11
 */
@Api(value="ParkCommentsController", tags={"园区评价管理"})
@RestController
@RequestMapping("/park/comments")
@Slf4j
public class ParkCommentsController extends BaseController {

    @Autowired
    private ParkCommentsService parkCommentsService;
    @Autowired
    private ParkService parkService;


    @ApiOperation(value="园区评价列表", notes="园区评价列表")
    @PostMapping("/list")
    public ResultVo list(@RequestBody ParkCommentsQuery query){
        //验证登陆
        getCurrUser();
        UserEntity userEntity=userService.findById(getCurrUserId());
        if(userEntity.getPlatformType()!=null && (userEntity.getPlatformType()!=1 && userEntity.getPlatformType() !=3)){
            return ResultVo.Fail("不是平台或园区账号不允许查看园区评论列表");
        }
        PageInfo<ParkCommentsVO> list = parkCommentsService.queryPageList(query);
        return ResultVo.Success(list);
    }

    @ApiOperation(value="查询园区评价详情", notes="查询园区评价详情")
    @PostMapping("/info")
    public ResultVo info(@JsonParam Long id){
        //验证登陆
        getCurrUser();
        if(id == null){
            return ResultVo.Fail("参数不正确，id为必传");
        }
        ParkCommentsVO vo = parkCommentsService.queryParkCommentsInfo(id);
        return ResultVo.Success(vo);
    }


    @ApiOperation(value="修改园区评价状态(1-可见 2-屏蔽)", notes="修改园区评价状态(1-可见 2-屏蔽)")
    @PostMapping("/updateStatus")
    public ResultVo updateStatus(@JsonParam Long id,@JsonParam Integer status){
        //验证登陆
        CurrUser currUser = getCurrUser();
        // 只能传  1-可见 2-屏蔽
        if(!status.equals(1) && !status.equals(2)){
            return ResultVo.Fail("传参的评价状态不正确");
        }
        parkCommentsService.updateStatus(id,status,currUser.getUseraccount());
        return ResultVo.Success();
    }

    @ApiOperation(value="园区评价回复", notes="园区评价回复")
    @PostMapping("/reply")
    public ResultVo reply(@Valid @RequestBody ParkCommentsPO po){
        //验证登陆
        CurrUser currUser = getCurrUser();
        parkCommentsService.reply(po,currUser);
        return ResultVo.Success();
    }


}
