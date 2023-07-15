# 税费政策表
ALTER TABLE `t_e_tax_policy`
  ADD COLUMN `month_invoice_amount` BIGINT(11) NULL   COMMENT '月开票额度' AFTER `quarter_invoice_amount`;

# 税费政策变更记录表
ALTER TABLE t_e_tax_policy_change
  ADD COLUMN `quarter_invoice_amount` BIGINT(11) DEFAULT NULL COMMENT '季开票额度' AFTER `total_invoice_amount`,
  ADD COLUMN `month_invoice_amount` BIGINT(11) NULL   COMMENT '月开票额度' AFTER `quarter_invoice_amount`;