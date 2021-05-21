package com.vfts.trade.service;

import com.vfts.trade.dao.ITradeMapper;
import com.vfts.trade.entity.AccountEntity;
import com.vfts.trade.entity.HoldingEntity;
import com.vfts.trade.entity.OrderEntity;
import com.vfts.trade.entity.TradeEntity;
import com.vfts.trade.iface.IOrderService;
import com.vfts.trade.iface.ITradeService;
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

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TradeServiceTest test class for TradeService
 * @author Axl
 */
@Slf4j
@SpringBootTest(classes = {com.vfts.trade.iface.ITradeService.class})
@ExtendWith(SpringExtension.class)
@SpringBootConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:trade_springMybatis.xml"})
public class TradeServiceTest {

    @Autowired
    private ITradeService tradeService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private ITradeMapper tradeMapper;

    @Test
    public void createTrade() throws Exception {
        String orderId = "e5deb52c-ce4c-45f2-846b-ac2d607bb6f0";
        try{
            log.info("try getting orderEntity");
            OrderEntity thisOrder = orderService.getOrderByOrderId(orderId);
            String uuid = thisOrder.getUuid();
            String fundId = thisOrder.getFundId();
            double tradePrice = tradeService.driverGetCurrentPrice(fundId);
            int orderType = thisOrder.getOrderType();
            double buyingAmount = thisOrder.getBuyingAmount();
            double sellingShares = thisOrder.getSellingShares();
            Date orderDate = thisOrder.getOrderDate();
            TradeEntity newTrade = new TradeEntity(uuid, orderId, fundId, tradePrice, orderType, buyingAmount, sellingShares, orderDate);
            log.info("try tradeMapper.createTrade");
            tradeMapper.createTrade(newTrade);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.info(e.getMessage());
            throw e;
        }

    }

    @Test
    public void displayAccountHome() throws Exception {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        AccountEntity thisAccount = tradeService.displayAccountHome(uuid);
        String result;
        if (thisAccount != null){
            result = "success";
            System.out.println("{uuid: "+thisAccount.getUuid()+"}, {balance: "+thisAccount.getAccountBalance()+
                    "}, {assets: "+thisAccount.getAssets()+"}, {totalYield: "+thisAccount.getTotalYield()+"}.");
        }
        else result = "failure";
        log.info("[TradeServiceTest] displayAccountHome>>>>>>{}", result);
        assertNotNull(thisAccount);
    }

    @Test
    public void listTradeHistory() throws Exception {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        List<TradeEntity> tradeHistory = tradeService.listTradeHistory(uuid);
        String result;
        if (tradeHistory != null){
            result = "success";
            for (TradeEntity tradeEntity : tradeHistory) {
                System.out.println(tradeEntity);
            }
        }
        else result = "failure";
        log.info("[TradeServiceTest] listTradeHistory>>>>>>{}", result);
        assertNotNull(tradeHistory);
    }

    @Test
    public void listCurrentHoldingsByUuid() throws Exception {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        List<HoldingEntity> holdingRecords = tradeService.listCurrentHoldingsByUuid(uuid);
        String result;
        if (holdingRecords != null){
            result = "success";
            for (HoldingEntity thisHolding : holdingRecords) {
                System.out.println(thisHolding);
            }
        }
        else result = "failure";
        log.info("[TradeServiceTest] listCurrentHoldingsByUuid>>>>>>{}", result);
        assertNotNull(holdingRecords);
    }

    @Test
    public void listSelfListedByUuid() throws Exception {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        List<String> selfListed = tradeService.listSelfListedByUuid(uuid);
        String result;
        if (selfListed != null){
            result = "success";
            System.out.print("selfListed: [");
            for (int i = 0; i < selfListed.size(); i ++){
                if (i != selfListed.size() - 1){
                    System.out.print(selfListed.get(i)+", ");
                }
                else System.out.println(selfListed.get(i)+"]");
            }
        }
        else result = "failure";
        log.info("[TradeServiceTest] listSelfListedByUuid>>>>>>{}", result);
        assertNotNull(selfListed);
    }

    @Test
    public void addToSelfListed() throws Exception {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        String fundId = "234";
        boolean result = tradeService.addToSelfListed(uuid, fundId);
        log.info("[TradeServiceTest] addToSelfListed>>>>>>{}", result);
        assertTrue(result);
    }

    @Test
    public void deleteFromSelfListed() throws Exception {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        String fundId = "234";
        boolean result = tradeService.deleteFromSelfListed(uuid, fundId);
        log.info("[TradeServiceTest] deleteFromSelfListed>>>>>>{}", result);
        assertTrue(result);
    }

    @Test
    public void getHoldingSharesByFundId() {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        String fundId = "123";
        double holdingShares = tradeService.getHoldingSharesByFundId(uuid, fundId);
        String result = "failure";
        if (holdingShares != -1.0) {
            result = "success";
            System.out.println("holdingShares = "+holdingShares);
        }
        log.info("[TradeServiceTest] getHoldingSharesByFundId>>>>>>{}", result);
        assertTrue(holdingShares >= 0);
    }

    @Test
    public void checkIfInSelfListed() {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        String fundId = "123";
        boolean result = tradeService.checkIfInSelfListed(uuid, fundId);
        log.info("[TradeServiceTest] checkIfInSelfListed>>>>>>{}", result);
        assertTrue(result);
    }

    @Test
    public void driverGetCurrentPrice() throws Exception {
        String fundId = "519702";
        double currentPrice = tradeService.driverGetCurrentPrice(fundId);
        log.info("[TradeServiceTest] driverGetCurrentPrice>>>>>>{}", currentPrice);
        assertTrue(currentPrice > 0);
    }

    @Test
    public void listProcessingSellingOrdersByFundId() throws Exception {
        String fundId = "001015";
        log.info("try tradeService.listProcessingSellingOrdersByFundId({})", fundId);
        List<OrderEntity> allProcessingSellingOrders = tradeService.listProcessingSellingOrdersByFundId(fundId);
        if (allProcessingSellingOrders != null) {
            int size = allProcessingSellingOrders.size();
            log.info(size + " processing selling order(s) retrieved on fundId "+fundId);
            for (OrderEntity oneProcessingSellingOrder : allProcessingSellingOrders) {
                System.out.println(oneProcessingSellingOrder);
            }
        }
        else{
            log.info("no processing selling orders found on fundId "+fundId);
        }
    }

    @Test
    public void driverUpdateYesterdayYield() throws Exception {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        log.info("try tradeService.driverUpdateYesterdayYield({})", uuid);
        tradeService.driverUpdateYesterdayYield(uuid);
    }

    @Test
    public void driverUpdateOrderToTrade() throws Exception {
        String orderId = "a3afad7a-2005-40ee-89d6-0e07049b167c";
        OrderEntity thisOrder = orderService.getOrderByOrderId(orderId);
        if (thisOrder!= null) {
            System.out.println(thisOrder);
            log.info("try tradeService.driverUpdateOrderToTrade()");
            tradeService.driverUpdateOrderToTrade(thisOrder);
            log.info("tradeService.driverUpdateOrderToTrade() completed");
        }
        else{
            log.info("failed to retrieve order with id="+orderId);
        }
    }

    @Test
    public void driverUpdateHoldingYield() throws Exception{
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        log.info("try tradeService.driverUpdateHoldingYield({})", uuid);
        tradeService.driverUpdateHoldingYield(uuid);
    }

    @Test
    public void driverUpdateAssets() throws Exception {
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        log.info("try tradeService.driverUpdateAssets({})", uuid);
        tradeService.driverUpdateAssets(uuid);
    }

    @Test
    public void driverUpdateTotalYield() throws Exception{
        String uuid = "a810a6a1-ced6-4f7e-8be3-2081edb5f1d1";
        log.info("try tradeService.driverUpdateTotalYield({})", uuid);
        tradeService.driverUpdateTotalYield(uuid);
    }
}

