package com.yuqian.itax.admin.controller.company;


import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.group.entity.InvoiceCategoryGroupEntity;
import com.yuqian.itax.group.entity.InvoiceHeadGroupEntity;
import com.yuqian.itax.group.entity.query.InvoiceHeadGroupQuery;
import com.yuqian.itax.group.service.InvoiceCategoryGroupService;
import com.yuqian.itax.system.entity.InvoiceCategoryEntity;
import com.yuqian.itax.system.entity.query.InvoiceCategoryGroupQuery;
import com.yuqian.itax.system.service.InvoiceCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 集团类目
 * @author：HZ
 * @Date：2020/03/04 15:12
 * @version：1.0
 */
@RestController
@RequestMapping("group/invoice/category")
@Slf4j
public class InvoiceCategoryGroupController extends BaseController {

    @Autowired
    InvoiceCategoryGroupService invoiceCategoryGroupService;
    /**
     * 获取集团开票类目列表
     */
    @PostMapping("getInvoiceCategoryGroup")
    public ResultVo getInvoiceCategoryGroup(@RequestBody InvoiceCategoryGroupQuery query){
        //验证登陆
        getCurrUser();
        List<InvoiceCategoryGroupEntity> list =invoiceCategoryGroupService.getInvoiceCategoryGroup(query);
        return ResultVo.Success(list);
    }
}
