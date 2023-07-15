#======V2.6.0版本数据脚本
#行业表增加发票样例字段
ALTER TABLE `t_e_industry`
ADD COLUMN `example_invoice`  varchar(255) NULL COMMENT '发票样例' AFTER `example_name`;
#新增一个开票状态审核未通过
ALTER TABLE `t_e_order`
MODIFY COLUMN `order_status`  int(2) NOT NULL COMMENT '订单状态：工商注册(0-待电子签字,1-待视频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证),开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过),会员升级( 0-待支付,1-支付中,2-财务审核,3-已完成,4-已取消), 充值提现、对公户提现(0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败),工商注销(0-待付款,1-注销处理中,2-注销成功,3-已取消),证件申请( 0-待付款 1-待发货,2-出库中,3-待签收,4-已签收,5-已取消),公户申请（0-待付款,1-等待预约,2-已完成,3-已取消）消费开票（0-待出票 1-出票中 2-已出票 3-出票失败）' AFTER `order_type`;

#新增园区税单表
CREATE TABLE `t_park_tax_bill` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '园区税单id',
  `tax_bill_year` int(4) DEFAULT NULL COMMENT '税款所属期年',
  `tax_bill_seasonal` int(1) DEFAULT NULL COMMENT '税款所属期-季度',
  `park_id` bigint(20) DEFAULT NULL COMMENT '园区id',
  `invoice_company_number` bigint(11) DEFAULT '0' COMMENT '本期开票企业',
  `uploading_company_number` bigint(11) DEFAULT '0' COMMENT '已上传企业',
  `already_tax_money` bigint(20) DEFAULT '0' COMMENT '已交税费',
  `should_tax_money` bigint(20) DEFAULT '0' COMMENT '总应纳税所得额',
  `recoverable_tax_money` bigint(20) DEFAULT '0' COMMENT '应退税费',
  `supplement_tax_money` bigint(20) DEFAULT '0' COMMENT '应补税费',
  `tax_bill_status` int(11) DEFAULT NULL COMMENT '税单状态 0-待确认 1-解析中 2-待上传 3-已确认',
  `cur_file_url` varchar(512) DEFAULT NULL COMMENT '最新附件地址',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `add_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='园区税单查询';






#新增企业税单表
CREATE TABLE `t_e_company_tax_bill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `park_tax_bill_id` bigint(20) DEFAULT NULL COMMENT '园区税单id',
  `tax_bill_year` int(4) DEFAULT NULL COMMENT '税款所属期年',
  `tax_bill_seasonal` int(1) DEFAULT NULL COMMENT '税款所属期-季度',
  `park_id` bigint(20) DEFAULT NULL,
  `company_id` bigint(20) DEFAULT NULL,
  `zp_invoice_amount` bigint(20) DEFAULT NULL COMMENT '增值税专用发票开票金额',
  `pp_invoice_amount` bigint(20) DEFAULT NULL COMMENT '增值税普通发票开票金额',
  `invoice_money` bigint(20) DEFAULT '0' COMMENT '本期开票金额',
  `already_tax_money` bigint(20) DEFAULT '0' COMMENT '已交税费',
  `should_tax_money` bigint(20) DEFAULT '0' COMMENT '总应纳税所得额',
  `recoverable_tax_money` bigint(20) DEFAULT '0' COMMENT '应退税费',
  `supplement_tax_money` bigint(20) DEFAULT '0' COMMENT '应补税费',
  `vat_taxable_income_amount` bigint(20) DEFAULT '0' COMMENT '增值税应纳税所得额',
  `vat_rate` decimal(4,2) DEFAULT '0.00' COMMENT '增值税适用税率',
  `vat_already_tax_money` bigint(20) DEFAULT '0' COMMENT '已缴增值税',
  `vat_should_tax_money` bigint(20) DEFAULT '0' COMMENT '应缴增值税',
  `vat_recoverable_tax_money` bigint(20) DEFAULT '0' COMMENT '应退增值税',
  `vat_supplement_tax_money` bigint(20) DEFAULT '0' COMMENT '应补增值税',
  `tax_bill_status` int(11) DEFAULT NULL COMMENT '税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税',
  `affirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `add_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `additional_taxable_income_amount` bigint(20) DEFAULT '0' COMMENT '附加税应纳税所得额',
  `additional_rate` decimal(4,2) DEFAULT '0.00' COMMENT '附加税适用税率',
  `additional_already_tax_money` bigint(20) DEFAULT '0' COMMENT '附加税已缴',
  `additional_should_tax_money` bigint(20) DEFAULT '0' COMMENT '附加税应缴',
  `additional_recoverable_tax_money` bigint(20) DEFAULT '0' COMMENT '附加税应退',
  `additional_supplement_tax_money` bigint(20) DEFAULT '0' COMMENT '附加税应补',
  `income_taxable_income_amount` bigint(20) DEFAULT '0' COMMENT '所得税应纳税所得额',
  `income_levy_way` int(11) DEFAULT NULL COMMENT '征收方式（1：核定征收率，2：核定应税所得率）',
  `taxable_income_rate` decimal(4,2) DEFAULT NULL COMMENT '应税所得率',
  `income_rate` decimal(4,2) DEFAULT '0.00' COMMENT '所得税适用税率',
  `income_already_tax_money` bigint(20) DEFAULT '0' COMMENT '所得税已缴',
  `income_should_tax_money` bigint(20) DEFAULT '0' COMMENT '所得税应缴',
  `income_recoverable_tax_money` bigint(20) DEFAULT '0' COMMENT '所得税应退',
  `income_supplement_tax_money` bigint(20) DEFAULT '0' COMMENT '所得税应补',
  `over_time_is_sms` int(1) DEFAULT '0' COMMENT '超过15天未补交是否已经发短信提醒 0-未发 1-已发',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=616 DEFAULT CHARSET=utf8mb4 COMMENT='企业税单表';






#新增园区上传税单记录表
CREATE TABLE `t_e_park_tax_bill_file_record` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '园区上传税单记录表',
  `park_tax_bill` bigint(20) DEFAULT NULL COMMENT '园区税单id',
  `company_id` bigint(20) DEFAULT NULL COMMENT '企业id',
  `company_name` varchar(255) DEFAULT NULL COMMENT '企业名称',
  `batch_number` varchar(255) DEFAULT NULL COMMENT '批次号',
  `ein` varchar(255) DEFAULT NULL COMMENT '税号',
  `season_invoice_amount` bigint(20) DEFAULT NULL COMMENT '本季开票金额',
  `total_tax_amount` bigint(20) DEFAULT NULL COMMENT '总税费',
  `vat` bigint(20) DEFAULT NULL COMMENT '增值税',
  `additional` bigint(20) DEFAULT NULL COMMENT '附加税',
  `income` bigint(20) DEFAULT NULL COMMENT '所得税',
  `file_url` varchar(512) DEFAULT NULL COMMENT '附件地址',
  `failed` varchar(255) DEFAULT NULL COMMENT '失败原因',
  `status` int(11) DEFAULT NULL COMMENT '解析状态 0-解析成功 1-解析失败',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `add_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `tax_bill_year` int(4) DEFAULT NULL COMMENT '税款所属期年',
  `tax_bill_seasonal` int(1) DEFAULT NULL COMMENT '税款所属期-季度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COMMENT='园区上传税单记录表';



#增加税单超时时间
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `dict_desc`, `add_time`, `add_user`) VALUES ('tax_bill_overtime', '15', '税单超时时间控制（单位：天）', '2020-12-03 13:49:07', 'admin');

INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('notice_title_template', '#parkName##taxBillYear#年#taxBillSeasonal#季度税单已生成', NULL, '税单通知标题模板', now(), NULL, NULL, NULL, NULL);
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('notice_template', '本期您有#recoverable#个企业需退税，#supplement#个企业需补税，为了不影响您的开票申请，请于15天内完成税款补缴操作！您可前往“我的-税单”处理退税和补税。',
 NULL, '税单通知内容模板',now(), NULL, NULL, NULL, NULL);


#开票表增加字段
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `tax_year`  int(4) NULL DEFAULT NULL COMMENT '税期年' AFTER `is_refund_postage_fee`,
ADD COLUMN `tax_seasonal`  int(1) NULL DEFAULT NULL COMMENT '税期-季度' AFTER `tax_year`;

#企业开票记录
ALTER TABLE `t_e_company_invoice_record`
ADD COLUMN `end_time`  datetime NULL COMMENT '有效截至时间' AFTER `oem_code`;

#更新历史开票记录表有效截至时间
update t_e_company_invoice_record ir,t_e_member_company c
set ir.end_time = c.end_time
where ir.company_id = c.id;

#修改注释
ALTER TABLE `t_e_order`
MODIFY COLUMN `order_type`  int(1) NOT NULL COMMENT '订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税' AFTER `user_type`,
MODIFY COLUMN `order_status`  int(2) NOT NULL COMMENT '订单状态：工商注册(0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证),\r\n开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过),\r\n会员升级( 0-待支付,1-支付中,2-财务审核,3-已完成,4-已取消), \r\n充值提现、对公户提现、补税，退税(0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败),工商注销(0-待付款,1-注销处理中,2-注销成功,3-已取消),证件申请( 0-待付款 1-待发货,2-出库中,3-待签收,4-已签收,5-已取消),公户申请（0-待付款,1-等待预约,2-已完成,3-已取消）消费开票（0-待出票 1-出票中 2-已出票 3-出票失败）' AFTER `order_type`;
ALTER TABLE `t_e_pay_water`
MODIFY COLUMN `order_type`  int(1) NOT NULL COMMENT '订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税' AFTER `pay_amount`;

#增加短信模板
INSERT INTO  `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YCS', '31', '温馨提醒：#parkName#园区#taxBillYear#年#taxBillSeasonal#季度企业税单已生成，本期您有#reNum#个企业需退税，#suNum#个企业需补税，请您前往小程序“我的-税单”查看。', '1',now(), 'admin', now(), '', '');
INSERT INTO  `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YCS', '32', '温馨提醒：您有#number#个待补税的企业税单超过15天未处理，相关企业在完成补税前已暂停开票服务，请尽快前往小程序“我的-税单”进行补税处理！', '1', now(), 'admin', now(), '', '');

#增加短信模板
INSERT INTO  `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select 'YSC' `oem_code`, `template_type`, `template_content`, `status`, now() `add_time`, `add_user`,now() `update_time`, `update_user`, `remark`
from t_e_sms_template WHERE oem_code='YCS' and template_type in (31,32);
INSERT INTO  `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select 'SYZJ' `oem_code`, `template_type`, `template_content`, `status`, now() `add_time`, `add_user`,now() `update_time`, `update_user`, `remark`
from t_e_sms_template WHERE oem_code='YCS' and template_type in (31,32);
INSERT INTO  `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select 'ZQHL' `oem_code`, `template_type`, `template_content`, `status`, now() `add_time`, `add_user`,now() `update_time`, `update_user`, `remark`
from t_e_sms_template WHERE oem_code='YCS' and template_type in (31,32);
INSERT INTO  `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select 'DMSH' `oem_code`, `template_type`, `template_content`, `status`, now() `add_time`, `add_user`,now() `update_time`, `update_user`, `remark`
from t_e_sms_template WHERE oem_code='YCS' and template_type in (31,32);
INSERT INTO  `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select 'YZGT' `oem_code`, `template_type`, `template_content`, `status`, now() `add_time`, `add_user`,now() `update_time`, `update_user`, `remark`
from t_e_sms_template WHERE oem_code='YCS' and template_type in (31,32);
INSERT INTO  `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select 'NBGT' `oem_code`, `template_type`, `template_content`, `status`, now() `add_time`, `add_user`,now() `update_time`, `update_user`, `remark`
from t_e_sms_template WHERE oem_code='YCS' and template_type in (31,32);
INSERT INTO  `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select 'AYGT' `oem_code`, `template_type`, `template_content`, `status`, now() `add_time`, `add_user`,now() `update_time`, `update_user`, `remark`
from t_e_sms_template WHERE oem_code='YCS' and template_type in (31,32);
INSERT INTO  `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select 'YCBW' `oem_code`, `template_type`, `template_content`, `status`, now() `add_time`, `add_user`,now() `update_time`, `update_user`, `remark`
from t_e_sms_template WHERE oem_code='YCS' and template_type in (31,32);
#账单明细字典表配置调整
UPDATE `sys_e_dictionary` SET `dict_value`='[{\"key\":\"全部\",\"value\":\"1\"},{\"key\":\"充值\",\"value\":\"2\"},{\"key\":\"提现\",\"value\":\"3\"},{\"key\":\"企业注册\",\"value\":\"5\"},{\"key\":\"企业开票\",\"value\":\"6\"},{\"key\":\"会员升级\",\"value\":\"7\"},{\"key\":\"企业注销\",\"value\":\"8\"},{\"key\":\"证件申请\",\"value\":\"9\"},{\"key\":\"企业补税\",\"value\":\"13\"},{\"key\":\"企业退税\",\"value\":\"14\"}]'  WHERE `dict_code` = 'consumerBillType' LIMIT 1;
#通知表副标题扩容
ALTER TABLE `t_e_message_notice`
MODIFY COLUMN `notice_subtitle`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '副标题' AFTER `notice_title`;
ALTER TABLE `t_e_notice_manage`
MODIFY COLUMN `notice_subtitle`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '副标题' AFTER `notice_title`;


#处理开票订单所属税期字段
update t_e_invoice_order io set io.tax_year=DATE_FORMAT(io.add_time,'%Y') ,io.tax_seasonal= case when DATE_FORMAT(add_time,'%m')=03 or DATE_FORMAT(add_time,'%m')=01 or DATE_FORMAT(add_time,'%m')=02 then 1
                when DATE_FORMAT(add_time,'%m')=04 or DATE_FORMAT(add_time,'%m')=05 or DATE_FORMAT(add_time,'%m')=06 then 2
when DATE_FORMAT(add_time,'%m')=07 or DATE_FORMAT(add_time,'%m')=08 or DATE_FORMAT(add_time,'%m')=09 then 3
when DATE_FORMAT(add_time,'%m')=10 or DATE_FORMAT(add_time,'%m')=11 or DATE_FORMAT(add_time,'%m')=12 then 4 end
where  io.order_no in(select  order_no from t_e_order o where o.order_status not in (0,1,2,3,8,9,10));

####云财在线近12个月开票记录
CREATE OR REPLACE VIEW COMPANY_INVOICE_RECORD_STATISTICS_VIEW AS  SELECT
	invrecode.member_id user_id, #会员id
	invrecode.company_id, #企业id
	IFNULL(a.use_total_invoice_amount,0) use_total_invoice_amount, #近12个月已开票金额
	IFNULL(a.use_invoice_amount_quarter_pp,0) use_invoice_amount_quarter_pp, #本季度已开普票金额
	IFNULL(a.use_invoice_amount_quarter_zp,0) use_invoice_amount_quarter_zp, #本季度已开专票金额
	invrecode.use_invoice_amount use_invoice_amount_year, #本年已开票金额（企业注册时间往后推一年）
	invrecode.end_time #企业本年有效截至时间
FROM
(SELECT r.*,c.member_id FROM t_e_company_invoice_record r ,t_e_member_company c WHERE r.company_id = c.id and r.end_time >= now() GROUP BY company_id HAVING r.id = min(r.id)) invrecode
 left join
	(
		SELECT o.user_id, i.company_id,
			sum( CASE WHEN o.add_time >= DATE_FORMAT( date_add(NOW(), INTERVAL - 11 MONTH), '%Y-%m-01' ) THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_total_invoice_amount,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 1 THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_invoice_amount_quarter_pp,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 2 THEN i.invoice_amount ELSE 0 END ) use_invoice_amount_quarter_zp
		FROM
			t_e_order o,
			t_e_invoice_order i
		WHERE o.order_no = i.order_no AND o.order_status NOT IN (8,10) AND o.user_type = 1 GROUP BY o.user_id, i.company_id ) a
on  a.company_id = invrecode.company_id;