package com.yuqian.itax.admin.controller.company;

import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.group.entity.InvoiceHeadGroupEntity;
import com.yuqian.itax.group.entity.po.InvoiceHeadGroupPO;
import com.yuqian.itax.group.entity.query.InvoiceHeadGroupQuery;
import com.yuqian.itax.group.service.InvoiceHeadGroupService;
import com.yuqian.itax.system.entity.CreditCodeEntity;
import com.yuqian.itax.system.service.CreditCodeService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.enums.UserAccountTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;


/**
 * 集团发票抬头
 * @author：HZ
 * @Date：2020/03/04 15:12
 * @version：1.0
 */
@RestController
@RequestMapping("group/invoice/head")
@Slf4j
public class InvoiceHeadGroupController extends BaseController {

    @Autowired
    InvoiceHeadGroupService invoiceHeadGroupService;
    @Autowired
    CreditCodeService creditCodeService;
    /**
     * 集团开票抬头列表
     * @return
     */
    @PostMapping("getInvoiceHeadGroup")
    public ResultVo getInvoiceHeadGroup(@RequestBody InvoiceHeadGroupQuery query){
        //验证登陆
        getCurrUser();
        List<InvoiceHeadGroupEntity> list =invoiceHeadGroupService.getInvoiceHeadGroup(query);
        return ResultVo.Success(list);
    }

    /**
     * 新增集团开票抬头
     * @return
     */
    @PostMapping("addInvoiceHeadGroup")
    public ResultVo addInvoiceHeadGroup(@RequestBody @Validated InvoiceHeadGroupPO po, BindingResult result) {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        //验证登陆
        getCurrUser();
        try{
            invoiceHeadGroupService.addInvoiceHeadGroup(po,getCurrUseraccount());
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 编辑集团开票抬头
     * @return
     */
    @PostMapping("updateInvoiceHeadGroup")
    public ResultVo updateInvoiceHeadGroup(@RequestBody @Validated InvoiceHeadGroupPO po, BindingResult result) {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }
        if(null == po.getId()){
            return ResultVo.Fail("id不能为空");
        }
        //验证登陆
        getCurrUser();
        try{
            invoiceHeadGroupService.updateInvoiceHeadGroup(po,getCurrUseraccount());
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 集团开票抬头详情
     * @return
     */
    @PostMapping("getInvoiceHeadGroupDetail")
    public ResultVo getInvoiceHeadGroupDetail(@JsonParam Long id) {
        //验证登陆
        getCurrUser();
        InvoiceHeadGroupEntity entity=invoiceHeadGroupService.findById(id);
        return ResultVo.Success(entity);
    }

    /**
     * @Description 税务登记号查询
     * @Author yejian
     * @Date 2020/02/20 12:40
     * @param keyWord
     * @return ResultVo<CreditCodeEntity>
     */
    @PostMapping("/getCreditCode")
    public ResultVo getCreditCode(@JsonParam String keyWord ,@JsonParam String oemCode) {
        try {
            if(StringUtils.isBlank(keyWord)){
                return ResultVo.Fail("请输入关键字");
            }
            if(StringUtils.isBlank(oemCode)){
                return ResultVo.Fail("请选择机构编码");
            }
            CreditCodeEntity creditCodeEntity = creditCodeService.getCreditCode(oemCode, keyWord);
            InvoiceHeadGroupEntity invoiceHeadGroupEntity=new InvoiceHeadGroupEntity();
            invoiceHeadGroupEntity.setCompanyName(creditCodeEntity.getEname());
            invoiceHeadGroupEntity.setCompanyAddress(creditCodeEntity.getAddress());
            invoiceHeadGroupEntity.setEin(creditCodeEntity.getCreditCode());
            invoiceHeadGroupEntity.setPhone(creditCodeEntity.getTel());
            invoiceHeadGroupEntity.setBankName(creditCodeEntity.getBank());
            invoiceHeadGroupEntity.setBankNumber(creditCodeEntity.getBankAccount());
            return ResultVo.Success(invoiceHeadGroupEntity);
        } catch (BusinessException e){
            log.error("税务登记号查询异常{}",e.getMessage());
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            log.error("税务登记号查询异常：{}",e.getMessage());
            return ResultVo.Fail(e.getMessage());
        }
    }

    /**
     * 删除发票抬头
     * @param id
     * @param oemCode
     * @return
     */
    @PostMapping("delete")
    public ResultVo delete(@JsonParam Long id, @JsonParam String oemCode) {
        if (id == null) {
            return ResultVo.Fail("发票抬头主键不能为空");
        }
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        InvoiceHeadGroupEntity entity = new InvoiceHeadGroupEntity();
        entity.setId(id);
        if (userEntity.getPlatformType() == 1 && Objects.equals(UserAccountTypeEnum.ADMIN.getValue(), userEntity.getAccountType())) {
            if (StringUtils.isBlank(oemCode)) {
                return ResultVo.Fail("请选择要操作的OEM机构");
            }
            entity.setOemCode(oemCode);
        } else {
            entity.setOemCode(userEntity.getOemCode());
        }
        entity = invoiceHeadGroupService.selectOne(entity);
        if (entity == null) {
            return ResultVo.Fail("发票抬头不存在");
        }
        invoiceHeadGroupService.delById(id);
        return ResultVo.Success();
    }
}
