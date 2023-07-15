package com.yuqian.itax.admin.controller.system;

import com.google.common.collect.Maps;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.BankBinEntity;
import com.yuqian.itax.system.service.BankBinService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * 卡bin信息控制器
 * @author：pengwei
 * @Date：2019/12/6 11:12
 * @version：1.0
 */
@RestController
@RequestMapping("bank/bin")
public class BankBinController extends BaseController {

    @Autowired
    private BankBinService bankBinService;

    @PostMapping("query")
    public ResultVo downLoadFile(@JsonParam String bankNumber){
        if (StringUtils.isBlank(bankNumber)) {
            return ResultVo.Fail("请填写银行卡号");
        }
        BankBinEntity entity = bankBinService.findByCardNo(bankNumber);
        if (entity == null || StringUtils.isBlank(entity.getBankName())) {
            return ResultVo.Fail("卡bin信息识别失败");
        }
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("bankCode", entity.getBankCode());
        map.put("bankName", entity.getBankName());
        map.put("cardType", entity.getCardType());
        return ResultVo.Success(map);
    }
}
