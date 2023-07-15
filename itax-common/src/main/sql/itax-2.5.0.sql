#======V2.5.0版本数据脚本

#税费政策增加征收方式
ALTER TABLE `t_e_tax_policy`
ADD COLUMN `levy_way`  int(1) NULL DEFAULT 1 COMMENT '征收方式（1：核定征收率，2：核定应税所得率）' AFTER `company_type`;

#经营所得适用个人所得税税率表和初始化数据
CREATE TABLE `t_e_business_income_rule` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `level` bigint(11) NOT NULL COMMENT '级数',
  `min_amount` bigint(11) NOT NULL COMMENT '全年应纳税所得额最小值',
  `max_amount` bigint(11) NOT NULL COMMENT '全年应纳税所得额最大值',
  `rate` decimal(4,2) NOT NULL COMMENT '税率',
  `quick` bigint(11) NOT NULL COMMENT '速算扣除数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='经营所得适用个人所得税税率表';
INSERT INTO `t_e_business_income_rule` VALUES (1, 1, 0, 3000000, 0.05, 0);
INSERT INTO `t_e_business_income_rule` VALUES (2, 2, 3000000, 9000000, 0.10, 1500);
INSERT INTO `t_e_business_income_rule` VALUES (3, 3, 9000000, 30000000, 0.20, 10500);
INSERT INTO `t_e_business_income_rule` VALUES (4, 4, 30000000, 50000000, 0.30, 40500);
INSERT INTO `t_e_business_income_rule` VALUES (5, 5, 50000000, 9223372036854775807, 0.35, 65500);