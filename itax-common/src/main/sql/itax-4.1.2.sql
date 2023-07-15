ALTER TABLE `t_e_tax_policy`
  ADD COLUMN `income_taxable_income_breaks` BIGINT(11) NULL   COMMENT '应纳税所得额减免' AFTER `is_water_conservancy_fund_halved`,
  ADD COLUMN `income_tax_relief_ratio` DECIMAL(6,2) NULL   COMMENT '所得税税收减免比例' AFTER `income_taxable_income_breaks`;

ALTER TABLE `t_e_tax_policy_change`
  ADD COLUMN `income_taxable_income_breaks` BIGINT(11) NULL   COMMENT '应纳税所得额减免' AFTER `is_water_conservancy_fund_halved`,
  ADD COLUMN `income_tax_relief_ratio` DECIMAL(6,2) NULL   COMMENT '所得税税收减免比例' AFTER `income_taxable_income_breaks`;
