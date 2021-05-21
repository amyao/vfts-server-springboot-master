package com.vfts.trade.controller;

import com.alibaba.fastjson.JSON;
import com.vfts.trade.entity.AccountEntity;
import com.vfts.trade.entity.HoldingEntity;
import com.vfts.trade.entity.OrderEntity;
import com.vfts.trade.entity.TradeEntity;
import com.vfts.trade.iface.IOrderService;
import com.vfts.trade.iface.ITradeService;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * TradeController class
 * specifies and implements APIs to be used by the frontend
 * @author Axl
 */
@Slf4j
@RestController
@RequestMapping(value = "/uiapi/trade")
@CrossOrigin
public class TradeController {

    @Autowired
    private ITradeService tradeService;
    @Autowired
    private IOrderService orderService;

    /**
     * 展示账户中心的AccountEntity的部分
     * @param uuid uuid
     * @return account object
     * @throws Exception failed to find matching account
     */
    @RequestMapping(value = "/accountEntity",method = RequestMethod.POST)
    public ResponseEntity<AccountEntity> displayAccountHome(@RequestParam String uuid) throws Exception {
        try{
            log.info("try displaying account home");
            AccountEntity thisAccount = tradeService.displayAccountHome(uuid);
            if (thisAccount != null) {
                log.info("displaying account home success");
                return new ResponseEntity<>(thisAccount, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.info("failed to display account home: uuid does not match any existing account");
            throw new Exception("uuid does not match any existing account");
        }
        return new ResponseEntity<>((AccountEntity)null, HttpStatus.NO_CONTENT);
    }

    /**
     * 展示账户中心的CurrentHoldings部分
     * @param uuid uuid
     * @return list of all holdings
     * @throws Exception failed to find holding records
     */
    @RequestMapping(value="/currentHoldings", method = RequestMethod.POST)
    public ResponseEntity<List<HoldingEntity>> displayCurrentHoldings(@RequestParam String uuid) throws Exception {
        try {
            log.info("try displaying current holdings");
            List<HoldingEntity> currentHoldings = tradeService.listCurrentHoldingsByUuid(uuid);
            if (currentHoldings != null) {
                log.info("displaying current holdings success");
                return new ResponseEntity<>(currentHoldings, HttpStatus.OK);
            }
        } catch (Exception e){
            log.info("failed to display current holdings: uuid does not match any existing account");
            System.out.println(e.getMessage());
            throw new Exception("uuid does not match any existing account");
        }
        return new ResponseEntity<>((List<HoldingEntity>)null, HttpStatus.NO_CONTENT);
    }

    /**
     * 展示账户中心的orderHistory部分
     * @param uuid uuid
     * @return List<OrderEntity> list of all orders
     * @throws Exception failed to find order history
     */
    @RequestMapping(value = "/orderHistory",method = RequestMethod.POST)
    public ResponseEntity<List<OrderEntity>> displayOrderHistory(@RequestParam String uuid) throws Exception {
        try {
            log.info("try displaying order history");
            List<OrderEntity> orderHistory = orderService.listOrderHistory(uuid);
            if (orderHistory != null) {
                log.info("displaying order history success");
                return new ResponseEntity<>(orderHistory, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.info("failed to display order history: uuid does not match any existing account");
            throw new Exception("uuid does not match any existing account");
        }
        return new ResponseEntity<>((List<OrderEntity>)null, HttpStatus.NO_CONTENT);
    }

    /**
     * 展示账户中心的tradeHistory部分
     * @param uuid uuid
     * @return list of all trades
     * @throws Exception failed to find trade history
     */
    @RequestMapping(value = "/tradeHistory",method = RequestMethod.POST)
    public ResponseEntity<List<TradeEntity>> displayTradeHistory(@RequestParam String uuid) throws Exception {
        try {
            log.info("try displaying order history");
            List<TradeEntity> tradeHistory = tradeService.listTradeHistory(uuid);
            if (tradeHistory != null) {
                log.info("displaying trade history success");
                return new ResponseEntity<>(tradeHistory, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.info("failed to display trade history: uuid does not match any existing account");
            throw new Exception("uuid does not match any existing account");
        }
        return new ResponseEntity<>((List<TradeEntity>)null, HttpStatus.NO_CONTENT);
    }

    /**
     * 展示自选列表selfListed
     * @param uuid uuid
     * @return list of fundIds
     * @throws Exception failed to get selfListed
     */
    @RequestMapping(value = "/selfListed",method = RequestMethod.POST)
    public ResponseEntity<List<String>> displaySelfListed(@RequestParam String uuid) throws Exception{
        try {
            log.info("try displaying self listed");
            List<String> selfListed = tradeService.listSelfListedByUuid(uuid);
            if (selfListed != null) {
                log.info("displaying self listed success");
                return new ResponseEntity<>(selfListed, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.info("failed to display self listed: uuid does not match any existing account");
            throw new Exception("uuid does not match any existing account");
        }
        return new ResponseEntity<>((List<String>) null, HttpStatus.NO_CONTENT);
    }

    /**
     * 买入订单(orderType = 0)
     * @param uuid, fundId, buyingAmount, payPwd
     * @return String
     * @throws Exception failed to place a buying order
     */
    @RequestMapping(value = "/buyOrder",method = RequestMethod.POST)
    public ResponseEntity<String> buy(@RequestParam String uuid, @RequestParam String fundId, @RequestParam double buyingAmount, @RequestParam String payPwd) throws Exception {
        try {
            log.info("try placing an order to buy");
            OrderEntity newBuyOrder = orderService.buy(uuid, fundId, buyingAmount, payPwd);
            log.info("newBuyingOrder: orderId = {}", JSON.toJSONString(newBuyOrder));
            if (newBuyOrder != null) {
                log.info("placing a buying order success");
                return new ResponseEntity<>("Buying order placed successfully", HttpStatus.OK);
            }
        } catch (Exception e){
            log.info("failed to place an order to buy");
            throw new Exception("failed to place an order to buy");
        }
        return new ResponseEntity<>("Failed to place buying order", HttpStatus.NO_CONTENT);
    }

    /**
     * 卖出订单(orderType = 1)
     * @param uuid, fundId, sellingShares, payPwd
     * @return String
     * @throws Exception failed to place a selling order
     */
    @RequestMapping(value = "/sellOrder",method = RequestMethod.POST)
    public ResponseEntity<String> sell(@RequestParam String uuid, @RequestParam String fundId, @RequestParam double sellingShares, @RequestParam String payPwd) throws Exception {
        try {
            log.info("try placing an order to sell");
            OrderEntity newSellOrder = orderService.sell(uuid, fundId, sellingShares, payPwd);
            if (newSellOrder != null) {
                log.info("placing a selling order success");
                return new ResponseEntity<>("Selling order placed successfully", HttpStatus.OK);
            }
        } catch (Exception e){
            log.info("failed to place an order to sell");
            throw new Exception("failed to place an order to sell");
        }
        return new ResponseEntity<>("Failed to place selling order", HttpStatus.NO_CONTENT);
    }

    /**
     * withdrawOrder
     * @param orderId orderId
     * @return  String
     * @throws Exception failed to withdraw order
     */
    @RequestMapping(value = "/withdrawOrder",method = RequestMethod.POST)
    public ResponseEntity<String> withdrawOrder(@RequestParam String orderId) throws Exception {
        try {
            log.info("try withdrawing order #"+orderId);
            boolean withdrawalSuccess = orderService.withdrawOrder(orderId);
            if (withdrawalSuccess) {
                log.info("withdrawing an order success");
                return new ResponseEntity<>("Order #"+orderId+"withdrawn successfully", HttpStatus.OK);
            }
        } catch (Exception e){
            log.info(e.getMessage());
            throw new Exception("failed to withdraw order #"+orderId);
        }
        return new ResponseEntity<>("Failed to withdraw order #"+orderId, HttpStatus.NO_CONTENT);
    }

    /**
     * add to selfListed
     * @param uuid uuid
     * @param fundId fundId
     * @return String
     * @throws Exception failed to add to selfListed
     */
    @RequestMapping(value = "/addToSelfListed", method = RequestMethod.POST)
    public ResponseEntity<String> addToSelfListed(@RequestParam String uuid, @RequestParam String fundId) throws Exception {
        try {
            log.info("try adding to selfListed");
            if (tradeService.addToSelfListed(uuid, fundId)) {
                log.info("adding to selfListed success");
                return new ResponseEntity<>("Successfully added to selfListed", HttpStatus.OK);
            }
        } catch (Exception e) {
            log.info("failed to add to selfListed");
            throw new Exception("failed to add to selfListed");
        }
        return new ResponseEntity<>("Failed to add to selfListed", HttpStatus.NO_CONTENT);
    }

    //5. Delete from SelfListed
    // params: String uuid, int fundId

    /**
     * delete from selfListed
     * @param uuid uuid
     * @param fundId fundId
     * @return String
     * @throws Exception failed to delete from selfListed
     */
    @RequestMapping(value = "/deleteFromSelfListed", method = RequestMethod.POST)
    public ResponseEntity<String> deleteFromSelfListed(@RequestParam String uuid, @RequestParam String fundId) throws Exception {
        try {
            log.info("try deleting from selfListed");
            if (tradeService.deleteFromSelfListed(uuid, fundId)) {
                log.info("deleting from selfListed success");
                return new ResponseEntity<>("Successfully deleted from selfListed", HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new Exception("failed to delete from selfListed");
        }
        log.info("failed to delete from selfListed");
        return new ResponseEntity<>("Failed to delete from selfListed", HttpStatus.NO_CONTENT);
    }

    //7. get holding shares of a fund of an account

    /**
     * get the holding shares of a fund in an account
     * @param uuid uuid
     * @param fundId fundId
     * @return holdingShares
     * @throws Exception failed to get holdingShares of the matching holding records in account with uuid
     */
    @RequestMapping(value = "/getHoldingShares", method = RequestMethod.POST)
    public ResponseEntity<Double> getHoldingSharesByFundId(@RequestParam String uuid, @RequestParam String fundId) throws Exception {
        try {
            log.info("try getting holdingShares");
            double holdingShares = tradeService.getHoldingSharesByFundId(uuid, fundId);
            if (holdingShares > 0) {
                log.info("matching holdingEntity found");
                return new ResponseEntity<>(holdingShares, HttpStatus.OK);
            }
            else {
                log.info("not holding fundId "+fundId);
                return new ResponseEntity<>(-1.0, HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            throw new Exception("Failed to get holding shares: not currently holding fundId "+fundId);
        }
    }

    /**
     * check if a fund is in self-listed
     * @param uuid uuid
     * @param fundId fundId
     * @return 0=false, 1=true
     * @throws Exception failed to check if a fund is in selfListed
     */
    @RequestMapping(value = "/checkIfInSelfListed", method = RequestMethod.POST)
    public ResponseEntity<Integer> checkIfInSelfListed(@RequestParam String uuid, @RequestParam String fundId) throws Exception {
        try {
            log.info("try checking if fund is in selfListed");
            if (tradeService.checkIfInSelfListed(uuid, fundId)) {
                return new ResponseEntity<>(1, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.info("failed to check if fund is in selfListed");
            throw new Exception("Failed to check if fund is in selfListed");
        }
        return new ResponseEntity<>(0, HttpStatus.NO_CONTENT);
    }
}