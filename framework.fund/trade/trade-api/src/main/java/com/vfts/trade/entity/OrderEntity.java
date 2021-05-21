package com.vfts.trade.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * OrderEntity class
 * @author Axl
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {
    private String uuid;
    private String orderId;
    private String fundId;
    /**
     * orderType
     * 0:buy, 1:sell
     * Only one of the two following two properties is used for one order:
     *     e.g. If the order is of type 0:buy,
     *     buyingAmount is to be used and sellingShares is set to 0
     */
    private int orderType;
    private double sellingShares;
    private double buyingAmount;
    private Date orderDate;
    private String orderStatus = "processing";

    /**
     * Overload Constructor
     * @param uuid uuid
     * @param fundId fundId
     * @param orderType orderType
     * @param sellingShares sellingShares
     * @param buyingAmount buyingAmount
     * @throws ParseException while parsing Date
     */
    public OrderEntity(String uuid, String fundId, int orderType, double sellingShares, double buyingAmount) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.uuid = uuid;
        this.orderId = UUID.randomUUID().toString();
        this.fundId = fundId;
        this.orderType = orderType;
        this.sellingShares = sellingShares;
        this.buyingAmount = buyingAmount;
        // generate orderDate in format: yyyy-MM-dd HH:mm:ss
        Date dateUnformatted = new Date();
        String dateStr = sdf.format(dateUnformatted);
        this.orderDate = sdf.parse(dateStr);
    }
}
