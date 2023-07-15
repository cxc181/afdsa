package com.yuqian.itax.api.controller.user;

import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.capital.entity.dto.BankCardDTO;
import com.yuqian.itax.capital.entity.dto.UnbindBankCardDTO;
import com.yuqian.itax.capital.entity.vo.BankCardVO;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.entity.query.BillDetailQuery;
import com.yuqian.itax.order.entity.vo.BillDetailVO;
import com.yuqian.itax.order.entity.vo.BillIncomePayVO;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.system.entity.BankBinEntity;
import com.yuqian.itax.system.entity.BankInfoEntity;
import com.yuqian.itax.system.entity.vo.BankInfoVO;
import com.yuqian.itax.system.service.BankBinService;
import com.yuqian.itax.system.service.BankInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/16 11:04
 *  @Description: 会员银行卡管理控制器
 */
@Api(tags = "会员银行卡管理控制器")
@Slf4j
@RestController
@RequestMapping("/user/bankcard")
public class BankCardController extends BaseController {
    @Autowired
    private UserBankCardService userBankCardService;
    @Autowired
    private BankInfoService bankInfoService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BankBinService bankBinService;

    /**
     * @Description 获取用户绑定银行卡列表
     * @Author  Kaven
     * @Date   2019/12/16 11:12
     * @Return ResultVo
    */
    @ApiOperation("获取用户绑定银行卡列表")
    @PostMapping("/listBankCards")
    public ResultVo<List<BankCardVO>> listBankCards(@JsonParam Integer walletType){
        if (null == walletType) {
            return ResultVo.Fail("钱包类型不能为空");
        }
        List<BankCardVO> list = this.userBankCardService.listBankCards(getCurrUser().getUserId(), getRequestHeadParams("oemCode"), walletType);
        return ResultVo.Success(list);
    }

    /**
     * @Description 解绑银行卡
     * @Author  Kaven
     * @Date   2019/12/16 11:38
     * @Param  id verifyCode
     * @Return ResultVo
     * @Exception BusinessException
    */
    @ApiOperation("解绑银行卡")
    @PostMapping("/unbind")
    public ResultVo unbind(@RequestBody @Validated UnbindBankCardDTO dto, BindingResult result){
        if(null == dto){
            return ResultVo.Fail("传入参数为空");
        }

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        if(null == dto.getId() || StringUtils.isBlank(dto.getVerifyCode())){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        this.userBankCardService.unbind(getCurrUser().getUserId(),dto.getId(),dto.getVerifyCode());
        return ResultVo.Success();
    }

    /**
     * @Description 银行卡绑定
     * @Author  Kaven
     * @Date   2019/12/16 13:54
     * @Param  dto
     * @Return ResultVo
     * @Exception BusinessException
    */
    @ApiOperation("银行卡绑定")
    @PostMapping("/bind")
    public ResultVo bind(@RequestBody BankCardDTO dto) {
        if(null == dto){
            return ResultVo.Fail("传入参数为空");
        }
        if(null == dto.getName() || StringUtils.isBlank(dto.getVerifyCode()) || StringUtils.isBlank(dto.getReserveMobile())
                || StringUtils.isBlank(dto.getBankNumber()) || StringUtils.isBlank(dto.getIdCardNo())){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        dto.setUserId(getCurrUserId());
        dto.setOemCode(this.getRequestHeadParams("oemCode"));
        String registRedisTime = System.currentTimeMillis() + 300000 + ""; // redis标识值
        try {
            dto.setRegistRedisTime(registRedisTime);
            this.userBankCardService.bindCard(dto);
        } catch (BusinessException e) {
            // 释放redis锁
            redisService.unlock(RedisKey.REGIST_ORDER_BIND_REDIS_KEY + dto.getReserveMobile(),registRedisTime);
            throw e;
        }
        return ResultVo.Success();
    }

    /**
     * @Author yejian
     * @Date 2019/12/19 14:09
     * @param
     * @return ResultVo<BankInfoVO>
     */
    @ApiOperation("查询支持银行卡列表")
    @PostMapping("/listBankInfo")
    public ResultVo<List<BankInfoVO>> listBankInfo() {
        if(null == getCurrUser()){
            return ResultVo.Fail("操作失败，用户未登录！");
        }
        List<BankInfoVO> bankInfoList = bankInfoService.listBankInfo();
        return ResultVo.Success(bankInfoList);
    }

    /**
     * @Description 通过银行卡号获取银行名称及logo的接口
     * @Author  Kaven
     * @Date   2019/12/24 20:08
     * @Param  cardNo
     * @Return ResultVo
     * @Exception
    */
    @ApiOperation("通过银行卡号获取银行名称及LOGO")
    @PostMapping("/queryCarbin")
    public ResultVo<BankInfoVO> queryCarbin(@JsonParam String cardNo) {
        if(StringUtils.isBlank(cardNo)){
            return ResultVo.Fail("查询失败，银行卡号不能为空！");
        }
        BankBinEntity bankBin = this.bankBinService.findByCardNo(cardNo);
        if (null == bankBin) {
            return ResultVo.Success();
        }
        // 查询银行信息
        BankInfoEntity t = new BankInfoEntity();
        t.setBankCode(bankBin.getBankCode().substring(0,4));// 截取前4位
        BankInfoEntity bank = this.bankInfoService.selectOne(t);
        BankInfoVO bankInfo = new BankInfoVO();
        bankInfo.setBankName(bank.getBankName());
        bankInfo.setBankLogoUrl(bank.getBankLogoUrl());
        return ResultVo.Success(bankInfo);
    }

    /**
     * @Author yejian
     * @Date 2019/03/05 10:40
     * @param query
     * @return ResultVo<PageResultVo<BillDetailVO>>
     */
    @ApiOperation("分页查询账单明细列表")
    @PostMapping("/listBillDetail")
    public ResultVo<PageResultVo<BillDetailVO>> listBillDetail(@RequestBody @Valid BillDetailQuery query, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        List<BillDetailVO> billDetailList = orderService.listBillDetail(getCurrUserId(), getRequestHeadParams("oemCode"), query);
        return ResultVo.Success(PageResultVo.restPage(billDetailList));
    }

    /**
     * @Author yejian
     * @Date 2019/03/05 10:40
     * @param query
     * @return ResultVo<BillIncomePayVO>
     */
    @ApiOperation("统计账单收入和支出")
    @PostMapping("/statisBillIncomePay")
    public ResultVo<BillIncomePayVO> statisBillIncomePay(@RequestBody @Valid BillDetailQuery query, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        BillIncomePayVO billIncomePay = orderService.statisBillIncomePay(getCurrUserId(), getRequestHeadParams("oemCode"), query);
        return ResultVo.Success(billIncomePay);
    }

    /**
     * @Author yejian
     * @Date 2020/06/24 10:40
     * @param
     * @return ResultVo
     */
    @ApiOperation("纳呗签约注册查询")
    @PostMapping("/nabeiSignQuery")
    public ResultVo nabeiSignQuery() {
        Map<String,Object> resultMap = userBankCardService.nabeiSignQuery(getCurrUserId(), getRequestHeadParams("oemCode"));
        return ResultVo.Success(resultMap);
    }

    /**
     * @Author yejian
     * @Date 2020/06/28 09:20
     * @param
     * @return ResultVo
     */
    @ApiOperation("纳呗签约注册")
    @PostMapping("/nabeiSignRegister")
    public ResultVo nabeiSignRegister() {
        userBankCardService.nabeiSignRegister(getCurrUserId(), getRequestHeadParams("oemCode"));
        return ResultVo.Success();
    }

    @ApiOperation("纳呗签约申请")
    @PostMapping("/nabeiH5Sign")
    public ResultVo nabeiH5Sign() {
        Map<String, Object> resultMap = userBankCardService.nabeiH5Sign(getCurrUserId(), getRequestHeadParams("oemCode"));
        return ResultVo.Success(resultMap);
    }
}