package com.vfts.trade.service;

import com.vfts.trade.entity.OrderEntity;
import com.vfts.trade.iface.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.vfts.trade.iface.ITradeService;

import java.util.List;

/**
 * ScheduledTask class
 * @author Axl
 */
@Component
@Slf4j
public class ScheduledTask {

    @Autowired
    private ITradeService tradeService;
    @Autowired
    private IOrderService orderService;

    /**
     * cron expression
     * "[秒] [分] [小时] [日] [周] [月] [年]"
     * Examples:
     * //@Scheduled(cron = "0 0 1 * * ?") 每天凌晨1点整
     * //@Scheduled(cron = "0 30 0 * * ?") 每天凌晨0点30分
     */
    @Scheduled(cron = "10 58 12 * * ?")
    public void scheduleTaskWithCronExpression() throws Exception {

        log.info("try getting all registered uuids");
        List<String> allRegisteredUuids = orderService.returnAllRegisteredUuids();
        int numOfUuids = allRegisteredUuids.size();
        log.info("{} registered uuids retrieved", numOfUuids);

        String uuid;

        /**
         * 1. update yesterdayYield for all accounts
         */
        log.info("START---1. update yesterdayYield for all accounts---START");

        log.info("try tradeService.driverUpdateYesterdayYield");
        for (int i = 0; i < numOfUuids; i ++){
            uuid = allRegisteredUuids.get(i);
            tradeService.driverUpdateYesterdayYield(uuid);
        }
        log.info("END---1. update yesterdayYield for all accounts---END");

        /**
         * 2. update all "processing" orders to trades
         */
        log.info("START---2. update all \"processing\" orders to trades---START");
        log.info("retrieving \"processing\" order(s)");
        List<OrderEntity> allProcessingOrders = tradeService.listAllProcessingOrders();
        if (allProcessingOrders != null) {
            int size = allProcessingOrders.size();
            log.info("{} processing orders retrieved", size);
            log.info("try updating order(s) to trade(s)");
            for (int i = 0; i < size; i++) {
                log.info("processing order {}", i);
                OrderEntity thisOrder = allProcessingOrders.get(i);
                tradeService.driverUpdateOrderToTrade(thisOrder);
            }
        }
        log.info("END---2. update all \"processing\" orders to trades---END");


        /**
         * 3. update holdingYield, assets, and totalYield for all accounts
         */
        log.info("START---3. update holdingYield, assets, and totalYield for all accounts---START");
        for (int i = 0; i < numOfUuids; i ++){
            uuid = allRegisteredUuids.get(i);
            log.info("processing account {}", i);
            //update holdingYield
            log.info("try updating holdingYield for account {}",i);
            tradeService.driverUpdateHoldingYield(uuid);
            //update assets
            log.info("try updating assets for account {}",i);
            tradeService.driverUpdateAssets(uuid);
            //update totalYield
            log.info("try updating totalYield for account {}",i);
            tradeService.driverUpdateTotalYield(uuid);
        }
        log.info("END---3. update holdingYield, assets, and totalYield for all accounts---END");
    }
}

