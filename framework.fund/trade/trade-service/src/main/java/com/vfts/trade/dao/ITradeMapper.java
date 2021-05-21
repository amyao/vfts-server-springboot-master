package com.vfts.trade.dao;

import com.vfts.trade.entity.HoldingEntity;
import com.vfts.trade.entity.OrderEntity;
import com.vfts.trade.entity.TradeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ITradeMapper interface
 * @author Axl
 */
@Mapper
public interface ITradeMapper {

    /**
     * listCurrentHoldingsByUuid
     * @param uuid uuid
     * @return list of holdings
     */
    List<HoldingEntity> listCurrentHoldingsByUuid(@Param("uuid") String uuid);

    /**
     * listTradeHistory
     * @param uuid uuid
     * @return list of trades
     */
    List<TradeEntity> listTradeHistory(@Param("uuid") String uuid);

    /**
     * updateAccountBalance
     * @param uuid uuid
     * @param newBalance newBalance
     * @return 1=true, 0=false
     */
    int updateAccountBalance(@Param("uuid") String uuid, @Param("newBalance") double newBalance);

    /**
     * getBalanceByUuid
     * @param uuid uuid
     * @return accountBalance
     */
    double getBalanceByUuid(@Param("uuid") String uuid);

    /**
     * getStartingBalanceByUuid
     * @param uuid uuid
     * @return startingBalance
     */
    double getStartingBalanceByUuid(@Param("uuid") String uuid);

    /**
     * getAssetsByUuid
     * @param uuid uuid
     * @return assets (= accountBalance + 所有持有基金的当前估值)
     */
    double getAssetsByUuid(@Param("uuid") String uuid);

    /**
     * getYesterdayYieldByUuid
     * @param uuid uuid
     * @return yesterdayYield
     */
    double getYesterdayYieldByUuid(@Param("uuid") String uuid);

    /**
     * getHoldingYieldByUuid
     * @param uuid uuid
     * @return holdingYield
     */
    double getHoldingYieldByUuid(@Param("uuid") String uuid);

    /**
     * getTotalYieldByUuid
     * @param uuid uuid
     * @return totalYield
     */
    double getTotalYieldByUuid(@Param("uuid") String uuid);

    /**
     * setYesterdayYield
     * @param uuid uuid
     * @param newYesterdayYield yesterdayYield
     * @return 1=true, 0=false
     */
    int setYesterdayYield(@Param("uuid") String uuid, @Param("newYesterdayYield") double newYesterdayYield);

    /**
     * setHoldingYield
     * @param uuid uuid
     * @param newHoldingYield holdingYield
     * @return 1=true, 0=false
     */
    int setHoldingYield(@Param("uuid") String uuid, @Param("newHoldingYield") double newHoldingYield);

    /**
     * setTotalYield
     * @param uuid uuid
     * @param newTotalYield totalYield
     * @return 1=true, 0=false
     */
    int setTotalYield(@Param("uuid") String uuid, @Param("newTotalYield") double newTotalYield);

    /**
     * setAssets
     * @param uuid uuid
     * @param newAssets assets
     * @return 1=true, 0=false
     */
    int setAssets(@Param("uuid") String uuid, @Param("newAssets") double newAssets);

    /**
     * listSelfListedByUuid
     * @param uuid uuid
     * @return list of fundIds
     */
    List<String> listSelfListedByUuid(@Param("uuid") String uuid);

    /**
     * create a trade record
     * @param tradeEntity trade object
     */
    void createTrade(TradeEntity tradeEntity);

    /**
     * addToSelfListed
     * @param uuid uuid
     * @param fundId fundId
     * @return 1=true, 0=false
     */
    int addToSelfListed(@Param("uuid") String uuid, @Param("fundId") String fundId);

    /**
     * deleteFromSelfListed
     * @param uuid uuid
     * @param fundId fundId
     * @return 1=true, 0=false
     */
    int deleteFromSelfListed(@Param("uuid") String uuid, @Param("fundId") String fundId);

    /**
     * addToCurrentHoldings: create a new holding record
     * @param newHolding holding object
     * @return 1=true, 0=false
     */
    int addToCurrentHoldings(HoldingEntity newHolding);

    /**
     * deleteFromCurrentHoldings
     * @param uuid uuid
     * @param fundId fundId
     * @return 1=true, 0=false
     */
    int deleteFromCurrentHoldings(@Param("uuid") String uuid, @Param("fundId") String fundId);

    /**
     * updateOneHolding to holding object
     * Properties in HoldingEntity that are possibly modified/updated by this function are:
     * double holdingShares,
     * Date startHoldingDate,
     * double transactionPrice.
     * @param updatedHolding holding object
     * @return 1=true, 0=false
     */
    int updateOneHolding(HoldingEntity updatedHolding);

    /**
     * checkIfInSelfListed
     * @param uuid uuid
     * @param fundId fundId
     * @return
     */
    int checkIfInSelfListed(@Param("uuid") String uuid, @Param("fundId") String fundId);

    /**
     * getTradeByOrderId
     * @param orderId orderId
     * @return trade object
     */
    TradeEntity getTradeByOrderId(@Param("orderId") String orderId);

    /**
     * listAllProcessingOrders
     * @return list of orders
     */
    List<OrderEntity> listAllProcessingOrders();

    /**
     * listProcessingSellingOrdersByFundId
     * @param fundId fundId
     * @return list of orders
     */
    List<OrderEntity> listProcessingSellingOrdersByFundId(@Param("fundId") String fundId);
}
