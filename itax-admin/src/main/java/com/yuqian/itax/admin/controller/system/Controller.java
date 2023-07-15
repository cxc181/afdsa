package com.yuqian.itax.admin.controller.system;

import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.CreditCodeEntity;
import com.yuqian.itax.system.service.CreditCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("controller")
public class Controller extends BaseController {

    @Autowired
    CreditCodeService creditCodeService;

    /*
     * 企查查
    */
    @PostMapping("/creditCode")
    public ResultVo creditCode(@JsonParam String oemCode,@JsonParam String name) {
        CreditCodeEntity creditCodeEntity = creditCodeService.getCreditCode(oemCode, name);
        return ResultVo.Success(creditCodeEntity);
    }
}
