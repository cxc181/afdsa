package com.yuqian.itax.api.controller.user;

import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.corporateaccount.service.CorporateAccountContOrderService;
import com.yuqian.itax.order.entity.vo.CreatCorpAccContOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName CorporateAccountContOrderController
 * @Description 对公户续费订单控制器
 * @Author lmh
 * @Date 2021/9/8 11:41
 * @Version 1.0
 */
@Api(tags = "对公户续费订单控制器")
@RestController
@RequestMapping("/corpAccContOrder")
@Slf4j
public class CorporateAccountContOrderController extends BaseController {

    @Autowired
    private CorporateAccountContOrderService corporateAccountContOrderService;

    @ApiOperation("创建对公户续费订单")
    @PostMapping("/creatContOrder")
    public ResultVo creatContOrder(@JsonParam Long companyCorpAccId) {
        if (null == companyCorpAccId) {
            throw new BusinessException("对公户id不能为空");
        }
        CreatCorpAccContOrderVO vo = corporateAccountContOrderService.creatContOrder(companyCorpAccId, getCurrUserId());
        return ResultVo.Success(vo);
    }
}
