package com.yuqian.itax.order.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.dao.InvoiceRecordMapper;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.InvoiceRecordDetailEntity;
import com.yuqian.itax.order.entity.InvoiceRecordEntity;
import com.yuqian.itax.order.entity.query.InvoiceRecordQuery;
import com.yuqian.itax.order.entity.vo.ConfirmInvoiceRecordVo;
import com.yuqian.itax.order.entity.vo.InvoiceRecordDetailVO;
import com.yuqian.itax.order.entity.vo.InvoiceRecordVO;
import com.yuqian.itax.user.entity.CompanyTaxHostingEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 开票记录表service
 * 
 * @Date: 2020年12月25日 11:42:04 
 * @author 蒋匿
 */
public interface InvoiceRecordService extends IBaseService<InvoiceRecordEntity,InvoiceRecordMapper> {


    /**
     * 根据条件查询开票记录
     * @param query
     * @return
     */
    List<InvoiceRecordVO> querylistInvoiceRecord(InvoiceRecordQuery query);

    /**
     * 分页查询开票记录
     * @param query
     * @return
     */
    PageInfo<InvoiceRecordVO> listPage(InvoiceRecordQuery query);

    /**
     * 确认出票页面
     * @param invoiceRecordNo
     * @return
     */
    ConfirmInvoiceRecordVo gotoConfirmInvoiceRecord(String invoiceRecordNo);

    /**
     * 修改开票记录状态
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 订单号
     * @param status 开票记录状态 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消
     * @param desc  开票记录描述
     * @param handlingWay 处理方式 1-线下、2-托管
     * @param updateUser 修改人
     */
    void updateInvoiceRecordStatus(String invoiceRecordNo,String orderNo,Integer status,String desc,Integer handlingWay,String updateUser);

    /**
     * 立即开票
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 订单号
     * @param addUser 添加人
     * @param isForce 是否强制开票 1-强制 0-正常
     */
    String openInvoice(String invoiceRecordNo,String orderNo,String addUser,String isForce);

    /**
     * 创建开票记录
     * @param invoiceOrderEntity 开票订单
     * @param companyTaxHostingEntity  托管信息
     * @param taxNo  税号
     * @param parkId 园区id
     * @param memberId 会员id
     * @param addUser 操作人
     * @param isImmediatelyInvoice 是否立即出票 true-立即出票
     */
    void createInvoiceRecord(InvoiceOrderEntity invoiceOrderEntity,CompanyTaxHostingEntity companyTaxHostingEntity,String taxNo,Long parkId,Long memberId,String addUser,boolean isImmediatelyInvoice);

    /**
     * 添加开票记录历史数据
     * @param invoiceRecordEntity
     * @param status
     * @param invoiceDesc
     * @param addUser
     */
    void addInvoiceRecordChange(InvoiceRecordEntity invoiceRecordEntity,Integer status,String invoiceDesc,String addUser);
    /**
     * 调用百旺开票接口
     * @param invoiceOrderEntity 开票订单实体
     * @param invoiceRecordEntity 开票记录实体
     * @param companyTaxHostingEntity 企业托管实体
     * @param taxNo  企业税号
     * @param updateUser 操作人
     * @param isForce 是否强制开票 1-强制 0-正常
     */
    String gotoBWInvoice(InvoiceOrderEntity invoiceOrderEntity, InvoiceRecordEntity invoiceRecordEntity, CompanyTaxHostingEntity companyTaxHostingEntity,
                       String taxNo, String updateUser,String isForce);

    /**
     * 查询百旺 发票领用存
     * @param oemParams oem机构参数
     * @param taxNo 税号
     * @param taxDiscCode 税务盘编号
     * @param invoiceWay 开票方式 1-纸质 2-电票
     * @return
     */
     Map<String,Object> queryInventory(Map<String,String> oemParams, String taxNo, String taxDiscCode, Integer invoiceWay);

    /**
     *  调用开票
     * @param invoiceOrderEntity 开票订单实体
     * @param invoiceRecordEntity 开票记录实体
     * @param companyTaxHostingEntity 税务托管实体
     * @param detailEntityList 开票明细
     * @param oemParams 百旺参数配置
     * @param taxNo 开票企业税号
     * @param addUser 操作人
     */
    void invoiceIssue(InvoiceOrderEntity invoiceOrderEntity,InvoiceRecordEntity invoiceRecordEntity,CompanyTaxHostingEntity companyTaxHostingEntity,
                        List<InvoiceRecordDetailEntity> detailEntityList,Map<String,String> oemParams,String taxNo,String addUser);

    /**
     *  百旺开票
     * @param orderMap 开票接口业务参数
     * @param oemParams 百旺参数配置
     * @param requestNo 请求号
     * @param addUser 操作人
     */
    String bwInvoiceIssue(Map<String,Object> orderMap,  Map<String,String> oemParams,String requestNo,String addUser);


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
    void formatCreate(List<InvoiceRecordDetailEntity> detailList, InvoiceRecordEntity invoiceRecordEntity, Map<String,String> oemParams,String companyName, String taxNo, String email, String updateUser);

    /**
     * 获取百旺电子发票配置参数
     * @param oemCode 机构编码
     * @return
     */
     Map<String,String> getBWInvoiceParams(String oemCode);

    /**
     * 调用百旺打印接口
     * @param detailId 开票明细id
     * @param oemCode oem机构编码
     * @param printType 0：发票打印，1：清单打印
     * @param updateUser
     */
     String printBWInvoice(Long detailId,String oemCode,String printType,String updateUser);

    /**
     * 线下开票
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @param updateUser 操作人
     */
    void offlineInvoice(String invoiceRecordNo,String orderNo,String updateUser);

    /**
     * 强制成功
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @param updateUser 操作人
     */
    void forceSuccess(String invoiceRecordNo,String orderNo,String updateUser);

    /**
     * 重新推送
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @param updateUser 操作人
     */
    void sendAgain(String invoiceRecordNo,String orderNo,String updateUser);

    /**
     * 开票记录明细
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     */
    List<InvoiceRecordDetailVO> invoiceRecordDetailList(String invoiceRecordNo, String orderNo);


    /**
     * 根据集团开票得订单号查询子订单 未完成得开票记录
     * @param orderNo
     * @param status
     * @param notCompleted
     * @return
     */
    List<InvoiceRecordEntity> queryGroupInvoiceOrderByGroupOrderNo(String orderNo,String status,String notCompleted);

    /**
     * 开票记录明细打印
     * @param detailId
     * @param invoiceRecordNo 开票记录编号
     * @param printType 0：发票打印，1：清单打印
     * @param updateUser 操作人
     */
    String invoiceDetailPrint(Long detailId,String invoiceRecordNo,String printType,String updateUser);

    /**
     * 作废开票明细
     * @param detailIds 明细ids，多个开票记录id之间用 逗号分隔
     * @param updateUser 操作人
     */
    void invoiceDetailInvalid(String detailIds,String updateUser);


    void updateInvoiceRecordStatusByGroupOrderNoAndStatuss(String groupOrderNo, Integer status, String desc, String updateUser, Date updateTime, String statuss, String notStatuss);

    /**
     * 根据订单号和状态查询开票记录
     * @param orderNo
     * @param statuss
     * @return
     */
    List<InvoiceRecordEntity> queryInvoiceRecordByOrderNoAndStatus(String orderNo, String statuss);

    /**
     * 修改正在开票的开票记录信息
     * @param invoiceRecordNo
     * @param updateUser
     */
    void updateInvoiceRecordByIng(String invoiceRecordNo,String updateUser);

    /**
     * 取消开票记录
     * @param orderNo 订单号
     * @param type 1-作废不重开 2-作废重开
     * @param remark 备注
     * @param updateUser 修改人
     * @return
     */
    void invoiceRecordCancel(String orderNo,String type,String remark,String updateUser);

    /**
     * 根据订单号查询跨季度的开票记录数
     * @param orderNo
     * @return
     */
    int queryLastQuarterRecordNumByOrderNo(String orderNo);

    /**
     * 根据订单号查询跨季度的开票记录数
     * @param orderNo
     * @return
     */
    int queryLastQuarterRecordNumByOrderNo(String orderNo,Date tickTime);

    /**
     * 根据订单号查询电子发票地址
     * @param orderNo
     * @return
     */
    String getInvoiceDetailImgUrlsByOrder(String orderNo);
    /**
     * 查询未完成的开票记录数
     * @param orderNo
     * @param invoiceRecordNo
     * @return
     */
    int findUnfinishedInvoiceRecordByOrderNo(String orderNo,String invoiceRecordNo);

    /**
     * 根据订单号获取最大出票时间
     * @param orderNo
     * @return
     */
    Date getMaxTicketTimeByOrderNo(String orderNo);
}

