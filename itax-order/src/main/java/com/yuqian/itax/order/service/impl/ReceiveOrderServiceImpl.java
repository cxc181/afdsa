package com.yuqian.itax.order.service.impl;

import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.dao.ReceiveOrderMapper;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.ReceiveOrderEntity;
import com.yuqian.itax.order.entity.vo.ReceiveOrderVO;
import com.yuqian.itax.order.entity.vo.ReceiveServerVO;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.order.service.ReceiveOrderService;
import com.yuqian.itax.user.entity.CustomerServiceWorkNumberEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.CustomerServiceWorkNumberService;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.util.util.OrderNoFactory;
import com.yuqian.itax.workorder.entity.WorkOrderChangeRecordEntity;
import com.yuqian.itax.workorder.entity.WorkOrderEntity;
import com.yuqian.itax.workorder.entity.query.WorkOrderQuery;
import com.yuqian.itax.workorder.enums.WorkOrderStatusEnum;
import com.yuqian.itax.workorder.enums.WorkOrderTypeEnum;
import com.yuqian.itax.workorder.service.WorkOrderChangeRecordService;
import com.yuqian.itax.workorder.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service("receiveOrderService")
public class ReceiveOrderServiceImpl extends BaseServiceImpl<ReceiveOrderEntity,ReceiveOrderMapper> implements ReceiveOrderService {
    @Resource
    private ReceiveOrderMapper receiveOrderMapper;
    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private WorkOrderChangeRecordService workOrderChangeRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private OemService oemService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerServiceWorkNumberService customerServiceWorkNumberService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReceiveServerVO getReceiveServer(String oemCode, String orderNo, Integer orderType,Integer workOrderType) throws BusinessException {
        log.info("接收到自动派单参数：{}，{}，{}",oemCode,orderNo,orderType);
        OrderEntity orderEntity=orderService.queryByOrderNo(orderNo);
        int index = 0;// 临时变量，用来存储坐席客服下标
        Long recvOrderUserId = null;

        // 判断工单类型为2-开票审核（同一个用户同一天创建的开票订单优先派给同一个客服）
        if (WorkOrderTypeEnum.INVOICE.getValue().equals(workOrderType)) {
            // 查询用户当天是否存在其他工单类型为“开票审核”的开票工单
            WorkOrderQuery workOrderQuery = new WorkOrderQuery();
            workOrderQuery.setFilterableOrder(orderNo);
            workOrderQuery.setWorkOrderType(workOrderType);
            workOrderQuery.setUserId(orderEntity.getUserId());
            workOrderQuery.setAddTime(new Date());
            List<WorkOrderEntity> workOrderEntities = workOrderService.queryByUserId(workOrderQuery);
            if (CollectionUtil.isNotEmpty(workOrderEntities)) {
                // 获取工单接单客服id并查询客服状态
                WorkOrderEntity workOrderEntity = workOrderEntities.get(0);
                UserEntity user = userService.getUserByUserName(workOrderEntity.getCustomerServiceAccount(), null);
                if (null != user) {
                    // 查询工号
                    Example example = new Example(CustomerServiceWorkNumberEntity.class);
                    example.createCriteria().andEqualTo("userId", user.getId()).andEqualTo("status", 1);
                    List<CustomerServiceWorkNumberEntity> workNumberEntities = customerServiceWorkNumberService.selectByExample(example);
                    if (1 == user.getStatus() && CollectionUtil.isNotEmpty(workNumberEntities)) {
                        recvOrderUserId = user.getId();
                    }
                }
            }
        }
        // 偏好派单
        if (null == recvOrderUserId) {
            //如果订单号存在过工单自动派单的时候偏好上次派发的那个客服
            List<WorkOrderEntity> workOrderEntities = workOrderService.queryWorkOrderByOrderNo(orderNo);
            if (!CollectionUtils.isEmpty(workOrderEntities)) {
                UserEntity customerUserEntity = userService.qeruyUserByAccountAndOemCode(oemCode, workOrderEntities.get(0).getCustomerServiceAccount());
                OemEntity oemEntity = oemService.getOem(workOrderEntities.get(0).getOemCode());
                if (oemEntity == null) {
                    throw new BusinessException("机构不存在");
                }
                Integer workAuditWay = oemEntity.getWorkAuditWay();
                if (workAuditWay == null) {
                    throw new BusinessException("机构工单审核方未配置");
                }
                if (customerUserEntity != null
                        && customerUserEntity.getStatus() != 2
                        && ((workAuditWay == 1 && customerUserEntity.getOemCode() == null) || (workAuditWay == 2 && Objects.equals(customerUserEntity.getOemCode(), workOrderEntities.get(0).getOemCode())))) {
                    //偏好派单
                    log.info("偏好派单==================================");
                    recvOrderUserId = customerUserEntity.getId();
                }
            }
        }
        // 自动派单
        if (null == recvOrderUserId) {
            log.info("自动派单==================================");
            //自动派单
            // 1.查询所有状态为1的坐席列表
            List<ReceiveOrderEntity> list = this.receiveOrderMapper.getReceiveList(oemCode);
            if(!CollectionUtils.isEmpty(list)){
                // 2.统计全部的接单数量
                ReceiveOrderVO statVO = this.receiveOrderMapper.getTotalNums(oemCode);

                // 3.用（（接单数量+1）% 坐席数量） 获取坐席集合下标
                index = (statVO.getTotalRecv() + 1) % statVO.getCustomerNum();

                // 4.根据结算接口取集合相对于下标的坐席数据
                recvOrderUserId = list.get(index).getUserId();
            }
        }

        if(recvOrderUserId!=null){
            // 指定客服接单后，将（接单表）中对应客服id的接单数据进行+1
            ReceiveOrderEntity t = new ReceiveOrderEntity();
            t.setUserId(recvOrderUserId);
            List<ReceiveOrderEntity> roeList = this.select(t);
            if(roeList.size() > 1){

                throw new BusinessException("数据异常，多个客服接单记录冲突");
            }
            // 该客服现有接单数量
            int receiveOrderNum = roeList.get(0).getReceiveOrderNum();
            ReceiveOrderEntity updateEntity = new ReceiveOrderEntity();
            updateEntity.setId(roeList.get(0).getId());
            updateEntity.setReceiveOrderNum(receiveOrderNum + 1);
            updateEntity.setUpdateTime(new Date());
            this.editByIdSelective(updateEntity);
        }

        // 加入redis锁机制，防止重复生成工单
        String registRedisTime = (System.currentTimeMillis() + 300000) + ""; // redis标识值
        boolean flag = redisService.lock(RedisKey.REGIST_ORDER_DISPATCH_REDIS_KEY + orderNo,registRedisTime,60);
        if(!flag){
            throw new BusinessException("请勿重复提交！");
        }

        // 不管有无客服接单，都生成一条工单记录
        WorkOrderEntity workOrder = new WorkOrderEntity();
        workOrder.setWorkOrderNo(OrderNoFactory.getWorkOrderCode(recvOrderUserId));// 生成工单号
        workOrder.setAddTime(new Date());
        workOrder.setAddUser(null);
        // 查询oem机构信息
        OemEntity oem = this.oemService.getOem(oemCode);
        if (null == oem) {
            // 释放redis锁
            redisService.unlock(RedisKey.REGIST_ORDER_DISPATCH_REDIS_KEY + orderNo,registRedisTime);
            throw new BusinessException("自动派单失败，未查询到oem机构");
        }
        workOrder.setOemCode(orderEntity.getOemCode());
        workOrder.setOemName(this.oemService.getOem(orderEntity.getOemCode()).getOemName());
        workOrder.setOrderNo(orderNo);
        workOrder.setOrderType(orderType); // 订单类型 1- 办理核名 2-开票审核
        workOrder.setWorkOrderType(workOrderType); // 工单类型 1- 办理核名 2-开票审核 3-流水审核

        if(null != recvOrderUserId){
            // 查询客服账号信息
            UserEntity user = this.userService.findById(recvOrderUserId);
            if(null == user){
                // 释放redis锁
                redisService.unlock(RedisKey.REGIST_ORDER_DISPATCH_REDIS_KEY + orderNo,registRedisTime);
                throw new BusinessException("自动派单失败，客服信息不存在");
            }
            workOrder.setCustomerServiceAccount(user.getUsername());
            workOrder.setCustomerServiceName(user.getNickname());
            workOrder.setCustomerServiceId(user.getId());
        }

        workOrder.setWorkOrderStatus(WorkOrderStatusEnum.WAITING_FOR_ORDERS.getValue());// 工单状态 类型为开户： 0-待接单 1-处理中 2-已完成 3-已取消
        this.workOrderService.insertSelective(workOrder);

        // 工单变更记录表添加一条工单数据记录
        WorkOrderChangeRecordEntity changeRecord = new WorkOrderChangeRecordEntity();
        BeanUtils.copyProperties(workOrder,changeRecord);
        changeRecord.setId(null);
        this.workOrderChangeRecordService.insertSelective(changeRecord);

        log.info("自动派单完成，指定接单人ID：{}",recvOrderUserId);
        // 返回
        ReceiveServerVO serverVO = new ReceiveServerVO();
        serverVO.setRecvOrderUserId(recvOrderUserId);
        serverVO.setWorkOrderNo(workOrder.getWorkOrderNo());

        // 释放redis锁
        redisService.unlock(RedisKey.REGIST_ORDER_DISPATCH_REDIS_KEY + orderNo,registRedisTime);
        return serverVO;
    }

    @Override
    public ReceiveOrderEntity queryByUserId(Long userId) {
        return mapper.queryByUserId(userId);
    }

    /**
     * @Description 在指定size范围内取一随机数
     * @Author  Kaven
     * @Date   2019/12/24 16:18
     * @Param  size
     * @Return int
     * @Exception
    */
    private int getRandom(int size) {
        //生成0到size之间的数字
        return new Random().nextInt(size + 1);
    }
}

