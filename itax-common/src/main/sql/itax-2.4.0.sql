#======V2.4.0版本数据脚本

#行业数据表增加其他说明
ALTER TABLE `t_e_industry`
ADD COLUMN `order_desc`  varchar(100) NULL COMMENT '其他说明' AFTER `example_name`;

#订单主表添加操作小程序来源
ALTER TABLE `t_e_order`
ADD COLUMN `source_type` int(1) NULL COMMENT '操作小程序来源 1-微信小程序 2-支付宝小程序' AFTER `remark`;

#会员表添加操作小程序来源
ALTER TABLE `t_e_member_account`
ADD COLUMN `source_type` int(1) NULL COMMENT '操作小程序来源 1-微信小程序 2-支付宝小程序' AFTER `is_pay_upgrade`;

#税费规则配置增加行业id
ALTER TABLE `t_e_tax_rules_config`
ADD COLUMN `industry_id`  bigint(11) NULL DEFAULT NULL COMMENT '行业id' AFTER `policy_id`;

#园区表添加核定说明
ALTER TABLE `t_e_park`
ADD COLUMN `verify_desc` varchar(256) NULL COMMENT '核定说明' AFTER `park_address`;

#会员账号表添加支付宝用户ID
ALTER TABLE `t_e_member_account`
ADD COLUMN `alipay_user_id` varchar(16) NULL COMMENT '支付宝userId，支付宝支付时需要' AFTER `real_name`;

###############快照表结构初始化####
#会员快照表
drop table if exists t_e_member_snapshot;
create table t_e_member_snapshot
(
   id                   bigint(11) not null auto_increment comment '主键id',
   member_id            bigint(11) comment '会员id',
   member_account       varchar(32) comment '会员账号',
   oem_code             varchar(12) comment '机构编码',
   parent_member_id     bigint(11) comment '上级会员id',
   up_diamond_id        bigint(11) comment '上级钻石id',
   attribution_employees_id bigint(11) comment '所属员工id',
   super_diamond_id     bigint(11) comment '上上级钻石id',
   super_employees_id   bigint(11) comment '上上级员工id',
   level_no             int(1) comment '当前等级  0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5城市服务商',
   status               int(1) comment '会员状  1-正常 0-禁用 2-注销',
   regist_time          datetime comment '注册时间',
   snapshot_time       datetime comment '快照时间',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_member_snapshot comment '会员快照表';


#员工快照
drop table if exists t_e_employees_snapshot;
create table t_e_employees_snapshot
(
   id                   bigint(11) not null auto_increment comment '主键id',
   member_id            bigint(11) comment '会员id',
   member_account       varchar(32) comment '会员账号',
   oem_code             varchar(12) comment '机构编码',
   parent_member_id     bigint(11) comment '上级会员id',
   add_revenue          bigint(11) comment '新增收益(分)',
   status               int(1) comment '会员状  1-正常 0-禁用 2-注销',
   regist_time          datetime comment '注册时间',
   snapshot_time       datetime comment '快照时间',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_employees_snapshot comment '员工快照';

#用户资金快照
drop table if exists t_e_user_capital_snapshot;
create table t_e_user_capital_snapshot
(
   id                   bigint(11) not null auto_increment comment '主键id',
   user_id            bigint(11) comment '用户id',
   user_type            int(1) comment '用户类型 1-会员 2 -系统用户',
   oem_code             varchar(12) comment '机构编码',
   total_amount_consumption bigint(11) comment '消费钱包总余额(分)',
   available_amount_consumption bigint(11) comment '消费钱包可用余额(分)',
   block_amount_consumption bigint(11) comment '消费钱包冻结金额(分)',
   outstanding_amount_consumption bigint(11) comment '消费钱包待结算金额(分)',
   total_amount_commission bigint(11) comment '佣金钱包总余额(分)',
   available_amount_commission bigint(11) comment '佣金钱包可用余额(分)',
   block_amount_commission bigint(11) comment '佣金钱包冻结金额(分)',
   add_revenue          bigint(11) comment '新增收益(分)',
   snapshot_time       datetime comment '快照时间',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_user_capital_snapshot comment '用户资金快照';



#系统用户快照
drop table if exists sys_e_user_snapshot;
create table sys_e_user_snapshot
(
   id                   bigint(11) not null auto_increment comment '主键id',
   user_id              bigint(11) comment '用户id',
   oem_code             varchar(12) comment '机构编码',
   user_type            int(1) comment '用户类型 1-平台 2-机构 3-园区 4-高级城市合伙人 5-城市合伙人',
   username             varchar(32) comment '注册账号',
   binding_account      varchar(32) comment '绑定手机号',
   status               int(1) comment '状态 0-禁用 1-可用 2-注销',
   regist_time          datetime comment '注册时间',
   snapshot_time       datetime comment '快照时间',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table sys_e_user_snapshot comment '系统用户快照';

#订单快照
drop table if exists t_e_order_snapshot;
create table t_e_order_snapshot
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   user_id              bigint(11) comment '用户id',
   park_id              bigint(11) comment '园区id',
   oem_code             varchar(12) comment '机构编码',
   company_id           bigint(11) comment '企业id',
   user_type            int(1) comment '用户类型 1-会员 2-城市合伙人 3-高级城市合伙人 4-平台 5-管理员 6-其他',
   order_type           int(1) comment '订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票',
   product_id           bigint(11) comment '产品id (会员升级对应等级id)',
   upgrade_level_no     int(1) comment '会员升级等级   0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5城市服务商',
   pay_amount           bigint(11) comment '支付金额(分)',
   service_fee          bigint(11) comment '服务费(分)',
   profit_amount        bigint(11) comment '可分润金额(分)',
   oem_profits          bigint(11) comment '机构利润(分)',
   snapshot_time       datetime comment '快照时间(订单完成时间)',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_order_snapshot comment '订单快照(已完成)';

#开票订单快照
drop table if exists t_e_invoice_order_snapshot;
create table t_e_invoice_order_snapshot
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   user_id              bigint(11) comment '用户id',
   park_id              bigint(11) comment '园区id',
   oem_code             varchar(12) comment '机构编码',
   company_id           bigint(11) comment '企业id',
   user_type            int(1) comment '用户类型 1-会员 2-城市合伙人 3-高级城市合伙人 4-平台 5-管理员 6-其他',
   invoice_amount       bigint(11) comment '开票金额(分)',
   invoice_type         int(1) comment '发票类型 1-增值税普通发票 2-增值税专用发票',
   invoice_way          int(1) comment '发票方式 1-纸质发票 2-电子发票',
   total_tax_fee        bigint(11) comment '总税费(分)',
   vat_fee              bigint(11) comment '增值税(分)',
   vat_payment          bigint(11) comment '增值税补缴(分)',
   income_tax_payment   bigint(11) comment '所得税补缴(分)',
   surcharge            bigint(11) comment '附加税(分)',
   surcharge_payment    bigint(11) comment '附加税补缴(分)',
   refund_tax_fee       bigint(11) comment '已退税费(分)',
   postage_fees         bigint(11) comment '快递费(分)',
   service_fee          bigint(11) comment '服务费(分)',
   personal_income_tax  bigint(11) comment '所得税(分)',
   profit_amount        bigint(11) comment '可分润金额(分)',
   oem_profits          bigint(11) comment '机构利润(分)',
   snapshot_time       datetime comment '快照时间(订单完成时间)',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_invoice_order_snapshot comment '开票订单快照(已完成)';

#企业表
ALTER TABLE `t_e_member_company`
ADD INDEX (`oem_code`) ;

ALTER TABLE `t_e_register_order_change_record`
ADD INDEX (`oem_code`, `add_time`) ;

ALTER TABLE `t_e_order_snapshot`
ADD INDEX (`oem_code`, `snapshot_time`) ;

ALTER TABLE `t_e_invoice_order_snapshot`
ADD INDEX (`oem_code`, `snapshot_time`) ;

#消息通知表增加副标题
ALTER TABLE `t_e_message_notice`
ADD COLUMN `notice_subtitle` varchar(60) NULL COMMENT '副标题' AFTER `notice_title`;


ALTER TABLE `t_e_user_rela`
ADD INDEX (`user_id`, `user_class`) ;

#资金表
ALTER TABLE `t_e_user_capital_account`
ADD INDEX `idx_userId_userType` (`oem_code`, `user_id`, `user_type`) ;

#资金变动记录表
ALTER TABLE `t_e_user_capital_change_record`
ADD INDEX `idx_userId_userType` (`user_id`, `user_type`) ,
ADD INDEX `idx_addTime` (`add_time`);

#资金快照表
ALTER TABLE `t_e_user_capital_snapshot`
ADD INDEX `idx_userId_userType` (`user_id`, `user_type`) ,
ADD INDEX `idx_snapshotTime` (`snapshot_time`) ;

#系统通知表
ALTER TABLE `t_e_notice_manage`
ADD COLUMN `out_time`  datetime NULL DEFAULT NULL COMMENT '下线时间' AFTER `release_way`;

#通知管理表
ALTER TABLE `t_e_notice_manage`
MODIFY COLUMN `notice_type`  int(1) NULL DEFAULT NULL COMMENT '通知类型  1-短信通知 2-站内通知 3-悬浮公告' AFTER `oem_code`;

#支付宝邀请码相关配置
INSERT INTO `itax`.`sys_e_oem_params`(`id`, `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES (104, 'YCS', 22, '2021001199602334', '', 'https://openapi.alipay.com/gateway.do', NULL, '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', 1, '2020-10-23 15:37:30', 'admin', NULL, NULL, '支付宝小程序二维码配置');

#支付宝支付通道相关配置
INSERT INTO `itax`.`sys_e_oem_params`(`id`, `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES (102, 'YCS', 20, 'M00000', 'd9729feb74992cc3482b350163a1a010', 'https://tst.api.mch.duobeikeji.cn/gateway/payment/v1/alipayApplet/createOrder', '{\"productCode\":\"PRD00095\",\"appId\": \"2021001199602334\",\"keyNum\": \"bb9aa8f2499c329b88f37567dd9aab31\",\"signKey\": \"0a474834-ab3a-4ada-a33d-3c48b1f28c86\",\"pubKey\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsO2nz7Ipy/CnlOWoBWk72SYYJFwcExiop13xEo1JDe/sRfoaDv8OTFkU+3gryGkz0bYYKFwls76PCGdNMAUsT6ZAfpMP1EuPVJElGFsRUR5r38KfuUI8I8hBOpccpvvmQe5cRwVSdsiMrbxeoC7FTWeyYQyFrFBRwK177E4hpyOAucGDjmBlxElNxQkX2tVXlRXcuwcWbL68SVV5wZjxLp345oy6bLvFs6rtntY+L0cAmkLk5cqr3Tk2f96bXa5/cJ/D1BferXmGS2fCLeCfszs13NFp7Sau8VYdzMpib08P03wtr7mqbYpwG/Qbt3Rhn+zDjlCwkb9gFuRNXIDvfwIDAQAB\"}', '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', 1, '2019-12-18 14:53:09', 'admin', '2020-01-13 11:15:07', 'admin', '勿动！！支付宝支付通道相关配置');
INSERT INTO `itax`.`sys_e_oem_params`(`id`, `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES (107, 'YCS', 21, 'M00000', 'd9729feb74992cc3482b350163a1a010', 'https://tst.api.mch.duobeikeji.cn/gateway/payment/v1/query/queryOrder', '{\"productCode\":\"PRD00097\",\"appId\": \"2021001199602334\",\"keyNum\": \"bb9aa8f2499c329b88f37567dd9aab31\",\"signKey\": \"d9729feb74992cc3482b350163a1a010\",\"pubKey\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsO2nz7Ipy/CnlOWoBWk72SYYJFwcExiop13xEo1JDe/sRfoaDv8OTFkU+3gryGkz0bYYKFwls76PCGdNMAUsT6ZAfpMP1EuPVJElGFsRUR5r38KfuUI8I8hBOpccpvvmQe5cRwVSdsiMrbxeoC7FTWeyYQyFrFBRwK177E4hpyOAucGDjmBlxElNxQkX2tVXlRXcuwcWbL68SVV5wZjxLp345oy6bLvFs6rtntY+L0cAmkLk5cqr3Tk2f96bXa5/cJ/D1BferXmGS2fCLeCfszs13NFp7Sau8VYdzMpib08P03wtr7mqbYpwG/Qbt3Rhn+zDjlCwkb9gFuRNXIDvfwIDAQAB\"}', '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', 1, '2020-08-18 16:23:47', 'admin', '2020-08-18 16:23:47', 'admin', '勿动！！支付宝支付订单查询通道相关配置-新渠道');

#支付宝最小和最大限额
INSERT INTO `itax`.`sys_e_dictionary`(`id`, `dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES (83, 'ali_recharge_amount_max_limit', '1000000', NULL, '单笔充值最大限额（分）', '2020-08-10 10:44:15', 'admin', NULL, NULL, NULL);
INSERT INTO `itax`.`sys_e_dictionary`(`id`, `dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES (84, 'ali_recharge_amount_min_limit', '1', NULL, '单笔充值最小限额（分）', '2020-08-10 10:44:15', 'admin', NULL, NULL, NULL);