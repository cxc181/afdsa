#分润记录表
ALTER TABLE `t_e_profits_detail`
  ADD COLUMN `withdraw_order_no` VARCHAR(32) NULL   COMMENT '提现订单号' AFTER `wallet_type`;

#oem机构表
ALTER TABLE `t_e_oem`
  ADD COLUMN `max_commission_withdraw_single_limit` BIGINT(11) DEFAULT NULL   COMMENT '佣金钱包提现单笔最高限额（分）' AFTER `consumption_withdraw_rate`,
  ADD COLUMN `commission_withdraw_month_limit` BIGINT(11) DEFAULT  NULL   COMMENT '佣金钱包提现单月限额（分）' AFTER `max_commission_withdraw_single_limit`,
  ADD COLUMN `recharge_days` BIGINT(11) DEFAULT  NULL  COMMENT '消费钱包提现充值天数限制' AFTER `commission_withdraw_month_limit`;

#订单表
ALTER TABLE `t_e_order`
  ADD COLUMN `is_submit_performance_voucher` INT(1) DEFAULT 0  NULL   COMMENT '是否提交佣金提现业绩凭证 0-无需提交 1-未提交 2-已提交' AFTER `is_self_paying`;

#园区表
ALTER TABLE `t_e_park`
  ADD COLUMN `is_register_profit` INT(1) DEFAULT 1 NULL   COMMENT '是否企业注册分润 0-否 1-是' AFTER `park_thumbnail`,
  ADD COLUMN `is_renew_profit` INT(1) DEFAULT 1 NULL   COMMENT '是否托管续费分润 0-否 1-是' AFTER `is_register_profit`;