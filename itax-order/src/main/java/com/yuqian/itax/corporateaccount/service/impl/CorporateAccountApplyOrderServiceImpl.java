package com.yuqian.itax.corporateaccount.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.corporateaccount.dao.CorporateAccountApplyOrderMapper;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountApplyOrderChangeEntity;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountApplyOrderEntity;
import com.yuqian.itax.corporateaccount.entity.vo.CorporateAccountApplyOrderVO;
import com.yuqian.itax.corporateaccount.enums.CorporateAccountApplyOrderStatusEnum;
import com.yuqian.itax.corporateaccount.query.CorporateAccountApplyOrderQuery;
import com.yuqian.itax.corporateaccount.service.CorporateAccountApplyOrderChangeService;
import com.yuqian.itax.corporateaccount.service.CorporateAccountApplyOrderService;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountApplyOrdeVO;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.ChannelPushStateEnum;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.enums.WalletTypeEnum;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.park.entity.ParkCorporateAccountConfigEntity;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.enums.ParkStatusEnum;
import com.yuqian.itax.park.service.ParkCorporateAccountConfigService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.enums.PayChannelEnum;
import com.yuqian.itax.pay.enums.PayWaterStatusEnum;
import com.yuqian.itax.pay.enums.PayWaterTypeEnum;
import com.yuqian.itax.pay.enums.PayWayEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.ProductParkRelaEntity;
import com.yuqian.itax.product.entity.dto.ProductDiscountActivityAPIDTO;
import com.yuqian.itax.product.entity.vo.ProductDiscountActivityVO;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ProductDiscountActivityService;
import com.yuqian.itax.product.service.ProductParkRelaService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.vo.CompanyCorpAccApplyIndividualVO;
import com.yuqian.itax.user.enums.CompanyCorporateAccountStatusEnum;
import com.yuqian.itax.user.enums.MemberCompanyOverdueStatusEnum;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.enums.UserTypeEnum;
import com.yuqian.itax.user.service.CompanyCorporateAccountService;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.user.service.MemberCrowdLabelRelaService;
import com.yuqian.itax.util.util.OrderNoFactory;
import com.yuqian.itax.util.util.UniqueNumGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("corporateAccountApplyOrderService")
public class CorporateAccountApplyOrderServiceImpl extends BaseServiceImpl<CorporateAccountApplyOrderEntity, CorporateAccountApplyOrderMapper> implements CorporateAccountApplyOrderService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserCapitalAccountService userCapitalAccountService;
    @Autowired
    private CorporateAccountApplyOrderService corporateAccountApplyOrderService;
    @Autowired
    CorporateAccountApplyOrderChangeService corporateAccountApplyOrderChangeService;
    @Autowired
    private CompanyCorporateAccountService companyCorporateAccountService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private ParkService parkService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ParkCorporateAccountConfigService parkCorporateAccountConfigService;
    @Autowired
    private PayWaterService payWaterService;
    @Autowired
    private OemService oemService;
    @Autowired
    private ProductDiscountActivityService productDiscountActivityService;
    @Autowired
    private MemberCrowdLabelRelaService memberCrowdLabelRelaService;
    @Autowired
    private ProductParkRelaService productParkRelaService;

    @Override
    public PageInfo<CorporateAccountApplyOrdeVO> queryCorporateAccountApplyOrderPageInfo(CorporateAccountApplyOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(mapper.queryCorporateAccountApplyOrderList(query));
    }

    @Override
    public List<CorporateAccountApplyOrdeVO> queryCorporateAccountApplyOrderList(CorporateAccountApplyOrderQuery query) {
        return (mapper.queryCorporateAccountApplyOrderList(query));
    }

    @Override
    public void cannel(Long id,String account) {
        CorporateAccountApplyOrderQuery query=new CorporateAccountApplyOrderQuery();
        query.setId(id);
        CorporateAccountApplyOrderEntity corporateAccountApplyOrderEntity=mapper.selectByPrimaryKey(id);
        OrderEntity orderEntity=orderService.queryByOrderNo(corporateAccountApplyOrderEntity.getOrderNo());
        if(orderEntity.getOrderStatus()==3){
            throw new BusinessException("订单已取消");
        }
        if(orderEntity.getOrderStatus()==2){
            throw new BusinessException("订单已完成不允许取消");
        }

        if (orderEntity.getOrderStatus() == 1) {
            UserCapitalAccountEntity accEntity = userCapitalAccountService.queryByUserIdAndType(corporateAccountApplyOrderEntity.getMemberId(),UserTypeEnum.MEMBER.getValue(),corporateAccountApplyOrderEntity.getOemCode(),WalletTypeEnum.CONSUMER_WALLET.getValue());
            //已付款退款并记录变动数据
            userCapitalAccountService.unfreezeBalance(accEntity, orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getPayAmount(), orderEntity.getAddUser());

            // 生成支付流水
            PayWaterEntity water = new PayWaterEntity();
            water.setPayNo(UniqueNumGenerator.generatePayNo());// 生成32位流水号
            water.setOrderNo(orderEntity.getOrderNo());
            water.setMemberId(corporateAccountApplyOrderEntity.getMemberId());
            water.setUserType(UserTypeEnum.MEMBER.getValue());// 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
            water.setOemCode(orderEntity.getOemCode());
            OemEntity oemEntity=oemService.getOem(orderEntity.getOemCode());
            water.setOemName(oemEntity.getOemName());
            water.setOrderAmount(orderEntity.getOrderAmount());
            water.setPayAmount(orderEntity.getPayAmount());
            water.setOrderType(OrderTypeEnum.CORPORATE_APPLY.getValue());
            water.setPayWaterType(PayWaterTypeEnum.REFUND.getValue());// 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款
            water.setPayWay(PayWayEnum.BALANCEPAY.getValue());// 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
            water.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
            UserCapitalAccountEntity userCapitalAccountEntity=userCapitalAccountService.queryByUserIdAndType(corporateAccountApplyOrderEntity.getMemberId(),UserTypeEnum.MEMBER.getValue(),orderEntity.getOemCode(),WalletTypeEnum.CONSUMER_WALLET.getValue());
            water.setPayAccount(userCapitalAccountEntity.getCapitalAccount());
            water.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());// 钱包类型 1-消费钱包 2-佣金钱包
            water.setAddTime(new Date());
            water.setAddUser(account);
            water.setPayChannels(PayChannelEnum.BALANCEPAY.getValue());//支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行
            payWaterService.insertSelective(water);

        }
        //更新订单状态
        orderService.updateOrderStatus(account, corporateAccountApplyOrderEntity.getOrderNo(), 3);
        //更新对公户申请单
        corporateAccountApplyOrderEntity.setUpdateTime(new Date());
        corporateAccountApplyOrderEntity.setUpdateUser(account);
        corporateAccountApplyOrderEntity.setRemark("对公户申请订单取消");
        corporateAccountApplyOrderService.editByIdSelective(corporateAccountApplyOrderEntity);
        //增加对公申请订单变更记录
        corporateAccountApplyOrderChangeService.addCorporateAccountApplyOrderChange(corporateAccountApplyOrderEntity, 3, account);
    }

    @Override
    public List<CompanyCorpAccApplyIndividualVO> applyIndividuallist(Long memberId, String oemCode) throws BusinessException {
        List<CompanyCorpAccApplyIndividualVO> individualList = new LinkedList<CompanyCorpAccApplyIndividualVO>();
        List<CompanyCorpAccApplyIndividualVO> sortList = new LinkedList<CompanyCorpAccApplyIndividualVO>(); //用于排序, 已存在或者申请中的放后面

        // 查询用户的个体列表
        List<MemberCompanyEntity> companyList = memberCompanyService.listCorpAccCompany(memberId, oemCode);

        // 循环个体列表
        if (CollectionUtil.isNotEmpty(companyList)) {
            for (MemberCompanyEntity company : companyList) {
                // 查询个体是否已开通企业对公户
                CompanyCorporateAccountEntity acc = new CompanyCorporateAccountEntity();
                acc.setCompanyId(company.getId());
                acc.setMemberId(company.getMemberId());
                acc.setStatus(CompanyCorporateAccountStatusEnum.NORMAL.getValue());
                acc = companyCorporateAccountService.selectOne(acc);
                if (null == acc) {
                    // 设置值
                    CompanyCorpAccApplyIndividualVO individual = new CompanyCorpAccApplyIndividualVO();
                    individual.setCompanyId(company.getId());
                    individual.setCompanyName(company.getCompanyName());
                    individual.setOperatorName(company.getOperatorName());
                    individual.setParkId(company.getParkId());
                    individual.setParkName(Optional.ofNullable(parkService.findById(company.getParkId())).map(ParkEntity::getParkName).orElse(null));
                    individual.setOverdueStatus(company.getOverdueStatus());

                    // 不存在已开通的企业对公户，查询是否存在已申请的订单
                    int applyingCount = mapper.queryCorAccApplyingOrder(memberId, oemCode, company.getId());
                    if (applyingCount > 0) {
                        individual.setStatusDesc("申请中");
                        sortList.add(individual);
                    } else {
                        individualList.add(individual);
                    }
                } else {
                    // 设置值
                    CompanyCorpAccApplyIndividualVO individual = new CompanyCorpAccApplyIndividualVO();
                    individual.setCompanyId(company.getId());
                    individual.setCompanyName(company.getCompanyName());
                    individual.setOperatorName(company.getOperatorName());
                    individual.setParkId(company.getParkId());
                    individual.setParkName(Optional.ofNullable(parkService.findById(company.getParkId())).map(ParkEntity::getParkName).orElse(null));
                    individual.setStatusDesc("已存在对公户");
                    individual.setOverdueStatus(company.getOverdueStatus());
                    sortList.add(individual);
                }
            }
        }
        individualList.addAll(sortList);

        if (null == individualList || individualList.isEmpty()) {
            throw new BusinessException("没有符合申请条件的个体户");
        }
        return individualList;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Map<String, Object> createApplyOrder(Long memberId, String oemCode, Long companyId, String sourceType) throws BusinessException {
        Map<String, Object> result = new HashMap<String, Object>();

        if (null == companyId) {
            throw new BusinessException("企业ID不能为空");
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询个体是否已开通企业对公户
        CompanyCorporateAccountEntity acc = new CompanyCorporateAccountEntity();
        acc.setCompanyId(companyId);
        acc.setMemberId(memberId);
        acc.setStatus(CompanyCorporateAccountStatusEnum.NORMAL.getValue());
        acc = companyCorporateAccountService.selectOne(acc);
        if (null != acc) {
            throw new BusinessException("企业已存在对公户，无需重复申请");
        }

        // 查询个体是否存在已申请的对公户订单
        int applyingCount = mapper.queryCorAccApplyingOrder(memberId, oemCode, companyId);
        if (applyingCount > 0) {
            throw new BusinessException("对公户正在申请中，无需重复申请");
        }

        // 查询企业
        MemberCompanyEntity company = new MemberCompanyEntity();
        company.setId(companyId);
        company.setOemCode(oemCode);
        company.setMemberId(memberId);
        company = memberCompanyService.selectOne(company);
        if (null == company) {
            throw new BusinessException("未查询到企业");
        }

        // 校验企业托管费状态（状态：1-正常 2-即将过期 3-已过期）
        if (Objects.equals(company.getOverdueStatus(),MemberCompanyOverdueStatusEnum.OVERDUE.getValue())){
            throw new BusinessException("企业已过期，无法申请对公户");
        }

        // 校验企业类型，非个体企业暂不允许创建对公户
        if (company.getCompanyType()!=1){
            throw new BusinessException("非个体企业暂不允许创建对公户");
        }
        // 校验公司状态(状态：1->正常；2->禁用；4->已注销 5->注销中)
//        if (!IntervalUtil.isInTheInterval(company.getStatus().toString(), "[" + MemberCompanyStatusEnum.NORMAL.getValue().toString()  + "]")) {
//            throw new BusinessException("公司已注销或注销中，无法申请对公户");
//        }
        if (!Objects.equals(company.getStatus(),MemberCompanyStatusEnum.NORMAL.getValue())) {
            throw new BusinessException("公司状态异常，无法申请对公户");
        }

        // 查询园区
        ParkEntity park = new ParkEntity();
        park.setId(company.getParkId());
        park.setStatus(ParkStatusEnum.ON_SHELF.getValue());//状态 0-待上线 1-已上架 2-已下线  3-已暂停  4-已删除
        park = parkService.selectOne(park);
        if (null == park) {
            throw new BusinessException("园区不存在或未上架");
        }

        // 查询产品
        ProductEntity product = new ProductEntity();
        product.setOemCode(oemCode);
        product.setProdType(ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue());
        product.setStatus(ParkStatusEnum.ON_SHELF.getValue());//状态 0-待上架 1-已上架 2-已下架 3-已暂停
        product = productService.selectOne(product);
        if (null == product) {
            throw new BusinessException("产品不存在或未上架");
        }

        // 查询产品园区关系
        ProductParkRelaEntity relaEntity = new ProductParkRelaEntity();
        relaEntity.setProductId(product.getId());
        relaEntity.setParkId(park.getId());
        relaEntity = productParkRelaService.selectOne(relaEntity);
        if (null == relaEntity) {
            throw new BusinessException("该园区不支持对公户申请");
        }

        // 查询企业对公户配置
        //2.7申请单不保存对公户申请银行
//        ParkCorporateAccountConfigEntity accConfig = new ParkCorporateAccountConfigEntity();
//        accConfig.setParkId(park.getId());
//        accConfig.setStatus(ParkCorporateAccountConfigStatusEnum.AVAILABLE.getValue());
//        accConfig = parkCorporateAccountConfigService.selectOne(accConfig);
//        if (null == accConfig) {
//            throw new BusinessException("企业对公户配置不存在或不可用");
//        }

        //查询企业对公户配置V2.9
        List<ParkCorporateAccountConfigEntity> list = Optional.ofNullable(parkCorporateAccountConfigService.getConfigByParkId(park.getId())).orElseThrow(() -> new BusinessException("企业对公户配置不存在或不可用"));
        ParkCorporateAccountConfigEntity accountConfigEntity = list.get(0); //临时方案，待银行总行增加时视具体需求更新

        // 生成订单号
        String orderNo = OrderNoFactory.getOrderCode(memberId);
        // 保存订单主表信息
        OrderEntity mainOrder = new OrderEntity();
        mainOrder.setOrderNo(orderNo);
        mainOrder.setUserId(memberId);
        mainOrder.setUserType(member.getMemberType());
        mainOrder.setOrderType(OrderTypeEnum.CORPORATE_APPLY.getValue());
        mainOrder.setOrderStatus(CorporateAccountApplyOrderStatusEnum.TO_BE_PAY.getValue());
        mainOrder.setProductId(product.getId());
        mainOrder.setProductName(product.getProdName());
        mainOrder.setOemCode(oemCode);
        mainOrder.setParkId(company.getParkId());
        mainOrder.setWalletType(WalletTypeEnum.CONSUMER_WALLET.getValue());
        mainOrder.setPayAmount(product.getProdAmount() + product.getProcessingFee());
        mainOrder.setOrderAmount(product.getProdAmount() + product.getProcessingFee());
        mainOrder.setAddTime(new Date());
        mainOrder.setAddUser(member.getMemberAccount());
        mainOrder.setSourceType(Integer.parseInt(sourceType));
        mainOrder.setChannelProductCode(member.getChannelProductCode());
        mainOrder.setChannelCode(member.getChannelCode());
        mainOrder.setChannelEmployeesId(member.getChannelEmployeesId());
        mainOrder.setChannelServiceId(member.getChannelServiceId());
        mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
        mainOrder.setChannelUserId(member.getChannelUserId());

        /**
         *  判断是否存在特价活动，
         *  如果存在，订单金额 = 特价活动金额，支付金额取 = 特价活动*折扣， 优惠金额 = 订单金额 - 支付金额取
         */
        ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO = new ProductDiscountActivityAPIDTO();
        productDiscountActivityAPIDTO.setOemCode(oemCode);
        productDiscountActivityAPIDTO.setMemberId(memberId);
        productDiscountActivityAPIDTO.setIndustryId(company.getIndustryId());
        productDiscountActivityAPIDTO.setParkId(company.getParkId());
        productDiscountActivityAPIDTO.setProductType(ProductTypeEnum.CORPORATE_ACCOUNT_APPLY.getValue());
        ProductDiscountActivityVO productDiscountActivityVO = productDiscountActivityService.getProductDiscountActivityByProductType(productDiscountActivityAPIDTO);
        if(productDiscountActivityVO!=null) {
            mainOrder.setPayAmount(productDiscountActivityVO.getSpecialPriceAmount() + productDiscountActivityVO.getProcessingFee());
            mainOrder.setOrderAmount(productDiscountActivityVO.getSpecialPriceAmount() + productDiscountActivityVO.getProcessingFee());
            mainOrder.setDiscountActivityId(productDiscountActivityVO.getDiscountActivityId());
        }
        //保存人群标签id
        Long crowdLabelId = memberCrowdLabelRelaService.getCrowLabelIdByMemberId(member.getId(),member.getOemCode());
        if(crowdLabelId!=null){
            mainOrder.setCrowdLabelId(crowdLabelId);
        }
        orderService.insertSelective(mainOrder);

        // 保存对公户申请订单信息
        CorporateAccountApplyOrderEntity applyOrder = new CorporateAccountApplyOrderEntity();
        applyOrder.setOrderNo(orderNo);
        applyOrder.setMemberId(memberId);
        applyOrder.setCompanyId(companyId);
//        applyOrder.setApplyBankName(accConfig.getCorporateAccountBankName());
        applyOrder.setOemCode(oemCode);
        applyOrder.setHandleFee(product.getProcessingFee());
        applyOrder.setEscrowFee(product.getProdAmount());
        if(productDiscountActivityVO!=null) {
            applyOrder.setHandleFee(productDiscountActivityVO.getProcessingFee());
            applyOrder.setEscrowFee(productDiscountActivityVO.getSpecialPriceAmount());
        }
        applyOrder.setParkId(park.getId());
        applyOrder.setParkCode(park.getParkCode());
        applyOrder.setAddTime(new Date());
        applyOrder.setAddUser(member.getMemberAccount());
        applyOrder.setRemark("创建对公户申请订单");
        applyOrder.setHeadquartersName(accountConfigEntity.getHeadquartersName());
        applyOrder.setHeadquartersNo(accountConfigEntity.getHeadquartersNo());
        corporateAccountApplyOrderService.insertSelective(applyOrder);

        // 保存对公户申请订单变更记录
        CorporateAccountApplyOrderChangeEntity applyOrderChange = new CorporateAccountApplyOrderChangeEntity();
        BeanUtil.copyProperties(applyOrder, applyOrderChange);
        applyOrderChange.setId(null);
        applyOrderChange.setStatus(CorporateAccountApplyOrderStatusEnum.TO_BE_PAY.getValue());
        applyOrderChange.setHeadquartersName(accountConfigEntity.getHeadquartersName());
        applyOrderChange.setHeadquartersNo(accountConfigEntity.getHeadquartersNo());
        corporateAccountApplyOrderChangeService.insertSelective(applyOrderChange);

        result.put("orderNo", orderNo);
        result.put("payAmount", mainOrder.getPayAmount());
        result.put("goodsName", product.getProdName());
        return result;
    }

    @Override
    public List<CorporateAccountApplyOrderVO> applyOrderList(Long memberId, String oemCode) throws BusinessException {
        return (mapper.queryApplyOrderList(memberId, oemCode));
    }


    @Override
    public void cancelApplyOrder(Long memberId, String oemCode, String orderNo) throws BusinessException {
        if (StrUtil.isEmpty(orderNo)) {
            throw new BusinessException("订单号不能为空");
        }

        // 查询主表订单信息
        OrderEntity order = orderService.queryByOrderNo(orderNo);
        if (null == order) {
            throw new BusinessException("未查询到订单");
        }

        if (!Objects.equals(order.getUserId(), memberId)) {
            throw new BusinessException("不是会员的订单");
        }

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if (null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        // 查询对公户申请订单信息
        CorporateAccountApplyOrderEntity applyOrder = mapper.queryByOrderNo(orderNo);
        if (null == applyOrder) {
            throw new BusinessException("未查询到对公户申请订单");
        }

        // 对公户申请订单状态: 0-待支付 1-等待预约 2-已完成 3-已取消
        if (order.getOrderStatus().equals(CorporateAccountApplyOrderStatusEnum.TO_BE_SUBSCRIBE.getValue())) {
            throw new BusinessException("等待预约的订单无法取消，请联系客服");
        } else if (order.getOrderStatus().equals(CorporateAccountApplyOrderStatusEnum.COMPLETED.getValue())) {
            throw new BusinessException("已完成的订单无法取消");
        } else if (order.getOrderStatus().equals(CorporateAccountApplyOrderStatusEnum.CANCELED.getValue())) {
            throw new BusinessException("已取消的订单无法再次取消");
        } else if (order.getOrderStatus().equals(CorporateAccountApplyOrderStatusEnum.TO_BE_PAY.getValue())) {
            // 修改主表订单状态
            order.setOrderStatus(CorporateAccountApplyOrderStatusEnum.CANCELED.getValue());
            order.setUpdateUser(member.getMemberAccount());
            order.setUpdateTime(new Date());
            orderService.editByIdSelective(order);

            // 新增对公户申请订单变更记录
            CorporateAccountApplyOrderChangeEntity applyOrderChange = new CorporateAccountApplyOrderChangeEntity();
            BeanUtils.copyProperties(applyOrder, applyOrderChange);
            applyOrderChange.setId(null);
            applyOrderChange.setStatus(CorporateAccountApplyOrderStatusEnum.CANCELED.getValue());
            applyOrderChange.setUpdateUser(member.getMemberAccount());
            applyOrderChange.setUpdateTime(new Date());
            applyOrderChange.setRemark("取消对公户申请订单");
            corporateAccountApplyOrderChangeService.insertSelective(applyOrderChange);
        }
    }

    @Override
    public CorporateAccountApplyOrderEntity queryByOrderNo(String orderNo) {
        return mapper.queryByOrderNo(orderNo);
    }

    @Override
    public int queryCorAccApplyingOrder(Long memberId,String oemCode,Long companyId){
        return this.mapper.queryCorAccApplyingOrder(memberId, oemCode,companyId);
    }
}

