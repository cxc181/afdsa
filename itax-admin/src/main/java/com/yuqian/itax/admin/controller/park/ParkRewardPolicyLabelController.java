package com.yuqian.itax.admin.controller.park;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.park.entity.ParkRewardPolicyLabelPO;
import com.yuqian.itax.park.entity.query.ParkRewardPolicyLabelQuery;
import com.yuqian.itax.park.entity.vo.ParkRewardPolicyLabelVO;
import com.yuqian.itax.park.entity.vo.RewardPolicyLabelBaseVO;
import com.yuqian.itax.park.service.ParkRewardPolicyLabelService;
import com.yuqian.itax.park.service.RewardPolicyLabelBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;


/**
 * 园区奖励政策管理
 * auth: cxz
 * time: 2022/10/11
 */
@Api(value="ParkRewardPolicyLabelController", tags={"园区奖励政策管理"})
@RestController
@RequestMapping("/park/reward/label")
@Slf4j
public class ParkRewardPolicyLabelController extends BaseController {

    @Autowired
    private ParkRewardPolicyLabelService parkRewardPolicyLabelService;
    @Autowired
    private RewardPolicyLabelBaseService rewardPolicyLabelBaseService;

    @ApiOperation(value="查询园区奖励政策列表-分页", notes="查询园区奖励政策列表-分页")
    @PostMapping("/list")
    public ResultVo list(@Valid @RequestBody ParkRewardPolicyLabelQuery query){
        getCurrUser();
        PageInfo<ParkRewardPolicyLabelVO> list = parkRewardPolicyLabelService.queryPageList(query);
        return ResultVo.Success(list);
    }

    @ApiOperation(value="查询园区奖励政策详情", notes="查询园区奖励政策详情")
    @PostMapping("/info")
    public ResultVo info(@JsonParam Long id){
        getCurrUser();
        ParkRewardPolicyLabelVO vo = parkRewardPolicyLabelService.info(id);
        return ResultVo.Success(vo);
    }

    @ApiOperation(value="新增/修改园区奖励政策", notes="新增/修改园区奖励政策")
    @PostMapping("/save")
    public ResultVo save(@Valid @RequestBody ParkRewardPolicyLabelPO po){
        CurrUser currUser = getCurrUser();
        parkRewardPolicyLabelService.add(po,currUser);
        return ResultVo.Success();
    }

    @ApiOperation(value="删除园区奖励政策", notes="删除园区奖励政策")
    @PostMapping("/delete")
    public ResultVo delete(@JsonParam Long id){
        parkRewardPolicyLabelService.delete(id);
        return ResultVo.Success();
    }

    @ApiOperation(value="新增园区奖励政策常用标签接口", notes="新增园区奖励政策常用标签接口")
    @PostMapping("/addLabelBase")
    public ResultVo addLabelBase(){
        getCurrUser();
        return ResultVo.Success();
    }

    @ApiOperation(value="查询园区奖励政策常用标签接口", notes="查询园区奖励政策常用标签接口")
    @PostMapping("/queryLabelBaseList")
    public ResultVo queryLabelBaseList(){
        List<RewardPolicyLabelBaseVO> list = rewardPolicyLabelBaseService.queryList();
        return ResultVo.Success(list);
    }


}
