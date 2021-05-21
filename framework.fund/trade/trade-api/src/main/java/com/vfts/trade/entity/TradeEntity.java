package com.vfts.trade.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TradeEntity class
 * @author Axl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeEntity {
    private String uuid;
    private String orderId;
    private String fundId;
    /** tradeType
     * 0=buy, 1=sell
     */
    private int tradeType;
    private double tradeShares;
    private double tradePrice;
    private Date orderDate;
    private Date tradeDate;

    /**
     * Overload Constructor
     * @param uuid uuid
     * @param orderId orderId
     * @param fundId fundId
     * @param tradePrice tradePrice
     * @param orderType orderType
     * @param buyingAmount buyingAmouny
     * @param sellingShares sellingShares
     * @param orderDate orderDate
     * @throws ParseException while parsing Date
     */
    public TradeEntity(String uuid, String orderId, String fundId, double tradePrice, int orderType, double buyingAmount, double sellingShares, Date orderDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.uuid = uuid;
        this.orderId = orderId;
        this.fundId = fundId;
        this.tradePrice = tradePrice;
        this.tradeType = orderType;
        if (tradeType == 0){
            //tradeType: buy
            this.tradeShares = buyingAmount / tradePrice;
        }
        else{
            //tradeType: sell
            tradeShares = sellingShares;
        }
        this.orderDate = orderDate;
        //generate orderDate in format: yyyy-MM-dd HH:mm:ss
        Date dateUnformatted = new Date();
        String dateStr = sdf.format(dateUnformatted);
        this.tradeDate = sdf.parse(dateStr);
    }
}
