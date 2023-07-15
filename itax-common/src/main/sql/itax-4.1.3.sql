ALTER TABLE `t_e_oem`
  ADD COLUMN `consumption_withdraw_explain` VARCHAR(256) NULL   COMMENT '消费钱包提现说明' AFTER `receiving_bank_account`,
  ADD COLUMN `consumption_withdraw_free_credit` BIGINT(11) DEFAULT 0  NULL   COMMENT '消费钱包提现免费额度（分）' AFTER `consumption_withdraw_explain`,
  ADD COLUMN `consumption_withdraw_rate` DECIMAL(6,2) DEFAULT 0.00  NULL   COMMENT '消费钱包提现手续费率（百分数）' AFTER `consumption_withdraw_free_credit`;