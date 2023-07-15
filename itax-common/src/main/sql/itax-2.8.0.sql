#======V2.8.0版本数据脚本
#后台用户表
ALTER TABLE `sys_e_user`
ADD COLUMN `sms_lock`  int NULL COMMENT '短信锁定标识' AFTER `update_user`;
#机构配置表新增数据
INSERT INTO `sys_e_oem_params` ( `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( '', '25', '', '', '', '10', '', '', '1', now(), 'admin', now(), 'admin', '近2小时允许发送短信数');
INSERT INTO `sys_e_oem_params` ( `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YCS', '25', '', '', '', '10', '', '', '1', now(), 'admin', now(), 'admin', '近2小时允许发送短信数');
INSERT INTO `sys_e_oem_params` ( `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YSC', '25', '', '', '', '10', '', '', '1', now(), 'admin', now(), 'admin', '近2小时允许发送短信数');
INSERT INTO `sys_e_oem_params` ( `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'DMSH', '25', '', '', '', '10', '', '', '1', now(), 'admin', now(), 'admin', '近2小时允许发送短信数');
INSERT INTO `sys_e_oem_params` ( `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'ZQHL', '25', '', '', '', '10', '', '', '1', now(), 'admin', now(), 'admin', '近2小时允许发送短信数');
INSERT INTO `sys_e_oem_params` ( `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YZGT', '25', '', '', '', '10', '', '', '1', now(), 'admin', now(), 'admin', '近2小时允许发送短信数');
INSERT INTO `sys_e_oem_params` ( `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'AYGT', '25', '', '', '', '10', '', '', '1', now(), 'admin', now(), 'admin', '近2小时允许发送短信数');
INSERT INTO `sys_e_oem_params` ( `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'NBGT', '25', '', '', '', '10', '', '', '1', now(), 'admin', now(), 'admin', '近2小时允许发送短信数');
INSERT INTO `sys_e_oem_params` ( `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YCBW', '25', '', '', '', '10', '', '', '1', now(), 'admin', now(), 'admin', '近2小时允许发送短信数');
INSERT INTO `sys_e_oem_params` ( `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YCHX', '25', '', '', '', '10', '', '', '1', now(), 'admin', now(), 'admin', '近2小时允许发送短信数');


#日统计表新增字段
ALTER TABLE `t_e_user_order_statistics_day`
ADD COLUMN `custody_fee_renewal_direct`  bigint(11) NULL DEFAULT 0 COMMENT '直推托管费续费' AFTER `remark`,
ADD COLUMN `custody_fee_renewal_fission`  bigint(11) NULL DEFAULT 0 COMMENT '裂变托管费续费' AFTER `custody_fee_renewal_direct`;

#分润表新增分润类型
ALTER TABLE `t_e_profits_detail`
MODIFY COLUMN `profits_type`  int(1) NULL DEFAULT NULL COMMENT '分润类型  1-会费 2-托管费 3-开票服务费 4-注销服务费 5-会费返还 6-托管费续费' AFTER `order_type`;

#订单主表
ALTER TABLE `t_e_order`
MODIFY COLUMN `order_type`  int(1) NOT NULL COMMENT '订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税 15-托管费续费' AFTER `user_type`,
MODIFY COLUMN `order_status`  int(2) NOT NULL COMMENT '订单状态：工商注册(0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，10-待设立登记、11-待提交签名、12-签名待确认),\r\n开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过),\r\n会员升级( 0-待支付,1-支付中,2-财务审核,3-已完成,4-已取消), \r\n充值提现、对公户提现、补税，退税(0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败),工商注销(0-待付款,1-注销处理中,2-注销成功,3-已取消),证件申请( 0-待付款 1-待发货,2-出库中,3-待签收,4-已签收,5-已取消),公户申请（0-待付款,1-等待预约,2-已完成,3-已取消）消费开票（0-待出票 1-出票中 2-已出票 3-出票失败）托管费续费（0-待支付、1-支付中、2-已完成、3-已取消）' AFTER `order_type`;


ALTER TABLE `t_e_product`
MODIFY COLUMN `prod_type`  int(2) NOT NULL COMMENT '产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-黄金会员（废弃） 10-钻石会员 （废弃）\r\n11-个体注销 12-个独注销 13-有限合伙注销 14-有限责任注销 15-公户申请和托管 16-个体托管费续费' AFTER `prod_code`;

ALTER TABLE `t_e_park`
ADD COLUMN `process_mark`  int(1) NULL DEFAULT 1 COMMENT '流程标记（1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程）' AFTER `reviewer`,
ADD COLUMN `process_desc`  varchar(255) NULL COMMENT '流程描述' AFTER `process_mark`;

ALTER TABLE `t_e_invoice_order`
ADD COLUMN `corporate_account`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对公账户' AFTER `email`,
ADD COLUMN `corporate_account_bank_name`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对公户银行名称' AFTER `corporate_account`;

ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `corporate_account`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对公账户' AFTER `is_refund_postage_fee`,
ADD COLUMN `corporate_account_bank_name`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对公户银行名称' AFTER `corporate_account`;


#会员订单变动记录表
DROP TABLE IF EXISTS t_e_member_account_change;
CREATE TABLE `t_e_member_account_change` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
	`account_id` bigint(11) NOT NULL COMMENT '会员id',
  `member_account` varchar(32) NOT NULL COMMENT '会员账号',
  `member_name` varchar(32) NOT NULL COMMENT '会员昵称',
  `head_img` varchar(128) DEFAULT NULL COMMENT '头像',
  `real_name` varchar(32) DEFAULT NULL COMMENT '用户名',
  `alipay_user_id` varchar(16) DEFAULT NULL COMMENT '支付宝userId，支付宝支付时需要',
  `open_id` varchar(32) DEFAULT NULL COMMENT '微信openId，微信支付时需要',
  `member_phone` varchar(16) NOT NULL COMMENT '会员手机号',
  `phone_new` varchar(16) DEFAULT NULL COMMENT '新手机号',
  `email` varchar(64) DEFAULT NULL COMMENT '最近开票电子邮箱',
  `oem_code` varchar(12) NOT NULL COMMENT '机构编码',
  `member_level` bigint(11) NOT NULL COMMENT '会员等级id',
  `level_name` varchar(16) DEFAULT NULL COMMENT '会员等级名称',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态  1-正常 0-禁用 2-注销',
  `parent_member_id` bigint(11) DEFAULT NULL COMMENT '邀请人id',
  `parent_member_account` varchar(32) DEFAULT NULL COMMENT '邀请人账号',
  `member_tree` varchar(64) DEFAULT NULL COMMENT '层级树',
  `province_code` varchar(16) DEFAULT NULL COMMENT '所在省份',
  `city_code` varchar(16) DEFAULT NULL COMMENT '所在城市',
  `member_type` int(1) NOT NULL COMMENT '会员类型  1-会员 2-员工 ',
  `pay_password` varchar(32) DEFAULT NULL COMMENT '支付密码',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `add_user` varchar(32) DEFAULT NULL COMMENT '添加人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '修改人',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  `id_card_no` varchar(18) DEFAULT NULL COMMENT '身份证号码',
  `id_card_front` varchar(255) DEFAULT NULL COMMENT '身份证正面照地址',
  `id_card_back` varchar(255) DEFAULT NULL COMMENT '身份证反面照地址',
  `id_card_addr` varchar(128) DEFAULT NULL COMMENT '身份证地址',
  `auth_status` int(1) DEFAULT '0' COMMENT '实名认证状态 0-未认证 1-认证成功 2-认证失败',
  `expire_date` varchar(21) DEFAULT NULL COMMENT '身份证有效期，格式如2010/01/01-2020/01/01',
  `invite_code` varchar(32) DEFAULT NULL COMMENT '邀请码',
  `employees_limit` int(5) DEFAULT NULL COMMENT '钻石会员员工上限',
  `extend_type` int(1) DEFAULT NULL COMMENT '推广角色 1-散客 2-直客 3-顶级直客',
  `attribution_employees_id` bigint(11) DEFAULT NULL COMMENT '所属员工id',
  `attribution_employees_account` varchar(32) DEFAULT NULL COMMENT '所属员工账号',
  `up_diamond_id` bigint(11) DEFAULT NULL COMMENT '上级钻石会员id',
  `up_diamond_account` varchar(32) DEFAULT NULL COMMENT '上级钻石会员账号',
  `super_diamond_id` bigint(11) DEFAULT NULL COMMENT '上上级钻石会员id',
  `super_diamond_account` varchar(32) DEFAULT NULL COMMENT '上上级钻石会员账号',
  `super_employees_id` bigint(11) DEFAULT NULL COMMENT '上上级员工id',
  `super_employees_account` varchar(32) DEFAULT NULL COMMENT '上上级员工账号',
  `is_pay_upgrade` int(1) DEFAULT '0' COMMENT '是否付费升级 0-否 1-是',
  `source_type` int(1) DEFAULT NULL COMMENT '操作小程序来源 1-微信小程序 2-支付宝小程序',
  PRIMARY KEY (`id`)
);
alter table t_e_member_account_change comment '会员账号变动表';

#续费订单表
drop table if exists t_e_cont_register_order;
create table t_e_cont_register_order
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   memeber_id           bigint(11) comment '会员id',
   company_id           bigint(11) comment '企业id',
   corporate_account_id bigint(11) comment '对公户id',
   oem_code             varchar(12) comment '机构编码',
   park_id              bigint(11) comment '园区id',
   order_amount         bigint(11) comment '订单金额(分)',
   pay_amount           bigint(11) comment '支付金额(分)',
   cont_type            int(1) comment '续费类型 1-托管费 2-对公户',
   order_status         int(1) comment '订单状态 0-待支付 1-支付中 2-已完成 3-已取消',
   complete_time        datetime comment '完成时间',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_cont_register_order comment '续费订单表';

#续费订单变更表
drop table if exists t_e_cont_register_order_change;
create table t_e_cont_register_order_change
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   memeber_id           bigint(11) comment '会员id',
   company_id           bigint(11) comment '企业id',
   corporate_account_id bigint(11) comment '对公户id',
   oem_code             varchar(12) comment '机构编码',
   park_id              bigint(11) comment '园区id',
   order_amount         bigint(11) comment '订单金额(分)',
   pay_amount           bigint(11) comment '支付金额(分)',
   cont_type            int(1) comment '续费类型 1-托管费 2-对公户',
   order_status         int(1) comment '订单状态 0-待支付 1-支付中 2-已完成 3-已取消',
   complete_time        datetime comment '完成时间',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_cont_register_order_change comment '续费订单变更表';


#会员企业表新增过期状态字段,去除状态字段已过期选项
ALTER TABLE `t_e_member_company`
MODIFY COLUMN `status`  int(1) NOT NULL DEFAULT 1 COMMENT '状态 1-正常 2-禁用 4-已注销 5-注销中 ' AFTER `end_time`,
ADD COLUMN `overdue_status`  int(1) NULL DEFAULT 1 COMMENT '过期状态 1-正常 2-即将过期 3-已过期' AFTER `order_no`;

#园区税单状态增加
ALTER TABLE `t_park_tax_bill`
MODIFY COLUMN `tax_bill_status`  int(11) NULL DEFAULT NULL COMMENT '税单状态 0-待确认 1-解析中 2-待上传 3-已确认 4-待凭证上传 5-推送中' AFTER `vouchers_status`;

#会员表增加字段
ALTER TABLE `t_e_member_account`
ADD COLUMN `sign`  int(2) NULL COMMENT '会员标签 0-普通 1-海星' AFTER `source_type`;

#
ALTER TABLE `t_e_order`
MODIFY COLUMN `order_status`  int(2) NOT NULL COMMENT '订单状态：工商注册(0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，10-待设立登记、11-待提交签名、12-签名待确认),\r\n开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过),\r\n会员升级( 0-待支付,1-支付中,2-财务审核,3-已完成,4-已取消), \r\n充值提现、对公户提现、补税，退税(0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败),工商注销(0-待付款,1-注销处理中,2-注销成功,3-已取消),证件申请( 0-待付款 1-待发货,2-出库中,3-待签收,4-已签收,5-已取消),公户申请（0-待付款,1-等待预约,2-已完成,3-已取消）消费开票（0-待出票 1-出票中 2-已出票 3-出票失败）托管费续费（0-待支付、1-完成、2-取消 3-支付中）' AFTER `order_type`;

#续费订单表增加索引
ALTER TABLE `t_e_cont_register_order`
ADD INDEX (`order_no`) ,
ADD INDEX (`park_id`) ;

#会员变动表
ALTER TABLE `t_e_member_account_change`
ADD COLUMN `sign`  int(1) NULL COMMENT '会员标签 0-普通 1-海星' AFTER `source_type`,
ADD COLUMN `file_url`  varchar(64) NULL COMMENT '附件地址' AFTER `sign`;

#字典表
UPDATE sys_e_dictionary SET dict_value='[{\"key\":\"全部\",\"value\":\"1\"},{\"key\":\"充值\",\"value\":\"2\"},{\"key\":\"提现\",\"value\":\"3\"},{\"key\":\"企业注册\",\"value\":\"5\"},{\"key\":\"企业开票\",\"value\":\"6\"},{\"key\":\"会员升级\",\"value\":\"7\"},{\"key\":\"企业注销\",\"value\":\"8\"},{\"key\":\"证件申请\",\"value\":\"9\"},{\"key\":\"企业补税\",\"value\":\"13\"},{\"key\":\"企业退税\",\"value\":\"14\"},{\"key\":\"托管费续费\",\"value\":\"15\"}]' WHERE dict_code = 'consumerBillType' LIMIT 1;
UPDATE `sys_e_dictionary` SET `dict_value`='[{\"key\": \"全部\",\"value\": \"-1\"}, {\"key\": \"企业注册\",\"value\": \"5\"}, {\"key\": \"企业开票\",\"value\": \"6\"}, {\"key\": \"用户升级\",\"value\": \"7\"}, {\"key\": \"企业注销\",\"value\": \"8\"}, {\"key\": \"证件申请\",\"value\": \"9\"}, {\"key\": \"对公户申请\",\"value\": \"10\"}, {\"key\": \"托管费续费\",\"value\": \"15\"}]'  WHERE dict_code = 'consumerInvBillType' LIMIT 1;
UPDATE `sys_e_dictionary` SET `dict_value`='[{\"key\":\"全部\",\"value\":\"1\"},{\"key\":\"提现\",\"value\":\"3\"},{\"key\":\"推广分润\",\"value\":\"4\"}]'  WHERE dict_code = 'commissionBillType' LIMIT 1;
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('expire_surplus_days', '30', NULL, '企业托管费到期弹框有效天数', NOW(), 'admin', NULL, NULL, NULL);
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('overdue_days', '1', NULL, '企业托管费已过期提醒有效天数', NOW(), 'admin', NULL, NULL, NULL);
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('haixin_email', 'hanzheng@99366.cn', NULL, '海星会员统计邮件发送邮箱（多人用，隔开）', NOW(), NULL, NULL, NULL, NULL);

#消费记录表
ALTER TABLE `t_e_member_consumption_record`
MODIFY COLUMN `order_type`  int(1) NULL DEFAULT NULL COMMENT '订单类型 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 15-托管费续费' AFTER `order_no`;

#消息通知表
ALTER TABLE `t_e_message_notice`
MODIFY COLUMN `business_type`  int(1) NULL DEFAULT NULL COMMENT '业务类型 1-开户待支付 2-开户已完成 3-开票 4-用章 5-开票流水审核  6-待身份验证 7-托管费到期提醒' AFTER `open_mode`;

#短信模板配置表
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('YSC', '34', '您有 #number# 家企业的托管费有效期不足#days#天，为了不影响您的业务，请尽快去续费哦~', '1', NOW(), 'admin', NULL, NULL, NULL);
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('YSC', '35', '您有 #number# 家企业托管费已过期，为了不影响您的业务，请尽快去续费哦~', '1', NOW(), 'admin', NULL, NULL, NULL);

#分润表新增托管费续费订单类型
ALTER TABLE `t_e_profits_detail`
MODIFY COLUMN `order_type`  int(1) NOT NULL COMMENT '订单类型  1-会员升级 2-工商开户 3-开票 4-工商注销 6-托管费续费订单' AFTER `profits_no`;

#企业开票记录表修改字段数据类型
ALTER TABLE `t_e_company_invoice_record`
MODIFY COLUMN `end_time`  date NULL DEFAULT NULL COMMENT '有效截至时间' AFTER `oem_code`;

#园区表
update t_e_park set process_mark = 2 where park_code in ('CSYQ_EH','CSYQ')

#企业表新增字段是否发送过期提醒通知
ALTER TABLE `t_e_member_company`
ADD COLUMN `is_send_notice`  INT(1) NULL DEFAULT 0 COMMENT '是否发送过期提醒通知 0-未发送 1-已发送' AFTER `overdue_status`;

#开票订单变更表
ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `tax_year`  int(4) NULL COMMENT '税期年' AFTER `is_refund_postage_fee`,
ADD COLUMN `tax_seasonal`  int(1) NULL COMMENT '税期-季度' AFTER `tax_year`,
ADD COLUMN `email`  varchar(64) NULL COMMENT '收票邮箱' AFTER `tax_seasonal`,
ADD COLUMN `corporate_account`  varchar(32) NULL COMMENT '对公账户' AFTER `email`,
ADD COLUMN `corporate_account_bank_name`  varchar(64) NULL COMMENT '对公户银行名称' AFTER `corporate_account`;

#订单快照表
ALTER TABLE `t_e_order_snapshot`
MODIFY COLUMN `order_type`  int(1) NULL DEFAULT NULL COMMENT '订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税 15-托管费续费' AFTER `user_type`;



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
(SELECT r.*,c.member_id FROM t_e_company_invoice_record r ,t_e_member_company c WHERE r.company_id = c.id and r.end_time >= DATE_FORMAT(NOW(),'%Y-%m-%d') GROUP BY company_id HAVING r.id = min(r.id)) invrecode
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

#用户资金账户变更记录表新增订单类型
ALTER TABLE `t_e_user_capital_change_record`
MODIFY COLUMN `order_type`  int(1) NOT NULL COMMENT '订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-会费 8-工商注销 9-对公户申请 10-托管费续费' AFTER `detail_desc`;
#新增平台登录短信模板
INSERT INTO `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( '', '36', '本次登录验证码是#verficationCode#，请勿将此验证码告知他人，验证码5分钟内有效如非本人操作，请联系我们或忽略。', '1', now(), 'admin', now(), NULL, NULL);
INSERT INTO `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YCS', '36', '本次登录验证码是#verficationCode#，请勿将此验证码告知他人，验证码5分钟内有效如非本人操作，请联系我们或忽略。', '1', now(), 'admin', now(), NULL, NULL);

