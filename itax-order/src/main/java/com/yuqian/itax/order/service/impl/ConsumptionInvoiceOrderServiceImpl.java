package com.yuqian.itax.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.InvoiceInfoByOemEntity;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.InvoiceInfoByOemService;
import com.yuqian.itax.agent.service.OemConfigService;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.dao.ConsumptionInvoiceOrderMapper;
import com.yuqian.itax.order.entity.*;
import com.yuqian.itax.order.entity.dto.ConsumptionInvOrderDTO;
import com.yuqian.itax.order.entity.query.ConsumptionInvoiceOrderQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.order.enums.*;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.system.entity.InvoiceCategoryBaseEntity;
import com.yuqian.itax.system.service.InvoiceCategoryBaseService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.FileUtil;
import com.yuqian.itax.util.util.OrderNoFactory;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.Asserts;
import org.assertj.core.util.Lists;
import org.bouncycastle.asn1.crmf.OptionalValidity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Optionals;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service("consumptionInvoiceOrderService")
public class ConsumptionInvoiceOrderServiceImpl extends BaseServiceImpl<ConsumptionInvoiceOrderEntity,ConsumptionInvoiceOrderMapper> implements ConsumptionInvoiceOrderService {

    @Autowired
    SmsService smsService;
    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Override
    public PageInfo<ConsumptionInvoiceOrderVO> invoicePage(ConsumptionInvoiceOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(mapper.queryInvoiceList(query));
    }

    @Override
    public List<ConsumptionInvoiceOrderVO> invoiceList(ConsumptionInvoiceOrderQuery query) {
        return mapper.queryInvoiceList(query);
    }
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InvoiceHeadService invoiceHeadService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private MemberOrderRelaService memberOrderRelaService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    private ConsumptionInvoiceOrderChangeService consumptionInvoiceOrderChangeService;
    @Autowired
    private DaifuApiService daifuApiService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private MemberConsumptionRecordService memberConsumptionRecordService;
    @Autowired
    private OemConfigService oemConfigService;
    @Autowired
    private InvoiceCategoryBaseService invoiceCategoryBaseService;
    @Autowired
    private InvoiceInfoByOemService invoiceInfoByOemService;
    @Autowired
    private InvoiceRecordService invoiceRecordService;
    @Autowired
    private OssService ossService;
    @Autowired
    private MemberAddressService memberAddressService;
    @Autowired
    private ConsumptionInvoiceOrderService consumptionInvoiceOrderService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createOrder(ConsumptionInvOrderDTO dto) throws BusinessException {
        log.info("创建消费开票订单开始：{}", JSON.toJSONString(dto));
        // 校验当前登录人
        MemberAccountEntity member = this.memberAccountService.findById(dto.getCurrUserId());
        if(null == member){
            throw new BusinessException("当前登录用户不存在");
        }

        // 校验订单
        String[] orderNos = dto.getConsumptionOrderRela().split(",");
        if(orderNos.length > 0){
            for(int i = 0 ; i < orderNos.length; i++){
                OrderEntity order = this.orderService.queryByOrderNo(orderNos[i]);
                if(null == order){
                    throw new BusinessException("订单号【" + orderNos[i] + "】不存在");
                }
                // 检查消费订单是否已开过票
                MemberConsumptionRecordEntity t = new MemberConsumptionRecordEntity();
                t.setOrderNo(orderNos[i]);
                MemberConsumptionRecordEntity record = this.memberConsumptionRecordService.selectOne(t);
                if(null != record && record.getIsOpenInvoice().intValue() == 1){
                    throw new BusinessException("所选订单存在已经开过票的记录，请检查");
                }
            }
        }
        // 校验发票抬头
        InvoiceHeadEntity invoiceHead = this.invoiceHeadService.findById(dto.getInvoiceHeadId());
        if(null == invoiceHead){
            throw new BusinessException("发票抬头不存在");
        }

        // 开电票参数校验
        if (null != dto.getInvoiceWay() && InvoiceWayEnum.ELECTRON.getValue().equals(dto.getInvoiceWay())) {
            // 邮箱不能为空
            if (StringUtil.isBlank(dto.getBillToEmail())) {
                throw new BusinessException("收票邮箱不能为空");
            }
        }

        MemberAddressEntity memberAddress = new MemberAddressEntity();
        // 开纸票参数校验
        if (null != dto.getInvoiceWay() && InvoiceWayEnum.PAPER.getValue().equals(dto.getInvoiceWay())) {
            // 一般纳税人资质证明不能为空
            if (StringUtil.isBlank(dto.getGeneralTaxpayerQualification())) {
                throw new BusinessException("一般纳税人资质证明不能为空");
            }
            // 校验一般纳税人资质证明是否已上传
            boolean existPrivate = ossService.doesObjectExistPrivate(dto.getGeneralTaxpayerQualification());
            if (!existPrivate) {
                throw new BusinessException("一般纳税人资质证明未上传");
            }
            // 校验会员收件地址
            if (null == dto.getMemberAddressId()) {
                throw new BusinessException("会员收件地址不能为空");
            }
            memberAddress.setId(dto.getMemberAddressId());
            memberAddress.setOemCode(member.getOemCode());
            memberAddress.setMemberId(member.getId());
            memberAddress = Optional.ofNullable(this.memberAddressService.selectOne(memberAddress)).orElseThrow(() -> new BusinessException("会员收件地址不存在"));
            // 发票抬头必填项校验
            if (StringUtil.isBlank(invoiceHead.getCompanyAddress())) {
                throw new BusinessException("抬头公司地址为空");
            }
            if (StringUtil.isBlank(invoiceHead.getPhone())) {
                throw new BusinessException("抬头电话为空");
            }
            if (StringUtil.isBlank(invoiceHead.getBankName())) {
                throw new BusinessException("抬头开户银行为空");
            }
            if (StringUtil.isBlank(invoiceHead.getBankNumber())) {
                throw new BusinessException("抬头银行账户为空");
            }
        }

        log.info("校验完成，开始创建订单：{}",dto);
        String orderNo = createConsumptionInvOrder(dto,member,invoiceHead,memberAddress);
        log.info("消费开票订单创建完成:{}",orderNo);
        return orderNo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createConsumptionInvOrder(ConsumptionInvOrderDTO dto,MemberAccountEntity member,InvoiceHeadEntity invoiceHead, MemberAddressEntity memberAddress) {
        // 保存订单主表
        String orderNo = OrderNoFactory.getOrderCode(dto.getCurrUserId()); // 生成订单号
        // 保存订单主表信息
        OrderEntity order = new OrderEntity();
        order.setOemCode(dto.getOemCode());
        order.setOrderNo(orderNo);
        order.setOrderAmount(dto.getTotalAmount());
        order.setPayAmount(dto.getTotalAmount());
        order.setProfitAmount(0L);
        order.setAuditStatus(0);// 默认审核状态为0 待审核
        order.setOrderType(OrderTypeEnum.CONSUMPTION_INVOICE.getValue());
        order.setWalletType(WalletTypeEnum.OTHER.getValue());
        order.setAddUser(member.getMemberAccount());
        order.setSourceType(dto.getSourceType());
        order.setOrderStatus(ChannelPushStateEnum.TO_BE_PAY.getValue());
        order.setChannelProductCode(member.getChannelProductCode());
        order.setChannelCode(member.getChannelCode());
        order.setChannelEmployeesId(member.getChannelEmployeesId());
        order.setChannelServiceId(member.getChannelServiceId());
        order.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        order.setChannelUserId(member.getChannelUserId());
        // 补全订单参数
        completionParameter(member, order);
        orderService.insertSelective(order);
        log.info("消费开票订单主表保存成功。。");

        // 生成消费开票订单
        ConsumptionInvoiceOrderEntity consumptionIvnOrder = new ConsumptionInvoiceOrderEntity();
        BeanUtils.copyProperties(invoiceHead,consumptionIvnOrder);
        BeanUtils.copyProperties(memberAddress, consumptionIvnOrder);
        // 查询开票类目
        OemConfigEntity t = new OemConfigEntity();
        t.setOemCode(dto.getOemCode());
        t.setParamsCode("consumption_invoice_category");
        OemConfigEntity config = this.oemConfigService.selectOne(t);
        if(null != config){
            String categoryName = config.getParamsValue();
            consumptionIvnOrder.setCategoryName(categoryName);
            String[] categoryNames = categoryName.split("\\*");
            InvoiceCategoryBaseEntity entity = new InvoiceCategoryBaseEntity();
            entity.setTaxClassificationAbbreviation(categoryNames[0]);
            entity.setGoodsName(categoryNames[1]);
            List<InvoiceCategoryBaseEntity> list = invoiceCategoryBaseService.select(entity);
            if(list!=null && list.size()>0){
                consumptionIvnOrder.setCategoryId(list.get(0).getId());
            }
        }
        consumptionIvnOrder.setOrderNo(orderNo);
        consumptionIvnOrder.setConsumptionOrderRela(dto.getConsumptionOrderRela());
        consumptionIvnOrder.setInvoiceAmount(dto.getTotalAmount());
        consumptionIvnOrder.setOemCode(dto.getOemCode());
        // 根据发票方式 开电票默认为普票类型，开纸票默认为专票类型
        if (null != dto.getInvoiceWay() && InvoiceWayEnum.ELECTRON.getValue().equals(dto.getInvoiceWay())) {
            consumptionIvnOrder.setInvoiceType(InvoiceTypeEnum.UPGRADE.getValue());
            consumptionIvnOrder.setInvoiceTypeName(InvoiceTypeEnum.UPGRADE.getMessage());
            consumptionIvnOrder.setInvoiceWay(InvoiceWayEnum.ELECTRON.getValue());
        } else if (null != dto.getInvoiceWay() && InvoiceWayEnum.PAPER.getValue().equals(dto.getInvoiceWay())) {
            consumptionIvnOrder.setInvoiceType(InvoiceTypeEnum.REGISTER.getValue());
            consumptionIvnOrder.setInvoiceTypeName(InvoiceTypeEnum.REGISTER.getMessage());
            consumptionIvnOrder.setGeneralTaxpayerQualification(dto.getGeneralTaxpayerQualification());
            consumptionIvnOrder.setInvoiceWay(InvoiceWayEnum.PAPER.getValue());
        }
        consumptionIvnOrder.setBillToEmail(dto.getBillToEmail());
        consumptionIvnOrder.setAddTime(new Date());
        consumptionIvnOrder.setAddUser(member.getMemberAccount());
        consumptionIvnOrder.setId(null);
        this.insertSelective(consumptionIvnOrder);
        log.info("消费开票订单保存成功。。");

        // 保存消费开票订单变更记录
        ConsumptionInvoiceOrderChangeEntity changeEntity = new ConsumptionInvoiceOrderChangeEntity();
        BeanUtils.copyProperties(consumptionIvnOrder,changeEntity);
        changeEntity.setId(null);
        changeEntity.setAddTime(new Date());
        this.consumptionInvoiceOrderChangeService.insertSelective(changeEntity);
        log.info("消费开票订单变更记录保存成功。。");

        // 更新会员消费记录（是否已开票更新为1）
        this.memberConsumptionRecordService.updateRecordByOrderNo(dto.getConsumptionOrderRela());
        log.info("更新会员消费记录成功。。");

        //创建开票记录
        createConsumptionInvoiceRecord(consumptionIvnOrder.getOrderNo(),consumptionIvnOrder.getInvoiceAmount(),member.getMemberAccount());
        return orderNo;
    }

    /**
     * 创建开票记录
     * @param orderNo
     * @param invoiceAmount
     * @param addUser
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createConsumptionInvoiceRecord(String orderNo,Long invoiceAmount,String addUser){
        if(StringUtils.isBlank(orderNo)){
            throw new BusinessException("订单号不存在");
        }
        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if(orderEntity == null){
            throw new BusinessException("未找到订单信息");
        }

        InvoiceOrderEntity invoiceOrderEntity = new InvoiceOrderEntity();
        ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity = new ConsumptionInvoiceOrderEntity();
        consumptionInvoiceOrderEntity.setOemCode(orderEntity.getOemCode());
        consumptionInvoiceOrderEntity.setOrderNo(orderEntity.getOrderNo());
        consumptionInvoiceOrderEntity = this.selectOne(consumptionInvoiceOrderEntity);
        if(consumptionInvoiceOrderEntity == null){
            throw new BusinessException("未找到消费开票订单信息");
        }
        BeanUtil.copyProperties(consumptionInvoiceOrderEntity,invoiceOrderEntity);
        invoiceOrderEntity.setEmail(consumptionInvoiceOrderEntity.getBillToEmail());
        invoiceOrderEntity.setCreateWay(4);

        InvoiceInfoByOemEntity invoiceInfoByOemEntity = new InvoiceInfoByOemEntity();
        invoiceInfoByOemEntity.setOemCode(orderEntity.getOemCode());
        invoiceInfoByOemEntity = invoiceInfoByOemService.selectOne(invoiceInfoByOemEntity);
        if(invoiceInfoByOemEntity == null){
            throw new BusinessException("未找到消费开票配置信息");
        }
        invoiceOrderEntity.setVatFeeRate(invoiceInfoByOemEntity.getVatRate().divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_UP));
        invoiceOrderEntity.setInvoiceAmount(invoiceAmount);

        CompanyTaxHostingEntity companyTaxHostingEntity = null;
        if(invoiceInfoByOemEntity.getHostingStatus().intValue() == 1) {
            companyTaxHostingEntity = new CompanyTaxHostingEntity();
            BeanUtil.copyProperties(invoiceInfoByOemEntity, companyTaxHostingEntity);
        }
        boolean isImmediatelyInvoice = false;
        if(invoiceInfoByOemEntity.getIsImmediatelyInvoice().intValue() == 1){
            isImmediatelyInvoice = true;
        }
        invoiceRecordService.createInvoiceRecord(invoiceOrderEntity,companyTaxHostingEntity,invoiceInfoByOemEntity.getEin(),null, orderEntity.getUserId(), addUser,isImmediatelyInvoice);
    }

    @Override
    public void applyInvoice(Long id,String account) {
        ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity=mapper.selectByPrimaryKey(id);
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(consumptionInvoiceOrderEntity.getOemCode(),15);
        if(null == paramsEntity){
            throw new BusinessException("未配置渠道代付相关信息！");
        }
        JSONObject oemParamsjsonObject =  JSONObject.parseObject(paramsEntity.getParamsValues());
        String clerk = oemParamsjsonObject.getString("clerk");
        Double taxRate = oemParamsjsonObject.getDouble("taxRate");//税率
        String name = oemParamsjsonObject.getString("name");//商品名称
        String taxCode = oemParamsjsonObject.getString("taxCode"); ////税收分类编码
        String priceTaxFlag = oemParamsjsonObject.getString("priceTaxFlag"); ////单价含税标志

        //设置开票参数
        Map<String,Object> map=new HashMap<>();
        map.put("orderNo",consumptionInvoiceOrderEntity.getOrderNo());
        map.put("type","01");//只开蓝票
        map.put("companyName",consumptionInvoiceOrderEntity.getCompanyName());
        map.put("amt",consumptionInvoiceOrderEntity.getInvoiceAmount());
        BigDecimal bg = BigDecimal.valueOf(consumptionInvoiceOrderEntity.getInvoiceAmount()/(1+taxRate)*(taxRate)).setScale(0, BigDecimal.ROUND_HALF_UP);
        Double tax= bg.doubleValue();//不含税金额
        Double taxfreeAmt=BigDecimal.valueOf(consumptionInvoiceOrderEntity.getInvoiceAmount().doubleValue()-tax).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();//税额
        map.put("taxfreeAmt",taxfreeAmt);//不含税总金额
        map.put("buyer",consumptionInvoiceOrderEntity.getCompanyName());//购买方名称
        map.put("taxnum",consumptionInvoiceOrderEntity.getEin());//购方纳税识别号
        map.put("email",consumptionInvoiceOrderEntity.getBillToEmail());//接受发票pdf电子邮箱
        map.put("clerk",clerk);//开票员
        map.put("billType",1);//开票类型1-正票，2-红票（暂时只开正票）
        List<Map<String, Object>> details = new ArrayList<>();
        Map<String,Object> detail=new HashMap<>();
        detail.put("name",name);//商品名称
        detail.put("taxCode",taxCode);//税收分类编码
        detail.put("taxFreeAmt",taxfreeAmt);//不含税金额
        detail.put("tax",tax);//税额
        detail.put("amt",consumptionInvoiceOrderEntity.getInvoiceAmount());//含税金额
        detail.put("priceTaxFlag",priceTaxFlag);//单价含税标志
        detail.put("taxRate",taxRate);//税率
        details.add(detail);
        map.put("details",details);//商品明细列表
        JSONObject jsonObject=daifuApiService.applyInvoice(paramsEntity,map);
        if(jsonObject!=null){
            JSONObject object=jsonObject.getJSONObject("data");
            String code=jsonObject.getString("code");
            /*if(!"00".equals(code)){
                //订单设置失败
                //修改订单状态为出票失败
                orderService.updateOrderStatus(account, consumptionInvoiceOrderEntity.getOrderNo(), 3);
                // 保存消费开票订单变更记录
                ConsumptionInvoiceOrderChangeEntity changeEntity = new ConsumptionInvoiceOrderChangeEntity();
                BeanUtils.copyProperties(consumptionInvoiceOrderEntity,changeEntity);
                changeEntity.setStatus(3);
                changeEntity.setId(null);
                changeEntity.setAddTime(new Date());
                changeEntity.setAddUser(account);
                this.consumptionInvoiceOrderChangeService.insertSelective(changeEntity);
                //释放关联消费发票
                String consumptionOrderRela=consumptionInvoiceOrderEntity.getConsumptionOrderRela();
                String []consumptionOrderRelas=consumptionOrderRela.split(",");
                for (String orderNo:consumptionOrderRelas) {
                    MemberConsumptionRecordEntity entity=new MemberConsumptionRecordEntity();
                    entity.setOrderNo(orderNo);
                    MemberConsumptionRecordEntity memberConsumptionRecordEntity=memberConsumptionRecordService.selectOne(entity);
                    memberConsumptionRecordEntity.setIsOpenInvoice(0);
                    memberConsumptionRecordEntity.setUpdateTime(new Date());
                    memberConsumptionRecordEntity.setUpdateUser("admin-xxjob");
                    memberConsumptionRecordService.editByIdSelective(memberConsumptionRecordEntity);
                }
                //记录失败原因
                consumptionInvoiceOrderEntity.setRemark( object.getString("bizCodeMsg"));
                consumptionInvoiceOrderEntity.setUpdateUser("admin-xxjob");
                consumptionInvoiceOrderEntity.setUpdateTime(new Date());
                mapper.updateByPrimaryKey(consumptionInvoiceOrderEntity);
                throw new BusinessException(object.getString("bizCodeMsg"));
            }*/
            if(object!=null){
                if("00".equals(object.getString("bizCode"))||"1001".equals(object.getString("bizCode"))||"8005".equals(object.getString("bizCode"))||"1018".equals(object.getString("bizCode"))){
                    //修改订单状态为出票中
                    orderService.updateOrderStatus(account, consumptionInvoiceOrderEntity.getOrderNo(), 1);
                    // 保存消费开票订单变更记录
                    ConsumptionInvoiceOrderChangeEntity changeEntity = new ConsumptionInvoiceOrderChangeEntity();
                    BeanUtils.copyProperties(consumptionInvoiceOrderEntity,changeEntity);
                    changeEntity.setStatus(1);
                    changeEntity.setId(null);
                    changeEntity.setAddTime(new Date());
                    changeEntity.setAddUser(account);
                    this.consumptionInvoiceOrderChangeService.insertSelective(changeEntity);
                    //记录原因
                    consumptionInvoiceOrderEntity.setRemark( object.getString("bizCodeMsg"));
                    consumptionInvoiceOrderEntity.setUpdateUser("admin-xxjob");
                    consumptionInvoiceOrderEntity.setUpdateTime(new Date());
                    mapper.updateByPrimaryKey(consumptionInvoiceOrderEntity);
                }else{
                    //2.4暂定不处理
                    /*//订单设置失败
                    //修改订单状态为出票失败
                    orderService.updateOrderStatus(account, consumptionInvoiceOrderEntity.getOrderNo(), 3);
                    // 保存消费开票订单变更记录
                    ConsumptionInvoiceOrderChangeEntity changeEntity = new ConsumptionInvoiceOrderChangeEntity();
                    BeanUtils.copyProperties(consumptionInvoiceOrderEntity,changeEntity);
                    changeEntity.setStatus(3);
                    changeEntity.setId(null);
                    changeEntity.setAddTime(new Date());
                    changeEntity.setAddUser(account);
                    this.consumptionInvoiceOrderChangeService.insertSelective(changeEntity);
                    //释放关联消费发票
                    String consumptionOrderRela=consumptionInvoiceOrderEntity.getConsumptionOrderRela();
                    String []consumptionOrderRelas=consumptionOrderRela.split(",");
                    for (String orderNo:consumptionOrderRelas) {
                        MemberConsumptionRecordEntity entity=new MemberConsumptionRecordEntity();
                        entity.setOrderNo(orderNo);
                        MemberConsumptionRecordEntity memberConsumptionRecordEntity=memberConsumptionRecordService.selectOne(entity);
                        memberConsumptionRecordEntity.setIsOpenInvoice(0);
                        memberConsumptionRecordEntity.setUpdateTime(new Date());
                        memberConsumptionRecordEntity.setUpdateUser("admin-xxjob");
                        memberConsumptionRecordService.editByIdSelective(memberConsumptionRecordEntity);
                    }*/
                    //记录失败原因
                    consumptionInvoiceOrderEntity.setRemark( object.getString("bizCodeMsg"));
                    consumptionInvoiceOrderEntity.setUpdateUser("admin-xxjob");
                    consumptionInvoiceOrderEntity.setUpdateTime(new Date());
                    mapper.updateByPrimaryKey(consumptionInvoiceOrderEntity);

                    throw new BusinessException(object.getString("bizCodeMsg"));
                }
            }else{
                //修改订单状态为出票中
                orderService.updateOrderStatus(account, consumptionInvoiceOrderEntity.getOrderNo(), 1);
                // 保存消费开票订单变更记录
                ConsumptionInvoiceOrderChangeEntity changeEntity = new ConsumptionInvoiceOrderChangeEntity();
                BeanUtils.copyProperties(consumptionInvoiceOrderEntity,changeEntity);
                changeEntity.setStatus(1);
                changeEntity.setId(null);
                changeEntity.setAddTime(new Date());
                changeEntity.setAddUser(account);
                this.consumptionInvoiceOrderChangeService.insertSelective(changeEntity);

                //记录原因
                consumptionInvoiceOrderEntity.setRemark("返回为空");
                consumptionInvoiceOrderEntity.setUpdateUser("admin-xxjob");
                consumptionInvoiceOrderEntity.setUpdateTime(new Date());
                mapper.updateByPrimaryKey(consumptionInvoiceOrderEntity);
            }
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyInvoiceFail(Long id, String account,String remark) {

        ConsumptionInvoiceOrderEntity  consumptionInvoiceOrderEntity=mapper.selectByPrimaryKey(id);
        OrderEntity orderEntity=orderService.queryByOrderNo(consumptionInvoiceOrderEntity.getOrderNo());
//        if(orderEntity.getOrderStatus()!=0){
//            throw  new BusinessException("只有待出票得订单可以出票失败,请确认");
//        }
        //释放关联消费发票
        String consumptionOrderRela=consumptionInvoiceOrderEntity.getConsumptionOrderRela();
        String []consumptionOrderRelas=consumptionOrderRela.split(",");
        for (String orderNo:consumptionOrderRelas) {
            MemberConsumptionRecordEntity entity=new MemberConsumptionRecordEntity();
            entity.setOrderNo(orderNo);
            MemberConsumptionRecordEntity memberConsumptionRecordEntity=memberConsumptionRecordService.selectOne(entity);
            memberConsumptionRecordEntity.setIsOpenInvoice(0);
            memberConsumptionRecordEntity.setUpdateTime(new Date());
            memberConsumptionRecordEntity.setUpdateUser(account);
            memberConsumptionRecordService.editByIdSelective(memberConsumptionRecordEntity);
        }
        //修改订单状态为出票失败
        orderService.updateOrderStatus(account, consumptionInvoiceOrderEntity.getOrderNo(), 3);
        // 保存消费开票订单变更记录
        ConsumptionInvoiceOrderChangeEntity changeEntity = new ConsumptionInvoiceOrderChangeEntity();
        BeanUtils.copyProperties(consumptionInvoiceOrderEntity,changeEntity);
        changeEntity.setStatus(3);
        changeEntity.setId(null);
        changeEntity.setAddTime(new Date());
        changeEntity.setAddUser(account);
        this.consumptionInvoiceOrderChangeService.insertSelective(changeEntity);

        consumptionInvoiceOrderEntity.setRemark(remark);
        consumptionInvoiceOrderEntity.setUpdateUser(account);
        consumptionInvoiceOrderEntity.setUpdateTime(new Date());
        mapper.updateByPrimaryKey(consumptionInvoiceOrderEntity);
        //发送短信
        MemberAccountEntity memberAccountEntity=memberAccountService.findById(orderEntity.getUserId());
        Map<String, Object> map = new HashMap();
        map.put("msg", remark);
        this.smsService.sendTemplateSms(memberAccountEntity.getMemberPhone(), consumptionInvoiceOrderEntity.getOemCode(), VerifyCodeTypeEnum.INVOICE_APPLY_FAIL.getValue(), map, 1);
        log.info("开票查询失败发送通知给【" + memberAccountEntity.getMemberPhone() + "】成功");
    }

    @Override
    public List<ConsumptionInvoiceOrderPageVO> findConsumptionInvoiceOrderList(Long memberId, String oemCode) throws BusinessException {
        return mapper.findConsumptionInvoiceOrderList(memberId, oemCode);
    }

    @Override
    public ConsumptionInvoiceOrderDetailVO getDetailByOrderNo(Long memberId, String oemCode, String orderNo) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }
        ConsumptionInvoiceOrderDetailVO entity =  mapper.getDetailByOrderNo(memberId, oemCode, orderNo);
        if(entity == null){
            throw new BusinessException("未找到对应的消费开票订单");
        }
        if(StringUtils.isNotBlank(entity.getInvoiceImgs())){
            String[] imgs = entity.getInvoiceImgs().split(",");
            for(int a = 0;a <imgs.length;a++){
                imgs[a] = ossService.getPrivateImgUrl(imgs[a]);
            }
            entity.setInvoiceImgList(imgs);
        }else  if(StringUtils.isNotBlank(entity.getInvoicePdfUrl())){
            String[] imgs = new String[1];
            imgs[0] = FileUtil.pdfUrl2pngBase64(entity.getInvoicePdfUrl());
            entity.setInvoiceImgList(imgs);
        }
        return entity;
    }

    @Override
    public List<ConsumptionRelaOrderVO> findConsumptionRelaOrderList(Long memberId, String oemCode, String consumptionOrderRela) throws BusinessException {
        return mapper.findConsumptionRelaOrderList(memberId, oemCode, consumptionOrderRela);
    }

    @Override
    public Map<String, Object> checkTheLogistics(String orderNo, Long currUserId) {
        // 查询当前登录用户
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(currUserId)).orElseThrow(() -> new BusinessException("未查询到用户信息"));

        // 查询订单
        Optional.ofNullable(orderService.queryByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到订单信息"));

        // 查询消费开票订单
        ConsumptionInvoiceOrderEntity entity = new ConsumptionInvoiceOrderEntity();
        entity.setOrderNo(orderNo);
        entity = Optional.ofNullable(this.selectOne(entity)).orElseThrow(() -> new BusinessException("未查询到消费开票订单"));

        Map<String, Object> map = new HashMap<>();
        List<LogisticsInfoVO> list = Lists.newArrayList();
        map.put("courierCompanyName", entity.getCourierCompanyName());
        map.put("courierNumber", entity.getCourierNumber());

        // 查询物流信息
        List<LogisticsInfoEntity> logisticsInfoEntities = logisticsInfoService.queryLogisticsInfoList(entity.getCourierCompanyName(), entity.getCourierNumber(), orderNo, member.getMemberAccount());
        if (null == logisticsInfoEntities || logisticsInfoEntities.isEmpty()) {
            map.put("logistics", list);
            return map;
        }

        // 封装为返回对象
        for (LogisticsInfoEntity logisticsInfo : logisticsInfoEntities) {
            LogisticsInfoVO logisticsInfoVO = new LogisticsInfoVO();
            logisticsInfoVO.setContext(logisticsInfo.getLogisticsInfo());
            logisticsInfoVO.setTime(DateUtil.formatTimesTampDate(logisticsInfo.getLogisticsTime()));
            list.add(logisticsInfoVO);
        }
        map.put("logistics", list);
        return map;
    }

    @Override
    public ConsumptionInvoiceReceivingVO getReceivingInfoById(Long id) {
        return mapper.getReceivingInfoById(id);
    }

    @Override
    @Transactional
    public void updateConsumptionInvoiceOrder(Long id, String courierNumber, String courierCompanyName, String userName) {
        ConsumptionInvoiceOrderEntity  consumptionInvoiceOrderEntity  =consumptionInvoiceOrderService.findById(id);
        if (consumptionInvoiceOrderEntity == null){
            throw new BusinessException("未找到消费开票订单信息");
        }
        consumptionInvoiceOrderEntity.setCourierNumber(courierNumber);
        consumptionInvoiceOrderEntity.setCourierCompanyName(courierCompanyName);
        consumptionInvoiceOrderEntity.setUpdateTime(new Date());
        consumptionInvoiceOrderEntity.setUpdateUser(userName);
        ConsumptionInvoiceOrderChangeEntity consumptionInvoiceOrderChangeEntity=new ConsumptionInvoiceOrderChangeEntity();
        BeanUtils.copyProperties(consumptionInvoiceOrderEntity,consumptionInvoiceOrderChangeEntity);
        consumptionInvoiceOrderChangeEntity.setStatus(5);
        consumptionInvoiceOrderChangeEntity.setId(null);
        consumptionInvoiceOrderChangeEntity.setAddUser(userName);
        consumptionInvoiceOrderChangeEntity.setAddTime(new Date());
        consumptionInvoiceOrderChangeEntity.setUpdateTime(null);
        consumptionInvoiceOrderChangeEntity.setUpdateUser(null);
        consumptionInvoiceOrderChangeService.add(consumptionInvoiceOrderChangeEntity);
        //修改订单状态为待签收
        orderService.updateOrderStatus(userName, consumptionInvoiceOrderEntity.getOrderNo(), 5);
        consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);
    }

    @Override
    public List<ConsumptionInvoiceOrderJobVO> listConsumptionInvoiceByStatus() {
        return mapper.listConsumptionInvoiceByStatus();
    }

    /**
     * @Description 订单主表参数补全
     * @Author Kaven
     * @Date 2020/9/27 14:41
     * @Param MemberAccountEntity OrderEntity
     * @Return
     * @Exception
     */
    private void completionParameter(MemberAccountEntity member, OrderEntity order) {
        // 充值提现不需要保存会员订单关系
        MemberOrderRelaEntity more = this.invoiceOrderService.getUserTree(member.getId(), order.getOemCode(), 7);// 消费开票订单，暂定7
        if (more != null) {
            more.setMemberId(member.getId());
            more.setOemCode(order.getOemCode());
            more.setAddTime(new Date());
            // 设置会员等级
            more.setAddUser(member.getMemberAccount());
            MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());
            more.setMemberLevel(level.getLevelNo());
            this.memberOrderRelaService.insertSelective(more);
        }

        // 补全订单主表信息
        order.setUserId(member.getId());
        order.setUserType(1);
        if (more != null) {
            order.setRelaId(more.getId());
        }
        order.setAddUser(member.getMemberAccount());
        order.setAddTime(new Date());
        order.setOrderStatus(ConsumtionInvOrderStatusEnum.TICKETING.getValue());// 默认出票中
    }
}

