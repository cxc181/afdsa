# 园区表
ALTER TABLE `t_e_park`
  CHANGE `income_levy_type` `income_levy_type` INT(1) DEFAULT 1  NULL   COMMENT '所得税征收方式 1-查账征收 2-核定征收（作废）';

# 字典表
INSERT INTO `sys_e_dictionary` (`dict_code`, `dict_value`, `dict_desc`, `add_time`, `add_user`) VALUES ('payable_allow_error_value', '0.01', '批量报税应缴允许误差', NOW(), 'admin');

# 企业表
ALTER TABLE `t_e_member_company`
  CHANGE `status` `status` INT(1) DEFAULT 1  NOT NULL   COMMENT '状态 1-正常 2-禁用 4-已税务注销 5-注销中 6-已工商注销';

# 企业变更记录表
ALTER TABLE `t_e_member_company_change`
  CHANGE `status` `status` INT(1) DEFAULT 1  NOT NULL   COMMENT '状态 1-正常 2-禁用 4-已税务注销 5-注销中 6-已工商注销';

-- 历史已注销企业状态更新为已工商注销
UPDATE t_e_member_company SET `status` = 6 WHERE `status` = 4;
UPDATE t_e_member_company_change SET `status` = 6 WHERE `status` = 4;

# 订单表
ALTER TABLE `t_e_order`
  CHANGE `order_status` `order_status` INT(2) NOT NULL   COMMENT '订单状态：工商注册(0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，10-待设立登记、11-待提交签名 12-签名待确认 13-待创建),\r\n开票(0-待创建,1-待付款,2-待审核,3-出票中,4-待发货,5-出库中,6-待收货,7-已签收,8-已取消,9-待出款,10-审核未通过 11-待财务审核),\r\n会员升级( 0-待支付,1-支付中,2-财务审核,3-已完成,4-已取消), \r\n充值提现、对公户提现、补税，退税(0-待提交,1-支付、提现中,2-支付、提现完成,3-支付、提现失败,4-订单过期,5-已取消,6-待财务审核,7-财务审核失败),工商注销(0-待付款,1-注销处理中,2-税务注销成功,3-已取消 4-税单待处理 5-工商注销成功),证件申请( 0-待付款 1-待发货,2-出库中,3-待签收,4-已签收,5-已取消),公户申请（0-待付款,1-等待预约,2-已完成,3-已取消）消费开票（0-待出票 1-出票中 2-已完成 3-出票失败 4-待发货 5-待签收）托管费续费、对公户续费（0-待支付、1-支付中、2-已完成、3-已取消）';

# 企业注销订单变更记录表
ALTER TABLE `t_e_company_cancel_order_change_record`
  CHANGE `order_status` `order_status` INT(2) NULL   COMMENT '订单状态 0-待付款,1-注销处理中,2-税务注销成功,3-已取消 4-税单待处理 5-工商注销成功';