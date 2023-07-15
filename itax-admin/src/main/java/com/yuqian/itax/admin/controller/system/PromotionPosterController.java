package com.yuqian.itax.admin.controller.system;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.system.dao.PosterOemRelaMapper;
import com.yuqian.itax.system.entity.PosterOemRelaEntity;
import com.yuqian.itax.system.entity.dto.PromotionPosterPO;
import com.yuqian.itax.system.entity.query.PromotionPosterQuery;
import com.yuqian.itax.system.entity.vo.PromotionPosterVO;
import com.yuqian.itax.system.service.PosterOemRelaService;
import com.yuqian.itax.system.service.PromotionPosterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 推广海报管理
 */
@RestController
@RequestMapping("/promotion")
@Slf4j
public class PromotionPosterController extends BaseController {

    @Autowired
    PromotionPosterService promotionPosterService;
    @Autowired
    PosterOemRelaMapper posterOemRelaMapper;

    /**
     * 海报列表（分页）
     */
    @PostMapping("queryPromotionPoster")
    public ResultVo queryPromotionPoster(@RequestBody PromotionPosterQuery query) {
        PageInfo<PromotionPosterVO> page=promotionPosterService.queryPromotionPosterPage(query);
        return ResultVo.Success(page);
    }

    /**
     * 新增海报
     */
    @PostMapping("addPromotionPoster")
    public ResultVo addPromotionPoster(@RequestBody PromotionPosterPO po) {
        try{
            promotionPosterService.addPromotionPosterPage(po,getCurrUseraccount());
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }
    /**
     * 编辑海报
     */
    @PostMapping("updatePromotionPoster")
    public ResultVo updatePromotionPoster(@RequestBody PromotionPosterPO po) {
      try{
        promotionPosterService.updatePromotionPoster(po,getCurrUseraccount());
      }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
      }
        return ResultVo.Success();
    }

    /**
     * 海报详情
     */
    @PostMapping("queryPromotionPosterDetail")
    public ResultVo queryPromotionPosterDetail(@JsonParam Long id) {
        PromotionPosterVO promotionPosterVO=promotionPosterService.queryPromotionPosterDetail(id);
        return ResultVo.Success(promotionPosterVO);
    }

    /**
     * 删除海报
     */
    @PostMapping("deletePromotionPoster")
    public ResultVo deletePromotionPoster(@JsonParam Long id) {
        promotionPosterService.delById(id);
        //删除海报与机构关系表数据
        PosterOemRelaEntity posterOemRelaEntity=new PosterOemRelaEntity();
        posterOemRelaEntity.setPosterId(id);
        posterOemRelaMapper.delete(posterOemRelaEntity);
        return ResultVo.Success();
    }
    /**
     * 自动获取最大显示顺序字段
     */
    @PostMapping("getMaxPosterSn")
    public ResultVo getMaxPosterSn() {
        Integer sn=promotionPosterService.getMaxPosterSn();
        if(sn==null){
            sn=1;
        }
        return ResultVo.Success(sn);
    }
}
