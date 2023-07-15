package com.yuqian.itax.api.controller.agent;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.OemParkRelaEntity;
import com.yuqian.itax.agent.service.OemParkRelaService;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.dto.ParkCommentDTO;
import com.yuqian.itax.park.entity.query.ParkCommentsQuery;
import com.yuqian.itax.park.entity.query.ParkListOfMenuQuery;
import com.yuqian.itax.park.entity.query.SearchParkQuery;
import com.yuqian.itax.park.entity.vo.ParkCommentListVO;
import com.yuqian.itax.park.entity.vo.ParkDetailOfMenuVO;
import com.yuqian.itax.park.entity.vo.ParkListOfMenuVO;
import com.yuqian.itax.park.entity.vo.SearchParkListVO;
import com.yuqian.itax.park.enums.ParkStatusEnum;
import com.yuqian.itax.park.service.ParkCommentsService;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.system.entity.query.ParkBusinessScopeQuery;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.system.service.ParkBusinessscopeService;
import com.yuqian.itax.user.service.MemberProfitsRulesService;
import com.yuqian.itax.util.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Api(tags = "园区控制器")
@Slf4j
@RestController
@RequestMapping("/agent/park")
public class ParkController extends BaseController {

    @Autowired
    private ParkService parkService;
    @Autowired
    private MemberProfitsRulesService memberProfitsRulesService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ParkBusinessscopeService parkbusinessscopeService;
    @Autowired
    private ParkCommentsService parkCommentsService;
    @Autowired
    private OssService ossService;
    @Autowired
    private OemParkRelaService oemParkService;

    @ApiOperation("搜索园区列表")
    @PostMapping("/searchPark")
    public ResultVo getParkList(@RequestBody SearchParkQuery query) {
        String oemCode = getRequestHeadParams("oemCode");
        query.setOemCode(oemCode);
        List<SearchParkListVO> list = parkService.searchPark(query);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        return ResultVo.Success(map);
    }

    @ApiOperation("查询园区特殊事项说明")
    @PostMapping("/specialConsiderations")
    public ResultVo specialConsiderations(@JsonParam Long parkId, @JsonParam Long productId) {
        if (null == parkId) {
            return ResultVo.Fail("园区id不能为空");
        }
        if (null == productId) {
            return ResultVo.Fail("产品id不能为空");
        }
        // 查询产品
        ProductEntity product = productService.findById(productId);
        Map<String, Object> map = parkService.getSpecialConsiderations(parkId, getRequestHeadParams("oemCode"), product.getProdType());
        // 根据当前会员等级计算产品优惠价（会员价）
        String oemCode = this.getRequestHeadParams("oemCode");
        Long discount = this.memberProfitsRulesService.queryMemberDiscount(getCurrUserId(),productId,oemCode,parkId);
        map.put("vipAmount", discount);
        return ResultVo.Success(map);
    }

    @ApiOperation("查询园区经营范围")
    @PostMapping("/businessScope")
    public ResultVo getBusinessScope(@RequestBody @Validated ParkBusinessScopeQuery query, BindingResult results) {
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }

        // 校验机构所属园区
        Example example = new Example(OemParkRelaEntity.class);
        example.createCriteria().andEqualTo("oemCode", getRequestHeadParams("oemCode")).andEqualTo("parkId", query);
        Optional.ofNullable(oemParkService.selectByExample(example)).orElseThrow(() -> new BusinessException("所属机构下未找到要查询的园区"));

        // 查询园区
        ParkEntity park = Optional.ofNullable(parkService.findById(query.getParkId())).orElseThrow(() -> new BusinessException("未查询到园区信息"));
        if (!ParkStatusEnum.ON_SHELF.getValue().equals(park.getStatus())) {
            throw new BusinessException("园区不可用");
        }

        PageResultVo<String> page = parkbusinessscopeService.findByParkId(query);
        return ResultVo.Success(page.getList());
    }

    @ApiOperation("获取园区政策标签")
    @PostMapping("/parkPolicyLabel")
    public ResultVo getParkPolicyLabel() {
        List<String> list = parkService.getParkPolicyLabel(getRequestHeadParams("oemCode"));
        return ResultVo.Success(list);
    }

    /**
     * 小程序园区菜单
     * @param query
     * @return
     */
    @ApiOperation("园区列表")
    @PostMapping("/list")
    public ResultVo getParkList(@RequestBody ParkListOfMenuQuery query) {
        String oemCode = getRequestHeadParams("oemCode");
        query.setOemCode(oemCode);
        List<ParkListOfMenuVO> list = parkService.getParkListOfMenu(query);
        if (CollectionUtil.isEmpty(list)) {
            return ResultVo.Success(list);
        }

        // 园区预览图处理
        for (ParkListOfMenuVO vo : list) {
            if (StringUtil.isNotBlank(vo.getParkThumbnail())) {
                vo.setParkThumbnail(vo.getParkThumbnail());
            }
        }
        return ResultVo.Success(list);
    }

    @ApiOperation("获取园区详情")
    @PostMapping("/detail")
    public ResultVo getParkDetail(@JsonParam Long parkId) {
        String oemCode = getRequestHeadParams("oemCode");
        if(StringUtil.isBlank(oemCode)){
            return ResultVo.Success(new ParkDetailOfMenuVO());
        }
        ParkDetailOfMenuVO vo = parkService.getParkDetailOfMenu(parkId,oemCode);
        if (null == vo) {
            return ResultVo.Success(new ParkDetailOfMenuVO());
        }

        // 园区实景图处理
        if (StringUtil.isNotBlank(vo.getParkImgs())) {
            List<String> list = Lists.newArrayList();
            String[] split = vo.getParkImgs().split(",");
            for (String s : split) {
                list.add(s);
            }
            vo.setParkImgList(list);
        }
        return ResultVo.Success(vo);
    }

    @ApiOperation("获取园区评论")
    @PostMapping("/commentList")
    public ResultVo getCommentList(@RequestBody ParkCommentsQuery query) {
        PageInfo<ParkCommentListVO> list = parkCommentsService.getCommentList(query);
        return ResultVo.Success(list);
    }

    @ApiOperation("添加园区评论")
    @PostMapping("/addComment")
    public ResultVo addComment(@RequestBody @Validated ParkCommentDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return ResultVo.Fail(result.getAllErrors().get(0).getDefaultMessage());
        }

        dto.setMemberId(getCurrUserId());
        dto.setOemCode(getRequestHeadParams("oemCode"));
        dto.setMemberAccount(getCurrUser().getUseraccount());
        parkCommentsService.addComment(dto);
        return ResultVo.Success();
    }
}
