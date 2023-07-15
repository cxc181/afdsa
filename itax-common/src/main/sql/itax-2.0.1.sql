#======V2.0.1版本数据脚本

#字典表
INSERT INTO sys_e_dictionary (dict_code, dict_value, parent_dict_id, dict_desc, add_time, add_user, update_time, update_user, remark)
VALUES ('withdraw_agent_min_limit', '0', NULL, '后台代理提现最小金额0元', NOW(), 'admin', NULL, NULL, NULL);

#流水表
ALTER TABLE `t_e_pay_water`
MODIFY COLUMN `remark`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;

