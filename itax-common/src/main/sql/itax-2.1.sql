#======V2.1版本数据脚本

#开票订单表增加索引
ALTER TABLE t_e_invoice_order
ADD INDEX (group_order_no, oem_code) ;

#行业例外表增加索引
ALTER TABLE t_r_oem_park_industry_blacklist
ADD INDEX (oem_code, park_id, industry_id) ;

