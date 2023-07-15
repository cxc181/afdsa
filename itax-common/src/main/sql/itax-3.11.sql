#成本项基础表
drop table if exists t_e_cost_item_base;
CREATE TABLE `t_e_cost_item_base` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `cost_item_name` varchar(64) DEFAULT NULL COMMENT '成本项名称',
  `cost_item_rate` decimal(8,4) DEFAULT NULL COMMENT '成本项比例',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `add_user` varchar(32) DEFAULT NULL COMMENT '添加人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '修改人',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) COMMENT='成本项基础表';

#会员成本项表
drop table if exists t_e_member_cost_item;
CREATE TABLE `t_e_member_cost_item` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
	`member_id` bigint(11) NOT NULL COMMENT '会员id',
  `cost_item_name` varchar(64) DEFAULT NULL COMMENT '成本项名称',
  `cost_item_rate` decimal(8,4) DEFAULT NULL COMMENT '成本项比例',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `add_user` varchar(32) DEFAULT NULL COMMENT '添加人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '修改人',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) COMMENT='会员成本项表';

#税费政策
ALTER TABLE `t_e_tax_policy`
MODIFY COLUMN `levy_way`  int(1) NULL DEFAULT 1 COMMENT '计税方式（1：预缴征收率，2：核定应税所得率）' AFTER `company_type`,
MODIFY COLUMN `income_levy_type`  int(1) NULL DEFAULT 1 COMMENT '所得税征收方式 1-查账征收 2-核定征收' AFTER `policy_file_url`;

#园区管理
ALTER TABLE `t_e_park`
ADD COLUMN `income_levy_type`  int(1) NULL DEFAULT 1 COMMENT '所得税征收方式 1-查账征收 2-核定征收' AFTER `special_considerations`;

#我的企业
ALTER TABLE `t_e_member_company`
ADD COLUMN `cancel_remark`  varchar(128) NULL COMMENT '注销说明' AFTER `tax_reg_date`,
ADD COLUMN `sign_img`  varchar(128) NULL COMMENT '注册签名图片' AFTER `cancel_remark`;

#订单主表
ALTER TABLE `t_e_order`
MODIFY COLUMN `order_status`  int(2) NOT NULL COMMENT '订单状态：工商注册(0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，10-待设立登记、11-待提交签名、12-签名待确认),\r\n开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过),\r\n会员升级( 0-待支付,1-支付中,2-财务审核,3-已完成,4-已取消), \r\n充值提现、对公户提现、补税，退税(0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败),工商注销(0-待付款,1-注销处理中,2-注销成功,3-已取消 4-税单待处理),证件申请( 0-待付款 1-待发货,2-出库中,3-待签收,4-已签收,5-已取消),公户申请（0-待付款,1-等待预约,2-已完成,3-已取消）消费开票（0-待出票 1-出票中 2-已完成 3-出票失败 4-待发货 5-待签收）托管费续费、对公户续费（0-待支付、1-支付中、2-已完成、3-已取消）' AFTER `order_type`;

#企业注销订单变更记录
ALTER TABLE `t_e_company_cancel_order_change_record`
MODIFY COLUMN `order_status`  int(2) NULL DEFAULT NULL COMMENT '订单状态 0-待付款 1-注销处理中 2-注销成功 3-已取消 4-税单待处理' AFTER `oem_code`;

#企业税单表
ALTER TABLE `t_e_company_tax_bill`
MODIFY COLUMN `tax_bill_status`  int(1) NULL DEFAULT NULL COMMENT '税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税 6-待核对   7-待填报成本 8-待申报 9-已作废' AFTER `vat_supplement_tax_money`,
MODIFY COLUMN `income_levy_way`  int(1) NULL DEFAULT NULL COMMENT '计税方式（1：预缴征收率，2：核定应税所得率）' AFTER `income_taxable_income_amount`,
CHANGE COLUMN `cost_amount` `year_cost_amount`  bigint(11) NULL DEFAULT NULL COMMENT '年度累计成本金额（分）' AFTER `cancellation_amount`,
CHANGE COLUMN `income_amount` `year_income_amount`  bigint(11) NULL DEFAULT NULL COMMENT '年度累计收入金额（分）' AFTER `year_cost_amount`,
ADD COLUMN `ticket_pic`  varchar(128) NULL COMMENT '罚款凭证' AFTER `vat_voucher_pic`,
ADD COLUMN `cost_item_imgs`  text NULL COMMENT '成本项图片' AFTER `year_income_amount`,
ADD COLUMN `ticket_free_income_amount`  bigint(11) NULL COMMENT '无票收入金额（分）' AFTER `cost_item_imgs`,
ADD COLUMN `vat_deduction_amount`  bigint(11) NULL COMMENT '增值税&附加税扣除金额（分）' AFTER `ticket_free_income_amount`,
ADD COLUMN `iit_deduction_amount`  bigint(11) NULL COMMENT '个税扣除金额（分）' AFTER `vat_deduction_amount`,
ADD COLUMN `quarter_cost_amount`  bigint(11) NULL COMMENT '本季累计成本金额（分）' AFTER `iit_deduction_amount`,
ADD COLUMN `quarter_income_amount`  bigint(11) NULL COMMENT '季度累计收入金额（分）' AFTER `quarter_cost_amount`,
ADD COLUMN `sign_img`  varchar(128) NULL COMMENT '用户签名' AFTER `quarter_income_amount`,
ADD COLUMN `income_levy_type`  int(1) NULL COMMENT '所得税征收方式 1-查账征收 2-核定征收' AFTER `sign_img`,
ADD COLUMN `year_payable_income_tax`  bigint(20) DEFAULT 0 NULL COMMENT '本年应缴所得税（分）' AFTER `income_levy_type`;

ALTER TABLE `t_e_company_tax_bill`
MODIFY COLUMN `tax_bill_status`  int(1) NULL DEFAULT NULL COMMENT '税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税 6-待核对   7-待填报成本 8-待申报 9-已作废' AFTER `vat_supplement_tax_money`,
MODIFY COLUMN `income_levy_way`  int(1) NULL DEFAULT NULL COMMENT '计税方式（1：预缴征收率，2：核定应税所得率）' AFTER `income_taxable_income_amount`,
MODIFY COLUMN `over_time_is_sms`  int(1) NULL DEFAULT NULL COMMENT '超过15天未补交是否已经发短信提醒' AFTER `income_supplement_tax_money`,
MODIFY COLUMN `iit_vouchers_status`  int(1) NULL DEFAULT NULL COMMENT '个人所得税凭证上传状态 1-未上传 2-已上传 3-无需上传' AFTER `ticket_pic`,
MODIFY COLUMN `vat_vouchers_status`  int(1) NULL DEFAULT NULL COMMENT ' 增值税凭证上传状态 1-未上传 2-已上传 3-无需上传' AFTER `iit_vouchers_status`,
MODIFY COLUMN `ticket_free_income_amount`  bigint(20) NULL DEFAULT NULL COMMENT '无票收入金额（分）' AFTER `cost_item_imgs`,
MODIFY COLUMN `vat_deduction_amount`  bigint(20) NULL DEFAULT NULL COMMENT '增值税&附加税扣除金额（分）' AFTER `ticket_free_income_amount`,
MODIFY COLUMN `iit_deduction_amount`  bigint(20) NULL DEFAULT NULL COMMENT '个税扣除金额（分）' AFTER `vat_deduction_amount`,
MODIFY COLUMN `quarter_cost_amount`  bigint(20) NULL DEFAULT NULL COMMENT '本季累计成本金额（分）' AFTER `iit_deduction_amount`,
MODIFY COLUMN `quarter_income_amount`  bigint(20) NULL DEFAULT NULL COMMENT '季度累计收入金额（分）' AFTER `quarter_cost_amount`,
MODIFY COLUMN `sign_img`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户签名' AFTER `quarter_income_amount`;

#园区税单
ALTER TABLE `t_park_tax_bill`
MODIFY COLUMN `vouchers_status`  int(1) NULL DEFAULT 0 COMMENT '凭证状态 0-未上传 1-解析中 2-已上传3-部分已上传' AFTER `already_upload_iit_vouchers_company_number`,
MODIFY COLUMN `tax_bill_status`  int(1) NULL DEFAULT NULL COMMENT '税单状态 0-待确认 1-解析中 2-待上传 3-已确认 4-待凭证上传 5-推送中 6-已推送 7-待核对 8-待处理 9-已处理' AFTER `vouchers_status`,
ADD COLUMN `income_levy_type`  int(1) NULL COMMENT '所得税征收方式 1-查账征收 2-核定征收' AFTER `tax_bill_status`;

#园区税单变更表
ALTER TABLE `t_e_park_tax_bill_change`
MODIFY COLUMN `vouchers_status`  int(1) NULL DEFAULT 0 COMMENT '凭证状态 0-未上传 1-解析中 2-已上传3-部分已上传' AFTER `already_upload_iit_vouchers_company_number`,
MODIFY COLUMN `tax_bill_status`  int(1) NULL DEFAULT NULL COMMENT '税单状态 0-待确认 1-解析中 2-待上传 3-已确认 4-待凭证上传 5-推送中 6-已推送 7-待核对 8-待处理 9-已处理' AFTER `vouchers_status`,
ADD COLUMN `income_levy_type`  int(1) NULL COMMENT '所得税征收方式 1-查账征收 2-核定征收' AFTER `tax_bill_status`;

#园区操作截止时间配置
ALTER TABLE `t_e_park_endtime_config`
MODIFY COLUMN `oper_type`  int(1) NULL DEFAULT NULL COMMENT '操作类型 1-创建开票提示 2-开票记录自动开票 3-成本确认' AFTER `park_id`,
ADD COLUMN `year`  int(4) NULL COMMENT '年份' AFTER `content`,
ADD COLUMN `quarter`  int(1) NULL COMMENT '季度' AFTER `year`;
# 无票收入金额最大值
INSERT INTO `sys_e_dictionary` ( `dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('without_ticket_amount_max', '500000000', NULL, '无票收入金额最大值', now(), 'admin', now(), NULL, NULL);
# 税单完税凭证解析记录表添加罚款凭证地址
ALTER TABLE `t_e_tax_bill_credentials_record`
ADD COLUMN `ticket_voucher_pic`  VARCHAR(256)  COMMENT '罚款凭证地址';
# 增值税&附加税扣除金额最大金额
INSERT INTO `sys_e_dictionary` ( `dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('vat_deduction_amount_max', '500000000', NULL, '增值税&附加税扣除金额最大金额', now(), 'admin', now(), NULL, NULL);
#企业税单表添加订单编号
ALTER TABLE `t_e_company_tax_bill`
ADD COLUMN `order_no`  varchar(32)   COMMENT '订单编号';
#企业税单表添加生成方式
ALTER TABLE `t_e_company_tax_bill`
ADD COLUMN `generate_type`  int(1)   COMMENT '生成方式 1季度自动生成 2企业注销生成';
#短信模板表修改
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`)
SELECT oem_code, '46', '尊敬的用户，#taxBillSeasonal#季度税单已生成，请前往小程序填写成本明细，并签字确认，截止日期为#year#年#month#月#day#号，为了避免超时提交造成申报逾期，请尽快前往处理哦~', '1', NOW(), 'admin'
    FROM t_e_oem WHERE oem_code is not null and oem_code != '';
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`)
SELECT oem_code, '43', '尊敬的用户，#taxBillSeasonal#季度税单离确认成本截止日期仅剩#remindDay#天，未填报成本或完成补税则无法进行申报，并可能产生罚款、转非正常户、影响征信等一系列影响，请尽快前往处理哦~', '1', NOW(), 'admin'
    FROM t_e_oem WHERE oem_code is not null and oem_code != '';
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`)
SELECT oem_code, '44', '尊敬的用户，您有个体户超时未确认成本，将无法按期申报，请前往查看哦~', '1', NOW(), 'admin'
    FROM t_e_oem WHERE oem_code is not null and oem_code != '';
#短信模板添加
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`)
SELECT oem_code, '45', '尊敬的用户，您的个体户#taxBillSeasonal#税期已完成报税，可前往税单查看哦~', '1', NOW(), 'admin'
FROM t_e_oem WHERE oem_code is not null and oem_code != '';
#字典表
INSERT INTO `sys_e_dictionary` (`id`, `dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES (126, 'notice_template_by_accounts', '#taxBillSeasonal#季度税单已生成，请前往税单确认成本，截止日期为#year#年#month#月#day#号，为了避免超时提交造成申报逾期，请尽快前往处理哦~', NULL, '税单通知模板查账征收', '2022-03-31 22:24:50', 'admin', NULL, NULL, NULL);
#报税完成通知标题
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('declaration_completed_title', '您的个体户#companyName#的#taxBillSeasonal#税期已完成报税，可前往税单查看哦~', NULL, NULL, now(), 'admin', NULL, NULL, NULL);
#报税完成通知内容
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'declaration_completed_content', '尊敬的用户，您的个体户#taxBillSeasonal#税期已完成报税，可前往税单查看哦~', NULL, NULL, now(), 'admin', NULL, NULL, NULL);
INSERT INTO sys_e_dictionary (dict_code,dict_value,dict_desc,add_time,add_user) VALUES('confirm_cost_remind_days','3','确认成本通知提醒提前天数',NOW(),'admin');
INSERT INTO sys_e_dictionary (dict_code,dict_value,dict_desc,add_time,add_user) VALUES('confirm_cost_remind','{"noticeTitle": "您有未确认成本的税单","noticeSubtitle": "#taxBillSeasonal#季度税单离确认成本截止日期仅剩#remindDay#天，未填报成本或完成补税则无法进行申报，并可能产生罚款、转非正常户、影响征信等一系列影响，请尽快前往处理哦~","noticeContent": "尊敬的用户，#taxBillSeasonal#季度税单离确认成本截止日期仅剩#remindDay#天，未填报成本或完成补税则无法进行申报，并可能产生罚款、转非正常户、影响征信等一系列影响，请尽快前往处理哦~"}','确认成本即将超时通知模板',NOW(),'admin');
INSERT INTO sys_e_dictionary (dict_code,dict_value,dict_desc,add_time,add_user) VALUES('confirm_overtime_remind','{"noticeTitle": "申报超时风险提示","noticeSubtitle": "您有个体户超时未确认成本，可能逾期申报，请尽快前往税单处理哦~","noticeContent": "尊敬的用户，您有个体户超时未确认成本，将无法按期申报，请尽快前往处理哦~"}','确认成本即将超时通知模板',NOW(),'admin');
INSERT INTO sys_e_dictionary (dict_code,dict_value,dict_desc,add_time,add_user) VALUES('year_income_tax_halve_amount',100000000,'所得税减半应纳税所得额限额',NOW(),'admin');

#企业税单
ALTER TABLE `t_e_company_tax_bill`
CHANGE COLUMN `vat_deduction_amount` `vat_deduction_taxfee`  bigint(20) NULL DEFAULT NULL COMMENT '增值税扣除税费（分）' AFTER `ticket_free_income_amount`,
ADD COLUMN `additional_deduction_taxfee`  bigint(20) NULL COMMENT '附加税扣除税费(分)' AFTER `vat_deduction_taxfee`;


