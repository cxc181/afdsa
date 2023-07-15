#注册订单与商品明细的关系表
drop table if exists t_r_register_order_goods_detail;
create table t_r_register_order_goods_detail
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   goods_name           varchar(128) comment '商品名称',
   tax_classification_code varchar(32) comment '税收分类编码',
   park_businessscope_id bigint(11) comment '园区经营范围id',
   businessscope_name   varchar(128) comment '经营范围名称',
   businessscope_base_id bigint(11) comment '经营范围基础库id',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_register_order_goods_detail comment '注册订单与商品明细的关系表';

#园区经营范围表
drop table if exists t_e_park_businessscope;
create table t_e_park_businessscope
(
   id                   bigint(11) not null auto_increment comment '主键id',
   park_id              bigint(11) comment '园区id',
   businessscope_name  varchar(128) comment '经营范围名称',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   is_delete            int(1) NOT NULL DEFAULT 1 COMMENT '是否删除 0 删除 1正常',
   primary key (id)
);
alter table t_e_park_businessscope comment '园区经营范围表';

#园区经营范围与经营范围基础库关系表
drop table if exists t_r_park_businessscope_taxcode;
create table t_r_park_businessscope_taxcode
(
   id                   bigint(11) not null auto_increment comment '主键id',
   park_businessscope_id bigint(11) comment '园区经营范围id',
   businessscope_base_id bigint(11) comment '经营范围基础库id',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_park_businessscope_taxcode comment '园区经营范围与经营范围基础库关系表';

#企业经营范围表
drop table if exists t_e_member_company_businessscope;
create table t_e_member_company_businessscope
(
   id                   bigint(11) not null auto_increment comment '主键id',
   company_id           bigint(11) comment '企业id',
   park_id              bigint(11) comment '园区id',
   businessscope_base_id bigint(11) comment '经营范围基础库id',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_member_company_businessscope comment '企业经营范围表';


#开票订单表
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `is_recalculate_service_fee`  int(1)  NULL DEFAULT 1 COMMENT '服务费是否已重新计算 0-否 1-是' AFTER `relevance_order_no`;

#开票订单变更表
ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `is_recalculate_service_fee`  int(1)  NULL DEFAULT 1 COMMENT '服务费是否已重新计算 0-否 1-是' AFTER `relevance_order_no`;

#注册订单表
ALTER TABLE `t_e_register_order`
ADD COLUMN `is_all_codes`  int(1) NULL DEFAULT 1 COMMENT '是否已全部赋码 0-否 1-是' AFTER `pay_type`;

#注册订单变更表
ALTER TABLE `t_e_register_order_change_record`
ADD COLUMN `is_all_codes`  int(1) NULL DEFAULT 1 COMMENT '是否已全部赋码 0-否 1-是' AFTER `payment_voucher`;

#注册订单表
ALTER TABLE `t_e_register_order`
ADD COLUMN `industry_business_scope`  varchar(256) NULL COMMENT '行业经验范围' AFTER `is_all_codes`,
ADD COLUMN `own_business_scope`  varchar(128) NULL COMMENT '自选经验范围' AFTER `industry_business_scope`,
ADD COLUMN `taxcode_business_scope`  varchar(128) NULL COMMENT '税费分类编码对应的经验范围' AFTER `own_business_scope`;

#注册订单变更表
ALTER TABLE `t_e_register_order_change_record`
ADD COLUMN `industry_business_scope`  varchar(256) NULL COMMENT '行业经验范围' AFTER `is_all_codes`,
ADD COLUMN `own_business_scope`  varchar(128) NULL COMMENT '自选经验范围' AFTER `industry_business_scope`,
ADD COLUMN `taxcode_business_scope`  varchar(128) NULL COMMENT '税费分类编码对应的经验范围' AFTER `own_business_scope`;

#短信模板表
UPDATE t_e_sms_template SET template_content = '尊敬的用户：您有开票订单审核未通过(原因：#reason#)，支付资金已退回至您的可用余额。', update_time = NOW(), remark='文案更新' WHERE template_type = 12


#园区上传税单记录表
ALTER TABLE `t_e_park_tax_bill_file_record`
ADD COLUMN `cancellation_amount`  bigint(20)  DEFAULT '0' COMMENT '作废/红冲金额' AFTER `tax_bill_seasonal`;


#字典表
INSERT into sys_e_dictionary (dict_code,dict_value,dict_desc,add_time,add_user) VALUES('audit_fail_remark','存在一笔审核不通过订单造成后续部分订单税费和服务费计算错误，需重新提交','开票工单审核失败其他订单备注',NOW(),'admin');
INSERT into sys_e_dictionary (dict_code,dict_value,dict_desc,add_time,add_user) VALUES('registration_protocol_address','https://itax.yuncaiol.cn/tax/static/page/user_agreement.html?parkCode=#{park_code}&productType=1&oemCode=#{oem_code}','《委托注册服务协议》',NOW(),'admin');