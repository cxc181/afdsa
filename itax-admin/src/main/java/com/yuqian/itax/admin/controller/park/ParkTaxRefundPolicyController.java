package com.yuqian.itax.admin.controller.park;

import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.park.entity.ParkTaxRefundPolicyPO;
import com.yuqian.itax.park.entity.vo.ParkTaxRefundPolicyVO;
import com.yuqian.itax.park.service.ParkTaxRefundPolicyService;
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
 * 园区返税政策
 * auth: cxz
 * time: 2022/10/11
 */
@Api(value="ParkTaxRefundPolicyController", tags={"园区返税政策"})
@RestController
@RequestMapping("/park/refund/policy")
@Slf4j
public class ParkTaxRefundPolicyController extends BaseController {

    @Autowired
    private ParkTaxRefundPolicyService parkTaxRefundPolicyService;

    @ApiOperation(value="查询园区返税政策详情", notes="查询园区返税政策详情")
    @PostMapping("/info")
    public ResultVo info(@JsonParam Long parkId){
        getCurrUser();
        List<ParkTaxRefundPolicyVO> list = parkTaxRefundPolicyService.info(parkId);
        return ResultVo.Success(list);
    }

    @ApiOperation(value="修改园区返税政策", notes="修改园区返税政策")
    @PostMapping("/save")
    public ResultVo save(@Valid @RequestBody List<ParkTaxRefundPolicyPO> poList){
        CurrUser currUser = getCurrUser();
        parkTaxRefundPolicyService.save(poList,currUser);
        return ResultVo.Success();
    }

}
