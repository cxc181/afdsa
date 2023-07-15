#企业月度账本表
drop table if exists t_e_company_account_book_month;
create table t_e_company_account_book_month
(
   id                   bigint(11) not null auto_increment comment '主键id',
   company_id           bigint(11) comment '企业id',
   ein                  varchar(32) comment '企业税号',
   year                 int(4) comment '所属年份',
   quarter              int(1) comment '所属季度',
   invoice_income       bigint(11) comment '有票收入',
   no_invoice_income    bigint(11) comment '无票收入',
   invoice_spending     bigint(11) comment '有票支出',
   no_invoice_spending  bigint(11) comment '无票支出',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_company_account_book_month comment '企业月度账本表';

#企业季度账本
drop table if exists t_e_company_account_book_quarter;
create table t_e_company_account_book_quarter
(
   id                   bigint(11) not null auto_increment comment '主键id',
   company_id           bigint(11) comment '企业id',
   ein                  varchar(32) comment '企业税号',
   year                 int(4) comment '所属年份',
   quarter              int(1) comment '所属季度',
   vat_surcharge_tax_amount bigint(11) comment '增值税及附加',
   income_tax_amount    bigint(11) comment '所得税',
   vat_surcharge_tax_imgs varchar(256) comment '增值税及附件完税凭证',
   income_tax_imgs      varchar(256) comment '所得税完税凭证',
   pay_taxes_status     int(1) comment '完税状态  0-未完税 1-已完税',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_company_account_book_quarter comment '企业季度账本';

#oem机构对公户配置表
drop table if exists t_e_oem_corporate_account_config;
create table t_e_oem_corporate_account_config
(
   id                   bigint(11) not null auto_increment comment '主键id',
   oem_code             varchar(12) comment '机构编码',
   corporate_account    varchar(32) comment '对公账户',
   corporate_account_bank_name varchar(64) comment '对公户银行名称',
   oem_comapny_name     varchar(64) comment '企业名称',
   voucher_member_code  varchar(32) comment '制单员编号',
   entrust_project_code varchar(32) comment '委托项目编号',
   project_use_code     varchar(32) comment '项目用途编号',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_oem_corporate_account_config comment 'oem机构对公户配置表';

#oem机构对公户银行收款记录表
drop table if exists t_e_oem_corporate_account_collection_record;
create table t_e_oem_corporate_account_collection_record
(
   id                   bigint(11) not null auto_increment comment '主键id',
   oem_code             varchar(12) comment 'oem机构',
   corporate_account    varchar(32) comment '对公户账号',
   bank_collection_record_no varchar(64) comment '银行唯一编号',
   smy                  varchar(64) comment '摘要',
   dhamt                bigint(11) comment '借方发生额(分)',
   cr_hpnam             bigint(11) comment '贷方发生额(分)',
   hpn_amt              bigint(11) comment '发生金额(分)',
   acba                 bigint(11) comment '账户余额(分)',
   trading_time         datetime comment '交易时间',
   other_party_bank_account varchar(32) comment '对方账户名',
   other_party_bank_name varchar(64) comment '对方开户行',
   other_party_bank_number varchar(32) comment '对方账户',
   trading_status       int(1) comment '交易状态  1-支出 2-收入 ',
   trading_remark       varchar(128) comment '交易备注',
   remaining_available_amount bigint(11) comment '剩余可用额度（分）',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_oem_corporate_account_collection_record comment 'oem机构对公户银行收款记录表';

#oem机构收入收入明细与订单的关系表
drop table if exists t_r_oem_collection_record_by_order;
create table t_r_oem_collection_record_by_order
(
   id                   bigint(11) not null auto_increment comment '主键id',
   oem_collection_record_id bigint(11) comment 'oem机构收款记录id',
   order_no             varchar(32) comment '订单号',
   use_amount           bigint(11) comment '使用金额',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_oem_collection_record_by_order comment 'oem机构收入收入明细与订单的关系表';

#园区与企业类型的关系表
drop table if exists t_e_park_company_type;
create table t_e_park_company_type
(
   id                   bigint(11) not null auto_increment comment '主键id',
   park_id              bigint(11) comment '园区id',
   company_type         int(1) comment '企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任',
   quarter_invoice_amount bigint(11) comment '季开票限额(分)',
   policy_file_url      varchar(128) comment '政策文件地址',
   read_content         varchar(128) comment '视频认证阅读内容',
   transact_require     varchar(128) comment '办理要求',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_park_company_type comment '园区与企业类型的关系表';

#园区税收政策
drop table if exists t_e_park_tax_policy;
create table t_e_park_tax_policy
(
   id                   bigint(11) not null auto_increment comment '主键id',
   park_id              bigint(11) comment '园区id',
   levy_way             int(1) comment '计税方式 1：预缴征收率，2：核定应税所得率',
   vat_breaks_amount    bigint(11) comment '增值税个税减免额度',
   vat_breaks_cycle     int(1) comment '增值税个税减免周期 1-按月 2-按季度',
   surcharge_breaks_amount bigint(11) comment '附加税减免额度',
   surcharge_breaks_cycle int(1) comment '附加税减免周期  1-按月 2-按季度',
   income_tax_breaks_amount bigint(11) comment '所得税减免额度',
   income_tax_breaks_cycle int(1) comment '所得税减免周期 1-按月 2-按季度',
   income_levy_type     int(1) comment '所得税征收方式 1-查账征收 2-核定征收',
   taxpayer_type        int(1) comment '纳税性质  1-小规模纳税人 2-一般纳税人',
   is_stamp_duty_halved int(1) comment '印花税是否减半 0-否 1-是',
   is_water_conservancy_fund_halved int(1) comment '水利建设基金是否减半 0-否 1-是',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_park_tax_policy comment '园区税收政策';

#园区税收政策变更表
drop table if exists t_e_park_tax_policy_changes;
create table t_e_park_tax_policy_changes
(
   id                   bigint(11) not null auto_increment comment '主键id',
   park_tax_policy_id   char(10) comment '园区政策id',
   park_id              bigint(11) comment '园区id',
   levy_way             int(1) comment '计税方式 1：预缴征收率，2：核定应税所得率',
   vat_breaks_amount    bigint(11) comment '增值税个税减免额度',
   vat_breaks_cycle     int(1) comment '增值税个税减免周期 1-按月 2-按季度',
   surcharge_breaks_amount bigint(11) comment '附加税减免额度',
   surcharge_breaks_cycle int(1) comment '附加税减免周期  1-按月 2-按季度',
   income_tax_breaks_amount bigint(11) comment '所得税减免额度',
   income_tax_breaks_cycle int(1) comment '所得税减免周期 1-按月 2-按季度',
   income_levy_type     int(1) comment '所得税征收方式 1-查账征收 2-核定征收',
   taxpayer_type        int(1) comment '纳税性质  1-小规模纳税人 2-一般纳税人',
   is_stamp_duty_halved int(1) comment '印花税是否减半 0-否 1-是',
   is_water_conservancy_fund_halved int(1) comment '水利建设基金是否减半 0-否 1-是',
   tax_rules_config_json text comment '税费规则配置json',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_park_tax_policy_changes comment '园区税收政策变更表';
