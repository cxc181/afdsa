package com.yuqian.itax.order.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemConfigService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.ContRegisterOrderChangeMapper;
import com.yuqian.itax.order.dao.ContRegisterOrderMapper;
import com.yuqian.itax.order.dao.OrderMapper;
import com.yuqian.itax.order.entity.ContRegisterOrderChangeEntity;
import com.yuqian.itax.order.entity.ContRegisterOrderEntity;
import com.yuqian.itax.order.entity.MemberOrderRelaEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.query.ContRegisterOrderQuery;
import com.yuqian.itax.order.entity.vo.ContRegisterOrderVO;
import com.yuqian.itax.order.enums.ChannelPushStateEnum;
import com.yuqian.itax.order.enums.ContOrderStatusEnum;
import com.yuqian.itax.order.enums.ContTypeEnum;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.enums.ParkStatusEnum;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.dto.ProductDiscountActivityAPIDTO;
import com.yuqian.itax.product.entity.vo.ProductDiscountActivityVO;
import com.yuqian.itax.product.enums.ProductStatusEnum;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ProductDiscountActivityService;
import com.yuqian.itax.product.service.ProductParkRelaService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.MemberLevelEntity;
import com.yuqian.itax.user.enums.UserTypeEnum;
import com.yuqian.itax.user.service.*;
import com.yuqian.itax.util.util.OrderNoFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Service("contRegisterOrderService")
public class ContRegisterOrderServiceImpl extends BaseServiceImpl<ContRegisterOrderEntity,ContRegisterOrderMapper> implements ContRegisterOrderService {

    @Resource
    private ContRegisterOrderChangeMapper contRegisterOrderChangeMapper;

    @Resource
    private OrderMapper orderMapper;

    @Autowired
    private MemberAccountService memberAccountService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    private OemService oemService;

    @Autowired
    private ParkService parkService;

    @Autowired
    private ProductParkRelaService productParkRelaService;

    @Autowired
    private MemberLevelService memberLevelService;

    @Autowired
    private ContRegisterOrderService contRegisterOrderService;

    @Autowired
    private InvoiceOrderService invoiceOrderService;

    @Autowired
    private MemberOrderRelaService memberOrderRelaService;

    @Autowired
    private OrderService orderService;

    @Resource
    private MemberProfitsRulesService memberProfitsRulesService;

    @Resource
    private ContRegisterOrderChangeService contRegisterOrderChangeService;

    @Autowired
    private OemConfigService oemConfigService;

    @Autowired
    private ProductDiscountActivityService productDiscountActivityService;

    @Autowired
    private MemberCrowdLabelRelaService memberCrowdLabelRelaService;

    @Override
    public PageInfo<ContRegisterOrderVO> listPageContRegOrder(ContRegisterOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(listContRegOrder(query));
    }

    @Override
    public List<ContRegisterOrderVO> listContRegOrder(ContRegisterOrderQuery query) {
        query.setOrderType(OrderTypeEnum.CUSTODY_FEE_RENEWAL.getValue());
        return mapper.listContRegOrder(query);
    }

    @Override
    @Transactional
    public void cancelOrder(ContRegisterOrderEntity contRegisterOrderEntity) {
        //修改托管费续费表
        mapper.updateByPrimaryKeySelective(contRegisterOrderEntity);
        //修改订单主表
        orderMapper.updateOrderStatus(contRegisterOrderEntity.getOrderNo(), contRegisterOrderEntity.getOrderStatus(), contRegisterOrderEntity.getUpdateUser(), contRegisterOrderEntity.getUpdateTime());
        //添加历史记录变更表
        ContRegisterOrderChangeEntity record = new ContRegisterOrderChangeEntity();
        BeanUtils.copyProperties(contRegisterOrderEntity, record);
        record.setId(null);
        record.setAddTime(contRegisterOrderEntity.getUpdateTime());
        record.setAddUser(contRegisterOrderEntity.getUpdateUser());
        contRegisterOrderChangeMapper.insertSelective(record);
    }

    @Override
    public String createContRegOrder(Long currUserId, String oemCode, Long companyId) {

        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(currUserId);
        if (Objects.isNull(member)) {
            throw new BusinessException("未查询到会员账号");
        }
        Long memberId = member.getId();
        //查询企业信息
        MemberCompanyEntity company = memberCompanyService.findById(companyId);
        if (Objects.isNull(company)) {
            throw new BusinessException("操作失败，企业信息不存在");
        }
        //获取园区id
        Long parkId = company.getParkId();
        //查询机构信息
        OemEntity oem = oemService.getOem(oemCode);
        if (Objects.isNull(oem)) {
            throw new BusinessException("操作失败，oem机构不存在");
        }
        // 查询园区信息
        ParkEntity park = this.parkService.findById(parkId);
        if (Objects.isNull(park)) {
            throw new BusinessException("操作失败，园区不存在");
        }
        if (!ParkStatusEnum.ON_SHELF.getValue().equals(park.getStatus())) {
            throw new BusinessException("续费订单创建失败，园区不可用");
        }
        // 获取企业对应注册产品类型
        Integer productType = this.companyTypeTransferProductType(company.getCompanyType());
        if (null == productType) {
            throw new BusinessException("未知产品类型");
        }
        //查询产品
        ProductEntity product = productService.queryProductByProdType(productType, oemCode, parkId);
        if (Objects.isNull(product)) {
            throw new BusinessException("操作失败，产品信息不存在");
        }
        if (ProductStatusEnum.PAUSED.getValue().equals(product.getStatus())) {
            throw new BusinessException("该功能已暂停，若有疑问可联系客服~");
        } else if (!Objects.equals(product.getStatus(), ProductStatusEnum.ON_SHELF.getValue())){
            throw new BusinessException("操作失败，产品不可用");
        }

        //检验是否已存在未支付订单，存在即取消已有订单新建
        ContRegisterOrderEntity contRegisterOrderEntity = new ContRegisterOrderEntity();
        contRegisterOrderEntity.setCompanyId(company.getId());
        contRegisterOrderEntity.setOrderStatus(ContOrderStatusEnum.TO_BE_PAY.getValue());
        List<ContRegisterOrderEntity> orderEntityList = contRegisterOrderService.select(contRegisterOrderEntity);
        if (!orderEntityList.isEmpty()){
        for (ContRegisterOrderEntity orderEntity : orderEntityList) {
            orderEntity.setUpdateTime(new Date());
            orderEntity.setUpdateUser(member.getMemberAccount());
            orderEntity.setOrderStatus(ContOrderStatusEnum.CANCELLED.getValue());
            orderEntity.setRemark("取消未支付订单");
            contRegisterOrderService.cancelOrder(orderEntity);
            }
        }

        String orderNo = null;
        try {
            //生成订单编号
            orderNo = OrderNoFactory.getOrderCode(currUserId);
            //订单金额
            Long prodAmount = product.getProdAmount();
            //支付金额
            Long payAmount = this.memberProfitsRulesService.queryMemberDiscount(memberId,product.getId(),oemCode,parkId);

            /**
             *  判断是否存在特价活动，
             *  如果存在，订单金额 = 特价活动金额，支付金额取 = 特价活动*折扣， 优惠金额 = 订单金额 - 支付金额取
             */
            ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO = new ProductDiscountActivityAPIDTO();
            productDiscountActivityAPIDTO.setOemCode(oemCode);
            productDiscountActivityAPIDTO.setMemberId(company.getMemberId());
            productDiscountActivityAPIDTO.setIndustryId(company.getIndustryId());
            productDiscountActivityAPIDTO.setParkId(company.getParkId());
            productDiscountActivityAPIDTO.setProductType(productType);
            ProductDiscountActivityVO productDiscountActivityVO = productDiscountActivityService.getProductDiscountActivityByProductType(productDiscountActivityAPIDTO);
            if(productDiscountActivityVO!=null) {
                prodAmount = productDiscountActivityVO.getSpecialPriceAmount();
                payAmount = productDiscountActivityVO.getPayAmount();
            }

            //保存续费订单
            ContRegisterOrderEntity contRegOrdEntity = new ContRegisterOrderEntity();
            contRegOrdEntity.setOrderNo(orderNo);
            contRegOrdEntity.setMemeberId(memberId);
            contRegOrdEntity.setCompanyId(companyId);
            contRegOrdEntity.setOemCode(oemCode);
            contRegOrdEntity.setParkId(parkId);
            contRegOrdEntity.setOrderAmount(prodAmount);
            contRegOrdEntity.setPayAmount(payAmount);
            contRegOrdEntity.setContType(ContTypeEnum.TRUSTEE_FEE.getValue());
            contRegOrdEntity.setOrderStatus(ContOrderStatusEnum.TO_BE_PAY.getValue());
            contRegOrdEntity.setAddTime(new Date());
            contRegOrdEntity.setAddUser(member.getMemberAccount());
            contRegisterOrderService.insertSelective(contRegOrdEntity);

            //保存会员订单关系
            MemberLevelEntity level = this.memberLevelService.findById(member.getMemberLevel());
            if (Objects.isNull(level)){
                throw new BusinessException("会员等级信息不存在");
            }
            MemberOrderRelaEntity more = this.invoiceOrderService.getUserTree(memberId, oemCode, 1);
            if (Objects.nonNull(more)) {
                more.setMemberId(memberId);
                // 设置会员等级
                more.setMemberLevel(level.getLevelNo());
                more.setAddTime(new Date());
                more.setAddUser(member.getMemberAccount());
                more.setOemCode(oemCode);
                more.setOemName(oem.getOemName());
                this.memberOrderRelaService.insertSelective(more);

            }else{
                throw new BusinessException("会员订单关系表表不存在");
            }

            //保存续费订单变更记录
            ContRegisterOrderChangeEntity contRegisterOrderChangeEntity = new ContRegisterOrderChangeEntity();
            BeanUtils.copyProperties(contRegOrdEntity,contRegisterOrderChangeEntity);
            contRegisterOrderChangeEntity.setId(null);
            this.contRegisterOrderChangeService.insertSelective(contRegisterOrderChangeEntity);

            //保存订单主表
            OrderEntity mainOrder = new OrderEntity();
            mainOrder.setOrderNo(orderNo);
            mainOrder.setAddTime(new Date());
            mainOrder.setAddUser(member.getMemberAccount());
            mainOrder.setUserId(memberId);
            mainOrder.setRelaId(more.getId());
            mainOrder.setUserType(UserTypeEnum.MEMBER.getValue());
            mainOrder.setOrderType(OrderTypeEnum.CUSTODY_FEE_RENEWAL.getValue());
            mainOrder.setOrderStatus(ContOrderStatusEnum.TO_BE_PAY.getValue());
            mainOrder.setProductId(product.getId());
            mainOrder.setProductName(product.getProdName());
            mainOrder.setOemCode(oemCode);
            mainOrder.setParkId(parkId);
            mainOrder.setOrderAmount(prodAmount);
            mainOrder.setPayAmount(payAmount);
            mainOrder.setProfitAmount(park.getIsRenewProfit() == 1 ? payAmount : 0L);
            mainOrder.setDiscountAmount(prodAmount-payAmount);
            mainOrder.setChannelProductCode(member.getChannelProductCode());
            mainOrder.setChannelCode(member.getChannelCode());
            mainOrder.setChannelEmployeesId(member.getChannelEmployeesId());
            mainOrder.setChannelServiceId(member.getChannelServiceId());
            mainOrder.setChannelUserId(member.getChannelUserId());
            OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByCode(mainOrder.getOemCode(),"is_open_channel");
            if(oemConfigEntity!=null && "1".equals(oemConfigEntity.getParamsValue())){
                mainOrder.setChannelPushState(ChannelPushStateEnum.TO_BE_PAY.getValue());
            }else{
                mainOrder.setChannelPushState(ChannelPushStateEnum.CANCELLED.getValue());
            }
            //保存人群标签id
            Long crowdLabelId = memberCrowdLabelRelaService.getCrowLabelIdByMemberId(member.getId(),member.getOemCode());
            if(crowdLabelId!=null){
                mainOrder.setCrowdLabelId(crowdLabelId);
            }
            if(productDiscountActivityVO!=null){
                mainOrder.setDiscountActivityId(productDiscountActivityVO.getDiscountActivityId());
            }
            orderService.insertSelective(mainOrder);
        } catch (BusinessException e) {
            throw new BusinessException("创建托管费续费订单失败，"+ e.getMessage());
        }
        return orderNo;
    }

    /**
     * @Description 企业类型转换为产品类型（续费产品）
     * @Author  yejian
     * @Date   2019/12/20 16:34
     * @Param  companyType
     * @Return Integer
     */
    public Integer companyTypeTransferProductType(Integer companyType) {
        if(companyType == 1) {
            return ProductTypeEnum.INDIVIDUAL_RENEWALS.getValue();
        }else if(companyType == 2) {
            return ProductTypeEnum.INDEPENDENTLY_RENEWALS.getValue();
        }else if(companyType == 3) {
            return ProductTypeEnum.LIMITED_PARTNER_RENEWALS.getValue();
        }else if(companyType == 4) {
            return ProductTypeEnum.LIMITED_LIABILITY_RENEWALS.getValue();
        }else{
            return null;
        }
    }
}

