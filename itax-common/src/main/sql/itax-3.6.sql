#园区禁用字
CREATE TABLE `t_e_park_disable_word` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `park_id` bigint(11) DEFAULT NULL COMMENT '园区id',
  `disable_word` varchar(64) DEFAULT NULL COMMENT '禁用字',
  `add_user` varchar(64) DEFAULT NULL COMMENT '添加人',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `update_user` varchar(64) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) COMMENT='园区禁用字';

#园区政策配置
ALTER TABLE `t_e_tax_rules_config`
ADD COLUMN `is_open_pp`  int(1) NULL DEFAULT 1 COMMENT '是否允许开普票 0-不允许 1-允许' AFTER `local_education_surcharge_rate`,
ADD COLUMN `is_open_zp`  int(1) NULL DEFAULT 0 COMMENT '是否允许开专票 0-不允许 1-允许' AFTER `is_open_pp`;

#园区表
ALTER TABLE `t_e_park`
ADD COLUMN `special_considerations`  varchar(256) NULL COMMENT '特殊事项说明' AFTER `process_desc`;

#消费开票订单表
ALTER TABLE `t_e_consumption_invoice_order`
ADD COLUMN `courier_number`  varchar(64) NULL COMMENT '快递单号' AFTER `remark`,
ADD COLUMN `courier_company_name`  varchar(64) NULL COMMENT '快递公司名称' AFTER `courier_number`,
ADD COLUMN `general_taxpayer_qualification`  varchar(256) NULL COMMENT '一般纳税人资质' AFTER `courier_company_name`;

#消费开票订单变更表
ALTER TABLE `t_e_consumption_invoice_order_change`
ADD COLUMN `courier_number`  varchar(64) NULL COMMENT '快递单号' AFTER `remark`,
ADD COLUMN `courier_company_name`  varchar(64) NULL COMMENT '快递公司名称' AFTER `courier_number`,
ADD COLUMN `general_taxpayer_qualification`  varchar(256) NULL COMMENT '一般纳税人资质' AFTER `courier_company_name`;

#订单主表订单类型新增枚举
ALTER TABLE `t_e_order`
MODIFY COLUMN `order_status`  int(2) NOT NULL COMMENT '订单状态：工商注册(0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，10-待设立登记、11-待提交签名、12-签名待确认),\r\n开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过),\r\n会员升级( 0-待支付,1-支付中,2-财务审核,3-已完成,4-已取消), \r\n充值提现、对公户提现、补税，退税(0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败),工商注销(0-待付款,1-注销处理中,2-注销成功,3-已取消),证件申请( 0-待付款 1-待发货,2-出库中,3-待签收,4-已签收,5-已取消),公户申请（0-待付款,1-等待预约,2-已完成,3-已取消）消费开票（0-待出票 1-出票中 2-已完成 3-出票失败 4-待发货 5-待签收）托管费续费、对公户续费（0-待支付、1-支付中、2-已完成、3-已取消）' AFTER `order_type`;

#企业税单第三季度增值税率历史数据处理
UPDATE t_e_company_tax_bill SET vat_rate = vat_rate*100 WHERE vat_rate > 0 AND vat_rate < 1 AND tax_bill_year = 2021 AND tax_bill_seasonal = 3;