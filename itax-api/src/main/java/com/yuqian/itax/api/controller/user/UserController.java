package com.yuqian.itax.api.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.capital.entity.vo.MemberCapitalAccountVO;
import com.yuqian.itax.capital.entity.vo.ProfitDetailVO;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.product.entity.vo.MemberVipVO;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.vo.PosterBase64VO;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberLevelEntity;
import com.yuqian.itax.user.entity.dto.EmployeeManageOfListDetailDTO;
import com.yuqian.itax.user.entity.dto.EmployeeManageOfSurveyDTO;
import com.yuqian.itax.user.entity.dto.GenqrcodeDTO;
import com.yuqian.itax.user.entity.dto.UserAuthDTO;
import com.yuqian.itax.user.entity.query.EmployeeManageOfListQuery;
import com.yuqian.itax.user.entity.query.EmployeeManageOfPushListQuery;
import com.yuqian.itax.user.entity.query.ExtendUserQuery;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.enums.MemberLevelEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberLevelService;
import com.yuqian.itax.wechat.WeChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Api(tags = "会员账号控制器")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private MemberAccountService accountService;
    @Autowired
    private MemberLevelService memberLevelService;
    @Autowired
    private UserCapitalAccountService userCapitalAccountService;
    @Autowired
    private InvoiceOrderService invoiceOrderService;
    @Autowired
    private WeChatService weChatService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProfitsDetailService profitsDetailService;

    /**
     * @Description 更新用户城市信息
     * @Author Kaven
     * @Date 2019/12/10 9:12
     * @Param provinceCode cityCode cityName
     * @Return ResultVo
     */
    @ApiOperation("更新用户城市信息")
    @PostMapping("/updateUserRegion")
    public ResultVo updateUserRegion(@JsonParam String provinceCode, @JsonParam String cityCode, @JsonParam String cityName){
        this.accountService.updateUserRegion(getCurrUser().getUserId(),provinceCode,cityCode,cityName);
        return ResultVo.Success();
    }

    /**
     * @Description 用户实名认证（二要素验证）
     * @Author  Kaven
     * @Date   2020/2/13 10:16
     * @Param userName idCardNo idCardFront idCardBack expireDate
     * @Return ResultVo
     * @Exception BusinessException
    */
    @ApiOperation("用户实名认证（二要素验证）")
    @PostMapping("/userAuth")
    public ResultVo userAuth(@RequestBody @Valid UserAuthDTO dto, BindingResult results) throws IOException {
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        dto.setIsOther(0);// 用户本人实名认证
        this.accountService.userAuth(getCurrUser().getUserId(),oemCode,dto,0);
        return ResultVo.Success();
    }

    /**
     * @Description 获取会员VIP信息
     * @Author  Kaven
     * @Date   2019/12/14 16:14
     * @Param  prodType
     * @Return ResultVo
     */
    @ApiOperation("获取会员VIP信息")
    @ApiImplicitParam(name="prodType",value="产品类型（9-税务顾问 10-城市服务商）",dataType="Integer",required = true)
    @PostMapping("/getMemberVipInfo")
    public ResultVo<MemberVipVO> getMemberVipInfo(@JsonParam Integer prodType){
        if(null == prodType){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }

        JSONObject params = new JSONObject();
        params.put("prodType", prodType);
        MemberBaseInfoVO memberBaseInfoVO = accountService.getMemberBaseInfo(getCurrUserId());
        if(memberBaseInfoVO!= null && memberBaseInfoVO.getLevelNo().equals(MemberLevelEnum.DIAMOND.getValue())) {
            params.put("prodType", 10);
            prodType = 10;
        }
        MemberVipVO vipInfo = this.memberLevelService.getMemberVipInfo(getRequestHeadParams("oemCode"), prodType);
        return ResultVo.Success(vipInfo);
    }

    /**
     * @Description 获取会员中心数据
     * @Author  Kaven
     * @Date   2020/6/3 9:50
     * @Param
     * @Return
     * @Exception
    */
    @ApiOperation("获取会员中心数据")
    @PostMapping("/getVipCenterInfo")
    public ResultVo<List<MemberUpgradeRulesVO>> getVipCenterInfo(){
        log.info("收到会员中心数据查询请求：{},{}",getCurrUserId(),getRequestHeadParams("oemCode"));
        List<MemberUpgradeRulesVO> list = orderService.selectUpgradeInfo(getRequestHeadParams("oemCode"), getCurrUserId());
        return ResultVo.Success(list);
    }

    /**
     * @Description 获取会员个人基本信息
     * @Author  Kaven
     * @Date   2019/12/16 9:30
     * @Return ResultVo
    */
    @ApiOperation("获取会员个人基本信息")
    @PostMapping("getBaseInfo")
    public ResultVo<MemberBaseInfoVO> getBaseInfo(){
        MemberBaseInfoVO baseInfo = this.accountService.getMemberBaseInfo(getCurrUser().getUserId());
        return ResultVo.Success(baseInfo);
    }

    /**
     * @Description 修改用户昵称
     * @Author  Kaven
     * @Date   2019/12/16 16:29
     * @Param  nickName
     * @Return ResultVo
    */
    @ApiOperation("修改用户昵称")
    @ApiImplicitParam(name="nickName",value="用户昵称",dataType="String",required = true)
    @PostMapping("/updateNickname")
    public ResultVo updateNickname(@JsonParam String nickName){
        if(StringUtils.isBlank(nickName)){
            return ResultVo.Fail("操作失败，昵称不能为空！");
        }
        this.accountService.updateNickname(getCurrUser().getUserId(),nickName);
        return ResultVo.Success();
    }

    /**
     * @Description 修改用户备注
     * @Author  Kaven
     * @Date   2020/6/5 9:10
     * @Param userId remark
     * @Return ResultVo
     * @Exception
    */
    @ApiOperation("推广中心-修改用户备注")
    @ApiImplicitParam(name="remark",value="备注信息",dataType="String",required = true)
    @PostMapping("/updateUserRemark")
    public ResultVo updateUserRemark(@JsonParam Long userId,@JsonParam String remark){
        if(null == userId){
            return ResultVo.Fail("操作失败，用户ID不能为空！");
        }
        this.accountService.updateUserRemark(getCurrUserId(),userId,remark,getRequestHeadParams("oemCode"));
        return ResultVo.Success();
    }

    /**
     * @Description 推广中心-业绩总览查询
     * @Author  Kaven
     * @Date   2020/6/5 14:55
     * @Param currUserId：用户登录ID flag:传1表示查询本月业绩，不传查业绩总览
     * @Return ResultVo<AchievementStatVO>
     * @Exception
    */
    @ApiOperation("推广中心-业绩总览查询")
    @PostMapping("queryAchievement")
    public ResultVo<AchievementStatVO> queryAchievement(){
        String oemCode = this.getRequestHeadParams("oemCode");
        AchievementStatVO statVO = this.accountService.queryAchievementStatistic(getCurrUserId(),oemCode);
        return ResultVo.Success(statVO);
    }

    /**
     * @Description 推广中心-业绩总览-直推用户列表查询(目前此接口没有使用，现在使用的与员工直推用户列表共用)
     * @Author  Kaven
     * @Date   2020/6/5 15:28
     * @Param userId-筛选时传递，不传查所有
     * @Return ResultVo<ExtendUserVO>
     * @Exception
    */
    @ApiOperation("推广中心-业绩总览-直推用户列表查询(目前此接口没有使用，现在使用的与员工直推用户列表共用)")
    @Deprecated
    @PostMapping("listDirectUsers")
    public ResultVo<PageResultVo<ExtendUserVO>> listDirectUsers(@RequestBody ExtendUserQuery query){
        query.setCurrUserId(getCurrUserId());
        query.setOemCode(this.getRequestHeadParams("oemCode"));
        PageResultVo<ExtendUserVO> pageData = this.invoiceOrderService.listDirectUsers(query);
        return ResultVo.Success(pageData);
    }

    /**
     * @Description 推广中心-直推用户列表-直推用户推广业绩
     * @Author  yejian
     * @Date   2020/6/10 14:28
     * @Param userId
     * @Return ResultVo<ExtendUserVO>
     */
    @ApiOperation("推广中心-直推用户列表-直推用户推广业绩")
    @PostMapping("pushExtendResult")
    public ResultVo<PushExtendResultVO> listDirectUsers(@JsonParam Long userId){
        PushExtendResultVO pushExtendResult = accountService.queryPushExtendResult(userId);
        return ResultVo.Success(pushExtendResult);
    }

    /**
     * @Description 推广中心-升为直客
     * @Author yejian
     * @Date 2020/6/11 14:28
     * @Param userId
     * @Return ResultVo
     * V2.2版本去除该功能
     */
//    @ApiOperation("推广中心-升为直客")
//    @PostMapping("upgradeToDirect")
//    public ResultVo upgradeToDirect(@JsonParam Long userId){
//        invoiceOrderService.upgradeToDirect(getCurrUserId(), userId);
//        return ResultVo.Success();
//    }

    /**
     * @Description 推广中心-会员个体列表查询
     * @Author Kaven
     * @Date 2020/6/5 9:23
     * @Param userId
     * @Return ResultVo<MemberCoStatisticVO>
     * @Exception
     */
    @ApiOperation("推广中心-会员个体列表查询")
    @PostMapping("listMemberCompany")
    public ResultVo<MemberCoStatisticVO> listMemberComInvoice(@RequestBody ExtendUserQuery query){
        if(null == query){
            return ResultVo.Fail("查询失败，参数不能为空");
        }
        if(null == query.getUserId()){
            return ResultVo.Fail("查询失败，用户ID不能为空");
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        query.setOemCode(oemCode);
        MemberCoStatisticVO memberCompnany = this.accountService.listMemberCompany(query);
        return ResultVo.Success(memberCompnany);
    }

    /**
     * @Description 员工注销
     * @Author  Kaven
     * @Date   2020/4/20 11:53
     * @Param   memberId
     * @Return  ResultVo
     * @Exception  BusinessException
    */
    @ApiOperation("员工注销")
    @PostMapping("/cancelStaff")
    public ResultVo cancelStaff(@JsonParam Long memberId){
        if(null == memberId){
            return ResultVo.Fail("操作失败，员工ID不能为空！");
        }
        this.accountService.cancelStaff(getCurrUser().getUserId(), memberId, getRequestHeadParams("oemCode"));
        return ResultVo.Success();
    }

    /**
     * @Description 获取我的钱包金额
     * @Author  Kaven
     * @Date   2019/12/25 11:17
     * @Param
     * @Return
     * @Exception
    */
    @ApiOperation("获取我的钱包金额")
    @PostMapping("getBalance")
    public ResultVo<MemberCapitalAccountVO> getBalance(){
        MemberCapitalAccountVO accountVO = this.userCapitalAccountService.getBalance(getCurrUser().getUserId(), getRequestHeadParams("oemCode"));
        DictionaryEntity dict = this.sysDictionaryService.getByCode("withdraw_scheme_switch");
        if (null != dict) {
            accountVO.setWithdrawSchemeSwitch(Integer.valueOf(dict.getDictValue()));
        }
        return ResultVo.Success(accountVO);
    }


    /**
     * @Description 推广中心-会员统计信息查询
     * @Author  Kaven
     * @Date   2019/12/18 9:58
     * @Return ResultVo
     * @Exception BusinessException
     */
    @ApiOperation("会员统计信息查询")
    @PostMapping("memberExtendStats")
    public ResultVo<MemberExtendVO> memberExtendStats(@RequestBody MemberExtendQuery query){
        query.setUserId(getCurrUserId());
        query.setOemCode(getRequestHeadParams("oemCode"));
        MemberExtendVO extendVO = this.accountService.memberExtendStats(query);
        return ResultVo.Success(extendVO);
    }

    /**
     * @Description 推广中心-企业注册进度跟进
     * @Author  Kaven
     * @Date   2020/6/6 3:35 下午
     * @Param  MemberExtendQuery
     * @Return ResultVo<CompanyRegProgressVO>
     * @Exception
    */
    @ApiOperation("推广中心-企业注册进度跟进")
    @PostMapping("companyRegProgress")
    public ResultVo<CompanyRegProgressVO> companyRegProgress(){
        MemberExtendQuery query = new MemberExtendQuery();
        query.setUserId(getCurrUserId());
        query.setOemCode(getRequestHeadParams("oemCode"));
        CompanyRegProgressVO regData = this.accountService.queryCompanyRegProgress(query);
        return ResultVo.Success(regData);
    }

    /**
     * @Description 推广中心-企业开票进度跟进
     * @Author  Kaven
     * @Date   2020/6/6 4:31 下午
     * @Param   MemberExtendQuery
     * @Return  ResultVo<CompanyRegProgressVO>
     * @Exception
    */
    @ApiOperation("推广中心-企业开票进度跟进")
    @PostMapping("companyInvoiceProgress")
    public ResultVo<CompanyInvoiceProgressVO> companyInvoiceProgress(){
        MemberExtendQuery query = new MemberExtendQuery();
        query.setUserId(getCurrUserId());
        query.setOemCode(getRequestHeadParams("oemCode"));
        CompanyInvoiceProgressVO regData = this.accountService.queryCompanyInvoiceProgress(query);
        return ResultVo.Success(regData);
    }

    /**
     * @Description 查询员工数量（邀请上限和当前员工总数）
     * @Author  Kaven
     * @Date   2020/4/20 11:26
     * @Return  ResultVo<MemberCountVO>
     * @Exception  BusinessException
    */
    @ApiOperation("查询员工数量（邀请上限和当前员工总数）")
    @PostMapping("getStaffCount")
    public ResultVo<MemberCountVO> getStaffCount(){
        MemberCountVO countVO = this.accountService.getStaffCount(getCurrUserId(), getRequestHeadParams("oemCode"));
        return ResultVo.Success(countVO);
    }

    /**
     * @Description 查询已邀请注册但未开户的用户列表
     * @Author  Kaven
     * @Date   2020/3/26 15:20
     * @Param MemberExtendQuery
     * @Return ResultVo<InvitedRegUserVO>
     * @Exception BusinessException
    */
    @ApiOperation("查询已邀请注册但未开户的用户列表")
    @PostMapping("queryInvitedRegUser")
    public ResultVo<InvitedRegUserVO> queryInvitedRegUser(@RequestBody MemberExtendQuery query){
        query.setUserId(getCurrUserId());
        query.setOemCode(getRequestHeadParams("oemCode"));
        InvitedRegUserVO regUserVO = this.accountService.queryInvitedRegUser(query);
        return ResultVo.Success(regUserVO);
    }

    /**
     * @Description 员工管理<团队概况(含本人推广业绩)>
     * @Author yejian
     * @Date 2020/06/03 09:03
     * @return ResultVo<EmployeeManageOfTeamVO>
     */
    @ApiOperation("员工管理<团队概况(含本人推广业绩)>")
    @PostMapping("/empManageOfTeam")
    public ResultVo<EmployeeManageOfTeamVO> getEmployeeManageOfTeam() {
        EmployeeManageOfTeamVO empTeam = accountService.getEmployeeManageOfTeam(getCurrUserId(), getRequestHeadParams("oemCode"));
        return ResultVo.Success(empTeam);
    }

    /**
     * @Description 员工管理<业绩概况(含本人推广业绩)>
     * @Author yejian
     * @Date 2020/06/05 09:03
     * @return ResultVo<EmployeeManageOfResultVO>
     */
    @ApiOperation("员工管理<业绩概况(含本人推广业绩)>")
    @PostMapping("/empManageOfSurvey")
    public ResultVo<EmployeeManageOfSurveyVO> getEmployeeManageOfSurvey(@RequestBody EmployeeManageOfSurveyDTO entity) {
        entity.setMemberId(getCurrUserId());
        entity.setOemCode(getRequestHeadParams("oemCode"));
        EmployeeManageOfSurveyVO empResult = accountService.getEmployeeManageOfSurvey(entity);
        return ResultVo.Success(empResult);
    }

    /**
     * @Description 员工管理<员工列表>
     * @Author yejian
     * @Date 2020/06/05 15:03
     * @return ResultVo<PageInfo>
     */
    @ApiOperation("员工管理<员工列表>")
    @PostMapping("/empManageOfList")
    public ResultVo<PageInfo> getEmployeeManageOfList(@RequestBody EmployeeManageOfListQuery query) {
        query.setMemberId(getCurrUserId());
        query.setOemCode(getRequestHeadParams("oemCode"));
        List<EmployeeListVO> empList = accountService.getEmployeeManageOfList(query);
        PageInfo pageInfo = PageResultVo.listToPage(query.getPageNumber(), query.getPageSize(), empList);
        return ResultVo.Success(pageInfo);
    }

    /**
     * @Description 员工管理<员工列表(员工业绩)>
     * @Author yejian
     * @Date 2020/06/06 13:03
     * @return ResultVo<EmployeeManageOfListDetailVO>
     */
    @ApiOperation("员工管理<员工列表(员工业绩明细)>")
    @PostMapping("/empManageOfListDetail")
    public ResultVo<EmployeeManageOfListDetailVO> getEmployeeManageOfListDetail(@RequestBody EmployeeManageOfListDetailDTO entity) {
        EmployeeManageOfListDetailVO empListDetail = accountService.getEmployeeManageOfListDetail(entity);
        return ResultVo.Success(empListDetail);
    }

    /**
     * @Description 员工管理<下拉选择直推用户列表>
     * @Author yejian
     * @Date 2020/06/06 13:03
     * @return ResultVo<selectEmpPushListVO>
     */
    @ApiOperation("员工管理<下拉选择直推用户列表>")
    @PostMapping("/selectEmpPushList")
    public ResultVo<SelectEmpPushListVO> selectEmpPushList(@JsonParam Long empId, @JsonParam String keyword) {
        SelectEmpPushListVO selectEmpPushList = accountService.selectEmpPushList(getCurrUserId(), empId, keyword);
        return ResultVo.Success(selectEmpPushList);
    }

    /**
     * @Description 推广中心-直推用户筛选（支持模糊查询）
     * @Author  Kaven
     * @Date   2020/8/14 15:04
     * @Param ExtendUserQuery
     * @Return ResultVo<PageInfo>
     * @Exception
    */
    @ApiOperation("推广中心-直推用户筛选")
    @PostMapping("/listDirectPushUser")
    public ResultVo<PageInfo> listDirectPushUser(@RequestBody ExtendUserQuery query) {
        SelectEmpPushListVO selectEmpPushList = accountService.selectEmpPushList(getCurrUserId(), query.getUserId(), query.getKeyword());
        List<EmployeePushListVO> pushList = new ArrayList<EmployeePushListVO>();

        // 查询会员账号
        MemberAccountEntity member = accountService.findById(selectEmpPushList.getId());

        // 查询会员等级
        MemberLevelEntity level = new MemberLevelEntity();
        level.setId(member.getMemberLevel());
        level.setOemCode(member.getOemCode());
        level = memberLevelService.selectOne(level);

        // 如果是会员是城市服务商就添加自己
        if (level.getLevelNo() == 3 || level.getLevelNo() == 5) {
            if (StringUtils.isNotBlank(query.getKeyword())) {
                if (Objects.equals(selectEmpPushList.getId().toString(), query.getKeyword())
                        || selectEmpPushList.getMemberPhone().contains(query.getKeyword())
                        || selectEmpPushList.getMemberName().contains(query.getKeyword())) {
                    EmployeePushListVO employeePushListVO = new EmployeePushListVO();
                    employeePushListVO.setId(selectEmpPushList.getId());
                    employeePushListVO.setMemberName(selectEmpPushList.getMemberName());
                    employeePushListVO.setMemberPhone(selectEmpPushList.getMemberPhone());
                    pushList.add(employeePushListVO);
                }
            } else if (Objects.equals(getCurrUserId(), selectEmpPushList.getId())) {
                EmployeePushListVO employeePushListVO = new EmployeePushListVO();
                employeePushListVO.setId(selectEmpPushList.getId());
                employeePushListVO.setMemberName(selectEmpPushList.getMemberName());
                employeePushListVO.setMemberPhone(selectEmpPushList.getMemberPhone());
                pushList.add(employeePushListVO);
            }
        }

        // 添加直推会员列表
        pushList.addAll(selectEmpPushList.getPushList());

        PageInfo pageInfo = PageResultVo.listToPage(query.getPageNumber(), query.getPageSize(), pushList);
        return ResultVo.Success(pageInfo);
    }

    /**
     * @Description 员工管理<员工直推用户列表>
     * @Author yejian
     * @Date 2020/06/09 13:03
     * @return ResultVo<PageInfo>
     */
    @ApiOperation("员工管理<员工直推用户列表>")
    @PostMapping("/empManageOfPushList")
    public ResultVo<PageInfo> getEmployeeManageOfPushList(@RequestBody EmployeeManageOfPushListQuery query) {
        query.setMemberId(getCurrUserId());
        query.setOemCode(getRequestHeadParams("oemCode"));
        List<EmpManageOfPushListVO> empPushList = accountService.getEmployeeManageOfPushList(query);
        PageInfo pageInfo = PageResultVo.listToPage(query.getPageNumber(), query.getPageSize(), empPushList);
        return ResultVo.Success(pageInfo);
    }

    /**
     * @Description 通过手机号获取会员昵称
     * @Author yejian
     * @Date 2020/1/13 15:03
     * @param phone
     * @return ResultVo
     */
    @ApiOperation("通过手机号获取会员昵称")
    @PostMapping("/getMemberName")
    public ResultVo getMemberName(@JsonParam String phone) {
        if(StringUtils.isBlank(phone)){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        MemberAccountEntity account = new MemberAccountEntity();
        account.setOemCode(getRequestHeadParams("oemCode"));
        account.setStatus(1);
        account.setMemberPhone(phone);
        account = accountService.selectOne(account);
        return ResultVo.Success(account.getMemberName());
    }

    @ApiOperation("获取小程序二维码")
    @PostMapping("/getQRCode")
    public ResultVo<String> getQRCode(@RequestBody @Valid GenqrcodeDTO entity, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        String base64QRCode = weChatService.getQRCode(
                getRequestHeadParams("oemCode"),
                entity.getScene(),
                entity.getWidth(),
                entity.getPage(),
                1,
                org.apache.commons.lang.StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType"));
        return ResultVo.Success(base64QRCode);
    }

    @ApiOperation("获取海报列表")
    @PostMapping("/getPoster")
    public ResultVo<List<PosterBase64VO>> getPoster(@RequestBody @Valid GenqrcodeDTO entity, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        List<PosterBase64VO> posterList = accountService.getPoster(
                getRequestHeadParams("oemCode"),
                entity,
                org.apache.commons.lang.StringUtils.isBlank(getRequestHeadParams("sourceType")) ? "1" : getRequestHeadParams("sourceType"));
        return ResultVo.Success(posterList);
    }

    @ApiOperation("获取用户上次适用邮箱")
    @PostMapping("/getMemberAccountEmail")
    public ResultVo getMemberAccountEmail() {
        MemberAccountEntity memberAccountEntity=memberAccountService.findById(getCurrUserId());
        return ResultVo.Success(memberAccountEntity.getEmail());
    }

    @ApiOperation("佣金提现分润记录")
    @PostMapping("profitsDetailForWithdraw")
    public ResultVo profitsDetailForWithdraw(@JsonParam Integer pageSize, @JsonParam Integer pageNumber) {
        PageResultVo vo = profitsDetailService.profitsDetailForWithdraw(getCurrUserId(), getRequestHeadParams("oemCode"), pageSize, pageNumber);
        return ResultVo.Success(vo);
    }

    @ApiOperation("消费钱包可提现金额")
    @PostMapping("/usableWithdrawAmount")
    public ResultVo usableWithdrawAmount() {
        Long usableWithdrawAmount = orderService.usableWithdrawAmount(getCurrUserId(), getRequestHeadParams("oemCode"));
        Map<String, Long> map = Maps.newHashMap();
        map.put("usableWithdrawAmount", usableWithdrawAmount);
        return ResultVo.Success(map);
    }
}