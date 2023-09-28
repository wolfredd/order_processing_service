package com.sagatrading.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductData {
    @JsonProperty("BID_PRICE")
    private Double bidPrice;

    @JsonProperty("SELL_LIMIT")
    private Integer sellLimit;

    @JsonProperty("ASK_PRICE")
    private Double askPrice;

    @JsonProperty("BUY_LIMIT")
    private Integer buyLimit;

    @JsonProperty("MAX_PRICE_SHIFT")
    private Double maxPriceShift;

    @JsonProperty("TICKER")
    private String ticker;

    @JsonProperty("LAST_TRADED_PRICE")
    private Double lastTradedPrice;

    public ProductData() {
    }

    public Double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Integer getSellLimit() {
        return sellLimit;
    }

    public void setSellLimit(Integer sellLimit) {
        this.sellLimit = sellLimit;
    }

    public Double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(Double askPrice) {
        this.askPrice = askPrice;
    }

    public Integer getBuyLimit() {
        return buyLimit;
    }

    public void setBuyLimit(Integer buyLimit) {
        this.buyLimit = buyLimit;
    }

    public Double getMaxPriceShift() {
        return maxPriceShift;
    }

    public void setMaxPriceShift(Double maxPriceShift) {
        this.maxPriceShift = maxPriceShift;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Double getLastTradedPrice() {
        return lastTradedPrice;
    }

    public void setLastTradedPrice(Double lastTradedPrice) {
        this.lastTradedPrice = lastTradedPrice;
    }

    @Override
    public String toString() {
        return "ProductDetails{" +
                "bidPrice=" + bidPrice +
                ", sellLimit=" + sellLimit +
                ", askPrice=" + askPrice +
                ", buyLimit=" + buyLimit +
                ", maxPriceShift=" + maxPriceShift +
                ", ticker='" + ticker + '\'' +
                ", lastTradedPrice=" + lastTradedPrice +
                '}';
    }
}
