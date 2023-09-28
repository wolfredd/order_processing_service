package com.sagatrading.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sagatrading.model.OrderSide;
import com.sagatrading.model.OrderType;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

//"{
// \"orderType\":\"LIMIT\",
// \"product\":\"GOOGL\",
// \"side\":\"SELL\",
// \"orderID\":\"dd705ee1-9614-4951-96cc-a2601bf2ad22\",
// \"price\":1.2,
// \"qty\":2000,
// \"cumQty\":127,
// \"cumPrx\":\"1.20\",
// \"exchange\":\"MAL1\",
// \"timestamp\":\"2023-06-08T04:10:17.425\
// "}"

@Data
public class MarketDataUpdate {
    private OrderType orderType;
    private String product;
    private OrderSide side;
    private UUID orderID;
    private double price;
    private int qty;
    private int cumQty;
    private double cumPrx;
    private String exchange;
    private Date timestamp;
}
