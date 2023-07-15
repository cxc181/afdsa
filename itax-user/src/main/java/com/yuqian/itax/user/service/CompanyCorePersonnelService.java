package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.dao.CompanyCorePersonnelMapper;
import com.yuqian.itax.user.entity.CompanyCorePersonnelEntity;
import com.yuqian.itax.user.entity.dto.CompanyCorePersonnelDTO;
import com.yuqian.itax.user.entity.dto.UpdateRegOrderDTO;
import com.yuqian.itax.user.entity.vo.*;

import java.util.List;

/**
 * 企业核心成员信息表service
 * 
 * @Date: 2022年06月27日 17:55:00 
 * @author 蒋匿
 */
public interface CompanyCorePersonnelService extends IBaseService<CompanyCorePersonnelEntity,CompanyCorePersonnelMapper> {

    /**
     * 新增/编辑用户核心成员
     * @param dto
     */
    void addOrUpdatePersonnel(CompanyCorePersonnelDTO dto);


    /**
     * 校验用户核心成员信息
     * @param controlType 0-操作合伙人/股东 1-操作成员(有职务)
     * @return
     */
    void check(int controlType, String orderNo);

    /**
     * 查询用户企业核心成员信息列表
     * @param memberId
     * @param type 查询类型 0-全部 1-合伙人/股东 2-成员
     * @return
     */
    List<CompanyCorePersonnelVO> list(Long memberId,Long companyId, String orderNo, int type);

    /**
     * 查询用户企业核心成员信息详情
     * @param id
     * @return
     */
    CompanyCorePersonnelVO detail(Long id);

    /**
     * 根据订单id查询成员信息
     * @param orderNoList
     * @return
     */
    List<CompanyCorePersonnelExportVO> getCompanyCorePersonnelByOrderNo(List<String> orderNoList);

    /**
     * 根据企业id或订单号获取企业成员信息
     * @param companyId
     * @param orderNo
     * @return
     */
    List<CompanyCorePersonnelVO> getCompanyCorePersonnelByCompanyIdOrOrderNo(Long companyId,String orderNo);

    /**
     * 根据订单号修改核心程序的企业id
     * @param companyId
     * @param orderNo
     * @return
     */
    void updateCompanyCorePersonnelCompanyIdByOrderNo(Long companyId,String orderNo);

    /**
     * 查询企业注册订单
     * @param orderNo
     * @return
     */
    CompanyRegisterOrderVO getCompanyRegOrder(String orderNo);

    /**
     * 更新注册订单
     */
    void updateCompanyRegOrder(UpdateRegOrderDTO dto);

    /**
     * 搜索股东/合伙人列表
     * @param isExecutivePartner
     * @param orderNo
     * @param personnelName
     * @return
     */
    List<ShareholderPersonnelListVO> shareholderPersonnelList(Integer isExecutivePartner, String orderNo, String personnelName);

    /**
     * 删除成员
     * @param id
     * @param controlType 操作类型 0-操作合伙人/股东 1-操作成员
     */
    void deletePersonnel(Long id, Long currUserId, int controlType);
}

