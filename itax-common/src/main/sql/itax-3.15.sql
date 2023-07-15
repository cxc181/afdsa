#注册订单
ALTER TABLE `t_e_register_order`
ADD COLUMN `registered_capital`  decimal(15,6) NULL COMMENT '注册资本（万元）' AFTER `taxcode_business_scope`;

#注册订单变更表
ALTER TABLE `t_e_register_order_change_record`
ADD COLUMN `registered_capital`  decimal(15,6) NULL COMMENT '注册资本（万元）' AFTER `taxcode_business_scope`;

#我的企业
ALTER TABLE `t_e_member_company`
ADD COLUMN `registered_capital`  decimal(15,6) NULL COMMENT '注册资本（万元）' AFTER `sign_img`,
ADD COLUMN `approved_turnover`  bigint(20) NULL COMMENT '核定经营额(分）' AFTER `registered_capital`;

#企业变更表
ALTER TABLE `t_e_member_company_change`
ADD COLUMN `registered_capital`  decimal(15,6) NULL COMMENT '注册资本（万元）' AFTER `tax_reg_date`,
ADD COLUMN `approved_turnover`  bigint(20) NULL COMMENT '核定经营额(分）' AFTER `registered_capital`;

#产品与园区的关系
ALTER TABLE `t_r_product_park`
ADD COLUMN `company_type`  int(1) NULL COMMENT '企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任' AFTER `remark`,
ADD COLUMN `process_mark`  int(1) NULL COMMENT '流程标记 1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程 ' AFTER `company_type`;

#园区与协议模板的关系表
ALTER TABLE `t_r_park_agreement_template`
ADD COLUMN `product_id`  bigint(11) NULL COMMENT '产品id' AFTER `remark`,
ADD COLUMN `company_type`  int(1) NULL COMMENT '企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任' AFTER `product_id`;

#产品管理
ALTER TABLE `t_e_product`
ADD COLUMN `company_type`  int(1) NULL COMMENT '企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任' AFTER `cancel_total_limit`,
ADD COLUMN `agreement_template_id`  bigint(11) NULL COMMENT '收费标准协议模板id' AFTER `company_type`;

#工商注册预订单
drop table if exists t_e_register_pre_order;
CREATE TABLE `t_e_register_pre_order` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `member_id` bigint(11) NOT NULL COMMENT '会员id',
	`order_no` varchar(32) NOT NULL COMMENT '订单号',
  `oem_code` varchar(12) NOT NULL COMMENT '机构编码',
  `operator_name` varchar(32) NOT NULL COMMENT '法人姓名',
  `contact_phone` varchar(16) NOT NULL COMMENT '联系电话',
  `email` varchar(32) DEFAULT NULL COMMENT '电子邮箱',
  `id_card_front` varchar(256) NOT NULL COMMENT '身份证正面',
  `id_card_reverse` varchar(256) NOT NULL COMMENT '身份证反面',
  `id_card_number` varchar(20) NOT NULL COMMENT '身份证号码',
  `id_card_addr` varchar(128) DEFAULT NULL COMMENT '身份证地址',
  `expire_date` varchar(21) DEFAULT NULL COMMENT '身份证有效期，格式如2010/01/01-2020/01/01',
  `shop_name` varchar(32) DEFAULT NULL COMMENT '字号',
  `shop_name_one` varchar(32) DEFAULT NULL COMMENT '备选字号1',
  `shop_name_two` varchar(32) DEFAULT NULL COMMENT '备选字号2',
  `organization_form` int(1) DEFAULT NULL COMMENT '组织形式  -- 暂时预留',
  `industry_id` bigint(11) NOT NULL COMMENT '行业类型id',
  `business_address` varchar(128) DEFAULT NULL COMMENT '经营地址',
  `ratify_tax` varchar(64) DEFAULT NULL COMMENT '核定税种',
  `business_scope` varchar(256) DEFAULT NULL COMMENT '经营范围',
  `registered_name` varchar(64) DEFAULT NULL COMMENT '注册名称',
  `pay_order_no` varchar(32) DEFAULT NULL COMMENT '支付订单编号',
  `sign_img` varchar(256) DEFAULT NULL COMMENT '签名单',
  `order_amount` bigint(11) DEFAULT '0' COMMENT '订单金额',
  `discount_amount` bigint(11) DEFAULT '0' COMMENT '优惠金额',
  `pay_amount` bigint(11) DEFAULT '0' COMMENT '支付金额',
  `alert_number` int(1) DEFAULT NULL COMMENT '通知次数',
  `regist_file` varchar(256) DEFAULT NULL COMMENT '登记文件',
  `agent_account` varchar(32) DEFAULT NULL COMMENT '经办人账号',
  `customer_service_phone` varchar(20) DEFAULT NULL COMMENT '专属客服电话',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `add_user` varchar(32) DEFAULT NULL COMMENT '添加人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '修改人',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  `company_type` int(1) NOT NULL COMMENT '企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任',
  `video_addr` varchar(256) DEFAULT NULL COMMENT '短视频地址',
  `is_other` int(1) DEFAULT '0' COMMENT '是否为他人办理 0-本人办理 1-为他人办理',
  `is_wechat_authorization` int(1) DEFAULT NULL COMMENT '是否微信授权 0-未授权 1-已授权',
  `wechat_message_notice_result` int(1) DEFAULT NULL COMMENT '微信消息通知结果 0-失败 1-成功',
  `wechat_message_error_cause` varchar(500) DEFAULT NULL COMMENT '微信消息通知失败原因',
  `wechat_message_notice_time` datetime DEFAULT NULL COMMENT '微信消息通知时间',
  `example_name` varchar(64) DEFAULT NULL COMMENT '示例名称',
  `wechat_message_template_id` bigint(11) DEFAULT NULL COMMENT '微信消息模板id',
  `is_open_authentication` int(1) DEFAULT '0' COMMENT '是否已开启身份验证 0-未开启 1-已开启',
  `coupons_issue_id` bigint(11) DEFAULT NULL COMMENT '优惠卷发放记录id',
  `rejected_item` varchar(32) DEFAULT NULL COMMENT '驳回项，多个项之间用逗号分割 1-字号 2-身份证 3-视频',
  `payment_voucher` varchar(128) DEFAULT NULL COMMENT '支付凭证',
  `pay_type` varchar(1) DEFAULT NULL COMMENT '支付方式',
  `is_all_codes` int(1) DEFAULT '1' COMMENT '是否已全部赋码 0-否 1-是',
  `industry_business_scope` varchar(256) DEFAULT NULL COMMENT '行业经验范围',
  `own_business_scope` varchar(128) DEFAULT NULL COMMENT '自选经验范围',
  `taxcode_business_scope` varchar(128) DEFAULT NULL COMMENT '税费分类编码对应的经验范围',
  `registered_capital` decimal(15,6) DEFAULT NULL COMMENT '注册资本（万元）',
  PRIMARY KEY (`id`)
) COMMENT='工商注册预订单';

#企业核心成员信息表
drop table if exists t_e_company_core_personnel;
create table t_e_company_core_personnel
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   member_id            bigint(11) comment '会员id',
   company_id           bigint(11) comment '企业id',
   company_type         int(1) comment '企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任',
   personnel_type       int(1) comment '成员类型 1-股东 2-监事 3-财务',
   identity_type        int(1) comment '身份类型 1-自然人 2-企业',
   personnel_name       varchar(32) comment '姓名',
   contact_phone        varchar(32) comment '联系电话',
   certificate_no       varchar(32) comment '证件号',
   certificate_addr     varchar(128) comment '证件地址',
   expire_date          varchar(32) comment '证件有效期',
   id_card_front        varchar(256) comment '身份证正面',
   id_card_reverse      varchar(256) comment '身份证反面',
   business_license     varchar(256) comment '企业营业执照',
   investment_amount    decimal(15,6) comment '投资金额(万元)',
   share_proportion     decimal(7,4) comment '占股比例(小数)',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_company_core_personnel comment '企业核心成员信息表';

#字典表
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`) VALUES ('tax_monitoring_individual', '[{"parkId": "1","quotaWarnAmount": "450000"}]', NULL, '税务监控配置(个体户)', NOW(), 'admin');
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`) VALUES ('tax_monitoring_independently', '[{"parkId": "1","quotaWarnAmount": "450000"}]', NULL, '税务监控配置(个独)', NOW(), 'admin');
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`) VALUES ('tax_monitoring_limited_partner', '[{"parkId": "1","quotaWarnAmount": "450000"}]', NULL, '税务监控配置(有限合伙)', NOW(), 'admin');
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`) VALUES ('tax_monitoring_limited_liability', '[{"parkId": "1","quotaWarnAmount": "450000"}]', NULL, '税务监控配置(有限责任)', NOW(), 'admin');
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`) VALUES ('shareholder_number', '49', NULL, '(有限合伙/有限责任企业)股东数上限', NOW(), 'admin');

#办理协议
INSERT INTO `t_r_park_agreement_template`(`park_id`, `agreement_template_id`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`,`product_id`, `company_type`)
select o.park_id,case when a.template_type = 2 and o.agreement_template_id is not null then o.agreement_template_id else p.agreement_template_id end,
now(),'admin',now(),'admin','初始化数据',(select id from t_e_product where oem_code = o.oem_code and prod_type = 1 ),1
from t_r_park_agreement_template p,t_e_agreement_template a,t_r_oem_park o
where p.agreement_template_id = a.id
and o.park_id = p.park_id;

update t_e_oem o,t_e_product p
set p.agreement_template_id = o.agreement_template_id,p.update_time=now(),p.update_user='admin',
p.company_type = 1
where o.oem_code = p.oem_code and p.prod_type <5 ;

# 产品园区关系表新增企业类型、流程标记，历史数据处理
UPDATE t_r_product_park pp SET pp.`process_mark` = (SELECT p.`process_mark` FROM t_e_park p WHERE p.`id` = pp.`park_id`);