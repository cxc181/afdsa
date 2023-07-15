# 企业税单添加状态
alter table t_e_company_tax_bill modify column tax_bill_status int(2) COMMENT '税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税 6-待核对   7-待填报成本 8-待申报 9-已作废 10-待财务审核 11-审核不通过';

#企业税单变更记录
alter table t_e_company_tax_bill_change modify column tax_bill_status int(2) COMMENT '税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税 6-待核对   7-待填报成本 8-待申报 9-已作废 10-待财务审核 11-审核不通过';