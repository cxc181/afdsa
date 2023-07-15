package com.yuqian.itax.admin.controller.company;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.CompanyResourcesUseRecordEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.dto.CompanyResourcesUseRecordAdminDTO;
import com.yuqian.itax.user.entity.query.CompanyResourcesUseRecordAdminQuery;
import com.yuqian.itax.user.entity.vo.CompanyResourcesUseRecordAdminVO;
import com.yuqian.itax.user.enums.MemberCompanyStatusEnum;
import com.yuqian.itax.user.service.CompanyResourcesUseRecordService;
import com.yuqian.itax.user.service.MemberCompanyChangeService;
import com.yuqian.itax.user.service.MemberCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 企业资源Controll
 * auth: HZ
 * time: 2019/12/09
 */
@RestController
@RequestMapping("/company")
public class CompanyResourcesUseRecordController  extends BaseController{

    @Autowired
    CompanyResourcesUseRecordService companyResourcesUseRecordService;
    @Autowired
    MemberCompanyService memberCompanyService;
    @Autowired
    OssService ossService;
    @Autowired
    private MessageNoticeService messageNoticeService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private MemberCompanyChangeService memberCompanyChangeService;
    /**
     * 用章申请列表查询
     * auth: HZ
     * time: 2019/12/09
     */
    @PostMapping("/companyResourcesUseRecordPageInfo")
    public ResultVo companyResourcesUseRecordPageInfo(@RequestBody CompanyResourcesUseRecordAdminQuery companyResourcesUseRecordAdminQuery){
        //验证登陆
        getCurrUser();
        companyResourcesUseRecordAdminQuery.setOemCode(getRequestHeadParams("oemCode"));
        UserEntity userEntity=userService.findById(getCurrUserId());
        if(null == userEntity){
            return ResultVo.Fail(getCurrUseraccount()+ ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if(userEntity.getParkId()!=null){
            companyResourcesUseRecordAdminQuery.setParkId(userEntity.getParkId());
        }
        //分页查询
        PageInfo<CompanyResourcesUseRecordAdminVO> mList=companyResourcesUseRecordService.companyResourcesUseRecordPage(companyResourcesUseRecordAdminQuery);

        return ResultVo.Success(mList);
    }

    /**
     * 查看附件
     * auth: HZ
     * time: 2019/12/09
     */
    @PostMapping("/companyResourcesUseRecordDetail")
    public ResultVo companyResourcesUseRecordDetail(@JsonParam Long id){
        //验证登陆
        getCurrUser();
        //分页查询
        CompanyResourcesUseRecordEntity  companyResourcesUseRecordEntity=companyResourcesUseRecordService.findById(id);
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(companyResourcesUseRecordEntity.getCompanyId());
        Map<String,Object> map=new HashMap();
        map.put("companyName",memberCompanyEntity.getCompanyName());
        String[] addr=companyResourcesUseRecordEntity.getImgsAddr().split(",");
        List<String> list =new ArrayList<>();
        for(int i=0;i<addr.length;i++){
            list.add(ossService.getPrivateImgUrl(addr[i]));
        }
        map.put("imgsAddr",list);
        return ResultVo.Success(map);
    }

    /**
     * 用章申请撤回
     * auth: HZ
     * time: 2019/12/09
     */

    @PostMapping("/recall")
    public ResultVo recall(@JsonParam Long id){
        //验证登陆
        getCurrUser();
        CompanyResourcesUseRecordEntity companyResourcesUseRecordEntity=companyResourcesUseRecordService.findById(id);
        companyResourcesUseRecordEntity.setAuditStatus(3);
        companyResourcesUseRecordService.editByIdSelective(companyResourcesUseRecordEntity);
        return ResultVo.Success();
    }

    /**
     * 用章申请
     * @param dto
     * @return
     */
    @PostMapping("apply")
    public ResultVo apply(@RequestBody @Validated CompanyResourcesUseRecordAdminDTO dto, BindingResult result) {
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        //验证登陆
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        MemberCompanyEntity companyEntity = memberCompanyService.findById(dto.getCompanyId());
        if (companyEntity == null) {
            return ResultVo.Fail("企业不存在");
        }
        if (Objects.equals(companyEntity.getStatus(), MemberCompanyStatusEnum.COMPANY_CANCELLED.getValue())) {
            return ResultVo.Fail("企业已注销，不能进行用章申请");
        }
        CompanyResourcesUseRecordEntity entity = dto.dtoToEntity(userEntity,companyEntity);

        companyResourcesUseRecordService.insertSelective(entity);
        DictionaryEntity dic = dictionaryService.getByCode("company_resources_notice_tmpl");
        if (dic == null) {
            return ResultVo.Success();
        }
        MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
        messageNoticeEntity.setOemCode(companyEntity.getOemCode());
        messageNoticeEntity.setOpenMode(3);
        messageNoticeEntity.setBusinessType(4);
        messageNoticeEntity.setNoticeType(2);
        messageNoticeEntity.setNoticePosition("1,2");
        messageNoticeEntity.setNoticeTitle("用章审核通知");
        messageNoticeEntity.setUserId(companyEntity.getMemberId());
        messageNoticeEntity.setUserType(1);
        messageNoticeEntity.setSourceId(entity.getId());
        messageNoticeEntity.setOrderNo(null);
        messageNoticeEntity.setNoticeContent(dic.getDictValue());
        messageNoticeEntity.setStatus(0);
        messageNoticeEntity.setAddUser(userEntity.getUsername());
        messageNoticeEntity.setAddTime(new Date());
        messageNoticeService.saveMessageNotice(messageNoticeEntity);

        //添加企业变更记录
        memberCompanyChangeService.insertChangeInfo(companyEntity,userEntity.getUsername(),"用章申请");
        return ResultVo.Success();
    }
}
