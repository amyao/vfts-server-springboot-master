package com.vfts.trade.iface;

import com.vfts.trade.entity.AccountEntity;
import com.vfts.trade.entity.OrderEntity;
import com.vfts.trade.entity.TradeEntity;
import com.vfts.trade.entity.HoldingEntity;

import java.util.List;

/**
 * ITradeService interface
 * @author Axl
 */
public interface ITradeService {

    /**
     * 展示账户中心
     * @param uuid uuid
     * @return AccountEntity
     * @throws Exception failed to find the matching account object
     */
    AccountEntity displayAccountHome(String uuid) throws Exception;

    /**
     * 展示所有持仓
     * @param uuid uuid
     * @return List<HoldingEntity> list of all holdings
     * @throws Exception failed to find matching holding records
     */
    List<HoldingEntity> listCurrentHoldingsByUuid(String uuid) throws Exception;

    /**
     * 展示交易记录
     * @param uuid uuid
     * @return List<TradeEntity> list of trade history
     * @throws Exception failed to find matching trade records
     */
    List<TradeEntity> listTradeHistory(String uuid) throws Exception;

    /**
     * 添加到自选
     * @param uuid uuid
     * @param fundId fundId
     * @return boolean
     * @throws Exception failed to add to selfListed: already in selfListed OR invalid uuid&/fundId
     */
    boolean addToSelfListed(String uuid, String fundId) throws Exception;

    /**
     * 删除自选
     * @param uuid uuid
     * @param fundId fundId
     * @return boolean
     * @throws Exception failed to delete from selfListed
     */
    boolean deleteFromSelfListed(String uuid, String fundId) throws Exception;

    /**
     * 请求获取持有份额
     * @param uuid uuid
     * @param fundId fundId
     * @return double holdingShares
     */
    double getHoldingSharesByFundId(String uuid, String fundId);

    /**
     * 检查是否属于自选
     * @param uuid uuid
     * @param fundId fundId
     * @return  boolean
     */
    boolean checkIfInSelfListed(String uuid, String fundId);

    /**
     * 展示自选列表
     * @param uuid uuid
     * @return List<Integer> selfListed
     * @throws Exception failed to find matching selfListed
     */
    List<String> listSelfListedByUuid(String uuid) throws Exception;

    /**
     * returns a list of order objects whose status is "processing"
     * @return list of order objects
     * @throws Exception failed to find order entities whose status is "processing"
     */
    List<OrderEntity> listAllProcessingOrders() throws Exception;

    /**
     * returns a list of order objects whose status is "processing" AND whose fundId is fundId
     * @param fundId fundId
     * @return list of order objects
     * @throws Exception failed to find matching order entities
     */
    List<OrderEntity> listProcessingSellingOrdersByFundId(String fundId) throws Exception;

    /**
     * driver function to get current net worth of a fund by fundId
     * @param fundId fundId
     * @return netWorth
     * @throws Exception failed to get current price of a matching fund
     */
    double driverGetCurrentPrice(String fundId) throws Exception;

    /**
     * driver function to get yesterday's net worth of a fund by fundId
     * @param fundId fundId
     * @return yesterday's netWorth
     * @throws Exception failed to get yesterday's price of a matching fund
     */
    double driverGetYesterdayPrice(String fundId) throws Exception;

    /**
     * driver function to update yesterdayYield for account with uuid
     * @param uuid uuid
     * @throws Exception failed to update yesterdayYield for account with uuid
     */
    void driverUpdateYesterdayYield(String uuid) throws Exception;

    /**
     * driver function to update an order record to a corresponding trade record
     * @param orderEntity an order object
     * @throws Exception failed to update the order record to a trade record
     */
    void driverUpdateOrderToTrade(OrderEntity orderEntity) throws Exception;

    /**
     * driver function to update holdingYield for account with uuid
     * @param uuid uuid
     * @throws Exception failed to update holdingYield for account with uuid
     */
    void driverUpdateHoldingYield(String uuid) throws Exception;

    /**
     * driver function to update assets for account with uuid
     * @param uuid uuid
     * @throws Exception failed to update assets for account with uuid
     */
    void driverUpdateAssets(String uuid) throws Exception;

    /**
     * driver function to update totalYield for account with uuid
     * @param uuid uuid
     * @throws Exception failed to update totalYield for account with uuid
     */
    void driverUpdateTotalYield(String uuid) throws Exception;
}

