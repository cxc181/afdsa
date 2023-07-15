#优惠卷表
drop table if exists t_e_coupons;
create table t_e_coupons
(
   id                   bigint(11) not null auto_increment comment '主键id',
   coupons_code         varchar(32) comment '优惠卷编码',
   oem_code             varchar(12) comment '机构编码',
   face_amount          bigint(11) default 0 comment '面额',
   usable_range         varchar(32) comment '可用范围，多个之间用户逗号分割  1-个体户注册 2-开票 3-注销 4-个体户续费',
   start_date           date comment '生效日期',
   end_date             date comment '截止日期',
   status               int(1) default 0 comment '状态 0-下架 1-上架',
   description          varchar(128) comment '描述',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_coupons comment '优惠卷表';

#优惠卷发放记录表
drop table if exists t_e_coupons_issue_record;
create table t_e_coupons_issue_record
(
   id                   bigint(11) not null auto_increment comment '主键id',
   coupons_id           bigint(11) comment '优惠卷id',
   coupons_code         varchar(32) comment '优惠卷编码',
   member_id            bigint(11) comment '会员id',
   oem_code             varchar(12) comment '机构编码',
   issue_time           datetime comment '发放时间',
   use_time             datetime comment '使用时间',
   oper_user            varchar(32) comment '操作人',
   status               int(1) default 0 comment '状态  0-未使用 1-已使用 2-已过期 3-已撤回',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_coupons_issue_record comment '优惠卷发放记录表';

#埋点表
drop table if exists t_e_buried_point;
create table t_e_buried_point
(
   id                   bigint(11) not null auto_increment comment '主键id',
   oper_platform        int(1) comment '操作平台 1-微信小程序 2-支付宝小程序 3-运营平台',
   oper_function        int(1) comment '操作功能 1-banner',
   user_type            int(1) comment '用户类型 1-会员 2-非会员',
   user_id              bigint(11) comment '用户id',
   source_id            bigint(11) comment '资源id ,根据操作功能获取对应的资源id',
   oper_time            datetime comment '操作时间',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_buried_point comment '埋点表';

#个合同注册订单
ALTER TABLE `t_e_register_order`
ADD COLUMN `coupons_issue_id`  bigint(11) NULL COMMENT '优惠卷发放记录id' AFTER `is_open_authentication`,
ADD COLUMN `rejected_item`  varchar(32) NULL COMMENT '驳回项，多个项之间用逗号分割 1-字号 2-身份证 3-视频' AFTER `coupons_issue_id`;

#个合同注册订单变更表
ALTER TABLE `t_e_register_order_change_record`
ADD COLUMN `coupons_issue_id`  bigint(11) NULL COMMENT '优惠卷发放记录id' AFTER `is_open_authentication`,
ADD COLUMN `rejected_item`  varchar(32) NULL COMMENT '驳回项，多个项之间用逗号分割 1-字号 2-身份证 3-视频' AFTER `coupons_issue_id`;

#会员企业表
ALTER TABLE `t_e_member_company`
ADD COLUMN `cancel_credentials`  varchar(256) NULL COMMENT '注销凭证' AFTER `is_send_notice`;

#消费发票订单表
ALTER TABLE `t_e_consumption_invoice_order`
ADD COLUMN `invoice_imgs`  varchar(1024) NULL COMMENT '发票图片地址，多个图片之间用逗号分割' AFTER `invoice_operator`;

#消费发票订单表变动
ALTER TABLE `t_e_consumption_invoice_order_change`
ADD COLUMN `invoice_imgs`  varchar(1024) NULL COMMENT '发票图片地址，多个图片之间用逗号分割' AFTER `invoice_operator`;

#开票订单表
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `risk_commitment`  varchar(512) NULL COMMENT '风险承诺函，多个图片之间用逗号分割' AFTER `taxable_income_rate`;

#开票订单表变更表
ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `risk_commitment`  varchar(512) NULL COMMENT '风险承诺函，多个图片之间用逗号分割' AFTER `taxable_income_rate`;

#oem机构开票信息
ALTER TABLE `t_e_invoice_info_by_oem`
ADD COLUMN `hosting_status`  int(1) NULL DEFAULT 0 COMMENT '托管状态 0-未托管 1-已托管' AFTER `email`,
ADD COLUMN `tax_disc_type`  int(1) NULL COMMENT '税务盘类型 1-ukey 2-税控盘' AFTER `hosting_status`,
ADD COLUMN `tax_disc_code`  varchar(32) NULL COMMENT '税务盘编号' AFTER `tax_disc_type`,
ADD COLUMN `face_amount_type`  int(1) NULL COMMENT '票面金额类型 1-1w 2-10w 3-100w' AFTER `tax_disc_code`,
ADD COLUMN `face_amount`  bigint(11) NULL COMMENT '票面金额(分)' AFTER `face_amount_type`,
ADD COLUMN `channel`  int(1) NULL DEFAULT 1 COMMENT '通道方 1-百旺' AFTER `face_amount`,
ADD COLUMN `is_immediately_invoice`  int(1) NULL DEFAULT 1 COMMENT '是否立即开票 0-否 1-是' AFTER `channel`,
ADD COLUMN `vat_rate`  decimal(6,4) default 1 NULL COMMENT '消费发票增值税率' AFTER `is_immediately_invoice`;


#字典表
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `dict_desc`) VALUES ('notice_title_company_register', '注册申请驳回通知', '注册申请驳回通知标题模板');
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `dict_desc`) VALUES ('notice_company_register', '您申请个体工商户的资料不符合工商要求被驳回，请重新提交！您申请个体工商户的资料不符合工商要求被驳回，请重新提交！', '注册申请驳回通知内容模板');

#优惠券表
ALTER TABLE `t_e_coupons`
  ADD COLUMN `coupons_name` VARCHAR(64) NULL   COMMENT '优惠券名称' AFTER `coupons_code`;

#消费开票订单表
ALTER TABLE `t_e_consumption_invoice_order`
ADD COLUMN `category_id`  bigint(11) NULL COMMENT '开票类目id' AFTER `district_name`;

#消费开票订单变更表
ALTER TABLE `t_e_consumption_invoice_order_change`
ADD COLUMN `category_id`  bigint(11) NULL COMMENT '开票类目id' AFTER `district_name`;

#消费发票配置
INSERT INTO `t_e_oem_config` (`oem_code`, `params_code`, `params_value`, `parent_params_code`, `params_desc`, `add_time`, `add_user`)
select oem_code,'invoice_info_by_oem', '{\"drawer\":\"简晶晶\",\"payee\":\"许建山\",\"reviewer\":\"吴梦红\"}', 'invoice_info_by_oem', '消费发票开票信息',now(),'admin' from t_e_oem where oem_code is not null;

#短信模板表
update t_e_sms_template set template_content = '您的消费申请单出票失败，失败原因为：#msg#。' where template_type = 30;
update t_e_sms_template set template_content ='您的#compName#已成功在工商完成注销，您可登陆小程序在我的-我的企业-详情查看企业注销凭证。'where template_type=18;
update t_e_sms_template set template_content = '您申请个体工商户所提交的部分资料不符合工商要求被驳回，请前往“小程序-我的-企业注册订单”页面重新提交，如有疑问，请联系客服#telPhone#。' where template_type = 27;

#字典表
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`)
VALUES ('register_rejected_notice_tmpl', '您申请个体工商户的资料不符合工商要求被驳回，请重新提交！', NULL, '驳回驳回通知模板', now(), 'admin');

#工单表
ALTER TABLE `t_e_work_order`
MODIFY COLUMN `remark`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;

#工单表变更表
ALTER TABLE `t_e_work_order_change_record`
MODIFY COLUMN `remark`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;

#开票订单表
ALTER TABLE `t_e_invoice_order`
MODIFY COLUMN `remark`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;

#开票订单变更表
ALTER TABLE `t_e_invoice_order_change_record`
MODIFY COLUMN `remark`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;


#注销凭证字段扩容
ALTER TABLE `t_e_member_company`
MODIFY COLUMN `cancel_credentials`  varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '注销凭证' AFTER `is_send_notice`;

ALTER TABLE `t_e_company_cancel_order`
MODIFY COLUMN `attachment_addr`  varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '附件地址' AFTER `company_name`;

ALTER TABLE `t_e_company_cancel_order_change_record`
MODIFY COLUMN `attachment_addr`  varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '附件地址' AFTER `company_name`;

UPDATE `sys_e_dictionary` SET  `dict_value`='您的直推用户#registeredName#有一笔企业注册订单被资料驳回，请您关注~',`update_time`=now(), `update_user`=NULL, `remark`=NULL WHERE `dict_code`='register_pay_notice_tmpl';

ALTER TABLE `t_e_invoice_order`
MODIFY COLUMN `invoice_imgs`  varchar(3000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发票图片地址，多个图片之间用逗号分割' AFTER `personal_income_tax_quota`;


ALTER TABLE `t_e_invoice_order_change_record`
MODIFY COLUMN `invoice_imgs`  varchar(3000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发票图片地址，多个图片之间用逗号分割' AFTER `personal_income_tax_quota`;

#开票记录明细
ALTER TABLE `t_e_invoice_record_detail`
ADD COLUMN `is_format_create`  int(1) NULL DEFAULT NULL COMMENT '是否已生成版式 1-生成中 2-已生成 3-生成失败' AFTER `status`;












