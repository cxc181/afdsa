package com.yuqian.itax.admin.controller.corporate;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.corporateaccount.query.CorporateAccountApplyOrderQuery;
import com.yuqian.itax.corporateaccount.service.CorporateAccountApplyOrderService;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountApplyOrdeVO;
import com.yuqian.itax.park.entity.ParkCorporateAccountConfigEntity;
import com.yuqian.itax.park.service.ParkCorporateAccountConfigService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.enums.UserTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * 对公户申请订单
 * @author：HZ
 * @Date：2020/9/710:12
 * @version：1.0
 */
@RestController
@RequestMapping("corporate/account/apply/order")
@Slf4j
public class CorporateAccountApplyOrderController  extends BaseController {

    @Autowired
    private CorporateAccountApplyOrderService corporateAccountApplyOrderService;
    @Autowired
    private ParkCorporateAccountConfigService parkCorporateAccountConfigService;
    /**
     * 分页查询
     * @param query
     * @return
     */
    @PostMapping("page")
    public ResultVo queryCorporateAccountCollectionRecordPageInfo(@RequestBody CorporateAccountApplyOrderQuery query){
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
        query.setPlatformType(userEntity.getPlatformType());
        PageInfo<CorporateAccountApplyOrdeVO> page = corporateAccountApplyOrderService.queryCorporateAccountApplyOrderPageInfo(query);
        return ResultVo.Success(page);
    }

    /**
     * 导出
     * @param query
     * @return
     */
    @PostMapping("export")
    public ResultVo export(@RequestBody CorporateAccountApplyOrderQuery query) {
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        query.setOemCode(userEntity.getOemCode());
        // 当前登录用户身份是否为园区
        if (Objects.equals(userEntity.getPlatformType(),3)) {
            query.setParkId(userEntity.getParkId());
        }
        query.setPlatformType(userEntity.getPlatformType());
        List<CorporateAccountApplyOrdeVO> lists = corporateAccountApplyOrderService.queryCorporateAccountApplyOrderList(query);
        if (CollectionUtil.isEmpty(lists)) {
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("公户申请单", "公户申请单", CorporateAccountApplyOrdeVO.class, lists);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("公户申请单导出异常：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件失败");
        }
    }
    /**
     * 订单取消
     */
    @PostMapping("cannel")
    public ResultVo cannel(@JsonParam Long id) {
        try{
            corporateAccountApplyOrderService.cannel(id,getCurrUseraccount());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }
    /**
     * 公户开户行列表
     */

    @PostMapping("bankList")
    public ResultVo bankList(@JsonParam Long parkId, @JsonParam String headquartersNo) {
        ParkCorporateAccountConfigEntity entity=new ParkCorporateAccountConfigEntity();
        entity.setParkId(parkId);
        entity.setStatus(1);
        entity.setHeadquartersNo(headquartersNo);
        List<ParkCorporateAccountConfigEntity> list=parkCorporateAccountConfigService.select(entity);
        return ResultVo.Success(list);
    }
}
