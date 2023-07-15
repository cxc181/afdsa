#oem机构表
ALTER TABLE `t_e_oem`
ADD COLUMN `is_other_oem_pay`  int(1) NULL DEFAULT 0 COMMENT '是否其他oem机构收单  0-否 1-是' AFTER `is_send_audit_bills_message`,
ADD COLUMN `other_pay_oemcode`  varchar(12) NULL COMMENT '收单机构oemcode' AFTER `is_other_oem_pay`,
ADD COLUMN `oem_appid`  varchar(32) NULL COMMENT 'oem小程序appid' AFTER `other_pay_oemcode`,
ADD COLUMN `is_checkstand`  int(1) NULL DEFAULT 0 COMMENT '是否收银台 0-否 1-是' AFTER `oem_appid`;

#支付流水表
ALTER TABLE `t_e_pay_water`
ADD COLUMN `other_pay_oemcode`  varchar(12) NULL COMMENT '收单机构oemcode' AFTER `refund_status`;

#支付流水历史数据处理
update t_e_pay_water set other_pay_oemcode = oem_code;

#通知管理
ALTER TABLE `t_e_notice_manage`
MODIFY COLUMN `user_phones`  longtext NULL COMMENT '用户手机号 多个账号之间用逗号分割' AFTER `notice_obj`;

ALTER TABLE `t_e_notice_manage`
ADD COLUMN `user_list_url`  VARCHAR(128) NULL COMMENT '用户导入文件地址' AFTER `user_phones`;
