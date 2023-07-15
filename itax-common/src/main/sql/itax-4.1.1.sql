#园区奖励政策表
drop table if exists t_e_park_reward_policy_label;
create table t_e_park_reward_policy_label
(
   id                   bigint(11) not null auto_increment comment '主键id',
   park_id              bigint(11) comment '园区id',
   reward_label         varchar(64) comment '奖励标签',
   reward_desc          varchar(512) comment '奖励说明',
   reward_label_base_id bigint(11) comment '基础奖励政策id',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_park_reward_policy_label comment '园区奖励政策标签表';

#基础奖励政策表
drop table if exists t_e_reward_policy_label_base;
create table t_e_reward_policy_label_base
(
   id                   bigint(11) not null auto_increment comment '主键id',
   reward_label         varchar(64) comment '奖励标签',
   reward_desc          varchar(512) comment '奖励说明',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_reward_policy_label_base comment '基础奖励政策标签表';

#园区评价表
drop table if exists t_e_park_comments;
create table t_e_park_comments
(
   id                   bigint(11) not null auto_increment comment '主键id',
   park_id              bigint(11) comment '园区id',
   member_id            bigint(11) comment '会员id',
   user_ratings         decimal(4,2) comment '用户评分',
   comments_content     varchar(1024) comment '评论内容',
   oem_code             varchar(12) comment '评论机构编码',
   status               int(1) comment '评论状态  1-可见 2-屏蔽',
   reply_content        varchar(256) comment '回复内容',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_park_comments comment '园区评价表';

#订单主表
ALTER TABLE `t_e_order`
MODIFY COLUMN `order_status`  int(2) NOT NULL COMMENT '订单状态：工商注册(0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，10-待设立登记、11-待提交签名 12-签名待确认 13-待创建),\r\n开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过 11-待财务审核),\r\n会员升级( 0-待支付,1-支付中,2-财务审核,3-已完成,4-已取消), \r\n充值提现、对公户提现、补税，退税(0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败),工商注销(0-待付款,1-注销处理中,2-注销成功,3-已取消 4-税单待处理),证件申请( 0-待付款 1-待发货,2-出库中,3-待签收,4-已签收,5-已取消),公户申请（0-待付款,1-等待预约,2-已完成,3-已取消）消费开票（0-待出票 1-出票中 2-已完成 3-出票失败 4-待发货 5-待签收）托管费续费、对公户续费（0-待支付、1-支付中、2-已完成、3-已取消）' AFTER `order_type`;

#园区表
ALTER TABLE `t_e_park`
ADD COLUMN `user_ratings`  decimal(4,2) NULL COMMENT '用户评分' AFTER `affiliating_area`,
ADD COLUMN `register_desc`  varchar(512) NULL COMMENT '工商注册说明' AFTER `user_ratings`,
ADD COLUMN `tax_handle_desc`  varchar(512) NULL COMMENT '税务办理说明' AFTER `register_desc`,
ADD COLUMN `corporate_account_handle_desc`  varchar(512) NULL COMMENT '对公户办理说明' AFTER `tax_handle_desc`,
ADD COLUMN `park_imgs`  varchar(512) NULL COMMENT '园区实景图片' AFTER `corporate_account_handle_desc`,
ADD COLUMN `tax_policy_desc`  varchar(512) NULL COMMENT '税收政策说明' AFTER `park_imgs`;

#园区表
ALTER TABLE `t_e_park`
ADD COLUMN `park_thumbnail`  varchar(128) NULL COMMENT '园区缩略图' AFTER `tax_policy_desc`;


#企业成员表
ALTER TABLE `t_e_company_core_personnel`
MODIFY COLUMN `personnel_type`  int(1) NULL DEFAULT NULL COMMENT '成员类型  1-经理 2- 监事 3-执行董事 4-财务 5-无职务' AFTER `company_type`,
ADD COLUMN `partner_type`  int(1) NULL COMMENT '合伙人类型 1-普通 2-有限合伙' AFTER `personnel_type`,
ADD COLUMN `is_legal_person`  int(1) NULL COMMENT '是否法人 0-否 1-是' AFTER `partner_type`,
ADD COLUMN `is_executive_partner`  int(1) NULL COMMENT '是否执行事务合伙人 0-否 1-是' AFTER `is_legal_person`,
ADD COLUMN `appoint_party_id`  bigint(11) NULL COMMENT '委派方id' AFTER `is_executive_partner`,
MODIFY COLUMN `is_shareholder` INT(11) NULL COMMENT '是否股东/合伙人 0-否 1-是' AFTER `appoint_party_id`;

#修改园区返税政策表
RENAME TABLE `t_e_park_reward_policy` TO `t_e_park_tax_refund_policy`;
ALTER TABLE `t_e_park_tax_refund_policy` COMMENT='园区返税政策';

#字典表
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('articles_of_incorporation_yyzr', 'https://oss-itax-public.inabei.cn/template/articles_of_incorporation_yyzr.html', NULL, '有限责任公司章程',now(), 'admin', now(), '', '');
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ('articles_of_incorporation_yyhh', 'https://oss-itax-public.inabei.cn/template/articles_of_incorporation_yyhh.html', NULL, '有限合伙企业章程',now(), 'admin', now(), NULL, NULL);

#注册订单申请名称历史数据处理
update t_e_register_order reg,t_e_park  p,t_e_order o,t_e_industry i
set reg.shop_name = case when reg.shop_name is not null and reg.shop_name !='' then CONCAT(p.park_city,reg.shop_name,replace(reg.example_name,'***','')) else null end ,
reg.shop_name_one = case when reg.shop_name_one is not null and reg.shop_name_one !='' then CONCAT(p.park_city,reg.shop_name_one,replace(reg.example_name,'***','')) else null end,
reg.shop_name_two = case when reg.shop_name_two is not null and reg.shop_name_two !='' then CONCAT(p.park_city,reg.shop_name_two,replace(reg.example_name,'***','')) else null end
where reg.order_no = o.order_no and p.id = o.park_id and reg.industry_id = i.id;

#注册订单变更表申请名称历史数据处理
update t_e_register_order_change_record reg,t_e_park  p,t_e_order o,t_e_industry i
set reg.shop_name = case when reg.shop_name is not null and reg.shop_name !='' then CONCAT(p.park_city,reg.shop_name,replace(reg.example_name,'***','')) else null end ,
reg.shop_name_one = case when reg.shop_name_one is not null and reg.shop_name_one !='' then CONCAT(p.park_city,reg.shop_name_one,replace(reg.example_name,'***','')) else null end,
reg.shop_name_two = case when reg.shop_name_two is not null and reg.shop_name_two !='' then CONCAT(p.park_city,reg.shop_name_two,replace(reg.example_name,'***','')) else null end
where reg.order_no = o.order_no and p.id = o.park_id and reg.industry_id = i.id;

#修改企业核心成员的数据
update t_e_company_core_personnel set is_legal_person = 1 where personnel_type = 1 and is_legal_person is null;

#个体户历史数据企业成员处理
INSERT INTO `t_e_company_core_personnel` (`order_no`, `member_id`, `company_id`, `company_type`, `personnel_type`, `partner_type`, `is_legal_person`,
 `is_executive_partner`, `appoint_party_id`, `is_shareholder`, `identity_type`, `personnel_name`, `contact_phone`, `certificate_no`, `certificate_addr`,
`expire_date`, `id_card_front`, `id_card_reverse`, `business_license`, `investment_amount`, `share_proportion`,
 `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select c.`order_no`, `member_id`,c.id as `company_id`, c.`company_type`,null `personnel_type`,null `partner_type`,1 `is_legal_person`,
 null `is_executive_partner`,null `appoint_party_id`, 0 `is_shareholder`, 1 `identity_type`, c.operator_name `personnel_name`, c.operator_tel `contact_phone`,
c.id_card_number `certificate_no`, o.id_card_addr `certificate_addr`, o.expire_date `expire_date`, c.id_card_front `id_card_front`, c.id_card_reverse `id_card_reverse`, null `business_license`,
null `investment_amount`, null `share_proportion`, now() `add_time`, 'admin' `add_user`, now() `update_time`, 'admin' `update_user`, '历史数据处理' `remark`
 from t_e_member_company c,t_e_register_order o where c.order_no = o.order_no and c.company_type = 1;

INSERT INTO `t_e_company_core_personnel` (`order_no`, `member_id`, `company_id`, `company_type`, `personnel_type`, `partner_type`, `is_legal_person`,
 `is_executive_partner`, `appoint_party_id`, `is_shareholder`, `identity_type`, `personnel_name`, `contact_phone`, `certificate_no`, `certificate_addr`,
`expire_date`, `id_card_front`, `id_card_reverse`, `business_license`, `investment_amount`, `share_proportion`,
 `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select o.`order_no`, o1.user_id `member_id`,null `company_id`, o.`company_type`,null `personnel_type`,null `partner_type`,1 `is_legal_person`,
 null `is_executive_partner`,null `appoint_party_id`, 0 `is_shareholder`, 1 `identity_type`, o.operator_name `personnel_name`, o.contact_phone `contact_phone`,
o.id_card_number `certificate_no`, o.id_card_addr `certificate_addr`, o.expire_date `expire_date`, o.id_card_front `id_card_front`, o.id_card_reverse `id_card_reverse`, null `business_license`,
null `investment_amount`, null `share_proportion`, now() `add_time`, 'admin' `add_user`, now() `update_time`, 'admin' `update_user`, '历史数据处理' `remark`
 from t_e_register_order o,t_e_order o1 where o.order_no = o1.order_no and company_type = 1 and o.order_no not in (select order_no from t_e_member_company where order_no is not null);

update t_e_company_core_personnel p,t_e_member_company c
set p.company_id = c.id
where p.order_no = c.order_no and p.company_id is null;

# 注册订单表
ALTER TABLE `t_e_register_order`
 CHANGE `contact_phone` `contact_phone` VARCHAR(16) CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '联系电话',
  CHANGE `id_card_front` `id_card_front` VARCHAR(256) CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '身份证正面',
  CHANGE `id_card_reverse` `id_card_reverse` VARCHAR(256) CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '身份证反面',
  CHANGE `id_card_number` `id_card_number` VARCHAR(20) CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '身份证号码',
  CHANGE `operator_name` `operator_name` VARCHAR(32) CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '经营者姓名';
#注册订单变更表
ALTER TABLE `t_e_register_order_change_record`
  CHANGE `contact_phone` `contact_phone` VARCHAR(16) CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '联系电话',
  CHANGE `id_card_front` `id_card_front` VARCHAR(256) CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '身份证正面',
  CHANGE `id_card_reverse` `id_card_reverse` VARCHAR(256) CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '身份证反面',
  CHANGE `id_card_number` `id_card_number` VARCHAR(20) CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '身份证号码',
  CHANGE `operator_name` `operator_name` VARCHAR(32) CHARSET utf8 COLLATE utf8_general_ci NULL   COMMENT '经营者姓名';

# 园区订单量统计视图(不区分机构)
CREATE OR REPLACE VIEW park_order_quantity_statistics_view AS
SELECT p.`id` park_id, IFNULL(COUNT(o.`id`),0) order_number
FROM t_e_order o
RIGHT JOIN t_e_park p ON p.`id` = o.`park_id`
GROUP BY p.`id`;

#企业成员表
ALTER TABLE `t_e_company_core_personnel`
MODIFY COLUMN `personnel_type`  varchar(32) NULL DEFAULT NULL COMMENT '成员类型  1-经理 2- 监事 3-执行董事 4-财务 5-无职务,多个类型之间用逗号分割' AFTER `company_type`;

#园区评论
ALTER TABLE `t_e_park_comments`
MODIFY COLUMN `comments_content`  varchar(1024) CHARACTER SET utf8mb4 NULL DEFAULT NULL COMMENT '评论内容' AFTER `user_ratings`;

#企业成员表
update t_e_company_core_personnel set personnel_type = 5 where company_type in (1,2,3) and personnel_type = 1;

#企业成员表数据处理
update t_e_company_core_personnel p ,t_e_register_order r
set p.company_type = r.company_type
where p.order_no = r.order_no and p.company_type is null;

#注册订单表
update t_e_business_scope b ,t_e_register_order r
set r.industry_business_scope = b.business_content
where b.industry_id = r.industry_id
and r.industry_business_scope is null;

#园区表
ALTER TABLE `t_e_park`
MODIFY COLUMN `park_imgs`  varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '园区实景图片' AFTER `corporate_account_handle_desc`;

