package com.sagatrading.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sagatrading.model.OrderSide;
import com.sagatrading.model.OrderType;
import lombok.Data;

@Data
public class OrderBookItem {
    private String product;
    private Integer quantity;
    private Double price;
    private OrderSide side;
    private String orderID;
    private OrderType orderType;
    private Integer cumulatitiveQuantity;
    private Double cumulatitivePrice;

    @JsonIgnore
    private Object executions;
}
