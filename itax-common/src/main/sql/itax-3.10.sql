
#协议模板表
drop table if exists t_e_agreement_template;
create table t_e_agreement_template
(
   id                   bigint(11) not null auto_increment comment '主键id',
   template_code        varchar(32) comment '模板编码',
   template_name        varchar(64) comment '模板名称',
   template_type        int(1) comment '模板类型  1-收费标准 2-委托注册协议  3-园区办理协议',
   template_content     longblob comment '模板内容',
   template_html_url    varchar(256) comment '模板html地址(相对地址，公域)',
   template_status      int(1) comment '模板状态  1-启用 2-禁用',
   template_desc        varchar(128) comment '模板说明',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_agreement_template comment '协议模板表';

#园区与协议模板的关系表
drop table if exists t_r_park_agreement_template;
create table t_r_park_agreement_template
(
   id                   bigint(11) not null auto_increment comment '主键id',
   park_id              bigint(11) comment '园区id',
   agreement_template_id bigint(11) comment '协议模板id',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_park_agreement_template comment '园区与协议模板的关系表';

#机构管理
ALTER TABLE `t_e_oem`
ADD COLUMN `agreement_template_id`  bigint(11) NULL COMMENT '收费标准协议模板id' AFTER `work_audit_way`;

#企业对公户表
ALTER TABLE `t_e_company_corporate_account`
ADD COLUMN `single_withdrawal_limit`  bigint(11) NULL COMMENT '提现限额单笔(分)' AFTER `is_send_notice`,
ADD COLUMN `daily_withdrawal_limit`  bigint(11) NULL COMMENT '提现限额单日(分)' AFTER `single_withdrawal_limit`;

#对公户银行收款核销记录表
ALTER TABLE `t_e_collection_withdrawal_amount_change_record`
ADD COLUMN `invoice_order_no`  varchar(32) NULL COMMENT '开票订单编号' AFTER `remark`;

#对公户银行收款核销记录表 历史数据开票订单号初始化
update t_e_collection_withdrawal_amount_change_record wacr,
   t_e_corporate_account_withdrawal_order wo
set wacr.invoice_order_no = wo.invoice_order_no,
    wacr.update_time = now(),
    wacr.remark='历史数据开票订单号补充'
where wacr.order_no = wo.order_no;

#机构园区关系表
ALTER TABLE `t_r_oem_park`
ADD COLUMN `agreement_template_id` BIGINT(11) NULL   COMMENT '机构专属协议模板id' AFTER `park_id`;
#代理充值最小金额
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('recharge_agent_min_limit', '0', NULL, '后台代理充值最小金额0元', now(), 'admin', NULL, NULL, NULL);
#代理充值最大金额
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('recharge_agent_max_limit', '1000000000', NULL, '后台代理充值最大金额10000000元', now(), 'admin', NULL, NULL, NULL);

#流水表
ALTER TABLE `t_e_pay_water`
ADD COLUMN `refund_status` int(1) NULL COMMENT '退款状态 1-退款中 2-退款成功 3-退款失败' AFTER `pay_pic`;
#协议模板替换地址
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('agreement_template_type', '1', NULL, '协议模板替换地址 1-dev  2-test 3-prod', now(), 'admin', NULL, NULL, NULL);


