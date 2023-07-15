package com.yuqian.itax.api.controller.order;

import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.tax.entity.CostItemBaseEntity;
import com.yuqian.itax.tax.entity.MemberCostItemEntity;
import com.yuqian.itax.tax.entity.dto.CostItemDTO;
import com.yuqian.itax.tax.entity.vo.CommonCostItemVO;
import com.yuqian.itax.tax.service.CostItemBaseService;
import com.yuqian.itax.tax.service.MemberCostItemService;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;


/**
 * 成本项Controller
 * @author：lmh
 * @Date：2022/3/10
 * @version：1.0
 */
@RestController
@RequestMapping("/costItem")
@Slf4j
public class CostItemController extends BaseController {

    @Autowired
    private MemberCostItemService memberCostItemService;
    @Autowired
    private CostItemBaseService costItemBaseService;

    /**
     * 用户常用成本项
     * @return
     */
    @PostMapping("/common")
    public ResultVo common() {
        List<CommonCostItemVO> vo = memberCostItemService.findCommonCostItem(getCurrUserId());
        return ResultVo.Success(vo);
    }

    /**
     * 更新成本项
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("/update")
    public ResultVo update(@RequestBody @Validated CostItemDTO dto, BindingResult result) {
        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        // 查询用户成本项
        MemberCostItemEntity entity = new MemberCostItemEntity();
        entity.setCostItemName(dto.getCostItemName());
        entity.setMemberId(getCurrUserId());
        List<MemberCostItemEntity> entities = memberCostItemService.select(entity);

        if (dto.getOperateType() == 1) { //添加成本项
            if (StringUtil.isBlank(dto.getCostItemName())) {
                return ResultVo.Fail("成本项名称不能为空");
            }
            // 校验成本项是否已存在
            CostItemBaseEntity baseEntity = new CostItemBaseEntity();
            baseEntity.setCostItemName(dto.getCostItemName());
            List<CostItemBaseEntity> baseEntities = costItemBaseService.select(baseEntity);
            if (CollectionUtil.isNotEmpty(entities) || CollectionUtil.isNotEmpty(baseEntities)) {
                return ResultVo.Fail("添加失败，成本项已存在");
            }
            // 添加成本项
            entity.setMemberId(getCurrUserId());
            entity.setAddTime(new Date());
            entity.setAddUser(getCurrUseraccount());
            memberCostItemService.insertSelective(entity);
        } else if (dto.getOperateType() == 2){ //删除成本项
            if (null == dto.getId()) {
                return ResultVo.Fail("成本项id不能为空");
            }
            if (CollectionUtil.isEmpty(entities)) {
                return ResultVo.Fail("成本项不存在");
            }
            memberCostItemService.delById(dto.getId());
        } else {
            return ResultVo.Fail("无效的操作类型");
        }
        return ResultVo.Success();
    }
}