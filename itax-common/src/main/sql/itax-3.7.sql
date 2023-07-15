#开票订单表
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `goods_details`  text NULL COMMENT '商品明细json' AFTER `pay_type`;

ALTER TABLE `t_e_invoice_order`
ADD COLUMN `payment_voucher`  varchar(128) NULL COMMENT '支付凭证' AFTER `goods_details`;

#开票订单变更表
ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `pay_type`  varchar(1) NULL COMMENT '支付方式 1-线上支付 2-线下支付' AFTER `achievement_error_remark`,
ADD COLUMN `goods_details`  text NULL COMMENT '商品明细json' AFTER `pay_type`;

ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `payment_voucher`  varchar(128) NULL COMMENT '支付凭证' AFTER `goods_details`;

#订单主表
ALTER TABLE `t_e_order`
ADD COLUMN `payer_name`  varchar(256) NULL COMMENT '费用承担方' AFTER `discount_activity_id`,
ADD COLUMN `is_self_paying`  int(1) NULL DEFAULT 1 COMMENT '是否自费 1-自费 2-承担方' AFTER `payer_name`;

#开票记录明细
ALTER TABLE `t_e_invoice_record_detail`
ADD COLUMN `goods_details`  text NULL COMMENT '商品明细json' AFTER `remark`,
ADD COLUMN `is_print_detail`  int(1) NULL DEFAULT 0 COMMENT '是否打印清单 1-打印 0-不打印' AFTER `goods_details`,
MODIFY COLUMN  `invoice_details_list` TEXT NULL COMMENT '发票明细集合' AFTER `invoice_total_price_tax`;

ALTER TABLE `t_e_member_company_change`
MODIFY COLUMN `business_scope`  varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '经营范围' AFTER `ein`;
#接入方表
ALTER TABLE `t_e_oem_access_party`
ADD COLUMN `callback_url`  varchar(128) NULL COMMENT '回调地址' AFTER `applet_address`;

#注册订单表
ALTER TABLE `t_e_register_order`
ADD COLUMN `payment_voucher`  varchar(128) NULL COMMENT '支付凭证' AFTER `rejected_item`;
#注册订单表
ALTER TABLE `t_e_register_order` ADD COLUMN `pay_type`  varchar(1) NULL DEFAULT 1 COMMENT '支付方式 在线支付_1,线下结算_2';

#注册订单变更记录表
ALTER TABLE `t_e_register_order_change_record`
ADD COLUMN `payment_voucher`  varchar(128) NULL COMMENT '支付凭证' AFTER `rejected_item`;

#经营范围税费分类编码表
drop table if exists t_e_businessscope_taxcode;
create table t_e_businessscope_taxcode
(
   id                   bigint(11) not null auto_increment comment '主键id',
   business_scop_name   varchar(128) comment '经营范围',
   tax_classification_code varchar(64) comment '税收分类编码',
   tax_classification_name varchar(64) comment '税费分类名称',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(256) comment '备注',
   primary key (id)
);
alter table t_e_businessscope_taxcode comment '经营范围税费分类编码表';

#开票订单与商品的关系表
drop table if exists t_r_invoiceorder_goodsdetail;
create table t_r_invoiceorder_goodsdetail
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   goods_name           varchar(128) comment '商品名称',
   tax_classification_code varchar(32) comment '税收分类编码',
   goods_specification  varchar(40) comment '规格型号',
   goods_unit           varchar(32) comment '计量单位',
   goods_quantity       decimal(14,8) comment '商品数量',
   goods_price          bigint(11) comment '商品单价',
   goods_total_price    bigint(11) comment '总金额',
   goods_tax_rate       decimal(10,8) comment '税率',
   goods_total_tax      bigint(11) comment '总税费',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_invoiceorder_goodsdetail comment '开票订单与商品的关系表';

ALTER TABLE `t_r_invoiceorder_goodsdetail`
MODIFY COLUMN `goods_price`  decimal(14,4) NULL DEFAULT NULL COMMENT '商品单价' AFTER `goods_quantity`,
MODIFY COLUMN `goods_tax_rate`  decimal(14,8) NULL DEFAULT NULL COMMENT '税率' AFTER `goods_total_price`;

