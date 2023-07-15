#======V1.0.7版本数据脚本

#短信模板表
UPDATE t_e_sms_template
SET template_content = '您申请的个体工商户资料审核未通过，支付费用已退回至小程序钱包，您可前往小程序“我的-我的钱包”查看，如有疑问，请联系客服#telPhone#。'
WHERE
	template_type = '10';

INSERT INTO t_e_sms_template (oem_code, template_type, template_content, status, add_time, add_user, update_time, update_user, remark)
VALUES ('YCS', '27', '您申请注册的个体工商户由于字号不符合工商要求被驳回，请前往“小程序-我的-企业注册订单”页面提交新的字号，如有疑问，请联系客服#telPhone#。', '1', '2020-05-07 15:00:55', 'admin', NULL, NULL, NULL);

#字典表
INSERT INTO sys_e_dictionary (dict_code, dict_value,  dict_desc, add_time, add_user)
VALUES ('notice_miniprogram_state', 'developer', '跳转小程序类型：developer为开发版；trial为体验版；formal为正式版；默认为正式版', NOW(), 'admin');
,('wechat_request_url_prefix', 'https://api.weixin.qq.com/', '微信官方接口调用url', NOW(), 'admin')
,('wechat_register_audit_notice_page', 'pages/me/enterprise-order/enterprise-order', '微信企业注册审核跳转页', NOW(), 'admin');
UPDATE sys_e_dictionary SET dict_value='您的直推用户#registeredName#有一笔企业注册订单被核名驳回，请您关注~' WHERE dict_code = 'register_pay_notice_tmpl' LIMIT 1

#系统用户表
ALTER TABLE `sys_e_user`
ADD INDEX `user_name_index` (`username`, `oem_code`) ;



