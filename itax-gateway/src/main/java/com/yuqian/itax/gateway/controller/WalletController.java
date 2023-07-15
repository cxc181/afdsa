package com.yuqian.itax.gateway.controller;

import com.yuqian.itax.capital.entity.vo.MemberCapitalAccountApiVO;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.gateway.annotation.JsonParam;
import com.yuqian.itax.order.entity.query.BillDetailApiQuery;
import com.yuqian.itax.order.entity.vo.BillRecordVO;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.profits.entity.query.MemberProfitsApiQuery;
import com.yuqian.itax.profits.entity.query.MemberProfitsQuery;
import com.yuqian.itax.profits.entity.vo.MemberProfitsRecordVO;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @Author: yejian
 * @Date: 2020/11/12 14:11
 * @Description: 钱包接口控制器
 */
@Api(tags = "钱包API")
@RestController
@RequestMapping("/wallet")
@Slf4j
public class WalletController extends BaseController {

    @Autowired
    private UserCapitalAccountService userCapitalAccountService;
    @Autowired
    private ProfitsDetailService profitsDetailService;
    @Autowired
    private OrderService orderService;

    /**
     * @return ResultVo<PageResultVo>
     * @Description 获取我的钱包金额
     * @Author yejian
     * @Date 2020/11/12 14:17
     */
    @ApiOperation("获取我的钱包金额")
    @PostMapping("getBalance")
    public ResultVo<MemberCapitalAccountApiVO> getBalance(@JsonParam Long userId, @JsonParam Integer levelNo) {
        log.info("收到获取我的钱包金额请求：{},{}", userId, levelNo);
        if (null == userId) {
            return ResultVo.Fail("操作失败，会员ID不能为空！");
        }

        if (null == levelNo) {
            return ResultVo.Fail("操作失败，会员等级不能为空！");
        }
        MemberCapitalAccountApiVO accountApiVO = userCapitalAccountService.getBalanceApi(userId, levelNo, getRequestHeadParams("oemCode"));
        return ResultVo.Success(accountApiVO);
    }

    /**
     * @param query
     * @return ResultVo<BillRecordVO>
     * @Description 查询会员账单记录
     * @Author yejian
     * @Date 2019/03/05 10:40
     */
    @ApiOperation("查询会员账单记录")
    @PostMapping("/listBillDetail")
    public ResultVo<BillRecordVO> listBillDetail(@RequestBody @Valid BillDetailApiQuery query, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        if (!Objects.equals(query.getType(), 1) && !Objects.equals(query.getType(), 3) && !Objects.equals(query.getType(), 4)) {
            return ResultVo.Fail("不支持此查询类型！");
        }
        query.setOemCode(getRequestHeadParams("oemCode"));
        BillRecordVO billRecord = orderService.listBillDetailApi(query);
        return ResultVo.Success(billRecord);
    }

    /**
     * @Description 查询会员分润记录
     * @Author yejian
     * @Date 2020/11/16 17:21
     * @Param MemberProfitsApiQuery
     * @Return ResultVo<MemberProfitsRecordVO>
     */
    @ApiOperation("查询会员分润记录")
    @PostMapping("/listProfitDetail")
    public ResultVo<MemberProfitsRecordVO> listProfitDetail(@RequestBody @Valid MemberProfitsApiQuery query, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        MemberProfitsQuery profitsQuery = new MemberProfitsQuery();
        profitsQuery.setPageNumber(query.getPageNumber());
        profitsQuery.setPageSize(query.getPageSize());
        profitsQuery.setUserId(query.getUserId());
        profitsQuery.setOemCode(getRequestHeadParams("oemCode"));
        profitsQuery.setMonth(query.getMonth());
        profitsQuery.setStartDate(query.getStartDate());
        profitsQuery.setEndDate(query.getEndDate());
        profitsQuery.setProfitsType(query.getProfitsType());
        profitsQuery.setLevelNo(query.getLevelNo());
        MemberProfitsRecordVO profitsRecord = profitsDetailService.queryMemberProfitsListNew(profitsQuery);
        return ResultVo.Success(profitsRecord);
    }

}