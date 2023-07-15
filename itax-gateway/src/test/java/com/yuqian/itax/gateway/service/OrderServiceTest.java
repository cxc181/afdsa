package com.yuqian.itax.gateway.service;

import com.yuqian.itax.gateway.ItaxGatewayBootstrap;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.user.entity.dto.CompanyCancelApiDTO;
import com.yuqian.itax.user.entity.vo.CompanyCancelApiVO;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertThat;

@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ItaxGatewayBootstrap.class)
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    private static final String OEM_CODE = "YCS";

    @Test
    public void b_CompanyCancellation() {
        CompanyCancelApiDTO param = new CompanyCancelApiDTO();
        param.setCompanyId(217L);
        param.setRegPhone("18207409328");
        param.setExternalOrderNo("120000");
        CompanyCancelApiVO result = orderService.createComCancelOrder(OEM_CODE, param);
        assertThat(result.getOrderNo().length(), Matchers.greaterThan(0));
    }
}
