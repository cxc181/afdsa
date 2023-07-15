package com.yuqian.itax.order.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.InvoiceRecordDetailMapper;
import com.yuqian.itax.order.entity.InvoiceRecordDetailEntity;
import com.yuqian.itax.order.entity.query.InvoiceRecordDetailQuery;
import com.yuqian.itax.order.entity.vo.InvoiceDetailVO;
import com.yuqian.itax.order.entity.vo.InvoiceRecordDetailVO;
import com.yuqian.itax.order.service.InvoiceRecordDetailService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service("invoiceRecordDetailService")
@Slf4j
public class InvoiceRecordDetailServiceImpl extends BaseServiceImpl<InvoiceRecordDetailEntity,InvoiceRecordDetailMapper> implements InvoiceRecordDetailService {

    @Autowired
    private OssService ossService;

    @Autowired
    private DictionaryService sysDictionaryService;
    /**
     * 查询开票记录明细
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @return
     */
    @Override
    public List<InvoiceRecordDetailVO> invoiceRecordDetailList(String invoiceRecordNo, String orderNo){
        return this.mapper.invoiceRecordDetailList(invoiceRecordNo,orderNo);
    }

    /**
     * 电子发票pdf转成图片
     * @param entity
     */
    public void invoicePdf2Img(InvoiceRecordDetailEntity entity){
        if(entity == null || StringUtils.isBlank(entity.getEinvoiceUrl())){
            return;
        }else{
            //将开票记录的是否已生成版式改成 生成中
            entity.setIsFormatCreate(1);
            this.editByIdSelective(entity);

            String url = entity.getEinvoiceUrl();
            try {
                String saveFileName = UUID.randomUUID().toString();
                String path = "ADMIN/"+DateUtil.format(new Date(),"yyyyMMdd");
                String bucket = sysDictionaryService.getByCode("oss_privateBucketName").getDictValue();
                //保存电子发票图片
                String imgBase64 = FileUtil.pdfUrl2pngBase64(url);//pdf网络地址转png文件base64
                String page = sysDictionaryService.getByCode("oss_page").getDictValue();
                if(StringUtils.isNotBlank(page) && StringUtils.startsWith(page, "/")){ //oss包名不能以/开头
                    page = page.substring(1);
                }
                //base64图片 需要去除前缀
                imgBase64 = imgBase64.replace("data:image/png;base64,","");
                ossService.uploadBase64(saveFileName+".png",page+path+"/",bucket,imgBase64);
                entity.setEinvoiceOssImgUrl(path+"/"+saveFileName+".png");

                //保存电子发票pdf
                byte[] bytes = FileUtil.readFileByBytes(url);
                ossService.upload(path+"/"+saveFileName+".pdf",bytes);
                entity.setEinvoiceOssPdfUrl(path+"/"+saveFileName+".pdf");
                entity.setIsFormatCreate(2); //已生成版式
                entity.setUpdateTime(new Date());
                entity.setUpdateUser("admin");
                this.editByIdSelective(entity);
            }catch (Exception e){
                log.error(e.getMessage());
                //生成失败
                entity.setIsFormatCreate(3);
                entity.setUpdateTime(new Date());
                entity.setUpdateUser("admin");
                this.editByIdSelective(entity);
            }
        }

    }

    /**
     * 根据订单编号查询开票记录明细
     * @param orderNo
     * @return
     */
    @Override
    public List<InvoiceDetailVO> querryByOrderNo(String orderNo) {
        InvoiceRecordDetailQuery invoiceRecordDetailQuery = new InvoiceRecordDetailQuery();
        invoiceRecordDetailQuery.setOrderNo(orderNo);
        return mapper.queryByOrderNo(invoiceRecordDetailQuery);
    }

    /**
     * 查询开票中的开票记录明细
     * @param invoiceRecordNo
     * @return
     */
    @Override
    public List<Map<String,Object>> queryDetailStatusNumByIng(String invoiceRecordNo){
        return mapper.queryDetailStatusNumByIng(invoiceRecordNo);
    }

    /**
     * 根据开票记录编号修改开票明细状态
     * @param invoiceRecordNo
     * @param status
     */
    @Override
    public void updateByInvoiceRecordNo(String invoiceRecordNo,Integer status){
         mapper.updateByInvoiceRecordNo(invoiceRecordNo,status);
    }

    /**
     * 根据开票记录编号查询开票明细
     * @param invoiceRecordNo
     * @return
     */
    @Override
    public List<InvoiceRecordDetailEntity> findByInvoiceRecordNo(String invoiceRecordNo){
        return mapper.findByInvoiceRecordNo(invoiceRecordNo);
    }
}

