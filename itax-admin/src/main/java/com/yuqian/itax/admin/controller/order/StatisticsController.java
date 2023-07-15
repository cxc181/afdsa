package com.yuqian.itax.admin.controller.order;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.entity.query.BaseQuery;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.snapshot.entity.query.*;
import com.yuqian.itax.snapshot.entity.vo.*;
import com.yuqian.itax.snapshot.service.InvoiceOrderSnapshotService;
import com.yuqian.itax.snapshot.service.MemberSnapshotService;
import com.yuqian.itax.snapshot.service.OrderSnapshotService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.UserOrderStatisticsDayEntity;
import com.yuqian.itax.user.entity.query.IndividualQuery;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayAgentQuery;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayEmployeeQuery;
import com.yuqian.itax.user.entity.query.UserOrderStatisticsDayMemberQuery;
import com.yuqian.itax.user.entity.vo.IndividualVO;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayAgentVO;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayEmployeeVO;
import com.yuqian.itax.user.entity.vo.UserOrderStatisticsDayMemberVO;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.user.service.UserOrderStatisticsDayService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 统计数据controller
 * @author：pengwei
 * @Date：2020/10/21 15:32
 * @version：1.0
 */
@RestController
@RequestMapping("statistics")
@Slf4j
public class StatisticsController extends BaseController {

    @Autowired
    UserOrderStatisticsDayService  userOrderStatisticsDayService;

    @Autowired
    OrderSnapshotService orderSnapshotService;
    @Autowired
    MemberSnapshotService memberSnapshotService;
    @Autowired
    InvoiceOrderSnapshotService invoiceOrderSnapshotService;

    @Autowired
    MemberCompanyService memberCompanyService;

    /**
     * 个体数据统计
     */
    @PostMapping("individual")
    public ResultVo individual(@RequestBody IndividualQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        //初始化参数
        initParam(query, userEntity);

        return ResultVo.Success(memberCompanyService.individualCount(query));
    }

    /**
     * 个体数据统计导出
     */
    @PostMapping("individual/export")
    public ResultVo individualExport(@RequestBody IndividualQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try {
            //初始化参数
            initParam(query, userEntity);

            List<IndividualVO> lists = memberCompanyService.individualCount(query);
            if (CollectionUtil.isEmpty(lists)) {
                return ResultVo.Fail("暂无数据导出");
            }
            exportExcel("个体数据统计", "个体数据统计", IndividualVO.class, lists);
            return null;
        } catch (Exception e) {
            log.error("个体数据统计导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * 开票数据统计
     */
    @PostMapping("invoice")
    public ResultVo invoice(@RequestBody InvoiceSnapshotQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        //初始化参数
        initParam(query, userEntity);

        return ResultVo.Success(invoiceOrderSnapshotService.invoiceCount(query));
    }

    /**
     * 开票数据统计
     */
    @PostMapping("invoice/export")
    public ResultVo invoiceExport(@RequestBody InvoiceSnapshotQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try {
            //初始化参数
            initParam(query, userEntity);

            List<InvoiceSnapshotVO> lists = invoiceOrderSnapshotService.invoiceCount(query);
            if (CollectionUtil.isEmpty(lists)) {
                return ResultVo.Fail("暂无数据导出");
            }
            exportExcel("开票数据统计", "开票数据统计", InvoiceSnapshotVO.class, lists);
            return null;
        } catch (Exception e) {
            log.error("开票数据统计导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * 充值提现统计
     */
    @PostMapping("recharge/withdraw")
    public ResultVo rechargeWithdraw(@RequestBody OrderSnapshotQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        //初始化参数
        initParam(query, userEntity);

        return ResultVo.Success(orderSnapshotService.rechargeWithdraw(query));
    }

    /**
     * 充值提现统计导出
     */
    @PostMapping("recharge/withdraw/export")
    public ResultVo rechargeWithdrawExport(@RequestBody OrderSnapshotQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try {
            //初始化参数
            initParam(query, userEntity);

            List<RechargeWithdrawSnapshotVO> lists = orderSnapshotService.rechargeWithdraw(query);
            if (CollectionUtil.isEmpty(lists)) {
                return ResultVo.Fail("暂无数据导出");
            }
            exportExcel("充值提现统计", "充值提现统计", RechargeWithdrawSnapshotVO.class, lists);
            return null;
        } catch (Exception e) {
            log.error("充值提现导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * oem机构资金流统计
     */
    @PostMapping("oem/capital")
    public ResultVo oemCapital(@RequestBody OemCapitalQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        //初始化参数
        initParam(query, userEntity);

        List<OemCapitalVO> lists = orderSnapshotService.oemCapital(query);
        return ResultVo.Success(lists);
    }

    /**
     * oem机构资金流统计
     */
    @PostMapping("oem/capital/export")
    public ResultVo oemCapitalExport(@RequestBody OemCapitalQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        try {
            //初始化参数
            initParam(query, userEntity);

            List<OemCapitalVO> lists = orderSnapshotService.oemCapital(query);
            if (CollectionUtil.isEmpty(lists)) {
                return ResultVo.Fail("暂无数据导出");
            }
            exportExcel("oem机构资金流统计", "oem机构资金流统计", OemCapitalVO.class, lists);
            return null;
        } catch (Exception e) {
            log.error("oem机构资金流统计导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }

    /**
     * 初始化参数
     * @param query
     * @param userEntity
     */
    public void initParam(BaseQuery query, UserEntity userEntity) {
        DateTime now = DateTime.now().minusDays(1);
        if (StringUtils.isBlank(query.getStartDate())) {
            Date date = now.minusMonths(1).millisOfDay().withMinimumValue().toDate();
            query.setStartDate(DateUtil.formatTimesTampDate(date));
        } else {
            try {
                Date date = DateTime.parse(query.getStartDate()).millisOfDay().withMinimumValue().toDate();
                query.setStartDate(DateUtil.formatTimesTampDate(date));
            } catch (Exception e) {
                log.error("日期转换异常");
                log.error(e.getMessage(), e);
                Date date = now.minusMonths(1).millisOfDay().withMinimumValue().toDate();
                query.setStartDate(DateUtil.formatTimesTampDate(date));
            }
        }
        if (StringUtils.isBlank(query.getEndDate())) {
            Date date = now.millisOfDay().withMaximumValue().toDate();
            query.setEndDate(DateUtil.formatTimesTampDate(date));
        } else {
            try {
                Date date = DateTime.parse(query.getEndDate()).millisOfDay().withMaximumValue().toDate();
                query.setEndDate(DateUtil.formatTimesTampDate(date));
            } catch (Exception e) {
                log.error("日期转换异常");
                log.error(e.getMessage(), e);
                Date date = now.millisOfDay().withMaximumValue().toDate();
                query.setEndDate(DateUtil.formatTimesTampDate(date));
            }
        }

        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
    }

    /**
     * 员工业绩统计
     */

    @PostMapping("employee")
    public ResultVo employee(@RequestBody UserOrderStatisticsDayEmployeeQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
        if(StringUtil.isBlank(query.getStartDate())&&StringUtil.isBlank(query.getEndDate())){
            return ResultVo.Fail("请输入统计周期");
        }
        query.setStartDate(query.getStartDate()+" 00:00:00");
        query.setEndDate(query.getEndDate()+" 23:59:59");
        PageInfo<UserOrderStatisticsDayEmployeeVO> list= userOrderStatisticsDayService.queryUserOrderStatisticsDayEmployeePage(query);
        return ResultVo.Success(list);
    }

    /**
     * 员工业绩统计导出
     */

    @PostMapping("employee/export")
    public ResultVo employeeExport(@RequestBody UserOrderStatisticsDayEmployeeQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
        if(StringUtil.isBlank(query.getStartDate())&&StringUtil.isBlank(query.getEndDate())){
            return ResultVo.Fail("请输入统计周期");
        }
        query.setStartDate(query.getStartDate()+" 00:00:00");
        query.setEndDate(query.getEndDate()+" 23:59:59");

        List<UserOrderStatisticsDayEmployeeVO> list= userOrderStatisticsDayService.queryUserOrderStatisticsDayEmployee(query);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel(" 员工业绩统计导出", " 员工业绩统计导出", UserOrderStatisticsDayEmployeeVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("员工业绩统计导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }
    /**
     * 会员业绩统计
     */

    @PostMapping("member")
    public ResultVo member(@RequestBody UserOrderStatisticsDayMemberQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
        if(StringUtil.isBlank(query.getStartDate())&&StringUtil.isBlank(query.getEndDate())){
            return ResultVo.Fail("请输入统计周期");
        }
        query.setStartDate(query.getStartDate()+" 00:00:00");
        query.setEndDate(query.getEndDate()+" 23:59:59");

        PageInfo<UserOrderStatisticsDayMemberVO> list= userOrderStatisticsDayService.queryUserOrderStatisticsDayMemberPage(query);
        return ResultVo.Success(list);
    }
    /**
     * 会员业绩统计导出
     */

    @PostMapping("member/export")
    public ResultVo memberExport(@RequestBody UserOrderStatisticsDayMemberQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
        if(StringUtil.isBlank(query.getStartDate())&&StringUtil.isBlank(query.getEndDate())){
            return ResultVo.Fail("请输入统计周期");
        }
        query.setStartDate(query.getStartDate()+" 00:00:00");
        query.setEndDate(query.getEndDate()+" 23:59:59");

        List<UserOrderStatisticsDayMemberVO> list= userOrderStatisticsDayService.queryUserOrderStatisticsDayMember(query);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel(" 会员业绩统计导出", " 会员业绩统计导出", UserOrderStatisticsDayMemberVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("会员业绩统计导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }

    /**
     * 合伙人业绩统计
     */

    @PostMapping("agent")
    public ResultVo agent(@RequestBody UserOrderStatisticsDayAgentQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
        if(StringUtil.isBlank(query.getStartDate())&&StringUtil.isBlank(query.getEndDate())){
            return ResultVo.Fail("请输入统计周期");
        }
        query.setStartDate(query.getStartDate()+" 00:00:00");
        query.setEndDate(query.getEndDate()+" 23:59:59");

        PageInfo<UserOrderStatisticsDayAgentVO> list= userOrderStatisticsDayService.queryUserOrderStatisticsDayAgentPage(query);
        return ResultVo.Success(list);
    }

    /**
     * 合伙人业绩统计导出
     */

    @PostMapping("agent/export")
    public ResultVo agentExport(@RequestBody UserOrderStatisticsDayAgentQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
        if(StringUtil.isBlank(query.getStartDate())&&StringUtil.isBlank(query.getEndDate())){
            return ResultVo.Fail("请输入统计周期");
        }
        query.setStartDate(query.getStartDate()+" 00:00:00");
        query.setEndDate(query.getEndDate()+" 23:59:59");

        List<UserOrderStatisticsDayAgentVO> list= userOrderStatisticsDayService.queryUserOrderStatisticsDayAgent(query);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel(" 合伙人业绩统计导出", " 合伙人业绩统计导出", UserOrderStatisticsDayAgentVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("合伙人业绩统计导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }
    /**
     * 用户数统计
     */
    @PostMapping("user")
    public ResultVo user(@RequestBody MemberSnapshotQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
        if(StringUtil.isBlank(query.getStartDate())&&StringUtil.isBlank(query.getEndDate())){
            return ResultVo.Fail("请输入统计周期");
        }
        query.setStartDate(query.getStartDate()+" 00:00:00");
        query.setEndDate(query.getEndDate()+" 23:59:59");

        List<MemberSnapshotVO> list= memberSnapshotService.queryMemberSnapshotUser(query);
        //计算汇总数据
        MemberSnapshotVO memberSnapshotVO=new MemberSnapshotVO();
        for (MemberSnapshotVO vo:list) {
            memberSnapshotVO.setTotalMember(memberSnapshotVO.getTotalMember()+vo.getTotalMember());
            memberSnapshotVO.setTotalPtMember(memberSnapshotVO.getTotalPtMember()+vo.getTotalPtMember());
            memberSnapshotVO.setTotalVipMember(memberSnapshotVO.getTotalVipMember()+vo.getTotalVipMember());
            memberSnapshotVO.setTotalSwgwMember(memberSnapshotVO.getTotalSwgwMember()+vo.getTotalSwgwMember());
            memberSnapshotVO.setTotalCsfwsMember(memberSnapshotVO.getTotalCsfwsMember()+vo.getTotalCsfwsMember());
            memberSnapshotVO.setTotalEmpMember(memberSnapshotVO.getTotalEmpMember()+vo.getTotalEmpMember());
            memberSnapshotVO.setTotalHhr(memberSnapshotVO.getTotalHhr()+vo.getTotalHhr());
            memberSnapshotVO.setTotalGjhhr(memberSnapshotVO.getTotalGjhhr()+vo.getTotalGjhhr());
            memberSnapshotVO.setAddMember(memberSnapshotVO.getAddMember()+vo.getAddMember());
            memberSnapshotVO.setAddVipMember(memberSnapshotVO.getAddVipMember()+vo.getAddVipMember());
            memberSnapshotVO.setAddSwgwMember(memberSnapshotVO.getAddSwgwMember()+vo.getAddSwgwMember());
            memberSnapshotVO.setAddCsfwsMember(memberSnapshotVO.getAddCsfwsMember()+vo.getAddCsfwsMember());
            memberSnapshotVO.setAddEmpMember(memberSnapshotVO.getAddEmpMember()+vo.getAddEmpMember());
            memberSnapshotVO.setAddHhr(memberSnapshotVO.getAddHhr()+vo.getAddHhr());
            memberSnapshotVO.setAddGjhhr(memberSnapshotVO.getAddGjhhr()+vo.getAddGjhhr());
        }
        memberSnapshotVO.setOemName("汇总");
        list.add(memberSnapshotVO);
        return ResultVo.Success(list);
    }

    /**
     * 用户数统计导出
     */
    @PostMapping("user/export")
    public ResultVo userExport(@RequestBody MemberSnapshotQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
        if(StringUtil.isBlank(query.getStartDate())&&StringUtil.isBlank(query.getEndDate())){
            return ResultVo.Fail("请输入统计周期");
        }
        query.setStartDate(query.getStartDate()+" 00:00:00");
        query.setEndDate(query.getEndDate()+" 23:59:59");

        List<MemberSnapshotVO> list= memberSnapshotService.queryMemberSnapshotUser(query);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        //计算汇总数据
        MemberSnapshotVO memberSnapshotVO=new MemberSnapshotVO();
        for (MemberSnapshotVO vo:list) {
            memberSnapshotVO.setTotalMember(memberSnapshotVO.getTotalMember()+vo.getTotalMember());
            memberSnapshotVO.setTotalPtMember(memberSnapshotVO.getTotalPtMember()+vo.getTotalPtMember());
            memberSnapshotVO.setTotalVipMember(memberSnapshotVO.getTotalVipMember()+vo.getTotalVipMember());
            memberSnapshotVO.setTotalSwgwMember(memberSnapshotVO.getTotalSwgwMember()+vo.getTotalSwgwMember());
            memberSnapshotVO.setTotalCsfwsMember(memberSnapshotVO.getTotalCsfwsMember()+vo.getTotalCsfwsMember());
            memberSnapshotVO.setTotalEmpMember(memberSnapshotVO.getTotalEmpMember()+vo.getTotalEmpMember());
            memberSnapshotVO.setTotalHhr(memberSnapshotVO.getTotalGjhhr()+vo.getTotalHhr());
            memberSnapshotVO.setTotalGjhhr(memberSnapshotVO.getTotalGjhhr()+vo.getTotalGjhhr());
            memberSnapshotVO.setAddMember(memberSnapshotVO.getAddMember()+vo.getAddMember());
            memberSnapshotVO.setAddVipMember(memberSnapshotVO.getAddVipMember()+vo.getAddVipMember());
            memberSnapshotVO.setAddSwgwMember(memberSnapshotVO.getAddSwgwMember()+vo.getAddSwgwMember());
            memberSnapshotVO.setAddCsfwsMember(memberSnapshotVO.getAddCsfwsMember()+vo.getAddCsfwsMember());
            memberSnapshotVO.setAddEmpMember(memberSnapshotVO.getAddEmpMember()+vo.getAddEmpMember());
            memberSnapshotVO.setAddHhr(memberSnapshotVO.getAddHhr()+vo.getAddHhr());
            memberSnapshotVO.setAddGjhhr(memberSnapshotVO.getAddGjhhr()+vo.getAddGjhhr());
        }
        memberSnapshotVO.setOemName("汇总");
        list.add(memberSnapshotVO);
        try {
            exportExcel(" 用户数统计导出", " 用户数统计导出", MemberSnapshotVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error(" 用户数统计统计导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }
    /**
     *
     * 园区交易量统计
     */
    @PostMapping("park")
    public ResultVo park(@RequestBody InvoiceOrderSnapshotParkQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(userEntity.getPlatformType()!=1){
            return ResultVo.Fail("仅平台组织的账号可以查看此报表");
        }
        if(StringUtil.isBlank(query.getStartDate())&&StringUtil.isBlank(query.getEndDate())){
            return ResultVo.Fail("请输入统计周期");
        }
        query.setStartDate(query.getStartDate()+" 00:00:00");
        query.setEndDate(query.getEndDate()+" 23:59:59");

        List<InvoiceOrderSnapshotParkVO> list= invoiceOrderSnapshotService.queryInvoiceOrderSnapshotPark(query);
        //计算汇总数据
        Integer companyNumber=0;
        Integer cannelCompanyNumber=0;
        Integer addCompanyNumber=0;
        BigDecimal addTotalInvoiceAmount=new BigDecimal(0);
        BigDecimal addZpInvoiceMoney=new BigDecimal(0);
        BigDecimal addPpInvoiceMoney=new BigDecimal(0);
        Integer addTotalInvoiceNumber=0;
        Integer addCannelCompanyNumber=0;
        BigDecimal totalInvoiceAmount=new BigDecimal(0);
        BigDecimal zpInvoiceMoney=new BigDecimal(0);
        BigDecimal ppInvoiceMoney=new BigDecimal(0);
        for (InvoiceOrderSnapshotParkVO vo:list) {
            companyNumber+=vo.getCompanyNumber();
            cannelCompanyNumber+=vo.getCannelCompanyNumber();
            addCompanyNumber+=vo.getAddCompanyNumber();
            addTotalInvoiceAmount=addTotalInvoiceAmount.add(vo.getAddTotalInvoiceAmount()==null?new BigDecimal(0):vo.getAddTotalInvoiceAmount());
            addZpInvoiceMoney=addZpInvoiceMoney.add(vo.getAddZpInvoiceMoney()==null?new BigDecimal(0):vo.getAddZpInvoiceMoney());
            addPpInvoiceMoney=addPpInvoiceMoney.add(vo.getAddPpInvoiceMoney()==null?new BigDecimal(0):vo.getAddPpInvoiceMoney());
            addTotalInvoiceNumber+=vo.getAddTotalInvoiceNumber();
            addCannelCompanyNumber+=vo.getAddCannelCompanyNumber();
            totalInvoiceAmount=totalInvoiceAmount.add(vo.getTotalInvoiceAmount()==null?new BigDecimal(0):vo.getTotalInvoiceAmount());
            zpInvoiceMoney=zpInvoiceMoney.add(vo.getZpInvoiceMoney()==null?new BigDecimal(0):vo.getZpInvoiceMoney());
            ppInvoiceMoney=ppInvoiceMoney.add(vo.getPpInvoiceMoney()==null?new BigDecimal(0):vo.getPpInvoiceMoney());
        }
        InvoiceOrderSnapshotParkVO invoiceOrderSnapshotParkVO=new InvoiceOrderSnapshotParkVO();
        invoiceOrderSnapshotParkVO.setParkName("汇总");
        invoiceOrderSnapshotParkVO.setCompanyNumber(companyNumber);
        invoiceOrderSnapshotParkVO.setCannelCompanyNumber(cannelCompanyNumber);
        invoiceOrderSnapshotParkVO.setAddCompanyNumber(addCompanyNumber);
        invoiceOrderSnapshotParkVO.setAddCannelCompanyNumber(addCannelCompanyNumber);
        invoiceOrderSnapshotParkVO.setAddTotalInvoiceAmount(addTotalInvoiceAmount);
        invoiceOrderSnapshotParkVO.setAddZpInvoiceMoney(addZpInvoiceMoney);
        invoiceOrderSnapshotParkVO.setAddPpInvoiceMoney(addPpInvoiceMoney);
        invoiceOrderSnapshotParkVO.setAddTotalInvoiceNumber(addTotalInvoiceNumber);
        invoiceOrderSnapshotParkVO.setTotalInvoiceAmount(totalInvoiceAmount);
        invoiceOrderSnapshotParkVO.setZpInvoiceMoney(zpInvoiceMoney);
        invoiceOrderSnapshotParkVO.setPpInvoiceMoney(ppInvoiceMoney);
        list.add(invoiceOrderSnapshotParkVO);
        return ResultVo.Success(list);
    }
    /**
     *
     * 园区交易量统计导出
     */
    @PostMapping("park/export")
    public ResultVo parkExport( @RequestBody InvoiceOrderSnapshotParkQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(userEntity.getPlatformType()!=1){
            return ResultVo.Fail("仅平台组织的账号可以查看此报表");
        }
        if(StringUtil.isBlank(query.getStartDate())&&StringUtil.isBlank(query.getEndDate())){
            return ResultVo.Fail("请输入统计周期");
        }
        query.setStartDate(query.getStartDate()+" 00:00:00");
        query.setEndDate(query.getEndDate()+" 23:59:59");

        List<InvoiceOrderSnapshotParkVO> list= invoiceOrderSnapshotService.queryInvoiceOrderSnapshotPark(query);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Fail("暂无数据导出");
        }
        //计算汇总数据
        Integer companyNumber=0;
        Integer cannelCompanyNumber=0;
        Integer addCompanyNumber=0;
        BigDecimal addTotalInvoiceAmount=new BigDecimal(0);
        BigDecimal addZpInvoiceMoney=new BigDecimal(0);
        BigDecimal addPpInvoiceMoney=new BigDecimal(0);
        Integer addTotalInvoiceNumber=0;
        Integer addCannelCompanyNumber=0;
        BigDecimal totalInvoiceAmount=new BigDecimal(0);
        BigDecimal zpInvoiceMoney=new BigDecimal(0);
        BigDecimal ppInvoiceMoney=new BigDecimal(0);
        for (InvoiceOrderSnapshotParkVO vo:list) {
            companyNumber+=vo.getCompanyNumber();
            cannelCompanyNumber+=vo.getCannelCompanyNumber();
            addCompanyNumber+=vo.getAddCompanyNumber();
            addTotalInvoiceAmount=addTotalInvoiceAmount.add(vo.getAddTotalInvoiceAmount()==null?new BigDecimal(0):vo.getAddTotalInvoiceAmount());
            addZpInvoiceMoney=addZpInvoiceMoney.add(vo.getAddZpInvoiceMoney()==null?new BigDecimal(0):vo.getAddZpInvoiceMoney());
            addPpInvoiceMoney=addPpInvoiceMoney.add(vo.getAddPpInvoiceMoney()==null?new BigDecimal(0):vo.getAddPpInvoiceMoney());
            addTotalInvoiceNumber+=vo.getAddTotalInvoiceNumber();
            addCannelCompanyNumber+=vo.getAddCannelCompanyNumber();
            totalInvoiceAmount=totalInvoiceAmount.add(vo.getTotalInvoiceAmount()==null?new BigDecimal(0):vo.getTotalInvoiceAmount());
            zpInvoiceMoney=zpInvoiceMoney.add(vo.getZpInvoiceMoney()==null?new BigDecimal(0):vo.getZpInvoiceMoney());
            ppInvoiceMoney=ppInvoiceMoney.add(vo.getPpInvoiceMoney()==null?new BigDecimal(0):vo.getPpInvoiceMoney());
        }
        InvoiceOrderSnapshotParkVO invoiceOrderSnapshotParkVO=new InvoiceOrderSnapshotParkVO();
        invoiceOrderSnapshotParkVO.setParkName("汇总");
        invoiceOrderSnapshotParkVO.setCompanyNumber(companyNumber);
        invoiceOrderSnapshotParkVO.setCannelCompanyNumber(cannelCompanyNumber);
        invoiceOrderSnapshotParkVO.setAddCompanyNumber(addCompanyNumber);
        invoiceOrderSnapshotParkVO.setAddCannelCompanyNumber(addCannelCompanyNumber);
        invoiceOrderSnapshotParkVO.setAddTotalInvoiceAmount(addTotalInvoiceAmount);
        invoiceOrderSnapshotParkVO.setAddZpInvoiceMoney(addZpInvoiceMoney);
        invoiceOrderSnapshotParkVO.setAddPpInvoiceMoney(addPpInvoiceMoney);
        invoiceOrderSnapshotParkVO.setAddTotalInvoiceNumber(addTotalInvoiceNumber);
        invoiceOrderSnapshotParkVO.setTotalInvoiceAmount(totalInvoiceAmount);
        invoiceOrderSnapshotParkVO.setZpInvoiceMoney(zpInvoiceMoney);
        invoiceOrderSnapshotParkVO.setPpInvoiceMoney(ppInvoiceMoney);
        list.add(invoiceOrderSnapshotParkVO);


        try {
            exportExcel(" 园区交易量统计导出", " 园区交易量统计导出", InvoiceOrderSnapshotParkVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error(" 园区交易量统计导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }

}
