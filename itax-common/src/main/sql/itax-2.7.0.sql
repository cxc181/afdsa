#======V2.7.0版本数据脚本
#园区税单表
ALTER TABLE `t_park_tax_bill`
ADD COLUMN `should_upload_vat_vouchers_company_number`  int NULL COMMENT '应上传增值税凭证企业' AFTER `supplement_tax_money`,
ADD COLUMN `should_upload_iit_vouchers_company_number`  int NULL COMMENT '应上传个税凭证企业' AFTER `should_upload_vat_vouchers_company_number`,
ADD COLUMN `already_upload_vat_vouchers_company_number`  int NULL AFTER `should_upload_iit_vouchers_company_number`,
ADD COLUMN `already_upload_iit_vouchers_company_number`  int NULL COMMENT '已上传个税凭证企业' AFTER `already_upload_vat_vouchers_company_number`,
ADD COLUMN `vouchers_status`  int NULL COMMENT '凭证状态 0-未上传 1-解析中 2-已上传' AFTER `already_upload_iit_vouchers_company_number`;
#企业税单
ALTER TABLE `t_e_company_tax_bill`
ADD COLUMN `iit_voucher_pic`  varchar(128) NULL COMMENT '个人所得税凭证图片' AFTER `over_time_is_sms`,
ADD COLUMN `vat_voucher_pic`  varchar(128) NULL COMMENT '增值税凭证图片' AFTER `iit_voucher_pic`;

#对公户申请订单表
ALTER TABLE `t_e_corporate_account_apply_order`
ADD COLUMN `pay_time`  datetime NULL COMMENT '付款完成时间' AFTER `corporate_account_id`;

#对公户申请订单变更记录表
ALTER TABLE `t_e_corporate_account_apply_order_change`
ADD COLUMN `pay_time`  datetime NULL COMMENT '付款完成时间' AFTER `corporate_account_id`;

#企业开票类目关系表
ALTER TABLE `t_e_company_invoice_category`
ADD COLUMN `category_base_id`  bigint(11) NULL COMMENT '类目id' AFTER `industry_id`;

#行业开票类目表修改
ALTER TABLE `t_e_invoice_category`
ADD COLUMN `park_id`  bigint(11) NULL COMMENT '园区id' AFTER `industry_id`,
ADD COLUMN `category_base_id`  bigint(11) NULL COMMENT '类目id' AFTER `park_id`;

#园区表
ALTER TABLE `t_e_park`
ADD COLUMN `drawer`  varchar(32) NULL COMMENT '开票人' AFTER `verify_desc`,
ADD COLUMN `payee`  varchar(32) NULL COMMENT '收款人' AFTER `drawer`,
ADD COLUMN `reviewer`  varchar(32) NULL COMMENT '复核人' AFTER `payee`;

#集团开票类目表
ALTER TABLE `t_e_invoice_category_group`
ADD COLUMN `category_base_id`  bigint(11) NULL AFTER `industry_id`;

#税单完税凭证解析记录表
drop table if exists t_e_tax_bill_credentials_record;
create table t_e_tax_bill_credentials_record
(
   id                   bigint(11) not null auto_increment comment '主键id',
   company_tax_bill_id  bigint(11) comment '企业税单id',
   ein                  varchar(32) comment '税号',
   park_tax_bill_id     bigint(11) comment '园区税单id',
   batch_number         varchar(32) comment '批次号',
   iit_voucher_pic      varchar(256) comment '个税凭证地址',
   vat_voucher_pic      varchar(256) comment '增值税凭证地址',
   status               int(1) comment '解析状态 0-解析成功 1-解析失败',
   result_msg           varchar(128) comment '失败原因',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_tax_bill_credentials_record comment '税单完税凭证解析记录表';

#基础开票类目库
drop table if exists t_e_invoice_category_base;
create table t_e_invoice_category_base
(
   id                   bigint(11) not null auto_increment comment '主键id',
   tax_classification_code varchar(32) comment '税收分类编码',
   tax_classification_name varchar(32) comment '税收分类名称',
   tax_classification_abbreviation varchar(32) comment '税收分类简称',
   goods_name           varchar(32) comment '商品名称',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_invoice_category_base comment '基础开票类目库';

#企业税务托管表
drop table if exists t_e_company_tax_hosting;
create table t_e_company_tax_hosting
(
   id                   bigint(11) not null auto_increment comment '主键id',
   company_id           bigint(11) comment '企业id',
   tax_disc_type        int(1) comment '税务盘类型 1-ukey 2-税控盘',
   tax_disc_code        varchar(32) comment '税务盘编号',
   channel              int(1) comment '通道方 1-百旺',
   status               int(1) default 0 comment '托管状态 0-未托管 1-已托管',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_company_tax_hosting comment '企业税务托管表';

#oem机构开票类目关系表
drop table if exists t_r_oem_invoice_category;
create table t_r_oem_invoice_category
(
   id                   bigint(11) not null auto_increment comment '主键id',
   oem_invoice_info_id  bigint(11) comment 'oem机构开票信息id',
   oem_code             varchar(12) comment '机构编码',
   category_base_id bigint(11) comment '类目库id',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_oem_invoice_category comment 'oem机构开票类目关系表';

#开票记录表
drop table if exists t_e_invoice_record;
create table t_e_invoice_record
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   oem_code             varchar(12) comment '机构编码',
   invoice_record_no    varchar(32) comment '开票记录编号',
   invoice_amount       bigint(11) comment '开票金额',
   status               int(1) comment '状态 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消',
   handling_way         int(1) comment '处理方式 1-线下、2-托管',
   park_id              bigint(11) comment '园区id',
   invoice_desc         varchar(128) comment '描述',
   invoice_total_price  bigint(11) comment '合计金额',
   invoice_total_tax    bigint(11) comment '合计税额',
   invoice_total_price_tax bigint(11) comment '价税合计',
   complete_time        datetime comment '完成时间',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_invoice_record comment '开票记录表';

#开票记录变更表
drop table if exists t_e_invoice_record_change;
create table t_e_invoice_record_change
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   oem_code             varchar(12) comment '机构编码',
   invoice_record_no    varchar(32) comment '开票记录编号',
   invoice_amount       bigint(11) comment '开票金额',
   status               int(1) comment '状态 0-待提交、1-人工出票、2-待补票、3-出票中断、4-出票失败、5-推送失败、6-待确认、7-已完成 8-已取消',
   handling_way         int(1) comment '处理方式 1-线下、2-托管',
   park_id              bigint(11) comment '园区id',
   invoice_desc         varchar(128) comment '描述',
   invoice_total_price  bigint(11) comment '合计金额',
   invoice_total_tax    bigint(11) comment '合计税额',
   invoice_total_price_tax bigint(11) comment '价税合计',
   complete_time        datetime comment '完成时间',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_invoice_record_change comment '开票记录变更表';

#开票明细表
drop table if exists t_e_invoice_record_detail;
create table t_e_invoice_record_detail
(
   id                   bigint(11) not null auto_increment comment '主键id',
   invoice_record_no    varchar(32) comment '开票记录编号',
   invoice_trade_no     varchar(32) comment '开票流水号',
   invoice_code         varchar(32) comment '发票代码',
   invoice_no           varchar(32) comment '发票号码',
   invoice_check_code   varchar(32) comment '校验码',
   invoice_date         datetime comment '开票日期',
   invoice_qr_code      varchar(5000) comment '二维码',
   tax_control_code     varchar(256) comment '税控码',
   invoice_type_code    varchar(32) comment '发票类型代码 004：增值税专用发票；007：增值税普通发票 ；026：增值税电子发票；028:增值税电子专用发票；',
   eInvoice_url         varchar(256) comment '电子发票地址',
   invoice_total_price  bigint(11) comment '合计金额',
   invoice_total_tax    bigint(11) comment '合计税额',
   invoice_total_price_tax bigint(11) comment '价税合计',
   invoice_details_list varchar(1000) comment '发票明细集合',
   detail_desc          varchar(64) comment '描述',
   status               int(1) comment '状态 0-正常，1-已打印，2-已作废，3-出票失败',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_invoice_record_detail comment '开票明细表';

#会员账号表
ALTER TABLE `t_e_member_account`
ADD COLUMN `email`  varchar(64) NULL COMMENT '最近开票电子邮箱' AFTER `password`;

#会员收件地址表
drop table if exists t_e_member_address;
create table t_e_member_address
(
   id                   bigint(11) not null auto_increment comment '主键id',
   member_id            char(10) comment '会员id',
   oem_code             char(10) comment '机构编码',
   recipient            varchar(32) comment '收件人',
   recipient_phone      varchar(16) comment '联系电话',
   recipient_address    varchar(128) comment '详细地址',
   status               int(1) comment '状态  1-可用 0-不可用',
   province_code        varchar(16) comment '省编码',
   province_name        varchar(32) comment '省名称',
   city_code            varchar(16) comment '市编码',
   city_name            varchar(32) comment '市名称',
   district_code        varchar(16) comment '区编码',
   district_name        varchar(32) comment '区名称',
   is_default           int(1) comment '是否默认 0-不默认 1-默认',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_member_address comment '会员收件地址表';

#开票订单增加收票邮箱
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `email`  varchar(64) NULL COMMENT '收票邮箱' AFTER `tax_seasonal`;

#企业税务托管表
ALTER TABLE `t_e_company_tax_hosting`
ADD COLUMN `face_amount_type`  int(1) NULL COMMENT '票面金额类型 1-1w 2-10w 3-100w' AFTER `tax_disc_code`,
ADD COLUMN `face_amount`  bigint(11) NULL COMMENT '票面金额(分)' AFTER `face_amount_type`;


#开票记录表
ALTER TABLE `t_e_invoice_record`
ADD COLUMN `trade_no`  varchar(32) NULL COMMENT '渠道流水号' AFTER `invoice_record_no`;

#开票记录变更表
ALTER TABLE `t_e_invoice_record_change`
ADD COLUMN `trade_no`  varchar(32) NULL COMMENT '渠道流水号' AFTER `invoice_record_no`;

#开票记录明细
ALTER TABLE `t_e_invoice_record_detail`
ADD COLUMN `eInvoice_oss_pdf_url`  varchar(256) NULL COMMENT 'oss电子发票pdf地址' AFTER `eInvoice_url`,
ADD COLUMN `eInvoice_oss_img_url`  varchar(256) NULL COMMENT 'oss电子发票图片地址' AFTER `eInvoice_oss_pdf_url`;

#开票记录表
ALTER TABLE `t_e_invoice_record`
ADD COLUMN `company_id`  bigint(11) NULL COMMENT '企业id' AFTER `order_no`;

##开票记录变更表
ALTER TABLE `t_e_invoice_record_change`
ADD COLUMN `company_id`  bigint(11) NULL COMMENT '企业id' AFTER `order_no`;
##修改集团开票备注
ALTER TABLE `t_e_invoice_order_group`
MODIFY COLUMN `category_group_id`  bigint(11) NULL DEFAULT NULL COMMENT '开票基础类目id' AFTER `district_name`,
MODIFY COLUMN `category_group_name`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开票基础类目名称' AFTER `category_group_id`;

ALTER TABLE `t_e_invoice_order_group`
ADD COLUMN `email`  varchar(64) NULL COMMENT '收票邮箱' AFTER `remark`;

#开票订单表
ALTER TABLE `t_e_invoice_order`
ADD INDEX `idx_orderNo` (`order_no`) ;
#oem机构开票类目关系表
ALTER TABLE `t_r_oem_invoice_category`
ADD COLUMN `category_name`  varchar(32) NULL COMMENT '类目名称' AFTER `category_base_id`;

#开票记录明细
ALTER TABLE `t_e_invoice_record_detail`
ADD INDEX `idx_recordNo` (`invoice_record_no`) ;

#开票记录表
ALTER TABLE `t_e_invoice_record`
ADD INDEX `idx_recordNo` (`invoice_record_no`) ,
ADD INDEX `idx_orderNo_recordNo` (`order_no`, `invoice_record_no`) ;


#oem机构开票信息新增收票邮箱字段
ALTER TABLE `t_e_invoice_info_by_oem`
ADD COLUMN `email`  varchar(64) NULL COMMENT '收票邮箱' AFTER `remark`;

#去掉开票订单表校验
ALTER TABLE `t_e_invoice_order`
MODIFY COLUMN `recipient`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头收件人' AFTER `bank_number`,
MODIFY COLUMN `recipient_phone`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头联系电话' AFTER `recipient`,
MODIFY COLUMN `recipient_address`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头详细地址' AFTER `recipient_phone`,
MODIFY COLUMN `province_code`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头省编码' AFTER `recipient_address`,
MODIFY COLUMN `city_code`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头市编码' AFTER `province_name`,
MODIFY COLUMN `district_code`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头区编码' AFTER `city_name`;

#去掉开票订单变更表校验
ALTER TABLE `t_e_invoice_order_change_record`
MODIFY COLUMN `recipient`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头收件人' AFTER `bank_number`,
MODIFY COLUMN `recipient_phone`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头联系电话' AFTER `recipient`,
MODIFY COLUMN `recipient_address`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头详细地址' AFTER `recipient_phone`,
MODIFY COLUMN `province_code`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头省编码' AFTER `recipient_address`,
MODIFY COLUMN `city_code`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头市编码' AFTER `province_name`,
MODIFY COLUMN `district_code`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '抬头区编码' AFTER `city_name`;
#集团发票抬头新增
ALTER TABLE `t_e_invoice_head_group`
ADD COLUMN `email`  varchar(64) NULL COMMENT '收票邮箱' AFTER `remark`;
#####设置默认值
ALTER TABLE `t_park_tax_bill`
MODIFY COLUMN `vouchers_status`  int(11) NULL DEFAULT 0 COMMENT '凭证状态 0-未上传 1-解析中 2-已上传' AFTER `already_upload_iit_vouchers_company_number`;

##增加默认值
ALTER TABLE `t_park_tax_bill`
MODIFY COLUMN `should_upload_vat_vouchers_company_number`  int(11) NULL DEFAULT 0 COMMENT '应上传增值税凭证企业' AFTER `supplement_tax_money`,
MODIFY COLUMN `should_upload_iit_vouchers_company_number`  int(11) NULL DEFAULT 0 COMMENT '应上传个税凭证企业' AFTER `should_upload_vat_vouchers_company_number`,
MODIFY COLUMN `already_upload_vat_vouchers_company_number`  int(11) NULL DEFAULT 0 COMMENT '已上传增值税凭证企业' AFTER `should_upload_iit_vouchers_company_number`,
MODIFY COLUMN `already_upload_iit_vouchers_company_number`  int(11) NULL DEFAULT 0 COMMENT '已上传个税凭证企业' AFTER `already_upload_vat_vouchers_company_number`;

#开票记录明细表
ALTER TABLE `t_e_invoice_record_detail`
ADD COLUMN `request_no`  varchar(32) NULL COMMENT '请求号' AFTER `invoice_record_no`;

#开票明细
ALTER TABLE `t_e_invoice_record_detail`
MODIFY COLUMN `status`  int(1) NULL DEFAULT NULL COMMENT '状态 0-正常，1-已打印，2-已作废，3-出票失败 4-出票中' AFTER `detail_desc`;

#企业对公户账户表
ALTER TABLE `t_e_company_corporate_account`
ADD COLUMN `corporate_account_bank_name`  varchar(64) NULL COMMENT '对公户银行名称' AFTER `corporate_account`;

#字典表
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('emergency_contact', 'liuhongwei@99366.cn,Lijiaying@99366.cn,jiangni@99366.cn', NULL, '邮箱紧急通知人', now(), 'admin', NULL, NULL, NULL);
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('bw_invoice_issue', '3', NULL, '百旺开票接口调用重复次数', now(), 'admin', NULL, NULL, NULL);

#字典表
INSERT INTO `sys_e_dictionary` (`dict_code`,`dict_value`,`parent_dict_id`,`dict_desc`,`add_time`,`add_user`,`update_time`,`update_user`,`remark`)
VALUES('bw_invoice_difference','0',NULL,'百旺票面金额差额,税率为0%时使用(分)',now(),NULL,now(),NULL,NULL);

#百旺电子发票配置
INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`,
`update_time`, `update_user`, `remark`)
VALUES ('YCS', '23', 'M00000', 'd9729feb74992cc3482b350163a1a010', 'https://tst.api.mch.duobeikeji.cn/', '{"keyNum": "bb9aa8f2499c329b88f37567dd9aab31"}',
'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl+7xBgL3AXK5QDHqIAydZjEAswjWFkzMxT9UzOCSPtZiyawv8osrpqVLnIPopCt7W9xRIvRCEwTxzuPJHW4IJDPERAI2MRtHUhf3Kb7dAHvTj7xxJVNqlKUyfD5UdmN82TJBgeECEAJwCDBPWohcX5CtkixXkFfrfHHPDbnm04Tdd0+UxwNSlzub0bNp5UHpqE3XaHh9r5D9T22TQbeWy6IKLbXIIK0NCew/I+3rcb5bUJ5BSfeGmZGosRl6Z7KWYW+Qf243n2+6aW+oKa1n0bKc7kqf3mirfv0aH9creyH2F0cHGsi1tJa3+BSNMJj6HxvlNw814lQQuokxX7vYOQIDAQAB', 'MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDhQt7VrfulElLrUp0o+Wjt/N9B31575KILfybIh4ysptfyEuEkPAsq4gOUi08FvWoihNMlXVLMm1RIKeDP+oB8AWwqoaTp/ewRhnzcKEybHNajnEXgZU1BDQifSMMCXLUMDxZH6+9ITXW3BpCA4Ya6pcg8nzCGBW4IErAiBbQmU/NNtvgkBoFjLeSPdouULHM0dNBfH2S1EmYrBbhlVjYyuUaqCQqwOQHf58/X63vz6UGgAs46PqAW2Y2bunLBz/Qm4NE/lMjA/lEN2XXAQFYqvekv9mO2Z5qSRu+fGqiXk6I0+wr7vdoEUPOs/kcY718Gse70NJSlULuwGAJ+eK05AgMBAAECggEBANQ7HpuP6lGiLAGOuoeKRPRElUwI2Yo85PeT+sRgAYgBQd/PLdPnxlXGz7y6a99qkH6pSg4gGQyxI/1Bh5Ar4bXz2SdpWGklVa9FWIpMZbCWwRsw9xzuFJU/ZGo+MY0eL22HIWTtw2oJoOiqBKeI1FkyLLABA8ShhQOSJ+RGH28jUEiZZgiCqhi/JEtwTeChpjsXurDsl59kQKMglic2YywUVqw7q8U/YCY73EeR7XfcEBydL/ArTEsS0ArVmDrAFHtFNPiy898UXH25ymbtar3VGBfGbsbwUeTxunQHRkcGT0KhLpmPlQtANXuDirj3ht0gHm8JlR0LqFooZHtPrwkCgYEA++sGQWZjCwdrFqXkN1GJEP+D3cCLv7Y5eDkBQcEaLxg9DA5SLEg9vbcWjn1sKFQuulVIAaPBIYGjbD7SgvVR/4jObEI2U6hcKJSXA4EWr5cMnRZlQRvVCrl0y8X8Z7EmXoiXk7OYvELuRabdnVgeMKeDBVnRMVN1teYveTVd+lMCgYEA5OlFdkdkJm8XxL0ujc2bPL2E/IA22rg7oSOKanx68BU0jKnzSNbEQmc05gcDJi0Z+myMKZgPgcpTXQirMXIGTf3hGwqvsS4gG4vIZTEsvrfYtamDtUSDYk9mBc6dARmWE5m/B6BhOQIERZydkKat+U7xgOcuQqFD4t0I9JoxAMMCgYEAiCudQQK4LMEAyMg5GRHQtkh3ngkzQRid7fdaT5Gytpwye27OSjNudDsgTTtMmGwQqhXpC0HZ5mOt05gNsE4s2aS4d9o+hW/vCNxw2KsReS572fm+F7iUquxeTruhWOdv5w+MZ4Ff4PuI0kuFZUS5ZPFXKDBJnJERgpZ/ONR5DRMCgYEA04KDLz3Z2PVvRdbzzAy9sor+9PxXMxNl7YdUXv4z7d/6FTr3U0c2QpDwPNcf3sdvqIdhnxEkyu0yx9S5sc9E6g16UK04F4OiITGwcWfVi0d+UqSV0QjESTNJ9nWpdpm4pNW7wAU9sBof7l4+7KhGOgTqewuhH+m4O0e2/aP3QVcCgYEA3Ro4qS0Io0rw/dM12qo84XrKWVhW33W5Pm+V/BTcx6D/f7T9fsr/1HHr4QJTeX4TPHGMWgDBC2MEtt5GRkdAf3sUVPUQXLRaIIhd9ZYZgX6uF0dhTzydlAgq2Bf3jz6M+0Yh07WWGVEJ0hfeMpdgJihetOKS/AnTP3vMblWuCHI=',
 '1', now(), 'admin', now(), 'admin', '百旺电子发票配置-新渠道');
INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`,
 `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ( 'YCS', '24', 'jiangni@99366.cn', 'Jn7890', 'smtp.exmail.qq.com', '{\"port\": \"465\"}',
NULL, NULL, '1', now(), 'admin', now(), 'admin', '电子邮箱发票配置');

#对公户提现金额，时间限制
INSERT INTO sys_e_dictionary ( dict_code, dict_value, parent_dict_id, dict_desc, add_time, add_user, update_time, update_user, remark) VALUES ( 'corporate_withdraw_amt_min_limit', '100000', NULL, '对公户提现账户预留最小金额限制（分）', '2020-12-29 10:34:32', 'admin', NULL, NULL, NULL);
INSERT INTO sys_e_dictionary (dict_code, dict_value, parent_dict_id, dict_desc, add_time, add_user, update_time, update_user, remark) VALUES ( 'corporate_withdraw_repeat_time', '24', NULL, '对公户提现金额不允许重复时间', '2020-12-29 10:34:32', 'admin', NULL, NULL, NULL);

#机构开票类目表
INSERT INTO `t_r_oem_invoice_category` (`oem_invoice_info_id`, `oem_code`, `category_base_id`, `category_name`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select id,oem_code,'93' category_base_id,'现代服务*服务费' `category_name`,now() add_time,'admin' add_user, `update_time`, `update_user`, `remark` from t_e_invoice_info_by_oem;

#短信模板表
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('YCS', '33', '尊敬的用户，您开票订单号为#orderNo#的电子发票已发送至您的邮箱，请注意查收', '1', now(), 'admin', NULL, NULL, NULL);
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('YSC', '33', '尊敬的用户，您开票订单号为#orderNo#的电子发票已发送至您的邮箱，请注意查收', '1', now(), 'admin', NULL, NULL, NULL);
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('DMSH', '33', '尊敬的用户，您开票订单号为#orderNo#的电子发票已发送至您的邮箱，请注意查收', '1', now(), 'admin', NULL, NULL, NULL);
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('ZQHL', '33', '尊敬的用户，您开票订单号为#orderNo#的电子发票已发送至您的邮箱，请注意查收', '1', now(), 'admin', NULL, NULL, NULL);
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('YZGT', '33', '尊敬的用户，您开票订单号为#orderNo#的电子发票已发送至您的邮箱，请注意查收', '1', now(), 'admin', NULL, NULL, NULL);
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('AYGT', '33', '尊敬的用户，您开票订单号为#orderNo#的电子发票已发送至您的邮箱，请注意查收', '1', now(), 'admin', NULL, NULL, NULL);
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('NBGT', '33', '尊敬的用户，您开票订单号为#orderNo#的电子发票已发送至您的邮箱，请注意查收', '1', now(), 'admin', NULL, NULL, NULL);
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('YCBW', '33', '尊敬的用户，您开票订单号为#orderNo#的电子发票已发送至您的邮箱，请注意查收', '1', now(), 'admin', NULL, NULL, NULL);
INSERT INTO `t_e_sms_template` (`oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('YCHX', '33', '尊敬的用户，您开票订单号为#orderNo#的电子发票已发送至您的邮箱，请注意查收', '1', now(), 'admin', NULL, NULL, NULL);

#修改行业类目表 基础类目id
update t_e_invoice_category c,t_e_invoice_category_base  b
set c.category_base_id = b.id
where substring_index(c.category_name, '*', 1) = b.tax_classification_abbreviation
  and substring_index(category_name, '*', -1) = goods_name;

#修改集团开票类目表 基础类目id
update t_e_invoice_category_group c,t_e_invoice_category_base  b
set c.category_base_id = b.id
where substring_index(c.category_name, '*', 1) = b.tax_classification_abbreviation
  and substring_index(category_name, '*', -1) = goods_name;

#修改企业开票类目表 基础类目id
update t_e_company_invoice_category c,t_e_invoice_category_base  b
set c.category_base_id = b.id
where substring_index(c.category_name, '*', 1) = b.tax_classification_abbreviation
  and substring_index(category_name, '*', -1) = goods_name;

#删除开票类目字段
ALTER TABLE `t_e_invoice_info_by_oem`
DROP COLUMN `category_name`;


#开票订单 出票中 状态 自动生成开票记录
INSERT INTO `t_e_invoice_record` (`order_no`, `company_id`, `oem_code`, `invoice_record_no`, `trade_no`, `invoice_amount`, `status`, `handling_way`,
`park_id`, `invoice_desc`, `invoice_total_price`, `invoice_total_tax`, `invoice_total_price_tax`, `complete_time`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
SELECT
	o.order_no,
	io.company_id,
	o.oem_code,
	UUID() invoice_record_no,
	null TRADE_NO,
	io.invoice_amount,
	1 STATUS,
	1 handling_way,
	o.park_id,
	'脚本生成开票记录' invoice_desc,
	(io.invoice_amount - (ceil(io.invoice_amount/(1+io.VAT_fee_rate) * io.VAT_fee_rate))) invoice_total_price,
	ceil(io.invoice_amount/(1+io.VAT_fee_rate) * io.VAT_fee_rate) invoice_total_tax,
	io.invoice_amount invoice_total_price_tax,
	'' complete_time,
	NOW() add_time,
	'admin' add_user,
	null update_time,
	null update_user,
	'历史数据处理' remark
FROM
	t_e_order o
INNER JOIN t_e_invoice_order io ON io.order_no = o.order_no
WHERE
	o.order_status = 3
AND o.order_type = 6;

#用户收货地址表
INSERT INTO `t_e_member_address` (`member_id`, `oem_code`, `recipient`, `recipient_phone`, `recipient_address`, `status`, `province_code`, `province_name`,
`city_code`, `city_name`, `district_code`, `district_name`, `is_default`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
SELECT
        ih.`member_id`,
        ma.`oem_code`,
        ih.`recipient`,
        ih.`recipient_phone`,
        ih.`recipient_address`,
        ih.`status`,
        ih.`province_code`,
        ih.`province_name`,
        ih.`city_code`,
        ih.`city_name`,
        ih.`district_code`,
        ih.`district_name`,
        0 is_default,
        ih.`add_time`,
        ih.`add_user`,
        now() `update_time`,
        null `update_user`,
        '历史数据处理'`remark`
FROM
        t_e_invoice_head ih
INNER JOIN
        t_e_member_account ma
ON
        ma.`id` = ih.`member_id`
;

#发票抬头表
ALTER TABLE `t_e_invoice_head`
DROP COLUMN `recipient`,
DROP COLUMN `recipient_phone`,
DROP COLUMN `recipient_address`,
DROP COLUMN `province_code`,
DROP COLUMN `province_name`,
DROP COLUMN `city_code`,
DROP COLUMN `city_name`,
DROP COLUMN `district_code`,
DROP COLUMN `district_name`;

#添加索引
ALTER TABLE `t_e_company_invoice_category`
ADD INDEX (`category_base_id`) ;
ALTER TABLE `t_r_oem_invoice_category`
ADD INDEX (`category_base_id`) ;
ALTER TABLE `t_e_invoice_category`
ADD INDEX (`category_base_id`) ;