package com.yuqian.itax.system.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 税收分类编码与增值税率的关系表
 *
 * @Date: 2022年08月19日 11:38:14
 * @author cxz
 */
@Getter
@Setter
@Table(name="t_r_classification_code_vat")
public class ClassificationCodeVatEntity implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 税收分类编码
     */
    private String taxClassificationCode;

    /**
     * 增值税率
     */
    private BigDecimal vatFeeRate;

    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 添加人
     */
    private String addUser;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 备注
     */
    private String remark;

}
