package com.yuqian.itax.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.order.entity.dto.FileUploadDTO;
import com.yuqian.itax.order.entity.dto.OuterRegOrderDTO;
import com.yuqian.itax.order.entity.dto.ResubmitRegOrderDTO;
import com.yuqian.itax.order.entity.dto.SupplyMeterialDTO;
import com.yuqian.itax.order.entity.vo.RegOrderReturnVO;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.service.MemberAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 企业注册订单相关控制器
 * @Author  Kaven
 * @Date   2020/7/15 14:19
*/
@Api(tags = "企业注册订单服务API")
@Slf4j
@RestController
@RequestMapping("order/registerorder")
public class RegisterOrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RegisterOrderService registerOrderService;
    @Autowired
    private OemParamsService oemParamsService;
    @Autowired
    private MemberAccountService memberAccountService;

    /**
     * @Description 工商注册（含用户注册）
     * @Author  Kaven
     * @Date   2020/7/15 14:47
     * @Param OuterRegOrderDTO
     * @Return ResultVo<RegOrderReturnVO>
     * @Exception
    */
    @ApiOperation("创建工商注册订单")
    @PostMapping("createRegOrder")
    public ResultVo<RegOrderReturnVO> createRegOrder(@RequestBody @Valid OuterRegOrderDTO entity, BindingResult result) throws IOException {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        if(null == entity){
            return ResultVo.Fail("操作失败，订单对象不能为空！");
        }
        Map<String,Object> resultMap = new HashMap<String,Object>();
        entity.setOemCode(this.getRequestHeadParams("oemCode"));
        RegOrderReturnVO returnVO = this.registerOrderService.createRegOrderForOuter(entity);
        return ResultVo.Success(returnVO);
    }

    /**
     * @Description 文件上传至OSS服务器
     * @Author  Kaven
     * @Date   2020/7/17 11:13
     * @Param FileUploadDTO
     * @Return ResultVo<Map<String,Object>>
     * @Exception
    */
    @ApiOperation("文件上传至OSS服务器")
    @PostMapping("fileUpload")
    public ResultVo<Map<String,Object>> fileUpload(@RequestBody @Validated FileUploadDTO uploadDto, BindingResult result) throws IOException {
        if(null == uploadDto){
            return ResultVo.Fail("参数不能为空！");
        }

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        Map<String,Object> resultMap = resultMap = this.registerOrderService.fileUpload(uploadDto,getRequestHeadParams("oemCode"));
        return ResultVo.Success(resultMap);
    }

    /**
     * @Description 工商注册订单：资料补充
     * @Author  Kaven
     * @Date   2020/7/15 14:47
     * @Param RegOrderFileDTO
     * @Return ResultVo
     * @Exception
    */
    @ApiOperation("工商注册订单：资料补充")
    @PostMapping("updateRegOrderFile")
    public ResultVo updateRegOrderFile(@RequestBody @Validated SupplyMeterialDTO smDto, BindingResult result){
        if(null == smDto){
            return ResultVo.Fail("参数不能为空！");
        }

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        smDto.setOemCode(this.getRequestHeadParams("oemCode"));
        this.registerOrderService.supplyMaterials(smDto);
        return ResultVo.Success();
    }

    /**
     * @Description 核名驳回重新提交企业注册订单
     * @Author  Kaven
     * @Date   2020/7/15 14:48
     * @Param ResubmitRegOrderDTO
     * @Return ResultVo
     * @Exception
    */
    @ApiOperation("核名驳回重新提交企业注册订单")
    @PostMapping("resubmitRegOrder")
    public ResultVo resubmitRegOrder(@RequestBody @Valid ResubmitRegOrderDTO entity, BindingResult result){
        log.info("收到重新提交企业注册订单请求：{}",JSON.toJSONString(entity));

        if(null == entity){
            return ResultVo.Fail("操作失败，对象不能为空！");
        }
        if(StringUtils.isBlank(entity.getRegPhone())){
            return ResultVo.Fail("操作失败，经营者手机号不能为空");
        }
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        entity.setOemCode(this.getRequestHeadParams("oemCode"));
        // 校验用户是否存在
        MemberAccountEntity member = this.memberAccountService.queryByAccount(entity.getRegPhone(),entity.getOemCode());
        if(null == member){
            return ResultVo.Fail("操作失败，未找到经营者用户信息");
        }
        this.registerOrderService.resubmitRegOrder(member.getId(),entity);
        // 封装返回
        Map<String,Object> dataMap = Maps.newHashMap();
        dataMap.put("orderStatus",entity.getOrderStatus());
        return ResultVo.Success(dataMap);
    }

}
