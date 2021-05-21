package com.vfts.trade.service;

import com.vfts.trade.dao.IOrderMapper;
import com.vfts.trade.dao.ITradeMapper;
import com.vfts.trade.entity.AccountEntity;
import com.vfts.trade.entity.HoldingEntity;
import com.vfts.trade.entity.OrderEntity;
import com.vfts.trade.entity.TradeEntity;
import com.vfts.trade.iface.ITradeService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * TradeService Implementation class for ITradeService
 * @author Axl
 */
@Slf4j
@Service
public class TradeService implements ITradeService {

    @Autowired
    private ITradeMapper tradeMapper;
    @Autowired
    private IOrderMapper orderMapper;
    @Autowired
    private HttpClient httpClient;

    /**
     * 展示账户中心
     * @param uuid uuid
     * @return AccountEntity
     * @throws Exception failed to get account object
     */
    @Override
    public AccountEntity displayAccountHome(String uuid) throws Exception {
        try{
            AccountEntity thisAccount = orderMapper.getAccountByUuid(uuid);
            if (thisAccount != null) {
                return thisAccount;
            }
        } catch (Exception e) {
            throw new Exception("uuid does not match any existing account");
        }
        return null;
    }

    /**
     * 展示交易记录
     * @param uuid uuid
     * @return List<TradeEntity> list of trades
     * @throws Exception failed to get trade history
     */
    @Override
    public List<TradeEntity> listTradeHistory(String uuid) throws Exception {
        try {
            List<TradeEntity> tradeHistory = tradeMapper.listTradeHistory(uuid);
            if (tradeHistory != null) {
                return tradeHistory;
            }
        } catch (Exception e) {
            throw new Exception("no trade history found");
        }
        return null;
    }

    /**
     * 展示所有持仓
     * @param uuid uuid
     * @return List<HoldingEntity> list of holdings
     * @throws Exception failed to get holding records
     */
    @Override
    public List<HoldingEntity> listCurrentHoldingsByUuid(String uuid) throws Exception {
        try {
            List<HoldingEntity> currentHoldings = tradeMapper.listCurrentHoldingsByUuid(uuid);
            if (currentHoldings != null) {
                return currentHoldings;
            }
        } catch (Exception e) {
            throw new Exception("no current holdings found");
        }
        return null;
    }

    /**
     * 展示自选列表
     * @param uuid uuid
     * @return List<Integer> selfListed
     * @throws Exception failed to get selfListed
     */
    @Override
    public List<String> listSelfListedByUuid(String uuid) throws Exception {
        try {
            List<String> selfListed = tradeMapper.listSelfListedByUuid(uuid);
            if (selfListed != null) {
                return selfListed;
            }
        } catch (Exception e) {
            throw new Exception("no self listed found");
        }
        return null;
    }

    /**
     * 添加到自选
     * @param uuid uuid
     * @param fundId fundId
     * @return boolean
     * @throws Exception failed to add to selfListed
     */
    @Override
    public boolean addToSelfListed(String uuid, String fundId) throws Exception {
        try {
            int alreadySelfListed = tradeMapper.checkIfInSelfListed(uuid, fundId);
            if (alreadySelfListed == 0){
                return (tradeMapper.addToSelfListed(uuid, fundId) == 1);
            }
        } catch (Exception e) {
            throw new Exception("Failed to add to self listed");
        }
        return false;
    }

    /**
     * 删除自选
     * @param uuid uuid
     * @param fundId fundId
     * @return boolean
     * @throws Exception failed to delete from selfListed
     */
    @Override
    public boolean deleteFromSelfListed(String uuid, String fundId) throws Exception {
        try {
            log.info("try tradeMapper.deleteFromSelfListed");
            int result = tradeMapper.deleteFromSelfListed(uuid, fundId);
            if (result == 1) {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception ("Failed to delete from selfListed: no matching selfListed found");
        }
        return false;
    }

    /**
     * 请求获取持有份额
     * @param uuid uuid
     * @param fundId fundId
     * @return double holdingShares; -1.0 if not found
     */
    @Override
    public double getHoldingSharesByFundId(String uuid, String fundId){
        List<HoldingEntity> allHoldings = tradeMapper.listCurrentHoldingsByUuid(uuid);
        for (HoldingEntity oneHolding : allHoldings) {
            if (oneHolding.getFundId().equals(fundId)) {
                return oneHolding.getHoldingShares();
            }
        }
        //if not found, return -1 as error code
        return -1.0;
    }

    /**
     * 检查是否属于自选
     * @param uuid uuid
     * @param fundId fundId
     * @return  boolean
     */
    @Override
    public boolean checkIfInSelfListed(String uuid, String fundId) {
        List<String> thisSelfListed = tradeMapper.listSelfListedByUuid(uuid);
        for (String oneFundId : thisSelfListed) {
            if (oneFundId.equals(fundId)) {
                return true;
            }
        }
        return false;
    }

    //***************************  SCHEDULED FUNCTIONS and DRIVER FUNCTIONS  ******************************//

    private static final String CURRENT_PRICE_API_URL = "https://api.doctorxiong.club/v1/fund?code=";
    private static final String PRICE_API_TOKEN = "&token=39CknoZcrT";

    /**
     * utility function to get current price of a fund
     * @param  fundId fundId
     * @return double currentPrice
     * @throws Exception failed to get currentPrice
     */
    private double getCurrentPrice(String fundId) throws Exception {
        String url = CURRENT_PRICE_API_URL + fundId + PRICE_API_TOKEN;
        try{
            String JsonStr = httpClient.client(url);
            JSONObject jsonObject1 = JSONObject.fromObject(JsonStr);
            String dataStr = jsonObject1.getString("data");
            int lengthDataStr = dataStr.length();
            dataStr = dataStr.substring(1, lengthDataStr - 1);
            JSONObject jsonObject2 = JSONObject.fromObject(dataStr);
            String currentPriceStr = jsonObject2.getString("netWorth");
            return Double.parseDouble(currentPriceStr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("failed to get current net price for fundId "+fundId+" from url "+ url);
        }
    }

    /**
     * driverGetCurrentPrice
     * @param fundId fundId
     * @return today's netWorth
     * @throws Exception failed to get currentPrice
     */
    @Override
    public double driverGetCurrentPrice(String fundId) throws Exception {
        double currentPrice;
        try {
            currentPrice = getCurrentPrice(fundId);
        } catch (Exception e){
            System.out.println(e.getMessage());
            log.info(e.getMessage());
            throw e;
        }
        return currentPrice;
    }

    /**
     * getYesterdayPrice
     * @param fundId fundId
     * @return yesterday's netWorth
     * @throws Exception failed to get yesterday's price
     */
    private double getYesterdayPrice(String fundId) throws Exception {
        String url = CURRENT_PRICE_API_URL + fundId + PRICE_API_TOKEN;
        try{
            String JsonStr = httpClient.client(url);
            JSONObject jsonObject1 = JSONObject.fromObject(JsonStr);
            String dataStr = jsonObject1.getString("data");
            int lengthDataStr = dataStr.length();
            dataStr = dataStr.substring(1, lengthDataStr - 1);
            JSONObject jsonObject2 = JSONObject.fromObject(dataStr);
            String currentPriceStr = jsonObject2.getString("netWorth");
            double currentPrice = Double.parseDouble(currentPriceStr);
            String dayGrowthPercStr = jsonObject2.getString("dayGrowth");
            double dayGrowthPerc = Double.parseDouble(dayGrowthPercStr);
            return currentPrice / (1 + dayGrowthPerc * 0.01);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("failed to get yesterday's net price for fundId "+fundId+" from url "+ url);
        }
    }

    /**
     * driverGetYesterdayPrice
     * @param fundId fundId
     * @return yesterdayPrice
     * @throws Exception failed to get yesterday's netWorth
     */
    @Override
    public double driverGetYesterdayPrice(String fundId) throws Exception {
        double yesterdayPrice;
        try {
            yesterdayPrice = getYesterdayPrice(fundId);
        } catch (Exception e){
            System.out.println(e.getMessage());
            log.info(e.getMessage());
            throw e;
        }
        return yesterdayPrice;
    }

    /**
     * listAllProcessingOrders
     * @return list of orders
     * @throws Exception failed to list all orders whose status is "processing"
     */
    @Override
    public List<OrderEntity> listAllProcessingOrders() throws Exception{
        List<OrderEntity> allProcessingOrders;
        try{
            log.info("try tradeMapper.listAllProcessingOrders");
            allProcessingOrders = tradeMapper.listAllProcessingOrders();
            if (allProcessingOrders != null){
                log.info("allProcessingOrders successfully retrieved");
            }
        } catch (Exception e) {
            log.info("failed to retrieve allProcessingOrders");
            System.out.println(e.getMessage());
            throw new Exception(e.getMessage());
        }
        return allProcessingOrders;
    }

    /**
     * listProcessingSellingOrdersByFundId
     * returns a list of order objects whose status is "processing" AND whose fundId is fundId
     * @param fundId fundId
     * @return list of order objects
     * @throws Exception failed to find matching order entities
     */
    @Override
    public List<OrderEntity> listProcessingSellingOrdersByFundId(String fundId) throws Exception {
        List<OrderEntity> allProcessingSellingOrders;
        try{
            log.info("try tradeMapper.listAllProcessingOrders where fundId = "+fundId);
            allProcessingSellingOrders = tradeMapper.listProcessingSellingOrdersByFundId(fundId);
            if (allProcessingSellingOrders != null){
                log.info("allProcessingSellingOrders where fundId = "+fundId+" successfully retrieved");
            }
        } catch (Exception e) {
            log.info("failed to retrieve allProcessingSellingOrders where fundId = "+fundId);
            System.out.println(e.getMessage());
            throw new Exception(e.getMessage());
        }
        return allProcessingSellingOrders;
    }

    /**
     *
     * @param uuid uuid
     * @throws Exception failed to update yesterdayYield
     */
    @Override
    public void driverUpdateYesterdayYield(String uuid) throws Exception{
        try {
            updateYesterdayYield(uuid);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    //**********************    FOLLOWING INDEXED FUNCTIONS IMPLEMENT SCHEDULED TASKS    **********************//

    /**
     * 1. 更新每一条持有记录的List<Double> yieldHistory = (currentPrice - yesterdayPrice) * yesterdayHoldingShares
     * 更新账户的昨日收益 = sum(yieldHistory[end]) for all holdings
     * @param uuid uuid
     * @throws Exception failed to update yesterdayYield
     */
    private void updateYesterdayYield(String uuid) throws Exception {
        try {
            log.info("try tradeMapper.listCurrentHoldingsByUuid({})",uuid);
            List<HoldingEntity> currentHoldings = tradeMapper.listCurrentHoldingsByUuid(uuid);
            double newYesterdayYield = 0;
            double oneYesterdayYield;
            for (HoldingEntity thisHolding : currentHoldings) {
                String fundId = thisHolding.getFundId();
                double thisCurrentPrice = getCurrentPrice(fundId);
                double thisYesterdayPrice = getYesterdayPrice(fundId);
                //compute yesterday's holdingShares
                double yesterdayHoldingShares = thisHolding.getHoldingShares();
                List<OrderEntity> processingSellingOrders = tradeMapper.listProcessingSellingOrdersByFundId(fundId);
                if (processingSellingOrders != null) {
                    int size = processingSellingOrders.size();
                    log.info("{} processingSellingOrders retrieved on fundId {}", size, fundId);
                    for (OrderEntity oneProcessingSellingOrder : processingSellingOrders) {
                        System.out.println(oneProcessingSellingOrder);
                        yesterdayHoldingShares += oneProcessingSellingOrder.getSellingShares();
                    }
                    log.info("yesterdayHoldingShares = {} for fundId {}", yesterdayHoldingShares, fundId);
                } else {
                    log.info("no processingSellingOrders found on fundId {}", fundId);
                }
                //compute yesterdayYield in account_entity
                oneYesterdayYield = (thisCurrentPrice - thisYesterdayPrice) * yesterdayHoldingShares;
                log.info("oneYesterdayYield = {} for fundId {}", oneYesterdayYield, fundId);
                newYesterdayYield += oneYesterdayYield;
                log.info("newYesterdayYield is incremented to {} after adding oneYesterdayYield = {}", newYesterdayYield, oneYesterdayYield);
                //update yieldRecord and date in yieldHistory for this one holding
                Date date = new Date();
                log.info("try orderMapper.updateYieldHistory({}:{})", date, newYesterdayYield);
                int retUpdateYieldHistory = orderMapper.updateYieldHistory(uuid, fundId, newYesterdayYield, date);
                if (retUpdateYieldHistory == 1) {
                    log.info("Successfully updated yieldHistory with {}:{}", date, newYesterdayYield);
                } else {
                    log.info("Failed to update yieldHistory with {}:{}", date, newYesterdayYield);
                }
            }
            //update yesterdayYield
            log.info("try tradeMapper.setYesterdayYield({}, {})", uuid, newYesterdayYield);
            int retSetYesterdayYield = tradeMapper.setYesterdayYield(uuid, newYesterdayYield);
            if (retSetYesterdayYield == 1) {
                log.info("Successfully updated yesterdayYield for account with uuid " + uuid + " to " + newYesterdayYield);
            } else {
                log.info("Failed to update yesterdayYield for account with uuid " + uuid + " to " + newYesterdayYield);
            }
        } catch (Exception e) {
            throw new Exception("failed to update yesterdayYield for account with uuid " + uuid);
        }
    }

    @Override
    public void driverUpdateOrderToTrade(OrderEntity thisOrder) throws Exception{
        try {
            updateOrderToTrade(thisOrder);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    final static String SUCCESS_STATUS = "success";
    /**
     * 2. 订单转交易
     * @param thisOrder order object
     * @throws Exception failed to update order to trade
     */
    private void updateOrderToTrade(OrderEntity thisOrder) throws Exception {
        //Constructor: TradeEntity(String uuid, String orderId, String fundId, double tradePrice, int orderType, double buyingAmount, double sellingShares, Date orderDate)
        log.info("in tradeService.updateOrderToTrade");
        try {
            int orderType = thisOrder.getOrderType();
            String uuid = thisOrder.getUuid();
            String orderId = thisOrder.getOrderId();
            String fundId = thisOrder.getFundId();
            double tradePrice = getCurrentPrice(fundId);

            /*buying order*/
            if (orderType == 0){
                log.info("orderType==buying");
                TradeEntity newTrade = new TradeEntity(uuid, thisOrder.getOrderId(), thisOrder.getFundId(), tradePrice, 0, thisOrder.getBuyingAmount(), 0, thisOrder.getOrderDate());
                log.info("create a new trade record");
                tradeMapper.createTrade(newTrade);
                /*update the record in currentHoldings of the Account accordingly*/
                List<HoldingEntity> currentHoldings = tradeMapper.listCurrentHoldingsByUuid(uuid);
                for (int i = 0; i < currentHoldings.size(); i ++){
                    if (currentHoldings.get(i).getFundId().equals(fundId)){
                        //matching holding record found
                        log.info("matching holding record found -- updating the fund");
                        HoldingEntity matchingHolding = currentHoldings.get(i);
                        //update the holdingShares
                        log.info("update the holdingShares");
                        double oldHoldingShares = matchingHolding.getHoldingShares();
                        double newHoldingShares = oldHoldingShares + newTrade.getTradeShares();
                        matchingHolding.setHoldingShares(newHoldingShares);
                        //update the transactionPrice
                        log.info("update the transactionPrice");
                        double oldTransactionPrice = matchingHolding.getTransactionPrice();
                        double buyingShares = newTrade.getTradeShares();
                        double newTransactionPrice = (oldTransactionPrice * oldHoldingShares + tradePrice * buyingShares) / (oldHoldingShares + buyingShares);
                        matchingHolding.setTransactionPrice(newTransactionPrice);
                        //update the matching HoldingEntity record
                        log.info("update the matching HoldingEntity record");
                        tradeMapper.updateOneHolding(matchingHolding);
                        break;
                    }
                    /*no matching holding found-> insert new holding record*/
                    if (i == currentHoldings.size() - 1){
                        log.info("first time buying this fund -- creating new holding record");
                        //Constructor: HoldingEntity(String uuid, String fundId, double holdingShares, String startHoldingDate)
                        HoldingEntity newHoldingRecord = new HoldingEntity(uuid, fundId, newTrade.getTradeShares(), newTrade.getTradeDate(), tradePrice);
                        tradeMapper.addToCurrentHoldings(newHoldingRecord);
                    }
                }
                //update orderStatus
                log.info("update orderStatus");
                int retUpdateOrderStatus = orderMapper.updateOrderStatus(orderId, SUCCESS_STATUS);
                if (retUpdateOrderStatus == 1){
                    log.info("successfully updated orderStatus");
                }
                else {
                    log.info("failed to update orderStatus");
                }
            }
            else{//selling order
                log.info("orderType==selling");
                TradeEntity newTrade = new TradeEntity(uuid, thisOrder.getOrderId(), thisOrder.getFundId(), tradePrice, 1, 0, thisOrder.getSellingShares(), thisOrder.getOrderDate());
                //add a new record to DB of this trade
                log.info("create a new trade record");
                tradeMapper.createTrade(newTrade);
                //update currentHoldings ONLY IF all shares are sold --> delete the holding record
                List<HoldingEntity> currentHoldings = tradeMapper.listCurrentHoldingsByUuid(uuid);
                for (HoldingEntity oneHolding : currentHoldings) {
                    if (oneHolding.getFundId().equals(fundId)) {
                        //found matching holding record
                        log.info("found matching holding record");
                        /*double newHoldingShares = matchingHolding.getHoldingShares() - newTrade.getTradeShares();*/
                        if (oneHolding.getHoldingShares() == 0) {
                            //delete holding
                            log.info("selling all holding shares CONFIRMED -- deleting holding record");
                            tradeMapper.deleteFromCurrentHoldings(uuid, fundId);
                        }
                        break;
                    }
                }
                //update orderStatus
                log.info("update orderStatus");
                orderMapper.updateOrderStatus(orderId, SUCCESS_STATUS);
                //update accountBalance
                AccountEntity thisAccount = orderMapper.getAccountByUuid(uuid);
                log.info("update accountBalance");
                double newBalance = thisAccount.getAccountBalance() + thisOrder.getSellingShares() * newTrade.getTradePrice();
                int retUpdateAccountBalance = tradeMapper.updateAccountBalance(uuid, newBalance);
                if (retUpdateAccountBalance == 1){
                    log.info("successfully updated accountBalance to {}",newBalance);
                }
                else {
                    log.info("failed to update accountBalance to {}",newBalance);
                }
            }
        } catch (Exception e) {
            throw new Exception("Failed to update order to trade");
        }
    }

    @Override
    public void driverUpdateHoldingYield(String uuid) throws Exception{
        try {
            updateHoldingYield(uuid);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    /**
     * 3. 更新持有收益 = holdingShares * (currentPrice - holdingEntity.transactionPrice) for all holdings
     * @param uuid uuid
     * @throws Exception failed to update holdingYield
     */
    private void updateHoldingYield(String uuid) throws Exception {
        try {
            List<HoldingEntity> currentHoldings = tradeMapper.listCurrentHoldingsByUuid(uuid);
            double newHoldingYield = 0;
            double oneHoldingYield;
            if (currentHoldings != null){
                int size = currentHoldings.size();
                log.info("{} holding record(s) found", size);
                for (HoldingEntity thisHolding : currentHoldings) {
                    System.out.println(thisHolding);
                    String thisFundId = thisHolding.getFundId();
                    double thisTransactionPrice = thisHolding.getTransactionPrice();
                    double thisCurrentPrice = getCurrentPrice(thisFundId);
                    oneHoldingYield = thisHolding.getHoldingShares() * (thisCurrentPrice - thisTransactionPrice);
                    log.info("currentPrice = {}, transactionPrice = {},  oneHoldingYield = {}", thisCurrentPrice, thisTransactionPrice, oneHoldingYield);
                    newHoldingYield += oneHoldingYield;
                    log.info("newHoldingYield = {}", newHoldingYield);
                }
                int ret = tradeMapper.setHoldingYield(uuid, newHoldingYield);
                if (ret == 1){
                    log.info("Successfully updated holdingYield for account with uuid " + uuid + " to " + newHoldingYield);
                }
                else {
                    log.info("Failed to update holdingYield for account with uuid " + uuid + " to " + newHoldingYield);
                }
            }
        } catch (Exception e){
            throw new Exception("failed to update holdingYield for account with uuid "+uuid);
        }
    }

    @Override
    public void driverUpdateAssets(String uuid) throws Exception{
        try {
            updateAssets(uuid);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    /**
     * 4. 更新账户总资产 = holdingShares * currentPrice (obtained at 12pm everyday) for all holdings + accountBalance
     * @param uuid uuid
     * @throws Exception failed to update assets
     */
    private void updateAssets(String uuid) throws Exception {
        try{
            log.info("try tradeMapper.listCurrentHoldingsByUuid({})", uuid);
            List<HoldingEntity> currentHoldings = tradeMapper.listCurrentHoldingsByUuid(uuid);
            double newAssets = tradeMapper.getBalanceByUuid(uuid);
            double valueOfOneHolding;
            if (currentHoldings != null){
                int size = currentHoldings.size();
                log.info("{} holding record(s) found", size);
                for (HoldingEntity thisHolding : currentHoldings) {
                    String thisFundId = thisHolding.getFundId();
                    valueOfOneHolding = thisHolding.getHoldingShares() * getCurrentPrice(thisFundId);
                    newAssets += valueOfOneHolding;
                    log.info("newAssets += valueOfOneHolding ({}) --> {}", valueOfOneHolding, newAssets);
                }
                log.info("try tradeMapper.setAssets");
                int ret = tradeMapper.setAssets(uuid, newAssets);
                if (ret == 1){
                    log.info("Successfully updated assets for account with uuid " + uuid + " to " + newAssets);
                }
                else {
                    log.info("Failed to update assets for account with uuid " + uuid + " to " + newAssets);
                }
            }
        } catch (Exception e){
            throw new Exception("failed to update assets for account with uuid "+uuid);
        }
    }

    @Override
    public void driverUpdateTotalYield(String uuid) throws Exception{
        try {
            updateTotalYield(uuid);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    /**
     * 更新总收益 = updatedAssets - startingBalance
     * NOTE: must be run after updateAssets()
     * @param uuid uuid
     */
    private void updateTotalYield(String uuid) throws Exception {
        try {
            double assets = tradeMapper.getAssetsByUuid(uuid);
            double startingBalance = tradeMapper.getStartingBalanceByUuid(uuid);
            double newTotalYield = assets - startingBalance;
            int ret = tradeMapper.setTotalYield(uuid, newTotalYield);
            if (ret == 1){
                log.info("Successfully updated totalYield for account with uuid " + uuid + " to " + newTotalYield);
            }
            else {
                log.info("Failed to update totalYield for account with uuid " + uuid + " to " + newTotalYield);
            }
        } catch (Exception e){
            throw new Exception("failed to update totalYield for account with uuid "+uuid);
        }
    }
}