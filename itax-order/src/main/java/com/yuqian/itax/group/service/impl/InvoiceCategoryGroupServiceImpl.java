package com.yuqian.itax.group.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.group.dao.InvoiceCategoryGroupMapper;
import com.yuqian.itax.group.entity.InvoiceCategoryGroupEntity;
import com.yuqian.itax.group.service.InvoiceCategoryGroupService;
import com.yuqian.itax.system.entity.query.InvoiceCategoryGroupQuery;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("invoiceCategoryGroupService")
public class InvoiceCategoryGroupServiceImpl extends BaseServiceImpl<InvoiceCategoryGroupEntity,InvoiceCategoryGroupMapper> implements InvoiceCategoryGroupService {

    @Override
    public List<InvoiceCategoryGroupEntity> getInvoiceCategoryGroup(InvoiceCategoryGroupQuery query) {
        return mapper.getInvoiceCategoryGroup(query);
    }
}

