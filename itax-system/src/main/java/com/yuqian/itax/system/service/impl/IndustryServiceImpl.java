package com.yuqian.itax.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.system.dao.IndustryMapper;
import com.yuqian.itax.system.entity.*;
import com.yuqian.itax.system.entity.dto.BizScopeDTO;
import com.yuqian.itax.system.entity.dto.IndustryApiDTO;
import com.yuqian.itax.system.entity.dto.IndustryInfoDTO;
import com.yuqian.itax.system.entity.query.IndustryQuery;
import com.yuqian.itax.system.entity.vo.*;
import com.yuqian.itax.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("industryService")
public class IndustryServiceImpl extends BaseServiceImpl<IndustryEntity,IndustryMapper> implements IndustryService {
    @Resource
    private InvoiceCategoryService invoiceCategoryService;
    @Resource
    private RatifyTaxService ratifyTaxService;
    @Resource
    private BusinessScopeService businessScopeService;
    @Resource
    private ParkService parkService;
    @Resource
    private CityService cityService;
    @Resource
    private InvoiceCategoryBaseService invoiceCategoryBaseService;
    @Resource
    private BusinessscopeTaxcodeService businessscopeTaxCodeService;

    @Override
    public IndustryInfoVO getById(Long industryId, Long parkId) throws BusinessException {
        // 判断行业ID和园区是否匹配
        IndustryEntity tt = new IndustryEntity();
        tt.setId(industryId);
        tt.setParkId(parkId);
        IndustryEntity entity = this.selectOne(tt);
        if(null == entity){
            throw new BusinessException("未找到相关园区的行业信息");
        }
        IndustryInfoVO industryVo = new IndustryInfoVO();
        industryVo.setCompanyType(entity.getCompanyType());
        industryVo.setOrderDesc(entity.getOrderDesc());
        InvoiceCategoryEntity t = new InvoiceCategoryEntity();
        t.setIndustryId(industryId);
        List<InvoiceCategoryEntity> invoiceCategoryList = this.invoiceCategoryService.select(t);
        List<RatifyTaxEntity> ratifyTaxList = ratifyTaxService.listRatifyTax(industryId);
        List<BusinessScopeEntity> businessScopelist = businessScopeService.listBusinessScope(industryId);
        industryVo.setBusinessScopelist(businessScopelist);
        industryVo.setRatifyTaxList(ratifyTaxList);
        industryVo.setInvoiceCategoryList(invoiceCategoryList);
        industryVo.setBusinessContent(businessScopelist.get(0).getBusinessContent());

        /**
         * 若园区ID不为空，生成名称示例，格式如“贵溪市XXX信息咨询服务中心（兼容外部调用）
         */
        if(null != parkId){
            // 查询园区所在市
            ParkEntity park = this.parkService.findById(parkId);
            if(null == park){
                throw new BusinessException("园区信息不存在，请联系客服");
            }
            // 示例名称生成
            IndustryEntity industry = this.findById(industryId);
            industryVo.setExampleNameHead(park.getParkCity());
            industryVo.setExampleNameTail(industry.getExampleName().replace("*",""));
            String exmpleName = park.getParkCity() + industry.getExampleName();
            industryVo.setExampleName(exmpleName);
        }
        return industryVo;
    }

    @Override
    public PageInfo<IndustryInfoVO> listPage(IndustryQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        PageInfo pageInfo = new PageInfo(mapper.listIndustryInfo(query));
        List<IndustryInfoVO> lists = pageInfo.getList();
        if (CollectionUtil.isEmpty(lists)) {
            return pageInfo;
        }
        InvoiceCategoryEntity t;
        for (IndustryInfoVO industryVO : lists) {
            Long industryId = industryVO.getId();
            t = new InvoiceCategoryEntity();
            t.setIndustryId(industryId);
            List<InvoiceCategoryEntity> invoiceCategoryList = this.invoiceCategoryService.select(t);
            List<RatifyTaxEntity> ratifyTaxList = ratifyTaxService.listRatifyTax(industryId);
            List<BusinessScopeEntity> businessScopelist = businessScopeService.listBusinessScope(industryId);
            industryVO.setBusinessScopelist(businessScopelist);
            industryVO.setRatifyTaxList(ratifyTaxList);
            industryVO.setInvoiceCategoryList(invoiceCategoryList);
        }
        return pageInfo;
    }

    @Override
    public List<IndustryInfoVO> queryIndustryList(IndustryQuery query) {
        List<IndustryInfoVO> lists = mapper.listIndustryInfo(query);
        if (CollectionUtil.isEmpty(lists)) {
            return lists;
        }
        InvoiceCategoryEntity t;
        for (IndustryInfoVO industryVO : lists) {
            Long industryId = industryVO.getId();
            t = new InvoiceCategoryEntity();
            t.setIndustryId(industryId);
            List<InvoiceCategoryEntity> invoiceCategoryList = this.invoiceCategoryService.select(t);
            List<RatifyTaxEntity> ratifyTaxList = ratifyTaxService.listRatifyTax(industryId);
            List<BusinessScopeEntity> businessScopelist = businessScopeService.listBusinessScope(industryId);
            industryVO.setBusinessScopelist(businessScopelist);
            for (BusinessScopeEntity businessScopeEntity :   businessScopelist    ) {
                industryVO.setBusinessContent((industryVO.getBusinessContent()==null?"":industryVO.getBusinessContent())+" "+businessScopeEntity.getBusinessContent());
            }
            industryVO.setRatifyTaxList(ratifyTaxList);
            for (RatifyTaxEntity ratifyTaxEntity :   ratifyTaxList    ) {
                industryVO.setTaxName((industryVO.getTaxName()==null?"":industryVO.getTaxName())+" "+ratifyTaxEntity.getTaxName());
            }
            industryVO.setInvoiceCategoryList(invoiceCategoryList);
            for (InvoiceCategoryEntity invoiceCategoryEntity :   invoiceCategoryList    ) {
                industryVO.setCategoryNames((industryVO.getCategoryNames()==null?"":industryVO.getCategoryNames())+" \r\n "+invoiceCategoryEntity.getCategoryName());
            }
        }
        return lists;
    }

    @Override
    @Transactional
    public void add(IndustryInfoDTO dto) {
        //保存行业信息
        IndustryEntity industryEntity = dto.getIndustryEntity();
        mapper.insertSelective(industryEntity);
        dto.setIndustryId(industryEntity.getId());
        //保存核定税种
        List<RatifyTaxEntity> ratifies = dto.getRatifyTaxEntity();
        if (CollectionUtil.isNotEmpty(ratifies)) {
            for (RatifyTaxEntity ratify : ratifies) {
                ratifyTaxService.insertSelective(ratify);
            }
        }
        //保存经营范围
        List<BusinessScopeEntity> businesses = dto.getBusinessScopeEntity();
        if (CollectionUtil.isNotEmpty(businesses)) {
            for (BusinessScopeEntity business : businesses) {
                businessScopeService.insertSelective(business);
            }
        }
        //保存开票类目
        invoiceCategoryService.addBatch(dto);
    }

    @Override
    @Transactional
    public void delete(Long industryId) {
        mapper.deleteByPrimaryKey(industryId);
        ratifyTaxService.delByIndustryId(industryId);
        businessScopeService.delByIndustryId(industryId);
        invoiceCategoryService.delByIndustryId(industryId);
    }

    @Override
    @Transactional
    public void edit(IndustryEntity industryEntity, IndustryInfoDTO dto) {
        //经营范围
        businessScopeService.delByIndustryId(industryEntity.getId());
        List<BusinessScopeEntity> businesses = dto.getBusinessScopeEntity();
        if (CollectionUtil.isNotEmpty(businesses)) {
            for (BusinessScopeEntity business : businesses) {
                businessScopeService.insertSelective(business);
            }
        }
        //核定税种
        ratifyTaxService.delByIndustryId(industryEntity.getId());
        List<RatifyTaxEntity> ratifies = dto.getRatifyTaxEntity();
        if (CollectionUtil.isNotEmpty(ratifies)) {
            for (RatifyTaxEntity ratify : ratifies) {
                ratifyTaxService.insertSelective(ratify);
            }
        }
        //开票类目
        invoiceCategoryService.delByIndustryId(industryEntity.getId());
        List<InvoiceCategoryBaseStringVO> list=new ArrayList<>();
        for (InvoiceCategoryBaseStringVO vo:dto.getCategoryList()) {
            if(vo.getId()==null){
                InvoiceCategoryBaseEntity entity=new InvoiceCategoryBaseEntity();
                entity.setTaxClassificationAbbreviation(vo.getTaxClassificationAbbreviation());
                entity.setGoodsName(vo.getGoodsName());
                List<InvoiceCategoryBaseEntity> invoiceCategoryBaseEntities=invoiceCategoryBaseService.select(entity);
                if(CollectionUtil.isEmpty(invoiceCategoryBaseEntities)){
                    continue;
                }
                vo.setId(invoiceCategoryBaseEntities.get(0).getId());

            }
            list.add(vo);
        }
        dto.setCategoryList(list);
        invoiceCategoryService.addBatch(dto);

        //行业主表
        industryEntity.setExampleInvoice(dto.getExampleInvoice());
        industryEntity.setExampleName(dto.getExampleName());
        industryEntity.setIndustryName(dto.getIndustryName());
        industryEntity.setOrderDesc(dto.getOrderDesc());
        industryEntity.setUpdateTime(dto.getAddTime());
        industryEntity.setUpdateUser(dto.getAddUser());
        mapper.updateByPrimaryKeySelective(industryEntity);
    }

    @Override
    public List<IndustryApiVO> getIndustryList(IndustryApiDTO entity) throws BusinessException {
        ParkEntity park = new ParkEntity();
        park.setParkCode(entity.getParkCode());
        park = parkService.selectOne(park);
        if(null == park){
            throw new BusinessException(ErrorCodeEnum.PARK_NOT_EXIST);
        }
        if(!Objects.equals(park.getStatus(), 1)){
            throw new BusinessException(ErrorCodeEnum.PARK_STAUS_ERROR);
        }
        List<IndustryApiVO> list = mapper.getIndustryList(entity.getParkCode(), entity.getCompanyType(),entity.getOemCode(),null);
        return list;
    }

    @Override
    public ScopeCategoryVO getBizScopeAndInvCategory(BizScopeDTO dto) throws BusinessException {
        log.info("查询开票类目和经营范围:{}", JSON.toJSONString(dto));

        // 查询园区信息
        List<ParkEntity> parkList = this.parkService.getParkByParkCode(dto.getParkCode(),null);
        if(CollectionUtil.isEmpty(parkList)){
            throw new BusinessException(ErrorCodeEnum.PARK_NOT_EXIST);
        }

        Long parkId = parkList.get(0).getId();

        IndustryEntity industry = this.findById(dto.getIndustryId());
        if(null == industry){
            throw new BusinessException(ErrorCodeEnum.INDUSTRY_NOT_SUPPORT);
        }

        // 判断行业ID和园区是否匹配
        IndustryEntity t = new IndustryEntity();
        t.setId(dto.getIndustryId());
        t.setParkId(parkId);
        IndustryEntity entity = this.selectOne(t);
        if(null == entity){
            throw new BusinessException(ErrorCodeEnum.INDUSTRY_NOT_SUPPORT);
        }

        // 查询开票类目列表
        InvoiceCategoryEntity ice = new InvoiceCategoryEntity();
        ice.setIndustryId(dto.getIndustryId());
        List<InvoiceCategoryEntity> invoiceCategoryList = this.invoiceCategoryService.select(ice);
        // 查询经营范围列表
        List<BusinessScopeEntity> businessScopelist = businessScopeService.listBusinessScope(dto.getIndustryId());
        // 查询核定税种列表
        List<RatifyTaxEntity> ratifyTaxList = ratifyTaxService.listRatifyTax(dto.getIndustryId());// 核定税种

        // 封装返回信息
        ScopeCategoryVO vo = new ScopeCategoryVO();
        List<BusinessScopeVO> scopeList = Lists.newArrayList(); // 经营范围列表
        businessScopelist.stream().forEach(businessScope -> {
            BusinessScopeVO scope = new BusinessScopeVO();
            BeanUtils.copyProperties(businessScope,scope);
            scopeList.add(scope);
        });

        List<RatifyTaxVO> taxList = Lists.newArrayList(); // 核定税种列表
        ratifyTaxList.stream().forEach(ratifyTax -> {
            RatifyTaxVO ratify = new RatifyTaxVO();
            BeanUtils.copyProperties(ratifyTax,ratify);
            taxList.add(ratify);
        });

        List<InvoiceCategoryVO> icvList = Lists.newArrayList(); // 开票类目列表
        invoiceCategoryList.stream().forEach(invoiceCategory -> {
            InvoiceCategoryVO icv = new InvoiceCategoryVO();
            BeanUtils.copyProperties(invoiceCategory,icv);
            icvList.add(icv);
        });
        vo.setBusinessScopelist(scopeList);
        vo.setRatifyTaxList(taxList);
        vo.setInvoiceCategoryList(icvList);
        return vo;
    }

    @Override
    public List<IndustryApiVO> selectIndustry(String oemCode, Long parkId, Integer companyType, String industryName) throws BusinessException {
        log.info("查询行业列表接口请求：{},{},{},{}",oemCode,parkId,companyType,industryName);

        ParkEntity park = new ParkEntity();
        park.setId(parkId);
        park = parkService.selectOne(park);
        if(null == park){
            throw new BusinessException(ErrorCodeEnum.PARK_NOT_EXIST.getText());
        }
        if(!Objects.equals(park.getStatus(), 1)){
            throw new BusinessException(ErrorCodeEnum.PARK_STAUS_ERROR.getText());
        }
        List<IndustryApiVO> list = mapper.getIndustryList(park.getParkCode(), companyType,oemCode,industryName);
        return list;
    }

    @Override
    public List<IndustryAdminVO> queryBlackListByParkId(String oemCode, Long parkId) {
        return mapper.queryBlackListByParkId(oemCode, parkId);
    }

    @Override
    public List<IndustryEntity> selectByIndustryIds(Long parkId, List<Long> ids) {
        return mapper.selectByIndustryIds(parkId, ids);
    }

    @Override
    public List<IndustryAndParkInfoVo> queryIndustryByParkIds(List<String> parkIds) {
        return mapper.queryIndustryByParkIds(parkIds);
    }

    @Override
    public ParkIndustryPresentationVO presentation(Long industryId) {
        ParkIndustryPresentationVO vo = mapper.queryIndustryPresentation(industryId);

        // 根据行业查询开票类目名称
        List<String> list = invoiceCategoryService.findCategoryNameByIndustryId(industryId);
        vo.setCategoryNames(list);

        return vo;
    }
}

