#支付流水表新增订单类型
ALTER TABLE `t_e_pay_water`
MODIFY COLUMN `order_type`  int(1) NOT NULL COMMENT '订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税  15-托管费续费 16-对公户续费' AFTER `pay_amount`;
#产品表新增产品类型
ALTER TABLE `t_e_product`
MODIFY COLUMN `prod_type`  int(2) NOT NULL COMMENT '产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-黄金会员（废弃） 10-钻石会员 （废弃）\r\n11-个体注销 12-个独注销 13-有限合伙注销 14-有限责任注销 15-公户申请和托管 16-个体托管费续费 17-对公户年费续费' AFTER `prod_code`;
#开票订单表新增开票方式枚举
ALTER TABLE `t_e_invoice_order`
MODIFY COLUMN `create_way`  int(1) NULL DEFAULT NULL COMMENT '开票方式 1-自助开票 2-集团代开 3-佣金开票 4-第三方开票' AFTER `wallet_type`;
#园区经营地址生成规则新增字段
ALTER TABLE `t_e_park_business_address_rules`
ADD COLUMN `address_type`  int(1) NULL DEFAULT NULL COMMENT '地址类型1-固定经营地址 2-按房间号自动递增' AFTER `remark`,
ADD COLUMN `regist_postfix`  varchar(256) NULL COMMENT '地址后缀' AFTER `address_type`;
#对公户账号表新增过期标识
ALTER TABLE `t_e_company_corporate_account`
MODIFY COLUMN `status` int(1) DEFAULT NULL COMMENT '状态(1-正常  2-冻结 3-注销)' AFTER `bind_bank_phone`,
ADD COLUMN `overdue_status`  int(1) NULL DEFAULT 1 COMMENT '过期状态 1-正常 2-即将过期 3-已过期' AFTER `headquarters_no`,
ADD COLUMN `is_send_notice`  int(1) NULL DEFAULT 0 COMMENT '是否发送过期提醒通知 0-未发送 1-已发送' AFTER `overdue_status`;
#处理对公户账号表过期状态字段历史数据
UPDATE t_e_company_corporate_account SET overdue_status = 1 WHERE overdue_status IS NULL;
#订单主表订单类型新增枚举
ALTER TABLE `t_e_order`
MODIFY COLUMN `order_type`  int(1) NOT NULL COMMENT '订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票 13-补税 14-退税 15-托管费续费 16-对公户续费' AFTER `user_type`,
MODIFY COLUMN `order_status`  int(2) NOT NULL COMMENT '订单状态：工商注册(0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，10-待设立登记、11-待提交签名、12-签名待确认),\r\n开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过),\r\n会员升级( 0-待支付,1-支付中,2-财务审核,3-已完成,4-已取消), \r\n充值提现、对公户提现、补税，退税(0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败),工商注销(0-待付款,1-注销处理中,2-注销成功,3-已取消),证件申请( 0-待付款 1-待发货,2-出库中,3-待签收,4-已签收,5-已取消),公户申请（0-待付款,1-等待预约,2-已完成,3-已取消）消费开票（0-待出票 1-出票中 2-已出票 3-出票失败）托管费续费、对公户续费（0-待支付、1-支付中、2-已完成、3-已取消）' AFTER `order_type`,
MODIFY COLUMN `source_type` int(1) NULL COMMENT '操作小程序来源 1-微信小程序 2-支付宝小程序 3-接入方 4-抖音' AFTER `remark`;
#新增对公户续费订单表
CREATE TABLE `t_e_corporate_account_cont_order` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) DEFAULT NULL COMMENT '订单号',
  `user_id` bigint(32) DEFAULT NULL COMMENT '用户id',
  `company_id` bigint(11) DEFAULT NULL COMMENT '企业id',
  `corporate_account_id` bigint(11) DEFAULT NULL COMMENT '对公户id',
  `corporate_account_bank_name` varchar(32) DEFAULT NULL COMMENT '对公户开户行',
  `corporate_account` varchar(32) DEFAULT NULL COMMENT '对公户银行账号',
  `order_amount` bigint(32) DEFAULT NULL COMMENT '订单金额（分）',
  `pay_amount` bigint(32) DEFAULT NULL COMMENT '支付金额（分）',
  `park_id` bigint(20) DEFAULT NULL COMMENT '园区id',
  `oem_code` varchar(32) DEFAULT NULL COMMENT '机构编码',
  `expiration_time` datetime DEFAULT NULL COMMENT '过期时间',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `add_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对公户续费订单表';



#新增对公户续费订单表变更表
CREATE TABLE `t_e_corporate_account_cont_order_change` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) DEFAULT NULL COMMENT '订单号',
  `user_id` bigint(32) DEFAULT NULL COMMENT '用户id',
  `company_id` bigint(11) DEFAULT NULL COMMENT '企业id',
  `corporate_account_id` bigint(11) DEFAULT NULL COMMENT '对公户id',
  `corporate_account_bank_name` varchar(32) DEFAULT NULL COMMENT '对公户开户行',
  `corporate_account` varchar(32) DEFAULT NULL COMMENT '对公户银行账号',
  `order_amount` bigint(32) DEFAULT NULL COMMENT '订单金额（分）',
  `pay_amount` bigint(32) DEFAULT NULL COMMENT '支付金额（分）',
  `park_id` bigint(20) DEFAULT NULL COMMENT '园区id',
  `oem_code` varchar(32) DEFAULT NULL COMMENT '机构编码',
  `status` int(1) DEFAULT NULL COMMENT '0-待支付、1-支付中、2-已完成、3-已取消',
  `expiration_time` datetime DEFAULT NULL COMMENT '过期时间',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `add_user` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '更新人',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对公户续费订单表记录表';

#
ALTER TABLE `t_e_invoice_order`
MODIFY COLUMN `create_way`  int(1) NULL DEFAULT NULL COMMENT '开票方式 1-自助开票 2-集团代开 3-佣金开票 5-接入方开票' AFTER `wallet_type`;
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `pay_type`  varchar(1) NULL DEFAULT 1 COMMENT '1-线上支付 2-线下支付' AFTER `achievement_error_remark`;



#短信模板配置表
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`)
SELECT oem_code, '40', '您有 #number# 家对公户年费有效期不足#days#天，为了不影响您提现，请立即前往“我的对公户”进行续费哦。', '1', NOW(), 'admin'
FROM t_e_oem WHERE oem_code is not null and oem_code != '';
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`)
SELECT oem_code, '41', '您有 #number# 家对公户年费已过期，为了不影响您提现，请立即前往“我的对公户”进行续费哦。', '1', NOW(), 'admin'
FROM t_e_oem WHERE oem_code is not null and oem_code != '';

#字典表
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('annual_fee_will_expire', '您有 #count# 家对公户年费还有不到#surplusDays#天就到期了，为了不影响您提现，请立即前往“我的对公户”进行续费哦。', NULL, '对公户年费即将过期短信', NOW(), 'admin', NULL, NULL, NULL);
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('annual_fee_is_overdue', '您有 #count# 家对公户年费已经过期了，为了不影响您提现，请立即前往“我的对公户”进行续费哦', NULL, '对公户年费已过期短信', NOW(), 'admin', NULL, NULL, NULL);
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('age_range_of_operator', '[18,59]', NULL, '经营者年龄区间', NOW(), 'admin', NULL, NULL, NULL);
UPDATE `sys_e_dictionary` SET `dict_value`='[{"key":"全部","value":"1"},{"key":"充值","value":"2"},{"key":"提现","value":"3"},{"key":"企业注册","value":"5"},{"key":"企业开票","value":"6"},{"key":"会员升级","value":"7"},{"key":"企业注销","value":"8"},{"key":"企业补税","value":"13"},{"key":"企业退税","value":"14"},{"key":"续费","value":"15"}]'  WHERE dict_code = 'consumerBillType' LIMIT 1;
UPDATE `sys_e_dictionary` SET `dict_value`='[{"key": "全部","value": "-1"}, {"key": "企业注册","value": "5"}, {"key": "企业开票","value": "6"}, {"key": "会员升级","value": "7"}, {"key": "企业注销","value": "8"}, {"key": "对公户申请","value": "10"}, {"key": "续费","value": "15"}]'  WHERE dict_code = 'consumerInvBillType' LIMIT 1;

#消息通知表
ALTER TABLE `t_e_message_notice`
MODIFY COLUMN `business_type`  int(1) NULL DEFAULT NULL COMMENT '业务类型 1-开户待支付 2-开户已完成 3-开票 4-用章 5-开票流水审核  6-待身份验证 7-托管费到期提醒 8-工商注册确认登记 9-工商注册用户未提交签名 10-对公户年费到期提醒' AFTER `open_mode`;
#园区经营地址生成规则表历史数据地址类型添加默认值
update t_e_park_business_address_rules set address_type = 2 where  address_type is null;
# 工单备注字段长度增加
alter table t_e_work_order modify remark  varchar(256);
alter table t_e_work_order_change_record modify remark  varchar(256);
alter table t_e_invoice_order_change_record modify remark  varchar(256);
alter table t_e_invoice_order_change_record modify achievement_error_remark  varchar(256);
alter table t_e_invoice_order modify audit_error_remark  varchar(256);
alter table t_e_invoice_order modify achievement_error_remark  varchar(256);
alter table t_e_park_business_address_rules modify regist_unit  varchar(16);