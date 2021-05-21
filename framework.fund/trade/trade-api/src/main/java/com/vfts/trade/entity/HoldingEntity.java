package com.vfts.trade.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * HoldingEntity class
 * @author Axl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoldingEntity {
    private String uuid;
    private String fundId;
    private double holdingShares;
    private Date startHoldingDate;
    private double transactionPrice;
    private List<Double> yieldHistory;
    private List<Date> yieldDate;

    /**
     * Overload Constructor for HoldingEntity class
     * @param uuid uuid
     * @param fundId fundId
     * @param tradeShares tradeShares
     * @param tradeDate tradeDate
     * @param tradePrice tradePrice
     */
    public HoldingEntity(String uuid, String fundId, double tradeShares, Date tradeDate, double tradePrice) {
        this.uuid = uuid;
        this.fundId = fundId;
        this.holdingShares = tradeShares;
        this.startHoldingDate = tradeDate;
        this.transactionPrice = tradePrice;
    }
}
