package com.vfts.trade.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Axl
 * AccountEntity class
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {
    private String uuid;
    private static final double startingBalance =  10000;
    private double accountBalance;
    /**
     * assets
     * = holdingShares * currentPrice (obtained at 12pm everyday) for all holdings + accountBalance
     */
    private double assets;
    /**
     * holdingYield
     * = holdingShares * (currentPrice - holdingEntity.transactionPrice) for all holdings
     */
    private double holdingYield = 0;
    /**
     * totalYield
     * = assets - startingBalance
     */
    private double totalYield = 0;
    /**
     * yesterdayYield
     * = sum(yieldHistory[end]) for all holdings
     */
    private double yesterdayYield = 0;

    public AccountEntity(String uuid, double accountBalance) {
        this.uuid = uuid;
        this.accountBalance = accountBalance;
    }
}
