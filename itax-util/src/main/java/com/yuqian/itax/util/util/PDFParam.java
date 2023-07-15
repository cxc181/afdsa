package com.yuqian.itax.util.util;

import com.itextpdf.text.Image;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * PDF参数
 */
@Getter
@Setter
@ToString
public class PDFParam implements Serializable {

    /**
     * PDF模板字段域名称
     */
    private String name;

    /**
     * PDF模板字段域值
     */
    private String value;

    /**
     * PDF模板字段域值(img)
     */
    private Image image;

    /**
     * PDF模板字段域类型 ：0-文本，1-图片
     */
    private Integer type = 0;

    /**
     * 静态实例化参数方法
     *
     * @param name
     * @param value
     * @param type
     * @return
     */
    public static PDFParam instance(String name, String value, Integer type) {
        PDFParam param = new PDFParam();
        param.setName(name);
        param.setValue(value);
        param.setType(type);
        return param;
    }

    /**
     * 静态实例化参数方法
     *
     * @param name
     * @return
     */
    public static PDFParam instanceImg(String name, Image image) {
        PDFParam param = new PDFParam();
        param.setImage(image);
        param.setName(name);
        param.setType(1);
        return param;
    }

    /**
     * 静态实例化参数方法，类型默认文本(type=0)
     *
     * @param name
     * @param value
     * @return
     */
    public static PDFParam instance(String name, String value) {
        PDFParam param = new PDFParam();
        param.setName(name);
        param.setValue(value);
        return param;
    }
}
