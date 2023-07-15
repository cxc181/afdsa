#工商注册订单
ALTER TABLE `t_e_register_order`
ADD COLUMN `taxpayer_type`  int(1) NULL DEFAULT 1 COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `registered_capital`;

#工商注册订单变更记录
ALTER TABLE `t_e_register_order_change_record`
ADD COLUMN `taxpayer_type`  int(1) NULL DEFAULT 1 COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `registered_capital`;

#工商注册预订单
ALTER TABLE `t_e_register_pre_order`
ADD COLUMN `taxpayer_type`  int(1) NULL DEFAULT 1 COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `registered_capital`;

#企业注销订单表
ALTER TABLE `t_e_company_cancel_order`
ADD COLUMN `taxpayer_type`  int(1) NULL DEFAULT 1 COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `remark`;

#企业注销订单变更记录
ALTER TABLE `t_e_company_cancel_order_change_record`
ADD COLUMN `taxpayer_type`  int(1) NULL DEFAULT 1 COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `remark`;

#企业核心成员信息表
ALTER TABLE `t_e_company_core_personnel`
MODIFY COLUMN `personnel_type`  int(1) NULL DEFAULT NULL COMMENT '成员类型 1-法人 2-监事 3-财务 4-无职务股东' AFTER `company_type`,
ADD COLUMN `is_shareholder`  int(1) NULL COMMENT '是否股东 0-否 1-是' AFTER `personnel_type`;

#企业税务托管表
ALTER TABLE `t_e_company_tax_hosting`
MODIFY COLUMN `channel`  int(1) NULL DEFAULT NULL COMMENT '通道方 1-百旺 2-园区' AFTER `face_amount`;

#税费政策表
ALTER TABLE `t_e_tax_policy`
ADD COLUMN `taxpayer_type`  int(1) NULL DEFAULT 1 COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `remark`,
ADD COLUMN `park_policy_desc`  varchar(128) NULL COMMENT '园区政策说明' AFTER `taxpayer_type`,
ADD COLUMN `special_considerations`  varchar(256) NULL COMMENT '特殊事项说明' AFTER `park_policy_desc`,
ADD COLUMN `stamp_duty_breaks_cycle`  int(1) NULL COMMENT '印花税申报周期 1-按月 2-按季度' AFTER `special_considerations`,
ADD COLUMN `is_stamp_duty_halved`  int(1) NULL DEFAULT 0 COMMENT '印花税是否减半 0-否 1-是' AFTER `stamp_duty_breaks_cycle`,
ADD COLUMN `water_conservancy_fund_breaks_cycle`  int(1) NULL COMMENT '水利建设基金申报周期 1-按月 2-按季度' AFTER `is_stamp_duty_halved`,
ADD COLUMN `is_water_conservancy_fund_halved`  int(1) NULL DEFAULT 0 COMMENT '水利建设基金是否减半 0-否 1-是' AFTER `water_conservancy_fund_breaks_cycle`;

#税费政策变更表
ALTER TABLE `t_e_tax_policy_change`
ADD COLUMN `taxpayer_type`  int(1) NULL DEFAULT 1 COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `remark`,
ADD COLUMN `park_policy_desc`  varchar(128) NULL COMMENT '园区政策说明' AFTER `taxpayer_type`,
ADD COLUMN `special_considerations`  varchar(256) NULL COMMENT '特殊事项说明' AFTER `park_policy_desc`,
ADD COLUMN `stamp_duty_breaks_cycle`  int(1) NULL COMMENT '印花税申报周期 1-按月 2-按季度' AFTER `special_considerations`,
ADD COLUMN `is_stamp_duty_halved`  int(1) NULL DEFAULT 0 COMMENT '印花税是否减半 0-否 1-是' AFTER `stamp_duty_breaks_cycle`,
ADD COLUMN `water_conservancy_fund_breaks_cycle`  int(1) NULL COMMENT '水利建设基金申报周期 1-按月 2-按季度' AFTER `is_stamp_duty_halved`,
ADD COLUMN `is_water_conservancy_fund_halved`  int(1) NULL DEFAULT 0 COMMENT '水利建设基金是否减半 0-否 1-是' AFTER `water_conservancy_fund_breaks_cycle`;

#税费规则配置
ALTER TABLE `t_e_tax_rules_config`
ADD COLUMN `taxpayer_type`  int(1) NULL COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `is_open_zp`;
ALTER TABLE `t_e_tax_rules_config`
MODIFY COLUMN `tax_type`  int(1) NOT NULL COMMENT '税种类型 1-所得税 2-增值税 3-附加税 4-印花税 5-水利建设基金' AFTER `max_amount`;



#我的企业
ALTER TABLE `t_e_member_company`
ADD COLUMN `user_agreement_imgs`  varchar(1024) NULL COMMENT '委托注册协议图片' AFTER `approved_turnover`,
ADD COLUMN `taxpayer_type`  int(1) NULL DEFAULT 1 COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `user_agreement_imgs`;

#企业变更表
ALTER TABLE `t_e_member_company_change`
ADD COLUMN `user_agreement_imgs`  varchar(1024) NULL COMMENT '委托注册协议图片' AFTER `approved_turnover`,
ADD COLUMN `taxpayer_type`  int(1) NULL DEFAULT 1 COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `user_agreement_imgs`;

#订单主表
ALTER TABLE `t_e_order`
MODIFY COLUMN `order_status`  int(2) NOT NULL COMMENT '订单状态：工商注册(0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，10-待设立登记、11-待提交签名、12-签名待确认),\r\n开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过 11-待财务审核),\r\n会员升级( 0-待支付,1-支付中,2-财务审核,3-已完成,4-已取消), \r\n充值提现、对公户提现、补税，退税(0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败),工商注销(0-待付款,1-注销处理中,2-注销成功,3-已取消 4-税单待处理),证件申请( 0-待付款 1-待发货,2-出库中,3-待签收,4-已签收,5-已取消),公户申请（0-待付款,1-等待预约,2-已完成,3-已取消）消费开票（0-待出票 1-出票中 2-已完成 3-出票失败 4-待发货 5-待签收）托管费续费、对公户续费（0-待支付、1-支付中、2-已完成、3-已取消）' AFTER `order_type`;

#开票订单
ALTER TABLE `t_e_invoice_order`
MODIFY COLUMN `payment_voucher`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付凭证' AFTER `goods_details`,
ADD COLUMN `is_reopen`  int(1) NULL DEFAULT 0 COMMENT '发票是否已重开 0-否 1-是' AFTER `is_recalculate_service_fee`,
ADD COLUMN `stamp_duty_rate`  decimal(10,8) NULL COMMENT '印花税税率' AFTER `is_reopen`,
ADD COLUMN `stamp_duty_amount`  bigint NULL COMMENT '应缴印花税金额' AFTER `stamp_duty_rate`,
ADD COLUMN `water_conservancy_fund_rate`  decimal(10,8) NULL COMMENT '水利建设基金税率' AFTER `stamp_duty_amount`,
ADD COLUMN `water_conservancy_fund_amount`  bigint NULL COMMENT '应缴水利建设基金金额' AFTER `water_conservancy_fund_rate`,
ADD COLUMN `taxpayer_type`  int(1) NULL DEFAULT 1 COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `water_conservancy_fund_amount`,
ADD COLUMN `receipt_payment_voucher`  varchar(1024) NULL COMMENT '收款凭证' AFTER `taxpayer_type`,
ADD COLUMN `VAT_breaks_cycle` int(1) DEFAULT NULL COMMENT '增值税申报周期 1-按月 2-按季度' AFTER `receipt_payment_voucher`,
ADD COLUMN `surcharge_breaks_cycle` int(1) DEFAULT NULL COMMENT '附加税申报周期 1-按月 2-按季度' AFTER `VAT_breaks_cycle`,
ADD COLUMN `stamp_duty_breaks_cycle` int(1) DEFAULT NULL COMMENT '印花税申报周期 1-按月 2-按季度' AFTER `surcharge_breaks_cycle`,
ADD COLUMN `water_conservancy_fund_breaks_cycle` int(1) DEFAULT NULL COMMENT '增值税申报周期 1-按月 2-按季度' AFTER `stamp_duty_breaks_cycle`,
ADD COLUMN `income_tax_breaks_cycle` int(1) DEFAULT NULL COMMENT '所得税申报周期 1-按月 2-按季度' AFTER `water_conservancy_fund_breaks_cycle`;

#开票订单变更记录
ALTER TABLE `t_e_invoice_order_change_record`
MODIFY COLUMN `payment_voucher`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付凭证' AFTER `goods_details`,
ADD COLUMN `is_reopen`  int(1) NULL DEFAULT 0 COMMENT '发票是否已重开 0-否 1-是' AFTER `is_recalculate_service_fee`,
ADD COLUMN `stamp_duty_rate`  decimal(10,8) NULL COMMENT '印花税税率' AFTER `is_reopen`,
ADD COLUMN `stamp_duty_amount`  bigint NULL COMMENT '应缴印花税金额' AFTER `stamp_duty_rate`,
ADD COLUMN `water_conservancy_fund_rate`  decimal(10,8) NULL COMMENT '水利建设基金税率' AFTER `stamp_duty_amount`,
ADD COLUMN `water_conservancy_fund_amount`  bigint NULL COMMENT '应缴水利建设基金金额' AFTER `water_conservancy_fund_rate`,
ADD COLUMN `taxpayer_type`  int(1) NULL DEFAULT 1 COMMENT '纳税人类型  1-小规模纳税人 2-一般纳税人' AFTER `water_conservancy_fund_amount`,
ADD COLUMN `receipt_payment_voucher`  varchar(1024) NULL COMMENT '收款凭证' AFTER `taxpayer_type`,
ADD COLUMN `VAT_breaks_cycle` int(1) DEFAULT NULL COMMENT '增值税申报周期 1-按月 2-按季度' AFTER `receipt_payment_voucher`,
ADD COLUMN `surcharge_breaks_cycle` int(1) DEFAULT NULL COMMENT '附加税申报周期 1-按月 2-按季度' AFTER `VAT_breaks_cycle`,
ADD COLUMN `stamp_duty_breaks_cycle` int(1) DEFAULT NULL COMMENT '印花税申报周期 1-按月 2-按季度' AFTER `surcharge_breaks_cycle`,
ADD COLUMN `water_conservancy_fund_breaks_cycle` int(1) DEFAULT NULL COMMENT '增值税申报周期 1-按月 2-按季度' AFTER `stamp_duty_breaks_cycle`,
ADD COLUMN `income_tax_breaks_cycle` int(1) DEFAULT NULL COMMENT '所得税申报周期 1-按月 2-按季度' AFTER `water_conservancy_fund_breaks_cycle`,
ADD COLUMN `cancellation_remark`  varchar(255) NULL COMMENT '作废/红冲说明' AFTER `income_tax_breaks_cycle`;

#支付流水
ALTER TABLE `t_e_pay_water`
MODIFY COLUMN `pay_water_type`  int(1) NOT NULL COMMENT '流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款 6-对公户提现 7-企业退税 8-线下支付' AFTER `order_type`,
MODIFY COLUMN `pay_way`  int(1) NOT NULL COMMENT '支付方式  1-微信 2-余额 3-支付宝 4-快捷支付 5-字节跳动 6-线下转账' AFTER `pay_water_type`,
MODIFY COLUMN `pay_channels`  int(1) NOT NULL COMMENT '支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗 8-字节跳动支付 9-线下' AFTER `pay_way`,
MODIFY COLUMN `pay_status`  int(1) NOT NULL DEFAULT 0 COMMENT '支付状态 0-待支付 1-支付中 2 -支付成功 3-支付失败 4-待财务审核 5-财务审核失败 6-待财务打款（线下）' AFTER `pay_time`,
ADD COLUMN `refund_voucher`  varchar(1024) NULL COMMENT '退款凭证' AFTER `other_pay_oemcode`;

#机构管理
ALTER TABLE `t_e_oem`
ADD COLUMN `receiving_bank_account_branch`  varchar(256) NULL COMMENT '收款账号开户行' AFTER `is_checkstand`,
ADD COLUMN `receiving_bank_account`  varchar(64) NULL COMMENT '收款银行账号' AFTER `receiving_bank_account_branch`;

#消费开票订单
ALTER TABLE `t_e_consumption_invoice_order`
ADD COLUMN `invoice_company_ein`  varchar(64) NULL COMMENT '开票主体公司税号' AFTER `general_taxpayer_qualification`,
ADD COLUMN `invoice_company_name`  varchar(64) NULL COMMENT '开票主体公司名称' AFTER `invoice_company_ein`;

#消费开票订单变更记录表
ALTER TABLE `t_e_consumption_invoice_order_change`
ADD COLUMN `invoice_company_ein`  varchar(64) NULL COMMENT '开票主体公司税号' AFTER `general_taxpayer_qualification`,
ADD COLUMN `invoice_company_name`  varchar(64) NULL COMMENT '开票主体公司名称' AFTER `invoice_company_ein`;

#税收分类编码与增值税率的关系表
CREATE TABLE `t_r_classification_code_vat` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
	`tax_classification_code`  varchar(32) NULL COMMENT '税收分类编码' ,
	`vat_fee_rate`  decimal(6,4) NULL COMMENT '增值税率' ,
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `add_user` varchar(32) DEFAULT NULL COMMENT '添加人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '修改人',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) COMMENT='税收分类编码与增值税率的关系表';

#税费政策表
ALTER TABLE `t_e_tax_policy`
MODIFY COLUMN `total_invoice_amount`  bigint(11) NULL DEFAULT 0 COMMENT '年度开票总额' AFTER `status`;
ALTER TABLE `t_e_tax_policy`
MODIFY COLUMN `income_tax_breaks_cycle`  int(1) NULL DEFAULT NULL COMMENT '所得税减免周期 1-按月 2-按季度' AFTER `income_tax_breaks_amount`;

#企业年开票记录表
ALTER TABLE `t_e_company_invoice_record`
MODIFY COLUMN `total_invoice_amount` BIGINT(11) NULL COMMENT '年度总开票金额';

#企业表
update t_e_member_company set registered_capital = 20 where company_type = 1;

#企业成员表数据调整
update t_e_company_core_personnel set is_shareholder = 1 where investment_amount >0 ;
update t_e_company_core_personnel set is_shareholder = 0 where investment_amount is null ;
update t_e_company_core_personnel set identity_type = 1 where id_card_front is not null;
update t_e_company_core_personnel set identity_type = 2 where business_license is not null;

#支付流水历史数据处理
# update t_e_pay_water set pay_status=1,remark='历史数据处理,将状态 4改成1 v4.0'   where pay_status = 4;
# update t_e_pay_water set pay_status=3,remark='历史数据处理,将状态 5改成3 v4.0'   where pay_status = 5;

#园区政策配置历史数据处理
update t_e_tax_policy set income_levy_type = 1,remark='历史数据处理v4.0' where company_type != 1;
INSERT INTO t_e_tax_policy( `park_id`, `company_type`, `levy_way`, `VAT_breaks_amount`, `VAT_breaks_cycle`, `income_tax_breaks_amount`, `income_tax_breaks_cycle`,
 `surcharge_breaks_amount`, `surcharge_breaks_cycle`, `transact_require`, `status`, `total_invoice_amount`, `quarter_invoice_amount`, `policy_file_url`,
`income_levy_type`, `read_content`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`, `taxpayer_type`, `park_policy_desc`, `special_considerations`,
 `stamp_duty_breaks_cycle`, `is_stamp_duty_halved`, `water_conservancy_fund_breaks_cycle`, `is_water_conservancy_fund_halved`)
select `park_id`, `company_type`, `levy_way`, `VAT_breaks_amount`, `VAT_breaks_cycle`, `income_tax_breaks_amount`, `income_tax_breaks_cycle`,
 `surcharge_breaks_amount`, `surcharge_breaks_cycle`, `transact_require`, `status`, `total_invoice_amount`, `quarter_invoice_amount`, `policy_file_url`,
 `income_levy_type`, `read_content`, now() `add_time`,'admin' `add_user`, now()`update_time`,'admin' `update_user`, '历史数据处理v4.0'`remark`, 2 `taxpayer_type`,
`park_policy_desc`, `special_considerations`,
 `stamp_duty_breaks_cycle`, `is_stamp_duty_halved`, `water_conservancy_fund_breaks_cycle`, `is_water_conservancy_fund_halved`
from t_e_tax_policy
where company_type != 1 group by park_id,company_type having count(company_type)!=2;

#字典表
INSERT INTO `sys_e_dictionary` (`id`, `dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES (null, 'historical_order_point_in_time', '2022-08-20', NULL, 'V4.0上线历史开票订单划定时间点', now(), 'admin', NULL, NULL, NULL);

update t_e_register_order set registered_capital = 20 where company_type = 1;
update t_e_member_company set registered_capital = 20 where company_type = 1;