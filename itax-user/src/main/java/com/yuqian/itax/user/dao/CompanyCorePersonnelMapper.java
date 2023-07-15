package com.yuqian.itax.user.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.CompanyCorePersonnelEntity;
import com.yuqian.itax.user.entity.dto.UpdateRegOrderDTO;
import com.yuqian.itax.user.entity.vo.CompanyCorePersonnelExportVO;
import com.yuqian.itax.user.entity.vo.CompanyRegisterOrderVO;
import com.yuqian.itax.user.entity.vo.ShareholderPersonnelListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业核心成员信息表dao
 * 
 * @Date: 2022年06月27日 17:55:00 
 * @author 蒋匿
 */
@Mapper
public interface CompanyCorePersonnelMapper extends BaseMapper<CompanyCorePersonnelEntity> {

    /**
     * 根据订单id查询成员信息
     * @param orderNoList
     * @return
     */
    List<CompanyCorePersonnelExportVO> getCompanyCorePersonnelByOrderNo(@Param("orderNoList") List orderNoList);

    /**
     * 根据订单号修改核心程序的企业id
     * @param companyId
     * @param orderNo
     * @return
     */
     void updateCompanyCorePersonnelCompanyIdByOrderNo(@Param("companyId")Long companyId,@Param("orderNo") String orderNo);

    /**
     * 查询企业注册订单
     * @param orderNo
     * @return
     */
    CompanyRegisterOrderVO getCompanyRegOrder(String orderNo);

    void updateCompanyRegOrder(UpdateRegOrderDTO dto);

    /**
     * 搜索合伙人/股东列表
     * @param isExecutivePartner
     * @param orderNo
     * @param personnelName
     * @return
     */
    List<ShareholderPersonnelListVO> shareholderPersonnelList(@Param("isExecutivePartner") Integer isExecutivePartner, @Param("orderNo") String orderNo,
                                                              @Param("personnelName") String personnelName);

    List<CompanyCorePersonnelEntity> personnelList(@Param("type") Integer type, @Param("memberId") Long memberId, @Param("orderNo") String orderNo);
}

