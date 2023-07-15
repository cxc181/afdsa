ALTER TABLE `t_e_pay_water`
MODIFY COLUMN `pay_channels`  int(1) NOT NULL COMMENT '支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗 8-字节跳动支付' AFTER `pay_way`;
ALTER TABLE `t_e_pay_water`
MODIFY COLUMN `pay_way`  int(1) NOT NULL COMMENT '支付方式  1-微信 2-余额 3-支付宝 4-快捷支付 5-字节跳动' AFTER `pay_water_type`;



INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('YCS', '30', 'tta7b2d10f388af56801', '062428ed4328adce9c7b829cbc223712688dd7b4', '', '{\"payAccount\":\"70060687693322672720\",\"paySalt\":\"FOaAa75RSNzjFdn1C0Zh0phzGwuOWKf6LJG4DCc0\",\"token\":\"\"}', '', '', '1', now(), 'admin', now(), '', '字节跳动配置');

INSERT INTO `sys_e_dictionary` ( `dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('bytedancePayNotifyUrl', 'https://itaxdev.yuncaiol.cn/itax-api/order/registerorder/bytedancePayNotify', NULL, '字节跳动支付通知URL', now(), 'admin', now(), '', '');

INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `parent_dict_id`, `dict_desc`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`)
VALUES ('itax_bytedance_switch', '1', NULL, '字节跳动支付挡板开关：1-挡板支付 0-正常支付', now(), 'admin', now(), '', '');

ALTER TABLE `t_e_member_account`
MODIFY COLUMN `open_id`  varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信openId，微信支付时需要' AFTER `alipay_user_id`;
ALTER TABLE `t_e_member_account_change`
MODIFY COLUMN `open_id`  varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信openId，微信支付时需要' AFTER `alipay_user_id`;



