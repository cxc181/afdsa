package com.yuqian.itax.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.dao.CompanyCancelOrderChangeRecordMapper;
import com.yuqian.itax.order.dao.CompanyCancelOrderMapper;
import com.yuqian.itax.order.dao.MemberOrderRelaMapper;
import com.yuqian.itax.order.dao.OrderMapper;
import com.yuqian.itax.order.entity.CompanyCancelOrderChangeRecordEntity;
import com.yuqian.itax.order.entity.CompanyCancelOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.query.TZBOrderQuery;
import com.yuqian.itax.order.entity.vo.ComCancelOrderVO;
import com.yuqian.itax.order.enums.CompCancelOrderStatusEnum;
import com.yuqian.itax.order.enums.WalletTypeEnum;
import com.yuqian.itax.order.service.CompanyCancelOrderService;
import com.yuqian.itax.order.service.MemberConsumptionRecordService;
import com.yuqian.itax.order.service.MemberOrderRelaService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.pay.dao.PayWaterMapper;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.tax.entity.CompanyTaxBillChangeEntity;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.query.PendingTaxBillQuery;
import com.yuqian.itax.tax.entity.vo.PendingTaxBillVO;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.ComCancelExtendDetailVO;
import com.yuqian.itax.user.entity.vo.ExtendMemberVO;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


@Service("companyCancelOrderService")
@Slf4j
public class CompanyCancelOrderServiceImpl extends BaseServiceImpl<CompanyCancelOrderEntity,CompanyCancelOrderMapper> implements CompanyCancelOrderService {

    @Resource
    private CompanyCancelOrderChangeRecordMapper companyCancelOrderChangeRecordMapper;

    @Resource
    private CompanyCancelOrderMapper companyCancelOrderMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private MemberOrderRelaMapper memberOrderRelaMapper;

    @Resource
    private PayWaterMapper payWaterMapper;

    @Autowired
    private UserCapitalAccountService userCapitalAccountService;

    @Autowired
    private MemberOrderRelaService memberOrderRelaService;

    @Autowired
    private MemberAccountService memberAccountService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private OemService oemService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    private ProfitsDetailService profitsDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberConsumptionRecordService memberConsumptionRecordService;

    @Autowired
    private CompanyTaxBillService companyTaxBillService;

    @Override
    @Transactional
    public void cancelOrder(OrderEntity entity, CompanyCancelOrderEntity companyCancelOrderEntity) {
        if (entity.getOrderStatus() > CompCancelOrderStatusEnum.TO_BE_PAY.getValue() && entity.getPayAmount() > 0) {
            //退款到余额
            orderService.refund(entity, entity.getUpdateUser(), entity.getUpdateTime(),2, false);
        }
        //修改我的企业状态
        memberCompanyService.updateStatus(companyCancelOrderEntity.getCompanyId(), MemberCompanyStatusEnum.NORMAL.getValue(), entity.getUpdateUser());
        //修改状态
        orderMapper.updateOrderStatus(entity.getOrderNo(), entity.getOrderStatus(), entity.getUpdateUser(), entity.getUpdateTime());
        //保存历史记录
        addCompanyCancelOrderChangeRecord(companyCancelOrderEntity, entity.getOrderStatus(), entity.getUpdateUser(), entity.getUpdateTime());
    }

    public void addCompanyCancelOrderChangeRecord(CompanyCancelOrderEntity companyCancelOrderEntity, Integer status, String addUser, Date addTime) {
        CompanyCancelOrderChangeRecordEntity record = new CompanyCancelOrderChangeRecordEntity();
        BeanUtils.copyProperties(companyCancelOrderEntity, record);
        record.setId(null);
        record.setUpdateTime(null);
        record.setUpdateUser(null);
        record.setOrderStatus(status);
        record.setAddTime(addTime);
        record.setAddUser(addUser);
        companyCancelOrderChangeRecordMapper.insertSelective(record);
    }

    @Override
    @Transactional
    public void cancelConfirm(OrderEntity entity, CompanyCancelOrderEntity companyCancelEntity, MemberCompanyEntity memberCompanyEntity,String account) {
        //保存企业注销订单
        companyCancelEntity.setUpdateTime(entity.getUpdateTime());
        companyCancelEntity.setUpdateUser(entity.getUpdateUser());
        mapper.updateByPrimaryKeySelective(companyCancelEntity);
        //保存历史记录
        addCompanyCancelOrderChangeRecord(companyCancelEntity, entity.getOrderStatus(), entity.getUpdateUser(), entity.getUpdateTime());
        //修改订单
        orderService.editByIdSelective(entity);
        //修改我的企业状态
        memberCompanyEntity.setStatus(MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue());
        memberCompanyEntity.setUpdateTime(new Date());
        memberCompanyEntity.setUpdateUser(account);
        memberCompanyService.editByIdSelective(memberCompanyEntity);
        //添加消费记录
        memberConsumptionRecordService.insertSelective(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), entity.getUserId(), entity.getPayAmount(), entity.getUpdateUser(), "企业注销订单确认已注销");
        //  为确认成本的税单改成已作废
        PendingTaxBillQuery query = new PendingTaxBillQuery();
        query.setEin(memberCompanyEntity.getEin());
        query.setCompanyId(companyCancelEntity.getCompanyId());
        query.setStatusRange(1);
        List<PendingTaxBillVO> pendingTaxBillList = companyTaxBillService.pendingTaxBill(query);
        if (CollectionUtil.isNotEmpty(pendingTaxBillList)){
            for (PendingTaxBillVO vo:pendingTaxBillList){
                CompanyTaxBillEntity companyTaxBillEntity = companyTaxBillService.findById(vo.getCompanyTaxBillId());
                companyTaxBillEntity.setTaxBillStatus(9);
                companyTaxBillEntity.setUpdateTime(new Date());
                companyTaxBillEntity.setUpdateUser(account);
                companyTaxBillService.editByIdSelective(companyTaxBillEntity);
                // 添加变更记录
                CompanyTaxBillChangeEntity changeEntity = new CompanyTaxBillChangeEntity();
                BeanUtils.copyProperties(companyTaxBillEntity, changeEntity);
                changeEntity.setId(null);
                changeEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
                changeEntity.setAddTime(new Date());
                changeEntity.setAddUser(account);
                changeEntity.setUpdateTime(null);
                changeEntity.setUpdateUser(null);
                changeEntity.setDescrip("企业注销成功，作废未确认成本税单");
            }
        }

        if (entity.getPayAmount() > 0) {
            //扣除自己资金
            userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), entity.getUserId(), 1, entity.getPayAmount(), 0L,0L, entity.getPayAmount(), "公司注销订单成功", entity.getUpdateUser(), entity.getUpdateTime(), 0, WalletTypeEnum.CONSUMER_WALLET.getValue());
            UserEntity oemUser = new UserEntity();
            oemUser.setOemCode(entity.getOemCode());
            oemUser.setPlatformType(2);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
            oemUser.setAccountType(1);//账号类型  1-管理员  2-城市合伙人 3-城市合伙人 4-坐席客服 5-财务 6-经办人 7-运营
            oemUser.setStatus(1);//状态 0-禁用 1-可用
            oemUser = userService.selectOne(oemUser);
            //给机构增加资金
            userCapitalAccountService.addBalanceByProfits(entity.getOemCode(), entity.getOrderNo(), entity.getOrderType(), oemUser.getId(), 2, entity.getPayAmount(),  entity.getPayAmount(),0L, 0L, "公司注销订单成功", entity.getUpdateUser(), entity.getUpdateTime(), 1, WalletTypeEnum.CONSUMER_WALLET.getValue());
            //分润
            try {
                profitsDetailService.saveProfitsDetailByOrderNo(entity.getOrderNo(), entity.getUpdateUser());
            }catch (Exception e){
                //分润失败
                entity.setIsShareProfit(2);
                entity.setProfitStatus(3);
                entity.setUpdateTime(new Date());
                entity.setUpdateUser("admin");
                entity.setRemark("分润失败原因："+e.getMessage());
                orderMapper.updateByPrimaryKeySelective(entity);

                // 短信通知紧急联系人
                DictionaryEntity dict = this.dictionaryService.getByCode("emergency_contact");
                if(null != dict){
                    String dicValue = dict.getDictValue();
                    String[] contacts = dicValue.split(",");
                    for(String contact : contacts){
                        Map<String,Object> map = new HashMap();
                        map.put("oemCode",entity.getOemCode());
                        map.put("orderNo",entity.getOrderNo());
                        this.smsService.sendTemplateSms(contact,entity.getOemCode(), VerifyCodeTypeEnum.NOTICE.getValue(), map,1);
                        log.info("分润失败发送通知给【" + contact + "】成功");
                    }
                }
            }
        }

        MemberAccountEntity accEntity = memberAccountService.findById(entity.getUserId());
        if (accEntity == null) {
            throw new BusinessException(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        OemEntity oem = oemService.getOem(entity.getOemCode());
        if (oem == null) {
            throw new BusinessException("机构不存在");
        }
        //发送短信给用户
        Map<String, Object> map = Maps.newHashMap();
        map.put("compName", memberCompanyEntity.getCompanyName());
        smsService.sendTemplateSms(accEntity.getMemberPhone(), accEntity.getOemCode(), VerifyCodeTypeEnum.COMPANY_CANCEL_USER.getValue(), map, 2);
        //发送短信给经营者
        //是否为他人办理 0-本人办理 1-为他人办理
        if(Objects.equals(memberCompanyEntity.getIsOther(),1)){
            map.put("serviceTel", oem.getCustomerServiceTel());
            smsService.sendTemplateSms(memberCompanyEntity.getOperatorTel(), memberCompanyEntity.getOemCode(), VerifyCodeTypeEnum.COMPANY_CANCEL_OPERATOR.getValue(), map, 2);
        }
        //日统计
        orderService.statisticsMemberGeneralize(entity, entity.getUpdateUser(), 1);
    }

    @Override
    public PageResultVo<ComCancelOrderVO> queryComCancelOrder(TZBOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());

        List<ComCancelOrderVO> list = this.companyCancelOrderMapper.queryComCancelOrderList(query);
        PageInfo<ComCancelOrderVO> pageInfo = new PageInfo<ComCancelOrderVO>(list);

        PageResultVo<ComCancelOrderVO> result = new PageResultVo<ComCancelOrderVO>();
        result.setList(pageInfo.getList());
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setPageSize(query.getPageSize());
        result.setPageNum(query.getPageNumber());
        result.setOrderBy("createTime DESC");
        return result;
    }

    @Override
    public ComCancelExtendDetailVO queryComCancelStat(MemberExtendQuery query) {
        log.info("查询企业注销统计信息：{}", JSON.toJSONString(query));

        MemberAccountEntity  member = this.memberAccountService.findById(query.getUserId());
        if(null == member){
            throw new BusinessException("查询失败，" + ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }

        ComCancelExtendDetailVO detail = this.companyCancelOrderMapper.queryComCancelStat(query);
        return detail;
    }
    @Override
    public ComCancelExtendDetailVO queryComCancelStatByChannelServiceId(MemberExtendQuery query) {
        log.info("查询企业注销统计信息：{}", JSON.toJSONString(query));
        ComCancelExtendDetailVO detail = this.companyCancelOrderMapper.queryComCancelStatByChannelServiceId(query);
        return detail;
    }

    @Override
    public List<ExtendMemberVO> queryCancelOrderListByStatus(MemberExtendQuery query) {
        log.info("查询企业注销订单列表：{}",JSON.toJSONString(query));
        List<ExtendMemberVO> list = this.companyCancelOrderMapper.queryCancelOrderListByStatus(query);
        return list;
    }
    @Override
    public List<ExtendMemberVO> queryCancelOrderListByStatusByChannelServiceId(MemberExtendQuery query) {
        log.info("查询企业注销订单列表：{}",JSON.toJSONString(query));
        List<ExtendMemberVO> list = this.companyCancelOrderMapper.queryCancelOrderListByStatusByChannelServiceId(query);
        return list;
    }
    @Override
    public Long queryTotalCancelFee(MemberExtendQuery query) {
        log.info("查询企业注销订单列表：{}",JSON.toJSONString(query));
        return this.companyCancelOrderMapper.queryTotalCancelFee(query);
    }
    @Override
    public Long queryTotalCancelFeeByChannelServiceId(MemberExtendQuery query) {
        log.info("查询企业注销订单列表：{}",JSON.toJSONString(query));
        return this.companyCancelOrderMapper.queryTotalCancelFeeByChannelServiceId(query);
    }

    @Override
    public List<ComCancelOrderVO> queryByCompanyId(Long companyId) {
        return mapper.queryByCompanyId(companyId);
    }
}

