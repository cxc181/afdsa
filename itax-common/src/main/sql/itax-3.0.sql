#渠道信息表
drop table if exists t_e_channel_info;
create table t_e_channel_info
(
   id                   bigint not null auto_increment comment '主键id',
   channel_code         varchar(32) comment '渠道编码',
   channel_name         varchar(32) comment '渠道名称',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_channel_info comment '渠道信息表';

#会员表
ALTER TABLE `t_e_member_account`
ADD COLUMN `channel_service_id`  integer(11) NULL COMMENT '渠道服务商id' AFTER `sign`,
ADD COLUMN `channel_employees_id`  integer(11) NULL COMMENT '渠道员工id' AFTER `channel_service_id`,
ADD COLUMN `channel_code`  varchar(32) NULL COMMENT '渠道编码' AFTER `channel_employees_id`,
ADD COLUMN `is_demotion`  int(1) NULL DEFAULT 0 COMMENT '是否手动降级 0-未降级 1-已手动降级' AFTER `channel_code`;
ALTER TABLE `t_e_member_account`
ADD COLUMN `channel_product_code`  varchar(32) NULL COMMENT '渠道产品编码' AFTER `is_demotion`;


#会员变动表
ALTER TABLE `t_e_member_account_change`
ADD COLUMN `channel_service_id`  integer(11) NULL COMMENT '渠道服务商id' AFTER `file_url`,
ADD COLUMN `channel_employees_id`  integer(11) NULL COMMENT '渠道员工id' AFTER `channel_service_id`,
ADD COLUMN `channel_code`  varchar(32) NULL COMMENT '渠道编码' AFTER `channel_employees_id`,
ADD COLUMN `is_demotion`  int(1) NULL DEFAULT 0 COMMENT '是否手动降级 0-未降级 1-已手动降级' AFTER `channel_code`;
ALTER TABLE `t_e_member_account_change`
ADD COLUMN `channel_product_code`  varchar(32) NULL COMMENT '渠道产品编码' AFTER `is_demotion`;

#开票表
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `channel_code`  varchar(32) NULL COMMENT '渠道编码' AFTER `risk_commitment`,
ADD COLUMN `channel_service_id`  bigint(11) NULL COMMENT '服务商id' AFTER `channel_code`;

#开票变更表
ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `channel_code`  varchar(32) NULL COMMENT '渠道编码' AFTER `risk_commitment`,
ADD COLUMN `channel_service_id`  bigint(11) NULL COMMENT '服务商id' AFTER `channel_code`;

#订单主表
ALTER TABLE `t_e_order`
ADD COLUMN `channel_service_id`  bigint(11) NULL COMMENT '服务商id' AFTER `source_type`,
ADD COLUMN `channel_employees_id`  bigint(11) NULL COMMENT '员工id' AFTER `channel_service_id`,
ADD COLUMN `channel_code`  varchar(32) NULL COMMENT '渠道编码' AFTER `channel_employees_id`,
ADD COLUMN `channel_push_state`  int NULL COMMENT '推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送' AFTER `channel_code`;
ALTER TABLE `t_e_order`
ADD COLUMN `channel_product_code`  varchar(32) NULL COMMENT '渠道产品编码' AFTER `channel_push_state`;

#oem机构配置表
INSERT INTO `t_e_oem_config` (`oem_code`, `params_code`, `params_value`, `parent_params_code`, `params_desc`, `add_time`, `add_user`,`remark`)
select oem_code,'is_open_channel',1,null,'是否接入渠道 1-接入 0-不接入',now(),'admin','初始化数据' from t_e_oem;

#接入国金配置
INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`,`remark`)
VALUES ('YCS', '26', 'GUOJIN', '1a23fec0b19b4dcd8d1426601713fa5f', 'https://helperdev.99366.cn/helper-gateway/', null, null, null, '1', now(), 'admin', '国金助手接入配置');
INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`,`remark`)
VALUES ('YSC', '26', 'GUOJIN', '1a23fec0b19b4dcd8d1426601713fa5f', 'https://helperdev.99366.cn/helper-gateway/', null, null, null, '1', now(), 'admin', '国金助手接入配置');
INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`,`remark`)
VALUES ('NBGT', '26', 'GUOJIN', '1a23fec0b19b4dcd8d1426601713fa5f', 'https://helperdev.99366.cn/helper-gateway/', null, null, null, '1', now(), 'admin', '国金助手接入配置');

ALTER TABLE `t_e_member_account`
ADD INDEX `idx_parentmember` (`oem_code`, `parent_member_id`) ;

ALTER TABLE `t_e_member_account`
ADD INDEX `idx_channel` (`oem_code`, `channel_service_id`, `channel_employees_id`) ;

ALTER TABLE `t_e_member_company`
ADD INDEX `idx_memberId` (`member_id`, `oem_code`) ;

ALTER TABLE `t_e_profits_detail`
ADD INDEX `idx_user` (`user_id`, `user_type`, `oem_code`) ;

#初始化国金助手数据
INSERT INTO `t_e_channel_info` (`id`, `channel_code`, `channel_name`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('1', 'GUOJIN', '国金助手', '2021-05-19 10:36:24', 'admin', NULL, NULL, '初始化数据');
INSERT INTO `t_e_channel_info` (`id`, `channel_code`, `channel_name`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('2', 'BAIWANG', '百旺助手', '2021-05-19 10:36:24', 'admin', NULL, NULL, '初始化数据');
INSERT INTO `t_e_channel_info` (`id`, `channel_code`, `channel_name`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('3', 'HANGXIN', '航信助手', '2021-05-19 10:36:24', 'admin', NULL, NULL, '初始化数据');

##############城市合伙人和员工注销
#注销员工账号(会员订单关系表)
update t_r_member_order mo, t_e_member_account m
  set account_first = CONCAT(account_first,'_1'),mo.update_time = now(),mo.remark='迁移至国金助手，手动注销'
 where mo.level_first = -1 and mo.oem_code = m.oem_code and m.member_type =2 and m.STATUS != 2 and mo.account_first = m.member_account;
update t_r_member_order mo, t_e_member_account m
  set account_two = CONCAT(account_two,'_1'),mo.update_time = now(),mo.remark='迁移至国金助手，手动注销'
 where mo.level_two = -1 and mo.oem_code = m.oem_code and m.member_type =2 and m.STATUS != 2 and mo.account_two = m.member_account;
update t_r_member_order mo, t_e_member_account m
  set account_three = CONCAT(account_three,'_1'),mo.update_time = now(),mo.remark='迁移至国金助手，手动注销'
 where mo.level_three = -1 and mo.oem_code = m.oem_code and m.member_type =2 and m.STATUS != 2 and mo.account_three = m.member_account;
update t_r_member_order mo, t_e_member_account m
  set account_four = CONCAT(account_four,'_1'),mo.update_time = now(),mo.remark='迁移至国金助手，手动注销'
 where mo.level_four = -1 and mo.oem_code = m.oem_code and m.member_type =2 and m.STATUS != 2 and mo.account_four = m.member_account;

#注销城市合伙人(会员订单关系表)
update t_r_member_order mo ,sys_e_user u
set city_providers = CONCAT(city_providers,'_1'),mo.update_time = now(),mo.remark='迁移至国金助手，手动注销'
where  mo.city_providers = u.username and mo.oem_code = u.oem_code and u.platform_type > 3 and u.account_type = 1 and u.`status`!=2;
update t_r_member_order mo ,sys_e_user u
set city_partner = CONCAT(city_partner,'_1'),mo.update_time = now(),mo.remark='迁移至国金助手，手动注销'
where  mo.city_partner = u.username and mo.oem_code = u.oem_code and u.platform_type > 3 and u.account_type = 1 and u.`status`!=2;

#订单分润表
update t_e_profits_detail pd set user_account = CONCAT(user_account,'_1'),update_time = now(),remark='迁移至国金助手，手动注销' where  user_type in (2,3) and user_account in (select username from sys_e_user where oem_code = pd.oem_code and platform_type > 3 and account_type = 1 and `status`!=2);
update t_e_profits_detail pd set attribution_employees_account = CONCAT(attribution_employees_account,'_1'),update_time = now(),remark='迁移至国金助手，手动注销' where  attribution_employees_account in (select member_account from t_e_member_account where oem_code = pd.oem_code and member_type =2 and STATUS != 2);
#注销城市合伙人账号
update sys_e_user set `status` = 2 ,username = CONCAT(username,'_1'),update_time = now(),remark='迁移至国金助手，手动注销' where platform_type > 3 and account_type = 1 and `status`!=2 ;

#注销所属员工和上上员工
update t_e_member_account m , t_e_member_account m1
set m.attribution_employees_account = CONCAT(m.attribution_employees_account,'_1'),m.update_time = now(),m.remark='迁移至国金助手，手动注销'
where m.attribution_employees_account = m1.member_account
  and m.oem_code = m1.oem_code
  and m1.member_type =2 and m1.STATUS != 2;
update t_e_member_account m , t_e_member_account m1
set m.super_employees_account = CONCAT(m.super_employees_account,'_1'),m.update_time = now(),m.remark='迁移至国金助手，手动注销'
where  m.super_employees_account = m1.member_account
 and m.oem_code = m1.oem_code
 and m1.member_type =2 and m1.STATUS != 2;
#注销员工
update t_e_member_account m set member_account = CONCAT(member_account,'_1'),update_time = now(),remark='迁移至国金助手，手动注销' ,`status`= 2,is_demotion = 1 where member_type = 2 and `status`!= 2;

#城市服务商和税务顾问都降级为VIP
update t_e_member_account mo set member_level = (select id from t_e_member_level where oem_code = mo.oem_code and level_no = 1),level_name = 'VIP',
	is_demotion = 1,update_time=now(),remark='迁移至国金助手，手动降级'
 where member_level in (select id from t_e_member_level where oem_code = mo.oem_code and level_no>=3) and `status`!=2;

#修改会员税务顾问和城市服务商的佣金钱包数据
INSERT INTO `t_e_user_capital_change_record` (`capital_account_id`, `user_id`, `user_type`, `oem_code`, `changes_amount`, `changes_before_amount`, `changes_after_amount`,
 `changes_type`, `outstanding_amount`, `detail_desc`, `order_type`, `order_no`, `wallet_type`, `add_time`, `add_user`, `remark`)
select id as `capital_account_id`, user_id as `user_id`, user_type as `user_type`, `oem_code`,total_amount as `changes_amount`, total_amount as `changes_before_amount`, 0 as `changes_after_amount`,
 2 `changes_type`, `outstanding_amount`,'迁移至国金助手' `detail_desc`, 4 `order_type`, null `order_no`, `wallet_type`, now() `add_time`, 'admin' `add_user`,'迁移至国金助手' `remark`
 from t_e_user_capital_account where user_type = 1 and user_id in (select id from t_e_member_account where is_demotion = 1) and wallet_type = 2;
INSERT INTO `t_e_user_capital_change_record` (`capital_account_id`, `user_id`, `user_type`, `oem_code`, `changes_amount`, `changes_before_amount`, `changes_after_amount`,
 `changes_type`, `outstanding_amount`, `detail_desc`, `order_type`, `order_no`, `wallet_type`, `add_time`, `add_user`, `remark`)
select id as `capital_account_id`, user_id as `user_id`, user_type as `user_type`, `oem_code`,total_amount as `changes_amount`, total_amount as `changes_before_amount`, 0 as `changes_after_amount`,
 2 `changes_type`, `outstanding_amount`,'迁移至国金助手' `detail_desc`, 4 `order_type`, null `order_no`, `wallet_type`, now() `add_time`, 'admin' `add_user`,'迁移至国金助手' `remark`
 from t_e_user_capital_account where user_type != 1 and user_id in (select id from sys_e_user where platform_type > 3 and account_type = 1) and wallet_type = 1;

update t_e_user_capital_account set total_amount = 0,available_amount = 0 ,block_amount = 0,update_time = now(),update_user='admin',remark = '迁移至国金助手，手动降级' where user_type = 1 and user_id in (select id from t_e_member_account where is_demotion = 1) and wallet_type = 2;
update t_e_user_capital_account set total_amount = 0,available_amount = 0 ,block_amount = 0,update_time = now(),update_user='admin',remark = '迁移至国金助手，手动降级' where user_type != 1 and user_id in (select id from sys_e_user where platform_type > 3 and account_type = 1) and wallet_type = 1;

#取消未付款订单
update t_e_order set order_status = 6,update_time = now(),update_user='admin',remark = '迁移至国金助手，手动降级' where order_type = 5 and order_status in (0,1,3) and oem_code in ('YCS','YCBW','YCHX');
update t_e_order set order_status = 8,update_time = now(),update_user='admin',remark = '迁移至国金助手，手动降级' where order_type = 6 and order_status in (0,1) and oem_code in ('YCS','YCBW','YCHX');
update t_e_order set order_status = 4,update_time = now(),update_user='admin',remark = '迁移至国金助手，手动降级' where order_type = 7 and order_status in (0) and oem_code in ('YCS','YCBW','YCHX');
update t_e_order set order_status = 4,update_time = now(),update_user='admin',remark = '迁移至国金助手，手动降级' where order_type = 8 and order_status in (0) and oem_code in ('YCS','YCBW','YCHX');
update t_e_order set order_status = 2,update_time = now(),update_user='admin',remark = '迁移至国金助手，手动降级' where order_type = 15 and order_status in (0) and oem_code in ('YCS','YCBW','YCHX');

#会员等级表
update t_e_member_level set oem_code = CONCAT(oem_code,'_1'),update_time = now(),update_user='admin',remark='迁移至国金助手，手动降级' where  level_no > 1;
update t_e_member_profits_rules set oem_code = CONCAT(oem_code,'_1'),update_time = now(),update_user='admin',remark='迁移至国金助手，手动降级' where  user_level > 1;

#订单的推送状态
update t_e_order set channel_push_state = 4;
update t_e_order o set channel_push_state = 0 where ((order_type = 5 and order_status in (2,4,8,9,10,11,12))
or (order_type = 6 and order_status in (2,3,4,5,6,9))
or (order_type = 7 and order_status in (1,2))
or (order_type = 8 and order_status in (1))
or (order_type = 15 and order_status in (3)))
and '1'=(select params_value from t_e_oem_config where oem_code = o.oem_code and params_code = 'is_open_channel');

#修改在途订单的分润等级和分润费率
update t_r_member_order mo1 ,(select rela_id,oem_code from t_e_order where ((order_type in(1,2,3,4,12,13,14) and order_status not in (2,3,4,5,7))
or (order_type in(5) and order_status not in (5,6,7))
or (order_type in(6) and order_status not in (7,8,10))
or (order_type in(7) and order_status not in (3,4))
or (order_type in(8) and order_status not in (2,3))
or (order_type in(9) and order_status not in (4,5))
or (order_type in(10) and order_status not in (2,3))
or (order_type in(11) and order_status not in (2,3))
or (order_type in(15) and order_status not in (2,3)))) a
set mo1.level_two_profits_rate = 0,mo1.level_three_profits_rate = 0,mo1.level_four_profits_rate = 0,city_providers_profits_rate=0,city_partner_profits_rate=0,mo1.update_time = now()
,mo1.remark='迁移至国金助手，手动降级'
where a.rela_id = mo1.id and mo1.oem_code = a.oem_code;

#修改在途订单的分润等级和分润费率
update t_r_member_order mo1 ,(select rela_id,oem_code from t_e_order where ((order_type in(1,2,3,4,12,13,14) and order_status not in (2,3,4,5,7))
or (order_type in(5) and order_status not in (5,6,7))
or (order_type in(6) and order_status not in (7,8,10))
or (order_type in(7) and order_status not in (3,4))
or (order_type in(8) and order_status not in (2,3))
or (order_type in(9) and order_status not in (4,5))
or (order_type in(10) and order_status not in (2,3))
or (order_type in(11) and order_status not in (2,3))
or (order_type in(15) and order_status not in (2,3)))) a
set mo1.level_two_profits_rate = 0,mo1.level_three_profits_rate = 0,mo1.level_four_profits_rate = 0,city_providers_profits_rate=0,city_partner_profits_rate=0,mo1.update_time = now()
,mo1.remark='迁移至国金助手，手动降级', mo1.level_first_profits_rate = 0,mo1.level_first = null
where a.rela_id = mo1.id and mo1.oem_code = a.oem_code and (mo1.level_first = -1 or mo1.member_level >= 1) ;

#修改在途订单的分润等级和分润费率
update t_r_member_order mo1 ,(select rela_id,oem_code from t_e_order where (order_type in(5) and order_status not in (5,6,7))
or (order_type in(15) and order_status not in (2,3))) a
set mo1.level_two_profits_rate = 0,mo1.level_three_profits_rate = 0,mo1.level_four_profits_rate = 0,city_providers_profits_rate=0,city_partner_profits_rate=0,mo1.update_time = now()
,mo1.remark='迁移至国金助手，手动降级', mo1.level_first = 1,mo1.level_first_profits_rate = 25
where a.rela_id = mo1.id and mo1.oem_code = a.oem_code and (mo1.level_first > 1 and mo1.member_level = 0) and mo1.level_first_profits_rate>0;

#修改在途订单的分润等级和分润费率
update t_r_member_order mo1 ,(select rela_id,oem_code from t_e_order where (order_type in(1,2,3,4,12,13,14) and order_status not in (2,3,4,5,7))
or (order_type in(6) and order_status not in (7,8,10))
or (order_type in(7) and order_status not in (3,4))
or (order_type in(8) and order_status not in (2,3))
or (order_type in(9) and order_status not in (4,5))
or (order_type in(10) and order_status not in (2,3))
or (order_type in(11) and order_status not in (2,3))) a
set mo1.level_two_profits_rate = 0,mo1.level_three_profits_rate = 0,mo1.level_four_profits_rate = 0,city_providers_profits_rate=0,city_partner_profits_rate=0,mo1.update_time = now()
,mo1.remark='迁移至国金助手，手动降级', mo1.level_first = 1,mo1.level_first_profits_rate = 10
where a.rela_id = mo1.id and mo1.oem_code = a.oem_code and (mo1.level_first > 1 and mo1.member_level = 0) and mo1.level_first_profits_rate>0;

#修改上级为员工的会员对应的parentid
update t_e_member_account m,(select * from t_e_member_account where member_type = 2 and is_demotion = 1 and remark like '%迁移至国金助手%') b
set m.parent_member_id = null ,m.parent_member_account = null,m.update_time = now(),m.remark='上级用户迁移至国金助手'
where  m.parent_member_id = b.id and m.oem_code = b.oem_code and m.oem_code in ('YCS','YCBW','YCHX');
#修改税务顾问和城市服务商的parentid
update t_e_member_account set parent_member_id = null,parent_member_account = null where is_demotion = 1 and remark like '%迁移至国金助手%' and oem_code in ('YCS','YCBW','YCHX');

#修改parentid
update t_r_member_order mo, t_e_member_account m
  set mo.parent_member_id = null,mo.parent_member_account = null,mo.update_time = now(),mo.remark='迁移至国金助手，手动注销'
 where mo.oem_code = m.oem_code and mo.member_id = m.id  and m.oem_code in ('YCS','YCBW','YCHX')
   and m.parent_member_id is null;

#修改member_tree ，许执行多次
update t_e_member_account m ,t_e_member_account m1
set m.member_tree = substring(m.member_tree,POSITION(CONCAT('/',m1.id,'/') in CONCAT(m.member_tree,'/'))+1),m.update_time = now(),m.remark='迁移至国金助手，手动降级'
where CONCAT(m.member_tree,'/') like CONCAT('%/',m1.id,'/%')
and m.channel_service_id = m1.channel_service_id and m.oem_code in ('YCS','YCBW','YCHX')
and  m1.parent_member_id is null and m1.is_demotion = 1 and m1.channel_service_id is not null;

update t_r_member_order set account_first = null,account_first_id = null,phone_first = null,name_first = null,update_time=now(),remark='历史数据分润用户信息修改0531'
  where level_first is null and account_first_id is not null










