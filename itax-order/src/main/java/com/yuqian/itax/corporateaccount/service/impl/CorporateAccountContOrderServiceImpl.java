package com.yuqian.itax.corporateaccount.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.corporateaccount.dao.CorporateAccountContOrderMapper;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountContOrderChangeEntity;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountContOrderEntity;
import com.yuqian.itax.corporateaccount.entity.vo.CorporateAccountContOrderVO;
import com.yuqian.itax.corporateaccount.query.CorporateAccountContOrderQuery;
import com.yuqian.itax.corporateaccount.service.CorporateAccountContOrderChangeService;
import com.yuqian.itax.corporateaccount.service.CorporateAccountContOrderService;
import com.yuqian.itax.corporateaccount.vo.CorpAccUnpaidContOrderVO;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.vo.CreatCorpAccContOrderVO;
import com.yuqian.itax.order.enums.ContOrderStatusEnum;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.dto.ProductDiscountActivityAPIDTO;
import com.yuqian.itax.product.entity.vo.ProductDiscountActivityVO;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ProductDiscountActivityService;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.enums.CompanyCorporateAccountStatusEnum;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.enums.MemberStateEnum;
import com.yuqian.itax.user.enums.UserTypeEnum;
import com.yuqian.itax.user.service.CompanyCorporateAccountService;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.OrderNoFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("corporateAccountContOrderService")
@Slf4j
public class CorporateAccountContOrderServiceImpl extends BaseServiceImpl<CorporateAccountContOrderEntity, CorporateAccountContOrderMapper> implements CorporateAccountContOrderService {

    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private CompanyCorporateAccountService companyCorporateAccountService;
    @Autowired
    private MemberCompanyService memberCompanyService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CorporateAccountContOrderChangeService corporateAccountContOrderChangeService;
    @Autowired
    private ProductDiscountActivityService productDiscountActivityService;

    @Override
    public PageInfo<CorporateAccountContOrderVO> getContOrderListPage(CorporateAccountContOrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(mapper.listPages(query));
    }

    @Override
    public List<CorporateAccountContOrderVO> listPages(CorporateAccountContOrderQuery query) {
        return mapper.listPages(query);
    }

    @Override
    @Transactional
    public CreatCorpAccContOrderVO creatContOrder(Long companyCorpAccId, Long currUserId) {
        // 参数校验
        if (null == companyCorpAccId) {
            throw new BusinessException("对公户id不能为空");
        }
        if (null == currUserId) {
            throw new BusinessException("当前登录用户Id为空");
        }

        // 查询对公户
        CompanyCorporateAccountEntity companyCorpAcc = Optional.ofNullable(companyCorporateAccountService.findById(companyCorpAccId)).orElseThrow(() -> new BusinessException("未查询到对公户信息"));
        if (CompanyCorporateAccountStatusEnum.CANCELLED.getValue().equals(companyCorpAcc.getStatus())) {
            throw new BusinessException("对公户已注销，无法续费");
        }
        // 查询用户
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(currUserId)).orElseThrow(() -> new BusinessException("未查询到用户信息"));
        if (!Objects.equals(companyCorpAcc.getMemberId(), member.getId())) {
            throw new BusinessException("对公户不属于当前登录用户，无法续费");
        }
        if (!MemberStateEnum.STATE_ACTIVE.getValue().equals(member.getStatus())) {
            throw new BusinessException("所属用户不可用，无法续费");
        }
        // 查询个体户
        MemberCompanyEntity company = Optional.ofNullable(memberCompanyService.findById(companyCorpAcc.getCompanyId())).orElseThrow(() -> new BusinessException("未查询到个体户信息"));
        if (MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue().equals(company.getStatus())) {
            throw new BusinessException("所属个体户已注销，无法续费");
        }
        // 查询产品
        ProductEntity product = productService.queryProductByProdType(ProductTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue(), company.getOemCode(), company.getParkId());
        if (null == product) {
            throw new BusinessException("未找到符合条件的产品，无法续费");
        }
        Long productAmount = product.getProdAmount();
        /**
         *  判断是否存在特价活动，
         *  如果存在，订单金额 = 特价活动金额，支付金额取 = 特价活动*折扣， 优惠金额 = 订单金额 - 支付金额取
         */
        ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO = new ProductDiscountActivityAPIDTO();
        productDiscountActivityAPIDTO.setOemCode(company.getOemCode());
        productDiscountActivityAPIDTO.setMemberId(member.getId());
        productDiscountActivityAPIDTO.setIndustryId(company.getIndustryId());
        productDiscountActivityAPIDTO.setParkId(company.getParkId());
        productDiscountActivityAPIDTO.setProductType(ProductTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue());
        ProductDiscountActivityVO productDiscountActivityVO = productDiscountActivityService.getProductDiscountActivityByProductType(productDiscountActivityAPIDTO);
        if (null != productDiscountActivityVO) {
            productAmount = productDiscountActivityVO.getSpecialPriceAmount();
        }

        String orderNo = null;

        log.info("创建对公户年费续费订单开始：个体户id{}", companyCorpAccId);
        try {
            // 查询是否有未完成的续费订单
            List<CorpAccUnpaidContOrderVO> unpaidList = this.queryUnpaidByCompanyCorpAccId(companyCorpAccId);
            if (null != unpaidList && !unpaidList.isEmpty()) {
                // 取消未支付订单
                for (CorpAccUnpaidContOrderVO entity : unpaidList) {
                    // 更新订单状态为“已取消
                    this.cancelOrder(entity.getOrderNo(), member.getMemberAccount());
                    // 新增“已取消”对公户续费变更记录
                    CorporateAccountContOrderChangeEntity contOrderChangeEntity = new CorporateAccountContOrderChangeEntity();
                    contOrderChangeEntity.setOrderNo(entity.getOrderNo());
                    List<CorporateAccountContOrderChangeEntity> list = corporateAccountContOrderChangeService.select(contOrderChangeEntity);
                    contOrderChangeEntity = list.get(0);
                    contOrderChangeEntity.setId(null);
                    contOrderChangeEntity.setStatus(ContOrderStatusEnum.CANCELLED.getValue());
                    contOrderChangeEntity.setAddTime(new Date());
                    contOrderChangeEntity.setAddUser(member.getMemberAccount());
                    corporateAccountContOrderChangeService.insertSelective(contOrderChangeEntity);
                }
            }
            // 生成订单号
            orderNo = OrderNoFactory.getOrderCode(currUserId);
            Date date = new Date();
            // 生成主订单(初始状态为待支付)
            OrderEntity order = new OrderEntity();
            order.setOrderNo(orderNo);
            order.setUserId(currUserId);
            order.setUserType(UserTypeEnum.MEMBER.getValue());
            order.setOrderType(OrderTypeEnum.CORPORATE_ACCOUNT_RENEW.getValue());
            order.setOrderStatus(ContOrderStatusEnum.TO_BE_PAY.getValue());
            order.setProductId(product.getId());
            order.setProductName(product.getProdName());
            order.setOemCode(company.getOemCode());
            order.setParkId(company.getParkId());
            order.setOrderAmount(productAmount);
            order.setPayAmount(productAmount);
            order.setAddTime(date);
            order.setAddUser(member.getMemberAccount());
            // 保存主订单
            orderService.insertSelective(order);
            // 生成对公户续费订单
            CorporateAccountContOrderEntity contOrder = new CorporateAccountContOrderEntity();
            contOrder.setOrderNo(orderNo);
            contOrder.setUserId(currUserId);
            contOrder.setCompanyId(company.getId());
            contOrder.setCorporateAccountId(companyCorpAccId);
            contOrder.setCorporateAccountBankName(companyCorpAcc.getCorporateAccountBankName());
            contOrder.setCorporateAccount(companyCorpAcc.getCorporateAccount());
            contOrder.setOrderAmount(productAmount);
            contOrder.setPayAmount(productAmount);
            contOrder.setParkId(company.getParkId());
            contOrder.setOemCode(company.getOemCode());
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(companyCorpAcc.getExpirationTime());
            calendar.add(Calendar.YEAR,1);
            contOrder.setExpirationTime(calendar.getTime());
            contOrder.setAddTime(date);
            contOrder.setAddUser(member.getMemberAccount());
            this.insertSelective(contOrder);
            // 生成对公户续费变更记录
            CorporateAccountContOrderChangeEntity changeEntity = new CorporateAccountContOrderChangeEntity();
            ObjectUtil.copyObject(contOrder, changeEntity);
            changeEntity.setId(null);
            changeEntity.setStatus(ContOrderStatusEnum.TO_BE_PAY.getValue());
            corporateAccountContOrderChangeService.insertSelective(changeEntity);
        } catch (Exception e) {
            log.info("创建对公户年费续费订单失败：{}", e.getMessage());
            throw new BusinessException("创建对公户年费续费订单失败：" + e.getMessage());
        }

        // 构建返回数据
        CreatCorpAccContOrderVO vo = new CreatCorpAccContOrderVO();
        vo.setOrderNo(orderNo);
        vo.setPayAmount(product.getProdAmount());
        vo.setServiceContent(product.getProdName());
        log.info("创建对公户年费续费订单结束：orderNo{}", orderNo);
        return vo;
    }

    @Override
    public List<CorpAccUnpaidContOrderVO> queryUnpaidByCompanyCorpAccId(Long companyCorpAccId) {
        return mapper.queryUnpaidByCompanyCorpAccId(companyCorpAccId);
    }

    @Override
    public void cancelOrder(String orderNo, String updateAccount) {
        orderService.updateOrderStatus(updateAccount, orderNo, ContOrderStatusEnum.CANCELLED.getValue());
    }
}

