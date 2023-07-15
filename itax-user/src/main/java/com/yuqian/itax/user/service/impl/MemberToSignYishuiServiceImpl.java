package com.yuqian.itax.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.itextpdf.text.BadElementException;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.user.dao.MemberToSignYishuiMapper;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberToSignYishuiEntity;
import com.yuqian.itax.user.enums.MemberAuthStatusEnum;
import com.yuqian.itax.user.enums.MemberTypeEnum;
import com.yuqian.itax.user.enums.UserTypeEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberToSignYishuiService;
import com.yuqian.itax.util.util.PDFFileTemplateUtils;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.yishui.entity.*;
import com.yuqian.itax.yishui.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("memberToSignYishuiService")
public class MemberToSignYishuiServiceImpl extends BaseServiceImpl<MemberToSignYishuiEntity, MemberToSignYishuiMapper> implements MemberToSignYishuiService {

    @Autowired
    private UserBankCardService userBankCardService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private OssService ossService;
    @Autowired
    private DictionaryService dictionaryService;
    @Resource
    private YiShuiService yiShuiService;
    @Autowired
    private OemService oemService;

    @Override
    public boolean yishuiSignQuery(Long currUserId, String oemCode) throws BusinessException {
        //校验银行卡信息
        UserBankCardEntity userBankCardEntity = userBankCardService.getBankCardInfoByUserIdAndUserType(currUserId, UserTypeEnum.MEMBER.getValue(), oemCode);
        if (userBankCardEntity == null) {
            throw new BusinessException("请先绑定银行卡");
        }
        //查询当前登录人是否签约
        MemberToSignYishuiEntity userYishuiEntity = new MemberToSignYishuiEntity();
        userYishuiEntity.setUserId(currUserId);
        MemberToSignYishuiEntity yishuiEntity = this.selectOne(userYishuiEntity);
        if (yishuiEntity != null && yishuiEntity.getIsContract() == 1) {
            return true;
        }

        //当前登录人未签约，查询当前身份证下的用户
        MemberAccountEntity memebr = memberAccountService.findById(currUserId);
        if (null == memebr) {
            throw new BusinessException("未查询到用户信息");
        }
        if (!MemberAuthStatusEnum.AUTH_SUCCESS.getValue().equals(memebr.getAuthStatus()) || StringUtil.isBlank(memebr.getIdCardNo())) {
            throw new BusinessException("用户未实名");
        }
        // 获取用户机构信息
        OemEntity oemEntity = oemService.getOem(memebr.getOemCode());
        if (null == oemEntity) {
            throw new BusinessException("未查询到机构信息");
        }
        //查询易税表该身份证已签约的记录
        Example example = new Example(MemberToSignYishuiEntity.class);
        example.createCriteria()
                .andEqualTo("isContract", 1)
                .andEqualTo("idCardNo", memebr.getIdCardNo());
        List<MemberToSignYishuiEntity> entities = this.selectByExample(example);
        if (CollectionUtil.isEmpty(entities)) {
            // 该机构是否配置易税接入参数，未配置接入参数直接报错
            yiShuiService.getYsParamConfig(oemCode, "");
            //返回未签约，让用户去签约
            return false;
        }
        boolean flag = false;
        for (MemberToSignYishuiEntity entity : entities) {
            // 获取机构信息
            OemEntity oem = oemService.getOem(entity.getOemCode());
            if (Objects.equals(oemEntity.getCompanyName(), oem.getCompanyName())) {
                flag = true;
            }
        }
        if (!flag) {
            return false;
        }

        //存在当前身份已签约的账户
        MemberToSignYishuiEntity signedUserYiShui = entities.get(0);
        if (yishuiEntity == null) {
            // 新增前校验是否配置易税接入参数，未配置接入参数直接报错
            yiShuiService.getYsParamConfig(oemCode, "");
            //进行新增
            MemberToSignYishuiEntity insertEntity = new MemberToSignYishuiEntity();
            insertEntity.setUserId(currUserId);
            insertEntity.setEnterpriseProfessionalFacilitatorId(signedUserYiShui.getEnterpriseProfessionalFacilitatorId());
            insertEntity.setProfessionalId(signedUserYiShui.getProfessionalId());
            insertEntity.setProfessionalSn(signedUserYiShui.getProfessionalSn());
            insertEntity.setIdCardNo(memebr.getIdCardNo());
            insertEntity.setIsContract(signedUserYiShui.getIsContract());
            insertEntity.setContractStartTime(signedUserYiShui.getContractStartTime());
            insertEntity.setContractEndTime(signedUserYiShui.getContractEndTime());
            insertEntity.setIsAuth(signedUserYiShui.getIsAuth());
            insertEntity.setAuthTime(signedUserYiShui.getAuthTime());
            insertEntity.setOemCode(signedUserYiShui.getOemCode());
            insertEntity.setCreateTime(new Date());
            insertEntity.setCreateUser(currUserId + "");
            this.insertSelective(insertEntity);
        } else {
            //进行修改
            yishuiEntity.setEnterpriseProfessionalFacilitatorId(signedUserYiShui.getEnterpriseProfessionalFacilitatorId());
            yishuiEntity.setProfessionalId(signedUserYiShui.getProfessionalId());
            yishuiEntity.setProfessionalSn(signedUserYiShui.getProfessionalSn());
            yishuiEntity.setIdCardNo(memebr.getIdCardNo());
            yishuiEntity.setIsContract(signedUserYiShui.getIsContract());
            yishuiEntity.setContractStartTime(signedUserYiShui.getContractStartTime());
            yishuiEntity.setContractEndTime(signedUserYiShui.getContractEndTime());
            yishuiEntity.setIsAuth(signedUserYiShui.getIsAuth());
            yishuiEntity.setAuthTime(signedUserYiShui.getAuthTime());
            yishuiEntity.setUpdateUser(currUserId + "");
            yishuiEntity.setUpdateTime(new Date());
            this.editByIdSelective(yishuiEntity);
        }

        return true;
    }

    /**
     * 校验补全银行卡易税id（已签约情况下）
     *
     * @param userBankCardEntity
     * @param professionalId
     */
    @Override
    public void verifyAndSetBankYiShuiId(UserBankCardEntity userBankCardEntity, Long professionalId) throws BusinessException {
        Long professionalBankId = userBankCardEntity.getProfessionalBankId();
        if (professionalBankId != null) {
            return;
        }
        String token = yiShuiService.getToken(userBankCardEntity.getOemCode());
        YsMerConfig ysMerCfg = yiShuiService.getYsParamConfig(userBankCardEntity.getOemCode(), null);
        //查询该人员的易税银行卡列表
        ProfessionalListReq req = new ProfessionalListReq();
        req.setKeywords(userBankCardEntity.getIdCard());
        ContractInfoResp resp = ProfessionalListService.exec(req, token, ysMerCfg);
        if (!Objects.equals(resp.getCode(), YiShuiBaseResp.SUCCESS)) {
            throw new BusinessException(resp.getMsg());
        }
        List<BankInfoDto> bankList = resp.getBank_lists();
        if (CollectionUtil.isEmpty(bankList)) {
            //新增易税银行卡绑定
            addYishuiBank(userBankCardEntity, professionalId, token, ysMerCfg);
        } else {
            //匹配当前银行卡
            boolean flag = matchBankCard(userBankCardEntity, bankList);
            //未匹配到银行卡，新增易税银行卡绑定
            if (!flag) {
                addYishuiBank(userBankCardEntity, professionalId, token, ysMerCfg);
            }
        }
    }

    /**
     * 匹配当前银行卡，匹配到则进行更新易税银行卡id，匹配不到返回false
     *
     * @param userBankCardEntity
     * @param bankList
     * @return
     */
    public boolean matchBankCard(UserBankCardEntity userBankCardEntity, List<BankInfoDto> bankList) {
        String bankNumber = userBankCardEntity.getBankNumber().trim();
        String back4 = bankNumber.substring(bankNumber.length() - 4);
        String front4 = bankNumber.substring(0, 4);
        for (BankInfoDto bankInfoDto : bankList) {
            String bankCode = bankInfoDto.getBank_code().trim();
            String yishuiBack4 = bankCode.substring(bankCode.length() - 4);
            String yishuiFront4 = bankCode.substring(0, 4);
            if (back4.equals(yishuiBack4) && front4.equals(yishuiFront4)) {
                //将银行卡id绑定进去
                UserBankCardEntity updateUserBankCardEntity = new UserBankCardEntity();
                updateUserBankCardEntity.setId(userBankCardEntity.getId());
                updateUserBankCardEntity.setProfessionalBankId(bankInfoDto.getProfessional_bank_id());
                updateUserBankCardEntity.setUpdateUser(userBankCardEntity.getUserId() + "");
                updateUserBankCardEntity.setUpdateTime(new Date());
                userBankCardService.editByIdSelective(updateUserBankCardEntity);
                return true;
            }
        }
        return false;
    }

    /**
     * 新增易税银行卡绑定
     *
     * @param userBankCardEntity
     * @param professionalId
     * @param token
     * @param ysMerCfg
     */
    public void addYishuiBank(UserBankCardEntity userBankCardEntity, Long professionalId, String token, YsMerConfig ysMerCfg) {
        AddBankReq addBankReq = new AddBankReq();
        addBankReq.setName(userBankCardEntity.getUserName());
        addBankReq.setBank_code(userBankCardEntity.getBankNumber());
        addBankReq.setMobile(userBankCardEntity.getPhone());
        addBankReq.setProfessional_id(professionalId);
        YiShuiBaseResp res = AddBankService.exec(addBankReq, token, ysMerCfg);
        if (!Objects.equals(res.getCode(), YiShuiBaseResp.SUCCESS)) {
            throw new BusinessException(res.getMsg());
        }
        //将银行卡id绑定进去
        UserBankCardEntity updateUserBankCardEntity = new UserBankCardEntity();
        updateUserBankCardEntity.setId(userBankCardEntity.getId());
        updateUserBankCardEntity.setProfessionalBankId(Long.parseLong(res.getData()));
        updateUserBankCardEntity.setUpdateUser(userBankCardEntity.getUserId() + "");
        updateUserBankCardEntity.setUpdateTime(new Date());
        userBankCardService.editByIdSelective(updateUserBankCardEntity);
    }

    @Override
    public void yishuiSign(String fileKey, Long currUserId, String oemCode) throws BusinessException, IOException, BadElementException {
        //签约前置校验
        String head = dictionaryService.getValueByCode("oss_req_head");
        String bucketName = dictionaryService.getValueByCode("oss_publicBucketName");
        String endpoint = dictionaryService.getValueByCode("oss_endpoint");
        String ossFileUrl = head + bucketName + "." + endpoint + "/";
        UserBankCardEntity userBankCardEntity = validateSign(currUserId, ossFileUrl + fileKey, oemCode);
        //获取易税配置
        YsMerConfig ysMerCfg = yiShuiService.getYsParamConfig(oemCode, null);
        //获取易税token
        String token = yiShuiService.getToken(userBankCardEntity.getOemCode());
        //查询本地易税签约记录
        MemberToSignYishuiEntity entity = new MemberToSignYishuiEntity();
        entity.setUserId(currUserId);
        MemberToSignYishuiEntity ySEntity = this.selectOne(entity);
        // 1. 查询在易税签约情况
        ContractListReq req = new ContractListReq();
        req.setKeyword(userBankCardEntity.getIdCard());
        ContractInfoResp resp = ContractListService.exec(req, token, ysMerCfg);
        if (Objects.equals(resp.getCode(), "501")) {
            // 2. 新增（易税查询显示人员信息不存在，进行易税签约）
            addEmployeeUpdateLocal(userBankCardEntity, token, currUserId, ysMerCfg, ySEntity, ossFileUrl, fileKey);
        } else if (!Objects.equals(resp.getCode(), YiShuiBaseResp.SUCCESS)) {
            throw new BusinessException(resp.getMsg());
        } else {
            // 2. 修改（易税查询显示人员信息存在，进行易税人员修改）
            if (resp.getIs_contract() != null && resp.getIs_contract() == 1) {
                // 易税已签约情况下，将签约信息更新到本地库
                yishuiSigned(currUserId, ysMerCfg, resp, ySEntity, userBankCardEntity.getIdCard());
                return;
            } else {
                // 进行易税人员修改
                yishuiUpdate(currUserId, ysMerCfg, ySEntity, resp, userBankCardEntity, token, ossFileUrl, fileKey);
            }
        }
        // 3.再次查询并更新签约信息
        updateLocalSignInfo(currUserId, userBankCardEntity, token, ysMerCfg);
        // 4. 更新签约人银行卡信息
//        this.verifyAndSetBankYiShuiId(userBankCardEntity, professionalId);
    }

    /**
     * 易税查询显示人员信息存在，进行易税人员修改
     *
     * @param currUserId
     * @param ysMerCfg
     * @param yiShuiEntity
     * @param resp
     * @param userBankCardEntity
     * @param token
     * @param ossFileUrl
     * @param fileKey
     */
    private void yishuiUpdate(Long currUserId, YsMerConfig ysMerCfg, MemberToSignYishuiEntity yiShuiEntity, ContractInfoResp resp, UserBankCardEntity userBankCardEntity, String token, String ossFileUrl, String fileKey) throws BadElementException, BusinessException, IOException {
        //未签约情况下，调用人员修改接口
        if (yiShuiEntity == null) {
            log.error("业务处理异常，请稍后再试，currUserId：{}", currUserId);
            throw new BusinessException("签约失败，请重新签约");
        }
        ContractSaveReq saveReq = new ContractSaveReq();
        saveReq.setEnterprise_professional_facilitator_id(yiShuiEntity.getEnterpriseProfessionalFacilitatorId());
        saveReq.setName(userBankCardEntity.getUserName());
        saveReq.setCer_code(userBankCardEntity.getIdCard());
        saveReq.setBank_code(userBankCardEntity.getBankNumber());
        saveReq.setMobile(userBankCardEntity.getPhone());
        saveReq.setSign_img(ossFileUrl);
        //生成pdf
        String signAgreementUrl = getPdfUrl(userBankCardEntity, fileKey);
        saveReq.setProtocol_img(signAgreementUrl);
        saveReq.setContract_img(signAgreementUrl);
        YiShuiBaseResp res = ContractSaveService.exec(saveReq, token, ysMerCfg);
        if (!Objects.equals(res.getCode(), YiShuiBaseResp.SUCCESS)) {
            throw new BusinessException(resp.getMsg());
        }
    }

    /**
     * 签约前置校验
     *
     * @param currUserId
     * @param ossFileUrl
     * @return
     * @throws BusinessException
     */
    private UserBankCardEntity validateSign(Long currUserId, String ossFileUrl, String oemCode) throws BusinessException {
        if (StringUtils.isBlank(ossFileUrl)) {
            throw new BusinessException("签名未找到");
        }
        //签约查询
//        yishuiSignQuery(currUserId);
        // 查询用户银行卡信息
        UserBankCardEntity userBankCardEntity = userBankCardService.getBankCardInfoByUserIdAndUserType(currUserId, MemberTypeEnum.MEMBER.getValue(), oemCode);
        if (userBankCardEntity == null) {
            throw new BusinessException("银行卡信息不存在");
        }
        return userBankCardEntity;
    }

    /**
     * 再次查询并更新签约信息
     *
     * @return
     */
    private void updateLocalSignInfo(Long currUserId, UserBankCardEntity userBankCardEntity, String token, YsMerConfig ysMerCfg) {
        ContractListReq contractListReq = new ContractListReq();
        contractListReq.setKeyword(userBankCardEntity.getIdCard());
        ContractInfoResp resp = ContractListService.exec(contractListReq, token, ysMerCfg);
        if (!Objects.equals(resp.getCode(), YiShuiBaseResp.SUCCESS)) {
            throw new BusinessException(resp.getMsg());
        }
        if (resp.getIs_contract() != null && resp.getIs_contract() == 1) {
            // 易税已签约情况下，将签约信息更新到本地库
            MemberToSignYishuiEntity updateEntity = new MemberToSignYishuiEntity();
            updateEntity.setEnterpriseProfessionalFacilitatorId(resp.getEnterprise_professional_facilitator_id());
            updateEntity.setProfessionalId(resp.getProfessional_id());
            updateEntity.setProfessionalSn(resp.getProfessional_sn());
            updateEntity.setIsContract(resp.getIs_contract());
            if (resp.getContract_start_time() != null) {
                updateEntity.setContractStartTime(DateUtil.date(resp.getContract_start_time()));
            }
            if (resp.getContract_end_time() != null) {
                updateEntity.setContractEndTime(DateUtil.date(resp.getContract_end_time()));
            }
            updateEntity.setIsAuth(resp.getIs_auth());
            updateEntity.setUpdateTime(new Date());
            updateEntity.setUpdateUser(currUserId + "");
            Example example = new Example(MemberToSignYishuiEntity.class);
            example.createCriteria().andEqualTo("userId", currUserId);
            if (mapper.updateByExampleSelective(updateEntity, example) < 1) {
                log.error("签约失败，updateEntity：{}，userId：{}", JSONUtil.toJsonPrettyStr(updateEntity), currUserId);
                throw new BusinessException("签约失败，请重新签约");
            }
        } else {
            // 进行易税人员修改
            log.error("签约失败，resp：{}", JSONUtil.toJsonPrettyStr(resp));
            throw new BusinessException("签约失败，请重新签约");
        }
    }

    /**
     * 易税查询未签约，进行签约，并将签约返回信息更新到本地库
     *
     * @param yishuiEntity
     * @param ossFileUrl
     * @param fileKey
     */
    private void addEmployeeUpdateLocal(UserBankCardEntity userBankCardEntity, String token, Long currUserId, YsMerConfig ysMerCfg, MemberToSignYishuiEntity yishuiEntity, String ossFileUrl, String fileKey) throws BadElementException, BusinessException, IOException {
        AddEmployeeReq addEmployeeReq = new AddEmployeeReq();
        addEmployeeReq.setName(userBankCardEntity.getUserName());
        addEmployeeReq.setCer_code(userBankCardEntity.getIdCard());
        addEmployeeReq.setBank_code(userBankCardEntity.getBankNumber());
        addEmployeeReq.setMobile(userBankCardEntity.getPhone());
        addEmployeeReq.setSign_img(ossFileUrl + fileKey);
        //生成pdf
        String signAgreementUrl = getPdfUrl(userBankCardEntity, fileKey);
        addEmployeeReq.setProtocol_img(ossFileUrl + signAgreementUrl);
        addEmployeeReq.setContract_img(ossFileUrl + signAgreementUrl);
        addEmployeeReq.setAuth("1");
        //调用易税接口添加人员
        AddEmployeeResp resp = AddEmployeeService.exec(addEmployeeReq, token, ysMerCfg);
        if (!Objects.equals(resp.getCode(), YiShuiBaseResp.SUCCESS)) {
            throw new BusinessException("签约易税失败：" + resp.getMsg());
        }
        Long enterpriseProfessionalFacilitatorId = resp.getEnterprise_professional_facilitator_id();
        if (yishuiEntity == null) {
            //当前账户不存在，新增签约记录
            MemberToSignYishuiEntity insertEntity = new MemberToSignYishuiEntity();
            insertEntity.setUserId(currUserId);
            insertEntity.setEnterpriseProfessionalFacilitatorId(enterpriseProfessionalFacilitatorId);
            insertEntity.setProfessionalId(resp.getProfessional_id());
            insertEntity.setProfessionalSn(resp.getProfessional_sn());
            insertEntity.setIdCardNo(userBankCardEntity.getIdCard());
            insertEntity.setIsContract(enterpriseProfessionalFacilitatorId != null ? 1 : 0);
            insertEntity.setContractStartTime(new Date());
            insertEntity.setContractEndTime(new Date());
            insertEntity.setIsAuth(1);
            insertEntity.setOemCode(ysMerCfg.getOemCode());
            insertEntity.setCreateTime(new Date());
            insertEntity.setCreateUser(currUserId + "");
            this.insertSelective(insertEntity);
        } else {
            //本地账户信息存在，更新签约信息
            yishuiEntity.setEnterpriseProfessionalFacilitatorId(enterpriseProfessionalFacilitatorId);
            yishuiEntity.setProfessionalId(resp.getProfessional_id());
            yishuiEntity.setProfessionalSn(resp.getProfessional_sn());
            yishuiEntity.setIdCardNo(userBankCardEntity.getIdCard());
            yishuiEntity.setIsContract(enterpriseProfessionalFacilitatorId != null ? 1 : 0);
            yishuiEntity.setContractStartTime(new Date());
            yishuiEntity.setContractEndTime(new Date());
            yishuiEntity.setIsAuth(1);
            yishuiEntity.setUpdateTime(new Date());
            yishuiEntity.setUpdateUser(currUserId + "");
            this.editByIdSelective(yishuiEntity);
        }
    }

    /**
     * 生成pdf
     *
     * @param userBankCardEntity
     * @param fileKey
     * @return
     * @throws BusinessException
     * @throws IOException
     * @throws BadElementException
     */
    private String getPdfUrl(UserBankCardEntity userBankCardEntity, String fileKey) throws BusinessException, IOException, BadElementException {
        //读取模板
        String yishuiTemplateUrl = dictionaryService.getValueByCode("yishui_template_url");
        if (StringUtil.isBlank(yishuiTemplateUrl)) {
            throw new BusinessException("未配置自由职业者协议模板");
        }
        byte[] templateBytes = ossService.download(yishuiTemplateUrl, "oss_publicBucketName");
        //读取签名图片
        byte[] signImgBytes = ossService.download(fileKey, "oss_publicBucketName");
        //读取易税公章
        String ysPlatOfficialSealUrl = dictionaryService.getValueByCode("ys_plat_official_seal");
        byte[] ysPlatOfficialSeal = ossService.download(ysPlatOfficialSealUrl, "oss_publicBucketName");
        //生成pdf协议
        byte[] pdfBytes;
        try {
            pdfBytes = PDFFileTemplateUtils.signAgreement(templateBytes, signImgBytes, ysPlatOfficialSeal, userBankCardEntity.getPhone(), DateUtil.today(), userBankCardEntity.getUserName(), userBankCardEntity.getIdCard());
        } catch (Exception e) {
            log.info("生成pdf协议失败");
            throw new BusinessException("协议生成失败：" + e.getMessage());
        }
        //上传pdf协议
        String fileName = userBankCardEntity.getIdCard() + "_signAgreement_" + userBankCardEntity.getOemCode();
        String dir = "protocol";
        boolean b = ossService.uploadPublic(dir + "/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + fileName + ".pdf", pdfBytes);
        if (!b) {
            throw new BusinessException("协议上传失败");
        }
        fileName = dir + "/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + fileName + ".pdf";
        return fileName;
    }

    /**
     * 易税签约情况下，将签约信息更新到本地库
     *
     * @param currUserId
     * @param ysMerCfg
     * @param resp
     * @param yishuiEntity
     */
    private void yishuiSigned(Long currUserId, YsMerConfig ysMerCfg, ContractInfoResp resp, MemberToSignYishuiEntity yishuiEntity, String idCardNo) {
        if (yishuiEntity == null) {
            //当前账户不存在，新增签约记录
            MemberToSignYishuiEntity insertEntity = new MemberToSignYishuiEntity();
            insertEntity.setUserId(currUserId);
            insertEntity.setEnterpriseProfessionalFacilitatorId(resp.getEnterprise_professional_facilitator_id());
            insertEntity.setProfessionalId(resp.getProfessional_id());
            insertEntity.setProfessionalSn(resp.getProfessional_sn());
            insertEntity.setIdCardNo(idCardNo);
            insertEntity.setIsContract(resp.getIs_contract());
            if (resp.getContract_start_time() != null) {
                insertEntity.setContractStartTime(DateUtil.date(resp.getContract_start_time()));
            }
            if (resp.getContract_end_time() != null) {
                insertEntity.setContractEndTime(DateUtil.date(resp.getContract_end_time()));
            }
            insertEntity.setIsAuth(resp.getIs_auth());
            insertEntity.setOemCode(ysMerCfg.getOemCode());
            insertEntity.setCreateTime(new Date());
            insertEntity.setCreateUser(currUserId + "");
            this.insertSelective(insertEntity);
        } else {
            //本地账户信息存在，更新签约信息
            yishuiEntity.setEnterpriseProfessionalFacilitatorId(resp.getEnterprise_professional_facilitator_id());
            yishuiEntity.setProfessionalId(resp.getProfessional_id());
            yishuiEntity.setProfessionalSn(resp.getProfessional_sn());
            yishuiEntity.setIdCardNo(idCardNo);
            yishuiEntity.setIsContract(resp.getIs_contract());
            if (resp.getContract_start_time() != null) {
                yishuiEntity.setContractStartTime(DateUtil.date(resp.getContract_start_time()));
            }
            if (resp.getContract_end_time() != null) {
                yishuiEntity.setContractEndTime(DateUtil.date(resp.getContract_end_time()));
            }
            yishuiEntity.setIsAuth(resp.getIs_auth());
            yishuiEntity.setUpdateTime(new Date());
            yishuiEntity.setUpdateUser(currUserId + "");
            this.editByIdSelective(yishuiEntity);
        }
    }
}

