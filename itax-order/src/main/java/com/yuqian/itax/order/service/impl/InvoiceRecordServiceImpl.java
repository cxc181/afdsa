package com.yuqian.itax.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.InvoiceInfoByOemEntity;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.service.InvoiceInfoByOemService;
import com.yuqian.itax.agent.service.OemConfigService;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.order.dao.InvoiceRecordMapper;
import com.yuqian.itax.order.entity.*;
import com.yuqian.itax.order.entity.query.InvoiceRecordQuery;
import com.yuqian.itax.order.entity.vo.ConfirmInvoiceRecordVo;
import com.yuqian.itax.order.entity.vo.InvoiceRecordDetailVO;
import com.yuqian.itax.order.entity.vo.InvoiceRecordVO;
import com.yuqian.itax.order.enums.InvoiceOrderStatusEnum;
import com.yuqian.itax.order.enums.InvoiceRecordStatusEnum;
import com.yuqian.itax.order.enums.OrderTypeEnum;
import com.yuqian.itax.order.service.*;
import com.yuqian.itax.park.entity.ParkEndtimeConfigEntity;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.service.ParkEndtimeConfigService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.InvoiceCategoryBaseEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.InvoiceCategoryBaseService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.CompanyTaxHostingEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.service.CompanyTaxHostingService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.*;
import com.yuqian.itax.util.util.channel.BWElectricInvoiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("invoiceRecordService")
@Slf4j
public class InvoiceRecordServiceImpl extends BaseServiceImpl<InvoiceRecordEntity,InvoiceRecordMapper> implements InvoiceRecordService {

    @Autowired
    private CompanyTaxHostingService companyTaxHostingService;

    @Autowired
    private OemParamsService oemParamsService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    private InvoiceRecordChangeService invoiceRecordChangeService;

    @Autowired
    private ParkService parkService;

    @Autowired
    private InvoiceCategoryBaseService invoiceCategoryBaseService;

    @Autowired
    private InvoiceRecordDetailService invoiceRecordDetailService;

    @Autowired
    private OemService oemService;

    @Autowired
    private InvoiceOrderService invoiceOrderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OssService ossService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private ParkEndtimeConfigService parkEndtimeConfigService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ConsumptionInvoiceOrderService consumptionInvoiceOrderService;

    @Autowired
    ConsumptionInvoiceOrderChangeService consumptionInvoiceOrderChangeService;

    @Autowired
    private InvoiceInfoByOemService invoiceInfoByOemService;

    @Autowired
    private OemConfigService oemConfigService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private InvoiceorderGoodsdetailRelaService  invoiceorderGoodsdetailRelaService;

    @Override
    public List<InvoiceRecordVO> querylistInvoiceRecord(InvoiceRecordQuery query) {
        return mapper.querylistInvoiceRecord(query);
    }

    /**
     * 分页查询开票记录
     * @param query
     * @return
     */
    @Override
    public PageInfo<InvoiceRecordVO> listPage(InvoiceRecordQuery query){
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(querylistInvoiceRecord(query));
    }

    /**
     * 确认出票页面
     * @param invoiceRecordNo
     * @return
     */
    @Override
    public ConfirmInvoiceRecordVo gotoConfirmInvoiceRecord(String invoiceRecordNo){
        ConfirmInvoiceRecordVo vo = this.mapper.gotoConfirmInvoiceRecord(invoiceRecordNo);
        if(vo != null){
            List<Map<String,String>> taxSeasonalList = new ArrayList<>();
            //获取当前时间的季度
            Map<String,String> taxSeasonal1 = new HashMap<>();
            Date currentDate = new Date();
            String currYear =DateUtil.getYear(currentDate)+"";
            String currQuarter = DateUtil.getQuarter(currentDate);
            taxSeasonal1.put("taxYear",currYear);
            taxSeasonal1.put("taxQuarter",currQuarter);
            taxSeasonalList.add(taxSeasonal1);
            //获取开票订单创建时间对应的季度
            Date invoiceOrderAddTime = vo.getInvoiceOrderAddTime();
            String invoiceAddYear =DateUtil.getYear(invoiceOrderAddTime)+"";
            String invoiceAddQuarter = DateUtil.getQuarter(invoiceOrderAddTime);
            if(!currYear.equals(invoiceAddYear) || !currQuarter.equals(invoiceAddQuarter)){
                Map<String,String> taxSeasonal2 = new HashMap<>();
                taxSeasonal2.put("taxYear",invoiceAddYear);
                taxSeasonal2.put("taxQuarter",invoiceAddQuarter);
                taxSeasonalList.add(taxSeasonal2);
            }
            vo.setTaxSeasonal(taxSeasonalList);
        }
        return vo;
    }

    /**
     * 修改开票记录状态
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 订单号
     * @param status 开票记录状态 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消
     * @param desc  开票记录描述
     * @param handlingWay 处理方式 1-线下、2-托管
     * @param updateUser 修改人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInvoiceRecordStatus(String invoiceRecordNo,String orderNo,Integer status,String desc,Integer handlingWay,String updateUser){
        if(StringUtil.isBlank(invoiceRecordNo)){
            throw new BusinessException("开票记录编号不能为空");
        }
        //查询开票记录
        InvoiceRecordEntity entity = new InvoiceRecordEntity();
        entity.setInvoiceRecordNo(invoiceRecordNo);
        entity.setOrderNo(orderNo);
        entity = this.selectOne(entity);
        if(entity==null){
            throw new BusinessException("未找到开票记录");
        }
        entity.setStatus(status);
        if(handlingWay != null) {
            entity.setHandlingWay(handlingWay);
        }
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(updateUser);
        entity.setInvoiceDesc(desc);

        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if(orderEntity == null){
            throw new BusinessException("未找到相对应的订单");
        }
        if(orderEntity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()) { //开票订单
            //判断是否需要修改开票订单
            InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(orderNo);
            if (invoiceOrderEntity == null) {
                throw new BusinessException("未找到相对应开票订单");
            }
            //开票订单发票类型为电子发票，转为线下开票时 修改开票订单的发票类型为 纸质发票
            if (ObjectUtils.equals(status, InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue())
                    && ObjectUtils.equals(invoiceOrderEntity.getInvoiceWay(), 2)) {
//            invoiceOrderEntity.setInvoiceWay(InvoiceWayEnum.PAPER.getValue());
                invoiceOrderEntity.setRemark("手动变更为人工出票");
                invoiceOrderEntity.setConfirmInvoiceTime(entity.getTicketTime());
                invoiceOrderService.editAndSaveHistory(invoiceOrderEntity, InvoiceOrderStatusEnum.IN_TICKETING.getValue(), updateUser, "手动变更为人工出票");
            }
            //非集团开票 发票类型为电子票且已完成，则将开票订单状态改成 已完成，并触发分润
            if (ObjectUtils.equals(status, InvoiceRecordStatusEnum.COMPLETED.getValue())
                    && !ObjectUtils.equals(invoiceOrderEntity.getCreateWay(), 2)
                    && ObjectUtils.equals(entity.getHandlingWay(),2)
                    && ObjectUtils.equals(invoiceOrderEntity.getInvoiceWay(), 2)) {
                //判断是否存在非已完成、已取消状态的开票记录
                int count = findUnfinishedInvoiceRecordByOrderNo(invoiceOrderEntity.getOrderNo(), invoiceRecordNo);
                if (count == 0) {
                    //将发票图片、修改开票订单税期更新到开票订单表
                    String urls = "";
                    while(true) {
                        urls = getInvoiceDetailImgUrlsByOrder(invoiceOrderEntity.getOrderNo());
                        log.debug("查询电子发票地址结果-----------："+urls);
                        if (StringUtils.isBlank(urls)) {
                            try {
                                Thread.sleep(5000L);
                            } catch (Exception e) {
                            }
                        } else {
                            invoiceOrderEntity.setInvoiceImgs(urls);
                            break;
                        }
                    }
                    Date currentDate = new Date();
                    invoiceOrderEntity.setTaxYear(DateUtil.getYear(currentDate));
                    invoiceOrderEntity.setTaxSeasonal(Integer.parseInt(DateUtil.getQuarter(currentDate)));
                    invoiceOrderEntity.setConfirmInvoiceTime(entity.getTicketTime());
                    invoiceOrderEntity.setRemark("电子发票更新税期数据");
                    invoiceOrderService.editByIdSelective(invoiceOrderEntity);

                    //修改开票订单状态
                    MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(invoiceOrderEntity.getCompanyId());
                    invoiceOrderService.confirmReceipt(memberCompanyEntity.getMemberId(), invoiceOrderEntity.getOemCode(), invoiceOrderEntity.getOrderNo(),updateUser);
                }
            }
        }else if (orderEntity.getOrderType().intValue() == OrderTypeEnum.CONSUMPTION_INVOICE.getValue()){ //消费开票
            ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity = new ConsumptionInvoiceOrderEntity();
            consumptionInvoiceOrderEntity.setOemCode(orderEntity.getOemCode());
            consumptionInvoiceOrderEntity.setOrderNo(orderEntity.getOrderNo());
            consumptionInvoiceOrderEntity = consumptionInvoiceOrderService.selectOne(consumptionInvoiceOrderEntity);
            if(consumptionInvoiceOrderEntity == null){
                throw new BusinessException("未找到相对应消费开票订单");
            }
            if (ObjectUtils.equals(status, InvoiceRecordStatusEnum.COMPLETED.getValue())
                    && ObjectUtils.equals(entity.getHandlingWay(),2)
                    && ObjectUtils.equals(consumptionInvoiceOrderEntity.getInvoiceWay(), 2)) {
                //判断是否存在非已完成、已取消状态的开票记录
                int count = findUnfinishedInvoiceRecordByOrderNo(consumptionInvoiceOrderEntity.getOrderNo(), invoiceRecordNo);
                if (count == 0) {
                    //将发票图片、修改开票订单税期更新到开票订单表
                    String urls = "";
                    while(true) {
                        urls = getInvoiceDetailImgUrlsByOrder(consumptionInvoiceOrderEntity.getOrderNo());
                        log.debug("查询电子发票地址结果-----------："+urls);
                        if (StringUtils.isBlank(urls)) {
                            try {
                                Thread.sleep(5000L);
                            } catch (Exception e) {
                            }
                        } else {
                            consumptionInvoiceOrderEntity.setInvoiceImgs(urls);
                            break;
                        }
                    }
                    consumptionInvoiceOrderEntity.setCompleteTime(entity.getTicketTime());
                    consumptionInvoiceOrderEntity.setRemark("消费发票出票完成");
                    consumptionInvoiceOrderEntity.setUpdateTime(new Date());
                    consumptionInvoiceOrderEntity.setUpdateUser(updateUser);
                    consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);

                    ConsumptionInvoiceOrderChangeEntity consumptionInvoiceOrderChangeEntity=new ConsumptionInvoiceOrderChangeEntity();
                    BeanUtils.copyProperties(consumptionInvoiceOrderEntity,consumptionInvoiceOrderChangeEntity);
                    consumptionInvoiceOrderChangeEntity.setStatus(2);
                    consumptionInvoiceOrderChangeEntity.setId(null);
                    consumptionInvoiceOrderChangeEntity.setAddUser(updateUser);
                    consumptionInvoiceOrderChangeEntity.setAddTime(new Date());
                    consumptionInvoiceOrderChangeEntity.setUpdateTime(null);
                    consumptionInvoiceOrderChangeEntity.setUpdateUser(null);
                    consumptionInvoiceOrderChangeService.add(consumptionInvoiceOrderChangeEntity);
                    //修改订单状态为已出票
                    orderService.updateOrderStatus(updateUser, consumptionInvoiceOrderEntity.getOrderNo(), 2);
                }
            }
        }

        //判断是否为集团开票，如果是集团开票则直接将开票记录设置为 已完成
//        if(ObjectUtils.equals(invoiceOrderEntity.getCreateWay(),2) && ObjectUtils.equals(status,InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue())){
//            entity.setStatus(InvoiceRecordStatusEnum.COMPLETED.getValue());
//        }
        if(ObjectUtils.equals(status,InvoiceRecordStatusEnum.COMPLETED.getValue())){
            entity.setCompleteTime(new Date());
        }
        this.editByIdSelective(entity);
        addInvoiceRecordChange(entity,status,desc,updateUser);

    }

    /**
     * 根据订单号查询电子发票地址
     * @param orderNo
     * @return
     */
    @Override
    public String getInvoiceDetailImgUrlsByOrder(String orderNo){
        return mapper.getInvoiceDetailImgUrlsByOrder(orderNo);
    }

    /**
     * 根据订单号查询未完成的开票记录
     * @param orderNo
     * @param invoiceRecordNo
     * @return
     */
    @Override
    public int findUnfinishedInvoiceRecordByOrderNo(String orderNo,String invoiceRecordNo){
        //判断是否存在非已完成、已取消状态的开票记录
        Example example = new Example(InvoiceRecordEntity.class);
        example.createCriteria().andEqualTo("orderNo",orderNo)
                .andNotEqualTo("invoiceRecordNo",invoiceRecordNo)
                .andNotEqualTo("status",InvoiceRecordStatusEnum.COMPLETED.getValue())
                .andNotEqualTo("status",InvoiceRecordStatusEnum.CANCELED.getValue());
        int count = mapper.selectCountByExample(example);
        return count;
    }

    /**
     * 根据订单号获取最大出票时间
     * @param orderNo
     * @return
     */
    @Override
    public Date getMaxTicketTimeByOrderNo(String orderNo){
        InvoiceRecordEntity entity = new InvoiceRecordEntity();
        entity.setOrderNo(orderNo);
        List<InvoiceRecordEntity> list = mapper.select(entity);
        Date date = null;
        if(list!=null && list.size()>0){
            for (InvoiceRecordEntity vo : list){
                if(date == null && vo.getTicketTime() != null){
                    date = vo.getTicketTime();
                    continue;
                }
                if(vo.getTicketTime()!= null && DateUtil.diffDateTime(vo.getTicketTime(),date)>0){
                    date = vo.getTicketTime();
                }
            }
        }
        return date;
    }

    /**
     * 立即开票
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 订单号
     * @param addUser 添加人
     * @param isForce 是否强制开票 1-强制 0-正常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String openInvoice(String invoiceRecordNo,String orderNo,String addUser,String isForce){
        int count = queryLastQuarterRecordNumByOrderNo(orderNo);
        if(count>0){
            throw new BusinessException("该开票记录跨季，不能操作！");
        }
        InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
        invoiceRecordEntity.setInvoiceRecordNo(invoiceRecordNo);
        invoiceRecordEntity.setOrderNo(orderNo);
        invoiceRecordEntity = this.selectOne(invoiceRecordEntity);
        if(invoiceRecordEntity == null){
            throw new BusinessException("未找到相对应的开票记录");
        }
        //只有待提交，待补票，出票中断 和出票失败 可以调用开票接口
        if(!ObjectUtils.equals(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.TO_SUBMIT.getValue())
                &&!ObjectUtils.equals(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.FOR_TICKET.getValue())
                &&!ObjectUtils.equals(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.NTERRUPTION_OF_THE_DRAWER.getValue())
                &&!ObjectUtils.equals(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.THE_DRAWER_FAILURE.getValue()) ){
            throw new BusinessException("当前开票记录不能进行开票操作");
        }
        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if(orderEntity == null){
            throw new BusinessException("订单不存在");
        }
        if(orderEntity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()) { //自助开票、佣金开票、集团开票
            InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(orderNo);
            if (invoiceOrderEntity == null) {
                throw new BusinessException("开票订单不存在");
            }
            MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(invoiceOrderEntity.getCompanyId());
            if (memberCompanyEntity == null) {
                throw new BusinessException("未找打企业信息");
            }
            //判断企业的税务盘托管状态
            CompanyTaxHostingEntity companyTaxHostingEntity = companyTaxHostingService.getCompanyTaxHostingByCompanyId(invoiceOrderEntity.getCompanyId(),1);
            if (companyTaxHostingEntity != null) { //未托管默认人工线下处理
                //调用百旺接口
                String message = gotoBWInvoice(invoiceOrderEntity, invoiceRecordEntity, companyTaxHostingEntity, memberCompanyEntity.getEin(), addUser, isForce);
                if ("1".equals(message)) {
                    message = "库存不足";
                } else if ("2".equals(message)) {
                    message = "查询库存失败";
                }
                return message;
            } else {
                return "当前企业未托管税务盘，不能进行开票操作";
            }
        }else if(orderEntity.getOrderType().intValue() == OrderTypeEnum.CONSUMPTION_INVOICE.getValue()){//消费发票
            ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity=new ConsumptionInvoiceOrderEntity();
            consumptionInvoiceOrderEntity.setOrderNo(orderNo);
            List<ConsumptionInvoiceOrderEntity> consumptionInvoiceOrderList = consumptionInvoiceOrderService.select(consumptionInvoiceOrderEntity);
            if(consumptionInvoiceOrderList == null || consumptionInvoiceOrderList.size()>1){
                throw new BusinessException("消费开票订单不存在");
            }
            consumptionInvoiceOrderEntity = consumptionInvoiceOrderList.get(0);
            //实体转换
            InvoiceOrderEntity invoiceOrderEntity = new InvoiceOrderEntity();
            BeanUtil.copyProperties(consumptionInvoiceOrderEntity,invoiceOrderEntity);
            invoiceOrderEntity.setCreateWay(4); //设置为消费发票

            //获取oem机构的托管信息
            InvoiceInfoByOemEntity invoiceInfoByOemEntity = new InvoiceInfoByOemEntity();
            invoiceInfoByOemEntity.setOemCode(orderEntity.getOemCode());
            invoiceInfoByOemEntity = invoiceInfoByOemService.selectOne(invoiceInfoByOemEntity);
            if(invoiceInfoByOemEntity == null){
                throw new BusinessException("未找到oem机构的开票配置信息");
            }

            if(invoiceInfoByOemEntity.getHostingStatus().intValue() == 1){
                CompanyTaxHostingEntity companyTaxHostingEntity = new CompanyTaxHostingEntity();
                BeanUtil.copyProperties(invoiceInfoByOemEntity,companyTaxHostingEntity);
                companyTaxHostingEntity.setStatus(invoiceInfoByOemEntity.getHostingStatus());
                invoiceOrderEntity.setVatFeeRate(invoiceInfoByOemEntity.getVatRate().divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_UP));
                //调用百旺接口
                String message = gotoBWInvoice(invoiceOrderEntity, invoiceRecordEntity, companyTaxHostingEntity, invoiceInfoByOemEntity.getEin(), addUser, isForce);
                if ("1".equals(message)) {
                    message = "库存不足";
                } else if ("2".equals(message)) {
                    message = "查询库存失败";
                }
                return message;
            }else{
                return "当前企业未托管税务盘，不能进行开票操作";
            }
        }
        return "";
    }

    /**
     * 根据订单号查询跨季度的开票记录数
     * @param orderNo
     * @return
     */
    @Override
    public int queryLastQuarterRecordNumByOrderNo(String orderNo){
        //判断是否存在跨季的开票记录
        int year = DateUtil.getYear(new Date());
        String quarterNum = DateUtil.getQuarter();
        String[] quarterStartAndEndDate = DateUtil.getCurrQuarter(year,Integer.parseInt(quarterNum));
        String quarterStartDate = quarterStartAndEndDate[0];
        Example example = new Example(InvoiceRecordEntity.class);
        example.createCriteria().andEqualTo("orderNo",orderNo)
                .andNotEqualTo("status",InvoiceRecordStatusEnum.CANCELED)
                .andLessThan("ticketTime",quarterStartDate+" 00:00:00");
        Integer count = mapper.selectCountByExample(example);
        return count;
    }

    /**
     * 根据订单号查询跨季度的开票记录数
     * @param orderNo
     * @return
     */
    @Override
    public int queryLastQuarterRecordNumByOrderNo(String orderNo,Date tickTime){
        //判断是否存在跨季的开票记录
        int year = DateUtil.getYear(tickTime);
        String quarterNum = DateUtil.getQuarter(tickTime);
        String[] quarterStartAndEndDate = DateUtil.getCurrQuarter(year,Integer.parseInt(quarterNum));
        String quarterStartDate = quarterStartAndEndDate[0];
        String quarterEndDate = quarterStartAndEndDate[1];
        Example example = new Example(InvoiceRecordEntity.class);
        example.createCriteria().andEqualTo("orderNo",orderNo)
                .andNotEqualTo("status",InvoiceRecordStatusEnum.CANCELED)
                .andLessThanOrEqualTo("ticketTime",quarterStartDate+" 00:00:00")
                .andGreaterThanOrEqualTo ("ticketTime",quarterEndDate+" 23:59:59");
        Integer count = mapper.selectCountByExample(example);
        return count;
    }

    /**
     * 创建开票记录
     * @param invoiceOrderEntity
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createInvoiceRecord(InvoiceOrderEntity invoiceOrderEntity,CompanyTaxHostingEntity companyTaxHostingEntity,String taxNo, Long parkId,Long memberId,String addUser,boolean isImmediatelyInvoice){
        log.debug("创建开票记录----请求参数："+ JSONObject.toJSONString(invoiceOrderEntity)+",parkId"+parkId+",memberId:"+memberId);
        if(invoiceOrderEntity == null){
            throw new BusinessException("未找到开票订单数据");
        }
        //判断企业的税务盘托管状态
        InvoiceRecordEntity invoiceRecordEntity = null;
        if(companyTaxHostingEntity == null ){ //未托管默认人工线下处理
            invoiceRecordEntity = createEntity(invoiceOrderEntity,InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue(),1,parkId,memberId);
            invoiceRecordEntity.setInvoiceDesc("无税务盘");
            invoiceRecordEntity.setAddUser(addUser);
        }else if(ObjectUtil.equal(invoiceOrderEntity.getInvoiceType(),2)){ //专票 人工线下处理
            invoiceRecordEntity = createEntity(invoiceOrderEntity,InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue(),1,parkId,memberId);
            invoiceRecordEntity.setInvoiceDesc("专票只能人工处理");
            invoiceRecordEntity.setAddUser(addUser);
        }else{ //开票类型：普票 已托管
            invoiceRecordEntity = createEntity(invoiceOrderEntity,InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue(),1,parkId,memberId);
            invoiceRecordEntity.setAddUser(addUser);
            if(StringUtil.isBlank(taxNo)){
                throw new BusinessException("未找到企业信息或税号不存在");
            }
            if(ObjectUtil.equal(invoiceOrderEntity.getInvoiceWay(),2) && StringUtil.isBlank(invoiceOrderEntity.getEmail())){  //发票方式 电子发票校验邮箱地址是否存在
                throw new BusinessException("电子邮箱不能为空");
            }
            this.insertSelective(invoiceRecordEntity);
            addInvoiceRecordChange(invoiceRecordEntity,invoiceRecordEntity.getStatus(),invoiceRecordEntity.getInvoiceDesc(),invoiceRecordEntity.getAddUser());
            //TODO  园区托管不要生产开票记录明细数据  add ni.jiang start
//            //生成开票记录明细
//            String invoiceTypeCode = "007";
//            //发票方式：1->纸质发票；2-电子发票
//            if(ObjectUtil.equal(invoiceOrderEntity.getInvoiceWay(),1)){
//                //发票类型：1->普通发票；2-增值税发票
//                if(ObjectUtil.equal(invoiceOrderEntity.getInvoiceType(),1)){
//                    invoiceTypeCode = "007";
//                }else{
//                    invoiceTypeCode = "004";
//                }
//            }else  if(ObjectUtil.equal(invoiceOrderEntity.getInvoiceWay(),2)){
//                //发票类型：1->普通发票；2-增值税发票
//                if(ObjectUtil.equal(invoiceOrderEntity.getInvoiceType(),1)){
//                    invoiceTypeCode = "026";
//                }else{
//                    invoiceTypeCode = "028";
//                }
//            }
//            //创建开票明细
//            if(StringUtils.isNotBlank(invoiceOrderEntity.getCategoryName())) { //开票类目不为空，则按开票金额进行拆票
//                createRecordDetails(companyTaxHostingEntity, invoiceRecordEntity.getInvoiceRecordNo(), invoiceTypeCode, invoiceOrderEntity.getInvoiceAmount(),
//                        invoiceOrderEntity.getVatFeeRate(), addUser,null);
//            }else if(StringUtils.isBlank(invoiceOrderEntity.getCategoryName())&&StringUtils.isNotBlank(invoiceOrderEntity.getGoodsDetails())){
//                //带商品明细的开票明细重开
//                if(invoiceOrderEntity.getRecodeDetails()!=null && invoiceOrderEntity.getRecodeDetails().size()>0){
//                    List<InvoiceRecordDetailEntity> recodeDetails = invoiceOrderEntity.getRecodeDetails();
//                    //重新生成开票明细
//                    InvoiceRecordDetailEntity invoiceRecordDetailEntity = null;
//                    for (InvoiceRecordDetailEntity irdentity:  recodeDetails) {
//                        invoiceRecordDetailEntity = new InvoiceRecordDetailEntity();
//                        invoiceRecordDetailEntity.setInvoiceRecordNo(invoiceRecordEntity.getInvoiceRecordNo());
//                        invoiceRecordDetailEntity.setRequestNo(UUID.randomUUID().toString().replaceAll("-", ""));
//                        invoiceRecordDetailEntity.setInvoiceTypeCode(invoiceTypeCode);
//                        invoiceRecordDetailEntity.setStatus(5); //待出票
//                        invoiceRecordDetailEntity.setAddTime(new Date());
//                        invoiceRecordDetailEntity.setAddUser(addUser);
//                        invoiceRecordDetailEntity.setInvoiceTotalPrice(irdentity.getInvoiceTotalPrice());
//                        invoiceRecordDetailEntity.setInvoiceTotalTax(irdentity.getInvoiceTotalTax());
//                        invoiceRecordDetailEntity.setInvoiceTotalPriceTax(irdentity.getInvoiceTotalPriceTax());
//                        invoiceRecordDetailEntity.setGoodsDetails(irdentity.getGoodsDetails());
//                        invoiceRecordDetailEntity.setIsPrintDetail(irdentity.getIsPrintDetail());
//                        invoiceRecordDetailService.insertSelective(invoiceRecordDetailEntity);
//                    }
//                }else { //商品明细不为空，且开票类目为空，则按商品进行拆票
//                    InvoiceorderGoodsdetailRelaEntity relaEntity = new InvoiceorderGoodsdetailRelaEntity();
//                    relaEntity.setOrderNo(invoiceOrderEntity.getOrderNo());
//                    List<InvoiceorderGoodsdetailRelaEntity> list = invoiceorderGoodsdetailRelaService.select(relaEntity);
//                    if(list == null || list.size()<1){
//                        throw new BusinessException("未找到订单商品信息数据");
//                    }
//                    invoiceOrderEntity.setGoodsDetails(JSONObject.toJSONString(list));
//                    createRecordDetails(companyTaxHostingEntity, invoiceRecordEntity.getInvoiceRecordNo(), invoiceTypeCode, invoiceOrderEntity.getInvoiceAmount(),
//                            invoiceOrderEntity.getVatFeeRate(), addUser, invoiceOrderEntity.getGoodsDetails());
//                }
//            }
//            //判断是否季度最后截止时间
//            boolean  isEndDateFlag = isQuarterEndTime(parkId,invoiceOrderEntity.getInvoiceWay());
//            if(!isEndDateFlag && isImmediatelyInvoice) {
//                //调用百旺接口
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("invoiceRecordNo",invoiceRecordEntity.getInvoiceRecordNo());
//                jsonObject.put("orderNo",invoiceRecordEntity.getOrderNo());
//                jsonObject.put("updateUser",invoiceRecordEntity.getAddUser());
//                jsonObject.put("isForce",0);
//                rabbitTemplate.convertAndSend("openInvoicing",jsonObject);
////                try {
////                    String message = gotoBWInvoice(invoiceOrderEntity, invoiceRecordEntity, companyTaxHostingEntity, taxNo, addUser, "0");
////                    if (StringUtil.isNotBlank(message) && !"1".equals(message) && !"2".equals(message)) {
////                        //修改开票记录状态 待提交状态开票记录将状态改成 4-出票失败
////                        invoiceRecordEntity.setStatus(InvoiceRecordStatusEnum.THE_DRAWER_FAILURE.getValue());
////                        invoiceRecordEntity.setInvoiceDesc("出票失败," + message);
////                        invoiceRecordEntity.setUpdateTime(new Date());
////                        invoiceRecordEntity.setUpdateUser(addUser);
////                        this.editByIdSelective(invoiceRecordEntity);
////                        addInvoiceRecordChange(invoiceRecordEntity, invoiceRecordEntity.getStatus(), invoiceRecordEntity.getInvoiceDesc(), addUser);
////                        //修改明细状态
////                        invoiceRecordDetailService.updateByInvoiceRecordNo(invoiceRecordEntity.getInvoiceRecordNo(), 3);
////                    }
////                } catch (Exception e) {
////                    log.error("调用百旺开票接口失败,错误信息：" + e.getMessage());
////                    //修改开票记录状态 待提交状态开票记录将状态改成 4-出票失败
////                    invoiceRecordEntity.setStatus(InvoiceRecordStatusEnum.THE_DRAWER_FAILURE.getValue());
////                    invoiceRecordEntity.setInvoiceDesc("出票失败," + e.getMessage());
////                    invoiceRecordEntity.setUpdateTime(new Date());
////                    invoiceRecordEntity.setUpdateUser(addUser);
////                    this.editByIdSelective(invoiceRecordEntity);
////                    addInvoiceRecordChange(invoiceRecordEntity, invoiceRecordEntity.getStatus(), invoiceRecordEntity.getInvoiceDesc(), addUser);
////                    //修改明细状态
////                    invoiceRecordDetailService.updateByInvoiceRecordNo(invoiceRecordEntity.getInvoiceRecordNo(), 3);
////                }
////                return;
//            }
            //TODO  园区托管不要生产开票记录明细数据  add ni.jiang start
            return ;
        }
        this.insertSelective(invoiceRecordEntity);
        addInvoiceRecordChange(invoiceRecordEntity,invoiceRecordEntity.getStatus(),invoiceRecordEntity.getInvoiceDesc(),invoiceRecordEntity.getAddUser());
        //集团开票记录  状态为人工，线下的开票记录直接改成完成
//        if(ObjectUtil.equal(invoiceOrderEntity.getCreateWay(),InvoiceCreateWayEnum.GROUP.getValue())
//                && ObjectUtil.equal(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue())
//                && ObjectUtil.equal(invoiceRecordEntity.getHandlingWay(),1)){
//            updateInvoiceRecordStatus(invoiceRecordEntity.getInvoiceRecordNo(),invoiceOrderEntity.getOrderNo(),InvoiceRecordStatusEnum.COMPLETED.getValue(),
//                    "完成",null,addUser);
//        }
    }

    /**
     * 判断当前时间是否为季度最后自动开票截止时间
     * @param parkId 园区id
     * @param invoiceWay 开票方式 1-纸票 2-电票
     * @return
     */
    private boolean isQuarterEndTime(Long parkId,Integer invoiceWay){
        int year = DateUtil.getYear(new Date());
        String currQuarter = DateUtil.getQuarter();
        String[] quarterStartAndEndDate = DateUtil.getCurrQuarter(year,Integer.parseInt(currQuarter));
        String quarterEndDate = "";
        if(quarterStartAndEndDate!= null && quarterStartAndEndDate.length == 2){
            quarterEndDate = quarterStartAndEndDate[1];
        }
        if(StringUtils.isBlank(quarterEndDate)){
            return false;
        }
        String currDate = DateUtil.format(new Date(),"yyyy-MM-dd");
        if(currDate.equals(quarterEndDate)){ //当前日期为季度最后一天，则获取园区季度自动开票截止时间配置
            ParkEndtimeConfigEntity endtimeConfigEntity = new ParkEndtimeConfigEntity();
            endtimeConfigEntity.setParkId(parkId);
            endtimeConfigEntity.setInvoiceWay(invoiceWay);
            endtimeConfigEntity.setOperType(2);
            List<ParkEndtimeConfigEntity> configList = parkEndtimeConfigService.select(endtimeConfigEntity);
            if(configList != null && configList.size()>0){
                endtimeConfigEntity = configList.get(0);
                if(endtimeConfigEntity!= null && endtimeConfigEntity.getStartTime() !=null && endtimeConfigEntity.getEndTime() != null){
                    String startDateStr =DateUtil.format(endtimeConfigEntity.getStartTime(),DateUtil.TIMES_PATTERN) ;
                    String endDateStr =DateUtil.format(endtimeConfigEntity.getEndTime(),DateUtil.TIMES_PATTERN) ;
                    Long startDate = DateUtil.parseDate(currDate+" "+startDateStr,DateUtil.TIMESTAMP_PATTERN).getTime();
                    Long endDate = DateUtil.parseDate(currDate+" "+endDateStr,DateUtil.TIMESTAMP_PATTERN).getTime();
                    Long currTime = System.currentTimeMillis();
                    if(currTime>=startDate && currTime<= endDate){
                       return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 调用百旺开票接口
     * @param invoiceOrderEntity 开票订单实体
     * @param invoiceRecordEntity 开票记录实体
     * @param companyTaxHostingEntity 企业托管实体
     * @param taxNo  企业税号
     * @param updateUser 操作人
     * @param isForce 是否强制开票 1-强制 0-正常
     */
    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public String gotoBWInvoice(InvoiceOrderEntity invoiceOrderEntity,InvoiceRecordEntity invoiceRecordEntity,CompanyTaxHostingEntity companyTaxHostingEntity,
                              String taxNo,String updateUser,String isForce){
        Map<String,String> oemParams = getBWInvoiceParams(invoiceOrderEntity.getOemCode());
        if(StringUtils.isNotBlank(isForce) && "1".equals(isForce)){
            //查询开票明细数量
            List<Integer> statusList = new ArrayList<>();
            statusList.add(3);
            statusList.add(4);
            statusList.add(5);
            Example example = new Example(InvoiceRecordDetailEntity.class);
            example.createCriteria().andEqualTo("invoiceRecordNo",invoiceRecordEntity.getInvoiceRecordNo())
                    .andIn("status",statusList);
            //查询根据开票记录编号和状态为 3-出票失败、4-出票中的开票明细列表
            List<InvoiceRecordDetailEntity> detailEntityList = invoiceRecordDetailService.selectByExample(example);
            if(detailEntityList!= null && detailEntityList.size()>0) {
                invoiceIssue(invoiceOrderEntity,invoiceRecordEntity,companyTaxHostingEntity,detailEntityList,oemParams,taxNo,updateUser);
            }else{
                return "未找到需要开具的发票信息";
            }
        }else {
            //调用领用存接口
            Map<String, Object> inventoryMap = queryInventory(oemParams, taxNo, companyTaxHostingEntity.getTaxDiscCode(), invoiceOrderEntity.getInvoiceWay());
            if (inventoryMap.containsKey("totalSurplusNo")) {
                Integer totalSurplusNo = (Integer) inventoryMap.get("totalSurplusNo");
                //查询开票明细数量
                List<Integer> statusList = new ArrayList<>();
                statusList.add(3);
                statusList.add(4);
                statusList.add(5);
                Example example = new Example(InvoiceRecordDetailEntity.class);
                example.createCriteria().andEqualTo("invoiceRecordNo",invoiceRecordEntity.getInvoiceRecordNo())
                        .andIn("status",statusList);
                //查询根据开票记录编号和状态为 3-出票失败、4-出票中的开票明细列表
                List<InvoiceRecordDetailEntity> detailEntityList = invoiceRecordDetailService.selectByExample(example);
                if (detailEntityList!=null && detailEntityList.size() <= totalSurplusNo) { //票数充足
                    if(detailEntityList.size()<1){
                        return "未找到需要开具的发票信息";
                    }
                    invoiceIssue(invoiceOrderEntity, invoiceRecordEntity, companyTaxHostingEntity,detailEntityList,oemParams, taxNo, updateUser);
                } else {
                    //修改开票记录状态 将状态改成 2-待补票
                    invoiceRecordEntity.setStatus(InvoiceRecordStatusEnum.FOR_TICKET.getValue());
                    invoiceRecordEntity.setInvoiceDesc("库存不足");
                    invoiceRecordEntity.setUpdateTime(new Date());
                    invoiceRecordEntity.setUpdateUser(updateUser);
                    this.editByIdSelective(invoiceRecordEntity);
                    addInvoiceRecordChange(invoiceRecordEntity, invoiceRecordEntity.getStatus(), invoiceRecordEntity.getInvoiceDesc(), updateUser);
                    return "1";
                }
            } else {  //调用 领用存接口失败
                if (ObjectUtil.equal(invoiceRecordEntity.getStatus(), InvoiceRecordStatusEnum.TO_SUBMIT.getValue())) {
                    //修改开票记录状态 待提交状态开票记录将状态改成 4-出票失败
                    invoiceRecordEntity.setStatus(InvoiceRecordStatusEnum.THE_DRAWER_FAILURE.getValue());
                    invoiceRecordEntity.setInvoiceDesc("查询库存失败");
                    invoiceRecordEntity.setUpdateTime(new Date());
                    invoiceRecordEntity.setUpdateUser(updateUser);
                    this.editByIdSelective(invoiceRecordEntity);
                    addInvoiceRecordChange(invoiceRecordEntity, invoiceRecordEntity.getStatus(), invoiceRecordEntity.getInvoiceDesc(), updateUser);
                    return "2";
                } else {
                    throw new BusinessException(inventoryMap.get("errorMsg").toString());
                }
            }
        }
        return "";
    }

    /**
     * 查询百旺 发票领用存
     * @param oemParams oem机构参数
     * @param taxNo 税号
     * @param taxDiscCode 税务盘编号
     * @param invoiceWay 开票方式 1-纸质 2-电票
     * @return
     */
    @Override
    public Map<String,Object> queryInventory(Map<String,String> oemParams,String taxNo,String taxDiscCode,Integer invoiceWay){
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("taxNo", taxNo);
        orderMap.put("invoiceTerminalCode", taxDiscCode);
        if(ObjectUtil.equal(invoiceWay,1)){  //发票方式 纸质发票
            orderMap.put("invoiceTypeCode", "007");
        }else if(ObjectUtil.equal(invoiceWay,2)){ //发票方式 电子发票
            orderMap.put("invoiceTypeCode", "026");
        }
        Map<String,Object> resultMap = new HashMap<>();
        JSONObject jsonObject = BWElectricInvoiceUtil.queryInventory(orderMap,oemParams);
        if(jsonObject == null){
            resultMap.put("errorMsg","网络连接失败，请稍后再试！！！");
            return resultMap;
        }
        if(jsonObject!=null && "00".equals(jsonObject.getString("code"))){
            if(jsonObject.containsKey("data")) {
                JSONObject dataJsonObject =  jsonObject.getJSONObject("data");
                if("00".equals(dataJsonObject.getString("bizCode"))){
                    JSONArray jsonArray = dataJsonObject.getJSONArray("results");
                    if(jsonArray !=null){
                        int totalSurplusNo = 0; //计算发票份数
                        for (Object obj : jsonArray) {
                            JSONObject resultObj = (JSONObject) obj;
                            totalSurplusNo += resultObj.getInteger("surplusNo");
                        }
                        resultMap.put("totalSurplusNo",totalSurplusNo);
                    }else {
                        resultMap.put("totalSurplusNo",0);
                    }
                }else{
                    String errorMsg =dataJsonObject.getString("bizCodeMsg");
                    if(StringUtils.isBlank(errorMsg)){
                        errorMsg = "请求上游领用存接口失败，请稍后再试！";
                    }
                    resultMap.put("errorMsg",errorMsg);
                }
            }
        }else{
            String errorMsg = jsonObject.getString("message");
            if(StringUtils.isBlank(errorMsg)){
                errorMsg = "请求上游领用存接口失败，请稍后再试！";
            }
            resultMap.put("errorMsg",errorMsg);
        }
        return resultMap;
    }

    /**
     *  百旺开票
     * @param invoiceOrderEntity 开票订单实体
     * @param invoiceRecordEntity 开票记录实体
     * @param companyTaxHostingEntity 税务托管实体
     * @param oemParams 百旺参数配置
     * @param taxNo 开票企业税号
     * @param updateUser 操作人
     */
    @Override
    public void invoiceIssue(InvoiceOrderEntity invoiceOrderEntity,InvoiceRecordEntity invoiceRecordEntity,CompanyTaxHostingEntity companyTaxHostingEntity,
                                List<InvoiceRecordDetailEntity> detailEntityList, Map<String,String> oemParams,String taxNo,String updateUser){
        Map<String, Object> orderMap = new HashMap<>();
        if(invoiceOrderEntity.getCreateWay().intValue() !=4) { //非消费发票
            MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(invoiceOrderEntity.getCompanyId());
            if (memberCompanyEntity != null) {
                ParkEntity parkEntity = parkService.findById(memberCompanyEntity.getParkId());
                if (parkEntity == null) {
                    throw new BusinessException("未找到园区配置信息");
                } else if (parkEntity.getStatus() != 1) {
                    throw new BusinessException("园区已下架，暂不支持开票");
                } else if (StringUtil.isBlank(parkEntity.getDrawer()) || StringUtil.isBlank(parkEntity.getReviewer()) || StringUtil.isBlank(parkEntity.getPayee())) {
                    throw new BusinessException("园区未配置开票审核人信息");
                } else {
                    orderMap.put("drawer", parkEntity.getDrawer());
                    orderMap.put("checker", parkEntity.getReviewer());
                    orderMap.put("payee", parkEntity.getPayee());
                }
                //对公户账户 修改2.8v
                if (StringUtil.isNotBlank(invoiceOrderEntity.getCorporateAccountBankName()) && StringUtil.isNotBlank(invoiceOrderEntity.getCorporateAccount())) {
                    //销方开户行及账号
                    orderMap.put("sellerBankAccount", invoiceOrderEntity.getCorporateAccountBankName() + " " + invoiceOrderEntity.getCorporateAccount());
                }
                //销方地址及电话
//            orderMap.put("sellerAddressPhone",memberCompanyEntity.getBusinessAddress()+" "+memberCompanyEntity.getOperatorTel());
            }
        }else{ //消费发票
            InvoiceInfoByOemEntity invoiceInfoByOemEntity = new InvoiceInfoByOemEntity();
            invoiceInfoByOemEntity.setOemCode(invoiceOrderEntity.getOemCode());
            invoiceInfoByOemEntity = invoiceInfoByOemService.selectOne(invoiceInfoByOemEntity);
            if(invoiceInfoByOemEntity != null ){
                if (StringUtil.isNotBlank(invoiceInfoByOemEntity.getBankName()) && StringUtil.isNotBlank(invoiceInfoByOemEntity.getBankNumber())) {
                    //销方开户行及账号
                    orderMap.put("sellerBankAccount", invoiceInfoByOemEntity.getBankName() + " " + invoiceInfoByOemEntity.getBankNumber());
                }
            }
            OemConfigEntity oemConfigEntity = new OemConfigEntity();
            oemConfigEntity.setParamsCode("invoice_info_by_oem");
            oemConfigEntity.setOemCode(invoiceOrderEntity.getOemCode());
            List<OemConfigEntity> oemConfigEntities = oemConfigService.select(oemConfigEntity);
            if(oemConfigEntities==null || oemConfigEntities.size()<1){
                throw new BusinessException("该oem机构未配置消费发票开票人信息");
            }
            oemConfigEntity = oemConfigEntities.get(0);
            String invoiceInfo = oemConfigEntity.getParamsValue();
            JSONObject jsonObject = JSONObject.parseObject(invoiceInfo);
            orderMap.put("drawer", jsonObject.getString("drawer"));
            orderMap.put("checker", jsonObject.getString("reviewer"));
            orderMap.put("payee", jsonObject.getString("payee"));
        }
        orderMap.put("taxNo", taxNo);
        orderMap.put("invoiceTerminalCode", companyTaxHostingEntity.getTaxDiscCode());
        orderMap.put("invoiceType", "0"); //正票
        orderMap.put("isSplit", "0"); //拆分
        orderMap.put("buyerTaxNo",invoiceOrderEntity.getEin());
        orderMap.put("buyerName", invoiceOrderEntity.getCompanyName());
        orderMap.put("buyerAddressPhone", invoiceOrderEntity.getCompanyAddress()+" "+invoiceOrderEntity.getPhone());
        orderMap.put("buyerBankAccount",invoiceOrderEntity.getBankName()+" "+invoiceOrderEntity.getBankNumber());
        if(StringUtils.isNotBlank(invoiceOrderEntity.getInvoiceRemark())) {
            orderMap.put("remarks", invoiceOrderEntity.getInvoiceRemark());
        }else{
            orderMap.put("remarks", "");
        }
        orderMap.put("priceTaxMark",1); //是否含税  0-不含税 1-含税
        JSONArray array = null;
        JSONObject good = null;
        //获取开票类目编码
        InvoiceCategoryBaseEntity invoiceCategoryBaseEntity = null;
        if(invoiceOrderEntity.getCategoryId()!=null) {
            invoiceCategoryBaseEntity = invoiceCategoryBaseService.findById(invoiceOrderEntity.getCategoryId());
            if (invoiceCategoryBaseEntity == null) {
                throw new BusinessException("未找到开票类目信息");
            }
        }
//        good.put("goodsTotalPrice", MoneyUtil.fen2yuan(new BigDecimal(invoiceRecordEntity.getInvoiceTotalPrice())));  //不含税 传这个值

        JSONObject jsonObject = null;
        for(InvoiceRecordDetailEntity vo : detailEntityList){
            array = new JSONArray();
            good = new JSONObject();
            if(StringUtils.isBlank(vo.getGoodsDetails())) {
                good.put("goodsLineNo", 1);
                if ("*".equals(invoiceCategoryBaseEntity.getGoodsName().trim())) {
                    good.put("goodsName", invoiceOrderEntity.getGoodsName());
                } else {
                    String goodsName = invoiceOrderEntity.getCategoryName();
                    good.put("goodsName", goodsName.split("\\*")[1]);
                }
                good.put("goodsCode", invoiceCategoryBaseEntity.getTaxClassificationCode());
                orderMap.put("invoiceTypeCode", vo.getInvoiceTypeCode());
                orderMap.put("orderNo", vo.getRequestNo());
                orderMap.put("invoiceTotalPrice", MoneyUtil.fen2yuanStr(new BigDecimal(vo.getInvoiceTotalPrice())));
                orderMap.put("invoiceTotalTax", MoneyUtil.fen2yuanStr(new BigDecimal(vo.getInvoiceTotalTax())));
                orderMap.put("invoiceTotalPriceTax", MoneyUtil.fen2yuanStr(new BigDecimal(vo.getInvoiceTotalPriceTax())));

                good.put("goodsTotalPrice", MoneyUtil.fen2yuanStr(new BigDecimal(vo.getInvoiceTotalPriceTax()))); //含税  传这个
                good.put("goodsTaxRate", invoiceOrderEntity.getVatFeeRate());
                good.put("goodsTotalTax", MoneyUtil.fen2yuanStr(new BigDecimal(vo.getInvoiceTotalTax())));
                array.add(good);
            }else{
                List<InvoiceorderGoodsdetailRelaEntity> list = JSONObject.parseArray(vo.getGoodsDetails(),InvoiceorderGoodsdetailRelaEntity.class);
                InvoiceorderGoodsdetailRelaEntity entity = null;
                for(int i=0;i<list.size();i++){
                    entity = list.get(i);
                    good.put("goodsLineNo", i+1);
                    good.put("goodsName", entity.getGoodsName());
                    good.put("goodsCode", entity.getTaxClassificationCode());
                    good.put("goodsSpecification",entity.getGoodsSpecification());
                    good.put("goodsUnit",entity.getGoodsUnit());
                    good.put("goodsTotalPrice", MoneyUtil.fen2yuanStr(new BigDecimal(entity.getGoodsTotalPrice()))); //含税  传这个
                    good.put("goodsTaxRate", entity.getGoodsTaxRate());
                    good.put("goodsPrice",entity.getGoodsPrice().divide(new BigDecimal(100)).setScale(8,BigDecimal.ROUND_UP));
                    good.put("goodsQuantity",entity.getGoodsQuantity());
                    good.put("goodsTotalTax", MoneyUtil.fen2yuanStr(new BigDecimal(entity.getGoodsTotalTax())));
                    array.add(good);
                }
                if(ObjectUtils.equals(1,vo.getIsPrintDetail()) && (
                        "004".equals(vo.getInvoiceTypeCode()) || "007".equals(vo.getInvoiceTypeCode())
                )){
                    orderMap.put("invoiceListMark", 1); //专普票发票明细大于等于8行必须带清单）：大于8行必须为清单票(电子票只能为非请单票)
                }
                orderMap.put("invoiceTypeCode", vo.getInvoiceTypeCode());
                orderMap.put("orderNo", vo.getRequestNo());
                orderMap.put("invoiceTotalPrice", MoneyUtil.fen2yuanStr(new BigDecimal(vo.getInvoiceTotalPrice())));
                orderMap.put("invoiceTotalTax", MoneyUtil.fen2yuanStr(new BigDecimal(vo.getInvoiceTotalTax())));
                orderMap.put("invoiceTotalPriceTax", MoneyUtil.fen2yuanStr(new BigDecimal(vo.getInvoiceTotalPriceTax())));
            }
            orderMap.put("invoiceDetailsList", array.toJSONString());

            //通过mq，调用百旺开票接口
            jsonObject = new JSONObject();
            jsonObject.put("orderMap",orderMap);
            jsonObject.put("oemParams",oemParams);
            jsonObject.put("updateUser",updateUser);
            jsonObject.put("requestNo",vo.getRequestNo());
            jsonObject.put("oemCode",invoiceOrderEntity.getOemCode());
            log.info("发送mq调用百旺开票接口,参数："+jsonObject.toJSONString());
            rabbitTemplate.convertAndSend("bwInvoiceIssue",jsonObject);

            //将开票明细状态改成 出票中
            vo.setStatus(4);
            vo.setDetailDesc("出票中");
            vo.setUpdateTime(new Date());
            vo.setUpdateUser(updateUser);
            invoiceRecordDetailService.editByIdSelective(vo);
        }
        updateInvoiceRecordStatus(invoiceRecordEntity.getInvoiceRecordNo(),invoiceRecordEntity.getOrderNo(),InvoiceRecordStatusEnum.INVOICING.getValue(),
                        "出票中",null,updateUser);

    }
    /**
     *  百旺开票
     * @param orderMap 开票接口业务参数
     * @param oemParams 百旺参数配置
     * @param requestNo 请求号
     * @param addUser 操作人
     */
    @Override
    public String bwInvoiceIssue(Map<String,Object> orderMap,Map<String,String> oemParams,String requestNo,String addUser){
        JSONObject jsonObject = BWElectricInvoiceUtil.invoiceIssue(orderMap,oemParams);
        if(jsonObject == null){
            return "网络连接失败，请稍后再试";
        }
        if(jsonObject!=null && "00".equals(jsonObject.getString("code"))){
            if(jsonObject.containsKey("data")) {
                JSONObject dataJsonObject =  jsonObject.getJSONObject("data");
                if("00".equals(dataJsonObject.getString("bizCode"))){
                    //结果处理
                    try {
                        invoiceBWResultHandler(dataJsonObject, requestNo, addUser);
                    }catch (BusinessException e){
                        return e.getMessage();
                    }
                }else if("pp.002".equals(dataJsonObject.getString("bizCode"))){
                    //开具发票重复错误码，进行订单查询
                    JSONObject bwOrderObject = BWElectricInvoiceUtil.queryOrder(requestNo,oemParams);
                    if(bwOrderObject == null){
                        return "网络连接失败，请稍后再试";
                    }
                    if(bwOrderObject!=null && "00".equals(bwOrderObject.getString("code"))) {
                        if (bwOrderObject.containsKey("data")) {
                            JSONObject orderDataJsonObject = bwOrderObject.getJSONObject("data");
                            if ("00".equals(orderDataJsonObject.getString("bizCode"))) {
                                //结果处理
                                try {
                                    invoiceBWResultHandler(orderDataJsonObject,requestNo,addUser);
                                }catch (BusinessException e){
                                    return e.getMessage();
                                }
                            }else{
                                return "上游查询返回错误："+orderDataJsonObject.getString("bizCodeMsg");
                            }
                        }
                    }else{
                        return "上游查询返回错误："+bwOrderObject.getString("bizCodeMsg");
                    }
                }else{
                    return "上游开具发票返回错误："+dataJsonObject.getString("bizCodeMsg");
                }
            }
        }else{
            return "上游开具发票返回错误："+jsonObject.getString("message");
        }
        return "";
    }

    /**
     * 开票结果处理
     * @param dataJsonObject
     * @param requestNo
     * @param updateUser
     */
    public void invoiceBWResultHandler(JSONObject dataJsonObject,String requestNo,String updateUser){
        InvoiceRecordDetailEntity invoiceRecordDetailEntity = null;
        if(dataJsonObject.containsKey("invoiceFail")){  //开票失败列表
            invoiceRecordDetailEntity = conversionInvoiceRecordDetail(dataJsonObject.getJSONArray("invoiceFail"),updateUser,3);
            if(invoiceRecordDetailEntity!=null){
                invoiceRecordDetailEntity.setDetailDesc("出票失败");
            }
        }
        if(dataJsonObject.containsKey("invoiceSuccess")){ //开票成功列表
            invoiceRecordDetailEntity = conversionInvoiceRecordDetail( dataJsonObject.getJSONArray("invoiceSuccess"),updateUser,0);
            if(invoiceRecordDetailEntity!=null) {
                invoiceRecordDetailEntity.setDetailDesc("出票完成");
            }
        }
        if(dataJsonObject.containsKey("invoiceSuccess") && dataJsonObject.containsKey("invoiceFail")){
            if(invoiceRecordDetailEntity!=null) {
                invoiceRecordDetailEntity.setDetailDesc("出票失败，存在多个发票明细，请联系上游");
                invoiceRecordDetailEntity.setStatus(3);
            }
        }
        //更新开票明细
        InvoiceRecordDetailEntity entity = new InvoiceRecordDetailEntity();
        entity.setRequestNo(requestNo);
        List<InvoiceRecordDetailEntity> list = invoiceRecordDetailService.select(entity);
        if(list != null && list.size() == 1){
            if(invoiceRecordDetailEntity==null){
                throw new BusinessException("开票明细为空");
            }
            invoiceRecordDetailEntity.setId(list.get(0).getId());
            invoiceRecordDetailService.editByIdSelective(invoiceRecordDetailEntity);
            //出票明细成功后，更新开票记录的出票日期字段
            InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
            invoiceRecordEntity.setInvoiceRecordNo(list.get(0).getInvoiceRecordNo());
            invoiceRecordEntity = mapper.selectOne(invoiceRecordEntity);
            if(invoiceRecordEntity!=null && invoiceRecordDetailEntity.getStatus().intValue() == 0){
                invoiceRecordEntity.setTicketTime(new Date());
                invoiceRecordEntity.setUpdateTime(new Date());
                mapper.updateByPrimaryKeySelective(invoiceRecordEntity);
            }
        }else{
            throw new BusinessException("开票记录明细更新失败，原因：未找到或已找到多条开票记录明细[requestNo:"+requestNo+"]");
        }
    }

    /**
     * 调用版式文件并发送邮箱接口
     * @param detailList 税单明细列表
     * @param invoiceRecordEntity 开票记录
     * @param oemParams 请求参数
     * @param companyName 开票企业名称
     * @param taxNo 税号
     * @param  updateUser 修改人
     * @return
     */
    @Override
    public void formatCreate(List<InvoiceRecordDetailEntity> detailList,InvoiceRecordEntity invoiceRecordEntity,Map<String,String> oemParams,String companyName,String taxNo,String email,String updateUser){
        boolean flag = true; //标记是否全部处理完成
        StringBuffer stringBuffer = new StringBuffer();
        OemEntity oemEntity = oemService.getOem(invoiceRecordEntity.getOemCode());
        if(oemEntity == null){
            throw new BusinessException("未找到OEM机构信息");
        }
        if(oemParams == null){
            oemParams = getBWInvoiceParams(invoiceRecordEntity.getOemCode());
        }
        stringBuffer.append("<p>尊敬的 "+oemEntity.getOemName()+" 用户，您好：</p>\n" +
                "   <p>&nbsp;&nbsp;&nbsp;&nbsp; "+companyName+" 为您开具了电子发票，请您查收！</p>\n" +
                "   <br/>\n" +
                "   <table style=\"border: 1px #dcdcdc solid;width: 100%;text-align: center;border-collapse: collapse;\">\n" +
                "\t\t<tr style=\"line-height: 38px;\">\n" +
                "\t\t\t<td style=\"border:1px #dcdcdc solid;width:490px;\">订单编号</td>\n" +
                "\t\t\t<td style=\"border:1px #dcdcdc solid;\">"+invoiceRecordEntity.getOrderNo()+"</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr style=\"line-height: 38px;\">\n" +
                "\t\t\t<td style=\"border:1px #dcdcdc solid;width:490px;\">开票金额</td>\n" +
                "\t\t\t<td style=\"border:1px #dcdcdc solid;\">"+MoneyUtil.fen2yuanStr(new BigDecimal(invoiceRecordEntity.getInvoiceAmount()))+"元</td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr style=\"padding:16px 0px\">\n" +
                "\t\t\t<td style=\"border:1px #dcdcdc solid;width:490px;\">电子发票下载列表</td>\n" +
                "\t\t\t<td style=\"padding: 16px 0px;border:1px #dcdcdc solid;\">");
        int index = 1;
        for(InvoiceRecordDetailEntity vo:detailList){
            String url = gotoBWFormatCreate(oemParams,taxNo,vo.getInvoiceTradeNo(),0);
            if(StringUtil.isNotBlank(url)){
                vo.setEinvoiceUrl(url);
                vo.setUpdateTime(new Date());
                vo.setUpdateUser(updateUser);
//                invoiceRecordDetailService.editByIdSelective(vo);
                stringBuffer.append("<p style=\"line-height: 14px;\"> <a href='").append(vo.getEinvoiceUrl()).append("'>电子发票"+(index++)+".pdf</a></p>");
                //调用mq,保存发票信息到oss
                rabbitTemplate.convertAndSend("invoicePdf2Img",JSONObject.parseObject(JSONObject.toJSONString(vo)));
            }else{
                throw new BusinessException("请求号["+vo.getRequestNo()+"]版式生成失败");
            }
        }
        stringBuffer.append("</td></tr></table>");
        if(flag){ //版式文件全部生成成功，发送邮件
            try {
                InvoiceRecordDetailEntity invoiceRecordDetailEntity = null;
                List<InvoiceRecordDetailEntity> list = null;
                Long count = 0L;
                //判断版式文件是否已经生成完成
                while(true) {
                    list = invoiceRecordDetailService.findByInvoiceRecordNo(invoiceRecordEntity.getInvoiceRecordNo());
                    if(list != null&&list.size()>0) {
                        log.debug("查询发票记录版式文件生成结果=========："+list.size());
                        count = list.stream().filter(vo-> vo.getIsFormatCreate()!=null && vo.getIsFormatCreate().intValue()==3).count();
                        if(count>0L){
                            if(!ObjectUtil.equal(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.COMPLETED.getValue())) {
                                //修改开票记录状态
                                updateInvoiceRecordStatus(invoiceRecordEntity.getInvoiceRecordNo(), invoiceRecordEntity.getOrderNo(), InvoiceRecordStatusEnum.FAILED_TO_PUSH.getValue(), "版式文件生成失败", null, updateUser);
                            }else{
                                invoiceRecordEntity.setUpdateTime(new Date());
                                invoiceRecordEntity.setUpdateUser(updateUser);
                                invoiceRecordEntity.setInvoiceDesc("版式文件生成失败");
                                this.editByIdSelective(invoiceRecordEntity);
                                addInvoiceRecordChange(invoiceRecordEntity,InvoiceRecordStatusEnum.FAILED_TO_PUSH.getValue(),"版式文件生成失败",updateUser);
                            }
                            return;
                        }
                        count = list.stream().filter(vo-> vo.getIsFormatCreate()!=null && vo.getIsFormatCreate().intValue()==1).count();
                        if(count>0L){
                            try{Thread.sleep(3000L);}catch (Exception e){}
                            continue;
                        }else{
                            break;
                        }
                    }
                }
                OemParamsEntity oemParamsEntity = oemParamsService.getParams(invoiceRecordEntity.getOemCode(),OemParamsTypeEnum.EMAIL_CONFIG.getValue());
                Map<String,String> params = new HashMap<>();
                params.put("account",oemParamsEntity.getAccount());
                params.put("password",oemParamsEntity.getSecKey());
                params.put("emailHost",oemParamsEntity.getUrl());
                JSONObject jsonObject = JSONObject.parseObject(oemParamsEntity.getParamsValues());
                params.put("port",jsonObject.getString("port"));
                EmailUtils.send(params,"","电子发票下载",stringBuffer.toString(),email,null);
                //判断开票记录是否已完成，已完成的开票记录重新推送，不修改开票记录状态
                if(ObjectUtil.equal(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.COMPLETED.getValue())){
                    invoiceRecordEntity.setUpdateTime(new Date());
                    invoiceRecordEntity.setUpdateUser(updateUser);
                    this.editByIdSelective(invoiceRecordEntity);
                    addInvoiceRecordChange(invoiceRecordEntity,invoiceRecordEntity.getStatus(),"电子发票重新推送成功",updateUser);
                }else {
                    //推送成功，修改开票记录
                    updateInvoiceRecordStatus(invoiceRecordEntity.getInvoiceRecordNo(), invoiceRecordEntity.getOrderNo(), InvoiceRecordStatusEnum.COMPLETED.getValue(), "已完成", null, updateUser);
                }
            }catch (Exception e){
                if(ObjectUtil.equal(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.COMPLETED.getValue())){
                    invoiceRecordEntity.setUpdateTime(new Date());
                    invoiceRecordEntity.setUpdateUser(updateUser);
                    this.editByIdSelective(invoiceRecordEntity);
                    addInvoiceRecordChange(invoiceRecordEntity,InvoiceRecordStatusEnum.FAILED_TO_PUSH.getValue(),"电子发票重新推送失败",updateUser);
                }else {
                    log.error(e.getMessage(), e);
                    //修改开票记录状态
                    updateInvoiceRecordStatus(invoiceRecordEntity.getInvoiceRecordNo(), invoiceRecordEntity.getOrderNo(), InvoiceRecordStatusEnum.FAILED_TO_PUSH.getValue(), "电子发票发送失败", null, updateUser);
                }
            }
        }else{
            if(ObjectUtil.equal(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.COMPLETED.getValue())){
                invoiceRecordEntity.setUpdateTime(new Date());
                invoiceRecordEntity.setUpdateUser(updateUser);
                this.editByIdSelective(invoiceRecordEntity);
                addInvoiceRecordChange(invoiceRecordEntity,InvoiceRecordStatusEnum.FAILED_TO_PUSH.getValue(),"电子发票重新推送失败",updateUser);
            }else {
                //推送失败，修改开票记录
                updateInvoiceRecordStatus(invoiceRecordEntity.getInvoiceRecordNo(),invoiceRecordEntity.getOrderNo(),InvoiceRecordStatusEnum.FAILED_TO_PUSH.getValue(),"版式文件生成失败",null,updateUser);
            }
        }
    }

    /**
     * 调用百旺版式接口，调用接口重试3次，每次间隔10s
     * @param oemParams
     * @param taxNo 税号
     * @param seriaNo 发票流水号
     * @param num 重复次数
     * @return
     */
    private String gotoBWFormatCreate(Map<String,String> oemParams,String taxNo,String seriaNo,int num){
        if(num>2){
            return "";
        }
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("taxNo", taxNo);
        orderMap.put("serialNo",seriaNo);
        JSONObject jsonObject = BWElectricInvoiceUtil.formatCreate(orderMap,oemParams);
        if(jsonObject!=null && "00".equals(jsonObject.getString("code"))) {
            if (jsonObject.containsKey("data")) {
                JSONObject dataJsonObject = jsonObject.getJSONObject("data");
                if ("00".equals(dataJsonObject.getString("bizCode"))) {
                   String url = dataJsonObject.getString("eInvoiceUrl");
                   if(StringUtil.isNotBlank(url)){
                       return url;
                   }
                }else{
                    try{
                        Thread.sleep(10*1000L);
                    }catch (Exception e){}finally {
                        gotoBWFormatCreate(oemParams,taxNo,seriaNo,++num);
                    }
                }
            }
        }else{
            try{
                Thread.sleep(10*1000L);
            }catch (Exception e){}finally {
                gotoBWFormatCreate(oemParams,taxNo,seriaNo,++num);
            }
        }
        return "";
    }

    /**
     * 调用百旺打印接口
     * @param detailId 开票明细id
     * @param oemCode oem机构编码
     * @param printType 0：发票打印，1：清单打印
     * @param updateUser
     */
    @Override
    public String printBWInvoice(Long detailId,String oemCode,String printType,String updateUser){
        if(detailId == null){
            throw new BusinessException("发票id不能为空");
        }
        InvoiceRecordDetailEntity invoiceRecordDetailEntity = invoiceRecordDetailService.findById(detailId);
        if(invoiceRecordDetailEntity == null){
            throw new BusinessException("未找到对应的发票信息");
        }
        Map<String,String> oemParams = getBWInvoiceParams(oemCode);
        if(oemParams == null){
            throw new BusinessException("未找到OEM机构配置信息");
        }
        InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
        invoiceRecordEntity.setInvoiceRecordNo(invoiceRecordDetailEntity.getInvoiceRecordNo());
        invoiceRecordEntity = selectOne(invoiceRecordEntity);
        String invoiceTerminalCode = "";
        String taxNo = "";
        if(invoiceRecordEntity != null){
            OrderEntity orderEntity = orderService.queryByOrderNo(invoiceRecordEntity.getOrderNo());
            if(orderEntity == null){
                throw new BusinessException("未找到订单信息");
            }
            if(orderEntity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()) { //开票订单
                CompanyTaxHostingEntity entity = companyTaxHostingService.getCompanyTaxHostingByCompanyId(invoiceRecordEntity.getCompanyId(),1);
                if (entity != null) {
                    invoiceTerminalCode = entity.getTaxDiscCode();
                } else {
                    throw new BusinessException("未找到企业托管配置信息");
                }
                MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(invoiceRecordEntity.getCompanyId());
                if(memberCompanyEntity != null){
                    taxNo = memberCompanyEntity.getEin();
                }
            }else if(orderEntity.getOrderType().intValue() == OrderTypeEnum.CONSUMPTION_INVOICE.getValue()){ //消费开票
                InvoiceInfoByOemEntity invoiceInfoByOemEntity = new InvoiceInfoByOemEntity();
                invoiceInfoByOemEntity.setOemCode(orderEntity.getOemCode());
                invoiceInfoByOemEntity = invoiceInfoByOemService.selectOne(invoiceInfoByOemEntity);
                if(invoiceInfoByOemEntity == null){
                    throw new BusinessException("未找到oem机构开票配置信息");
                }
                invoiceTerminalCode = invoiceInfoByOemEntity.getTaxDiscCode();
                taxNo = invoiceInfoByOemEntity.getEin();
            }
        }else{
            throw new BusinessException("未找到开票记录信息");
        }
        if(StringUtils.isBlank(taxNo)){
            throw new BusinessException("未找到开票企业信息");
        }
        if(StringUtils.isBlank(invoiceTerminalCode)){
            throw new BusinessException("税控盘编号未找到，请联系运营进行确认");
        }
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("taxNo", taxNo);
        orderMap.put("invoiceTerminalCode",invoiceTerminalCode);
        orderMap.put("invoiceTypeCode", invoiceRecordDetailEntity.getInvoiceTypeCode());
        orderMap.put("invoiceCode", invoiceRecordDetailEntity.getInvoiceCode());
        orderMap.put("invoiceNo", invoiceRecordDetailEntity.getInvoiceNo());
        orderMap.put("printType", printType);
        JSONObject jsonObject = BWElectricInvoiceUtil.printInvoice(orderMap,oemParams);
        if(jsonObject == null){
            return "网络连接失败，请稍后再试";
        }
        if(jsonObject!=null && "00".equals(jsonObject.getString("code"))) {
            if (jsonObject.containsKey("data")) {
                JSONObject dataJsonObject = jsonObject.getJSONObject("data");
                if ("00".equals(dataJsonObject.getString("bizCode"))) {
                    invoiceRecordDetailEntity.setStatus(1); //已打印
                    invoiceRecordDetailEntity.setUpdateUser(updateUser);
                    invoiceRecordDetailEntity.setUpdateTime(new Date());
                    invoiceRecordDetailEntity.setDetailDesc("打印完成");
                    invoiceRecordDetailService.editByIdSelective(invoiceRecordDetailEntity);
                } else {
                    String errorMsg = "上游返回数据异常，请稍后再试";
                    if(jsonObject.containsKey("message")){
                        errorMsg = dataJsonObject.getString("bizCodeMsg");
                    }
                    invoiceRecordDetailEntity.setUpdateUser(updateUser);
                    invoiceRecordDetailEntity.setUpdateTime(new Date());
                    invoiceRecordDetailEntity.setDetailDesc(errorMsg);
                    invoiceRecordDetailService.editByIdSelective(invoiceRecordDetailEntity);
                    return errorMsg;
                }
            }else{
                return "请求上游渠道异常，请稍后再试！";
            }
        }else{
            String errorMsg = "上游返回数据异常，请稍后再试";
            if(jsonObject.containsKey("message")){
                errorMsg = jsonObject.getString("message");
            }
            invoiceRecordDetailEntity.setUpdateUser(updateUser);
            invoiceRecordDetailEntity.setUpdateTime(new Date());
            invoiceRecordDetailEntity.setDetailDesc(errorMsg);
            invoiceRecordDetailService.editByIdSelective(invoiceRecordDetailEntity);
            return errorMsg;
        }
        return "";
    }

    /**
     * 开票记录明细打印
     * @param detailId
     */
    @Override
    public String invoiceDetailPrint(Long detailId,String invoiceRecordNo,String printType,String updateUser){
        InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
        invoiceRecordEntity.setInvoiceRecordNo(invoiceRecordNo);
        List<InvoiceRecordEntity> list = this.select(invoiceRecordEntity);
        if(list== null || list.size() != 1){
            return "开票记录编号错误";
        }else {
            String message = printBWInvoice(detailId,list.get(0).getOemCode(),printType,updateUser);
            return message;
        }
    }

    /**
     * 作废开票明细
     * @param detailIds 明细ids，多个开票记录id之间用 逗号分隔
     * @param updateUser 操作人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invoiceDetailInvalid(String detailIds,String updateUser){
        String[] ids = detailIds.split(",");
        InvoiceRecordDetailEntity invoiceRecordDetailEntity;
        Long invoiceRecordAmount = 0L;
        String invoiceRecordNo = "";
        //去除重复id
        Set<String> idsSet = new HashSet<>();
        List<InvoiceRecordDetailEntity> recodeDetails = new ArrayList<>(); //保存需要作废重开的发票明细
        Collections.addAll(idsSet, ids);
        for (String id:idsSet){
            invoiceRecordDetailEntity = invoiceRecordDetailService.findById(Long.valueOf(id));
            if(invoiceRecordDetailEntity == null){
                throw new BusinessException("未找到需要作废得开票记录明细");
            }else{
                invoiceRecordAmount += invoiceRecordDetailEntity.getInvoiceTotalPriceTax();
                if(StringUtils.isBlank(invoiceRecordNo)){
                    invoiceRecordNo = invoiceRecordDetailEntity.getInvoiceRecordNo();
                }
            }
            invoiceRecordDetailEntity.setStatus(2);
            invoiceRecordDetailEntity.setDetailDesc("发票作废");
            invoiceRecordDetailEntity.setUpdateTime(new Date());
            invoiceRecordDetailEntity.setUpdateUser(updateUser);
            invoiceRecordDetailService.editByIdSelective(invoiceRecordDetailEntity);
            recodeDetails.add(invoiceRecordDetailEntity);
        }
        if(StringUtils.isBlank(invoiceRecordNo)){
            throw new BusinessException("未找到开票记录编号");
        }

        //重新添加开票记录
        InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
        invoiceRecordEntity.setInvoiceRecordNo(invoiceRecordNo);
        List<InvoiceRecordEntity> invoiceRecordList = this.select(invoiceRecordEntity);
        if(invoiceRecordList == null || invoiceRecordList.size()!= 1){
            throw new BusinessException("未找到开票记录");
        }else{
            invoiceRecordEntity = invoiceRecordList.get(0);
        }
        OrderEntity orderEntity = orderService.queryByOrderNo(invoiceRecordEntity.getOrderNo());
        if(orderEntity == null){
            throw new BusinessException("未找到订单信息");
        }
        if(orderEntity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()) { //开票订单
            //查询开票订单
            InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(invoiceRecordEntity.getOrderNo());
            if(invoiceOrderEntity == null){
                throw new BusinessException("未找到开票订单信息");
            }
            invoiceOrderEntity.setInvoiceAmount(invoiceRecordAmount);
            //查询企业
            MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(invoiceRecordEntity.getCompanyId());
            if(memberCompanyEntity == null){
                throw new BusinessException("未找到开票企业信息");
            }
            //查询企业托管信息
            CompanyTaxHostingEntity companyTaxHostingEntity = companyTaxHostingService.getCompanyTaxHostingByCompanyId(invoiceOrderEntity.getCompanyId(),1);
            if(StringUtils.isNotBlank(invoiceOrderEntity.getGoodsDetails())){
                invoiceOrderEntity.setRecodeDetails(recodeDetails);
            }
            createInvoiceRecord(invoiceOrderEntity,companyTaxHostingEntity,memberCompanyEntity.getEin(), memberCompanyEntity.getParkId(), memberCompanyEntity.getMemberId(), updateUser,true);
        }else if(orderEntity.getOrderType().intValue() == OrderTypeEnum.CONSUMPTION_INVOICE.getValue()){  //消费开票
            consumptionInvoiceOrderService.createConsumptionInvoiceRecord(orderEntity.getOrderNo(),invoiceRecordAmount,updateUser);
        }
        //判断是否全部作废
        InvoiceRecordDetailEntity entity = new InvoiceRecordDetailEntity();
        entity.setInvoiceRecordNo(invoiceRecordNo);
        List<InvoiceRecordDetailEntity> list = invoiceRecordDetailService.select(entity);
        if(list!= null && list.size() == idsSet.size()){
            updateInvoiceRecordStatus(invoiceRecordNo,orderEntity.getOrderNo(),InvoiceRecordStatusEnum.CANCELED.getValue(),"已作废",null,updateUser);
        }
    }

    @Override
    public List<InvoiceRecordEntity> queryGroupInvoiceOrderByGroupOrderNo(String orderNo,String status,String notCompleted) {
        return mapper.queryGroupInvoiceOrderByGroupOrderNo(orderNo,status,notCompleted);
    }


    @Override
    public void updateInvoiceRecordStatusByGroupOrderNoAndStatuss(String groupOrderNo, Integer status, String desc, String updateUser, Date updateTime, String statuss, String notStatuss) {
        mapper.updateInvoiceRecordStatusByGroupOrderNoAndStatuss( groupOrderNo,  status,  desc,  updateUser,  updateTime,  statuss,  notStatuss);
        //添加变更记录
        List<InvoiceRecordEntity> list =mapper.queryGroupInvoiceOrderByGroupOrderNo(groupOrderNo,statuss,notStatuss);
        for (InvoiceRecordEntity entity:list) {
            addInvoiceRecordChange(entity,status,desc,updateUser);
        }
    }

    @Override
    public List<InvoiceRecordEntity> queryInvoiceRecordByOrderNoAndStatus(String orderNo, String statuss) {
        return mapper.queryInvoiceRecordByOrderNoAndStatus(orderNo,statuss);
    }


    /**
     * 线下开票
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @param updateUser 操作人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offlineInvoice(String invoiceRecordNo,String orderNo,String updateUser){
        InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
        invoiceRecordEntity.setInvoiceRecordNo(invoiceRecordNo);
        invoiceRecordEntity.setOrderNo(orderNo);
        invoiceRecordEntity = this.selectOne(invoiceRecordEntity);
        if(invoiceRecordEntity == null){
            throw new BusinessException("未找到相对应的开票记录");
        }
        //只有出票失败 可以转线下开票
        if( !ObjectUtils.equals(invoiceRecordEntity.getStatus(), InvoiceRecordStatusEnum.TO_SUBMIT.getValue())
                &&!ObjectUtils.equals(invoiceRecordEntity.getStatus(), InvoiceRecordStatusEnum.FOR_TICKET.getValue())
                &&!ObjectUtils.equals(invoiceRecordEntity.getStatus(), InvoiceRecordStatusEnum.THE_DRAWER_FAILURE.getValue())){
            throw new BusinessException("当前开票记录不能进行线下开票操作");
        }
        updateInvoiceRecordStatus(invoiceRecordNo, orderNo, InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue(), "手动变更为人工出票", 1, updateUser);

    }

    /**
     * 强制成功
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @param updateUser 操作人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forceSuccess(String invoiceRecordNo,String orderNo,String updateUser){
        //判断是否存在跨季的开票记录
        int count = queryLastQuarterRecordNumByOrderNo(orderNo);
        if(count>0){
            throw new BusinessException("该开票记录跨季，不能操作！");
        }
        InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
        invoiceRecordEntity.setInvoiceRecordNo(invoiceRecordNo);
        invoiceRecordEntity.setOrderNo(orderNo);
        invoiceRecordEntity = this.selectOne(invoiceRecordEntity);
        if(invoiceRecordEntity == null){
            throw new BusinessException("未找到相对应的开票记录");
        }
        //只有出票中断状态可以进行强制成功
        if(!ObjectUtils.equals(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.NTERRUPTION_OF_THE_DRAWER.getValue())){
            throw new BusinessException("当前开票记录不能进行线下开票操作");
        }

        OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
        if(orderEntity == null){
            throw new BusinessException("未找到相对应订单");
        }

        InvoiceOrderEntity invoiceOrderEntity = null;
        if(orderEntity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()){
            //判断是否需要修改开票订单
            invoiceOrderEntity = invoiceOrderService.queryByOrderNo(orderNo);
        }else if(orderEntity.getOrderType().intValue() == OrderTypeEnum.CONSUMPTION_INVOICE.getValue()){
            ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity = new ConsumptionInvoiceOrderEntity();
            consumptionInvoiceOrderEntity.setOrderNo(orderNo);
            consumptionInvoiceOrderEntity.setOemCode(orderEntity.getOemCode());
            consumptionInvoiceOrderEntity = consumptionInvoiceOrderService.selectOne(consumptionInvoiceOrderEntity);
            if(consumptionInvoiceOrderEntity!=null){
                invoiceOrderEntity = new InvoiceOrderEntity();
                BeanUtil.copyProperties(consumptionInvoiceOrderEntity,invoiceOrderEntity);
                invoiceOrderEntity.setCreateWay(4);
                invoiceOrderEntity.setEmail(consumptionInvoiceOrderEntity.getBillToEmail());
                InvoiceInfoByOemEntity invoiceInfoByOemEntity = new InvoiceInfoByOemEntity();
                invoiceInfoByOemEntity.setOemCode(orderEntity.getOemCode());
                invoiceInfoByOemEntity = invoiceInfoByOemService.selectOne(invoiceInfoByOemEntity);
                if(invoiceInfoByOemEntity == null){
                    throw new BusinessException("未找到消费开票配置信息");
                }
                invoiceOrderEntity.setVatFeeRate(invoiceInfoByOemEntity.getVatRate().divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_UP));
            }
        }
        if(invoiceOrderEntity == null){
            throw new BusinessException("未找到相对应开票订单");
        }
        //失败部分生产新的开票记录，处理方式为线下，状态为人工出票
        InvoiceRecordDetailEntity recordDetailEntity = new InvoiceRecordDetailEntity();
        recordDetailEntity.setInvoiceRecordNo(invoiceRecordNo);
        recordDetailEntity.setStatus(3);
        List<InvoiceRecordDetailEntity> failDetailList = invoiceRecordDetailService.select(recordDetailEntity);
        if(failDetailList != null) {
            Long amount = failDetailList.stream().mapToLong(InvoiceRecordDetailEntity::getInvoiceTotalPriceTax).sum();
            invoiceOrderEntity.setInvoiceAmount(amount);
            InvoiceRecordEntity invoiceRecordEntityNew = createEntity(invoiceOrderEntity, InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue(), 1, orderEntity.getParkId(), orderEntity.getUserId());
            invoiceRecordEntityNew.setAddUser(updateUser);
            this.insertSelective(invoiceRecordEntityNew);
            addInvoiceRecordChange(invoiceRecordEntityNew, invoiceRecordEntityNew.getStatus(), invoiceRecordEntityNew.getInvoiceDesc(), updateUser);
        }

        //纸票，将开票记录改成 待确认
        if(ObjectUtils.equals(invoiceOrderEntity.getInvoiceWay(),1)){
            updateInvoiceRecordStatus(invoiceRecordNo, orderNo, InvoiceRecordStatusEnum.TO_BE_CONFIRMED.getValue(), "强制成功", null, updateUser);
        }else if(ObjectUtils.equals(invoiceOrderEntity.getInvoiceWay(),2)){ //电票 调用推送接口，进行推送
            //查询开票明细，状态为正常的明细数据
            InvoiceRecordDetailEntity invoiceRecordDetailEntity = new InvoiceRecordDetailEntity();
            invoiceRecordDetailEntity.setInvoiceRecordNo(invoiceRecordNo);
            invoiceRecordDetailEntity.setStatus(0);
            List<InvoiceRecordDetailEntity> list = invoiceRecordDetailService.select(invoiceRecordDetailEntity);
            if(orderEntity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()) {
                MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(invoiceOrderEntity.getCompanyId());
                if (memberCompanyEntity == null || StringUtils.isBlank(memberCompanyEntity.getEin())) {
                    throw new BusinessException("企业税号不存在");
                }
                //调用版本生成并推送邮件
                formatCreate(list, invoiceRecordEntity, null, memberCompanyEntity.getCompanyName(), memberCompanyEntity.getEin(), invoiceOrderEntity.getEmail(), updateUser);
            }else if(orderEntity.getOrderType().intValue() == OrderTypeEnum.CONSUMPTION_INVOICE.getValue()){
                InvoiceInfoByOemEntity invoiceInfoByOemEntity = new InvoiceInfoByOemEntity();
                invoiceInfoByOemEntity.setOemCode(orderEntity.getOemCode());
                invoiceInfoByOemEntity = invoiceInfoByOemService.selectOne(invoiceInfoByOemEntity);
                if(invoiceInfoByOemEntity == null){
                    throw new BusinessException("未找到相对应oem机构开票信息");
                }
                invoiceOrderEntity.setVatFeeRate(invoiceInfoByOemEntity.getVatRate().divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN));
                //调用版本生成并推送邮件
                formatCreate(list, invoiceRecordEntity, null, invoiceInfoByOemEntity.getCompanyName(), invoiceInfoByOemEntity.getEin(), invoiceOrderEntity.getEmail(), updateUser);
            }else{
                throw new BusinessException("开票方式不正确");
            }
        }else{
            throw new BusinessException("发票方式不正确");
        }
    }

    /**
     * 重新推送
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @param updateUser 操作人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendAgain(String invoiceRecordNo,String orderNo,String updateUser){
        String redisTime = (System.currentTimeMillis() + 1000*180) + "";
        boolean lockResult = redisService.lock(RedisKey.LOCK_INVOICE_PUSH_BY_INVOICERECORDNO + invoiceRecordNo, redisTime, 180);
        if(!lockResult){
            throw new BusinessException("请勿重复提交！");
        }
        try {
            InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
            invoiceRecordEntity.setInvoiceRecordNo(invoiceRecordNo);
            invoiceRecordEntity.setOrderNo(orderNo);
            invoiceRecordEntity = this.selectOne(invoiceRecordEntity);
            if(invoiceRecordEntity == null){
                throw new BusinessException("未找到相对应的开票记录");
            }
            //只有出票中断状态可以进行强制成功
            if(!ObjectUtils.equals(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.FAILED_TO_PUSH.getValue())
                    && !ObjectUtils.equals(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.COMPLETED.getValue())
                    && !ObjectUtils.equals(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.INVOICING.getValue())){
                throw new BusinessException("当前开票记录不能进行重新推送操作");
            }
            //查询开票明细，状态为正常的明细数据
            InvoiceRecordDetailEntity invoiceRecordDetailEntity = new InvoiceRecordDetailEntity();
            invoiceRecordDetailEntity.setInvoiceRecordNo(invoiceRecordNo);
            invoiceRecordDetailEntity.setStatus(0);
            List<InvoiceRecordDetailEntity> list = invoiceRecordDetailService.select(invoiceRecordDetailEntity);

            OrderEntity orderEntity = orderService.queryByOrderNo(orderNo);
            if(orderEntity == null){
                throw new BusinessException("未找到相对应订单信息");
            }
            if(orderEntity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()) {  //开票订单
                //判断是否需要修改开票订单
                InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(orderNo);
                if (invoiceOrderEntity == null) {
                    throw new BusinessException("未找到相对应开票订单");
                }
                MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(invoiceOrderEntity.getCompanyId());
                if (memberCompanyEntity == null || StringUtils.isBlank(memberCompanyEntity.getEin())) {
                    throw new BusinessException("企业税号不存在");
                }
                //调用版本生成并推送邮件
                formatCreate(list, invoiceRecordEntity, null,memberCompanyEntity.getCompanyName(), memberCompanyEntity.getEin(), invoiceOrderEntity.getEmail(), updateUser);
            }else if(orderEntity.getOrderType().intValue() == OrderTypeEnum.CONSUMPTION_INVOICE.getValue()){ //消费订单
                ConsumptionInvoiceOrderEntity entity = new ConsumptionInvoiceOrderEntity();
                entity.setOrderNo(orderNo);
                entity.setOemCode(orderEntity.getOemCode());
                entity = consumptionInvoiceOrderService.selectOne(entity);
                if(entity == null){
                    throw new BusinessException("未找到相对应消费开票订单");
                }
                InvoiceInfoByOemEntity invoiceInfoByOemEntity = new InvoiceInfoByOemEntity();
                invoiceInfoByOemEntity.setOemCode(orderEntity.getOemCode());
                invoiceInfoByOemEntity = invoiceInfoByOemService.selectOne(invoiceInfoByOemEntity);
                if(invoiceInfoByOemEntity == null){
                    throw new BusinessException("未找到相对应oem机构开票信息");
                }
                //调用版本生成并推送邮件
                formatCreate(list, invoiceRecordEntity, null,invoiceInfoByOemEntity.getCompanyName(), invoiceInfoByOemEntity.getEin(), entity.getBillToEmail(), updateUser);
            }
        }catch (BusinessException e){
            throw new BusinessException(e.getMessage());
        }finally {
            redisService.unlock(RedisKey.LOCK_INVOICE_PUSH_BY_INVOICERECORDNO + invoiceRecordNo, redisTime);
        }
    }

    /**
     * 开票记录明细
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     */
    @Override
    public List<InvoiceRecordDetailVO> invoiceRecordDetailList(String invoiceRecordNo, String orderNo){
        List<InvoiceRecordDetailVO> list = invoiceRecordDetailService.invoiceRecordDetailList(invoiceRecordNo,orderNo);
        if(list != null){
            list.forEach(vo->{
                if(StringUtils.isNotBlank(vo.getEInvoiceOssImgUrl())){
                    vo.setEInvoiceOssImgUrl(ossService.getPrivateImgUrl(vo.getEInvoiceOssImgUrl()));
                }
            });
        }
        return list;
    }

    /**
     * 生成开票记录明细
     * @param invoiceResultArray
     * @param updateUser
     * @param status 0-正常，1-已打印，2-已作废，3-出票失败
     * @return
     */
    private InvoiceRecordDetailEntity conversionInvoiceRecordDetail(JSONArray invoiceResultArray,String updateUser,Integer status){
        InvoiceRecordDetailEntity invoiceRecordDetailEntity = null;
        if(invoiceResultArray !=null){
            invoiceRecordDetailEntity = new InvoiceRecordDetailEntity();
            JSONObject resultObj = (JSONObject) invoiceResultArray.get(0);
            invoiceRecordDetailEntity.setInvoiceCode(resultObj.getString("invoiceCode"));
            invoiceRecordDetailEntity.setInvoiceNo(resultObj.getString("invoiceNo"));
            invoiceRecordDetailEntity.setInvoiceCheckCode(resultObj.getString("invoiceCheckCode"));
            String invoiceDateStr = resultObj.getString("invoiceDate");
            if(StringUtil.isNotBlank(invoiceDateStr)){
                Date invoiceDate = DateUtil.parseDate(invoiceDateStr,"yyyyMMddHHmmss");
                invoiceDate = DateUtil.parseDate(DateUtil.format(invoiceDate,"yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss");
                invoiceRecordDetailEntity.setInvoiceDate(invoiceDate);
            }
            invoiceRecordDetailEntity.setInvoiceQrCode(resultObj.getString("invoiceQrCode"));
            invoiceRecordDetailEntity.setTaxControlCode(resultObj.getString("taxControlCode"));
            invoiceRecordDetailEntity.setInvoiceTradeNo(resultObj.getString("serialNo"));
            invoiceRecordDetailEntity.setEinvoiceUrl(resultObj.getString("eInvoiceUrl"));
            invoiceRecordDetailEntity.setInvoiceDetailsList(resultObj.getString("invoiceDetailsList"));
            invoiceRecordDetailEntity.setStatus(status);
            invoiceRecordDetailEntity.setUpdateTime(new Date());
            invoiceRecordDetailEntity.setUpdateUser(updateUser);
        }
        return invoiceRecordDetailEntity;
    }

    /**
     * 添加开票记录变更记录
     * @param invoiceRecordEntity
     * @param status
     * @param invoiceDesc
     * @param addUser
     */
    @Override
    public void addInvoiceRecordChange(InvoiceRecordEntity invoiceRecordEntity,Integer status,String invoiceDesc,String addUser){
        InvoiceRecordChangeEntity invoiceRecordChangeEntity = new InvoiceRecordChangeEntity();
        BeanUtil.copyProperties(invoiceRecordEntity,invoiceRecordChangeEntity);
        invoiceRecordChangeEntity.setId(null);
        invoiceRecordChangeEntity.setStatus(status);
        invoiceRecordChangeEntity.setInvoiceDesc(invoiceDesc);
        if(ObjectUtil.equal(status,InvoiceRecordStatusEnum.TO_SUBMIT.getValue())){
            invoiceRecordChangeEntity.setAddTime(invoiceRecordEntity.getAddTime());
        }else{
            invoiceRecordChangeEntity.setAddTime(new Date());
        }
        invoiceRecordChangeEntity.setAddUser(addUser);
        invoiceRecordChangeEntity.setUpdateTime(null);
        invoiceRecordChangeEntity.setUpdateUser(null);
        invoiceRecordChangeService.insertSelective(invoiceRecordChangeEntity);
    }
    /**
     * 获取百旺电子发票配置参数
     * @param oemCode 机构编码
     * @return
     */
    @Override
    public Map<String,String> getBWInvoiceParams(String oemCode){
        OemParamsEntity oemParamsEntity = oemParamsService.getParams(oemCode,OemParamsTypeEnum.BW_INVOICE_CONFIG.getValue());
        if(oemParamsEntity == null){
            throw new BusinessException("oem机构未配置百旺电子开票参数");
        }
        Map<String,String> oemParmas = new HashMap<>();
        oemParmas.put("account",oemParamsEntity.getAccount());
        oemParmas.put("secKey",oemParamsEntity.getSecKey());
        oemParmas.put("url",oemParamsEntity.getUrl());
        oemParmas.put("publicKey",oemParamsEntity.getPublicKey());
        JSONObject jsonObject = JSONObject.parseObject(oemParamsEntity.getParamsValues());
        oemParmas.put("keyNum",jsonObject.getString("keyNum"));
        return oemParmas;
    }

    /**
     * 生成实体
     * @param invoiceOrderEntity 开票订单实体
     * @param status 状态 0-待提交、1-人工出票、2-待补票、3-出票中断、4-待确认、5-推送失败、6-待确认、7-已完成
     * @param handLingWay 处理方式 1-线下、2-托管
     * @param parkId 园区id
     * @param memberId 会员id
     * @return
     */
    private InvoiceRecordEntity createEntity(InvoiceOrderEntity invoiceOrderEntity,Integer status,Integer handLingWay,Long parkId,Long memberId){
        InvoiceRecordEntity entity = new InvoiceRecordEntity();
        entity.setInvoiceRecordNo(OrderNoFactory.getBWOrderCode(memberId)); //生成开票记录号
        entity.setOrderNo(invoiceOrderEntity.getOrderNo());
        entity.setCompanyId(invoiceOrderEntity.getCompanyId());
        entity.setOemCode(invoiceOrderEntity.getOemCode());
        Long invoiceAmount = invoiceOrderEntity.getInvoiceAmount(); //开票金额
        BigDecimal vatFeeRate = invoiceOrderEntity.getVatFeeRate(); //增值税率
        // 税费 = 开票金额 / （1+税率） * 税率
        Long vatFee = (new BigDecimal(invoiceAmount)).divide(vatFeeRate.add(new BigDecimal("1")),2)
                .multiply(vatFeeRate)
                .setScale(0,BigDecimal.ROUND_UP).longValue();
        entity.setInvoiceAmount(invoiceAmount);
        entity.setInvoiceTotalPriceTax(invoiceAmount);
        entity.setInvoiceTotalTax(vatFee);
        entity.setInvoiceTotalPrice(invoiceAmount - vatFee );
        entity.setStatus(status);
        entity.setHandlingWay(handLingWay);
        entity.setParkId(parkId);
        entity.setAddTime(new Date());
        return entity;
    }

    /**
     * 创建开票记录明细
     * @param companyTaxHostingEntity
     * @param invoiceRecordNo
     * @param invoiceType
     * @param invoiceAmount
     * @param vatFeeRate
     * @param addUser
     * @param goodsDetails 商品明细json数组
     */
    public void createRecordDetails(CompanyTaxHostingEntity companyTaxHostingEntity,String invoiceRecordNo,String invoiceType,Long invoiceAmount,BigDecimal vatFeeRate,
                                    String addUser,String goodsDetails){
        //判断是否费率是否为0%
         Long differenceAmount = 0L;
         if(vatFeeRate.compareTo(new BigDecimal(0))==0||vatFeeRate.compareTo(BigDecimal.valueOf(0.00))==0){
            DictionaryEntity dictionaryEntity = dictionaryService.getByCode("bw_invoice_difference");
            if(dictionaryEntity != null){
                try {
                    differenceAmount = Long.getLong(dictionaryEntity.getDictValue());
                }catch (Exception e){
                    differenceAmount = 0L;
                }
            }
         }
         long faceAmount = companyTaxHostingEntity.getFaceAmount()- differenceAmount;
        InvoiceRecordDetailEntity invoiceRecordDetailEntity = null;
         if(StringUtils.isBlank(goodsDetails)) { //开票明细为空，则按开票金额拆分
             //将开票金额进行拆分
             long faceNum = (new BigDecimal(invoiceAmount).divide(new BigDecimal(faceAmount), 2))
                     .setScale(0, BigDecimal.ROUND_UP).longValue();

             //拆分生成开票明细
             for (int i = 0; i < faceNum; i++) {
                 if (ObjectUtil.equal(invoiceAmount, 0)) {
                     break;
                 }
                 invoiceRecordDetailEntity = new InvoiceRecordDetailEntity();
                 invoiceRecordDetailEntity.setInvoiceRecordNo(invoiceRecordNo);
                 invoiceRecordDetailEntity.setRequestNo(UUID.randomUUID().toString().replaceAll("-", ""));
                 invoiceRecordDetailEntity.setInvoiceTypeCode(invoiceType);
                 invoiceRecordDetailEntity.setStatus(5); //待出票
                 invoiceRecordDetailEntity.setAddTime(new Date());
                 invoiceRecordDetailEntity.setAddUser(addUser);
                 if (invoiceAmount > faceAmount) {
                     // 税费 = 开票金额 / （1+税率） * 税率
                     Long vatFee = (new BigDecimal(faceAmount)).divide(vatFeeRate.add(new BigDecimal("1")), 2)
                             .multiply(vatFeeRate)
                             .setScale(0, BigDecimal.ROUND_UP).longValue();
                     invoiceRecordDetailEntity.setInvoiceTotalPrice(faceAmount - vatFee);
                     invoiceRecordDetailEntity.setInvoiceTotalTax(vatFee);
                     invoiceRecordDetailEntity.setInvoiceTotalPriceTax(faceAmount);

                     invoiceAmount -= faceAmount;
                 } else {
                     Long vatFee = (new BigDecimal(invoiceAmount)).divide(vatFeeRate.add(new BigDecimal("1")), 2)
                             .multiply(vatFeeRate)
                             .setScale(0, BigDecimal.ROUND_UP).longValue();
                     invoiceRecordDetailEntity.setInvoiceTotalPrice(invoiceAmount - vatFee);
                     invoiceRecordDetailEntity.setInvoiceTotalTax(vatFee);
                     invoiceRecordDetailEntity.setInvoiceTotalPriceTax(invoiceAmount);

                     invoiceAmount = 0L;
                 }
                 invoiceRecordDetailService.insertSelective(invoiceRecordDetailEntity);
             }
         }else{
             List<InvoiceorderGoodsdetailRelaEntity> list = JSONObject.parseArray(goodsDetails,InvoiceorderGoodsdetailRelaEntity.class);
             //校验商品明细，将商品拆分成多个小于票面金额的商品
             List<InvoiceorderGoodsdetailRelaEntity> moreAmountList = new ArrayList<>();
             moreAmountList = list.stream().filter( vo -> vo.getGoodsTotalPrice() > faceAmount).collect(Collectors.toList());
             if(moreAmountList != null && moreAmountList.size() >0){
                 moreAmountList.forEach(vo->{
                     InvoiceorderGoodsdetailRelaEntity entity = null;
                     if(vo.getGoodsPrice().compareTo(new BigDecimal(faceAmount))>0){//单价大于票面金额
                         BigDecimal num = new BigDecimal(faceAmount).divide(vo.getGoodsPrice(),4,BigDecimal.ROUND_DOWN);
                         BigDecimal quantity = vo.getGoodsQuantity();
                         Long  totalAmount = 0L;
                         while (quantity.compareTo(num) >-1){
                             entity = new InvoiceorderGoodsdetailRelaEntity();
                             BeanUtils.copyProperties(vo,entity);
                             entity.setGoodsQuantity(num);
                             entity.setGoodsTotalPrice(num.multiply(entity.getGoodsPrice()).setScale(0, BigDecimal.ROUND_UP).longValue());
                             entity.setGoodsTotalTax(new BigDecimal(entity.getGoodsTotalPrice()).multiply(entity.getGoodsTaxRate()).setScale(0, BigDecimal.ROUND_UP).longValue());
                             entity.setAddTime(new Date());
                             totalAmount +=  entity.getGoodsTotalPrice();
                             list.add(entity);
                             quantity = quantity.subtract(num);
                         }
                         if(quantity.compareTo(new BigDecimal(0)) >0 && quantity.compareTo(num) <0){
                             entity = new InvoiceorderGoodsdetailRelaEntity();
                             BeanUtils.copyProperties(vo,entity);
                             entity.setGoodsQuantity(quantity);
//                             entity.setGoodsTotalPrice(quantity.multiply(vo.getGoodsPrice()).setScale(0, BigDecimal.ROUND_UP).longValue());
                             entity.setGoodsTotalPrice(vo.getGoodsTotalPrice()-totalAmount);
                             entity.setGoodsTotalTax(new BigDecimal(entity.getGoodsTotalPrice()).multiply(entity.getGoodsTaxRate()).setScale(0, BigDecimal.ROUND_UP).longValue());
                             entity.setAddTime(new Date());
                             entity.setGoodsPrice(new BigDecimal(entity.getGoodsTotalPrice()).divide(entity.getGoodsQuantity(),4, BigDecimal.ROUND_HALF_UP));
                             list.add(entity);
                         }
                     }else{
                         if(vo.getGoodsPrice().compareTo(new BigDecimal(faceAmount/2))>0){ //商品金额大于票面金额的一半则直接将商品数量进行单一拆分
                            BigDecimal num = vo.getGoodsQuantity();
                             Long  totalAmount = 0L;
                            while(num.compareTo(new BigDecimal(1)) > -1){
                                entity = new InvoiceorderGoodsdetailRelaEntity();
                                BeanUtils.copyProperties(vo,entity);
                                entity.setGoodsQuantity(new BigDecimal(1));
                                entity.setGoodsTotalPrice(vo.getGoodsPrice().setScale(0, BigDecimal.ROUND_UP).longValue());
                                entity.setGoodsTotalTax(new BigDecimal(entity.getGoodsTotalPrice()).multiply(entity.getGoodsTaxRate()).setScale(0, BigDecimal.ROUND_UP).longValue());
                                entity.setAddTime(new Date());
                                list.add(entity);
                                num = num.subtract(new BigDecimal(1));
                                totalAmount +=  entity.getGoodsTotalPrice();
                            }
                            if(num.compareTo(new BigDecimal(0)) >0 && num.compareTo(new BigDecimal(1)) <0){
                                entity = new InvoiceorderGoodsdetailRelaEntity();
                                BeanUtils.copyProperties(vo,entity);
                                entity.setGoodsQuantity(num);
//                                entity.setGoodsTotalPrice(num.multiply(vo.getGoodsPrice()).setScale(0, BigDecimal.ROUND_UP).longValue());
                                entity.setGoodsTotalPrice(vo.getGoodsTotalPrice()-totalAmount);
                                entity.setGoodsTotalTax(new BigDecimal(entity.getGoodsTotalPrice()).multiply(entity.getGoodsTaxRate()).setScale(0, BigDecimal.ROUND_UP).longValue());
                                entity.setAddTime(new Date());
                                entity.setGoodsPrice(new BigDecimal(entity.getGoodsTotalPrice()).divide(entity.getGoodsQuantity(),4, BigDecimal.ROUND_HALF_UP));
                                list.add(entity);
                            }
                         }else{ //商品金额小于等于票面金额的一半则用商品单价*(票面金额/单价)
                             Long num = new BigDecimal(faceAmount).divide(vo.getGoodsPrice(),0,BigDecimal.ROUND_DOWN).longValue();
                             BigDecimal quantity = vo.getGoodsQuantity();
                             Long  totalAmount = 0L;
                             while((quantity.subtract(new BigDecimal(num))).compareTo(new BigDecimal(0))>-1){
                                 entity = new InvoiceorderGoodsdetailRelaEntity();
                                 BeanUtils.copyProperties(vo,entity);
                                 entity.setGoodsQuantity(new BigDecimal(num));
                                 entity.setGoodsTotalPrice(new BigDecimal(num).multiply(vo.getGoodsPrice()).setScale(0, BigDecimal.ROUND_UP).longValue());
                                 entity.setGoodsTotalTax(new BigDecimal(entity.getGoodsTotalPrice()).multiply(entity.getGoodsTaxRate()).setScale(0, BigDecimal.ROUND_UP).longValue());
                                 entity.setAddTime(new Date());
                                 list.add(entity);
                                 quantity = quantity.subtract(new BigDecimal(num));
                                 totalAmount +=  entity.getGoodsTotalPrice();
                             }
                             if(quantity.compareTo(new BigDecimal(0)) >0 && quantity.compareTo(new BigDecimal(num)) <0){
                                 entity = new InvoiceorderGoodsdetailRelaEntity();
                                 BeanUtils.copyProperties(vo,entity);
                                 entity.setGoodsQuantity(quantity);
//                                 entity.setGoodsTotalPrice(quantity.multiply(vo.getGoodsPrice()).setScale(0, BigDecimal.ROUND_UP).longValue());
                                 entity.setGoodsTotalPrice(vo.getGoodsTotalPrice()-totalAmount);
                                 entity.setGoodsTotalTax(new BigDecimal(entity.getGoodsTotalPrice()).multiply(entity.getGoodsTaxRate()).setScale(0, BigDecimal.ROUND_UP).longValue());
                                 entity.setAddTime(new Date());
                                 entity.setGoodsPrice(new BigDecimal(entity.getGoodsTotalPrice()).divide(entity.getGoodsQuantity(),4, BigDecimal.ROUND_HALF_UP));
                                 list.add(entity);
                             }
                         }
                     }
                     list.remove(vo);
                 });
             }
             List<InvoiceorderGoodsdetailRelaEntity> removeList = new ArrayList<>();
             Long amount = 0L;
             Long vatFee = 0L;
             int len = 0;
             InvoiceorderGoodsdetailRelaEntity entity = null;
             while(!list.isEmpty()){
                 len = list.size();
                 entity = list.get(0);
                 removeList.add(entity);
                 invoiceRecordDetailEntity = new InvoiceRecordDetailEntity();
                 invoiceRecordDetailEntity.setInvoiceRecordNo(invoiceRecordNo);
                 invoiceRecordDetailEntity.setRequestNo(UUID.randomUUID().toString().replaceAll("-", ""));
                 invoiceRecordDetailEntity.setInvoiceTypeCode(invoiceType);
                 invoiceRecordDetailEntity.setStatus(5); //待出票
                 invoiceRecordDetailEntity.setAddTime(new Date());
                 invoiceRecordDetailEntity.setAddUser(addUser);

                 amount = entity.getGoodsTotalPrice();
                 vatFee = entity.getGoodsTotalTax();
                 if(amount<=faceAmount) {
                     for (int i = 1; i < len; i++) {
                         if((amount+list.get(i).getGoodsTotalPrice())<=faceAmount){
                             amount += list.get(i).getGoodsTotalPrice();
                             vatFee += list.get(i).getGoodsTotalTax();
                             removeList.add(list.get(i));
                         }
                     }
                     invoiceRecordDetailEntity.setInvoiceTotalPrice(amount - vatFee);
                     invoiceRecordDetailEntity.setInvoiceTotalTax(vatFee);
                     invoiceRecordDetailEntity.setInvoiceTotalPriceTax(amount);
                     if(removeList!=null && removeList.size()>=8){
                         invoiceRecordDetailEntity.setIsPrintDetail(1);
                     }
                     invoiceRecordDetailEntity.setGoodsDetails(JSONObject.toJSONString(removeList));
                     invoiceRecordDetailService.insertSelective(invoiceRecordDetailEntity);
                     list.removeAll(removeList);
                     removeList.clear();
                 }
             }
         }
    }

    /**
     * 修改正在开票的开票记录信息
     * @param invoiceRecordNo
     * @param updateUser
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInvoiceRecordByIng(String invoiceRecordNo,String updateUser){
        List<Map<String,Object>> list = invoiceRecordDetailService.queryDetailStatusNumByIng(invoiceRecordNo);
        if(list!= null && list.size()>0){
            int totalNum = 0; //总明细数
            int successNum = 0; //开票成功数
            int failNum = 0; //开票失败数
            int ingNum = 0; //出票中数
            int formatCreateNum = 0; //版式生成中
            String recordNo = ""; //开票记录编号
            String orderNo = ""; //订单号
            for(Map<String,Object> map : list){
                totalNum = Integer.parseInt(map.get("totalNum").toString());
                successNum = Integer.parseInt(map.get("successNum").toString());
                failNum = Integer.parseInt(map.get("failNum").toString());
                ingNum = Integer.parseInt(map.get("ingNum").toString());
                formatCreateNum = Integer.parseInt(map.get("formatCreateNum").toString());
                recordNo = map.get("invoiceRecordNo").toString();
                orderNo = map.get("orderNo").toString();
                if(formatCreateNum>0){
                    continue;
                }
                if(totalNum == successNum ){ //全部成功
                    //判断是电票还是纸票
                    InvoiceRecordDetailEntity invoiceRecordDetailEntity = new InvoiceRecordDetailEntity();
                    invoiceRecordDetailEntity.setInvoiceRecordNo(recordNo);
                    List<InvoiceRecordDetailEntity> detailEntityList = invoiceRecordDetailService.select(invoiceRecordDetailEntity);
                    if(detailEntityList!=null){
                        invoiceRecordDetailEntity = detailEntityList.get(0);
                        if("026".equals(invoiceRecordDetailEntity.getInvoiceTypeCode().trim())
                                || "028".equals(invoiceRecordDetailEntity.getInvoiceTypeCode().trim())){ //电票
                            try {
                                //调用版式
                                sendAgain(recordNo, orderNo, updateUser);
                            }catch (BusinessException bx){
                                String errorMsg = bx.getMessage();
                                if(StringUtils.isNotBlank(errorMsg) && errorMsg.indexOf("请勿重复提交")>-1){
                                    continue;
                                }
                                InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
                                invoiceRecordEntity.setInvoiceRecordNo(invoiceRecordNo);
                                invoiceRecordEntity.setOrderNo(orderNo);
                                invoiceRecordEntity = this.selectOne(invoiceRecordEntity);
                                if(invoiceRecordEntity != null && ObjectUtil.equal(invoiceRecordEntity.getStatus(),InvoiceRecordStatusEnum.COMPLETED.getValue())){
                                    invoiceRecordEntity.setUpdateTime(new Date());
                                    invoiceRecordEntity.setUpdateUser(updateUser);
                                    this.editByIdSelective(invoiceRecordEntity);
                                    addInvoiceRecordChange(invoiceRecordEntity,InvoiceRecordStatusEnum.FAILED_TO_PUSH.getValue(),"电子发票重新推送失败",updateUser);
                                }else {
                                    //推送失败，修改开票记录
                                    updateInvoiceRecordStatus(recordNo,orderNo,InvoiceRecordStatusEnum.FAILED_TO_PUSH.getValue(),"版式文件生成失败",null,updateUser);
                                }
                            }
                        }else{ //纸票
                            updateInvoiceRecordStatus(recordNo,orderNo,InvoiceRecordStatusEnum.TO_BE_CONFIRMED.getValue(),"全部出票完成",null,updateUser);
                        }
                    }
                }else if(totalNum == failNum){ //全部失败
                    updateInvoiceRecordStatus(recordNo,orderNo,InvoiceRecordStatusEnum.THE_DRAWER_FAILURE.getValue(),"全部出票失败",null,updateUser);
                }else if(successNum >0 && failNum > 0 && ingNum < 1){ //开票中断
                    updateInvoiceRecordStatus(recordNo,orderNo,InvoiceRecordStatusEnum.NTERRUPTION_OF_THE_DRAWER.getValue(),"部分出票失败",null,updateUser);
                }
            }
        }
    }


    /**
     * 取消开票记录
     * @param orderNo 订单号
     * @param type 1-作废不重开 2-作废重开
     * @param remark 备注
     * @param updateUser 修改人
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invoiceRecordCancel(String orderNo, String type, String remark, String updateUser) {
        //查询出票中的开票记录
        Integer ingNum = mapper.countInvoicingRecordNumByOrderNo(orderNo);
        //不存在出票中的开票记录，将全部开票记录全部取消
        if(ingNum>0){
            throw new BusinessException("存在出票中的开票记录，不能进行取消操作");
        }
        InvoiceRecordEntity invoiceRecordEntity = new InvoiceRecordEntity();
        invoiceRecordEntity.setOrderNo(orderNo);
         List<InvoiceRecordEntity> list =  mapper.select(invoiceRecordEntity);
         if(list != null && list.size()>0){
             list.forEach(vo->{
                 vo.setStatus(InvoiceRecordStatusEnum.CANCELED.getValue());
                 vo.setRemark(remark);
                 vo.setUpdateTime(new Date());
                 vo.setUpdateUser(updateUser);
                 mapper.updateByPrimaryKeySelective(vo);
                 addInvoiceRecordChange(vo,InvoiceRecordStatusEnum.CANCELED.getValue(),remark,updateUser);
             });
         }

        OrderEntity entity  = orderService.queryByOrderNo(orderNo);
        if(entity == null){
            throw new BusinessException("未找到订单信息");
        }
        if(entity.getOrderType().intValue() == OrderTypeEnum.INVOICE.getValue()) {  //开票订单
            //type = 2 ,则根据订单金额重新生成开票记录,将状态改成人工出票，处理方式改成 线下
            if ("2".equals(type)) {
                InvoiceOrderEntity invoiceOrderEntity = invoiceOrderService.queryByOrderNo(invoiceRecordEntity.getOrderNo());
                if(invoiceOrderEntity == null){
                    throw new BusinessException("未找到开票订单信息");
                }
                invoiceOrderEntity.setInvoiceImgs(null);
                invoiceOrderEntity.setUpdateTime(new Date());
                invoiceOrderEntity.setUpdateUser(updateUser);
                invoiceOrderEntity.setRemark("开票记录重开，清空开票图片");
                invoiceOrderService.editByIdSelective(invoiceOrderEntity);

                invoiceRecordEntity = createEntity(invoiceOrderEntity, InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue(), 1, entity.getParkId(), entity.getUserId());
                invoiceRecordEntity.setAddUser(updateUser);
                this.insertSelective(invoiceRecordEntity);
                addInvoiceRecordChange(invoiceRecordEntity, invoiceRecordEntity.getStatus(), invoiceRecordEntity.getInvoiceDesc(), updateUser);
            } else {//type = 1 或其他类型,则取消订单
                entity.setRemark(remark);
                orderService.cancelOrder(entity, InvoiceOrderStatusEnum.CANCELED.getValue(), updateUser, false);
            }
        }else if(entity.getOrderType().intValue() == OrderTypeEnum.CONSUMPTION_INVOICE.getValue()){ //消费发票
            ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity = new ConsumptionInvoiceOrderEntity();
            consumptionInvoiceOrderEntity.setOrderNo(entity.getOrderNo());
            consumptionInvoiceOrderEntity.setOemCode(entity.getOemCode());
            consumptionInvoiceOrderEntity = consumptionInvoiceOrderService.selectOne(consumptionInvoiceOrderEntity);
            if(consumptionInvoiceOrderEntity == null){
                throw new BusinessException("未找到消费开票订单信息");
            }
            if ("2".equals(type)) {
                consumptionInvoiceOrderEntity.setInvoiceImgs(null);
                consumptionInvoiceOrderEntity.setInvoicePdfUrl(null);
                consumptionInvoiceOrderEntity.setUpdateTime(new Date());
                consumptionInvoiceOrderEntity.setUpdateUser(updateUser);
                consumptionInvoiceOrderEntity.setRemark("开票记录重开，清空开票图片");
                consumptionInvoiceOrderService.editByIdSelective(consumptionInvoiceOrderEntity);

                InvoiceInfoByOemEntity invoiceInfoByOemEntity = new InvoiceInfoByOemEntity();
                invoiceInfoByOemEntity.setOemCode(entity.getOemCode());
                invoiceInfoByOemEntity = invoiceInfoByOemService.selectOne(invoiceInfoByOemEntity);
                if(invoiceInfoByOemEntity == null){
                    throw new BusinessException("未找到消费开票配置信息");
                }
                InvoiceOrderEntity invoiceOrderEntity = new InvoiceOrderEntity();
                BeanUtil.copyProperties(consumptionInvoiceOrderEntity,invoiceOrderEntity);
                invoiceOrderEntity.setEmail(consumptionInvoiceOrderEntity.getBillToEmail());
                invoiceOrderEntity.setVatFeeRate(invoiceInfoByOemEntity.getVatRate().divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_UP));

                //重新创建开票记录
                invoiceRecordEntity = createEntity(invoiceOrderEntity, InvoiceRecordStatusEnum.ARTIFICIAL_TICKET.getValue(), 1, entity.getParkId(), entity.getUserId());
                invoiceRecordEntity.setAddUser(updateUser);
                this.insertSelective(invoiceRecordEntity);
                addInvoiceRecordChange(invoiceRecordEntity, invoiceRecordEntity.getStatus(), invoiceRecordEntity.getInvoiceDesc(), updateUser);
            }else {//type = 1 则取消订单
                consumptionInvoiceOrderService.applyInvoiceFail(consumptionInvoiceOrderEntity.getId(),updateUser, remark);
            }
        }

    }
}

