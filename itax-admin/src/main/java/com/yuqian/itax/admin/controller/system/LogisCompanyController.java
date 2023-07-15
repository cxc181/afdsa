package com.yuqian.itax.admin.controller.system;

import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.LogisCompanyEntity;
import com.yuqian.itax.system.service.LogisCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 物流公司信息controller
 * @version：3.1
 */
@Slf4j
@RestController
@RequestMapping("logisCompany")
public class LogisCompanyController extends BaseController {

    @Autowired
    private LogisCompanyService logisCompanyService;

    @PostMapping("queryLogisInfo")
    public ResultVo queryLogisInfo(@JsonParam String companyName){
        List<LogisCompanyEntity> list =  logisCompanyService.queryByLikeCompanyName(companyName);
        if (list == null || list.size()<1){
            return ResultVo.Fail("快递公司名称错误");
        }
        return ResultVo.Success(list);
    }

}
