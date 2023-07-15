package com.yuqian.itax.admin.controller.corporate;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.corporateaccount.entity.vo.CorporateAccountContOrderVO;
import com.yuqian.itax.corporateaccount.query.CorporateAccountContOrderQuery;
import com.yuqian.itax.corporateaccount.service.CorporateAccountContOrderService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.util.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 对公户续费订单
 * @author：wangKaiL
 * @Date：2021/9/7 10:12
 * @version：1.0
 */
@RestController
@RequestMapping("corporate/account/cont/order")
@Slf4j
public class CorporateAccountContOrderController extends BaseController {

    @Autowired
    private CorporateAccountContOrderService corporateAccountContOrderService;

    /**
     * 分页查询
     * @param query
     * @return
     */
    @PostMapping("page")
    public ResultVo queryCorporateAccountContOrderPageInfo(@RequestBody CorporateAccountContOrderQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        // 园区账号只能查看所属园区的数据
        if (Objects.equals(userEntity.getPlatformType(),3) && null != userEntity.getParkId()) {
            query.setParkId(userEntity.getParkId());
        } else if (Objects.equals(userEntity.getPlatformType(),2) && null != userEntity.getOemCode()) {
            query.setOemCode(userEntity.getOemCode());
        }
        PageInfo<CorporateAccountContOrderVO> page =  corporateAccountContOrderService.getContOrderListPage(query);
        return ResultVo.Success(page);
    }

    @PostMapping("batch/corporateAccountContOrder")
    public ResultVo batchCorporateAccountContOrder(@RequestBody CorporateAccountContOrderQuery query){
        CurrUser currUser = getCurrUser();
        UserEntity userEntity = userService.findById(currUser.getUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        // 园区账号只能查看所属园区的数据
        if (Objects.equals(userEntity.getPlatformType(),3) && null != userEntity.getParkId()) {
            query.setParkId(userEntity.getParkId());
        } else if (Objects.equals(userEntity.getPlatformType(),2) && null != userEntity.getOemCode()) {
            query.setOemCode(userEntity.getOemCode());
        }
        query.setPlatformType(userEntity.getPlatformType());
        List<CorporateAccountContOrderVO> list = corporateAccountContOrderService.listPages(query);
        if(CollectionUtils.isEmpty(list)){
            return ResultVo.Fail("暂无数据导出");
        }
        try {
            exportExcel("公户续费订单"+ DateUtil.format(new Date(),"yyyy-MM-dd"), "公户续费订单"+ DateUtil.format(new Date(),"yyyy-MM-dd"), CorporateAccountContOrderVO.class, list);
            return ResultVo.Success();
        } catch (Exception e) {
            log.error("公户续费订单：" + e.getMessage(), e);
            return ResultVo.Fail("导出文件异常");
        }
    }
}
