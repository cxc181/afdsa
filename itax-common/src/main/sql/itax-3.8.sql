#园区税单表
ALTER TABLE `t_park_tax_bill`
ADD COLUMN `cancellation_company`  int(11)  DEFAULT '0' COMMENT '作废/红冲企业' AFTER `cur_file_url`;

#园区税单变更记录表
ALTER TABLE `t_e_park_tax_bill_change`
ADD COLUMN `cancellation_company`  int(11)  DEFAULT '0' COMMENT '作废/红冲企业' AFTER `cur_file_url`;

#企业税单表
ALTER TABLE `t_e_company_tax_bill`
ADD COLUMN `cancellation_amount`  bigint(20)  DEFAULT '0' COMMENT '作废/红冲金额' AFTER `vat_vouchers_status`;

#开票订单表
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `cancellation_voucher`  text NULL COMMENT '作废/红冲凭证' AFTER `payment_voucher`,
ADD COLUMN `invoice_mark` int(1) NULL DEFAULT 0 COMMENT '发票标识 0-正常 1-已作废/红冲 2-作废重开' AFTER `cancellation_voucher`,
ADD COLUMN `cancellation_time` datetime DEFAULT NULL COMMENT '作废时间' AFTER `invoice_mark`,
 ADD COLUMN `cancellation_remark` varchar(128) DEFAULT NULL COMMENT '作废/红冲说明' AFTER `cancellation_time`,
ADD COLUMN `relevance_order_no` varchar(32) DEFAULT NULL COMMENT '作废重开关联订单号' AFTER `cancellation_remark`;

#开票订单变更表
ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `cancellation_voucher`  text NULL COMMENT '作废/红冲凭证' AFTER `payment_voucher`,
ADD COLUMN `invoice_mark` int(1) NULL DEFAULT 0 COMMENT '发票标识 0-正常 1-已作废/红冲 2-作废重开' AFTER `cancellation_voucher`,
ADD COLUMN `cancellation_time` datetime DEFAULT NULL COMMENT '作废时间' AFTER `invoice_mark`,
ADD COLUMN `relevance_order_no` varchar(32) DEFAULT NULL COMMENT '作废重开关联订单号' AFTER `cancellation_time`;

####云财在线近12个月开票记录
CREATE OR REPLACE VIEW COMPANY_INVOICE_RECORD_STATISTICS_VIEW AS
SELECT
	invrecode.member_id user_id, #会员id
	invrecode.company_id, #企业id
	IFNULL(a.use_total_invoice_amount,0) use_total_invoice_amount, #近12个月已开票金额
	IFNULL(a.use_invoice_amount_quarter_pp,0) use_invoice_amount_quarter_pp, #本季度已开普票金额
	IFNULL(a.use_invoice_amount_quarter_zp,0) use_invoice_amount_quarter_zp, #本季度已开专票金额
	invrecode.use_invoice_amount use_invoice_amount_year, #本年已开票金额（企业注册时间往后推一年）
	invrecode.end_time #企业本年有效截至时间
FROM
(SELECT r.*,c.member_id FROM t_e_company_invoice_record r ,t_e_member_company c WHERE r.company_id = c.id and r.end_time >= DATE_FORMAT(NOW(),'%Y-%m-%d') GROUP BY company_id HAVING r.id = min(r.id)) invrecode
 left join
	(
		SELECT o.user_id, i.company_id,
			sum( CASE WHEN o.add_time >= DATE_FORMAT( date_add(NOW(), INTERVAL - 11 MONTH), '%Y-%m-01' ) THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_total_invoice_amount,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 1 THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_invoice_amount_quarter_pp,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 2 THEN i.invoice_amount ELSE 0 END ) use_invoice_amount_quarter_zp
		FROM
			t_e_order o,
			t_e_invoice_order i,
            t_e_member_company c
		WHERE o.order_no = i.order_no AND o.order_status NOT IN (8,10) AND o.user_type = 1 and c.id = i.company_id and (c.ein is null or c.ein = '') and i.invoice_mark <> 1 GROUP BY o.user_id, i.company_id
union
select c1.member_id user_id,c1.id company_id,use_total_invoice_amount,use_invoice_amount_quarter_pp,use_invoice_amount_quarter_zp from t_e_member_company c1,(
SELECT o.user_id, i.company_id,c.ein,
			sum( CASE WHEN o.add_time >= DATE_FORMAT( date_add(NOW(), INTERVAL - 11 MONTH), '%Y-%m-01' ) THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_total_invoice_amount,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 1 THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_invoice_amount_quarter_pp,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 2 THEN i.invoice_amount ELSE 0 END ) use_invoice_amount_quarter_zp
		FROM
			t_e_order o,
			t_e_invoice_order i,
      t_e_member_company c
		WHERE o.order_no = i.order_no AND o.order_status NOT IN (8,10) AND o.user_type = 1 and c.id = i.company_id and (c.ein is not null and c.ein != '') and i.invoice_mark <> 1
     GROUP BY c.ein
) t where c1.ein = t.ein) a
on  a.company_id = invrecode.company_id;
#订单作废通知模板
INSERT into sys_e_dictionary (dict_code,dict_value,dict_desc,add_time,add_user) VALUES('to_void_message','您有一笔开票订单已作废/红冲，税费将在本季度税单进行结算，可前往开票订单列表查看~','订单作废通知',NOW(),'admin'
);
INSERT INTO `sys_e_dictionary` ( `dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('cancellation', '您有一笔作废重开的开票订单需确认，请尽快前往处理哦~', NULL, '订单作废重开通知', NOW(), 'admin', NULL, NULL, NULL);
#订单作废重开短信模板
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`)
SELECT oem_code, '42', '您有一笔发票作废/红冲后重开的开票订单需确认，请尽快处理哦~', '1', NOW(), 'admin'
FROM t_e_oem WHERE oem_code is not null and oem_code != '';


#========企业税单添加 成本项管理 202112132030==========
ALTER TABLE `t_e_company_tax_bill`
ADD COLUMN `cost_amount`  bigint(20) NULL default 0 COMMENT '成本金额（分）' AFTER `cancellation_amount`;


CREATE TABLE `t_e_company_tax_cost_item` (
`id`  bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id' ,
`company_tax_id`  bigint(11) NULL COMMENT '企业税单id' ,
`industry_id`  bigint(11) NULL COMMENT '行业id' ,
`cost_item_name`  varchar(64) NULL COMMENT '成本项名称' ,
`cost_item_rate`  decimal(8,4) NULL COMMENT '成本项比例' ,
`cost_item_amount`  bigint(20) NULL COMMENT '成本项金额（分）' ,
`add_time` datetime DEFAULT NULL COMMENT '添加时间',
`add_user` varchar(32) DEFAULT NULL COMMENT '添加人',
`update_time` datetime DEFAULT NULL COMMENT '修改时间',
`update_user` varchar(32) DEFAULT NULL COMMENT '修改人',
`remark` varchar(64) DEFAULT NULL COMMENT '备注',
PRIMARY KEY (`id`)
)ENGINE=InnoDB COMMENT='企业税单成本项表';

ALTER TABLE `t_e_company_tax_bill`
MODIFY COLUMN `cost_amount` bigint(20) NULL DEFAULT NULL COMMENT '年度累计成本金额（分）' AFTER `cancellation_amount`,
ADD COLUMN `income_amount` bigint(20) NULL COMMENT '年度累计收入金额（分）' AFTER `cost_amount`;

