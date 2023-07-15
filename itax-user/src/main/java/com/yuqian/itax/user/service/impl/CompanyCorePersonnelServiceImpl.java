package com.yuqian.itax.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.dao.CompanyCorePersonnelMapper;
import com.yuqian.itax.user.entity.CompanyCorePersonnelEntity;
import com.yuqian.itax.user.entity.dto.CompanyCorePersonnelDTO;
import com.yuqian.itax.user.entity.dto.UpdateRegOrderDTO;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.enums.MemberCompanyTypeEnum;
import com.yuqian.itax.user.service.CompanyCorePersonnelService;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.util.util.AuthKeyUtils;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.IntervalUtil;
import com.yuqian.itax.util.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("companyCorePersonnelService")
public class CompanyCorePersonnelServiceImpl extends BaseServiceImpl<CompanyCorePersonnelEntity,CompanyCorePersonnelMapper> implements CompanyCorePersonnelService {

    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private OssService ossService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private OemParamsService oemParamsService;

    private static final int SHAREHOLDER_NUMBER_MAX = 49;
    private static final int SHAREHOLDER_NUMBER_MIN = 2;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdatePersonnel(CompanyCorePersonnelDTO dto) {
        // 1.查询注册订单
        CompanyRegisterOrderVO regOrder = this.getCompanyRegOrder(dto.getOrderNo());
        if (regOrder.getOrderStatus() != 13 && regOrder.getOrderStatus() != 0) {
            throw new BusinessException("订单状态不正确");
        }

        // 2.参数校验（必要性和有效性）
        // 2.1 身份类型为企业时，营业执照地址不能为空
        if (dto.getIdentityType() == 2) {
            if (StringUtil.isBlank(dto.getBusinessLicense())) {
                throw new BusinessException("营业执照不能为空");
            }
            // 有限合伙、有限责任营业执照需要证件有效期
            if ((MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(dto.getCompanyType())
                    || MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(dto.getCompanyType()))
                    && StringUtil.isBlank(dto.getExpireDate())) {
                throw new BusinessException("证件有效期为空");
            }
        } else { // 2.2 自然人身份，身份证正反面照地址不能为空
            if (StringUtil.isBlank(dto.getIdCardFront()) || StringUtil.isBlank(dto.getIdCardReverse())) {
                throw new BusinessException("身份证照片不能为空");
            }
        }
        // 2.3 非个体必要性参数校验
        if (dto.getCompanyType() != 1) {
            if (StringUtil.isBlank(dto.getPersonnelName())) {
                throw new BusinessException("名称不能为空");
            }
            if (StringUtil.isBlank(dto.getCertificateNo())) {
                throw new BusinessException("证件号不能为空");
            }
        }
        // 2.4 有限合伙企业参数校验
        if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(dto.getCompanyType()) && dto.getControlType() == 0) {
            if (null == dto.getPartnerType()) {
                throw new BusinessException("合伙人类型为空");
            }
            if (dto.getPartnerType() == 1 && null == dto.getIsExecutivePartner()) {
                throw new BusinessException("是否执行事务合伙人为空");
            }
            // 2.4.1 执行事务合伙人必须是普通合伙人
            if (null != dto.getIsExecutivePartner() && dto.getIsExecutivePartner() == 1
                    && null != dto.getPartnerType() && dto.getPartnerType() != 1) {
                throw new BusinessException("数据有误，有限合伙人不能成为执行事务合伙人");
            }
        }
        // 2.5 占股比例。是股东/合伙人时，投资金额和占股比例不能为空
        if (null != dto.getIsShareholder() && dto.getIsShareholder() == 1) {
            if (dto.getShareProportion()==null || dto.getShareProportion().compareTo(new BigDecimal(0))<=0) {
                throw new BusinessException("占股比例不能为空");
            }
            if (dto.getInvestmentAmount()!=null && dto.getInvestmentAmount().compareTo(new BigDecimal(0))<=0) {
                throw new BusinessException("投资金额不能为0");
            } else if (null == dto.getInvestmentAmount()) {
                // 根据投资比例计算投资金额
                BigDecimal multiply = regOrder.getRegisteredCapital().multiply(dto.getShareProportion());
                dto.setInvestmentAmount(multiply);
            }
        }
        // 2.6 参数有效性校验即数据控制
        // 2.6.1 成员类型。有限合伙企业操作成员时，职务仅允许财务和法人（即成员类型不是财务或无职务）
        if (dto.getControlType() == 1 && MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(dto.getCompanyType())
                && dto.getPersonnelType() < 4) {
            throw new BusinessException("不支持的成员类型");
        }
        // 2.6.2 是否股东/合伙人。操作类型是股东/合伙人时，该字段必为1“是”
        if (dto.getControlType() == 0) {
            dto.setIsShareholder(1);
        }
        // 2.6.3 操作股东/合伙人时不能有职务（除个人独资添加股东时默认法人外）
        if (dto.getControlType() == 0 && (dto.getPersonnelType() != 5 || (dto.getIsLegalPerson() == 1 && !MemberCompanyTypeEnum.INDEPENDENTLY.getValue().equals(dto.getCompanyType())))) {
            throw new BusinessException("数据有误，该成员不能有职务");
        }
        // 2.6.4 有限责任企业法人必须来产生于执行董事、经理、监事代表人
        if (dto.getControlType() == 1 && MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(dto.getCompanyType())
                && dto.getIsLegalPerson() == 1) {
            if (dto.getPersonnelType() == 4) {
                throw new BusinessException("财务不能是法人");
            }
            if (dto.getPersonnelType() == 5) {
                throw new BusinessException("数据有误，法定代表人需在执行董事、经理、监事代表人产生");
            }
        }
        // 2.6.5 所有企业成员都只能是自然人
        if (dto.getControlType() == 1 && dto.getIdentityType() != 1) {
            throw new BusinessException("数据有误，成员必须为自然人");
        }

        // 3.编辑核心成员时校验
        CompanyCorePersonnelEntity personnelEntity = null;
        boolean isUpdate = false;// 是否是更新成员
        // 3.1 编辑核心成员时成员是否存在
        if (null != dto.getId()) {
            isUpdate = true;
            // 查询核心成员信息
            personnelEntity = this.findById(dto.getId());
            if (null == personnelEntity) {
                throw new BusinessException("未查询到成员信息");
            }
            // 3.2 编辑股东或合伙人时参数校验
            if (personnelEntity.getIdentityType().compareTo(dto.getIdentityType()) != 0) {
                throw new BusinessException("身份类型不允许修改，如需修改请删除后新增");
            }
            if (!personnelEntity.getPersonnelName().equals(dto.getPersonnelName())) {
                throw new BusinessException("名称不允许修改，如需修改请删除后新增");
            }
            if (!personnelEntity.getCertificateNo().equals(dto.getCertificateNo())) {
                throw new BusinessException("证件号不允许修改，如需修改请删除后新增");
            }
            // 编辑“是否为执行事务合伙人”为“否”时，已委派的执行事务合伙人不允许修改该值
            if (null != dto.getIsExecutivePartner() && personnelEntity.getIsExecutivePartner()!=null && personnelEntity.getIsExecutivePartner() == 1 && dto.getIsExecutivePartner() == 0) {
                Example example = new Example(CompanyCorePersonnelEntity.class);
                example.createCriteria().andEqualTo("appointPartyId", dto.getId());
                List<CompanyCorePersonnelEntity> list = this.selectByExample(example);
                if (CollectionUtil.isNotEmpty(list)) {
                    throw new BusinessException("已委派法人的执行事务合伙人不能更改");
                }
            }
        }

        // 4.新增股东/合伙人时的重复添加校验
        if (dto.getControlType() == 0 && null == dto.getId()) {
            // 4.1 股东/合伙人信息不能重复
            // 根据订单号+用户id+身份类型+成员类型+姓名+证件号查询核心成员
            Example example = new Example(CompanyCorePersonnelEntity.class);
            example.createCriteria().andEqualTo("orderNo", regOrder.getOrderNo())
                    .andEqualTo("memberId", dto.getMemberId())
                    .andEqualTo("identityType", dto.getIdentityType())
                    .andEqualTo("isShareholder", 1)
                    .andEqualTo("personnelName", dto.getPersonnelName())
                    .andEqualTo("certificateNo", dto.getCertificateNo());
            List<CompanyCorePersonnelEntity> personnelEntities = mapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(personnelEntities)) {
                throw new BusinessException("该成员已存在，请勿重复添加");
            }
        }

        // 5.合伙人/股东信息校验
        if (dto.getIsShareholder()!=null && dto.getIsShareholder() == 1 && null != dto.getId()) {
            // 查询股东信息
            Example shareholderExm = new Example(CompanyCorePersonnelEntity.class);
            shareholderExm.createCriteria().andEqualTo("orderNo", regOrder.getOrderNo())
                    .andEqualTo("memberId", dto.getMemberId())
                    .andEqualTo("isShareholder", "1").andNotEqualTo("id", dto.getId());
            List<CompanyCorePersonnelEntity> shareholders = mapper.selectByExample(shareholderExm);

            // 股东数校验
            if (CollectionUtil.isNotEmpty(shareholders)) {
                if (MemberCompanyTypeEnum.INDEPENDENTLY.getValue().equals(dto.getCompanyType())) { // 个人独资股东数仅限一人
                    throw new BusinessException("个人独资企业股东数限1人");
                } else if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(dto.getCompanyType())
                        || MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(dto.getCompanyType())
                ) { // 有限合伙/有限责任公司
                    int number = SHAREHOLDER_NUMBER_MAX;
                    // 获取公司股东数配置
                    String shareholderNumber = dictionaryService.getValueByCode("shareholder_number");
                    if (StringUtil.isNotBlank(shareholderNumber)) {
                        number = Integer.parseInt(shareholderNumber);
                    }
                    if (shareholders.size() > number - 1) {
                        throw new BusinessException(MemberCompanyTypeEnum.getByValue(dto.getCompanyType()).getMessage() + "企业股东数不能超过" + number + "人");
                    }
                }
            }
        }

        // 6.操作成员时的冲突校验
        if (dto.getControlType() == 1) {
            // 6.1 经理不能重复
            if (dto.getPersonnelType() == 1) {
                Example e1 = new Example(CompanyCorePersonnelEntity.class);
                e1.createCriteria().andLike("personnelType", "%1%").andEqualTo("orderNo", regOrder.getOrderNo());
                if (null != dto.getId()) {
                    e1.and(e1.createCriteria().andNotEqualTo("id", dto.getId()));
                }
                List<CompanyCorePersonnelEntity> list1 = mapper.selectByExample(e1);
                if (dto.getPersonnelType() == 1 && CollectionUtil.isNotEmpty(list1)) {
                    throw new BusinessException("经理已存在，请勿重复添加");
                }
            }
            // 6.2 监事不能重复
            if (dto.getPersonnelType() == 2) {
                Example e2 = new Example(CompanyCorePersonnelEntity.class);
                e2.createCriteria().andLike("personnelType", "%2%").andEqualTo("orderNo", regOrder.getOrderNo());
                if (null != dto.getId()) {
                    e2.and(e2.createCriteria().andNotEqualTo("id", dto.getId()));
                }
                List<CompanyCorePersonnelEntity> list2 = mapper.selectByExample(e2);
                if (dto.getPersonnelType() == 2 && CollectionUtil.isNotEmpty(list2)) {
                    throw new BusinessException("监事已存在，请勿重复添加");
                }
            }
            // 6.3 执行董事不能重复
            if (dto.getPersonnelType() == 3) {
                Example e3 = new Example(CompanyCorePersonnelEntity.class);
                e3.createCriteria().andLike("personnelType", "%3%").andEqualTo("orderNo", regOrder.getOrderNo());
                if (null != dto.getId()) {
                    e3.and(e3.createCriteria().andNotEqualTo("id", dto.getId()));
                }
                List<CompanyCorePersonnelEntity> list3 = mapper.selectByExample(e3);
                if (dto.getPersonnelType() == 3 && CollectionUtil.isNotEmpty(list3)) {
                    throw new BusinessException("执行董事已存在，请勿重复添加");
                }
            }
            // 6.4 财务不能重复
            if (dto.getPersonnelType() == 4) {
                Example e4 = new Example(CompanyCorePersonnelEntity.class);
                e4.createCriteria().andLike("personnelType", "%4%").andEqualTo("orderNo", regOrder.getOrderNo());
                if (null != dto.getId()) {
                    e4.and(e4.createCriteria().andNotEqualTo("id", dto.getId()));
                }
                List<CompanyCorePersonnelEntity> list4 = mapper.selectByExample(e4);
                if (dto.getPersonnelType() == 4 && CollectionUtil.isNotEmpty(list4)) {
                    throw new BusinessException("财务已存在，请勿重复添加");
                }
            }
            // 6.5 法人不能重复
            if (dto.getIsLegalPerson() == 1) {
                Example e5 = new Example(CompanyCorePersonnelEntity.class);
                e5.createCriteria().andEqualTo("isLegalPerson", "1").andEqualTo("orderNo", regOrder.getOrderNo());
                if (null != dto.getId()) {
                    e5.and(e5.createCriteria().andNotEqualTo("id", dto.getId()));
                } else if (null != dto.getMappingShareholdersId()) {
                    e5.and(e5.createCriteria().andNotEqualTo("id", dto.getMappingShareholdersId()));
                }
                List<CompanyCorePersonnelEntity> list5 = mapper.selectByExample(e5);
                if (dto.getIsLegalPerson() == 1 && CollectionUtil.isNotEmpty(list5)) {
                    throw new BusinessException("法人已存在，请勿重复添加");
                }
            }

            // 成员用户是否已存在
            Example example = new Example(CompanyCorePersonnelEntity.class);
            example.createCriteria().andEqualTo("orderNo", regOrder.getOrderNo())
                    .andEqualTo("certificateNo", dto.getCertificateNo());
            List<CompanyCorePersonnelEntity> list = this.selectByExample(example);
            if (CollectionUtil.isNotEmpty(list) && list.size() == 1) {
                CompanyCorePersonnelEntity corePersonnelEntity = list.get(0);
                // 成员用户的职务类型与本次添加的成员用户类型不能同时存在监事和其他非无职务成员类型
                String personnelType = corePersonnelEntity.getPersonnelType();
                personnelType = personnelType + "," + dto.getPersonnelType();
                if (personnelType.contains("2")) {
                    if (personnelType.contains("1")) {
                        throw new BusinessException("监事和经理不能是同一个人");
                    } else if (personnelType.contains("3")) {
                        throw new BusinessException("监事和执行董事不能是同一个人");
                    } else if (personnelType.contains("4")) {
                        throw new BusinessException("监事和财务不能是同一个人");
                    }
                }
                // 成员不能同时成为法人和财务
                if (personnelType.contains("4") && (dto.getIsLegalPerson() == 1 || corePersonnelEntity.getIsLegalPerson() == 1)) {
                    throw new BusinessException("财务不能和法人是同一个人");
                }
                personnelEntity = corePersonnelEntity;
                isUpdate = true;
                dto.setIsShareholder(corePersonnelEntity.getIsShareholder());
            }
        }

        // 自然人身份证二要素验证
        if (!isUpdate && dto.getIdentityType() == 1) {
            //读取要素认证相关配置 paramsType=5
            OemParamsEntity paramsEntity = oemParamsService.getParams(dto.getOemCode(), 5);
            if (null == paramsEntity) {
                throw new BusinessException("未配置身份二要素相关信息！");
            }
            // agentNo
            String agentNo = paramsEntity.getAccount();
            // signKey
            String signKey = paramsEntity.getSecKey();
            // authUrl
            String authUrl = paramsEntity.getUrl();
            String authResult = AuthKeyUtils.auth2Key(agentNo, signKey, authUrl, dto.getPersonnelName(), dto.getCertificateNo(), paramsEntity.getParamsValues());
            if (StringUtils.isBlank(authResult)) {
                throw new BusinessException("二要素认证失败");
            }
            JSONObject resultObj = JSONObject.parseObject(authResult);
            if (!"00".equals(resultObj.getString("code"))) {
                throw new BusinessException("二要素认证失败：" + resultObj.getString("msg"));
            }
        }

        // 新增股东时，是否映射到成员
        if (dto.getControlType() == 0 && null == dto.getId()) {
            Example example = new Example(CompanyCorePersonnelEntity.class);
            example.createCriteria().andEqualTo("orderNo", regOrder.getOrderNo())
                    .andEqualTo("isShareholder", 0)
                    .andEqualTo("personnelName", dto.getPersonnelName())
                    .andEqualTo("certificateNo", dto.getCertificateNo());
            List<CompanyCorePersonnelEntity> personnelEntities = mapper.selectByExample(example);
            // 股东/合伙人映射到了成员（且不存在重复股东/合伙人的可能性）
            if (CollectionUtil.isNotEmpty(personnelEntities) && personnelEntities.size() == 1) {
                // 直接编辑该成员为股东
                personnelEntity = personnelEntities.get(0);
                dto.setIsLegalPerson(personnelEntity.getIsLegalPerson());
            }
        }

        // 构建核心成员对象
        if (null == personnelEntity) {
            personnelEntity = new CompanyCorePersonnelEntity();
            personnelEntity.setAddTime(new Date());
            personnelEntity.setAddUser(dto.getAddUser());
            personnelEntity.setPersonnelType(dto.getPersonnelType().toString());
        } else {
            personnelEntity.setUpdateTime(new Date());
            personnelEntity.setUpdateUser(dto.getAddUser());
            isUpdate = true;
        }
        ObjectUtil.copyNotBlankObject(dto, personnelEntity);
        personnelEntity.setAppointPartyId(dto.getAppointPartyId());
        // 成员类型处理
        String personnelType = null == personnelEntity.getPersonnelType() ? "" : personnelEntity.getPersonnelType();
        if (null != dto.getPersonnelType() && !personnelType.contains(dto.getPersonnelType().toString())) {
            personnelType = personnelType + "," + dto.getPersonnelType();
            personnelEntity.setPersonnelType(personnelType);
        }

        if (dto.getIdentityType() == 1) {
            personnelEntity.setBusinessLicense(null);
        } else {
            personnelEntity.setIdCardFront(null);
            personnelEntity.setIdCardReverse(null);
        }
        // 数据校正（普通合伙人执行事务合伙人被改为有限合伙人时，清除执行合伙人数据）
        if (null != dto.getPartnerType() && dto.getPartnerType() == 2) {
            personnelEntity.setIsExecutivePartner(null);
        }
        // 新增/编辑核心成员
        if (isUpdate) {
            mapper.updateByPrimaryKey(personnelEntity);
        } else {
            mapper.insert(personnelEntity);
        }

        // 新增成员是法人时，需要补充注册订单经营者信息
        if (dto.getIsLegalPerson() == 1) {
            if (StringUtil.isBlank(dto.getPersonnelName())) {
                throw new BusinessException("法人姓名不能为空");
            }
            if (StringUtil.isBlank(dto.getCertificateNo())) {
                throw new BusinessException("身份证号码不能为空");
            }
            // 有限合伙企业法人需要执行事务合伙人委派
            if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(dto.getCompanyType())) {
                if (null == dto.getAppointPartyId()) {
                    throw new BusinessException("委派方id为空");
                }
                // 查询委派方
                Example example = new Example(CompanyCorePersonnelEntity.class);
                example.createCriteria().andEqualTo("id", dto.getAppointPartyId()).andEqualTo("isExecutivePartner", 1);
                List<CompanyCorePersonnelEntity> list = this.selectByExample(example);
                if (CollectionUtil.isEmpty(list)) {
                    throw new BusinessException("未查询到委派方合伙人信息或委派方为非执行事务合伙人");
                }
            }

            // 校验经营者年龄
            String year = dto.getCertificateNo().substring(6, 10);
            boolean numeric = StringUtils.isNumeric(year);
            if (!numeric) {
                throw new BusinessException("身份证号码格式有误");
            }
            String age = null;
            age = new BigDecimal(DateUtil.getYear(new Date())).subtract(new BigDecimal(year)).toString();
            DictionaryEntity rangeOfOperator = dictionaryService.getByCode("age_range_of_operator");
            if (null == rangeOfOperator) {
                throw new BusinessException("未配置经营者年龄范围");
            }
            boolean inTheInterval = IntervalUtil.isInTheInterval(age, rangeOfOperator.getDictValue());
            if (!inTheInterval) {
                throw new BusinessException("经营者年龄不符合注册要求");
            }

            // 更新注册订单信息
            UpdateRegOrderDTO updateRegOrderDTO = new UpdateRegOrderDTO();
            updateRegOrderDTO.setContactPhone(dto.getContactPhone());
            updateRegOrderDTO.setExpireDate(dto.getExpireDate());
            updateRegOrderDTO.setIdCardNumber(dto.getCertificateNo());
            updateRegOrderDTO.setIdCardAddr(dto.getCertificateAddr());
            updateRegOrderDTO.setIdCardFront(dto.getIdCardFront());
            updateRegOrderDTO.setIdCardReverse(dto.getIdCardReverse());
            updateRegOrderDTO.setOperatorName(dto.getPersonnelName());
            updateRegOrderDTO.setOrderNo(dto.getOrderNo());
            updateRegOrderDTO.setRemark("更新注册订单法人信息");
            updateRegOrderDTO.setUpdateUser(dto.getAddUser());
            updateRegOrderDTO.setUpdateTime(new Date());
            this.updateCompanyRegOrder(updateRegOrderDTO);
        }
    }

    @Override
    public void check(int controlType, String orderNo) {
        // 查询用户注册订单
        RegPreOrderVO regOrder = Optional.ofNullable(memberAccountService.queryRegOrderByOrderNo(orderNo)).orElseThrow(() -> new BusinessException("未查询到用户注册预订单"));

        List<CompanyCorePersonnelVO> list = this.getCompanyCorePersonnelByCompanyIdOrOrderNo(null, orderNo);
        if (CollectionUtil.isEmpty(list)) {
            throw new BusinessException("未查询到企业成员信息，请添加企业成员");
        }

        // 操作类型为合伙人/股东
        if (controlType == 0) {
            // 校验有限合伙企业合伙人/有限责任企业股东数量是否达到最低标准；股东/合伙人占股比例是否等于1（100%）
            if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(regOrder.getCompanyType())
                    || MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(regOrder.getCompanyType())) {
                Integer minNumber = SHAREHOLDER_NUMBER_MIN;
                long number = list.stream().filter(x -> x.getIsShareholder().equals(1)).count();
                String s = MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(regOrder.getCompanyType()) ? "合伙人" : "股东";
                if (number < minNumber) {
                    throw new BusinessException(MemberCompanyTypeEnum.getByValue(regOrder.getCompanyType()).getMessage() + "企业" + s + "数不能少于" + minNumber + "人");
                }
                BigDecimal shareProportion = list.stream().filter(x -> x.getIsShareholder().equals(1)).map(CompanyCorePersonnelVO::getShareProportion).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (shareProportion.compareTo(BigDecimal.ONE) != 0) {
                    throw new BusinessException(s + "占股比例之和不为100%");
                }
                // 有限合伙企业至少有一个执行事务合伙人
                if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(regOrder.getCompanyType())) {
                    long executivePartnerNumber = list.stream().filter(x -> null != x.getIsExecutivePartner() && x.getIsExecutivePartner() == 1).count();
                    if (executivePartnerNumber < 1) {
                        throw new BusinessException("合伙人中必须至少有一个执行事务合伙人");
                    }
                }
            }
        } else { // 操作类型为成员
            // 有限合伙企业必须有法人和财务；有限责任企业必须有“执行董事”、“经理”、“监视代表人”、“财务代表人”
            if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(regOrder.getCompanyType())
                    || MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(regOrder.getCompanyType())) {
                // 校验是否添加了法人
                long legalPerson = list.stream().filter(x -> x.getIsLegalPerson() == 1).count();
                if (legalPerson <= 0) {
                    throw new BusinessException("请添加法人！");
                }
                // 校验是否添加了财务
                long finance = list.stream().filter(x -> x.getPersonnelType().contains("4")).count();
                if (finance <= 0) {
                    throw new BusinessException("请添加财务！");
                }
                if (MemberCompanyTypeEnum.LIMITED_LIABILITY.getValue().equals(regOrder.getCompanyType())) {
                    // 校验是否添加了经理
                    long manager = list.stream().filter(x -> x.getPersonnelType().contains("1")).count();
                    if (manager <= 0) {
                        throw new BusinessException("请添加经理！");
                    }
                    // 校验是否添加了监事
                    long Supervisor = list.stream().filter(x -> x.getPersonnelType().contains("2")).count();
                    if (Supervisor <= 0) {
                        throw new BusinessException("请添加监事！");
                    }
                    // 校验是否添加了执行董事
                    long executiveDirector = list.stream().filter(x -> x.getPersonnelType().contains("3")).count();
                    if (executiveDirector <= 0) {
                        throw new BusinessException("请添加执行董事！");
                    }
                }
            }
        }
    }

    @Override
    public List<CompanyCorePersonnelVO> list(Long memberId,Long companyId, String orderNo, int type) {
        List<CompanyCorePersonnelVO> list = Lists.newArrayList();

        // 查询企业成员信息时，查询全部核心成员
        if (type == 0) {
            list = this.getCompanyCorePersonnelByCompanyIdOrOrderNo(companyId, orderNo);
        } else { // 企业注册查询股东/合伙人信息
            // 查询用户注册订单
            RegPreOrderVO regPreOrderVO = memberAccountService.queryRegOrderByOrderNo(orderNo);
            // 注册订单不存在
            if (null == regPreOrderVO) {
                throw new BusinessException("未查询到用户注册订单");
            }

            ObjectUtil.copyListObject(mapper.personnelList(type, memberId, regPreOrderVO.getOrderNo()), list, CompanyCorePersonnelVO.class);
        }

        // 图片处理
        for (CompanyCorePersonnelVO vo : list) {
            if (StringUtil.isNotBlank(vo.getIdCardFront())) {
                vo.setIdCardFrontUrl(ossService.getPrivateImgUrl(vo.getIdCardFront()));
            }
            if (StringUtil.isNotBlank(vo.getIdCardReverse())) {
                vo.setIdCardReverseUrl(ossService.getPrivateImgUrl(vo.getIdCardReverse()));
            }
        }
        return list;
    }

    @Override
    public CompanyCorePersonnelVO detail(Long id) {
        CompanyCorePersonnelVO vo = new CompanyCorePersonnelVO();
        CompanyCorePersonnelEntity entity = this.findById(id);
        if (null == entity) {
            return null;
        }
        BeanUtil.copyProperties(entity, vo);
        if (StringUtil.isNotBlank(entity.getIdCardFront())) {
            String idCardFrontUrl = ossService.getPrivateImgUrl(entity.getIdCardFront());
            vo.setIdCardFrontUrl(idCardFrontUrl);
        }
        if (StringUtil.isNotBlank(entity.getIdCardReverse())) {
            String idCardReverseUrl = ossService.getPrivateImgUrl(entity.getIdCardReverse());
            vo.setIdCardReverseUrl(idCardReverseUrl);
        }
        if (StringUtil.isNotBlank(entity.getBusinessLicense())) {
            String businessLicenseUrl = ossService.getPrivateImgUrl(entity.getBusinessLicense());
            vo.setBusinessLicenseUrl(businessLicenseUrl);
        }

        // 查询委派方
        if (null != vo.getAppointPartyId()) {
            CompanyCorePersonnelEntity personnelEntity = this.findById(vo.getAppointPartyId());
            if (null == personnelEntity) {
                throw new BusinessException("未查询到委派方信息");
            }
            vo.setAppointPartyName(personnelEntity.getPersonnelName());
        }
        return vo;
    }

    @Override
    public List<CompanyCorePersonnelExportVO> getCompanyCorePersonnelByOrderNo(List<String> orderNoList) {
        return mapper.getCompanyCorePersonnelByOrderNo(orderNoList);
    }

    /**
     * 根据企业id或订单号获取企业成员信息
     * @param companyId
     * @param orderNo
     * @return
     */
    @Override
    public List<CompanyCorePersonnelVO> getCompanyCorePersonnelByCompanyIdOrOrderNo(Long companyId,String orderNo) {
        if(companyId == null && StringUtil.isBlank(orderNo) ){
            return null;
        }
        CompanyCorePersonnelEntity entity = new CompanyCorePersonnelEntity();
         if(companyId!=null){
             entity.setCompanyId(companyId);
         }else if (StringUtil.isNotBlank(orderNo)){
             entity.setOrderNo(orderNo);
         }
         List<CompanyCorePersonnelEntity> list = select(entity);
         if(list!=null && list.size()>0){
             List<CompanyCorePersonnelVO> coreList = new ArrayList<>(list.size());
             CompanyCorePersonnelVO vo = null;
             for(CompanyCorePersonnelEntity coreEntity : list){
                 vo = new CompanyCorePersonnelVO();
                 BeanUtil.copyProperties(coreEntity,vo);
                 if(StringUtil.isNotBlank(coreEntity.getIdCardFront())){
                     vo.setIdCardFrontUrl(ossService.getPrivateImgUrl(coreEntity.getIdCardFront()));
                     vo.getImgUrl().add(ossService.getPrivateImgUrl(coreEntity.getIdCardFront()));
                 }
                 if(StringUtil.isNotBlank(coreEntity.getIdCardReverse())){
                     vo.setIdCardReverseUrl(ossService.getPrivateImgUrl(coreEntity.getIdCardReverse()));
                     vo.getImgUrl().add(ossService.getPrivateImgUrl(coreEntity.getIdCardReverse()));
                 }
                 if(StringUtil.isNotBlank(coreEntity.getBusinessLicense())){
                     vo.setBusinessLicenseUrl(ossService.getPrivateImgUrl(coreEntity.getBusinessLicense()));
                     vo.getImgUrl().add(ossService.getPrivateImgUrl(coreEntity.getBusinessLicense()));
                 }
                 //获取企业成员标签列表数据
                 List<String> labelList = getcorePersonnnelLabelList(coreEntity);
                 vo.setLabelList(labelList);
                 coreList.add(vo);
             }
             return coreList;
         }
         return null;
    }

    /**
     * 获取企业成员的成员标签列表
     * @param coreEntity
     * @return
     */
    private List<String> getcorePersonnnelLabelList(CompanyCorePersonnelEntity coreEntity){
        if(coreEntity == null){
            return null;
        }
        List<String> labelsList = new ArrayList<>();
        if(coreEntity.getCompanyType() !=null && coreEntity.getCompanyType()==3){
            if(coreEntity.getIsExecutivePartner() !=null && coreEntity.getIsExecutivePartner() == 1){
                labelsList.add("执行事务合伙人");
            }else{
                if(coreEntity.getPartnerType() !=null && coreEntity.getPartnerType() == 1){
                    labelsList.add("普通合伙人");
                }else if(coreEntity.getPartnerType() !=null && coreEntity.getPartnerType() == 2){
                    labelsList.add("有限合伙人");
                }
            }
        }else {
            if(coreEntity.getIsShareholder()!=null && coreEntity.getIsShareholder() == 1){
                labelsList.add("股东");
            }
        }
        if(coreEntity.getCompanyType()==1 && coreEntity.getIsLegalPerson() != null && coreEntity.getIsLegalPerson() == 1){
            labelsList.add("经营者");
        }else {
            if (coreEntity.getCompanyType()!=1 && coreEntity.getIsLegalPerson() != null && coreEntity.getIsLegalPerson() == 1) {
                labelsList.add("法人");
            }
        }
        if(StringUtil.isNotBlank(coreEntity.getPersonnelType())){
            String[] types = coreEntity.getPersonnelType().split(",");
            for (String t :types){
                if("1".equals(t)){
                    labelsList.add("经理");
                }else if("2".equals(t)){
                    labelsList.add("监事");
                }else if("3".equals(t)){
                    labelsList.add("执行董事");
                }else if("4".equals(t)){
                    labelsList.add("财务");
                }
            }
        }
        return labelsList;
    }

    /**
     * 根据订单号修改核心程序的企业id
     * @param companyId
     * @param orderNo
     * @return
     */
    public void updateCompanyCorePersonnelCompanyIdByOrderNo(Long companyId,String orderNo){
        mapper.updateCompanyCorePersonnelCompanyIdByOrderNo(companyId,orderNo);
    }

    @Override
    public CompanyRegisterOrderVO getCompanyRegOrder(String orderNo) {
        CompanyRegisterOrderVO vo = mapper.getCompanyRegOrder(orderNo);
        if (null == vo) {
            throw new BusinessException("未查询到企业注册订单");
        }
        return vo;
    }

    @Override
    public void updateCompanyRegOrder(UpdateRegOrderDTO dto) {
        mapper.updateCompanyRegOrder(dto);
    }

    @Override
    public List<ShareholderPersonnelListVO> shareholderPersonnelList(Integer isExecutivePartner, String orderNo, String personnelName) {
        if (StringUtil.isBlank(orderNo)) {
            throw new BusinessException("订单编号不能为空");
        }
        return mapper.shareholderPersonnelList(isExecutivePartner, orderNo, personnelName);
    }

    @Override
    public void deletePersonnel(Long id, Long currUserId, int controlType) {
        if (null == id) {
            throw new BusinessException("企业成员id为空");
        }
        CompanyCorePersonnelEntity entity = this.findById(id);
        if (null == entity) {
            throw new BusinessException("未查询到企业核心成员信息");
        }
        if (!Objects.equals(entity.getMemberId(), currUserId)) {
            throw new BusinessException("所选成员不属于当前登录用户");
        }

        // 是否映射
        if (controlType == 0 && (!entity.getPersonnelType().contains("5") || entity.getIsLegalPerson() == 1)) {
            // 已经成为委派人的合伙人不允许删除
            if (MemberCompanyTypeEnum.LIMITED_PARTNER.getValue().equals(entity.getCompanyType())) {
                Example example = new Example(CompanyCorePersonnelEntity.class);
                example.createCriteria().andEqualTo("orderNo", entity.getOrderNo())
                        .andEqualTo("appointPartyId", entity.getId());
                List<CompanyCorePersonnelEntity> list = this.selectByExample(example);
                if (CollectionUtil.isNotEmpty(list)) {
                    throw new BusinessException("已委派法人的执行事务合伙人不能删除");
                }
            }
            // 股东/合伙人有其映射成员，保留其成员数据，去掉股东/合伙人标签
            entity.setIsShareholder(0);
            this.editByIdSelective(entity);
        } else if (controlType == 1 && entity.getIsShareholder() == 1) {
            // 成员有其映射股东/合伙人，保留其股东/合伙人，去掉其职务
            entity.setPersonnelType("5");
            entity.setIsLegalPerson(0);
            this.editByIdSelective(entity);
        } else {
            // 无映射关系时直接删除
            this.delById(id);
        }
    }
}

