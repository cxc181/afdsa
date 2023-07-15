#开票订单变更记录表
ALTER TABLE `t_e_invoice_order_change_record`
ADD INDEX `idx_orderNo` (`order_no`) ;

#企业注册订单变更记录表
ALTER TABLE `t_e_register_order_change_record`
ADD INDEX `idx_orderNo` (`order_no`) ;

#产品表
ALTER TABLE `t_e_product`
MODIFY COLUMN `prod_type`  int(2) NOT NULL COMMENT '产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-黄金会员（废弃） 10-钻石会员 （废弃）11-个体注销 12-个独注销 13-有限合伙注销 14-有限责任注销 15-公户申请和托管 16-个体托管费续费 17-对公户年费续费 18-个独托管费续费 19-有限合伙托管费续费 20-有限责任托管费续费' AFTER `prod_code`;

#优惠卷表
ALTER TABLE `t_e_coupons`
MODIFY COLUMN `usable_range`  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可用范围  1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户' AFTER `face_amount`;


#园区表
ALTER TABLE `t_e_park`
ADD COLUMN `affiliating_area` INT(1) NULL   COMMENT '所属地区 1-江西' AFTER `official_seal_img`;
