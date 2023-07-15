package com.yuqian.itax.api.controller.user;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.entity.query.BaseQuery;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.corporateaccount.entity.vo.CorporateAccountApplyOrderVO;
import com.yuqian.itax.corporateaccount.service.CorporateAccountApplyOrderService;
import com.yuqian.itax.corporateaccount.service.CorporateAccountCollectionRecordService;
import com.yuqian.itax.user.entity.query.ComCorpAccQuery;
import com.yuqian.itax.user.entity.query.CorporateAccountCollectionRecordQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.service.CompanyCorporateAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description 企业对公户控制器
 * @Author  Kaven
 * @Date   2020/9/7 11:03
*/
@Api(tags = "企业对公户控制器")
@RestController
@RequestMapping("/companycoporate")
@Slf4j
public class CompanyCorpAccController extends BaseController {

    @Autowired
    private CompanyCorporateAccountService companyCorporateAccountService;
    @Autowired
    private CorporateAccountCollectionRecordService corporateAccountCollectionRecordService;
    @Autowired
    private CorporateAccountApplyOrderService corporateAccountApplyOrderService;

    /**
     * @Description 分页查询企业对公户列表：不包括已注销的记录
     * @Author Kaven
     * @Date 2020/9/7 11:05
     * @Param ComCorpAccQuery
     * @Return ResultVo<PageResultVo < CompanyCorpAccountVO>>
     * @Exception
     */
    @ApiOperation("分页查询企业对公户列表")
    @PostMapping("/listComCorpAccountPage")
    public ResultVo listComCorpAccountPage(@RequestBody @Valid ComCorpAccQuery query, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        query.setOemCode(this.getRequestHeadParams("oemCode"));
        query.setCurrUserId(getCurrUserId());
        List<CompanyCorpAccountVO> list = this.companyCorporateAccountService.listComCorpAccountPage(query);
        Map<String, Object> map = Maps.newHashMap();
        map.put("pageList", PageResultVo.restPage(list));
        map.put("parkName", "");
        Map<String, String> parkNameMap = companyCorporateAccountService.applyUsablePark(getRequestHeadParams("oemCode"));
        if (null != parkNameMap && parkNameMap.size() > 0) {
            map.put("parkName", parkNameMap.get("parkName"));
        }
        return ResultVo.Success(map);
    }

    /**
     * @Description 企业对公户详情查询
     * @Author  Kaven
     * @Date   2020/9/7 14:26
     * @Param   id
     * @Return  ResultVo<CompanyCorpAccountVO>
     * @Exception
    */
    @ApiOperation("企业对公户详情查询")
    @PostMapping("/getDetail")
    public ResultVo<CompanyCorpAccountDetailVO> getDetail(@JsonParam Long id) {
        if(id == null){
            return ResultVo.Fail("ID不能为空");
        }
        CompanyCorpAccountDetailVO detailVO = this.companyCorporateAccountService.getDetail(id);
        return ResultVo.Success(detailVO);
    }

    /**
     * @Description 对公户账户明细查询
     * @Author  Kaven
     * @Date   2020/9/7 15:49
     * @Param CorporateAccountCollectionRecordQuery
     * @Return ResultVo<PublicAccountDetailVO>
     * @Exception
    */
    @ApiOperation("对公户账户明细查询")
    @PostMapping("/corpAccountCollectionRecords")
    public ResultVo<PublicAccountDetailVO> getCorpAccountCollectionRecords(@RequestBody @Valid CorporateAccountCollectionRecordQuery query, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        query.setOemCode(getRequestHeadParams("oemCode"));
        PublicAccountDetailVO detailVO = this.corporateAccountCollectionRecordService.getCorpAccountCollectionRecords(query);
        return ResultVo.Success(detailVO);
    }

    /**
     * @Description 对公户提现-获取收款个人银行卡信息
     * @Author  Kaven
     * @Date   2020/9/8 13:43
     * @Param corporateAccountId
     * @Return ResultVo<CorporateAccountBankCardVO>
     * @Exception
    */
    @ApiOperation("对公户提现-获取收款个人银行卡信息")
    @PostMapping("/queryCorpBankCardInfo")
    public ResultVo<CorporateAccountBankCardVO> queryCorpBankCardInfo(@JsonParam Long corporateAccountId) {
        if(null == corporateAccountId){
            return ResultVo.Fail("企业对公户ID不能为空");
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        CorporateAccountBankCardVO bankCardVO = this.companyCorporateAccountService.queryCorpBankCardInfo(corporateAccountId,oemCode);
        return ResultVo.Success(bankCardVO);
    }

    /**
     * @Description 对公户提现-选择开票记录（分页）
     * @Author  Kaven
     * @Date   2020/9/8 15:00
     * @Param   CorporateAccountCollectionRecordQuery
     * @Return  ResultVo<PageResultVo<CorporateInvoiceOrderVO>>
     * @Exception
    */
    @ApiOperation("对公户提现-选择开票记录（分页）")
    @PostMapping("/listInvoiceOrderPage")
    public ResultVo<PageResultVo<CorporateInvoiceOrderVO>> listInvoiceOrderPage(@RequestBody @Valid CorporateAccountCollectionRecordQuery query, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        List<CorporateInvoiceOrderVO> list = this.companyCorporateAccountService.listInvoiceOrderForCorp(query);
        return ResultVo.Success(PageResultVo.restPage(list));
    }

    /**
     * @Description 公户提现-选择收款记录（分页）
     * @Author  Kaven
     * @Date   2020/9/8 15:07
     * @Param   CorporateAccountCollectionRecordQuery
     * @Return  ResultVo<PageInfo<CorporateAccountCollectionRecordVO>>
     * @Exception
     */
    @ApiOperation("对公户提现-选择收款记录（分页）")
    @PostMapping("/listCollectionRecordPage")
    public ResultVo<PageInfo<CorporateAccountCollectionRecordVO>> listCollectionRecordPage(@RequestBody @Valid CorporateAccountCollectionRecordQuery query, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        if(StringUtils.isEmpty(query.getCompanyName())){
            return ResultVo.Fail("发票抬头公司不能为空");
        }
        query.setOemCode(getRequestHeadParams("oemCode"));
        query.setUserId(getCurrUserId());
        query.setTradingStatus(2);// 只查询收款记录
        query.setIsChoose("1");
        PublicAccountDetailVO detailVO = this.corporateAccountCollectionRecordService.getCorpAccountCollectionRecords(query);
        return ResultVo.Success(detailVO.getFlowPageList());
    }

    /**
     * 对公户申请-申请详情
     * @Author yejian
     * @Date 2020/09/08 14:40
     * @return ResultVo
     */
    @ApiOperation("对公户申请-申请详情")
    @PostMapping(value = "/applyDetail")
    public ResultVo<CompanyCorpAccApplyDetailVO> applyDetail(@JsonParam Long parkId) {
        if (null == parkId) {
            return ResultVo.Fail("园区id不能为空");
        }
        CompanyCorpAccApplyDetailVO companyCorpAccApplyDetailVO = companyCorporateAccountService.applyDetail(getRequestHeadParams("oemCode"), getCurrUserId(), parkId);
        return ResultVo.Success(companyCorpAccApplyDetailVO);
    }

    /**
     * 对公户申请-分页查询申请个体列表
     *
     * @return ResultVo<PageInfo>
     * @Author yejian
     * @Date 2020/09/08 14:40
     * @Param query
     */
    @ApiOperation("对公户申请-分页查询申请个体列表")
    @PostMapping(value = "/applyIndividuallist")
    public ResultVo<PageInfo> applyIndividuallist(@RequestBody BaseQuery query) {
        List<CompanyCorpAccApplyIndividualVO> individualist = corporateAccountApplyOrderService.applyIndividuallist(getCurrUserId(), getRequestHeadParams("oemCode"));
        PageInfo pageInfo = PageResultVo.listToPage(query.getPageNumber(), query.getPageSize(), individualist);
        return ResultVo.Success(pageInfo);
    }

    /**
     * 对公户申请-创建对公户申请订单
     *
     * @param companyId
     * @return ResultVo<Map < String, Object>>
     * @Author yejian
     * @Date 2020/09/09 14:40
     */
    @ApiOperation("对公户申请-创建对公户申请订单")
    @ApiImplicitParam(name = "companyId", value = "企业ID", dataType = "Long", required = true)
    @PostMapping(value = "/createApplyOrder")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.COMPANY_COPORATE_APPLY_CREATE_ORDER, lockTime = 10)
    public ResultVo<Map<String, Object>> createApplyOrder(@JsonParam Long companyId) {
        Map<String, Object> resultMap = corporateAccountApplyOrderService.createApplyOrder(
                getCurrUserId(),
                getRequestHeadParams("oemCode"),
                companyId,
                org.apache.commons.lang.StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType"));
        return ResultVo.Success(resultMap);
    }

    /**
     * 对公户申请-分页查询申请订单列表
     * @Author yejian
     * @Date 2020/09/08 14:40
     * @param query
     * @return ResultVo
     */
    @ApiOperation("对公户申请-分页查询申请订单列表")
    @PostMapping(value = "/applyOrderList")
    public ResultVo<PageInfo> applyOrderList(@RequestBody BaseQuery query) {
        List<CorporateAccountApplyOrderVO> applyOrderList = corporateAccountApplyOrderService.applyOrderList(getCurrUserId(), getRequestHeadParams("oemCode"));
        PageInfo pageInfo = PageResultVo.listToPage(query.getPageNumber(), query.getPageSize(), applyOrderList);
        return ResultVo.Success(pageInfo);
    }

    /**
     * 对公户申请-取消申请订单
     * @Author yejian
     * @Date 2020/09/08 14:40
     * @param orderNo
     * @return ResultVo
     */
    @ApiOperation("对公户申请-取消申请订单")
    @ApiImplicitParam(name = "orderNo", value = "订单编号", dataType = "String", required = true)
    @PostMapping(value = "/cancelApplyOrder")
    public ResultVo cancelApplyOrder(@JsonParam String orderNo) {
        corporateAccountApplyOrderService.cancelApplyOrder(getCurrUserId(), getRequestHeadParams("oemCode"), orderNo);
        return ResultVo.Success();
    }

    /**
     * 对公户续费详情
     * @param companyCorpAccId
     * @return
     */
    @ApiOperation("对公户续费详情")
    @PostMapping("/companyCorpAccRenewDetail")
    public ResultVo companyCorpAccRenewDetail(@JsonParam Long companyCorpAccId) {
        if (null == companyCorpAccId) {
            throw new BusinessException("对公户id不能为空！");
        }
        CompanyCorpAccRenewDetailVO vo = companyCorporateAccountService.companyCorpAccRenewDetail(companyCorpAccId, getCurrUserId());

        return ResultVo.Success(vo);
    }
}