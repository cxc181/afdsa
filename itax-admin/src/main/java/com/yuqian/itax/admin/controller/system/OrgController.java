package com.yuqian.itax.admin.controller.system;

import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.orgs.entity.OrgEntity;
import com.yuqian.itax.orgs.entity.vo.OrgVO;
import com.yuqian.itax.orgs.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/org")
public class OrgController extends BaseController {

    /**
     * 查询所有组织树
     * @author HZ
     * @return
     */
    @PostMapping("/orgTreeListAll")
    public ResultVo orgTreeListAll() {
        //查询当前登陆账号所在组织
        OrgEntity orgEntity=orgService.queryOrgEntityByUserId(getCurrUserId());
        List<OrgVO> orgTree = orgService.queryOrgTreeAll(orgEntity.getId());
        if (null != orgTree) {
            return ResultVo.Success(orgTree);
        }
        return ResultVo.Fail();
    }

}
