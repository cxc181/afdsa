package com.yuqian.itax.gateway.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.capital.entity.dto.UserWithdrawDTO;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.dao.InvoiceOrderMapper;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.dto.InvoiceConfirmDTO;
import com.yuqian.itax.order.entity.vo.OrderNoVO;
import com.yuqian.itax.order.service.InvoiceOrderChangeRecordService;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.InvoiceRecordService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.tax.entity.query.PendingTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.PendingTaxBillVO;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.user.dao.CompanyTaxHostingMapper;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.dto.*;
import com.yuqian.itax.user.entity.po.UpdateMemberPhonePO;
import com.yuqian.itax.user.entity.query.AgentMemberQuery;
import com.yuqian.itax.user.entity.query.GjMemberQuery;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.MoneyUtil;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *  @Author: HZ
 *  @Date: 2021/4/28
 *  @Description: 对外给国金的controller
 */
@RestController
@RequestMapping("/gj")
@Slf4j
public class GjController extends BaseController {

    @Autowired
    InvoiceOrderService invoiceOrderService;

    @Autowired
    private MemberAccountService memberAccountService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private InvoiceRecordService invoiceRecordService;

    @Autowired
    private InvoiceOrderChangeRecordService invoiceOrderChangeRecordService;

    @Resource
    private CompanyTaxHostingMapper companyTaxHostingMapper;

    @Resource
    private InvoiceOrderMapper invoiceOrderMapper;

    @Autowired
    private CompanyTaxBillService companyTaxBillService;

    /**
     * 个体户佣金开票提交接口
     * @Author HZ
     * @Date 2021/04/28
     * @param entity
     * @return orderNo佣金开票订单号
     */
    @PostMapping("createInvoiceOrderForCommission")
    public ResultVo createInvoiceOrderForCommission(@RequestBody UserWithdrawDTO entity){
        if(StringUtil.isNotBlank(getRequestHeadParams("oemCode"))){
            entity.setOemCode(getRequestHeadParams("oemCode"));
        }
        try{
            vaild(entity);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        //做个sign防重复提交
        //查询未付款订单
        List<OrderNoVO> unpaidList = invoiceOrderService.getUnpaidList(entity.getUserId(), entity.getOemCode(), entity.getCompanyId());
        if(CollectionUtil.isNotEmpty(unpaidList)){
            for (OrderNoVO vo :unpaidList){
                invoiceOrderService.xxljobCancelInvOrder(vo.getOrderNo());
            }
        }
        // 校验是否存在超时未确认成本税单
        PendingTaxBillQuery pendingTaxBillQuery = new PendingTaxBillQuery();
        pendingTaxBillQuery.setCompanyId(entity.getCompanyId());
        pendingTaxBillQuery.setStatusRange(1);
        List<PendingTaxBillVO> pendingTaxBillVOS = companyTaxBillService.pendingTaxBill(pendingTaxBillQuery);
        if (CollectionUtil.isNotEmpty(pendingTaxBillVOS)) {
            List<PendingTaxBillVO> collect = pendingTaxBillVOS.stream().filter(x -> x.getTimeDifference() < 0).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect)) {
                return ResultVo.Fail("该企业存在超时未确认成本的税单");
            }
        }
        List<String> cacelOrderList = orderService.getOderNoByCompany(entity.getCompanyId());
        if(CollectionUtil.isNotEmpty(cacelOrderList)){
            return ResultVo.Fail("存在待处理的注销订单");
        }

        //开票
        String commissionInvoiceOrderNo = invoiceOrderService.createInvoiceOrderForCommission(entity,"国金佣金提现，操作人："+entity.getAgentId());
        return ResultVo.Success(commissionInvoiceOrderNo);
    }

    private void vaild( UserWithdrawDTO entity){
        if(entity.getCompanyId()==null){
            throw  new BusinessException("企业ID不能为空");
        }
        if(entity.getUserId()==null){
            throw  new BusinessException("用户ID不能为空");
        }
        if(entity.getAgentId()==null){
            throw  new BusinessException("服务商/员工ID不能为空");
        }
        if(entity.getAmount()==null){
            throw  new BusinessException("开票金额不能为空");
        }
        if(entity.getAmount()<=0){
            throw  new BusinessException("开票金额不能小于等于0");
        }
        if(StringUtil.isBlank(entity.getOemCode())){
            throw  new BusinessException("oemCode不能为空");
        }
        if(entity.getInvoiceType()==null){
            throw  new BusinessException("发票类型不能为空");
        }
        if(entity.getInvoiceType()!=1&&entity.getInvoiceType()!=2){
            throw  new BusinessException("无效的开票类型");
        }
        if(StringUtil.isBlank(entity.getCategoryName())){
            throw  new BusinessException("开票类目不能为空");
        }
        if(StringUtil.isBlank(entity.getCompanyName())){
            throw  new BusinessException("发票抬头公司不能为空");
        }
        if(StringUtil.isBlank(entity.getCompanyAddress())){
            throw  new BusinessException("发票抬头公司地址不能为空");
        }
        if(StringUtil.isBlank(entity.getEin())){
            throw  new BusinessException("发票抬头公司税号不能为空");
        }
        if(StringUtil.isBlank(entity.getPhone())){
            throw  new BusinessException("发票抬头电话不能为空");
        }
        if(StringUtil.isBlank(entity.getBankName())){
            throw  new BusinessException("发票抬头开户行不能为空");
        }
        if(StringUtil.isBlank(entity.getBankNumber())){
            throw  new BusinessException("发票抬头银行账号不能为空");
        }
        if(StringUtil.isBlank(entity.getRecipient())){
            throw  new BusinessException("收件人不能为空");
        }
        if(StringUtil.isBlank(entity.getRecipientPhone())){
            throw  new BusinessException("收件人电话不能为空");
        }
        if(StringUtil.isBlank(entity.getRecipientAddress())){
            throw  new BusinessException("抬头详细地址不能为空");
        }
        if(StringUtil.isBlank(entity.getProvinceName())){
            throw  new BusinessException("省信息不能为空");
        }
        if(StringUtil.isBlank(entity.getCityName())){
            throw  new BusinessException("市信息不能为空");
        }
        if(StringUtil.isBlank(entity.getDistrictName())){
            throw  new BusinessException("区信息不能为空");
        }
    }

    /**
     * 推广用户查询接口
     * @Author HZ
     * @Date 2021/04/29
     */
    @PostMapping("queryAgentMember")
    public ResultVo queryAgentMember(@RequestBody JSONObject jsonObject){
        AgentMemberQuery query =new AgentMemberQuery();
        try {
            String channelServiceIds =jsonObject.getStr("channelServiceIdList");
            if(StringUtil.isNotBlank(channelServiceIds)){
                String [] channelServiceIdList=channelServiceIds.split(",");
                List<Long> resultList = new ArrayList<>(channelServiceIdList.length);
                for (String s : channelServiceIdList) {
                    resultList.add(Long.parseLong(s));
                }
                query.setChannelServiceIdList( resultList);
            }
            String channelEmployeesIds =jsonObject.getStr("channelEmployeesIdList");
            if(StringUtil.isNotBlank(channelEmployeesIds)){
                String [] channelEmployeesIdList=channelEmployeesIds.split(",");
                List<Long> resultList = new ArrayList<>(channelEmployeesIdList.length);
                for (String s : channelEmployeesIdList) {
                    resultList.add(Long.parseLong(s));
                }
                query.setChannelEmployeesIdList( resultList);
            }
        }catch (Exception e){
            return ResultVo.Fail("channelServiceIdList/channelEmployeesIdList参数类型错误！");
        }
        try{
            String channelUserIdsStr =jsonObject.getStr("channelUserIds");
            if(StringUtil.isNotBlank(channelUserIdsStr)){
                String [] channelUserIdsStrList=channelUserIdsStr.split(",");
                List<Long> resultList = new ArrayList<>(channelUserIdsStrList.length);
                for (String s : channelUserIdsStrList) {
                    resultList.add(Long.parseLong(s));
                }
                query.setChannelUserIds( resultList);
            }
        }catch (Exception e){
            return ResultVo.Fail("channelUserIds参数类型错误！");
        }
         query.setPageNumber(jsonObject.getInt("pageNumber")==null?1:jsonObject.getInt("pageNumber"));
        query.setPageSize(jsonObject.getInt("pageSize")==null?10:jsonObject.getInt("pageSize"));
        query.setMemberName(jsonObject.getStr("memberName"));
        query.setMemberPhone(jsonObject.getStr("memberPhone"));
        query.setMemberLevel(jsonObject.getInt("memberLevel"));
        query.setIsFission(jsonObject.getInt("isFission"));
        query.setStatus(jsonObject.getInt("status"));
        query.setChannelProductCode(jsonObject.getStr("channelProductCode"));
        query.setChannelCode(jsonObject.getStr("channelCode"));
        query.setStartDate(jsonObject.getStr("startTime"));
        query.setEndDate(jsonObject.getStr("endTime"));
        if(StringUtil.isNotBlank(getRequestHeadParams("oemCode"))){
            query.setOemCode(getRequestHeadParams("oemCode"));
        }
        if(query.getIsFission()!=null && query.getIsFission()!=0 && query.getIsFission() != 1){
            return ResultVo.Fail("渠道直推或裂变参数错误！");
        }
        PageInfo<AgentMemberVO> pageInfo=memberAccountService.queryAgentMemberPageInfo(query);
        return ResultVo.Success(pageInfo);
    }

    /**
     * 推广关系修改
     * @Author HZ
     * @Date 2021/04/29
     * @param jsonObject 查询条件接受实体
     */
    @PostMapping("updateChannelServiceIdMember")
    public ResultVo updateChannelServiceIdMember(@RequestBody JSONObject jsonObject){
        if(jsonObject == null){
            return ResultVo.Fail("参数不能为空！");
        }
        String gjOemCode = "";
        String oemCode = "";
        if(jsonObject.containsKey("gjOemCode")){
            gjOemCode = jsonObject.getStr("gjOemCode");
        }
        if(StringUtil.isNotBlank(getRequestHeadParams("oemCode"))){
            oemCode = getRequestHeadParams("oemCode");
        }
        JSONArray jsonArray = null;
        if(jsonObject.containsKey("list")){
            jsonArray = jsonObject.getJSONArray("list");
        }
        if(jsonArray == null){
            return ResultVo.Fail("参数不能为空！");
        }
        int length = jsonArray.size();
        AgentMemberQuery query = null;
        JSONObject itemObject = null;
        JSONArray userArray = null;
        List<Long> channelUserList = null;
        List<AgentMemberQuery> list = new ArrayList<>();
        for(int i =0 ;i<length;i++){
            itemObject = jsonArray.getJSONObject(i);
            if(itemObject == null){
                return ResultVo.Fail("参数不能为空！");
            }
            query =new AgentMemberQuery();
            query.setChannelCode(gjOemCode);
            query.setOemCode(oemCode);
            if(itemObject.containsKey("userIds")){
                userArray = itemObject.getJSONArray("userIds");
                if(userArray == null || userArray.size() == 0){
                    return ResultVo.Fail("渠道用户id列表不能为空");
                }
                try {
                    channelUserList = userArray.toList(Long.class);
                }catch (Exception e){
                    return ResultVo.Fail("渠道用户id列表参数类型错误！");
                }
                query.setChannelUserIds(channelUserList);
            }
            if(itemObject.containsKey("agentUserId")){
                try {
                    query.setChannelServiceId(itemObject.getLong("agentUserId"));
                }catch (Exception e){
                    return ResultVo.Fail("agentUserId参数类型错误！");
                }
            }
            if(itemObject.containsKey("employeeUserId")){
                try {
                    query.setChannelEmployeesId(itemObject.getLong("employeeUserId"));
                }catch (Exception e){
                    return ResultVo.Fail("employeeUserId参数类型错误！");
                }
            }
            if(query.getChannelServiceId()==null){
                return ResultVo.Fail("服务商id不能为空");
            }
            if(StringUtils.isBlank(query.getChannelCode())){
                return ResultVo.Fail("渠道code不能为空");
            }
            list.add(query);
        }
        try{
            memberAccountService.updateChannelServiceIdMember(list);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }
    /**
     * 接查询国金助手用户在云财的直推和裂变用户的信息数据
     * @Date 2021/04/28
     * @param entity
     * @return orderNo佣金开票订单号
     */
    @ApiOperation("根据国金助手用户id查询直推或裂变会员")
    @PostMapping("getMemberInfoByChannelServiceId")
    public ResultVo getMemberInfoByChannelServiceId(@RequestBody @Valid GjMemberQuery entity, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        String oemCode = getRequestHeadParams("oemCode");
        if(StringUtils.isBlank(oemCode)){
            return ResultVo.Fail("oemCode不能为空！");
        }else{
            entity.setOemCode(oemCode);
        }
//        if(null == entity.getIsFission()){
//            return ResultVo.Fail("渠道直推或裂变参数不能为空！");
//        }
//        if(null != entity.getIsFission() && entity.getIsFission()!=0 && entity.getIsFission() != 1){
//            return ResultVo.Fail("渠道直推或裂变参数错误！");
//        }
        String status = entity.getStatus();
        if(StringUtils.isNotBlank(status)){
            entity.setStatusList(Arrays.asList(status.split(",")));
        }

        PageInfo<GjMemberInfoVO> pageInfo = memberAccountService.getMemberInfoByChannelServiceIdPageInfo(entity);

        return ResultVo.Success(pageInfo);
    }

    /**
     * @Description 国金推广用户业绩查询
     * @Author jiangni
     * @Date 2020/06/09 13:03
     * @return ResultVo<PageInfo>
     */
    @ApiOperation("国金推广用户业绩查询")
    @PostMapping("/userPushResultListByChannelUser")
    public ResultVo<PageInfo> userPushResultListByChannelUser(@RequestBody @Valid GjMemberQuery query, BindingResult results) {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        String oemCode = getRequestHeadParams("oemCode");
        if(StringUtils.isBlank(oemCode)){
            return ResultVo.Fail("oemCode不能为空！");
        }else{
            query.setOemCode(oemCode);
        }
//        if(null == query.getIsFission()){
//            return ResultVo.Fail("渠道直推或裂变参数不能为空！");
//        }
//        if(query.getIsFission()!=0 && query.getIsFission() != 1){
//            return ResultVo.Fail("渠道直推或裂变参数错误！");
//        }
        String status = query.getStatus();
        if(StringUtils.isNotBlank(status)){
            query.setStatusList(Arrays.asList(status.split(",")));
        }
        PageInfo pageInfo = memberAccountService.getUserPushResultPageByChannelUser(query);
        return ResultVo.Success(pageInfo);
    }

    /**
     * @Description 国金推广用户业绩查询(废弃)
     * @Author jiangni
     * @Date 2020/06/09 13:03
     * @return ResultVo<PageInfo>
     * @deprecated 弃使用
     */
    @ApiOperation("国金推广用户统计")
    @PostMapping("/userPushResultStatisByChannelUser")
    @Deprecated
    public ResultVo userPushResultStatisByChannelUser(@RequestBody JSONObject jsonObject) {
        String oemCode = getRequestHeadParams("oemCode");
        if(StringUtils.isBlank(oemCode)){
            return ResultVo.Fail("oemCode不能为空！");
        }
        if(!jsonObject.containsKey("channelUsers")){
            return ResultVo.Fail("渠道服务商不能为空");
        }
        List<Long> userList = null;
        try {
           userList = jsonObject.getJSONArray("channelUsers").toList(Long.class);
        }catch (Exception e){
            return ResultVo.Fail("channelUsers参数类型错误！");
        }
        if(null == userList || userList.size() == 0){
            return ResultVo.Fail("渠道服务商不能为空");
        }
        List<GjPushStatisInfoVO> list = memberAccountService.userPushResultStatisByChannelUser(userList,oemCode);
        Map<String,Object>  result = new HashMap<>();
        if(list!=null && list.size()>0){
            result.put("monthDirectUserCount",list.stream().mapToInt(GjPushStatisInfoVO::getMonthDirectUserCount).sum());
            result.put("monthFissionUserCount",list.stream().mapToInt(GjPushStatisInfoVO::getMonthFissionUserCount).sum());
            result.put("monthDirectCompanyCount",list.stream().mapToInt(GjPushStatisInfoVO::getMonthDirectCompanyCount).sum());
            result.put("monthFissionCompanyCount",list.stream().mapToInt(GjPushStatisInfoVO::getMonthFissionCompanyCount).sum());
            result.put("totalDirectUserCount",list.stream().mapToInt(GjPushStatisInfoVO::getTotalDirectUserCount).sum());
            result.put("totalFissionUserCount",list.stream().mapToInt(GjPushStatisInfoVO::getTotalFissionUserCount).sum());
            result.put("totalDirectCompanyCount",list.stream().mapToInt(GjPushStatisInfoVO::getTotalDirectCompanyCount).sum());
            result.put("totalFissionCompanyCount",list.stream().mapToInt(GjPushStatisInfoVO::getTotalFissionCompanyCount).sum());
            result.put("servicesList",list);
        }
        return ResultVo.Success(result);
    }

    /**
     * @param accountBindDTO
     * @return ResultVo
     * @Author shudu
     * @Date 2021/04/28 14:11
     */
    @ApiOperation("国金助手账号绑定")
    @PostMapping("/accountBind")
    public ResultVo accountBind(@RequestBody AccountBindDTO accountBindDTO){
        if(accountBindDTO.getPhone()==null||"".equals(accountBindDTO.getPhone())){
            return ResultVo.Fail("手机号不能为空");
        }
        if(accountBindDTO.getName()==null||"".equals(accountBindDTO.getName())){
            return ResultVo.Fail("服务商姓名不能为空");
        }
        if(accountBindDTO.getIdCard()==null||"".equals(accountBindDTO.getIdCard())){
            return ResultVo.Fail("服务商身份证号不能为空");
        }
        Map<String,Object> map = new HashMap<>();
        List<MemberAccountEntity> memberAccountEntityList =  memberAccountService.queryMemberByPhoneAndOemCode(accountBindDTO.getPhone(),getRequestHeadParams("oemCode"));
        if(memberAccountEntityList!=null&&memberAccountEntityList.size()>0){
            memberAccountEntityList.stream().forEach(m ->{
                if(accountBindDTO.getName().equals(m.getRealName())&&accountBindDTO.getIdCard().equals(m.getIdCardNo())){
                    map.put("memberId",m.getId());
                    map.put("oemCode",m.getOemCode());
                }
            });
        }else {
            return ResultVo.Fail("手机号不存在");
        }
        if(map.isEmpty()){
            return ResultVo.Fail("账号实名信息和当前账号不一致，请确认后重试");
        }else{
            return ResultVo.Success(map);
        }
    }
    /**
     * @param queryCompanyInfoDTO
     * @return ResultVo
     * @Author shudu
     * @Date 2021/04/28 14:11
     */
    @ApiOperation("佣金开票个体户查询")
    @PostMapping("/queryCompanyInfo")
    public ResultVo queryCompanyInfo(@RequestBody @Valid QueryCompanyInfoDTO queryCompanyInfoDTO, BindingResult result){
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        MemberAccountEntity entity = new MemberAccountEntity();
        entity.setChannelUserId(queryCompanyInfoDTO.getChannelUserId());
        entity.setOemCode(getRequestHeadParams("oemCode"));
        entity.setChannelCode(queryCompanyInfoDTO.getChannelCode());
        entity.setStatus(1);
        List<MemberAccountEntity> list = memberAccountService.select(entity);
        if(list == null || list.size() == 0){
            return ResultVo.Fail("未查询到会员信息");
        }else if(list.size()>1){
            return ResultVo.Fail("查询到多个会员信息，请联系客服确认");
        }
        entity = list.get(0);
        Map<String,Object> map = new HashMap<>();
        if(entity!=null){
            map.put("memberAccount",entity.getMemberAccount());
        }else{
            return ResultVo.Fail("未查询到会员信息");
        }
        if(queryCompanyInfoDTO.getCategory()!=null&&!"".equals(queryCompanyInfoDTO.getCategory())){
            String[] arr = queryCompanyInfoDTO.getCategory().split(",");
            List<String> categoryBaseId = new ArrayList<>();
            for(String c:arr){
                categoryBaseId.add(c);
            }
            List<MemberCompanyVo> companyList = memberCompanyService.getMemberCompanyBymemberId(entity.getId(), getRequestHeadParams("oemCode"), categoryBaseId,queryCompanyInfoDTO.getOperatorIdCardNo());
            Long allInvAmount = 0L;
            List<InvoiceOrderEntity> invOrderList = new ArrayList<InvoiceOrderEntity>();
            List<GJMemberCompanyVo> resultCompanyList = new ArrayList<>();
            GJMemberCompanyVo gjMemberCompanyVo = null;
            for (MemberCompanyVo company : companyList) {
                // 增值税减免周期 1-按月 2-按季度
                if (Objects.equals(company.getVatBreaksCycle(), 1)) {
                    // 查询本月开票订单
                    String month = DateUtil.format(new Date(), "yyyy-MM");
                    invOrderList = invoiceOrderMapper.InvOrderListOfDate(entity.getId(), getRequestHeadParams("oemCode"), company.getParkId(), company.getId(), month, null, null, null);
                } else if (Objects.equals(company.getVatBreaksCycle(), 2)) {
                    //查询本季度开票订单
                    int year = DateUtil.getYear(new Date());
                    String[] currQuarter = DateUtil.getCurrQuarter(year,Integer.valueOf(DateUtil.getQuarter()));
                    invOrderList = invoiceOrderMapper.InvOrderListOfDate(entity.getId(), getRequestHeadParams("oemCode"), company.getParkId(), company.getId(), null, currQuarter[0], currQuarter[1], null);
                }
                // 累加开票金额
                allInvAmount = invOrderList.stream().mapToLong(InvoiceOrderEntity::getInvoiceAmount).sum();
                // 判断开票金额是否在优惠政策内
                if (MoneyUtil.moneyComp(new BigDecimal(allInvAmount), new BigDecimal(company.getVatBreaksAmount()))) {
                    // 如果累计开票金额大于减免额度
                    company.setVatBreaksRemainAmount(0L);
                } else {
                    // 如果累计开票金额小于减免额度
                    company.setVatBreaksRemainAmount(company.getVatBreaksAmount() - allInvAmount);
                }

                // 个人所得税减免周期 1-按月 2-按季度
                if (Objects.equals(company.getIncomeTaxBreaksCycle(), 1L)) {
                    //查询本月开票订单
                    String month = DateUtil.format(new Date(), "yyyy-MM");
                    invOrderList = invoiceOrderMapper.InvOrderListOfDate(entity.getId(), getRequestHeadParams("oemCode"), company.getParkId(), company.getId(), month, null, null, null);
                } else if (Objects.equals(company.getIncomeTaxBreaksCycle(), 2L)) {
                    //查询本季度开票订单
                    int year = DateUtil.getYear(new Date());
                    String[] currQuarter = DateUtil.getCurrQuarter(year,Integer.valueOf(DateUtil.getQuarter()));
                    invOrderList = invoiceOrderMapper.InvOrderListOfDate(entity.getId(), getRequestHeadParams("oemCode"), company.getParkId(), company.getId(), null, currQuarter[0], currQuarter[1], null);
                }

                // 累加本开票金额
                allInvAmount = invOrderList.stream().mapToLong(InvoiceOrderEntity::getInvoiceAmount).sum();

                // 判断开票金额是否在优惠政策内
                if (MoneyUtil.moneyComp(new BigDecimal(allInvAmount), new BigDecimal(company.getIncomeTaxBreaksAmount()))) {
                    // 如果累计开票金额大于减免额度
                    company.setIncomeTaxBreaksRemainAmount(0L);
                } else {
                    // 如果累计开票金额小于减免额度
                    company.setIncomeTaxBreaksRemainAmount(company.getIncomeTaxBreaksAmount() - allInvAmount);
                }
                //参数转换 add ni.jiang
                gjMemberCompanyVo = new GJMemberCompanyVo();
                BeanUtils.copyProperties(company,gjMemberCompanyVo);
                resultCompanyList.add(gjMemberCompanyVo);
            }
            resultCompanyList.forEach(vo -> {
                if (MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue().equals(vo.getStatus())) {
                    vo.setStatus(MemberCompanyStatusEnum.TAX_CANCELLED.getValue());
                }
            });
            map.put("companyList",resultCompanyList);
            return ResultVo.Success(map);
        }
        return ResultVo.Success();
    }
    /**
     * @param updateUserRemarkDTO
     * @return ResultVo
     * @Author shudu
     * @Date 2021/04/28 14:11
     */
    @ApiOperation("直客用户备注编辑")
    @PostMapping("/updateUserRemark")
    public ResultVo updateUserRemark(@RequestBody UpdateUserRemarkDTO updateUserRemarkDTO){
        if(updateUserRemarkDTO.getUserId()==null||"".equals(updateUserRemarkDTO.getUserId())){
            return ResultVo.Fail("用户id不能为空");
        }
        if(updateUserRemarkDTO.getRemark()==null||"".equals(updateUserRemarkDTO.getRemark())){
            return ResultVo.Fail("备注不能为空");
        }
        int i = memberAccountService.updateUserRemark(Long.parseLong(updateUserRemarkDTO.getUserId()),Long.parseLong(updateUserRemarkDTO.getUserId()),updateUserRemarkDTO.getRemark(),getRequestHeadParams("oemCode"));
        if(i>0){
            return ResultVo.Success();
        }else{
            return ResultVo.Fail("该userId在机构不存在");
        }
    }

    /**
     * @Description 小程序推广二维码获取
     * @Author  shudu
     * @Date   2021/4/30 11:13
     * @Param   GenExtQrcodeDTO BindingResult
     * @Return  ResultVo<Map>
     * @Exception
     */
    @PostMapping("generalizedQrCode")
    public ResultVo<Map> generalizedQrCode(@RequestBody @Validated GenExtQrcodeDTO dto, BindingResult result){
        if(null == dto){
            return ResultVo.Fail("参数不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        try {
            String oemCode = this.getRequestHeadParams("oemCode");
            String sourceType = StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType");
            dto.setOemCode(oemCode);
            dto.setSourceType(sourceType);
            String qrCode = this.memberAccountService.generalizedQrCode(dto);
            Map<String,String> dataMap = Maps.newHashMap();
            dataMap.put("qrCode",qrCode);
            return ResultVo.Success(dataMap);
        } catch (BusinessException e) {
            log.error("获取二维码异常：{}",e.getMessage());
            return ResultVo.Fail(e.getMessage());
        }catch (Exception e) {
            log.error("系统未知异常：{}",e.getMessage());
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * @Description 推广中心-推广订单按进度统计
     * @Author HZ
     * @Date 2021/4/30
     * @Param userId
     * @Return ResultVo<CompanyRegProgressVO>
     */
    @ApiOperation("推广中心-推广订单按进度统计")
    @PostMapping("progress")
    public ResultVo progress(@RequestBody JSONObject jsonObject) {
        if(jsonObject == null){
            return ResultVo.Fail("操作失败，必传参数不能为空！");
        }
        MemberExtendQuery query = new MemberExtendQuery();
        if(jsonObject.containsKey("userId") && StringUtils.isNotBlank(jsonObject.getStr("userId"))){
            try {
                query.setUserId(jsonObject.getLong("userId"));
            }catch (Exception e){
                return ResultVo.Fail("操作失败，userId参数格式错误");
            }
        }
        if(jsonObject.containsKey("memberUserIds")
                && jsonObject.getJSONArray("memberUserIds")!=null
                && jsonObject.getJSONArray("memberUserIds").size()>0){
            try {
                query.setChannelUserIds(jsonObject.getJSONArray("memberUserIds").toList(Long.class));
            }catch (Exception e){
                return ResultVo.Fail("操作失败，memberUserIds参数格式错误");
            }
        }
        String oemCode=getRequestHeadParams("oemCode");
        if (StringUtil.isBlank(oemCode)) {
            return ResultVo.Fail("操作失败，oem机构编码不能为空！");
        }
        if(query.getUserId() == null && query.getChannelUserIds() == null){
            return ResultVo.Fail("服务商/员工ID 或 映射用户id列表不能为空");
        }
        query.setOemCode(oemCode);
        CompanyRegProgressVO regData = memberAccountService.queryCompanyRegProgressByChannelServiceId(query);
        //开票
        CompanyInvoiceProgressVO invoiceData = memberAccountService.queryCompanyInvoiceProgressByChannelServiceId(query);
        GjProgressVO gjProgressVO= new GjProgressVO(regData,invoiceData);
        return ResultVo.Success(gjProgressVO);
    }

    /**
     * @Description 推广中心-推广记录明细查询
     * @Author  HZ
     * @Date   2021/4/30 10:09 下午
     * @Param  MemberExtendQuery
     * @Return ResultVo<ExtendRecordDetailVO>
     * @Exception
     */
    @ApiOperation("推广中心-推广记录明细查询")
    @PostMapping("queryExtendRecordDetail")
    public ResultVo<ExtendRecordDetailVO> queryExtendRecordDetail(@RequestBody MemberExtendQuery query,@RequestBody JSONObject jsonObject) {
        log.info("收到查询推广记录明细请求：{}", JSON.toJSONString(query));
        if(StringUtil.isNotBlank(getRequestHeadParams("oemCode"))){
            query.setOemCode(getRequestHeadParams("oemCode"));
        }
        if(jsonObject.containsKey("userId")
                && StringUtils.isNotBlank(jsonObject.getStr("userId"))) {
            if (query.getUserId() == null) {
                return ResultVo.Fail("服务商/员工ID未传");
            }
        }
        if(jsonObject.containsKey("memberUserIds")
                && jsonObject.getJSONArray("memberUserIds")!=null
                && jsonObject.getJSONArray("memberUserIds").size()>0){
            try {
                query.setChannelUserIds(jsonObject.getJSONArray("memberUserIds").toList(Long.class));
            }catch (Exception e){
                return ResultVo.Fail("操作失败，memberUserIds参数格式错误");
            }
        }
        if(query.getUserId() == null && query.getChannelUserIds() == null){
            return ResultVo.Fail("服务商/员工ID 或 映射用户id列表不能为空");
        }
        if(query.getOrderType()==null){
            return ResultVo.Fail("未知的订单类型");
        }
        if(query.getOrderType()==6&&(query.getOrderStatus()!=0&&query.getOrderStatus()!=2&&query.getOrderStatus()!=3&&query.getOrderStatus()!=7)){
            return ResultVo.Fail("查询开票：未知的订单状态");
        }
        if(query.getOrderType()==5&&(query.getOrderStatus()!=0&&query.getOrderStatus()!=1&&query.getOrderStatus()!=2&&query.getOrderStatus()!=4&&query.getOrderStatus()!=5&&query.getOrderStatus()!=8)){
            return ResultVo.Fail("查询注册个体户：未知的订单状态");
        }
        if(query.getOrderType()==8&&(query.getOrderStatus()!=0&&query.getOrderStatus()!=1&&query.getOrderStatus()!=2)){
            return ResultVo.Fail("查询注册注销:未知的订单状态");
        }
        ExtendRecordDetailVO extendVO = this.orderService.queryExtendRecordDetailByChannelServiceId(query);
        return ResultVo.Success(extendVO);
    }
    /**
     * @param invoiceConfirmDTO
     * @return ResultVo
     * @Author shudu
     * @Date 2021/04/28 14:11
     */
    @ApiOperation("个体户佣金开票确认")
    @PostMapping("/invoiceConfirm")
    public ResultVo invoiceConfirm(@RequestBody InvoiceConfirmDTO invoiceConfirmDTO){
        if(StringUtil.isBlank(invoiceConfirmDTO.getResult())){
            return ResultVo.Fail("提现结果不能为空");
        }
        if(invoiceConfirmDTO.getOrderNo()==null||"".equals(invoiceConfirmDTO.getOrderNo())){
            return ResultVo.Fail("订单编号不能为空");
        }

        OrderEntity entity = orderService.queryByOrderNo(invoiceConfirmDTO.getOrderNo());
        if(entity==null){
            return ResultVo.Fail("订单号不存在，请检查");
        }

        if(6==entity.getOrderType()&&9==entity.getOrderStatus()){
            if("1".equals(invoiceConfirmDTO.getResult())){
                invoiceOrderService.invoiceConfirmGatewaySuccess(invoiceConfirmDTO.getOrderNo(),null,"个体户佣金开票确认出票中","国金助手："+entity.getChannelCode());
            }else if("2".equals(invoiceConfirmDTO.getResult())){
                invoiceOrderService.invoiceConfirmGateway(invoiceConfirmDTO.getOrderNo(),"个体户佣金开票确认取消订单","国金助手："+entity.getChannelCode());
            }else {
                return ResultVo.Fail("result参数无效");
            }
        }else{
            return ResultVo.Fail("订单状态不为待出款，无法变更状态");
        }

        return ResultVo.Success();
    }

    /**
     * 国金用户登录
     * @param query
     * @return
     */
    @ApiOperation("国金用户登录")
    @PostMapping("loginFormGJ")
    public ResultVo loginFormGJ(@RequestBody GjMemberQuery query) {
        if(query == null){
            return ResultVo.Fail("参数不能为空");
        }
        if(StringUtils.isBlank(query.getChannelOemCode())){
            return ResultVo.Fail("国金的oemcode不能为空");
        }
        if(query.getChannelUserId() == null){
            return ResultVo.Fail("登录账号的id不能为空");
        }
        String oemCode = getRequestHeadParams("oemCode");
        if(StringUtils.isBlank(oemCode)){
            return ResultVo.Fail("需要登录的oemcode不能为空");
        }
        if(StringUtils.isBlank(query.getMemberAccount())){
            return ResultVo.Fail("登录手机号不能为空");
        }
        query.setOemCode(oemCode);
        String token = memberAccountService.loginFormGJ(query);
        if(StringUtil.isBlank(token)){
            return ResultVo.Fail("登录失败，请稍后再试");
        }else{
            Map<String,String> tokenMap = new HashMap<>();
            tokenMap.put("token",token);
            return ResultVo.Success(tokenMap);
        }
    }

    /**
     * 国金用户实名认证
     * @param gjUserAuthDTO
     * @param result
     * @return
     */
    @ApiOperation("国金用户实名注册")
    @PostMapping("authFormGJ")
    public ResultVo authFormGJ(@RequestBody @Valid GJUserAuthDTO gjUserAuthDTO, BindingResult result) {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        String oemCode = getRequestHeadParams("oemCode");
        if(StringUtils.isBlank(oemCode)){
            return ResultVo.Fail("需要登录的oemcode不能为空");
        }
        if(StringUtils.isBlank(gjUserAuthDTO.getOldMemberAccount()) && StringUtils.isBlank(gjUserAuthDTO.getMemberAccount())){
            return ResultVo.Fail("新手机号与原手机号必须存在至少一个");
        }
        MemberAccountEntity oldMemberAccount = null;
        //原手机号不为空
        if(StringUtils.isNotBlank(gjUserAuthDTO.getOldMemberAccount())){
            Example example = new Example(MemberAccountEntity.class);
            example.createCriteria().andEqualTo("oemCode",oemCode)
                    .andEqualTo("channelCode",gjUserAuthDTO.getChannelOemCode())
                    .andEqualTo("channelUserId",gjUserAuthDTO.getChannelUserId())
                    .andEqualTo("memberAccount",gjUserAuthDTO.getOldMemberAccount())
                    .andNotEqualTo("status",2);
            List<MemberAccountEntity> memberList = memberAccountService.selectByExample(example);
            if(memberList == null || memberList.size() == 0){
                return ResultVo.Fail("未找到原手机号信息，请确认信息是否正确");
            }else if(memberList.size()>1){
                return ResultVo.Fail("找到多个原手机号的信息，请联系客服");
            }
            oldMemberAccount = memberList.get(0);
        }
        boolean isRegist = false;
        //账号修改
        if(StringUtils.isNotBlank(gjUserAuthDTO.getMemberAccount())){
            MemberAccountEntity memberEntity = memberAccountService.queryByAccount(gjUserAuthDTO.getMemberAccount(),oemCode);
            //如果原手机号为空则表示为新增
            if(memberEntity != null && (StringUtils.isBlank(gjUserAuthDTO.getOldMemberAccount()) || (StringUtils.isNotBlank(gjUserAuthDTO.getOldMemberAccount()) && !gjUserAuthDTO.getOldMemberAccount().equals(gjUserAuthDTO.getMemberAccount())))) {
                return ResultVo.Fail("新手机号已存在，不能进行重复注册");
            }else if(memberEntity == null && StringUtils.isBlank(gjUserAuthDTO.getOldMemberAccount())){
                //同步注册会员信息
                memberAccountService.syncAccountFromGJ(gjUserAuthDTO,oemCode);
                oldMemberAccount = memberAccountService.queryByAccount(gjUserAuthDTO.getMemberAccount(),oemCode);
                isRegist = true;
            }//如果原手机号不为空且与新手机号不同则表示修改
            else if(oldMemberAccount!=null && memberEntity == null && !gjUserAuthDTO.getOldMemberAccount().equals(gjUserAuthDTO.getMemberAccount())){
                UpdateMemberPhonePO po = new UpdateMemberPhonePO();
                po.setPhone(gjUserAuthDTO.getMemberAccount());
                po.setId(oldMemberAccount.getId());
                po.setRemark("国金助手同步实名信息");
                //会员修改手机号
                orderService.updateMemberPhone(oldMemberAccount,po,"国金助手");
            }
        }
        if(oldMemberAccount == null){
            return ResultVo.Fail("实名失败，未找到需要实名的用户信息");
        }

        Map<String,String> resultMap = new HashMap<>();
        //修改实名信息
        String message = memberAccountService.updateAuthFromGj(gjUserAuthDTO,oldMemberAccount);
        resultMap.put("authStatus", "true");
        resultMap.put("authErrMsg", "");
        if(StringUtils.isNotBlank(message)){
            if(isRegist) {
                resultMap.put("authStatus", "false");
                resultMap.put("authErrMsg", message);
            }else{
                return ResultVo.Fail(message);
            }
        }
        if(isRegist) {
            resultMap.put("registStatus", isRegist + "");
            return  ResultVo.Success(resultMap);
        }
        return  ResultVo.Success();
    }

    /**
     * 根据国金用户id查询会员信息
     * @param jsonObject
     * @return
     */
    @ApiOperation("根据国金用户id查询会员信息")
    @PostMapping("findMemberByChannelUserId")
    public ResultVo findMemberByChannelUserId(@RequestBody JSONObject jsonObject) {
        if(!jsonObject.containsKey("channelOemCode") || !jsonObject.containsKey("channelUserId")){
            return ResultVo.Fail("渠道编码和渠道用户id不能为空");
        }
        String oemCode = getRequestHeadParams("oemCode");
        if(StringUtils.isBlank(oemCode)){
            return ResultVo.Fail("云财oemcode不能为空");
        }
        String  channelOemCode = jsonObject.getStr("channelOemCode");
        Long channelUserId = null;
        try {
             channelUserId = jsonObject.getLong("channelUserId");
        }catch (Exception e){
            return ResultVo.Fail("渠道用户id格式错误");
        }
        Example example = new Example(MemberAccountEntity.class);
        example.createCriteria()
                .andEqualTo("channelCode",channelOemCode)
                .andEqualTo("channelUserId",channelUserId)
                .andEqualTo("oemCode",oemCode).andNotEqualTo("status",2);
        List<MemberAccountEntity> list = memberAccountService.selectByExample(example);
        if(list == null || list.size() == 0 ){
            return ResultVo.Success();
        }else if(list.size()>1){
            return ResultVo.Fail("已找到多个会员信息，请联系客服进行确认");
        }else{
            Map<String,Object> resultMap = new HashMap<>();
            MemberAccountEntity memberAccountEntity = list.get(0);
            resultMap.put("memberName",StringUtils.isNotBlank(memberAccountEntity.getRealName())? memberAccountEntity.getRealName() : memberAccountEntity.getMemberName());
            resultMap.put("memberAccount",memberAccountEntity.getMemberAccount());
            resultMap.put("status",memberAccountEntity.getStatus());
            resultMap.put("channelServiceId",memberAccountEntity.getChannelServiceId());
            resultMap.put("channelEmployeesId",memberAccountEntity.getChannelEmployeesId());
            return ResultVo.Success(resultMap);
        }
    }

    /**
     * 根据国金用户id查询企业列表
     * @param jsonObject
     * @return
     */
    @ApiOperation("根据国金用户id查询企业列表")
    @PostMapping("findCompanyListByChannelUserId")
    public ResultVo findCompanyListByChannelUserId(@RequestBody JSONObject jsonObject) {
        if(!jsonObject.containsKey("channelOemCode") || !jsonObject.containsKey("channelUserId")){
            return ResultVo.Fail("渠道编码和渠道用户id不能为空");
        }
        String oemCode = getRequestHeadParams("oemCode");
        if(StringUtils.isBlank(oemCode)){
            return ResultVo.Fail("云财oemcode不能为空");
        }
        String  channelOemCode = jsonObject.getStr("channelOemCode");
        Long channelUserId = null;
        try {
            channelUserId = jsonObject.getLong("channelUserId");
        }catch (Exception e){
            return ResultVo.Fail("渠道用户id格式错误");
        }
        List<GJUserCompanyVo> list = memberCompanyService.findCompanyListByChannelUserId(channelUserId,channelOemCode,oemCode);
        Map<String,Object> map = new HashMap<>();
        map.put("totalCompanyNum",list ==null? 0 : list.size());
        map.put("normalCompanyNum",list ==null? 0 : list.stream().filter(vo -> ObjectUtil.equal(1,vo.getIsCancel())).collect(Collectors.toList()).size());
        map.put("cancelCompanyNum",list ==null? 0 : list.stream().filter(vo -> ObjectUtil.equal(2,vo.getIsCancel())).collect(Collectors.toList()).size());
        map.put("companyList",list);
        return  ResultVo.Success(map);
    }

    /**
     * @Description 国金数据统计
     * @Author jiangni
     * @Date 2021/07/16 13:03
     * @return ResultVo<PageInfo>
     */
    @ApiOperation("国金数据统计")
    @PostMapping("/gjDataStatisByChannelUser")
    public ResultVo gjDataStatisByChannelUser(@RequestBody JSONObject jsonObject) {
        String oemCode = getRequestHeadParams("oemCode");
        if(StringUtils.isBlank(oemCode)){
            return ResultVo.Fail("云财OemCode不能为空！");
        }
        List<Long> channelDirectServicesUserIds = null;
        List<Long> channelFissionServicesUserIds = null;
        List<Long> channelDirectUserIds = null;
        List<Long> channelFissionUserIds = null;
        if(jsonObject.containsKey("channelDirectServicesUserIds") && jsonObject.getJSONArray("channelDirectServicesUserIds").size()>0){
            channelDirectServicesUserIds = jsonObject.getJSONArray("channelDirectServicesUserIds").toList(Long.class);
        }
        if(jsonObject.containsKey("channelFissionServicesUserIds") && jsonObject.getJSONArray("channelFissionServicesUserIds").size()>0){
            channelFissionServicesUserIds = jsonObject.getJSONArray("channelFissionServicesUserIds").toList(Long.class);
        }
        if(jsonObject.containsKey("channelDirectUserIds") && jsonObject.getJSONArray("channelDirectUserIds").size()>0){
            channelDirectUserIds = jsonObject.getJSONArray("channelDirectUserIds").toList(Long.class);
        }
        if(jsonObject.containsKey("channelFissionUserIds") && jsonObject.getJSONArray("channelFissionUserIds").size()>0){
            channelFissionUserIds = jsonObject.getJSONArray("channelFissionUserIds").toList(Long.class);
        }
        if(channelDirectServicesUserIds == null &&channelFissionServicesUserIds == null
                &&channelDirectUserIds == null && channelFissionUserIds == null){
            return ResultVo.Fail("参数不能为空");
        }

        Map<String,Object>  result = memberAccountService.gjDataStatisByChannelUser(channelDirectServicesUserIds,channelFissionServicesUserIds,channelDirectUserIds,channelFissionUserIds,oemCode);
        return ResultVo.Success(result);
    }
}
