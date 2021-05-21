package com.vfts.trade.service;

import com.vfts.trade.dao.IOrderMapper;
import com.vfts.trade.entity.AccountEntity;
import com.vfts.trade.entity.OrderEntity;
import com.vfts.trade.iface.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderServiceTest test class for OrderService
 * @author Axl
 */
@Slf4j
@SpringBootTest(classes = {com.vfts.trade.iface.IOrderService.class})
@ExtendWith(SpringExtension.class)
@SpringBootConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:trade_springMybatis.xml"})
public class OrderServiceTest {

    @Autowired
    private IOrderService orderService;
    @Autowired
    private IOrderMapper orderMapper;

    @Test
    public void createOrderTest() throws ParseException {
        OrderEntity newOrder = new OrderEntity("a810a6a1-ced6-4f7e-8be3-2081edb5f1d1","543210",1,200,0);
        log.info("try createOrder");
        String result = "success";
        try{
            orderMapper.createOrder(newOrder);
            log.info("createOrder completed");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = "failure";
            log.info("createOrder failed");
        }
        assertSame("success", result);
    }

    @Test
    public void listOrderHistory() throws Exception {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        List<OrderEntity> orderHistory;
        try{
            orderHistory = orderService.listOrderHistory(uuid);
            if (orderHistory != null){
                for (OrderEntity orderEntity : orderHistory) System.out.println(orderEntity);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            throw e;
        }
        String result;
        result = "failure";
        if (orderHistory != null) result = "success";
        log.info("[OrderServiceTest] listOrderHistory>>>>>>{}", result);
        assertNotNull(orderHistory);
    }

    @Test
    public void buy() throws ParseException {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        String fundId = "009275";
        double buyingAmount = 1500;
        String payPwd = "fghijk";
        OrderEntity newBuyingOrder = orderService.buy(uuid, fundId, buyingAmount, payPwd);
        String result;
        if (newBuyingOrder != null){
            result = "success";
            System.out.println(newBuyingOrder);
        }
        else result = "failure";
        log.info("[OrderServiceTest] buy>>>>>>{}", result);
        assertNotNull(newBuyingOrder);
    }

    @Test
    public void sell() throws ParseException {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        String fundId = "001015";
        double sellingShares = 2000;
        String payPwd = "fghijk";
        OrderEntity newSellingOrder = orderService.sell(uuid, fundId, sellingShares, payPwd);
        String result;
        if (newSellingOrder != null){
            result = "success";
            System.out.println(newSellingOrder);
        }
        else result = "failure";
        log.info("[OrderServiceTest] sell>>>>>>{}", result);
        assertNotNull(newSellingOrder);
    }

    @Test
    public void withdrawOrder() {
        String orderId = "8b1d2f1a-feb2-44b7-be57-2831d4262c56";
        boolean result = orderService.withdrawOrder(orderId);
        log.info("[OrderServiceTest] withdrawOrder>>>>>>{}", result);
        assertTrue(result);
    }

    @Test
    public void getAccountByUuid() {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        AccountEntity thisAccount = orderService.getAccountByUuid(uuid);
        String result;
        if (thisAccount != null){
            result = "success";
            System.out.println(thisAccount);
        }
        else result = "failure";
        log.info("[OrderServiceTest] getAccountByUuid>>>>>>{}", result);
        assertNotNull(thisAccount);
    }
}