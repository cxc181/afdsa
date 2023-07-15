#开票订单
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `achievement_imgs`  text NULL COMMENT '成果图片，多个图片之间用逗号分割' AFTER `supplement_explain`,
ADD COLUMN `achievement_video`  varchar(500) NULL COMMENT '成果视频' AFTER `achievement_imgs`,
ADD COLUMN `achievement_status`  int(1) NULL DEFAULT 0 COMMENT '成果状态 0-无需上传 1-成果前置 2-待上传 3-审核中 4-审核不通过 5-审核通过' AFTER `achievement_video`;

#开票订单变更表
ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `achievement_imgs`  text NULL COMMENT '成果图片，多个图片之间用逗号分割' AFTER `supplement_explain`,
ADD COLUMN `achievement_video`  varchar(500) NULL COMMENT '成果视频' AFTER `achievement_imgs`,
ADD COLUMN `achievement_status`  int(1) NULL DEFAULT 0 COMMENT '成果状态 0-无需上传 1-成果前置 2-待上传 3-审核中 4-审核不通过 5-审核通过' AFTER `achievement_video`;

#oem机构表
ALTER TABLE `t_e_oem`
ADD COLUMN `work_audit_way`  int(1) NULL DEFAULT 1 COMMENT '工单审核方 1-平台客服  2-oem机构客服' AFTER `is_big_customer`;

#工单表
ALTER TABLE `t_e_work_order`
MODIFY COLUMN `work_order_type`  int(1) NOT NULL COMMENT '工单类型 1- 办理核名 2-开票审核 3-流水审核 4-成果审核' AFTER `order_type`;

#工单变更表
ALTER TABLE `t_e_work_order_change_record`
MODIFY COLUMN `work_order_type`  int(1) NOT NULL COMMENT '工单类型 1- 工商注册 2-开票 3-流水审核 4-成果审核' AFTER `order_no`;

#人群标签
ALTER TABLE `t_e_crowd_label`
ADD COLUMN `add_user_mode`  int(1) NULL DEFAULT 1 COMMENT '添加用户方式 1-列表导入 2-指定接入方' AFTER `remark`,
ADD COLUMN `access_party_id`  bigint NULL COMMENT '接入方id' AFTER `add_user_mode`;

#人群标签变更表
ALTER TABLE `t_e_crowd_label_change`
ADD COLUMN `add_user_mode`  int(1) NULL DEFAULT 1 COMMENT '添加用户方式 1-列表导入 2-指定接入方' AFTER `remark`,
ADD COLUMN `access_party_id`  bigint NULL COMMENT '接入方id' AFTER `add_user_mode`;

#会员表
ALTER TABLE `t_e_member_account`
ADD COLUMN `access_party_id`  bigint NULL COMMENT '接入方id' AFTER `is_clean_bank_card`;

#会员变更表
ALTER TABLE `t_e_member_account_change`
ADD COLUMN `access_party_id`  bigint NULL COMMENT '接入方id' AFTER `channel_product_code`;

#开票记录表
ALTER TABLE `t_e_invoice_record`
ADD COLUMN `work_order_desc`  varchar(128) NULL COMMENT '工单审核备注' AFTER `remark`;

#开票记录变动表
ALTER TABLE `t_e_invoice_record_change`
ADD COLUMN `work_order_desc`  varchar(128) NULL COMMENT '工单审核备注' AFTER `remark`;

#工单备注表
drop table if exists t_e_work_order_desc;
create table t_e_work_order_desc
(
   id                   bigint(11) not null auto_increment comment '主键id',
   order_no             varchar(32) comment '订单号',
   work_order_no        varchar(32) comment '工单号',
   desc_content         varchar(256) comment '备注内容',
   customer_service_account varchar(32) comment '操作坐席账号',
   customer_service_name varchar(32) comment '操作坐席名称',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_work_order_desc comment '工单备注表';

#接入方信息表
drop table if exists t_e_oem_access_party;
create table t_e_oem_access_party
(
   id                   bigint(11) not null auto_increment comment '主键id',
   access_party_name    varchar(32) comment '接入方名称',
   access_party_code    varchar(32) comment '接入方编号',
   oem_code             varchar(12) comment '所属oem机构',
   access_party_secret  varchar(32) comment '秘钥',
   status               int(1) default 1 comment '状态  1-上架 2-下级',
   login_time           bigint(11) not null default '604800' COMMENT '登录超时时间',
   appletAddress        varchar(128) comment '跳转小程序地址',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table t_e_oem_access_party comment '接入方信息表';

#工单表
ALTER TABLE `t_e_work_order`
ADD COLUMN `achievement_imgs`  text NULL COMMENT '成果图片，多个图片之间用逗号分割' AFTER `remark`,
ADD COLUMN `achievement_video`  varchar(500) NULL COMMENT '成果视频' AFTER `achievement_imgs`;

#工单变工表
ALTER TABLE `t_e_work_order_change_record`
ADD COLUMN `achievement_imgs`  text NULL COMMENT '成果图片，多个图片之间用逗号分割' AFTER `remark`,
ADD COLUMN `achievement_video`  varchar(500) NULL COMMENT '成果视频' AFTER `achievement_imgs`;
#开票订单表
ALTER TABLE `t_e_invoice_order`
ADD COLUMN `achievement_error_remark`  varchar(64) NULL COMMENT '成果审核失败原因' AFTER `achievement_status`;
#开票订单变更表
ALTER TABLE `t_e_invoice_order_change_record`
ADD COLUMN `achievement_error_remark`  varchar(64) NULL COMMENT '成果审核失败原因' AFTER `achievement_status`;
#工单表
ALTER TABLE `t_e_work_order`
ADD COLUMN `customer_service_id`  bigint NULL COMMENT '坐席id,系统用户表id' AFTER `achievement_video`;
#工单变更表
ALTER TABLE `t_e_work_order_change_record`
ADD COLUMN `customer_service_id`  bigint NULL COMMENT '坐席id,系统用户表id' AFTER `achievement_video`;
#接入方表
ALTER TABLE `t_e_oem_access_party`
MODIFY COLUMN `remark`  varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注' AFTER `update_user`;



##新增机构配置表-微信退款参数
INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YCS', '27', 'M130972719', 'b755b965deb2e144f7616162986e0270', 'https://tst.api.mch.duobeikeji.cn/gateway/payment/v1/refund/refundOrder', '{\"channel\":\"new\",\"appId\": \"wxb884fccbb878f5b8\",\"keyNum\": \"bb9aa8f2499c329b88f37567dd9aab31\",\"signKey\": \"2c7a69073936966c8d8662bda82af7c4\",\"productCode\":\"PRD00002\"}', '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', '1', NOW(), 'admin', NOW(), 'admin', '勿动！！微信支付通道相关配置-新渠道');

update t_e_sms_template set template_content = '您申请的个体户资料审核未通过，支付费用已原路退回，如有疑问，请联系客ian服#telPhone#。',update_time = now(),remark='模板修改' where template_type = 10;
update t_e_sms_template set template_content = '您申请注册的个体户由于部分资料不符合市场监督要求被驳回，请提交新的资料，如有疑问，请联系客服#telPhone#。',update_time = now(),remark='模板修改' where template_type = 27;
update t_e_sms_template set template_content = '尊敬的用户：您办理的#regName#个体户已注册成功，马上前往“我的企业”查看吧！',update_time = now(),remark='模板修改' where template_type = 11;
update t_e_sms_template set template_content = '尊敬的用户，您注册的企业“#regName#”已设立登记，为了不影响您的出证，请尽快进行提交签名确认操作，如有疑问，可联系您的专属客服经理，谢谢。',update_time = now(),remark='模板修改' where template_type = 37;
update t_e_sms_template set template_content = '尊敬的用户，您注册的企业“#regName#”经在市场监督管理系统核查未在湖南登记APP提交签名，请尽快进行重新提交签名确认操作，如有疑问，可联系您的专属客服经理，谢谢。',update_time = now(),remark='模板修改' where template_type = 38;
update t_e_sms_template set template_content = '您有一笔需补传资料的开票订单已完成，为避免法律风险，请30日内前往小程序”我的-证据补传”页面补传相应资料。',update_time = now(),remark='模板修改' where template_type = 25;

## 字典表添加待补传成果订单超时天数
INSERT INTO sys_e_dictionary  (dict_code,dict_value,parent_dict_id,dict_desc,add_time,add_user) VALUES ('achievement_timeout','30',null,'待补传成果订单超时天数', NOW(),'admin');

##新增机构配置表-微信h5支付
INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YCS', '28', 'M130972719', 'b755b965deb2e144f7616162986e0270', 'https://tst.api.mch.duobeikeji.cn/gateway/payment/v1/webpay', '{\"keyNum\": \"bb9aa8f2499c329b88f37567dd9aab31\",\"signKey\": \"2c7a69073936966c8d8662bda82af7c4\",\"productCode\":\"PRD00010\"}', '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', '1', NOW(), 'admin', NOW(), 'admin', '勿动！！微信h5支付通道相关配置');
INSERT INTO `sys_e_oem_params` ( `oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YSC', '29', 'M610483443', '607c480c7cf9ec5e61cb353d9137e2fa', 'https://tst.api.mch.duobeikeji.cn/gateway/payment/v1/query/queryRefundOrder', '{\"channel\":\"new\",\"appId\": \"wxd581fea82998fac0\",\"keyNum\": \"bb9aa8f2499c329b88f37567dd9aab31\",\"signKey\": \"9f9d055841a7cf914e0c2211dd7b0ef3\",\"productCode\":\"PRD00002\"}', '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', '1', '2020-08-18 16:21:09', 'admin', '2020-08-18 16:21:09', 'admin', '勿动！！微信退款查询通道相关配置-新渠道');

ALTER TABLE `t_e_pay_water`
ADD COLUMN `pay_pic`  varchar(256) NULL COMMENT '打款凭证' AFTER `remark`;

## 新增机构配置表-微信公众号支付
INSERT INTO `sys_e_oem_params` (`oem_code`, `params_type`, `account`, `sec_key`, `url`, `params_values`, `public_key`, `private_key`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YCS', '30', 'M130972719', 'b755b965deb2e144f7616162986e0270', 'https://tst.api.mch.duobeikeji.cn/gateway/payment/v1/refund/refundOrder', '{"channel":"new","appId": "wxd581fea82998fac0","keyNum": "bb9aa8f2499c329b88f37567dd9aab31","signKey": "9f9d055841a7cf914e0c2211dd7b0ef3","productCode":"PRD00001"}', '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', '1', NOW(), 'admin', NOW(), 'admin', '勿动！！微信支付通道相关配置-新渠道');

#修改坐席客服id
update t_e_work_order wo set customer_service_id = (select id from sys_e_user where username = wo.customer_service_account and (oem_code is null or oem_code = '') and account_type = 2)
  where customer_service_id is null;
update t_e_work_order wo set customer_service_id = (select id from sys_e_user where username = wo.customer_service_account and oem_code = wo.oem_code and account_type = 2)
where customer_service_id is null;
#补传成果审核不通过通知短信模板
INSERT INTO `t_e_sms_template` ( `oem_code`, `template_type`, `template_content`, `status`, `add_time`, `add_user`, `update_time`, `update_user`, `remark`) VALUES ( 'YCS', '39', '您补传的#companyName#的交易成果审核未通过，请您尽快前往小程序查看原因并重新上传哦~', '1', '2021-08-23 16:43:41', 'admin', '2021-08-23 16:19:28', '', '');
