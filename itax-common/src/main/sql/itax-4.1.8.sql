# 注册订单表
ALTER TABLE `t_e_register_order`
  ADD COLUMN `is_auto_create` INT(1) DEFAULT 0 COMMENT '是否自动生成（企业信息） 0-否 1-是' AFTER `taxpayer_type`;

# 注册订单变更记录表
ALTER TABLE `t_e_register_order_change_record`
  ADD COLUMN `is_auto_create` INT(1) DEFAULT 0 COMMENT '是否自动生成（企业信息） 0-否 1-是' AFTER `taxpayer_type`;