#======V2.9.0版本数据脚本
#园区税单变更表
drop table if exists t_e_park_tax_bill_change;
CREATE TABLE `t_e_park_tax_bill_change` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `park_bills_id` bigint(11) DEFAULT NULL COMMENT '园区税单id',
  `tax_bill_year` int(4) DEFAULT NULL COMMENT '税款所属期年',
  `tax_bill_seasonal` int(1) DEFAULT NULL COMMENT '税款所属期-季度',
  `park_id` bigint(20) DEFAULT NULL COMMENT '园区id',
  `invoice_company_number` bigint(11) DEFAULT '0' COMMENT '本期开票企业',
  `uploading_company_number` bigint(11) DEFAULT '0' COMMENT '已上传企业',
  `already_tax_money` bigint(20) DEFAULT '0' COMMENT '已交税费',
  `should_tax_money` bigint(20) DEFAULT '0' COMMENT '总应纳税所得额',
  `recoverable_tax_money` bigint(20) DEFAULT '0' COMMENT '应退税费',
  `supplement_tax_money` bigint(20) DEFAULT '0' COMMENT '应补税费',
  `should_upload_vat_vouchers_company_number` int(11) DEFAULT '0' COMMENT '应上传增值税凭证企业',
  `should_upload_iit_vouchers_company_number` int(11) DEFAULT '0' COMMENT '应上传个税凭证企业',
  `already_upload_vat_vouchers_company_number` int(11) DEFAULT '0' COMMENT '已上传增值税凭证企业',
  `already_upload_iit_vouchers_company_number` int(11) DEFAULT '0' COMMENT '已上传个税凭证企业',
  `vouchers_status` int(11) DEFAULT '0' COMMENT '凭证状态 0-未上传 1-解析中 2-已上传3-部分已上传',
  `tax_bill_status` int(11) DEFAULT NULL COMMENT '税单状态 0-待确认 1-解析中 2-待上传 3-已确认 4-待凭证上传 5-推送中',
  `cur_file_url` varchar(512) DEFAULT NULL COMMENT '最新附件地址',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `add_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区税单变更表';

#园区操作截止时间配置
drop table if exists t_e_park_endtime_config;
create table t_e_park_endtime_config
(
   id                   bigint(11) not null auto_increment comment '主键id',
   park_id              bigint(11) comment '园区id',
   oper_type            int(1) comment '操作类型 1-创建开票提示 2-开票记录自动开票',
   invoice_way          int(1) comment '发票方式 1-纸质发票 2-电子发票',
   start_time           datetime comment '提示开始时间',
   end_time             datetime comment '提示结束时间',
   content              varchar(512) comment '提示内容',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_park_endtime_config comment '园区操作截止时间配置';

#服务费收费阶段明细
drop table if exists t_e_invoice_service_fee_detail;
create table t_e_invoice_service_fee_detail
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   company_id           bigint(11) comment '企业id',
   oem_code             varchar(12) comment '机构编码',
   phase_amount         bigint(11) comment '阶段金额(分)',
   fee_rate             decimal(4,2) comment '适用费率',
   fee_amount           bigint(11) comment '服务费(分)',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_invoice_service_fee_detail comment '服务费收费阶段明细';

#订单微信授权关系表
drop table if exists t_r_order_wechat_auth;
create table t_r_order_wechat_auth
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   oem_code             varchar(12) comment '机构编码',
   member_id            bigint(11) comment '会员id',
   wechat_tmpl_type     int(1) comment '微信模板类型 1-工单审核 2-邀请签名 3-签名确认结果',
   wechat_tmpl_id       bigint(11) comment '微信模板id',
   auth_status          int(1) comment '授权状态 0-未授权 1-已授权',
   wechat_result        varchar(128) comment '微信通知结果',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_order_wechat_auth comment '订单微信授权关系表';

#税种类型 添加 城建税、教育费附加税、地方教育附加税
ALTER TABLE `t_e_tax_rules_config`
ADD COLUMN `urban_construction_tax_rate`  decimal(4,2) NULL DEFAULT 0 COMMENT '城建税税率' AFTER `remark`,
ADD COLUMN `education_surcharge_tax_rate`  decimal(4,2) NULL DEFAULT 0 COMMENT '教育费附加税税率' AFTER `urban_construction_tax_rate`,
ADD COLUMN `local_education_surcharge_rate`  decimal(4,2) NULL DEFAULT 0 COMMENT '地方教育附加税税率' AFTER `education_surcharge_tax_rate`;

#企业对公户表
ALTER TABLE `t_e_company_corporate_account`
  ADD COLUMN `headquarters_name` VARCHAR(64) NULL   COMMENT '银行总部名称' AFTER `remark`,
  ADD COLUMN `headquarters_no` VARCHAR(32) NULL   COMMENT '银行总部编号' AFTER `headquarters_name`;

#对公户申请订单表
ALTER TABLE `t_e_corporate_account_apply_order`
  ADD COLUMN `headquarters_name` VARCHAR(64) NULL   COMMENT '银行总部名称' AFTER `remark`,
  ADD COLUMN `headquarters_no` VARCHAR(32) NULL   COMMENT '银行总部编号' AFTER `headquarters_name`;

#对公户申请订单变更表
ALTER TABLE `t_e_corporate_account_apply_order_change`
  ADD COLUMN `headquarters_name` VARCHAR(64) NULL   COMMENT '银行总部名称' AFTER `remark`,
  ADD COLUMN `headquarters_no` VARCHAR(32) NULL   COMMENT '银行总部编号' AFTER `headquarters_name`;

#园区对公户提现配置表
ALTER TABLE `t_e_park_corporate_account_config`
  ADD COLUMN `headquarters_name` VARCHAR(64) NULL   COMMENT '银行总部名称' AFTER `remark`,
  ADD COLUMN `headquarters_no` VARCHAR(32) NULL   COMMENT '银行总部编号' AFTER `headquarters_name`;

UPDATE `t_e_park_corporate_account_config` SET `headquarters_name`='中国建设银行', `headquarters_no`='105';
UPDATE `t_e_company_corporate_account` SET `headquarters_name`='中国建设银行', `headquarters_no`='105';
UPDATE `t_e_corporate_account_apply_order` SET `headquarters_name`='中国建设银行', `headquarters_no`='105';
UPDATE `t_e_corporate_account_apply_order_change` SET `headquarters_name`='中国建设银行', `headquarters_no`='105';

#园区表
UPDATE `t_e_park` SET `process_mark`='3', `process_desc`='工商局签名流程' WHERE (`id`='8') LIMIT 1;

#开票记录表
ALTER TABLE `t_e_invoice_record`
  ADD COLUMN `ticket_time` DATETIME NULL   COMMENT '出票时间' AFTER `complete_time`;

#开票记录变更表
ALTER TABLE `t_e_invoice_record_change`
  ADD COLUMN `ticket_time` DATETIME NULL   COMMENT '出票时间' AFTER `complete_time`;

#微信模板
ALTER TABLE `t_e_wechat_message_template`
MODIFY COLUMN `template_type`  int(1) NOT NULL COMMENT '模板类型 1-工商开户审核 2-邀请签名 3-签名确认结果' AFTER `oem_code`;

#开票订单表
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `confirm_invoice_time`  date NULL COMMENT '出票日期' AFTER `corporate_account_bank_name`,
ADD COLUMN `refundable_VAT` BIGINT(11) DEFAULT 0  NULL   COMMENT '应退增值税' AFTER `confirm_invoice_time`,
ADD COLUMN `refundable_surcharge` BIGINT(11) DEFAULT 0  NULL   COMMENT '应退附加税' AFTER `refundable_VAT`,
ADD COLUMN `refundable_tax` BIGINT(11) DEFAULT 0  NULL   COMMENT '应退所得税' AFTER `refundable_surcharge`;

#短信模板
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`)
SELECT oem_code, '37', '尊敬的用户，您注册的企业#regName#已设立登记，为了不影响您的出证，请尽快前往小程序进行提交签名确认操作，如有疑问，可联系您的专属客服经理，谢谢。', '1', NOW(), 'admin' FROM t_e_oem WHERE oem_code is not null and oem_code != '';
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`)
SELECT oem_code, '38', '尊敬的用户，您注册的企业#regName#经在工商局系统核查未在湖南登记APP提交签名，请尽快前往小程序进行重新提交签名确认操作，如有疑问，可联系您的专属客服经理，谢谢。', '1', NOW(), 'admin' FROM t_e_oem WHERE oem_code is not null and oem_code != '';

#字典表通知模板
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`)
VALUES ('register_confirm_register_tmpl', '您有一个注册企业需确认提交签名，是否前往查看？', NULL, '工商注册确认登记', NOW(), 'admin');
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`)
VALUES ('register_user_not_sign_tmpl', '您有一个注册企业经确认未提交签名，需要重新确认提交签名，是否前往查看？', NULL, '工商注册确认登记', NOW(), 'admin');
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `dict_desc`, `add_time`, `add_user`)
VALUES ('wechat_page_company_register', 'pages/home/home?navigateTo=enterprise-order', '微信通知跳转企业注册订单列表', '2021-03-02 11:44:01', 'admin');

#通知表
ALTER TABLE `t_e_message_notice`
MODIFY COLUMN `business_type`  int(1) NULL DEFAULT NULL COMMENT '业务类型 1-开户待支付 2-开户已完成 3-开票 4-用章 5-开票流水审核  6-待身份验证 7-托管费到期提醒 8-工商注册确认登记 9-工商注册用户未提交签名' AFTER `open_mode`;

#历史数据处理 开票订单表得确认出票时间
update t_e_invoice_order io set io.confirm_invoice_time=(select o.update_time from t_e_order o where o.order_no=io.order_no) where
io.order_no in (select o.order_no from t_e_order o where   o.order_status = 7		AND o.order_type = 6);

ALTER TABLE `t_e_invoice_order`
MODIFY COLUMN `confirm_invoice_time`  datetime NULL DEFAULT NULL COMMENT '出票日期' AFTER `corporate_account_bank_name`;

#开票订单表新增已缴税字段
ALTER TABLE `t_e_invoice_order`
  ADD COLUMN `paid_vat_fee` BIGINT(11) NULL   COMMENT '已缴增值税' AFTER `refundable_tax`,
  ADD COLUMN `paid_surcharge` BIGINT(11) NULL   COMMENT '已缴附加税' AFTER `paid_vat_fee`,
  ADD COLUMN `paid_income_tax` BIGINT(11) NULL   COMMENT '已缴所得税' AFTER `paid_surcharge`,
  ADD COLUMN `paid_income_tax_year` BIGINT(11) NULL   COMMENT '本年已缴所得税' AFTER `paid_income_tax`,
  ADD COLUMN `period_invoice_amount` BIGINT(11) NULL   COMMENT '本周期开票金额' AFTER `paid_income_tax_year`,
  ADD COLUMN `historical_invoice_amount` BIGINT(11) NULL   COMMENT '本年历史开票金额' AFTER `period_invoice_amount`,
  ADD COLUMN `taxable_income_rate` BIGINT(11) NULL   COMMENT '应税所得率' AFTER `historical_invoice_amount`;
ALTER TABLE `t_e_invoice_order`
MODIFY COLUMN `paid_VAT_fee`  bigint(11) NULL DEFAULT 0 COMMENT '已缴增值税' AFTER `refundable_tax`,
MODIFY COLUMN `paid_surcharge`  bigint(11) NULL DEFAULT 0 COMMENT '已缴附加税' AFTER `paid_VAT_fee`,
MODIFY COLUMN `paid_income_tax`  bigint(11) NULL DEFAULT 0 COMMENT '已缴所得税' AFTER `paid_surcharge`,
MODIFY COLUMN `paid_income_tax_year`  bigint(11) NULL DEFAULT 0 COMMENT '本年已缴所得税' AFTER `paid_income_tax`,
MODIFY COLUMN `period_invoice_amount`  bigint(11) NULL DEFAULT 0 COMMENT '本周期开票金额' AFTER `paid_income_tax_year`,
MODIFY COLUMN `historical_invoice_amount`  bigint(11) NULL DEFAULT 0 COMMENT '本年历史开票金额' AFTER `period_invoice_amount`,
MODIFY COLUMN `taxable_income_rate` DECIMAL(6,2) NULL DEFAULT 0 COMMENT '应税所得率' AFTER `historical_invoice_amount`;


#企业税单新增字段
ALTER TABLE `t_e_company_tax_bill`
ADD COLUMN `iit_vouchers_status`  int NULL COMMENT '个人所得税凭证上传状态 1-未上传 2-已上传 3-无需上传' AFTER `vat_voucher_pic`,
ADD COLUMN `vat_vouchers_status`  int NULL COMMENT ' 增值税凭证上传状态 1-未上传 2-已上传 3-无需上传' AFTER `iit_vouchers_status`;

ALTER TABLE `t_e_register_order`
MODIFY COLUMN `remark`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;

ALTER TABLE `t_e_register_order_change_record`
MODIFY COLUMN `remark`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;

#修改历史数据的出票日期
update t_e_invoice_record r set ticket_time = (case when r.complete_time is not null then r.complete_time
					when r.complete_time is null then (select max(invoice_date) from t_e_invoice_record_detail where invoice_record_no = r.invoice_record_no and status not in (3,4))end ) ,
  update_time = now(),remark = '历史数据同步出票时间';

ALTER TABLE `t_e_work_order`
MODIFY COLUMN `remark`  varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;

ALTER TABLE `t_e_work_order_change_record`
MODIFY COLUMN `remark`  varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;

#字典表
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`) VALUES ('redis_token_outtime_sys', '1800000', NULL, '系统用户token超时时间', '2021-03-29 13:45:48', 'admin');

update t_park_tax_bill set tax_bill_status = 6 where tax_bill_status = 3;

#开票记录明细
ALTER TABLE `t_e_invoice_record_detail`
MODIFY COLUMN `status`  int(1) NULL DEFAULT NULL COMMENT '状态 0-正常，1-已打印，2-已作废，3-出票失败 4-出票中 5-待出票' AFTER `detail_desc`;

#开票记录表
ALTER TABLE `t_e_invoice_record`
MODIFY COLUMN `status`  int(1) NULL DEFAULT NULL COMMENT '状态 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消 9-出票中' AFTER `invoice_amount`;
ALTER TABLE `t_e_invoice_record_change`
MODIFY COLUMN `status`  int(1) NULL DEFAULT NULL COMMENT '状态 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消 9-出票中' AFTER `invoice_amount`;



#预税单视图
CREATE OR REPLACE VIEW COMPANY_CURRENT_QUARTER_TAX_STATISTICS_VIEW AS
select year(now()) 'year',QUARTER(now()) 'quarter',mc.member_id 'member_id',mc.park_id 'park_id',mc.company_type 'company_type',mc.industry_id 'industry_id',
mc.id 'company_id',mc.company_name 'company_name',mc.operator_name 'operator_name',policy.vat_breaks_cycle as 'vat_breaks_cycle',policy.income_tax_breaks_cycle as 'income_tax_breaks_cycle' ,
ifnull(t.vat_zp_amount,0) 'vat_zp_amount',
ifnull(t.vat_pp_amount,0) 'vat_pp_amount',
ifnull(t.more_quarter_invoice_amount,0) 'more_quarter_invoice_amount',
ifnull(t.invoice_amount,0) 'invoice_amount',
ifnull(t.vat_taxable_income_amount,0) 'vat_taxable_income_amount',
ifnull(t.vat_should_tax_money,0) 'vat_should_tax_money',
ifnull(t.vat_rate,0) 'vat_rate',
ifnull(t.vat_already_tax_money,0) 'vat_already_tax_money',
ifnull(t.additional_taxable_income_amount,0) 'additional_taxable_income_amount',
ifnull(t.additional_should_tax_money,0) 'additional_should_tax_money',
ifnull(t.additional_rate,0) 'additional_rate',
ifnull(t.additional_already_tax_money,0) 'additional_already_tax_money',
ifnull(policy.levy_way,0) 'levy_way',
ifnull(t.income_taxable_income_amount,0) 'income_taxable_income_amount',
ifnull(t.income_should_tax_money,0) 'income_should_tax_money',
ifnull(t.taxable_income_rate,0) 'taxable_income_rate',
case when t.levy_way = 1 then ifnull(t.taxable_income_rate,0) else ifnull(t.income_rate,0) end 'income_rate',
ifnull(t.income_already_tax_money,0) 'income_already_tax_money'
 from t_e_member_company mc left join (
select ctax.*,
case when ctax.levy_way = 1 then ctax.income_taxable_income
     when ctax.levy_way = 2 then
     (select ceil(ctax.income_taxable_income  * rate /100) from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
			and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1) end 'income_taxable_income_amount',
case when ctax.levy_way = 1 then 0
     when ctax.levy_way = 2 then
     (select rate*100 from t_e_business_income_rule where  min_amount < (select ceil(ctax.income_taxable_income  * rate / 100) from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
					and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)
					and  max_amount >= (select ceil(ctax.income_taxable_income  * rate / 100) from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
					and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)) end 'income_rate',
case when ctax.levy_way = 1 then
		(select rate from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
			and ctax.income_taxable_income > (min_amount + ctax.income_tax_breaks_amount) and  ctax.income_taxable_income <= if(max_amount>= POWER(2,31),max_amount,(max_amount + ctax.income_tax_breaks_amount))
			and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)
     when ctax.levy_way = 2 then
   (select rate from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
			and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)  end 'taxable_income_rate',
case when ctax.levy_way = 1 then
		(select ceil(ctax.income_taxable_income * rate /100) from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
			and ctax.income_taxable_income > (min_amount + ctax.income_tax_breaks_amount) and  ctax.income_taxable_income <= if(max_amount>= POWER(2,31),max_amount,(max_amount + ctax.income_tax_breaks_amount))
			and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)
     when ctax.levy_way = 2 then
   (select ceil((select ceil(ctax.income_taxable_income * trc.rate /100)* ir.rate - quick - (select ifnull(sum(income_should_tax_money),0) from t_e_company_tax_bill where tax_bill_year=year(now()) and tax_bill_seasonal != QUARTER(now()) and company_id = ctax.company_id)
			from t_e_business_income_rule ir where  ceil(ctax.income_taxable_income * trc.rate /100) > min_amount  and  ceil(ctax.income_taxable_income * trc.rate /100) <= max_amount order by min_amount limit 1))
				from t_e_tax_rules_config trc where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
				and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)  end 'income_should_tax_money'
from (
select year(now()) 'year',QUARTER(now()) 'quarter',c.member_id 'member_id',c.park_id 'park_id',c.company_type 'company_type',c.industry_id 'industry_id',
c.id 'company_id',c.company_name 'company_name',c.operator_name 'operator_name',tp.vat_breaks_cycle as 'vat_breaks_cycle',tp.income_tax_breaks_cycle as 'income_tax_breaks_cycle' ,
sum(case when inv.invoice_type = 2 and (QUARTER(inv.confirm_invoice_time)=QUARTER(now()) or (o.order_status >= 2 and o.order_status < 8 and inv.confirm_invoice_time is null)) then inv.invoice_amount else 0 end) as 'vat_zp_amount',
sum(case when inv.invoice_type = 1 and (QUARTER(inv.confirm_invoice_time)=QUARTER(now()) or (o.order_status >= 2 and o.order_status < 8 and inv.confirm_invoice_time is null)) then inv.invoice_amount else 0 end) as 'vat_pp_amount',
sum(case when QUARTER(inv.confirm_invoice_time)=QUARTER(now()) and QUARTER(inv.add_time)!=QUARTER(now()) then inv.invoice_amount else 0 end) 'more_quarter_invoice_amount',
sum(inv.invoice_amount) 'invoice_amount',
case when tp.VAT_breaks_amount >= a.invoice_amount then (a.invoice_amount - ceil(a.zp_vat_fee))
		else a.invoice_amount - ceil(a.zp_vat_fee+a.pp_vat_fee) end 'vat_taxable_income_amount',
case when tp.VAT_breaks_amount >= a.invoice_amount then ceil(a.zp_vat_fee)
    else ceil(a.zp_vat_fee+a.pp_vat_fee)  end  'vat_should_tax_money',
ceil(a.vat_fee_rate*100)'vat_rate',
sum(ifnull(inv.vat_fee,0)) + sum(IFNULL(inv.VAT_payment, 0)) 'vat_already_tax_money',
case when tp.VAT_breaks_amount >= a.invoice_amount then ceil(a.zp_vat_fee)  else ceil(a.zp_vat_fee+a.pp_vat_fee) end 'additional_taxable_income_amount',
case when tp.VAT_breaks_amount < a.invoice_amount
				then (select ceil((a.zp_vat_fee+a.pp_vat_fee) * (urban_construction_tax_rate+education_surcharge_tax_rate+local_education_surcharge_rate) /100)
						from t_e_tax_rules_config where policy_id = tp.id and company_type = c.company_type and tax_type = 3 limit 1)
   when tp.VAT_breaks_amount >= a.invoice_amount
			then (select ceil(a.zp_vat_fee * (urban_construction_tax_rate)/100)
					from t_e_tax_rules_config where policy_id = tp.id and company_type = c.company_type and tax_type = 3 limit 1) else 0  end as 'additional_should_tax_money',
(select (urban_construction_tax_rate+education_surcharge_tax_rate+local_education_surcharge_rate)
						from t_e_tax_rules_config where policy_id = tp.id and company_type = c.company_type and tax_type = 3 limit 1) 'additional_rate',
sum(ifnull(inv.surcharge,0))+sum(IFNULL(inv.surcharge_payment, 0)) 'additional_already_tax_money',
tp.levy_way 'levy_way',
tp.income_tax_breaks_amount 'income_tax_breaks_amount',
case when tp.levy_way = 1 then
   case when tp.income_tax_breaks_amount < a.invoice_amount
				then (a.invoice_amount - ceil(case when tp.VAT_breaks_amount >= a.invoice_amount then a.zp_vat_fee else (a.zp_vat_fee+a.pp_vat_fee) end ))
				else 0 end
    when tp.levy_way = 2 then
			 (select ifnull((b.year_invoice_amount - sum(vat_should_tax_money)),0) from t_e_company_tax_bill where tax_bill_year=year(now()) and tax_bill_seasonal != QUARTER(now()) and company_id = c.id) +
					(a.invoice_amount - ceil(case when tp.VAT_breaks_amount >= a.invoice_amount then a.zp_vat_fee else (a.zp_vat_fee+a.pp_vat_fee) end ))
 end 'income_taxable_income',
 sum(ifnull(inv.personal_income_tax,0))+sum(IFNULL(inv.income_tax_payment, 0))- sum(IFNULL(inv.refund_tax_fee, 0)) 'income_already_tax_money'
from t_e_member_company c
left join t_e_tax_policy tp on c.company_type = tp.company_type and c.park_id = tp.park_id
left join t_e_invoice_order inv on c.id = inv.company_id
left join t_e_order o on  inv.order_no = o.order_no
left join (select company_id,max(vat_fee_rate) vat_fee_rate,sum(invoice_amount) invoice_amount,
			sum(case when invoice_type = 2 then vat_fee else 0 end) zp_vat_fee,
			sum(case when invoice_type = 1 then vat_fee else 0 end) pp_vat_fee
				from (
			select company_id,invoice_type,vat_fee_rate,sum(invoice_amount) invoice_amount,
				ifnull((sum(invoice_amount) / (1+VAT_fee_rate) * VAT_fee_rate),0) as 'vat_fee'
				from t_e_invoice_order io,t_e_order od
				where io.order_no = od.order_no and od.order_status>=2 and od.order_status <=7 and VAT_fee_rate is not null
			and ((QUARTER(confirm_invoice_time)=QUARTER(now()) and  DATE_FORMAT(io.confirm_invoice_time,'%Y') = DATE_FORMAT(now(),'%Y')) or (confirm_invoice_time is null))
			 group by company_id,vat_fee_rate,invoice_type ) vat
			group by company_id) a  on a.company_id = c.id
left join (
select ior.company_id,ifnull(sum(ior.invoice_amount),0) year_invoice_amount from t_e_order ord ,t_e_invoice_order ior
   where ord.order_no = ior.order_no and ord.order_status>= 2 and ord.order_status < 8
   and DATE_FORMAT(ior.add_time,'%Y') = DATE_FORMAT(now(),'%Y')
group by ior.company_id
) b on b.company_id = c.id
where c.`status`!=4
and o.order_status>= 2 and o.order_status < 8
and ((QUARTER(inv.confirm_invoice_time)=QUARTER(now()) and  DATE_FORMAT(inv.confirm_invoice_time,'%Y') = DATE_FORMAT(now(),'%Y')) or (inv.confirm_invoice_time is null))
group by c.id) ctax) t on mc.id = t.company_id
left join t_e_tax_policy policy on mc.company_type = policy.company_type and mc.park_id = policy.park_id
where mc.`status`!=4;

#修改税单状态
ALTER TABLE `t_park_tax_bill`
MODIFY COLUMN `tax_bill_status`  int(11) NULL DEFAULT NULL COMMENT '税单状态 0-待确认 1-解析中 2-待上传 3-已确认 4-待凭证上传 5-推送中 6-已推送' AFTER `vouchers_status`;

ALTER TABLE `t_e_invoice_record`
MODIFY COLUMN `remark`  varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;

ALTER TABLE `t_e_invoice_record_change`
MODIFY COLUMN `remark`  varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;



#处理历史数据开票时间
update
	t_e_invoice_order io
	INNER JOIN (SELECT
	*
FROM
	(SELECT
	order_no,
	case WHEN (invoice_way = 1 and order_status = 4) or (invoice_way = 2 and order_status = 7) THEN add_time
	end add_time
FROM
	t_e_invoice_order_change_record
) a
WHERE add_time is NOT NULL
GROUP BY order_no) b on b.order_no = io.order_no
set
	confirm_invoice_time = b.add_time

#税费规则配置
ALTER TABLE `t_e_tax_rules_config`
MODIFY COLUMN `rate`  decimal(6,4) NOT NULL COMMENT '税率' AFTER `company_type`,
MODIFY COLUMN `urban_construction_tax_rate`  decimal(6,4) NULL DEFAULT 0.00 COMMENT '城建税税率' AFTER `remark`,
MODIFY COLUMN `education_surcharge_tax_rate`  decimal(6,4) NULL DEFAULT 0.00 COMMENT '教育费附加税税率' AFTER `urban_construction_tax_rate`,
MODIFY COLUMN `local_education_surcharge_rate`  decimal(6,4) NULL DEFAULT 0.00 COMMENT '地方教育附加税税率' AFTER `education_surcharge_tax_rate`;

#开票订单表
ALTER TABLE `t_e_invoice_order`
MODIFY COLUMN `VAT_fee_rate`  decimal(10,8) NULL DEFAULT NULL COMMENT '增值税税率' AFTER `invoice_imgs`,
MODIFY COLUMN `personal_income_tax_rate`  decimal(10,8) NULL DEFAULT NULL COMMENT '个人所得税税率' AFTER `VAT_fee_rate`,
MODIFY COLUMN `surcharge_rate`  decimal(10,8) NULL DEFAULT NULL COMMENT '附加税税率' AFTER `personal_income_tax_rate`,
MODIFY COLUMN `taxable_income_rate`  decimal(10,8) NULL DEFAULT NULL COMMENT '应税所得率' AFTER `historical_invoice_amount`;

#开票订单历史表
ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `confirm_invoice_time`  date NULL COMMENT '出票日期' AFTER `corporate_account_bank_name`,
ADD COLUMN `refundable_VAT` BIGINT(11) NULL DEFAULT 0 COMMENT '应退增值税' AFTER `confirm_invoice_time`,
ADD COLUMN `refundable_surcharge` BIGINT(11) NULL DEFAULT 0  COMMENT '应退附加税' AFTER `refundable_VAT`,
ADD COLUMN `refundable_tax` BIGINT(11) NULL DEFAULT 0 COMMENT '应退所得税' AFTER `refundable_surcharge`,
ADD COLUMN `paid_vat_fee` BIGINT(11) NULL DEFAULT 0  COMMENT '已缴增值税' AFTER `refundable_tax`,
ADD COLUMN `paid_surcharge` BIGINT(11) NULL DEFAULT 0  COMMENT '已缴附加税' AFTER `paid_vat_fee`,
ADD COLUMN `paid_income_tax` BIGINT(11) NULL DEFAULT 0  COMMENT '已缴所得税' AFTER `paid_surcharge`,
ADD COLUMN `paid_income_tax_year` BIGINT(11) NULL DEFAULT 0  COMMENT '本年已缴所得税' AFTER `paid_income_tax`,
ADD COLUMN `period_invoice_amount` BIGINT(11) NULL DEFAULT 0  COMMENT '本周期开票金额' AFTER `paid_income_tax_year`,
ADD COLUMN `historical_invoice_amount` BIGINT(11) NULL  DEFAULT 0 COMMENT '本年历史开票金额' AFTER `period_invoice_amount`,
ADD COLUMN `taxable_income_rate` DECIMAL(10,8) NULL DEFAULT 0.00 COMMENT '应税所得率' AFTER `historical_invoice_amount`;

#开票订单表
ALTER TABLE `t_e_invoice_order_change_record`
MODIFY COLUMN `VAT_fee_rate`  decimal(10,8) NULL DEFAULT NULL COMMENT '增值税税率' AFTER `invoice_imgs`,
MODIFY COLUMN `personal_income_tax_rate`  decimal(10,8) NULL DEFAULT NULL COMMENT '个人所得税税率' AFTER `VAT_fee_rate`,
MODIFY COLUMN `surcharge_rate`  decimal(10,8) NULL DEFAULT NULL COMMENT '附加税税率' AFTER `personal_income_tax_rate`,
MODIFY COLUMN `taxable_income_rate`  decimal(10,8) NULL DEFAULT NULL COMMENT '应税所得率' AFTER `historical_invoice_amount`;

#企业税单表
ALTER TABLE `t_e_company_tax_bill`
MODIFY COLUMN `additional_rate`  decimal(10,8) NULL DEFAULT 0 COMMENT '附加税适用税率' AFTER `additional_taxable_income_amount`,
MODIFY COLUMN `taxable_income_rate`  decimal(10,8) NULL DEFAULT NULL COMMENT '应税所得率' AFTER `income_levy_way`,
MODIFY COLUMN `income_rate`  decimal(10,8) NULL DEFAULT 0 COMMENT '所得税适用税率' AFTER `taxable_income_rate`;
ALTER TABLE `t_e_company_tax_bill`
MODIFY COLUMN `vat_rate`  decimal(10,8) NULL DEFAULT 0 COMMENT '增值税适用税率' AFTER `vat_taxable_income_amount`;

