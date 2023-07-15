package com.yuqian.itax.system.service;

import com.yuqian.itax.agent.entity.OemParamsEntity;

/**
 * OCR识别service
 * @author yejian
 */
public interface OcrService {

    /**
     * 身份证正面识别
     * @param imageUrl
     * @return
     */
    public String idCardFront(OemParamsEntity paramsEntity, String imageUrl);

    /**
     * 身份证反面识别
     * @param imageUrl
     * @return
     */
    public String idCardBack(OemParamsEntity paramsEntity, String imageUrl);

    /**
     * 营业执照ocr识别
     * @param paramsEntity
     * @param imageUrl
     * @return
     */
    public String ocrBusinessLicense(OemParamsEntity paramsEntity,String imageUrl);

}
