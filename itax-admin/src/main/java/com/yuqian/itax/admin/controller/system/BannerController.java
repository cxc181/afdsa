package com.yuqian.itax.admin.controller.system;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.system.entity.BannerEntity;
import com.yuqian.itax.system.entity.dto.BannerDTO;
import com.yuqian.itax.system.entity.query.BannerQuery;
import com.yuqian.itax.system.entity.vo.BannerDetailVO;
import com.yuqian.itax.system.entity.vo.BannerListVO;
import com.yuqian.itax.system.service.BannerService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 广告位控制器
 * @author：pengwei
 * @Date：2020/3/5 9:12
 * @version：1.0
 */
@RestController
@RequestMapping("banner")
public class BannerController extends BaseController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private OemService oemService;

    @Autowired
    private OssService ossService;

    @ApiOperation("列表页")
    @PostMapping("page")
    public ResultVo listPageProduct(@RequestBody BannerQuery query){
        CurrUser currUser = getCurrUser();
        if (query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() != 1) {
            query.setOemCode(userEntity.getOemCode());
        }
        PageInfo<BannerListVO> page = bannerService.listPageBanner(query);
        return ResultVo.Success(page);
    }

    @ApiOperation("新增")
    @PostMapping("add")
    public ResultVo add(@RequestBody @Validated(Add.class) BannerDTO dto, BindingResult result) {
        CurrUser currUser = getCurrUser();
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        if (userEntity.getPlatformType() == 1) {
            if (StringUtils.isBlank(dto.getOemCode())) {
                return ResultVo.Fail("机构编码不能为空");
            }
            OemEntity oem = oemService.getOem(dto.getOemCode());
            if (oem == null) {
                return ResultVo.Fail("机构不存在");
            }
        } else {
            dto.setOemCode(userEntity.getOemCode());
        }
        dto.setId(null);
        String s = ValidData(dto);
        if (StringUtils.isNotBlank(s)) {
            return ResultVo.Fail(s);
        }
        bannerService.insertSelective(dto.toEntity(currUser.getUseraccount()));
        return ResultVo.Success();
    }

    private String ValidData(BannerDTO dto) {
        if (bannerService.orderNumIsExist(dto.getOemCode(), dto.getOrderNum(), dto.getId())) {
            return "当前排序已存在，请调整后再试！";
        }
        if (dto.getIsShare() == 1) {
            if (StringUtils.isBlank(dto.getShareTitle())) {
                return "分享标题不能为空";
            }
            if (dto.getShareTitle().length() > 20) {
                return "分享标题不能超过20个字";
            }
//            if (StringUtils.isBlank(dto.getShareImageAddress())) {
//                return "分享图地址不能为空";
//            }
//            if (dto.getShareImageAddress().length() > 128) {
//                return "分享图地址不能超过128个字";
//            }
            if (StringUtils.isNotBlank(dto.getShareImageAddress()) && dto.getShareImageAddress().length() > 128) {
                return "分享图地址不能超过128个字";
            }
        } else {
            dto.setShareTitle(null);
            dto.setShareImageAddress(null);
        }
        return null;
    }

    @ApiOperation("详情")
    @PostMapping("detail")
    public ResultVo detail(@JsonParam Long id) {
        CurrUser currUser = getCurrUser();
        if (id == null) {
            return ResultVo.Fail("主键不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        BannerEntity entity = bannerService.findById(id);
        if (entity == null) {
            return ResultVo.Fail("广告位不存在");
        }
        if (userEntity.getPlatformType() != 1 && !StringUtils.equals(entity.getOemCode(), userEntity.getOemCode())) {
            return ResultVo.Fail("广告位不属于当前OEM机构");
        }
        return ResultVo.Success(new BannerDetailVO(entity, oemService.getOem(entity.getOemCode())));
    }

    @ApiOperation("修改")
    @PostMapping("edit")
    public ResultVo edit(@RequestBody @Validated(Update.class) BannerDTO dto, BindingResult result) {
        CurrUser currUser = getCurrUser();
        if(result.hasErrors()) {
            return ResultVo.Fail(result);
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        BannerEntity entity = bannerService.findById(dto.getId());
        if (entity == null) {
            return ResultVo.Fail("广告位不存在");
        }
        if (userEntity.getPlatformType() != 1 && !StringUtils.equals(entity.getOemCode(), userEntity.getOemCode())) {
            return ResultVo.Fail("广告位不属于当前OEM机构");
        }
        dto.setOemCode(entity.getOemCode());
        dto.setPosition(entity.getPosition());
        String s = ValidData(dto);
        if (StringUtils.isNotBlank(s)) {
            return ResultVo.Fail(s);
        }
        bannerService.edit(dto.toEntity(currUser.getUseraccount(), entity));
        return ResultVo.Success();
    }

    @ApiOperation("删除")
    @PostMapping("delete")
    public ResultVo delete(@JsonParam Long id) {
        CurrUser currUser = getCurrUser();
        if (id == null) {
            return ResultVo.Fail("主键不能为空");
        }
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        BannerEntity entity = bannerService.findById(id);
        if (entity == null) {
            return ResultVo.Fail("广告位不存在");
        }
        if (userEntity.getPlatformType() != 1 && !StringUtils.equals(entity.getOemCode(), userEntity.getOemCode())) {
            return ResultVo.Fail("广告位不属于当前OEM机构");
        }
        bannerService.delById(id);
        //删除oss里的图片
        ossService.deletePublicObject(entity.getBannerAddress());
        return ResultVo.Success();
    }
}
