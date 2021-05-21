package com.vfts.trade.dao;

import com.vfts.trade.entity.AccountEntity;
import com.vfts.trade.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * IOrderMapper interface
 * @author Axl
 */
@Mapper
public interface IOrderMapper {

    /**
     * returnAllRegisteredUuids
     * @return list of uuids
     */
    List<String> returnAllRegisteredUuids();

    /**
     * createAccount
     * @param accountEntity account object
     */
    void createAccount(AccountEntity accountEntity);

    /**
     * updateAccount
     * @param accountEntity account object
     */
    void updateAccount(AccountEntity accountEntity);

    /**
     *  create a new order to buy or sell fund
     * @param orderEntity object object
     */
    void createOrder(OrderEntity orderEntity);

    /**
     * withdrawOrder
     * @param orderId orderId
     * @return 1=true, 0=false
     */
    int withdrawOrder(@Param("orderId") String orderId);

    /**
     * updateOrderStatus
     * @param orderId orderId
     * @param newStatus newStatus
     * @return 1=true, 0=false
     */
    int updateOrderStatus(@Param("orderId") String orderId, @Param("newStatus") String newStatus);

    /**
     * listOrderHistory
     * @param uuid uuid
     * @return list of orders
     */
    List<OrderEntity> listOrderHistory(@Param("uuid") String uuid);

    /**
     * getAccountByUuid
     * @param uuid uuid
     * @return account object
     */
    AccountEntity getAccountByUuid(@Param("uuid") String uuid);

    /**
     * listYieldHistory
     * @param uuid uuid
     * @param fundId fundId
     * @return list of yield records
     */
    List<Double> listYieldHistory(@Param("uuid") String uuid, @Param("fundId") String fundId);

    /**
     * updateYieldHistory
     * @param uuid uuid
     * @param fundId fundId
     * @param newYesterdayYield yesterday's yield of the fund with fundId in account with uuid
     * @param date yesterday's date
     * @return 1=true, 0=false
     */
    int updateYieldHistory(@Param("uuid") String uuid, @Param("fundId") String fundId, @Param("newYesterdayYield") double newYesterdayYield, @Param("date") Date date);

    /**
     * getOrderByOrderId
     * @param orderId orderId
     * @return order object
     */
    OrderEntity getOrderByOrderId(@Param("orderId") String orderId);

    /**
     * checkPayPwd
     * @param uuid uuid
     * @param payPwd payPwd
     * @return 1=true, 0=false
     */
    int checkPayPwd(@Param("uuid") String uuid, @Param("payPwd") String payPwd);
}