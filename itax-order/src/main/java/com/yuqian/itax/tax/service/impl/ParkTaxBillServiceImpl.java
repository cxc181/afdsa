package com.yuqian.itax.tax.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.entity.NoticeManageEntity;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.message.service.NoticeManageService;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.order.service.InvoiceOrderService;
import com.yuqian.itax.park.entity.ParkEntity;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.park.entity.TaxRulesConfigEntity;
import com.yuqian.itax.park.entity.vo.TaxRulesConfigVO;
import com.yuqian.itax.park.enums.IncomeLevyTypeEnum;
import com.yuqian.itax.park.service.ParkService;
import com.yuqian.itax.park.service.TaxPolicyService;
import com.yuqian.itax.park.service.TaxRulesConfigService;
import com.yuqian.itax.system.entity.BusinessIncomeRuleEntity;
import com.yuqian.itax.system.service.BusinessIncomeRuleService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.tax.dao.ParkTaxBillMapper;
import com.yuqian.itax.tax.entity.*;
import com.yuqian.itax.tax.entity.query.*;
import com.yuqian.itax.tax.entity.vo.*;
import com.yuqian.itax.tax.enums.ParkTaxBillStatusEnum;
import com.yuqian.itax.tax.enums.TaxBillStatusEnum;
import com.yuqian.itax.tax.enums.VouchersStatusEnum;
import com.yuqian.itax.tax.enums.VouchersUpStatusEnum;
import com.yuqian.itax.tax.service.*;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.enums.CompanyTaxPayerTypeEnum;
import com.yuqian.itax.user.enums.MemberCompanyTypeEnum;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.user.service.UserService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Service("parkTaxBillService")
@Slf4j
public class ParkTaxBillServiceImpl extends BaseServiceImpl<TParkTaxBillEntity, ParkTaxBillMapper> implements ParkTaxBillService {

    @Autowired
    CompanyTaxBillService companyTaxBillService;
    @Autowired
    SmsService smsService;
    @Autowired
    MemberCompanyService memberCompanyService;
    @Autowired
    MemberAccountService memberAccountService;
    @Autowired
    ParkService parkService;
    @Autowired
    NoticeManageService manageService;
    @Resource
    InvoiceOrderService invoiceOrderService;
    @Autowired
    DictionaryService dictionaryService;
    @Autowired
    OssService ossService;
    @Autowired
    UserService userService;
    @Autowired
    TaxRulesConfigService taxRulesConfigService;
    @Autowired
    ParkTaxBillFileRecordService parkTaxBillFileRecordService;

    @Autowired
    TaxPolicyService taxPolicyService;
    @Autowired
    BusinessIncomeRuleService businessIncomeRuleService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    TaxBillCredentialsRecordService taxBillCredentialsRecordService;
    @Resource
    ParkTaxBillChangeService parkTaxBillChangeService;
    @Autowired
    private MessageNoticeService messageNoticeService;
    @Autowired
    private  CompanyTaxBillChangeService companyTaxBillChangeService;
    @Autowired
    private OemService oemService;

    @Override
    public List<ParkTaxBillXXJOBVO> queryParkTaxBillByTime(ParkTaxBillQuery query) {
        return mapper.queryParkTaxBillByTime(query);
    }

    @Override
    public List<ParkTaxBillXXJOBVO> queryParkTaxBillByCompanyTaxBill(ParkTaxBillQuery query) {
        return mapper.queryParkTaxBillByCompanyTaxBill(query);
    }


    @Override
    public void confirmation(Long id,String account) {
        TParkTaxBillEntity tParkTaxBillEntity=mapper.selectByPrimaryKey(id);
        //修改企业状态
        List<CompanyTaxBillEntity> list =companyTaxBillService.queryCompanyTaxBillByParkTaxBillId(tParkTaxBillEntity.getId());
        int shouldUploadVatVouchersCompanyNumber=0;
        int shouldUploadIitVouchersCompanyNumber=0;
        for (CompanyTaxBillEntity co:list) {
            if(co.getVatShouldTaxMoney()>0){
                shouldUploadVatVouchersCompanyNumber++;
            }
            if(co.getIncomeShouldTaxMoney()>0){
                shouldUploadIitVouchersCompanyNumber++;
            }
            co.setAffirmTime(new Date());
            co.setUpdateTime(new Date());
            co.setUpdateUser(account);
            companyTaxBillService.editByIdSelective(co);
            CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
            BeanUtils.copyProperties(co,companyTaxBillChangeEntity);
            companyTaxBillChangeEntity.setId(null);
            companyTaxBillChangeEntity.setCompanyTaxBillId(co.getId());
            companyTaxBillChangeEntity.setDescrip("税单确认");
            companyTaxBillChangeEntity.setAddTime(new Date());
            companyTaxBillChangeEntity.setAddUser(account);
            companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
        }

        //修改园区状态
        tParkTaxBillEntity.setShouldUploadVatVouchersCompanyNumber(shouldUploadVatVouchersCompanyNumber);
        tParkTaxBillEntity.setShouldUploadIitVouchersCompanyNumber(shouldUploadIitVouchersCompanyNumber);
        tParkTaxBillEntity.setTaxBillStatus(ParkTaxBillStatusEnum.TAX_REFUNDED.getValue());
        tParkTaxBillEntity.setUpdateTime(new Date());
        tParkTaxBillEntity.setUpdateUser(account);
        mapper.updateByPrimaryKey(tParkTaxBillEntity);
        //增加历史记录
        ParkTaxBillChangeEntity parkTaxBillChangeEntity=new ParkTaxBillChangeEntity();
        BeanUtils.copyProperties(tParkTaxBillEntity,parkTaxBillChangeEntity);
        parkTaxBillChangeEntity.setId(null);
        parkTaxBillChangeEntity.setParkBillsId(tParkTaxBillEntity.getId());
        parkTaxBillChangeEntity.setRemark("操作立即确认");
        parkTaxBillChangeEntity.setAddTime(new Date());
        parkTaxBillChangeEntity.setAddUser(account);
        parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(Long id, String account) {
        TParkTaxBillEntity tParkTaxBillEntity=mapper.selectByPrimaryKey(id);
        //修改企业状态
        List<CompanyTaxBillEntity> list =companyTaxBillService.queryCompanyTaxBillByParkTaxBillId(tParkTaxBillEntity.getId());
        if (tParkTaxBillEntity.getIncomeLevyType() != null && tParkTaxBillEntity.getIncomeLevyType().equals(2)){
            for (CompanyTaxBillEntity co:list) {
                if(co.getRecoverableTaxMoney()>0){
                    co.setTaxBillStatus(TaxBillStatusEnum.TAX_TO_BE_REFUNDED.getValue());
                }
                if(co.getSupplementTaxMoney()>0){
                    co.setTaxBillStatus(TaxBillStatusEnum.TAX_TO_BE_PAID.getValue());
                }
                if(co.getRecoverableTaxMoney()==0 &&co.getSupplementTaxMoney()==0){
                    co.setTaxBillStatus(TaxBillStatusEnum.NORMAL.getValue());
                    co.setCompleteTime(new Date());
                }
                if(co.getTaxBillStatus().equals(TaxBillStatusEnum.NORMAL.getValue())){
                    co.setCompleteTime(new Date());
                }
                co.setAffirmTime(new Date());//推送时间
                co.setUpdateTime(new Date());
                co.setUpdateUser(account);
                companyTaxBillService.editByIdSelective(co);
                CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
                BeanUtils.copyProperties(co,companyTaxBillChangeEntity);
                companyTaxBillChangeEntity.setId(null);
                companyTaxBillChangeEntity.setCompanyTaxBillId(co.getId());
                companyTaxBillChangeEntity.setDescrip("税单推送");
                companyTaxBillChangeEntity.setAddTime(new Date());
                companyTaxBillChangeEntity.setAddUser(account);
                companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
            }
            //修改园区状态
            tParkTaxBillEntity.setTaxBillStatus(ParkTaxBillStatusEnum.PUSH.getValue());
        }else {
            for (CompanyTaxBillEntity co:list) {
                // 计算增值税、附加税
//                Map<String, Object> map = invoiceOrderService.taxCalculation(co.getCompanyId(), 2, null, null, co.getTaxBillSeasonal(), co.getTaxBillYear(), 1);
//                BeanUtil.copyProperties(map, co);
                co.setTaxBillStatus(TaxBillStatusEnum.TO_BE_WRITE_COST.getValue());
                co.setAffirmTime(new Date());//推送时间
                co.setUpdateTime(new Date());
                co.setUpdateUser(account);
                companyTaxBillService.editByIdSelective(co);
                CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
                BeanUtils.copyProperties(co,companyTaxBillChangeEntity);
                companyTaxBillChangeEntity.setId(null);
                companyTaxBillChangeEntity.setCompanyTaxBillId(co.getId());
                companyTaxBillChangeEntity.setDescrip("税单推送");
                companyTaxBillChangeEntity.setAddTime(new Date());
                companyTaxBillChangeEntity.setAddUser(account);
                companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
            }
            //增加历史记录
            ParkTaxBillChangeEntity parkTaxBillChangeEntity=new ParkTaxBillChangeEntity();
            tParkTaxBillEntity.setTaxBillStatus(ParkTaxBillStatusEnum.PUSH.getValue());
            BeanUtils.copyProperties(tParkTaxBillEntity,parkTaxBillChangeEntity);
            parkTaxBillChangeEntity.setAddTime(new Date());
            parkTaxBillChangeEntity.setAddUser(account);
            parkTaxBillChangeEntity.setId(null);
            parkTaxBillChangeEntity.setParkBillsId(tParkTaxBillEntity.getId());
            parkTaxBillChangeEntity.setRemark("已推送");
            parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
            tParkTaxBillEntity.setUpdateTime(new Date());
            tParkTaxBillEntity.setUpdateUser(account);
            mapper.updateByPrimaryKey(tParkTaxBillEntity);
            //修改园区状态
            tParkTaxBillEntity.setTaxBillStatus(ParkTaxBillStatusEnum.PENDING.getValue());
        }
        //发送消息
        if (tParkTaxBillEntity != null && tParkTaxBillEntity.getIncomeLevyType() != null && tParkTaxBillEntity.getIncomeLevyType().equals(1)  ){
            sendSmsAndNotice(id,account);
        }else{
            sendSmsAndNoticeByApproved(id,account);
        }
        tParkTaxBillEntity.setUpdateTime(new Date());
        tParkTaxBillEntity.setUpdateUser(account);
        mapper.updateByPrimaryKey(tParkTaxBillEntity);
        //增加历史记录
        ParkTaxBillChangeEntity parkTaxBillChangeEntity=new ParkTaxBillChangeEntity();

        BeanUtils.copyProperties(tParkTaxBillEntity,parkTaxBillChangeEntity);
        parkTaxBillChangeEntity.setAddTime(new Date());
        parkTaxBillChangeEntity.setAddUser(account);
        parkTaxBillChangeEntity.setId(null);
        parkTaxBillChangeEntity.setParkBillsId(tParkTaxBillEntity.getId());
        parkTaxBillChangeEntity.setRemark("mq自动推送");
        parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
        tParkTaxBillEntity.setUpdateTime(new Date());
        tParkTaxBillEntity.setUpdateUser(account);
        mapper.updateByPrimaryKey(tParkTaxBillEntity);
    }

    @Override
    public PageInfo<ParkTaxBillVO> queryParkTaxBillPageInfo(ParkTaxBillQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(mapper.queryParkTaxBillPageInfo(query));
    }

    @Override
    public List<ParkTaxBillVO> queryParkTaxBillList(ParkTaxBillQuery query) {
        return mapper.queryParkTaxBillPageInfo(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mq(JSONObject json) {
        int taxBillYear=json.getIntValue("taxBillYear");
        int taxBillSeasonal=json.getIntValue("taxBillSeasonal");
        String account=json.getString("account");
        Long parkId=json.getLong("parkId");
        Long parkTaxBill=json.getLong("parkTaxBill");
        String fileUrl=json.getString("fileUrl");


        List<ParkTaxBillUploadVO> list= JSONObject.parseArray(json.getJSONArray("list").toJSONString(),ParkTaxBillUploadVO.class);
        try {
            ParkEntity parkEntity=parkService.findById(parkId);
            if(parkEntity==null){
                log.info("园区不存在");
                throw  new BusinessException("园区不存在");
            }
            List<ParkTaxBillUploadVO> failed = Lists.newArrayList();
            //上传企业数
            int uploadingCompanyNumber=0;
            TParkTaxBillEntity tParkTaxBillEntity=mapper.selectByPrimaryKey(parkTaxBill);
            uploadingCompanyNumber=tParkTaxBillEntity.getUploadingCompanyNumber();
            SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddHHmmss");
            String groupOrderNo=sdf.format(new Date());
            for (ParkTaxBillUploadVO vo : list) {
                vo.setBatchNumber(groupOrderNo);
                if (StringUtils.isBlank(vo.getCompanyName())) {
                    vo.setFailed("公司名称不能为空");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }
                if (StringUtils.isBlank(vo.getEin())) {
                    vo.setFailed("税号不能为空");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }
                if (vo.getInvoiceAmount()==null) {
                    vo.setFailed("本季开票金额不能为空");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }
                if (vo.getShouldTaxMoney()==null) {
                    vo.setFailed("总税费不能为空");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }
                if (vo.getVatShouldTaxMoney()==null) {
                    vo.setFailed("增值税不能为空");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }
                if (vo.getAdditionalShouldTaxMoney()==null) {
                    vo.setFailed("附加税不能为空");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    failed.add(vo);
                    continue;
                }
                if (vo.getIncomeShouldTaxMoney()==null) {
                    vo.setFailed("所得税不能为空");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }

                List<Long> entityList=memberCompanyService.queryMemberCompanyIdByEin(vo.getEin().trim());
                if(CollectionUtil.isEmpty(entityList)){
                    vo.setFailed(vo.getEin().trim()+"税号没查询到企业,或者企业状态不正常");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }

                //根据税号查询企业税单
                CompanyTaxBillEntity companyTaxBillEntity=companyTaxBillService.queryCompanyTaxBillByEin(vo.getEin().trim(),taxBillSeasonal,taxBillYear, vo.getCompanyId());
                if(companyTaxBillEntity==null){
                    vo.setFailed("该企业本期没有税单，请确认");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }
/*                if (vo.getCancellationAmount() !=null &&  !companyTaxBillEntity.getCancellationAmount().equals(vo.getCancellationAmount().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue())){
                    vo.setFailed("该企业作废/红冲金额不一致，请确认");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }*/
                MemberCompanyEntity memberCompanyEntity =memberCompanyService.findById(companyTaxBillEntity.getCompanyId());


                if(memberCompanyEntity==null){
                    vo.setFailed("企业不存在，请确认");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }

                if(!memberCompanyEntity.getParkId().equals(parkId)){
                    vo.setFailed("上传企业不属于当前园区税单，请确认");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }

              /*  if(memberCompanyEntity.getStatus()==4&&companyTaxBillEntity.getAlreadyTaxMoney().equals(vo.getInvoiceAmount().multiply( new BigDecimal(100)).longValue())){
                    vo.setFailed("已注销企业应缴和已缴税费须一致");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }*/

                if(!ObjectUtils.equals(memberCompanyEntity.getCompanyName(),vo.getCompanyName())){
                    vo.setFailed("企业名称和文件的企业名称不一致，请确认");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }
                if(!companyTaxBillEntity.getInvoiceMoney().equals(vo.getInvoiceAmount().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue())){
                    vo.setFailed("该企业本季开票金额和文件本季不一致，请确认");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }
                if (!(vo.getShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP))
                            .equals((vo.getVatShouldTaxMoney().add(vo.getAdditionalShouldTaxMoney()).add(vo.getIncomeShouldTaxMoney())).setScale(2,BigDecimal.ROUND_HALF_UP))) {
                    vo.setFailed("数据有误！总税费不等于各税费之和");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }

                // 修改企业税单相关数据(应缴已上传数据为准，税率取系统配置，应退应补计算所得)
                companyTaxBillEntity.setShouldTaxMoney(vo.getShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue());
                if(companyTaxBillEntity.getAlreadyTaxMoney()-vo.getShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue()>0){
                    companyTaxBillEntity.setRecoverableTaxMoney(companyTaxBillEntity.getAlreadyTaxMoney()-vo.getShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue());
                    companyTaxBillEntity.setSupplementTaxMoney(0L);
                }else{
                    companyTaxBillEntity.setSupplementTaxMoney(vo.getShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue()-companyTaxBillEntity.getAlreadyTaxMoney());
                    companyTaxBillEntity.setRecoverableTaxMoney(0L);
                }
                //增值税
                Long vatTaxableIncomeAmount=companyTaxBillEntity.getInvoiceMoney()-vo.getVatShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue();
                companyTaxBillEntity.setVatTaxableIncomeAmount(vatTaxableIncomeAmount);//增值税应纳税所得额=开票金额-增值税
                //查出减免额度
                TaxPolicyEntity taxPolicyEntity=taxPolicyService.queryTaxPolicyByParkId(companyTaxBillEntity.getParkId(),memberCompanyEntity.getCompanyType(),memberCompanyEntity.getTaxpayerType());
              /*  if(companyTaxBillEntity.getInvoiceMoney()<taxPolicyEntity.getVatBreaksAmount()){
                    companyTaxBillEntity.setVatRate(new BigDecimal(0));
                }else{

                }*/
                List<TaxRulesConfigVO> vat=taxRulesConfigService.queryTaxRulesConfigMinRate(companyTaxBillEntity.getParkId(),memberCompanyEntity.getCompanyType(),2);
                if(vat.get(0)==null){
                    vo.setFailed("找不到园区增值税税率，请确认");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    failed.add(vo);
                    continue;
                }
                companyTaxBillEntity.setVatRate(vat.get(0).getRate());
                companyTaxBillEntity.setVatShouldTaxMoney(vo.getVatShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue());
                if(companyTaxBillEntity.getVatAlreadyTaxMoney()-vo.getVatShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue()>0){
                    companyTaxBillEntity.setVatSupplementTaxMoney(0L);
                    companyTaxBillEntity.setVatRecoverableTaxMoney(companyTaxBillEntity.getVatAlreadyTaxMoney()-vo.getVatShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue());
                }else {
                    companyTaxBillEntity.setVatSupplementTaxMoney(vo.getVatShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue()-companyTaxBillEntity.getVatAlreadyTaxMoney());
                    companyTaxBillEntity.setVatRecoverableTaxMoney(0L);

                }
                //附加税
                companyTaxBillEntity.setAdditionalTaxableIncomeAmount(vo.getVatShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue());//附加税应纳税所得额=增值税
                companyTaxBillEntity.setAdditionalShouldTaxMoney(vo.getAdditionalShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue());
                List<TaxRulesConfigVO> addit=taxRulesConfigService.queryTaxRulesConfigMinRate(companyTaxBillEntity.getParkId(),memberCompanyEntity.getCompanyType(),3);
                if(addit.get(0)==null){
                    vo.setFailed("找不到园区附加税税率，请确认");
                    vo.setFileUrl(fileUrl);
                    vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                    vo.setParkTaxBill(parkTaxBill);
                    failed.add(vo);
                    continue;
                }
                companyTaxBillEntity.setAdditionalRate(addit.get(0).getRate());                //修改园区税单得值
                if(companyTaxBillEntity.getAdditionalAlreadyTaxMoney()-vo.getAdditionalShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue()>0){
                    companyTaxBillEntity.setAdditionalRecoverableTaxMoney(companyTaxBillEntity.getAdditionalAlreadyTaxMoney()-vo.getAdditionalShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue());
                    companyTaxBillEntity.setAdditionalSupplementTaxMoney(0L);
                }else {
                    companyTaxBillEntity.setAdditionalSupplementTaxMoney(vo.getAdditionalShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue()-companyTaxBillEntity.getAdditionalAlreadyTaxMoney());
                    companyTaxBillEntity.setAdditionalRecoverableTaxMoney(0L);
                }
                //所得税
                //查看列外行业
                TaxRulesConfigEntity tax=null;
                if(memberCompanyEntity.getIndustryId()!=null){
                    tax=taxRulesConfigService.queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount(1,companyTaxBillEntity.getInvoiceMoney(),companyTaxBillEntity.getParkId(),memberCompanyEntity.getCompanyType(),memberCompanyEntity.getIndustryId());
                }
                if(tax==null){
                    TaxRulesConfigEntity income=taxRulesConfigService.queryTaxRulesConfigEntityByTaxTypeAndInoviceAmount(1,companyTaxBillEntity.getInvoiceMoney(),companyTaxBillEntity.getParkId(),memberCompanyEntity.getCompanyType(),null);
                    companyTaxBillEntity.setIncomeLevyWay(taxPolicyEntity.getLevyWay());
                    if(taxPolicyEntity.getLevyWay()==2){
                        //本年累计开票金额
                        Long  totalYearInvoiceAmount =invoiceOrderService.queryTotaLInvoiceAmountByIit(taxBillYear,null,parkId,null,companyTaxBillEntity.getCompanyId());
                        //本年累计应纳税所得额
                        Long iitShouldNsAmountYear=0L;
                        //本年累计应缴增值税
                        Long totalYearVatShouldAmount=0L;
                        CompanyTaxBillQueryAdmin queryAdmin=new CompanyTaxBillQueryAdmin();
                        queryAdmin.setTaxBillYear(companyTaxBillEntity.getTaxBillYear());
                        Map<String,Object> map=companyTaxBillService.queryCompanyTaxBillTotalVatIiTfJByTime(queryAdmin);
                        totalYearVatShouldAmount= vo.getVatShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).add((BigDecimal) map.get("vat_total_should_amount")).longValue() ;
                        //计算本年度纳税所得额
                        iitShouldNsAmountYear=new BigDecimal(totalYearInvoiceAmount-totalYearVatShouldAmount).multiply(income.getRate().divide(new BigDecimal(100))).setScale(0,BigDecimal.ROUND_UP).longValue();

                        companyTaxBillEntity.setIncomeTaxableIncomeAmount(iitShouldNsAmountYear);//所得税税应纳税所得额=
                        BusinessIncomeRuleEntity businessIncomeRuleEntity=businessIncomeRuleService.queryBusinessIncomeRuleByAmount(iitShouldNsAmountYear);
                        if(income==null){
                            vo.setFailed("找不到园区所得税税率，请确认");
                            vo.setFileUrl(fileUrl);
                            vo.setParkTaxBill(parkTaxBill);
                            vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                            failed.add(vo);
                            continue;
                        }
                        companyTaxBillEntity.setIncomeRate(businessIncomeRuleEntity.getRate());
                        companyTaxBillEntity.setTaxableIncomeRate(income.getRate());
                    }
                    if(taxPolicyEntity.getLevyWay()==1){
                        companyTaxBillEntity.setIncomeTaxableIncomeAmount(companyTaxBillEntity.getInvoiceMoney()-vo.getVatShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue());//所得税应纳税所得额=增值税应纳税所得额
                        if(companyTaxBillEntity.getInvoiceMoney()<taxPolicyEntity.getIncomeTaxBreaksAmount()){
                            companyTaxBillEntity.setIncomeRate(new BigDecimal(0));
                        }else{
                            companyTaxBillEntity.setIncomeRate(income.getRate());
                        }
                    }
                }else{
                /*    if(tax==null){
                        vo.setFailed("找不到园区所得税税率，请确认");
                        vo.setFileUrl(fileUrl);
                        vo.setParkTaxBill(parkTaxBill);
                        vo.setStatus(1);
                        failed.add(vo);
                        continue;
                    }*/
                    //本年累计开票金额
                    Long totalYearInvoiceAmount =invoiceOrderService.queryTotaLInvoiceAmountByIit(taxBillYear,null,parkId,null,companyTaxBillEntity.getCompanyId());
                    //本年累计应纳税所得额
                    Long iitShouldNsAmountYear=0L;
                    //本年累计应缴增值税
                    Long totalYearVatShouldAmount=0L;
                    CompanyTaxBillQueryAdmin queryAdmin=new CompanyTaxBillQueryAdmin();
                    queryAdmin.setTaxBillYear(companyTaxBillEntity.getTaxBillYear());
                    Map<String,Object> map=companyTaxBillService.queryCompanyTaxBillTotalVatIiTfJByTime(queryAdmin);
                    totalYearVatShouldAmount= vo.getVatShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).add((BigDecimal) map.get("vat_total_should_amount")).longValue() ;
                    //计算本年度纳税所得额
                    iitShouldNsAmountYear=new BigDecimal(totalYearInvoiceAmount-totalYearVatShouldAmount).multiply(tax.getRate().divide(new BigDecimal(100))).setScale(0,BigDecimal.ROUND_UP).longValue();

                    companyTaxBillEntity.setIncomeLevyWay(taxPolicyEntity.getLevyWay());
                    if(taxPolicyEntity.getLevyWay()==2){

                        BusinessIncomeRuleEntity businessIncomeRuleEntity=businessIncomeRuleService.queryBusinessIncomeRuleByAmount(iitShouldNsAmountYear);
                        companyTaxBillEntity.setIncomeTaxableIncomeAmount(iitShouldNsAmountYear);//附加税应纳税所得额=增值税应纳税所得额
                        companyTaxBillEntity.setIncomeRate(businessIncomeRuleEntity.getRate());
                        companyTaxBillEntity.setTaxableIncomeRate(tax.getRate());
                    }
                    if(taxPolicyEntity.getLevyWay()==1){
                        companyTaxBillEntity.setIncomeTaxableIncomeAmount(vatTaxableIncomeAmount);
                        if(companyTaxBillEntity.getInvoiceMoney()<taxPolicyEntity.getIncomeTaxBreaksAmount()){
                            companyTaxBillEntity.setIncomeRate(new BigDecimal(0));
                        }else{
                            companyTaxBillEntity.setIncomeRate(tax.getRate());
                        }
                    }
                }

                companyTaxBillEntity.setIncomeShouldTaxMoney(vo.getIncomeShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue());

                if(companyTaxBillEntity.getIncomeAlreadyTaxMoney()-vo.getIncomeShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue()>0){
                    companyTaxBillEntity.setIncomeRecoverableTaxMoney(companyTaxBillEntity.getIncomeAlreadyTaxMoney()-vo.getIncomeShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue());
                    companyTaxBillEntity.setIncomeSupplementTaxMoney(0L);
                }else {
                    companyTaxBillEntity.setIncomeSupplementTaxMoney(vo.getIncomeShouldTaxMoney().setScale(2,BigDecimal.ROUND_HALF_UP).multiply( new BigDecimal(100)).longValue()-companyTaxBillEntity.getIncomeAlreadyTaxMoney());
                    companyTaxBillEntity.setIncomeRecoverableTaxMoney(0L);
                }
               /* Long  re= (companyTaxBillEntity.getVatSupplementTaxMoney()==null?0L:companyTaxBillEntity.getVatSupplementTaxMoney())
                        +(companyTaxBillEntity.getAdditionalSupplementTaxMoney()==null?0:companyTaxBillEntity.getAdditionalSupplementTaxMoney())
                        +(companyTaxBillEntity.getIncomeSupplementTaxMoney()==null?0:companyTaxBillEntity.getIncomeSupplementTaxMoney())
                        -(companyTaxBillEntity.getVatRecoverableTaxMoney()==null?0:companyTaxBillEntity.getVatRecoverableTaxMoney())
                        -(companyTaxBillEntity.getAdditionalRecoverableTaxMoney()==null?0:companyTaxBillEntity.getAdditionalRecoverableTaxMoney())
                        -(companyTaxBillEntity.getIncomeRecoverableTaxMoney()==null?0:companyTaxBillEntity.getIncomeRecoverableTaxMoney());
                if(re>0){
                    companyTaxBillEntity.setTaxBillStatus(2);
                }
                if(re <0){
                    companyTaxBillEntity.setTaxBillStatus(1);
                }
                if(re==0){
                    companyTaxBillEntity.setTaxBillStatus(3);
                }*/
               //设置凭证状态
                if(companyTaxBillEntity.getVatShouldTaxMoney()>0){
                    companyTaxBillEntity.setVatVouchersStatus(VouchersUpStatusEnum.VOUCHERS_TO_BE_REFUNDED.getValue());
                }else{
                    companyTaxBillEntity.setVatVouchersStatus(VouchersUpStatusEnum.VOUCHERS_REFUNDED.getValue());
                }
                if(companyTaxBillEntity.getIncomeShouldTaxMoney()>0){
                    companyTaxBillEntity.setIitVouchersStatus(VouchersUpStatusEnum.VOUCHERS_TO_BE_REFUNDED.getValue());
                }else{
                    companyTaxBillEntity.setIitVouchersStatus(VouchersUpStatusEnum.VOUCHERS_REFUNDED.getValue());
                }
                companyTaxBillEntity.setUpdateTime(new Date());
                companyTaxBillEntity.setUpdateUser("上传者:"+account);
                companyTaxBillEntity.setRemark(vo.getRemark());
                companyTaxBillService.editByIdSelective(companyTaxBillEntity);
                // 企业税单历史记录
                CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
                BeanUtils.copyProperties(companyTaxBillEntity,companyTaxBillChangeEntity);
                companyTaxBillChangeEntity.setId(null);
                companyTaxBillChangeEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
                companyTaxBillChangeEntity.setDescrip("修改税单");
                companyTaxBillChangeEntity.setAddTime(new Date());
                companyTaxBillChangeEntity.setAddUser(account);
                companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);
                ParkTaxBillFileRecordEntity entity=new ParkTaxBillFileRecordEntity();
                entity.setCompanyId(memberCompanyEntity.getId());
                entity.setParkTaxBill(parkTaxBill);
                entity.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                if(parkTaxBillFileRecordService.select(entity).size()<1){
                    uploadingCompanyNumber=uploadingCompanyNumber+1;
                }
                //
                vo.setStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
                vo.setFileUrl(fileUrl);
                vo.setCompanyId(memberCompanyEntity.getId());
                vo.setParkTaxBill(companyTaxBillEntity.getParkTaxBillId());
                vo.setFailed("解析成功");
                failed.add(vo);
            }
            //修改园区税单表数据
            Long shouldTaxMoneyTotal =companyTaxBillService.queryShouldCompanyByParkTaxBillId(parkTaxBill);
            tParkTaxBillEntity.setUploadingCompanyNumber(uploadingCompanyNumber);
            tParkTaxBillEntity.setShouldTaxMoney(shouldTaxMoneyTotal);
            Long a=tParkTaxBillEntity.getAlreadyTaxMoney()-shouldTaxMoneyTotal;
            if(a>0){
                tParkTaxBillEntity.setRecoverableTaxMoney(a);
                tParkTaxBillEntity.setSupplementTaxMoney(0L);
            }
            if(a<0){
                tParkTaxBillEntity.setRecoverableTaxMoney(0L);
                tParkTaxBillEntity.setSupplementTaxMoney(shouldTaxMoneyTotal-tParkTaxBillEntity.getAlreadyTaxMoney());
            }
            if (a==0){
                tParkTaxBillEntity.setSupplementTaxMoney(0L);
                tParkTaxBillEntity.setRecoverableTaxMoney(0L);
            }
            tParkTaxBillEntity.setCurFileUrl(fileUrl);
            tParkTaxBillEntity.setUpdateTime(new Date());
            tParkTaxBillEntity.setUpdateUser(account);
            tParkTaxBillEntity.setTaxBillStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
            mapper.updateByPrimaryKey(tParkTaxBillEntity);
            //增加历史记录
            ParkTaxBillChangeEntity parkTaxBillChangeEntity=new ParkTaxBillChangeEntity();
            BeanUtils.copyProperties(tParkTaxBillEntity,parkTaxBillChangeEntity);
            parkTaxBillChangeEntity.setId(null);
            parkTaxBillChangeEntity.setParkBillsId(tParkTaxBillEntity.getId());
            parkTaxBillChangeEntity.setAddUser(account);
            parkTaxBillChangeEntity.setAddTime(new Date());
            parkTaxBillChangeEntity.setRemark("操作修改税单");
            parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
            //批量插入解析记录表
            parkTaxBillFileRecordService.addBatch(failed,account);
        } catch (Exception e) {
            //修改状态为待上传
            ParkTaxBillQuery query=new ParkTaxBillQuery();
            query.setTaxBillYear(taxBillYear);
            query.setTaxBillSeasonal(taxBillSeasonal);
            query.setParkId(parkId);
            List<ParkTaxBillVO> voList=mapper.queryParkTaxBillPageInfo(query);
            TParkTaxBillEntity tParkTaxBillEntity=mapper.selectByPrimaryKey(voList.get(0).getId());
            tParkTaxBillEntity.setTaxBillStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
            mapper.updateByPrimaryKey(tParkTaxBillEntity);
            log.error("上传文件异常：" + e.getMessage(), e);
            return;
        }
    }

    @Override
    public void uploadVouchers(ParkTaxBillUploadVoucherQuery query,String account) {
        TParkTaxBillEntity parkTaxBillEntity=mapper.selectByPrimaryKey(query.getId());
            if(parkTaxBillEntity.getVouchersStatus()==1){
                   throw  new BusinessException("解析中状态不允许上传!");
            }
            parkTaxBillEntity.setVouchersStatus(VouchersStatusEnum.VOUCHERS_TO_BE_REFUNDED.getValue());
            parkTaxBillEntity.setUpdateUser(account);
            parkTaxBillEntity.setUpdateTime(new Date());
            mapper.updateByPrimaryKey(parkTaxBillEntity);
            //增加历史记录
            ParkTaxBillChangeEntity parkTaxBillChangeEntity=new ParkTaxBillChangeEntity();
            BeanUtils.copyProperties(parkTaxBillEntity,parkTaxBillChangeEntity);
            parkTaxBillChangeEntity.setId(null);
            parkTaxBillChangeEntity.setParkBillsId(parkTaxBillEntity.getId());
            parkTaxBillChangeEntity.setAddUser(account);
            parkTaxBillChangeEntity.setAddTime(new Date());
            parkTaxBillChangeEntity.setRemark("操作上传完税凭证");
            parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
            JSONObject json = new JSONObject();
            json.put("fileUrl", query.getFileUrl());
            json.put("parkTaxBill", query.getId());
            json.put("account", account);
            rabbitTemplate.convertAndSend("uploadVouchers", json);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadVouchersMq(JSONObject json) throws IOException {
        Long parkTaxBill=json.getLong("parkTaxBill");
        String fileUrl=json.getString("fileUrl");
        String account=json.getString("account");
        TParkTaxBillEntity parkTaxBillEntity=mapper.selectByPrimaryKey(parkTaxBill);
        if(parkTaxBillEntity==null){
            log.info("园区税单不存在");
            throw  new BusinessException("园区税单不存在");
        }
        try{
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
            String name=parkService.findById(parkTaxBillEntity.getParkId()).getParkCode()+"/"+sdf.format(new Date());

            //解析
            Map<String,Integer> map=analysisZip(ossService.getPrivateImgUrl(fileUrl),name,parkTaxBillEntity,account);
            int alreadyUploadVatVouchersCompanyNumber=map.get("alreadyUploadVatVouchersCompanyNumber");
            int alreadyUploadIitVouchersCompanyNumber=map.get("alreadyUploadIitVouchersCompanyNumber");
            if(alreadyUploadVatVouchersCompanyNumber+alreadyUploadIitVouchersCompanyNumber!=0){
                if((alreadyUploadVatVouchersCompanyNumber==parkTaxBillEntity.getShouldUploadVatVouchersCompanyNumber()&& alreadyUploadVatVouchersCompanyNumber==+parkTaxBillEntity.getShouldUploadIitVouchersCompanyNumber())){
                    parkTaxBillEntity.setVouchersStatus(VouchersStatusEnum.VOUCHERS_TO_BE_PAID.getValue());
                }else{
                    parkTaxBillEntity.setVouchersStatus(VouchersStatusEnum.VOUCHERS_REFUNDED.getValue());
                }
                parkTaxBillEntity.setAlreadyUploadIitVouchersCompanyNumber(alreadyUploadIitVouchersCompanyNumber);
                parkTaxBillEntity.setAlreadyUploadVatVouchersCompanyNumber(alreadyUploadVatVouchersCompanyNumber);
                parkTaxBillEntity.setUpdateTime(new Date());
                parkTaxBillEntity.setUpdateUser(account);
                parkTaxBillEntity.setRemark("上传附件解析");
                mapper.updateByPrimaryKey(parkTaxBillEntity);
            }else{
                parkTaxBillEntity.setVouchersStatus(VouchersStatusEnum.TO_BE_CONFIRMED.getValue());
                parkTaxBillEntity.setUpdateTime(new Date());
                parkTaxBillEntity.setUpdateUser(account);
                parkTaxBillEntity.setRemark("上传附件解析");
                mapper.updateByPrimaryKey(parkTaxBillEntity);
            }
        }catch (Exception e){
//            if (parkTaxBillEntity.getIncomeLevyType() != null && parkTaxBillEntity.getIncomeLevyType().equals(2)){
//                parkTaxBillEntity.setTaxBillStatus(ParkTaxBillStatusEnum.TAX_REFUNDED.getValue());
//            }
//            parkTaxBillEntity.setTaxBillStatus(ParkTaxBillStatusEnum.TAX_REFUNDED.getValue());
            parkTaxBillEntity.setVouchersStatus(VouchersStatusEnum.VOUCHERS_REFUNDED.getValue());
            mapper.updateByPrimaryKey(parkTaxBillEntity);
            //增加历史记录
            ParkTaxBillChangeEntity parkTaxBillChangeEntity=new ParkTaxBillChangeEntity();
            BeanUtils.copyProperties(parkTaxBillEntity,parkTaxBillChangeEntity);
            parkTaxBillChangeEntity.setId(null);
            parkTaxBillChangeEntity.setParkBillsId(parkTaxBillEntity.getId());
            parkTaxBillChangeEntity.setAddUser(account);
            parkTaxBillChangeEntity.setAddTime(new Date());
            parkTaxBillChangeEntity.setRemark("解析完税凭证失败");
            parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
        }

    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void addTaxBillXXJOB(int type,Long parkId,String remark,String account) {
        //查询出企业税单初始话数据
        int year = DateUtil.getYear(new Date());
        int month =DateUtil.getMonth(new Date())-1;
        int seasonal=0;
        if(type==1){
            if(month==0 || month==1||month==2){
                seasonal=1;
            }else if(month==3||month==4||month==5){
                seasonal=2;
            }else if(month==6||month==7||month==8){
                seasonal=3;
            }else if(month==9||month==10||month==11){
                seasonal=4;
            }
        }else {
            if(month==0 || month==1||month==2){
                seasonal=4;
                year=year-1;
            }else if(month==3||month==4||month==5){
                seasonal=1;
            }else if(month==6||month==7||month==8){
                seasonal=2;
            }else if(month==9||month==10||month==11){
                seasonal=3;
            }
        }
        CompanyTaxBillQueryAdmin adminQuery= new CompanyTaxBillQueryAdmin();
        adminQuery.setTaxBillSeasonal(seasonal);
        adminQuery.setTaxBillYear(year);
        adminQuery.setParkId(parkId);
        List<CompanyTaxBillListVOAdmin> companyTaxBillListVOAdmins =companyTaxBillService.queryCompanyTaxBillList(adminQuery);
        companyTaxBillListVOAdmins = companyTaxBillListVOAdmins.stream().filter(x -> null != x.getParkTaxBillId() // V3.11 不包括注销生成的企业税单
                && !TaxBillStatusEnum.TO_BE_CHECK.getValue().equals(x.getTaxBillStatus())
                && !TaxBillStatusEnum.CANCELLED.getValue().equals(x.getTaxBillStatus())).collect(Collectors.toList()); // 不含待核对、已作废
        if(CollectionUtil.isNotEmpty(companyTaxBillListVOAdmins)&&companyTaxBillListVOAdmins.get(0).getTaxBillStatus()!=0){
            throw  new BusinessException("企业税单已经推送不能重新生成");
        }else{
            //删除企业税单
            Example example=new Example(CompanyTaxBillEntity.class);
            example.createCriteria().andEqualTo("parkId",parkId).andEqualTo("taxBillSeasonal",seasonal)
                    .andEqualTo("taxBillYear",year).andIsNotNull("parkTaxBillId"); // V3.11 不删除注销生成的企业税单
            companyTaxBillService.delByExample(example);
            //删除园区税单
            Example parkExample=new Example(TParkTaxBillEntity.class);
            parkExample.createCriteria().andEqualTo("parkId",parkId).andEqualTo("taxBillSeasonal",seasonal).andEqualTo("taxBillYear",year);
            mapper.deleteByExample(parkExample);
        }
        //插入企业税单初始化
        CompanyTaxBillQuery query=new CompanyTaxBillQuery();
        query.setTaxBillSeasonal(seasonal);
        query.setTaxBillYear(year);
        query.setParkId(parkId);
        Date date = new Date();
        List<CompanyTaxBillXXJOBVO> list =companyTaxBillService.queryCompanyTaxBillByTime(query);
        for (CompanyTaxBillXXJOBVO co: list ) {
            if (null == co.getCompanyId()) {
                continue;
            }
            MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(co.getCompanyId());
            TaxPolicyEntity taxPolicyEntity = taxPolicyService.queryTaxPolicyByParkId(co.getParkId(), memberCompanyEntity.getCompanyType(),memberCompanyEntity.getTaxpayerType());
            if(taxPolicyEntity==null){
                throw new BusinessException("税费政策不存在");
            }
            // 查询园区征收方式
            ParkEntity park = parkService.findById(co.getParkId());
            if (null == park) {
                continue;
            }
            CompanyTaxBillEntity companyTaxBillEntity=new CompanyTaxBillEntity();
            companyTaxBillEntity.setParkTaxBillId(co.getParkId());
            companyTaxBillEntity.setParkId(co.getParkId());
            companyTaxBillEntity.setTaxBillYear(year);
            companyTaxBillEntity.setTaxBillSeasonal(seasonal);
            companyTaxBillEntity.setCompanyId(co.getCompanyId());
            companyTaxBillEntity.setIncomeLevyWay(taxPolicyEntity.getLevyWay());
            companyTaxBillEntity.setInvoiceMoney(co.getInvoiceAmount());
            companyTaxBillEntity.setZpInvoiceAmount(co.getZpInvoiceAmount());
            companyTaxBillEntity.setPpInvoiceAmount(co.getPpInvoiceAmount());
            companyTaxBillEntity.setAlreadyTaxMoney(co.getAlreadyTaxMoney());
            companyTaxBillEntity.setVatAlreadyTaxMoney(co.getVatFee());
            companyTaxBillEntity.setIncomeLevyType(taxPolicyEntity.getIncomeLevyType()); // V3.11 企业税单保存征收方式
            if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(taxPolicyEntity.getIncomeLevyType())) {
                companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TO_BE_CHECK.getValue()); // V3.11 查账方式企业税单初始状态为“待核对”
            } else {
                companyTaxBillEntity.setTaxBillStatus(TaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
            }
            companyTaxBillEntity.setAddTime(date);
            companyTaxBillEntity.setAddUser(account);
            companyTaxBillEntity.setAdditionalAlreadyTaxMoney(co.getSurcharge());
            companyTaxBillEntity.setIncomeAlreadyTaxMoney(co.getPersonalIncomeTax());
            companyTaxBillEntity.setGenerateType(1); //生成方式 1季度自动生成 2企业注销生成
            companyTaxBillEntity.setIitDeductionAmount(0L);
            companyTaxBillService.insertSelective(companyTaxBillEntity);
            //   添加历史记录
            CompanyTaxBillChangeEntity companyTaxBillChangeEntity = new CompanyTaxBillChangeEntity();
            BeanUtils.copyProperties(companyTaxBillEntity,companyTaxBillChangeEntity);
            companyTaxBillChangeEntity.setId(null);
            companyTaxBillChangeEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
            companyTaxBillChangeEntity.setDescrip("系统自动生成税单");
            companyTaxBillChangeEntity.setAddTime(new Date());
            companyTaxBillChangeEntity.setAddUser(account);
            companyTaxBillChangeService.insertSelective(companyTaxBillChangeEntity);

            // 获取税费信息（V3.2更新）
            int calculationType = 0;
            // 查账征收自动生成税单时只计算已缴及增值附加
            if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(taxPolicyEntity.getIncomeLevyType())) {
                calculationType = 1;
            }
            TaxCalculationVO entity = new TaxCalculationVO();
            entity.setCompanyId(co.getCompanyId());
            entity.setType(2);
            entity.setOrderNo(null);
            entity.setVatRate(null);
            entity.setSeason(seasonal);
            entity.setYear(year);
            entity.setCalculationType(calculationType);
            Map<String, Object> taxMap = null;
            try {
                taxMap = invoiceOrderService.taxCalculation(entity);
            } catch (Exception e) {
                log.info("企业税单税费计算错误，companyId：{},reason：{}", co.getCompanyId(), e.getMessage());
                throw new BusinessException("税单生成失败：" + e.getMessage());
            }
            BeanUtil.copyProperties(taxMap, companyTaxBillEntity);

            companyTaxBillService.editByIdSelective(companyTaxBillEntity);
        }
        //园区税单初始话
        ParkTaxBillQuery parkQuery=new ParkTaxBillQuery();
        parkQuery.setTaxBillSeasonal(seasonal);
        parkQuery.setTaxBillYear(year);
        parkQuery.setParkId(parkId);

        List<ParkTaxBillXXJOBVO> parkList=queryParkTaxBillByCompanyTaxBill(parkQuery);
        for (ParkTaxBillXXJOBVO vo: parkList ) {
            // 查询园区
            ParkEntity park = parkService.findById(vo.getParkId());
            if (null == park) {
                continue;
            }
            parkQuery.setParkId(vo.getParkId());
            TParkTaxBillEntity tParkTaxBillEntity=new TParkTaxBillEntity();
            tParkTaxBillEntity.setTaxBillYear(year);
            tParkTaxBillEntity.setTaxBillSeasonal(seasonal);
            tParkTaxBillEntity.setParkId(vo.getParkId());
            tParkTaxBillEntity.setInvoiceCompanyNumber(vo.getCompanyNumber());
            tParkTaxBillEntity.setAlreadyTaxMoney(vo.getAlreadyTaxMoney());
//            tParkTaxBillEntity.setIncomeLevyType(park.getIncomeLevyType());
            TaxPolicyEntity taxPolicyEntity = taxPolicyService.queryTaxPolicyByParkId(vo.getParkId(), MemberCompanyTypeEnum.INDIVIDUAL.getValue(), CompanyTaxPayerTypeEnum.SMALL_SCALE_TAXPAYER.getValue());
            if(taxPolicyEntity==null){
                throw new BusinessException("税费政策不存在");
            }
            tParkTaxBillEntity.setIncomeLevyType(taxPolicyEntity.getIncomeLevyType());
            tParkTaxBillEntity.setCancellationCompany(vo.getCancellationCompany());
            if (IncomeLevyTypeEnum.AUDIT_COLLECTION.getValue().equals(taxPolicyEntity.getIncomeLevyType())) {
                tParkTaxBillEntity.setTaxBillStatus(ParkTaxBillStatusEnum.TO_BE_CHECK.getValue());
            } else {
                tParkTaxBillEntity.setTaxBillStatus(ParkTaxBillStatusEnum.TO_BE_CONFIRMED.getValue());
            }
            tParkTaxBillEntity.setVouchersStatus(VouchersStatusEnum.TO_BE_CONFIRMED.getValue());
            tParkTaxBillEntity.setAddTime(new Date());
            tParkTaxBillEntity.setAddUser(account);
            mapper.insertSelective(tParkTaxBillEntity);
            //增加历史记录
            ParkTaxBillChangeEntity parkTaxBillChangeEntity=new ParkTaxBillChangeEntity();
            BeanUtils.copyProperties(tParkTaxBillEntity,parkTaxBillChangeEntity);
            parkTaxBillChangeEntity.setId(null);
            parkTaxBillChangeEntity.setParkBillsId(tParkTaxBillEntity.getId());
            parkTaxBillChangeEntity.setRemark(remark);
            parkTaxBillChangeService.insertSelective(parkTaxBillChangeEntity);
            //给企业税单加上园区税单ID
            companyTaxBillService.updateCompanyTaxBillByParkId(vo.getParkId(),tParkTaxBillEntity.getId(),seasonal,year);
        }
    }

    private static InputStream openUrl(String link) throws IOException {
        URL url = new URL(link);
        URLConnection conn = url.openConnection();
        conn.connect();
        return conn.getInputStream();
    }

    /**
     * 解析ZIP压缩包
     * @param path
     * @param name
     * @return
     * @throws IOException
     */
    private Map<String ,Integer> analysisZip(String path,String name,TParkTaxBillEntity parkTaxBillEntity,String account) throws IOException {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddHHmmss");
        String groupOrderNo=sdf.format(new Date());
        Map<String ,Integer> map =new HashMap<>();
        int alreadyUploadVatVouchersCompanyNumber=parkTaxBillEntity.getAlreadyUploadVatVouchersCompanyNumber();
        int alreadyUploadIitVouchersCompanyNumber=parkTaxBillEntity.getAlreadyUploadIitVouchersCompanyNumber();

        InputStream in =openUrl(path);
        Charset gbk = Charset.forName("gbk");
        ZipInputStream zin = new ZipInputStream(in,gbk);
        ZipEntry ze;
        while((ze = zin.getNextEntry()) != null){
            if(ze.getName().split("/").length<2){
                continue;
            }
            String ein=ze.getName().split("/")[0];
            String invoiceName=ze.getName().split("/")[1].toLowerCase();

            if(!invoiceName.contains("2")&&!invoiceName.contains("1")&&!invoiceName.contains("3")){
                //新增上传记录
                TaxBillCredentialsRecordEntity taxBillCredentialsRecordEntity=new TaxBillCredentialsRecordEntity();
                taxBillCredentialsRecordEntity.setParkTaxBillId(parkTaxBillEntity.getId());
                taxBillCredentialsRecordEntity.setEin(ein);

                taxBillCredentialsRecordEntity.setBatchNumber(groupOrderNo);
                taxBillCredentialsRecordEntity.setStatus(1);
                taxBillCredentialsRecordEntity.setAddTime(new Date());
                taxBillCredentialsRecordEntity.setAddUser(account);
                taxBillCredentialsRecordEntity.setResultMsg("非vat，iit，ticket文件");
                taxBillCredentialsRecordEntity.setRemark("上传完税凭证");
                taxBillCredentialsRecordService.insertSelective(taxBillCredentialsRecordEntity);
                continue;
            }
            String regex = "^[a-z0-9A-Z]{15,20}$";
            if(!ein.matches(regex)){
                //新增上传记录
                TaxBillCredentialsRecordEntity taxBillCredentialsRecordEntity=new TaxBillCredentialsRecordEntity();
                taxBillCredentialsRecordEntity.setParkTaxBillId(parkTaxBillEntity.getId());
                taxBillCredentialsRecordEntity.setEin(ein);

                taxBillCredentialsRecordEntity.setBatchNumber(groupOrderNo);
                if(invoiceName.contains("2")){
                    taxBillCredentialsRecordEntity.setVatVoucherPic(name);
                }
                if(invoiceName.contains("1")){
                    taxBillCredentialsRecordEntity.setIitVoucherPic(name);
                }
                if(invoiceName.contains("3")){
                    taxBillCredentialsRecordEntity.setTicketVoucherPic(name);
                }
                taxBillCredentialsRecordEntity.setStatus(1);
                taxBillCredentialsRecordEntity.setAddTime(new Date());
                taxBillCredentialsRecordEntity.setAddUser(account);
                taxBillCredentialsRecordEntity.setResultMsg(invoiceName+"税号格式不正确");
                taxBillCredentialsRecordEntity.setRemark("上传完税凭证");
                taxBillCredentialsRecordService.insertSelective(taxBillCredentialsRecordEntity);
                continue;
            }

            log.info(ein+"================="+invoiceName);
            CompanyTaxBillEntity companyTaxBillEntity=companyTaxBillService.queryCompanyTaxBillByEinAndParkTaxBillId(ein,parkTaxBillEntity.getId());
            if(companyTaxBillEntity==null){
                //新增上传记录
                TaxBillCredentialsRecordEntity taxBillCredentialsRecordEntity=new TaxBillCredentialsRecordEntity();
                taxBillCredentialsRecordEntity.setParkTaxBillId(parkTaxBillEntity.getId());
                taxBillCredentialsRecordEntity.setEin(ein);
                taxBillCredentialsRecordEntity.setCompanyTaxBillId(null);
                taxBillCredentialsRecordEntity.setBatchNumber(groupOrderNo);
                if(invoiceName.contains("2")){
                    taxBillCredentialsRecordEntity.setVatVoucherPic(name);
                }
                if(invoiceName.contains("1")){
                    taxBillCredentialsRecordEntity.setIitVoucherPic(name);
                }
                if(invoiceName.contains("3")){
                    taxBillCredentialsRecordEntity.setTicketVoucherPic(name);
                }
                taxBillCredentialsRecordEntity.setStatus(1);
                taxBillCredentialsRecordEntity.setAddTime(new Date());
                taxBillCredentialsRecordEntity.setAddUser(account);
                taxBillCredentialsRecordEntity.setResultMsg(invoiceName+"税号不存在企业税单");
                taxBillCredentialsRecordEntity.setRemark("上传完税凭证");
                taxBillCredentialsRecordService.insertSelective(taxBillCredentialsRecordEntity);
                continue;
            }
            // 去除完税凭证可上传性校验
            /*
            if(invoiceName.contains("2")&&companyTaxBillEntity.getVatShouldTaxMoney()<=0){
                //新增上传记录
                TaxBillCredentialsRecordEntity taxBillCredentialsRecordEntity=new TaxBillCredentialsRecordEntity();
                taxBillCredentialsRecordEntity.setParkTaxBillId(parkTaxBillEntity.getId());
                taxBillCredentialsRecordEntity.setEin(ein);
                taxBillCredentialsRecordEntity.setCompanyTaxBillId(null);
                taxBillCredentialsRecordEntity.setBatchNumber(groupOrderNo);
                if(invoiceName.contains("2")){
                    taxBillCredentialsRecordEntity.setVatVoucherPic(name);
                }
                if(invoiceName.contains("1")){
                    taxBillCredentialsRecordEntity.setIitVoucherPic(name);
                }
                taxBillCredentialsRecordEntity.setStatus(1);
                taxBillCredentialsRecordEntity.setAddTime(new Date());
                taxBillCredentialsRecordEntity.setAddUser(account);
                taxBillCredentialsRecordEntity.setResultMsg("该企业不需要上传凭证"+invoiceName);
                taxBillCredentialsRecordEntity.setRemark("上传完税凭证");
                taxBillCredentialsRecordService.insertSelective(taxBillCredentialsRecordEntity);
                continue;
            }
            if(invoiceName.contains("1")&&companyTaxBillEntity.getIncomeShouldTaxMoney()<=0){
                //新增上传记录
                TaxBillCredentialsRecordEntity taxBillCredentialsRecordEntity=new TaxBillCredentialsRecordEntity();
                 taxBillCredentialsRecordEntity.setParkTaxBillId(parkTaxBillEntity.getId());
                taxBillCredentialsRecordEntity.setEin(ein);
                taxBillCredentialsRecordEntity.setCompanyTaxBillId(null);
                taxBillCredentialsRecordEntity.setBatchNumber(groupOrderNo);
                if(invoiceName.contains("2")){
                    taxBillCredentialsRecordEntity.setVatVoucherPic(name);
                }
                if(invoiceName.contains("1")){
                    taxBillCredentialsRecordEntity.setIitVoucherPic(name);
                }
                taxBillCredentialsRecordEntity.setStatus(1);
                 taxBillCredentialsRecordEntity.setAddTime(new Date());
                taxBillCredentialsRecordEntity.setAddUser(account);
                taxBillCredentialsRecordEntity.setResultMsg("该企业不需要上传凭证"+invoiceName);
                taxBillCredentialsRecordEntity.setRemark("上传完税凭证");
                taxBillCredentialsRecordService.insertSelective(taxBillCredentialsRecordEntity);
                continue;
            }*/
            String ossPath="";
            if(!invoiceName.contains(".jpg")&&!invoiceName.contains(".png")&&!invoiceName.contains("pdf")){
                //新增上传记录
                TaxBillCredentialsRecordEntity taxBillCredentialsRecordEntity=new TaxBillCredentialsRecordEntity();
                taxBillCredentialsRecordEntity.setParkTaxBillId(parkTaxBillEntity.getId());
                taxBillCredentialsRecordEntity.setEin(ein);
                taxBillCredentialsRecordEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
                taxBillCredentialsRecordEntity.setBatchNumber(groupOrderNo);
                if(invoiceName.contains("2")){
                    taxBillCredentialsRecordEntity.setVatVoucherPic(name);
                }
                if(invoiceName.contains("1")){
                    taxBillCredentialsRecordEntity.setIitVoucherPic(name);
                }
                if(invoiceName.contains("3")){
                    taxBillCredentialsRecordEntity.setTicketVoucherPic(name);
                }
                taxBillCredentialsRecordEntity.setStatus(1);
                taxBillCredentialsRecordEntity.setAddTime(new Date());
                taxBillCredentialsRecordEntity.setAddUser(account);
                taxBillCredentialsRecordEntity.setResultMsg(invoiceName+"图片格式错误");
                taxBillCredentialsRecordEntity.setRemark("上传完税凭证");
                taxBillCredentialsRecordService.insertSelective(taxBillCredentialsRecordEntity);
                continue;
            }

            if(invoiceName.contains("pdf")){
                //保存电子发票图片
                InputStream inputStream = FileUtil.pdf2png(zin);//pdf网络地址转png文件base64
                //读取当个文件夹的输入流
                //上传oss
                InputStream is=inputStream;
                ByteArrayOutputStream os = null;
                String fileName=UUID.randomUUID()+".jpg";
                try {
                    os = new ByteArrayOutputStream();
                    int len = 0;
                    byte[] buffer = new byte[8192];

                    while ((len =  is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    byte[] in_b = os.toByteArray();
                    ossPath=name+"/"+ein+"/"+ fileName;
                    ossService.upload(name+"/"+ein+"/"+ fileName,in_b);
                } finally {
                    if(os!=null){
                        os.close();
                    }
                    if(is!=null){
                        is.close();
                    }
                }
            }else{
                //读取当个文件夹的输入流
                byte[] data = getByte(zin);
                //上传oss
                InputStream is=new ByteArrayInputStream(data);
                ByteArrayOutputStream os = null;
                String fileName=UUID.randomUUID()+".jpg";
                try {
                    os = new ByteArrayOutputStream();
                    int len = 0;
                    byte[] buffer = new byte[8192];

                    while ((len =  is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    byte[] in_b = os.toByteArray();
                    ossPath=name+"/"+ein+"/"+ fileName;
                    ossService.upload(name+"/"+ein+"/"+ fileName,in_b);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                finally {
                    if(os!=null){
                        os.close();
                    }
                    if(is!=null){
                        is.close();
                    }
                }
            }

            //存储企业税单完税凭证

            if(invoiceName.toLowerCase().contains("2")){
                companyTaxBillEntity.setVatVoucherPic(ossPath) ;
                companyTaxBillEntity.setVatVouchersStatus(VouchersUpStatusEnum.VOUCHERS_TO_BE_PAID.getValue());
                //税单完税凭证解析记录表
                Example example = new Example(TaxBillCredentialsRecordEntity.class);
                example.createCriteria().andEqualTo("parkTaxBillId",parkTaxBillEntity.getId()).andEqualTo("companyTaxBillId",companyTaxBillEntity.getId())
                .andEqualTo("status",0).andIsNotNull("vatVoucherPic");
                List<TaxBillCredentialsRecordEntity> list =taxBillCredentialsRecordService.selectByExample(example);
                if(list.size()<1){
                    //新增
                    alreadyUploadVatVouchersCompanyNumber++;
                }
            }
            if(invoiceName.toLowerCase().contains("1")){
                companyTaxBillEntity.setIitVoucherPic(ossPath);
                companyTaxBillEntity.setIitVouchersStatus(VouchersUpStatusEnum.VOUCHERS_TO_BE_PAID.getValue());
                //税单完税凭证解析记录表
                Example example = new Example(TaxBillCredentialsRecordEntity.class);
                example.createCriteria().andEqualTo("parkTaxBillId",parkTaxBillEntity.getId()).andEqualTo("companyTaxBillId",companyTaxBillEntity.getId())
                        .andEqualTo("status",0).andIsNotNull("iitVoucherPic");
                List<TaxBillCredentialsRecordEntity> list =taxBillCredentialsRecordService.selectByExample(example);
                if(list.size()<1){
                    //新增
                    alreadyUploadIitVouchersCompanyNumber++;
                }
            }
            if(invoiceName.toLowerCase().contains("3")){
                companyTaxBillEntity.setTicketPic(ossPath);
            }
            companyTaxBillEntity.setUpdateTime(new Date());
            companyTaxBillEntity.setUpdateUser(account);
            companyTaxBillService.editByIdSelective(companyTaxBillEntity);

            TaxBillCredentialsRecordEntity taxBillCredentialsRecordEntity=new TaxBillCredentialsRecordEntity();
            taxBillCredentialsRecordEntity.setParkTaxBillId(parkTaxBillEntity.getId());
            taxBillCredentialsRecordEntity.setEin(ein);
            taxBillCredentialsRecordEntity.setCompanyTaxBillId(companyTaxBillEntity.getId());
            taxBillCredentialsRecordEntity.setBatchNumber(groupOrderNo);
            if(invoiceName.contains("2")){
                taxBillCredentialsRecordEntity.setVatVoucherPic(ossPath);
            }
            if(invoiceName.contains("1")){
                taxBillCredentialsRecordEntity.setIitVoucherPic(ossPath);
            }
            if(invoiceName.contains("3")){
                taxBillCredentialsRecordEntity.setTicketVoucherPic(ossPath);
            }
            taxBillCredentialsRecordEntity.setStatus(0);
            taxBillCredentialsRecordEntity.setAddTime(new Date());
            taxBillCredentialsRecordEntity.setAddUser(account);
            taxBillCredentialsRecordEntity.setRemark("上传完税凭证");
            taxBillCredentialsRecordService.insertSelective(taxBillCredentialsRecordEntity);


        }
        zin.closeEntry();
        map.put("alreadyUploadVatVouchersCompanyNumber",alreadyUploadVatVouchersCompanyNumber);
        map.put("alreadyUploadIitVouchersCompanyNumber",alreadyUploadIitVouchersCompanyNumber);
        return map;
    }

    public File getFileByBytes(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            // 判断文件目录是否存在
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);

            //输出流
            fos = new FileOutputStream(file);

            //缓冲流
            bos = new BufferedOutputStream(fos);

            //将字节数组写出
            bos.write(bytes);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return file;
    }
    /**
     * 获取条目byte[]字节
     * @param zis
     * @return
     */
    public byte[] getByte(InflaterInputStream zis) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] temp = new byte[1024];
            byte[] buf = null;
            int length = 0;

            while ((length = zis.read(temp, 0, 1024)) != -1) {
                bout.write(temp, 0, length);
            }

            buf = bout.toByteArray();
            bout.close();
            return buf;
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    void sendSmsAndNotice(Long parkTaxBillId,String account){
        TParkTaxBillEntity entity = this.findById(parkTaxBillId);
        ParkEntity parkEntity = parkService.findById(entity.getParkId());
        TaxBillInfoToMemberQuery query =new TaxBillInfoToMemberQuery();
        query.setParkTaxBillId(parkTaxBillId);
        query.setTaxBillYear(entity.getTaxBillYear());
        query.setTaxBillSeasonal(entity.getTaxBillSeasonal());
        List<TaxBillInfoToMemberVO> list =companyTaxBillService.queryCompanyTaxBillInfoToMember(query);
        // 过滤掉本期开票金额为0的税单
        list = list.stream().filter(x -> x.getInvoiceMoney() > 0L).collect(Collectors.toList());
        Map<String,Object> flagMap = new HashMap<>();
        for (TaxBillInfoToMemberVO vo: list) {
            OemEntity oemEntity = oemService.getOem(vo.getOemCode());
            if (oemEntity.getIsSendAuditBillsMessage().equals(0)){
                continue;
            }
            List<Map<String,Object>> mapList=new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("phone", vo.getMemberPhone());
            map.put("userId", vo.getMemberId());
            mapList.add(map);
            //  每个电话号码只发一条短信
            if(flagMap.containsKey(vo.getMemberPhone())){
                continue;
            }else{
                flagMap.put( vo.getMemberPhone(),"1");
            }
            Map<String, Object> smsMap = new HashMap<>();
/*            smsMap.put("parkName", vo.getParkName());
            smsMap.put("taxBillYear", vo.getTaxBillYear());
            smsMap.put("taxBillSeasonal", vo.getTaxBillSeasonal());
            smsMap.put("reNum", vo.getRecoverable());
            smsMap.put("suNum", vo.getSupplement());*/
            if (vo.getYears() == null || vo.getMonths() == null || vo.getDays() == null){
                throw new BusinessException("未配置成本截至时间");
            }
            String taxPeriod = taxPeriod(vo.getTaxBillYear(),vo.getTaxBillSeasonal());
            smsMap.put("taxBillSeasonal", taxPeriod);
            smsMap.put("parkName",parkEntity.getParkName());
            smsMap.put("year",vo.getYears());
            smsMap.put("month",vo.getMonths());
            smsMap.put("day",vo.getDays());
            //发短信
            smsService.sendTemplateSms(vo.getMemberPhone(), vo.getOemCode(), VerifyCodeTypeEnum.TAX_BILL_BY_ACCOUNtS.getValue(), smsMap, 2);
            //发送消息
            String titleTemp=dictionaryService.getValueByCode("notice_template_by_accounts");
            titleTemp=titleTemp.replace("#taxBillSeasonal#",taxPeriod).replace("#parkName#",String.valueOf(parkEntity.getParkName())).replace("#year#",String.valueOf(vo.getYears())).replace("#month#",String.valueOf(vo.getMonths())).replace("#day#",vo.getDays().toString());
            MessageNoticeEntity messageNoticeEntity = new MessageNoticeEntity();
            messageNoticeEntity.setOemCode(vo.getOemCode());
            messageNoticeEntity.setNoticeType(2);
            messageNoticeEntity.setIsAlert(0);
            messageNoticeEntity.setNoticePosition("1,2");
            messageNoticeEntity.setOpenMode(3);
            messageNoticeEntity.setBusinessType(13);
            messageNoticeEntity.setNoticeTitle("待确认成本提醒");
            messageNoticeEntity.setNoticeContent(titleTemp);
            messageNoticeEntity.setUserPhones(vo.getMemberPhone());
            messageNoticeEntity.setStatus(0);
            messageNoticeEntity.setUserId(vo.getMemberId());
            messageNoticeEntity.setUserType(1);
            messageNoticeEntity.setAddTime(new Date());
            messageNoticeEntity.setAddUser("admin");
            messageNoticeService.saveMessageNotice(messageNoticeEntity);
        }

    }

    void sendSmsAndNoticeByApproved(Long parkTaxBillId,String account){
        TaxBillInfoToMemberQuery query =new TaxBillInfoToMemberQuery();
        query.setParkTaxBillId(parkTaxBillId);
        List<TaxBillInfoToMemberVO> list =companyTaxBillService.queryCompanyTaxBillInfoToMember(query);
        // 过滤掉本期开票金额为0的税单
        list = list.stream().filter(x -> x.getInvoiceMoney() > 0L).collect(Collectors.toList());

        for (TaxBillInfoToMemberVO vo: list) {
            List<Map<String,Object>> mapList=new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("phone", vo.getMemberPhone());
            map.put("userId", vo.getMemberId());
            mapList.add(map);
            Map<String, Object> smsMap = new HashMap<>();
            smsMap.put("parkName", vo.getParkName());
            smsMap.put("taxBillYear", vo.getTaxBillYear());
            smsMap.put("taxBillSeasonal", vo.getTaxBillSeasonal());
            smsMap.put("reNum", vo.getRecoverable());
            smsMap.put("suNum", vo.getSupplement());
            //发短信
            smsService.sendTemplateSms(vo.getMemberPhone(), vo.getOemCode(), VerifyCodeTypeEnum.TAX_BILL.getValue(), smsMap, 2);
            //发送消息
            NoticeManageEntity po=new NoticeManageEntity();
            po.setOemCode(vo.getOemCode());
            po.setNoticeType(2);
            po.setNoticePosition("1,2");
            po.setOpenMode(1);
            po.setNoticeObj(2);
            po.setReleaseWay(1);
            String titleTemp=dictionaryService.getValueByCode("notice_title_template");
            String noticeTemp=dictionaryService.getValueByCode("notice_template");
            titleTemp=titleTemp.replace("#parkName#",vo.getParkName()).replace("#taxBillYear#",String.valueOf(vo.getTaxBillYear())).replace("#taxBillSeasonal#",String.valueOf(vo.getTaxBillSeasonal()));
            noticeTemp=noticeTemp.replace("#recoverable#",String.valueOf(vo.getRecoverable())).replace("#supplement#",String.valueOf(vo.getSupplement()));
            po.setNoticeTitle(titleTemp);
            po.setNoticeSubtitle(noticeTemp);
            po.setNoticeContent(noticeTemp);
            manageService.sendNotice(po,account,mapList);
        }

    }


    private String taxPeriod(Integer taxBillYear,Integer taxBillSeasonal){
        if (taxBillYear == null || taxBillSeasonal == null){
            return "";
        }
        String taxPeriod = "";
        switch(taxBillSeasonal){
            case 1 :
                taxPeriod = taxBillYear+"年"+"第一季度";
                break;
            case 2 :
                taxPeriod = taxBillYear+"年"+"第二季度";
                break;
            case 3 :
                taxPeriod = taxBillYear+"年"+"第三季度";
                break;
            case 4 :
                taxPeriod = taxBillYear+"年"+"第四季度";
                break;
        }
        return taxPeriod;
    }

    /**
     * 服务器获取excel转成集合
     * @param uri
     * @return
     */
    public static List<ParkTaxBillUploadVO> getInputStream(String uri) {
        URL url;
        HttpURLConnection con = null;
        try {
            url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();
            int httpResult = con.getResponseCode();
            if(httpResult == HttpURLConnection.HTTP_OK){
                ImportParams params = new ImportParams();
                params.setTitleRows(0);
                params.setHeadRows(1);
                return ExcelImportUtil.importExcel(con.getInputStream(), ParkTaxBillUploadVO.class, params);
            }
        } catch (Exception e) {
            log.error("读取oss上文件异常");
            log.error(e.getMessage(), e);
        } finally {
            if(con != null) {
                con.disconnect();
            }
        }
        return null;

    }

}

