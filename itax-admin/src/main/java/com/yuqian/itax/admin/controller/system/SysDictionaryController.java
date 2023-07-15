package com.yuqian.itax.admin.controller.system;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.dto.DictionaryPO;
import com.yuqian.itax.system.entity.query.DictionaryQuery;
import com.yuqian.itax.system.entity.vo.DictionaryVO;
import com.yuqian.itax.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/system/dictionary")
public class SysDictionaryController extends BaseController {

    @Autowired
    DictionaryService dictionaryService;
    /**
     * 查询字典表
     */
    @PostMapping("/querySysDictionary")
    public ResultVo querySysDictionary(@RequestBody DictionaryQuery query){
        getCurrUser();
        PageInfo<DictionaryVO> list = dictionaryService.querySysDictionaryPageInfo(query);
        return ResultVo.Success(list);
    }


    /**
     * 查询字典表详情
     */
    @PostMapping("/querySysDictionaryDetail")
    public ResultVo querySysDictionaryDetail(@RequestBody DictionaryQuery query){
        getCurrUser();
        PageInfo<DictionaryVO> list = dictionaryService.querySysDictionaryPageInfo(query);
        return ResultVo.Success(list.getList().get(0));
    }
    /**
     * 新增字典表
     */
    @PostMapping("/addSysDictionary")
    public ResultVo addSysDictionary(@RequestBody @Valid DictionaryPO po, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        try{
            dictionaryService.addSysDictionaryList(po,getCurrUseraccount());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * 编辑字典表
     */
    @PostMapping("/updateSysDictionary")
    public ResultVo updateSysDictionary(@RequestBody @Valid DictionaryPO po, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        if(null == po.getId()){
            return ResultVo.Fail("id不能为空");
        }
        try{
            dictionaryService.updateSysDictionaryList(po,getCurrUseraccount());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }
    /**
     * 删除字典表
     */
    @PostMapping("/deleteSysDictionary")
    public ResultVo deleteSysDictionary(@JsonParam Long id){
        dictionaryService.delById(id);
        return ResultVo.Success();
    }
}
