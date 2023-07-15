package com.yuqian.itax.mq.receiver.order;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.group.entity.GroupPaymentAnalysisRecordEntity;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import com.yuqian.itax.group.service.GroupPaymentAnalysisRecordService;
import com.yuqian.itax.group.service.InvoiceOrderGroupService;
import com.yuqian.itax.system.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 集团代开解析订单
 * @author：pengwei
 * @Date：2020/3/6 11:12
 * @version：1.0
 */
@Component
@Slf4j
public class GroupPaymentAnalysisRecordReceiver {

    @Autowired
    private InvoiceOrderGroupService invoiceOrderGroupService;

    @Autowired
    private OssService ossService;

    @Autowired
    private GroupPaymentAnalysisRecordService groupPaymentAnalysisRecordService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static Pattern CHINESE_PATTERN = Pattern.compile("[\\u4E00-\\u9FA5]+");
    private static Pattern NUMBER_PATTERN = Pattern.compile("^(([1-9]\\d{0,2}((,[0-9]{3})*|([\\d])*))|([0]))(\\.(\\d){0,2})?$");

    @RabbitHandler
    @RabbitListener(queues = "createGroupPaymentAnalysisRecord", priority="100")
    public void process(JSONObject json) {
        log.info("收到集团代开解析订单，请求参数：{}", JSONObject.toJSONString(json));
        Long groupId = null;
        try {
            groupId = json.getLong("groupId");
            InvoiceOrderGroupEntity groupEntity = invoiceOrderGroupService.findById(groupId);
            if (groupEntity == null) {
                log.error("集团开票订单不存在，集团代开订单主键：{}", groupId);
                return;
            }
            if (StringUtils.isBlank(groupEntity.getAccountStatement())) {
                log.error("集团开票订单上传文件不存在，集团代开订单主键：{}", groupId);
                return;
            }
            String url = ossService.getPrivateVideoUrl(groupEntity.getAccountStatement());
            if (StringUtils.isBlank(url)) {
                log.error("oss上文件不存在，集团代开订单主键：{}", groupId);
                return;
            }
            //获取excel文件
            List<GroupPaymentAnalysisRecordEntity> list = getInputStream(url);
            if (CollectionUtil.isEmpty(list)) {
                log.error("oss上读取的excel文件为空，集团代开订单主键：{}", groupId);
                return;
            }
            //基础字段校验
            validateList(list, groupEntity);

            //批量初始化添加
            groupPaymentAnalysisRecordService.batchAdd(list);

            //发送新的mq
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("categoryName", groupEntity.getCategoryGroupName());
            for (GroupPaymentAnalysisRecordEntity entity : list) {
                if (Objects.equals(entity.getAnalysisResult(), 0)) {
                    jsonObject.put("analysisId", entity.getId());
                    rabbitTemplate.convertAndSend("createInvoiceOrder", jsonObject);
                }
            }
        } catch (Exception e) {
            log.error("集团代开解析订单全局异常，集团代开订单主键：{}", groupId);
            log.error(e.getMessage(), e);
            return;
        } finally {
            invoiceOrderGroupService.ticketing(groupId);
        }

    }

    /**
     * 校验excel文件
     * @param list
     * @param groupEntity
     */
    private void validateList(List<GroupPaymentAnalysisRecordEntity> list, InvoiceOrderGroupEntity groupEntity) {
        Date date = new Date();
        for (GroupPaymentAnalysisRecordEntity entity : list) {
            entity.setOemCode(groupEntity.getOemCode());
            entity.setGroupOrderNo(groupEntity.getOrderNo());
            entity.setAddTime(date);
            entity.setAddUser(groupEntity.getAddUser());
            entity.setAnalysisResult(0);
            int result = validateChinese(entity, entity.getPaymentGroupName(), 64, "付款企业名称");
            if (result != 0) {
                if (result == 2) {
                    entity.setPaymentGroupName("文字输入过长");
                }
                continue;
            }
            if (!StringUtils.equals(entity.getPaymentGroupName(), groupEntity.getCompanyName())) {
                entity.setAnalysisResult(2);
                entity.setErrorResult("出款企业和发票抬头公司不一致");
                continue;
            }
            result = validateChinese(entity, entity.getPaymentBankName(), 64, "付款银行名称");
            if (result != 0) {
                if (result == 2) {
                    entity.setPaymentBankName("文字输入过长");
                }
                continue;
            }
            result = validateNum(entity, entity.getPaymentBankAccount(), 64, "付款银行账号" , false);
            if (result != 0) {
                if (result == 2) {
                    entity.setPaymentBankAccount("文字输入过长");
                }
                continue;
            }
            result = validateDate(entity, entity.getPaymentTime(), 64, "付款时间");
            if (result != 0) {
                if (result == 2) {
                    entity.setPaymentTime("文字输入过长");
                }
                continue;
            }
            result = validateNum(entity, entity.getPaymentAmount(), 64, "付款金额", true);
            if (result != 0) {
                if (result == 2) {
                    entity.setPaymentAmount("文字输入过长");
                }
                continue;
            }
            result = validateNum(entity, entity.getInvoiceAmount(), 64, "本次开票金额", true);
            if (result != 0) {
                if (result == 2) {
                    entity.setInvoiceAmount("文字输入过长");
                }
                continue;
            }
            BigDecimal payAmt = new BigDecimal(entity.getPaymentAmount());
            BigDecimal invAmt = new BigDecimal(entity.getInvoiceAmount());
            if (payAmt.compareTo(invAmt) < 0) {
                entity.setAnalysisResult(2);
                entity.setErrorResult("付款金额必须大于开票金额");
                continue;
            }
            result = validateChinese(entity, entity.getPayeeName(), 32, "收款人姓名");
            if (result != 0) {
                if (result == 2) {
                    entity.setPayeeName("文字输入过长");
                }
                continue;
            }
            result = validateChinese(entity, entity.getPayeeBankName(), 64, "收款银行名称");
            if (result != 0) {
                if (result == 2) {
                    entity.setPayeeBankName("文字输入过长");
                }
                continue;
            }
            result = validateNum(entity, entity.getPayeeBankAccount(), 64, "收款银行卡号" , false);
            if (result != 0) {
                if (result == 2) {
                    entity.setPayeeBankAccount("文字输入过长");
                }
                continue;
            }
            if (StringUtils.isBlank(entity.getPayeeIdcard())) {
                entity.setAnalysisResult(2);
                entity.setErrorResult("收款人身份证号不能为空");
                continue;
            }
            if (entity.getPayeeIdcard().length() > 32) {
                entity.setPayeeIdcard("文字输入过长");
                entity.setAnalysisResult(2);
                entity.setErrorResult("收款人身份证号输入文字过长");
                continue;
            }
        }
    }

    /**
     * 校验中文
     * @param entity
     * @param str 校验字段
     * @param length 中文长度限制
     * @param desc 描述
     * @return
     */
    public static int validateChinese(GroupPaymentAnalysisRecordEntity entity, String str, int length, String desc) {
        if (StringUtils.isBlank(str)) {
            entity.setAnalysisResult(2);
            entity.setErrorResult(desc + "不能为空");
            return 1;
        }
        if (str.length() > length) {
            entity.setAnalysisResult(2);
            entity.setErrorResult(desc + "输入文字过长");
            return 2;
        }
        if (!CHINESE_PATTERN.matcher(str).matches()) {
            entity.setAnalysisResult(2);
            entity.setErrorResult(desc + "包含非中文");
            return 3;
        }
        return 0;
    }

    /**
     * 校验数字
     * @param entity
     * @param str 校验字段
     * @param length 中文长度限制
     * @param desc 描述
     * @return
     */
    public static int validateNum(GroupPaymentAnalysisRecordEntity entity, String str, int length, String desc, boolean isMoney) {
        if (StringUtils.isBlank(str)) {
            entity.setAnalysisResult(2);
            entity.setErrorResult(desc + "不能为空");
            return 1;
        }
        if (str.length() > length) {
            entity.setAnalysisResult(2);
            entity.setErrorResult(desc + "输入文字过长");
            return 2;
        }
        if (isMoney) {
            //金额
//            if (str.indexOf(",,") != -1) {
//                entity.setAnalysisResult(2);
//                entity.setErrorResult(desc + "格式有误");
//                return 3;
//            }
            if (!NUMBER_PATTERN.matcher(str).matches()) {
                entity.setAnalysisResult(2);
                entity.setErrorResult(desc + "格式有误");
                return 3;
            }
        } else {
            //数字
            if (!StringUtils.isNumeric(str)) {
                entity.setAnalysisResult(2);
                entity.setErrorResult(desc + "包含非数字");
                return 3;
            }
        }
        return 0;
    }

    /**
     * 校验日期格式
     * @param entity
     * @param str 校验字段
     * @param length 中文长度限制
     * @param desc 描述
     * @return
     */
    public static int validateDate(GroupPaymentAnalysisRecordEntity entity, String str, int length, String desc) {
        if (StringUtils.isBlank(str)) {
            entity.setAnalysisResult(2);
            entity.setErrorResult(desc + "不能为空");
            return 1;
        }
        if (str.length() > length) {
            entity.setAnalysisResult(2);
            entity.setErrorResult(desc + "输入文字过长");
            return 2;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sdf.parse(str);
        } catch (ParseException e) {
            entity.setAnalysisResult(2);
            entity.setErrorResult(desc + "日期格式错误");
            return 3;
        }
        return 0;
    }

    /**
     * 服务器获取excel转成集合
     * @param uri
     * @return
     */
    public static List<GroupPaymentAnalysisRecordEntity> getInputStream(String uri) {
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
                return ExcelImportUtil.importExcel(con.getInputStream(), GroupPaymentAnalysisRecordEntity.class, params);
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
