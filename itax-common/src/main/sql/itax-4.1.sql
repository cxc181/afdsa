#行业基础信息表
drop table if exists t_e_industry_base;
create table t_e_industry_base
(
   id                   bigint(11) not null auto_increment comment '主键id',
   industry_name        varchar(64) comment '行业名称',
   vat_rate             varchar(64) comment '增值税率数组，为百分比数据',
   status               int(1) comment '状态 1-正常 2-下架',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_industry_base comment '行业基础信息表';

#园区奖励政策
drop table if exists t_e_park_reward_policy;
create table t_e_park_reward_policy
(
   id                   bigint(11) not null auto_increment comment '主键id',
   park_id              bigint(11) comment '园区id',
   min_value            bigint(11) comment '税费最小值（分）',
   max_value            bigint(11) comment '税费最大值（分）',
   reward_rate          decimal(4,2) comment '奖励比例，百分比',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_park_reward_policy comment '园区奖励政策';

#企业所得税税率表
drop table if exists t_e_company_income_rule;
CREATE TABLE `t_e_company_income_rule` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `level` bigint(11) NOT NULL COMMENT '级数',
  `min_amount` bigint(11) NOT NULL COMMENT '全年应纳税所得额最小值（分）',
  `max_amount` bigint(11) NOT NULL COMMENT '全年应纳税所得额最大值（分）',
  `rate` decimal(6,4) NOT NULL COMMENT '税率(小数)',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
  PRIMARY KEY (`id`)
) COMMENT='企业所得税税率表';

INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('销售货物', '13', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:17', NULL, NULL);
INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('提供劳务', '13', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:17', '', '');
INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('进口货物', '13', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:17', '', '');
INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('建筑服务', '9', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:17', '', '');
INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('销售不动产', '9', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:14', NULL, NULL);
INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('交通运输服务', '9', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:14', NULL, NULL);
INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('邮政服务', '9', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:14', NULL, NULL);
INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('电信服务', '9', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:14', NULL, NULL);
INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('现代服务', '6', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:14', NULL, NULL);
INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( '生活服务', '6', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:14', NULL, NULL);
INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( '销售无形资产', '6', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:14', NULL, NULL);
INSERT INTO `t_e_industry_base` ( `industry_name`, `vat_rate`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( '金融服务', '6', '1', '2022-09-28 23:09:14', 'admin', '2022-09-28 23:09:14', NULL, NULL);

INSERT INTO `t_e_company_income_rule` ( `level`, `min_amount`, `max_amount`, `rate`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( '1', '0', '100000000', '0.0250', '2022-09-28 23:13:20', 'admin', '2022-09-28 23:13:20', NULL, NULL);
INSERT INTO `t_e_company_income_rule` ( `level`, `min_amount`, `max_amount`, `rate`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( '2', '100000000', '300000000', '0.0500', '2022-09-28 23:13:20', 'admin', '2022-09-28 23:13:20', NULL, NULL);
INSERT INTO `t_e_company_income_rule` ( `level`, `min_amount`, `max_amount`, `rate`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( '3', '300000000', '9223372036854775807', '0.2500', '2022-09-28 23:13:20', 'admin', '2022-09-28 23:13:20', NULL, NULL);


INSERT INTO `t_e_park_reward_policy` (`park_id`, `min_value`, `max_value`, `reward_rate`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select id, '5000000', '50000000', '15', now(), 'admin', now(), NULL, NULL from t_e_park;
INSERT INTO `t_e_park_reward_policy` (`park_id`, `min_value`, `max_value`, `reward_rate`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select id, '50000000', '100000000', '16', now(), 'admin', now(), NULL, NULL from t_e_park;
INSERT INTO `t_e_park_reward_policy` (`park_id`, `min_value`, `max_value`, `reward_rate`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select id, '100000000', '300000000', '17', now(), 'admin', now(), NULL, NULL from t_e_park;
INSERT INTO `t_e_park_reward_policy` (`park_id`, `min_value`, `max_value`, `reward_rate`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select id, '300000000', '600000000', '18', now(), 'admin', now(), NULL, NULL from t_e_park;
INSERT INTO `t_e_park_reward_policy` (`park_id`, `min_value`, `max_value`, `reward_rate`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select id, '600000000', '1500000000', '19', now(), 'admin', now(), NULL, NULL from t_e_park;
INSERT INTO `t_e_park_reward_policy` (`park_id`, `min_value`, `max_value`, `reward_rate`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select id, '1500000000', '9223372036854775807', '20', now(), 'admin', now(), NULL, NULL from t_e_park;
select id, '1500000000', '9223372036854775807', '0.2', now(), 'admin', now(), NULL, NULL from t_e_park;

#字典表
INSERT INTO `sys_e_dictionary` (`id`, `dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES (null, 'measuring_tool_share_url', 'https://itaxdev.yuncaiol.cn/itax-h5/#/pages/calculate/index?oemCode=', NULL, '测算工具分享url', now(), 'admin', NULL, NULL, NULL);
