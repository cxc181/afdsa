#======V1.0.6版本数据脚本
#工单表
ALTER TABLE `t_e_work_order`
DROP INDEX `order_no` ,
ADD INDEX `order_no` (`order_no`, `work_order_type`) USING BTREE ;

#短信模板表
INSERT INTO t_e_sms_template (oem_code, template_type, template_content, status, add_time, add_user) VALUES ('YCS', '26', '您补传的#companyName#的收款流水审核未通过，请您尽快前往小程序查看原因并重新上传哦~', '1', '2020-05-07 15:00:55', 'admin')
UPDATE `t_e_sms_template`
SET `template_content` = '您有一笔个体工商户户注册订单经客户经理与您确认后已取消，支付费用已退回至小程序钱包，您可前往小程序“我的-我的钱包”查看，如有疑问，请联系客服#telPhone#。'
WHERE `template_type` = '10';

