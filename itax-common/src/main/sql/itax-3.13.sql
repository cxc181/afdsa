#园区表
ALTER TABLE `t_e_park`
ADD COLUMN `park_type`  int(1) NULL DEFAULT 1 COMMENT '园区类型 1-自营园区  2-合作园区 3-外部园区' AFTER `park_code`;

#税费政策
ALTER TABLE `t_e_tax_policy`
ADD COLUMN `quarter_invoice_amount`  bigint(11) NULL COMMENT '季开票额度' AFTER `total_invoice_amount`;

#机构表
ALTER TABLE `t_e_oem`
ADD COLUMN `is_send_audit_bills_message`  int(1) NULL DEFAULT 1 COMMENT '是否发送查账征收税单消息   0-不发送  1-发送' AFTER `official_seal_img`;

#企业税单表
ALTER TABLE `t_e_company_tax_bill`
ADD COLUMN `income_tax_refund_amount`  bigint(20) NULL DEFAULT 0 COMMENT '个税可退税额' AFTER `year_payable_income_tax`,
ADD COLUMN `income_tax_year_freeze_amount`  bigint(20) NULL DEFAULT 0 COMMENT '个税年度冻结税额' AFTER `income_tax_refund_amount`,
ADD COLUMN `is_send_notice`  INT(1) NULL DEFAULT 0 COMMENT '是否发送超时确认成本提醒通知 0-未发送 1-已发送' AFTER `income_tax_year_freeze_amount`;

#企业税单变动表
ALTER TABLE `t_e_company_tax_bill_change`
ADD COLUMN `income_tax_refund_amount`  bigint(20) NULL DEFAULT 0 COMMENT '个税可退税额' AFTER `descrip`,
ADD COLUMN `income_tax_year_freeze_amount`  bigint(20) NULL DEFAULT 0 COMMENT '个税年度冻结税额' AFTER `income_tax_refund_amount`,
ADD COLUMN `is_send_notice`  INT(1) NULL DEFAULT 0 COMMENT '是否发送超时确认成本提醒通知 0-未发送 1-已发送' AFTER `income_tax_year_freeze_amount`;

#短信表修改
update t_e_sms_template set template_content='尊敬的用户，#parkName#的#taxBillSeasonal#季度税单已生成，请前往小程序填写成本明细，并签字确认，截止日期为#year#年#month#月#day#号，为了避免超时提交造成申报逾期，请尽快前往处理哦~' ,
  update_time = now(),update_user='admin',remark='内容修改'
 where template_type = 46;

 # 配置表修改
update sys_e_dictionary set dict_value= '#parkName#的#taxBillSeasonal#季度税单已生成，请前往税单确认成本，截止日期为#year#年#month#月#day#号，为了避免超时提交造成申报逾期，请尽快前往处理哦~',
	update_time = now(),update_user='admin',remark='内容修改'
 where dict_code = 'notice_template_by_accounts';

#注册驳回通知修改
update sys_e_dictionary set dict_value = '您申请企业的资料不符合市场监督管理局要求被驳回，请重新提交！' where dict_code = 'register_rejected_notice_tmpl';

# 查账征收2022年一季度税单，所得税应退>0时，个税年度冻结税额等于所得税应退税额
UPDATE t_e_company_tax_bill SET income_tax_year_freeze_amount = income_recoverable_tax_money WHERE tax_bill_year = 2022 AND tax_bill_seasonal = 1 AND income_levy_type = 1 AND income_recoverable_tax_money > 0;





