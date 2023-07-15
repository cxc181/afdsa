#开票订单表
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `supplement_explain`  varchar(512) NULL COMMENT '补充说明' AFTER `channel_service_id`;
#开票订单变更表
ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `supplement_explain`  varchar(512) NULL COMMENT '补充说明' AFTER `channel_service_id`;
#园区政策
ALTER TABLE `t_e_tax_policy`
ADD COLUMN `income_levy_type`  int(1) NULL DEFAULT 1 COMMENT '所得税征收类型 1-查账征收 2-核定征收' AFTER `policy_file_url`;
#订单主表
ALTER TABLE `t_e_order`
ADD COLUMN `crowd_label_id`  bigint(11) NULL COMMENT '人群标签id' AFTER `channel_user_id`;
#企业表
ALTER TABLE `t_e_member_company`
ADD COLUMN `tax_reg_date`  date NULL COMMENT '税务登记日期' AFTER `cancel_credentials`;
#企业变更表
ALTER TABLE `t_e_member_company_change`
ADD COLUMN `tax_reg_date`  date NULL COMMENT '税务登记日期' AFTER `cancel_credentials`;
#oem机构表
ALTER TABLE `t_e_oem`
ADD COLUMN `is_big_customer`  int NULL DEFAULT 0 COMMENT '是否大客户 0-否 1-是' AFTER `default_sms_code`;

#人群标签
drop table if exists t_e_crowd_label;
create table t_e_crowd_label
(
   id                   bigint(11) not null auto_increment comment '主键id',
   crowd_label_name     varchar(64) comment '标签名称',
   oem_code             varchar(12) comment '机构编码',
   status               int(1) comment '状态 1-正常 2-作废',
   member_user_num      int(11) comment '用户数',
   crowd_label_desc   varchar(128) comment '标签描述',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_crowd_label comment '人群标签';

#人群标签变更表
drop table if exists t_e_crowd_label_change;
create table t_e_crowd_label_change
(
   id                   bigint(11) not null auto_increment comment '主键id',
   crowd_label_id       bigint(11) comment '标签id',
   crowd_label_name     varchar(64) comment '标签名称',
   oem_code             varchar(12) comment '机构编码',
   status               int(1) comment '状态 1-正常 2-作废',
   member_user_num      int(11) comment '用户数',
   crowd_label_desc     varchar(128) comment '标签描述',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_crowd_label_change comment '人群标签变更表';

#人群标签会员关系表
drop table if exists t_r_member_crowd_label;
create table t_r_member_crowd_label
(
   id                   bigint(11) not null auto_increment comment '主键id',
   crowd_label_id       bigint(11) comment '人群标签id',
   member_id            bigint(11) comment '会员id',
   oem_code             varchar(12) comment '机构编码',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_member_crowd_label comment '人群标签会员关系表';

#产品特价活动表
drop table if exists t_e_product_discount_activity;
create table t_e_product_discount_activity
(
   id                   bigint(11) not null auto_increment comment '主键id',
   activity_name        varchar(64) comment '活动名称',
   product_type         int(1) comment '产品类型 1-个体开户 5-个体开票 11-个体注销  15-公户申请和托管 16-个体托管费续费',
   special_price_amount bigint(11) comment '特价金额(分)',
   status               int(1) default 0 comment '状态 0-待上架 1-已上架 2-已下架 3-已暂停',
   oem_code             varchar(12) comment '机构编码',
   activity_start_date  date comment '活动开始日期',
   activity_end_date    date comment '活动结束日期',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_product_discount_activity comment '产品特价活动表';

#产品特价活动人群关系表
drop table if exists t_r_discount_activity_crowd_label;
create table t_r_discount_activity_crowd_label
(
   id                   bigint(11) not null auto_increment comment '主键id',
   discount_activity_id bigint(11) comment '特价活动id',
   crowd_label_id       bigint(11) comment '人群id',
   oem_code             varchar(12) comment '机构编码',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_discount_activity_crowd_label comment '产品特价活动人群关系表';

#产品特价活动园区关系表
drop table if exists t_r_discount_activity_park;
create table t_r_discount_activity_park
(
   id                   bigint(11) not null auto_increment comment '主键id',
   discount_activity_id bigint(11) comment '特价活动id',
   park_id              bigint(11) comment '园区id',
   oem_code             varchar(12) comment '机构编码',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_discount_activity_park comment '产品特价活动园区关系表';

#产品特价活动行业关系表
drop table if exists t_r_discount_activity_industry;
create table t_r_discount_activity_industry
(
   id                   bigint(11) not null auto_increment comment '主键id',
   discount_activity_id bigint(11) comment '特价活动id',
   industry_id          bigint(11) comment '行业id',
   oem_code             varchar(12) comment '机构编码',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_discount_activity_industry comment '产品特价活动行业关系表';

#特价活动开票服务费标准
drop table if exists t_r_discount_activity_charge_standard;
create table t_r_discount_activity_charge_standard
(
   id                   bigint(11) not null auto_increment comment '主键id',
   charge_type          int(1) comment '费用类型 1-服务费 ',
   charge_min           bigint(11) comment '费用金额最小(分)',
   charge_max           bigint(11) comment '费用金额最大(分)',
   charge_rate          decimal(4,2) comment '收费比率(百分比)',
   oem_code             varchar(12) comment '机构编码',
   discount_activity_id bigint(11) comment '特价活动id',
   order_sn             int(2) comment '排序字段',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_r_discount_activity_charge_standard comment '特价活动开票服务费标准';
#产品特价活动变更表
drop table if exists t_e_product_discount_activity_change;
create table t_e_product_discount_activity_change
(
   id                   bigint(11) not null auto_increment comment '主键id',
   discount_activity_id bigint(11) comment '特价活动id',
   activity_name        varchar(64) comment '活动名称',
   product_type         int(1) comment '产品类型 1-个体开户 5-个体开票 11-个体注销  15-公户申请和托管 16-个体托管费续费',
   special_price_amount bigint(11) comment '特价金额(分)',
   status               int(1) comment '状态 0-待上架 1-已上架 2-已下架 3-已暂停',
   oem_code             varchar(12) comment '机构编码',
   activity_start_date  date comment '活动开始日期',
   activity_end_date    date comment '活动结束日期',
   crowd_label_ids      varchar(1000) comment '人群标签id，多个id直接用逗号分割',
   park_ids             varchar(1000) comment '园区id列表,多个id之间用逗号分割',
   industry_ids         varchar(1000) comment '行业id列表,多个id之间用逗号分割',
   charge_standard_json varchar(1000) comment '服务费阶梯列表，json对象',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_product_discount_activity_change comment '产品特价活动变更表';

ALTER TABLE `t_e_product_discount_activity`
ADD COLUMN `processing_fee`  bigint(11) NULL COMMENT '办理费（对公户独有）' AFTER `activity_end_date`,
ADD COLUMN `cancel_total_limit`  bigint(11) NULL COMMENT '注销累计开票额度' AFTER `processing_fee`;

#订单主表
ALTER TABLE `t_e_order`
ADD COLUMN `discount_activity_id`  bigint(11) NULL COMMENT '产品特价活动id' AFTER `crowd_label_id`;

#产品特价活动视图
CREATE OR REPLACE VIEW PRODUCT_DISCOUNT_ACTIVITY_VIEW AS
select pda.*,acl.crowd_label_id,cl.crowd_label_name,cl.`status` crowd_label_status,dai.industry_id,i.industry_name,i.`status` industry_status,
dap.park_id,p.park_name,p.park_code,p.`status` park_status, mcl.member_id,ma.real_name,ma.member_level,
 ma.member_name,ma.member_account,ma.`status` member_status
 from t_e_product_discount_activity pda
  inner join  t_r_discount_activity_crowd_label acl on pda.id = acl.discount_activity_id and pda.oem_code = acl.oem_code
  inner join t_e_crowd_label cl on cl.id = acl.crowd_label_id and cl.oem_code = acl.oem_code
  inner join  (select id,discount_activity_id,industry_id,oem_code from t_r_discount_activity_industry where discount_activity_id in (select id from t_e_product_discount_activity)
				union
				select i.id,act.discount_activity_id,i.id industry_id,act.oem_code from t_e_industry i,t_r_discount_activity_park act
					where i.park_id = act.park_id and act.discount_activity_id not in (select discount_activity_id from t_r_discount_activity_industry)
		) dai on pda.id = dai.discount_activity_id and pda.oem_code = acl.oem_code
  inner join  t_e_industry i on i.id = dai.industry_id
  inner join  t_r_discount_activity_park dap on pda.id = dap.discount_activity_id and pda.oem_code = acl.oem_code
  inner join t_e_park p on dap.park_id = p.id
  inner join  t_r_member_crowd_label mcl on acl.crowd_label_id = mcl.crowd_label_id and pda.oem_code = acl.oem_code
  inner join  t_e_member_account ma on mcl.member_id = ma.id and mcl.oem_code = ma.oem_code and ma.`status` != 2;


####云财在线近12个月开票记录
CREATE OR REPLACE VIEW COMPANY_INVOICE_RECORD_STATISTICS_VIEW AS
SELECT
	invrecode.member_id user_id, #会员id
	invrecode.company_id, #企业id
	IFNULL(a.use_total_invoice_amount,0) use_total_invoice_amount, #近12个月已开票金额
	IFNULL(a.use_invoice_amount_quarter_pp,0) use_invoice_amount_quarter_pp, #本季度已开普票金额
	IFNULL(a.use_invoice_amount_quarter_zp,0) use_invoice_amount_quarter_zp, #本季度已开专票金额
	invrecode.use_invoice_amount use_invoice_amount_year, #本年已开票金额（企业注册时间往后推一年）
	invrecode.end_time #企业本年有效截至时间
FROM
(SELECT r.*,c.member_id FROM t_e_company_invoice_record r ,t_e_member_company c WHERE r.company_id = c.id and r.end_time >= DATE_FORMAT(NOW(),'%Y-%m-%d') GROUP BY company_id HAVING r.id = min(r.id)) invrecode
 left join
	(
		SELECT o.user_id, i.company_id,
			sum( CASE WHEN o.add_time >= DATE_FORMAT( date_add(NOW(), INTERVAL - 11 MONTH), '%Y-%m-01' ) THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_total_invoice_amount,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 1 THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_invoice_amount_quarter_pp,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 2 THEN i.invoice_amount ELSE 0 END ) use_invoice_amount_quarter_zp
		FROM
			t_e_order o,
			t_e_invoice_order i,
      t_e_member_company c
		WHERE o.order_no = i.order_no AND o.order_status NOT IN (8,10) AND o.user_type = 1 and c.id = i.company_id and (c.ein is null or c.ein = '')  GROUP BY o.user_id, i.company_id
union
select c1.member_id user_id,c1.id company_id,use_total_invoice_amount,use_invoice_amount_quarter_pp,use_invoice_amount_quarter_zp from t_e_member_company c1,(
SELECT o.user_id, i.company_id,c.ein,
			sum( CASE WHEN o.add_time >= DATE_FORMAT( date_add(NOW(), INTERVAL - 11 MONTH), '%Y-%m-01' ) THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_total_invoice_amount,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 1 THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_invoice_amount_quarter_pp,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 2 THEN i.invoice_amount ELSE 0 END ) use_invoice_amount_quarter_zp
		FROM
			t_e_order o,
			t_e_invoice_order i,
      t_e_member_company c
		WHERE o.order_no = i.order_no AND o.order_status NOT IN (8,10) AND o.user_type = 1 and c.id = i.company_id and c.ein is not null and c.ein != ''
     GROUP BY c.ein
) t where c1.ein = t.ein) a
on  a.company_id = invrecode.company_id;


