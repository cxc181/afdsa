package com.yuqian.itax.api.controller.user;

import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.dto.CompanyCorePersonnelDTO;
import com.yuqian.itax.user.entity.vo.CompanyCorePersonnelVO;
import com.yuqian.itax.user.entity.vo.ShareholderPersonnelListVO;
import com.yuqian.itax.user.service.CompanyCorePersonnelService;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "企业核心成员控制器")
@Slf4j
@RestController
@RequestMapping("/company/corePersonnel")
public class CompanyCorePersonnelController extends BaseController {

    @Autowired
    private CompanyCorePersonnelService companyCorePersonnelService;
    @Autowired
    private OssService ossService;

    @ApiOperation("新增/编辑企业核心成员")
    @PostMapping("/addOrUpdatePersonnel")
    public ResultVo addOrUpdatePersonnel(@RequestBody @Validated CompanyCorePersonnelDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResultVo.Fail(result.getAllErrors().get(0).getDefaultMessage());
        }
        dto.setMemberId(getCurrUserId());
        dto.setAddUser(getCurrUseraccount());
        dto.setOemCode(getRequestHeadParams("oemCode"));
        String registRedisTime = (System.currentTimeMillis() + 10000) + ""; // redis标识值
        try {
            // 防止重复点击
            boolean lockResult = redisService.lock(RedisKey.LOCK_CORE_PERSONNEL_KEY + getCurrUserId()+dto.getOrderNo(), registRedisTime, 60);
            if (!lockResult) {
                throw new BusinessException("请勿重复提交！");
            }
            companyCorePersonnelService.addOrUpdatePersonnel(dto);
        } catch (Exception e) {
            return ResultVo.Fail(e.getMessage());
        } finally {
            redisService.unlock(RedisKey.LOCK_CORE_PERSONNEL_KEY + getCurrUserId()+dto.getOrderNo(), registRedisTime);
        }
        return ResultVo.Success();
    }

    /**
     * 校验核心成员必填信息
     * @param controlType 0-操作合伙人/股东 1-操作成员(有职务)
     * @return
     */
    @ApiOperation("校验核心成员必填信息")
    @PostMapping("/check")
    public ResultVo check(@JsonParam Integer controlType, @JsonParam String orderNo) {
        companyCorePersonnelService.check(controlType, orderNo);
        return ResultVo.Success();
    }

    /**
     * 查询核心成员列表
     * @param companyId
     * @param orderNo
     * @param type 查询类型 0-全部 1-合伙人/股东 2-成员
     * @return
     */
    @ApiOperation("查询核心成员列表")
    @PostMapping("/list")
    public ResultVo list(@JsonParam Long companyId, @JsonParam String orderNo, @JsonParam Integer type) {
        List<CompanyCorePersonnelVO> list = companyCorePersonnelService.list(getCurrUserId(),companyId, orderNo, type);
        return ResultVo.Success(list);
    }

    @ApiOperation("查询核心成员信息详情")
    @PostMapping("/detail")
    public ResultVo detail(@JsonParam Long id) {
        CompanyCorePersonnelVO vo = companyCorePersonnelService.detail(id);
        return ResultVo.Success(vo);
    }

    /**
     * 删除核心成员信息
     * @param id
     * @param controlType 操作类型 0-操作合伙人/股东 1-操作成员
     * @return
     */
    @ApiOperation("删除核心成员信息")
    @PostMapping("/delete")
    public ResultVo delete(@JsonParam Long id, @JsonParam Integer controlType) {
        if (null == id) {
            return ResultVo.Fail("核心成员id不能为空");
        }
        companyCorePersonnelService.deletePersonnel(id, getCurrUserId(), controlType);
        return ResultVo.Success();
    }

    /**
     * 合伙人/股东列表
     * @param isExecutivePartner 是否执行事务合伙人 0-否 1-是
     * @param orderNo
     * @param personnelName 姓名
     * @return
     */
    @ApiOperation("合伙人/股东列表")
    @PostMapping("/shareholderPersonnelList")
    public ResultVo shareholderPersonnelList(@JsonParam Integer isExecutivePartner, @JsonParam String orderNo, @JsonParam String personnelName) {
        List<ShareholderPersonnelListVO> list = companyCorePersonnelService.shareholderPersonnelList(isExecutivePartner, orderNo, personnelName);
        if (CollectionUtil.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                if (StringUtil.isNotBlank(list.get(i).getIdCardFront())) {
                    list.get(i).setIdCardFrontUrl(ossService.getPrivateImgUrl(list.get(i).getIdCardFront()));
                }
                if (StringUtil.isNotBlank(list.get(i).getIdCardReverse())) {
                    list.get(i).setIdCardReverseUrl(ossService.getPrivateImgUrl(list.get(i).getIdCardReverse()));
                }
            }
        }
        return ResultVo.Success(list);
    }
}