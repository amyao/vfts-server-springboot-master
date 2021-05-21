package com.vfts.trade.iface;

import com.vfts.trade.entity.OrderEntity;
import com.vfts.trade.entity.AccountEntity;

import java.text.ParseException;
import java.util.List;

/**
 * IOrderService interface
 * @author Axl
 */
public interface IOrderService {

    /**
     * returnAllRegisteredUuids
     * @return list of registered uuids
     * @throws Exception no registered uuids found
     */
    List<String> returnAllRegisteredUuids() throws Exception;

    /**
     * 展示订单记录
     * @param uuid uuid
     * @return List<OrderEntity> list of orders
     * @throws Exception no order history found
     */
    List<OrderEntity> listOrderHistory(String uuid) throws Exception;


    /**
     * 买入订单(orderType = 0)
     * @param uuid uuid
     * @param fundId fundId
     * @param buyingAmount amount of money to be spent on this order
     * @param payPwd payPwd
     * @return OrderEntity a buying order object
     * @throws ParseException while parsing Date
     */
    OrderEntity buy(String uuid, String fundId, double buyingAmount, String payPwd) throws ParseException;

    /**
     * 卖出订单(orderType = 1)
     * @param uuid uuid
     * @param fundId fundId
     * @param sellingShares amount of shares to sell in this order
     * @param payPwd payPwd
     * @return OrderEntity a selling order object
     * @throws ParseException while parsing Date
     */
    OrderEntity sell(String uuid, String fundId, double sellingShares, String payPwd) throws ParseException;

    /**
     * getOrderByOrderId
     * @param orderId orderId
     * @return an order object with the matching orderId
     */
    OrderEntity getOrderByOrderId(String orderId);

    /**
     * 撤销订单(orderStatus :processing" -> "withdrawn")
     * @param orderId orderId
     * @return boolean whether withdrawal is success
     */
    boolean withdrawOrder(String orderId);

    /**
     * getAccountByUuid
     * @param uuid uuid
     * @return AccountEntity a matching account object
     */
    AccountEntity getAccountByUuid(String uuid);
}