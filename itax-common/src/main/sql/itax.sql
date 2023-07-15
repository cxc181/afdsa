#======V1.0.5版本数据脚本
#会员表
ALTER TABLE `t_e_member_account`
ADD COLUMN `employees_limit`  int(5) NULL COMMENT '钻石会员员工上限' AFTER `invite_code`;

#机构表
ALTER TABLE `t_e_oem`
ADD COLUMN `employees_limit`  int(5) NULL COMMENT '钻石会员员工上限' AFTER `is_open_promotion`;

#行业表
ALTER TABLE `t_e_industry`
ADD COLUMN `company_type`  int(1) NULL COMMENT '企业类型1-个体开户 2-个独开户 3-有限合伙 4-有限责任' AFTER `status`;

#常见问题
ALTER TABLE `t_e_common_problems`
ADD COLUMN `order_num`  int(2) NULL COMMENT '排序 1-99  数字越小越靠前' AFTER `oem_code`;

#用户菜单表
drop table if exists sys_r_user_menu;
create table sys_r_user_menu
(
   id                   bigint(11) not null auto_increment comment '主键id',
   user_id              bigint(11) comment '用户ID',
   oem_code             varchar(12) comment '机构编码',
   menu_id              bigint(11) comment '菜单ID',
   add_time             datetime comment '添加时间',
   add_user             varchar(32) comment '添加人',
   update_time          datetime comment '修改时间',
   update_user          varchar(32) comment '修改人',
   remark               varchar(64) comment '备注',
   primary key (id)
);
alter table sys_r_user_menu comment '用户菜单关系表';