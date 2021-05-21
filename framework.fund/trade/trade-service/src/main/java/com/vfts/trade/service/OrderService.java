package com.vfts.trade.service;

import com.vfts.trade.dao.IOrderMapper;
import com.vfts.trade.dao.ITradeMapper;
import com.vfts.trade.entity.AccountEntity;
import com.vfts.trade.entity.HoldingEntity;
import com.vfts.trade.entity.OrderEntity;
import com.vfts.trade.iface.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

/**
 * OrderService Implementation class for IOrderService
 * @author Axl
 */
@Slf4j
@Service
public class OrderService implements IOrderService {

    @Autowired
    private IOrderMapper orderMapper;
    @Autowired
    private ITradeMapper tradeMapper;

    /**
     * returnAllRegisteredUuids
     * @return list of uuids
     * @throws Exception failed to find registered uuids
     */
    @Override
    public List<String> returnAllRegisteredUuids() throws Exception{
        try {
            log.info("try orderMapper.returnAllRegisteredUuids");
            List<String> allUuids = orderMapper.returnAllRegisteredUuids();
            if (allUuids != null){
                log.info("{} uuids retrieved", allUuids.size());
                return allUuids;
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new Exception(e.getMessage());
        }
        return null;
    }


    /**
     * 展示订单记录
     * @param uuid uuid
     * @return List<OrderEntity> list of orders
     */
    @Override
    public List<OrderEntity> listOrderHistory(String uuid) throws Exception {
        try {
            log.info("try orderMapper.listOrderHistory(String uuid)");
            List<OrderEntity> orderHistory = orderMapper.listOrderHistory(uuid);
            log.info("orderMapper.listOrderHistory completed");
            if (orderHistory != null) {
                return orderHistory;
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    /**
     * 买入订单(orderType = 0)
     * @param uuid, fundId, buyingAmount, payPwd
     * @return OrderEntity
     */
    @Override
    public OrderEntity buy(String uuid, String fundId, double buyingAmount, String payPwd) throws ParseException {
        log.info("trying userService.checkPayPwd");
        int payPwdMatches = orderMapper.checkPayPwd(uuid, payPwd);
        if (payPwdMatches == 1) {
            log.info("payPwd check passed");

            double accountBalance = tradeMapper.getBalanceByUuid(uuid);
            if (accountBalance < buyingAmount) {
                log.info("Failed to place an order to buy: buyingAmount exceeds boundary (accountBalance)");
                return null;
            }
            else{
                OrderEntity newOrder = new OrderEntity(uuid, fundId, 0, 0, buyingAmount);
                log.info("try orderMapper.createOrder()");
                orderMapper.createOrder(newOrder);
                log.info("orderMapper.createOrder() success");

                //update accountBalance
                log.info("update accountBalance");
                log.info("thisOrder.buyingAmount = "+buyingAmount);
                double oldBalance = tradeMapper.getBalanceByUuid(uuid);
                double newBalance = oldBalance - buyingAmount;
                log.info("newBalance = "+newBalance);
                tradeMapper.updateAccountBalance(uuid, newBalance);
                log.info("updating accountBalance success");
                return newOrder;
            }
        }
        else{
            log.info("Failed to place an order to buy: Wrong payment password.");
            return null;
        }
    }

    /**
     * 卖出订单(orderType = 1)
     * @param uuid, fundId, sellingShares, payPwd
     * @return OrderEntity
     */
    @Override
    public OrderEntity sell(String uuid, String fundId, double sellingShares, String payPwd) throws ParseException {
        //Constructor: OrderEntity(String uuid, int fundId, int orderType, double sellingShares, double buyingAmount)
        int payPwdMatches = orderMapper.checkPayPwd(uuid, payPwd);
        /*int payPwdMatches = 1;*/
        if (payPwdMatches == 1) {
            //check if sellingShares <= holdingShares
            List<HoldingEntity> currentHoldings = tradeMapper.listCurrentHoldingsByUuid(uuid);
            for (HoldingEntity oneHolding : currentHoldings) {
                if (oneHolding.getFundId().equals(fundId)) {
                    double holdingShares = oneHolding.getHoldingShares();
                    if (holdingShares < sellingShares) {
                        log.info("Failed to place an order to sell: sellingShares exceeds boundary (holdingShares)");
                        return null;
                    } else {
                        //found matching holding record
                        log.info("found matching holding record");
                        OrderEntity newOrder = new OrderEntity(uuid, fundId, 1, sellingShares, 0);
                        orderMapper.createOrder(newOrder);
                        log.info("new sellingOrder created");

                        //update holdingShares of corresponding HoldingEntity
                        log.info("update holdingShares of the matching holding record -- but never deleting it");
                        double newHoldingShares = oneHolding.getHoldingShares() - sellingShares;
                        log.info("newHoldingShares = " + newHoldingShares);
                        oneHolding.setHoldingShares(newHoldingShares);
                        //update the matching HoldingEntity record
                        log.info("try tradeMapper.updateOneHolding to push the change");
                        tradeMapper.updateOneHolding(oneHolding);
                        log.info("tradeMapper.updateOneHolding success");
                        log.info("returning newly created sellingOrder");
                        return newOrder;
                    }
                }
            }
        }
        else {
            log.info("Failed to place an order to sell: Wrong payment password.");
        }
        return null;
    }

    /**
     * getOrderByOrderId
     * @param orderId orderId
     * @return order oject
     */
    @Override
    public OrderEntity getOrderByOrderId(String orderId){
        return orderMapper.getOrderByOrderId(orderId);
    }

    /**
     * 撤销订单(orderStatus :processing" -> "withdrawn")
     * @param orderId orderId
     * @return boolean
     */
    @Override
    public boolean withdrawOrder(String orderId){
        OrderEntity thisOrder = getOrderByOrderId(orderId);
        if (thisOrder != null){
            int orderType = thisOrder.getOrderType();
            String uuid = thisOrder.getUuid();
            String fundId = thisOrder.getFundId();
            double buyingAmount = thisOrder.getBuyingAmount();
            double sellingShares = thisOrder.getSellingShares();
            if (orderType == 0){
                log.info("orderType = buy");
                //withdrawing a buyingOrder --> revert accountBalance
                double newBalance = tradeMapper.getBalanceByUuid(uuid) + buyingAmount;
                log.info("try reverting accountBalance");
                int resultUpdateAccountBalance = tradeMapper.updateAccountBalance(uuid,newBalance);
                if (resultUpdateAccountBalance == 0) {
                    log.info("failed to revert accountBalance to "+newBalance);
                    return false;
                }
                else {
                    log.info("revert accountBalance to "+newBalance+" success");
                }
            }
            else{
                //withdrawing a sellingOrder --> revert holdingShares
                log.info("orderType = sell");
                log.info("try tradeMapper.listCurrentHoldingsByUuid");
                List<HoldingEntity> currentHoldings = tradeMapper.listCurrentHoldingsByUuid(uuid);
                for (HoldingEntity oneHolding : currentHoldings) {
                    if (oneHolding.getFundId().equals(fundId)) {
                        log.info("matching holding record found");
                        double newHoldingShares = oneHolding.getHoldingShares() + sellingShares;
                        oneHolding.setHoldingShares(newHoldingShares);
                        //update the matching HoldingEntity record
                        log.info("try reverting holdingShares");
                        int resultUpdateHoldingShares = tradeMapper.updateOneHolding(oneHolding);
                        if (resultUpdateHoldingShares == 0) {
                            log.info("failed to revert holdingShares to " + newHoldingShares);
                            return false;
                        } else {
                            log.info("revert holdingShares to " + newHoldingShares + " success");
                        }
                    }
                }
            }
        }
        int resultSetWithdrawnStatus = orderMapper.withdrawOrder(orderId);
        if (resultSetWithdrawnStatus == 0){
            log.info("failed to set orderStatus to \"withdrawn\"");
            return false;
        }
        log.info("setting orderStatus to \"withdrawn\" success");
        return true;
    }

    /**
     * getAccountByUuid
     * @param uuid uuid
     * @return AccountEntity
     */
    @Override
    public AccountEntity getAccountByUuid(String uuid){
        return orderMapper.getAccountByUuid(uuid);
    }
}
