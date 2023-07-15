# 新增会员签约易税表
DROP TABLE if EXISTS `t_e_member_to_sign_yishui`;
CREATE TABLE `t_e_member_to_sign_yishui` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(11) NOT NULL COMMENT '用户id',
  `enterprise_professional_facilitator_id` bigint(11) DEFAULT NULL COMMENT '签约id',
  `professional_id` bigint(11) DEFAULT NULL COMMENT '人员id',
  `professional_sn` varchar(50) DEFAULT NULL COMMENT '人员编号',
  `id_card_no` varchar(18) DEFAULT NULL COMMENT '身份证号码',
  `is_contract` tinyint(1) NOT NULL DEFAULT '0' COMMENT '签约状态 0未签约 1已签约 2已解约',
  `contract_start_time` datetime DEFAULT NULL COMMENT '签约开始时间',
  `contract_end_time` datetime DEFAULT NULL COMMENT '签约结束时间',
  `is_auth` tinyint(1) DEFAULT '0' COMMENT '是否认证 0未认证 1已认证',
  `auth_time` datetime DEFAULT NULL COMMENT '认证时间',
  `oem_code` varchar(12) CHARACTER SET utf8 DEFAULT NULL COMMENT '机构编码',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `create_user` varchar(60) CHARACTER SET utf8 NOT NULL COMMENT '创建人账号',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` varchar(60) CHARACTER SET utf8 DEFAULT NULL COMMENT '修改人账号',
  `remark` varchar(300) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_INDEX` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员签约易税表';

# 新增易税回调请求参数表
DROP TABLE IF EXISTS `t_e_withdraw_order_yishui_notify`;
CREATE TABLE `t_e_withdraw_order_yishui_notify` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `merchant_code` VARCHAR(30) CHARACTER SET utf8 DEFAULT NULL COMMENT '商户号',
  `param_total` VARCHAR(1000) DEFAULT NULL COMMENT '易税回调原始请求参数',
  `param_type` VARCHAR(30) DEFAULT NULL COMMENT '易税回调类型：create_batch_order：订单落地通知，charge_resout：充值入账结果通知 , payment_resout：支付结果通知',
  `param_result` VARCHAR(255) DEFAULT NULL COMMENT '易税回调待解密请求参数',
  `param_result_plaintext` VARCHAR(500) DEFAULT NULL COMMENT '易税回调明文请求参数',
  `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='易税回调请求参数';

# 字典表
INSERT INTO sys_e_dictionary (dict_code,dict_value,dict_desc,add_time,add_user) VALUES('yishui_template_url','template/自由职业者签约协议.pdf','易税签约协议',NOW(),'admin');
INSERT INTO sys_e_dictionary (dict_code,dict_value,dict_desc,add_time,add_user) VALUES('ys_plat_official_seal','common/ys_official_seal.png','易税公章',NOW(),'admin');
UPDATE sys_e_dictionary SET dict_desc = '用户提现方案开关：1-北京代付 2-纳呗 3-其他 4-易税' WHERE dict_code = 'withdraw_scheme_switch';
INSERT INTO sys_e_dictionary (dict_code,dict_value,dict_desc,add_time,add_user) VALUES('itax_withdraw_yishui_switch','0','易税提现挡板开关：1-挡板提现 0-正常提现',NOW(),'admin');

# 渠道信息表
ALTER TABLE `t_e_channel_info`
  ADD COLUMN `channel_logo` VARCHAR(128) NULL   COMMENT '渠道图标oss地址' AFTER `app_id`;

# 支付流水表
ALTER TABLE `t_e_pay_water`
  CHANGE `pay_channels` `pay_channels` INT(1) NOT NULL   COMMENT '支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗 8-字节跳动支付 9-线下 10-易税';

# 用户银行卡表
ALTER TABLE `t_e_user_bank_card`
  ADD COLUMN `professional_bank_id` BIGINT(11) NULL   COMMENT '易税银行卡ID' AFTER `bank_card_type`;

# 易税接入配置
INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `status`, `add_time`, `add_user`, `remark`)
SELECT `oem_code`, '32', 'E20574429', 'c37b004a59cca3d2f9cdc0dfb87e0391',
	'https://api.yeeshui.com', '{"merchantCode":"YCSZZS","userName":"湖南云财数字","password":"123456","aseKey":"59cca3d2f9cdc0df","crowdId":"14034","resolveId":"4739"}',
	'1', NOW(), 'admin', '易税接入参数配置'
FROM t_e_oem WHERE company_name = '湖南云财数字科技有限公司';

INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `status`, `add_time`, `add_user`, `remark`)
SELECT `oem_code`, '32', 'E20595649', '8d6d5d7ee8a26c964c1f0bab9621e0b4',
	'https://api.yeeshui.com', '{"merchantCode":"DBLZS","userName":"多贝拉信息技术","password":"123456","aseKey":"e8a26c964c1f0bab","crowdId":"14036","resolveId":"4741"}',
	'1', NOW(), 'admin', '易税接入参数配置'
FROM t_e_oem WHERE company_name = '多贝拉信息技术有限公司';

INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `status`, `add_time`, `add_user`, `remark`)
SELECT `oem_code`, '32', 'E19099233', 'a2844dca668fc1a1d6f7c59235b9763a',
	'https://api.yeeshui.com', '{"merchantCode":"GUOJIN","userName":"湖南国金","password":"123456","aseKey":"668fc1a1d6f7c592","crowdId":"13337","resolveId":"3790"}',
	'1', NOW(), 'admin', '易税接入参数配置'
FROM t_e_oem WHERE company_name = '湖南国金通汇信息技术有限公司';

#渠道机构logo
UPDATE t_e_channel_info SET channel_logo = CONCAT("/icon/channel_logo/",channel_code,".png");

# 机构表
ALTER TABLE `t_e_oem`
  ADD COLUMN `official_seal_img_public` VARCHAR(128) NULL   COMMENT '公章图片地址(公域)' AFTER `official_seal_img`;
