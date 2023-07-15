package com.yuqian.itax.admin.controller.system;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.entity.po.OemParamsPO;
import com.yuqian.itax.agent.entity.query.OemParamsQuery;
import com.yuqian.itax.agent.entity.vo.OemParamsVO;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.dto.DictionaryPO;
import com.yuqian.itax.system.entity.query.DictionaryQuery;
import com.yuqian.itax.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/system/oem/params")
public class SyssOemParamsController extends BaseController{
    @Autowired
    OemParamsService oemParamsService;

    /**
     * 查询机构参数
     */
    @PostMapping("/querySysOemParams")
    public ResultVo querySysOemParams(@RequestBody OemParamsQuery query){
        getCurrUser();
        PageInfo<OemParamsVO> list = oemParamsService.querySysOemParamsPageInfo(query);
        return ResultVo.Success(list);
    }

    /**
     * 查询机构参数详情
     */
    @PostMapping("/querySysOemParamsDetail")
    public ResultVo querySysOemParamsDetail(@RequestBody OemParamsQuery query){
        getCurrUser();
        PageInfo<OemParamsVO> list = oemParamsService.querySysOemParamsPageInfo(query);
        return ResultVo.Success(list.getList().get(0));
    }

    /**
     * 新增机构参数
     */
    @PostMapping("/addSysOemParams")
    public ResultVo addSysOemParams(@RequestBody @Valid OemParamsPO po, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        try{
            oemParamsService.addSysOemParams(po,getCurrUseraccount());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 编辑字典表
     */
    @PostMapping("/updateSysOemParams")
    public ResultVo updateSysOemParams(@RequestBody @Valid OemParamsPO po, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        if(null == po.getId()){
            return ResultVo.Fail("id不能为空");
        }
        if(null == po.getStatus()){
            return ResultVo.Fail("状态不能为空");
        }
        try{
            oemParamsService.updateSysDictionaryList(po,getCurrUseraccount());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }
    /**
     * 删除字典表
     */
    @PostMapping("/deleteSysOemParams")
    public ResultVo deleteSysOemParams(@JsonParam Long id){
        oemParamsService.delById(id);
        return ResultVo.Success();
    }
}
