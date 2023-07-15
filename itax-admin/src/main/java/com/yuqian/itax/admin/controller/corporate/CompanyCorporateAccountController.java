package com.yuqian.itax.admin.controller.corporate;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.corporateaccount.entity.CollectionWithdrawalAmountChangeRecordEntity;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountApplyOrderEntity;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountCollectionRecordEntity;
import com.yuqian.itax.corporateaccount.service.CollectionWithdrawalAmountChangeRecordService;
import com.yuqian.itax.corporateaccount.service.CorporateAccountApplyOrderService;
import com.yuqian.itax.corporateaccount.service.CorporateAccountCollectionRecordService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.WalletTypeEnum;
import com.yuqian.itax.order.service.MemberConsumptionRecordService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.park.entity.ParkCorporateAccountConfigEntity;
import com.yuqian.itax.park.service.ParkCorporateAccountConfigService;
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.po.CompanyCorporateAccountPO;
import com.yuqian.itax.user.entity.po.CorporateAccountApplyOrderPO;
import com.yuqian.itax.user.entity.query.ComCorpAccQuery;
import com.yuqian.itax.user.entity.query.CompanyCorporateAccountQuery;
import com.yuqian.itax.user.entity.query.CompanyCorporateAccountVerificationQuery;
import com.yuqian.itax.user.entity.query.CorporateAccountCollectionRecordQuery;
import com.yuqian.itax.user.entity.vo.CompanyCorporateAccountVO;
import com.yuqian.itax.user.entity.vo.CorporateAccountVOAdmin;
import com.yuqian.itax.user.entity.vo.CorporateInvoiceOrderVO;
import com.yuqian.itax.user.service.CompanyCorporateAccountService;
import com.yuqian.itax.user.service.DaifuApiService;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 对公户账号管理
 * @author：HZ
 * @Date：2020/9/710:12
 * @version：1.0
 */
@RestController
@RequestMapping("corporate/account")
@Slf4j
public class  CompanyCorporateAccountController extends BaseController {

    @Autowired
    private CompanyCorporateAccountService companyCorporateAccountService;
    @Autowired
    private CorporateAccountApplyOrderService corporateAccountApplyOrderService;
    @Autowired
    MemberCompanyService memberCompanyService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    CorporateAccountCollectionRecordService corporateAccountCollectionRecordService;
    @Autowired
    OrderService orderService;
    @Autowired
    CollectionWithdrawalAmountChangeRecordService collectionWithdrawalAmountChangeRecordService;
    @Autowired
    OemParamsService oemParamsService;
    @Autowired
    DaifuApiService daifuApiService;
    @Autowired
    UserCapitalAccountService userCapitalAccountService;
    @Autowired
    MemberConsumptionRecordService memberConsumptionRecordService;

    @Autowired
    private ParkCorporateAccountConfigService parkCorporateAccountConfigService;
    /**
     * 分页查询
     * @param query
     * @return
     */
    @PostMapping("page")
    public ResultVo queryCorporateAccountCollectionRecordPageInfo(@RequestBody CompanyCorporateAccountQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        // 园区账号只能查看所属园区的数据
        if (Objects.equals(userEntity.getPlatformType(),3) && null != userEntity.getParkId()) {
            query.setParkId(userEntity.getParkId());
        } else if (Objects.equals(userEntity.getPlatformType(),3) && null == userEntity.getParkId()) {
            throw new BusinessException("未查询到用户园区信息");
        }
        query.setOemCode(userEntity.getOemCode());
        PageInfo<CompanyCorporateAccountVO> page = companyCorporateAccountService.querCompanyCorporateAccountServicePageInfo(query);
        return ResultVo.Success(page);
    }

    /**
     * 开户账号详情
     * @param id
     * @return
     */
    @PostMapping("detail")
    public ResultVo detail(@JsonParam Long id ){
        CurrUser currUser = getCurrUser();
        CompanyCorporateAccountVO vo=companyCorporateAccountService.queryCompanyCorporateAccountDetail( id);
        return ResultVo.Success(vo);
    }

    /**
     * 状态变更（冻结/解冻/注销）
     */
    @PostMapping("updateStatus")
    public ResultVo updateStatus(@JsonParam Long id ,@JsonParam Integer status,@JsonParam String photoUrl){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try{
            companyCorporateAccountService.updateStatus( id ,status, photoUrl);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 确认开户信息
     */
    @PostMapping("add")
    public ResultVo addCompanyCorporateAccount(@RequestBody CorporateAccountApplyOrderPO po){
        CurrUser currUser = getCurrUser();
        try{

            CorporateAccountApplyOrderEntity corporateAccountApplyOrderEntity=corporateAccountApplyOrderService.findById(po.getId());
            if(corporateAccountApplyOrderEntity==null){
                return ResultVo.Fail("公户申请订单不存在");
            }
            if(orderService.queryByOrderNo(corporateAccountApplyOrderEntity.getOrderNo()).getOrderStatus()!=1){
                return ResultVo.Fail("订单状态不为等待预约，请确认后再确认已开户");
            }
            //校验企业是否已经有对公户
            CompanyCorporateAccountEntity entity=new CompanyCorporateAccountEntity();
            entity.setCompanyId(corporateAccountApplyOrderEntity.getCompanyId());
            entity.setStatus(1);
            List<CompanyCorporateAccountEntity> list=companyCorporateAccountService.select(entity);
            if(!CollectionUtils.isEmpty(list) && list.size()>0){
                return ResultVo.Fail("该企业已经存在对公账户");
            }
            CompanyCorporateAccountEntity entity2=new CompanyCorporateAccountEntity();
            entity2.setStatus(1);
            entity2.setCorporateAccount(po.getCorporateAccount().trim());
            List<CompanyCorporateAccountEntity> list2=companyCorporateAccountService.select(entity2);
            if(!CollectionUtils.isEmpty(list2) && list2.size()>0){
                return ResultVo.Fail("对公账户已经存在");
            }
            //调用查询余额接口
            // 读取渠道代付相关配置 paramsType=13
            OemParamsEntity paramsEntity = this.oemParamsService.getParams(corporateAccountApplyOrderEntity.getOemCode(),13);
            if(null == paramsEntity){
                return ResultVo.Fail("未配置渠道代付相关信息！");
            }
            ComCorpAccQuery comCorpAccQuery =new ComCorpAccQuery();
            comCorpAccQuery.setTxnStffId(po.getVoucherMemberCode());
            comCorpAccQuery.setDraweeAccountNo(po.getCorporateAccount());


            JSONObject jsonObj=daifuApiService.queryCardBalance(paramsEntity,comCorpAccQuery);
            if(null != jsonObj&& !"00".equals(jsonObj.getString("bizCode"))){
                return ResultVo.Fail("系统异常，请确定开户信息是否正确或稍后再试。");
                //throw new BusinessException(jsonObj.getString("bizCodeMsg"));
            }
            //新增
            po.setCompanyId(corporateAccountApplyOrderEntity.getCompanyId());
            po.setMemberId(corporateAccountApplyOrderEntity.getMemberId());
            MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(corporateAccountApplyOrderEntity.getCompanyId());
            po.setCompanyName(memberCompanyEntity.getCompanyName());
            po.setIdCard(memberCompanyEntity.getIdCardNumber());
            MemberAccountEntity memberAccountEntity=memberAccountService.findById(corporateAccountApplyOrderEntity.getMemberId());
            po.setMemberPhone(memberAccountEntity.getMemberPhone());
            po.setOemCode(memberCompanyEntity.getOemCode());
            CompanyCorporateAccountPO companyCorporateAccountPO =new CompanyCorporateAccountPO();
            BeanUtils.copyProperties(po,companyCorporateAccountPO);
            companyCorporateAccountPO.setApplyId(po.getId());
            companyCorporateAccountPO.setId(null);
            companyCorporateAccountPO.setHeadquartersName(corporateAccountApplyOrderEntity.getHeadquartersName());
            companyCorporateAccountPO.setHeadquartersNo(corporateAccountApplyOrderEntity.getHeadquartersNo());
            CompanyCorporateAccountEntity companyCorporateAccountEntity =companyCorporateAccountService.addCompanyCorporateAccount( companyCorporateAccountPO,getCurrUseraccount());

            //更新申请表得对公户ID
            corporateAccountApplyOrderEntity.setCorporateAccountId(companyCorporateAccountEntity.getId());
            //2.7保存对公户申请单的申请银行
            corporateAccountApplyOrderEntity.setApplyBankName(Optional.ofNullable(parkCorporateAccountConfigService.findById(po.getCorporateAccountConfigId())).map(ParkCorporateAccountConfigEntity::getCorporateAccountBankName).orElse(null));
            corporateAccountApplyOrderEntity.setUpdateUser(getCurrUseraccount());
            corporateAccountApplyOrderEntity.setUpdateTime(new Date());
            corporateAccountApplyOrderEntity.setRemark("确认开户");
            corporateAccountApplyOrderService.editByIdSelective(corporateAccountApplyOrderEntity);

            OrderEntity orderEntity=orderService.queryByOrderNo(corporateAccountApplyOrderEntity.getOrderNo());
            //扣除自己资金
            userCapitalAccountService.addBalanceByProfits(orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getUserId(), 1, orderEntity.getPayAmount(), 0L, 0L, orderEntity.getPayAmount(), "对公户订单完成", getCurrUseraccount(), new Date(), 0, WalletTypeEnum.CONSUMER_WALLET.getValue());
            UserEntity oemUser = new UserEntity();
            oemUser.setOemCode(orderEntity.getOemCode());
            oemUser.setPlatformType(2);//平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
            oemUser.setAccountType(1);//账号类型  1-管理员  2-城市合伙人 3-城市合伙人 4-坐席客服 5-财务 6-经办人 7-运营
            oemUser.setStatus(1);//状态 0-禁用 1-可用
            oemUser = userService.selectOne(oemUser);
            //保存近两个月得对公户明细信息
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            Date date = new Date();
            System.out.println(df.format(date));   // 当前系统时间
            Date newDate = stepMonth(date, -2);
            CorporateAccountCollectionRecordQuery query =new CorporateAccountCollectionRecordQuery();
            query.setCorporateAccountId(companyCorporateAccountEntity.getId());
            query.setIsSystem(1);
            query.setStartDate(df.format(newDate));
            query.setEndDate(df.format(date));
            query.setOemCode(orderEntity.getOemCode());
            corporateAccountCollectionRecordService.queryCorpAccountCollectionRecordsAdmin(query);

            //给机构增加资金
            userCapitalAccountService.addBalanceByProfits(orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getOrderType(), oemUser.getId(), 2, orderEntity.getPayAmount(), orderEntity.getPayAmount(), 0L, 0L, "对公户订单完成", getCurrUseraccount(), new Date(), 1, WalletTypeEnum.CONSUMER_WALLET.getValue());
            //添加消费记录
            memberConsumptionRecordService.insertSelective(orderEntity.getOemCode(), orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getUserId(), orderEntity.getPayAmount(), getCurrUseraccount(), "对公户订单完成");

            //更改申请单主订单状态  0-待付款,1-等待预约,2-已完成,3-已取消
            orderService.updateOrderStatus(getCurrUseraccount(),corporateAccountApplyOrderEntity.getOrderNo(),2);

        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }


    public static Date stepMonth(Date sourceDate, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.MONTH, month);
        return c.getTime();
    }

        /**
         * 账户明细
         */

    @PostMapping("queryCompanyCorporateAccountDetailList")
    public ResultVo queryCompanyCorporateAccountDetailList(@RequestBody CorporateAccountCollectionRecordQuery query){
        CurrUser currUser = getCurrUser();
        PageInfo<CorporateAccountVOAdmin> pageInfo=null;
        try{
            pageInfo= corporateAccountCollectionRecordService.queryCorpAccountCollectionRecordsAdmin(query);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success(pageInfo);
    }

    /**
     * 账户明细
     */

    @PostMapping("queryCompanyCorporateAccountDetailListNotToekn")
    public ResultVo queryCompanyCorporateAccountDetailListNotToekn(@RequestBody CorporateAccountCollectionRecordQuery query){
        PageInfo<CorporateAccountVOAdmin> pageInfo=null;
        try{
            pageInfo= corporateAccountCollectionRecordService.queryCorpAccountCollectionRecordsAdmin(query);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success(pageInfo);
    }

    /**
     * 对公账户明细导出
     */

    @PostMapping("queryCompanyCorporateAccountDetailList/export")
    public ResultVo queryCompanyCorporateAccountDetailListExport(@RequestBody CorporateAccountCollectionRecordQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setOemCode(userEntity.getOemCode());
        List<CorporateAccountVOAdmin> lists = corporateAccountCollectionRecordService.getCorpAccountCollectionRecordsAdmin(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("对公账户明细", "对公账户明细", CorporateAccountVOAdmin.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("对公账户明细导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }
    /**
     * 根据开票抬头获取有剩余额度的开票订单列表
     */
    @PostMapping("listInvoiceOrderByHeaderName")
    public ResultVo listInvoiceOrderByHeaderName(@RequestBody CorporateAccountCollectionRecordQuery query, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        getCurrUser();
        try{
            if(query.getCorporateAccountId()!=null && query.getCompanyId()==null){
                CompanyCorporateAccountEntity companyCorporateAccountEntity = companyCorporateAccountService.findById(query.getCorporateAccountId());
                if(companyCorporateAccountEntity==null){
                    return ResultVo.Fail("未找到对公户信息");
                }
                query.setCompanyId(companyCorporateAccountEntity.getCompanyId());
            }
            if(query.getRecordId()==null){
                return ResultVo.Fail("收款记录id不能为空");
            }
            CorporateAccountCollectionRecordEntity corporateAccountCollectionRecordEntity = corporateAccountCollectionRecordService.findById(query.getRecordId());
            if(corporateAccountCollectionRecordEntity==null){
                return ResultVo.Fail("未找到收款记录信息");
            }
            query.setCompanyName(corporateAccountCollectionRecordEntity.getOtherPartyBankAccount());
            query.setStartDate("-1");
            query.setEndDate("-1");
            List<CorporateInvoiceOrderVO> list = this.companyCorporateAccountService.listInvoiceOrderForCorp(query);
            return ResultVo.Success(list);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 额度核销
     */
    @PostMapping("companyCorporateAccountVerification")
    public ResultVo companyCorporateAccountVerification(@RequestBody CompanyCorporateAccountVerificationQuery query){
        CurrUser currUser = getCurrUser();
        try{
            corporateAccountCollectionRecordService.companyCorporateAccountVerification(query,getCurrUseraccount());
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }
    /**
     * 查询核销记录
     */
    @PostMapping("collectionWithdrawalAmountRhangeRecord/list")
    public ResultVo queryCollectionWithdrawalAmountRhangeRecordList(@JsonParam String bankCollectionRecordNo){
        CurrUser currUser = getCurrUser();
        List<CollectionWithdrawalAmountChangeRecordEntity> list=null;
        try{
            Example example=new Example(CollectionWithdrawalAmountChangeRecordEntity.class);
            example.createCriteria().andEqualTo("bankCollectionRecordNo",bankCollectionRecordNo);
            example.setOrderByClause("change_time desc");
            example.orderBy("addTime").desc();
            list=collectionWithdrawalAmountChangeRecordService.selectByExample(example);
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success(list);
    }

    /**
     * 对公户修改配置信息
     */
    @PostMapping("update")
    public ResultVo update(@RequestBody CompanyCorporateAccountPO po){
        CurrUser currUser = getCurrUser();
        List<CollectionWithdrawalAmountChangeRecordEntity> list=null;
        if(po.getId()==null){
            return ResultVo.Fail("请选择对公户账号");
        }
        try{
            companyCorporateAccountService.update(po,getCurrUseraccount());
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success(list);
    }

}
