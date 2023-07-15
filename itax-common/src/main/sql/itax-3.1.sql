##园区政策配置表
ALTER TABLE `t_e_tax_policy`
MODIFY COLUMN `income_tax_breaks_cycle`  int(11) NULL DEFAULT NULL COMMENT '所得税减免周期 1-按月 2-按季度' AFTER `income_tax_breaks_amount`,
ADD COLUMN `surcharge_breaks_amount`  bigint(11) NULL DEFAULT 0 COMMENT '附加税减免额度' AFTER `income_tax_breaks_cycle`,
ADD COLUMN `surcharge_breaks_cycle`  int(1) NULL COMMENT '附加税减免周期  1-按月 2-按季度' AFTER `surcharge_breaks_amount`;

##优惠券兑换码表
CREATE TABLE `t_e_coupon_exchange_code` (
  `id` bigint(32) NOT NULL COMMENT '主键id',
  `exchange_code` varchar(16) NOT NULL COMMENT '兑换码',
  `exchange_name` varchar(64) NOT NULL COMMENT '兑换码名称',
  `limited_number` int(10) NOT NULL DEFAULT '0' COMMENT '限量兑换张数',
  `exchange_number_person` int(10) NOT NULL DEFAULT '0' COMMENT '每人可兑换张数',
  `has_exchange_number` int(10) DEFAULT NULL COMMENT '已兑换张数',
  `status` int(1) NOT NULL COMMENT '状态 0-未生效 1-已生效 2-已过期 3-已作废 4-已暂停',
  `coupons_id` bigint(11) NOT NULL COMMENT '优惠券id',
  `start_date` datetime NOT NULL COMMENT '生效日期',
  `end_date` datetime NOT NULL COMMENT '截至日期',
  `add_time` datetime DEFAULT NULL COMMENT '创建日期',
  `add_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(255) DEFAULT NULL COMMENT '修改人',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='优惠券兑换码表';


#优惠券发放记录
ALTER TABLE `t_e_coupons_issue_record`
ADD COLUMN `issue_type`  int(1) NULL COMMENT '发放方式 0-批量发放 1-兑换码' AFTER `oem_code`;
ALTER TABLE `t_e_coupons_issue_record`
ADD COLUMN `coupons_exchange_id`  bigint(11) NULL COMMENT '兑换码id' AFTER `coupons_id`;


##集团开票订单表新增付款截图字段
ALTER TABLE `t_e_invoice_order_group`
ADD COLUMN `pay_img_url`  varchar(64) NULL COMMENT '付款截图' AFTER `email`;
ALTER TABLE `t_e_invoice_order_group`
ADD COLUMN `audit_desc`  varchar(255) NULL AFTER `pay_img_url`;
ALTER TABLE `t_e_invoice_order_group`
MODIFY COLUMN `order_status`  int(1) NULL DEFAULT NULL COMMENT '订单状态 0-流水解析中 1-出票中 2-已签收 3-已取消 4-待财务审核' AFTER `invoice_amount`;



##新增企业变更表
CREATE TABLE `t_e_member_company_change` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `company_id` bigint(11) NOT NULL  COMMENT '企业id',
  `member_id` bigint(11) NOT NULL COMMENT '会员id',
  `company_name` varchar(64) NOT NULL COMMENT '公司名称',
  `ein` varchar(32) DEFAULT NULL COMMENT '税号',
  `business_scope` varchar(256) DEFAULT NULL COMMENT '经营范围',
  `industry_id` bigint(11) NOT NULL COMMENT '行业类型id',
  `industry` varchar(64) DEFAULT NULL COMMENT '行业',
  `business_address` varchar(128) DEFAULT NULL COMMENT '经营地址',
  `business_license` varchar(128) NOT NULL COMMENT '营业执照',
  `business_license_copy` varchar(128) DEFAULT NULL COMMENT '营业执照副本',
  `end_time` date NOT NULL COMMENT '有效时间',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态 1-正常 2-禁用 4-已注销 5-注销中 ',
  `annual_fee` bigint(11) DEFAULT '0' COMMENT '年费',
  `oem_code` varchar(12) NOT NULL COMMENT '机构编码',
  `park_id` bigint(11) NOT NULL COMMENT '园区id',
  `is_top_up` int(1) NOT NULL DEFAULT '0' COMMENT '是否满额 0-否 1-是',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `add_user` varchar(32) DEFAULT NULL COMMENT '添加人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '修改人',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  `operator_name` varchar(64) NOT NULL COMMENT '经营者名称',
  `company_type` int(1) NOT NULL COMMENT '企业类型1-个体户 2-个人独资企业 3-有限合伙 4-有限责任',
  `operator_tel` varchar(16) DEFAULT NULL COMMENT '经营者手机号',
  `operator_email` varchar(32) DEFAULT NULL COMMENT '经营者邮箱',
  `agent_account` varchar(32) DEFAULT NULL COMMENT '经办人账号',
  `id_card_number` varchar(20) DEFAULT NULL COMMENT '身份证号码',
  `id_card_front` varchar(256) DEFAULT NULL COMMENT '身份证正面',
  `id_card_reverse` varchar(256) DEFAULT NULL COMMENT '身份证反面',
  `is_other` int(1) DEFAULT '0' COMMENT '是否为他人办理 0-本人办理 1-为他人办理',
  `commission_invoice_default` int(1) DEFAULT '0' COMMENT '佣金默认开票主体 0-否 1-是',
  `order_no` varchar(32) DEFAULT NULL COMMENT '订单号',
  `overdue_status` int(1) DEFAULT '1' COMMENT '过期状态 1-正常 2-即将过期 3-已过期',
  `is_send_notice` int(1) DEFAULT '0' COMMENT '是否发送过期提醒通知 0-未发送 1-已发送',
  `cancel_credentials` varchar(512) DEFAULT NULL COMMENT '注销凭证',
  PRIMARY KEY (`id`),
  KEY `oem_code` (`oem_code`),
  KEY `idx_memberId` (`member_id`,`oem_code`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='企业变更表';

##优惠卷管理表状态枚举变更
ALTER TABLE `t_e_coupons`
MODIFY COLUMN `status`  int(1) NULL DEFAULT 0 COMMENT '状态 0-未生效 1-已生效 2-已过期 3-已作废' AFTER `end_date`;

####云财在线近12个月开票记录
CREATE OR REPLACE VIEW COMPANY_INVOICE_RECORD_STATISTICS_VIEW AS  SELECT
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
			sum(CASE WHEN o.add_time >= DATE_FORMAT( date_add(NOW(), INTERVAL - 11 MONTH), '%Y-%m-01' )  THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_total_invoice_amount,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 1 THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_invoice_amount_quarter_pp,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 2 THEN i.invoice_amount ELSE 0 END ) use_invoice_amount_quarter_zp
		FROM
			t_e_order o,
			t_e_invoice_order i,
      t_e_member_company c
		WHERE o.order_no = i.order_no AND o.order_status NOT IN (8,10) AND o.user_type = 1
      and c.id = i.company_id and i.oem_code = c.oem_code and c.ein is null
      GROUP BY o.user_id, i.company_id
union
select c.member_id user_id,c.id company_id,t.use_total_invoice_amount,t.use_invoice_amount_quarter_pp,t.use_invoice_amount_quarter_zp from (
	SELECT c.ein,
			sum(CASE WHEN o.add_time >= DATE_FORMAT( date_add(NOW(), INTERVAL - 11 MONTH), '%Y-%m-01' )  THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_total_invoice_amount,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 1 THEN IFNULL(i.invoice_amount,0) ELSE 0 END ) use_invoice_amount_quarter_pp,
			sum( CASE WHEN QUARTER (o.add_time) = QUARTER (now()) AND i.invoice_type = 2 THEN i.invoice_amount ELSE 0 END ) use_invoice_amount_quarter_zp
		FROM
			t_e_order o,
			t_e_invoice_order i,
      t_e_member_company c
		WHERE o.order_no = i.order_no AND o.order_status NOT IN (8,10) AND o.user_type = 1
      and c.id = i.company_id and i.oem_code = c.oem_code and c.ein is not null
      GROUP BY c.ein
) t,t_e_member_company c
where t.ein = c.ein and c.ein is not null ) a
on  a.company_id = invrecode.company_id;


#预税单视图
CREATE OR REPLACE VIEW COMPANY_CURRENT_QUARTER_TAX_STATISTICS_VIEW AS
select year(now()) 'year',QUARTER(now()) 'quarter',mc.member_id 'member_id',mc.park_id 'park_id',mc.company_type 'company_type',mc.industry_id 'industry_id',
mc.id 'company_id',mc.company_name 'company_name',mc.operator_name 'operator_name',policy.vat_breaks_cycle as 'vat_breaks_cycle',policy.income_tax_breaks_cycle as 'income_tax_breaks_cycle' ,
ifnull(t.vat_zp_amount,0) 'vat_zp_amount',
ifnull(t.vat_pp_amount,0) 'vat_pp_amount',
ifnull(t.more_quarter_invoice_amount,0) 'more_quarter_invoice_amount',
ifnull(t.invoice_amount,0) 'invoice_amount',
ifnull(t.vat_taxable_income_amount,0) 'vat_taxable_income_amount',
ifnull(t.vat_should_tax_money,0) 'vat_should_tax_money',
ifnull(t.vat_rate,0) 'vat_rate',
ifnull(t.vat_already_tax_money,0) 'vat_already_tax_money',
ifnull(t.additional_taxable_income_amount,0) 'additional_taxable_income_amount',
ifnull(t.additional_should_tax_money,0) 'additional_should_tax_money',
ifnull(t.additional_rate,0) 'additional_rate',
ifnull(t.additional_already_tax_money,0) 'additional_already_tax_money',
ifnull(policy.levy_way,0) 'levy_way',
ifnull(t.income_taxable_income_amount,0) 'income_taxable_income_amount',
ifnull(t.income_should_tax_money,0) 'income_should_tax_money',
ifnull(t.taxable_income_rate,0) 'taxable_income_rate',
case when t.levy_way = 1 then ifnull(t.taxable_income_rate,0) else ifnull(t.income_rate,0) end 'income_rate',
ifnull(t.income_already_tax_money,0) 'income_already_tax_money'
 from t_e_member_company mc left join (
select ctax.*,
case when ctax.levy_way = 1 then ctax.income_taxable_income
     when ctax.levy_way = 2 then
     (select ceil(ctax.income_taxable_income  * rate /100) from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
			and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1) end 'income_taxable_income_amount',
case when ctax.levy_way = 1 then 0
     when ctax.levy_way = 2 then
     (select rate*100 from t_e_business_income_rule where  min_amount < (select ceil(ctax.income_taxable_income  * rate / 100) from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
					and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)
					and  max_amount >= (select ceil(ctax.income_taxable_income  * rate / 100) from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
					and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)) end 'income_rate',
case when ctax.levy_way = 1 then
		(select rate from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
			and ctax.income_taxable_income > (min_amount + ctax.income_tax_breaks_amount) and  ctax.income_taxable_income <= if(max_amount>= POWER(2,31),max_amount,(max_amount + ctax.income_tax_breaks_amount))
			and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)
     when ctax.levy_way = 2 then
   (select rate from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
			and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)  end 'taxable_income_rate',
case when ctax.levy_way = 1 then
		(select ceil(ctax.income_taxable_income * rate /100) from t_e_tax_rules_config where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
			and ctax.income_taxable_income > (min_amount + ctax.income_tax_breaks_amount) and  ctax.income_taxable_income <= if(max_amount>= POWER(2,31),max_amount,(max_amount + ctax.income_tax_breaks_amount))
			and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)
     when ctax.levy_way = 2 then
   (select ceil((select ceil(ctax.income_taxable_income * trc.rate /100)* ir.rate - quick - (select ifnull(sum(income_should_tax_money),0) from t_e_company_tax_bill where tax_bill_year=year(now()) and tax_bill_seasonal != QUARTER(now()) and company_id = ctax.company_id)
			from t_e_business_income_rule ir where  ceil(ctax.income_taxable_income * trc.rate /100) > min_amount  and  ceil(ctax.income_taxable_income * trc.rate /100) <= max_amount order by min_amount limit 1))
				from t_e_tax_rules_config trc where company_type =ctax.company_type and tax_type = 1 and  park_id = ctax.park_id
				and (case when industry_id = ctax.industry_id then industry_id = ctax.industry_id when industry_id is null then industry_id is null  end)
			order by industry_id desc,min_amount limit 1)  end 'income_should_tax_money'
from (
select year(now()) 'year',QUARTER(now()) 'quarter',c.member_id 'member_id',c.park_id 'park_id',c.company_type 'company_type',c.industry_id 'industry_id',
c.id 'company_id',c.company_name 'company_name',c.operator_name 'operator_name',tp.vat_breaks_cycle as 'vat_breaks_cycle',tp.income_tax_breaks_cycle as 'income_tax_breaks_cycle' ,
sum(a.zp_vat_amount) as 'vat_zp_amount',
sum(a.pp_vat_amount) as 'vat_pp_amount',
b.more_quarter_invoice_amount 'more_quarter_invoice_amount',
sum(a.invoice_amount) 'invoice_amount',
case when tp.VAT_breaks_amount >= a.invoice_amount then (a.invoice_amount - ceil(a.zp_vat_fee))
		else a.invoice_amount - ceil(a.zp_vat_fee+a.pp_vat_fee) end 'vat_taxable_income_amount',
case when tp.VAT_breaks_amount >= a.invoice_amount then ceil(a.zp_vat_fee)
    else ceil(a.zp_vat_fee+a.pp_vat_fee)  end  'vat_should_tax_money',
ceil(a.vat_fee_rate*100)'vat_rate',
sum(a.vat_already_tax_money) 'vat_already_tax_money',
case when tp.surcharge_breaks_amount >= a.invoice_amount then ceil(a.zp_vat_fee)  else case when tp.VAT_breaks_amount >= a.invoice_amount then ceil(a.zp_vat_fee) else ceil(a.zp_vat_fee+a.pp_vat_fee) end end 'additional_taxable_income_amount',
case when tp.surcharge_breaks_amount < a.invoice_amount
				then (select ceil((case when tp.VAT_breaks_amount >= a.invoice_amount then ceil(a.zp_vat_fee) else ceil(a.zp_vat_fee+a.pp_vat_fee) end) * (urban_construction_tax_rate+education_surcharge_tax_rate+local_education_surcharge_rate) /100)
						from t_e_tax_rules_config where policy_id = tp.id and company_type = c.company_type and tax_type = 3 limit 1)
   when tp.surcharge_breaks_amount >= a.invoice_amount
			then (select ceil(a.zp_vat_fee * (urban_construction_tax_rate)/100)
					from t_e_tax_rules_config where policy_id = tp.id and company_type = c.company_type and tax_type = 3 limit 1) else 0  end as 'additional_should_tax_money',
(select (urban_construction_tax_rate+education_surcharge_tax_rate+local_education_surcharge_rate)
						from t_e_tax_rules_config where policy_id = tp.id and company_type = c.company_type and tax_type = 3 limit 1) 'additional_rate',
sum(a.additional_already_tax_money) 'additional_already_tax_money',
tp.levy_way 'levy_way',
tp.income_tax_breaks_amount 'income_tax_breaks_amount',
case when tp.levy_way = 1 then
   case when tp.income_tax_breaks_amount < a.invoice_amount
				then (a.invoice_amount - ceil(case when tp.VAT_breaks_amount >= a.invoice_amount then a.zp_vat_fee else (a.zp_vat_fee+a.pp_vat_fee) end ))
				else 0 end
    when tp.levy_way = 2 then
			 (select ifnull((b.year_invoice_amount - sum(vat_should_tax_money)),0) from t_e_company_tax_bill where tax_bill_year=year(now()) and tax_bill_seasonal != QUARTER(now()) and company_id = c.id) +
					(a.invoice_amount - ceil(case when tp.VAT_breaks_amount >= a.invoice_amount then a.zp_vat_fee else (a.zp_vat_fee+a.pp_vat_fee) end ))
 end 'income_taxable_income',
sum(a.income_already_tax_money) 'income_already_tax_money'
from t_e_member_company c
left join t_e_tax_policy tp on c.company_type = tp.company_type and c.park_id = tp.park_id
left join (select company_id,max(vat_fee_rate) vat_fee_rate,sum(invoice_amount) invoice_amount,
	sum(case when invoice_type = 2 then invoice_amount else 0 end) zp_vat_amount,
	sum(case when invoice_type = 1 then invoice_amount else 0 end) pp_vat_amount,
	sum(case when invoice_type = 2 then vat_fee else 0 end) zp_vat_fee,
	sum(case when invoice_type = 1 then vat_fee else 0 end) pp_vat_fee,
	sum(vat.vat_already_tax_money) 'vat_already_tax_money',
	sum(vat.additional_already_tax_money) 'additional_already_tax_money',
	sum(vat.income_already_tax_money) 'income_already_tax_money'
		from (
			select company_id,invoice_type,vat_fee_rate,sum(invoice_amount) invoice_amount,
	(sum(ifnull(io.vat_fee,0)) + sum(IFNULL(io.VAT_payment, 0))) 'vat_already_tax_money',
	(sum(ifnull(io.surcharge,0))+sum(IFNULL(io.surcharge_payment, 0))) 'additional_already_tax_money',
	( sum(ifnull(io.personal_income_tax,0))+sum(IFNULL(io.income_tax_payment, 0))- sum(IFNULL(io.refund_tax_fee, 0))) 'income_already_tax_money',
		ifnull((sum(invoice_amount) / (1+VAT_fee_rate) * VAT_fee_rate),0) as 'vat_fee'
		from t_e_invoice_order io,t_e_order od,t_e_member_company c
		where io.order_no = od.order_no and od.order_status>=2 and od.order_status <=7 and VAT_fee_rate is not null
		and io.company_id = c.id and c.ein is null
		and ((QUARTER(confirm_invoice_time)=QUARTER(now()) and  DATE_FORMAT(io.confirm_invoice_time,'%Y') = DATE_FORMAT(now(),'%Y')) or (confirm_invoice_time is null))
		group by company_id,vat_fee_rate,invoice_type
	union
	 select c.id company_id,a1.invoice_type,a1.vat_fee_rate,a1.invoice_amount,a1.vat_already_tax_money,a1.additional_already_tax_money,a1.income_already_tax_money, a1.vat_fee
		from (select c.ein,invoice_type,vat_fee_rate,sum(invoice_amount) invoice_amount,
	(sum(ifnull(io.vat_fee,0)) + sum(IFNULL(io.VAT_payment, 0))) 'vat_already_tax_money',
	(sum(ifnull(io.surcharge,0))+sum(IFNULL(io.surcharge_payment, 0))) 'additional_already_tax_money',
	( sum(ifnull(io.personal_income_tax,0))+sum(IFNULL(io.income_tax_payment, 0))- sum(IFNULL(io.refund_tax_fee, 0))) 'income_already_tax_money',
		ifnull((sum(invoice_amount) / (1+VAT_fee_rate) * VAT_fee_rate),0) as 'vat_fee'
		from t_e_invoice_order io,t_e_order od,t_e_member_company c
		where io.order_no = od.order_no and od.order_status>=2 and od.order_status <=7 and VAT_fee_rate is not null
		and io.company_id = c.id and c.ein is not null
		and ((QUARTER(confirm_invoice_time)=QUARTER(now()) and  DATE_FORMAT(io.confirm_invoice_time,'%Y') = DATE_FORMAT(now(),'%Y')) or (confirm_invoice_time is null))
		group by c.ein,vat_fee_rate,invoice_type) a1,t_e_member_company c
		where a1.ein = c.ein and c.ein is not null
	) vat group by company_id
	) a  on a.company_id = c.id
left join (
select ior.company_id,sum(case when DATE_FORMAT(ior.add_time,'%Y') = DATE_FORMAT(now(),'%Y') then ior.invoice_amount else 0 end)  year_invoice_amount,
sum(case when QUARTER(ior.confirm_invoice_time)=QUARTER(now()) and QUARTER(ior.add_time)!=QUARTER(now()) then ior.invoice_amount else 0 end) more_quarter_invoice_amount
 from t_e_order ord ,t_e_invoice_order ior,t_e_member_company c
   where ord.order_no = ior.order_no and ord.order_status>= 2 and ord.order_status < 8 and ior.company_id = c.id and c.ein is null
group by ior.company_id
 union
select c.id company_id,year_invoice_amount,a.more_quarter_invoice_amount from (
select mc.ein,sum(case when DATE_FORMAT(ior.add_time,'%Y') = DATE_FORMAT(now(),'%Y') then ior.invoice_amount else 0 end) year_invoice_amount,
sum(case when QUARTER(ior.confirm_invoice_time)=QUARTER(now()) and QUARTER(ior.add_time)!=QUARTER(now()) then ior.invoice_amount else 0 end) more_quarter_invoice_amount
 from t_e_order ord ,t_e_invoice_order ior,t_e_member_company mc
   where ord.order_no = ior.order_no and ord.order_status>= 2 and ord.order_status < 8 and ior.company_id = mc.id and mc.ein is not null
group by mc.ein) a,t_e_member_company c
where a.ein = c.ein and c.ein is not null
) b on b.company_id = c.id
where c.`status`!=4
group by c.id) ctax) t on mc.id = t.company_id
left join t_e_tax_policy policy on mc.company_type = policy.company_type and mc.park_id = policy.park_id
where mc.`status`!=4;


#=================同步国金助手2.1版本调整 6.21
#会员表
ALTER TABLE `t_e_member_account`
MODIFY COLUMN `channel_service_id`  bigint(11) NULL DEFAULT NULL COMMENT '渠道服务商id' AFTER `sign`,
MODIFY COLUMN `channel_employees_id`  bigint(11) NULL DEFAULT NULL COMMENT '渠道员工id' AFTER `channel_service_id`,
ADD COLUMN `auth_push_state`  int NULL COMMENT '实名推送状态推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送' AFTER `is_demotion`,
ADD COLUMN `member_auth_type`  int NULL DEFAULT 0 COMMENT '会员身份类型 0-未知 1-个人 2-企业' AFTER `auth_push_state`,
ADD COLUMN `channel_user_id`  bigint(11) NULL COMMENT '渠道用户id' AFTER `member_auth_type`;

#会员变动表
ALTER TABLE `t_e_member_account_change`
MODIFY COLUMN `channel_service_id`  bigint(11) NULL DEFAULT NULL COMMENT '渠道服务商id' AFTER `sign`,
MODIFY COLUMN `channel_employees_id`  bigint(11) NULL DEFAULT NULL COMMENT '渠道员工id' AFTER `channel_service_id`,
ADD COLUMN `auth_push_state`  int NULL COMMENT '实名推送状态推送状态：0-待推送 1-推送中 2-已推送 3-推送失败 4-无需推送' AFTER `is_demotion`,
ADD COLUMN `member_auth_type`  int NULL DEFAULT 0   COMMENT '会员身份类型 0-未知 1-个人 2-企业' AFTER `auth_push_state`,
ADD COLUMN `channel_user_id`  bigint(11) NULL COMMENT '渠道用户id' AFTER `member_auth_type`;

#订单主表
ALTER TABLE `t_e_order`
ADD COLUMN `channel_user_id`  bigint(11) NULL COMMENT '渠道用户id' AFTER `channel_product_code`;

#通知管理
ALTER TABLE `t_e_message_notice`
MODIFY COLUMN `notice_content`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '通知内容' AFTER `notice_subtitle`;
ALTER TABLE `t_e_notice_manage`
MODIFY COLUMN `notice_content`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '通知内容' AFTER `notice_subtitle`;


#==========历史数据处理==========================
#将历史电票订单发票图片保存到开票订单表
update t_e_invoice_order io set io.invoice_imgs = (select GROUP_CONCAT(ird.eInvoice_oss_img_url) from t_e_invoice_record ir,t_e_invoice_record_detail ird
  where ir.invoice_record_no = ird.invoice_record_no and ir.order_no = io.order_no and ird.`status` != 2
),io.remark = '历史数据修改发票图片' where io.confirm_invoice_time is not NULL and io.invoice_imgs is null;

#已注销企业根据企业名称匹配税号
update t_e_member_company mc1,t_e_member_company mc2
set mc1.ein = mc2.ein ,mc1.remark = '已注销企业批量匹配税号'
where mc1.company_name = mc2.company_name and mc1.id != mc2.id
and mc1.`status` = 4 and mc2.`status`!= 4 and mc2.ein is not null;

#迁移的个体户税单迁移到新的个体户
update t_e_company_tax_bill ctb,
(select c.id cid,a.* from t_e_member_company c,(
select ct.*,mc.ein from t_e_member_company mc,t_e_company_tax_bill ct where ct.company_id = mc.id and mc.`status` = 4 and mc.park_id = ct.park_id
)a where c.ein = a.ein and c.`status`!= 4 and c.id != a.company_id and c.park_id = a.park_id) b
set ctb.company_id = b.cid,ctb.remark = '迁移的个体户税单迁移到新的个体户',ctb.update_time = now()
where ctb.id = b.id;

#已使用优惠卷将优惠卷状态改成已使用
update t_e_coupons_issue_record cir set `status` = 1,update_time = now(),remark='已使用优惠卷状态修改' where id in (
select coupons_issue_id from t_e_order o ,t_e_register_order ro
where o.order_no = ro.order_no and o.order_status not in (0,1,3,6,7) and coupons_issue_id is not null
) and `status` = 2;

#优惠卷状态修改
update t_e_coupons t1,
(select id,case when `status` = 0 then 3 when DATE_FORMAT(start_date,'%Y-%m-%d') > DATE_FORMAT(now(),'%Y-%m-%d') then 0
  when DATE_FORMAT(start_date,'%Y-%m-%d') <= DATE_FORMAT(now(),'%Y-%m-%d') and DATE_FORMAT(end_date,'%Y-%m-%d') >= DATE_FORMAT(now(),'%Y-%m-%d') then 1
  when  DATE_FORMAT(end_date,'%Y-%m-%d') < DATE_FORMAT(now(),'%Y-%m-%d') then 2 end as status  from t_e_coupons) t2
set t1.`status` = t2.`status` ,t1.update_time = now(),remark='优惠卷状态修改'
where t1.id = t2.id;

#业务合同扩容
ALTER TABLE `t_e_invoice_order`
MODIFY COLUMN `business_contract_imgs`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '业务合同' AFTER `create_way`;
ALTER TABLE `t_e_invoice_order_change_record`
MODIFY COLUMN `business_contract_imgs`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '业务合同' AFTER `create_way`;

#渠道信息表
ALTER TABLE `t_e_channel_info`
ADD COLUMN `app_id`  varchar(32) NOT NULL COMMENT 'appid' AFTER `channel_name`;
##会员表增加字段
ALTER TABLE `t_e_member_account`
ADD COLUMN `is_clean_bank_card`  int NULL COMMENT '是否清除绑卡数据 0-没有清除 1-已清除' AFTER `channel_product_code`;

#渠道信息表修改数据
UPDATE `t_e_channel_info` SET `channel_name`='国金助手',`update_time`=now(), `update_user`='admin' WHERE `channel_code`='GUOJIN';
UPDATE `t_e_channel_info` SET `channel_name`='创航助手',`update_time`=now(), `update_user`='admin' WHERE `channel_code`='BAIWANG';
UPDATE `t_e_channel_info` SET `channel_name`='诺税筹助手',`update_time`=now(), `update_user`='admin' WHERE `channel_code`='HANGXIN';

update t_e_member_account set member_auth_type = 1;
update t_e_member_account_change set member_auth_type = 1;
