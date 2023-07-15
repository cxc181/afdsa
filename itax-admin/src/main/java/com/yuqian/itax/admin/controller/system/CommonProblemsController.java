package com.yuqian.itax.admin.controller.system;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.entity.CommonProblemsEntity;
import com.yuqian.itax.system.entity.dto.CommonProblemsPO;
import com.yuqian.itax.system.entity.query.CommonProblemsQuery;
import com.yuqian.itax.system.entity.vo.CommonProblemsVO;
import com.yuqian.itax.system.service.CommonProblemsService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

    /**
     * 常见问题管理controller
     */

@RestController
@RequestMapping("common/problems")
public class CommonProblemsController extends BaseController {


        @Autowired
        CommonProblemsService commonProblemsService;

        /**
         * 获取常见问题列表（分页）
         */
        @PostMapping("/page")
        public ResultVo page(@RequestBody CommonProblemsQuery query) {

            UserEntity userEntity = userService.findById(getCurrUserId());
            if (userEntity.getPlatformType() !=null&&userEntity.getPlatformType() == 3) {
                //园区组织看不到数据
                return ResultVo.Success();
            } else if (userEntity.getPlatformType() == 1) {
                query.setOemCode(null);
            } else {
                query.setOemCode(userEntity.getOemCode());
            }
            PageInfo pageInfo = commonProblemsService.getCommomProblePageInfo(query);
            return ResultVo.Success(pageInfo);
        }


        /**
         * 常见问题新增
         */
        @PostMapping("/add")
        public ResultVo add(@RequestBody CommonProblemsPO po) {
            try {
                commonProblemsService.addCommonProblemsEntity(po, getCurrUseraccount());
            } catch (BusinessException e) {
                return ResultVo.Fail(e.getMessage());
            }
            return ResultVo.Success();
        }


        /**
         * 常见问题编辑
         */
        @PostMapping("/update")
        public ResultVo update(@RequestBody CommonProblemsPO po) {
            try {
                if(null == po.getId()){
                    return ResultVo.Fail("id不能为空");
                }
                commonProblemsService.updateCommonProblemsEntity(po, getCurrUseraccount());
            } catch (BusinessException e) {
                return ResultVo.Fail(e.getMessage());
            }
            return ResultVo.Success();
        }

        /**
         * 常见问题详情
         */
        @PostMapping("/detail")
        public ResultVo detail(@JsonParam Long id) {
            try {
                CommonProblemsVO commonProblemsEntity=commonProblemsService.getCommonProblemsById(id);
                return ResultVo.Success(commonProblemsEntity);
            } catch (BusinessException e) {
                return ResultVo.Fail(e.getMessage());
            }
        }

        /**
         * 常见问题删除（物理删除）
         */
        @PostMapping("/delete")
        public ResultVo delete(@JsonParam Long id) {
            try {
                commonProblemsService.delById(id);
            } catch (BusinessException e) {
                return ResultVo.Fail(e.getMessage());
            }
            return ResultVo.Success();
        }






}