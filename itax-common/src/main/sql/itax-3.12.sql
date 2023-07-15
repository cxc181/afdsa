#企业税单变更表
drop table if exists t_e_company_tax_bill_change;
CREATE TABLE `t_e_company_tax_bill_change`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_tax_bill_id` bigint(20) NOT NULL COMMENT '企业税单id',
  `park_tax_bill_id` bigint(20) NULL DEFAULT NULL COMMENT '园区税单id',
  `tax_bill_year` int(4) NULL DEFAULT NULL COMMENT '税款所属期年',
  `tax_bill_seasonal` int(1) NULL DEFAULT NULL COMMENT '税款所属期-季度',
  `park_id` bigint(20) NULL DEFAULT NULL COMMENT '园区id',
  `company_id` bigint(20) NULL DEFAULT NULL COMMENT '企业id',
  `zp_invoice_amount` bigint(20) NULL DEFAULT NULL COMMENT '增值税专用发票开票金额',
  `pp_invoice_amount` bigint(20) NULL DEFAULT NULL COMMENT '增值税普通发票开票金额',
  `invoice_money` bigint(20) NULL DEFAULT 0 COMMENT '本期开票金额',
  `already_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '已交税费',
  `should_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '总应纳税所得额',
  `recoverable_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '应退税费',
  `supplement_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '应补税费',
  `vat_taxable_income_amount` bigint(20) NULL DEFAULT 0 COMMENT '增值税应纳税所得额',
  `vat_rate` decimal(10, 8) NULL DEFAULT 0.00000000 COMMENT '增值税适用税率',
  `vat_already_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '已缴增值税',
  `vat_should_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '应缴增值税',
  `vat_recoverable_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '应退增值税',
  `vat_supplement_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '应补增值税',
  `tax_bill_status` int(1) NULL DEFAULT NULL COMMENT '税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税 6-待核对   7-待填报成本 8-待申报 9-已作废',
  `affirm_time` datetime NULL DEFAULT NULL COMMENT '确认时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `add_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `add_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `additional_taxable_income_amount` bigint(20) NULL DEFAULT 0 COMMENT '附加税应纳税所得额',
  `additional_rate` decimal(10, 8) NULL DEFAULT 0.00000000 COMMENT '附加税适用税率',
  `additional_already_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '附加税已缴',
  `additional_should_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '附加税应缴',
  `additional_recoverable_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '附加税应退',
  `additional_supplement_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '附加税应补',
  `income_taxable_income_amount` bigint(20) NULL DEFAULT 0 COMMENT '所得税应纳税所得额',
  `income_levy_way` int(1) NULL DEFAULT NULL COMMENT '计税方式（1：预缴征收率，2：核定应税所得率）',
  `taxable_income_rate` decimal(10, 8) NULL DEFAULT NULL COMMENT '应税所得率',
  `income_rate` decimal(10, 8) NULL DEFAULT 0.00000000 COMMENT '所得税适用税率',
  `income_already_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '所得税已缴',
  `income_should_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '所得税应缴',
  `income_recoverable_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '所得税应退',
  `income_supplement_tax_money` bigint(20) NULL DEFAULT 0 COMMENT '所得税应补',
  `over_time_is_sms` int(1) NULL DEFAULT NULL COMMENT '超过15天未补交是否已经发短信提醒',
  `iit_voucher_pic` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人所得税凭证图片',
  `vat_voucher_pic` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '增值税凭证图片',
  `ticket_pic` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '罚款凭证',
  `iit_vouchers_status` int(1) NULL DEFAULT NULL COMMENT '个人所得税凭证上传状态 1-未上传 2-已上传 3-无需上传',
  `vat_vouchers_status` int(1) NULL DEFAULT NULL COMMENT ' 增值税凭证上传状态 1-未上传 2-已上传 3-无需上传',
  `cancellation_amount` bigint(20) NULL DEFAULT 0 COMMENT '作废/红冲金额',
  `year_cost_amount` bigint(20) NULL DEFAULT NULL COMMENT '年度累计成本金额（分）',
  `year_income_amount` bigint(20) NULL DEFAULT NULL COMMENT '年度累计收入金额（分）',
  `cost_item_imgs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '成本项图片',
  `ticket_free_income_amount` bigint(20) NULL DEFAULT NULL COMMENT '无票收入金额（分）',
  `vat_deduction_taxfee` bigint(20) NULL DEFAULT NULL COMMENT '增值税扣除税费（分）',
  `additional_deduction_taxfee` bigint(20) NULL DEFAULT NULL COMMENT '附加税扣除税费(分)',
  `iit_deduction_amount` bigint(20) NULL DEFAULT NULL COMMENT '个税扣除金额（分）',
  `quarter_cost_amount` bigint(20) NULL DEFAULT NULL COMMENT '本季累计成本金额（分）',
  `quarter_income_amount` bigint(20) NULL DEFAULT NULL COMMENT '季度累计收入金额（分）',
  `sign_img` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户签名',
  `income_levy_type` int(1) NULL DEFAULT NULL COMMENT '所得税征收方式 1-查账征收 2-核定征收',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `generate_type` int(1) NULL DEFAULT NULL COMMENT '生成方式 1季度自动生成 2企业注销生成',
  `descrip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '变动内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '企业税单变更表' ROW_FORMAT = Dynamic;



#税费政策变更表
drop table if exists t_e_tax_policy_change;
CREATE TABLE `t_e_tax_policy_change` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `policy_id` bigint(11) NOT NULL COMMENT '政策id',
  `park_id` bigint(11) NOT NULL COMMENT '园区id',
  `company_type` int(1) NOT NULL COMMENT '企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任',
  `levy_way` int(1) DEFAULT '1' COMMENT '计税方式（1：预缴征收率，2：核定应税所得率）',
  `VAT_breaks_amount` bigint(11) DEFAULT '0' COMMENT '增值税个税减免额度',
  `VAT_breaks_cycle` int(1) DEFAULT NULL COMMENT '增值税个税减免周期 1-按月 2-按季度',
  `income_tax_breaks_amount` bigint(11) DEFAULT '0' COMMENT '所得税减免额度',
  `income_tax_breaks_cycle` int(11) DEFAULT NULL COMMENT '所得税减免周期 1-按月 2-按季度',
  `surcharge_breaks_amount` bigint(11) DEFAULT '0' COMMENT '附加税减免额度',
  `surcharge_breaks_cycle` int(1) DEFAULT NULL COMMENT '附加税减免周期  1-按月 2-按季度',
  `transact_require` varchar(128) DEFAULT NULL COMMENT '办理要求',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态 0-待上架 1-已上架 2-已下架 3-已暂停',
  `total_invoice_amount` bigint(11) NOT NULL DEFAULT '0' COMMENT '年度开票总额',
  `policy_file_url` varchar(128) DEFAULT NULL COMMENT '政策文件地址',
  `income_levy_type` int(1) DEFAULT '1' COMMENT '所得税征收方式 1-查账征收 2-核定征收',
  `read_content` varchar(128) DEFAULT NULL COMMENT '视频认证阅读内容',
  `tax_rules_config_json` text DEFAULT NULL COMMENT '税费规则配置json',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `add_user` varchar(32) DEFAULT NULL COMMENT '添加人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '修改人',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) COMMENT='税费政策变更表';

drop table if exists t_e_product_by_park;
CREATE TABLE `t_e_product_by_park` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `product_id` bigint(11) NOT NULL COMMENT '产品id',
	`park_id` bigint(11) NOT NULL COMMENT '园区id',
  `prod_type` int(2) NOT NULL COMMENT '产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-黄金会员（废弃） 10-钻石会员 （废弃）\r\n11-个体注销 12-个独注销 13-有限合伙注销 14-有限责任注销 15-公户申请和托管 16-个体托管费续费 17-对公户年费续费',
  `prod_amount` bigint(11) DEFAULT '0' COMMENT '金额',
  `amount_name` varchar(32) DEFAULT NULL COMMENT '金额名称',
  `amount_way` int(1) DEFAULT NULL COMMENT '费用方式 1-固定金额 2-比率',
  `oem_code` varchar(12) NOT NULL COMMENT '机构编码',
  `is_delete` int(1) NOT NULL DEFAULT '1' COMMENT '是否删除 0-未删除 1-已删除',
  `processing_fee` bigint(11) DEFAULT '0' COMMENT '办理费（对公户独有）',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `add_user` varchar(32) DEFAULT NULL COMMENT '添加人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` varchar(32) DEFAULT NULL COMMENT '修改人',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  `cancel_total_limit` bigint(11) DEFAULT NULL COMMENT '注销累计开票额度',
  PRIMARY KEY (`id`)
) COMMENT='产品园区定价';


#收费标准
ALTER TABLE `t_e_charge_standard`
ADD COLUMN `park_id`  bigint(11) NULL COMMENT '园区id' AFTER `remark`;
ALTER TABLE `t_e_charge_standard`
ADD COLUMN `park_product_id`  bigint(11) NULL COMMENT '园区产品定价id' AFTER `park_id`;

#机构管理
ALTER TABLE `t_e_oem`
ADD COLUMN `official_seal_img`  varchar(128) NULL COMMENT '公章图片地址' AFTER `agreement_template_id`;

#园区管理
ALTER TABLE `t_e_park`
ADD COLUMN `official_seal_img`  varchar(128) NULL COMMENT '公章图片地址' AFTER `income_levy_type`;

#协议模板表
ALTER TABLE `t_e_agreement_template`
ADD COLUMN `template_show_name`  varchar(64) NULL COMMENT '模板显示名称' AFTER `template_name`;
# oem参数配置ocr营业执照识别参数
INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
select `oem_code`, '31' `params_type`, `account`, `sec_key`,
'https://tst.api.mch.duobeikeji.cn/gateway/api/ocr/v1/recognizeBusinessLicense' `url`,
 '{"channel":"new","productCode":"PRD00099"}' `params_values`, `public_key`, `private_key`, `status`,now()  `add_time`, `add_user`, now() `update_time`, `update_user`, `remark` from sys_e_oem_params where params_type = 7 and `status` =1 ;







