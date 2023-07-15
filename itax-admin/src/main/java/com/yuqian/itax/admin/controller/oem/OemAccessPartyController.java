package com.yuqian.itax.admin.controller.oem;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.po.OemAccessPartyPO;
import com.yuqian.itax.agent.entity.query.OemAccessPartyQuery;
import com.yuqian.itax.agent.entity.vo.OemAccessPartyVO;
import com.yuqian.itax.agent.service.OemAccessPartyService;
import com.yuqian.itax.agent.service.OemConfigService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.user.entity.CrowdLabelEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.CrowdLabelService;
import com.yuqian.itax.util.util.MemberPsdUtil;
import com.yuqian.itax.util.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/oemAccessParty")
public class OemAccessPartyController extends BaseController {

    @Autowired
    OemAccessPartyService oemAccessPartyService;

    @Autowired
    private OemConfigService oemConfigService;

    @Autowired
    private CrowdLabelService crowdLabelService;

    /**
     * 分页查询
     * @param query
     * @return
     */
    @PostMapping("page")
    public ResultVo listOemAccessParty(@RequestBody OemAccessPartyQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        PageInfo<OemAccessPartyVO> pageInfo = oemAccessPartyService.queryOemAccessPartyPageInfo(query);
        return ResultVo.Success(pageInfo);
    }

    /**
     * 修改状态
     * @param id
     * @param status 1-上架 2-下架
     * @return
     */
    @PostMapping("updateStatus")
    public ResultVo updateStatus(@JsonParam Long id,@JsonParam Integer status){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        OemAccessPartyEntity entity = oemAccessPartyService.findById(id);
        if (entity == null){
            return ResultVo.Fail("接入方信息错误");
        }
        if (status == 2){
            List<CrowdLabelEntity> list =  crowdLabelService.queryByAccessPartyId(id);
            if (list != null && list.size() > 0){
                return ResultVo.Fail("该接入方配置了人群标签，请先下架人群标签！");
            }
        }
        entity.setStatus(status);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(userEntity.getUsername());
        oemAccessPartyService.editByIdSelective(entity);
        return ResultVo.Success();
    }

    /**
     * 新增H5接入方
     * @param po
     * @param result
     * @return
     */
    @PostMapping("addOemAccessParty")
    public ResultVo addOemAccessParty(@RequestBody @Validated OemAccessPartyPO po, BindingResult result){
        if(result.hasErrors()){
            return  ResultVo.Fail(result);
        }
        //带登陆验证
        CurrUser currUser = getCurrUser();
        //  查询该oem机构是否接入国金
        OemConfigEntity oemConfigEntity = oemConfigService.queryOemConfigByOemCodeAndParamsCode(po.getOemCode(),"is_open_channel","1");
        if (oemConfigEntity != null){
            return ResultVo.Fail("该oem机构已接入国金，无法新增！");
        }
        OemAccessPartyEntity entity = oemAccessPartyService.queryByAccessPartyCode(po.getAccessPartyCode().trim());
        if (entity != null){
            return ResultVo.Fail("编码重复，请重新输入！");
        }
        entity = oemAccessPartyService.queryByOemCodeAndAccessPartyName(po.getOemCode(),po.getAccessPartyName().trim(),null);
        if (entity != null){
            return ResultVo.Fail("接入方名称重复！");
        }
        oemAccessPartyService.addOemAccessParty(po,currUser.getUseraccount());
        return ResultVo.Success();
    }

    /**
     * 详情
     * @param id
     * @return
     */
    @PostMapping("detail")
    public ResultVo detail(@JsonParam Long id){
        //带登陆验证
        getCurrUser();
        if (id == null){
            return ResultVo.Fail("id不能为空");
        }
        OemAccessPartyVO oemAccessPartyVO = oemAccessPartyService.queryById(id);
        return ResultVo.Success(oemAccessPartyVO);
    }

    /**
     * 编辑H5
     * @param po
     * @param result
     * @return
     */
    @PostMapping("updateOemAccessParty")
    public ResultVo updateOemAccessParty(@RequestBody @Validated OemAccessPartyPO po, BindingResult result){
        if(result.hasErrors()){
            return  ResultVo.Fail(result);
        }
        if (po.getId() == null){
            return ResultVo.Fail("id不能为空！");
        }
        //带登陆验证
        getCurrUser();
        UserEntity userEntity = userService.findById(getCurrUser().getUserId());
        OemAccessPartyEntity entity = oemAccessPartyService.queryByOemCodeAndAccessPartyName(po.getOemCode(),po.getAccessPartyName().trim(),po.getId());
        if (entity != null){
            return ResultVo.Fail("接入方名称重复！");
        }
        oemAccessPartyService.updateOemAccessParty(po,userEntity.getUsername());
        return ResultVo.Success();
    }

    /**
     * 查看密钥
     * @param passWord
     * @param id
     * @return
     */
    @PostMapping("showSecret")
    public ResultVo showSecret(@JsonParam String passWord, @JsonParam Long id ){
        //带登陆验证
        getCurrUser();
        UserEntity userEntity = userService.findById(getCurrUser().getUserId());
        String pwd= MemberPsdUtil.encrypt(passWord, userEntity.getUsername(),userEntity.getSlat());
        if (StringUtil.isBlank(passWord)){
            return ResultVo.Fail("请输入密码");
        }
        if (!userEntity.getPassword().equals(pwd)){
            return ResultVo.Fail("密码不正确");
        }
        Map<String,String> map = new HashMap<>();
        OemAccessPartyEntity entity = oemAccessPartyService.findById(id);
        map.put("secret",entity.getAccessPartySecret());
        return ResultVo.Success(map);
    }
}
